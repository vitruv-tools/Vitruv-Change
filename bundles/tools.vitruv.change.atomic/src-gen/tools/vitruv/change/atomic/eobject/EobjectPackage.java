/**
 */
package tools.vitruv.change.atomic.eobject;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import tools.vitruv.change.atomic.AtomicPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.eobject.EobjectFactory
 * @model kind="package"
 * @generated
 */
public interface EobjectPackage extends EPackage
{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "eobject";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://vitruv.tools/metamodels/change/atomic/eobject/2.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "eobject";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EobjectPackage eINSTANCE = tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl.init();

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.eobject.impl.EObjectAddedEChangeImpl <em>EObject Added EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.eobject.impl.EObjectAddedEChangeImpl
	 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getEObjectAddedEChange()
	 * @generated
	 */
	int EOBJECT_ADDED_ECHANGE = 0;

	/**
	 * The feature id for the '<em><b>New Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_ADDED_ECHANGE__NEW_VALUE = AtomicPackage.ADDITIVE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EObject Added EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_ADDED_ECHANGE_FEATURE_COUNT = AtomicPackage.ADDITIVE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_ADDED_ECHANGE___GET_NEW_VALUE = AtomicPackage.ADDITIVE_ECHANGE___GET_NEW_VALUE;

	/**
	 * The number of operations of the '<em>EObject Added EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_ADDED_ECHANGE_OPERATION_COUNT = AtomicPackage.ADDITIVE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.eobject.impl.EObjectSubtractedEChangeImpl <em>EObject Subtracted EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.eobject.impl.EObjectSubtractedEChangeImpl
	 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getEObjectSubtractedEChange()
	 * @generated
	 */
	int EOBJECT_SUBTRACTED_ECHANGE = 1;

	/**
	 * The feature id for the '<em><b>Old Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_SUBTRACTED_ECHANGE__OLD_VALUE = AtomicPackage.SUBTRACTIVE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EObject Subtracted EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_SUBTRACTED_ECHANGE_FEATURE_COUNT = AtomicPackage.SUBTRACTIVE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_SUBTRACTED_ECHANGE___GET_OLD_VALUE = AtomicPackage.SUBTRACTIVE_ECHANGE___GET_OLD_VALUE;

	/**
	 * The number of operations of the '<em>EObject Subtracted EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_SUBTRACTED_ECHANGE_OPERATION_COUNT = AtomicPackage.SUBTRACTIVE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.eobject.impl.EObjectExistenceEChangeImpl <em>EObject Existence EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.eobject.impl.EObjectExistenceEChangeImpl
	 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getEObjectExistenceEChange()
	 * @generated
	 */
	int EOBJECT_EXISTENCE_ECHANGE = 2;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT = AtomicPackage.ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Id Attribute Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE = AtomicPackage.ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Affected EObject Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE = AtomicPackage.ECHANGE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>EObject Existence EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_EXISTENCE_ECHANGE_FEATURE_COUNT = AtomicPackage.ECHANGE_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>EObject Existence EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EOBJECT_EXISTENCE_ECHANGE_OPERATION_COUNT = AtomicPackage.ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl <em>Create EObject</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl
	 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getCreateEObject()
	 * @generated
	 */
	int CREATE_EOBJECT = 3;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE_EOBJECT__AFFECTED_ELEMENT = EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Id Attribute Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE_EOBJECT__ID_ATTRIBUTE_VALUE = EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE;

	/**
	 * The feature id for the '<em><b>Affected EObject Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE_EOBJECT__AFFECTED_EOBJECT_TYPE = EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE;

	/**
	 * The number of structural features of the '<em>Create EObject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE_EOBJECT_FEATURE_COUNT = EOBJECT_EXISTENCE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Create EObject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE_EOBJECT_OPERATION_COUNT = EOBJECT_EXISTENCE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.eobject.impl.DeleteEObjectImpl <em>Delete EObject</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.eobject.impl.DeleteEObjectImpl
	 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getDeleteEObject()
	 * @generated
	 */
	int DELETE_EOBJECT = 4;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_EOBJECT__AFFECTED_ELEMENT = EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Id Attribute Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_EOBJECT__ID_ATTRIBUTE_VALUE = EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE;

	/**
	 * The feature id for the '<em><b>Affected EObject Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_EOBJECT__AFFECTED_EOBJECT_TYPE = EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE;

	/**
	 * The number of structural features of the '<em>Delete EObject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_EOBJECT_FEATURE_COUNT = EOBJECT_EXISTENCE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Delete EObject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_EOBJECT_OPERATION_COUNT = EOBJECT_EXISTENCE_ECHANGE_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.eobject.EObjectAddedEChange <em>EObject Added EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EObject Added EChange</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectAddedEChange
	 * @generated
	 */
	EClass getEObjectAddedEChange();

