package tools.vitruv.change.composite.description.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.HierarchicalId;
import tools.vitruv.change.atomic.resolve.AtomicEChangeIdResolver;
import tools.vitruv.change.atomic.resolve.EChangeIdResolverAndApplicator;
import tools.vitruv.change.composite.description.VitruviusChange;

public class VitruviusChangeIdResolver extends AbstractVitruviusChangeResolver<HierarchicalId> {
	private AtomicEChangeIdResolver atomicChangeResolver;

	public VitruviusChangeIdResolver(AtomicEChangeIdResolver atomicChangeResolver) {
		this.atomicChangeResolver = atomicChangeResolver;
	}

	@Override
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<HierarchicalId> change) {
		return resolveAndApply(change, atomicChangeResolver::resolveAndApplyForward);
	}

	@Override
	public VitruviusChange<HierarchicalId> assignIds(VitruviusChange<EObject> change) {
		applyBackward(change);
		return applyForwardAndAssignIds(change);
	}

	private void applyBackward(VitruviusChange<EObject> change) {
		if (change instanceof CompositeContainerChangeImpl<EObject> compositeChange) {
			List<VitruviusChange<EObject>> changes = new LinkedList<>(compositeChange.getChanges());
			Collections.reverse(changes);
			changes.forEach(this::applyBackward);
		} else if (change instanceof TransactionalChangeImpl<EObject> transactionalChange) {
			List<EChange<EObject>> changes = new LinkedList<>(transactionalChange.getEChanges());
			Collections.reverse(changes);
			changes.forEach(EChangeIdResolverAndApplicator::applyBackward);
		}
		else {
			throw new IllegalStateException("trying to apply unknown change of class " + change.getClass().getSimpleName());
		}
	}

	private VitruviusChange<HierarchicalId> applyForwardAndAssignIds(VitruviusChange<EObject> change) {
		if (change instanceof CompositeContainerChangeImpl<EObject> compositeChange) {
			return new CompositeContainerChangeImpl<>(
					compositeChange.getChanges().stream().map(this::applyForwardAndAssignIds).toList());
		} else if (change instanceof TransactionalChangeImpl<EObject> transactionalChange) {
			List<EChange<HierarchicalId>> unresolvedChanges = transactionalChange.getEChanges().stream()
					.map(atomicChangeResolver::applyForwardAndAssignIds).toList();
			return new TransactionalChangeImpl<>(unresolvedChanges);
		}
		throw new IllegalStateException("trying to apply unknown change of class " + change.getClass().getSimpleName());
	}

}
