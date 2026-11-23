package tools.vitruv.change.atomic.resolve;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
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
 *
 * <p>Transactions are kept per thread. An element touched by a thread belongs to the transaction of
 * that thread, and ending the transaction on a thread checks and forgets only the elements touched
 * on it. Roots removed since the previous end of any transaction are only added while no other
 * thread has an open transaction with touched elements, because such a root may be in the middle
 * of a change of that other thread, which checks it itself when it touches the root and ends its
 * transaction. With a single thread, this is the same as one transaction for all.
 */
public final class ElementRemovalTracker {
  private final ResourceSet resourceSet;
  private final Map<Thread, Set<EObject>> touchedElementsByThread = new IdentityHashMap<>();
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
   * Remembers that the given element was handled during the current transaction of the calling
   * thread.
   *
   * @param element the handled element; {@code null} is ignored
   */
  public synchronized void touch(EObject element) {
    if (element != null) {
      touchedElementsByThread
          .computeIfAbsent(
              Thread.currentThread(), thread -> Collections.newSetFromMap(new IdentityHashMap<>()))
          .add(element);
    }
  }

  /**
   * Returns the elements that may have left the resource set during the current transaction of the
   * calling thread: the elements it touched, the roots removed from resources since the previous
   * transaction ended (unless another thread has an open transaction), and the contents of those
   * that are not in the resource set anymore. Some of them may still be in the resource set.
   *
   * @return the elements to check, compared by identity
   */
  public synchronized Set<EObject> getElementsThatMayHaveLeft() {
    Set<EObject> candidates = Collections.newSetFromMap(new IdentityHashMap<>());
    candidates.addAll(touchedElementsByThread.getOrDefault(Thread.currentThread(), Set.of()));
    if (!hasOpenTransactionOnOtherThread()) {
      addRootsRemovedSinceTransactionEnd(candidates);
    }
    List<EObject> outsideResourceSet =
        candidates.stream().filter(element -> !isInResourceSet(element)).toList();
    for (EObject element : outsideResourceSet) {
      // What an element contains left the resource set together with it.
      EcoreUtil.<EObject>getAllProperContents(element, false).forEachRemaining(candidates::add);
    }
    return candidates;
  }

  /**
   * Ends the current transaction of the calling thread: forgets the elements it touched and
   * remembers the current roots.
   */
  public synchronized void endTransaction() {
    touchedElementsByThread.remove(Thread.currentThread());
    rootsAtTransactionEnd.clear();
    for (Resource resource : resourceSet.getResources()) {
      if (!resource.getContents().isEmpty()) {
        rootsAtTransactionEnd.put(resource, new ArrayList<>(resource.getContents()));
      }
    }
  }

  private boolean hasOpenTransactionOnOtherThread() {
    Thread currentThread = Thread.currentThread();
    Iterator<Thread> threads = touchedElementsByThread.keySet().iterator();
    while (threads.hasNext()) {
      Thread thread = threads.next();
      if (!thread.isAlive()) {
        // a thread that ended without ending its transaction does not hold back the others
        threads.remove();
      } else if (thread != currentThread) {
        return true;
      }
    }
    return false;
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
