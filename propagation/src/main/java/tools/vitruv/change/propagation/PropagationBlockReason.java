package tools.vitruv.change.propagation;

/** Why a queued change cannot be propagated right now. */
public enum PropagationBlockReason {
  /** The propagation needs a user interaction that has not been answered yet. */
  USER_INTERACTION,
  /** A transaction currently prevents the propagation, e.g. because it holds a lock. */
  TRANSACTION,
  /**
   * The change shares model elements with an earlier change that has not been propagated
   * completely yet, so it waits for that change.
   */
  EARLIER_CHANGE
}
