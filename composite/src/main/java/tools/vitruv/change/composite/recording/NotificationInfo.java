package tools.vitruv.change.composite.recording;

import java.lang.reflect.Field;
import org.eclipse.emf.common.notify.Notification;
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
  private final Notification notification;
  private String validationMessage;

  @Override
  public int getEventType() {
    throw new IllegalArgumentException("Use eventTypeEnum for type-safe event types.");
  }

  public EventType getEventTypeEnum() {
    EventType switchResult = null;
    int eventType = this.notification.getEventType();
    switch (eventType) {
      case Notification.SET:
        switchResult = EventType.SET;
        break;
      case Notification.UNSET:
        switchResult = EventType.UNSET;
        break;
      case Notification.ADD:
        switchResult = EventType.ADD;
        break;
      case Notification.REMOVE:
        switchResult = EventType.REMOVE;
        break;
      case Notification.ADD_MANY:
        switchResult = EventType.ADD_MANY;
        break;
      case Notification.REMOVE_MANY:
        switchResult = EventType.REMOVE_MANY;
        break;
      case Notification.MOVE:
        switchResult = EventType.MOVE;
        break;
      case Notification.REMOVING_ADAPTER:
        switchResult = EventType.REMOVING_ADAPTER;
        break;
      case Notification.RESOLVE:
        switchResult = EventType.RESOLVE;
        break;
      default:
        throw new IllegalArgumentException("Unexpected eventType.");
    }
    return switchResult;
  }

  /**
   * @return the structural feature affected
   */
  public EStructuralFeature getStructuralFeature() {
    EStructuralFeature xblockexpression = null;
    {
      final Object feature = this.getFeature();
      EStructuralFeature xifexpression = null;
      if ((feature instanceof EStructuralFeature)) {
        xifexpression = ((EStructuralFeature) feature);
      } else {
        xifexpression = null;
      }
      xblockexpression = xifexpression;
    }
    return xblockexpression;
  }

  public boolean isStructuralFeatureNotification() {
    return (this.isAttributeNotification() || this.isReferenceNotification());
  }

  public boolean isAttributeNotification() {
    Object feature = this.getFeature();
    return (feature instanceof EAttribute);
  }

  /**
   * @return whether this notification signals a change of a reference value
   */
  public boolean isReferenceNotification() {
    Object feature = this.getFeature();
    return (feature instanceof EReference);
  }

  /**
   * @return the EAttribute if the notification relates to an attribute, null otherwise
   */
  public EAttribute getAttribute() {
    EAttribute xblockexpression = null;
    {
      final Object feature = this.getFeature();
      EAttribute xifexpression = null;
      if ((feature instanceof EAttribute)) {
        xifexpression = ((EAttribute) feature);
      } else {
        xifexpression = null;
      }
      xblockexpression = xifexpression;
    }
    return xblockexpression;
  }

  /**
   * @return the EReference if the notification relates to a reference feature, null otherwise
   */
  public EReference getReference() {
    EReference xblockexpression = null;
    {
      final Object feature = this.getFeature();
      EReference xifexpression = null;
      if ((feature instanceof EReference)) {
        xifexpression = ((EReference) feature);
      } else {
        xifexpression = null;
      }
      xblockexpression = xifexpression;
    }
    return xblockexpression;
  }

  /**
   * @return true if the changed feature is marked transient, false otherwise
   */
  public boolean isTransient() {
    return (this.isStructuralFeatureNotification() && this.getStructuralFeature().isTransient());
  }

  /**
   * @return true if the event is of type Notification.ADD, false otherwise
   */
  public boolean isAddEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.ADD);
  }

  /**
   * @return true if the event is of type Notification.REMOVE, false otherwise
   */
  public boolean isRemoveEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.REMOVE);
  }

  /**
   * @return true if the event is of type Notification.SET, false otherwise
   */
  public boolean isSetEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.SET);
  }

  /**
   * @return true if the event is of type Notification.ADDMANY, false otherwise
   */
  public boolean isAddManyEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.ADD_MANY);
  }

  /**
   * @return true if the event is of type Notification.REMOVEMANY, false otherwise
   */
  public boolean isRemoveManyEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.REMOVE_MANY);
  }

  /**
   * @return true if the event is of type Notification.MOVE, false otherwise
   */
  public boolean isMoveEvent() {
    int eventType = this.notification.getEventType();
    return (eventType == Notification.MOVE);
  }

  /**
   * @return true if this notification is followed by more notifications in a chain, false if this
   *     is the last notification of a chain
   */
  public boolean hasNext() {
    if ((!(this.notification instanceof NotificationImpl))) {
      return false;
    }
    try {
      final Field declaredField = NotificationImpl.class.getDeclaredField("next");
      declaredField.setAccessible(true);
      final Object object = declaredField.get(this.notification);
      final Notification nextNotification = ((Notification) object);
      if ((nextNotification == null)) {
        return false;
      }
      final Object feature = nextNotification.getFeature();
      if ((feature instanceof EReference)) {
        boolean isTransient = ((EReference) feature).isTransient();
        if (isTransient) {
          return false;
        }
      }
      return true;
    } catch (final Throwable t) {
      if (t instanceof RuntimeException) {
        return false;
      } else if (t instanceof IllegalAccessException) {
        return false;
      } else if (t instanceof NoSuchFieldException) {
        return false;
      } else {
        throw new RuntimeException(t);
      }
    }
  }

  /**
   * @return @see Notification#getNewValue()
   */
  public EObject getNewModelElementValue() {
    Object newValue = this.notification.getNewValue();
    return ((EObject) newValue);
  }

  /**
   * @return @see Notification#getOldValue()
   */
  public EObject getOldModelElementValue() {
    Object oldValue = this.notification.getOldValue();
    return ((EObject) oldValue);
  }

  public int getInitialIndex() {
    int xifexpression = (int) 0;
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
   * @return true if the feature is unsettable and was in the set state before this notification.
   */
  public boolean wasUnset() {
    return (((this.isStructuralFeatureNotification() && this.getStructuralFeature().isUnsettable())
            && this.notification.wasSet())
        && (!this.getNotifierModelElement().eIsSet(this.getStructuralFeature())));
  }

  /**
   * @return @see Notification#getNotifier()
   */
  public EObject getNotifierModelElement() {
    Object notifier = this.notification.getNotifier();
    return ((EObject) notifier);
  }

  /**
   * @return @see Notification#getNotifier()
   */
  public Resource getNotifierResource() {
    Object notifier = this.notification.getNotifier();
    return ((Resource) notifier);
  }

  /**
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
    switch (this.notification.getEventType()) {
      case Notification.ADD:
        return "ADD";
      case Notification.SET:
        return "SET";
      case Notification.ADD_MANY:
        return "ADDMANY";
      case Notification.REMOVE:
        return "REMOVE";
      case Notification.REMOVE_MANY:
        return "REMOVEMANY";
      case Notification.MOVE:
        return "MOVE";
      default:
        return String.valueOf(this.notification.getEventType());
    }
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
