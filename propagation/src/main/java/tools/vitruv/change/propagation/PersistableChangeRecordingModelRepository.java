package tools.vitruv.change.propagation;

/**
 * A repository for models that records changes to the models. The repository can be used to
 * propagate changes to the models in the repository.
 */
public interface PersistableChangeRecordingModelRepository extends ChangeRecordingModelRepository {
  /**
   * Registers an observer for model persistence.
   *
   * @param observer - {@link ModelsPersistenceObserver}
   */
  void registerModelPersistanceObserver(ModelsPersistenceObserver observer);

  /**
   * Deregisters an observer for model persistence.
   *
   * @param observer - {@link ModelsPersistenceObserver}
   */
  void deregisterModelPersistanceObserver(ModelsPersistenceObserver observer);

  /**
   * Loads existing models into the repository. The realization of this functionality depends on the
   * implementation.
   */
  void loadExistingModels();

  /**
   * Saves all models in this repository to their URIs and deletes those models that are empty,
   * i.e., have no contents.
   */
  void saveOrDeleteModels();
}
