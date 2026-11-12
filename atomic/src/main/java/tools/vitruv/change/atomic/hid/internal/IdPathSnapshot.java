package tools.vitruv.change.atomic.hid.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.InternalEList;
import tools.vitruv.change.atomic.hid.HierarchicalId;

/**
 * Remembers where the elements on the paths of the {@link HierarchicalId}s that a {@link
 * HierarchicalIdCalculator} calculated were, so that it can be checked later whether these IDs
 * would still be the same.
 *
 * <p>An ID is built from the URI of the element's resource, the index of the root above the
 * element in the contents of that resource, and one segment for each containment step from that
 * root down to the element. For each element on such a path, the snapshot remembers its index in
 * the contents of its resource, its index in the list that contains it, or the segment that its
 * container calculated for it. For each resource, it remembers its URI and its resource set, and
 * for each reference whose elements got their index as segment, it remembers the name of the
 * reference, which is part of the segment. Checking that an element is still at its index takes
 * constant time, whereas calculating the index means searching the list.
 *
 * <p>The check covers every input of {@link HierarchicalIdCalculator#getId}, so any change to how
 * the calculator builds IDs must be reflected here.
 */
public final class IdPathSnapshot {
  private final Set<EObject> roots = Collections.newSetFromMap(new IdentityHashMap<>());
  private final List<Position> positions = new ArrayList<>();
  private final List<SegmentPosition> calculatedSegments = new ArrayList<>();
  private final Map<Resource, ResourcePlace> resources = new IdentityHashMap<>();
  private final Map<EReference, String> indexedReferences = new IdentityHashMap<>();

  void rememberRoot(EObject root, Resource resource, int index) {
    if (roots.add(root)) {
      positions.add(new RootPosition(root, resource, index));
      resources.computeIfAbsent(
          resource, r -> new ResourcePlace(r, r.getURI(), r.getResourceSet()));
    }
  }

  /** Remembers the position of an element whose segment is its index. Called once per element. */
  void rememberIndex(EObject element, EObject container, EReference reference, int index) {
    positions.add(new IndexPosition((InternalEObject) element, container, reference, index));
    indexedReferences.putIfAbsent(reference, reference.getName());
  }

  /** Remembers the segment that a container calculated for an element. Called once per element. */
  void rememberSegment(
      EObject element, EObject container, EStructuralFeature feature, String segment) {
    SegmentPosition position =
        new SegmentPosition(
            (InternalEObject) element, (InternalEObject) container, feature, segment);
    positions.add(position);
    calculatedSegments.add(position);
  }

  /**
   * Returns whether all IDs calculated with this snapshot would still be the same: whether every
   * resource still has the remembered URI and resource set, every reference whose elements got
   * their index as segment still does so under the remembered name, and every element on the paths
   * is still at its remembered place and gets the same segment. The check does not resolve proxies
   * or change the model in any other way.
   *
   * @return {@code true} if the IDs are unchanged, {@code false} if they may have changed
   */
  public boolean isUnchanged() {
    try {
      // Calculating segments may mean searching the container's contents, so this comes last.
      return resources.values().stream().allMatch(ResourcePlace::isUnchanged)
          && indexedReferences.entrySet().stream()
              .allMatch(entry -> isStillIndexed(entry.getKey(), entry.getValue()))
          && positions.stream().allMatch(Position::isUnchanged)
          && calculatedSegments.stream().allMatch(SegmentPosition::hasSameSegment);
    } catch (RuntimeException e) {
      // For example, a containing feature was removed from its class. Reporting a change only
      // makes the caller calculate the IDs again, which behaves as it would without the snapshot.
      return false;
    }
  }

  /** Returns whether the reference still gives each element of its list a segment by index. */
  private static boolean isStillIndexed(EReference reference, String name) {
    return reference.isMany() && reference.getEKeys().isEmpty() && name.equals(reference.getName());
  }

  private interface Position {
    /** Returns whether the element is still at the remembered place. */
    boolean isUnchanged();
  }

  private record RootPosition(EObject element, Resource resource, int index) implements Position {
    @Override
    public boolean isUnchanged() {
      List<EObject> resourceRoots = resource.getContents();
      return index < resourceRoots.size() && resourceRoots.get(index) == element;
    }
  }

  /**
   * The position of an element that is not a root of a resource and whose segment is its index in
   * the list that contains it. If the element is still at that index of the container's list, the
   * container still contains it. The element must still not be a root of a resource itself,
   * because the path of its ID would then end at the element.
   */
  private record IndexPosition(
      InternalEObject element, EObject container, EReference reference, int index)
      implements Position {
    @Override
    public boolean isUnchanged() {
      if (element.eDirectResource() != null || container.eIsProxy()) {
        return false;
      }
      List<?> list = (List<?>) container.eGet(reference, false);
      if (index >= list.size()) {
        return false;
      }
      // the basic list does not resolve proxies
      Object elementAtIndex =
          list instanceof InternalEList<?> internalList
              ? internalList.basicGet(index)
              : list.get(index);
      return elementAtIndex == element;
    }
  }

  /** The position of an element whose segment its container calculates, e.g. from its name. */
  private record SegmentPosition(
      InternalEObject element,
      InternalEObject container,
      EStructuralFeature feature,
      String segment)
      implements Position {
    @Override
    public boolean isUnchanged() {
      return element.eDirectResource() == null
          && element.eInternalContainer() == container
          && !container.eIsProxy()
          && element.eContainingFeature() == feature;
    }

    boolean hasSameSegment() {
      return segment.equals(container.eURIFragmentSegment(feature, element));
    }
  }

  private record ResourcePlace(Resource resource, URI uri, ResourceSet resourceSet) {
    boolean isUnchanged() {
      return resource.getResourceSet() == resourceSet && Objects.equals(uri, resource.getURI());
    }
  }
}
