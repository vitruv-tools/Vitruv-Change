package tools.vitruv.change.propagation;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * A snapshot of a model repository, essentially a copy of the repository's models and
 * correspondence model at some point.
 */
public interface ModelRepositorySnapshot extends ResourceAccess, AutoCloseable {
  /**
   * Get an editable view of the correspondence model.
   *
   * @return the correspondence model
   */
  EditableCorrespondenceModelView<Correspondence> getCorrespondenceModel();

  /**
   * Get a map that maps the original repository objects to the related snapshot objects.
   *
   * @return the immutable map
   */
  Map<EObject, EObject> getRepositoryToSnapshotMap();

  /**
   * Get a map that maps the snapshot objects to the related, original repository objects.
   *
   * @return the immutable map
   */
  Map<EObject, EObject> getSnapshotToRepositoryMap();
}
