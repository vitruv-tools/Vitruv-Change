package tools.vitruv.change.composite.description.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.AtomicEChangeIdResolver;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.composite.description.VitruviusChange;

public class VitruviusChangeIdResolver extends AbstractVitruviusChangeResolver<HierarchicalId> {
	private AtomicEChangeIdResolver atomicChangeResolver;

	public VitruviusChangeIdResolver(AtomicEChangeIdResolver atomicChangeResolver) {
		this.atomicChangeResolver = atomicChangeResolver;
	}

	@Override
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<HierarchicalId> change) {
		return transformVitruviusChange(change, atomicChangeResolver::resolveAndApplyForward,
				transactionalChange -> atomicChangeResolver.endTransaction());
	}

	@Override
	public VitruviusChange<HierarchicalId> assignIds(VitruviusChange<EObject> change) {
		applyBackward(change);
		VitruviusChange<HierarchicalId> result = transformVitruviusChange(change, atomicChangeResolver::applyForwardAndAssignIds, transactionalChange -> {});
		/**
		 * TODO: the correct handling would be to call endTransaction() each time after
		 * a transactional change is applied forward or backward. Due to incomplete
		 * change recording (https://github.com/vitruv-tools/Vitruv-Change/issues/71)
		 * this would result in failures when handling a composite change with multiple
		 * transactional changes as containment information of cascade deleted elements
		 * would be lost.
		 */
		atomicChangeResolver.endTransaction();
		return result;
	}

	private void applyBackward(VitruviusChange<EObject> change) {
		if (change instanceof CompositeContainerChangeImpl<EObject> compositeChange) {
			List<VitruviusChange<EObject>> changes = new LinkedList<>(compositeChange.getChanges());
			Collections.reverse(changes);
			changes.forEach(this::applyBackward);
		} else if (change instanceof TransactionalChangeImpl<EObject> transactionalChange) {
			List<EChange<EObject>> changes = new LinkedList<>(transactionalChange.getEChanges());
			Collections.reverse(changes);
			changes.forEach(atomicChangeResolver::applyBackward);
		} else {
			throw new IllegalStateException(
					"trying to apply unknown change of class " + change.getClass().getSimpleName());
		}
	}
}
