/**
 */
package tools.vitruv.change.atomic.feature;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.feature.FeaturePackage
 * @generated
 */
public interface FeatureFactory extends EFactory
{
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	FeatureFactory eINSTANCE = tools.vitruv.change.atomic.feature.impl.FeatureFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Unset Feature</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Unset Feature</em>'.
	 * @generated
	 */
	<Element extends Object, Feature extends EStructuralFeature> UnsetFeature<Element, Feature> createUnsetFeature();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	FeaturePackage getFeaturePackage();

} //FeatureFactory
