package tools.vitruv.change.propagation.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceCopier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
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
  private final BiMap<EObject, EObject> repositoryToSnapshotMap;
  private final Function<String[], URI> metadataModelUriProvider;

  private boolean closed = false;

  private DefaultModelRepositorySnapshot(
      ResourceSet resourceSet,
      PersistableCorrespondenceModel correspondenceModel,
      BiMap<EObject, EObject> repositoryToSnapshotMap,
      Function<String[], URI> metadataModelUriProvider) {
    this.resourceSet = resourceSet;
    this.correspondenceModel = correspondenceModel;
    this.repositoryToSnapshotMap = repositoryToSnapshotMap;
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
        resourceSetCopy.originalToCopy(),
        metadataModelUriProvider);
  }

  /**
   * Copy a resource set and create a mapping between all objects of the original and the copy.
   * This means all resources of the resource set are copied, including all contents.
   *
   * @param originalResourceSet the resource set to copy
   * @return a copy of the resource set and the mapping
   */
  public static ResourceSetCopy copyResourceSet(ResourceSet originalResourceSet) {
    ResourceSet copiedResourceSet = withGlobalFactories(new ResourceSetImpl());

    Map<Resource, Resource> resourceCopies
        = ResourceCopier.copyViewResources(originalResourceSet.getResources(), copiedResourceSet);
    resourceCopies.forEach((original, copy) -> copy.setModified(original.isModified()));

    BiMap<EObject, EObject> mapping =
        createOriginalToCopyMapping(originalResourceSet, copiedResourceSet);

    return new ResourceSetCopy(copiedResourceSet, mapping);
  }

  /**
   * Maps the elements of each original resource to the elements of its copy: the resource in the
   * copied resource set that the original resource's URI resolves to, as with {@link
   * ResourceSet#getResource(URI, boolean)}. The copy has the same structure as the original, so
   * walking both in the same order pairs each element with its copy in linear time. If the walk
   * finds a difference, the elements of that resource are mapped by resolving their URIs in the
   * copied resource set instead.
   */
  static BiMap<EObject, EObject> createOriginalToCopyMapping(
      ResourceSet originalResourceSet, ResourceSet copiedResourceSet) {
    // A copied UML model loads the resources it references, which may then be copied once more;
    // the URIs of copied elements resolve to the first resource with their resource's URI.
    URIConverter uriConverter = copiedResourceSet.getURIConverter();
    Map<URI, Resource> copiesByUri = new HashMap<>();
    for (Resource copiedResource : copiedResourceSet.getResources()) {
      if (copiedResource.getURI() != null) {
        copiesByUri.putIfAbsent(uriConverter.normalize(copiedResource.getURI()), copiedResource);
      }
    }

    BiMap<EObject, EObject> result = HashBiMap.create();
    for (Resource originalResource : originalResourceSet.getResources()) {
      Resource copiedResource =
          originalResource.getURI() == null
              ? null
              : copiesByUri.get(uriConverter.normalize(originalResource.getURI()));
      Map<EObject, EObject> pairs =
          copiedResource == null ? null : pairByStructure(originalResource, copiedResource);
      if (pairs != null) {
        result.putAll(pairs);
      } else {
        addMappingsByUri(originalResource, copiedResourceSet, result);
      }
    }

    return result;
  }

  /**
   * Pairs the elements of a resource with those of its copy by walking both in the same order.
   *
   * @return the pairs, or {@code null} if the elements differ in number, class or containing
   *     feature, or a copy has another ID than its original
   */
  static Map<EObject, EObject> pairByStructure(Resource original, Resource copy) {
    Map<EObject, EObject> pairs = new HashMap<>();
    Iterator<EObject> originals = original.getAllContents();
    // the copy is not resolved: an unresolved proxy is copied as a proxy at the same position
    Iterator<EObject> copies = EcoreUtil.getAllContents(copy, false);
    while (originals.hasNext() && copies.hasNext()) {
      EObject originalElement = originals.next();
      EObject copiedElement = copies.next();
      if (!isCopy(originalElement, copiedElement)) {
        return null;
      }
      pairs.put(originalElement, copiedElement);
    }
    return originals.hasNext() || copies.hasNext() ? null : pairs;
  }

  private static boolean isCopy(EObject original, EObject copy) {
    if (original.eClass() != copy.eClass()
        || original.eContainingFeature() != copy.eContainingFeature()) {
      return false;
    }
    // a factory may give the copy of an element without ID an ID of its own
    String id = EcoreUtil.getID(original);
    return id == null || id.equals(EcoreUtil.getID(copy));
  }

  private static void addMappingsByUri(
      Resource originalResource, ResourceSet copiedResourceSet, BiMap<EObject, EObject> mapping) {
    Iterator<EObject> originals = originalResource.getAllContents();
    while (originals.hasNext()) {
      EObject original = originals.next();
      URI sourceUri = EcoreUtil.getURI(original);
      mapping.put(original, copiedResourceSet.getEObject(sourceUri, false));
    }
  }

  @Override
  public EditableCorrespondenceModelView<Correspondence> getCorrespondenceModel() {
    return CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(
        correspondenceModel);
  }

  @Override
  public Map<EObject, EObject> getRepositoryToSnapshotMap() {
    return Collections.unmodifiableMap(repositoryToSnapshotMap);
  }

  @Override
  public Map<EObject, EObject> getSnapshotToRepositoryMap() {
    return Collections.unmodifiableMap(repositoryToSnapshotMap.inverse());
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

  /**
   * Result provided by {@link #copyResourceSet(ResourceSet)}.
   *
   * @param resourceSet the copied resource set
   * @param originalToCopy the mapping between the original and the copy
   */
  public record ResourceSetCopy(ResourceSet resourceSet, BiMap<EObject, EObject> originalToCopy) {
  }
}
