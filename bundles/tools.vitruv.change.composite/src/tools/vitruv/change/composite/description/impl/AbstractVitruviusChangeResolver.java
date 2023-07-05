package tools.vitruv.change.composite.description.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;

public abstract class AbstractVitruviusChangeResolver<Id> implements VitruviusChangeResolver<Id> {
	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method. Returns the
	 * resolved change.
	 * 
	 * @parameter change the change to resolve
	 * @parameter resolveAndApplyForward the handler for resolving and applying a
	 *            single {@code EChange}.
	 * @parameter onTransactionEnd any cleanup logic after a transactional change
	 *            has been completely resolved and applied. This might be called
	 *            multiple times with different changes if the passed change is a
	 *            composite change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or applied.
	 */
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<Id> change,
			Function<EChange<Id>, EChange<EObject>> resolveAndApplyForward,
			Consumer<TransactionalChange<EObject>> onTransactionEnd) {
		if (change instanceof CompositeContainerChangeImpl<Id> compositeChange) {
			return new CompositeContainerChangeImpl<>(
					compositeChange.getChanges().stream().map(this::resolveAndApply).toList());
		} else if (change instanceof TransactionalChangeImpl<Id> transactionalChange) {
			List<EChange<EObject>> resolvedChanges = transactionalChange.getEChanges().stream()
					.map(resolveAndApplyForward::apply).toList();
			TransactionalChange<EObject> result = new TransactionalChangeImpl<>(resolvedChanges);
			onTransactionEnd.accept(result);
			return result;
		}
		throw new IllegalStateException(
				"trying to resolve unknown change of class " + change.getClass().getSimpleName());
	}
}
