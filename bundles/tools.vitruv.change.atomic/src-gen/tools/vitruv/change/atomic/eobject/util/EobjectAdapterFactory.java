/**
 */
package tools.vitruv.change.atomic.eobject.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.SubtractiveEChange;

import tools.vitruv.change.atomic.eobject.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.eobject.EobjectPackage
 * @generated
 */
public class EobjectAdapterFactory extends AdapterFactoryImpl
{
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static EobjectPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EobjectAdapterFactory()
	{
		if (modelPackage == null)
		{
			modelPackage = EobjectPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object)
	{
		if (object == modelPackage)
		{
			return true;
		}
		if (object instanceof EObject)
		{
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EobjectSwitch<Adapter> modelSwitch =
		new EobjectSwitch<Adapter>()
		{
			@Override
			public <Element extends Object> Adapter caseEObjectAddedEChange(EObjectAddedEChange<Element> object)
			{
				return createEObjectAddedEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseEObjectSubtractedEChange(EObjectSubtractedEChange<Element> object)
			{
				return createEObjectSubtractedEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseEObjectExistenceEChange(EObjectExistenceEChange<Element> object)
			{
				return createEObjectExistenceEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseCreateEObject(CreateEObject<Element> object)
			{
				return createCreateEObjectAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseDeleteEObject(DeleteEObject<Element> object)
			{
				return createDeleteEObjectAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseEChange(EChange<Element> object)
			{
				return createEChangeAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseAdditiveEChange(AdditiveEChange<Element, Value> object)
			{
				return createAdditiveEChangeAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseSubtractiveEChange(SubtractiveEChange<Element, Value> object)
			{
				return createSubtractiveEChangeAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object)
			{
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target)
	{
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.eobject.EObjectAddedEChange <em>EObject Added EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.eobject.EObjectAddedEChange
	 * @generated
	 */
	public Adapter createEObjectAddedEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange <em>EObject Subtracted EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
	 * @generated
	 */
	public Adapter createEObjectSubtractedEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.eobject.EObjectExistenceEChange <em>EObject Existence EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
	 * @generated
	 */
	public Adapter createEObjectExistenceEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.eobject.CreateEObject <em>Create EObject</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.eobject.CreateEObject
	 * @generated
	 */
	public Adapter createCreateEObjectAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.eobject.DeleteEObject <em>Delete EObject</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.eobject.DeleteEObject
	 * @generated
	 */
	public Adapter createDeleteEObjectAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.EChange <em>EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.EChange
	 * @generated
	 */
	public Adapter createEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.AdditiveEChange <em>Additive EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.AdditiveEChange
	 * @generated
	 */
	public Adapter createAdditiveEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.SubtractiveEChange <em>Subtractive EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.SubtractiveEChange
	 * @generated
	 */
	public Adapter createSubtractiveEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter()
	{
		return null;
	}

} //EobjectAdapterFactory
