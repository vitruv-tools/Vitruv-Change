/**
 */
package tools.vitruv.change.atomic.feature.attribute;

import org.eclipse.emf.ecore.EAttribute;

import tools.vitruv.change.atomic.feature.list.InsertInListEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Insert EAttribute Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * EChange which inserts a new value into a many valued attribute.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage#getInsertEAttributeValue()
 * @model ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface InsertEAttributeValue<Element extends Object, Value extends Object> extends InsertInListEChange<Element, EAttribute, Value>, AdditiveAttributeEChange<Element, Value>
{
} // InsertEAttributeValue
