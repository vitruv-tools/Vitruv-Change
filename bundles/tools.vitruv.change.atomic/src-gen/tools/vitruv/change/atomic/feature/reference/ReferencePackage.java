/**
 */
package tools.vitruv.change.atomic.feature.reference;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import tools.vitruv.change.atomic.feature.FeaturePackage;

import tools.vitruv.change.atomic.feature.list.ListPackage;

import tools.vitruv.change.atomic.feature.single.SinglePackage;

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
 * @see tools.vitruv.change.atomic.feature.reference.ReferenceFactory
 * @model kind="package"
 * @generated
 */
public interface ReferencePackage extends EPackage
{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "reference";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://vitruv.tools/metamodels/change/atomic/feature/reference/2.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "reference";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ReferencePackage eINSTANCE = tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl.init();

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.reference.impl.UpdateReferenceEChangeImpl <em>Update Reference EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.reference.impl.UpdateReferenceEChangeImpl
	 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getUpdateReferenceEChange()
	 * @generated
	 */
	int UPDATE_REFERENCE_ECHANGE = 0;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_REFERENCE_ECHANGE__AFFECTED_FEATURE = FeaturePackage.FEATURE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_REFERENCE_ECHANGE__AFFECTED_ELEMENT = FeaturePackage.FEATURE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The number of structural features of the '<em>Update Reference EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_REFERENCE_ECHANGE_FEATURE_COUNT = FeaturePackage.FEATURE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Is Containment</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_REFERENCE_ECHANGE___IS_CONTAINMENT = FeaturePackage.FEATURE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Update Reference EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_REFERENCE_ECHANGE_OPERATION_COUNT = FeaturePackage.FEATURE_ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.reference.impl.AdditiveReferenceEChangeImpl <em>Additive Reference EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.reference.impl.AdditiveReferenceEChangeImpl
	 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getAdditiveReferenceEChange()
	 * @generated
	 */
	int ADDITIVE_REFERENCE_ECHANGE = 1;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE__AFFECTED_FEATURE = UPDATE_REFERENCE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE__AFFECTED_ELEMENT = UPDATE_REFERENCE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>New Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE__NEW_VALUE = UPDATE_REFERENCE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Was Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE__WAS_UNSET = UPDATE_REFERENCE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Additive Reference EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE_FEATURE_COUNT = UPDATE_REFERENCE_ECHANGE_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Is Containment</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE___IS_CONTAINMENT = UPDATE_REFERENCE_ECHANGE___IS_CONTAINMENT;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE___GET_NEW_VALUE = UPDATE_REFERENCE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Additive Reference EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_REFERENCE_ECHANGE_OPERATION_COUNT = UPDATE_REFERENCE_ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.reference.impl.SubtractiveReferenceEChangeImpl <em>Subtractive Reference EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.reference.impl.SubtractiveReferenceEChangeImpl
	 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getSubtractiveReferenceEChange()
	 * @generated
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE = 2;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE__AFFECTED_FEATURE = UPDATE_REFERENCE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE__AFFECTED_ELEMENT = UPDATE_REFERENCE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Old Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE__OLD_VALUE = UPDATE_REFERENCE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Subtractive Reference EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE_FEATURE_COUNT = UPDATE_REFERENCE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Is Containment</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE___IS_CONTAINMENT = UPDATE_REFERENCE_ECHANGE___IS_CONTAINMENT;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE___GET_OLD_VALUE = UPDATE_REFERENCE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Subtractive Reference EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_REFERENCE_ECHANGE_OPERATION_COUNT = UPDATE_REFERENCE_ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.reference.impl.InsertEReferenceImpl <em>Insert EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.reference.impl.InsertEReferenceImpl
	 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getInsertEReference()
	 * @generated
	 */
	int INSERT_EREFERENCE = 3;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE__AFFECTED_FEATURE = ListPackage.INSERT_IN_LIST_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE__AFFECTED_ELEMENT = ListPackage.INSERT_IN_LIST_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE__INDEX = ListPackage.INSERT_IN_LIST_ECHANGE__INDEX;

	/**
	 * The feature id for the '<em><b>New Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE__NEW_VALUE = ListPackage.INSERT_IN_LIST_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Was Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE__WAS_UNSET = ListPackage.INSERT_IN_LIST_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Insert EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE_FEATURE_COUNT = ListPackage.INSERT_IN_LIST_ECHANGE_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE___GET_NEW_VALUE = ListPackage.INSERT_IN_LIST_ECHANGE___GET_NEW_VALUE;

	/**
	 * The operation id for the '<em>Is Containment</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE___IS_CONTAINMENT = ListPackage.INSERT_IN_LIST_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Insert EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EREFERENCE_OPERATION_COUNT = ListPackage.INSERT_IN_LIST_ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.reference.impl.RemoveEReferenceImpl <em>Remove EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.reference.impl.RemoveEReferenceImpl
	 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getRemoveEReference()
	 * @generated
	 */
	int REMOVE_EREFERENCE = 4;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE__AFFECTED_FEATURE = ListPackage.REMOVE_FROM_LIST_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE__AFFECTED_ELEMENT = ListPackage.REMOVE_FROM_LIST_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE__INDEX = ListPackage.REMOVE_FROM_LIST_ECHANGE__INDEX;

