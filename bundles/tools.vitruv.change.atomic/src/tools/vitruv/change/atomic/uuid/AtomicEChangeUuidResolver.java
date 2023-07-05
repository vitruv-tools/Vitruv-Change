package tools.vitruv.change.atomic.uuid;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.command.internal.ApplyEChangeSwitch;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.resolve.AtomicEChangeResolverHelper;

public class AtomicEChangeUuidResolver {
	private UuidResolver uuidResolver;

	public AtomicEChangeUuidResolver(UuidResolver uuidResolver) {
		this.uuidResolver = uuidResolver;
	}

	public EChange<EObject> resolveAndApplyForward(EChange<Uuid> unresolvedEChange) {
		EChange<EObject> resolvedEChange = resolve(unresolvedEChange);
		ApplyEChangeSwitch.applyEChange(resolvedEChange, true);
		updateUuidResolver(resolvedEChange, unresolvedEChange);
		return resolvedEChange;
	}

	private EChange<EObject> resolve(EChange<Uuid> unresolvedChange) {
		return AtomicEChangeResolverHelper.resolveChange(unresolvedChange, uuid -> {
			if (unresolvedChange instanceof CreateEObject<Uuid> createChange) {
				return EcoreUtil.create(createChange.getAffectedEObjectType());
			} else {
				return uuidResolver.getEObject(uuid);
			}
		}, this::resourceResolver);
	}
	
	public EChange<Uuid> assignIds(EChange<EObject> resolvedEChange) {
		EChange<Uuid> unresolvedEChange = AtomicEChangeResolverHelper.resolveChange(resolvedEChange, eObject -> {
			if (uuidResolver.hasUuid(eObject)) {
				return uuidResolver.getUuid(eObject);
			}
			else {
				if (resolvedEChange instanceof CreateEObject<EObject> createChange && createChange.getAffectedElement() == eObject) {
					return uuidResolver.registerEObject(eObject);
				}
				else if (resolvedEChange instanceof EObjectAddedEChange<EObject> addedChange && addedChange.getNewValue() == eObject) {
					return uuidResolver.registerEObject(eObject);
				}
				else {
					throw new IllegalStateException("trying to assign UUID for unknown element %s of change %s".formatted(eObject, resolvedEChange));
				}
			}
		}, this::resourceResolver);
		updateUuidResolver(resolvedEChange, unresolvedEChange);
		return unresolvedEChange;
	}

	/**
	 * Ends a transactions such that all {@link EObject}s not being contained in a
	 * resource, which is contained in a resource set, are removed from the UUID
	 * mapping.
	 */
	public void endTransaction() {
		uuidResolver.endTransaction();
	}

	private void updateUuidResolver(EChange<EObject> resolvedChange, EChange<Uuid> unresolvedChange) {
		if (resolvedChange instanceof CreateEObject<EObject> createResolvedChange
				&& unresolvedChange instanceof CreateEObject<Uuid> createUnresolvedChange) {
			registerOrUnregisterEObject(createUnresolvedChange.getAffectedElement(),
					createResolvedChange.getAffectedElement(), true);
		} else if (resolvedChange instanceof DeleteEObject<EObject> deleteResolvedChange
				&& unresolvedChange instanceof DeleteEObject<Uuid> deleteUnresolvedChange) {
			registerOrUnregisterEObject(deleteUnresolvedChange.getAffectedElement(),
					deleteResolvedChange.getAffectedElement(), false);
		}
	}

	private void registerOrUnregisterEObject(Uuid uuid, EObject eObject, boolean register) {
		if (register) {
			uuidResolver.registerEObject(uuid, eObject);
		} else {
			uuidResolver.unregisterEObject(uuid, eObject);
		}
	}
	
	private Resource resourceResolver(Resource sourceResource) {
		return uuidResolver.getResource(sourceResource.getURI());
	}
}
