package tools.vitruv.change.composite.description.impl;

import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;

public abstract class AbstractVitruviusChangeResolver<Id> implements VitruviusChangeResolver<Id> {
	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method. Returns the
	 * resolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or applied.
	 */
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<Id> change,
			Function<EChange<Id>, EChange<EObject>> resolveAndApplyForward) {
		if (change instanceof CompositeContainerChangeImpl<Id> compositeChange) {
			return new CompositeContainerChangeImpl<>(
					compositeChange.getChanges().stream().map(this::resolveAndApply).toList());
		} else if (change instanceof TransactionalChangeImpl<Id> transactionalChange) {
			List<EChange<EObject>> resolvedChanges = transactionalChange.getEChanges().stream()
					.map(resolveAndApplyForward::apply).toList();
			return new TransactionalChangeImpl<>(resolvedChanges);
		}
		throw new IllegalStateException(
				"trying to resolve unknown change of class " + change.getClass().getSimpleName());
	}
}
