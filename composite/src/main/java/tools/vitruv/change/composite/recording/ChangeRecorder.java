package tools.vitruv.change.composite.recording;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeUtil;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;

/**
 * Records changes to model elements as a {@link TransactionalChange}.
 * Recording can be started with {@link #beginRecording} and ended with {@link #endRecording}.
 * The recorder assumes that all objects that have been removed from
 * their containment reference without being added to a new containment
 * reference while changes were being recorded have been deleted,
 * resulting in an appropriate delete change.
 * The recorder considers resources being loaded
 * as existing and does thus not produce changes for it.

 * Does not record changes of the <code>xmi:id</code> tag in an
 * {@code org.eclipse.emf.ecore.xmi.XMLResource} if it is not stored in the element
 * but directly in the <code>Resource</code>.
 */
public class ChangeRecorder implements AutoCloseable {
  private static class NotificationRecorder implements Adapter {
    private final ChangeRecorder outer;

    private final Set<Resource> currentlyLoadingResources = new HashSet<Resource>();

    @Override
    public void notifyChanged(final Notification notification) {
      this.handleAdaptersForResourceAndResourceSetChanges(notification);
      final Iterable<? extends EChange<EObject>> newChanges =
              this.extractRelevantChanges(notification);
      if (this.outer.isRecording) {
        Iterables.<EChange<EObject>>addAll(this.outer.resultChanges, newChanges);
      }
    }

    private void handleAdaptersForResourceAndResourceSetChanges(final Notification notification) {
      boolean isResourceSetChange = isResourceSetResourcesChange(notification);

      if (isResourceSetChange) {
        handleResourceSetLoading(notification);
      }

      if (isContainmentChange(notification, isResourceSetChange)) {
        handleInfectionAndDesinfection(notification);
        return;
      }

      if (isResourceIsLoadedChange(notification)) {
        this.finishLoadingResource((Resource) notification.getNotifier());
      }
    }

    private void handleResourceSetLoading(final Notification notification) {
      switch (notification.getEventType()) {
        case Notification.ADD:
          this.startLoadingResource((Resource) notification.getNewValue());
          break;
        case Notification.ADD_MANY:
          ((Iterable<? extends Resource>) notification.getNewValue())
                  .forEach(this::startLoadingResource);
          break;
        default:
          break;
      }
    }

    private void handleInfectionAndDesinfection(final Notification notification) {
      int eventType = notification.getEventType();

      switch (eventType) {
        case Notification.SET:
        case Notification.UNSET:
        case Notification.REMOVE:
          // the old value of unsetting a list is whether it was set, which desinfect ignores
          this.desinfect(notification.getOldValue());
          break;
        case Notification.REMOVE_MANY:
          ((Iterable<?>) notification.getOldValue()).forEach(this::desinfect);
          break;
        default:
          break;
      }

      switch (eventType) {
        case Notification.ADD:
        case Notification.SET:
          this.infect(notification.getNewValue());
          break;
        case Notification.ADD_MANY:
          ((Iterable<?>) notification.getNewValue()).forEach(this::infect);
          break;
        default:
          break;
      }
    }

    private boolean isContainmentChange(
            final Notification notification, final boolean isResourceSetChange) {
      return isContainmentEReference(notification)
              || isResourceContentsChange(notification)
              || isResourceSetChange;
    }

    private boolean isContainmentEReference(final Notification notification) {
      Object feature = notification.getFeature();
      return feature instanceof EReference eReference && eReference.isContainment();
    }

    private boolean isResourceContentsChange(final Notification notification) {
      return notification.getNotifier() instanceof Resource
              && notification.getFeatureID(Resource.class) == Resource.RESOURCE__CONTENTS;
    }

    private boolean isResourceSetResourcesChange(final Notification notification) {
      return notification.getNotifier() instanceof ResourceSet
              && notification
              .getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES;
    }

    private boolean isResourceIsLoadedChange(final Notification notification) {
      return notification.getNotifier() instanceof Resource
              && notification
              .getFeatureID(Resource.class) == Resource.RESOURCE__IS_LOADED;
    }

