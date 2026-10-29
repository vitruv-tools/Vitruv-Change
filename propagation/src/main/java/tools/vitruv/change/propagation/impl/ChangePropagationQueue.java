package tools.vitruv.change.propagation.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.PendingUserInteraction;
import tools.vitruv.change.interaction.PendingUserInteractionException;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionOptions.NotificationType;
import tools.vitruv.change.interaction.UserInteractionOptions.WindowModality;
import tools.vitruv.change.interaction.impl.DeferringInteractionResultProvider;
import tools.vitruv.change.interaction.impl.PredefinedInteractionMatcher;
import tools.vitruv.change.interaction.impl.PredefinedInteractionResultProviderImpl;
import tools.vitruv.change.propagation.ChangePropagationObserver;
import tools.vitruv.change.propagation.NonDeferrablePropagationException;
import tools.vitruv.change.propagation.PropagationBlockReason;
import tools.vitruv.change.propagation.QueuedChange;
import tools.vitruv.change.propagation.QueuedPropagationStep;

/**
 * Propagates changes with a {@link ChangePropagator} one step at a time, so that a change waiting
 * for a user interaction or a transaction does not hold up the others.
 *
 * <p>An enqueued change is the root of a tree of queue items: the changes produced by propagating
 * an item are queued as its children at the front of the queue. The items of one change are
 * propagated in the order {@link ChangePropagator#propagateChange} propagates them, since later
 * items may rely on what the reactions to earlier ones created, such as correspondences. So while
 * one item of a change is blocked, the other items of that change wait as well, and the change is
 * moved to the end of the queue, where it is overtaken by the other changes. An item that needs an
 * unanswered user interaction is blocked, and propagated again from the start once an answer is
 * registered.
 *
 * <p>For the same reason, a change only overtakes an earlier change that has not been propagated
 * completely if the two share no model element: no element that one of them creates, deletes,
 * modifies, or adds to or removes from a reference is one of those of the other. A change that has
 * been applied to the models before another one is earlier than it. A change that has not been
 * applied yet comes after all applied ones, and after the changes enqueued before it that have not
 * been applied either. It is compared by the ids of its elements and only applied once it may go
 * ahead, so it is never applied on top of a change whose propagation has not finished. An item that
 * has to wait is blocked with {@link PropagationBlockReason#EARLIER_CHANGE} once the queue has come
 * across it, and checked again whenever an item has been propagated or a change removed. Elements
 * that the specifications only read are not taken into account.
 *
 * <p>If propagating an item fails for any other reason, the whole change is removed from the queue,
 * just as {@link ChangePropagator#propagateChange} stops at the first exception. The models keep
 * what was applied and propagated before the failure.
 *
 * <p>Notifications do not defer an item. They are passed on to the user interaction result
 * provider that was installed before, once the propagation step has completed or failed. Those of a
 * deferred step are dropped, since the item notifies again when it is propagated from the start.
 *
 * <p>The queue may be inspected and modified from other threads at any time. Items are propagated
 * by one thread at a time, and not while the change propagator propagates a change directly.
 */
public class ChangePropagationQueue {

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

    /** For a root item, when it was enqueued relative to the other changes. */
    private long enqueueOrder;

    /** Whether the item waits for an earlier change it shares model elements with. */
    private boolean waitsForEarlierChange;

    /** The ids of the elements of the input change; computed when first needed. */
    private Set<Uuid> inputElements;

    /** The elements of the applied change; computed when first needed. */
    private Set<EObject> elements;

