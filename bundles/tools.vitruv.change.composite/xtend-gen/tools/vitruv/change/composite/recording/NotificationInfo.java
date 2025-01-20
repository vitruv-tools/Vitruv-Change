package tools.vitruv.change.composite.recording;

import java.lang.reflect.Field;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.Delegate;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * NotificationInfo is a type safe wrapper for EMF Notifications. It wraps a {@link Notification}and implements a few
 * additional getter methods
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
class NotificationInfo implements Notification {
  @Delegate
  private final Notification notification;

  @Accessors
  private String validationMessage;

  @Override
  public int getEventType() {
    throw new IllegalArgumentException("Use eventTypeEnum for type-safe event types.");
  }

  public EventType getEventTypeEnum() {
    EventType _switchResult = null;
    int _eventType = this.notification.getEventType();
    switch (_eventType) {
      case Notification.SET:
        _switchResult = EventType.SET;
        break;
      case Notification.UNSET:
        _switchResult = EventType.UNSET;
        break;
      case Notification.ADD:
        _switchResult = EventType.ADD;
        break;
      case Notification.REMOVE:
        _switchResult = EventType.REMOVE;
        break;
      case Notification.ADD_MANY:
        _switchResult = EventType.ADD_MANY;
        break;
      case Notification.REMOVE_MANY:
        _switchResult = EventType.REMOVE_MANY;
        break;
      case Notification.MOVE:
        _switchResult = EventType.MOVE;
        break;
      case Notification.REMOVING_ADAPTER:
        _switchResult = EventType.REMOVING_ADAPTER;
        break;
      case Notification.RESOLVE:
        _switchResult = EventType.RESOLVE;
        break;
      default:
        throw new IllegalArgumentException("Unexpected eventType.");
    }
    return _switchResult;
  }

