/**
 */
package tools.vitruv.change.atomic.feature.reference.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.SubtractiveEChange;

import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;

import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange;
import tools.vitruv.change.atomic.feature.UpdateSingleValuedFeatureEChange;

import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;
import tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange;

import tools.vitruv.change.atomic.feature.reference.*;

import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.feature.reference.ReferencePackage
 * @generated
 */
public class ReferenceAdapterFactory extends AdapterFactoryImpl
{
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ReferencePackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceAdapterFactory()
	{
		if (modelPackage == null)
		{
			modelPackage = ReferencePackage.eINSTANCE;
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
	protected ReferenceSwitch<Adapter> modelSwitch =
		new ReferenceSwitch<Adapter>()
		{
			@Override
			public <Element extends Object> Adapter caseUpdateReferenceEChange(UpdateReferenceEChange<Element> object)
			{
				return createUpdateReferenceEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseAdditiveReferenceEChange(AdditiveReferenceEChange<Element> object)
			{
				return createAdditiveReferenceEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseSubtractiveReferenceEChange(SubtractiveReferenceEChange<Element> object)
			{
				return createSubtractiveReferenceEChangeAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseInsertEReference(InsertEReference<Element> object)
			{
				return createInsertEReferenceAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseRemoveEReference(RemoveEReference<Element> object)
			{
				return createRemoveEReferenceAdapter();
			}
			@Override
			public <Element extends Object> Adapter caseReplaceSingleValuedEReference(ReplaceSingleValuedEReference<Element> object)
			{
				return createReplaceSingleValuedEReferenceAdapter();
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
			public <Element extends Object, Feature extends EStructuralFeature> Adapter caseUpdateMultiValuedFeatureEChange(UpdateMultiValuedFeatureEChange<Element, Feature> object)
			{
				return createUpdateMultiValuedFeatureEChangeAdapter();
			}
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
			public <Element extends Object, Feature extends EStructuralFeature> Adapter caseUpdateSingleValuedFeatureEChange(UpdateSingleValuedFeatureEChange<Element, Feature> object)
			{
				return createUpdateSingleValuedFeatureEChangeAdapter();
			}
			@Override
			public <Element extends Object, Feature extends EStructuralFeature, Value extends Object> Adapter caseReplaceSingleValuedFeatureEChange(ReplaceSingleValuedFeatureEChange<Element, Feature, Value> object)
			{
				return createReplaceSingleValuedFeatureEChangeAdapter();
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
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange <em>Update Reference EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange
	 * @generated
	 */
	public Adapter createUpdateReferenceEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange <em>Additive Reference EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange
	 * @generated
	 */
	public Adapter createAdditiveReferenceEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange <em>Subtractive Reference EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange
	 * @generated
	 */
	public Adapter createSubtractiveReferenceEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.reference.InsertEReference <em>Insert EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.reference.InsertEReference
	 * @generated
	 */
	public Adapter createInsertEReferenceAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.reference.RemoveEReference <em>Remove EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.reference.RemoveEReference
	 * @generated
	 */
	public Adapter createRemoveEReferenceAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference <em>Replace Single Valued EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference
	 * @generated
	 */
	public Adapter createReplaceSingleValuedEReferenceAdapter()
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
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.UpdateSingleValuedFeatureEChange <em>Update Single Valued Feature EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.UpdateSingleValuedFeatureEChange
	 * @generated
	 */
	public Adapter createUpdateSingleValuedFeatureEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange <em>Replace Single Valued Feature EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange
	 * @generated
	 */
	public Adapter createReplaceSingleValuedFeatureEChangeAdapter()
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

} //ReferenceAdapterFactory
