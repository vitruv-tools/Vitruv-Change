package tools.vitruv.change.atomic.resolve

import edu.kit.ipd.sdq.activextendannotations.Utility
import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.command.ApplyEChangeSwitch
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange
import tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange
import tools.vitruv.change.atomic.id.HierarchicalId
import tools.vitruv.change.atomic.id.IdResolver
import tools.vitruv.change.atomic.resolve.internal.AtomicEChangeResolverHelper
import tools.vitruv.change.atomic.root.InsertRootEObject
import tools.vitruv.change.atomic.root.RemoveRootEObject

/**
 * Utility class for applying and resolving a given EChange.
 */
@Utility
class EChangeIdResolverAndApplicator {
	static def EChange<EObject> resolveBefore(EChange<HierarchicalId> eChange, IdResolver idResolver) {
		return resolveCopy(eChange, idResolver)
	}

	static def void applyForward(EChange<EObject> eChange, IdResolver idResolver) {
		executeUpdatingIds(eChange, idResolver, true)
	}

	static def void applyBackward(EChange<EObject> eChange, IdResolver idResolver) {
		executeUpdatingIds(eChange, idResolver, false)
	}

	static def void applyBackward(EChange<EObject> eChange) {
		ApplyEChangeSwitch.applyEChange(eChange, false)
	}
	
	static def void executeUpdatingIds(EChange<EObject> eChange, IdResolver idResolver, boolean forward) {
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
	
	private static def boolean isContainmentChange(EChange<?> eChange) {
		if (eChange instanceof UpdateReferenceEChange) {
			return eChange.containment
		}
		return false
	}
	
	private static def EObject getAffectedEObject(EChange<EObject> eChange) {
		switch (eChange) {
			FeatureEChange<EObject, ?>: eChange.affectedElement
			EObjectExistenceEChange<EObject>: eChange.affectedElement
			InsertRootEObject<EObject>: eChange.newValue
			RemoveRootEObject<EObject>: eChange.oldValue
		}
	}
	
	private static def EObject getOldContainedEObject(EChange<EObject> eChange) {
		switch (eChange) {
			SubtractiveReferenceEChange<EObject>: if (eChange.affectedFeature.containment) eChange.oldValue
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
	def private static EChange<EObject> resolveCopy(EChange<HierarchicalId> change, IdResolver idResolver) {
		return AtomicEChangeResolverHelper.resolveChange(change, [ idResolver.getEObject(it) ]) [ idResolver.getResource(it.URI) ]
	}
	
}
