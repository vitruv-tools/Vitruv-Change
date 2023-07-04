package tools.vitruv.change.atomic.resolve.internal;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;

/**
 * Utility class to allow unresolving changes that were created using the atomic
 * change factories. Since changes created by the factory are not yet applied,
 * the {@link EChangeUuidResolverAndApplicator#unresolveAndApplyBackward} method
 * cannot be used.
 */
public class AtomicEChangeUnresolver {
	private UuidResolver uuidResolver;
	private ResourceSet uuidResolverResourceSet;

	public AtomicEChangeUnresolver(UuidResolver uuidResolver, ResourceSet uuidResolverResourceSet) {
		this.uuidResolver = uuidResolver;
		this.uuidResolverResourceSet = uuidResolverResourceSet;
	}

	public EChange<Uuid> unresolve(EChange<? extends EObject> eChange) {
		UuidResolver temporaryUuidResolver = UuidResolver.create(uuidResolverResourceSet);
		return unresolve(eChange, temporaryUuidResolver);
	}

	public List<EChange<Uuid>> unresolve(List<? extends EChange<? extends EObject>> eChanges) {
		UuidResolver temporaryUuidResolver = UuidResolver.create(uuidResolverResourceSet);
		return eChanges.stream().map(eChange -> unresolve(eChange, temporaryUuidResolver)).toList();
	}

	private EChange<Uuid> unresolve(EChange<? extends EObject> eChange, UuidResolver temporaryUuidResolver) {
		return AtomicEChangeResolverHelper.resolveChange(eChange, eObject -> {
			if (uuidResolver.hasUuid(eObject)) {
				return uuidResolver.getUuid(eObject);
			} else if (temporaryUuidResolver.hasUuid(eObject)) {
				return temporaryUuidResolver.getUuid(eObject);
			} else {
				return temporaryUuidResolver.registerEObject(eObject);
			}
		}, resource -> uuidResolver.getResource(resource.getURI()));
	}
}
