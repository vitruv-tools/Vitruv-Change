/**
 */
package tools.vitruv.change.atomic.feature.reference;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.feature.reference.ReferencePackage
 * @generated
 */
public interface ReferenceFactory extends EFactory
{
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ReferenceFactory eINSTANCE = tools.vitruv.change.atomic.feature.reference.impl.ReferenceFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Insert EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Insert EReference</em>'.
	 * @generated
	 */
	<Element extends Object> InsertEReference<Element> createInsertEReference();

	/**
	 * Returns a new object of class '<em>Remove EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Remove EReference</em>'.
	 * @generated
	 */
	<Element extends Object> RemoveEReference<Element> createRemoveEReference();

	/**
	 * Returns a new object of class '<em>Replace Single Valued EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Replace Single Valued EReference</em>'.
	 * @generated
	 */
	<Element extends Object> ReplaceSingleValuedEReference<Element> createReplaceSingleValuedEReference();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ReferencePackage getReferencePackage();

} //ReferenceFactory
