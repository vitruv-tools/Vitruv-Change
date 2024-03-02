package tools.vitruv.change.atomic.hid.internal;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import tools.vitruv.change.atomic.hid.HierarchicalId;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

//TODO nbr: Add Javadoc
public class FilterModelResolverImpl extends HierarchicalIdResolverImpl {
	
	private ResourceSet filterResourceSet;

	private Map<EObject, EObject> mapCopy2OriginalObject;

	public FilterModelResolverImpl(ResourceSet filterResourceSet, ResourceSet preFilterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		super(preFilterResourceSet);
		this.mapCopy2OriginalObject = mapCopy2OriginalObject;
		checkArgument(filterResourceSet != null, "Resource set may not be null");
		this.filterResourceSet = filterResourceSet;
		
	}
	
	
	@Override
	public EObject getEObject(HierarchicalId id) {
		EObject eObject = getEObjectOrNull(id);
		checkState(eObject != null, "no EObject could be found for ID: %s", id);
		return eObject;
	}
	
	
	private EObject getEObjectOrNull(HierarchicalId id) {
		URI uri = URI.createURI(id.getId());
		
		EObject eObject = getEObjectIfReadonlyUri(uri);
		if (eObject != null) {
			return eObject;
		}
		eObject = getStoredEObject(uri);
		if (eObject != null) {
			return eObject;
		}
		eObject = getAndRegisterNonStoredEObject(uri);
		if (eObject != null) {
			return eObject;
		}
		eObject = mapObjectFromFilteredResourceSet(uri);
		if (eObject != null) {
			return eObject;
		} 
		
		return null;		
	}
	
	
	private EObject mapObjectFromFilteredResourceSet(URI uri) {
		//TODO nbr: Hier müssen evtl. noch die anderen Fälle wie in getEObjectOrNull auch für
		//filterResourceSet durchlaufen werden.
		EObject eObjectInFilterResourceSet = filterResourceSet.getEObject(uri, false);
		EObject eObjectInViewResourceSet = mapCopy2OriginalObject.get(eObjectInFilterResourceSet);
		if (eObjectInViewResourceSet != null) {
			getAndUpdateId(eObjectInViewResourceSet);
		}
		return eObjectInViewResourceSet;
	}

	public EObject getPreFilterObject(EObject eObject) {
		EObject result = mapCopy2OriginalObject.get(eObject);
		return result;
	}
}
