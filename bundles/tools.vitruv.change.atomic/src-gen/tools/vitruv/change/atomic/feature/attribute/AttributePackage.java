/**
 */
package tools.vitruv.change.atomic.feature.attribute;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see tools.vitruv.change.atomic.feature.attribute.AttributeFactory
 * @model kind="package"
 * @generated
 */
public interface AttributePackage extends EPackage
{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "attribute";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://vitruv.tools/metamodels/change/atomic/feature/attribute/2.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "attribute";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AttributePackage eINSTANCE = tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl.init();

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.UpdateAttributeEChangeImpl <em>Update Attribute EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.UpdateAttributeEChangeImpl
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getUpdateAttributeEChange()
	 * @generated
	 */
	int UPDATE_ATTRIBUTE_ECHANGE = 0;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_ATTRIBUTE_ECHANGE__AFFECTED_FEATURE = FeaturePackage.FEATURE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_ATTRIBUTE_ECHANGE__AFFECTED_ELEMENT = FeaturePackage.FEATURE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The number of structural features of the '<em>Update Attribute EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_ATTRIBUTE_ECHANGE_FEATURE_COUNT = FeaturePackage.FEATURE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Update Attribute EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPDATE_ATTRIBUTE_ECHANGE_OPERATION_COUNT = FeaturePackage.FEATURE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.AdditiveAttributeEChangeImpl <em>Additive Attribute EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.AdditiveAttributeEChangeImpl
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getAdditiveAttributeEChange()
	 * @generated
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE = 1;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE__AFFECTED_FEATURE = UPDATE_ATTRIBUTE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE__AFFECTED_ELEMENT = UPDATE_ATTRIBUTE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>New Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE = UPDATE_ATTRIBUTE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Was Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET = UPDATE_ATTRIBUTE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Additive Attribute EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE_FEATURE_COUNT = UPDATE_ATTRIBUTE_ECHANGE_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE___GET_NEW_VALUE = UPDATE_ATTRIBUTE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Additive Attribute EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIVE_ATTRIBUTE_ECHANGE_OPERATION_COUNT = UPDATE_ATTRIBUTE_ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.SubtractiveAttributeEChangeImpl <em>Subtractive Attribute EChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.SubtractiveAttributeEChangeImpl
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getSubtractiveAttributeEChange()
	 * @generated
	 */
	int SUBTRACTIVE_ATTRIBUTE_ECHANGE = 2;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ATTRIBUTE_ECHANGE__AFFECTED_FEATURE = UPDATE_ATTRIBUTE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ATTRIBUTE_ECHANGE__AFFECTED_ELEMENT = UPDATE_ATTRIBUTE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Old Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ATTRIBUTE_ECHANGE__OLD_VALUE = UPDATE_ATTRIBUTE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Subtractive Attribute EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ATTRIBUTE_ECHANGE_FEATURE_COUNT = UPDATE_ATTRIBUTE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ATTRIBUTE_ECHANGE___GET_OLD_VALUE = UPDATE_ATTRIBUTE_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Subtractive Attribute EChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBTRACTIVE_ATTRIBUTE_ECHANGE_OPERATION_COUNT = UPDATE_ATTRIBUTE_ECHANGE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl <em>Insert EAttribute Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getInsertEAttributeValue()
	 * @generated
	 */
	int INSERT_EATTRIBUTE_VALUE = 3;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE__AFFECTED_FEATURE = ListPackage.INSERT_IN_LIST_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE__AFFECTED_ELEMENT = ListPackage.INSERT_IN_LIST_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE__INDEX = ListPackage.INSERT_IN_LIST_ECHANGE__INDEX;

	/**
	 * The feature id for the '<em><b>New Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE__NEW_VALUE = ListPackage.INSERT_IN_LIST_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Was Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE__WAS_UNSET = ListPackage.INSERT_IN_LIST_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Insert EAttribute Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE_FEATURE_COUNT = ListPackage.INSERT_IN_LIST_ECHANGE_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE___GET_NEW_VALUE = ListPackage.INSERT_IN_LIST_ECHANGE___GET_NEW_VALUE;

	/**
	 * The number of operations of the '<em>Insert EAttribute Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSERT_EATTRIBUTE_VALUE_OPERATION_COUNT = ListPackage.INSERT_IN_LIST_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.RemoveEAttributeValueImpl <em>Remove EAttribute Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.RemoveEAttributeValueImpl
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getRemoveEAttributeValue()
	 * @generated
	 */
	int REMOVE_EATTRIBUTE_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EATTRIBUTE_VALUE__AFFECTED_FEATURE = ListPackage.REMOVE_FROM_LIST_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EATTRIBUTE_VALUE__AFFECTED_ELEMENT = ListPackage.REMOVE_FROM_LIST_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EATTRIBUTE_VALUE__INDEX = ListPackage.REMOVE_FROM_LIST_ECHANGE__INDEX;

