package tools.vitruv.change.propagation.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
import tools.vitruv.change.interaction.InteractionResultProvider;
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
  private static class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
    private final ChangePropagator outer;

    private final VitruviusChange<EObject> sourceChange;

    private final ChangePropagator.ChangePropagation previous;

    private final Set<Resource> changedResources = new HashSet<Resource>();

    private final List<EObject> createdObjects = new ArrayList<EObject>();

    private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

    private List<PropagatedChange> propagateChanges() {
      List<PropagatedChange> result = StreamSupport.stream(this.outer.getTransactionalChangeSequence(this.sourceChange).spliterator(), false)
          .flatMap(it -> this.propagateSingleChange(it).stream())
          .collect(Collectors.toList());
      this.handleObjectsWithoutResource();
      this.changedResources.forEach(it -> it.setModified(true));
      return result;
    }

    private List<PropagatedChange> propagateSingleChange(final TransactionalChange<EObject> change) {
      try {
        Preconditions.checkState(!change.getAffectedEObjects().isEmpty(),
          "There are no objects affected by this change:%s%s", System.lineSeparator(), change);
        final AutoCloseable userInteractorChange = this.installUserInteractorForChange(change);
        this.outer.changePropagationProvider.forEach(it -> it.registerObserver(this));
        this.outer.userInteractor.registerUserInputListener(this);
        List<TransactionalChange<EObject>> _xtrycatchfinallyexpression = null;
        try {
          Set<ChangePropagationSpecification> allSpecs = this.sourceChange.getAffectedEObjectsMetamodelDescriptors().stream()
              .flatMap(it -> {
                List<ChangePropagationSpecification> specs = this.outer.changePropagationProvider.getChangePropagationSpecifications(it);
                specs.forEach(s -> s.setUserInteractor(this.outer.userInteractor));
                return specs.stream();
              })
              .collect(Collectors.toCollection(LinkedHashSet::new));
          _xtrycatchfinallyexpression = allSpecs.stream()
              .flatMap(it -> StreamSupport.stream(this.propagateChangeForChangePropagationSpecification(change, it).spliterator(), false))
              .collect(Collectors.toList());
        } finally {
          this.outer.userInteractor.deregisterUserInputListener(this);
          this.outer.changePropagationProvider.forEach(it -> it.deregisterObserver(this));
          userInteractorChange.close();
        }
        final List<TransactionalChange<EObject>> propagationResultChanges = _xtrycatchfinallyexpression;
        if (ChangePropagator.logger.isDebugEnabled()) {
          String path = String.join(" -> ", this.getPropagationPath());
          String changes = propagationResultChanges.stream()
              .map(c -> String.valueOf(c.getAffectedEObjectsMetamodelDescriptors()))
              .collect(Collectors.joining(", "));
          ChangePropagator.logger.debug("Propagated " + path + " -> {" + changes + "}");
        }
        if (ChangePropagator.logger.isTraceEnabled()) {
          String resultChanges = propagationResultChanges.stream()
              .map(r -> "\t" + r.getAffectedEObjectsMetamodelDescriptors() + ": " + r)
              .collect(Collectors.joining("\n"));
          ChangePropagator.logger.trace("Result changes:\n" + resultChanges);
        }
        change.setUserInteractions(this.userInteractions);
        CompositeContainerChange<EObject> _createCompositeChange = VitruviusChangeFactory.getInstance().<EObject>createCompositeChange(propagationResultChanges);
        final PropagatedChange propagatedChange = new PropagatedChange(change, _createCompositeChange);
        final ArrayList<PropagatedChange> resultingChanges = new ArrayList<PropagatedChange>();
        resultingChanges.add(propagatedChange);
        if (!Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.SINGLE_STEP)) {
          Iterable<PropagatedChange> _propagateTransitiveChanges = this.propagateTransitiveChanges(
              propagationResultChanges.stream().filter(TransactionalChange::containsConcreteChange).collect(Collectors.toList()));
          Iterables.<PropagatedChange>addAll(resultingChanges, _propagateTransitiveChanges);
        }
        return resultingChanges;
      } catch (Throwable _e) {
        throw new RuntimeException(_e);
      }
    }

    private Iterable<PropagatedChange> propagateTransitiveChanges(final Iterable<TransactionalChange<EObject>> transitiveChanges) {
      List<TransactionalChange<EObject>> nonEmptyChanges = StreamSupport.stream(transitiveChanges.spliterator(), false)
          .filter(TransactionalChange::containsConcreteChange)
          .collect(Collectors.toList());
      List<TransactionalChange<EObject>> nonLeafChanges;
      if (Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES)) {
        nonLeafChanges = nonEmptyChanges.stream()
            .filter(it -> this.outer.changePropagationProvider.getChangePropagationSpecifications(
                it.getAffectedEObjectsMetamodelDescriptor()).size() > 1)
            .collect(Collectors.toList());
      } else {
        nonLeafChanges = nonEmptyChanges;
      }
      List<ChangePropagator.ChangePropagation> nextPropagations = nonLeafChanges.stream()
          .map(it -> new ChangePropagator.ChangePropagation(this.outer, it, this))
          .collect(Collectors.toList());
      return Iterables.concat(nextPropagations.stream()
          .map(it -> it.propagateChanges())
          .collect(Collectors.toList()));
    }

    private Iterable<TransactionalChange<EObject>> propagateChangeForChangePropagationSpecification(final TransactionalChange<EObject> change, final ChangePropagationSpecification propagationSpecification) {
      final Runnable _function = () -> {
        for (final EChange<EObject> eChange : change.getEChanges()) {
          propagationSpecification.propagateChange(eChange, this.outer.modelRepository.getCorrespondenceModel(),
            this.outer.modelRepository);
        }
      };
      final Iterable<TransactionalChange<EObject>> transitiveChanges = this.outer.modelRepository.recordChanges(_function);
      StreamSupport.stream(transitiveChanges.spliterator(), false)
          .flatMap(it -> it.getAffectedEObjects().stream())
          .map(EObject::eResource)
          .filter(Objects::nonNull)
          .forEach(this.changedResources::add);
      return transitiveChanges;
    }

    private AutoCloseable installUserInteractorForChange(final VitruviusChange<EObject> change) {
      final Iterable<UserInteractionBase> pastUserInputsFromChange = change.getUserInteractions();
      if (pastUserInputsFromChange != null && pastUserInputsFromChange.iterator().hasNext()) {
        return this.outer.userInteractor.replaceUserInteractionResultProvider(
            (InteractionResultProvider currentProvider) -> UserInteractionFactory.instance.createPredefinedInteractionResultProvider(
                currentProvider, Iterables.toArray(pastUserInputsFromChange, UserInteractionBase.class)));
      } else {
        return () -> {};
      }
    }

    private void handleObjectsWithoutResource() {
      List<EObject> objectsWithoutResource = this.createdObjects.stream()
          .filter(it -> it.eResource() == null)
          .collect(Collectors.toList());
      for (final EObject createdObjectWithoutResource : objectsWithoutResource) {
        Preconditions.checkState(
            !this.outer.modelRepository.getCorrespondenceModel().hasCorrespondences(createdObjectWithoutResource),
            "The object %s is part of a correspondence to %s but not in any resource", createdObjectWithoutResource,
            this.outer.modelRepository.getCorrespondenceModel().getCorrespondingEObjects(createdObjectWithoutResource));
        ChangePropagator.logger.warn("Object was created but has no correspondence and is thus lost: " + createdObjectWithoutResource);
      }
    }

    @Override
    public void objectCreated(final EObject createdObject) {
      this.createdObjects.add(createdObject);
    }

    @Override
    public void changePropagationStarted(final ChangePropagationSpecification specification, final EChange<EObject> change) {
      return;
    }

    @Override
    public void changePropagationStopped(final ChangePropagationSpecification specification, final EChange<EObject> change) {
      return;
    }

    @Override
    public void onUserInteractionReceived(final UserInteractionBase interaction) {
      this.userInteractions.add(interaction);
    }

    @Override
    public String toString() {
      return "propagate " + String.join(" -> ", this.getPropagationPath()) + ": " + this.sourceChange;
    }

    private Iterable<String> getPropagationPath() {
      if (this.previous == null) {
        return List.of("<input change> in " + this.sourceChange.getAffectedEObjectsMetamodelDescriptors());
      } else {
        return Iterables.concat(this.previous.getPropagationPath(),
            List.of(this.sourceChange.getAffectedEObjectsMetamodelDescriptors().toString()));
      }
    }

    public ChangePropagation(final ChangePropagator outer, final VitruviusChange<EObject> sourceChange, final ChangePropagator.ChangePropagation previous) {
      super();
      this.outer = outer;
      this.sourceChange = sourceChange;
      this.previous = previous;
    }
  }

  private static final Logger logger = LogManager.getLogger(ChangePropagator.class);

  private final ChangeRecordingModelRepository modelRepository;

  private final ChangePropagationSpecificationProvider changePropagationProvider;

  private final InternalUserInteractor userInteractor;

  private final ChangePropagationMode changePropagationMode;

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated transitively and cyclic, i.e. with
   * {@link ChangePropagationMode#TRANSITIVE_CYCLIC}.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository, final ChangePropagationSpecificationProvider changePropagationProvider, final InternalUserInteractor userInteractor) {
    this(modelRepository, changePropagationProvider, userInteractor, ChangePropagationMode.TRANSITIVE_CYCLIC);
  }

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated using the given <code>mode</code>.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository, final ChangePropagationSpecificationProvider changePropagationProvider, final InternalUserInteractor userInteractor, final ChangePropagationMode mode) {
    this.modelRepository = modelRepository;
    this.changePropagationProvider = changePropagationProvider;
    this.userInteractor = userInteractor;
    this.changePropagationMode = mode;
  }

  /**
   * Applies, then propagates <code>change</code>
   * through the models in <code>modelRepository</code>.
   *
   * @param change - {@link VitruviusChange}
   * @param observers - {@link Iterable} of {@link ChangePropagationObserver}
   * @return - {@link List} of {@link PropagatedChange}
   */
  public List<PropagatedChange> propagateChange(final VitruviusChange<Uuid> change, final Iterable<ChangePropagationObserver> observers) {
    final VitruviusChange<EObject> resolvedChange = this.modelRepository.applyChange(change);
    resolvedChange.getAffectedEObjects().stream()
        .map(EObject::eResource)
        .filter(Objects::nonNull)
        .forEach(it -> it.setModified(true));
    if (ChangePropagator.logger.isTraceEnabled()) {
      ChangePropagator.logger.trace("Will now propagate this input change:\n\t" + resolvedChange);
    }
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    List<PropagatedChange> result = new ChangePropagator.ChangePropagation(this, resolvedChange, null).propagateChanges();
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    return result;
  }

  private Iterable<TransactionalChange<EObject>> getTransactionalChangeSequence(final VitruviusChange<EObject> change) {
    if (!change.containsConcreteChange()) {
      return List.of();
    }
    if (change instanceof TransactionalChange) {
      return List.of((TransactionalChange<EObject>) change);
    }
    if (change instanceof CompositeChange) {
      return ((CompositeChange<EObject, ?>) change).getChanges().stream()
          .flatMap(it -> StreamSupport.stream(this.getTransactionalChangeSequence(it).spliterator(), false))
          .collect(Collectors.toList());
    }
    throw new IllegalStateException("Unexpected change type: " + change.getClass().getSimpleName());
  }
}