    private Iterable<? extends EChange<EObject>> extractRelevantChanges(
            final Notification notification) {
      Iterable<? extends EChange<EObject>> xifexpression = null;
      if ((this.affectsLoadingResource(notification)
              || this.affectsUnloadingResource(notification))) {
        xifexpression = List.of();
      } else {
        NotificationInfo notificationInfo = new NotificationInfo(notification);
        xifexpression = this.outer.converter.convert(notificationInfo);
      }
      final Iterable<? extends EChange<EObject>> changes = xifexpression;
      boolean isEmpty = Iterables.isEmpty(changes);
      if (!isEmpty) {
        final Consumer<EChange<EObject>> _function = (EChange<EObject> it) -> {
          if (it instanceof EObjectAddedEChange) {
            EObject newValue = ((EObjectAddedEChange<EObject>) it).getNewValue();
            this.outer.existingObjects.add(newValue);
            if (it instanceof UpdateReferenceEChange) {
              EObject affectedElement = ((UpdateReferenceEChange<EObject>) it).getAffectedElement();
              this.outer.existingObjects.add(affectedElement);
            }
          }
        };
        changes.forEach(_function);
      }
      return changes;
    }

    private void startLoadingResource(final Resource resource) {
      this.currentlyLoadingResources.add(resource);
    }

    private void finishLoadingResource(final Resource resource) {
      this.currentlyLoadingResources.remove(resource);
      ChangeRecorder.recursively(resource, it -> {
        if (it instanceof EObject eObject) {
          this.outer.existingObjects.add(eObject);
        }
        return this.outer.addAdapter(it);
      });
    }

    private boolean affectsLoadingResource(final Notification notification) {
      EObject xifexpression = null;
      Object newValue = notification.getNewValue();
      if ((newValue instanceof EObject)) {
        Object newValue1 = notification.getNewValue();
        xifexpression = ((EObject) newValue1);
      }
      final EObject newEObject = xifexpression;
      boolean and = false;
      Resource eResource = null;
      if (newEObject != null) {
        eResource = newEObject.eResource();
      }
      boolean tripleNotEquals = (eResource != null);

      if (tripleNotEquals) {
        and = this.currentlyLoadingResources.contains(newEObject.eResource());
      }
      return and;
    }

    private boolean affectsUnloadingResource(final Notification notification) {
      Object notifier = notification.getNotifier();
      if ((notifier instanceof Resource)) {
        Object notifier1 = notification.getNotifier();
        final Resource resource = ((Resource) notifier1);
        final ResourceSet resourceSet = resource.getResourceSet();
        return (((!resource.isLoaded()) && (resourceSet != null))
                && resourceSet.getURIConverter().exists(resource.getURI(), Map.of()));
      } else {
        return false;
      }
    }

    private void infect(final Object newValue) {
      if (newValue != null) {
        final Predicate<Notifier> _function = (Notifier it) -> {
          boolean xblockexpression = false;
          this.outer.toDesinfect.remove(it);
          xblockexpression = this.outer.addAdapter(it);
          return xblockexpression;
        };
        ChangeRecorder.recursively(((Notifier) newValue), _function);
      }
    }

    private void desinfect(final Object oldValue) {
      if (oldValue instanceof Notifier notifier) {
        this.outer.toDesinfect.add(notifier);
      }
    }

    @Override
    public Notifier getTarget() {
      return null;
    }

    @Override
    public boolean isAdapterForType(final Object type) {
      return false;
    }

    @Override
    public void setTarget(final Notifier newTarget) {
      // Intentionally empty: this recorder does not track a single adapter target
      // (getTarget() always returns null). It is attached to and detached from notifiers
      // explicitly by the enclosing ChangeRecorder, so EMF's target callback needs no action.
    }

    public NotificationRecorder(final ChangeRecorder outer) {
      super();
      this.outer = outer;
    }
  }

  private final ChangeRecorder.NotificationRecorder recordingAdapter = new ChangeRecorder
          .NotificationRecorder(this);

  private final Set<Notifier> rootObjects = new HashSet<Notifier>();

  private boolean isRecording = false;

  private List<EChange<EObject>> resultChanges = List.of();

  private final NotificationToEChangeConverter converter;

