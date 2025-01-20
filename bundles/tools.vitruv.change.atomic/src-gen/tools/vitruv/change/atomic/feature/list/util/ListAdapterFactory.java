/**
 */
package tools.vitruv.change.atomic.feature.list.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.SubtractiveEChange;

import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange;

import tools.vitruv.change.atomic.feature.list.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.feature.list.ListPackage
 * @generated
 */
public class ListAdapterFactory extends AdapterFactoryImpl
{
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ListPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ListAdapterFactory()
	{
		if (modelPackage == null)
		{
			modelPackage = ListPackage.eINSTANCE;
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
	protected ListSwitch<Adapter> modelSwitch =
		new ListSwitch<Adapter>()
		{
			@Override
			public <Element extends Object, Feature extends EStructuralFeature> Adapter caseUpdateSingleListEntryEChange(UpdateSingleListEntryEChange<Element, Feature> object)
			{
				return createUpdateSingleListEntryEChangeAdapter();
			}
			@Override
			public <Element extends Object, Feature extends EStructuralFeature, Value extends Object> Adapter caseInsertInListEChange(InsertInListEChange<Element, Feature, Value> object)
			{
				return createInsertInListEChangeAdapter();
			}
			@Override
			public <Element extends Object, Feature extends EStructuralFeature, Value extends Object> Adapter caseRemoveFromListEChange(RemoveFromListEChange<Element, Feature, Value> object)
			{
				return createRemoveFromListEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseEChange(EChange<Element> object)
			{
				return createEChangeAdapter();
			}
			@Override
			public <Element extends Object, Feature extends EStructuralFeature> Adapter caseFeatureEChange(FeatureEChange<Element, Feature> object)
			{
				return createFeatureEChangeAdapter();
			}
			@Override
			public <Element extends Object, Feature extends EStructuralFeature> Adapter caseUpdateMultiValuedFeatureEChange(UpdateMultiValuedFeatureEChange<Element, Feature> object)
			{
				return createUpdateMultiValuedFeatureEChangeAdapter();
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
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange <em>Update Single List Entry EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange
	 * @generated
	 */
	public Adapter createUpdateSingleListEntryEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.list.InsertInListEChange <em>Insert In List EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.list.InsertInListEChange
	 * @generated
	 */
	public Adapter createInsertInListEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.list.RemoveFromListEChange <em>Remove From List EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.list.RemoveFromListEChange
	 * @generated
	 */
	public Adapter createRemoveFromListEChangeAdapter()
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
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.FeatureEChange <em>EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.FeatureEChange
	 * @generated
	 */
	public Adapter createFeatureEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange <em>Update Multi Valued Feature EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange
	 * @generated
	 */
	public Adapter createUpdateMultiValuedFeatureEChangeAdapter()
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

} //ListAdapterFactory
