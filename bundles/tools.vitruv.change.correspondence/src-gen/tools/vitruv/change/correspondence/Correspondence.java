/**
 */
package tools.vitruv.change.correspondence;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Correspondence</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.correspondence.Correspondence#getTag <em>Tag</em>}</li>
 *   <li>{@link tools.vitruv.change.correspondence.Correspondence#getLeftEObjects <em>Left EObjects</em>}</li>
 *   <li>{@link tools.vitruv.change.correspondence.Correspondence#getRightEObjects <em>Right EObjects</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.correspondence.CorrespondencePackage#getCorrespondence()
 * @model abstract="true"
 * @generated
 */
public interface Correspondence extends EObject
{
	/**
	 * Returns the value of the '<em><b>Tag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tag</em>' attribute.
	 * @see #setTag(String)
	 * @see tools.vitruv.change.correspondence.CorrespondencePackage#getCorrespondence_Tag()
	 * @model
	 * @generated
	 */
	String getTag();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.correspondence.Correspondence#getTag <em>Tag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tag</em>' attribute.
	 * @see #getTag()
	 * @generated
	 */
	void setTag(String value);

	/**
	 * Returns the value of the '<em><b>Left EObjects</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Left EObjects</em>' reference list.
	 * @see tools.vitruv.change.correspondence.CorrespondencePackage#getCorrespondence_LeftEObjects()
	 * @model resolveProxies="false"
	 * @generated
	 */
	EList<EObject> getLeftEObjects();

	/**
	 * Returns the value of the '<em><b>Right EObjects</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right EObjects</em>' reference list.
	 * @see tools.vitruv.change.correspondence.CorrespondencePackage#getCorrespondence_RightEObjects()
	 * @model resolveProxies="false"
	 * @generated
	 */
	EList<EObject> getRightEObjects();

} // Correspondence
