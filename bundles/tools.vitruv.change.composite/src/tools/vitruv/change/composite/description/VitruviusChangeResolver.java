package tools.vitruv.change.composite.description;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import tools.vitruv.change.atomic.hid.AtomicEChangeHierarchicalIdFilterResolver;
import tools.vitruv.change.atomic.hid.AtomicEChangeHierarchicalIdResolver;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.atomic.uuid.AtomicEChangeUuidResolver;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.impl.VitruviusChangeFilterResolver;
import tools.vitruv.change.composite.description.impl.VitruviusChangeHierarchicalIdResolver;
import tools.vitruv.change.composite.description.impl.VitruviusChangeUuidResolver;

public interface VitruviusChangeResolver<Id> {
	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method. Returns the
	 * resolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or applied.
	 */
	public VitruviusChange<EObject> resolveAndApply(VitruviusChange<Id> change);

	/**
	 * Unresolves the change and assigns Ids to it. It has to be ensured that the
	 * model is in the state after the change has been applied. Returns the
	 * Id-assigned change.
	 */
	public VitruviusChange<Id> assignIds(VitruviusChange<EObject> change);

	/**
	 * Instantiates a new resolver that uses {@link Uuid UUIDs}.
	 * 
	 * @param uuidResolver the {@link UuidResolver} to resolve UUIDs with.
	 */
	public static VitruviusChangeResolver<Uuid> forUuids(UuidResolver uuidResolver) {
		AtomicEChangeUuidResolver resolver = new AtomicEChangeUuidResolver(uuidResolver);
		return new VitruviusChangeUuidResolver(resolver);
	}

	/**
	 * Instantiates a new resolver that uses {@link HierarchicalId HierarchicalIds}.
	 * 
	 * @param resourceSet the {@link ResourceSet} to use with this resolver.
	 */
	public static VitruviusChangeResolver<HierarchicalId> forHierarchicalIds(ResourceSet resourceSet) {
		AtomicEChangeHierarchicalIdResolver resolver = new AtomicEChangeHierarchicalIdResolver(resourceSet);
		return new VitruviusChangeHierarchicalIdResolver(resolver);
	}
	
	
	public static VitruviusChangeResolver<HierarchicalId> forHierarchicalIdsAndFilteredModel(ResourceSet sourceResourceSet,
			ResourceSet filterResourceSet, Map<EObject, EObject> mapCopy2OriginalObject) {	
		AtomicEChangeHierarchicalIdFilterResolver resolver = new AtomicEChangeHierarchicalIdFilterResolver(filterResourceSet, sourceResourceSet, mapCopy2OriginalObject);
		return new VitruviusChangeFilterResolver(resolver);
	}
	
	
}
