package tools.vitruv.change.composite.description;

import java.util.Optional;
import java.util.function.Function;

/** Read-only access to typed metadata annotations carried by a change. */
public interface AnnotationSource {
  /** An {@code AnnotationSource} without any annotations. */
  AnnotationSource EMPTY = new AnnotationSource() {
    @Override
    public <T> Optional<T> getAnnotation(Class<T> type) {
      return Optional.empty();
    }
  };

  /**
   * Returns the annotation of the given type.
   *
   * @param <T> the type of the annotation.
   * @param type the class used as key for the annotation.
   * @return the annotation, or an empty {@link Optional} if none of that type is present.
   */
  <T> Optional<T> getAnnotation(Class<T> type);

  /**
   * Creates an {@code AnnotationSource} from a raw lookup function.
   * Necessary because the generic SAM prevents direct lambda assignment.
   *
   * @param lookup function returning the annotation for a given type.
   * @return an {@code AnnotationSource} delegating to {@code lookup}.
   */
  @SuppressWarnings("unchecked")
  static AnnotationSource of(Function<Class<?>, Optional<?>> lookup) {
    return new AnnotationSource() {
      @Override
      public <T> Optional<T> getAnnotation(Class<T> type) {
        return (Optional<T>) lookup.apply(type);
      }
    };
  }
}
