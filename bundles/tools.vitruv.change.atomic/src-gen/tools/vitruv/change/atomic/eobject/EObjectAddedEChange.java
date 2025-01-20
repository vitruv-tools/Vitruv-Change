/**
 */
package tools.vitruv.change.atomic.eobject;

import tools.vitruv.change.atomic.AdditiveEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EObject Added EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which inserts an EObject into a resource or reference.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.eobject.EObjectAddedEChange#getNewValue <em>New Value</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectAddedEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface EObjectAddedEChange<Element extends Object> extends AdditiveEChange<Element, Element>
{
	/**
	 * Returns the value of the '<em><b>New Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>New Value</em>' reference.
	 * @see #setNewValue(Object)
	 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectAddedEChange_NewValue()
	 * @model kind="reference" resolveProxies="false" required="true"
	 * @generated
	 */
	Element getNewValue();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.eobject.EObjectAddedEChange#getNewValue <em>New Value</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>New Value</em>' reference.
	 * @see #getNewValue()
	 * @generated
	 */
	void setNewValue(Element value);

} // EObjectAddedEChange
