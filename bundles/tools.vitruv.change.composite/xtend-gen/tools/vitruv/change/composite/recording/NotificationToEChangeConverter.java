package tools.vitruv.change.composite.recording;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Converts an EMF notification to an {@link EChange}.
 * @author Heiko Klare
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
final class NotificationToEChangeConverter {
  @Extension
  private final TypeInferringAtomicEChangeFactory changeFactory = TypeInferringAtomicEChangeFactory.getInstance();

  private final Function2<? super EObject, ? super EObject, ? extends Boolean> isCreateChange;

  public EChange<EObject> createDeleteChange(final EObject eObject) {
    return this.changeFactory.<EObject>createDeleteEObjectChange(eObject);
  }

  private String convertExceptionMessage(final EventType eventType, final String notificationType) {
    return String.format("Event type {} for {} Notifications unexpected.");
  }

  private final String ATTRIBUTE_TYPE = "Attribute";

  private final String REFERENCE_TYPE = "Reference";

  private final String RESOURCE_CONTENTS_TYPE = "Resource Contents";

  /**
   * Converts the given notification to a list of {@link EChange}s.
   * @param n the notification to convert
   * @return the  {@link Iterable} of {@link EChange}s
   */
  public Iterable<? extends EChange<EObject>> convert(@Extension final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _switchResult = null;
    boolean _matched = false;
    boolean _isTouch = notification.isTouch();
    if (_isTouch) {
      _matched=true;
    }
    if (!_matched) {
      boolean _isTransient = notification.isTransient();
      if (_isTransient) {
        _matched=true;
      }
    }
    if (!_matched) {
      Object _oldValue = notification.getOldValue();
      Object _newValue = notification.getNewValue();
      boolean _equals = Objects.equal(_oldValue, _newValue);
      if (_equals) {
        _matched=true;
      }
    }
    if (_matched) {
      _switchResult = CollectionLiterals.<EChange<EObject>>emptyList();
    }
    if (!_matched) {
      boolean _isAttributeNotification = notification.isAttributeNotification();
      if (_isAttributeNotification) {
        _matched=true;
        Iterable<? extends EChange<EObject>> _switchResult_1 = null;
        EventType _eventTypeEnum = notification.getEventTypeEnum();
        if (_eventTypeEnum != null) {
          switch (_eventTypeEnum) {
            case SET:
              _switchResult_1 = this.handleSetAttribute(notification);
              break;
            case UNSET:
              _switchResult_1 = this.handleUnsetAttribute(notification);
              break;
            case ADD:
              _switchResult_1 = this.handleInsertAttribute(notification);
              break;
            case ADD_MANY:
              _switchResult_1 = this.handleMultiInsertAttribute(notification);
              break;
            case REMOVE:
              _switchResult_1 = this.handleRemoveAttribute(notification);
              break;
            case REMOVE_MANY:
              _switchResult_1 = this.handleMultiRemoveAttribute(notification);
              break;
            case MOVE:
              _switchResult_1 = this.handleMoveAttribute(notification);
              break;
            case RESOLVE:
              String _convertExceptionMessage = this.convertExceptionMessage(EventType.RESOLVE, this.ATTRIBUTE_TYPE);
              throw new IllegalArgumentException(_convertExceptionMessage);
            case REMOVING_ADAPTER:
              String _convertExceptionMessage_1 = this.convertExceptionMessage(EventType.REMOVING_ADAPTER, this.ATTRIBUTE_TYPE);
              throw new IllegalArgumentException(_convertExceptionMessage_1);
            default:
              int _eventType = notification.getEventType();
              String _plus = ("Unexpected event type " + Integer.valueOf(_eventType));
              throw new IllegalArgumentException(_plus);
          }
        } else {
          int _eventType = notification.getEventType();
          String _plus = ("Unexpected event type " + Integer.valueOf(_eventType));
          throw new IllegalArgumentException(_plus);
        }
        _switchResult = _switchResult_1;
      }
    }
    if (!_matched) {
      boolean _isReferenceNotification = notification.isReferenceNotification();
      if (_isReferenceNotification) {
        _matched=true;
        Iterable<? extends EChange<EObject>> _switchResult_2 = null;
        EventType _eventTypeEnum_1 = notification.getEventTypeEnum();
        if (_eventTypeEnum_1 != null) {
          switch (_eventTypeEnum_1) {
            case SET:
              _switchResult_2 = this.handleSetReference(notification);
              break;
            case UNSET:
              _switchResult_2 = this.handleUnsetReference(notification);
              break;
            case ADD:
              _switchResult_2 = this.handleInsertReference(notification);
              break;
            case ADD_MANY:
              _switchResult_2 = this.handleMultiInsertReference(notification);
              break;
            case REMOVE:
              _switchResult_2 = this.handleRemoveReference(notification);
              break;
            case REMOVE_MANY:
              _switchResult_2 = this.handleMultiRemoveReference(notification);
              break;
            case MOVE:
              _switchResult_2 = this.handleMoveReference(notification);
              break;
            case RESOLVE:
              String _convertExceptionMessage_2 = this.convertExceptionMessage(EventType.RESOLVE, this.REFERENCE_TYPE);
              throw new IllegalArgumentException(_convertExceptionMessage_2);
            case REMOVING_ADAPTER:
              String _convertExceptionMessage_3 = this.convertExceptionMessage(EventType.REMOVING_ADAPTER, this.REFERENCE_TYPE);
              throw new IllegalArgumentException(_convertExceptionMessage_3);
            default:
              int _eventType_1 = notification.getEventType();
              String _plus_1 = ("Unexpected event type " + Integer.valueOf(_eventType_1));
              throw new IllegalArgumentException(_plus_1);
          }
        } else {
          int _eventType_1 = notification.getEventType();
          String _plus_1 = ("Unexpected event type " + Integer.valueOf(_eventType_1));
          throw new IllegalArgumentException(_plus_1);
        }
        _switchResult = _switchResult_2;
      }
    }
    if (!_matched) {
      Object _notifier = notification.getNotifier();
      if ((_notifier instanceof Resource)) {
        _matched=true;
        Iterable<? extends EChange<EObject>> _switchResult_3 = null;
        int _featureID = notification.getFeatureID(Resource.class);
        switch (_featureID) {
          case Resource.RESOURCE__CONTENTS:
            Iterable<? extends EChange<EObject>> _switchResult_4 = null;
            EventType _eventTypeEnum_2 = notification.getEventTypeEnum();
            if (_eventTypeEnum_2 != null) {
              switch (_eventTypeEnum_2) {
                case ADD:
                  _switchResult_4 = this.handleInsertRootChange(notification);
                  break;
                case ADD_MANY:
                  _switchResult_4 = this.handleMultiInsertRootChange(notification);
                  break;
                case REMOVE:
                  _switchResult_4 = this.handleRemoveRootChange(notification);
                  break;
                case REMOVE_MANY:
                  _switchResult_4 = this.handleMultiRemoveRootChange(notification);
                  break;
                case SET:
                  String _convertExceptionMessage_4 = this.convertExceptionMessage(EventType.SET, this.RESOURCE_CONTENTS_TYPE);
                  throw new IllegalArgumentException(_convertExceptionMessage_4);
                case UNSET:
                  String _convertExceptionMessage_5 = this.convertExceptionMessage(EventType.UNSET, this.RESOURCE_CONTENTS_TYPE);
                  throw new IllegalArgumentException(_convertExceptionMessage_5);
                case MOVE:
                  String _convertExceptionMessage_6 = this.convertExceptionMessage(EventType.MOVE, this.RESOURCE_CONTENTS_TYPE);
                  throw new IllegalArgumentException(_convertExceptionMessage_6);
                case RESOLVE:
                  String _convertExceptionMessage_7 = this.convertExceptionMessage(EventType.RESOLVE, this.RESOURCE_CONTENTS_TYPE);
                  throw new IllegalArgumentException(_convertExceptionMessage_7);
                case REMOVING_ADAPTER:
                  String _convertExceptionMessage_8 = this.convertExceptionMessage(EventType.REMOVING_ADAPTER, this.RESOURCE_CONTENTS_TYPE);
                  throw new IllegalArgumentException(_convertExceptionMessage_8);
                default:
                  int _eventType_2 = notification.getEventType();
                  String _plus_2 = ("Unexpected event type " + Integer.valueOf(_eventType_2));
                  throw new IllegalArgumentException(_plus_2);
              }
            } else {
              int _eventType_2 = notification.getEventType();
              String _plus_2 = ("Unexpected event type " + Integer.valueOf(_eventType_2));
              throw new IllegalArgumentException(_plus_2);
            }
            _switchResult_3 = _switchResult_4;
            break;
          case Resource.RESOURCE__URI:
            Iterable<? extends EChange<EObject>> _switchResult_5 = null;
            EventType _eventTypeEnum_3 = notification.getEventTypeEnum();
            if (_eventTypeEnum_3 != null) {
              switch (_eventTypeEnum_3) {
                case SET:
                  _switchResult_5 = this.handleSetUriChange(notification);
                  break;
                default:
                  int _eventType_3 = notification.getEventType();
                  String _plus_3 = ("Unexpected event type " + Integer.valueOf(_eventType_3));
                  String _plus_4 = (_plus_3 + " for Resource URI Notification.");
                  throw new IllegalArgumentException(_plus_4);
              }
            } else {
              int _eventType_3 = notification.getEventType();
              String _plus_3 = ("Unexpected event type " + Integer.valueOf(_eventType_3));
              String _plus_4 = (_plus_3 + " for Resource URI Notification.");
              throw new IllegalArgumentException(_plus_4);
            }
            _switchResult_3 = _switchResult_5;
            break;
          default:
            _switchResult_3 = CollectionLiterals.<EChange<EObject>>emptyList();
            break;
        }
        _switchResult = _switchResult_3;
      }
    }
    if (!_matched) {
      _switchResult = CollectionLiterals.<EChange<EObject>>emptyList();
    }
    return _switchResult;
  }

