package tools.vitruv.change.atomic.hid;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.command.internal.ApplyEChangeSwitch;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange;
import tools.vitruv.change.atomic.hid.internal.FilterModelResolverImpl;
import tools.vitruv.change.atomic.resolve.AtomicEChangeResolverHelper;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

public class AtomicEChangeFilterResolver {

	private FilterModelResolverImpl filterResolver;


	public AtomicEChangeFilterResolver(ResourceSet filterResourceSet, ResourceSet preFilterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		filterResolver = new FilterModelResolverImpl(filterResourceSet, preFilterResourceSet, mapCopy2OriginalObject);
	}
	
	/**
	 * Applies a change backward. Since hierarchical Ids can only be assigned when
	 * the resource set is in the state before the change, this method might be used
	 * to reverse some change to allow Id assignment.
	 * 
	 * @param resolvedEChange the change to reverse.
	 * @see AtomicEChangeHierarchicalIdResolver#applyForwardAndAssignIds(EChange)
	 */
	public void applyBackward(EChange<EObject> resolvedEChange) {
		ApplyEChangeSwitch.applyEChange(resolvedEChange, false);
	}
	
	
	public EChange<EObject> applyForwardAndMapToObject(EChange<EObject> resolvedEChange) {
		EChange<EObject> unresolvedChange = unresolve(resolvedEChange);
		applyForward(resolvedEChange);
		return unresolvedChange;
	}
	
	
	private EChange<EObject> unresolve(EChange<EObject> resolvedChange) {
		return AtomicEChangeResolverHelper.resolveChange(resolvedChange, eObject -> filterResolver.getPreFilterObject(eObject),
				resource -> filterResolver.getResource(resource.getURI()));
	}
	
	
	private void applyForward(EChange<EObject> resolvedChange) {
		EObject affectedEObject = getAffectedEObject(resolvedChange);
		HierarchicalId affectedId = filterResolver.getAndUpdateId(affectedEObject);
		EObject oldObject = getOldContainedEObject(resolvedChange);
		ApplyEChangeSwitch.applyEChange(resolvedChange, true);
		if (isContainmentChange(resolvedChange) || affectedId != filterResolver.getAndUpdateId(affectedEObject)) {
			refreshIds(affectedEObject);
		}
		if (oldObject != null) {
			refreshIds(oldObject);
		}
	}
	
	
	private static EObject getAffectedEObject(EChange<EObject> eChange) {
		if (eChange instanceof FeatureEChange<EObject, ?> featureChange) {
			return featureChange.getAffectedElement();
		} else if (eChange instanceof EObjectExistenceEChange<EObject> existenceChange) {
			return existenceChange.getAffectedElement();
		} else if (eChange instanceof InsertRootEObject<EObject> insertChange) {
			return insertChange.getNewValue();
		} else if (eChange instanceof RemoveRootEObject<EObject> removeChange) {
			return removeChange.getOldValue();
		}
		throw new IllegalArgumentException("cannot identify affected EObject of change %s".formatted(eChange));
	}
	
	
	private static EObject getOldContainedEObject(EChange<EObject> eChange) {
		if (eChange instanceof SubtractiveReferenceEChange<EObject> subtractiveChange) {
			if (subtractiveChange.isContainment()) {
				return subtractiveChange.getOldValue();
			}
		}
		return null;
	}
	
	
	/**
	 * Ends a transactions such that all {@link EObject}s not being contained in a
	 * resource, which is contained in a resource set, are removed from the
	 * hierarchical ID mapping.
	 */
	public void endTransaction() {
		
		throw new Error();
		//idResolver.endTransaction();
	}
}
