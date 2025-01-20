/**
 */
package tools.vitruv.change.atomic.feature.list;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.SubtractiveEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Remove From List EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which removes a value from an EList.
 * <!-- end-model-doc -->
 *
 *
 * @see tools.vitruv.change.atomic.feature.list.ListPackage#getRemoveFromListEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject" ValueBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface RemoveFromListEChange<Element extends Object, Feature extends EStructuralFeature, Value extends Object> extends UpdateSingleListEntryEChange<Element, Feature>, SubtractiveEChange<Element, Value>
{
} // RemoveFromListEChange
