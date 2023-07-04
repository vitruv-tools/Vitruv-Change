package tools.vitruv.change.atomic.resolve;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;

/**
 * Resolves or unresolves any {@link EChange}.
 * If the {@code Identifier} for the same element in the same state may differ,
 * resolving and unresolving a change may not result in an equal resulting change.
 *
 * @param <Identifier> the type of identifier used.
 */
public interface AtomicEChangeResolver<Identifier> {
	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method. Returns the
	 * resolved change.
	 */
	EChange<EObject> resolveAndApplyForward(EChange<Identifier> unresolvedEChange);

	/**
	 * Unresolved the change and applies it backward so that the model is in the
	 * state before the change afterwards. It has to be ensured that the model is in
	 * a state after the change has been applied before calling this method. Returns
	 * the unresolved change.
	 */
	EChange<Identifier> unresolveAndApplyBackward(EChange<EObject> resolvedEChange);
}
