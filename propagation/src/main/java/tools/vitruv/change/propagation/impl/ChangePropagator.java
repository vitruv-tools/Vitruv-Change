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
import java.util.function.Consumer;
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

  /**
   * Collects the invocations of the specifications during one run of the propagation. The run is
   * halted as soon as an invocation is not {@link ChangePropagationStatus#SUCCEEDED}, or when the
   * {@link PropagationStepGuard} aborts it.
   */
  private static final class PropagationRun {
    private final PropagationStepGuard guard;

    private ChangePropagationResult result = ChangePropagationResult.UNHANDLED;

    /** The changes applied in this run, in order, to revert them when it halts under a guard. */
    private final List<TransactionalChange<EObject>> appliedChanges = new ArrayList<>();

    /** The user interactions of this run, to answer them alike when it is resubmitted. */
    private final List<UserInteractionBase> userInteractions = new ArrayList<>();

    /** The abort that halted this run, if any. */
    private PropagationAbortedException abort;

    private PropagationRun(PropagationStepGuard guard) {
      this.guard = guard;
    }

    private void add(ChangePropagationInvocation invocation) {
      result = result.append(ChangePropagationResult.of(invocation));
    }

    private void abort(PropagationAbortedException abort) {
      if (this.abort == null) {
        this.abort = abort;
      }
    }

    private boolean halted() {
      return abort != null || !result.succeeded();
    }
  }

  private static class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
    private final ChangePropagator outer;

    private final TransactionalChangeWithPreviousState sourceChange;

    private final int level;

    private final Function<TransactionalChangeWithPreviousState, PropagationResult> lowerLevelPropagation;

    private final ChangePropagator.ChangePropagation previous;

    private final PropagationRun run;

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
      if (!this.run.halted()) {
        this.handleObjectsWithoutResource();
      }
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
                specs.forEach(s -> {
                  s.setUserInteractor(this.outer.userInteractor);
                  s.setEChangeApplicationController(this.outer.applicationController);
                });
                return specs.stream();
              })
              .filter(it -> this.outer.changePropagationProvider.getChangePropagationSpecificationLevel(it) == this.level)
              .collect(Collectors.toCollection(LinkedHashSet::new));
          propagatedChanges = new ArrayList<>();
          for (ChangePropagationSpecification specification : allSpecs) {
            Iterables.addAll(propagatedChanges,
                this.propagateChangeForChangePropagationSpecification(
                    change, previousState, currentState, specification));
            if (this.run.halted()) {
              break;
            }
          }
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
        if (!Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.SINGLE_STEP)
            && !this.run.halted()) {
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
      List<PropagatedChange> result = new ArrayList<>();
      for (TransactionalChangeWithPreviousState nonLeafChange : nonLeafChanges) {
        if (this.run.halted()) {
          break;
        }
        PropagationResult lowerLevelResult = this.lowerLevelPropagation.apply(nonLeafChange);
        result.addAll(lowerLevelResult.propagatedChanges());
        if (this.run.halted()) {
          break;
        }
        result.addAll(new ChangePropagator.ChangePropagation(
            this.outer, lowerLevelResult.change(), this.level, this.lowerLevelPropagation, this,
            this.run).propagateChange());
      }
      return result;
    }

    private Iterable<TransactionalChangeWithPreviousState> propagateChangeForChangePropagationSpecification(final TransactionalChange<EObject> change, final ModelRepositorySnapshot previousState, final ModelRepositorySnapshot currentState, final ChangePropagationSpecification propagationSpecification) {
      try {
        this.run.guard.beforeStep(change, propagationSpecification);
      } catch (PropagationAbortedException abort) {
        this.run.abort(abort);
        return List.of();
      }
      final ChangePropagationInvocation[] invocation = new ChangePropagationInvocation[1];
      final Runnable tryPropagation = () -> {
        try {
          invocation[0] = propagationSpecification.propagateChanges(
              change.getEChanges(), change, this.outer.modelRepository.getCorrespondenceModel(),
              this.outer.modelRepository, previousState);
        } catch (RuntimeException e) {
          PropagationAbortedException abort = findAbort(e);
          if (abort != null) {
            // aborted from within the specification, e.g. while acquiring a lock
            this.run.abort(abort);
            invocation[0] = ChangePropagationInvocation.blocked(
                propagationSpecification, change.getEChanges(), List.of());
          } else {
            invocation[0] = ChangePropagationInvocation.failed(
                propagationSpecification, change.getEChanges(), List.of(), e);
          }
        }
      };
      final Iterable<TransactionalChange<EObject>> transitiveChanges =
          this.outer.modelRepository.recordChanges(tryPropagation);
      transitiveChanges.forEach(this.run.appliedChanges::add);
      this.run.add(invocation[0]);
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
        ChangePropagator.logger.warn(
            "Object was created but has no correspondence and is thus lost: {}",
            createdObjectWithoutResource);
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
      this.run.userInteractions.add(interaction);
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

    public ChangePropagation(
        final ChangePropagator outer,
        final TransactionalChangeWithPreviousState sourceChange,
        final int level,
        final Function<TransactionalChangeWithPreviousState, PropagationResult>
            lowerLevelPropagation,
        final ChangePropagator.ChangePropagation previous,
        final PropagationRun run) {
      super();
      this.outer = outer;
      this.sourceChange = sourceChange;
      this.level = level;
      this.lowerLevelPropagation = lowerLevelPropagation;
      this.previous = previous;
      this.run = run;
    }
  }

  private static final Logger logger = LogManager.getLogger(ChangePropagator.class);

  private final ChangeRecordingModelRepository modelRepository;

  private final ChangePropagationSpecificationProvider changePropagationProvider;

  private final InternalUserInteractor userInteractor;

  private final ChangePropagationMode changePropagationMode;

  private final EChangeApplicationController<EObject> applicationController;

  private volatile PropagationStepGuard stepGuard = PropagationStepGuard.NONE;

  /**
   * Sets the guard that is called before the user's change is applied and before each propagation
   * step, e.g. to acquire locks. See {@link PropagationStepGuard}.
   *
   * @param stepGuard the guard, {@link PropagationStepGuard#NONE} for none
   */
  public void setStepGuard(PropagationStepGuard stepGuard) {
    this.stepGuard = Objects.requireNonNull(stepGuard);
  }

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated transitively and cyclic, i.e. with
   * {@link ChangePropagationMode#TRANSITIVE_CYCLIC}.
   */
  public ChangePropagator(
      final ChangeRecordingModelRepository modelRepository,
      final ChangePropagationSpecificationProvider changePropagationProvider,
      final InternalUserInteractor userInteractor) {
    this(modelRepository, changePropagationProvider, userInteractor, ChangePropagationMode.TRANSITIVE_CYCLIC);
  }

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated using the given <code>mode</code>.
   */
  public ChangePropagator(
      final ChangeRecordingModelRepository modelRepository,
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
   * The change propagation specifications apply the changes they issue themselves through the
   * given <code>applicationController</code>.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository,
      final ChangePropagationSpecificationProvider changePropagationProvider,
      final InternalUserInteractor userInteractor, final ChangePropagationMode mode,
      final EChangeApplicationController<EObject> applicationController) {
    this.modelRepository = modelRepository;
    this.changePropagationProvider = changePropagationProvider;
    this.userInteractor = userInteractor;
    this.changePropagationMode = mode;
    this.applicationController = applicationController;
  }

  /**
   * Applies, then propagates <code>change</code>
   * through the models in <code>modelRepository</code>.
   *
   * <p>If a specification returns an invocation that is not
   * {@link ChangePropagationStatus#SUCCEEDED}, the propagation halts: no further specification,
   * level, transitive change or transactional change of <code>change</code> is processed. Without
   * a {@link PropagationStepGuard}, the changes that were already executed stay applied, and
   * nothing is undone; with a guard, they are reverted (see {@link PropagationStepGuard}). No
   * exception is thrown for this; the {@link PropagationReport} contains the result.
   *
   * @param change - {@link VitruviusChange}
   * @param observers - {@link Iterable} of {@link ChangePropagationObserver}
   * @return - the {@link PropagationReport} with the propagated changes and the
   *     {@link ChangePropagationResult} of the run
   */
  public PropagationReport propagateChangeWithResult(
      final VitruviusChange<Uuid> change,
      final Iterable<ChangePropagationObserver> observers) {
    return propagateChangeWithResult(change, observers, List.of(), propagatedChanges -> { });
  }

  /**
   * Applies, then propagates <code>change</code> like
   * {@link #propagateChangeWithResult(VitruviusChange, Iterable)}, answers the first user
   * interactions with <code>answers</code>, and runs <code>commit</code> on the propagated changes
   * if the propagation succeeded, before the {@link PropagationStepGuard} ends it. With a locking
   * guard, <code>commit</code> thus runs while the propagation still holds its locks: what it
   * persists (saved resources, a change log record) contains no changes of concurrent propagations
   * to the locked elements, and commits happen in the serialization order.
   *
   * <p>If the propagation halts or <code>commit</code> throws, and a guard is set, the changes of
   * the propagation are reverted in memory before the guard ends it (not what <code>commit</code>
   * may already have written). An exception of <code>commit</code> reaches the caller.
   *
   * @param change - {@link VitruviusChange}
   * @param observers - {@link Iterable} of {@link ChangePropagationObserver}
   * @param answers - the answers to the first user interactions, e.g. the
   *     {@link PropagationReport#userInteractions()} of a halted propagation of the same change
   *     that is resubmitted
   * @param commit - persists the propagated changes
   * @return - the {@link PropagationReport} with the propagated changes and the
   *     {@link ChangePropagationResult} of the run
   */
  public PropagationReport propagateChangeWithResult(
      final VitruviusChange<Uuid> change,
      final Iterable<ChangePropagationObserver> observers,
      final List<UserInteractionBase> answers,
      final Consumer<List<PropagatedChange>> commit) {
    if (ChangePropagator.logger.isTraceEnabled()) {
      ChangePropagator.logger.trace("Will begin propagation of change :\n\t {}", change);
    }
    final PropagationRun run = new PropagationRun(this.stepGuard);
    boolean committed = false;
    this.modelRepository.beginPropagationAttempt();
    try (AutoCloseable replay = replayUserInteractions(answers)) {
      List<PropagatedChange> result = propagateRun(change, observers, run);
      if (!run.halted()) {
        commit.accept(result);
        committed = true;
      }
      return new PropagationReport(result, run.result, run.userInteractions, run.abort);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      // only closing the replay of user interactions throws checked exceptions
      throw new IllegalStateException(e);
    } finally {
      if (!committed && run.guard != PropagationStepGuard.NONE) {
        ChangePropagator.logger.debug("Propagation did not complete, reverting {} changes",
            run.appliedChanges.size());
        this.modelRepository.revertChanges(run.appliedChanges);
      }
      this.modelRepository.endPropagationAttempt();
      run.guard.endPropagation(change, committed);
    }
  }

  private List<PropagatedChange> propagateRun(final VitruviusChange<Uuid> change,
      final Iterable<ChangePropagationObserver> observers, final PropagationRun run) {
    List<PropagatedChange> result = new ArrayList<>();
    try {
      run.guard.beginPropagation(change);
    } catch (PropagationAbortedException abort) {
      run.abort(abort);
      return result;
    }

    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    try {
      int maximumLevel = this.changePropagationProvider.getMaximumPropagationSpecificationLevel();

      for (TransactionalChange<Uuid> transactionalChange :
          change.getTransactionalChangeSequence()) {
        if (run.halted()) {
          break;
        }
        try {
          run.guard.beforeApply(transactionalChange);
        } catch (PropagationAbortedException abort) {
          run.abort(abort);
          break;
        }
        try (ModelRepositorySnapshot previousState = createSnapshotIfUsed()) {
          TransactionalChange<EObject> resolvedChange =
              (TransactionalChange<EObject>) this.modelRepository.applyChange(transactionalChange);
          run.appliedChanges.add(resolvedChange);
          // a transactional change inside a composite change also carries the composite's
          // annotations
          inheritAnnotations(change, resolvedChange);

          resolvedChange.getAffectedEObjects().stream()
                         .map(EObject::eResource)
                         .filter(Objects::nonNull)
                         .forEach(it -> it.setModified(true));

          if (ChangePropagator.logger.isTraceEnabled()) {
            ChangePropagator.logger.trace(
                "Will now propagate this input change:\n\t" + resolvedChange);
          }

          TransactionalChangeWithPreviousState changeWithPreviousState
              = new TransactionalChangeWithPreviousState(resolvedChange, previousState);
          result.addAll(
              this.propagateChange(changeWithPreviousState, maximumLevel, run).propagatedChanges());
        }
      }
    } finally {
      this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    }
    return result;
  }

  private static PropagationAbortedException findAbort(Throwable throwable) {
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      if (t instanceof PropagationAbortedException abort) {
        return abort;
      }
    }
    return null;
  }

  /** Answers the first user interactions of a propagation with the given answers. */
  private AutoCloseable replayUserInteractions(List<UserInteractionBase> answers) {
    if (answers.isEmpty()) {
      return () -> { };
    }
    return this.userInteractor.replaceUserInteractionResultProvider(currentProvider ->
        UserInteractionFactory.instance.createPredefinedInteractionResultProvider(currentProvider,
            answers.toArray(UserInteractionBase[]::new)));
  }

  /**
   * Applies, then propagates <code>change</code>
   * through the models in <code>modelRepository</code>.
   *
   * <p>This is {@link #propagateChangeWithResult} without the report: if a specification failed
   * with an error, the error is rethrown after the propagation halted; a blocked specification
   * only halts the propagation. If a {@link PropagationStepGuard} is set, a blocked or aborted
   * propagation has been reverted, and a {@link PropagationAbortedException} is thrown, so that
   * the caller can resubmit the change.
   *
   * @param change - {@link VitruviusChange}
   * @param observers - {@link Iterable} of {@link ChangePropagationObserver}
   * @return - {@link List} of {@link PropagatedChange}
   */
  public List<PropagatedChange> propagateChange(final VitruviusChange<Uuid> change, final Iterable<ChangePropagationObserver> observers) {
    boolean guarded = this.stepGuard != PropagationStepGuard.NONE;
    PropagationReport report = propagateChangeWithResult(change, observers);
    if (guarded && report.status() == ChangePropagationStatus.BLOCKED) {
      throw report.abort() != null ? report.abort()
          : new PropagationAbortedException("The propagation was blocked and has been reverted");
    }
    if (report.result().status() == ChangePropagationStatus.FAILED) {
      Throwable cause = report.result().cause().orElse(null);
      if (cause instanceof RuntimeException runtimeException) {
        throw runtimeException;
      }
      if (cause instanceof Error error) {
        throw error;
      }
      if (cause != null) {
        throw new RuntimeException(cause);
      }
    }
    return report.propagatedChanges();
  }

  private PropagationResult propagateChange(
      final TransactionalChangeWithPreviousState change, final int level,
      final PropagationRun run) {
    if (level < 0) {
      return new PropagationResult(change, List.of());
    }

    PropagationResult lowerLevelResult = this.propagateChange(change, level - 1, run);
    if (run.halted()) {
      return lowerLevelResult;
    }

    Function<TransactionalChangeWithPreviousState, PropagationResult> lowerLevelPropagation
        = lowerLevelChange -> propagateChange(lowerLevelChange, level - 1, run);

    ChangePropagation propagation = new ChangePropagation(
        this, lowerLevelResult.change(), level, lowerLevelPropagation, null, run);
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
