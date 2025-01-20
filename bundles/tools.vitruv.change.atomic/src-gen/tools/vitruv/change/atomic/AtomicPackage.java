/**
 */
package tools.vitruv.change.atomic;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

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
 * @see tools.vitruv.change.atomic.AtomicFactory
 * @model kind="package"
 * @generated
 */
public interface AtomicPackage extends EPackage
{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "atomic";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://vitruv.tools/metamodels/change/atomic/2.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "atomic";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AtomicPackage eINSTANCE = tools.vitruv.change.atomic.impl.AtomicPackageImpl.init();

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.impl.EChangeImpl <em>EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.impl.EChangeImpl
	 * @see tools.vitruv.change.atomic.impl.AtomicPackageImpl#getEChange()
	 * @generated
	 */
	int ECHANGE = 0;

	/**
	 * The number of structural features of the '<em>EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHANGE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECHANGE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.impl.AdditiveEChangeImpl <em>Additive EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.impl.AdditiveEChangeImpl
	 * @see tools.vitruv.change.atomic.impl.AtomicPackageImpl#getAdditiveEChange()
	 * @generated
	 */
	int ADDITIVE_ECHANGE = 1;

	/**
	 * The number of structural features of the '<em>Additive EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ECHANGE_FEATURE_COUNT = ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ECHANGE___GET_NEW_VALUE = ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Additive EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ECHANGE_OPERATION_COUNT = ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.impl.SubtractiveEChangeImpl <em>Subtractive EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.impl.SubtractiveEChangeImpl
	 * @see tools.vitruv.change.atomic.impl.AtomicPackageImpl#getSubtractiveEChange()
	 * @generated
	 */
	int SUBTRACTIVE_ECHANGE = 2;

	/**
	 * The number of structural features of the '<em>Subtractive EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ECHANGE_FEATURE_COUNT = ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ECHANGE___GET_OLD_VALUE = ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Subtractive EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ECHANGE_OPERATION_COUNT = ECHANGE_OPERATION_COUNT + 1;


	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.EChange <em>EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EChange</em>'.
	 * @see tools.vitruv.change.atomic.EChange
	 * @generated
	 */
	EClass getEChange();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.AdditiveEChange <em>Additive EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Additive EChange</em>'.
	 * @see tools.vitruv.change.atomic.AdditiveEChange
	 * @generated
	 */
	EClass getAdditiveEChange();

	/**
	 * Returns the meta object for the '{@link tools.vitruv.change.atomic.AdditiveEChange#getNewValue() <em>Get New Value</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get New Value</em>' operation.
	 * @see tools.vitruv.change.atomic.AdditiveEChange#getNewValue()
	 * @generated
	 */
	EOperation getAdditiveEChange__GetNewValue();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.SubtractiveEChange <em>Subtractive EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Subtractive EChange</em>'.
	 * @see tools.vitruv.change.atomic.SubtractiveEChange
	 * @generated
	 */
	EClass getSubtractiveEChange();

	/**
	 * Returns the meta object for the '{@link tools.vitruv.change.atomic.SubtractiveEChange#getOldValue() <em>Get Old Value</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Old Value</em>' operation.
	 * @see tools.vitruv.change.atomic.SubtractiveEChange#getOldValue()
	 * @generated
	 */
	EOperation getSubtractiveEChange__GetOldValue();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AtomicFactory getAtomicFactory();

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
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.impl.EChangeImpl <em>EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.impl.EChangeImpl
		 * @see tools.vitruv.change.atomic.impl.AtomicPackageImpl#getEChange()
		 * @generated
		 */
		EClass ECHANGE = eINSTANCE.getEChange();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.impl.AdditiveEChangeImpl <em>Additive EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.impl.AdditiveEChangeImpl
		 * @see tools.vitruv.change.atomic.impl.AtomicPackageImpl#getAdditiveEChange()
		 * @generated
		 */
		EClass ADDITIVE_ECHANGE = eINSTANCE.getAdditiveEChange();

		/**
		 * The meta object literal for the '<em><b>Get New Value</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ADDITIVE_ECHANGE___GET_NEW_VALUE = eINSTANCE.getAdditiveEChange__GetNewValue();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.impl.SubtractiveEChangeImpl <em>Subtractive EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.impl.SubtractiveEChangeImpl
		 * @see tools.vitruv.change.atomic.impl.AtomicPackageImpl#getSubtractiveEChange()
		 * @generated
		 */
		EClass SUBTRACTIVE_ECHANGE = eINSTANCE.getSubtractiveEChange();

		/**
		 * The meta object literal for the '<em><b>Get Old Value</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation SUBTRACTIVE_ECHANGE___GET_OLD_VALUE = eINSTANCE.getSubtractiveEChange__GetOldValue();

	}

} //AtomicPackage
