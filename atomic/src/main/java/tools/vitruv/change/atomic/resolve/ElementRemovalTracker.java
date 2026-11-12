package tools.vitruv.change.atomic.resolve;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Finds the elements that may have left a resource set during a transaction, without going through
 * all elements of the resource set. ID resolvers use it to check or clean up only those elements
 * when a transaction ends.
 *
 * <p>An element may have left the resource set if it was touched, i.e. handled by the resolver,
 * during the transaction, if it was the root of a resource when the previous transaction ended and
 * is not anymore, or if it is contained in one of these elements that is not in the resource set
 * anymore. The last case also covers elements that were removed together with their container
 * without being handled themselves. An element that is removed from deeper inside a resource
 * without it or its container being touched is not found.
 */
public final class ElementRemovalTracker {
  private final ResourceSet resourceSet;
  private final Set<EObject> touchedElements = Collections.newSetFromMap(new IdentityHashMap<>());
  private final Map<Resource, List<EObject>> rootsAtTransactionEnd = new IdentityHashMap<>();

  /**
   * Creates a tracker for the elements of the given resource set.
   *
   * @param resourceSet the resource set whose elements to track, not {@code null}
   */
  public ElementRemovalTracker(ResourceSet resourceSet) {
    checkArgument(resourceSet != null, "Resource set may not be null");
    this.resourceSet = resourceSet;
  }

  /**
   * Remembers that the given element was handled during the current transaction.
   *
   * @param element the handled element; {@code null} is ignored
   */
  public void touch(EObject element) {
    if (element != null) {
      touchedElements.add(element);
    }
  }

  /**
   * Returns the elements that may have left the resource set during the current transaction: the
   * touched elements, the roots removed from resources since the previous transaction ended, and
   * the contents of those that are not in the resource set anymore. Some of them may still be in
   * the resource set.
   *
   * @return the elements to check, compared by identity
   */
  public Set<EObject> getElementsThatMayHaveLeft() {
    Set<EObject> candidates = Collections.newSetFromMap(new IdentityHashMap<>());
    candidates.addAll(touchedElements);
    addRootsRemovedSinceTransactionEnd(candidates);
    List<EObject> outsideResourceSet =
        candidates.stream().filter(element -> !isInResourceSet(element)).toList();
    for (EObject element : outsideResourceSet) {
      // What an element contains left the resource set together with it.
      EcoreUtil.<EObject>getAllProperContents(element, false).forEachRemaining(candidates::add);
    }
    return candidates;
  }

  /** Ends the current transaction: forgets the touched elements and remembers the current roots. */
  public void endTransaction() {
    touchedElements.clear();
    rootsAtTransactionEnd.clear();
    for (Resource resource : resourceSet.getResources()) {
      if (!resource.getContents().isEmpty()) {
        rootsAtTransactionEnd.put(resource, new ArrayList<>(resource.getContents()));
      }
    }
  }

  private void addRootsRemovedSinceTransactionEnd(Set<EObject> candidates) {
    rootsAtTransactionEnd.forEach(
        (resource, roots) -> {
          List<EObject> currentRoots =
              resource.getResourceSet() == resourceSet ? resource.getContents() : List.of();
          for (EObject root : roots) {
            if (!currentRoots.contains(root)) {
              candidates.add(root);
            }
          }
        });
  }

  private boolean isInResourceSet(EObject element) {
    return element.eResource() != null && element.eResource().getResourceSet() == resourceSet;
  }
}
