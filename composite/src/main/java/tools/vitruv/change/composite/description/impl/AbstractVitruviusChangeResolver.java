package tools.vitruv.change.composite.description.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.Annotatable;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;

/**
 * Abstract implementation of a {@link VitruviusChangeResolver} that provides a generic method for
 * transforming any {@link VitruviusChange}.
 */
abstract class AbstractVitruviusChangeResolver<Id> implements VitruviusChangeResolver<Id> {
  /**
   * Generic method for transforming any {@link VitruviusChange}. Changes of a container change are
   * transformed sequentially and recursively. Changes of a transactional change are passed
   * sequentially to the {@code changeHandler}. After completely handling a transactional change,
   * the {@code onTransactionEnd} handler is called with the transformed change.
   *
   * @param change the change to transform
   * @param changeHandler the handler for transforming a single {@code EChange}.
   * @param onTransactionEnd any cleanup logic after a transactional change has been completely
   *     transformed. This might be called multiple times with different changes if the passed
   *     change is a composite change.
   * @throws IllegalStateException if the change cannot be resolved or applied.
   */
  protected <Source, Target> VitruviusChange<Target> transformVitruviusChange(
      VitruviusChange<Source> change,
      Function<EChange<Source>, EChange<Target>> changeHandler,
      Consumer<TransactionalChange<Target>> onTransactionEnd) {
    if (change instanceof CompositeContainerChangeImpl<Source> compositeChange) {
      CompositeContainerChangeImpl<Target> result = new CompositeContainerChangeImpl<>(
          compositeChange.getChanges().stream()
              .map(c -> transformVitruviusChange(c, changeHandler, onTransactionEnd))
              .toList());
      copyAnnotations(change, result);
      return result;
    } else if (change instanceof TransactionalChangeImpl<Source> transactionalChange) {
      List<EChange<Target>> resolvedChanges =
          transactionalChange.getEChanges().stream().map(changeHandler::apply).toList();
      TransactionalChangeImpl<Target> result = new TransactionalChangeImpl<>(resolvedChanges);
      result.setUserInteractions(change.getUserInteractions());
      copyAnnotations(change, result);
      onTransactionEnd.accept(result);
      return result;
    }
    throw new IllegalStateException(
        "trying to transform unknown change of class " + change.getClass().getSimpleName());
  }

  /**
   * Copies all annotations from {@code source} to {@code target}. Transformation creates fresh
   * change instances, so annotations would otherwise be silently dropped.
   */
  private static void copyAnnotations(Annotatable source, Annotatable target) {
    source.getAnnotations().forEach((type, value) -> setAnnotation(target, type, value));
  }

  private static <T> void setAnnotation(Annotatable target, Class<T> type, Object value) {
    target.setAnnotation(type, type.cast(value));
  }
}
