package tools.vitruv.change.atomic

import tools.vitruv.change.atomic.eobject.EObjectAddedEChange
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.feature.FeatureEChange
import org.eclipse.emf.ecore.EObject
import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkState
import tools.vitruv.change.atomic.id.IdResolver
import tools.vitruv.change.atomic.uuid.UuidResolver
import tools.vitruv.change.atomic.eobject.DeleteEObject
import tools.vitruv.change.atomic.eobject.CreateEObject

/**
 * Provides logic for initializing the IDs within changes. 
 */
class EChangeIdManager {
	val UuidResolver uuidResolver

	/**
	 * Initializes the manager with a {@link IdResolver}.
	 * 
	 * @param idResolver -
	 * 		the {@link IdResolver} to use for ID management
	 */
	new(UuidResolver uuidResolver) {
		checkArgument(uuidResolver !== null, "id resolver must not be null")
		this.uuidResolver = uuidResolver
	}

	def void setOrGenerateIds(EChange eChange) {
		switch eChange {
			CreateEObject<?>:
				setOrGenerateCreatedEObjectId(eChange)
			EObjectExistenceEChange<?>:
				setAffectedEObjectId(eChange)
			FeatureEChange<?,?>:
				setAffectedEObjectId(eChange)
		}
		switch eChange {
			EObjectSubtractedEChange<?>:
				setOldValueId(eChange)
		}
		switch eChange {
			EObjectAddedEChange<?>:
				setOrGenerateNewValueId(eChange)
		}
	}

	private def String getId(EObject object) {
		val id = uuidResolver.getUuid(object)
		checkState(id !== null, "id must not be null")
		return id
	}
	
	private def String getOrGenerateId(EObject object) {
		try {
			getId(object)
		}
		catch (IllegalStateException e) {
			uuidResolver.registerEObject(object)
		}
	}

	private def void setOrGenerateNewValueId(EObjectAddedEChange<?> addedEChange) {
		if (addedEChange.newValue === null) {
			return;
		}
		addedEChange.newValueID = addedEChange.newValue.getOrGenerateId
	}

	private def void setOldValueId(EObjectSubtractedEChange<?> subtractedEChange) {
		if (subtractedEChange.oldValue === null) {
			return;
		}
		subtractedEChange.oldValueID = subtractedEChange.oldValue.id
	}

	private def void setAffectedEObjectId(EObjectExistenceEChange<?> existenceChange) {
		val affectedEObject = existenceChange.affectedEObject
		checkArgument(affectedEObject !== null, "existence change must have an affected EObject: %s", existenceChange)
		existenceChange.affectedEObjectID = affectedEObject.id
	}
	
	private def void setOrGenerateCreatedEObjectId(CreateEObject<?> existenceChange) {
		val affectedEObject = existenceChange.affectedEObject
		checkArgument(affectedEObject !== null, "existence change must have an affected EObject: %s", existenceChange)
		existenceChange.affectedEObjectID = affectedEObject.getOrGenerateId
	}

	private def void setAffectedEObjectId(FeatureEChange<?, ?> featureChange) {
		val affectedEObject = featureChange.affectedEObject
		checkArgument(affectedEObject !== null, "feature change must have an affected EObject: %s", featureChange)
		featureChange.affectedEObjectID = affectedEObject.id
	}

}
