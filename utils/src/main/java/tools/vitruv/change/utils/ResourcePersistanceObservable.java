package tools.vitruv.change.utils;

/**
 * An observable that modifies and persists EMF {@link Resource}s.
 */
public interface ResourcePersistanceObservable {
  /**
   * Registers an observer for model persistence.
   *
   * @param observer - {@link ResourcePersistenceObserver}
   */
  void registerModelPersistanceObserver(ResourcePersistenceObserver observer);

  /**
   * Deregisters an observer for model persistence.
   *
   * @param observer - {@link ResourcePersistenceObserver}
   */
  void deregisterModelPersistanceObserver(ResourcePersistenceObserver observer);

}