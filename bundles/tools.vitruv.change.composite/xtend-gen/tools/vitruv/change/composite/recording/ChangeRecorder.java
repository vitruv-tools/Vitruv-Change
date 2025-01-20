package tools.vitruv.change.composite.recording;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
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
 * Recording can be started with {@link #beginRecording} and ended with {@link #endRecording}. The recorder assumes
 * that all objects that have been removed from their containment reference without being added to a new containment
 * reference while changes were being recorded have been deleted, resulting in an appropriate delete change.
 * The recorder considers resources being loaded as existing and does thus not produce changes for it.
 * 
 * Does not record changes of the <code>xmi:id</code> tag in an
 * {@link org.eclipse.emf.ecore.xmi.XMLResource XMLResource} if it is not stored in the element
 * but directly in the <code>Resource</code>.
 */
@SuppressWarnings("all")
public class ChangeRecorder implements AutoCloseable {
  @FinalFieldsConstructor
  private static class NotificationRecorder implements Adapter {
    @Extension
    private final ChangeRecorder outer;

    private final Set<Resource> currentlyLoadingResources = new HashSet<Resource>();

    @Override
    public void notifyChanged(final Notification notification) {
      this.handleAdaptersForResourceAndResourceSetChanges(notification);
      final Iterable<? extends EChange<EObject>> newChanges = this.extractRelevantChanges(notification);
      if (this.outer.isRecording) {
        Iterables.<EChange<EObject>>addAll(this.outer.resultChanges, newChanges);
      }
    }

    private void handleAdaptersForResourceAndResourceSetChanges(final Notification notification) {
      if (((notification.getNotifier() instanceof ResourceSet) && 
        (notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES))) {
        int _eventType = notification.getEventType();
        switch (_eventType) {
          case Notification.ADD:
            Object _newValue = notification.getNewValue();
            this.startLoadingResource(((Resource) _newValue));
            break;
          case Notification.ADD_MANY:
            Object _newValue_1 = notification.getNewValue();
            final Consumer<Resource> _function = (Resource it) -> {
              this.startLoadingResource(it);
            };
            ((Iterable<? extends Resource>) _newValue_1).forEach(_function);
            break;
        }
      }
      Object _feature = notification.getFeature();
      final Object feature = _feature;
      boolean _matched = false;
      if (feature instanceof EReference) {
        boolean _isContainment = ((EReference)feature).isContainment();
        if (_isContainment) {
          _matched=true;
        }
      }
      if (!_matched) {
        if (((notification.getNotifier() instanceof Resource) && 
          (notification.getFeatureID(Resource.class) == Resource.RESOURCE__CONTENTS))) {
          _matched=true;
        }
      }
      if (!_matched) {
        if (((notification.getNotifier() instanceof ResourceSet) && 
          (notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES))) {
          _matched=true;
        }
      }
      if (_matched) {
        int _eventType_1 = notification.getEventType();
        switch (_eventType_1) {
          case Notification.SET:
          case Notification.REMOVE:
            this.desinfect(notification.getOldValue());
            break;
          case Notification.REMOVE_MANY:
            Object _oldValue = notification.getOldValue();
            final Consumer<Object> _function_1 = (Object it) -> {
              this.desinfect(it);
            };
            ((Iterable<?>) _oldValue).forEach(_function_1);
            break;
        }
        int _eventType_2 = notification.getEventType();
        switch (_eventType_2) {
          case Notification.ADD:
          case Notification.SET:
            this.infect(notification.getNewValue());
            break;
          case Notification.ADD_MANY:
            Object _newValue_2 = notification.getNewValue();
            final Consumer<Object> _function_2 = (Object it) -> {
              this.infect(it);
            };
            ((Iterable<?>) _newValue_2).forEach(_function_2);
            break;
        }
      }
      if (!_matched) {
        if (((notification.getNotifier() instanceof Resource) && 
          (notification.getFeatureID(Resource.class) == Resource.RESOURCE__IS_LOADED))) {
          _matched=true;
          Object _notifier = notification.getNotifier();
          this.finishLoadingResource(((Resource) _notifier));
        }
      }
    }

    private Iterable<? extends EChange<EObject>> extractRelevantChanges(final Notification notification) {
      Iterable<? extends EChange<EObject>> _xifexpression = null;
      if ((this.affectsLoadingResource(notification) || this.affectsUnloadingResource(notification))) {
        _xifexpression = CollectionLiterals.emptyList();
      } else {
        NotificationInfo _notificationInfo = new NotificationInfo(notification);
        _xifexpression = this.outer.converter.convert(_notificationInfo);
      }
      final Iterable<? extends EChange<EObject>> changes = _xifexpression;
      boolean _isEmpty = IterableExtensions.isEmpty(changes);
      boolean _not = (!_isEmpty);
      if (_not) {
        final Consumer<EChange<EObject>> _function = (EChange<EObject> it) -> {
          boolean _matched = false;
          if (it instanceof EObjectAddedEChange) {
            _matched=true;
            EObject _newValue = ((EObjectAddedEChange<EObject>)it).getNewValue();
            this.outer.existingObjects.add(_newValue);
            boolean _matched_1 = false;
            if (it instanceof UpdateReferenceEChange) {
              _matched_1=true;
              EObject _affectedElement = ((UpdateReferenceEChange<EObject>)it).getAffectedElement();
              this.outer.existingObjects.add(_affectedElement);
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
      final Function1<Notifier, Boolean> _function = (Notifier it) -> {
        boolean _xblockexpression = false;
        {
          if ((it instanceof EObject)) {
            this.outer.existingObjects.add(((EObject)it));
          }
          _xblockexpression = this.outer.addAdapter(it);
        }
        return Boolean.valueOf(_xblockexpression);
      };
      ChangeRecorder.recursively(resource, _function);
    }

    private boolean affectsLoadingResource(final Notification notification) {
      EObject _xifexpression = null;
      Object _newValue = notification.getNewValue();
      if ((_newValue instanceof EObject)) {
        Object _newValue_1 = notification.getNewValue();
        _xifexpression = ((EObject) _newValue_1);
      } else {
        _xifexpression = null;
      }
      final EObject newEObject = _xifexpression;
      boolean _and = false;
      Resource _eResource = null;
      if (newEObject!=null) {
        _eResource=newEObject.eResource();
      }
      boolean _tripleNotEquals = (_eResource != null);
      if (!_tripleNotEquals) {
        _and = false;
      } else {
        boolean _contains = this.currentlyLoadingResources.contains(newEObject.eResource());
        _and = _contains;
      }
      return _and;
    }

    private boolean affectsUnloadingResource(final Notification notification) {
      Object _notifier = notification.getNotifier();
      if ((_notifier instanceof Resource)) {
        Object _notifier_1 = notification.getNotifier();
        final Resource resource = ((Resource) _notifier_1);
        final ResourceSet resourceSet = resource.getResourceSet();
        return (((!resource.isLoaded()) && (resourceSet != null)) && 
          resourceSet.getURIConverter().exists(resource.getURI(), CollectionLiterals.<Object, Object>emptyMap()));
      } else {
        return false;
      }
    }

    private void infect(final Object newValue) {
      if (((Notifier) newValue)!=null) {
        final Function1<Notifier, Boolean> _function = (Notifier it) -> {
          boolean _xblockexpression = false;
          {
            this.outer.toDesinfect.remove(it);
            _xblockexpression = this.outer.addAdapter(it);
          }
          return Boolean.valueOf(_xblockexpression);
        };
        ChangeRecorder.recursively(((Notifier) newValue), _function);
      }
    }

    private boolean desinfect(final Object oldValue) {
      boolean _xifexpression = false;
      if ((oldValue instanceof Notifier)) {
        _xifexpression = this.outer.toDesinfect.add(((Notifier)oldValue));
      }
      return _xifexpression;
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
    }

    public NotificationRecorder(final ChangeRecorder outer) {
      super();
      this.outer = outer;
    }
  }

  private final ChangeRecorder.NotificationRecorder recordingAdapter = new ChangeRecorder.NotificationRecorder(this);

  private final Set<Notifier> rootObjects = new HashSet<Notifier>();

  private boolean isRecording = false;

  private List<EChange<EObject>> resultChanges = CollectionLiterals.<EChange<EObject>>emptyList();

  private final NotificationToEChangeConverter converter;

  private final Set<EObject> existingObjects = new HashSet<EObject>();

  private final Set<Notifier> toDesinfect = new HashSet<Notifier>();

  private final ResourceSet resourceSet;

  public ChangeRecorder(final ResourceSet resourceSet) {
    this.resourceSet = resourceSet;
    final Function2<EObject, EObject, Boolean> _function = (EObject affectedObject, EObject addedObject) -> {
      return Boolean.valueOf(this.isCreateChange(affectedObject, addedObject));
    };
    NotificationToEChangeConverter _notificationToEChangeConverter = new NotificationToEChangeConverter(_function);
    this.converter = _notificationToEChangeConverter;
  }

  private boolean isCreateChange(final EObject affectedObject, final EObject addedObject) {
    boolean create = ((addedObject != null) && (!this.existingObjects.contains(addedObject)));
    create = (create && (((addedObject.eResource() == null) || (affectedObject == null)) || 
      Objects.equal(addedObject.eResource(), affectedObject.eResource())));
    if (create) {
      this.existingObjects.add(addedObject);
    }
    return create;
  }

  /**
   * Add the given elements and all its contained elements ({@link Resource}s, {@link EObject}s) to the recorder.
   * 
   * @param notifier - the {@link Notifier} to add the recorder to
   * @throws IllegalStateException if the recorder is already disposed
   */
  public void addToRecording(final Notifier notifier) {
    this.checkNotDisposed();
    Preconditions.<Notifier>checkNotNull(notifier, "notifier");
    Preconditions.checkArgument(this.isInOurResourceSet(notifier), 
      "cannot record changes in a different resource set!");
    boolean _add = this.rootObjects.add(notifier);
    if (_add) {
      final Function1<Notifier, Boolean> _function = (Notifier it) -> {
        boolean _xblockexpression = false;
        {
          if ((it instanceof EObject)) {
            this.existingObjects.add(((EObject)it));
          }
          _xblockexpression = this.addAdapter(it);
        }
        return Boolean.valueOf(_xblockexpression);
      };
      ChangeRecorder.recursively(notifier, _function);
    }
  }

  /**
   * Removes the given elements and all its contained elements (resources, EObjects) from the recorder.
   * @param notifier - the {@link Notifier} to remove the recorder from
   */
  public void removeFromRecording(final Notifier notifier) {
    this.checkNotDisposed();
    Preconditions.<Notifier>checkNotNull(notifier, "notifier");
    this.rootObjects.remove(notifier);
    final Function1<Notifier, Boolean> _function = (Notifier it) -> {
      return Boolean.valueOf(this.removeAdapter(it));
    };
    ChangeRecorder.recursively(notifier, _function);
  }

  /**
   * Starts recording changes on the registered elements.
   */
  public List<EChange<EObject>> beginRecording() {
    List<EChange<EObject>> _xblockexpression = null;
    {
      this.checkNotDisposed();
      Preconditions.checkState((!this.isRecording), "This recorder is already recording!");
      final Consumer<Notifier> _function = (Notifier it) -> {
        final Function1<Notifier, Boolean> _function_1 = (Notifier it_1) -> {
          return Boolean.valueOf(this.removeAdapter(it_1));
        };
        ChangeRecorder.recursively(it, _function_1);
      };
      this.toDesinfect.forEach(_function);
      this.toDesinfect.clear();
      this.isRecording = true;
      ArrayList<EChange<EObject>> _arrayList = new ArrayList<EChange<EObject>>();
      _xblockexpression = this.resultChanges = _arrayList;
    }
    return _xblockexpression;
  }

  @Override
  public void close() {
    this.isRecording = false;
    this.resultChanges = null;
    final Set<Notifier> rootCopy = Set.<Notifier>copyOf(this.rootObjects);
    this.rootObjects.clear();
    this.existingObjects.clear();
    final Consumer<Notifier> _function = (Notifier it) -> {
      final Function1<Notifier, Boolean> _function_1 = (Notifier it_1) -> {
        return Boolean.valueOf(this.removeAdapter(it_1));
      };
      ChangeRecorder.recursively(it, _function_1);
    };
    rootCopy.forEach(_function);
  }

  private void checkNotDisposed() {
    Preconditions.checkState((this.resultChanges != null), "This recorder has already been disposed!");
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
    this.resultChanges = List.<EChange<EObject>>copyOf(this.postprocessRemovals(this.resultChanges));
    return this.getChange();
  }

  /**
   * Creates {@link DeleteEObject} changes for every element implicitly deleted in the change
   * sequence and all of its contained elements. The delete changes are appended at the end
   * of the list. Contained elements are deleted before their container.
   */
  private List<EChange<EObject>> postprocessRemovals(final List<EChange<EObject>> changes) {
    boolean _isEmpty = changes.isEmpty();
    if (_isEmpty) {
      return changes;
    }
    final Set<EObject> removedElements = new HashSet<EObject>();
    for (final EChange<EObject> eChange : changes) {
      {
        boolean _matched = false;
        if (eChange instanceof EObjectSubtractedEChange) {
          _matched=true;
          boolean _isContainmentRemoval = EChangeUtil.isContainmentRemoval(eChange);
          if (_isContainmentRemoval) {
            EObject _oldValue = ((EObjectSubtractedEChange<EObject>)eChange).getOldValue();
            removedElements.add(_oldValue);
          }
        }
        boolean _matched_1 = false;
        if (eChange instanceof EObjectAddedEChange) {
          _matched_1=true;
          boolean _isContainmentInsertion = EChangeUtil.isContainmentInsertion(eChange);
          if (_isContainmentInsertion) {
            EObject _newValue = ((EObjectAddedEChange<EObject>)eChange).getNewValue();
            removedElements.remove(_newValue);
          }
        }
      }
    }
    boolean _isEmpty_1 = removedElements.isEmpty();
    boolean _not = (!_isEmpty_1);
    if (_not) {
      final Map<EObject, Iterable<EObject>> allElementsToDelete = new HashMap<EObject, Iterable<EObject>>();
      final Consumer<EObject> _function = (EObject element) -> {
        final Function1<Iterable<EObject>, Boolean> _function_1 = (Iterable<EObject> it) -> {
          return Boolean.valueOf(IterableExtensions.contains(it, element));
        };
        boolean _exists = IterableExtensions.<Iterable<EObject>>exists(allElementsToDelete.values(), _function_1);
        if (_exists) {
          return;
        }
        List<EObject> elementsToDelete = ListExtensions.<EObject>reverse(IteratorExtensions.<EObject>toList(element.eAllContents()));
        final Consumer<EObject> _function_2 = (EObject child) -> {
          boolean _containsKey = allElementsToDelete.containsKey(child);
          if (_containsKey) {
            allElementsToDelete.remove(child);
          }
        };
        elementsToDelete.forEach(_function_2);
        elementsToDelete.add(element);
        allElementsToDelete.put(element, elementsToDelete);
      };
      removedElements.forEach(_function);
      final Function1<Iterable<EObject>, Iterable<EChange<EObject>>> _function_1 = (Iterable<EObject> elementsToDelete) -> {
        final Function1<EObject, EChange<EObject>> _function_2 = (EObject it) -> {
          return this.converter.createDeleteChange(it);
        };
        return IterableExtensions.<EObject, EChange<EObject>>map(elementsToDelete, _function_2);
      };
      List<EChange<EObject>> _list = IterableExtensions.<EChange<EObject>>toList(IterableExtensions.<Iterable<EObject>, EChange<EObject>>flatMap(allElementsToDelete.values(), _function_1));
      Iterables.<EChange<EObject>>addAll(changes, _list);
    }
    return changes;
  }

  public TransactionalChange<EObject> getChange() {
    TransactionalChange<EObject> _xblockexpression = null;
    {
      this.checkNotDisposed();
      Preconditions.checkState((!this.isRecording), "This recorder is still recording!");
      _xblockexpression = VitruviusChangeFactory.getInstance().<EObject>createTransactionalChange(this.resultChanges);
    }
    return _xblockexpression;
  }

  public boolean isRecording() {
    return this.isRecording;
  }

  private static void _recursively(final ResourceSet resourceSet, final Function1<? super Notifier, ? extends Boolean> action) {
    Boolean _apply = action.apply(resourceSet);
    if ((_apply).booleanValue()) {
      final Consumer<Resource> _function = (Resource it) -> {
        ChangeRecorder.recursively(it, action);
      };
      resourceSet.getResources().forEach(_function);
    }
  }

  private static void _recursively(final Resource resource, final Function1<? super Notifier, ? extends Boolean> action) {
    Boolean _apply = action.apply(resource);
    if ((_apply).booleanValue()) {
      final Consumer<EObject> _function = (EObject it) -> {
        ChangeRecorder.recursively(it, action);
      };
      resource.getContents().forEach(_function);
    }
  }

  private static void _recursively(final EObject object, final Function1<? super Notifier, ? extends Boolean> action) {
    Boolean _apply = action.apply(object);
    if ((_apply).booleanValue()) {
      for (final TreeIterator<Notifier> properContents = EcoreUtil.<Notifier>getAllProperContents(object, true); properContents.hasNext();) {
        Boolean _apply_1 = action.apply(properContents.next());
        boolean _not = (!(_apply_1).booleanValue());
        if (_not) {
          properContents.prune();
        }
      }
    }
  }

  private boolean removeAdapter(final Notifier notifier) {
    return ((!this.rootObjects.contains(notifier)) && notifier.eAdapters().remove(this.recordingAdapter));
  }

  private boolean addAdapter(final Notifier notifier) {
    boolean _xblockexpression = false;
    {
      final EList<Adapter> eAdapters = notifier.eAdapters();
      _xblockexpression = ((!eAdapters.contains(this.recordingAdapter)) && eAdapters.add(this.recordingAdapter));
    }
    return _xblockexpression;
  }

  private boolean isInOurResourceSet(final Notifier notifier) {
    boolean _switchResult = false;
    boolean _matched = false;
    if (Objects.equal(notifier, null)) {
      _matched=true;
      _switchResult = true;
    }
    if (!_matched) {
      if (notifier instanceof EObject) {
        _matched=true;
        Resource _eResource = null;
        if (((EObject)notifier)!=null) {
          _eResource=((EObject)notifier).eResource();
        }
        _switchResult = this.isInOurResourceSet(_eResource);
      }
    }
    if (!_matched) {
      if (notifier instanceof Resource) {
        _matched=true;
        ResourceSet _resourceSet = null;
        if (((Resource)notifier)!=null) {
          _resourceSet=((Resource)notifier).getResourceSet();
        }
        _switchResult = this.isInOurResourceSet(_resourceSet);
      }
    }
    if (!_matched) {
      if (notifier instanceof ResourceSet) {
        _matched=true;
        _switchResult = Objects.equal(notifier, this.resourceSet);
      }
    }
    if (!_matched) {
      String _simpleName = notifier.getClass().getSimpleName();
      String _plus = ("Unexpected notifier type: " + _simpleName);
      throw new IllegalStateException(_plus);
    }
    return _switchResult;
  }

  private static void recursively(final Notifier object, final Function1<? super Notifier, ? extends Boolean> action) {
    if (object instanceof EObject
         && action != null) {
      _recursively((EObject)object, action);
      return;
    } else if (object instanceof Resource
         && action != null) {
      _recursively((Resource)object, action);
      return;
    } else if (object instanceof ResourceSet
         && action != null) {
      _recursively((ResourceSet)object, action);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(object, action).toString());
    }
  }
}
