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
import tools.vitruv.change.atomic.hid.internal.FilterModelResolverImpl;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdResolver;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdResolverImpl;
import tools.vitruv.change.atomic.resolve.AtomicEChangeResolverHelper;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * A resolver for resolving a change with {@link HierarchicalId} which has been executed on 
 * a filtered model in a specific FilterResourceSet and not the preFilterResourceSet (the resourceSet of the 
 * unfiltered model). Resolves changes
 * by searching objects in the given preFilterResourceSet first and if a change refers to an object which is 
 * not included in the preFilterResourceSet, it searches the HierarchicalId in the filterResourceSet and uses
 * mapCopy2OriginalObject to map the object in the filterResourceSet to the corresponding object in the 
 * preFilterResourceSet.
 * The filterResourceSet must be in the state before the change has been applied.
 * This Resolver must only be used for resolveAndApplyForward, but not for applyForwardAndAssignIds as it 
 * might behave unexpected.
 */
public class AtomicEChangeFilterResolver extends AtomicEChangeHierarchicalIdResolver {
	
	/**
	 * The resolver which is used to resolve HierarchicalIds
	 */
	private HierarchicalIdResolver hierarchicalFilteredModelResolver;


	public AtomicEChangeFilterResolver(ResourceSet filterResourceSet, ResourceSet preFilterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		super(new FilterModelResolverImpl(filterResourceSet, preFilterResourceSet, mapCopy2OriginalObject));
		hierarchicalFilteredModelResolver = new HierarchicalIdResolverImpl(filterResourceSet);
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
		EChange<EObject> resolvedViewResourceChange = resolve(unresolvedEChange, idResolver);
		EChange<EObject> resolvedFilterResourceChange = resolve(unresolvedEChange, hierarchicalFilteredModelResolver);
		applyForward(resolvedViewResourceChange, idResolver);
		applyForward(resolvedFilterResourceChange, hierarchicalFilteredModelResolver);
		return resolvedViewResourceChange;
	}
	
	/**
	 * 
	 */
	public EChange<HierarchicalId> applyForwardAndAssignIds(EChange<EObject> resolvedEChange) {
		//TODO nbr: Check whether this works
		return super.applyForwardAndAssignIds(resolvedEChange);
	}	
}
