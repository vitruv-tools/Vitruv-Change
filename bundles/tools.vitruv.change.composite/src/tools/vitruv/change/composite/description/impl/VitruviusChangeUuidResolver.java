package tools.vitruv.change.composite.description.impl;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.AtomicEChangeUuidResolver;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;

public class VitruviusChangeUuidResolver extends AbstractVitruviusChangeResolver<Uuid> {
	private AtomicEChangeUuidResolver atomicChangeResolver;

	public VitruviusChangeUuidResolver(AtomicEChangeUuidResolver atomicChangeResolver) {
		this.atomicChangeResolver = atomicChangeResolver;
	}

	@Override
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<Uuid> change) {
		return resolveAndApply(change, atomicChangeResolver::resolveAndApplyForward,
				transactionalChange -> atomicChangeResolver.endTransaction());
	}

	@Override
	public VitruviusChange<Uuid> assignIds(VitruviusChange<EObject> change) {
		if (change instanceof CompositeContainerChangeImpl<EObject> compositeChange) {
			return new CompositeContainerChangeImpl<>(
					compositeChange.getChanges().stream().map(it -> assignIds(it)).toList());
		} else if (change instanceof TransactionalChangeImpl<EObject> transactionalChange) {
			List<EChange<Uuid>> resolvedChanges = transactionalChange.getEChanges().stream()
					.map(atomicChangeResolver::assignIds).toList();
			atomicChangeResolver.endTransaction();
			return new TransactionalChangeImpl<>(resolvedChanges);
		}
		throw new IllegalStateException(
				"trying to resolve unknown change of class " + change.getClass().getSimpleName());
	}

}
