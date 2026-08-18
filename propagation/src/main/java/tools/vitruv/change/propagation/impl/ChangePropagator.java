package tools.vitruv.change.propagation.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.CompositeChange;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.propagation.ChangePropagationObserver;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationProvider;
import tools.vitruv.change.propagation.ChangeRecordingModelRepository;

public class ChangePropagator {
  private static final Logger LOGGER = LogManager.getLogger(ChangePropagator.class);

  private final ChangeRecordingModelRepository modelRepository;
  private final ChangePropagationSpecificationProvider changePropagationProvider;
  private final InternalUserInteractor userInteractor;
  private final ChangePropagationMode changePropagationMode;

  /**
   * Creates a change propagator to which changes can be passed, which are propagated using the
   * given <code>changePropagationProvider</code> and <code>userInteractor</code>. Changes are
   * recorded in the given <code>modelRepository</code> and propagated transitively and cyclic, i.e.
   * with {@link ChangePropagationMode#TRANSITIVE_CYCLIC}.
   */
  public ChangePropagator(
      ChangeRecordingModelRepository modelRepository,
      ChangePropagationSpecificationProvider changePropagationProvider,
      InternalUserInteractor userInteractor) {
    this(
        modelRepository,
        changePropagationProvider,
        userInteractor,
        ChangePropagationMode.TRANSITIVE_CYCLIC);
  }

  /**
   * Creates a change propagator to which changes can be passed, which are propagated using the
   * given <code>changePropagationProvider</code> and <code>userInteractor</code>. Changes are
   * recorded in the given <code>modelRepository</code> and propagated using the given <code>mode
   * </code>.
   */
  public ChangePropagator(
      ChangeRecordingModelRepository modelRepository,
      ChangePropagationSpecificationProvider changePropagationProvider,
      InternalUserInteractor userInteractor,
      ChangePropagationMode mode) {
    this.modelRepository = modelRepository;
    this.changePropagationProvider = changePropagationProvider;
    this.userInteractor = userInteractor;
    this.changePropagationMode = mode;
  }

