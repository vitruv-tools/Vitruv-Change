package tools.vitruv.change.composite.recording;

import static tools.vitruv.change.composite.message.Error.RESOURCE_URI_NOTIFICATION;
import static tools.vitruv.change.composite.message.Error.UNEXPECTED_EVENT_TYPE;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
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
  private final TypeInferringAtomicEChangeFactory changeFactory =
          TypeInferringAtomicEChangeFactory.getInstance();

  private final BiPredicate<EObject, EObject> isCreateChange;

  public EChange<EObject> createDeleteChange(final EObject eObject) {
    return this.changeFactory.createDeleteEObjectChange(eObject);
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
    final EReference containmentFeature = eObject.eContainmentFeature();
    final int index = containmentFeature.isMany()
        ? ((List<?>) eObject.eContainer().eGet(containmentFeature)).indexOf(eObject)
        : -1;
    return createRemoveFromContainerChange(eObject, index);
  }

  /**
   * Creates the change removing the given element from its current container, like
   * {@link #createRemoveFromContainerChange(EObject)}, for an element whose index in its
   * containment reference is already known.
   *
   * @param eObject the contained element, must have a container
   * @param index the index of the element in its containment reference if that is many-valued,
   *     ignored otherwise
   * @return the change removing the element from its container
   */
  public EChange<EObject> createRemoveFromContainerChange(final EObject eObject, final int index) {
    final EObject container = eObject.eContainer();
    final EReference containmentFeature = eObject.eContainmentFeature();
    if (containmentFeature.isMany()) {
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

  private static final String ATTRIBUTE_TYPE = "Attribute";

  private static final String REFERENCE_TYPE = "Reference";

  private static final String RESOURCE_CONTENTS_TYPE = "Resource Contents";

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

  private Iterable<? extends EChange<EObject>> handleMoveAttribute(
          final NotificationInfo notification) {
    Object oldValue = notification.getOldValue();
    RemoveEAttributeValue<EObject, Object> createRemoveAttributeChange = this.changeFactory
        .<EObject, Object>createRemoveAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), (Integer) oldValue, notification.getNewValue());
    InsertEAttributeValue<EObject, Object> createInsertAttributeChange = this.changeFactory
        .<EObject, Object>createInsertAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), notification.getPosition(), notification.getNewValue());
    return List.of(createRemoveAttributeChange, createInsertAttributeChange);
  }

  private Iterable<? extends EChange<EObject>> handleMoveReference(
          final NotificationInfo notification) {
    Object oldValue = notification.getOldValue();
    RemoveEReference<EObject> createRemoveReferenceChange =
            this.changeFactory.<EObject>createRemoveReferenceChange(
                    notification.getNotifierModelElement(),
                    notification.getReference(),
                    notification.getNewModelElementValue(),
            (Integer) oldValue);
    InsertEReference<EObject> createInsertReferenceChange =
            this.changeFactory.<EObject>createInsertReferenceChange(
                    notification.getNotifierModelElement(),
                    notification.getReference(),
                    notification.getNewModelElementValue(),
                    notification.getPosition());
    return List.of(createRemoveReferenceChange, createInsertReferenceChange);
  }

  private Iterable<? extends EChange<EObject>> handleSetAttribute(
          final NotificationInfo notification) {
    final EAttribute attribute = notification.getAttribute();
    if (attribute == null) {
      return List.of();
    }

    Iterable<? extends EChange<EObject>> switchResult = null;
    boolean matched = false;
    boolean isMany = attribute.isMany();
    if (!isMany) {
      matched = true;
      switchResult = this.handleReplaceAttribute(notification);
    }
    if (!matched && (notification.getOldValue() != null) && (notification.getNewValue() != null)) {
      matched = true;
      List<EChange<EObject>> handleRemoveAttribute = this.handleRemoveAttribute(notification);
      List<InsertEAttributeValue<EObject, Object>> handleInsertAttribute =
              this.handleInsertAttribute(notification);
      switchResult = Iterables.concat(handleRemoveAttribute, handleInsertAttribute);
    }
    if (!matched) {
      Object newValue = notification.getNewValue();
      boolean tripleNotEquals = (newValue != null);
      if (tripleNotEquals) {
        matched = true;
        switchResult = this.handleInsertAttribute(notification);
      }
    }
    if (!matched) {
      Object oldValue = notification.getOldValue();
      boolean tripleNotEquals1 = (oldValue != null);
      if (tripleNotEquals1) {
        matched = true;
        switchResult = this.handleRemoveAttribute(notification);
      }
    }
    if (!matched) {
      switchResult = List.of();
    }
    return switchResult;
  }

  private Iterable<? extends EChange<EObject>> handleSetReference(
          final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> switchResult = null;
    boolean matched = false;
    boolean isMany = notification.getReference().isMany();
    if (!isMany) {
      matched = true;
      switchResult = this.handleReplaceReference(notification);
    }
    if (!matched && (notification.getOldValue() != null) && (notification.getNewValue() != null)) {
      matched = true;
      Iterable<? extends EChange<EObject>> handleRemoveReference =
              this.handleRemoveReference(notification);
      Iterable<? extends EChange<EObject>> handleInsertReference =
              this.handleInsertReference(notification);
      switchResult = Iterables.concat(handleRemoveReference, handleInsertReference);
    }
    if (!matched) {
      Object newValue = notification.getNewValue();
      boolean tripleNotEquals = (newValue != null);
      if (tripleNotEquals) {
        matched = true;
        switchResult = this.handleInsertReference(notification);
      }
    }
    if (!matched) {
      Object oldValue = notification.getOldValue();
      boolean tripleNotEquals1 = (oldValue != null);
      if (tripleNotEquals1) {
        matched = true;
        switchResult = this.handleRemoveReference(notification);
      }
    }
    if (!matched) {
      switchResult = List.of();
    }
    return switchResult;
  }

  private Iterable<? extends EChange<EObject>> handleUnsetAttribute(
          final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> xifexpression = null;
    boolean isMany = notification.getAttribute().isMany();
    if (!isMany) {
      xifexpression = this.handleSetAttribute(notification);
    } else {
      xifexpression = List.of(
          this.changeFactory.createUnsetFeatureChange(notification.getNotifierModelElement(),
              notification.getAttribute()));
    }
    return xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleUnsetReference(
          final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> xifexpression = null;
    boolean isMany = notification.getReference().isMany();
    if (!isMany) {
      xifexpression = this.handleSetReference(notification);
    } else {
      xifexpression = List.of(
          this.changeFactory.createUnsetFeatureChange(notification.getNotifierModelElement(),
              notification.getReference()));
    }
    return xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleReplaceAttribute(
          final NotificationInfo notification) {
    final ReplaceSingleValuedEAttribute<EObject, Object> change = AttributeFactory.eINSTANCE
        .createReplaceSingleValuedEAttribute();
    change.setOldValue(notification.getOldValue());
    change.setNewValue(notification.getNewValue());
    change.setAffectedFeature(notification.getAttribute());
    change.setAffectedElement(notification.getNotifierModelElement());
    change.setIsUnset(notification.wasUnset());
    return List.of(change);
  }

  private Iterable<? extends EChange<EObject>> handleReplaceReference(
          final NotificationInfo notification) {
    final ReplaceSingleValuedEReference<EObject> change = this.changeFactory
        .createReplaceSingleReferenceChange(notification.getNotifierModelElement(),
            notification.getReference(), notification.getOldModelElementValue(),
            notification.getNewModelElementValue());
    change.setIsUnset(notification.wasUnset());
    return this.surroundWithCreateAndFeatureChangesIfNecessary(change);
  }

  private List<EChange<EObject>> handleRemoveAttribute(final NotificationInfo notification) {
    return this.addUnsetChangeIfNecessary(
        this.changeFactory.createRemoveAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), notification.getPosition(), notification.getOldValue()),
        notification);
  }

  private Iterable<? extends EChange<EObject>> handleMultiRemoveAttribute(
          final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> xifexpression;
    Object newValue = notification.getNewValue();
    boolean tripleEquals = (newValue == null);
    if (tripleEquals) {
      Iterable<? extends EChange<EObject>> xblockexpression;
      Object oldValue = notification.getOldValue();
      final List<?> oldValues = ((List<?>) oldValue);
      List<RemoveEAttributeValue<EObject, Object>> mapped = new ArrayList<>();
      for (int i = 0; i < oldValues.size(); i++) {
        Object value = oldValues.get(oldValues.size() - 1 - i);
        final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - i;
        mapped.add(this.changeFactory
                .createRemoveAttributeChange(notification
                        .getNotifierModelElement(),
                        notification.getAttribute(),
                        valueIndex, value));
      }
      xblockexpression = this.addUnsetChangeIfNecessary(mapped, notification);

      xifexpression = xblockexpression;
    } else {
      xifexpression = this.unsetChangeOrEmpty(notification);
    }
    return xifexpression;
  }

  private Iterable<? extends EChange<EObject>> handleRemoveReference(
          final NotificationInfo notification) {
    return this.addUnsetChangeIfNecessary(
        this.changeFactory.createRemoveReferenceChange(
                notification.getNotifierModelElement(),
                notification.getReference(),
                notification.getOldModelElementValue(),
                notification.getPosition()),
            notification);
  }

  private Iterable<? extends EChange<EObject>> handleMultiRemoveReference(
      final NotificationInfo notification) {
    Iterable<? extends EChange<EObject>> xifexpression;
    Object newValue = notification.getNewValue();
    boolean tripleEquals = (newValue == null);
    if (tripleEquals) {
      Iterable<? extends EChange<EObject>> xblockexpression;
      Object oldValue = notification.getOldValue();
      final List<?> oldValues = (List<?>) oldValue;
      List<RemoveEReference<EObject>> mapped = new ArrayList<>();
      for (int i = 0; i < oldValues.size(); i++) {
        EObject value = (EObject) oldValues.get(oldValues.size() - 1 - i);
        final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - i;
        mapped.add(this.changeFactory.createRemoveReferenceChange(notification
                        .getNotifierModelElement(),
                        notification.getReference(),
                        value, valueIndex));
      }
      xblockexpression = this.addUnsetChangeIfNecessary(mapped, notification);

      xifexpression = xblockexpression;
    } else {
      xifexpression = this.unsetChangeOrEmpty(notification);
    }
    return xifexpression;
  }

  private List<InsertEAttributeValue<EObject, Object>> handleInsertAttribute(
      final NotificationInfo notification) {
    return List.of(
        this.changeFactory.createInsertAttributeChange(notification.getNotifierModelElement(),
            notification.getAttribute(), notification.getPosition(), notification.getNewValue()));
  }

  private List<InsertEAttributeValue<EObject, Object>> handleMultiInsertAttribute(
      final NotificationInfo notification) {
    List<?> newValue = ((List<?>) notification.getNewValue());
    List<InsertEAttributeValue<EObject, Object>> result = new ArrayList<>();
    for (int i = 0; i < newValue.size(); i++) {
      Object value = newValue.get(i);
      result.add(this.changeFactory.createInsertAttributeChange(
          notification.getNotifierModelElement(), notification.getAttribute(),
          notification.getInitialIndex() + i, value));
    }
    return result;
  }

  private Iterable<? extends EChange<EObject>> handleInsertReference(
          final NotificationInfo notification) {
    return this.surroundWithCreateAndFeatureChangesIfNecessary(
        this.changeFactory.<EObject>createInsertReferenceChange(
                notification.getNotifierModelElement(),
                notification.getReference(),
                notification.getNewModelElementValue(),
                notification.getPosition()));
  }

  private Iterable<? extends EChange<EObject>> handleMultiInsertReference(
      final NotificationInfo notification) {
    List<?> newValue = ((List<?>) notification.getNewValue());
    List<EChange<EObject>> result = new ArrayList<>();
    for (int i = 0; i < newValue.size(); i++) {
      EObject value = (EObject) newValue.get(i);
      Iterables.addAll(result, this.surroundWithCreateAndFeatureChangesIfNecessary(
          this.changeFactory.createInsertReferenceChange(
              notification.getNotifierModelElement(), notification.getReference(),
              value, notification.getInitialIndex() + i)));
    }
    return result;
  }

  private Iterable<? extends EChange<EObject>> handleInsertRootChange(
          final NotificationInfo notification) {
    return this.surroundWithCreateAndFeatureChangesIfNecessary(
            this.changeFactory.<EObject>createInsertRootChange(
                    notification.getNewModelElementValue(),
                    notification.getNotifierResource(),
                    notification.getPosition()));
  }

  private List<EChange<EObject>> handleMultiInsertRootChange(
          final NotificationInfo notification) {
    List<?> newValue = ((List<?>) notification.getNewValue());
    List<EChange<EObject>> result = new ArrayList<>();
    for (int i = 0; i < newValue.size(); i++) {
      EObject value = (EObject) newValue.get(i);
      Iterables.addAll(result, this.surroundWithCreateAndFeatureChangesIfNecessary(
          this.changeFactory.createInsertRootChange(
              value, notification.getNotifierResource(), notification.getInitialIndex() + i)));
    }
    return result;
  }

  private List<RemoveRootEObject<EObject>> handleRemoveRootChange(
          final NotificationInfo notification) {
    return List.of(this.changeFactory.createRemoveRootChange(
            notification.getOldModelElementValue(),
            notification.getNotifierResource(),
            notification.getPosition()));
  }

  private List<RemoveRootEObject<EObject>> handleMultiRemoveRootChange(
          final NotificationInfo notification) {
    List<RemoveRootEObject<EObject>> xblockexpression = null;
    Object oldValue = notification.getOldValue();
    final List<?> oldValues = ((List<?>) oldValue);
    List<RemoveRootEObject<EObject>> mapped = new ArrayList<>();
    for (int i = 0; i < oldValues.size(); i++) {
      EObject value = (EObject) oldValues.get(oldValues.size() - 1 - i);
      final int valueIndex = (notification.getInitialIndex() + oldValues.size() - 1) - i;
      mapped.add(this.changeFactory
              .createRemoveRootChange(value, notification.getNotifierResource(), valueIndex));
    }
    xblockexpression = mapped;

    return xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> handleSetUriChange(
          final NotificationInfo notification) {
    Iterable<EChange<EObject>> xblockexpression;
    Object oldValue = notification.getOldValue();
    final URI oldUri = ((URI) oldValue);
    List<EObject> contents = new ArrayList<>(notification.getNotifierResource().getContents());
    List<RemoveRootEObject<EObject>> removeChanges = new ArrayList<>();
    for (int i = 0; i < contents.size(); i++) {
      EObject value = contents.get(contents.size() - 1 - i);
      final int valueIndex = (notification.getInitialIndex() + contents.size() - 1) - i;
      final Resource oldResource =
              notification.getNotifierResource().getResourceSet().createResource(oldUri);
      removeChanges.add(this.changeFactory
              .createRemoveRootChange(value, oldResource, oldUri, valueIndex));
    }
    List<EChange<EObject>> insertChanges = new ArrayList<>();
    for (int i = 0; i < contents.size(); i++) {
      EObject value = contents.get(i);
      Iterables.addAll(insertChanges,
              this.surroundWithCreateAndFeatureChangesIfNecessary(
                      this.changeFactory.createInsertRootChange(
              value, notification.getNotifierResource(),
                              notification.getInitialIndex() + i)));
    }
    xblockexpression = Iterables.<EChange<EObject>>concat(removeChanges, insertChanges);

    return xblockexpression;
  }

  private Iterable<? extends EChange<EObject>> allAdditiveChangesForChangeRelevantFeatures(
      final EObjectAddedEChange<EObject> change) {
    final BiFunction<EObject, EAttribute, Iterable<? extends EChange<EObject>>> function =
            EChangeCreationUtil::createAdditiveEChangeForAttribute;
    final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> function1 =
            (EObject object, EReference reference) -> {
              List<? extends EChange<EObject>> xifexpression = null;
              boolean isContainment = reference.isContainment();
              if (isContainment) {
                final Predicate<EObject> function2 = (EObject referencedObject) ->
                        this.isCreateChange.test(object, referencedObject);
                xifexpression = EChangeCreationUtil
                        .createAdditiveEChangeForReferencedObject(object, reference, function2);
              }
              return xifexpression;
            };
    Iterable<? extends EChange<EObject>> walkChangeRelevantFeatures = NotificationToEChangeConverter
        .walkChangeRelevantFeatures(change.getNewValue(), function, function1);
    final BiFunction<EObject, EReference, Iterable<? extends EChange<EObject>>> function2 =
            (EObject object, EReference reference) -> {
              List<? extends EChange<EObject>> xifexpression = null;
              boolean isContainment = reference.isContainment();
              if (!isContainment) {
                final Predicate<EObject> function3 = (EObject it) -> Boolean.FALSE;
                xifexpression = EChangeCreationUtil
                        .createAdditiveEChangeForReferencedObject(object, reference, function3);
              }
              return xifexpression;
            };
    Iterable<? extends EChange<EObject>> walkChangeRelevantFeatures1 =
            NotificationToEChangeConverter
                    .walkChangeRelevantFeatures(change.getNewValue(), null, function2);
    return Iterables.concat(walkChangeRelevantFeatures, walkChangeRelevantFeatures1);
  }

  private static Iterable<? extends EChange<EObject>> walkChangeRelevantFeatures(
          final EObject eObject,
          final BiFunction<EObject, EAttribute, Iterable<?
                  extends EChange<EObject>>> attributeVisitor,
          final BiFunction<EObject, EReference, Iterable<?
                  extends EChange<EObject>>> referenceVisitor) {
    final List<EChange<EObject>> result = new ArrayList<>();
    collectChanges(eObject, attributeVisitor, referenceVisitor, result);
    return result;
  }

  private static void collectChanges(
          final EObject eObject,
          final BiFunction<EObject, EAttribute, Iterable<?
                  extends EChange<EObject>>> attributeVisitor,
          final BiFunction<EObject, EReference, Iterable<?
                  extends EChange<EObject>>> referenceVisitor,
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
        for (final EObject child : NotificationToEChangeConverter
                .getReferencedElements(eObject, reference)) {
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

  private static Iterable<EObject> getReferencedElements(
          final EObject eObject,
          final EReference reference) {
    final Object value = eObject.eGet(reference);
    if (reference.isMany()) {
      return (List<EObject>) value;
    }
    return value == null ? List.of() : List.of((EObject) value);
  }

  private List<UnsetFeature<EObject, EStructuralFeature>> unsetChangeOrEmpty(
          final NotificationInfo notification) {
    if (notification.wasUnset()) {
      return List.of(
          this.changeFactory.createUnsetFeatureChange(
              notification.getNotifierModelElement(), notification.getStructuralFeature()));
    }
    return List.of();
  }

  private <T extends EChange<EObject>> Iterable<?
          extends EChange<EObject>> addUnsetChangeIfNecessary(
      final Iterable<T> changes,
      final NotificationInfo notification) {
    if (notification.wasUnset()) {
      List<UnsetFeature<EObject, EStructuralFeature>> of = List.of(
          this.changeFactory.<EObject, EStructuralFeature>createUnsetFeatureChange(
              notification.getNotifierModelElement(), notification.getStructuralFeature()));
      return Iterables.<EChange<EObject>>concat(changes, of);
    }
    return changes;
  }

  private List<EChange<EObject>> addUnsetChangeIfNecessary(
          final EChange<EObject> change,
      final NotificationInfo notification) {
    if (notification.wasUnset()) {
      return List.of(change,
          this.changeFactory.createUnsetFeatureChange(
              notification.getNotifierModelElement(), notification.getStructuralFeature()));
    }
    return List.of(change);
  }

  private Iterable<? extends EChange<EObject>> surroundWithCreateAndFeatureChangesIfNecessary(
      final EObjectAddedEChange<EObject> change) {
    EObject switchResult = null;
    if (change instanceof UpdateReferenceEChange) {
      switchResult = ((UpdateReferenceEChange<EObject>) change).getAffectedElement();
    }
    final EObject affectedElement = switchResult;
    if (Boolean.TRUE.equals(this.isCreateChange.test(affectedElement, change.getNewValue()))) {
      final CreateEObject<EObject> createChange = this.changeFactory
              .createCreateEObjectChange(change.getNewValue());
      List<EChange<EObject>> of = List.of(createChange, change);
      Iterable<? extends EChange<EObject>> allAdditiveChangesForChangeRelevantFeatures =
              this.allAdditiveChangesForChangeRelevantFeatures(change);
      return Iterables.concat(of, allAdditiveChangesForChangeRelevantFeatures);
    }
    return List.<EObjectAddedEChange<EObject>>of(change);
  }

  public NotificationToEChangeConverter(
          final BiPredicate<EObject, EObject> isCreateChange) {
    super();
    this.isCreateChange = isCreateChange;
  }
}