	/**
	 * The feature id for the '<em><b>Old Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EATTRIBUTE_VALUE__OLD_VALUE = ListPackage.REMOVE_FROM_LIST_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Remove EAttribute Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EATTRIBUTE_VALUE_FEATURE_COUNT = ListPackage.REMOVE_FROM_LIST_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EATTRIBUTE_VALUE___GET_OLD_VALUE = ListPackage.REMOVE_FROM_LIST_ECHANGE___GET_OLD_VALUE;

	/**
	 * The number of operations of the '<em>Remove EAttribute Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_EATTRIBUTE_VALUE_OPERATION_COUNT = ListPackage.REMOVE_FROM_LIST_ECHANGE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl <em>Replace Single Valued EAttribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl
	 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getReplaceSingleValuedEAttribute()
	 * @generated
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE = 5;

	/**
	 * The feature id for the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE__AFFECTED_FEATURE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__AFFECTED_FEATURE;

	/**
	 * The feature id for the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE__AFFECTED_ELEMENT = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__AFFECTED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Is Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE__IS_UNSET = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET;

	/**
	 * The feature id for the '<em><b>New Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Was Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Old Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Replace Single Valued EAttribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE_FEATURE_COUNT = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_FEATURE_COUNT + 3;

	/**
	 * The operation id for the '<em>Get New Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE___GET_NEW_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_NEW_VALUE;

	/**
	 * The operation id for the '<em>Get Old Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE___GET_OLD_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_OLD_VALUE;

	/**
	 * The operation id for the '<em>Is From Non Default Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE___IS_FROM_NON_DEFAULT_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_FROM_NON_DEFAULT_VALUE;

	/**
	 * The operation id for the '<em>Is To Non Default Value</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE___IS_TO_NON_DEFAULT_VALUE = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_TO_NON_DEFAULT_VALUE;

	/**
	 * The number of operations of the '<em>Replace Single Valued EAttribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_SINGLE_VALUED_EATTRIBUTE_OPERATION_COUNT = SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange <em>Update Attribute EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Update Attribute EChange</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange
	 * @generated
	 */
	EClass getUpdateAttributeEChange();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange <em>Additive Attribute EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Additive Attribute EChange</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange
	 * @generated
	 */
	EClass getAdditiveAttributeEChange();

	/**
	 * Returns the meta object for the attribute '{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#getNewValue <em>New Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>New Value</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#getNewValue()
	 * @see #getAdditiveAttributeEChange()
	 * @generated
	 */
	EAttribute getAdditiveAttributeEChange_NewValue();

	/**
	 * Returns the meta object for the attribute '{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#isWasUnset <em>Was Unset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Was Unset</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange#isWasUnset()
	 * @see #getAdditiveAttributeEChange()
	 * @generated
	 */
	EAttribute getAdditiveAttributeEChange_WasUnset();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange <em>Subtractive Attribute EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Subtractive Attribute EChange</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange
	 * @generated
	 */
	EClass getSubtractiveAttributeEChange();

	/**
	 * Returns the meta object for the attribute '{@link tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange#getOldValue <em>Old Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Old Value</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange#getOldValue()
	 * @see #getSubtractiveAttributeEChange()
	 * @generated
	 */
	EAttribute getSubtractiveAttributeEChange_OldValue();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue <em>Insert EAttribute Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Insert EAttribute Value</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue
	 * @generated
	 */
	EClass getInsertEAttributeValue();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue <em>Remove EAttribute Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Remove EAttribute Value</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue
	 * @generated
	 */
	EClass getRemoveEAttributeValue();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute <em>Replace Single Valued EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Replace Single Valued EAttribute</em>'.
	 * @see tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute
	 * @generated
	 */
	EClass getReplaceSingleValuedEAttribute();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AttributeFactory getAttributeFactory();

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
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.UpdateAttributeEChangeImpl <em>Update Attribute EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.UpdateAttributeEChangeImpl
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getUpdateAttributeEChange()
		 * @generated
		 */
		EClass UPDATE_ATTRIBUTE_ECHANGE = eINSTANCE.getUpdateAttributeEChange();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.AdditiveAttributeEChangeImpl <em>Additive Attribute EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.AdditiveAttributeEChangeImpl
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getAdditiveAttributeEChange()
		 * @generated
		 */
		EClass ADDITIVE_ATTRIBUTE_ECHANGE = eINSTANCE.getAdditiveAttributeEChange();

		/**
		 * The meta object literal for the '<em><b>New Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE = eINSTANCE.getAdditiveAttributeEChange_NewValue();

		/**
		 * The meta object literal for the '<em><b>Was Unset</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET = eINSTANCE.getAdditiveAttributeEChange_WasUnset();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.SubtractiveAttributeEChangeImpl <em>Subtractive Attribute EChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.SubtractiveAttributeEChangeImpl
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getSubtractiveAttributeEChange()
		 * @generated
		 */
		EClass SUBTRACTIVE_ATTRIBUTE_ECHANGE = eINSTANCE.getSubtractiveAttributeEChange();

		/**
		 * The meta object literal for the '<em><b>Old Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SUBTRACTIVE_ATTRIBUTE_ECHANGE__OLD_VALUE = eINSTANCE.getSubtractiveAttributeEChange_OldValue();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl <em>Insert EAttribute Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getInsertEAttributeValue()
		 * @generated
		 */
		EClass INSERT_EATTRIBUTE_VALUE = eINSTANCE.getInsertEAttributeValue();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.RemoveEAttributeValueImpl <em>Remove EAttribute Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.RemoveEAttributeValueImpl
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getRemoveEAttributeValue()
		 * @generated
		 */
		EClass REMOVE_EATTRIBUTE_VALUE = eINSTANCE.getRemoveEAttributeValue();

		/**
		 * The meta object literal for the '{@link tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl <em>Replace Single Valued EAttribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl
		 * @see tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl#getReplaceSingleValuedEAttribute()
		 * @generated
		 */
		EClass REPLACE_SINGLE_VALUED_EATTRIBUTE = eINSTANCE.getReplaceSingleValuedEAttribute();

	}

} //AttributePackage
