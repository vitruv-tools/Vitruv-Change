package tools.vitruv.change.atomic.hid;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.command.internal.ApplyEChangeSwitch;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange;
import tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange;
import tools.vitruv.change.atomic.hid.internal.FilterModelResolverImpl;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdResolver;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdResolverImpl;
import tools.vitruv.change.atomic.resolve.AtomicEChangeResolverHelper;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * A resolver for resolving a change with {@link HierarchicalId} which has been executed on 
 * a filtered model in a specific FilterResourceSet and not the unfilteredResourceSet (the resourceSet of the 
 * unfiltered model). Resolves changes
 * by searching objects in the given FilterResourceSet first and then mapping it to the unfilteredResourceSet.
 * The filterResourceSet must be in the state before the change has been applied.
 * This Resolver must only be used for resolveAndApplyForward, but not for applyForwardAndAssignIds as this might 
 * produce unexpected behaviour. 
 */
public class AtomicEChangeHierarchicalIdFilterResolver {
	
	/**
	 * The resolver which is used to resolve HierarchicalIds in the unfiltered ResourceSet.
	 */
	protected HierarchicalIdResolver idInUnfilteredModelResolver;
	
	/**
	 * The resolver which is used to resolve HierarchicalIds
	 */
	private HierarchicalIdResolver idInFilteredModelResolver;


	public AtomicEChangeHierarchicalIdFilterResolver(ResourceSet filterResourceSet, ResourceSet unfilteredResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		idInUnfilteredModelResolver = new FilterModelResolverImpl(filterResourceSet, unfilteredResourceSet, mapCopy2OriginalObject);
		idInFilteredModelResolver = new HierarchicalIdResolverImpl(filterResourceSet);
	}
	

	/**
	 * Resolves the given change using its {@link HierarchicalIdResolver} and
	 * applies it forward. The associated resource set must be in the state before
	 * the change has been applied.
	 * 
	 * @param unresolvedEChange the change to resolve and apply.
	 * @return Returns the resolved change.
	 */
	public EChange<EObject> resolveAndApplyForward(EChange<HierarchicalId> unresolvedEChange) {
		EChange<EObject> resolvedViewResourceChange = resolve(unresolvedEChange, idInUnfilteredModelResolver);
		EChange<EObject> resolvedFilterResourceChange = resolve(unresolvedEChange, idInFilteredModelResolver);
		applyForward(resolvedViewResourceChange, idInUnfilteredModelResolver);
		applyForward(resolvedFilterResourceChange, idInFilteredModelResolver);
		return resolvedViewResourceChange;
	}
	
	/**
	 * Ends a transactions such that all {@link EObject}s not being contained in a
	 * resource, which is contained in a resource set, are removed from the
	 * hierarchical ID mapping.
	 */
	public void endTransaction() {
		idInUnfilteredModelResolver.endTransaction();
		idInFilteredModelResolver.endTransaction();
	}
	
	
	private void applyForward(EChange<EObject> resolvedChange, HierarchicalIdResolver idResolver) {
		EObject affectedEObject = getAffectedEObject(resolvedChange);
		HierarchicalId affectedId = idResolver.getAndUpdateId(affectedEObject);
		EObject oldObject = getOldContainedEObject(resolvedChange);
		ApplyEChangeSwitch.applyEChange(resolvedChange, true);
		if (isContainmentChange(resolvedChange) || affectedId != idResolver.getAndUpdateId(affectedEObject)) {
			refreshIds(affectedEObject, idResolver);
		}
		if (oldObject != null) {
			refreshIds(oldObject, idResolver);
		}
	}
	
	private EChange<EObject> resolve(EChange<HierarchicalId> unresolvedChange, HierarchicalIdResolver idResolver) {
		return AtomicEChangeResolverHelper.resolveChange(unresolvedChange, id -> {
			if (unresolvedChange instanceof CreateEObject<HierarchicalId> createChange) {
				EObject createdElement = EcoreUtil.create(createChange.getAffectedEObjectType());
				HierarchicalId createdId = idResolver.getAndUpdateId(createdElement);
				checkState(createdId.equals(id),
						"generated ID %s does not match the original ID %s on element creation", createdId, id);
				return createdElement;
			} else {
				return idResolver.getEObject(id);
			}
		}, resource -> idResolver.getResource(resource.getURI()));
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
	
	protected static boolean isContainmentChange(EChange<EObject> eChange) {
		if (eChange instanceof UpdateReferenceEChange<EObject> referenceChange) {
			return referenceChange.isContainment();
		}
		return false;
	}

	protected void refreshIds(EObject eObject, HierarchicalIdResolver idResolver) {
		idResolver.getAndUpdateId(eObject);
		eObject.eContents().forEach(it -> refreshIds(it, idResolver));
	}
}
