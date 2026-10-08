package tools.vitruv.change.composite.description;

import java.util.Map;

/** Mixin for changes that can carry typed metadata annotations (e.g. author, timestamp). */
public interface Annotatable extends AnnotationSource {
  /**
   * Sets the annotation of the given type, replacing any existing annotation of that type.
   *
   * @param <T> the type of the annotation.
   * @param type the class used as key for the annotation.
   * @param value the annotation value.
   */
  <T> void setAnnotation(Class<T> type, T value);

  /**
   * Returns all annotations of this element.
   *
   * @return an unmodifiable map from annotation type to annotation value.
   */
  Map<Class<?>, Object> getAnnotations();
}
