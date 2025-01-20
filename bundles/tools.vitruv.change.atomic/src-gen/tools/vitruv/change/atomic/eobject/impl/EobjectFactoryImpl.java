/**
 */
package tools.vitruv.change.atomic.eobject.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import tools.vitruv.change.atomic.eobject.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EobjectFactoryImpl extends EFactoryImpl implements EobjectFactory
{
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EobjectFactory init()
	{
		try
		{
			EobjectFactory theEobjectFactory = (EobjectFactory)EPackage.Registry.INSTANCE.getEFactory(EobjectPackage.eNS_URI);
			if (theEobjectFactory != null)
			{
				return theEobjectFactory;
			}
		}
		catch (Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new EobjectFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EobjectFactoryImpl()
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
			case EobjectPackage.CREATE_EOBJECT: return createCreateEObject();
			case EobjectPackage.DELETE_EOBJECT: return createDeleteEObject();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public <Element extends Object> CreateEObject<Element> createCreateEObject()
	{
		CreateEObjectImpl<Element> createEObject = new CreateEObjectImpl<Element>();
		return createEObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public <Element extends Object> DeleteEObject<Element> createDeleteEObject()
	{
		DeleteEObjectImpl<Element> deleteEObject = new DeleteEObjectImpl<Element>();
		return deleteEObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EobjectPackage getEobjectPackage()
	{
		return (EobjectPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static EobjectPackage getPackage()
	{
		return EobjectPackage.eINSTANCE;
	}

} //EobjectFactoryImpl
