/**
 */
package tools.vitruv.change.atomic.feature.list;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.AdditiveEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Insert In List EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which inserts a value into an EList.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.list.ListPackage#getInsertInListEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface InsertInListEChange<Element extends Object, Feature extends EStructuralFeature, Value extends Object> extends UpdateSingleListEntryEChange<Element, Feature>, AdditiveEChange<Element, Value>
{
} // InsertInListEChange
