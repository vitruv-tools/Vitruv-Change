package tools.vitruv.change.correspondence.model;

import static com.google.common.base.Preconditions.checkState;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.loadOrCreateResource;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.CorrespondenceFactory;
import tools.vitruv.change.correspondence.Correspondences;
import tools.vitruv.change.utils.StreamUtils;

/** A persistable representation of a {@link CorrespondenceModel}. */
class PersistableCorrespondenceModelImpl implements PersistableCorrespondenceModel {
  private static final Logger logger =
      LogManager.getLogger(PersistableCorrespondenceModelImpl.class);
  private final Correspondences correspondences;
  private final CorrespondenceIndex correspondenceIndex;
  private final Resource correspondencesResource;

  /**
   * About how many times a correspondence can be moved in the list for the cost of removing a
   * correspondence from the list or adding it again, with its adapters and index entries; measured
   * with 128,000 correspondences. {@link #removeAll} compares the two costs.
   */
  private static final long MOVES_PER_DETACHED_OR_ATTACHED_CORRESPONDENCE = 10_000;

  /**
   * The URIs of the elements in the correspondences when the correspondences were last written,
   * with which the file refers to them. {@code null} if the correspondences have not been written
   * yet or not all of these elements have an ID, see {@link #collectElementUris()}.
   */
  private Map<EObject, ElementUri> writtenElementUris;

  /**
   * Creates a new correspondence model with the given resource URI.
   *
   * @param resourceUri the URI of the resource to store the correspondences in
   */
  public PersistableCorrespondenceModelImpl(URI resourceUri) {
    this(CorrespondenceFactory.eINSTANCE.createCorrespondences(), resourceUri);
  }

  private PersistableCorrespondenceModelImpl(Correspondences correspondences, URI resourceUri) {
    this.correspondences = correspondences;
    this.correspondenceIndex = new CorrespondenceIndex(correspondences);
    if (resourceUri != null) {
      this.correspondencesResource =
              withGlobalFactories(new ResourceSetImpl()).createResource(resourceUri);
      this.correspondencesResource.getContents().add(correspondences);
      // marks the resource as modified whenever the correspondences change
      this.correspondencesResource.setTrackingModification(true);
    } else {
      this.correspondencesResource = null;
    }
  }

  @Override
  public void loadSerializedCorrespondences(ResourceSet resolveIn) {
    checkState(
        correspondencesResource != null,
        "Correspondences resource must be specified to load existing correspondences");
    Resource loadedResource =
        loadOrCreateResource(
            withGlobalFactories(new ResourceSetImpl()), correspondencesResource.getURI());
    if (!loadedResource.getContents().isEmpty()) {
      Correspondences loadedCorrespondences = (Correspondences)
          loadedResource.getContents().getFirst();
      for (Correspondence correspondence : loadedCorrespondences.getCorrespondences()) {
        List<EObject> resolvedLeftObjects = resolve(correspondence.getLeftEObjects(), resolveIn);
        replace(correspondence.getLeftEObjects(), resolvedLeftObjects);
        List<EObject> resolvedRightObjects = resolve(correspondence.getRightEObjects(), resolveIn);
        replace(correspondence.getRightEObjects(), resolvedRightObjects);
      }
      this.correspondences.getCorrespondences().addAll(loadedCorrespondences.getCorrespondences());
    }
  }

  private static void replace(List<EObject> originalList, List<EObject> elementsToReplaceWith) {
    originalList.clear();
    originalList.addAll(elementsToReplaceWith);
  }

  private static List<EObject> resolve(List<EObject> eObjects, ResourceSet resolveIn) {
    List<EObject> resolvedEObjects =
        eObjects.stream().map(eObject -> EcoreUtil.resolve(eObject, resolveIn)).toList();
    for (EObject resolvedEObject : resolvedEObjects) {
      checkState(
          !resolvedEObject.eIsProxy(),
          "object %s is referenced in correspondence but could not be resolved",
          resolvedEObject);
    }
    return resolvedEObjects;
  }

  /**
   * Removes the correspondences of elements that are not in a resource anymore and writes the
   * correspondences if they or the URIs of their elements have changed since they were last
   * written. Whether the file is up to date is decided from what this model wrote, so the file must
   * not be written by anything else, such as a {@link #copy copy} of this model.
   */
  @Override
  public void save() {
    removeCorrespondencesForRemovedElements();
    if (this.correspondencesResource == null || isWrittenAsIs()) {
      return;
    }
    // a failed write leaves the file in an unknown state, so the next save writes it again
    this.writtenElementUris = null;
    try {
      this.correspondencesResource.save(null);
      this.correspondencesResource.setModified(false);
      this.writtenElementUris = collectElementUris();
    } catch (IOException e) {
      logger.error("Could not save correspondences resource", e);
    }
  }

