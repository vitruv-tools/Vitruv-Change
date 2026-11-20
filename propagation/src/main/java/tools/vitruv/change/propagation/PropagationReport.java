package tools.vitruv.change.propagation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import tools.vitruv.change.composite.description.PropagatedChange;

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
 */
public record PropagationReport(
    List<PropagatedChange> propagatedChanges, ChangePropagationResult result) {

  /**
   * Creates a report.
   *
   * @throws NullPointerException if the result, the list or an element of the list is {@code null}
   */
  public PropagationReport {
    checkNotNull(propagatedChanges, "propagated changes must not be null");
    checkNotNull(result, "result must not be null");
    propagatedChanges = List.copyOf(propagatedChanges);
  }
}
