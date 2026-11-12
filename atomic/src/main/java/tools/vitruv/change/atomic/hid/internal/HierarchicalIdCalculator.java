package tools.vitruv.change.atomic.hid.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.atomic.hid.ObjectResolutionUtil;

/**
 * Calculates the {@link HierarchicalId}s of many elements. The IDs are the same as those that
 * {@link HierarchicalIdResolver#getAndUpdateId} gives to elements in a resource, i.e. they are
 * built from {@link ObjectResolutionUtil#getHierarchicUriFragment}.
 *
 * <p>Such a fragment contains the index of each element on the path from the resource root in its
 * containing list, which EMF finds by searching the list. So calculating the IDs of all elements of
 * a long list takes quadratic time. This calculator instead indexes a list the first time it needs
 * the index of one of its elements. It also remembers the segment of each element that can
 * contain others, because the elements below a container share the segments of the path to it,
 * and a container may calculate a segment by searching its contents, as Ecore and UML do for
 * names. So it calculates the segment of each element once. The contents of the resources must
 * therefore not change while a calculator is used.
 *
 * <p>The calculator can remember the paths of the IDs it calculates in an {@link IdPathSnapshot},
 * which checks later whether these IDs are still the same. Any change to how the IDs are built
 * must therefore also be made in the snapshot.
 */
public final class HierarchicalIdCalculator {
  private final Map<Resource, Map<EObject, Integer>> rootIndices = new IdentityHashMap<>();
  private final Map<EObject, Integer> indicesInContainingList = new IdentityHashMap<>();
  private final Set<List<?>> indexedLists = Collections.newSetFromMap(new IdentityHashMap<>());
  private final Map<Class<?>, Boolean> usesDefaultSegments = new HashMap<>();
  private final Map<EObject, String> calculatedSegments = new IdentityHashMap<>();
  private final Map<EClass, Boolean> canContainOthers = new HashMap<>();
  private final IdPathSnapshot snapshot;

  /** Creates a calculator. */
  public HierarchicalIdCalculator() {
    this(null);
  }

  /**
   * Creates a calculator that remembers the paths of the IDs it calculates.
   *
   * @param snapshot the snapshot to remember the paths in, or {@code null} to not remember them
   */
  public HierarchicalIdCalculator(IdPathSnapshot snapshot) {
    this.snapshot = snapshot;
  }

  /**
   * Returns the hierarchical ID of the given element.
   *
   * @param eObject the element to calculate the ID for, must be contained in a resource
   * @return the hierarchical ID of the element
   */
  public HierarchicalId getId(EObject eObject) {
    Resource resource = eObject.eResource();
    checkArgument(resource != null, "element %s is not contained in a resource", eObject);
    Map<EObject, Integer> rootIndicesOfResource =
        rootIndices.computeIfAbsent(resource, r -> indexElements(r.getContents()));
    Deque<String> segments = new ArrayDeque<>();
    EObject current = eObject;
    Integer rootIndex = rootIndicesOfResource.get(current);
    while (rootIndex == null) {
      EObject container = current.eContainer();
      checkState(
          container != null,
          "some container of %s must be a root element of its resource",
          eObject);
      segments.addFirst(getFragmentSegment(container, current));
      current = container;
      rootIndex = rootIndicesOfResource.get(current);
    }
    if (snapshot != null) {
      snapshot.rememberRoot(current, resource, rootIndex);
    }
    StringBuilder fragment = new StringBuilder().append('/').append(rootIndex);
    segments.forEach(segment -> fragment.append('/').append(segment));
    return new HierarchicalId(resource.getURI().appendFragment(fragment.toString()).toString());
  }

  private String getFragmentSegment(EObject container, EObject child) {
    if (!canContainOthers.computeIfAbsent(child.eClass(), HierarchicalIdCalculator::canContain)) {
      // an element that cannot contain others is on the path of no other element
      return calculateFragmentSegment(container, child);
    }
    String segment = calculatedSegments.get(child);
    if (segment == null) {
      segment = calculateFragmentSegment(container, child);
      calculatedSegments.put(child, segment);
    }
    return segment;
  }

  /**
   * Returns whether elements of the given class can contain other elements, with a containment
   * reference or through a feature map.
   */
  private static boolean canContain(EClass eClass) {
    return !eClass.getEAllContainments().isEmpty()
        || eClass.getEAllAttributes().stream().anyMatch(FeatureMapUtil::isFeatureMap);
  }

  private String calculateFragmentSegment(EObject container, EObject child) {
    EStructuralFeature feature = child.eContainingFeature();
    if (feature instanceof EReference reference
        && reference.isMany()
        && reference.getEKeys().isEmpty()
        && usesDefaultSegments(container)) {
      Integer index = getIndexInContainingList(container, reference, child);
      if (index != null) {
        if (snapshot != null) {
          snapshot.rememberIndex(child, container, reference, index);
        }
        // the segment that BasicEObjectImpl#eURIFragmentSegment builds with List#indexOf
        return "@" + reference.getName() + "." + index;
      }
    }
    String segment = ((InternalEObject) container).eURIFragmentSegment(feature, child);
    if (snapshot != null) {
      snapshot.rememberSegment(child, container, feature, segment);
    }
    return segment;
  }

  private Integer getIndexInContainingList(EObject container, EReference reference, EObject child) {
    Integer index = indicesInContainingList.get(child);
    if (index == null) {
      List<?> list = (List<?>) container.eGet(reference, false);
      if (indexedLists.add(list)) {
        // the basic list does not resolve proxies
        List<?> elements = list instanceof InternalEList<?> internalList
            ? internalList.basicList() : list;
        for (int i = 0; i < elements.size(); i++) {
          if (elements.get(i) instanceof EObject element) {
            indicesInContainingList.putIfAbsent(element, i);
          }
        }
        index = indicesInContainingList.get(child);
      }
    }
    return index;
  }

  private boolean usesDefaultSegments(EObject container) {
    return usesDefaultSegments.computeIfAbsent(
        container.getClass(), HierarchicalIdCalculator::inheritsDefaultSegments);
  }

  /**
   * Returns whether the given class uses EMF's default fragment segments. Classes may override
   * them, as Ecore does with names, and their segments are then calculated by the class itself.
   */
  private static boolean inheritsDefaultSegments(Class<?> eObjectClass) {
    try {
      return eObjectClass
              .getMethod("eURIFragmentSegment", EStructuralFeature.class, EObject.class)
              .getDeclaringClass()
          == BasicEObjectImpl.class;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  private static Map<EObject, Integer> indexElements(List<EObject> elements) {
    Map<EObject, Integer> indices = new IdentityHashMap<>();
    for (int i = 0; i < elements.size(); i++) {
      indices.putIfAbsent(elements.get(i), i);
    }
    return indices;
  }
}
