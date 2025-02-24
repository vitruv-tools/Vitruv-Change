package tools.vitruv.change.propagation

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.common.util.EList
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.correspondence.Correspondence
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView
import tools.vitruv.change.utils.ResourceAccess


interface ChangeMultiPropagationSpecification extends ChangePropagationSpecification {
    	/**
	 * Returns whether this <code>ChangePropagationSpecification</code> handles the given change, i.e.,
	 * whether {@link #propagateChange} will perform changes when applied to that change.
	 * 
	 * @param changeList			the list of atomic changes for which to check whether this specification reacts 
	 * 								to it. Must not be <code>null</code>.
	 * @param correspondenceModel	the correspondence model to retrieve information about the target
	 * 								model from. Must not be <code>null</code>.
	 * @return	<code>true</code> if {@link #propagateChange} will perform modifications in response to the
	 * 			given change, <code>false</code> otherwise
	 */
    def boolean doesHandleChange(EList<EChange<EObject>> changeList, EditableCorrespondenceModelView<Correspondence> correspondenceModel)

    	/**
	 * Performs modifications in target models identified by accessing the given <code>CorrespondenceModel</code>
	 * for the elements changed by the given <code>EChange</code> in order to reflect the changes in the
	 * target model.
	 * 
	 * @param changeList 			the list of atomic changes which shall be propagated. Should affect only elements in 
	 * 								an instance of a source metamodel of this specification (see 
	 * 								{@link #getSourceMetamodelDescriptor}). Must not be <code>null</code>.
	 * @param correspondenceModel 	the correspondence model to retrieve information about the target
	 * 								model from. Must not be <code>null</code>.
	 * @param resourceAccess		an object for resource access, in particular to create new model files.
	 * 								Must not be <code>null</code>.
	 */
	def void propagateChange(EList<EChange<EObject>> changeList, EditableCorrespondenceModelView<Correspondence> correspondenceModel, ResourceAccess resourceAccess)
}