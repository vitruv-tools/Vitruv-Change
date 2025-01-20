/**
 */
package tools.vitruv.change.atomic.feature.attribute.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;

import tools.vitruv.change.atomic.feature.attribute.AttributePackage;
import tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange;

import tools.vitruv.change.atomic.feature.impl.FeatureEChangeImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Update Attribute EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class UpdateAttributeEChangeImpl<Element extends Object> extends FeatureEChangeImpl<Element, EAttribute> implements UpdateAttributeEChange<Element>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UpdateAttributeEChangeImpl()
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
		return AttributePackage.Literals.UPDATE_ATTRIBUTE_ECHANGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * This is specialized for the more specific type known in this context.
	 * @generated
	 */
	@Override
	public void setAffectedFeature(EAttribute newAffectedFeature)
	{
		super.setAffectedFeature(newAffectedFeature);
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

} //UpdateAttributeEChangeImpl
