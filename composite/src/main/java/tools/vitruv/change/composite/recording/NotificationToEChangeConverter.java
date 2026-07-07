package tools.vitruv.change.composite.recording;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import java.util.function.Function;
import java.util.function.BiFunction;
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
 *
 * @author Heiko Klare
 */
final class NotificationToEChangeConverter {
  private final TypeInferringAtomicEChangeFactory changeFactory = TypeInferringAtomicEChangeFactory.getInstance();

  private final BiFunction<EObject, EObject, Boolean> isCreateChange;

  public EChange<EObject> createDeleteChange(final EObject eObject) {
    return this.changeFactory.<EObject>createDeleteEObjectChange(eObject);
  }

  private String convertExceptionMessage(final EventType eventType, final String notificationType) {
    return String.format(
            "Event type %s for %s Notifications unexpected.",
            eventType,
            notificationType
    );
  }

  private final String ATTRIBUTE_TYPE = "Attribute";

  private final String REFERENCE_TYPE = "Reference";

  private final String RESOURCE_CONTENTS_TYPE = "Resource Contents";

  /**
   * Converts the given notification to a list of {@link EChange}s.
   *
   * @param notification the notification to convert
   * @return the {@link Iterable} of {@link EChange}s
   */
  public Iterable<? extends EChange<EObject>> convert(final NotificationInfo notification) {
    if (isIrrelevantNotification(notification)) {
      return List.of();
    }

    if (notification.isAttributeNotification()) {
      return convertAttributeNotification(notification);
    }

    if (notification.isReferenceNotification()) {
      return convertReferenceNotification(notification);
    }

    if (notification.getNotifier() instanceof Resource) {
      return convertResourceNotification(notification);
    }

    return List.of();
  }

  private boolean isIrrelevantNotification(final NotificationInfo notification) {
    return notification.isTouch()
            || notification.isTransient()
            || Objects.equals(notification.getOldValue(), notification.getNewValue());
  }

  private Iterable<? extends EChange<EObject>> convertAttributeNotification(
          final NotificationInfo notification) {
    final EventType eventType = requireEventType(notification);

    return switch (eventType) {
      case SET -> this.handleSetAttribute(notification);
      case UNSET -> this.handleUnsetAttribute(notification);
      case ADD -> this.handleInsertAttribute(notification);
      case ADD_MANY -> this.handleMultiInsertAttribute(notification);
      case REMOVE -> this.handleRemoveAttribute(notification);
      case REMOVE_MANY -> this.handleMultiRemoveAttribute(notification);
      case MOVE -> this.handleMoveAttribute(notification);
      case RESOLVE, REMOVING_ADAPTER -> {
        throw unexpectedNotificationEvent(eventType, ATTRIBUTE_TYPE);
      }
      default -> throw unexpectedEventType(notification);
    };
  }

  private Iterable<? extends EChange<EObject>> convertReferenceNotification(
          final NotificationInfo notification) {
    final EventType eventType = requireEventType(notification);

    return switch (eventType) {
      case SET -> this.handleSetReference(notification);
      case UNSET -> this.handleUnsetReference(notification);
      case ADD -> this.handleInsertReference(notification);
      case ADD_MANY -> this.handleMultiInsertReference(notification);
      case REMOVE -> this.handleRemoveReference(notification);
      case REMOVE_MANY -> this.handleMultiRemoveReference(notification);
      case MOVE -> this.handleMoveReference(notification);
      case RESOLVE, REMOVING_ADAPTER -> {
        throw unexpectedNotificationEvent(eventType, REFERENCE_TYPE);
      }
      default -> throw unexpectedEventType(notification);
    };
  }

  private Iterable<? extends EChange<EObject>> convertResourceNotification(
          final NotificationInfo notification) {
    return switch (notification.getFeatureID(Resource.class)) {
      case Resource.RESOURCE__CONTENTS -> convertResourceContentsNotification(notification);
      case Resource.RESOURCE__URI -> convertResourceUriNotification(notification);
      default -> List.of();
    };
  }

