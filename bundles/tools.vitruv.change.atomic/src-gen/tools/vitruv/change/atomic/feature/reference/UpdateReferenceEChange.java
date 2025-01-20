/**
 */
package tools.vitruv.change.atomic.feature.reference;

import org.eclipse.emf.ecore.EReference;

import tools.vitruv.change.atomic.feature.FeatureEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Update Reference EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which updates a reference with a new value.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.reference.ReferencePackage#getUpdateReferenceEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface UpdateReferenceEChange<Element extends Object> extends FeatureEChange<Element, EReference>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * The affected reference is a containment reference.
	 * <!-- end-model-doc -->
	 * @model kind="operation" unique="false" required="true"
	 * @generated
	 */
	boolean isContainment();

} // UpdateReferenceEChange
