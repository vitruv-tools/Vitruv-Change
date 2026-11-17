package tools.vitruv.change.propagation.impl;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Streams;
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

    /**
     * The thread this propagation runs on. It is registered as observer and listener at the
     * specifications and the user interactor, which are shared by all propagations of a virtual
     * model, so it also receives the notifications of propagations on other threads and ignores
     * them.
     */
    private final Thread propagationThread = Thread.currentThread();

    private boolean isOwnNotification() {
      return Thread.currentThread() == this.propagationThread;
    }

    private List<PropagatedChange> propagateChange() {
      List<PropagatedChange> result = this.propagateSingleChange(this.sourceChange.change(), this.sourceChange.previousState());
      this.handleObjectsWithoutResource();
      this.changedResources.forEach(it -> it.setModified(true));
      return result;
    }

    private List<PropagatedChange> propagateSingleChange(final TransactionalChange<EObject> change, final ModelRepositorySnapshot previousState) {
      try (
          ModelRepositorySnapshot currentState =
              outer.changePropagationMode.equals(ChangePropagationMode.SINGLE_STEP)
                  ? null : this.outer.createSnapshotIfUsed()) {
        Preconditions.checkState(!change.getAffectedEObjects().isEmpty(),
          "There are no objects affected by this change:%s%s", System.lineSeparator(), change);
        final AutoCloseable userInteractorChange = this.installUserInteractorForChange(change);
        this.outer.changePropagationProvider.forEach(it -> it.registerObserver(this));
        this.outer.userInteractor.registerUserInputListener(this);
        List<TransactionalChangeWithPreviousState> propagatedChanges;
        try {
          Set<ChangePropagationSpecification> allSpecs = change.getAffectedEObjectsMetamodelDescriptors().stream()
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
          this.outer.userInteractor.deregisterUserInputListener(this);
          this.outer.changePropagationProvider.forEach(it -> it.deregisterObserver(this));
          userInteractorChange.close();
        }
        final List<TransactionalChangeWithPreviousState> propagationResultChanges = propagatedChanges;
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
                    .map(lowerLevelPropagation)
                    .flatMap(it -> Streams.concat(
                            it.propagatedChanges().stream(),
                            new ChangePropagator.ChangePropagation(this.outer, it.change(), this.level, this.lowerLevelPropagation, this).propagateChange().stream()))
                    .toList();
    }

    private Iterable<TransactionalChangeWithPreviousState> propagateChangeForChangePropagationSpecification(final TransactionalChange<EObject> change, final ModelRepositorySnapshot previousState, final ModelRepositorySnapshot currentState, final ChangePropagationSpecification propagationSpecification) {
      this.outer.stepGuard.beforeStep(change, propagationSpecification);
      final Runnable _function = () -> propagationSpecification.propagateChanges(change.getEChanges(), change, this.outer.modelRepository.getCorrespondenceModel(), this.outer.modelRepository, previousState);
      final Iterable<TransactionalChange<EObject>> transitiveChanges = this.outer.modelRepository.recordChanges(_function);
      transitiveChanges.forEach(this.outer.currentAttempt.get().appliedChanges::add);
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
        Preconditions.checkState(
            !this.outer.modelRepository.getCorrespondenceModel().hasCorrespondences(createdObjectWithoutResource),
            "The object %s is part of a correspondence to %s but not in any resource", createdObjectWithoutResource,
            this.outer.modelRepository.getCorrespondenceModel().getCorrespondingEObjects(createdObjectWithoutResource));
        ChangePropagator.logger.warn("Object was created but has no correspondence and is thus lost: " + createdObjectWithoutResource);
      }
    }

    @Override
    public void objectCreated(final EObject createdObject) {
      if (!isOwnNotification()) {
        return;
      }
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
      if (!isOwnNotification()) {
        return;
      }
      this.userInteractions.add(interaction);
      this.outer.currentAttempt.get().userInteractions.add(interaction);
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

    public ChangePropagation(final ChangePropagator outer, final TransactionalChangeWithPreviousState sourceChange, final int level, final Function<TransactionalChangeWithPreviousState, PropagationResult> lowerLevelPropagation, final ChangePropagator.ChangePropagation previous) {
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

  /** What one attempt to propagate a change has applied and asked, to revert and replay it. */
  private static final class Attempt {
    private final List<TransactionalChange<EObject>> appliedChanges = new ArrayList<>();
    private final List<UserInteractionBase> userInteractions = new ArrayList<>();
  }

  /** The attempt running on the current thread (propagations are confined to their thread). */
  private final ThreadLocal<Attempt> currentAttempt = new ThreadLocal<>();

  private volatile PropagationStepGuard stepGuard = PropagationStepGuard.NONE;

  private volatile int maximumAttempts = 10;

  /**
   * Sets the guard that is called before the user's change is applied and before each propagation
   * step, e.g. to acquire locks. See {@link PropagationStepGuard}.
   */
  public void setStepGuard(PropagationStepGuard stepGuard) {
    this.stepGuard = Objects.requireNonNull(stepGuard);
  }

  /** How often a propagation is attempted before an abort is passed to the caller (default 10). */
  public void setMaximumAttempts(int maximumAttempts) {
    Preconditions.checkArgument(maximumAttempts > 0, "at least one attempt is required");
    this.maximumAttempts = maximumAttempts;
  }

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
    return propagateChange(change, observers, result -> { });
  }

  /**
   * Applies, then propagates <code>change</code> through the models in
   * <code>modelRepository</code>, and runs <code>commit</code> on the result before the step guard
   * ends the propagation. With a locking step guard, <code>commit</code> thus runs while the
   * propagation still holds its locks: what it persists (saved resources, a change log record)
   * contains no changes of concurrent propagations to the locked elements, and commits happen in
   * the serialisation order. If <code>commit</code> throws, the changes of the propagation are
   * reverted in memory (not what <code>commit</code> may already have written), the guard ends the
   * propagation as not committed and the exception reaches the caller; it is not retried.
   *
   * @param change - {@link VitruviusChange}
   * @param observers - {@link Iterable} of {@link ChangePropagationObserver}
   * @param commit - persists the propagated changes
   * @return - {@link List} of {@link PropagatedChange}
   */
  public List<PropagatedChange> propagateChange(final VitruviusChange<Uuid> change,
      final Iterable<ChangePropagationObserver> observers,
      final Consumer<List<PropagatedChange>> commit) {
    if (ChangePropagator.logger.isTraceEnabled()) {
      ChangePropagator.logger.trace("Will begin propagation of change :\n\t" + change);
    }
    Preconditions.checkState(currentAttempt.get() == null,
        "A propagation is already running on this thread");
    List<UserInteractionBase> answersToReplay = List.of();
    for (int attemptNumber = 1; ; attemptNumber++) {
      Attempt attempt = new Attempt();
      currentAttempt.set(attempt);
      boolean committed = false;
      try (AutoCloseable replay = replayUserInteractions(answersToReplay)) {
        stepGuard.beginPropagation(change);
        List<PropagatedChange> result = propagateChangeOnce(change, observers, attempt);
        try {
          commit.accept(result);
        } catch (RuntimeException e) {
          logger.debug("Commit failed, reverting {} changes", attempt.appliedChanges.size());
          modelRepository.revertChanges(attempt.appliedChanges);
          throw new CommitFailedException(e);
        }
        committed = true;
        return result;
      } catch (CommitFailedException e) {
        throw e.getCause();
      } catch (Exception e) {
        PropagationAbortedException abort = findAbort(e);
        if (abort == null) {
          throw e instanceof RuntimeException runtimeException
              ? runtimeException : new RuntimeException(e);
        }
        logger.debug("Propagation aborted (attempt {}), reverting {} changes: {}", attemptNumber,
            attempt.appliedChanges.size(), abort.getMessage());
        modelRepository.revertChanges(attempt.appliedChanges);
        if (!attempt.userInteractions.isEmpty()) {
          answersToReplay = List.copyOf(attempt.userInteractions);
        }
        if (attemptNumber >= maximumAttempts) {
          throw abort;
        }
      } finally {
        currentAttempt.remove();
        stepGuard.endPropagation(change, committed);
      }
    }
  }

  /** Carries a failure of the commit past the abort handling (an abort in it is not retried). */
  private static final class CommitFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    CommitFailedException(RuntimeException cause) {
      super(cause);
    }

    @Override
    public synchronized RuntimeException getCause() {
      return (RuntimeException) super.getCause();
    }
  }

  private static PropagationAbortedException findAbort(Throwable throwable) {
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      if (t instanceof PropagationAbortedException abort) {
        return abort;
      }
    }
    return null;
  }

  /** Answers the interactions of a retried propagation with those of the aborted attempt. */
  private AutoCloseable replayUserInteractions(List<UserInteractionBase> answers) {
    if (answers.isEmpty()) {
      return () -> { };
    }
    return this.userInteractor.replaceUserInteractionResultProvider(currentProvider ->
        UserInteractionFactory.instance.createPredefinedInteractionResultProvider(currentProvider,
            answers.toArray(UserInteractionBase[]::new)));
  }

  private List<PropagatedChange> propagateChangeOnce(final VitruviusChange<Uuid> change,
      final Iterable<ChangePropagationObserver> observers, final Attempt attempt) {
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    try {
      return propagateTransactionalChanges(change, attempt);
    } finally {
      this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    }
  }

  private List<PropagatedChange> propagateTransactionalChanges(final VitruviusChange<Uuid> change,
      final Attempt attempt) {
    int maximumLevel = this.changePropagationProvider.getMaximumPropagationSpecificationLevel();

    List<PropagatedChange> result = new ArrayList<>();

    for (TransactionalChange<Uuid> transactionalChange : change.getTransactionalChangeSequence()) {
      try (ModelRepositorySnapshot previousState = createSnapshotIfUsed()) {
        stepGuard.beforeApply(transactionalChange);
        TransactionalChange<EObject> resolvedChange =
            (TransactionalChange<EObject>) this.modelRepository.applyChange(transactionalChange);
        attempt.appliedChanges.add(resolvedChange);
        // a transactional change inside a composite change also carries the composite's annotations
        inheritAnnotations(change, resolvedChange);

        resolvedChange.getAffectedEObjects().stream()
                       .map(EObject::eResource)
                       .filter(Objects::nonNull)
                       .forEach(it -> it.setModified(true));

        if (ChangePropagator.logger.isTraceEnabled()) {
          ChangePropagator.logger.trace("Will now propagate this input change:\n\t" + resolvedChange);
        }

        TransactionalChangeWithPreviousState changeWithPreviousState
            = new TransactionalChangeWithPreviousState(resolvedChange, previousState);
        result.addAll(
            this.propagateChange(changeWithPreviousState, maximumLevel).propagatedChanges());
      }
    }
    return result;
  }

  private PropagationResult propagateChange(
      final TransactionalChangeWithPreviousState change, final int level) {
    if (level < 0) {
      return new PropagationResult(change, List.of());
    }

    PropagationResult lowerLevelResult = this.propagateChange(change, level - 1);

    Function<TransactionalChangeWithPreviousState, PropagationResult> lowerLevelPropagation
        = (lowerLevelChange) -> propagateChange(lowerLevelChange, level - 1);

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
   * Creates a snapshot of the model repository if a specification uses the state before a change.
   * Otherwise returns <code>null</code>, as creating a snapshot copies all models.
   */
  private ModelRepositorySnapshot createSnapshotIfUsed() {
    boolean previousStateUsed = StreamSupport.stream(changePropagationProvider.spliterator(), false)
        .anyMatch(ChangePropagationSpecification::usesPreviousState);
    return previousStateUsed ? modelRepository.createSnapshot() : null;
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
