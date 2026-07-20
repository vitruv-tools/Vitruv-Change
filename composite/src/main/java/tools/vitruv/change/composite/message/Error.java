package tools.vitruv.change.composite.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Holds constant error messages.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Error {
  public static final String UNEXPECTED_EVENT_TYPE = "Unexpected event type ";
  public static final String RESOURCE_URI_NOTIFICATION = " for Resource URI Notification.";
}
