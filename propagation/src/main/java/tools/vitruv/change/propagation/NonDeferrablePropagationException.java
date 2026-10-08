package tools.vitruv.change.propagation;

/**
 * Thrown when a queued change needs an unanswered user interaction, but its propagation has already
 * modified models.
 *
 * <p>Deferring would mean propagating the change again later, which would repeat those
 * modifications. The queue therefore relies on change propagation asking for user input before
 * modifying models, and fails with this exception when that does not hold.
 */
public class NonDeferrablePropagationException extends IllegalStateException {
  private static final long serialVersionUID = 1L;

  /**
   * Creates the exception.
   *
   * @param message the description
   * @param cause the pending user interaction that could not be deferred
   */
  public NonDeferrablePropagationException(String message, Throwable cause) {
    super(message, cause);
  }
}