	/**
	 * The feature id for the '<em><b>Old Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE__OLD_VALUE = ListPackage.REMOVE_FROM_LIST_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Remove EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE_FEATURE_COUNT = ListPackage.REMOVE_FROM_LIST_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE___GET_OLD_VALUE = ListPackage.REMOVE_FROM_LIST_ECHANGE___GET_OLD_VALUE;

	/**
	 * The operation id for the '<em>Is Containment</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE___IS_CONTAINMENT = ListPackage.REMOVE_FROM_LIST_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Remove EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EREFERENCE_OPERATION_COUNT = ListPackage.REMOVE_FROM_LIST_ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.reference.impl.ReplaceSingleValuedEReferenceImpl <em>Replace Single Valued EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.reference.impl.ReplaceSingleValuedEReferenceImpl
	 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getReplaceSingleValuedEReference()
	 * @generated
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE = 5;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE__AFFECTED_FEATURE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE__AFFECTED_ELEMENT = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Is Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE__IS_UNSET = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET;

	/**
	 * The feature id for the '<em><b>New Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE__NEW_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Was Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE__WAS_UNSET = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Old Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE__OLD_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Replace Single Valued EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE_FEATURE_COUNT = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 3;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE___GET_NEW_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_NEW_VALUE;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE___GET_OLD_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_OLD_VALUE;

	/**
	 * The operation id for the '<em>Is From Non Default Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE___IS_FROM_NON_DEFAULT_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_FROM_NON_DEFAULT_VALUE;

	/**
	 * The operation id for the '<em>Is To Non Default Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE___IS_TO_NON_DEFAULT_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_TO_NON_DEFAULT_VALUE;

	/**
	 * The operation id for the '<em>Is Containment</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE___IS_CONTAINMENT = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Replace Single Valued EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EREFERENCE_OPERATION_COUNT = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_OPERATION_COUNT + 1;


	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange <em>Update Reference EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Update Reference EChange</em>'.
	 * @see tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange
	 * @generated
	 */
	EClass getUpdateReferenceEChange();

	/**
	 * Returns the meta object for the '{@link tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange#isContainment() <em>Is Containment</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Is Containment</em>' operation.
	 * @see tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange#isContainment()
	 * @generated
	 */
	EOperation getUpdateReferenceEChange__IsContainment();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange <em>Additive Reference EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Additive Reference EChange</em>'.
	 * @see tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange
	 * @generated
	 */
	EClass getAdditiveReferenceEChange();

	/**
	 * Returns the meta object for the attribute '{@link tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange#isWasUnset <em>Was Unset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Was Unset</em>'.
	 * @see tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange#isWasUnset()
	 * @see #getAdditiveReferenceEChange()
	 * @generated
	 */
	EAttribute getAdditiveReferenceEChange_WasUnset();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange <em>Subtractive Reference EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Subtractive Reference EChange</em>'.
	 * @see tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange
	 * @generated
	 */
	EClass getSubtractiveReferenceEChange();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.reference.InsertEReference <em>Insert EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Insert EReference</em>'.
	 * @see tools.vitruv.change.atomic.feature.reference.InsertEReference
	 * @generated
	 */
	EClass getInsertEReference();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.reference.RemoveEReference <em>Remove EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Remove EReference</em>'.
	 * @see tools.vitruv.change.atomic.feature.reference.RemoveEReference
	 * @generated
	 */
	EClass getRemoveEReference();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference <em>Replace Single Valued EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Replace Single Valued EReference</em>'.
	 * @see tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference
	 * @generated
	 */
	EClass getReplaceSingleValuedEReference();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ReferenceFactory getReferenceFactory();

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
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.reference.impl.UpdateReferenceEChangeImpl <em>Update Reference EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.reference.impl.UpdateReferenceEChangeImpl
		 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getUpdateReferenceEChange()
		 * @generated
		 */
		EClass UPDATE_REFERENCE_ECHANGE = eINSTANCE.getUpdateReferenceEChange();

		/**
		 * The meta object literal for the '<em><b>Is Containment</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation UPDATE_REFERENCE_ECHANGE___IS_CONTAINMENT = eINSTANCE.getUpdateReferenceEChange__IsContainment();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.reference.impl.AdditiveReferenceEChangeImpl <em>Additive Reference EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.reference.impl.AdditiveReferenceEChangeImpl
		 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getAdditiveReferenceEChange()
		 * @generated
		 */
		EClass ADDITIVE_REFERENCE_ECHANGE = eINSTANCE.getAdditiveReferenceEChange();

		/**
		 * The meta object literal for the '<em><b>Was Unset</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADDITIVE_REFERENCE_ECHANGE__WAS_UNSET = eINSTANCE.getAdditiveReferenceEChange_WasUnset();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.reference.impl.SubtractiveReferenceEChangeImpl <em>Subtractive Reference EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.reference.impl.SubtractiveReferenceEChangeImpl
		 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getSubtractiveReferenceEChange()
		 * @generated
		 */
		EClass SUBTRACTIVE_REFERENCE_ECHANGE = eINSTANCE.getSubtractiveReferenceEChange();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.reference.impl.InsertEReferenceImpl <em>Insert EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.reference.impl.InsertEReferenceImpl
		 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getInsertEReference()
		 * @generated
		 */
		EClass INSERT_EREFERENCE = eINSTANCE.getInsertEReference();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.reference.impl.RemoveEReferenceImpl <em>Remove EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.reference.impl.RemoveEReferenceImpl
		 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getRemoveEReference()
		 * @generated
		 */
		EClass REMOVE_EREFERENCE = eINSTANCE.getRemoveEReference();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.reference.impl.ReplaceSingleValuedEReferenceImpl <em>Replace Single Valued EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.reference.impl.ReplaceSingleValuedEReferenceImpl
		 * @see tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl#getReplaceSingleValuedEReference()
		 * @generated
		 */
		EClass REPLACE_SINGLE_VALUED_EREFERENCE = eINSTANCE.getReplaceSingleValuedEReference();

	}

} //ReferencePackage
