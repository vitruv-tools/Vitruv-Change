package tools.vitruv.change.interaction;

/**
 * Signals that a change propagation needs a user interaction that has not been answered yet.
 *
 * <p>Thrown by {@link tools.vitruv.change.interaction.impl.DeferringInteractionResultProvider}
 * instead of blocking until the user answers, so that the propagation of the current change can be
 * deferred and retried once the answer is registered.
 */
public class PendingUserInteractionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final transient PendingUserInteraction interaction;

  /**
   * Creates the exception.
   *
   * @param interaction the interaction that is waiting for an answer
   */
  public PendingUserInteractionException(PendingUserInteraction interaction) {
    super("User interaction pending: " + interaction.message());
    this.interaction = interaction;
  }

  /** Returns the interaction that is waiting for an answer. */
  public PendingUserInteraction getInteraction() {
    return interaction;
  }

  /**
   * Finds a pending interaction in the cause chain of the given throwable, since propagation code
   * may wrap it.
   *
   * @param throwable the throwable to inspect, may be {@code null}
   * @return the exception, or {@code null} if the chain contains none
   */
  public static PendingUserInteractionException findIn(Throwable throwable) {
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      if (t instanceof PendingUserInteractionException pending) {
        return pending;
      }
      if (t.getCause() == t) {
        break;
      }
    }
    return null;
  }
}
