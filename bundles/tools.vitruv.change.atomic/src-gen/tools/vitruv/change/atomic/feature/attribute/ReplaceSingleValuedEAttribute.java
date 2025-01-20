/**
 */
package tools.vitruv.change.atomic.feature.attribute;

import org.eclipse.emf.ecore.EAttribute;

import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Replace Single Valued EAttribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * EChange which replaces the value of the single valued attribute.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage#getReplaceSingleValuedEAttribute()
 * @model ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface ReplaceSingleValuedEAttribute<Element extends Object, Value extends Object> extends ReplaceSingleValuedFeatureEChange<Element, EAttribute, Value>, AdditiveAttributeEChange<Element, Value>, SubtractiveAttributeEChange<Element, Value>
{
} // ReplaceSingleValuedEAttribute
