package tools.vitruv.change.composite.recording;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.junit.jupiter.api.Test;

class NotificationToEChangeConverterTest {
  private static final int UNEXPECTED_EVENT_TYPE = 999;

  @Test
  void unexpectedEventTypeMessageIsUsedForGeneralNotifications() {
    final NotificationToEChangeConverter converter =
        new NotificationToEChangeConverter((left, right) -> false);

    for (final UnexpectedNotificationKind kind
        : List.of(
            UnexpectedNotificationKind.ATTRIBUTE,
            UnexpectedNotificationKind.REFERENCE,
            UnexpectedNotificationKind.RESOURCE_CONTENTS)) {
      final UnexpectedEventNotification notification = new UnexpectedEventNotification(kind);
      final IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> converter.convert(notification));

      assertEquals("Unexpected event type 999", exception.getMessage());
    }
  }

  @Test
  void unexpectedEventTypeMessageIncludesResourceUriContext() {
    final NotificationToEChangeConverter converter =
        new NotificationToEChangeConverter((left, right) -> false);
    final UnexpectedEventNotification notification =
        new UnexpectedEventNotification(UnexpectedNotificationKind.RESOURCE_URI);

    final IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> converter.convert(notification));

    assertEquals(
        "Unexpected event type 999 for Resource URI Notification.",
        exception.getMessage());
  }

  private enum UnexpectedNotificationKind {
    ATTRIBUTE,
    REFERENCE,
    RESOURCE_CONTENTS,
    RESOURCE_URI
  }

  private static class UnexpectedEventNotification extends NotificationInfo {
    private final UnexpectedNotificationKind kind;

    UnexpectedEventNotification(final UnexpectedNotificationKind kind) {
      super(null);
      this.kind = kind;
    }

    @Override
    public EventType getEventTypeEnum() {
      return null;
    }

    @Override
    public int getEventType() {
      return UNEXPECTED_EVENT_TYPE;
    }

    @Override
    public Object getOldValue() {
      return "old";
    }

    @Override
    public Object getNewValue() {
      return "new";
    }

    @Override
    public boolean isTouch() {
      return false;
    }

    @Override
    public boolean isTransient() {
      return false;
    }

    @Override
    public boolean isAttributeNotification() {
      return this.kind == UnexpectedNotificationKind.ATTRIBUTE;
    }

    @Override
    public boolean isReferenceNotification() {
      return this.kind == UnexpectedNotificationKind.REFERENCE;
    }

    @Override
    public Object getNotifier() {
      if (this.kind == UnexpectedNotificationKind.RESOURCE_CONTENTS
          || this.kind == UnexpectedNotificationKind.RESOURCE_URI) {
        return new ResourceImpl();
      }
      return null;
    }

    @Override
    public int getFeatureID(final Class<?> expectedClass) {
      if (this.kind == UnexpectedNotificationKind.RESOURCE_CONTENTS) {
        return Resource.RESOURCE__CONTENTS;
      }
      if (this.kind == UnexpectedNotificationKind.RESOURCE_URI) {
        return Resource.RESOURCE__URI;
      }
      return -1;
    }
  }
}
