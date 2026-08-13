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

import com.google.common.collect.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
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
import tools.vitruv.change.propagation.*;

public class ChangePropagator {
  private record PropagationResult(TransactionalChangeWithPreviousState change, List<PropagatedChange> propagatedChanges) {
  }

  @FunctionalInterface
  private interface LowerLevelPropagation {
    PropagationResult propagateChange(TransactionalChangeWithPreviousState change);
  }

  private static class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
    private final ChangePropagator outer;

    private final TransactionalChangeWithPreviousState sourceChange;

    private final int level;

    private final LowerLevelPropagation lowerLevelPropagation;

    private final ChangePropagator.ChangePropagation previous;

    private final Set<Resource> changedResources = new HashSet<Resource>();

    private final List<EObject> createdObjects = new ArrayList<EObject>();

    private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

    private List<PropagatedChange> propagateChange() {
      List<PropagatedChange> result = this.propagateSingleChange(this.sourceChange.change(), this.sourceChange.previousState());
      this.handleObjectsWithoutResource();
      this.changedResources.forEach(it -> it.setModified(true));
      return result;
    }

    private List<PropagatedChange> propagateSingleChange(final TransactionalChange<EObject> change, final ModelSnapshot previousState) {
      try {
        Preconditions.checkState(!change.getAffectedEObjects().isEmpty(),
          "There are no objects affected by this change:%s%s", System.lineSeparator(), change);
        final AutoCloseable userInteractorChange = this.installUserInteractorForChange(change);
        this.outer.changePropagationProvider.forEach(it -> it.registerObserver(this));
        this.outer.userInteractor.registerUserInputListener(this);
        List<TransactionalChangeWithPreviousState> _xtrycatchfinallyexpression = null;
        try (ModelSnapshot currentState = !outer.changePropagationMode.equals(ChangePropagationMode.SINGLE_STEP) ? this.outer.modelRepository.createSnapshot() : null) {
          Set<ChangePropagationSpecification> allSpecs = change.getAffectedEObjectsMetamodelDescriptors().stream()
              .flatMap(it -> {
                List<ChangePropagationSpecification> specs = this.outer.changePropagationProvider.getChangePropagationSpecifications(it);
                specs.forEach(s -> s.setUserInteractor(this.outer.userInteractor));
                return specs.stream();
              })
              .filter(it -> this.outer.changePropagationProvider.getChangePropagationSpecificationLevel(it) == this.level)
              .collect(Collectors.toCollection(LinkedHashSet::new));
          _xtrycatchfinallyexpression = allSpecs.stream()
              .flatMap(it -> StreamSupport.stream(this.propagateChangeForChangePropagationSpecification(change, previousState, currentState, it).spliterator(), false))
              .toList();
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          this.outer.userInteractor.deregisterUserInputListener(this);
          this.outer.changePropagationProvider.forEach(it -> it.deregisterObserver(this));
          userInteractorChange.close();
        }
        final List<TransactionalChangeWithPreviousState> propagationResultChanges = _xtrycatchfinallyexpression;
        if (ChangePropagator.logger.isDebugEnabled()) {
          String path = String.join(" -> ", this.getPropagationPath());
          String changes = propagationResultChanges.stream()
              .map(c -> String.valueOf(c.change().getAffectedEObjectsMetamodelDescriptors()))
              .collect(Collectors.joining(", "));
          ChangePropagator.logger.debug("Propagated " + path + " -> {" + changes + "}");
        }
        if (ChangePropagator.logger.isTraceEnabled()) {
          String resultChanges = propagationResultChanges.stream()
              .map(r -> "\t" + r.change().getAffectedEObjectsMetamodelDescriptors() + ": " + r)
              .collect(Collectors.joining("\n"));
          ChangePropagator.logger.trace("Result changes:\n" + resultChanges);
        }
        change.setUserInteractions(this.userInteractions);
        CompositeContainerChange<EObject> _createCompositeChange = VitruviusChangeFactory.getInstance().<EObject>createCompositeChange(propagationResultChanges.stream().map(TransactionalChangeWithPreviousState::change).toList());
        final PropagatedChange propagatedChange = new PropagatedChange(change, _createCompositeChange);
        final ArrayList<PropagatedChange> resultingChanges = new ArrayList<PropagatedChange>();
        resultingChanges.add(propagatedChange);
        if (!Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.SINGLE_STEP)) {
          Iterable<PropagatedChange> _propagateTransitiveChanges = this.propagateTransitiveChanges(
              propagationResultChanges.stream().filter(it -> it.change().containsConcreteChange()).collect(Collectors.toList()));
          Iterables.<PropagatedChange>addAll(resultingChanges, _propagateTransitiveChanges);
        }
        return resultingChanges;
      } catch (Throwable _e) {
        throw new RuntimeException(_e);
      }
    }

    private Iterable<PropagatedChange> propagateTransitiveChanges(final Iterable<TransactionalChangeWithPreviousState> transitiveChanges) {
      List<TransactionalChangeWithPreviousState> nonEmptyChanges = StreamSupport.stream(transitiveChanges.spliterator(), false)
          .filter(it -> it.change().containsConcreteChange())
          .collect(Collectors.toList());
      List<TransactionalChangeWithPreviousState> nonLeafChanges;
      if (Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES)) {
        nonLeafChanges = nonEmptyChanges.stream()
            .filter(it -> this.outer.changePropagationProvider.getChangePropagationSpecifications(
                it.change().getAffectedEObjectsMetamodelDescriptor()).size() > 1)
            .collect(Collectors.toList());
      } else {
        nonLeafChanges = nonEmptyChanges;
      }
      return nonLeafChanges.stream()
                    .map(lowerLevelPropagation::propagateChange)
                    .flatMap(it -> Streams.concat(
                            it.propagatedChanges().stream(),
                            new ChangePropagator.ChangePropagation(this.outer, it.change(), this.level, this.lowerLevelPropagation, this).propagateChange().stream()))
                    .toList();
    }

    private Iterable<TransactionalChangeWithPreviousState> propagateChangeForChangePropagationSpecification(final TransactionalChange<EObject> change, final ModelSnapshot previousState, final ModelSnapshot currentState, final ChangePropagationSpecification propagationSpecification) {
      final Runnable _function = () -> propagationSpecification.propagateChanges(change.getEChanges(), this.outer.modelRepository.getCorrespondenceModel(), this.outer.modelRepository, previousState);
      final Iterable<TransactionalChange<EObject>> transitiveChanges = this.outer.modelRepository.recordChanges(_function);
      StreamSupport.stream(transitiveChanges.spliterator(), false)
                   .flatMap(it -> it.getAffectedEObjects().stream())
                   .map(EObject::eResource)
                   .filter(Objects::nonNull)
                   .forEach(this.changedResources::add);
      return StreamSupport.stream(transitiveChanges.spliterator(), false).map(it -> new TransactionalChangeWithPreviousState(it, currentState != null ? currentState.copy() : null)).toList();
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
          .toList();
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
        return List.of("<input change (level " + this.level + ")> in " + this.sourceChange.change().getAffectedEObjectsMetamodelDescriptors());
      } else {
        return Iterables.concat(this.previous.getPropagationPath(),
            List.of(this.sourceChange.change().getAffectedEObjectsMetamodelDescriptors().toString()));
      }
    }

    public ChangePropagation(final ChangePropagator outer, final TransactionalChangeWithPreviousState sourceChange, final int level, final LowerLevelPropagation lowerLevelPropagation, final ChangePropagator.ChangePropagation previous) {
      super();
      this.outer = outer;
      this.sourceChange = sourceChange;
      this.level = level;
      this.lowerLevelPropagation = lowerLevelPropagation;
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
    final List<TransactionalChangeWithPreviousState> resolvedChanges = this.modelRepository.applyChangeAndStorePreviousState(change);
    resolvedChanges.stream().flatMap(it -> it.change().getAffectedEObjects().stream())
        .map(EObject::eResource)
        .filter(Objects::nonNull)
        .forEach(it -> it.setModified(true));
    if (ChangePropagator.logger.isTraceEnabled()) {
      ChangePropagator.logger.trace("Will now propagate these input changes:\n\t" + resolvedChanges.stream().map(it -> it.change().toString()).collect(Collectors.joining("\n\t")));
    }

    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    int maximumLevel = this.changePropagationProvider.getMaximumPropagationSpecificationLevel();
    List<PropagatedChange> result = resolvedChanges.stream().flatMap(it -> propagateChange(it, maximumLevel).propagatedChanges().stream()).toList();
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    return result;
  }

  private PropagationResult propagateChange(final TransactionalChangeWithPreviousState change, final int level) {
    if (level < 0) {
      return new PropagationResult(change, List.of());
    }

    PropagationResult lowerLevelResult = this.propagateChange(change, level - 1);

    LowerLevelPropagation lowerLevelPropagation = (lowerLevelChange) -> propagateChange(lowerLevelChange, level - 1);
    ChangePropagation propagation = new ChangePropagation(this, lowerLevelResult.change(), level, lowerLevelPropagation, null);
    List<PropagatedChange> propagatedChanges = propagation.propagateChange();

    List<PropagatedChange> allPropagatedChanges = new ArrayList<>(lowerLevelResult.propagatedChanges());
    allPropagatedChanges.addAll(propagatedChanges);

    return new PropagationResult(merge(lowerLevelResult.change(), propagatedChanges), allPropagatedChanges);
  }

  private static TransactionalChangeWithPreviousState merge(TransactionalChangeWithPreviousState change, Iterable<PropagatedChange> propagatedChanges) {
    List<EChange<EObject>> eChanges = new ArrayList<>(change.change().getEChanges());

    for (PropagatedChange propagatedChange : propagatedChanges) {
      eChanges.addAll(propagatedChange.getConsequentialChanges().getEChanges());
    }

    return new TransactionalChangeWithPreviousState(
        VitruviusChangeFactory.getInstance().createTransactionalChange(eChanges),
        change.previousState()
    );
  }
}
