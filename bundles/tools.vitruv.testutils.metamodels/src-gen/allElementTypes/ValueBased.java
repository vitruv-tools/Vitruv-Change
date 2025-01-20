/**
 */
package allElementTypes;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Value Based</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link allElementTypes.ValueBased#getValue <em>Value</em>}</li>
 *   <li>{@link allElementTypes.ValueBased#getChildren <em>Children</em>}</li>
 *   <li>{@link allElementTypes.ValueBased#getReferenced <em>Referenced</em>}</li>
 * </ul>
 *
 * @see allElementTypes.AllElementTypesPackage#getValueBased()
 * @model
 * @generated
 */
public interface ValueBased extends Containable
{
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see allElementTypes.AllElementTypesPackage#getValueBased_Value()
	 * @model
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link allElementTypes.ValueBased#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

	/**
	 * Returns the value of the '<em><b>Children</b></em>' containment reference list.
	 * The list contents are of type {@link allElementTypes.Containable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Children</em>' containment reference list.
	 * @see allElementTypes.AllElementTypesPackage#getValueBased_Children()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<Containable> getChildren();

	/**
	 * Returns the value of the '<em><b>Referenced</b></em>' reference list.
	 * The list contents are of type {@link allElementTypes.Containable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referenced</em>' reference list.
	 * @see allElementTypes.AllElementTypesPackage#getValueBased_Referenced()
	 * @model
	 * @generated
	 */
	EList<Containable> getReferenced();

} // ValueBased
