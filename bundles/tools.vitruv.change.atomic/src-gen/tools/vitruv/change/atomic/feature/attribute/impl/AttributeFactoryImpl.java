/**
 */
package tools.vitruv.change.atomic.feature.attribute.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import tools.vitruv.change.atomic.feature.attribute.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AttributeFactoryImpl extends EFactoryImpl implements AttributeFactory
{
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static AttributeFactory init()
	{
		try
		{
			AttributeFactory theAttributeFactory = (AttributeFactory)EPackage.Registry.INSTANCE.getEFactory(AttributePackage.eNS_URI);
			if (theAttributeFactory != null)
			{
				return theAttributeFactory;
			}
		}
		catch (Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new AttributeFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeFactoryImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass)
	{
		switch (eClass.getClassifierID())
		{
			case AttributePackage.INSERT_EATTRIBUTE_VALUE: return createInsertEAttributeValue();
			case AttributePackage.REMOVE_EATTRIBUTE_VALUE: return createRemoveEAttributeValue();
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE: return createReplaceSingleValuedEAttribute();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public <Element extends Object, Value extends Object> InsertEAttributeValue<Element, Value> createInsertEAttributeValue()
	{
		InsertEAttributeValueImpl<Element, Value> insertEAttributeValue = new InsertEAttributeValueImpl<Element, Value>();
		return insertEAttributeValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public <Element extends Object, Value extends Object> RemoveEAttributeValue<Element, Value> createRemoveEAttributeValue()
	{
		RemoveEAttributeValueImpl<Element, Value> removeEAttributeValue = new RemoveEAttributeValueImpl<Element, Value>();
		return removeEAttributeValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public <Element extends Object, Value extends Object> ReplaceSingleValuedEAttribute<Element, Value> createReplaceSingleValuedEAttribute()
	{
		ReplaceSingleValuedEAttributeImpl<Element, Value> replaceSingleValuedEAttribute = new ReplaceSingleValuedEAttributeImpl<Element, Value>();
		return replaceSingleValuedEAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributePackage getAttributePackage()
	{
		return (AttributePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static AttributePackage getPackage()
	{
		return AttributePackage.eINSTANCE;
	}

} //AttributeFactoryImpl
