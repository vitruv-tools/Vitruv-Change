package tools.vitruv.change.propagation.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
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
import tools.vitruv.change.interaction.PendingUserInteraction;
import tools.vitruv.change.interaction.PendingUserInteractionException;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.impl.DeferringInteractionResultProvider;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.propagation.ChangePropagationObserver;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationProvider;
import tools.vitruv.change.propagation.ChangeRecordingModelRepository;
import tools.vitruv.change.propagation.NonDeferrablePropagationException;
import tools.vitruv.change.propagation.PropagationBlockReason;
import tools.vitruv.change.propagation.QueuedChange;
import tools.vitruv.change.propagation.QueuedPropagationStep;

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

    /** The outcome of propagating one transactional change through all specifications once. */
    private record StepResult(
        PropagatedChange propagatedChange, List<TransactionalChange<EObject>> resultChanges) {}

    private List<PropagatedChange> propagateSingleChange(final TransactionalChange<EObject> change) {
      final StepResult step = this.propagateOneStep(change);
      final ArrayList<PropagatedChange> resultingChanges = new ArrayList<PropagatedChange>();
      resultingChanges.add(step.propagatedChange());
      if (!Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.SINGLE_STEP)) {
        try {
          Iterable<PropagatedChange> transitivelyPropagated =
              this.propagateTransitiveChanges(
                  step.resultChanges().stream()
                      .filter(TransactionalChange::containsConcreteChange)
                      .toList());
          Iterables.<PropagatedChange>addAll(resultingChanges, transitivelyPropagated);
        } catch (RuntimeException e) {
          // Same wrapping as when transitive propagation ran inside propagateOneStep's try block.
          // Only runtime exceptions get here: each nested step already wraps everything it throws.
          throw new RuntimeException(e);
        }
      }
      return resultingChanges;
    }

    /**
     * Propagates a change as one step of the propagation queue: a single propagation step followed
     * by the bookkeeping {@link #propagateChanges()} does at the end of a propagation.
     */
    private StepResult propagateQueuedStep(final TransactionalChange<EObject> change) {
      final StepResult step = this.propagateOneStep(change);
      this.handleObjectsWithoutResource();
      this.changedResources.forEach(it -> it.setModified(true));
      return step;
    }

    /**
     * Propagates the given change through every applicable specification once, without propagating
     * the resulting changes further.
     */
    private StepResult propagateOneStep(final TransactionalChange<EObject> change) {
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
          _xtrycatchfinallyexpression = this.propagateThroughSpecifications(change, allSpecs);
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
        return new StepResult(propagatedChange, propagationResultChanges);
      } catch (Throwable _e) {
        throw new RuntimeException(_e);
      }
    }

    /**
     * Propagates the change through the given specifications in order. A pending user interaction
     * after an earlier specification already modified models is reported as non-deferrable.
     */
    private List<TransactionalChange<EObject>> propagateThroughSpecifications(
        final TransactionalChange<EObject> change,
        final Set<ChangePropagationSpecification> specifications) {
      final List<TransactionalChange<EObject>> results = new ArrayList<>();
      for (final ChangePropagationSpecification specification : specifications) {
        try {
          Iterables.addAll(
              results,
              this.propagateChangeForChangePropagationSpecification(change, specification));
        } catch (final RuntimeException e) {
          if (PendingUserInteractionException.findIn(e) != null
              && results.stream().anyMatch(TransactionalChange::containsConcreteChange)) {
            throw new NonDeferrablePropagationException(
                "A user interaction is pending, but propagating this change already modified"
                    + " models, so it cannot be deferred and retried without repeating those"
                    + " modifications: "
                    + change,
                e);
          }
          throw e;
        }
      }
      return results;
    }

    private Iterable<PropagatedChange> propagateTransitiveChanges(
        final Iterable<TransactionalChange<EObject>> transitiveChanges) {
      List<TransactionalChange<EObject>> nonLeafChanges =
          this.outer.selectTransitiveChanges(transitiveChanges);
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
          propagationSpecification.propagateChange(eChange, change,
              this.outer.modelRepository.getCorrespondenceModel(), this.outer.modelRepository);
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

  /**
   * Selects which of the changes produced by a propagation step are propagated further, according
   * to the {@link ChangePropagationMode}: only changes with concrete changes, and for {@link
   * ChangePropagationMode#TRANSITIVE_EXCEPT_LEAVES} only those that more than one specification
   * handles.
   */
  private List<TransactionalChange<EObject>> selectTransitiveChanges(
      final Iterable<TransactionalChange<EObject>> transitiveChanges) {
    List<TransactionalChange<EObject>> nonEmptyChanges =
        StreamSupport.stream(transitiveChanges.spliterator(), false)
            .filter(TransactionalChange::containsConcreteChange)
            .collect(Collectors.toList());
    if (Objects.equals(
        this.changePropagationMode, ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES)) {
      return nonEmptyChanges.stream()
          .filter(
              it ->
                  this.changePropagationProvider
                          .getChangePropagationSpecifications(
                              it.getAffectedEObjectsMetamodelDescriptor())
                          .size()
                      > 1)
          .collect(Collectors.toList());
    }
    return nonEmptyChanges;
  }

  // ---------------------------------------------------------------------------------------------
  // Change propagation queue
  //
  // Changes are enqueued with a task id and propagated one queue item at a time, so that a change
  // waiting for a user interaction or a transaction does not hold up the others. A client change is
  // the root of a tree of items: on its first propagation it is applied to the models, and every
  // change produced by propagating an item is queued as that item's child, directly at the front of
  // the queue so that the order is the same as for propagateChange as long as nothing blocks.
  //
  // User interactions are answered from the answers registered for the item's tree; an interaction
  // without an answer makes the item deferred: it is blocked with reason USER_INTERACTION and
  // requeued, and registering an answer unblocks it. A retry propagates the item from the start, so
  // propagation must ask for user input before modifying models (see
  // NonDeferrablePropagationException).
  //
  // The queue may be inspected and modified from other threads while an item is being propagated;
  // propagating itself (propagateNext) is done by one thread at a time and must not run
  // concurrently with propagateChange.
  // ---------------------------------------------------------------------------------------------

  /** Mutable queue entry. */
  private static final class QueueItem {
    private final int taskId;
    private final Integer parentTaskId;
    private final int rootTaskId;

    /** The client change, until it has been applied to the models; {@code null} afterwards. */
    private VitruviusChange<Uuid> inputChange;

    /** The applied change to propagate; {@code null} until the input change has been applied. */
    private TransactionalChange<EObject> change;

    private PropagationBlockReason blockReason;
    private PendingUserInteraction pendingInteraction;

    private QueueItem(final int taskId, final Integer parentTaskId, final int rootTaskId) {
      this.taskId = taskId;
      this.parentTaskId = parentTaskId;
      this.rootTaskId = rootTaskId;
    }
  }

  private final Object queueLock = new Object();
  private final Object executionLock = new Object();
  private final Deque<QueueItem> queue = new ArrayDeque<>();
  private final Map<Integer, List<UserInteractionBase>> userInteractionsByRoot = new HashMap<>();
  private final Map<Integer, PropagationBlockReason> blockedRoots = new HashMap<>();
  private QueueItem executingItem;

  /**
   * Enqueues a change under a newly generated task id.
   *
   * @param change the change to apply and propagate
   * @return the task id identifying the change
   */
  public int enqueueChange(final VitruviusChange<Uuid> change) {
    synchronized (queueLock) {
      int taskId;
      do {
        taskId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
      } while (isTaskIdInUse(taskId));
      return enqueueChange(change, taskId);
    }
  }

  /**
   * Enqueues a change under the given task id.
   *
   * @param change the change to apply and propagate
   * @param taskId the id to identify the change by
   * @return {@code taskId}
   * @throws IllegalArgumentException if the id is already used by a change that is still queued
   */
  public int enqueueChange(final VitruviusChange<Uuid> change, final int taskId) {
    Preconditions.checkArgument(change != null, "change must not be null");
    synchronized (queueLock) {
      Preconditions.checkArgument(!isTaskIdInUse(taskId), "Task id %s is already in use", taskId);
      final QueueItem item = new QueueItem(taskId, null, taskId);
      item.inputChange = change;
      queue.addLast(item);
      return taskId;
    }
  }

  /**
   * Returns the queue item with the given task id.
   *
   * @param taskId the id of the item
   * @return a snapshot of the item, empty if no item with this id is queued or being propagated
   */
  public Optional<QueuedChange> getQueuedChange(final int taskId) {
    synchronized (queueLock) {
      return allItems().stream().filter(it -> it.taskId == taskId).findFirst().map(this::snapshot);
    }
  }

  /** Returns snapshots of all queue items in queue order, the one being propagated first. */
  public List<QueuedChange> getQueuedChanges() {
    synchronized (queueLock) {
      return allItems().stream().map(this::snapshot).toList();
    }
  }

  /**
   * Returns whether the change enqueued under the given task id, or any change its propagation
   * produced, still waits to be propagated.
   *
   * @param taskId the task id returned by {@link #enqueueChange(VitruviusChange)}
   * @return {@code false} once the change has been propagated completely
   */
  public boolean isPending(final int taskId) {
    synchronized (queueLock) {
      return allItems().stream().anyMatch(it -> it.taskId == taskId || it.rootTaskId == taskId);
    }
  }

  /**
   * Blocks propagation. For the task id of an enqueued change this blocks the change and every
   * change its propagation produces, including those produced later; for the id of any other queue
   * item only that item.
   *
   * @param taskId the id of the change or item to block
   * @param reason why it is blocked
   * @throws IllegalArgumentException if nothing with this id is pending
   */
  public void block(final int taskId, final PropagationBlockReason reason) {
    Preconditions.checkArgument(reason != null, "reason must not be null");
    synchronized (queueLock) {
      if (isPendingRoot(taskId)) {
        blockedRoots.put(taskId, reason);
      } else {
        findQueuedItem(taskId).blockReason = reason;
      }
    }
  }

  /**
   * Lifts a block set by {@link #block(int, PropagationBlockReason)}. Blocks caused by unanswered
   * user interactions are lifted by {@link #registerUserInteractions(int, UserInteractionBase...)}
   * instead.
   *
   * @param taskId the id passed to {@link #block(int, PropagationBlockReason)}
   * @throws IllegalArgumentException if nothing with this id is pending
   */
  public void unblock(final int taskId) {
    synchronized (queueLock) {
      if (isPendingRoot(taskId)) {
        blockedRoots.remove(taskId);
      } else {
        final QueueItem item = findQueuedItem(taskId);
        if (item.blockReason != PropagationBlockReason.USER_INTERACTION) {
          item.blockReason = null;
        }
      }
    }
  }

  /**
   * Registers answers to user interactions for a change and unblocks every item of it that waits
   * for a user interaction. Answers are matched by message, as by {@link
   * tools.vitruv.change.interaction.PredefinedInteractionResultProvider}, and stay available for
   * all further items of the change. May be called while an item is being propagated.
   *
   * @param taskId the task id of the change, or of any of its queue items
   * @param interactions the answers
   * @throws IllegalArgumentException if nothing with this id is pending
   */
  public void registerUserInteractions(
      final int taskId, final UserInteractionBase... interactions) {
    synchronized (queueLock) {
      final int rootTaskId;
      if (isPendingRoot(taskId)) {
        rootTaskId = taskId;
      } else {
        rootTaskId =
            allItems().stream()
                .filter(it -> it.taskId == taskId)
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException("No pending change with task id " + taskId))
                .rootTaskId;
      }
      userInteractionsByRoot
          .computeIfAbsent(rootTaskId, id -> new ArrayList<>())
          .addAll(List.of(interactions));
      for (final QueueItem item : queue) {
        if (item.rootTaskId == rootTaskId
            && item.blockReason == PropagationBlockReason.USER_INTERACTION) {
          item.blockReason = null;
          item.pendingInteraction = null;
        }
      }
    }
  }

  /**
   * Attempts to propagate the first queue item, see {@link #propagateNext(Iterable)}.
   *
   * @return what happened, empty if the queue is empty
   */
  public Optional<QueuedPropagationStep> propagateNext() {
    return propagateNext(List.of());
  }

  /**
   * Attempts to propagate the first queue item once.
   *
   * <p>A blocked item is moved to the end of the queue. Otherwise the item is propagated (an
   * enqueued change is applied to the models first): either it is propagated completely and the
   * changes this produced are queued as its children at the front of the queue, or it needs an
   * unanswered user interaction, in which case it is blocked and moved to the end of the queue. If
   * propagation fails for any other reason, the item is removed and the exception is rethrown.
   *
   * @param observers observers to register at the specifications while propagating
   * @return what happened, empty if the queue is empty
   * @throws NonDeferrablePropagationException if the item needs an unanswered user interaction but
   *     has already modified models
   */
  public Optional<QueuedPropagationStep> propagateNext(
      final Iterable<ChangePropagationObserver> observers) {
    synchronized (executionLock) {
      final QueueItem item;
      final int knownAnswers;
      synchronized (queueLock) {
        item = queue.pollFirst();
        if (item == null) {
          return Optional.empty();
        }
        if (isBlocked(item)) {
          queue.addLast(item);
          return Optional.of(
              new QueuedPropagationStep(
                  item.taskId, QueuedPropagationStep.Outcome.SKIPPED_BLOCKED, List.of()));
        }
        executingItem = item;
        knownAnswers = userInteractionsOf(item.rootTaskId).size();
      }
      try {
        return Optional.of(propagateItem(item, observers));
      } catch (final RuntimeException e) {
        return Optional.of(handleFailedItem(item, e, knownAnswers));
      }
    }
  }

  /** Propagates an item that has been taken from the queue and requeues what follows from it. */
  private QueuedPropagationStep propagateItem(
      final QueueItem item, final Iterable<ChangePropagationObserver> observers) {
    if (item.inputChange != null) {
      // Queue the other parts of the client change right away, so they are not lost if
      // propagating the first part is deferred.
      final List<QueueItem> siblings = applyInputChange(item);
      synchronized (queueLock) {
        addAllFirst(siblings);
      }
    }
    final List<QueueItem> followUps = new ArrayList<>();
    final List<PropagatedChange> propagated = new ArrayList<>();
    if (item.change != null) {
      final ChangePropagation.StepResult step = propagateQueuedItem(item, observers);
      propagated.add(step.propagatedChange());
      if (!Objects.equals(this.changePropagationMode, ChangePropagationMode.SINGLE_STEP)) {
        followUps.addAll(
            newChildItems(item, item.taskId, selectTransitiveChanges(step.resultChanges())));
      }
    }
    synchronized (queueLock) {
      executingItem = null;
      addAllFirst(followUps);
      forgetRootIfDone(item.rootTaskId);
    }
    return new QueuedPropagationStep(
        item.taskId, QueuedPropagationStep.Outcome.PROPAGATED, propagated);
  }

  /**
   * Defers an item whose propagation needs an unanswered user interaction; removes the item and
   * rethrows for any other failure.
   */
  private QueuedPropagationStep handleFailedItem(
      final QueueItem item, final RuntimeException failure, final int knownAnswers) {
    final PendingUserInteractionException pending = PendingUserInteractionException.findIn(failure);
    final NonDeferrablePropagationException nonDeferrable =
        findCause(failure, NonDeferrablePropagationException.class);
    synchronized (queueLock) {
      executingItem = null;
      if (pending != null && nonDeferrable == null) {
        // If answers were registered while the item was running, retry instead of waiting.
        final boolean answeredMeanwhile =
            userInteractionsOf(item.rootTaskId).size() != knownAnswers;
        item.blockReason = answeredMeanwhile ? null : PropagationBlockReason.USER_INTERACTION;
        item.pendingInteraction = answeredMeanwhile ? null : pending.getInteraction();
        queue.addLast(item);
        return new QueuedPropagationStep(
            item.taskId, QueuedPropagationStep.Outcome.DEFERRED, List.of());
      }
      forgetRootIfDone(item.rootTaskId);
    }
    throw nonDeferrable != null ? nonDeferrable : failure;
  }

  /** Puts the items at the front of the queue, keeping their order. */
  private void addAllFirst(final List<QueueItem> items) {
    for (int i = items.size() - 1; i >= 0; i--) {
      queue.addFirst(items.get(i));
    }
  }

  /**
   * Propagates queue items until the queue is empty or every remaining item is blocked.
   *
   * @param observers observers to register at the specifications while propagating
   * @return the steps taken, in order
   */
  public List<QueuedPropagationStep> propagateUntilBlocked(
      final Iterable<ChangePropagationObserver> observers) {
    final List<QueuedPropagationStep> steps = new ArrayList<>();
    int skippedInARow = 0;
    while (true) {
      final int queued;
      synchronized (queueLock) {
        queued = queue.size();
      }
      if (queued == 0 || skippedInARow >= queued) {
        return steps;
      }
      final Optional<QueuedPropagationStep> step = propagateNext(observers);
      if (step.isEmpty()) {
        return steps;
      }
      steps.add(step.get());
      final QueuedPropagationStep.Outcome outcome = step.get().outcome();
      skippedInARow = outcome == QueuedPropagationStep.Outcome.PROPAGATED ? 0 : skippedInARow + 1;
    }
  }

  /**
   * Applies the client change of a root item. The item continues with the first of the resulting
   * transactional changes; the others are returned to be queued right after it.
   */
  private List<QueueItem> applyInputChange(final QueueItem item) {
    final VitruviusChange<EObject> resolvedChange =
        this.modelRepository.applyChange(item.inputChange);
    item.inputChange = null;
    resolvedChange.getAffectedEObjects().stream()
        .map(EObject::eResource)
        .filter(Objects::nonNull)
        .forEach(it -> it.setModified(true));
    final List<TransactionalChange<EObject>> parts = new ArrayList<>();
    getTransactionalChangeSequence(resolvedChange).forEach(parts::add);
    if (parts.isEmpty()) {
      return List.of();
    }
    item.change = parts.get(0);
    return newChildItems(item, item.taskId, parts.subList(1, parts.size()));
  }

  /**
   * Propagates the change of a queue item through all specifications once, answering user
   * interactions from the answers registered for its tree and deferring all others.
   */
  private ChangePropagation.StepResult propagateQueuedItem(
      final QueueItem item, final Iterable<ChangePropagationObserver> observers) {
    final UserInteractionBase[] answers;
    synchronized (queueLock) {
      answers = userInteractionsOf(item.rootTaskId).toArray(UserInteractionBase[]::new);
    }
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    try (AutoCloseable deferringProvider =
        this.userInteractor.replaceUserInteractionResultProvider(
            current ->
                UserInteractionFactory.instance.createPredefinedInteractionResultProvider(
                    new DeferringInteractionResultProvider(), answers))) {
      return new ChangePropagation(this, item.change, null).propagateQueuedStep(item.change);
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw new IllegalStateException("Could not restore the user interaction result provider", e);
    } finally {
      this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    }
  }

  /** Creates queue items for changes belonging to the same tree as {@code relative}. */
  private List<QueueItem> newChildItems(
      final QueueItem relative,
      final int parentTaskId,
      final List<TransactionalChange<EObject>> changes) {
    final List<QueueItem> children = new ArrayList<>();
    final Set<Integer> taken = new HashSet<>();
    synchronized (queueLock) {
      for (final TransactionalChange<EObject> change : changes) {
        int taskId;
        do {
          taskId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        } while (isTaskIdInUse(taskId) || !taken.add(taskId));
        final QueueItem child = new QueueItem(taskId, parentTaskId, relative.rootTaskId);
        child.change = change;
        children.add(child);
      }
    }
    return children;
  }

  private boolean isBlocked(final QueueItem item) {
    return item.blockReason != null || blockedRoots.containsKey(item.rootTaskId);
  }

  private QueuedChange snapshot(final QueueItem item) {
    final PropagationBlockReason reason =
        item.blockReason != null ? item.blockReason : blockedRoots.get(item.rootTaskId);
    return new QueuedChange(
        item.taskId, item.parentTaskId, item.rootTaskId, reason, item.pendingInteraction);
  }

  private List<QueueItem> allItems() {
    final List<QueueItem> items = new ArrayList<>();
    if (executingItem != null) {
      items.add(executingItem);
    }
    items.addAll(queue);
    return items;
  }

  private boolean isTaskIdInUse(final int taskId) {
    return allItems().stream().anyMatch(it -> it.taskId == taskId || it.rootTaskId == taskId);
  }

  private boolean isPendingRoot(final int taskId) {
    return allItems().stream().anyMatch(it -> it.rootTaskId == taskId);
  }

  private QueueItem findQueuedItem(final int taskId) {
    return queue.stream()
        .filter(it -> it.taskId == taskId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No queued change with task id " + taskId));
  }

  private List<UserInteractionBase> userInteractionsOf(final int rootTaskId) {
    return userInteractionsByRoot.getOrDefault(rootTaskId, List.of());
  }

  /** Drops the answers and blocks of a change once nothing of it is pending anymore. */
  private void forgetRootIfDone(final int rootTaskId) {
    if (!isPendingRoot(rootTaskId)) {
      userInteractionsByRoot.remove(rootTaskId);
      blockedRoots.remove(rootTaskId);
    }
  }

  private static <T extends Throwable> T findCause(final Throwable throwable, final Class<T> type) {
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      if (type.isInstance(t)) {
        return type.cast(t);
      }
      if (t.getCause() == t) {
        break;
      }
    }
    return null;
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
