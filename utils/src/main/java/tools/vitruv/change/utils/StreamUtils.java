package tools.vitruv.change.utils;

import java.util.Map;
import java.util.stream.Stream;

/**
 * A utility class with stream operations.
 */
public class StreamUtils {
  private StreamUtils() {

  }

  /**
   * Maps each element of the source stream to the element provided by the map,
   * if the element is in the map. Otherwise, the element is returned directly.
   *
   * @param <T> the type of the elements in the stream
   * @param source the source stream
   * @param map the map to use for mapping the elements
   * @return a stream with the mapped elements
   */
  public static <T> Stream<T> mapIfMappable(Stream<T> source, Map<T, T> map) {
    return source.map(element -> map.getOrDefault(element, element));
  }
}
