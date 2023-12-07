package tools.vitruv.change.correspondence.view;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.CorrespondenceFactory;
import tools.vitruv.change.correspondence.model.CorrespondenceModel;

public final class CorrespondenceModelViewFactory {
	public CorrespondenceModelViewFactory() {
		
	}
	
	public static CorrespondenceModelView<Correspondence> createCorrespondenceModelView(CorrespondenceModel correspondenceModel) {
		return new CorrespondenceModelViewImpl<Correspondence>(correspondenceModel, Correspondence.class);
	}

	public static EditableCorrespondenceModelView<Correspondence> createEditableCorrespondenceModelView(
			CorrespondenceModel correspondenceModel) {
		return new EditableCorrespondenceModelViewImpl<>(correspondenceModel, Correspondence.class,
				() -> CorrespondenceFactory.eINSTANCE.createManualCorrespondence());
	}
}
