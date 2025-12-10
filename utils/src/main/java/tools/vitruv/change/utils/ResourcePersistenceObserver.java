package tools.vitruv.change.utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * An observer that can be informed about I/O events that occur while changing
 * the models of a {@link PersistableChangeRecordingModelRepository}. 
 */
public interface ResourcePersistenceObserver {
  /**
   * Informs this observer that a resource at <code>uri</code> will be loaded.
   *
   * @param uri - {@link URI}
   */
  void startLoadingResource(final URI uri);

  /**
   * Informs this observer that <code>resource</code> has been loaded.
   *
   * @param resource - {@link Resource}
   */
  void finishLoadingResource(final Resource resource);

  /**
   * Informs this observer that a resource will be saved.
   *
   * @param resource - {@link Resource}
   */
  void startSavingResource(final Resource resource);

  /**
   * Informs this observer that <code>resource</code> has been saved.
   *
   * @param resource - {@link Resource}
   */
  void finishSavingResource(final Resource resource);

  /**
   * Informs this observer that a resource will be deleted.
   *
   * @param resource - {@link Resource}
   */
  void startDeletingResource(final Resource resource);

  /**
   * Informs this observer that <code>resource</code> has been deleted.
   *
   * @param resource - {@link Resource}
   */
  void finishDeletingResource(final Resource resource); 
}
