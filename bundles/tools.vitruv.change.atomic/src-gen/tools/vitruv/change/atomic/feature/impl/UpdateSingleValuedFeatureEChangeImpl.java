/**
 */
package tools.vitruv.change.atomic.feature.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.feature.FeaturePackage;
import tools.vitruv.change.atomic.feature.UpdateSingleValuedFeatureEChange;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Update Single Valued Feature EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class UpdateSingleValuedFeatureEChangeImpl<Element extends Object, Feature extends EStructuralFeature> extends FeatureEChangeImpl<Element, Feature> implements UpdateSingleValuedFeatureEChange<Element, Feature>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UpdateSingleValuedFeatureEChangeImpl()
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
		return FeaturePackage.Literals.UPDATE_SINGLE_VALUED_FEATURE_ECHANGE;
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

} //UpdateSingleValuedFeatureEChangeImpl
