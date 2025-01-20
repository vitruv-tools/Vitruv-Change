package tools.vitruv.change.atomic;

import com.google.common.base.Preconditions;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.ReferenceFactory;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootEChange;
import tools.vitruv.change.atomic.root.RootFactory;

/**
 * Factory singleton class for elements of change models.
 * Infers types (i.e. metaclasses and feature types) from parameters where possible.<br/>
 * 
 * Can be used by any transformation that creates change models.
 */
@SuppressWarnings("all")
public class TypeInferringAtomicEChangeFactory {
  private static TypeInferringAtomicEChangeFactory instance;

  protected TypeInferringAtomicEChangeFactory() {
  }

  /**
   * Get the singleton instance of the factory.
   * @return The singleton instance.
   */
  public static TypeInferringAtomicEChangeFactory getInstance() {
    if ((TypeInferringAtomicEChangeFactory.instance == null)) {
      TypeInferringAtomicEChangeFactory _typeInferringAtomicEChangeFactory = new TypeInferringAtomicEChangeFactory();
      TypeInferringAtomicEChangeFactory.instance = _typeInferringAtomicEChangeFactory;
    }
    return TypeInferringAtomicEChangeFactory.instance;
  }

  /**
   * Sets the attributes of a RootEChange.
   * @param change The RootEChange which attributes are to be set.
   * @param resourceURI The affected resource of the change.
   * @param URI the URI of the resource. May differ from URI of the resource if it has changed.
   * @param index The affected index of the resource.
   */
  protected void setRootChangeFeatures(final RootEChange<?> change, final Resource resource, final URI uri, final int index) {
    String _string = null;
    if (uri!=null) {
      _string=uri.toString();
    }
    change.setUri(_string);
    change.setResource(resource);
    change.setIndex(index);
  }

  /**
   * Sets the attributes of a FeatureEChange.
   * @param change The FeatureEChange which attributes are to be set.
   * @param affectedEObject The affected EObject of the change.
   * @param affectedFeature The affected feature of the change.
   */
  protected <A extends Object, F extends EStructuralFeature> void setFeatureChangeFeatures(final FeatureEChange<A, F> change, final A affectedEObject, final F affectedFeature) {
    change.setAffectedElement(affectedEObject);
    change.setAffectedFeature(affectedFeature);
  }

  /**
   * Sets the new value of a EObjectAddedEChange.
   * @param change The EObjectAddedEChange which new value is to be set.
   * @param newValue The new value of the change.
   */
  protected <T extends Object> void setNewValue(final EObjectAddedEChange<T> change, final T newValue) {
    change.setNewValue(newValue);
  }

  /**
   * Sets the old value of the EObjectSubtractedEChange.
   * @param change The EObjectSubtractedEChange which old value is to be set.
   * @param oldValue The old value of the change.
   */
  protected <T extends Object> void setOldValue(final EObjectSubtractedEChange<T> change, final T oldValue) {
    change.setOldValue(oldValue);
  }

  /**
   * Sets the affected EObject of a EObjectExistenceEChange.
   * @param change The EObjectExistenceEChange which affected EObject is to be set.
   * @param affectedEObject The affected EObject.
   */
  protected <A extends EObject> void setEObjectExistenceChange(final EObjectExistenceEChange<A> change, final A affectedEObject) {
    change.setAffectedElement(affectedEObject);
    change.setAffectedEObjectType(change.getAffectedElement().eClass());
    change.setIdAttributeValue(TypeInferringAtomicEChangeFactory.getID(change.getAffectedElement()));
  }

  /**
   * Creates a new {@link InsertRootEObject} EChange.
   * @param newValue The new root EObject.
   * @param resource The resource which the new root object is placed in.
   * @param index The index of the resource which the new root object is placed in.
   * @return The created InsertRootEObject EChange.
   */
  public <T extends Object> InsertRootEObject<T> createInsertRootChange(final T newValue, final Resource resource, final int index) {
    final InsertRootEObject<T> c = RootFactory.eINSTANCE.<T>createInsertRootEObject();
    this.<T>setNewValue(c, newValue);
    this.setRootChangeFeatures(c, resource, resource.getURI(), index);
    return c;
  }

