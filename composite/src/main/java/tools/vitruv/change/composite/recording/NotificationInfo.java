package tools.vitruv.change.composite.recording;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * NotificationInfo is a type safe wrapper for EMF Notifications. It wraps a {@link Notification}and
 * implements a few additional getter methods
 */
class NotificationInfo implements Notification {
  private static final VarHandle NEXT_NOTIFICATION_HANDLE = createNextNotificationHandle();
  private final Notification notification;
  private String validationMessage;

  @Override
  public int getEventType() {
    throw new IllegalArgumentException("Use eventTypeEnum for type-safe event types.");
  }

  /**
   * Returns the event type of the wrapped notification as an {@link EventType}.
   *
   * @return the event type
   * @throws IllegalArgumentException if the event type is not supported
   */
  public EventType getEventTypeEnum() {
    EventType switchResult = null;
    int eventType = this.notification.getEventType();
    switchResult = switch (eventType) {
      case Notification.SET -> EventType.SET;
      case Notification.UNSET -> EventType.UNSET;
      case Notification.ADD -> EventType.ADD;
      case Notification.REMOVE -> EventType.REMOVE;
      case Notification.ADD_MANY -> EventType.ADD_MANY;
      case Notification.REMOVE_MANY -> EventType.REMOVE_MANY;
      case Notification.MOVE -> EventType.MOVE;
      case Notification.REMOVING_ADAPTER -> EventType.REMOVING_ADAPTER;
      case Notification.RESOLVE -> EventType.RESOLVE;
      default -> throw new IllegalArgumentException("Unexpected eventType.");
    };
    return switchResult;
  }

  /**
   * Returns the structural feature affected by this notification.
   *
   * @return the affected feature, or {@code null} if the feature is not an
   *     {@link EStructuralFeature}
   */
  public EStructuralFeature getStructuralFeature() {
    final Object feature = getFeature();
    return feature instanceof EStructuralFeature ? (EStructuralFeature) feature : null;
  }

  public boolean isStructuralFeatureNotification() {
    return (this.isAttributeNotification() || this.isReferenceNotification());
  }

  /**
   * Checks whether this notification signals a change of an attribute value.
   *
   * @return {@code true} if the affected feature is an {@link EAttribute}
   */
  public boolean isAttributeNotification() {
    Object feature = this.getFeature();
    return (feature instanceof EAttribute);
  }

  /**
   * Checks whether this notification signals a change of a reference value.
   *
   * @return {@code true} if the affected feature is an {@link EReference}
   */
  public boolean isReferenceNotification() {
    Object feature = this.getFeature();
    return (feature instanceof EReference);
  }

  /**
   * Returns the attribute affected by this notification.
   *
   * @return the affected attribute, or {@code null} if the notification does not
   *     relate to an attribute
   */
  public EAttribute getAttribute() {
    final Object feature = this.getFeature();
    return feature instanceof  EAttribute ? (EAttribute) feature : null;
  }

  /**
   * Returns the reference affected by this notification.
   *
   * @return the affected reference, or {@code null} if the notification does not
   *     relate to a reference
   */
  public EReference getReference() {
    final Object feature = this.getFeature();
    return feature instanceof EReference ? (EReference) feature : null;
  }

  /**
   * Checks whether the affected feature is marked as transient.
   *
   * @return {@code true} if the changed feature is transient, {@code false} otherwise
   */
  public boolean isTransient() {
    return (this.isStructuralFeatureNotification() && this.getStructuralFeature().isTransient());
  }