  /**
   * @return the structural feature affected
   */
  public EStructuralFeature getStructuralFeature() {
    EStructuralFeature _xblockexpression = null;
    {
      final Object feature = this.getFeature();
      EStructuralFeature _xifexpression = null;
      if ((feature instanceof EStructuralFeature)) {
        _xifexpression = ((EStructuralFeature)feature);
      } else {
        _xifexpression = null;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  public boolean isStructuralFeatureNotification() {
    return (this.isAttributeNotification() || this.isReferenceNotification());
  }

  public boolean isAttributeNotification() {
    Object _feature = this.getFeature();
    return (_feature instanceof EAttribute);
  }

  /**
   * @return whether this notification signals a change of a reference value
   */
  public boolean isReferenceNotification() {
    Object _feature = this.getFeature();
    return (_feature instanceof EReference);
  }

  /**
   * @return the EAttribute if the notification relates to an attribute, null otherwise
   */
  public EAttribute getAttribute() {
    EAttribute _xblockexpression = null;
    {
      final Object feature = this.getFeature();
      EAttribute _xifexpression = null;
      if ((feature instanceof EAttribute)) {
        _xifexpression = ((EAttribute)feature);
      } else {
        _xifexpression = null;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  /**
   * @return the EReference if the notification relates to a reference feature, null otherwise
   */
  public EReference getReference() {
    EReference _xblockexpression = null;
    {
      final Object feature = this.getFeature();
      EReference _xifexpression = null;
      if ((feature instanceof EReference)) {
        _xifexpression = ((EReference)feature);
      } else {
        _xifexpression = null;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
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
    int _eventType = this.getEventType();
    return (_eventType == Notification.ADD);
  }

  /**
   * @return true if the event is of type Notification.REMOVE, false otherwise
   */
  public boolean isRemoveEvent() {
    int _eventType = this.getEventType();
    return (_eventType == Notification.REMOVE);
  }

  /**
   * @return true if the event is of type Notification.SET, false otherwise
   */
  public boolean isSetEvent() {
    int _eventType = this.getEventType();
    return (_eventType == Notification.SET);
  }

  /**
   * @return true if the event is of type Notification.ADD_MANY, false otherwise
   */
  public boolean isAddManyEvent() {
    int _eventType = this.getEventType();
    return (_eventType == Notification.ADD_MANY);
  }

  /**
   * @return true if the event is of type Notification.REMOVE_MANY, false otherwise
   */
  public boolean isRemoveManyEvent() {
    int _eventType = this.getEventType();
    return (_eventType == Notification.REMOVE_MANY);
  }

  /**
   * @return true if the event is of type Notification.MOVE, false otherwise
   */
  public boolean isMoveEvent() {
    int _eventType = this.getEventType();
    return (_eventType == Notification.MOVE);
  }

  /**
   * @return true if this notification is followed by more notifications in a chain, false if this is the last
   * notification of a chain
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
        boolean _isTransient = ((EReference)feature).isTransient();
        if (_isTransient) {
          return false;
        }
      }
      return true;
    } catch (final Throwable _t) {
      if (_t instanceof RuntimeException) {
        return false;
      } else if (_t instanceof IllegalAccessException) {
        return false;
      } else if (_t instanceof NoSuchFieldException) {
        return false;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }

  /**
   * @return @see Notification#getNewValue()
   */
  public EObject getNewModelElementValue() {
    Object _newValue = this.notification.getNewValue();
    return ((EObject) _newValue);
  }

  /**
   * @return @see Notification#getOldValue()
   */
  public EObject getOldModelElementValue() {
    Object _oldValue = this.notification.getOldValue();
    return ((EObject) _oldValue);
  }

  public int getInitialIndex() {
    int _xifexpression = (int) 0;
    int _position = this.getPosition();
    boolean _tripleEquals = (_position == NotificationInfo.NO_INDEX);
    if (_tripleEquals) {
      _xifexpression = 0;
    } else {
      _xifexpression = this.getPosition();
    }
    return _xifexpression;
  }

  /**
   * @return true if the feature is unsettable and was in the set state before this notification.
   */
  public boolean wasUnset() {
    return (((this.isStructuralFeatureNotification() && this.getStructuralFeature().isUnsettable()) && this.notification.wasSet()) && 
      (!this.getNotifierModelElement().eIsSet(this.getStructuralFeature())));
  }

  /**
   * @return @see Notification#getNotifier()
   */
  public EObject getNotifierModelElement() {
    Object _notifier = this.notification.getNotifier();
    return ((EObject) _notifier);
  }

  /**
   * @return @see Notification#getNotifier()
   */
  public Resource getNotifierResource() {
    Object _notifier = this.notification.getNotifier();
    return ((Resource) _notifier);
  }

  /**
   * @return a string useful for debugging only
   */
  public String getDebugString() {
    final StringBuilder sb = new StringBuilder();
    boolean _isAddEvent = this.isAddEvent();
    if (_isAddEvent) {
      sb.append("ADD");
    } else {
      boolean _isSetEvent = this.isSetEvent();
      if (_isSetEvent) {
        sb.append("SET");
      } else {
        boolean _isAddManyEvent = this.isAddManyEvent();
        if (_isAddManyEvent) {
          sb.append("ADD_MANY");
        } else {
          boolean _isRemoveEvent = this.isRemoveEvent();
          if (_isRemoveEvent) {
            sb.append("REMOVE");
          } else {
            boolean _isRemoveManyEvent = this.isRemoveManyEvent();
            if (_isRemoveManyEvent) {
              sb.append("REMOVE_MANY");
            } else {
              boolean _isMoveEvent = this.isMoveEvent();
              if (_isMoveEvent) {
                sb.append("MOVE");
              } else {
                sb.append(this.getEventType());
              }
            }
          }
        }
      }
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(" ");
    _builder.append("val: ");
    String _validationMessage = this.getValidationMessage();
    _builder.append(_validationMessage, " ");
    sb.append(_builder);
    Object _notifier = this.notification.getNotifier();
    final EObject n = ((EObject) _notifier);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append(" ");
    _builder_1.append("/ on: ");
    String _extractName = this.extractName(n);
    _builder_1.append(_extractName, " ");
    sb.append(_builder_1);
    sb.append(".");
    boolean _isAttributeNotification = this.isAttributeNotification();
    if (_isAttributeNotification) {
      sb.append(this.getAttribute().getName());
    } else {
      boolean _isReferenceNotification = this.isReferenceNotification();
      if (_isReferenceNotification) {
        sb.append(this.getReference().getName());
      }
    }
    sb.append(" / old: ");
    Object _oldValue = this.getOldValue();
    if ((_oldValue instanceof EObject)) {
      Object _oldValue_1 = this.getOldValue();
      sb.append(this.extractName(((EObject) _oldValue_1)));
    } else {
      sb.append(this.getOldValue());
    }
    sb.append(" / new: ");
    Object _newValue = this.getNewValue();
    if ((_newValue instanceof EObject)) {
      Object _newValue_1 = this.getNewValue();
      sb.append(this.extractName(((EObject) _newValue_1)));
    } else {
      sb.append(this.getNewValue());
    }
    return sb.toString();
  }

  private String extractName(final EObject o) {
    if ((o == null)) {
      return null;
    }
    final EStructuralFeature f = o.eClass().getEStructuralFeature("name");
    if (((f != null) && (o.eGet(f) != null))) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("\'");
      Object _eGet = o.eGet(f);
      _builder.append(((String) _eGet));
      _builder.append("\'");
      return _builder.toString();
    }
    return o.eClass().getName();
  }

  /**
   * Returns the type of the {@link Notification}.
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

  @Pure
  public String getValidationMessage() {
    return this.validationMessage;
  }

  public void setValidationMessage(final String validationMessage) {
    this.validationMessage = validationMessage;
  }
}
