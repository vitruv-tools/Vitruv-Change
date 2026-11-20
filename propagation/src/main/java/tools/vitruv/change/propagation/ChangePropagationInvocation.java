package tools.vitruv.change.propagation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;

/**
 * Records the result of one call to a {@link ChangePropagationSpecification}:
 * specification, which changes it received, result, and which changes it executed.
 *
 * <p>The invocations that took place during a call of a specification, for example the calls
 * of the parts of a combination of specifications, are not kept. They are combined into one
 * invocation, see {@link #ChangePropagationInvocation(ChangePropagationSpecification, List, List)}.
 *
 * @param specification the invoked {@link ChangePropagationSpecification}.
 * @param receivedChanges the received {@link EChange}s for this invocation.
 * @param status {@code ChangePropagationStatus}
 * @param executedChanges the changes the specification executed on target models, in execution
 *     order, including those executed before it stopped; empty if none were executed.
 * @param cause A throwable indicating the error of a {@link ChangePropagationStatus#FAILED} call,
 *     must be {@code null} for any other status
 */
public record ChangePropagationInvocation(
    ChangePropagationSpecification specification,
    List<EChange<EObject>> receivedChanges,
    ChangePropagationStatus status,
    List<EChange<EObject>> executedChanges,
    Throwable cause) {

  /**
   * Creates a new invocation.
   *
   * @throws NullPointerException if any argument but cause is {@code null}
   * @throws IllegalArgumentException if a cause is given for a status other than
   *     {@link ChangePropagationStatus#FAILED}
   */
  public ChangePropagationInvocation {
    checkNotNull(specification, "specification must not be null");
    checkNotNull(receivedChanges, "received changes must not be null");
    checkNotNull(status, "status must not be null");
    checkNotNull(executedChanges, "executed changes must not be null");
    checkArgument(
        cause == null || status == ChangePropagationStatus.FAILED,
        "a cause is only allowed for the status %s, but the status is %s",
        ChangePropagationStatus.FAILED,
        status);
    receivedChanges = List.copyOf(receivedChanges);
    executedChanges = List.copyOf(executedChanges);
  }

  /**
   * Creates an invocation that combines the invocations that took place during its call. The
   * combined invocations are not kept. The status and the cause are those of the first
   * invocation that is not {@link ChangePropagationStatus#SUCCEEDED}, otherwise the status is
   * {@code SUCCEEDED}; the executed changes are those of all invocations, concatenated in order.
   *
   * @param specification the specification that was invoked
   * @param receivedChanges the changes the call received
   * @param invocations the invocations that took place during the call, in execution order; may be
   *     empty, which results in a succeeded invocation without executed changes
   * @throws NullPointerException if the list or an element of the list is {@code null}
   */
  public ChangePropagationInvocation(
      ChangePropagationSpecification specification,
      List<EChange<EObject>> receivedChanges,
      List<ChangePropagationInvocation> invocations) {
    this(
        specification,
        receivedChanges,
        derivedStatus(firstNonSucceeded(invocations)),
        concatenatedExecutedChanges(invocations),
        firstNonSucceeded(invocations).map(ChangePropagationInvocation::cause).orElse(null));
  }

  /**
   * Creates an invocation that completed all its effects.
   *
   * @param specification the specification that was invoked
   * @param receivedChanges the changes the call received
   * @param executedChanges the changes the specification executed
   * @return an invocation with the status {@link ChangePropagationStatus#SUCCEEDED}
   */
  public static ChangePropagationInvocation succeeded(
      ChangePropagationSpecification specification,
      List<EChange<EObject>> receivedChanges,
      List<EChange<EObject>> executedChanges) {
    return new ChangePropagationInvocation(
        specification, receivedChanges, ChangePropagationStatus.SUCCEEDED, executedChanges, null);
  }

  /**
   * Creates an invocation that stopped before completing its effects, without an error.
   *
   * @param specification the specification that was invoked
   * @param receivedChanges the changes the call received
   * @param executedChanges the changes the specification executed before it stopped
   * @return an invocation with the status {@link ChangePropagationStatus#BLOCKED}
   */
  public static ChangePropagationInvocation blocked(
      ChangePropagationSpecification specification,
      List<EChange<EObject>> receivedChanges,
      List<EChange<EObject>> executedChanges) {
    return new ChangePropagationInvocation(
        specification, receivedChanges, ChangePropagationStatus.BLOCKED, executedChanges, null);
  }

  /**
   * Creates an invocation during which an error occurred.
   *
   * @param specification the specification that was invoked
   * @param receivedChanges the changes the call received
   * @param executedChanges the changes the specification executed before the error
   * @param cause the error, or {@code null} if the failure was reported without an exception
   * @return an invocation with the status {@link ChangePropagationStatus#FAILED}
   */
  public static ChangePropagationInvocation failed(
      ChangePropagationSpecification specification,
      List<EChange<EObject>> receivedChanges,
      List<EChange<EObject>> executedChanges,
      Throwable cause) {
    return new ChangePropagationInvocation(
        specification, receivedChanges, ChangePropagationStatus.FAILED, executedChanges, cause);
  }

  private static Optional<ChangePropagationInvocation> firstNonSucceeded(
      List<ChangePropagationInvocation> invocations) {
    return invocations.stream()
        .filter(invocation -> invocation.status() != ChangePropagationStatus.SUCCEEDED)
        .findFirst();
  }

  private static ChangePropagationStatus derivedStatus(
      Optional<ChangePropagationInvocation> firstNonSucceeded) {
    return firstNonSucceeded
        .map(ChangePropagationInvocation::status)
        .orElse(ChangePropagationStatus.SUCCEEDED);
  }

  private static List<EChange<EObject>> concatenatedExecutedChanges(
      List<ChangePropagationInvocation> invocations) {
    return invocations.stream()
        .flatMap(invocation -> invocation.executedChanges().stream())
        .toList();
  }
}