  private Iterable<? extends EChange<EObject>> handleMoveAttribute(@Extension final NotificationInfo notification) {
    Object _oldValue = notification.getOldValue();
    RemoveEAttributeValue<EObject, Object> _createRemoveAttributeChange = this.changeFactory.<EObject, Object>createRemoveAttributeChange(notification.getNotifierModelElement(), notification.getAttribute(), (((Integer) _oldValue)).intValue(), notification.getNewValue());
    InsertEAttributeValue<EObject, Object> _createInsertAttributeChange = this.changeFactory.<EObject, Object>createInsertAttributeChange(notification.getNotifierModelElement(), notification.getAttribute(), notification.getPosition(), notification.getNewValue());
    return Collections.<EChange<EObject>>unmodifiableList(CollectionLiterals.<EChange<EObject>>newArrayList(_createRemoveAttributeChange, _createInsertAttributeChange));
  }

  private Iterable<? extends EChange<EObject>> handleMoveReference(@Extension final NotificationInfo notification) {
    Object _oldValue = notification.getOldValue();
    RemoveEReference<EObject> _createRemoveReferenceChange = this.changeFactory.<EObject>createRemoveReferenceChange(notification.getNotifierModelElement(), notification.getReference(), notification.getNewModelElementValue(), (((Integer) _oldValue)).intValue());
    InsertEReference<EObject> _createInsertReferenceChange = this.changeFactory.<EObject>createInsertReferenceChange(notification.getNotifierModelElement(), notification.getReference(), notification.getNewModelElementValue(), notification.getPosition());
    return Collections.<EChange<EObject>>unmodifiableList(CollectionLiterals.<EChange<EObject>>newArrayList(_createRemoveReferenceChange, _createInsertReferenceChange));
  }

