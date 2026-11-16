package tools.vitruv.change.propagation;

import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.propagation.impl.ChangePropagator;

/**
 * Hook of the {@link ChangePropagator} for concurrency control, e.g. strict two-phase locking: it is
 * told when a propagation starts and ends, before the user's change is applied, and before each
 * propagation step (one specification propagating one change). All calls of one propagation
 * happen on the propagating thread; implementations may block there (waiting for locks).
 *
 * <p>To abort a propagation (e.g. as deadlock victim), a callback throws
 * {@link PropagationAbortedException}. The propagator then reverts every change applied in the
 * propagation so far (the user's change and all consequential changes, in reverse order), calls
 * {@link #endPropagation(VitruviusChange, boolean) endPropagation(change, false)} and starts the
 * propagation again, answering user interactions with the answers given in the aborted attempt.
 * Changes of the correspondence model are not reverted (it is not recorded).
 *
 * <p>Each attempt is bracketed by {@link #beginPropagation} and {@link #endPropagation}, also when
 * the propagation fails for another reason.
 */
public interface PropagationStepGuard {
  /** A guard that does nothing. */
  PropagationStepGuard NONE = new PropagationStepGuard() { };

  /**
   * An attempt to propagate {@code change} starts. Called again with the same object when the
   * propagation is retried after an abort.
   */
  default void beginPropagation(VitruviusChange<Uuid> change) {
  }

  /** {@code change}, part of the user's change, is about to be applied to the models. */
  default void beforeApply(TransactionalChange<Uuid> change) {
  }

  /** {@code specification} is about to propagate {@code change} (applied already). */
  default void beforeStep(TransactionalChange<EObject> change,
      ChangePropagationSpecification specification) {
  }

  /**
   * The attempt to propagate {@code change} ended: {@code committed} if the propagation completed,
   * otherwise it was aborted (and its changes reverted) or failed.
   */
  default void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
  }
}