  /**
   * Creates a new {@link RemoveRootEObject} EChange.
   * @param oldValue The root EObject which is removed.
   * @param resource The resource which the root object is removed from.
   * @param index The index of the resource which the root object is removed from.
   * @param oldUri The old URI of the resource. May differ from the current resource URI if it has been changed.
   * @return The created RemoveRootEObject EChange.
   */
  public <T extends Object> RemoveRootEObject<T> createRemoveRootChange(final T oldValue, final Resource resource, final URI oldUri, final int index) {
    final RemoveRootEObject<T> c = RootFactory.eINSTANCE.<T>createRemoveRootEObject();
    this.<T>setOldValue(c, oldValue);
    this.setRootChangeFeatures(c, resource, oldUri, index);
    return c;
  }

  /**
   * Creates a new {@link RemoveRootEObject} EChange.
   * @param oldValue The root EObject which is removed.
   * @param resource The resource which the root object is removed from.
   * @param index The index of the resource which the root object is removed from.
   * @return The created RemoveRootEObject EChange.
   */
  public <T extends EObject> RemoveRootEObject<T> createRemoveRootChange(final T oldValue, final Resource resource, final int index) {
    return this.<T>createRemoveRootChange(oldValue, resource, resource.getURI(), index);
  }

  /**
   * Creates a new {@link InsertEAttribute} EChange.
   * @param affectedEObject The affected EObject of the change.
   * @param affectedAttribute The affected EAttribute of the change.
   * @param newValue The inserted value.
   * @param index The index at which the new value is inserted in the attribute.
   * @return The created InsertEAttributeValue EChange.
   */
  public <S extends Object, T extends Object> InsertEAttributeValue<S, T> createInsertAttributeChange(final S affectedEObject, final EAttribute affectedAttribute, final int index, final T newValue) {
    final InsertEAttributeValue<S, T> c = AttributeFactory.eINSTANCE.<S, T>createInsertEAttributeValue();
    this.<S, EAttribute>setFeatureChangeFeatures(c, affectedEObject, affectedAttribute);
    c.setNewValue(newValue);
    c.setIndex(index);
    return c;
  }

  /**
   * Creates a new {@link ReplaceSingleValuedEAttribute} EChange.
   * @param affectedEObject The affected EObject of the change.
   * @param affectedAttribute The affected EAttribute of the change.
   * @param oldValue The replaced value.
   * @param newValue The new value.
   * @return The created ReplaceSingleValuedEAttribute EChange.
   */
  public <S extends Object, T extends Object> ReplaceSingleValuedEAttribute<S, T> createReplaceSingleAttributeChange(final S affectedEObject, final EAttribute affectedAttribute, final T oldValue, final T newValue) {
    final ReplaceSingleValuedEAttribute<S, T> c = AttributeFactory.eINSTANCE.<S, T>createReplaceSingleValuedEAttribute();
    this.<S, EAttribute>setFeatureChangeFeatures(c, affectedEObject, affectedAttribute);
    c.setOldValue(oldValue);
    c.setNewValue(newValue);
    return c;
  }

  /**
   * Creates a new {@link RemoveEAttributeValue} EChange.
   * @param affectedEObject The affected EObject of the change.
   * @param affectedAttribute The affected EAttribute of the change.
   * @param oldValue The removed value.
   * @param index The index at which the old value is removed from.
   * @return The created RemoveEAttributeValue EChange.
   */
  public <S extends Object, T extends Object> RemoveEAttributeValue<S, T> createRemoveAttributeChange(final S affectedEObject, final EAttribute affectedAttribute, final int index, final T oldValue) {
    final RemoveEAttributeValue<S, T> c = AttributeFactory.eINSTANCE.<S, T>createRemoveEAttributeValue();
    this.<S, EAttribute>setFeatureChangeFeatures(c, affectedEObject, affectedAttribute);
    c.setOldValue(oldValue);
    c.setIndex(index);
    return c;
  }

