package tools.vitruv.change.propagation.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceCopier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;
import tools.vitruv.change.correspondence.view.CorrespondenceModelViewFactory;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.ModelSnapshot;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;

public class DefaultModelSnapshot implements ModelSnapshot {
  private final ResourceSet resourceSet;
  private final BiMap<EObject, EObject> repositoryToSnapshot;
  private final PersistableCorrespondenceModel correspondenceModel;
  private final Function<String[], URI> metadataModelUriProvider;

  public DefaultModelSnapshot(ResourceSet resourceSet, BiMap<EObject, EObject> repositoryToSnapshot, PersistableCorrespondenceModel correspondenceModel, Function<String[], URI> metadataModelUriProvider) {
    this.resourceSet = resourceSet;
    this.repositoryToSnapshot = repositoryToSnapshot;
    this.correspondenceModel = correspondenceModel;
    this.metadataModelUriProvider = metadataModelUriProvider;
  }

  public static ModelSnapshot copyOf(ResourceSet resourceSet, PersistableCorrespondenceModel correspondenceModel, Function<String[], URI> metadataModelUriProvider) {
    ResourceSetCopy resourceSetCopy = copyResourceSet(resourceSet);
    PersistableCorrespondenceModel correspondenceModelCopy = correspondenceModel.copy(resourceSetCopy.originalToCopy());
    return new DefaultModelSnapshot(resourceSetCopy.resourceSet(), resourceSetCopy.originalToCopy(), correspondenceModelCopy, metadataModelUriProvider);
  }

  private static ResourceSetCopy copyResourceSet(ResourceSet originalResourceSet) {
    ResourceSet copiedResourceSet = withGlobalFactories(new ResourceSetImpl());

    Map<Resource, Resource> resourceCopies = ResourceCopier.copyViewResources(originalResourceSet.getResources(), copiedResourceSet);
    resourceCopies.forEach((original, copy) -> copy.setModified(original.isModified()));

    BiMap<EObject, EObject> mapping = createMapping(originalResourceSet, copiedResourceSet);

    return new ResourceSetCopy(copiedResourceSet, mapping);
  }

  private static BiMap<EObject, EObject> createMapping(ResourceSet originalResourceSet, ResourceSet copiedResourceSet) {
    BiMap<EObject, EObject> result = HashBiMap.create();

    for (Resource originalResource : originalResourceSet.getResources()) {
      for (EObject originalContent : originalResource.getContents()) {
        addMapping(originalContent, copiedResourceSet, result);

        var iterator = originalContent.eAllContents();
        while (iterator.hasNext()) {
          addMapping(iterator.next(), copiedResourceSet, result);
        }
      }
    }

    return result;
  }

  private static void addMapping(EObject original, ResourceSet copiedResourceSet, BiMap<EObject, EObject> mapping) {
    URI sourceUri = EcoreUtil.getURI(original);
    EObject copied = copiedResourceSet.getEObject(sourceUri, false);

    mapping.put(original, copied);
  }

  private record ResourceSetCopy(ResourceSet resourceSet, BiMap<EObject, EObject> originalToCopy) {
  }

  @Override
  public EditableCorrespondenceModelView<Correspondence> getCorrespondenceModel() {
    return CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(correspondenceModel);
  }

  @Override
  public Optional<EObject> getRepositoryEObject(EObject snapshotEObject) {
    return Optional.ofNullable(repositoryToSnapshot.inverse().get(snapshotEObject));
  }

  @Override
  public Optional<EObject> getSnapshotEObject(EObject repositoryEObject) {
    return Optional.ofNullable(repositoryToSnapshot.get(repositoryEObject));
  }

  @Override
  public void registerEObjectMapping(EObject repositoryEObject, EObject snapshotEObject) {
    EObject existingSnapshot = repositoryToSnapshot.get(repositoryEObject);
    if (existingSnapshot != null) {
      throw new IllegalStateException("EObject already mapped to another snapshot object");
    }
    EObject existingRepository = repositoryToSnapshot.inverse().get(snapshotEObject);
    if (existingRepository != null) {
      throw new IllegalStateException("EObject already mapped to another repository object");
    }
    repositoryToSnapshot.put(repositoryEObject, snapshotEObject);
  }

  @Override
  public URI getMetadataModelURI(String... metadataKey) {
    return metadataModelUriProvider.apply(metadataKey);
  }

  @Override
  public Resource getModelResource(URI uri) {
    Resource resource = resourceSet.getResource(uri, false);
    if (resource == null) {
      resource = resourceSet.createResource(uri);
    }
    return resource;
  }

  @Override
  public Collection<Resource> getModelResources() {
    return List.copyOf(resourceSet.getResources());
  }

  @Override
  public void persistAsRoot(EObject rootObject, URI uri) {
    Resource resource = getModelResource(uri);
    resource.getContents().add(rootObject);
    resource.setModified(true);
  }

  @Override
  public void close() {
    for (Resource resource : resourceSet.getResources()) {
      resource.unload();
    }
    resourceSet.getResources().clear();
    repositoryToSnapshot.clear();
  }
}
