package tools.vitruv.change.propagation;

import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.utils.ResourceAccess;

import java.util.Optional;

public interface ModelSnapshot extends ResourceAccess, AutoCloseable {
    EditableCorrespondenceModelView<Correspondence> getCorrespondenceModel();

    Optional<EObject> getRepositoryEObject(EObject snapshotEObject);

    Optional<EObject> getSnapshotEObject(EObject repositoryEObject);

    void registerEObjectMapping(EObject repositoryEObject, EObject snapshotEObject);
}
