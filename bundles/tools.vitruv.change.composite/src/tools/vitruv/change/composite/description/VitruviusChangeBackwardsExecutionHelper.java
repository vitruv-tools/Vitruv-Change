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

/**
 * Utility class to perform a {@link VitruvChange} backwards on a given {@link ResourceSet}. 
 */
public class VitruviusChangeBackwardsExecutionHelper {
		
	/**
	 * Applies the given {@link VitruvChange} backwards on the given {@link ResourceSet} The given 
	 * {@link VitruvChange} must 
	 * only reference {@link EObject}s in the given {@link ResourceSet}. After the execution, the 
	 * given {@link ResourceSet} will be in the state before the execution of the change. The 
	 * given {@link ResourceSet} must be in the state after the execution of the given {@link VitruvChange}.
	 * 
	 * @param resourceSet The resourceSet on which the change is supposed to be executed backwards
	 * @param change The change which is supposed to be executed backwards
	 */
	public static void applyBackward(ResourceSet resourceSet, VitruviusChange<EObject> change) {
		AtomicEChangeHierarchicalIdResolver atomicChangeResolver = new AtomicEChangeHierarchicalIdResolver(resourceSet);
		if (change instanceof CompositeContainerChangeImpl<EObject> compositeChange) {
			List<VitruviusChange<EObject>> changes = new LinkedList<>(compositeChange.getChanges());
			Collections.reverse(changes);
			changes.forEach(it -> applyBackward(resourceSet, it));
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
