package tools.vitruv.change.composite.description;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import tools.vitruv.change.atomic.id.HierarchicalId;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.resolve.AtomicEChangeIdResolver;
import tools.vitruv.change.atomic.resolve.AtomicEChangeUuidResolver;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.impl.VitruviusChangeIdResolver;
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

	public VitruviusChange<Id> assignIds(VitruviusChange<EObject> change);
	
	public static VitruviusChangeResolver<Uuid> forUuids(UuidResolver uuidResolver) {
		AtomicEChangeUuidResolver resolver = new AtomicEChangeUuidResolver(uuidResolver);
		return new VitruviusChangeUuidResolver(resolver);
	}
	
	public static VitruviusChangeResolver<HierarchicalId> forHierarchicalIds(ResourceSet resourceSet) {
		IdResolver idResolver = IdResolver.create(resourceSet);
		AtomicEChangeIdResolver resolver = new AtomicEChangeIdResolver(idResolver);
		return new VitruviusChangeIdResolver(resolver);
	}
}
