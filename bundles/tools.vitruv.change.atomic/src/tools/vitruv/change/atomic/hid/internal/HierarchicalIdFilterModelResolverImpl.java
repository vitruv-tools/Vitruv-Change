package tools.vitruv.change.atomic.hid.internal;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import tools.vitruv.change.atomic.hid.HierarchicalId;

public class HierarchicalIdFilterModelResolverImpl extends HierarchicalIdResolverImpl {
	
	private Map<EObject, EObject> mapCopy2Original;
	private ResourceSet filterResourceSet;
	
	
	

	public HierarchicalIdFilterModelResolverImpl(ResourceSet sourceResourceSet, ResourceSet filterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		super(sourceResourceSet);
		this.filterResourceSet = filterResourceSet;
		this.mapCopy2Original = mapCopy2OriginalObject;
	}
	
	
	@Override
	public HierarchicalId getAndUpdateId(EObject eObject) {
		//TODO nbr muss ich das hier auch noch implementieren? Für createObject Operation?
		return super.getAndUpdateId(eObject);
	}
	
	
	@Override
	public EObject getEObject(HierarchicalId id) {
		URI uri = URI.createURI(id.getId());
		EObject eObject = filterResourceSet.getEObject(uri, false);
		EObject originalObject = mapCopy2Original.get(eObject);
		
		return eObject;
	}

}
