package tools.vitruv.change.composite.recording;

/**
 * note:
 * - event type CREATE is deprecated and not added to EventYpe enum
 * - fields NO_FEATURE_ID, NO_INDEX and EVENT_TYPE_COUNT are no event types
 */
@SuppressWarnings("all")
enum EventType {
  ADD,

  ADD_MANY,

  MOVE,

  REMOVE,

  REMOVE_MANY,

  REMOVING_ADAPTER,

  RESOLVE,

  SET,

  UNSET;
}
