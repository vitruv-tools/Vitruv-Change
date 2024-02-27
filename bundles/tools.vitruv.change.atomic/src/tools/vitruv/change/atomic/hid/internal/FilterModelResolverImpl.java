package tools.vitruv.change.atomic.hid.internal;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

public class FilterModelResolverImpl {
	
	
	private ResourceSet resourceSet;
	
	private ResourceSet preFilterResourceSet;

	private Map<EObject, EObject> mapCopy2OriginalObject;

	public FilterModelResolverImpl(ResourceSet filterResourceSet, ResourceSet preFilterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		this.preFilterResourceSet = preFilterResourceSet;
		this.mapCopy2OriginalObject = mapCopy2OriginalObject;
		checkArgument(filterResourceSet != null, "Resource set may not be null");
		this.resourceSet = filterResourceSet;
		
	}
	
	
	public EObject getPreFilterObject(EObject eObject) {
		EObject result = mapCopy2OriginalObject.get(eObject);
		return result;
	}
	
	
	

}
