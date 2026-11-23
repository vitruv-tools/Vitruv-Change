package tools.vitruv.change.propagation;

/**
 * Thrown by a {@link PropagationStepGuard}, or from within a specification, to abort the running
 * propagation: it halts as {@link ChangePropagationStatus#BLOCKED}, and its changes are reverted.
 * The caller may resubmit the change.
 */
public class PropagationAbortedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public PropagationAbortedException(String message, Throwable cause) {
    super(message, cause);
  }

  public PropagationAbortedException(String message) {
    super(message);
  }
}