  private final Set<EObject> existingObjects = new HashSet<EObject>();

  private final Set<Notifier> toDesinfect = new HashSet<Notifier>();

  private final ResourceSet resourceSet;

  /**
   * Creates a change recorder for the given resource set.
   *
   * @param resourceSet the resource set whose changes are recorded
   */

  public ChangeRecorder(final ResourceSet resourceSet) {
    this.resourceSet = resourceSet;
    this.converter = new NotificationToEChangeConverter(this::isCreateChange);
  }

  private boolean isCreateChange(final EObject affectedObject, final EObject addedObject) {
    boolean create = ((addedObject != null) && (!this.existingObjects.contains(addedObject)));
    create = (create && (((addedObject.eResource() == null) || (affectedObject == null))
            || Objects.equals(addedObject.eResource(), affectedObject.eResource())));
    if (create) {
      this.existingObjects.add(addedObject);
    }
    return create;
  }

  /**
   * Add the given elements and all its contained elements ({@link Resource}s,
   * {@link EObject}s) to the recorder.
   *
   * @param notifier - the {@link Notifier} to add the recorder to
   * @throws IllegalStateException if the recorder is already disposed
   */
  public void addToRecording(final Notifier notifier) {
    this.checkNotDisposed();
    Preconditions.<Notifier>checkNotNull(notifier, "notifier");
    Preconditions.checkArgument(this.isInOurResourceSet(notifier),
            "cannot record changes in a different resource set!");
    boolean add = this.rootObjects.add(notifier);
    if (add) {
      ChangeRecorder.recursively(notifier, it -> {
        if (it instanceof EObject eObject) {
          this.existingObjects.add(eObject);
        }
        return this.addAdapter(it);
      });
    }
  }

  /**
   * Removes the given elements and
   * all its contained elements (resources, EObjects) from the recorder.
   *
   * @param notifier - the {@link Notifier} to remove the recorder from
   */
  public void removeFromRecording(final Notifier notifier) {
    this.checkNotDisposed();
    Preconditions.<Notifier>checkNotNull(notifier, "notifier");
    this.rootObjects.remove(notifier);
    final Predicate<Notifier> _function = this::removeAdapter;
    ChangeRecorder.recursively(notifier, _function);
  }

  /**
   * Starts recording changes on the registered elements.
   * Elements that were removed from the recorded models since the last recording started, also
   * while not recording, are treated as deleted if they were not added again and are not in any
   * resource anymore. The recorder stops recording and forgets them, and if one of them is added
   * again later, it is recorded as created. This does not apply to elements passed to
   * {@link #addToRecording} and their contents, which stay recorded until
   * {@link #removeFromRecording} is called.
   */
  public List<EChange<EObject>> beginRecording() {
    this.checkNotDisposed();
    Preconditions.checkState(!this.isRecording, "This recorder is already recording!");
    for (final Notifier removed : this.toDesinfect) {
      if (removed instanceof EObject eObject && eObject.eResource() == null) {
        ChangeRecorder.recursively(removed, this::stopRecordingDeleted);
      } else {
        ChangeRecorder.recursively(removed, this::removeAdapter);
      }
    }
    this.toDesinfect.clear();
    this.isRecording = true;
    this.resultChanges = new ArrayList<>();
    return this.resultChanges;
  }

  @Override
  public void close() {
    this.isRecording = false;
    this.resultChanges = null;
    final Set<Notifier> rootCopy = Set.<Notifier>copyOf(this.rootObjects);
    this.rootObjects.clear();
    this.existingObjects.clear();
    final Consumer<Notifier> _function = (Notifier it) -> {
      final Predicate<Notifier> function1 = this::removeAdapter;
      ChangeRecorder.recursively(it, function1);
    };
    rootCopy.forEach(_function);
  }

  private void checkNotDisposed() {
    Preconditions.checkState((this.resultChanges != null),
            "This recorder has already been disposed!");
  }

  /**
   * Ends recording changes on the registered elements.
   * All elements that were removed from their container and not inserted into another one
   * are treated as deleted and a delete change is created for them, inserted right after
   * the change describing the removal from the container.
   */
  public TransactionalChange<EObject> endRecording() {
    this.checkNotDisposed();
    Preconditions.checkState(this.isRecording, "This recorder is not recording");
    this.isRecording = false;
    this.resultChanges = List.copyOf(this.postprocessRemovals(this.resultChanges));
    return this.getChange();
  }

