/**
 */
package tools.vitruv.change.atomic.eobject;

import tools.vitruv.change.atomic.SubtractiveEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EObject Subtracted EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which removes an EObject from a resource or reference.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange#getOldValue <em>Old Value</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectSubtractedEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface EObjectSubtractedEChange<Element extends Object> extends SubtractiveEChange<Element, Element>
{
	/**
	 * Returns the value of the '<em><b>Old Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Old Value</em>' reference.
	 * @see #setOldValue(Object)
	 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectSubtractedEChange_OldValue()
	 * @model kind="reference" resolveProxies="false" required="true"
	 * @generated
	 */
	Element getOldValue();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange#getOldValue <em>Old Value</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Old Value</em>' reference.
	 * @see #getOldValue()
	 * @generated
	 */
	void setOldValue(Element value);

} // EObjectSubtractedEChange
