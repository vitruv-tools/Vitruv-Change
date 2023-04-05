package tools.vitruv.change.atomic

import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.uuid.UuidResolver

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkState

/**
 * Provides logic for initializing the UUIDs within changes. 
 */
class EChangeUuidManager {
	val UuidResolver uuidResolver

	/**
	 * Initializes the manager with a {@link UuidResolver}.
	 * 
	 * @param uuidResolver -
	 * 		the {@link UuidResolver} to use for UUID management
	 */
	new(UuidResolver uuidResolver) {
		checkArgument(uuidResolver !== null, "uuid resolver must not be null")
		this.uuidResolver = uuidResolver
	}
	
	def static void setOrGenerateIds(Iterable<EChange> eChanges, UuidResolver uuidResolver) {
		setOrGenerateIds(eChanges, uuidResolver, true)
	}
	
	def static void setOrGenerateIds(Iterable<EChange> eChanges, UuidResolver uuidResolver, boolean endTransaction) {
		val manager = new EChangeUuidManager(uuidResolver)
		eChanges.forEach [ eChange |
			manager.setOrGenerateIds(eChange)
		]
		if (endTransaction) {
			uuidResolver.endTransaction
		}
	}

	def void setOrGenerateIds(EChange eChange) {
		switch eChange {
			CreateEObject<?>:
				setOrGenerateCreatedEObjectUuid(eChange)
			EObjectExistenceEChange<?>:
				setAffectedEObjectUuid(eChange)
			FeatureEChange<?,?>:
				setAffectedEObjectUuid(eChange)
		}
		switch eChange {
			EObjectSubtractedEChange<?>:
				setOldValueUuid(eChange)
		}
		switch eChange {
			EObjectAddedEChange<?>:
				setOrGenerateNewValueUuid(eChange)
		}
	}

	private def String getUuid(EObject object) {
		val id = uuidResolver.getUuid(object)
		checkState(id !== null, "uuid must not be null")
		return id
	}
	
	private def String getOrGenerateUuid(EObject object) {
		if (uuidResolver.hasUuid(object)) {
			return getUuid(object)
		}
		uuidResolver.registerEObject(object)
	}

	private def void setOrGenerateNewValueUuid(EObjectAddedEChange<?> addedEChange) {
		if (addedEChange.newValue === null) {
			return;
		}
		addedEChange.newValueID = addedEChange.newValue.getOrGenerateUuid
	}

	private def void setOldValueUuid(EObjectSubtractedEChange<?> subtractedEChange) {
		if (subtractedEChange.oldValue === null) {
			return;
		}
		subtractedEChange.oldValueID = subtractedEChange.oldValue.uuid
	}

	private def void setAffectedEObjectUuid(EObjectExistenceEChange<?> existenceChange) {
		val affectedEObject = existenceChange.affectedEObject
		checkArgument(affectedEObject !== null, "existence change must have an affected EObject: %s", existenceChange)
		existenceChange.affectedEObjectID = affectedEObject.uuid
	}
	
	private def void setOrGenerateCreatedEObjectUuid(CreateEObject<?> existenceChange) {
		val affectedEObject = existenceChange.affectedEObject
		checkArgument(affectedEObject !== null, "existence change must have an affected EObject: %s", existenceChange)
		existenceChange.affectedEObjectID = affectedEObject.getOrGenerateUuid
	}

	private def void setAffectedEObjectUuid(FeatureEChange<?, ?> featureChange) {
		val affectedEObject = featureChange.affectedEObject
		checkArgument(affectedEObject !== null, "feature change must have an affected EObject: %s", featureChange)
		featureChange.affectedEObjectID = affectedEObject.uuid
	}

}
