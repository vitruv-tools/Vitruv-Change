package tools.vitruv.change.atomic.hid.internal;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import tools.vitruv.change.atomic.hid.HierarchicalId;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

/**
 * This is a special {@link HierarchicalIdResolver} which can be used to resolve {@link HierarchicalId}s on Filtered-Models.
 * The Resolver therefore needs the filtered {@link ResourceSet} as well as the unfiltered {@link ResourceSet}. The Resolver first tries to 
 * resolve a given {@link HierarchicalId} in the filtered {@link ResourceSet}. If the {@link HierarchicalId} can be resolved in the 
 * filtered {@link ResourceSet}, the Resolver uses the mapping-function, which has been provided during construction 
 * {@link mapCopy2OriginalObject}, to find the corresponding {@link EObject} in the unfiltered {@link ResourceSet}.
 */
public class FilterModelResolverImpl extends HierarchicalIdResolverImpl {
	
	private Map<EObject, EObject> mapCopy2OriginalObject;

	public FilterModelResolverImpl(ResourceSet filterResourceSet, ResourceSet preFilterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		super(filterResourceSet);
		this.mapCopy2OriginalObject = mapCopy2OriginalObject;
		checkArgument(filterResourceSet != null, "Resource set may not be null");		
	}
	
	
	@Override
	public EObject getEObject(HierarchicalId id) {
		EObject objectInFilterSet = super.getEObject(id);
		EObject eObject = getEObjectInUnfilteredSet(objectInFilterSet);
		checkState(eObject != null, "no EObject could be found for ID: %s", id);
		return eObject;
	}
	
	
	private EObject getEObjectInUnfilteredSet(EObject objectInFilteredSet) {
		EObject eObjectInViewResourceSet = mapCopy2OriginalObject.get(objectInFilteredSet);
		return eObjectInViewResourceSet;		
	}
	
}
