package tools.vitruv.change.propagation;

import tools.vitruv.change.interaction.PendingUserInteraction;

/**
 * Snapshot of one item in the change propagation queue of a {@link
 * tools.vitruv.change.propagation.impl.ChangePropagator}.
 *
 * <p>Items form a tree: the change a client enqueues is the root, and every change produced by
 * propagating an item is queued as a child of that item.
 *
 * @param taskId the id of this item
 * @param parentTaskId the id of the item whose propagation produced this one, {@code null} for a
 *     root
 * @param rootTaskId the id of the change the client enqueued that this item descends from
 * @param blockReason why the item cannot be propagated right now, {@code null} if it can
 * @param pendingInteraction the user interaction the item waits for, {@code null} if none
 */
public record QueuedChange(
    int taskId,
    Integer parentTaskId,
    int rootTaskId,
    PropagationBlockReason blockReason,
    PendingUserInteraction pendingInteraction) {

  /** Whether the item is currently skipped by the queue. */
  public boolean blocked() {
    return blockReason != null;
  }
}
