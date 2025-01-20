/**
 */
package tools.vitruv.change.interaction;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Multiple Choice Multi Selection User Interaction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.interaction.MultipleChoiceMultiSelectionUserInteraction#getSelectedIndices <em>Selected Indices</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.interaction.InteractionPackage#getMultipleChoiceMultiSelectionUserInteraction()
 * @model
 * @generated
 */
public interface MultipleChoiceMultiSelectionUserInteraction extends MultipleChoiceSelectionInteractionBase
{
	/**
	 * Returns the value of the '<em><b>Selected Indices</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Selected Indices</em>' attribute list.
	 * @see tools.vitruv.change.interaction.InteractionPackage#getMultipleChoiceMultiSelectionUserInteraction_SelectedIndices()
	 * @model
	 * @generated
	 */
	EList<Integer> getSelectedIndices();

} // MultipleChoiceMultiSelectionUserInteraction
