package tools.vitruv.change.correspondence.model;

import org.eclipse.emf.common.util.URI;

public final class CorrespondenceModelFactory {
	private CorrespondenceModelFactory() {
	}

	public static PersistableCorrespondenceModel createPersistableCorrespondenceModel(URI resourceUri) {
		return new PersistableCorrespondenceModelImpl(resourceUri);
	}

}
