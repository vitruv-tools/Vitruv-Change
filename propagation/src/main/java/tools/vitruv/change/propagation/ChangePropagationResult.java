package tools.vitruv.change.propagation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;

/**
 * The result of propagating changes: the {@link ChangePropagationInvocation}s that took place, in
 * execution order.
 *
 * <p>The status, the executed changes and the cause are derived from the
 * invocations.
 * A combination of specifications returns the invocations of its parts.
 * Each executed change belongs to exactly one invocation.
 *
 * @param invocations the invocations in execution order; not null or with null elements.
 *                    It is empty only if no specification was called, see {@link #UNHANDLED}.
 */
public record ChangePropagationResult(List<ChangePropagationInvocation> invocations) {
  /**
   * Returns the result for the case that no change was handled, so that no specification was
   * called. It has no invocation, is {@link ChangePropagationStatus#SUCCEEDED} and has no executed
   * changes. It is the neutral element of {@link #append(ChangePropagationResult)}.
   *
   * <p>A specification that was called for a change but does nothing does not return this result;
   * it returns a succeeded invocation without executed changes.
   */
  public static final ChangePropagationResult UNHANDLED = new ChangePropagationResult(List.of());

  /**
   * Creates a result from {@code invocations}.
   *
   * @throws NullPointerException if the list or an element of the list is {@code null}
   */
  public ChangePropagationResult {
    checkNotNull(invocations, "invocations must not be null");
    invocations = List.copyOf(invocations);
  }

  /**
   * Creates a result from the given invocations.
   *
   * @param invocations the invocations in execution order
   * @return a result with these invocations
   */
  public static ChangePropagationResult of(ChangePropagationInvocation... invocations) {
    return new ChangePropagationResult(List.of(invocations));
  }

  /**
   * Returns the status of the propagation result.
   *
   * @return SUCCEEDED if all invocations also succeeded,
   *     otherwise FAILED or BLOCKED, determined by the first invocation
   *     that failed or was blocked.
   */
  public ChangePropagationStatus status() {
    return firstNonSucceeded()
        .map(ChangePropagationInvocation::status)
        .orElse(ChangePropagationStatus.SUCCEEDED);
  }

  /**
   * Returns the changes executed by all invocations in execution order.
   *
   * @return the executed changes, empty if none were executed
   */
  public List<EChange<EObject>> executedChanges() {
    return invocations.stream()
        .flatMap(invocation -> invocation.executedChanges().stream())
        .toList();
  }

  /**
   * Returns the cause of the first invocation that is
   * {@link ChangePropagationStatus#FAILED}, if there is one such invocation.
   *
   * @return the cause, or an empty {@link Optional}
   */
  public Optional<Throwable> cause() {
    return firstNonSucceeded().map(ChangePropagationInvocation::cause);
  }

  /**
   * Returns whether all invocations succeeded.
   *
   * @return {@code true} if every invocation succeeded
   */
  public boolean succeeded() {
    return status() == ChangePropagationStatus.SUCCEEDED;
  }

  /**
   * Appends the invocations of {@code next} after the invocations of this result. If this result
   * is not {@link ChangePropagationStatus#SUCCEEDED}, propagation has stopped, and this result is
   * returned unchanged.
   *
   * @param next the result that follows this result, not {@code null}
   * @return this result if it is not succeeded or if {@code next} has no invocation; {@code next}
   *     if this result has no invocation; otherwise a result with the invocations of both
   */
  public ChangePropagationResult append(ChangePropagationResult next) {
    checkNotNull(next, "next result must not be null");
    if (!succeeded() || next.invocations.isEmpty()) {
      return this;
    }
    if (invocations.isEmpty()) {
      return next;
    }
    return new ChangePropagationResult(
        Stream.concat(invocations.stream(), next.invocations.stream()).toList());
  }

  private Optional<ChangePropagationInvocation> firstNonSucceeded() {
    return invocations.stream()
        .filter(invocation -> invocation.status() != ChangePropagationStatus.SUCCEEDED)
        .findFirst();
  }
}