  /**
   * Returns whether writing the correspondences would not change the file: the correspondences have
   * not changed since they were last written, and their elements still have the URIs the file
   * refers to them by. Path fragments in these URIs change when the models change, for example
   * when an element is inserted before a referenced one.
   */
  private boolean isWrittenAsIs() {
    if (writtenElementUris == null || correspondencesResource.isModified()) {
      return false;
    }
    for (Correspondence correspondence : correspondences.getCorrespondences()) {
      for (EObject element : correspondence.eCrossReferences()) {
        ElementUri writtenUri = writtenElementUris.get(element);
        if (writtenUri == null || !writtenUri.isUriOf(element)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns the URIs of the elements in the correspondences, or {@code null} if the URI fragment of
   * one of them is a path of indices, as for elements without an ID. Computing such a fragment
   * searches the lists on the path, so checking these URIs can take as long as writing the file.
   * The correspondences are then written on every save.
   */
  private Map<EObject, ElementUri> collectElementUris() {
    Map<EObject, ElementUri> elementUris = new IdentityHashMap<>();
    for (Correspondence correspondence : correspondences.getCorrespondences()) {
      // the left and right objects, and those of further references of a subclass
      for (EObject element : correspondence.eCrossReferences()) {
        ElementUri uri = ElementUri.of(element);
        if (uri.hasPathFragment() && !isOfStaticMetamodel(element)) {
          return null;
        }
        elementUris.put(element, uri);
      }
    }
    return elementUris;
  }

  /**
   * Returns whether the element belongs to a generated or registered metamodel, whose resource is
   * not in a resource set. Its fragment is a path of names, which does not change.
   */
  private static boolean isOfStaticMetamodel(EObject element) {
    return element instanceof EModelElement
        && element.eResource() != null
        && element.eResource().getResourceSet() == null;
  }

  /**
   * The URI by which the correspondence file refers to an element: the URI of the element's
   * resource and the element's URI fragment in it, or the element's whole URI if it is a proxy or
   * not in a resource. Comparing them does not create URIs, which EMF keeps in a global pool.
   *
   * @param uri the URI of the element's resource, or the element's URI if {@code fragment} is
   *     {@code null}
   * @param fragment the element's URI fragment in its resource
   */
  private record ElementUri(URI uri, String fragment) {
    static ElementUri of(EObject element) {
      Resource resource = element.eResource();
      return element.eIsProxy() || resource == null
          ? new ElementUri(EcoreUtil.getURI(element), null)
          : new ElementUri(resource.getURI(), resource.getURIFragment(element));
    }

    boolean isUriOf(EObject element) {
      Resource resource = element.eResource();
      if (element.eIsProxy() || resource == null) {
        return fragment == null && uri.equals(EcoreUtil.getURI(element));
      }
      return fragment != null
          && fragment.equals(resource.getURIFragment(element))
          && Objects.equals(uri, resource.getURI());
    }

    boolean hasPathFragment() {
      return fragment != null && fragment.startsWith("/");
    }
  }

  @Override
  public PersistableCorrespondenceModel copy(Map<EObject, EObject> originalToCopyMapping) {
    Correspondences copiedCorrespondences = EcoreUtil.copy(this.correspondences);

    for (int index = 0; index < copiedCorrespondences.getCorrespondences().size(); index++) {
      Correspondence originalCorrespondence = this.correspondences.getCorrespondences().get(index);
      Correspondence copiedCorrespondence = copiedCorrespondences.getCorrespondences().get(index);

      var newLeftEObjects = StreamUtils.mapIfMappable(
          copiedCorrespondence.getLeftEObjects().stream(), originalToCopyMapping).toList();
      var newRightEObjects = StreamUtils.mapIfMappable(
          copiedCorrespondence.getRightEObjects().stream(), originalToCopyMapping).toList();

      replace(copiedCorrespondence.getLeftEObjects(), newLeftEObjects);
      replace(copiedCorrespondence.getRightEObjects(), newRightEObjects);

      originalToCopyMapping.put(originalCorrespondence, copiedCorrespondence);
    }

    URI resourceUri = this.correspondencesResource != null
                      ? this.correspondencesResource.getURI()
                      : null;

    return new PersistableCorrespondenceModelImpl(copiedCorrespondences, resourceUri);
  }

  @Override
  public <C extends Correspondence> C addCorrespondenceBetween(
      List<EObject> firstEObjects,
      List<EObject> secondEObjects,
      String tag,
      Supplier<C> correspondenceCreator) {
    C correspondence = correspondenceCreator.get();
    correspondence.getLeftEObjects().addAll(firstEObjects);
    correspondence.getRightEObjects().addAll(secondEObjects);
    correspondence.setTag(tag);
    this.correspondences.getCorrespondences().add(correspondence);
    return correspondence;
  }

  private void removeCorrespondencesForRemovedElements() {
    List<Correspondence> staleCorrespondences = new ArrayList<>();
    List<Integer> staleIndices = new ArrayList<>();
    List<Correspondence> list = correspondences.getCorrespondences();
    for (int index = 0; index < list.size(); index++) {
      Correspondence element = list.get(index);
      if (element.getLeftEObjects().stream().anyMatch(this::isNotInManagedResource)
          || element.getRightEObjects().stream().anyMatch(this::isNotInManagedResource)) {
        checkState(
            element.getLeftEObjects().stream().allMatch(this::isNotInManagedResource)
                || element.getRightEObjects().stream().allMatch(this::isNotInManagedResource),
            "Correspondence between %s and %s contains elements %s that are not contained in a"
                + " resource anymore.",
            element.getLeftEObjects(),
            element.getRightEObjects(),
            Stream.concat(element.getLeftEObjects().stream(), element.getRightEObjects().stream())
                .filter(this::isNotInManagedResource)
                .toList());
        staleCorrespondences.add(element);
        staleIndices.add(index);
      }
    }
    if (staleCorrespondences.isEmpty()) {
      return;
    }

    removeAll(staleCorrespondences, staleIndices);
    if (logger.isTraceEnabled()) {
      for (Correspondence element : staleCorrespondences) {
        logger.trace(
            "Correspondence between {} and {} has been removed as all its elements have been"
                + " removed from resources.",
            element.getLeftEObjects(),
            element.getRightEObjects());
      }
    }
  }

  /**
   * Removes the given correspondences, which are at the given ascending indices of the
   * correspondence list. Removing a correspondence moves all correspondences after it, so removing
   * many of them one by one can take quadratic time, as does {@code EList.removeAll}. If that would
   * cost more than rebuilding the list, the list's contents are replaced by the remaining
   * correspondences instead, which takes linear time but removes and adds every remaining
   * correspondence. This only pays off for many stale correspondences in very long lists.
   */
  private void removeAll(List<Correspondence> staleCorrespondences, List<Integer> staleIndices) {
    List<Correspondence> list = correspondences.getCorrespondences();
    // removing from the back, the stale correspondences still to remove are not moved
    long moves = 0;
    for (int position = staleIndices.size() - 1; position >= 0; position--) {
      int removedBehind = staleIndices.size() - 1 - position;
      moves += list.size() - 1 - staleIndices.get(position) - removedBehind;
    }
    // rebuilding removes all correspondences and adds the remaining ones again
    long rebuildCost = MOVES_PER_DETACHED_OR_ATTACHED_CORRESPONDENCE
        * (2L * list.size() - staleIndices.size());
    if (moves <= rebuildCost) {
      for (int position = staleIndices.size() - 1; position >= 0; position--) {
        list.remove((int) staleIndices.get(position));
      }
      return;
    }
    Set<Correspondence> stale = Collections.newSetFromMap(new IdentityHashMap<>());
    stale.addAll(staleCorrespondences);
    List<Correspondence> remaining =
        list.stream().filter(correspondence -> !stale.contains(correspondence)).toList();
    list.clear();
    list.addAll(remaining);
  }

  private boolean isNotInManagedResource(EObject object) {
    return !(object instanceof EClass)
        && (object.eResource() == null || object.eResource().getResourceSet() == null);
  }

  private void removeCorrespondence(Correspondence correspondence) {
    EcoreUtil.remove(correspondence);
  }

  @Override
  public <C extends Correspondence> Set<C> removeCorrespondencesBetween(
      Class<C> correspondenceType, List<EObject> aEObjects, List<EObject> bEObjects, String tag) {
    Set<Correspondence> correspondencesBetween = getCorrespondencesBetween(aEObjects, bEObjects);
    Set<C> correspondencesToRemove =
        filterCorrespondenceTypeAndTag(correspondencesBetween, correspondenceType, tag);
    correspondencesToRemove.forEach(this::removeCorrespondence);
    return correspondencesToRemove;
  }

  private <C extends Correspondence> Set<C> filterCorrespondenceTypeAndTag(
      Set<Correspondence> original, Class<C> filteredType, String expectedTag) {
    return original.stream()
        .filter(filteredType::isInstance)
        .map(filteredType::cast)
        .filter(
            correspondence -> expectedTag == null || expectedTag.equals(correspondence.getTag()))
        .collect(Collectors.toSet());
  }

  private Set<Correspondence> getCorrespondences(List<EObject> eObjects) {
    return new HashSet<>(correspondenceIndex.getCorrespondences(eObjects));
  }

  private Set<Correspondence> getCorrespondencesBetween(
      List<EObject> firstEObjects, List<EObject> secondEObjects) {
    // The index only returns correspondences that have the first objects as one of their sides.
    return correspondenceIndex.getCorrespondences(firstEObjects).stream()
        .filter(
            correspondence ->
                isEitherSideOfCorrespondence(correspondence, secondEObjects)
                    && (!firstEObjects.equals(secondEObjects)
                        || correspondence
                            .getLeftEObjects()
                            .equals(correspondence.getRightEObjects())))
        .collect(Collectors.toSet());
  }

  private boolean isEitherSideOfCorrespondence(
      Correspondence correspondence, List<EObject> elementsToBeEitherSide) {
    return elementsToBeEitherSide.equals(correspondence.getLeftEObjects())
        || elementsToBeEitherSide.equals(correspondence.getRightEObjects());
  }

  private Set<List<EObject>> mapToCorrespondingEObjects(
      Set<? extends Correspondence> correspondences, List<EObject> originalEObjects) {
    return correspondences.stream()
        .map(correspondence -> getCorrespondingEObjects(correspondence, originalEObjects))
        .collect(Collectors.toSet());
  }

  private List<EObject> getCorrespondingEObjects(
      Correspondence correspondence, List<EObject> eObjects) {
    if (correspondence.getLeftEObjects().equals(eObjects)) {
      return correspondence.getRightEObjects();
    } else {
      return correspondence.getLeftEObjects();
    }
  }

  @Override
  public Set<List<EObject>> getCorrespondingEObjects(
      Class<? extends Correspondence> correspondenceType, List<EObject> eObjects, String tag) {
    Set<Correspondence> objectsCorrespondences = getCorrespondences(eObjects);
    Set<? extends Correspondence> properlyTaggedAndTypedCorrespondences =
        filterCorrespondenceTypeAndTag(objectsCorrespondences, correspondenceType, tag);
    return mapToCorrespondingEObjects(properlyTaggedAndTypedCorrespondences, eObjects);
  }

  @Override
  public boolean hasCorrespondences(List<EObject> eObjects) {
    return !this.getCorrespondences(eObjects).isEmpty();
  }

  @Override
  public Set<EObject> getAllEObjectsInACorrespondence() {
    Set<EObject> allCorrespondences = HashSet.newHashSet(
        correspondences.getCorrespondences().size());
    for (var correspondence : correspondences.getCorrespondences()) {
      allCorrespondences.addAll(correspondence.getLeftEObjects());
      allCorrespondences.addAll(correspondence.getRightEObjects());
    }
    return Set.copyOf(allCorrespondences);
  }

  @Override
  public Set<String> getAllTags() {
    return this.correspondences.getCorrespondences()
        .stream()
        .map(Correspondence::getTag)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Override
  public Map<String, Set<EObject>> getCorrespondingEObjectsWithTag(
      List<EObject> sourceEObjects, Class<? extends Correspondence> correspondenceType) {
    // Extract correspondences
    Set<Correspondence> correspondencesForSource = getCorrespondences(sourceEObjects);
    Set<? extends Correspondence> typedCorrespondences = filterCorrespondenceTypeAndTag(
        correspondencesForSource,
        correspondenceType,
        null
    );

    Map<String, Set<EObject>> correspondingTaggedElements = new HashMap<>();
    for (var correspondence : typedCorrespondences) {
      var correspondingElements =
          correspondence.getLeftEObjects().equals(sourceEObjects)
              ? correspondence.getRightEObjects()
              : correspondence.getLeftEObjects();
      correspondingTaggedElements.computeIfAbsent(
          correspondence.getTag(),
          tag -> new HashSet<>()
      ).addAll(correspondingElements);
    }
    return correspondingTaggedElements;
  }

  @Override
  public void close() throws Exception {
    if (correspondencesResource != null) {
      correspondencesResource.unload();
    }
  }
}
