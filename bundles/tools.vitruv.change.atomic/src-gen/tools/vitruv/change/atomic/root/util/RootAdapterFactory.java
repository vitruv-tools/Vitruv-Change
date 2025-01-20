/**
 */
package tools.vitruv.change.atomic.root.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.SubtractiveEChange;

import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;

import tools.vitruv.change.atomic.root.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.root.RootPackage
 * @generated
 */
public class RootAdapterFactory extends AdapterFactoryImpl
{
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static RootPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RootAdapterFactory()
	{
		if (modelPackage == null)
		{
			modelPackage = RootPackage.eINSTANCE;
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
	protected RootSwitch<Adapter> modelSwitch =
		new RootSwitch<Adapter>()
		{
			@Override
			public <Element extends Object> Adapter caseRootEChange(RootEChange<Element> object)
			{
				return createRootEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseInsertRootEObject(InsertRootEObject<Element> object)
			{
				return createInsertRootEObjectAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseRemoveRootEObject(RemoveRootEObject<Element> object)
			{
				return createRemoveRootEObjectAdapter();
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
			public <Element extends Object> Adapter caseEObjectAddedEChange(EObjectAddedEChange<Element> object)
			{
				return createEObjectAddedEChangeAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseSubtractiveEChange(SubtractiveEChange<Element, Value> object)
			{
				return createSubtractiveEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseEObjectSubtractedEChange(EObjectSubtractedEChange<Element> object)
			{
				return createEObjectSubtractedEChangeAdapter();
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
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.root.RootEChange <em>EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.root.RootEChange
	 * @generated
	 */
	public Adapter createRootEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.root.InsertRootEObject <em>Insert Root EObject</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.root.InsertRootEObject
	 * @generated
	 */
	public Adapter createInsertRootEObjectAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.root.RemoveRootEObject <em>Remove Root EObject</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.root.RemoveRootEObject
	 * @generated
	 */
	public Adapter createRemoveRootEObjectAdapter()
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

} //RootAdapterFactory
