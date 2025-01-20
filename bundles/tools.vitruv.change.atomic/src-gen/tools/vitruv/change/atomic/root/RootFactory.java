/**
 */
package tools.vitruv.change.atomic.root;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.root.RootPackage
 * @generated
 */
public interface RootFactory extends EFactory
{
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RootFactory eINSTANCE = tools.vitruv.change.atomic.root.impl.RootFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Insert Root EObject</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Insert Root EObject</em>'.
	 * @generated
	 */
	<Element extends Object> InsertRootEObject<Element> createInsertRootEObject();

	/**
	 * Returns a new object of class '<em>Remove Root EObject</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Remove Root EObject</em>'.
	 * @generated
	 */
	<Element extends Object> RemoveRootEObject<Element> createRemoveRootEObject();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	RootPackage getRootPackage();

} //RootFactory
