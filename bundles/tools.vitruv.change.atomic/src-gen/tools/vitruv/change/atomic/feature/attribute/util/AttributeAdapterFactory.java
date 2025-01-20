/**
 */
package tools.vitruv.change.atomic.feature.attribute.util;

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
import tools.vitruv.change.atomic.feature.UpdateSingleValuedFeatureEChange;

import tools.vitruv.change.atomic.feature.attribute.*;

import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;
import tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange;

import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage
 * @generated
 */
public class AttributeAdapterFactory extends AdapterFactoryImpl
{
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static AttributePackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeAdapterFactory()
	{
		if (modelPackage == null)
		{
			modelPackage = AttributePackage.eINSTANCE;
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
	protected AttributeSwitch<Adapter> modelSwitch =
		new AttributeSwitch<Adapter>()
		{
			@Override
			public <Element extends Object> Adapter caseUpdateAttributeEChange(UpdateAttributeEChange<Element> object)
			{
				return createUpdateAttributeEChangeAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseAdditiveAttributeEChange(AdditiveAttributeEChange<Element, Value> object)
			{
				return createAdditiveAttributeEChangeAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseSubtractiveAttributeEChange(SubtractiveAttributeEChange<Element, Value> object)
			{
				return createSubtractiveAttributeEChangeAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseInsertEAttributeValue(InsertEAttributeValue<Element, Value> object)
			{
				return createInsertEAttributeValueAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseRemoveEAttributeValue(RemoveEAttributeValue<Element, Value> object)
			{
				return createRemoveEAttributeValueAdapter();
			}
			@Override
			public <Element extends Object, Value extends Object> Adapter caseReplaceSingleValuedEAttribute(ReplaceSingleValuedEAttribute<Element, Value> object)
			{
				return createReplaceSingleValuedEAttributeAdapter();
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
			public <Element extends Object, Value extends Object> Adapter caseSubtractiveEChange(SubtractiveEChange<Element, Value> object)
			{
				return createSubtractiveEChangeAdapter();
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
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange <em>Update Attribute EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange
	 * @generated
	 */
	public Adapter createUpdateAttributeEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange <em>Additive Attribute EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange
	 * @generated
	 */
	public Adapter createAdditiveAttributeEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange <em>Subtractive Attribute EChange</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange
	 * @generated
	 */
	public Adapter createSubtractiveAttributeEChangeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue <em>Insert EAttribute Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue
	 * @generated
	 */
	public Adapter createInsertEAttributeValueAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue <em>Remove EAttribute Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue
	 * @generated
	 */
	public Adapter createRemoveEAttributeValueAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute <em>Replace Single Valued EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute
	 * @generated
	 */
	public Adapter createReplaceSingleValuedEAttributeAdapter()
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

} //AttributeAdapterFactory
