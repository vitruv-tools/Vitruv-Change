package tools.vitruv.change.atomic.resolve

import edu.kit.ipd.sdq.activextendannotations.Utility
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.command.ApplyEChangeSwitch
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.eobject.DeleteEObject
import tools.vitruv.change.atomic.uuid.Uuid
import tools.vitruv.change.atomic.uuid.UuidResolver

/**
 * Utility class for applying and resolving a given EChange.
 */
@Utility
class EChangeUuidResolverAndApplicator {		
	static def EChange<EObject> resolveAndApplyForward(EChange<Uuid> eChange, UuidResolver uuidResolver) {
		val resolvedChange = resolve(eChange, uuidResolver)
		ApplyEChangeSwitch.applyEChange(resolvedChange, true)
		switch eChange {
			CreateEObject<Uuid>: {
				val resolvedCreateChange = resolvedChange as CreateEObject<EObject>
				uuidResolver.registerEObject(eChange.affectedElement, resolvedCreateChange.affectedElement)
			}
		}
		return resolvedChange
	}
	
	static def EChange<Uuid> unresolveAndApplyBackward(EChange<EObject> eChange, UuidResolver uuidResolver) {
		val unresolvedChange = unresolve(eChange, uuidResolver)
		ApplyEChangeSwitch.applyEChange(eChange, false)
		switch eChange {
			DeleteEObject<EObject>: {
				val unresolvedDeleteChange = unresolvedChange as DeleteEObject<Uuid>
				uuidResolver.registerEObject(unresolvedDeleteChange.affectedElement, eChange.affectedElement)
			}
		}
		return unresolvedChange
	}

	/**
	 * Creates a copy of the change and resolves it using the given {@link UuidResolver}.
	 * 
	 * @param change					The {@link EChange} which shall be resolved.
	 * @param uuidResolver 				The {@link UuidResolver} to resolve {@link EObject}s from
	 * @return 							Returns a resolved copy of the change. If the copy could not be resolved 
	 * 									an {@link IllegalStateException} is thrown
	 * @throws IllegalArgumentException The change is already resolved.
	 * @throws IllegalStateException 	The change cannot be resolved.
	 */
	private static def EChange<EObject> resolve(EChange<Uuid> change, UuidResolver uuidResolver) {
		AtomicEChangeResolver.resolveChange(change) [ 
			switch change {
				CreateEObject<Uuid>: {
					return EcoreUtil.create(change.affectedEObjectType)
				}
				default:
					return uuidResolver.getEObject(it)
			}
		]
	}
	
	private static def EChange<Uuid> unresolve(EChange<EObject> change, UuidResolver uuidResolver) {
		AtomicEChangeResolver.resolveChange(change) [ uuidResolver.getUuid(it) ]
	}
}
