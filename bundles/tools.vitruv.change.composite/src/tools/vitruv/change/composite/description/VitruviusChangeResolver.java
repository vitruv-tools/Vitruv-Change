package tools.vitruv.change.composite.description;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.Id;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.resolve.EChangeIdResolverAndApplicator;
import tools.vitruv.change.atomic.resolve.EChangeUuidResolverAndApplicator;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.impl.CompositeContainerChangeImpl;
import tools.vitruv.change.composite.description.impl.TransactionalChangeImpl;

public final class VitruviusChangeResolver {
	private VitruviusChangeResolver() { }
	
	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method and that the
	 * changes use UUIDs. Returns the resolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or is already
	 *                               resolved.
	 */
	public static VitruviusChange<EObject> resolveAndApply(VitruviusChange<Uuid> change, UuidResolver uuidResolver) {
		if (change instanceof CompositeContainerChangeImpl<Uuid> compositeChange) {
			return new CompositeContainerChangeImpl<>(compositeChange.getChanges().stream().map(it -> resolveAndApply(change, uuidResolver)).toList());
		}
		else if (change instanceof TransactionalChangeImpl<Uuid> transactionalChange) {
			List<EChange> resolvedChanges = transactionalChange.getEChanges().stream().map(eChange -> {
				EChange resolvedEChange = EChangeUuidResolverAndApplicator.resolveBefore(eChange, uuidResolver);
				EChangeUuidResolverAndApplicator.applyForward(resolvedEChange, uuidResolver);
				return resolvedEChange;
			}).toList();
			return new TransactionalChangeImpl<>(resolvedChanges);
		}
		throw new IllegalStateException("trying to resolve unknown change of class " + change.getClass().getSimpleName());
	}

	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method and that the
	 * changes use hierarchical IDs. Returns the resolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or is already
	 *                               resolved.
	 */
	public static VitruviusChange<EObject> resolveAndApply(VitruviusChange<Id> change, IdResolver idResolver) {
		if (change instanceof CompositeContainerChangeImpl<Id> compositeChange) {
			return new CompositeContainerChangeImpl<>(compositeChange.getChanges().stream().map(it -> resolveAndApply(it, idResolver)).toList());
		}
		else if (change instanceof TransactionalChangeImpl<Id> transactionalChange) {
			List<EChange> resolvedChanges = transactionalChange.getEChanges().stream().map(eChange -> {
				EChange resolvedEChange = EChangeIdResolverAndApplicator.resolveBefore(eChange, idResolver);
				EChangeIdResolverAndApplicator.applyForward(resolvedEChange, idResolver);
				return resolvedEChange;
			}).toList();
			return new TransactionalChangeImpl<>(resolvedChanges);
		}
		throw new IllegalStateException("trying to resolve unknown change of class " + change.getClass().getSimpleName());
	}
	
	/**
	 * Returns an unresolved change, such that all its affected and referenced
	 * {@link EObjects} are removed.
	 */
	public static VitruviusChange<Uuid> unresolve(VitruviusChange<EObject> change, UuidResolver uuidResolver) {
		if (change instanceof CompositeContainerChangeImpl<EObject> containerChange) {
			return new CompositeContainerChangeImpl<>(containerChange.getChanges().stream().map(c -> unresolve(c,  uuidResolver)).toList());
		}
		else if (change instanceof TransactionalChangeImpl<EObject> transactionalChange) {
			List<EChange> unresolvedEChanges = transactionalChange.getEChanges().stream().map(EChangeUuidResolverAndApplicator::unresolve).toList();
			return new TransactionalChangeImpl<>(unresolvedEChanges);
		}
		throw new IllegalStateException("trying to unresolve unknown change of class " + change.getClass().getSimpleName());
	}
	
	/**
	 * Returns an unresolved change, such that all its affected and referenced
	 * {@link EObjects} are removed.
	 */
	public static VitruviusChange<Id> unresolve(VitruviusChange<EObject> change, IdResolver idResolver) {
		if (change instanceof CompositeContainerChangeImpl<EObject> containerChange) {
			return new CompositeContainerChangeImpl<>(containerChange.getChanges().stream().map(c -> unresolve(c, idResolver)).toList());
		}
		else if (change instanceof TransactionalChangeImpl<EObject> transactionalChange) {
			List<EChange> unresolvedEChanges = transactionalChange.getEChanges().stream().map(EChangeUuidResolverAndApplicator::unresolve).toList();
			return new TransactionalChangeImpl<>(unresolvedEChanges);
		}
		throw new IllegalStateException("trying to unresolve unknown change of class " + change.getClass().getSimpleName());
	}
}
