package tools.vitruv.change.atomic

import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.eobject.DeleteEObject
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
import tools.vitruv.change.atomic.eobject.EobjectFactory
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute
import tools.vitruv.change.atomic.feature.reference.InsertEReference
import tools.vitruv.change.atomic.feature.reference.ReferenceFactory
import tools.vitruv.change.atomic.feature.reference.RemoveEReference
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference
import tools.vitruv.change.atomic.root.InsertRootEObject
import tools.vitruv.change.atomic.root.RemoveRootEObject
import tools.vitruv.change.atomic.root.RootEChange
import tools.vitruv.change.atomic.root.RootFactory
import org.eclipse.emf.ecore.resource.Resource
import tools.vitruv.change.atomic.feature.FeatureFactory
import tools.vitruv.change.atomic.feature.UnsetFeature
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.util.EcoreUtil
import static com.google.common.base.Preconditions.checkArgument

/**
 * Factory singleton class for elements of change models.
 * Infers types (i.e. metaclasses and feature types) from parameters where possible.<br/>
 * 
 * Can be used by any transformation that creates change models.
 */
class TypeInferringAtomicEChangeFactory {
	static TypeInferringAtomicEChangeFactory instance

	protected new() {
	}

	/**
	 * Get the singleton instance of the factory.
	 * @return The singleton instance.
	 */
	def static TypeInferringAtomicEChangeFactory getInstance() {
		if (instance === null) {
			instance = new TypeInferringAtomicEChangeFactory()
		}
		return instance
	}

	/** 
	 * Sets the attributes of a RootEChange.
	 * @param change The RootEChange which attributes are to be set.
	 * @param resource The affected resource of the change.
	 * @param uri the URI of the resource. May differ from URI of the resource if it has changed.
	 * @param index The affected index of the resource.
	 */
	def protected setRootChangeFeatures(RootEChange<?> change, Resource resource, URI uri, int index) {
		change.uri = uri?.toString
		change.resource = resource
		change.index = index
	}

	/**
	 * Sets the attributes of a FeatureEChange.
	 * @param change The FeatureEChange which attributes are to be set.
	 * @param affectedEObject The affected EObject of the change.
	 * @param affectedFeature The affected feature of the change.
	 */
	def protected <A, F extends EStructuralFeature> void setFeatureChangeFeatures(
		FeatureEChange<A, F> change, A affectedEObject, F affectedFeature) {
		change.affectedElement = affectedEObject
		change.affectedFeature = affectedFeature
	}

	/** 
	 * Sets the new value of a EObjectAddedEChange.
	 * @param change The EObjectAddedEChange which new value is to be set.
	 * @param newValue The new value of the change.
	 */
	def protected <T> void setNewValue(EObjectAddedEChange<T> change, T newValue) {
		change.newValue = newValue
	}

	/**
	 * Sets the old value of the EObjectSubtractedEChange.
	 * @param change The EObjectSubtractedEChange which old value is to be set.
	 * @param oldValue The old value of the change.
	 */
	def protected <T> void setOldValue(EObjectSubtractedEChange<T> change, T oldValue) {
		change.oldValue = oldValue
	}

	/**
	 * Sets the affected EObject of a EObjectExistenceEChange.
	 * @param change The EObjectExistenceEChange which affected EObject is to be set.
	 * @param affectedEObject The affected EObject.
	 */
	def protected <A extends EObject> void setEObjectExistenceChange(EObjectExistenceEChange<A> change,
		A affectedEObject) {
		change.affectedElement = affectedEObject
		change.affectedEObjectType = change.affectedElement.eClass
		change.idAttributeValue = change.affectedElement.ID
	}

	/**
	 * Creates a new {@link InsertRootEObject} EChange.
	 * @param newValue The new root EObject.
	 * @param resource The resource which the new root object is placed in.
	 * @param index The index of the resource which the new root object is placed in.
	 * @return The created InsertRootEObject EChange.
	 */
	def <T> InsertRootEObject<T> createInsertRootChange(T newValue, Resource resource, int index) {
		val c = RootFactory.eINSTANCE.createInsertRootEObject
		setNewValue(c, newValue)
		setRootChangeFeatures(c, resource, resource.URI, index)
		return c
	}

