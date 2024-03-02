package tools.vitruv.change.composite.description;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.AtomicEChangeHierarchicalIdResolver;
import tools.vitruv.change.composite.description.impl.CompositeContainerChangeImpl;
import tools.vitruv.change.composite.description.impl.TransactionalChangeImpl;

//TODO nbr: Add Javadoc
public class VitruviusChangeBackwardsExecutionHelper {
	
	private AtomicEChangeHierarchicalIdResolver atomicChangeResolver;

	public VitruviusChangeBackwardsExecutionHelper(ResourceSet resourceSet) {
		atomicChangeResolver = new AtomicEChangeHierarchicalIdResolver(resourceSet);
	}
	
	
	public void applyBackward(VitruviusChange<EObject> change) {
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