  /**
   * Creates a new {@link InsertEReference} EChange.
   * @param affectedEObject The affected EObject of the change.
   * @param affectedReference The affected EReference of the change.
   * @param newValue The inserted value.
   * @param index The index at which the new value is inserted in the reference.
   * @return The created InsertEReference EChange.
   */
  public <T extends Object> InsertEReference<T> createInsertReferenceChange(final T affectedEObject, final EReference affectedReference, final T newValue, final int index) {
    final InsertEReference<T> c = ReferenceFactory.eINSTANCE.<T>createInsertEReference();
    this.<T, EReference>setFeatureChangeFeatures(c, affectedEObject, affectedReference);
    this.<T>setNewValue(c, newValue);
    c.setIndex(index);
    return c;
  }

  /**
   * Creates a new {@link ReplaceSingleValuedEReference} EChange.
   * @param affectedEObject The affected EObject of the change.
   * @param affectedReference The affected EReference of the change.
   * @param oldValue The value which is replaced.
   * @param newValue The value which replaces the old one.
   * @return The created ReplaceSingleValuedEReference EChange.
   */
  public <T extends Object> ReplaceSingleValuedEReference<T> createReplaceSingleReferenceChange(final T affectedEObject, final EReference affectedReference, final T oldValue, final T newValue) {
    final ReplaceSingleValuedEReference<T> c = ReferenceFactory.eINSTANCE.<T>createReplaceSingleValuedEReference();
    this.<T, EReference>setFeatureChangeFeatures(c, affectedEObject, affectedReference);
    this.<T>setOldValue(c, oldValue);
    this.<T>setNewValue(c, newValue);
    return c;
  }

  /**
   * Creates a new {@link RemoveEReference} EChange.
   * @param affectedEObject The affected EObject of the change.
   * @param affectedReference The affected EReference of the change.
   * @param oldValue The value which is removed.
   * @param index The index at which the old value is removed from.
   * @return The created RemoveEReference EChange.
   */
  public <T extends Object> RemoveEReference<T> createRemoveReferenceChange(final T affectedEObject, final EReference affectedReference, final T oldValue, final int index) {
    final RemoveEReference<T> c = ReferenceFactory.eINSTANCE.<T>createRemoveEReference();
    this.<T, EReference>setFeatureChangeFeatures(c, affectedEObject, affectedReference);
    this.<T>setOldValue(c, oldValue);
    c.setIndex(index);
    return c;
  }

  /**
   * Creates a new {@link CreateEObject} EChange.
   * @param affectedEObject The created EObject.
   * @return The created CreateEObject EChange.
   */
  public <A extends EObject> CreateEObject<A> createCreateEObjectChange(final A affectedEObject) {
    Preconditions.checkArgument((affectedEObject != null), "affected object must not be null");
    final CreateEObject<A> c = EobjectFactory.eINSTANCE.<A>createCreateEObject();
    this.<A>setEObjectExistenceChange(c, affectedEObject);
    return c;
  }

  /**
   * Creates a new {@link DeleteEObject} EChange.
   * @param affectedEObject The deleted EObject.
   * @return The created DeleteEObject EChange.
   */
  public <A extends EObject> DeleteEObject<A> createDeleteEObjectChange(final A affectedEObject) {
    Preconditions.checkArgument((affectedEObject != null), "affected object must not be null");
    final DeleteEObject<A> c = EobjectFactory.eINSTANCE.<A>createDeleteEObject();
    this.<A>setEObjectExistenceChange(c, affectedEObject);
    return c;
  }

  /**
   * Creates a new {@link UnsetFeature} EChange.
   * @param affectedEObject The EObject of which the feature was unset.
   * @param affectedFeature The feature that was unset.
   * @return The created UnsetFeature EChange.
   */
  public <A extends Object, F extends EStructuralFeature> UnsetFeature<A, F> createUnsetFeatureChange(final A affectedEObject, final F affectedFeature) {
    Preconditions.checkArgument((affectedEObject != null), "affected object must not be null");
    final UnsetFeature<A, F> c = FeatureFactory.eINSTANCE.<A, F>createUnsetFeature();
    this.<A, F>setFeatureChangeFeatures(c, affectedEObject, affectedFeature);
    return c;
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
  private static String getID(final EObject eObject) {
    final EAttribute idAttribute = eObject.eClass().getEIDAttribute();
    if (((idAttribute != null) && (!idAttribute.isDerived()))) {
      return EcoreUtil.getID(eObject);
    }
    return null;
  }
}