  /**
   * Creates {@link DeleteEObject} changes for every element implicitly deleted in the change
   * sequence and all of its contained elements. The delete changes are appended at the end
   * of the list. Contained elements are deleted before their container, each preceded by the
   * change removing it from its container.
   */
  private List<EChange<EObject>> postprocessRemovals(final List<EChange<EObject>> changes) {
    if (changes.isEmpty()) {
      return changes;
    }
    final Set<EObject> removedElements = findRemovedElements(changes);
    if (!removedElements.isEmpty()) {
      appendDeleteChanges(changes, removedElements);
    }
    return changes;
  }

  private static Set<EObject> findRemovedElements(final List<EChange<EObject>> changes) {
    // insertion-ordered, so that delete changes come in the same order in every run
    final Set<EObject> removedElements = new LinkedHashSet<>();
    for (final EChange<EObject> eChange : changes) {
      if (eChange instanceof EObjectSubtractedEChange
          && EChangeUtil.isContainmentRemoval(eChange)) {
        removedElements.add(((EObjectSubtractedEChange<EObject>) eChange).getOldValue());
      }
      if (eChange instanceof EObjectAddedEChange
          && EChangeUtil.isContainmentInsertion(eChange)) {
        removedElements.remove(((EObjectAddedEChange<EObject>) eChange).getNewValue());
      }
    }
    return removedElements;
  }

  private void appendDeleteChanges(final List<EChange<EObject>> changes,
      final Set<EObject> removedElements) {
    final Map<EObject, Iterable<EObject>> allElementsToDelete = new LinkedHashMap<>();
    final Set<EObject> collectedElements = new HashSet<>();
    for (final EObject element : removedElements) {
      if (collectedElements.contains(element) || isRootOfOwnResource(element)) {
        continue;
      }
      final List<EObject> elementsToDelete = collectContainedElements(element);
      java.util.Collections.reverse(elementsToDelete);
      for (final EObject child : elementsToDelete) {
        allElementsToDelete.remove(child);
      }
      elementsToDelete.add(element);
      collectedElements.addAll(elementsToDelete);
      allElementsToDelete.put(element, elementsToDelete);
    }
    final List<EChange<EObject>> deleteChanges = new ArrayList<>();
    // the model is not modified here, so each index is looked up in the unchanged lists
    final Map<EObject, Integer> indicesInContainer = new HashMap<>();
    for (final Map.Entry<EObject, Iterable<EObject>> entry : allElementsToDelete.entrySet()) {
      for (final EObject it : entry.getValue()) {
        // the removed element itself already left its container in a recorded change,
        // contained elements have to be removed from their containers before being deleted
        if (it != entry.getKey()) {
          deleteChanges.add(this.converter.createRemoveFromContainerChange(
              it, indexInContainer(it, indicesInContainer)));
        }
        // elements in their own resource (cross-resource containment) survive the deletion
        if (!isRootOfOwnResource(it)) {
          deleteChanges.add(this.converter.createDeleteChange(it));
        }
      }
    }
    Iterables.<EChange<EObject>>addAll(changes, deleteChanges);
  }

  /**
   * Returns the index of the given element in its many-valued containment reference, or -1 if
   * that reference is single-valued. Indexes the whole list on the first lookup in it, so that
   * looking up all elements of a list takes linear instead of quadratic time.
   */
  private static int indexInContainer(final EObject element,
      final Map<EObject, Integer> indicesInContainer) {
    final EReference containmentFeature = element.eContainmentFeature();
    if (!containmentFeature.isMany()) {
      return -1;
    }
    final Integer knownIndex = indicesInContainer.get(element);
    if (knownIndex != null) {
      return knownIndex;
    }
    final List<?> siblings = (List<?>) element.eContainer().eGet(containmentFeature);
    for (int index = 0; index < siblings.size(); index++) {
      indicesInContainer.put((EObject) siblings.get(index), index);
    }
    return indicesInContainer.get(element);
  }

