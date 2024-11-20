package tools.vitruv.change.propagation;

public interface PersistableChangeRecordingModelRepository extends ChangeRecordingModelRepository {
	/**
	 * Loads existing models into the repository. The realization of this functionality depends on the implementation.
	 */
	void loadExistingModels();

	/**
	 * Saves all models in this repository to their URIs and deletes those models that are empty, i.e., have no contents.
	 */
	void saveOrDeleteModels();
}
