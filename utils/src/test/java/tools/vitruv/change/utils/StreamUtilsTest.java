package tools.vitruv.change.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Tests {@link StreamUtils}. */
class StreamUtilsTest {
  @Test
  void mapsOnlyMappableElements() {
    Map<String, String> mapping = Map.of("a", "x", "c", "z");

    List<String> result = StreamUtils.mapIfMappable(Stream.of("a", "b", "c"), mapping).toList();

    assertEquals(List.of("x", "b", "z"), result);
  }
}