  private Iterable<? extends EChange<EObject>> handleSetAttribute(@Extension final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _switchResult = null;
    boolean _matched = false;
    boolean _isMany = notification.getAttribute().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _matched=true;
      _switchResult = this.handleReplaceAttribute(notification);
    }
    if (!_matched) {
      if (((notification.getOldValue() != null) && (notification.getNewValue() != null))) {
        _matched=true;
        List<EChange<EObject>> _handleRemoveAttribute = this.handleRemoveAttribute(notification);
        List<InsertEAttributeValue<EObject, Object>> _handleInsertAttribute = this.handleInsertAttribute(notification);
        _switchResult = Iterables.<EChange<EObject>>concat(_handleRemoveAttribute, _handleInsertAttribute);
      }
    }
    if (!_matched) {
      Object _newValue = notification.getNewValue();
      boolean _tripleNotEquals = (_newValue != null);
      if (_tripleNotEquals) {
        _matched=true;
        _switchResult = this.handleInsertAttribute(notification);
      }
    }
    if (!_matched) {
      Object _oldValue = notification.getOldValue();
      boolean _tripleNotEquals_1 = (_oldValue != null);
      if (_tripleNotEquals_1) {
        _matched=true;
        _switchResult = this.handleRemoveAttribute(notification);
      }
    }
    if (!_matched) {
      _switchResult = CollectionLiterals.<EChange<EObject>>emptyList();
    }
    return _switchResult;
  }

  private Iterable<? extends EChange<EObject>> handleSetReference(@Extension final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _switchResult = null;
    boolean _matched = false;
    boolean _isMany = notification.getReference().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _matched=true;
      _switchResult = this.handleReplaceReference(notification);
    }
    if (!_matched) {
      if (((notification.getOldValue() != null) && (notification.getNewValue() != null))) {
        _matched=true;
        Iterable<? extends EChange<EObject>> _handleRemoveReference = this.handleRemoveReference(notification);
        Iterable<? extends EChange<EObject>> _handleInsertReference = this.handleInsertReference(notification);
        _switchResult = Iterables.<EChange<EObject>>concat(_handleRemoveReference, _handleInsertReference);
      }
    }
    if (!_matched) {
      Object _newValue = notification.getNewValue();
      boolean _tripleNotEquals = (_newValue != null);
      if (_tripleNotEquals) {
        _matched=true;
        _switchResult = this.handleInsertReference(notification);
      }
    }
    if (!_matched) {
      Object _oldValue = notification.getOldValue();
      boolean _tripleNotEquals_1 = (_oldValue != null);
      if (_tripleNotEquals_1) {
        _matched=true;
        _switchResult = this.handleRemoveReference(notification);
      }
    }
    if (!_matched) {
      _switchResult = CollectionLiterals.<EChange<EObject>>emptyList();
    }
    return _switchResult;
  }

  private Iterable<? extends EChange<EObject>> handleUnsetAttribute(@Extension final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    boolean _isMany = notification.getAttribute().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _xifexpression = this.handleSetAttribute(notification);
    } else {
      _xifexpression = List.<UnsetFeature<EObject, EAttribute>>of(this.changeFactory.<EObject, EAttribute>createUnsetFeatureChange(notification.getNotifierModelElement(), notification.getAttribute()));
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleUnsetReference(@Extension final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    boolean _isMany = notification.getReference().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _xifexpression = this.handleSetReference(notification);
    } else {
      _xifexpression = List.<UnsetFeature<EObject, EReference>>of(this.changeFactory.<EObject, EReference>createUnsetFeatureChange(notification.getNotifierModelElement(), notification.getReference()));
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleReplaceAttribute(@Extension final NotificationInfo notification) {
    final ReplaceSingleValuedEAttribute<EObject, Object> change = AttributeFactory.eINSTANCE.<EObject, Object>createReplaceSingleValuedEAttribute();
    change.setOldValue(notification.getOldValue());
    change.setNewValue(notification.getNewValue());
    change.setAffectedFeature(notification.getAttribute());
    change.setAffectedElement(notification.getNotifierModelElement());
    change.setIsUnset(notification.wasUnset());
    return List.<ReplaceSingleValuedEAttribute<EObject, Object>>of(change);
  }

  private Iterable<? extends EChange<EObject>> handleReplaceReference(@Extension final NotificationInfo notification) {
    final ReplaceSingleValuedEReference<EObject> change = this.changeFactory.<EObject>createReplaceSingleReferenceChange(notification.getNotifierModelElement(), notification.getReference(), notification.getOldModelElementValue(), 
      notification.getNewModelElementValue());
    change.setIsUnset(notification.wasUnset());
    return this.surroundWithCreateAndFeatureChangesIfNecessary(change);
  }

  private List<EChange<EObject>> handleRemoveAttribute(@Extension final NotificationInfo notification) {
    return this.addUnsetChangeIfNecessary(this.changeFactory.<EObject, Object>createRemoveAttributeChange(notification.getNotifierModelElement(), notification.getAttribute(), notification.getPosition(), notification.getOldValue()), notification);
  }

  private Iterable<? extends EChange<EObject>> handleMultiRemoveAttribute(@Extension final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    Object _newValue = notification.getNewValue();
    boolean _tripleEquals = (_newValue == null);
    if (_tripleEquals) {
      Iterable<? extends EChange<EObject>> _xblockexpression = null;
      {
        Object _oldValue = notification.getOldValue();
        final List<?> oldValues = ((List<?>) _oldValue);
        final Function2<Integer, Object, RemoveEAttributeValue<EObject, Object>> _function = (Integer index, Object value) -> {
          RemoveEAttributeValue<EObject, Object> _xblockexpression_1 = null;
          {
            int _initialIndex = notification.getInitialIndex();
            int _size = oldValues.size();
            int _plus = (_initialIndex + _size);
            int _minus = (_plus - 1);
            final int valueIndex = (_minus - (index).intValue());
            _xblockexpression_1 = this.changeFactory.<EObject, Object>createRemoveAttributeChange(notification.getNotifierModelElement(), notification.getAttribute(), valueIndex, value);
          }
          return _xblockexpression_1;
        };
        _xblockexpression = this.<RemoveEAttributeValue<EObject, Object>>addUnsetChangeIfNecessary(IterableUtil.mapFixedIndexed(ListExtensions.reverseView(oldValues), _function), notification);
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = this.unsetChangeOrEmpty(notification);
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleRemoveReference(@Extension final NotificationInfo notification) {
    return this.addUnsetChangeIfNecessary(this.changeFactory.<EObject>createRemoveReferenceChange(notification.getNotifierModelElement(), notification.getReference(), notification.getOldModelElementValue(), notification.getPosition()), notification);
  }

  private Iterable<? extends EChange<EObject>> handleMultiRemoveReference(@Extension final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    Object _newValue = notification.getNewValue();
    boolean _tripleEquals = (_newValue == null);
    if (_tripleEquals) {
      Iterable<? extends EChange<EObject>> _xblockexpression = null;
      {
        Object _oldValue = notification.getOldValue();
        final List<EObject> oldValues = ((List<EObject>) _oldValue);
        final Function2<Integer, EObject, RemoveEReference<EObject>> _function = (Integer index, EObject value) -> {
          RemoveEReference<EObject> _xblockexpression_1 = null;
          {
            int _initialIndex = notification.getInitialIndex();
            int _size = oldValues.size();
            int _plus = (_initialIndex + _size);
            int _minus = (_plus - 1);
            final int valueIndex = (_minus - (index).intValue());
            _xblockexpression_1 = this.changeFactory.<EObject>createRemoveReferenceChange(notification.getNotifierModelElement(), notification.getReference(), value, valueIndex);
          }
          return _xblockexpression_1;
        };
        _xblockexpression = this.<RemoveEReference<EObject>>addUnsetChangeIfNecessary(IterableUtil.<EObject, RemoveEReference<EObject>>mapFixedIndexed(ListExtensions.<EObject>reverseView(oldValues), _function), notification);
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = this.unsetChangeOrEmpty(notification);
    }
    return _xifexpression;
  }

  private List<InsertEAttributeValue<EObject, Object>> handleInsertAttribute(@Extension final NotificationInfo notification) {
    return List.<InsertEAttributeValue<EObject, Object>>of(this.changeFactory.<EObject, Object>createInsertAttributeChange(notification.getNotifierModelElement(), notification.getAttribute(), notification.getPosition(), notification.getNewValue()));
  }

  private List<InsertEAttributeValue<EObject, Object>> handleMultiInsertAttribute(@Extension final NotificationInfo notification) {
    Object _newValue = notification.getNewValue();
    final Function2<Integer, Object, InsertEAttributeValue<EObject, Object>> _function = (Integer index, Object value) -> {
      EObject _notifierModelElement = notification.getNotifierModelElement();
      EAttribute _attribute = notification.getAttribute();
      int _initialIndex = notification.getInitialIndex();
      int _plus = (_initialIndex + (index).intValue());
      return this.changeFactory.<EObject, Object>createInsertAttributeChange(_notifierModelElement, _attribute, _plus, value);
    };
    return IterableUtil.mapFixedIndexed(((List<?>) _newValue), _function);
  }

  private Iterable<? extends EChange<EObject>> handleInsertReference(@Extension final NotificationInfo notification) {
    return this.surroundWithCreateAndFeatureChangesIfNecessary(this.changeFactory.<EObject>createInsertReferenceChange(notification.getNotifierModelElement(), notification.getReference(), notification.getNewModelElementValue(), notification.getPosition()));
  }

  private Iterable<? extends EChange<EObject>> handleMultiInsertReference(@Extension final NotificationInfo notification) {
    Object _newValue = notification.getNewValue();
    final Function2<Integer, EObject, Iterable<? extends EChange<EObject>>> _function = (Integer index, EObject value) -> {
      EObject _notifierModelElement = notification.getNotifierModelElement();
      EReference _reference = notification.getReference();
      int _initialIndex = notification.getInitialIndex();
      int _plus = (_initialIndex + (index).intValue());
      return this.surroundWithCreateAndFeatureChangesIfNecessary(this.changeFactory.<EObject>createInsertReferenceChange(_notifierModelElement, _reference, value, _plus));
    };
    return IterableUtil.<EObject, EChange<EObject>>flatMapFixedIndexed(((List<EObject>) _newValue), _function);
  }

  private Iterable<? extends EChange<EObject>> handleInsertRootChange(@Extension final NotificationInfo notification) {
    return this.surroundWithCreateAndFeatureChangesIfNecessary(this.changeFactory.<EObject>createInsertRootChange(notification.getNewModelElementValue(), notification.getNotifierResource(), notification.getPosition()));
  }

  private List<EChange<EObject>> handleMultiInsertRootChange(@Extension final NotificationInfo notification) {
    Object _newValue = notification.getNewValue();
    final Function2<Integer, EObject, Iterable<? extends EChange<EObject>>> _function = (Integer index, EObject value) -> {
      Resource _notifierResource = notification.getNotifierResource();
      int _initialIndex = notification.getInitialIndex();
      int _plus = (_initialIndex + (index).intValue());
      return this.surroundWithCreateAndFeatureChangesIfNecessary(this.changeFactory.<EObject>createInsertRootChange(value, _notifierResource, _plus));
    };
    return IterableUtil.<EObject, EChange<EObject>>flatMapFixedIndexed(((List<EObject>) _newValue), _function);
  }

  private List<RemoveRootEObject<EObject>> handleRemoveRootChange(@Extension final NotificationInfo notification) {
    return List.<RemoveRootEObject<EObject>>of(this.changeFactory.<EObject>createRemoveRootChange(notification.getOldModelElementValue(), notification.getNotifierResource(), notification.getPosition()));
  }

  private List<RemoveRootEObject<EObject>> handleMultiRemoveRootChange(@Extension final NotificationInfo notification) {
    List<RemoveRootEObject<EObject>> _xblockexpression = null;
    {
      Object _oldValue = notification.getOldValue();
      final List<EObject> oldValues = ((List<EObject>) _oldValue);
      final Function2<Integer, EObject, RemoveRootEObject<EObject>> _function = (Integer index, EObject value) -> {
        RemoveRootEObject<EObject> _xblockexpression_1 = null;
        {
          int _initialIndex = notification.getInitialIndex();
          int _size = oldValues.size();
          int _plus = (_initialIndex + _size);
          int _minus = (_plus - 1);
          final int valueIndex = (_minus - (index).intValue());
          _xblockexpression_1 = this.changeFactory.<EObject>createRemoveRootChange(value, notification.getNotifierResource(), valueIndex);
        }
        return _xblockexpression_1;
      };
      _xblockexpression = IterableUtil.<EObject, RemoveRootEObject<EObject>>mapFixedIndexed(ListExtensions.<EObject>reverseView(oldValues), _function);
    }
    return _xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> handleSetUriChange(@Extension final NotificationInfo notification) {
    Iterable<EChange<EObject>> _xblockexpression = null;
    {
      Object _oldValue = notification.getOldValue();
      final URI oldUri = ((URI) _oldValue);
      final Function2<Integer, EObject, RemoveRootEObject<EObject>> _function = (Integer index, EObject value) -> {
        RemoveRootEObject<EObject> _xblockexpression_1 = null;
        {
          int _initialIndex = notification.getInitialIndex();
          int _size = notification.getNotifierResource().getContents().size();
          int _plus = (_initialIndex + _size);
          int _minus = (_plus - 1);
          final int valueIndex = (_minus - (index).intValue());
          final Resource oldResource = notification.getNotifierResource().getResourceSet().createResource(oldUri);
          _xblockexpression_1 = this.changeFactory.<EObject>createRemoveRootChange(value, oldResource, oldUri, valueIndex);
        }
        return _xblockexpression_1;
      };
      List<RemoveRootEObject<EObject>> _mapFixedIndexed = IterableUtil.<EObject, RemoveRootEObject<EObject>>mapFixedIndexed(notification.getNotifierResource().getContents(), _function);
      final Function2<Integer, EObject, Iterable<? extends EChange<EObject>>> _function_1 = (Integer index, EObject value) -> {
        Resource _notifierResource = notification.getNotifierResource();
        int _initialIndex = notification.getInitialIndex();
        int _plus = (_initialIndex + (index).intValue());
        return this.surroundWithCreateAndFeatureChangesIfNecessary(this.changeFactory.<EObject>createInsertRootChange(value, _notifierResource, _plus));
      };
      List<EChange<EObject>> _flatMapFixedIndexed = IterableUtil.<EObject, EChange<EObject>>flatMapFixedIndexed(notification.getNotifierResource().getContents(), _function_1);
      _xblockexpression = Iterables.<EChange<EObject>>concat(_mapFixedIndexed, _flatMapFixedIndexed);
    }
    return _xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> allAdditiveChangesForChangeRelevantFeatures(final EObjectAddedEChange<EObject> change, final EObject eObject) {
    final Function2<EObject, EAttribute, Iterable<? extends EChange<EObject>>> _function = (EObject object, EAttribute attribute) -> {
      return EChangeCreationUtil.<EObject>createAdditiveEChangeForAttribute(object, attribute);
    };
    final Function2<EObject, EReference, Iterable<? extends EChange<EObject>>> _function_1 = (EObject object, EReference reference) -> {
      List<? extends EChange<EObject>> _xifexpression = null;
      boolean _isContainment = reference.isContainment();
      if (_isContainment) {
        final Function1<EObject, Boolean> _function_2 = (EObject referencedObject) -> {
          return this.isCreateChange.apply(object, referencedObject);
        };
        _xifexpression = EChangeCreationUtil.createAdditiveEChangeForReferencedObject(object, reference, _function_2);
      }
      return _xifexpression;
    };
    Iterable<? extends EChange<EObject>> _walkChangeRelevantFeatures = NotificationToEChangeConverter.walkChangeRelevantFeatures(change.getNewValue(), _function, _function_1);
    final Function2<EObject, EReference, Iterable<? extends EChange<EObject>>> _function_2 = (EObject object, EReference reference) -> {
      List<? extends EChange<EObject>> _xifexpression = null;
      boolean _isContainment = reference.isContainment();
      boolean _not = (!_isContainment);
      if (_not) {
        final Function1<EObject, Boolean> _function_3 = (EObject it) -> {
          return Boolean.valueOf(false);
        };
        _xifexpression = EChangeCreationUtil.createAdditiveEChangeForReferencedObject(object, reference, _function_3);
      }
      return _xifexpression;
    };
    Iterable<? extends EChange<EObject>> _walkChangeRelevantFeatures_1 = NotificationToEChangeConverter.walkChangeRelevantFeatures(change.getNewValue(), null, _function_2);
    return Iterables.<EChange<EObject>>concat(_walkChangeRelevantFeatures, _walkChangeRelevantFeatures_1);
  }

  private static Iterable<? extends EChange<EObject>> walkChangeRelevantFeatures(final EObject eObject, final Function2<? super EObject, ? super EAttribute, ? extends Iterable<? extends EChange<EObject>>> attributeVisitor, final Function2<? super EObject, ? super EReference, ? extends Iterable<? extends EChange<EObject>>> referenceVisitor) {
    Iterable<EChange<EObject>> _xblockexpression = null;
    {
      final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
        return Boolean.valueOf(EChangeCreationUtil.hasChangeableUnderivedPersistedNotContainingNonDefaultValue(eObject, it));
      };
      final Iterable<EStructuralFeature> changeRelevantFeatures = IterableExtensions.<EStructuralFeature>filter(eObject.eClass().getEAllStructuralFeatures(), _function);
      final Function1<EAttribute, Iterable<? extends EChange<EObject>>> _function_1 = (EAttribute it) -> {
        Iterable<? extends EChange<EObject>> _elvis = null;
        Iterable<? extends EChange<EObject>> _apply = null;
        if (attributeVisitor!=null) {
          _apply=attributeVisitor.apply(eObject, it);
        }
        if (_apply != null) {
          _elvis = _apply;
        } else {
          List<? extends EChange<EObject>> _emptyList = CollectionLiterals.emptyList();
          _elvis = _emptyList;
        }
        return _elvis;
      };
      final List<EChange<EObject>> thisLayerAttributeResults = IterableUtil.<EAttribute, EChange<EObject>>flatMapFixed(Iterables.<EAttribute>filter(changeRelevantFeatures, EAttribute.class), _function_1);
      final Function1<EReference, Iterable<? extends EChange<EObject>>> _function_2 = (EReference it) -> {
        Iterable<? extends EChange<EObject>> _elvis = null;
        Iterable<? extends EChange<EObject>> _apply = null;
        if (referenceVisitor!=null) {
          _apply=referenceVisitor.apply(eObject, it);
        }
        if (_apply != null) {
          _elvis = _apply;
        } else {
          List<? extends EChange<EObject>> _emptyList = CollectionLiterals.emptyList();
          _elvis = _emptyList;
        }
        return _elvis;
      };
      final List<EChange<EObject>> thisLayerReferenceResults = IterableUtil.<EReference, EChange<EObject>>flatMapFixed(Iterables.<EReference>filter(changeRelevantFeatures, EReference.class), _function_2);
      final Function1<EReference, Boolean> _function_3 = (EReference it) -> {
        return Boolean.valueOf(it.isContainment());
      };
      final Function1<EReference, Iterable<EObject>> _function_4 = (EReference it) -> {
        return NotificationToEChangeConverter.getReferencedElements(eObject, it);
      };
      final Function1<EObject, Iterable<? extends EChange<EObject>>> _function_5 = (EObject it) -> {
        return NotificationToEChangeConverter.walkChangeRelevantFeatures(it, attributeVisitor, referenceVisitor);
      };
      final List<EChange<EObject>> nextLayer = IterableUtil.<EObject, EChange<EObject>>flatMapFixed(IterableExtensions.<EReference, EObject>flatMap(IterableExtensions.<EReference>filter(Iterables.<EReference>filter(changeRelevantFeatures, EReference.class), _function_3), _function_4), _function_5);
      Iterable<EChange<EObject>> _plus = Iterables.<EChange<EObject>>concat(thisLayerAttributeResults, thisLayerReferenceResults);
      _xblockexpression = Iterables.<EChange<EObject>>concat(_plus, nextLayer);
    }
    return _xblockexpression;
  }

  private static Iterable<EObject> getReferencedElements(final EObject eObject, final EReference reference) {
    Iterable<EObject> _xifexpression = null;
    boolean _isMany = reference.isMany();
    if (_isMany) {
      Object _eGet = eObject.eGet(reference);
      _xifexpression = ((Iterable<EObject>) _eGet);
    } else {
      Object _eGet_1 = eObject.eGet(reference);
      _xifexpression = List.<EObject>of(((EObject) _eGet_1));
    }
    return _xifexpression;
  }

  private List<UnsetFeature<EObject, EStructuralFeature>> unsetChangeOrEmpty(final NotificationInfo notification) {
    List<UnsetFeature<EObject, EStructuralFeature>> _xifexpression = null;
    boolean _wasUnset = notification.wasUnset();
    if (_wasUnset) {
      _xifexpression = List.<UnsetFeature<EObject, EStructuralFeature>>of(this.changeFactory.<EObject, EStructuralFeature>createUnsetFeatureChange(notification.getNotifierModelElement(), notification.getStructuralFeature()));
    } else {
      _xifexpression = CollectionLiterals.<UnsetFeature<EObject, EStructuralFeature>>emptyList();
    }
    return _xifexpression;
  }

  private <T extends EChange<EObject>> Iterable<? extends EChange<EObject>> addUnsetChangeIfNecessary(final Iterable<T> changes, final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    boolean _wasUnset = notification.wasUnset();
    if (_wasUnset) {
      List<UnsetFeature<EObject, EStructuralFeature>> _of = List.<UnsetFeature<EObject, EStructuralFeature>>of(this.changeFactory.<EObject, EStructuralFeature>createUnsetFeatureChange(notification.getNotifierModelElement(), notification.getStructuralFeature()));
      _xifexpression = Iterables.<EChange<EObject>>concat(changes, _of);
    } else {
      _xifexpression = changes;
    }
    return _xifexpression;
  }

  private List<EChange<EObject>> addUnsetChangeIfNecessary(final EChange<EObject> change, final NotificationInfo notification) {
    List<EChange<EObject>> _xifexpression = null;
    boolean _wasUnset = notification.wasUnset();
    if (_wasUnset) {
      _xifexpression = List.<EChange<EObject>>of(change, this.changeFactory.<EObject, EStructuralFeature>createUnsetFeatureChange(notification.getNotifierModelElement(), notification.getStructuralFeature()));
    } else {
      _xifexpression = List.<EChange<EObject>>of(change);
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> surroundWithCreateAndFeatureChangesIfNecessary(final EObjectAddedEChange<EObject> change) {
    EObject _switchResult = null;
    boolean _matched = false;
    if (change instanceof UpdateReferenceEChange) {
      _matched=true;
      _switchResult = ((UpdateReferenceEChange<EObject>)change).getAffectedElement();
    }
    if (!_matched) {
      _switchResult = null;
    }
    final EObject affectedElement = _switchResult;
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    Boolean _apply = this.isCreateChange.apply(affectedElement, change.getNewValue());
    if ((_apply).booleanValue()) {
      Iterable<EChange<EObject>> _xblockexpression = null;
      {
        final CreateEObject<EObject> createChange = this.changeFactory.<EObject>createCreateEObjectChange(change.getNewValue());
        List<EChange<EObject>> _of = List.<EChange<EObject>>of(createChange, change);
        Iterable<? extends EChange<EObject>> _allAdditiveChangesForChangeRelevantFeatures = this.allAdditiveChangesForChangeRelevantFeatures(change, change.getNewValue());
        _xblockexpression = Iterables.<EChange<EObject>>concat(_of, _allAdditiveChangesForChangeRelevantFeatures);
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = List.<EObjectAddedEChange<EObject>>of(change);
    }
    return _xifexpression;
  }

  public NotificationToEChangeConverter(final Function2<? super EObject, ? super EObject, ? extends Boolean> isCreateChange) {
    super();
    this.isCreateChange = isCreateChange;
  }
}