  /**
   * Checks whether this is an {@link Notification#ADD} event.
   *
   * @return {@code true} if the event is of type {@code ADD}, {@code false} otherwise
   */
  public boolean isAddEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.ADD);
  }

  /**
   * Checks whether this is a {@link Notification#REMOVE} event.
   *
   * @return {@code true} if the event is of type {@code REMOVE}, {@code false} otherwise
   */
  public boolean isRemoveEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.REMOVE);
  }

  /**
   * Checks whether this is a {@link Notification#SET} event.
   *
   * @return {@code true} if the event is of type {@code SET}, {@code false} otherwise
   */
  public boolean isSetEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.SET);
  }

  /**
   * Checks whether this is an {@link Notification#ADD_MANY} event.
   *
   * @return {@code true} if the event is of type {@code ADD_MANY}, {@code false} otherwise
   */
  public boolean isAddManyEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.ADD_MANY);
  }

  /**
   * Checks whether this is a {@link Notification#REMOVE_MANY} event.
   *
   * @return {@code true} if the event is of type {@code REMOVE_MANY}, {@code false} otherwise
   */
  public boolean isRemoveManyEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.REMOVE_MANY);
  }

  /**
   * Checks whether this is a {@link Notification#MOVE} event.
   *
   * @return {@code true} if the event is of type {@code MOVE}, {@code false} otherwise
   */
  public boolean isMoveEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.MOVE);
  }

  /**
   * Checks whether this notification is followed by more notifications in a chain.
   *
   * <p>A following notification that changes a transient reference is ignored.
   *
   * @return {@code true} if this notification is followed by more notifications in
   *     a chain, {@code false} if this is the last notification of a chain
   */
  public boolean hasNext() {
    if (!(this.notification instanceof NotificationImpl notificationImpl)) {
      return false;
    }
    NotificationChain next = getNextNotification(notificationImpl);
    if (!(next instanceof Notification nextNotification)) {
      return false;
    }
    Object feature = nextNotification.getFeature();
    return !(feature instanceof EReference reference) || !reference.isTransient();
  }

  private static VarHandle createNextNotificationHandle() {
    try {
      return MethodHandles.privateLookupIn(NotificationImpl.class, MethodHandles.lookup())
          .findVarHandle(NotificationImpl.class, "next", NotificationChain.class);
    } catch (final ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Returns the notification that follows the given one in its chain.
   *
   * @param notification the notification to read from
   * @return the next notification in the chain, or {@code null} if there is none
   */
  private static NotificationChain getNextNotification(final NotificationImpl notification) {
    return (NotificationChain) NEXT_NOTIFICATION_HANDLE.get(notification);
  }

  /**
   * Returns the new value of this notification as an {@link EObject}.
   *
   * @return the new value
   * @see Notification#getNewValue()
   */
  public EObject getNewModelElementValue() {
    Object newValue = this.notification.getNewValue();
    return ((EObject) newValue);
  }

  /**
   * Returns the old value of this notification as an {@link EObject}.
   *
   * @return the old value
   * @see Notification#getOldValue()
   */
  public EObject getOldModelElementValue() {
    Object oldValue = this.notification.getOldValue();
    return ((EObject) oldValue);
  }

  public int getInitialIndex() {
    int xifexpression;
    int position = this.getPosition();
    boolean tripleEquals = (position == Notification.NO_INDEX);
    if (tripleEquals) {
      xifexpression = 0;
    } else {
      xifexpression = this.getPosition();
    }
    return xifexpression;
  }

  /**
   * Checks whether this notification unset an unsettable feature.
   *
   * @return {@code true} if the feature is unsettable, was set before this
   *     notification and is no longer set afterwards
   */
  public boolean wasUnset() {
    return (((this.isStructuralFeatureNotification() && this.getStructuralFeature().isUnsettable())
            && this.notification.wasSet())
        && (!this.getNotifierModelElement().eIsSet(this.getStructuralFeature())));
  }

  /**
   * Returns the notifier of this notification as an {@link EObject}.
   *
   * @return the notifier
   * @see Notification#getNotifier()
   */
  public EObject getNotifierModelElement() {
    Object notifier = this.notification.getNotifier();
    return ((EObject) notifier);
  }

  /**
   * Returns the notifier of this notification as a {@link Resource}.
   *
   * @return the notifier
   * @see Notification#getNotifier()
   */
  public Resource getNotifierResource() {
    Object notifier = this.notification.getNotifier();
    return ((Resource) notifier);
  }

  /**
   * Returns a readable description of this notification.
   *
   * @return a string useful for debugging only
   */
  public String getDebugString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.getEventTypeDebugName());
    sb.append(" val: ").append(this.getValidationMessage());
    Object notifier = this.notification.getNotifier();
    final EObject n = ((EObject) notifier);
    sb.append(" / on: ").append(this.extractName(n));
    sb.append(".");
    sb.append(this.getFeatureDebugName());
    sb.append(" / old: ").append(this.getValueDebugString(this.getOldValue()));
    sb.append(" / new: ").append(this.getValueDebugString(this.getNewValue()));
    return sb.toString();
  }

  private String getEventTypeDebugName() {
    return switch (this.notification.getEventType()) {
      case Notification.ADD -> "ADD";
      case Notification.SET -> "SET";
      case Notification.ADD_MANY -> "ADDMANY";
      case Notification.REMOVE -> "REMOVE";
      case Notification.REMOVE_MANY -> "REMOVEMANY";
      case Notification.MOVE -> "MOVE";
      default -> String.valueOf(this.notification.getEventType());
    };
  }

  private String getFeatureDebugName() {
    if (this.isAttributeNotification()) {
      return this.getAttribute().getName();
    }
    if (this.isReferenceNotification()) {
      return this.getReference().getName();
    }
    return "";
  }

  private Object getValueDebugString(final Object value) {
    if ((value instanceof EObject eObject)) {
      return this.extractName(eObject);
    }
    return value;
  }

  private String extractName(final EObject o) {
    if ((o == null)) {
      return null;
    }
    final EStructuralFeature f = o.eClass().getEStructuralFeature("name");
    if (((f != null) && (o.eGet(f) != null))) {
      return "'" + (String) o.eGet(f) + "'";
    }
    return o.eClass().getName();
  }

  /**
   * Returns the type of the {@link Notification}.
   *
   * @return a {@link Notification} type
   */
  public Class<? extends Notification> getNotificationType() {
    return this.notification.getClass();
  }

  public NotificationInfo(final Notification notification) {
    super();
    this.notification = notification;
  }

  public Object getFeature() {
    return this.notification.getFeature();
  }

  public int getFeatureID(final Class<?> arg0) {
    return this.notification.getFeatureID(arg0);
  }

  public boolean getNewBooleanValue() {
    return this.notification.getNewBooleanValue();
  }

  public byte getNewByteValue() {
    return this.notification.getNewByteValue();
  }

  public char getNewCharValue() {
    return this.notification.getNewCharValue();
  }

  public double getNewDoubleValue() {
    return this.notification.getNewDoubleValue();
  }

  public float getNewFloatValue() {
    return this.notification.getNewFloatValue();
  }

  public int getNewIntValue() {
    return this.notification.getNewIntValue();
  }

  public long getNewLongValue() {
    return this.notification.getNewLongValue();
  }

  public short getNewShortValue() {
    return this.notification.getNewShortValue();
  }

  public String getNewStringValue() {
    return this.notification.getNewStringValue();
  }

  public Object getNewValue() {
    return this.notification.getNewValue();
  }

  public Object getNotifier() {
    return this.notification.getNotifier();
  }

  public boolean getOldBooleanValue() {
    return this.notification.getOldBooleanValue();
  }

  public byte getOldByteValue() {
    return this.notification.getOldByteValue();
  }

  public char getOldCharValue() {
    return this.notification.getOldCharValue();
  }

  public double getOldDoubleValue() {
    return this.notification.getOldDoubleValue();
  }

  public float getOldFloatValue() {
    return this.notification.getOldFloatValue();
  }

  public int getOldIntValue() {
    return this.notification.getOldIntValue();
  }

  public long getOldLongValue() {
    return this.notification.getOldLongValue();
  }

  public short getOldShortValue() {
    return this.notification.getOldShortValue();
  }

  public String getOldStringValue() {
    return this.notification.getOldStringValue();
  }

  public Object getOldValue() {
    return this.notification.getOldValue();
  }

  public int getPosition() {
    return this.notification.getPosition();
  }

  public boolean isReset() {
    return this.notification.isReset();
  }

  public boolean isTouch() {
    return this.notification.isTouch();
  }

  public boolean merge(final Notification arg0) {
    return this.notification.merge(arg0);
  }

  public boolean wasSet() {
    return this.notification.wasSet();
  }

  public String getValidationMessage() {
    return this.validationMessage;
  }

  public void setValidationMessage(final String validationMessage) {
    this.validationMessage = validationMessage;
  }
}