  /**
   * Collects all elements contained in the given one in pre-order. Elements that are a root of
   * their own resource are included, but not their contents, as they are not deleted together
   * with their container.
   */
  private static List<EObject> collectContainedElements(final EObject element) {
    final List<EObject> containedElements = new ArrayList<>();
    final TreeIterator<EObject> iterator = element.eAllContents();
    while (iterator.hasNext()) {
      final EObject containedElement = iterator.next();
      containedElements.add(containedElement);
      if (isRootOfOwnResource(containedElement)) {
        iterator.prune();
      }
    }
    return containedElements;
  }

  private static boolean isRootOfOwnResource(final EObject element) {
    return ((InternalEObject) element).eDirectResource() != null;
  }

  /**
   * Returns the recorded changes. Recording must be stopped first.
   *
   * @return the recorded changes as a {@link TransactionalChange}
   */

  public TransactionalChange<EObject> getChange() {
    this.checkNotDisposed();
    Preconditions.checkState((!this.isRecording), "This recorder is still recording!");
    return VitruviusChangeFactory.getInstance()
              .createTransactionalChange(this.resultChanges);
  }

  public boolean isRecording() {
    return this.isRecording;
  }

  private static void recursivelyAction(final ResourceSet resourceSet,
                                        final Predicate<Notifier> action) {
    if (action.test(resourceSet)) {
      resourceSet.getResources().forEach(resource -> ChangeRecorder.recursively(resource, action));
    }
  }

  private static void recursivelyAction(final Resource resource,
                                        final Predicate<Notifier> action) {
    if (action.test(resource)) {
      resource.getContents().forEach(eObject -> ChangeRecorder.recursively(eObject, action));
    }
  }

  private static void recursivelyAction(final EObject object,
                                        final Predicate<Notifier> action) {
    if (action.test(object)) {
      for (final TreeIterator<Notifier> properContents =
           EcoreUtil.getAllProperContents(object, true); properContents.hasNext();) {
        if (!action.test(properContents.next())) {
          properContents.prune();
        }
      }
    }
  }

  private boolean removeAdapter(final Notifier notifier) {
    return ((!this.rootObjects.contains(notifier))
            && notifier.eAdapters().remove(this.recordingAdapter));
  }

  /**
   * Stops recording a deleted element and forgets it, so that it can be garbage collected.
   * Elements added to the recording explicitly stay recorded.
   *
   * @return whether recording the element was stopped, i.e., whether its contents have to be
   *     handled too
   */
  private boolean stopRecordingDeleted(final Notifier notifier) {
    if (!this.removeAdapter(notifier)) {
      return false;
    }
    if (notifier instanceof EObject) {
      this.existingObjects.remove(notifier);
    }
    return true;
  }

  private boolean addAdapter(final Notifier notifier) {
    boolean xblockexpression = false;

    final EList<Adapter> eAdapters = notifier.eAdapters();
    xblockexpression = ((!eAdapters.contains(this.recordingAdapter))
            && eAdapters.add(this.recordingAdapter));

    return xblockexpression;
  }

  private boolean isInOurResourceSet(final Notifier notifier) {
    switch (notifier) {
      case null -> {
        return true;
      }
      case EObject eObject -> {
        return this.isInOurResourceSet(eObject.eResource());
      }
      case Resource resource -> {
        return this.isInOurResourceSet(resource.getResourceSet());
      }
      case ResourceSet set -> {
        return Objects.equals(notifier, this.resourceSet);
      }
      default -> {
        //default
      }
    }
    String simpleName = notifier.getClass().getSimpleName();
    String message = ("Unexpected notifier type: " + simpleName);
    throw new IllegalStateException(message);
  }

  private static void recursively(final Notifier object, final Predicate<Notifier> action) {
    switch (object) {
      case EObject eObject when action != null -> recursivelyAction(eObject, action);
      case Resource resource when action != null -> recursivelyAction(resource, action);
      case ResourceSet set when action != null -> recursivelyAction(set, action);
      case null, default -> throw new IllegalArgumentException("Unhandled parameter types: "
              + Arrays.asList(object, action).toString());
    }
  }
}