  private Iterable<? extends EChange<EObject>> convertResourceContentsNotification(
          final NotificationInfo notification) {
    final EventType eventType = requireEventType(notification);

    return switch (eventType) {
      case ADD -> this.handleInsertRootChange(notification);
      case ADD_MANY -> this.handleMultiInsertRootChange(notification);
      case REMOVE -> this.handleRemoveRootChange(notification);
      case REMOVE_MANY -> this.handleMultiRemoveRootChange(notification);
      case SET, UNSET, MOVE, RESOLVE, REMOVING_ADAPTER ->
              throw unexpectedNotificationEvent(eventType, RESOURCE_CONTENTS_TYPE);
      default -> throw unexpectedEventType(notification);
    };
  }

  private Iterable<? extends EChange<EObject>> convertResourceUriNotification(
          final NotificationInfo notification) {
    final EventType eventType = requireResourceUriEventType(notification);

    return switch (eventType) {
      case SET -> this.handleSetUriChange(notification);
      default -> throw unexpectedResourceUriEventType(notification);
    };
  }

  private EventType requireEventType(final NotificationInfo notification) {
    final EventType eventType = notification.getEventTypeEnum();

    if (eventType == null) {
      throw unexpectedEventType(notification);
    }

    return eventType;
  }

  private EventType requireResourceUriEventType(final NotificationInfo notification) {
    final EventType eventType = notification.getEventTypeEnum();

    if (eventType == null) {
      throw unexpectedResourceUriEventType(notification);
    }

    return eventType;
  }

  private IllegalArgumentException unexpectedEventType(final NotificationInfo notification) {
    return new IllegalArgumentException("Unexpected event type " + notification.getEventType());
  }

  private IllegalArgumentException unexpectedResourceUriEventType(
          final NotificationInfo notification) {
    final String message = "Unexpected event type "
            + notification.getEventType()
            + " for Resource URI Notification.";

    return new IllegalArgumentException(message);
  }

  private IllegalArgumentException unexpectedNotificationEvent(
          final EventType eventType, final String notificationType) {
    return new IllegalArgumentException(this.convertExceptionMessage(eventType, notificationType));
  }