	/**
	 * Returns the meta object for the reference '{@link tools.vitruv.change.atomic.eobject.EObjectAddedEChange#getNewValue <em>New Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>New Value</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectAddedEChange#getNewValue()
	 * @see #getEObjectAddedEChange()
	 * @generated
	 */
	EReference getEObjectAddedEChange_NewValue();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange <em>EObject Subtracted EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EObject Subtracted EChange</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
	 * @generated
	 */
	EClass getEObjectSubtractedEChange();

	/**
	 * Returns the meta object for the reference '{@link tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange#getOldValue <em>Old Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Old Value</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange#getOldValue()
	 * @see #getEObjectSubtractedEChange()
	 * @generated
	 */
	EReference getEObjectSubtractedEChange_OldValue();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange <em>EObject Existence EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EObject Existence EChange</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
	 * @generated
	 */
	EClass getEObjectExistenceEChange();

	/**
	 * Returns the meta object for the reference '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedElement <em>Affected Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Affected Element</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedElement()
	 * @see #getEObjectExistenceEChange()
	 * @generated
	 */
	EReference getEObjectExistenceEChange_AffectedElement();

	/**
	 * Returns the meta object for the attribute '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getIdAttributeValue <em>Id Attribute Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id Attribute Value</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getIdAttributeValue()
	 * @see #getEObjectExistenceEChange()
	 * @generated
	 */
	EAttribute getEObjectExistenceEChange_IdAttributeValue();

	/**
	 * Returns the meta object for the reference '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedEObjectType <em>Affected EObject Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Affected EObject Type</em>'.
	 * @see tools.vitruv.change.atomic.eobject.EObjectExistenceEChange#getAffectedEObjectType()
	 * @see #getEObjectExistenceEChange()
	 * @generated
	 */
	EReference getEObjectExistenceEChange_AffectedEObjectType();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.eobject.CreateEObject <em>Create EObject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Create EObject</em>'.
	 * @see tools.vitruv.change.atomic.eobject.CreateEObject
	 * @generated
	 */
	EClass getCreateEObject();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.eobject.DeleteEObject <em>Delete EObject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Delete EObject</em>'.
	 * @see tools.vitruv.change.atomic.eobject.DeleteEObject
	 * @generated
	 */
	EClass getDeleteEObject();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	EobjectFactory getEobjectFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals
	{
		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.eobject.impl.EObjectAddedEChangeImpl <em>EObject Added EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.eobject.impl.EObjectAddedEChangeImpl
		 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getEObjectAddedEChange()
		 * @generated
		 */
		EClass EOBJECT_ADDED_ECHANGE = eINSTANCE.getEObjectAddedEChange();

		/**
		 * The meta object literal for the '<em><b>New Value</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EOBJECT_ADDED_ECHANGE__NEW_VALUE = eINSTANCE.getEObjectAddedEChange_NewValue();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.eobject.impl.EObjectSubtractedEChangeImpl <em>EObject Subtracted EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.eobject.impl.EObjectSubtractedEChangeImpl
		 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getEObjectSubtractedEChange()
		 * @generated
		 */
		EClass EOBJECT_SUBTRACTED_ECHANGE = eINSTANCE.getEObjectSubtractedEChange();

		/**
		 * The meta object literal for the '<em><b>Old Value</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EOBJECT_SUBTRACTED_ECHANGE__OLD_VALUE = eINSTANCE.getEObjectSubtractedEChange_OldValue();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.eobject.impl.EObjectExistenceEChangeImpl <em>EObject Existence EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.eobject.impl.EObjectExistenceEChangeImpl
		 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getEObjectExistenceEChange()
		 * @generated
		 */
		EClass EOBJECT_EXISTENCE_ECHANGE = eINSTANCE.getEObjectExistenceEChange();

		/**
		 * The meta object literal for the '<em><b>Affected Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT = eINSTANCE.getEObjectExistenceEChange_AffectedElement();

		/**
		 * The meta object literal for the '<em><b>Id Attribute Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE = eINSTANCE.getEObjectExistenceEChange_IdAttributeValue();

		/**
		 * The meta object literal for the '<em><b>Affected EObject Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE = eINSTANCE.getEObjectExistenceEChange_AffectedEObjectType();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl <em>Create EObject</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl
		 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getCreateEObject()
		 * @generated
		 */
		EClass CREATE_EOBJECT = eINSTANCE.getCreateEObject();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.eobject.impl.DeleteEObjectImpl <em>Delete EObject</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.eobject.impl.DeleteEObjectImpl
		 * @see tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl#getDeleteEObject()
		 * @generated
		 */
		EClass DELETE_EOBJECT = eINSTANCE.getDeleteEObject();

	}

} //EobjectPackage
