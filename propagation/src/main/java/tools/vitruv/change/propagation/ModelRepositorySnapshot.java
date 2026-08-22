package tools.vitruv.change.propagation;

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
}
