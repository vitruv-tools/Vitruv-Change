package tools.vitruv.change.atomic.resolve

import edu.kit.ipd.sdq.activextendannotations.Utility
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.command.ApplyEChangeSwitch
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.eobject.DeleteEObject
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
import tools.vitruv.change.atomic.uuid.UuidResolver

import static com.google.common.base.Preconditions.checkArgument

/**
 * Utility class for applying and resolving a given EChange.
 */
@Utility
class EChangeUuidResolverAndApplicator {
	static def <C extends EChange> C unresolve(C eChange) {
		val copy = EcoreUtil.copy(eChange)
		EChangeUnresolver.unresolve(copy)
		return copy
	}

	static def EChange resolveBefore(EChange eChange, UuidResolver uuidResolver) {
		return resolveCopy(eChange, uuidResolver)
	}

	static def void applyForward(EChange eChange, UuidResolver uuidResolver) {
		executeUpdatingIds(eChange, uuidResolver, true)
	}

	static def void applyBackward(EChange eChange, UuidResolver uuidResolver) {
		executeUpdatingIds(eChange, uuidResolver, false)
	}

	static def void executeUpdatingIds(EChange eChange, UuidResolver uuidResolver, boolean forward) {
		ApplyEChangeSwitch.applyEChange(eChange, forward)
		if (forward) {
			switch (eChange) {
				CreateEObject<?>:
					uuidResolver.registerEObject(eChange.affectedEObjectID, eChange.affectedEObject)
			}
			switch (eChange) {
				EObjectAddedEChange<?>:
					if (eChange.newValue !== null) {
						uuidResolver.registerEObject(eChange.newValueID, eChange.newValue)
					}
			}
		} else {
			switch (eChange) {
				DeleteEObject<?>:
					uuidResolver.registerEObject(eChange.affectedEObjectID, eChange.affectedEObject)
			}
			switch (eChange) {
				EObjectSubtractedEChange<?>:
					if (eChange.oldValue !== null) {
						uuidResolver.registerEObject(eChange.oldValueID, eChange.oldValue)
					}
			}
		}
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
	def private static EChange resolveCopy(EChange change, UuidResolver uuidResolver) {
		checkArgument(!change.isResolved, "change must not be resolved when trying to resolve")
		var EChange copy = EcoreUtil.copy(change)
		new AtomicEChangeUuidResolver(uuidResolver).resolve(copy)
		return copy
	}
}
