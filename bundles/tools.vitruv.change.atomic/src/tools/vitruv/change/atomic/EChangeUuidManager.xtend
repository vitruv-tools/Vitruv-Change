package tools.vitruv.change.atomic

import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.resolve.AtomicEChangeCopier
import tools.vitruv.change.atomic.uuid.Uuid
import tools.vitruv.change.atomic.uuid.UuidResolver

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkState

import static extension edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil.mapFixed

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
	
	def static Iterable<EChange<Uuid>> setOrGenerateIds(Iterable<EChange<EObject>> eChanges, UuidResolver uuidResolver) {
		setOrGenerateIds(eChanges, uuidResolver, true)
	}
	
	def static Iterable<EChange<Uuid>> setOrGenerateIds(Iterable<EChange<EObject>> eChanges, UuidResolver uuidResolver, boolean endTransaction) {
		val manager = new EChangeUuidManager(uuidResolver)
		val idAssignedChanges = eChanges.mapFixed [ eChange |
			manager.setOrGenerateIds(eChange)
		]
		if (endTransaction) {
			uuidResolver.endTransaction
		}
		return idAssignedChanges
	}

	def EChange<Uuid> setOrGenerateIds(EChange<EObject> eChange) {
		val result = AtomicEChangeCopier.copy(eChange)
		switch eChange {
			CreateEObject<EObject>:
				setOrGenerateCreatedEObjectUuid(eChange, result)
			EObjectExistenceEChange<EObject>:
				setAffectedEObjectUuid(eChange, result)
			FeatureEChange<EObject, ?>:
				setAffectedEObjectUuid(eChange, result)
		}
		switch eChange {
			EObjectSubtractedEChange<EObject>:
				setOldValueUuid(eChange, result)
		}
		switch eChange {
			EObjectAddedEChange<EObject>:
				setOrGenerateNewValueUuid(eChange, result)
		}
		return result
	}

	private def Uuid getUuid(EObject object) {
		val uuid = uuidResolver.getUuid(object)
		checkState(uuid !== null, "uuid must not be null")
		return uuid
	}
	
	private def Uuid getOrGenerateUuid(EObject object) {
		if (uuidResolver.hasUuid(object)) {
			return getUuid(object)
		}
		uuidResolver.registerEObject(object)
	}

	private def void setOrGenerateNewValueUuid(EObjectAddedEChange<EObject> addedEChange, EChange<Uuid> result) {
		if (addedEChange.newValue === null) {
			return;
		}
		(result as EObjectAddedEChange<Uuid>).newValue = addedEChange.newValue.getOrGenerateUuid
	}

	private def void setOldValueUuid(EObjectSubtractedEChange<EObject> subtractedEChange, EChange<Uuid> result) {
		if (subtractedEChange.oldValue === null) {
			return;
		}
		(result as EObjectSubtractedEChange<Uuid>).oldValue = subtractedEChange.oldValue.uuid
	}

	private def void setAffectedEObjectUuid(EObjectExistenceEChange<EObject> existenceChange, EChange<Uuid> result) {
		val affectedEObject = existenceChange.affectedElement
		checkArgument(affectedEObject !== null, "existence change must have an affected EObject: %s", existenceChange)
		(result as EObjectExistenceEChange<Uuid>).affectedElement = affectedEObject.uuid
	}
	
	private def void setOrGenerateCreatedEObjectUuid(CreateEObject<EObject> existenceChange, EChange<Uuid> result) {
		val affectedEObject = existenceChange.affectedElement
		checkArgument(affectedEObject !== null, "existence change must have an affected EObject: %s", existenceChange)
		(result as CreateEObject<Uuid>).affectedElement = affectedEObject.getOrGenerateUuid
	}

	private def void setAffectedEObjectUuid(FeatureEChange<EObject, ?> featureChange, EChange<Uuid> result) {
		val affectedEObject = featureChange.affectedElement
		checkArgument(affectedEObject !== null, "feature change must have an affected EObject: %s", featureChange)
		(result as FeatureEChange<Uuid, ?>).affectedElement = affectedEObject.uuid
	}

}
