package tools.vitruv.change.composite.description;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.resolve.AtomicEChangeResolver;
import tools.vitruv.change.composite.description.impl.CompositeContainerChangeImpl;
import tools.vitruv.change.composite.description.impl.TransactionalChangeImpl;

public final class VitruviusChangeResolver<Id> {
	private AtomicEChangeResolver<Id> atomicChangeResolver;

	public VitruviusChangeResolver(AtomicEChangeResolver<Id> atomicChangeResolver) {
		this.atomicChangeResolver = atomicChangeResolver;
	}

	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method. Returns the
	 * resolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or applied.
	 */
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<Id> change) {
		if (change instanceof CompositeContainerChangeImpl<Id> compositeChange) {
			return new CompositeContainerChangeImpl<>(
					compositeChange.getChanges().stream().map(it -> resolveAndApply(it)).toList());
		} else if (change instanceof TransactionalChangeImpl<Id> transactionalChange) {
			List<EChange<EObject>> resolvedChanges = transactionalChange.getEChanges().stream()
					.map(eChange -> atomicChangeResolver.resolveAndApplyForward(eChange)).toList();
			return new TransactionalChangeImpl<>(resolvedChanges);
		}
		throw new IllegalStateException(
				"trying to resolve unknown change of class " + change.getClass().getSimpleName());
	}

	/**
	 * Unresolved the change and applies it backward so that the model is in the
	 * state before the change afterwards. It has to be ensured that the model is in
	 * a state after the change has been applied before calling this method. Returns
	 * the unresolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be applied backwards.
	 */
	public VitruviusChange<Id> unresolveAndUnapply(VitruviusChange<EObject> change) {
		if (change instanceof CompositeContainerChangeImpl<EObject> containerChange) {
			List<VitruviusChange<EObject>> reversedResolvedChanges = new LinkedList<>(containerChange.getChanges());
			Collections.reverse(reversedResolvedChanges);
			List<VitruviusChange<Id>> unresolvedChanges = new LinkedList<>(
					reversedResolvedChanges.stream().map(c -> unresolveAndUnapply(c)).toList());
			Collections.reverse(unresolvedChanges);
			return new CompositeContainerChangeImpl<>(unresolvedChanges);
		} else if (change instanceof TransactionalChangeImpl<EObject> transactionalChange) {
			List<EChange<EObject>> reversedResolvedEChanges = new LinkedList<>(transactionalChange.getEChanges());
			Collections.reverse(reversedResolvedEChanges);
			List<EChange<Id>> unresolvedEChanges = new LinkedList<>(reversedResolvedEChanges.stream()
					.map(c -> atomicChangeResolver.unresolveAndApplyBackward(c)).toList());
			Collections.reverse(unresolvedEChanges);
			return new TransactionalChangeImpl<>(unresolvedEChanges);
		}
		throw new IllegalStateException(
				"trying to unresolve unknown change of class " + change.getClass().getSimpleName());
	}
}
