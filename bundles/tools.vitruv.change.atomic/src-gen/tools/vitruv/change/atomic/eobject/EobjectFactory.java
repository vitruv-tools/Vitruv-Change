/**
 */
package tools.vitruv.change.atomic.eobject;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.eobject.EobjectPackage
 * @generated
 */
public interface EobjectFactory extends EFactory
{
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EobjectFactory eINSTANCE = tools.vitruv.change.atomic.eobject.impl.EobjectFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Create EObject</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Create EObject</em>'.
	 * @generated
	 */
	<Element extends Object> CreateEObject<Element> createCreateEObject();

	/**
	 * Returns a new object of class '<em>Delete EObject</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Delete EObject</em>'.
	 * @generated
	 */
	<Element extends Object> DeleteEObject<Element> createDeleteEObject();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	EobjectPackage getEobjectPackage();

} //EobjectFactory
