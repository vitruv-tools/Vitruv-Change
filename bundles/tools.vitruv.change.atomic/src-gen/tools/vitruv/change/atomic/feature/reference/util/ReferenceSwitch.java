/**
 */
package tools.vitruv.change.atomic.feature.reference.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.util.Switch;

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
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see tools.vitruv.change.atomic.feature.reference.ReferencePackage
 * @generated
 */
public class ReferenceSwitch<T> extends Switch<T>
{
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ReferencePackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceSwitch()
	{
		if (modelPackage == null)
		{
			modelPackage = ReferencePackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage)
	{
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject)
	{
		switch (classifierID)
		{
			case ReferencePackage.UPDATE_REFERENCE_ECHANGE:
			{
				UpdateReferenceEChange<?> updateReferenceEChange = (UpdateReferenceEChange<?>)theEObject;
				T result = caseUpdateReferenceEChange(updateReferenceEChange);
				if (result == null) result = caseFeatureEChange(updateReferenceEChange);
				if (result == null) result = caseEChange(updateReferenceEChange);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ReferencePackage.ADDITIVE_REFERENCE_ECHANGE:
			{
				AdditiveReferenceEChange<?> additiveReferenceEChange = (AdditiveReferenceEChange<?>)theEObject;
				T result = caseAdditiveReferenceEChange(additiveReferenceEChange);
				if (result == null) result = caseUpdateReferenceEChange(additiveReferenceEChange);
				if (result == null) result = caseEObjectAddedEChange(additiveReferenceEChange);
				if (result == null) result = caseFeatureEChange(additiveReferenceEChange);
				if (result == null) result = caseAdditiveEChange(additiveReferenceEChange);
				if (result == null) result = caseEChange(additiveReferenceEChange);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ReferencePackage.SUBTRACTIVE_REFERENCE_ECHANGE:
			{
				SubtractiveReferenceEChange<?> subtractiveReferenceEChange = (SubtractiveReferenceEChange<?>)theEObject;
				T result = caseSubtractiveReferenceEChange(subtractiveReferenceEChange);
				if (result == null) result = caseUpdateReferenceEChange(subtractiveReferenceEChange);
				if (result == null) result = caseEObjectSubtractedEChange(subtractiveReferenceEChange);
				if (result == null) result = caseFeatureEChange(subtractiveReferenceEChange);
				if (result == null) result = caseSubtractiveEChange(subtractiveReferenceEChange);
				if (result == null) result = caseEChange(subtractiveReferenceEChange);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ReferencePackage.INSERT_EREFERENCE:
			{
				InsertEReference<?> insertEReference = (InsertEReference<?>)theEObject;
				T result = caseInsertEReference(insertEReference);
				if (result == null) result = caseInsertInListEChange(insertEReference);
				if (result == null) result = caseAdditiveReferenceEChange(insertEReference);
				if (result == null) result = caseUpdateSingleListEntryEChange(insertEReference);
				if (result == null) result = caseUpdateReferenceEChange(insertEReference);
				if (result == null) result = caseEObjectAddedEChange(insertEReference);
				if (result == null) result = caseUpdateMultiValuedFeatureEChange(insertEReference);
				if (result == null) result = caseAdditiveEChange(insertEReference);
				if (result == null) result = caseFeatureEChange(insertEReference);
				if (result == null) result = caseEChange(insertEReference);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ReferencePackage.REMOVE_EREFERENCE:
			{
				RemoveEReference<?> removeEReference = (RemoveEReference<?>)theEObject;
				T result = caseRemoveEReference(removeEReference);
				if (result == null) result = caseRemoveFromListEChange(removeEReference);
				if (result == null) result = caseSubtractiveReferenceEChange(removeEReference);
				if (result == null) result = caseUpdateSingleListEntryEChange(removeEReference);
				if (result == null) result = caseUpdateReferenceEChange(removeEReference);
				if (result == null) result = caseEObjectSubtractedEChange(removeEReference);
				if (result == null) result = caseUpdateMultiValuedFeatureEChange(removeEReference);
				if (result == null) result = caseSubtractiveEChange(removeEReference);
				if (result == null) result = caseFeatureEChange(removeEReference);
				if (result == null) result = caseEChange(removeEReference);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ReferencePackage.REPLACE_SINGLE_VALUED_EREFERENCE:
			{
				ReplaceSingleValuedEReference<?> replaceSingleValuedEReference = (ReplaceSingleValuedEReference<?>)theEObject;
				T result = caseReplaceSingleValuedEReference(replaceSingleValuedEReference);
				if (result == null) result = caseReplaceSingleValuedFeatureEChange(replaceSingleValuedEReference);
				if (result == null) result = caseAdditiveReferenceEChange(replaceSingleValuedEReference);
				if (result == null) result = caseSubtractiveReferenceEChange(replaceSingleValuedEReference);
				if (result == null) result = caseUpdateSingleValuedFeatureEChange(replaceSingleValuedEReference);
				if (result == null) result = caseUpdateReferenceEChange(replaceSingleValuedEReference);
				if (result == null) result = caseEObjectAddedEChange(replaceSingleValuedEReference);
				if (result == null) result = caseEObjectSubtractedEChange(replaceSingleValuedEReference);
				if (result == null) result = caseFeatureEChange(replaceSingleValuedEReference);
				if (result == null) result = caseAdditiveEChange(replaceSingleValuedEReference);
				if (result == null) result = caseSubtractiveEChange(replaceSingleValuedEReference);
				if (result == null) result = caseEChange(replaceSingleValuedEReference);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Update Reference EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Update Reference EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseUpdateReferenceEChange(UpdateReferenceEChange<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Additive Reference EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Additive Reference EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseAdditiveReferenceEChange(AdditiveReferenceEChange<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Subtractive Reference EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Subtractive Reference EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseSubtractiveReferenceEChange(SubtractiveReferenceEChange<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Insert EReference</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Insert EReference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseInsertEReference(InsertEReference<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Remove EReference</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Remove EReference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseRemoveEReference(RemoveEReference<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Replace Single Valued EReference</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Replace Single Valued EReference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseReplaceSingleValuedEReference(ReplaceSingleValuedEReference<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseEChange(EChange<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Feature extends EStructuralFeature> T caseFeatureEChange(FeatureEChange<Element, Feature> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Additive EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Additive EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Value extends Object> T caseAdditiveEChange(AdditiveEChange<Element, Value> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject Added EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject Added EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseEObjectAddedEChange(EObjectAddedEChange<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Subtractive EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Subtractive EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Value extends Object> T caseSubtractiveEChange(SubtractiveEChange<Element, Value> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject Subtracted EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject Subtracted EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object> T caseEObjectSubtractedEChange(EObjectSubtractedEChange<Element> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Update Multi Valued Feature EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Update Multi Valued Feature EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Feature extends EStructuralFeature> T caseUpdateMultiValuedFeatureEChange(UpdateMultiValuedFeatureEChange<Element, Feature> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Update Single List Entry EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Update Single List Entry EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Feature extends EStructuralFeature> T caseUpdateSingleListEntryEChange(UpdateSingleListEntryEChange<Element, Feature> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Insert In List EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Insert In List EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Feature extends EStructuralFeature, Value extends Object> T caseInsertInListEChange(InsertInListEChange<Element, Feature, Value> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Remove From List EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Remove From List EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Feature extends EStructuralFeature, Value extends Object> T caseRemoveFromListEChange(RemoveFromListEChange<Element, Feature, Value> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Update Single Valued Feature EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Update Single Valued Feature EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Feature extends EStructuralFeature> T caseUpdateSingleValuedFeatureEChange(UpdateSingleValuedFeatureEChange<Element, Feature> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Replace Single Valued Feature EChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Replace Single Valued Feature EChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <Element extends Object, Feature extends EStructuralFeature, Value extends Object> T caseReplaceSingleValuedFeatureEChange(ReplaceSingleValuedFeatureEChange<Element, Feature, Value> object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object)
	{
		return null;
	}

} //ReferenceSwitch
