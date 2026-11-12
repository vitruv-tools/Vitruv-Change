package tools.vitruv.change.atomic.uuid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UuidTest {

  @Test
  @DisplayName("the raw value is the textual value of the UUID")
  void rawValue() {
    Uuid uuid = new Uuid("6f1c0e4a-2b5d-4c11-9a77-0e0c4d5e6f70");

    assertEquals("6f1c0e4a-2b5d-4c11-9a77-0e0c4d5e6f70", uuid.getRawValue());
    assertEquals("Uuid(6f1c0e4a-2b5d-4c11-9a77-0e0c4d5e6f70)", uuid.toString());
  }

  @Test
  @DisplayName("UUIDs are equal if and only if their raw values are equal")
  void equalityFollowsRawValue() {
    assertEquals(new Uuid("a"), new Uuid("a"));
    assertEquals(new Uuid("a").hashCode(), new Uuid("a").hashCode());
    assertNotEquals(new Uuid("a"), new Uuid("b"));
  }
}
