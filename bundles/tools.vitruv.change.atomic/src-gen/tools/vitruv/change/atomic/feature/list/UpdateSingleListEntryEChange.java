/**
 */
package tools.vitruv.change.atomic.feature.list;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Update Single List Entry EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which changes an EList.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange#getIndex <em>Index</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.atomic.feature.list.ListPackage#getUpdateSingleListEntryEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface UpdateSingleListEntryEChange<Element extends Object, Feature extends EStructuralFeature> extends UpdateMultiValuedFeatureEChange<Element, Feature>
{
	/**
	 * Returns the value of the '<em><b>Index</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Index</em>' attribute.
	 * @see #setIndex(int)
	 * @see tools.vitruv.change.atomic.feature.list.ListPackage#getUpdateSingleListEntryEChange_Index()
	 * @model default="0" unique="false" required="true"
	 * @generated
	 */
	int getIndex();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange#getIndex <em>Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Index</em>' attribute.
	 * @see #getIndex()
	 * @generated
	 */
	void setIndex(int value);

} // UpdateSingleListEntryEChange
