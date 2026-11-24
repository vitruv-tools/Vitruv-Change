package tools.vitruv.change.propagation;

/**
 * The status of a call of a {@link ChangePropagationSpecification} for one change.
 *
 * <p>The status is derived from applying single changes during the specification.
 */
public enum ChangePropagationStatus {
  /**
   * The specification completed all its effects for the change, or did not handle the change.
   */
  SUCCEEDED,

  /**
   * The specification stopped before completing all of its effects,
   * without an error having occurred.
   */
  BLOCKED,

  /**
   * An error occurred during the execution of the specification.
   */
  FAILED
}
