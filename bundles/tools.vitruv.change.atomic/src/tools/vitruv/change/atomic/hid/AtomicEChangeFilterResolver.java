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

public class AtomicEChangeFilterResolver extends AtomicEChangeHierarchicalIdResolver {
	
	private HierarchicalIdResolver hierarchicalFilteredModelResolver;


	public AtomicEChangeFilterResolver(ResourceSet filterResourceSet, ResourceSet preFilterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {
		super(new FilterModelResolverImpl(filterResourceSet, preFilterResourceSet, mapCopy2OriginalObject));
		//TODO nbr: Richtiges ResourceSet?
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
		//TODO nbr: Hier muss ich wahrscheinlich eigene Methode schreiben, da ansonsten ids überschrieben werden. für eigenen Change resolver
		applyForward(resolvedFilterResourceChange, hierarchicalFilteredModelResolver);
		
		//TODO nbr: Hier weitermachen: Was muss ich hier zurückgeben?
		return resolvedViewResourceChange;
	}
	
	
	public EChange<HierarchicalId> applyForwardAndAssignIds(EChange<EObject> resolvedEChange) {
		//TODO nbr: Provide better solution
		throw new NullPointerException("Not implemented yet");
	}	
}