  private Iterable<? extends EChange<EObject>> handleMoveAttribute(final NotificationInfo notification) {
    Object _oldValue = notification.getOldValue();
    RemoveEAttributeValue<EObject, Object> _createRemoveAttributeChange = this.changeFactory
        .<EObject, Object>createRemoveAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), (((Integer) _oldValue)).intValue(), notification.getNewValue());
    InsertEAttributeValue<EObject, Object> _createInsertAttributeChange = this.changeFactory
        .<EObject, Object>createInsertAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), notification.getPosition(), notification.getNewValue());
    return List.of(_createRemoveAttributeChange, _createInsertAttributeChange);
  }

  private Iterable<? extends EChange<EObject>> handleMoveReference(final NotificationInfo notification) {
    Object _oldValue = notification.getOldValue();
    RemoveEReference<EObject> _createRemoveReferenceChange = this.changeFactory.<EObject>createRemoveReferenceChange(
        notification.getNotifierModelElement(), notification.getReference(), notification.getNewModelElementValue(),
        (((Integer) _oldValue)).intValue());
    InsertEReference<EObject> _createInsertReferenceChange = this.changeFactory.<EObject>createInsertReferenceChange(
        notification.getNotifierModelElement(), notification.getReference(), notification.getNewModelElementValue(),
        notification.getPosition());
    return List.of(_createRemoveReferenceChange, _createInsertReferenceChange);
  }

  private Iterable<? extends EChange<EObject>> handleSetAttribute(final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _switchResult = null;
    boolean _matched = false;
    boolean _isMany = notification.getAttribute().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _matched = true;
      _switchResult = this.handleReplaceAttribute(notification);
    }
    if (!_matched) {
      if (((notification.getOldValue() != null) && (notification.getNewValue() != null))) {
        _matched = true;
        List<EChange<EObject>> _handleRemoveAttribute = this.handleRemoveAttribute(notification);
        List<InsertEAttributeValue<EObject, Object>> _handleInsertAttribute = this.handleInsertAttribute(notification);
        _switchResult = Iterables.<EChange<EObject>>concat(_handleRemoveAttribute, _handleInsertAttribute);
      }
    }
    if (!_matched) {
      Object _newValue = notification.getNewValue();
      boolean _tripleNotEquals = (_newValue != null);
      if (_tripleNotEquals) {
        _matched = true;
        _switchResult = this.handleInsertAttribute(notification);
      }
    }
    if (!_matched) {
      Object _oldValue = notification.getOldValue();
      boolean _tripleNotEquals_1 = (_oldValue != null);
      if (_tripleNotEquals_1) {
        _matched = true;
        _switchResult = this.handleRemoveAttribute(notification);
      }
    }
    if (!_matched) {
      _switchResult = List.of();
    }
    return _switchResult;
  }

  private Iterable<? extends EChange<EObject>> handleSetReference(final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _switchResult = null;
    boolean _matched = false;
    boolean _isMany = notification.getReference().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _matched = true;
      _switchResult = this.handleReplaceReference(notification);
    }
    if (!_matched) {
      if (((notification.getOldValue() != null) && (notification.getNewValue() != null))) {
        _matched = true;
        Iterable<? extends EChange<EObject>> _handleRemoveReference = this.handleRemoveReference(notification);
        Iterable<? extends EChange<EObject>> _handleInsertReference = this.handleInsertReference(notification);
        _switchResult = Iterables.<EChange<EObject>>concat(_handleRemoveReference, _handleInsertReference);
      }
    }
    if (!_matched) {
      Object _newValue = notification.getNewValue();
      boolean _tripleNotEquals = (_newValue != null);
      if (_tripleNotEquals) {
        _matched = true;
        _switchResult = this.handleInsertReference(notification);
      }
    }
    if (!_matched) {
      Object _oldValue = notification.getOldValue();
      boolean _tripleNotEquals_1 = (_oldValue != null);
      if (_tripleNotEquals_1) {
        _matched = true;
        _switchResult = this.handleRemoveReference(notification);
      }
    }
    if (!_matched) {
      _switchResult = List.of();
    }
    return _switchResult;
  }

  private Iterable<? extends EChange<EObject>> handleUnsetAttribute(final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    boolean _isMany = notification.getAttribute().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _xifexpression = this.handleSetAttribute(notification);
    } else {
      _xifexpression = List.<UnsetFeature<EObject, EAttribute>>of(
          this.changeFactory.<EObject, EAttribute>createUnsetFeatureChange(notification.getNotifierModelElement(),
              notification.getAttribute()));
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleUnsetReference(final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    boolean _isMany = notification.getReference().isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _xifexpression = this.handleSetReference(notification);
    } else {
      _xifexpression = List.<UnsetFeature<EObject, EReference>>of(
          this.changeFactory.<EObject, EReference>createUnsetFeatureChange(notification.getNotifierModelElement(),
              notification.getReference()));
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleReplaceAttribute(final NotificationInfo notification) {
    final ReplaceSingleValuedEAttribute<EObject, Object> change = AttributeFactory.eINSTANCE
        .<EObject, Object>createReplaceSingleValuedEAttribute();
    change.setOldValue(notification.getOldValue());
    change.setNewValue(notification.getNewValue());
    change.setAffectedFeature(notification.getAttribute());
    change.setAffectedElement(notification.getNotifierModelElement());
    change.setIsUnset(notification.wasUnset());
    return List.<ReplaceSingleValuedEAttribute<EObject, Object>>of(change);
  }

  private Iterable<? extends EChange<EObject>> handleReplaceReference(final NotificationInfo notification) {
    final ReplaceSingleValuedEReference<EObject> change = this.changeFactory
        .<EObject>createReplaceSingleReferenceChange(notification.getNotifierModelElement(),
            notification.getReference(), notification.getOldModelElementValue(),
            notification.getNewModelElementValue());
    change.setIsUnset(notification.wasUnset());
    return this.surroundWithCreateAndFeatureChangesIfNecessary(change);
  }

  private List<EChange<EObject>> handleRemoveAttribute(final NotificationInfo notification) {
    return this.addUnsetChangeIfNecessary(
        this.changeFactory.<EObject, Object>createRemoveAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), notification.getPosition(), notification.getOldValue()),
        notification);
  }

  private Iterable<? extends EChange<EObject>> handleMultiRemoveAttribute(final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    Object _newValue = notification.getNewValue();
    boolean _tripleEquals = (_newValue == null);
    if (_tripleEquals) {
      Iterable<? extends EChange<EObject>> _xblockexpression = null;
      {
        Object _oldValue = notification.getOldValue();
        final List<?> oldValues = ((List<?>) _oldValue);
        List<RemoveEAttributeValue<EObject, Object>> _mapped = new ArrayList<>();
        for (int _i = 0; _i < oldValues.size(); _i++) {
          Object value = oldValues.get(oldValues.size() - 1 - _i);
          final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - _i;
          _mapped.add(this.changeFactory.<EObject, Object>createRemoveAttributeChange(
              notification.getNotifierModelElement(), notification.getAttribute(), valueIndex, value));
        }
        _xblockexpression = this.<RemoveEAttributeValue<EObject, Object>>addUnsetChangeIfNecessary(
            _mapped, notification);
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = this.unsetChangeOrEmpty(notification);
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleRemoveReference(final NotificationInfo notification) {
    return this.addUnsetChangeIfNecessary(
        this.changeFactory.<EObject>createRemoveReferenceChange(notification.getNotifierModelElement(),
            notification.getReference(), notification.getOldModelElementValue(), notification.getPosition()),
        notification);
  }

  private Iterable<? extends EChange<EObject>> handleMultiRemoveReference(
      final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    Object _newValue = notification.getNewValue();
    boolean _tripleEquals = (_newValue == null);
    if (_tripleEquals) {
      Iterable<? extends EChange<EObject>> _xblockexpression = null;
      {
        Object _oldValue = notification.getOldValue();
        final List<EObject> oldValues = ((List<EObject>) _oldValue);
        List<RemoveEReference<EObject>> _mapped = new ArrayList<>();
        for (int _i = 0; _i < oldValues.size(); _i++) {
          EObject value = oldValues.get(oldValues.size() - 1 - _i);
          final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - _i;
          _mapped.add(this.changeFactory.<EObject>createRemoveReferenceChange(
              notification.getNotifierModelElement(), notification.getReference(), value, valueIndex));
        }
        _xblockexpression = this.<RemoveEReference<EObject>>addUnsetChangeIfNecessary(_mapped, notification);
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = this.unsetChangeOrEmpty(notification);
    }
    return _xifexpression;
  }

  private List<InsertEAttributeValue<EObject, Object>> handleInsertAttribute(
      final NotificationInfo notification) {
    return List.<InsertEAttributeValue<EObject, Object>>of(
        this.changeFactory.<EObject, Object>createInsertAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), notification.getPosition(), notification.getNewValue()));
  }

  private List<InsertEAttributeValue<EObject, Object>> handleMultiInsertAttribute(
      final NotificationInfo notification) {
    List<?> _newValue = ((List<?>) notification.getNewValue());
    List<InsertEAttributeValue<EObject, Object>> _result = new ArrayList<>();
    for (int _i = 0; _i < _newValue.size(); _i++) {
      Object value = _newValue.get(_i);
      _result.add(this.changeFactory.<EObject, Object>createInsertAttributeChange(
          notification.getNotifierModelElement(), notification.getAttribute(),
          notification.getInitialIndex() + _i, value));
    }
    return _result;
  }

  private Iterable<? extends EChange<EObject>> handleInsertReference(final NotificationInfo notification) {
    return this.surroundWithCreateAndFeatureChangesIfNecessary(
        this.changeFactory.<EObject>createInsertReferenceChange(notification.getNotifierModelElement(),
            notification.getReference(), notification.getNewModelElementValue(), notification.getPosition()));
  }

  private Iterable<? extends EChange<EObject>> handleMultiInsertReference(
      final NotificationInfo notification) {
    List<EObject> _newValue = ((List<EObject>) notification.getNewValue());
    List<EChange<EObject>> _result = new ArrayList<>();
    for (int _i = 0; _i < _newValue.size(); _i++) {
      EObject value = _newValue.get(_i);
      Iterables.addAll(_result, this.surroundWithCreateAndFeatureChangesIfNecessary(
          this.changeFactory.<EObject>createInsertReferenceChange(
              notification.getNotifierModelElement(), notification.getReference(),
              value, notification.getInitialIndex() + _i)));
    }
    return _result;
  }

  private Iterable<? extends EChange<EObject>> handleInsertRootChange(final NotificationInfo notification) {
    return this.surroundWithCreateAndFeatureChangesIfNecessary(this.changeFactory.<EObject>createInsertRootChange(
        notification.getNewModelElementValue(), notification.getNotifierResource(), notification.getPosition()));
  }

  private List<EChange<EObject>> handleMultiInsertRootChange(final NotificationInfo notification) {
    List<EObject> _newValue = ((List<EObject>) notification.getNewValue());
    List<EChange<EObject>> _result = new ArrayList<>();
    for (int _i = 0; _i < _newValue.size(); _i++) {
      EObject value = _newValue.get(_i);
      Iterables.addAll(_result, this.surroundWithCreateAndFeatureChangesIfNecessary(
          this.changeFactory.<EObject>createInsertRootChange(
              value, notification.getNotifierResource(), notification.getInitialIndex() + _i)));
    }
    return _result;
  }

  private List<RemoveRootEObject<EObject>> handleRemoveRootChange(final NotificationInfo notification) {
    return List.<RemoveRootEObject<EObject>>of(this.changeFactory.<EObject>createRemoveRootChange(
        notification.getOldModelElementValue(), notification.getNotifierResource(), notification.getPosition()));
  }

  private List<RemoveRootEObject<EObject>> handleMultiRemoveRootChange(final NotificationInfo notification) {
    List<RemoveRootEObject<EObject>> _xblockexpression = null;
    {
      Object _oldValue = notification.getOldValue();
      final List<EObject> oldValues = ((List<EObject>) _oldValue);
      List<RemoveRootEObject<EObject>> _mapped = new ArrayList<>();
      for (int _i = 0; _i < oldValues.size(); _i++) {
        EObject value = oldValues.get(oldValues.size() - 1 - _i);
        final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - _i;
        _mapped.add(this.changeFactory.<EObject>createRemoveRootChange(value,
            notification.getNotifierResource(), valueIndex));
      }
      _xblockexpression = _mapped;
    }
    return _xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> handleSetUriChange(final NotificationInfo notification) {
    Iterable<EChange<EObject>> _xblockexpression = null;
    {
      Object _oldValue = notification.getOldValue();
      final URI oldUri = ((URI) _oldValue);
      List<EObject> _contents = new ArrayList<>(notification.getNotifierResource().getContents());
      List<RemoveRootEObject<EObject>> _removeChanges = new ArrayList<>();
      for (int _i = 0; _i < _contents.size(); _i++) {
        EObject value = _contents.get(_contents.size() - 1 - _i);
        final int valueIndex = (notification.getInitialIndex() + _contents.size() - 1) - _i;
        final Resource oldResource = notification.getNotifierResource().getResourceSet().createResource(oldUri);
        _removeChanges.add(this.changeFactory.<EObject>createRemoveRootChange(value, oldResource, oldUri, valueIndex));
      }
      List<EChange<EObject>> _insertChanges = new ArrayList<>();
      for (int _i = 0; _i < _contents.size(); _i++) {
        EObject value = _contents.get(_i);
        Iterables.addAll(_insertChanges, this.surroundWithCreateAndFeatureChangesIfNecessary(
            this.changeFactory.<EObject>createInsertRootChange(
                value, notification.getNotifierResource(), notification.getInitialIndex() + _i)));
      }
      _xblockexpression = Iterables.<EChange<EObject>>concat(_removeChanges, _insertChanges);
    }
    return _xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> allAdditiveChangesForChangeRelevantFeatures(
      final EObjectAddedEChange<EObject> change, final EObject eObject) {
    final BiFunction<EObject, EAttribute, Iterable<? extends EChange<EObject>>> _function = (EObject object,
        EAttribute attribute) -> {
      return EChangeCreationUtil.<EObject>createAdditiveEChangeForAttribute(object, attribute);
    };
    final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> _function_1 = (EObject object,
        EReference reference) -> {
      List<? extends EChange<EObject>> _xifexpression = null;
      boolean _isContainment = reference.isContainment();
      if (_isContainment) {
        final Function<EObject, Boolean> _function_2 = (EObject referencedObject) -> {
          return this.isCreateChange.apply(object, referencedObject);
        };
        _xifexpression = EChangeCreationUtil.createAdditiveEChangeForReferencedObject(object, reference, _function_2);
      }
      return _xifexpression;
    };
    Iterable<? extends EChange<EObject>> _walkChangeRelevantFeatures = NotificationToEChangeConverter
        .walkChangeRelevantFeatures(change.getNewValue(), _function, _function_1);
    final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> _function_2 = (EObject object,
        EReference reference) -> {
      List<? extends EChange<EObject>> _xifexpression = null;
      boolean _isContainment = reference.isContainment();
      boolean _not = (!_isContainment);
      if (_not) {
        final Function<EObject, Boolean> _function_3 = (EObject it) -> {
          return Boolean.valueOf(false);
        };
        _xifexpression = EChangeCreationUtil.createAdditiveEChangeForReferencedObject(object, reference, _function_3);
      }
      return _xifexpression;
    };
    Iterable<? extends EChange<EObject>> _walkChangeRelevantFeatures_1 = NotificationToEChangeConverter
        .walkChangeRelevantFeatures(change.getNewValue(), null, _function_2);
    return Iterables.<EChange<EObject>>concat(_walkChangeRelevantFeatures, _walkChangeRelevantFeatures_1);
  }

  private static Iterable<? extends EChange<EObject>> walkChangeRelevantFeatures(final EObject eObject,
      final BiFunction<EObject, EAttribute, Iterable<? extends EChange<EObject>>> attributeVisitor,
      final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> referenceVisitor) {
    Iterable<EChange<EObject>> _xblockexpression = null;
    {
      final Function<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
        return Boolean
            .valueOf(EChangeCreationUtil.hasChangeableUnderivedPersistedNotContainingNonDefaultValue(eObject, it));
      };
      final List<EStructuralFeature> changeRelevantFeatures = eObject.eClass().getEAllStructuralFeatures()
          .stream().filter(it -> _function.apply(it)).toList();
      final Function<EAttribute, Iterable<? extends EChange<EObject>>> _function_1 = (EAttribute it) -> {
        Iterable<? extends EChange<EObject>> _elvis = null;
        Iterable<? extends EChange<EObject>> _apply = null;
        if (attributeVisitor != null) {
          _apply = attributeVisitor.apply(eObject, it);
        }
        if (_apply != null) {
          _elvis = _apply;
        } else {
          List<? extends EChange<EObject>> _emptyList = List.of();
          _elvis = _emptyList;
        }
        return _elvis;
      };
      List<EChange<EObject>> thisLayerAttributeResults = new ArrayList<>();
      for (EAttribute it : Iterables.<EAttribute>filter(changeRelevantFeatures, EAttribute.class)) {
        Iterables.addAll(thisLayerAttributeResults, _function_1.apply(it));
      }
      final Function<EReference, Iterable<? extends EChange<EObject>>> _function_2 = (EReference it) -> {
        Iterable<? extends EChange<EObject>> _elvis = null;
        Iterable<? extends EChange<EObject>> _apply = null;
        if (referenceVisitor != null) {
          _apply = referenceVisitor.apply(eObject, it);
        }
        if (_apply != null) {
          _elvis = _apply;
        } else {
          List<? extends EChange<EObject>> _emptyList = List.of();
          _elvis = _emptyList;
        }
        return _elvis;
      };
      List<EChange<EObject>> thisLayerReferenceResults = new ArrayList<>();
      for (EReference it : Iterables.<EReference>filter(changeRelevantFeatures, EReference.class)) {
        Iterables.addAll(thisLayerReferenceResults, _function_2.apply(it));
      }
      final Function<EReference, Boolean> _function_3 = (EReference it) -> {
        return Boolean.valueOf(it.isContainment());
      };
      final Function<EReference, Iterable<EObject>> _function_4 = (EReference it) -> {
        return NotificationToEChangeConverter.getReferencedElements(eObject, it);
      };
      final Function<EObject, Iterable<? extends EChange<EObject>>> _function_5 = (EObject it) -> {
        return NotificationToEChangeConverter.walkChangeRelevantFeatures(it, attributeVisitor, referenceVisitor);
      };
      List<EChange<EObject>> nextLayer = new ArrayList<>();
      Iterable<EObject> _nextLayerElements = Iterables.concat(
          Iterables.transform(
              Iterables.filter(
                  Iterables.<EReference>filter(changeRelevantFeatures, EReference.class),
                  (EReference it) -> _function_3.apply(it)),
              (EReference it) -> _function_4.apply(it)));
      for (EObject it : _nextLayerElements) {
        Iterables.addAll(nextLayer, _function_5.apply(it));
      }
      Iterable<EChange<EObject>> _plus = Iterables.<EChange<EObject>>concat(thisLayerAttributeResults,
          thisLayerReferenceResults);
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
      _xifexpression = List.<UnsetFeature<EObject, EStructuralFeature>>of(
          this.changeFactory.<EObject, EStructuralFeature>createUnsetFeatureChange(
              notification.getNotifierModelElement(), notification.getStructuralFeature()));
    } else {
      _xifexpression = List.of();
    }
    return _xifexpression;
  }

  private <T extends EChange<EObject>> Iterable<? extends EChange<EObject>> addUnsetChangeIfNecessary(
      final Iterable<T> changes, final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    boolean _wasUnset = notification.wasUnset();
    if (_wasUnset) {
      List<UnsetFeature<EObject, EStructuralFeature>> _of = List.<UnsetFeature<EObject, EStructuralFeature>>of(
          this.changeFactory.<EObject, EStructuralFeature>createUnsetFeatureChange(
              notification.getNotifierModelElement(), notification.getStructuralFeature()));
      _xifexpression = Iterables.<EChange<EObject>>concat(changes, _of);
    } else {
      _xifexpression = changes;
    }
    return _xifexpression;
  }

  private List<EChange<EObject>> addUnsetChangeIfNecessary(final EChange<EObject> change,
      final NotificationInfo notification) {
    List<EChange<EObject>> _xifexpression = null;
    boolean _wasUnset = notification.wasUnset();
    if (_wasUnset) {
      _xifexpression = List.<EChange<EObject>>of(change,
          this.changeFactory.<EObject, EStructuralFeature>createUnsetFeatureChange(
              notification.getNotifierModelElement(), notification.getStructuralFeature()));
    } else {
      _xifexpression = List.<EChange<EObject>>of(change);
    }
    return _xifexpression;
  }

  private Iterable<? extends EChange<EObject>> surroundWithCreateAndFeatureChangesIfNecessary(
      final EObjectAddedEChange<EObject> change) {
    EObject _switchResult = null;
    boolean _matched = false;
    if (change instanceof UpdateReferenceEChange) {
      _matched = true;
      _switchResult = ((UpdateReferenceEChange<EObject>) change).getAffectedElement();
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
        final CreateEObject<EObject> createChange = this.changeFactory
            .<EObject>createCreateEObjectChange(change.getNewValue());
        List<EChange<EObject>> _of = List.<EChange<EObject>>of(createChange, change);
        Iterable<? extends EChange<EObject>> _allAdditiveChangesForChangeRelevantFeatures = this
            .allAdditiveChangesForChangeRelevantFeatures(change, change.getNewValue());
        _xblockexpression = Iterables.<EChange<EObject>>concat(_of, _allAdditiveChangesForChangeRelevantFeatures);
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = List.<EObjectAddedEChange<EObject>>of(change);
    }
    return _xifexpression;
  }

  public NotificationToEChangeConverter(final BiFunction<EObject, EObject, Boolean> isCreateChange) {
    super();
    this.isCreateChange = isCreateChange;
  }
}
