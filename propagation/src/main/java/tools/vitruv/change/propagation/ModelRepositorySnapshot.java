package tools.vitruv.change.propagation;

import java.util.Optional;
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
   * Map from an object from this snapshot to the equivalent object in the original repository.
   * This mapping does not apply to correspondence objects themselves.
   *
   * @param snapshotEObject the object in this snapshot
   * @return the object in the original repository, or empty if no mapping exists
   */
  Optional<EObject> getRepositoryEObject(EObject snapshotEObject);

  /**
   * Map from an object in the original repository to the equivalent object in this snapshot.
   * This mapping does not apply to correspondence objects themselves.
   *
   * @param repositoryEObject the object in the original repository
   * @return the object in this snapshot, or empty if no mapping exists
   */
  Optional<EObject> getSnapshotEObject(EObject repositoryEObject);
}
