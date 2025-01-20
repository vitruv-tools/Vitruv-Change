/**
 */
package tools.vitruv.change.atomic.feature.attribute;

import org.eclipse.emf.ecore.EAttribute;

import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Remove EAttribute Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * EChange which removes the value of a many valued attribute.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage#getRemoveEAttributeValue()
 * @model ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface RemoveEAttributeValue<Element extends Object, Value extends Object> extends RemoveFromListEChange<Element, EAttribute, Value>, SubtractiveAttributeEChange<Element, Value>
{
} // RemoveEAttributeValue