  /**
   * Applies, then propagates <code>change</code> through the models in <code>modelRepository</code>
   * .
   *
   * @param change - {@link VitruviusChange}
   * @return - {@link List} of {@link PropagatedChange}
   */
  public List<PropagatedChange> propagateChange(VitruviusChange<Uuid> change) {
    VitruviusChange<EObject> resolvedChange = modelRepository.applyChange(change);
    for (EObject affectedObject : resolvedChange.getAffectedEObjects()) {
      Resource resource = affectedObject.eResource();
      if (resource != null) {
        resource.setModified(true);
      }
    }
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Will now propagate this input change:\n\t" + resolvedChange);
    }
    return new ChangePropagation(resolvedChange, null).propagateChanges();
  }

  private List<TransactionalChange<EObject>> getTransactionalChangeSequence(
      VitruviusChange<EObject> change) {
    if (!change.containsConcreteChange()) {
      return List.of();
    }
    if (change instanceof TransactionalChange<EObject> transactionalChange) {
      return List.of(transactionalChange);
    }
    if (change instanceof CompositeChange<EObject, ?> compositeChange) {
      return compositeChange.getChanges().stream()
          .flatMap(it -> getTransactionalChangeSequence(it).stream())
          .collect(Collectors.toList());
    }
    throw new IllegalStateException("Unexpected change type: " + change.getClass().getSimpleName());
  }

  /**
   * Propagates a single source change (and, depending on {@link #changePropagationMode}, the
   * changes resulting from that propagation) through the models in {@link #modelRepository}.
   */
  private class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
    private final VitruviusChange<EObject> sourceChange;
    private final ChangePropagation previous;

    private final Set<Resource> changedResources = new LinkedHashSet<>();
    private final List<EObject> createdObjects = new ArrayList<>();
    private final List<UserInteractionBase> userInteractions = new ArrayList<>();

    ChangePropagation(VitruviusChange<EObject> sourceChange, ChangePropagation previous) {
      this.sourceChange = sourceChange;
      this.previous = previous;
    }

    private List<PropagatedChange> propagateChanges() {
      // First, the whole VitruviusChange is propagated to the change propagation specifications
      // that can handle it directly. Then, the remaining specifications handle the change
      // atomically, change by change (non-atomic-enabled specifications are called too, but
      // perform no-ops there).
      List<PropagatedChange> result = new ArrayList<>(propagateNonAtomicChange());
      for (TransactionalChange<EObject> change : getTransactionalChangeSequence(sourceChange)) {
        result.addAll(propagateSingleChange(change));
      }
      handleObjectsWithoutResource();
      changedResources.forEach(resource -> resource.setModified(true));
      return result;
    }

    private List<PropagatedChange> propagateNonAtomicChange() {
      List<TransactionalChange<EObject>> propagationResultChanges =
          propagateWithUserInteractionHandling(
              sourceChange,
              () -> {
                List<TransactionalChange<EObject>> results = new ArrayList<>();
                for (ChangePropagationSpecification specification :
                    getChangePropagationSpecifications(
                        ChangePropagationSpecification::doesHandleNonAtomicChanges)) {
                  Iterables.addAll(
                      results,
                      propagateNonAtomicChangeForChangePropagationSpecification(
                          sourceChange, specification));
                }
                return results;
              });
      logPropagationResult(propagationResultChanges);

      List<PropagatedChange> resultingChanges = new ArrayList<>();
      if (!propagationResultChanges.isEmpty()) {
        CompositeContainerChange<EObject> compositeResultChange =
            VitruviusChangeFactory.getInstance().createCompositeChange(propagationResultChanges);
        resultingChanges.add(new PropagatedChange(sourceChange, compositeResultChange));
      }
      if (changePropagationMode != ChangePropagationMode.SINGLE_STEP) {
        resultingChanges.addAll(
            propagateTransitiveChanges(concreteChangesOf(propagationResultChanges)));
      }
      return resultingChanges;
    }

    private List<PropagatedChange> propagateSingleChange(TransactionalChange<EObject> change) {
      Preconditions.checkState(
          !change.getAffectedEObjects().isEmpty(),
          "There are no objects affected by this change:%s%s",
          System.lineSeparator(),
          change);

      List<TransactionalChange<EObject>> propagationResultChanges =
          propagateWithUserInteractionHandling(
              change,
              () -> {
                List<TransactionalChange<EObject>> results = new ArrayList<>();
                for (ChangePropagationSpecification specification :
                    getChangePropagationSpecifications(specification -> true)) {
                  Iterables.addAll(
                      results,
                      propagateChangeForChangePropagationSpecification(change, specification));
                }
                return results;
              });

      logPropagationResult(propagationResultChanges);
      change.setUserInteractions(userInteractions);
      CompositeContainerChange<EObject> compositeResultChange =
          VitruviusChangeFactory.getInstance().createCompositeChange(propagationResultChanges);
      PropagatedChange propagatedChange = new PropagatedChange(change, compositeResultChange);

      List<PropagatedChange> resultingChanges = new ArrayList<>();
      resultingChanges.add(propagatedChange);
      if (changePropagationMode != ChangePropagationMode.SINGLE_STEP) {
        resultingChanges.addAll(
            propagateTransitiveChanges(concreteChangesOf(propagationResultChanges)));
      }
      return resultingChanges;
    }

    /**
     * Installs the user interactions recorded for <code>change</code>, registers this as observer
     * and user input listener, runs <code>propagation</code>, and reverts all of that again
     * afterwards.
     */
    private List<TransactionalChange<EObject>> propagateWithUserInteractionHandling(
        VitruviusChange<EObject> change,
        Supplier<List<TransactionalChange<EObject>>> propagation) {
      try (AutoCloseable userInteractorChange = installUserInteractorForChange(change)) {
        changePropagationProvider.forEach(it -> it.registerObserver(this));
        userInteractor.registerUserInputListener(this);
        try {
          return propagation.get();
        } finally {
          userInteractor.deregisterUserInputListener(this);
          changePropagationProvider.forEach(it -> it.deregisterObserver(this));
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private List<TransactionalChange<EObject>> concreteChangesOf(
        List<TransactionalChange<EObject>> changes) {
      return changes.stream()
          .filter(TransactionalChange::containsConcreteChange)
          .collect(Collectors.toList());
    }

    /**
     * Returns the change propagation specifications applicable to {@link #sourceChange} for which
     * <code>filter</code> holds.
     */
    private Set<ChangePropagationSpecification> getChangePropagationSpecifications(
        Predicate<ChangePropagationSpecification> filter) {
      Set<ChangePropagationSpecification> specifications = new LinkedHashSet<>();
      for (MetamodelDescriptor descriptor : sourceChange.getAffectedEObjectsMetamodelDescriptors()) {
        for (ChangePropagationSpecification specification :
            changePropagationProvider.getChangePropagationSpecifications(descriptor)) {
          if (filter.test(specification)) {
            specification.setUserInteractor(userInteractor);
            specifications.add(specification);
          }
        }
      }
      return specifications;
    }

    private void logPropagationResult(List<TransactionalChange<EObject>> propagationResultChanges) {
      if (LOGGER.isDebugEnabled()) {
        String path = String.join(" -> ", getPropagationPath());
        String changes =
            propagationResultChanges.stream()
                .map(c -> String.valueOf(c.getAffectedEObjectsMetamodelDescriptors()))
                .collect(Collectors.joining(", "));
        LOGGER.debug("Propagated " + path + " -> {" + changes + "}");
      }
      if (LOGGER.isTraceEnabled()) {
        String resultChanges =
            propagationResultChanges.stream()
                .map(r -> "\t" + r.getAffectedEObjectsMetamodelDescriptors() + ": " + r)
                .collect(Collectors.joining("\n"));
        LOGGER.trace("Result changes:\n" + resultChanges);
      }
    }

    private List<PropagatedChange> propagateTransitiveChanges(
        List<TransactionalChange<EObject>> transitiveChanges) {
      List<PropagatedChange> result = new ArrayList<>();
      for (TransactionalChange<EObject> change : transitiveChanges) {
        if (isPropagatedFurther(change)) {
          result.addAll(new ChangePropagation(change, this).propagateChanges());
        }
      }
      return result;
    }

    /**
     * Returns whether the given change is propagated further, depending on {@link
     * #changePropagationMode}: in {@link ChangePropagationMode#TRANSITIVE_EXCEPT_LEAVES}, changes
     * for which only a single specification is registered (i.e. leaf changes) are not propagated
     * further.
     */
    private boolean isPropagatedFurther(TransactionalChange<EObject> change) {
      if (changePropagationMode != ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES) {
        return true;
      }
      return changePropagationProvider
              .getChangePropagationSpecifications(change.getAffectedEObjectsMetamodelDescriptor())
              .size()
          > 1;
    }

    private Iterable<TransactionalChange<EObject>> propagateChangeForChangePropagationSpecification(
        TransactionalChange<EObject> change, ChangePropagationSpecification propagationSpecification) {
      Iterable<TransactionalChange<EObject>> transitiveChanges =
          modelRepository.recordChanges(
              () -> {
                for (EChange<EObject> eChange : change.getEChanges()) {
                  propagationSpecification.propagateChange(
                      eChange, modelRepository.getCorrespondenceModel(), modelRepository);
                }
              });
      registerChangedResources(transitiveChanges);
      return transitiveChanges;
    }

    private Iterable<TransactionalChange<EObject>>
        propagateNonAtomicChangeForChangePropagationSpecification(
            VitruviusChange<EObject> change, ChangePropagationSpecification propagationSpecification) {
      Iterable<TransactionalChange<EObject>> transitiveChanges =
          modelRepository.recordChanges(
              () ->
                  propagationSpecification.propagateNonAtomicChange(
                      change, modelRepository.getCorrespondenceModel(), modelRepository));
      registerChangedResources(transitiveChanges);
      return transitiveChanges;
    }

    private void registerChangedResources(Iterable<TransactionalChange<EObject>> transitiveChanges) {
      for (TransactionalChange<EObject> transitiveChange : transitiveChanges) {
        for (EObject affectedObject : transitiveChange.getAffectedEObjects()) {
          Resource resource = affectedObject.eResource();
          if (resource != null) {
            changedResources.add(resource);
          }
        }
      }
    }

    private AutoCloseable installUserInteractorForChange(VitruviusChange<EObject> change) {
      Iterable<UserInteractionBase> pastUserInteractions = change.getUserInteractions();
      if (pastUserInteractions == null || Iterables.isEmpty(pastUserInteractions)) {
        return () -> {};
      }
      return userInteractor.replaceUserInteractionResultProvider(
          currentProvider ->
              UserInteractionFactory.instance.createPredefinedInteractionResultProvider(
                  currentProvider, Iterables.toArray(pastUserInteractions, UserInteractionBase.class)));
    }

    private void handleObjectsWithoutResource() {
      for (EObject createdObjectWithoutResource : createdObjects) {
        if (createdObjectWithoutResource.eResource() != null) {
          continue;
        }
        Preconditions.checkState(
            !modelRepository.getCorrespondenceModel().hasCorrespondences(createdObjectWithoutResource),
            "The object %s is part of a correspondence to %s but not in any resource",
            createdObjectWithoutResource,
            modelRepository.getCorrespondenceModel().getCorrespondingEObjects(createdObjectWithoutResource));
        LOGGER.warn(
            "Object was created but has no correspondence and is thus lost: "
                + createdObjectWithoutResource);
      }
    }

    @Override
    public void objectCreated(EObject createdObject) {
      createdObjects.add(createdObject);
    }

    @Override
    public void changePropagationStarted(
        ChangePropagationSpecification specification, EChange<EObject> change) {
      // Nothing to do.
    }

    @Override
    public void changePropagationStopped(
        ChangePropagationSpecification specification, EChange<EObject> change) {
      // Nothing to do.
    }

    @Override
    public void onUserInteractionReceived(UserInteractionBase interaction) {
      userInteractions.add(interaction);
    }

    @Override
    public String toString() {
      return "propagate " + String.join(" -> ", getPropagationPath()) + ": " + sourceChange;
    }

    private List<String> getPropagationPath() {
      if (previous == null) {
        return List.of(
            "<input change> in " + sourceChange.getAffectedEObjectsMetamodelDescriptors());
      }
      List<String> path = new ArrayList<>(previous.getPropagationPath());
      path.add(sourceChange.getAffectedEObjectsMetamodelDescriptors().toString());
      return path;
    }
  }
}
