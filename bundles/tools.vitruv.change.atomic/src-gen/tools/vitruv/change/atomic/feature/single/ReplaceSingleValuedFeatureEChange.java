/**
 */
package tools.vitruv.change.atomic.feature.single;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.SubtractiveEChange;

import tools.vitruv.change.atomic.feature.UpdateSingleValuedFeatureEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Replace Single Valued Feature EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which replaces a single valued attribute or reference.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange#isIsUnset <em>Is Unset</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.atomic.feature.single.SinglePackage#getReplaceSingleValuedFeatureEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface ReplaceSingleValuedFeatureEChange<Element extends Object, Feature extends EStructuralFeature, Value extends Object> extends UpdateSingleValuedFeatureEChange<Element, Feature>, AdditiveEChange<Element, Value>, SubtractiveEChange<Element, Value>
{
	/**
	 * Returns the value of the '<em><b>Is Unset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Unset</em>' attribute.
	 * @see #setIsUnset(boolean)
	 * @see tools.vitruv.change.atomic.feature.single.SinglePackage#getReplaceSingleValuedFeatureEChange_IsUnset()
	 * @model
	 * @generated
	 */
	boolean isIsUnset();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange#isIsUnset <em>Is Unset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Unset</em>' attribute.
	 * @see #isIsUnset()
	 * @generated
	 */
	void setIsUnset(boolean value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * The change don't replace the default value.
	 * @return The change don't replace the default value.
	 * <!-- end-model-doc -->
	 * @model kind="operation" unique="false"
	 * @generated
	 */
	boolean isFromNonDefaultValue();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * The change don't replaces a value with the default value.
	 * @return The change don't replaces a value with the default value.
	 * <!-- end-model-doc -->
	 * @model kind="operation" unique="false"
	 * @generated
	 */
	boolean isToNonDefaultValue();

} // ReplaceSingleValuedFeatureEChange
