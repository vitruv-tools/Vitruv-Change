package tools.vitruv.change.propagation.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.emf.ecore.EObject;
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
 * an item are queued as its children at the front of the queue. An item that needs an unanswered
 * user interaction is blocked and moved to the end of the queue, and propagated again from the
 * start once an answer is registered.
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
  private final Map<Integer, List<UserInteractionBase>> userInteractionsByRoot = new HashMap<>();
  private final Map<Integer, PropagationBlockReason> blockedRoots = new HashMap<>();
  private QueueItem executingItem;

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
   * item only that item. If that item is being propagated, the block only takes effect if the item
   * is deferred and goes back into the queue: once it has been propagated it is gone, and the
   * changes it produced are new items that the block does not cover.
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
   * Lifts a block set by {@link #block(int, PropagationBlockReason)}. Blocks caused by unanswered
   * user interactions are lifted by {@link #registerUserInteractions(int, UserInteractionBase...)}
   * instead, so an item that still waits for an answer stays blocked until the answer is
   * registered.
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
   * Registers answers to user interactions for a change and unblocks every item of it that waits
   * for a user interaction; a {@link PropagationBlockReason#TRANSACTION} block stays until {@link
   * #unblock(int)}. Answers are matched to interactions as described at {@link
   * PendingUserInteraction}: by type, message and, for selections, choices. They stay available
   * for all further items of the change, and each propagation step gets all of them again. An item
   * that stays blocked for another reason keeps the interaction it waits for until one of the
   * answers matches it. May be called while an item is being propagated.
   *
   * @param taskId the task id of the change, or of any of its queue items
   * @param interactions the answers
   * @throws IllegalArgumentException if nothing with this id is pending
   */
  public void registerUserInteractions(
      final int taskId, final UserInteractionBase... interactions) {
    synchronized (queueLock) {
      final int rootTaskId = isPendingRoot(taskId) ? taskId : findPendingItem(taskId).rootTaskId;
      userInteractionsByRoot
          .computeIfAbsent(rootTaskId, id -> new ArrayList<>())
          .addAll(List.of(interactions));
      for (final QueueItem item : queue) {
        if (item.rootTaskId == rootTaskId) {
          applyNewAnswers(item, List.of(interactions));
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
        queue.addLast(item);
        return new QueuedPropagationStep(
            item.taskId, QueuedPropagationStep.Outcome.DEFERRED, List.of());
      }
      forgetRootIfDone(item.rootTaskId);
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
   * registered for its tree and deferring all others. Once the previous user interaction result
   * provider is restored, the notifications of the step are passed on to it, unless the step is
   * deferred.
   */
  private ChangePropagator.SingleStepResult propagateQueuedItem(
      final QueueItem item, final Iterable<ChangePropagationObserver> observers) {
    final UserInteractionBase[] answers;
    synchronized (queueLock) {
      answers = userInteractionsOf(item.rootTaskId).toArray(UserInteractionBase[]::new);
    }
    final DeferringInteractionResultProvider deferringProvider =
        new DeferringInteractionResultProvider();
    final AtomicReference<InteractionResultProvider> previousProvider = new AtomicReference<>();
    ChangePropagator.SingleStepResult step = null;
    RuntimeException failure = null;
    try (AutoCloseable restorePreviousProvider =
        changePropagator
            .getUserInteractor()
            .replaceUserInteractionResultProvider(
                current -> {
                  previousProvider.set(current);
                  return createStepProvider(deferringProvider, answers);
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
    return step;
  }

  /**
   * Creates the user interaction result provider for one propagation step: it answers user
   * interactions from the given answers and leaves all others to {@code deferringProvider}.
   * Notifications go to {@code deferringProvider} directly. No answer is ever registered for a
   * notification, but an answer to a confirmation would otherwise match one and be used up by it.
   */
  private static InteractionResultProvider createStepProvider(
      final DeferringInteractionResultProvider deferringProvider,
      final UserInteractionBase[] answers) {
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
    provider.addUserInteractions(answers);
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
            || blockedRoots.containsKey(item.rootTaskId);
    if (!blockedOtherwise || isAnsweredBy(item.pendingInteraction, newAnswers)) {
      if (item.blockReason == PropagationBlockReason.USER_INTERACTION) {
        item.blockReason = null;
      }
      item.pendingInteraction = null;
    }
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

  private boolean hasUnblockedItem() {
    synchronized (queueLock) {
      return queue.stream().anyMatch(it -> !isBlocked(it));
    }
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

  /** Drops the answers and blocks of a change once nothing of it is pending anymore. */
  private void forgetRootIfDone(final int rootTaskId) {
    if (!isPendingRoot(rootTaskId)) {
      userInteractionsByRoot.remove(rootTaskId);
      blockedRoots.remove(rootTaskId);
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
