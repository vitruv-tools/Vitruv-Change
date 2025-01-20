/**
 */
package tools.vitruv.change.atomic;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Additive EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which adds a new value, like an EObject or primitive type.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.AtomicPackage#getAdditiveEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface AdditiveEChange<Element extends Object, Value extends Object> extends EChange<Element>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * Get the added value.
	 * @return The newly added value.
	 * <!-- end-model-doc -->
	 * @model kind="operation" required="true"
	 * @generated
	 */
	Value getNewValue();

} // AdditiveEChange
