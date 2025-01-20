/**
 */
package tools.vitruv.change.atomic.feature.attribute;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage
 * @generated
 */
public interface AttributeFactory extends EFactory
{
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AttributeFactory eINSTANCE = tools.vitruv.change.atomic.feature.attribute.impl.AttributeFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Insert EAttribute Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Insert EAttribute Value</em>'.
	 * @generated
	 */
	<Element extends Object, Value extends Object> InsertEAttributeValue<Element, Value> createInsertEAttributeValue();

	/**
	 * Returns a new object of class '<em>Remove EAttribute Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Remove EAttribute Value</em>'.
	 * @generated
	 */
	<Element extends Object, Value extends Object> RemoveEAttributeValue<Element, Value> createRemoveEAttributeValue();

	/**
	 * Returns a new object of class '<em>Replace Single Valued EAttribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Replace Single Valued EAttribute</em>'.
	 * @generated
	 */
	<Element extends Object, Value extends Object> ReplaceSingleValuedEAttribute<Element, Value> createReplaceSingleValuedEAttribute();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	AttributePackage getAttributePackage();

} //AttributeFactory
