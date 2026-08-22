package tools.vitruv.change.propagation.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceCopier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import tools.vitruv.change.propagation.ModelRepositorySnapshot;

/**
 * Default implementation of the {@link ModelRepositorySnapshot} interface.
 */
public class DefaultModelRepositorySnapshot implements ModelRepositorySnapshot {
  private final ResourceSet resourceSet;
  private final PersistableCorrespondenceModel correspondenceModel;
  private final Function<String[], URI> metadataModelUriProvider;

  private boolean closed = false;

  private DefaultModelRepositorySnapshot(
      ResourceSet resourceSet,
      PersistableCorrespondenceModel correspondenceModel,
      Function<String[], URI> metadataModelUriProvider) {
    this.resourceSet = resourceSet;
    this.correspondenceModel = correspondenceModel;
    this.metadataModelUriProvider = metadataModelUriProvider;
  }

  /**
   * Create a new {@link ModelRepositorySnapshot} as the copy of the given {@link ResourceSet}
   * and {@link PersistableCorrespondenceModel}. The correspondence model should refer to elements
   * contained within the given {@link ResourceSet}.
   *
   * @param resourceSet              the {@link ResourceSet} to copy
   * @param correspondenceModel      the {@link PersistableCorrespondenceModel} to copy
   * @param metadataModelUriProvider the URI provider of the repository
   * @return a new {@link ModelRepositorySnapshot}
   */
  public static ModelRepositorySnapshot copyOf(
      ResourceSet resourceSet,
      PersistableCorrespondenceModel correspondenceModel,
      Function<String[], URI> metadataModelUriProvider) {
    ResourceSetCopy resourceSetCopy = copyResourceSet(resourceSet);
    PersistableCorrespondenceModel correspondenceModelCopy
        = correspondenceModel.copy(resourceSetCopy.originalToCopy());

    return new DefaultModelRepositorySnapshot(
        resourceSetCopy.resourceSet(),
        correspondenceModelCopy,
        metadataModelUriProvider);
  }

  private static ResourceSetCopy copyResourceSet(ResourceSet originalResourceSet) {
    ResourceSet copiedResourceSet = withGlobalFactories(new ResourceSetImpl());

    Map<Resource, Resource> resourceCopies
        = ResourceCopier.copyViewResources(originalResourceSet.getResources(), copiedResourceSet);
    resourceCopies.forEach((original, copy) -> copy.setModified(original.isModified()));

    BiMap<EObject, EObject> mapping = createMapping(originalResourceSet, copiedResourceSet);

    return new ResourceSetCopy(copiedResourceSet, mapping);
  }

  private static BiMap<EObject, EObject> createMapping(
      ResourceSet originalResourceSet,
      ResourceSet copiedResourceSet) {
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

  private static void addMapping(
      EObject original, ResourceSet copiedResourceSet, BiMap<EObject, EObject> mapping) {
    URI sourceUri = EcoreUtil.getURI(original);
    EObject copied = copiedResourceSet.getEObject(sourceUri, false);

    mapping.put(original, copied);
  }

  @Override
  public EditableCorrespondenceModelView<Correspondence> getCorrespondenceModel() {
    return CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(
        correspondenceModel);
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
    resource
        .getContents()
        .add(rootObject);
    resource.setModified(true);
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }
    closed = true;

    for (Resource resource : resourceSet.getResources()) {
      resource.unload();
    }

    resourceSet
        .getResources()
        .clear();
  }

  private record ResourceSetCopy(ResourceSet resourceSet, BiMap<EObject, EObject> originalToCopy) {
  }
}
