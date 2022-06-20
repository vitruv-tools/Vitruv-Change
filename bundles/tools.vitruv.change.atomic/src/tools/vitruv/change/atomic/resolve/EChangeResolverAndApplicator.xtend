package tools.vitruv.change.atomic.resolve

import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.change.atomic.EChange
import static com.google.common.base.Preconditions.checkArgument
import edu.kit.ipd.sdq.activextendannotations.Utility
import tools.vitruv.change.atomic.command.ApplyEChangeSwitch
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.root.InsertRootEObject
import tools.vitruv.change.atomic.root.RemoveRootEObject
import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange
import tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange
import tools.vitruv.change.atomic.id.IdResolver

/**
 * Utility class for applying and resolving a given EChange.
 */
@Utility
class EChangeResolverAndApplicator {
	static def <C extends EChange> C unresolve(C eChange) {
		val copy = EcoreUtil.copy(eChange)
		EChangeUnresolver.unresolve(copy)
		return copy
	}
	
	static def EChange resolveBefore(EChange eChange, IdResolver idResolver) {
		return resolveCopy(eChange, idResolver)
	}

	static def void applyForward(EChange eChange, IdResolver idResolver) {
		executeUpdatingIds(eChange, idResolver, true)
	}

	static def void applyBackward(EChange eChange, IdResolver idResolver) {
		executeUpdatingIds(eChange, idResolver, false)
	}

	static def void applyBackward(EChange eChange) {
		ApplyEChangeSwitch.applyEChange(eChange, false)
	}
	
	static def void executeUpdatingIds(EChange eChange, IdResolver idResolver, boolean forward) {
		val affectedObject = eChange.affectedEObject
		val affectedId = idResolver.getAndUpdateId(affectedObject)
		val oldObject = eChange.oldContainedEObject
		ApplyEChangeSwitch.applyEChange(eChange, forward)
		if (eChange.isContainmentChange || affectedId != idResolver.getAndUpdateId(affectedObject)) {
			affectedObject.updateIds(idResolver)
		}
		if (oldObject !== null) {
			oldObject.updateIds(idResolver)
		}
	}
	
	private static def boolean isContainmentChange(EChange eChange) {
		if (eChange instanceof UpdateReferenceEChange) {
			return eChange.containment
		}
		return false
	}
	
	private static def getAffectedEObject(EChange eChange) {
		switch (eChange) {
			FeatureEChange<?, ?>: eChange.affectedEObject
			EObjectExistenceEChange<?>: eChange.affectedEObject
			InsertRootEObject<?>: eChange.newValue
			RemoveRootEObject<?>: eChange.oldValue
		}
	}
	
	private static def EObject getOldContainedEObject(EChange eChange) {
		switch (eChange) {
			SubtractiveReferenceEChange<?, ?>: if (eChange.affectedFeature.containment) eChange.oldValue
		}
	}
	
	private static def void updateIds(EObject object, IdResolver idResolver) {
		idResolver.getAndUpdateId(object)
		object.eContents.forEach[updateIds(idResolver)]
	}

	/**
	 * Creates a copy of the change and resolves it using the given {@link idResolver}.
	 * 
	 * @param change					The {@link EChange} which shall be resolved.
	 * @param idResolver 				The {@link idResolver} to resolve {@link EObject}s from
	 * @return 							Returns a resolved copy of the change. If the copy could not be resolved 
	 * 									an {@link IllegalStateException} is thrown
	 * @throws IllegalArgumentException The change is already resolved.
	 * @throws IllegalStateException 	The change cannot be resolved.
	 */
	def private static EChange resolveCopy(EChange change, IdResolver idResolver) {
		checkArgument(!change.isResolved, "change must not be resolved when trying to resolve")
		var EChange copy = EcoreUtil.copy(change)
		new AtomicEChangeResolver(idResolver).resolve(copy)
		return copy
	}
	
}