	/**
	 * Creates a new {@link RemoveRootEObject} EChange.
	 * @param oldValue The root EObject which is removed.
	 * @param resource The resource which the root object is removed from.
	 * @param index The index of the resource which the root object is removed from.
	 * @param oldUri The old URI of the resource. May differ from the current resource URI if it has been changed.
	 * @return The created RemoveRootEObject EChange.
	 */
	def <T> RemoveRootEObject<T> createRemoveRootChange(T oldValue, Resource resource, URI oldUri, int index) {
		val c = RootFactory.eINSTANCE.createRemoveRootEObject
		setOldValue(c, oldValue)
		setRootChangeFeatures(c, resource, oldUri, index)
		return c
	}

	/**
	 * Creates a new {@link RemoveRootEObject} EChange.
	 * @param oldValue The root EObject which is removed.
	 * @param resource The resource which the root object is removed from.
	 * @param index The index of the resource which the root object is removed from.
	 * @return The created RemoveRootEObject EChange.
	 */
	def <T extends EObject> RemoveRootEObject<T> createRemoveRootChange(T oldValue, Resource resource, int index) {
		createRemoveRootChange(oldValue, resource, resource.URI, index)
	}

	/**
	 * Creates a new {@link InsertEAttributeValue} EChange.
	 * @param affectedEObject The affected EObject of the change.
	 * @param affectedAttribute The affected EAttribute of the change.
	 * @param newValue The inserted value.
	 * @param index The index at which the new value is inserted in the attribute.
	 * @return The created InsertEAttributeValue EChange.
	 */
	def <S, T> InsertEAttributeValue<S, T> createInsertAttributeChange(S affectedEObject,
		EAttribute affectedAttribute, int index, T newValue) {
		val c = AttributeFactory.eINSTANCE.createInsertEAttributeValue()
		setFeatureChangeFeatures(c, affectedEObject, affectedAttribute)
		c.newValue = newValue
		c.index = index
		return c
	}

	/** 
	 * Creates a new {@link ReplaceSingleValuedEAttribute} EChange.
	 * @param affectedEObject The affected EObject of the change.
	 * @param affectedAttribute The affected EAttribute of the change.
	 * @param oldValue The replaced value.
	 * @param newValue The new value.
	 * @return The created ReplaceSingleValuedEAttribute EChange.
	 */
	def <S, T> ReplaceSingleValuedEAttribute<S, T> createReplaceSingleAttributeChange(
		S affectedEObject, EAttribute affectedAttribute, T oldValue, T newValue) {
		val c = AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute
		setFeatureChangeFeatures(c, affectedEObject, affectedAttribute)
		c.oldValue = oldValue
		c.newValue = newValue
		return c
	}

	/**
	 * Creates a new {@link RemoveEAttributeValue} EChange.
	 * @param affectedEObject The affected EObject of the change.
	 * @param affectedAttribute The affected EAttribute of the change.
	 * @param oldValue The removed value.
	 * @param index The index at which the old value is removed from.
	 * @return The created RemoveEAttributeValue EChange.
	 */
	def <S, T> RemoveEAttributeValue<S, T> createRemoveAttributeChange(S affectedEObject,
		EAttribute affectedAttribute, int index, T oldValue) {
		val c = AttributeFactory.eINSTANCE.createRemoveEAttributeValue()
		setFeatureChangeFeatures(c, affectedEObject, affectedAttribute)
		c.oldValue = oldValue
		c.index = index
		return c
	}

	/**
	 * Creates a new {@link InsertEReference} EChange.
	 * @param affectedEObject The affected EObject of the change.
	 * @param affectedReference The affected EReference of the change.
	 * @param newValue The inserted value.
	 * @param index The index at which the new value is inserted in the reference.
	 * @return The created InsertEReference EChange.
	 */
	def <T> InsertEReference<T> createInsertReferenceChange(T affectedEObject,
		EReference affectedReference, T newValue, int index) {
		val c = ReferenceFactory.eINSTANCE.createInsertEReference()
		setFeatureChangeFeatures(c, affectedEObject, affectedReference)
		setNewValue(c, newValue)
		c.index = index
		return c
	}

