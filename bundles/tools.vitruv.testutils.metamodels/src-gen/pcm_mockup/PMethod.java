/**
 */
package pcm_mockup;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>PMethod</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link pcm_mockup.PMethod#getReturnType <em>Return Type</em>}</li>
 * </ul>
 *
 * @see pcm_mockup.Pcm_mockupPackage#getPMethod()
 * @model
 * @generated
 */
public interface PMethod extends Identified, PNamedElement
{
	/**
	 * Returns the value of the '<em><b>Return Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Return Type</em>' reference.
	 * @see #setReturnType(PInterface)
	 * @see pcm_mockup.Pcm_mockupPackage#getPMethod_ReturnType()
	 * @model
	 * @generated
	 */
	PInterface getReturnType();

	/**
	 * Sets the value of the '{@link pcm_mockup.PMethod#getReturnType <em>Return Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Return Type</em>' reference.
	 * @see #getReturnType()
	 * @generated
	 */
	void setReturnType(PInterface value);

} // PMethod
