package tools.vitruv.change.propagation;

import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.propagation.impl.ChangePropagator;

/**
 * Hook of the {@link ChangePropagator} for concurrency control, e.g. strict two-phase locking: it
 * is told when a propagation starts and ends, before the user's change is applied, and before
 * each propagation step (one specification propagating one change). All calls of one propagation
 * happen on the propagating thread; implementations may block there (waiting for locks).
 *
 * <p>To abort a propagation (e.g. as deadlock victim), a callback throws
 * {@link PropagationAbortedException}; a specification may throw it as well (e.g. from a lock
 * request while it runs). The propagation then halts as {@link ChangePropagationStatus#BLOCKED}.
 * Whenever a guard is set and a propagation does not complete (aborted, blocked or failed), the
 * propagator reverts every change applied in it so far (the user's change and all consequential
 * changes, in reverse order) and, if the model repository records them (as the default one does),
 * the changes of the correspondence model, and then calls
 * {@link #endPropagation(VitruviusChange, boolean) endPropagation(change, false)}. The propagator
 * does not retry: the caller resubmits the change, passing the
 * {@link PropagationReport#userInteractions()} of the halted propagation so that the user is not
 * asked again.
 *
 * <p>Each propagation is bracketed by {@link #beginPropagation} and {@link #endPropagation}, also
 * when it fails.
 */
public interface PropagationStepGuard {
  /** A guard that does nothing. */
  PropagationStepGuard NONE = new PropagationStepGuard() { };

  /**
   * A propagation of {@code change} starts. Called again with the same object when the caller
   * resubmits the change.
   *
   * @param change the user's change
   * @throws PropagationAbortedException to abort the propagation
   */
  default void beginPropagation(VitruviusChange<Uuid> change) {
  }

  /**
   * {@code change}, part of the user's change, is about to be applied to the models.
   *
   * @param change the part of the user's change
   * @throws PropagationAbortedException to abort the propagation
   */
  default void beforeApply(TransactionalChange<Uuid> change) {
  }

  /**
   * {@code specification} is about to propagate {@code change} (applied already).
   *
   * @param change the change to propagate
   * @param specification the specification that propagates it
   * @throws PropagationAbortedException to abort the propagation
   */
  default void beforeStep(TransactionalChange<EObject> change,
      ChangePropagationSpecification specification) {
  }

  /**
   * The propagation of {@code change} ended: {@code committed} if it completed (and its commit
   * succeeded), otherwise its changes have been reverted.
   *
   * @param change the user's change
   * @param committed whether the propagation completed
   */
  default void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
  }
}
