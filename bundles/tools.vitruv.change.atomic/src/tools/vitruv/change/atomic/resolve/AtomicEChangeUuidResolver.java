package tools.vitruv.change.atomic.resolve;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeUuidManager;
import tools.vitruv.change.atomic.command.ApplyEChangeSwitch;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.resolve.internal.AtomicEChangeResolverHelper;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;

public class AtomicEChangeUuidResolver {
	private UuidResolver uuidResolver;

	public AtomicEChangeUuidResolver(UuidResolver uuidResolver) {
		this.uuidResolver = uuidResolver;
	}

	public EChange<EObject> resolveAndApplyForward(EChange<Uuid> unresolvedEChange) {
		EChange<EObject> resolvedEChange = resolve(unresolvedEChange);
		ApplyEChangeSwitch.applyEChange(resolvedEChange, true);
		updateUuidResolver(resolvedEChange, unresolvedEChange, true);
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
		EChange<Uuid> unresolvedEChange = EChangeUuidManager.setOrGenerateIds(List.of(resolvedEChange), uuidResolver, false).iterator().next();
		return unresolvedEChange;
	}

	private EChange<Uuid> unresolve(EChange<EObject> resolvedChange) {
		return AtomicEChangeResolverHelper.resolveChange(resolvedChange, eObject -> {
			if (resolvedChange instanceof DeleteEObject<EObject> deleteChange) {
				return uuidResolver.generateUuid(EcoreUtil.create(deleteChange.getAffectedEObjectType()));
			}
			return uuidResolver.getUuid(eObject);
		}, this::resourceResolver);
	}

	private void updateUuidResolver(EChange<EObject> resolvedChange, EChange<Uuid> unresolvedChange, boolean forward) {
		if (resolvedChange instanceof CreateEObject<EObject> createResolvedChange
				&& unresolvedChange instanceof CreateEObject<Uuid> createUnresolvedChange) {
			registerOrUnregisterEObject(createUnresolvedChange.getAffectedElement(),
					createResolvedChange.getAffectedElement(), forward);
		} else if (resolvedChange instanceof DeleteEObject<EObject> deleteResolvedChange
				&& unresolvedChange instanceof DeleteEObject<Uuid> deleteUnresolvedChange) {
			registerOrUnregisterEObject(deleteUnresolvedChange.getAffectedElement(),
					deleteResolvedChange.getAffectedElement(), !forward);
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
