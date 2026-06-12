package tools.vitruv.change.composite.description;

import java.util.Optional;
import java.util.function.Function;

/** Read-only access to typed metadata annotations carried by a change. */
public interface AnnotationSource {
    <T> Optional<T> getAnnotation(Class<T> type);

    /**
     * Creates an {@code AnnotationSource} from a raw lookup function.
     * Necessary because the generic SAM prevents direct lambda assignment.
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

    AnnotationSource EMPTY = new AnnotationSource() {
        @Override
        public <T> Optional<T> getAnnotation(Class<T> type) {
            return Optional.empty();
        }
    };
}
