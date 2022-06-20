package tools.vitruv.change.correspondence.impl

import tools.vitruv.change.correspondence.Correspondence
import tools.vitruv.change.correspondence.CorrespondenceModel
import tools.vitruv.change.correspondence.InternalCorrespondenceModel
import tools.vitruv.change.correspondence.CorrespondenceFactory

class GenericCorrespondenceModelViewImpl extends CorrespondenceModelViewImpl<Correspondence> implements CorrespondenceModel {
	
	new(InternalCorrespondenceModel correspondenceModel) {
		// FIXME Finally the CorrespondenceModel should not be editable
		super(Correspondence, correspondenceModel, [CorrespondenceFactory.eINSTANCE.createManualCorrespondence]);
	}
	
}