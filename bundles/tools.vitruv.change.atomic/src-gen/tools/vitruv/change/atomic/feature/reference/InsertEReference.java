/**
 */
package tools.vitruv.change.atomic.feature.reference;

import org.eclipse.emf.ecore.EReference;

import tools.vitruv.change.atomic.feature.list.InsertInListEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Insert EReference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * EChange which inserts an EObject into a many valued reference.
 * If the reference is a containment reference, the inserted object will be taken from the staging area.
 * There it must be placed by a {@link CreateEObject} EChange or by removing it from another reference.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.reference.ReferencePackage#getInsertEReference()
 * @model ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface InsertEReference<Element extends Object> extends InsertInListEChange<Element, EReference, Element>, AdditiveReferenceEChange<Element>
{
} // InsertEReference