	/**
	 * Creates a new {@link ReplaceSingleValuedEReference} EChange.
	 * @param affectedEObject The affected EObject of the change.
	 * @param affectedReference The affected EReference of the change.
	 * @param oldValue The value which is replaced.
	 * @param newValue The value which replaces the old one.
	 * @return The created ReplaceSingleValuedEReference EChange.
	 */
	def <T> ReplaceSingleValuedEReference<T> createReplaceSingleReferenceChange(
		T affectedEObject, EReference affectedReference, T oldValue, T newValue) {
		val c = ReferenceFactory.eINSTANCE.createReplaceSingleValuedEReference
		setFeatureChangeFeatures(c, affectedEObject, affectedReference)
		setOldValue(c, oldValue)
		setNewValue(c, newValue)
		return c
	}

	/**
	 * Creates a new {@link RemoveEReference} EChange.
	 * @param affectedEObject The affected EObject of the change.
	 * @param affectedReference The affected EReference of the change.
	 * @param oldValue The value which is removed.
	 * @param index The index at which the old value is removed from.
	 * @return The created RemoveEReference EChange.
	 */
	def <T> RemoveEReference<T> createRemoveReferenceChange(T affectedEObject,
		EReference affectedReference, T oldValue, int index) {
		val c = ReferenceFactory.eINSTANCE.createRemoveEReference()
		setFeatureChangeFeatures(c, affectedEObject, affectedReference)
		setOldValue(c, oldValue)
		c.index = index
		return c
	}

	/**
	 * Creates a new {@link CreateEObject} EChange.
	 * @param affectedEObject The created EObject.
	 * @return The created CreateEObject EChange.
	 */
	def <A extends EObject> CreateEObject<A> createCreateEObjectChange(A affectedEObject) {
		checkArgument(affectedEObject !== null, "affected object must not be null")
		val c = EobjectFactory.eINSTANCE.createCreateEObject()
		setEObjectExistenceChange(c, affectedEObject)
		return c
	}

	/**
	 * Creates a new {@link DeleteEObject} EChange.
	 * @param affectedEObject The deleted EObject.
	 * @return The created DeleteEObject EChange.
	 */
	def <A extends EObject> DeleteEObject<A> createDeleteEObjectChange(A affectedEObject) {
		checkArgument(affectedEObject !== null, "affected object must not be null")
		val c = EobjectFactory.eINSTANCE.createDeleteEObject()
		setEObjectExistenceChange(c, affectedEObject)
		return c
	}

	/**
	 * Creates a new {@link UnsetFeature} EChange.
	 * @param affectedEObject The EObject of which the feature was unset.
	 * @param affectedFeature The feature that was unset.
	 * @return The created UnsetFeature EChange.
	 */
	def <A, F extends EStructuralFeature> createUnsetFeatureChange(A affectedEObject, F affectedFeature) {
		checkArgument(affectedEObject !== null, "affected object must not be null")
		val c = FeatureFactory.eINSTANCE.createUnsetFeature
		setFeatureChangeFeatures(c, affectedEObject, affectedFeature)
		return c
	}
	
		/**
	 * Return the value of the ID attribute of the given {@link EObject}, according to
	 * {@link EcoreUtil#getID(EObject) EcoreUtil}.
	 * If the object has no ID attribute or if is marked as <code>derived</code>, 
	 * <code>null</code> will be returned.
	 * 
	 * @see 	EcoreUtil#getID(EObject)
	 * @param 	eObject
	 * 			The object to get the ID attribute value from
	 * @return 	The ID attribute value of the given {@link EObject} or <code>null</code> 
	 * 			if it has no ID attribute or if it is marked as <code>derived</code>.
	 */
	private static def String getID(EObject eObject) {
		val idAttribute = eObject.eClass.EIDAttribute
		if (idAttribute !== null && !idAttribute.derived) {
			return EcoreUtil.getID(eObject)
		}
		return null
	}

}
