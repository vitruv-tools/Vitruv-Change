package tools.vitruv.change.atomic.hid;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdFilterModelResolverImpl;
import tools.vitruv.change.atomic.resolve.AtomicEChangeResolverHelper;

public class AtomicEChangeHierarchicalIdFilteredModelResolver extends AtomicEChangeHierarchicalIdResolver {

	public AtomicEChangeHierarchicalIdFilteredModelResolver(ResourceSet sourceResourceSet, ResourceSet filterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		super(new HierarchicalIdFilterModelResolverImpl(sourceResourceSet, filterResourceSet, mapCopy2OriginalObject));
	}
	
	
//	protected EChange<EObject> resolve(EChange<HierarchicalId> unresolvedChange) {
//		return AtomicEChangeResolverHelper.resolveChange(unresolvedChange, id -> {
//			if (unresolvedChange instanceof CreateEObject<HierarchicalId> createChange) {
//				EObject createdElement = EcoreUtil.create(createChange.getAffectedEObjectType());
//				HierarchicalId createdId = idResolver.getAndUpdateId(createdElement);
//				checkState(createdId.equals(id),
//						"generated ID %s does not match the original ID %s on element creation", createdId, id);
//				return createdElement;
//			} else {
//				return idResolver.getEObject(id);
//			}
//		}, resource -> idResolver.getResource(resource.getURI()));
//	}

}
