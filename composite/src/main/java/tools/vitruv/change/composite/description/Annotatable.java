package tools.vitruv.change.composite.description;

import java.util.Map;

/** Mixin for changes that can carry typed metadata annotations (e.g. author, timestamp). */
public interface Annotatable extends AnnotationSource {
    <T> void setAnnotation(Class<T> type, T value);

    Map<Class<?>, Object> getAnnotations();
}
