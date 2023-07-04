package tools.vitruv.change.atomic

import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.id.IdResolver

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkState
import tools.vitruv.change.atomic.id.HierarchicalId

/**
 * Provides logic for initializing the IDs within changes. 
 */
class EChangeIdManager {
	val IdResolver idResolver

	/**
	 * Initializes the manager with a {@link IdResolver}.
	 * 
	 * @param idResolver -
	 * 		the {@link IdResolver} to use for ID management
	 */
	new(IdResolver idResolver) {
		checkArgument(idResolver !== null, "id resolver must not be null")
		this.idResolver = idResolver
	}

	def EChange<HierarchicalId> setOrGenerateIds(EChange<EObject> eChange) {
		switch eChange {
			EObjectExistenceEChange<?>:
				setOrGenerateAffectedEObjectId(eChange)
			FeatureEChange<?,?>:
				setOrGenerateAffectedEObjectId(eChange)
		}
		switch eChange {
			EObjectSubtractedEChange<?>:
				setOrGenerateOldValueId(eChange)
		}
		switch eChange {
			EObjectAddedEChange<?>:
				setOrGenerateNewValueId(eChange)
		}
		return null //TODO: - return something
	}

	private def HierarchicalId getId(EObject object) {
		val id = idResolver.getAndUpdateId(object)
		checkState(id !== null, "id must not be null")
		return id
	}

	private def void setOrGenerateNewValueId(EObjectAddedEChange<?> addedEChange) {
		if (addedEChange.newValue === null) {
			return;
		}
//		addedEChange.newValueID = addedEChange.newValue.id
	}

	private def void setOrGenerateOldValueId(EObjectSubtractedEChange<?> subtractedEChange) {
		if (subtractedEChange.oldValue === null) {
			return;
		}
//		subtractedEChange.oldValueID = subtractedEChange.oldValue.id
	}

	private def void setOrGenerateAffectedEObjectId(EObjectExistenceEChange<?> existenceChange) {
//		val affectedEObject = existenceChange.affectedEObject
//		checkArgument(affectedEObject !== null, "existence change must have an affected EObject: %s", existenceChange)
//		existenceChange.affectedEObjectID = affectedEObject.id	
	}

	private def void setOrGenerateAffectedEObjectId(FeatureEChange<?, ?> featureChange) {
//		val affectedEObject = featureChange.affectedEObject
//		checkArgument(affectedEObject !== null, "feature change must have an affected EObject: %s", featureChange)
//		featureChange.affectedEObjectID = affectedEObject.id
	}

}
