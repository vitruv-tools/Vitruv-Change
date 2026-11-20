package tools.vitruv.change.propagation;

/**
 * Thrown by a {@link PropagationStepGuard} to abort the running propagation: the propagator reverts
 * its changes and retries it. Thrown to the caller if the retries are exhausted.
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
