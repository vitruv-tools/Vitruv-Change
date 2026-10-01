package tools.vitruv.change.propagation;

import java.util.List;
import tools.vitruv.change.composite.description.PropagatedChange;

/**
 * Result of one attempt to propagate the first item of the change propagation queue.
 *
 * @param taskId the id of the item that was attempted
 * @param outcome what happened to it
 * @param propagatedChanges the changes propagated in this step, empty unless the outcome is {@link
 *     Outcome#PROPAGATED}
 */
public record QueuedPropagationStep(
    int taskId, Outcome outcome, List<PropagatedChange> propagatedChanges) {

  /** What happened to the attempted item. */
  public enum Outcome {
    /** The item was propagated; the changes this produced were queued as its children. */
    PROPAGATED,
    /** The item needs an unanswered user interaction; it was blocked and requeued. */
    DEFERRED,
    /** The item was blocked and therefore requeued without being propagated. */
    SKIPPED_BLOCKED
  }

  /** Creates the step; {@code propagatedChanges} is copied. */
  public QueuedPropagationStep {
    propagatedChanges = List.copyOf(propagatedChanges);
  }
}
