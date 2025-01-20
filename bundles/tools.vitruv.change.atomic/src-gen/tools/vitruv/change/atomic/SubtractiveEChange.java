/**
 */
package tools.vitruv.change.atomic;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Subtractive EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which removes an existing value, like an EObject or primitive type.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.AtomicPackage#getSubtractiveEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface SubtractiveEChange<Element extends Object, Value extends Object> extends EChange<Element>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * Get the removed value.
	 * @return The value which will be removed.
	 * <!-- end-model-doc -->
	 * @model kind="operation" required="true"
	 * @generated
	 */
	Value getOldValue();

} // SubtractiveEChange
