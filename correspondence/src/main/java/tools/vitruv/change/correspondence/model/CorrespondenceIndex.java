package tools.vitruv.change.correspondence.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.CorrespondencePackage;
import tools.vitruv.change.correspondence.Correspondences;

/**
 * Indexes the correspondences of a {@link Correspondences} container by their sides, so that the
 * correspondences of given objects are found without going through all correspondences.
 *
 * <p>The index is an adapter on the container. It follows every change to the correspondences
 * in the container and to their left and right objects, no matter who makes it.
 */
class CorrespondenceIndex extends EContentAdapter {
  private final Map<List<EObject>, Set<Correspondence>> correspondencesBySide = new HashMap<>();
  private final Map<Correspondence, Set<List<EObject>>> sidesOfCorrespondence = new HashMap<>();

  /**
   * Indexes the correspondences in the given container and keeps the index up to date.
   *
   * @param correspondences the container whose correspondences to index
   */
  CorrespondenceIndex(Correspondences correspondences) {
    correspondences.getCorrespondences().forEach(this::index);
    correspondences.eAdapters().add(this);
  }

  /**
   * Returns the correspondences whose left or right objects are exactly the given objects, in the
   * same order.
   *
   * @param side the objects of one side of the correspondences to find
   * @return an unmodifiable view of the correspondences, empty if there are none
   */
  Set<Correspondence> getCorrespondences(List<EObject> side) {
    Set<Correspondence> correspondences = correspondencesBySide.get(side);
    return correspondences == null ? Set.of() : Collections.unmodifiableSet(correspondences);
  }

  @Override
  public void notifyChanged(Notification notification) {
    // Keeps this adapter on the correspondences that are added and removes it from removed ones.
    super.notifyChanged(notification);
    if (notification.isTouch()) {
      return;
    }
    Object feature = notification.getFeature();
    if (feature == CorrespondencePackage.Literals.CORRESPONDENCES__CORRESPONDENCES) {
      updateForChangedCorrespondences(notification);
    } else if ((feature == CorrespondencePackage.Literals.CORRESPONDENCE__LEFT_EOBJECTS
            || feature == CorrespondencePackage.Literals.CORRESPONDENCE__RIGHT_EOBJECTS)
        && notification.getNotifier() instanceof Correspondence correspondence
        && sidesOfCorrespondence.containsKey(correspondence)) {
      index(correspondence);
    }
  }

  private void updateForChangedCorrespondences(Notification notification) {
    switch (notification.getEventType()) {
      case Notification.ADD -> index((Correspondence) notification.getNewValue());
      case Notification.ADD_MANY ->
          ((Collection<?>) notification.getNewValue())
              .forEach(added -> index((Correspondence) added));
      case Notification.REMOVE -> unindex((Correspondence) notification.getOldValue());
      case Notification.REMOVE_MANY ->
          ((Collection<?>) notification.getOldValue())
              .forEach(removed -> unindex((Correspondence) removed));
      case Notification.SET -> {
        unindex((Correspondence) notification.getOldValue());
        index((Correspondence) notification.getNewValue());
      }
      default -> {
        // Moving a correspondence within the container does not change its sides.
      }
    }
  }

  private void index(Correspondence correspondence) {
    if (correspondence == null) {
      return;
    }
    unindex(correspondence);
    // A correspondence whose sides are the same objects is indexed under that side once.
    Set<List<EObject>> sides = new HashSet<>();
    sides.add(List.copyOf(correspondence.getLeftEObjects()));
    sides.add(List.copyOf(correspondence.getRightEObjects()));
    sidesOfCorrespondence.put(correspondence, sides);
    for (List<EObject> side : sides) {
      correspondencesBySide.computeIfAbsent(side, key -> new LinkedHashSet<>()).add(correspondence);
    }
  }

  private void unindex(Correspondence correspondence) {
    Set<List<EObject>> sides = sidesOfCorrespondence.remove(correspondence);
    if (sides == null) {
      return;
    }
    for (List<EObject> side : sides) {
      Set<Correspondence> correspondences = correspondencesBySide.get(side);
      correspondences.remove(correspondence);
      if (correspondences.isEmpty()) {
        correspondencesBySide.remove(side);
      }
    }
  }
}
