package tools.vitruv.change.propagation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.interaction.UserInteractionBase;

/**
 * The report of one change propagation run: the propagated changes it produced, and
 * the {@link ChangePropagationResult} with every invocation of a {@link
 * ChangePropagationSpecification} during the run.
 *
 * <p>The latter {@link ChangePropagationResult} only records explicitly made changes
 * by a {@link ChangePropagationSpecification}, whereas the propagated changes may also
 * contain further changes derived by e.g. notifications.
 *
 * <p>If a specification is {@link ChangePropagationStatus#BLOCKED} or {@link
 * ChangePropagationStatus#FAILED}, propagation has halted.
 * All changes in {@code propagatedChanges} remain applied.
 *
 * @param propagatedChanges the propagated changes of the run, in the order the engine produced
 *     them; possibly partial after a halt. Stored as an unmodifiable copy.
 * @param result the appended results of every specification call of the run, in execution order,
 *     Its status is the first status that is not {@link ChangePropagationStatus#SUCCEEDED}.
 *     Never {@code null}.
 * @param userInteractions the user interactions of the run, in order; to answer them alike when
 *     a halted propagation is resubmitted. Stored as an unmodifiable copy.
 * @param abort the {@link PropagationAbortedException} that halted the run, thrown by a
 *     {@link PropagationStepGuard} or from within a specification; {@code null} if there is none
 */
public record PropagationReport(
    List<PropagatedChange> propagatedChanges, ChangePropagationResult result,
    List<UserInteractionBase> userInteractions, PropagationAbortedException abort) {

  /**
   * Creates a report.
   *
   * @throws NullPointerException if the result, the list or an element of the list is {@code null}
   */
  public PropagationReport {
    checkNotNull(propagatedChanges, "propagated changes must not be null");
    checkNotNull(result, "result must not be null");
    checkNotNull(userInteractions, "user interactions must not be null");
    propagatedChanges = List.copyOf(propagatedChanges);
    userInteractions = List.copyOf(userInteractions);
  }

  /**
   * Creates a report of a run without user interactions and without abort.
   *
   * @param propagatedChanges the propagated changes of the run
   * @param result the result of the run
   */
  public PropagationReport(List<PropagatedChange> propagatedChanges,
      ChangePropagationResult result) {
    this(propagatedChanges, result, List.of(), null);
  }

  /**
   * Returns the status of the run: {@link ChangePropagationStatus#BLOCKED} if it was aborted,
   * otherwise the status of the {@link #result()}.
   *
   * @return the status of the run
   */
  public ChangePropagationStatus status() {
    return abort != null ? ChangePropagationStatus.BLOCKED : result.status();
  }
}
