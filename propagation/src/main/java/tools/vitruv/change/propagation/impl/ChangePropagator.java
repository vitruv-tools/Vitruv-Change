package tools.vitruv.change.propagation.impl;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
import tools.vitruv.change.composite.description.Annotatable;
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
  private static class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
    private final ChangePropagator outer;

    private final TransactionalChangeWithPreviousState sourceChange;

    private final int level;

    private final Function<TransactionalChangeWithPreviousState, PropagationResult> lowerLevelPropagation;

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

    private List<PropagatedChange> propagateSingleChange(
        TransactionalChange<EObject> change, ModelRepositorySnapshot previousState) {
      try (
          ModelRepositorySnapshot currentState =
              !(outer.changePropagationMode == ChangePropagationMode.SINGLE_STEP)
                  ? null
                  : this.outer.modelRepository.createSnapshot()) {
        checkState(!change.getAffectedEObjects().isEmpty(),
            "There are no objects affected by this change:%s%s", System.lineSeparator(), change);
        AutoCloseable userInteractorChange = installUserInteractorForChange(change);
        outer.changePropagationProvider.forEach(it -> it.registerObserver(this));
        outer.userInteractor.registerUserInputListener(this);
        List<TransactionalChangeWithPreviousState> propagatedChanges;

        try {
          Set<ChangePropagationSpecification> allSpecs = change
              .getAffectedEObjectsMetamodelDescriptors()
              .stream()
              .flatMap(it -> {
                List<ChangePropagationSpecification> specs = this.outer.changePropagationProvider.getChangePropagationSpecifications(it);
                specs.forEach(s -> s.setUserInteractor(this.outer.userInteractor));
                return specs.stream();
              })
              .filter(it -> this.outer.changePropagationProvider.getChangePropagationSpecificationLevel(it) == this.level)
              .collect(Collectors.toCollection(LinkedHashSet::new));
          propagatedChanges = allSpecs.stream()
              .flatMap(it -> StreamSupport.stream(this.propagateChangeForChangePropagationSpecification(change, previousState, currentState, it).spliterator(), false))
              .toList();
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          outer.userInteractor.deregisterUserInputListener(this);
          outer.changePropagationProvider.forEach(it -> it.deregisterObserver(this));
          userInteractorChange.close();
        }

        final List<TransactionalChangeWithPreviousState> propagationResultChanges = propagatedChanges;
        if (ChangePropagator.logger.isDebugEnabled()) {
          String path = String.join(" -> ", this.getPropagationPath());
          String changes = propagationResultChanges.stream()
              .map(c -> String.valueOf(c.change().getAffectedEObjectsMetamodelDescriptors()))
              .collect(Collectors.joining(", "));
          ChangePropagator.logger.debug("Propagated {} -> {{}}", path, changes);
        }
        if (ChangePropagator.logger.isTraceEnabled()) {
          String resultChanges = propagationResultChanges.stream()
              .map(r -> "\t" + r.change().getAffectedEObjectsMetamodelDescriptors() + ": " + r)
              .collect(Collectors.joining("\n"));
          ChangePropagator.logger.trace("Result changes:\n {}", resultChanges);
        }
        change.setUserInteractions(this.userInteractions);
        CompositeContainerChange<EObject> _createCompositeChange = VitruviusChangeFactory.getInstance().<EObject>createCompositeChange(propagationResultChanges.stream().map(TransactionalChangeWithPreviousState::change).toList());
        final PropagatedChange propagatedChange = new PropagatedChange(change, _createCompositeChange);
        final List<PropagatedChange> resultingChanges = new ArrayList<>();
        resultingChanges.add(propagatedChange);

        if (!Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.SINGLE_STEP)) {
          Iterable<PropagatedChange> propagatedTransitiveChanges = this.propagateTransitiveChanges(
              propagationResultChanges.stream()
                  .filter(it -> it.change().containsConcreteChange())
                  .toList());
          Iterables.addAll(resultingChanges, propagatedTransitiveChanges);
        }
        return resultingChanges;
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }

    private Iterable<PropagatedChange> propagateTransitiveChanges(final Iterable<TransactionalChangeWithPreviousState> transitiveChanges) {
      List<TransactionalChangeWithPreviousState> nonEmptyChanges = StreamSupport.stream(transitiveChanges.spliterator(), false)
          .filter(it -> it.change().containsConcreteChange())
          .toList()
      List<TransactionalChangeWithPreviousState> nonLeafChanges;
      if (Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES)) {
        nonLeafChanges = nonEmptyChanges.stream()
            .filter(it -> this.outer.changePropagationProvider.getChangePropagationSpecifications(
                it.change().getAffectedEObjectsMetamodelDescriptor()).size() > 1)
            .toList()
      } else {
        nonLeafChanges = nonEmptyChanges;
      }
      return nonLeafChanges.stream()
                    .map(lowerLevelPropagation)
                    .flatMap(it -> Streams.concat(
                            it.propagatedChanges().stream(),
                            new ChangePropagator.ChangePropagation(this.outer, it.change(), this.level, this.lowerLevelPropagation, this).propagateChange().stream()))
                    .toList();
    }

    private Iterable<TransactionalChangeWithPreviousState> propagateChangeForChangePropagationSpecification(
        TransactionalChange<EObject> change,
        ModelRepositorySnapshot previousState,
        ModelRepositorySnapshot currentState,
        ChangePropagationSpecification propagationSpecification) {
      final RuntimeException [] propagationFailure = new RuntimeException[1];
      final Runnable changePropagationApplicator = () -> {
        try {
          propagationSpecification.propagateChanges(change.getEChanges(),
              change,
              outer.modelRepository.getCorrespondenceModel(),
              outer.modelRepository,
              previousState);
        } catch (RuntimeException e) {
          propagationFailure[0] = e;
        }
      };
      final Iterable<TransactionalChange<EObject>> transitiveChanges =
          this.outer.modelRepository.recordChanges(changePropagationApplicator);
      transitiveChanges.forEach(it -> this.outer.registerApplied(it.getEChanges()));
      if (propagationFailure[0] != null) {
        throw propagationFailure[0];
      }
      // consequential changes keep the annotations of the change that caused them
      transitiveChanges.forEach(it -> inheritAnnotations(change, it));

      StreamSupport.stream(transitiveChanges.spliterator(), false)
                   .flatMap(it -> it.getAffectedEObjects().stream())
                   .map(EObject::eResource)
                   .filter(Objects::nonNull)
                   .forEach(this.changedResources::add);
      return StreamSupport.stream(transitiveChanges.spliterator(), false).map(it -> new TransactionalChangeWithPreviousState(it, currentState)).toList();
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
        checkState(
            !this.outer.modelRepository.getCorrespondenceModel().hasCorrespondences(createdObjectWithoutResource),
            "The object %s is part of a correspondence to %s but not in any resource", createdObjectWithoutResource,
            this.outer.modelRepository.getCorrespondenceModel().getCorrespondingEObjects(createdObjectWithoutResource));
        ChangePropagator.logger.warn(
            "Object was created but has no correspondence and is thus lost: {}",
            createdObjectWithoutResource);
      }
    }

    @Override
    public void objectCreated(EObject createdObject) {
      this.createdObjects.add(createdObject);
    }

    @Override
    public void changePropagationStarted(
        ChangePropagationSpecification specification, EChange<EObject> change) {
      // ChangePropagation ignores change propagation start events
    }

    @Override
    public void changePropagationStopped(
        ChangePropagationSpecification specification, EChange<EObject> change) {
      // ChangePropagation ignores change propagation stop events
    }

    @Override
    public void onUserInteractionReceived(final UserInteractionBase interaction) {
      this.userInteractions.add(interaction);
    }

    @Override
    public String toString() {
      return "propagate " + String.join(" -> ", this.getPropagationPath())
          + ": " + this.sourceChange;
    }

    private Iterable<String> getPropagationPath() {
      if (this.previous == null) {
        return List.of("<input change (level " + this.level + ")> in " +
            this.sourceChange.change().getAffectedEObjectsMetamodelDescriptors());
      } else {
        return Iterables.concat(this.previous.getPropagationPath(),
            List.of(this.sourceChange.change().getAffectedEObjectsMetamodelDescriptors().toString()));
      }
    }

    public ChangePropagation(
        final ChangePropagator outer,
        final TransactionalChangeWithPreviousState sourceChange,
        final int level,
        final Function<TransactionalChangeWithPreviousState, PropagationResult> lowerLevelPropagation,
        final ChangePropagator.ChangePropagation previous) {
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
  private final EChangeApplicationController<EObject> applicationController;

  /**
   * Log of all changes applied during the current top-level propagation, newest first.
   * Used to undo the propagation if it fails.
   */
  private Deque<EChange<EObject>> appliedChanges = new ArrayDeque<>();

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated transitively and cyclic, i.e. with
   * {@link ChangePropagationMode#TRANSITIVE_CYCLIC}.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository,
                          final ChangePropagationSpecificationProvider changePropagationProvider,
                          final InternalUserInteractor userInteractor) {
    this(modelRepository,
        changePropagationProvider,
        userInteractor,
        ChangePropagationMode.TRANSITIVE_CYCLIC);
  }

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated using the given <code>mode</code>.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository,
                          final ChangePropagationSpecificationProvider changePropagationProvider,
                          final InternalUserInteractor userInteractor,
                          final ChangePropagationMode mode) {
    this(modelRepository, changePropagationProvider, userInteractor, mode,
        new DefaultEChangeApplicationController());
  }

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated using the given <code>mode</code>.
   * The application and undo of changes is controlled by the given
   * <code>applicationController</code>.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository,
                          final ChangePropagationSpecificationProvider changePropagationProvider,
                          final InternalUserInteractor userInteractor,
                          final ChangePropagationMode mode,
                          final EChangeApplicationController<EObject> applicationController) {
    this.modelRepository = modelRepository;
    this.changePropagationProvider = changePropagationProvider;
    this.userInteractor = userInteractor;
    this.changePropagationMode = mode;
    this.applicationController = applicationController;
  }

  /**
   * Registers {@code changes} as applied to the models, so that they
   * are undone if the propagation fails.
   *
   * @param changes {@link Iterable}
   * @see {@link ChangePropagator#undoAppliedChanges}
   */
  private void registerApplied(final Iterable<EChange<EObject>> changes) {
    for (EChange<EObject> appliedChange : changes) {
      this.applicationController.prepare(appliedChange);
      this.appliedChanges.push(appliedChange);
    }
  }

  /**
   * Undoes all changes applied during the current propagation step, newest first.
   */
  private void undoAppliedChanges() {
    for (EChange<EObject> appliedChange : this.appliedChanges) {
      this.applicationController.undo(appliedChange);
    }
  }

  private void finishAppliedChanges() {
    for (EChange<EObject> appliedChange : this.appliedChanges) {
      this.applicationController.finish(appliedChange);
    }
    this.appliedChanges = new ArrayDeque<>();
  }

  /**
   * Applies, then propagates <code>change</code>
   * through the models in <code>modelRepository</code>.
   *
   * @param change - {@link VitruviusChange}
   * @param observers - {@link Iterable} of {@link ChangePropagationObserver}
   * @return - {@link List} of {@link PropagatedChange}
   */
  public List<PropagatedChange> propagateChange(
      final VitruviusChange<Uuid> change,
      final Iterable<ChangePropagationObserver> observers) {
    if (ChangePropagator.logger.isTraceEnabled()) {
      ChangePropagator.logger.trace("Will begin propagation of change :\n\t{}", change);
    }

    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    int maximumLevel = this.changePropagationProvider.getMaximumPropagationSpecificationLevel();
    List<PropagatedChange> result = new ArrayList<>();

    try {
      for (TransactionalChange<Uuid> transactionalChange : change.getTransactionalChangeSequence()) {
        propagateTransactionalChange(change, transactionalChange, maximumLevel, result);
      }
    } catch (RuntimeException e) {
      // Undo the entire change, including its source changes
      undoAppliedChanges();
      throw e;
    } finally {
      finishAppliedChanges();
      this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    }

    return result;
  }

  private void propagateTransactionalChange(
      final VitruviusChange<Uuid> change,
      final TransactionalChange<Uuid> transactionalChange,
      final int maximumLevel,
      final List<PropagatedChange> result) {
    try (ModelRepositorySnapshot previousState = this.modelRepository.createSnapshot()) {
      TransactionalChange<EObject> resolvedChange =
          (TransactionalChange<EObject>) this.modelRepository.applyChange(transactionalChange);
      // the source change is already applied; it should be undone after its consequential changes
      registerApplied(resolvedChange.getEChanges());

      inheritAnnotations(change, resolvedChange);
      resolvedChange.getAffectedEObjects().stream()
                     .map(EObject::eResource)
                     .filter(Objects::nonNull)
                     .forEach(it -> it.setModified(true));

      if (ChangePropagator.logger.isTraceEnabled()) {
        ChangePropagator.logger.trace(
            "Will now propagate this input change:\n\t {}",
            resolvedChange);
      }

      TransactionalChangeWithPreviousState changeWithPreviousState
          = new TransactionalChangeWithPreviousState(resolvedChange, previousState);
      result.addAll(
          this.propagateChange(changeWithPreviousState, maximumLevel).propagatedChanges());
    }
  }

  private PropagationResult propagateChange(
      final TransactionalChangeWithPreviousState change, final int level) {
    if (level < 0) {
      return new PropagationResult(change, List.of());
    }

    PropagationResult lowerLevelResult = this.propagateChange(change, level - 1);
    Function<TransactionalChangeWithPreviousState, PropagationResult> lowerLevelPropagation
        = lowerLevelChange -> propagateChange(lowerLevelChange, level - 1);

    ChangePropagation propagation = new ChangePropagation(
        this, lowerLevelResult.change(), level, lowerLevelPropagation, null);
    List<PropagatedChange> propagatedChanges = propagation.propagateChange();

    List<PropagatedChange> allPropagatedChanges
        = new ArrayList<>(lowerLevelResult.propagatedChanges());
    allPropagatedChanges.addAll(propagatedChanges);

    return new PropagationResult(
        merge(lowerLevelResult.change(), propagatedChanges),
        allPropagatedChanges);
  }

  private static TransactionalChangeWithPreviousState merge(
      TransactionalChangeWithPreviousState change, Iterable<PropagatedChange> propagatedChanges) {
    List<EChange<EObject>> eChanges = new ArrayList<>(change.change().getEChanges());

    for (PropagatedChange propagatedChange : propagatedChanges) {
      eChanges.addAll(propagatedChange.getConsequentialChanges().getEChanges());
    }

    TransactionalChange<EObject> mergedChange =
        VitruviusChangeFactory.getInstance().createTransactionalChange(eChanges);
    inheritAnnotations(change.change(), mergedChange);
    return new TransactionalChangeWithPreviousState(mergedChange, change.previousState());
  }

  /**
   * Copies the annotations of <code>source</code> to <code>target</code>,
   * unless <code>target</code> already has an annotation of the same type.
   */
  private static void inheritAnnotations(Annotatable source, Annotatable target) {
    source.getAnnotations().forEach((type, value) -> {
      if (target.getAnnotation(type).isEmpty()) {
        setAnnotation(target, type, value);
      }
    });
  }

  private static <T> void setAnnotation(Annotatable target, Class<T> type, Object value) {
    target.setAnnotation(type, type.cast(value));
  }
}
