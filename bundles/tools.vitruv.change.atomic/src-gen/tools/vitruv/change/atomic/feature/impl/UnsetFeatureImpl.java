/**
 */
package tools.vitruv.change.atomic.feature.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.feature.FeaturePackage;
import tools.vitruv.change.atomic.feature.UnsetFeature;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Unset Feature</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class UnsetFeatureImpl<Element extends Object, Feature extends EStructuralFeature> extends FeatureEChangeImpl<Element, Feature> implements UnsetFeature<Element, Feature>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UnsetFeatureImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return FeaturePackage.Literals.UNSET_FEATURE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * This is specialized for the more specific type known in this context.
	 * @generated
	 */
	@Override
	public void setAffectedElement(Element newAffectedElement)
	{
		super.setAffectedElement(newAffectedElement);
	}

} //UnsetFeatureImpl
