/**
 */
package tools.vitruv.change.atomic.eobject;

import org.eclipse.emf.ecore.EClass;

import tools.vitruv.change.atomic.EChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EObject Existence EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which creates or deletes an EObject.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedElement <em>Affected Element</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getIdAttributeValue <em>Id Attribute Value</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedEObjectType <em>Affected EObject Type</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectExistenceEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface EObjectExistenceEChange<Element extends Object> extends EChange<Element>
{
	/**
	 * Returns the value of the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * The created or deleted EObject.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Affected Element</em>' reference.
	 * @see #setAffectedElement(Object)
	 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectExistenceEChange_AffectedElement()
	 * @model kind="reference" resolveProxies="false" required="true"
	 * @generated
	 */
	Element getAffectedElement();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedElement <em>Affected Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Affected Element</em>' reference.
	 * @see #getAffectedElement()
	 * @generated
	 */
	void setAffectedElement(Element value);

	/**
	 * Returns the value of the '<em><b>Id Attribute Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id Attribute Value</em>' attribute.
	 * @see #setIdAttributeValue(String)
	 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectExistenceEChange_IdAttributeValue()
	 * @model
	 * @generated
	 */
	String getIdAttributeValue();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getIdAttributeValue <em>Id Attribute Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Attribute Value</em>' attribute.
	 * @see #getIdAttributeValue()
	 * @generated
	 */
	void setIdAttributeValue(String value);

	/**
	 * Returns the value of the '<em><b>Affected EObject Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Affected EObject Type</em>' reference.
	 * @see #setAffectedEObjectType(EClass)
	 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#getEObjectExistenceEChange_AffectedEObjectType()
	 * @model
	 * @generated
	 */
	EClass getAffectedEObjectType();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedEObjectType <em>Affected EObject Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Affected EObject Type</em>' reference.
	 * @see #getAffectedEObjectType()
	 * @generated
	 */
	void setAffectedEObjectType(EClass value);

} // EObjectExistenceEChange