    private QueueItem(final int taskId, final Integer parentTaskId, final int rootTaskId) {
      this.taskId = taskId;
      this.parentTaskId = parentTaskId;
      this.rootTaskId = rootTaskId;
    }
  }

  private final ChangePropagator changePropagator;

  private final Object queueLock = new Object();
  private final Object executionLock = new Object();
  private final Deque<QueueItem> queue = new ArrayDeque<>();

  /** All answers registered for a change, in the order they were registered. */
  private final Map<Integer, List<UserInteractionBase>> userInteractionsByRoot = new HashMap<>();

  /**
   * The answers registered for a change that no propagated step has used yet. An answer object
   * registered twice counts as two answers, so it is listed twice.
   */
  private final Map<Integer, List<UserInteractionBase>> unusedUserInteractionsByRoot =
      new HashMap<>();

  private final Map<Integer, PropagationBlockReason> blockedRoots = new HashMap<>();
  private QueueItem executingItem;

  /** When each pending change was applied to the models, relative to the other changes. */
  private final Map<Integer, Long> startOrderByRoot = new HashMap<>();

  /** The next value for {@link QueueItem#enqueueOrder} and {@link #startOrderByRoot}. */
  private long nextOrder;

  /**
   * Creates an empty queue that propagates changes with the given change propagator.
   *
   * @param changePropagator the change propagator to propagate the queued changes with
   */
  public ChangePropagationQueue(final ChangePropagator changePropagator) {
    this.changePropagator =
        Preconditions.checkNotNull(changePropagator, "changePropagator must not be null");
  }

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
      item.enqueueOrder = nextOrder++;
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
   * @return {@code false} once the change has been propagated completely or its propagation has
   *     failed
   */
  public boolean isPending(final int taskId) {
    synchronized (queueLock) {
      return allItems().stream().anyMatch(it -> it.taskId == taskId || it.rootTaskId == taskId);
    }
  }

  /**
   * Blocks propagation. For the task id of an enqueued change this blocks the change and every
   * change its propagation produces, including those produced later. For the id of any other queue
   * item it blocks that item, which holds back the other items of its change as well, since a
   * change is propagated in order. If that item is being propagated, the block only takes effect if
   * the item is deferred and goes back into the queue: once it has been propagated it is gone, and
   * the changes it produced are new items that the block does not cover.
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
        findPendingItem(taskId).blockReason = reason;
      }
    }
  }

  /**
   * Lifts a block set by {@link #block(int, PropagationBlockReason)}. The change goes on once none
   * of its items is blocked anymore. Blocks caused by unanswered user interactions are lifted by
   * {@link #registerUserInteractions(int, UserInteractionBase...)} instead, so an item that still
   * waits for an answer stays blocked until the answer is registered. Likewise, an item that waits
   * for an earlier change it shares model elements with stays blocked until it may go ahead.
   *
   * @param taskId the id passed to {@link #block(int, PropagationBlockReason)}
   * @throws IllegalArgumentException if nothing with this id is pending
   */
  public void unblock(final int taskId) {
    synchronized (queueLock) {
      if (isPendingRoot(taskId)) {
        blockedRoots.remove(taskId);
      } else {
        final QueueItem item = findPendingItem(taskId);
        if (item.blockReason != PropagationBlockReason.USER_INTERACTION) {
          item.blockReason =
              item.pendingInteraction != null ? PropagationBlockReason.USER_INTERACTION : null;
        }
      }
    }
  }

  /**
   * Registers answers to user interactions for a change and unblocks the item of it that waits for
   * a user interaction, if any; a {@link PropagationBlockReason#TRANSACTION} block stays until
   * {@link #unblock(int)}. As the change is propagated in order, at most one of its items waits for
   * an answer at a time. Answers are matched to interactions as described at {@link
   * PendingUserInteraction}: by type, message and, for selections, choices. Each answer answers at
   * most one interaction, just as each question is answered once when the change is propagated with
   * {@link ChangePropagator#propagateChange}. This holds per registration: an answer object that is
   * passed twice, or registered again later, counts as one answer each time. Until it is used, an
   * answer is available to all further items of the change. An answer that a deferred step used is
   * available again when the step is repeated. An item that stays blocked for another reason keeps
   * the interaction it waits for until one of the answers matches it. May be called while an item
   * is being propagated.
   *
   * @param taskId the task id of the change, or of any of its queue items
   * @param interactions the answers
   * @throws IllegalArgumentException if nothing with this id is pending
   */
  public void registerUserInteractions(
      final int taskId, final UserInteractionBase... interactions) {
    synchronized (queueLock) {
      final int rootTaskId = isPendingRoot(taskId) ? taskId : findPendingItem(taskId).rootTaskId;
      final List<UserInteractionBase> answers = List.of(interactions);
      userInteractionsByRoot.computeIfAbsent(rootTaskId, id -> new ArrayList<>()).addAll(answers);
      unusedUserInteractionsByRoot
          .computeIfAbsent(rootTaskId, id -> new ArrayList<>())
          .addAll(answers);
      for (final QueueItem item : queue) {
        if (item.rootTaskId == rootTaskId) {
          applyNewAnswers(item, answers);
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
   * <p>If the item, its change or another item of its change is blocked, or the item shares model
   * elements with an earlier change that has not been propagated completely, the item and the rest
   * of its change are moved to the end of the queue, keeping their order. Otherwise the item is
   * propagated (an enqueued change is applied to the models first): either it is propagated
   * completely and the changes this produced are queued as its children at the front of the queue,
   * or it needs an unanswered user interaction, in which case it is put back at the front of the
   * queue and, unless answers registered for its change while it ran unblock it, blocked and moved
   * to the end of the queue together with the rest of its change. An item unblocked like this is
   * attempted again in the next step. If propagation fails for any other reason, the item and all
   * other items of its change are removed, and the exception is rethrown.
   *
   * @param observers observers to register at the specifications while propagating
   * @return what happened, empty if the queue is empty
   * @throws NonDeferrablePropagationException if the item needs an unanswered user interaction but
   *     has already modified models; its change is removed as well
   */
  public Optional<QueuedPropagationStep> propagateNext(
      final Iterable<ChangePropagationObserver> observers) {
    synchronized (executionLock) {
      final QueueItem item;
      final int knownAnswers;
      synchronized (queueLock) {
        item = queue.peekFirst();
        if (item == null) {
          return Optional.empty();
        }
        if (!isBlocked(item) && sharesElementsWithEarlierChange(item)) {
          item.waitsForEarlierChange = true;
        }
        if (isBlocked(item)) {
          moveChangeToEnd(item.rootTaskId);
          return Optional.of(
              new QueuedPropagationStep(
                  item.taskId, QueuedPropagationStep.Outcome.SKIPPED_BLOCKED, List.of()));
        }
        queue.removeFirst();
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

  /**
   * Propagates queue items until the queue is empty or every remaining item is blocked.
   *
   * @param observers observers to register at the specifications while propagating
   * @return the steps taken, in order
   */
  public List<QueuedPropagationStep> propagateUntilBlocked(
      final Iterable<ChangePropagationObserver> observers) {
    final List<QueuedPropagationStep> steps = new ArrayList<>();
    while (hasUnblockedItem()) {
      final Optional<QueuedPropagationStep> step = propagateNext(observers);
      if (step.isEmpty()) {
        break;
      }
      steps.add(step.get());
    }
    return steps;
  }

  /** Propagates an item that has been taken from the queue and requeues what follows from it. */
  private QueuedPropagationStep propagateItem(
      final QueueItem item, final Iterable<ChangePropagationObserver> observers) {
    if (item.inputChange != null) {
      // Queue the other parts of the client change right away, so they are not lost if
      // propagating the first part is deferred.
      final List<QueueItem> siblings = applyInputChange(item);
      synchronized (queueLock) {
        startOrderByRoot.put(item.rootTaskId, nextOrder++);
        addAllFirst(siblings);
      }
    }
    final List<QueueItem> followUps = new ArrayList<>();
    final List<PropagatedChange> propagated = new ArrayList<>();
    if (item.change != null) {
      final ChangePropagator.SingleStepResult step = propagateQueuedItem(item, observers);
      propagated.add(step.propagatedChange());
      followUps.addAll(newChildItems(item, step.followUpChanges()));
    }
    synchronized (queueLock) {
      executingItem = null;
      addAllFirst(followUps);
      forgetRootIfDone(item.rootTaskId);
      stopWaitingForEarlierChanges();
    }
    return new QueuedPropagationStep(
        item.taskId, QueuedPropagationStep.Outcome.PROPAGATED, propagated);
  }

  /**
   * Defers an item whose propagation needs an unanswered user interaction; for any other failure,
   * removes the item's whole change and rethrows.
   */
  private QueuedPropagationStep handleFailedItem(
      final QueueItem item, final RuntimeException failure, final int knownAnswers) {
    synchronized (queueLock) {
      executingItem = null;
      if (isDeferral(failure)) {
        // A block set while the item was running stays until unblock lifts it.
        if (item.blockReason == null) {
          item.blockReason = PropagationBlockReason.USER_INTERACTION;
        }
        item.pendingInteraction = PendingUserInteractionException.findIn(failure).getInteraction();
        // Answers registered while the item was running are applied as if registered just now.
        final List<UserInteractionBase> answers = userInteractionsOf(item.rootTaskId);
        if (answers.size() != knownAnswers) {
          applyNewAnswers(item, answers.subList(knownAnswers, answers.size()));
        }
        // The item was the first of its change when it was taken, and the other parts of an input
        // change were queued at the front while it ran, so putting it back at the front keeps the
        // change in order. While it waits, the whole change moves to the end, where the other
        // changes overtake it.
        queue.addFirst(item);
        if (isBlocked(item)) {
          moveChangeToEnd(item.rootTaskId);
        }
        return new QueuedPropagationStep(
            item.taskId, QueuedPropagationStep.Outcome.DEFERRED, List.of());
      }
      // Like propagateChange, a failure ends the whole change. What was applied and propagated
      // before stays in the models.
      queue.removeIf(it -> it.rootTaskId == item.rootTaskId);
      forgetRootIfDone(item.rootTaskId);
      stopWaitingForEarlierChanges();
    }
    final NonDeferrablePropagationException nonDeferrable =
        findCause(failure, NonDeferrablePropagationException.class);
    throw nonDeferrable != null ? nonDeferrable : failure;
  }

  /**
   * Applies the client change of a root item. The item continues with the first of the resulting
   * transactional changes; the others are returned to be queued right after it.
   */
  private List<QueueItem> applyInputChange(final QueueItem item) {
    final List<TransactionalChange<EObject>> parts =
        changePropagator.applyWithoutPropagating(item.inputChange);
    item.inputChange = null;
    if (parts.isEmpty()) {
      return List.of();
    }
    item.change = parts.get(0);
    return newChildItems(item, parts.subList(1, parts.size()));
  }

  /**
   * Propagates the change of a queue item one step, answering user interactions from the answers
   * registered for its change that no earlier step has used, and deferring all others. Once the
   * previous user interaction result provider is restored, the notifications of the step are passed
   * on to it, unless the step is deferred. If the step succeeds, the answers it used are used up.
   */
  private ChangePropagator.SingleStepResult propagateQueuedItem(
      final QueueItem item, final Iterable<ChangePropagationObserver> observers) {
    final List<UserInteractionBase> answers;
    synchronized (queueLock) {
      answers = unusedUserInteractionsOf(item.rootTaskId);
    }
    final DeferringInteractionResultProvider deferringProvider =
        new DeferringInteractionResultProvider();
    final PredefinedInteractionResultProviderImpl stepProvider =
        createStepProvider(deferringProvider, answers);
    final AtomicReference<InteractionResultProvider> previousProvider = new AtomicReference<>();
    ChangePropagator.SingleStepResult step = null;
    RuntimeException failure = null;
    try (AutoCloseable restorePreviousProvider =
        changePropagator
            .getUserInteractor()
            .replaceUserInteractionResultProvider(
                current -> {
                  previousProvider.set(current);
                  return stepProvider;
                })) {
      step = changePropagator.propagateSingleStep(item.change, observers);
    } catch (final RuntimeException e) {
      failure = e;
    } catch (final Exception e) {
      failure =
          new IllegalStateException("Could not restore the user interaction result provider", e);
    }
    // A deferred step is propagated again from the start, which repeats its notifications.
    if (failure == null || !isDeferral(failure)) {
      deferringProvider.forwardNotificationsTo(previousProvider.get());
    }
    if (failure != null) {
      throw failure;
    }
    // Only a step that went through uses up its answers. A deferred step needs the same answers
    // again when it is repeated, and a failed one ends the change anyway.
    synchronized (queueLock) {
      markUsed(item.rootTaskId, answers, stepProvider.getRemainingUserInteractions());
    }
    return step;
  }

  /**
   * Creates the user interaction result provider for one propagation step: it answers user
   * interactions from the given answers and leaves all others to {@code deferringProvider}.
   * Notifications go to {@code deferringProvider} directly. No answer is ever registered for a
   * notification, but an answer to a confirmation would otherwise match one and be used up by it.
   */
  private static PredefinedInteractionResultProviderImpl createStepProvider(
      final DeferringInteractionResultProvider deferringProvider,
      final List<UserInteractionBase> answers) {
    final PredefinedInteractionResultProviderImpl provider =
        new PredefinedInteractionResultProviderImpl(deferringProvider) {
          @Override
          public void getNotificationInteractionResult(
              final WindowModality windowModality,
              final String title,
              final String message,
              final String positiveDecisionText,
              final NotificationType notificationType) {
            deferringProvider.getNotificationInteractionResult(
                windowModality, title, message, positiveDecisionText, notificationType);
          }
        };
    provider.addUserInteractions(answers.toArray(UserInteractionBase[]::new));
    return provider;
  }

  /**
   * Updates an item of a change for answers newly registered for that change. An item that can be
   * propagated again right away no longer waits for its user interaction, as propagating it asks
   * again if the answers do not suffice. An item that stays blocked for another reason keeps
   * waiting for its interaction until one of the answers matches it, so the interaction can still
   * be looked up while the item is blocked.
   */
  private void applyNewAnswers(final QueueItem item, final List<UserInteractionBase> newAnswers) {
    final boolean blockedOtherwise =
        (item.blockReason != null && item.blockReason != PropagationBlockReason.USER_INTERACTION)
            || item.waitsForEarlierChange
            || blockedRoots.containsKey(item.rootTaskId)
            || blockOfOtherItemOfChange(item) != null;
    if (!blockedOtherwise || isAnsweredBy(item.pendingInteraction, newAnswers)) {
      if (item.blockReason == PropagationBlockReason.USER_INTERACTION) {
        item.blockReason = null;
      }
      item.pendingInteraction = null;
    }
  }

  /**
   * Uses up the answers a step used for its change: those given to the step that its provider no
   * longer holds. The lists are compared occurrence by occurrence, so an answer object given twice
   * and used once stays available once.
   */
  private void markUsed(
      final int rootTaskId,
      final List<UserInteractionBase> given,
      final List<UserInteractionBase> remaining) {
    final List<UserInteractionBase> used = new ArrayList<>(given);
    remaining.forEach(it -> removeOnce(used, it));
    // Answers registered while the step ran come after the ones it was given, so the first
    // occurrence of a used answer is the one the step was given.
    final List<UserInteractionBase> unused = unusedUserInteractionsByRoot.get(rootTaskId);
    used.forEach(it -> removeOnce(unused, it));
  }

  /** Creates queue items for changes that belong to the tree of {@code parent}, as its children. */
  private List<QueueItem> newChildItems(
      final QueueItem parent, final List<TransactionalChange<EObject>> changes) {
    final List<QueueItem> children = new ArrayList<>();
    final Set<Integer> taken = new HashSet<>();
    synchronized (queueLock) {
      for (final TransactionalChange<EObject> change : changes) {
        int taskId;
        do {
          taskId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        } while (isTaskIdInUse(taskId) || !taken.add(taskId));
        final QueueItem child = new QueueItem(taskId, parent.taskId, parent.rootTaskId);
        child.change = change;
        children.add(child);
      }
    }
    return children;
  }

  /** Puts the items at the front of the queue, keeping their order. */
  private void addAllFirst(final List<QueueItem> items) {
    for (int i = items.size() - 1; i >= 0; i--) {
      queue.addFirst(items.get(i));
    }
  }

  /**
   * Moves all queued items of a change to the end of the queue, keeping their order. Moving only
   * the first one would let the next item of the change overtake it if the change were unblocked
   * before that item had been moved as well.
   */
  private void moveChangeToEnd(final int rootTaskId) {
    final List<QueueItem> items = queue.stream().filter(it -> it.rootTaskId == rootTaskId).toList();
    queue.removeIf(it -> it.rootTaskId == rootTaskId);
    queue.addAll(items);
  }

  private boolean hasUnblockedItem() {
    synchronized (queueLock) {
      return queue.stream().anyMatch(it -> !isBlocked(it));
    }
  }

  private boolean isBlocked(final QueueItem item) {
    return blockReasonOf(item) != null;
  }

  /**
   * Returns why an item cannot be propagated right now, {@code null} if it can: its own block, the
   * block of its change, the earlier change it waits for, or the block of another queued item of
   * its change, which it waits for.
   */
  private PropagationBlockReason blockReasonOf(final QueueItem item) {
    if (item.blockReason != null) {
      return item.blockReason;
    }
    final PropagationBlockReason changeBlock = blockedRoots.get(item.rootTaskId);
    if (changeBlock != null) {
      return changeBlock;
    }
    return item.waitsForEarlierChange
        ? PropagationBlockReason.EARLIER_CHANGE
        : blockOfOtherItemOfChange(item);
  }

  /** Returns the block of another queued item of the item's change, {@code null} if none. */
  private PropagationBlockReason blockOfOtherItemOfChange(final QueueItem item) {
    return queue.stream()
        .filter(it -> it != item && it.rootTaskId == item.rootTaskId)
        .map(ChangePropagationQueue::ownBlockReasonOf)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  /** Returns the block of the item itself, without those of its change. */
  private static PropagationBlockReason ownBlockReasonOf(final QueueItem item) {
    if (item.blockReason != null) {
      return item.blockReason;
    }
    return item.waitsForEarlierChange ? PropagationBlockReason.EARLIER_CHANGE : null;
  }

  /**
   * Whether the item has to wait for an earlier change that has not been propagated completely
   * because they share model elements. The item of an applied change is compared with the queued
   * items of the changes applied before its change. A change that has not been applied yet is
   * compared with the queued items of all applied changes, by the elements its ids currently stand
   * for, and with the changes enqueued before it that have not been applied either, by its ids.
   */
  private boolean sharesElementsWithEarlierChange(final QueueItem item) {
    if (item.inputChange == null) {
      final Long startOrder = startOrderByRoot.get(item.rootTaskId);
      final Set<EObject> itemElements = elementsOf(item);
      return queue.stream()
          .filter(other -> other.rootTaskId != item.rootTaskId && other.change != null)
          .filter(other -> startOrderByRoot.get(other.rootTaskId) < startOrder)
          .anyMatch(other -> !Collections.disjoint(elementsOf(other), itemElements));
    }
    final Set<Uuid> itemIds = inputElementsOf(item);
    if (itemIds.isEmpty()) {
      return false;
    }
    final Set<EObject> existingItemElements = changePropagator.getExistingElements(itemIds);
    return queue.stream()
        .filter(other -> other.rootTaskId != item.rootTaskId)
        .anyMatch(
            other ->
                other.inputChange != null
                    ? other.enqueueOrder < item.enqueueOrder
                        && !Collections.disjoint(inputElementsOf(other), itemIds)
                    : other.change != null
                        && !Collections.disjoint(elementsOf(other), existingItemElements));
  }

  /** Lets every item check again whether it has to wait for an earlier change. */
  private void stopWaitingForEarlierChanges() {
    queue.forEach(it -> it.waitsForEarlierChange = false);
  }

  private static Set<EObject> elementsOf(final QueueItem item) {
    if (item.elements == null) {
      item.elements = elementsChangedBy(item.change.getEChanges());
    }
    return item.elements;
  }

  private static Set<Uuid> inputElementsOf(final QueueItem item) {
    if (item.inputElements == null) {
      item.inputElements = elementsChangedBy(item.inputChange.getEChanges());
    }
    return item.inputElements;
  }

  /**
   * Returns the elements that atomic changes create, delete or modify, and those they add to or
   * remove from a reference.
   */
  private static <E> Set<E> elementsChangedBy(final List<EChange<E>> eChanges) {
    final Set<E> elements = new HashSet<>();
    for (final EChange<E> eChange : eChanges) {
      if (eChange instanceof FeatureEChange<E, ?> featureChange) {
        elements.add(featureChange.getAffectedElement());
      }
      if (eChange instanceof EObjectExistenceEChange<E> existenceChange) {
        elements.add(existenceChange.getAffectedElement());
      }
      if (eChange instanceof EObjectAddedEChange<E> addedChange) {
        elements.add(addedChange.getNewValue());
      }
      if (eChange instanceof EObjectSubtractedEChange<E> subtractedChange) {
        elements.add(subtractedChange.getOldValue());
      }
    }
    elements.remove(null);
    return elements;
  }

  /**
   * Takes a snapshot of an item. An item that waits for another item of its change reports the
   * block reason of that item, but only the item that asked carries the pending interaction.
   */
  private QueuedChange snapshot(final QueueItem item) {
    return new QueuedChange(
        item.taskId,
        item.parentTaskId,
        item.rootTaskId,
        blockReasonOf(item),
        item.pendingInteraction);
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

  private QueueItem findPendingItem(final int taskId) {
    return allItems().stream()
        .filter(it -> it.taskId == taskId)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("No pending change with task id " + taskId));
  }

  private List<UserInteractionBase> userInteractionsOf(final int rootTaskId) {
    return userInteractionsByRoot.getOrDefault(rootTaskId, List.of());
  }

  /** Returns the answers registered for a change that no step has used up, in their order. */
  private List<UserInteractionBase> unusedUserInteractionsOf(final int rootTaskId) {
    return List.copyOf(unusedUserInteractionsByRoot.getOrDefault(rootTaskId, List.of()));
  }

  /** Drops the answers, blocks and order of a change once nothing of it is pending anymore. */
  private void forgetRootIfDone(final int rootTaskId) {
    if (!isPendingRoot(rootTaskId)) {
      userInteractionsByRoot.remove(rootTaskId);
      unusedUserInteractionsByRoot.remove(rootTaskId);
      blockedRoots.remove(rootTaskId);
      startOrderByRoot.remove(rootTaskId);
    }
  }

  /**
   * Whether a failed propagation step is deferred: it needs an unanswered user interaction and has
   * not modified models yet.
   */
  private static boolean isDeferral(final RuntimeException failure) {
    return PendingUserInteractionException.findIn(failure) != null
        && findCause(failure, NonDeferrablePropagationException.class) == null;
  }

  /**
   * Whether one of the answers is matched to the interaction when its item is propagated again, by
   * the same rules a propagation step uses.
   */
  private static boolean isAnsweredBy(
      final PendingUserInteraction interaction, final List<UserInteractionBase> answers) {
    if (interaction == null) {
      return false;
    }
    final PredefinedInteractionMatcher matcher = new PredefinedInteractionMatcher();
    answers.forEach(matcher::addInteraction);
    final String message = interaction.message();
    return switch (interaction.kind()) {
      case CONFIRMATION -> matcher.getConfirmationResult(message).isPresent();
      case TEXT_INPUT -> matcher.getTextInputResult(message) != null;
      case SINGLE_SELECTION ->
          matcher.getSingleSelectionResult(message, interaction.choices()) != null;
      case MULTIPLE_SELECTION ->
          matcher.getMultiSelectionResult(message, interaction.choices()) != null;
    };
  }

  /**
   * Removes the first occurrence of an answer from the list, compared by identity: two equal
   * answers are still two answers.
   */
  private static void removeOnce(
      final List<UserInteractionBase> answers, final UserInteractionBase answer) {
    for (int i = 0; i < answers.size(); i++) {
      if (answers.get(i) == answer) {
        answers.remove(i);
        return;
      }
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
}
