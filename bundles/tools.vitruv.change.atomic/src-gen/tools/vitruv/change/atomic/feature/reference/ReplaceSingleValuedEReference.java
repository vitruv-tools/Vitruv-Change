/**
 */
package tools.vitruv.change.atomic.feature.reference;

import org.eclipse.emf.ecore.EReference;

import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Replace Single Valued EReference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * EChange which replaces a single valued reference with a new EObject.
 * If the reference is a containment reference, the new object will be taken from the staging
 * area and the old one will be placed in it.
 * The new object must be placed in the staging area by a {@link CreateEObject} EChange or by removing
 * it from another reference.
 * The old one can be deleted by a {@link DeleteEObject} EChange or resinserted by another change.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.reference.ReferencePackage#getReplaceSingleValuedEReference()
 * @model ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface ReplaceSingleValuedEReference<Element extends Object> extends ReplaceSingleValuedFeatureEChange<Element, EReference, Element>, AdditiveReferenceEChange<Element>, SubtractiveReferenceEChange<Element>
{
} // ReplaceSingleValuedEReference
