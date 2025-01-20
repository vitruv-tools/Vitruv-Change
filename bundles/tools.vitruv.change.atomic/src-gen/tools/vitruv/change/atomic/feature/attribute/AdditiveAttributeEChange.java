/**
 */
package tools.vitruv.change.atomic.feature.attribute;

import tools.vitruv.change.atomic.AdditiveEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Additive Attribute EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which inserts a new value into an attribute.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#getNewValue <em>New Value</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#isWasUnset <em>Was Unset</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage#getAdditiveAttributeEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface AdditiveAttributeEChange<Element extends Object, Value extends Object> extends UpdateAttributeEChange<Element>, AdditiveEChange<Element, Value>
{
	/**
	 * Returns the value of the '<em><b>New Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * The value which will be inserted.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>New Value</em>' attribute.
	 * @see #setNewValue(Object)
	 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage#getAdditiveAttributeEChange_NewValue()
	 * @model unique="false" required="true"
	 * @generated
	 */
	Value getNewValue();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#getNewValue <em>New Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>New Value</em>' attribute.
	 * @see #getNewValue()
	 * @generated
	 */
	void setNewValue(Value value);

	/**
	 * Returns the value of the '<em><b>Was Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Was Unset</em>' attribute.
	 * @see #setWasUnset(boolean)
	 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage#getAdditiveAttributeEChange_WasUnset()
	 * @model
	 * @generated
	 */
	boolean isWasUnset();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#isWasUnset <em>Was Unset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Was Unset</em>' attribute.
	 * @see #isWasUnset()
	 * @generated
	 */
	void setWasUnset(boolean value);

} // AdditiveAttributeEChange
