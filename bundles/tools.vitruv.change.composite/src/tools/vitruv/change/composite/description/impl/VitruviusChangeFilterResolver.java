package tools.vitruv.change.composite.description.impl;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.hid.AtomicEChangeHierarchicalIdFilterResolver;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.composite.description.VitruviusChange;

/**
 * This Resolver can be used to resolve {@link VitruvChange} Objects which have been made on a 
 * filtered {@link ResourceSet} on the unfiltered {@link ResourceSet}. The resolver uses {@link HierarchicalId}s.
 * May not be used to assign Ids but only to resolve Changes which reference {@link HierarchicalId}s.
 */
public class VitruviusChangeFilterResolver extends AbstractVitruviusChangeResolver<HierarchicalId> {
	
	private AtomicEChangeHierarchicalIdFilterResolver atomicChangeResolver;

	public VitruviusChangeFilterResolver(AtomicEChangeHierarchicalIdFilterResolver atomicChangeResolver) {
		this.atomicChangeResolver = atomicChangeResolver;
	}

	
	@Override
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<HierarchicalId> change) {
		return transformVitruviusChange(change, atomicChangeResolver::resolveAndApplyForward,
				transactionalChange -> atomicChangeResolver.endTransaction());
	}
	
	@Override
	public VitruviusChange<HierarchicalId> assignIds(VitruviusChange<EObject> change) {
		throw new Error("assigning ids is not supported");
	}
}
