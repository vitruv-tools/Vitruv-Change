package tools.vitruv.change.composite.recording;

import static tools.vitruv.change.composite.message.Error.RESOURCE_URI_NOTIFICATION;
import static tools.vitruv.change.composite.message.Error.UNEXPECTED_EVENT_TYPE;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
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

  /**
   * Creates the change removing the given element from its current container: a
   * {@link RemoveEReference} if the containment reference is many-valued, or a
   * {@link ReplaceSingleValuedEReference} to <code>null</code> otherwise.
   *
   * @param eObject the contained element, must have a container
   * @return the change removing the element from its container
   */
  public EChange<EObject> createRemoveFromContainerChange(final EObject eObject) {
    final EObject container = eObject.eContainer();
    final EReference containmentFeature = eObject.eContainmentFeature();
    if (containmentFeature.isMany()) {
      final int index = ((List<?>) container.eGet(containmentFeature)).indexOf(eObject);
      return this.changeFactory.<EObject>createRemoveReferenceChange(
          container, containmentFeature, eObject, index);
    }
    return this.changeFactory.<EObject>createReplaceSingleReferenceChange(
        container, containmentFeature, eObject, null);
  }

  private String convertExceptionMessage(final EventType eventType, final String notificationType) {
    return String.format(
            "Event type %s for %s Notifications unexpected.",
            eventType,
            notificationType
    );
  }

  private final static String ATTRIBUTE_TYPE = "Attribute";

  private final static String REFERENCE_TYPE = "Reference";

  private final static String RESOURCE_CONTENTS_TYPE = "Resource Contents";

  /**
   * Converts the given notification to a list of {@link EChange}s.
   *
   * @param notification the notification to convert
   * @return the {@link Iterable} of {@link EChange}s
   */
  public Iterable<EChange<EObject>> convert(final NotificationInfo notification) {
    if (isIrrelevantNotification(notification)) {
      return List.of();
    }

    if (notification.isAttributeNotification()) {
      return Lists.newArrayList(convertAttributeNotification(notification));
    }

    if (notification.isReferenceNotification()) {
      return Lists.newArrayList(convertReferenceNotification(notification));
    }

    if (notification.getNotifier() instanceof Resource) {
      return Lists.newArrayList(convertResourceNotification(notification));
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
      case RESOLVE, REMOVING_ADAPTER ->
          throw unexpectedNotificationEvent(eventType, ATTRIBUTE_TYPE);
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
      case RESOLVE, REMOVING_ADAPTER ->
          throw unexpectedNotificationEvent(eventType, REFERENCE_TYPE);
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
    return new IllegalArgumentException(UNEXPECTED_EVENT_TYPE + notification.getEventType());
  }

  private IllegalArgumentException unexpectedResourceUriEventType(
          final NotificationInfo notification) {
    final String message = UNEXPECTED_EVENT_TYPE
            + notification.getEventType()
            + RESOURCE_URI_NOTIFICATION;

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
            notification.getAttribute(), (Integer) _oldValue, notification.getNewValue());
    InsertEAttributeValue<EObject, Object> _createInsertAttributeChange = this.changeFactory
        .<EObject, Object>createInsertAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), notification.getPosition(), notification.getNewValue());
    return List.of(_createRemoveAttributeChange, _createInsertAttributeChange);
  }

  private Iterable<? extends EChange<EObject>> handleMoveReference(final NotificationInfo notification) {
    Object _oldValue = notification.getOldValue();
    RemoveEReference<EObject> _createRemoveReferenceChange = this.changeFactory.<EObject>createRemoveReferenceChange(
        notification.getNotifierModelElement(), notification.getReference(), notification.getNewModelElementValue(),
            (Integer) _oldValue);
    InsertEReference<EObject> _createInsertReferenceChange = this.changeFactory.<EObject>createInsertReferenceChange(
        notification.getNotifierModelElement(), notification.getReference(), notification.getNewModelElementValue(),
        notification.getPosition());
    return List.of(_createRemoveReferenceChange, _createInsertReferenceChange);
  }

  private Iterable<? extends EChange<EObject>> handleSetAttribute(final NotificationInfo notification) {
    final EAttribute attribute = notification.getAttribute();
    if (attribute == null) {
      return List.of();
    }

    Iterable<? extends EChange<EObject>> _switchResult = null;
    boolean _matched = false;
    boolean _isMany = attribute.isMany();
    boolean _not = (!_isMany);
    if (_not) {
      _matched = true;
      _switchResult = this.handleReplaceAttribute(notification);
    }
    if (!_matched && (notification.getOldValue() != null) && (notification.getNewValue() != null)) {
      _matched = true;
      List<EChange<EObject>> _handleRemoveAttribute = this.handleRemoveAttribute(notification);
      List<InsertEAttributeValue<EObject, Object>> _handleInsertAttribute = this.handleInsertAttribute(notification);
      _switchResult = Iterables.<EChange<EObject>>concat(_handleRemoveAttribute, _handleInsertAttribute);
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
    if (!_matched && (notification.getOldValue() != null) && (notification.getNewValue() != null)) {
      _matched = true;
      Iterable<? extends EChange<EObject>> _handleRemoveReference = this.handleRemoveReference(notification);
      Iterable<? extends EChange<EObject>> _handleInsertReference = this.handleInsertReference(notification);
      _switchResult = Iterables.<EChange<EObject>>concat(_handleRemoveReference, _handleInsertReference);
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
    Iterable<? extends EChange<EObject>> _xifexpression;
    Object _newValue = notification.getNewValue();
    boolean _tripleEquals = (_newValue == null);
    if (_tripleEquals) {
      Iterable<? extends EChange<EObject>> _xblockexpression;
      Object _oldValue = notification.getOldValue();
      final List<?> oldValues = ((List<?>) _oldValue);
      List<RemoveEAttributeValue<EObject, Object>> _mapped = new ArrayList<>();
      for (int _i = 0; _i < oldValues.size(); _i++) {
        Object value = oldValues.get(oldValues.size() - 1 - _i);
        final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - _i;
        _mapped.add(this.changeFactory.<EObject, Object>createRemoveAttributeChange(notification.getNotifierModelElement(), notification.getAttribute(), valueIndex, value));
      }
      _xblockexpression = this.<RemoveEAttributeValue<EObject, Object>>addUnsetChangeIfNecessary(_mapped, notification);

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
    Iterable<? extends EChange<EObject>> _xifexpression;
    Object _newValue = notification.getNewValue();
    boolean _tripleEquals = (_newValue == null);
    if (_tripleEquals) {
      Iterable<? extends EChange<EObject>> _xblockexpression;
      Object _oldValue = notification.getOldValue();
      final List<?> oldValues = (List<?>) _oldValue;
      List<RemoveEReference<EObject>> _mapped = new ArrayList<>();
      for (int _i = 0; _i < oldValues.size(); _i++) {
        EObject value = (EObject) oldValues.get(oldValues.size() - 1 - _i);
        final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - _i;
        _mapped.add(this.changeFactory.<EObject>createRemoveReferenceChange(notification.getNotifierModelElement(), notification.getReference(), value, valueIndex));
      }
      _xblockexpression = this.<RemoveEReference<EObject>>addUnsetChangeIfNecessary(_mapped, notification);

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
    List<?> _newValue = ((List<?>) notification.getNewValue());
    List<EChange<EObject>> _result = new ArrayList<>();
    for (int _i = 0; _i < _newValue.size(); _i++) {
      EObject value = (EObject)_newValue.get(_i);
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
    List<?> _newValue = ((List<?>) notification.getNewValue());
    List<EChange<EObject>> _result = new ArrayList<>();
    for (int _i = 0; _i < _newValue.size(); _i++) {
      EObject value = (EObject)_newValue.get(_i);
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
    Object _oldValue = notification.getOldValue();
    final List<?> oldValues = ((List<?>) _oldValue);
    List<RemoveRootEObject<EObject>> _mapped = new ArrayList<>();
    for (int _i = 0; _i < oldValues.size(); _i++) {
      EObject value = (EObject)oldValues.get(oldValues.size() - 1 - _i);
      final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - _i;
      _mapped.add(this.changeFactory.<EObject>createRemoveRootChange(value, notification.getNotifierResource(), valueIndex));
    }
    _xblockexpression = _mapped;

    return _xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> handleSetUriChange(final NotificationInfo notification) {
    Iterable<EChange<EObject>> _xblockexpression;
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
      Iterables.addAll(_insertChanges, this.surroundWithCreateAndFeatureChangesIfNecessary(this.changeFactory.<EObject>createInsertRootChange(
              value, notification.getNotifierResource(), notification.getInitialIndex() + _i)));
    }
    _xblockexpression = Iterables.<EChange<EObject>>concat(_removeChanges, _insertChanges);

    return _xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> allAdditiveChangesForChangeRelevantFeatures(
      final EObjectAddedEChange<EObject> change) {
    final BiFunction<EObject, EAttribute, Iterable<? extends EChange<EObject>>> _function = EChangeCreationUtil::createAdditiveEChangeForAttribute;
    final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> _function_1 = (EObject object,
        EReference reference) -> {
      List<? extends EChange<EObject>> _xifexpression = null;
      boolean _isContainment = reference.isContainment();
      if (_isContainment) {
        final Predicate<EObject> _function_2 = (EObject referencedObject) -> this.isCreateChange.apply(object, referencedObject);
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
        final Predicate<EObject> _function_3 = (EObject it) -> {
          return Boolean.FALSE;
        };
        _xifexpression = EChangeCreationUtil.createAdditiveEChangeForReferencedObject(object, reference, _function_3);
      }
      return _xifexpression;
    };
    Iterable<? extends EChange<EObject>> _walkChangeRelevantFeatures_1 = NotificationToEChangeConverter
        .walkChangeRelevantFeatures(change.getNewValue(), null, _function_2);
    return Iterables.<EChange<EObject>>concat(_walkChangeRelevantFeatures, _walkChangeRelevantFeatures_1);
  }

  private static Iterable<? extends EChange<EObject>> walkChangeRelevantFeatures(
          final EObject eObject,
          final BiFunction<EObject, EAttribute, Iterable<? extends EChange<EObject>>> attributeVisitor,
          final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> referenceVisitor) {
    final List<EChange<EObject>> result = new ArrayList<>();
    collectChanges(eObject, attributeVisitor, referenceVisitor, result);
    return result;
  }

  private static void collectChanges(
          final EObject eObject,
          final BiFunction<EObject, EAttribute, Iterable<? extends EChange<EObject>>> attributeVisitor,
          final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> referenceVisitor,
          final List<EChange<EObject>> result) {
    final List<EStructuralFeature> features = eObject.eClass().getEAllStructuralFeatures().stream()
            .filter(feature -> EChangeCreationUtil
                    .hasChangeableUnderivedPersistedNotContainingNonDefaultValue(eObject, feature))
            .toList();
    final List<EAttribute> attributes = filterByType(features, EAttribute.class);
    final List<EReference> references = filterByType(features, EReference.class);

    attributes.forEach(attribute -> addVisitResult(eObject, attribute, attributeVisitor, result));
    references.forEach(reference -> addVisitResult(eObject, reference, referenceVisitor, result));
    for (final EReference reference : references) {
      if (reference.isContainment()) {
        for (final EObject child : NotificationToEChangeConverter.getReferencedElements(eObject, reference)) {
          collectChanges(child, attributeVisitor, referenceVisitor, result);
        }
      }
    }
  }

  private static <F extends EStructuralFeature> void addVisitResult(
          final EObject eObject, final F feature,
          final BiFunction<EObject, F, Iterable<? extends EChange<EObject>>> visitor,
          final List<EChange<EObject>> result) {
    if (visitor == null) {
      return;
    }
    final Iterable<? extends EChange<EObject>> changes = visitor.apply(eObject, feature);
    if (changes != null) {
      changes.forEach(result::add);
    }
  }

  private static <T> List<T> filterByType(final List<?> items, final Class<T> type) {
    return items.stream().filter(type::isInstance).map(type::cast).toList();
  }

  private static Iterable<EObject> getReferencedElements(final EObject eObject, final EReference reference) {
    final Object value = eObject.eGet(reference);
    if (reference.isMany()) {
        return (List<EObject>) value;
    }
    return value == null ? List.of() : List.of((EObject) value);
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
    Iterable<? extends EChange<EObject>> _xifexpression;
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
    List<EChange<EObject>> _xifexpression;
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
      if (change instanceof UpdateReferenceEChange) {
        _switchResult = ((UpdateReferenceEChange<EObject>) change).getAffectedElement();
    }
    final EObject affectedElement = _switchResult;
    Iterable<? extends EChange<EObject>> _xifexpression;
    Boolean _apply = this.isCreateChange.apply(affectedElement, change.getNewValue());
    if (_apply) {
      Iterable<EChange<EObject>> _xblockexpression;
      final CreateEObject<EObject> createChange = this.changeFactory.<EObject>createCreateEObjectChange(change.getNewValue());
      List<EChange<EObject>> _of = List.<EChange<EObject>>of(createChange, change);
      Iterable<? extends EChange<EObject>> _allAdditiveChangesForChangeRelevantFeatures = this.allAdditiveChangesForChangeRelevantFeatures(change);
      _xblockexpression = Iterables.<EChange<EObject>>concat(_of, _allAdditiveChangesForChangeRelevantFeatures);

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
