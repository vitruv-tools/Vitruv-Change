package tools.vitruv.change.composite.description.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.AtomicEChangeFilterResolver;
import tools.vitruv.change.atomic.hid.AtomicEChangeHierarchicalIdResolver;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.composite.description.VitruviusChange;

/**
 * This Resolver can be used to resolve {@link VitruvChange} Objects which have been made on a 
 * filtered {@link ResourceSet} on the unfiltered {@link ResourceSet}. The resolver uses {@link HierarchicalId}s.
 * May not be used to assign Ids but only to resolve Changes which reference {@link HierarchicalId}s.
 */
public class VitruviusChangeFilterResolver extends AbstractVitruviusChangeResolver<HierarchicalId> {
	
	private AtomicEChangeFilterResolver atomicChangeResolver;

	public VitruviusChangeFilterResolver(AtomicEChangeFilterResolver atomicChangeResolver) {
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
