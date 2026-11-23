package tools.vitruv.change.composite.description;

import com.google.common.collect.Lists;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.impl.CompositeContainerChangeImpl;
import tools.vitruv.change.composite.description.impl.TransactionalChangeImpl;

/**
 * Factory for creating {@link VitruviusChange}s.
 *
 * <p>Use {@link #getInstance()} to obtain the shared instance.
 */
public class VitruviusChangeFactory {
  private static VitruviusChangeFactory instance;

  private VitruviusChangeFactory() {
  }

  /**
   * Returns the shared instance of this factory, creating it on first use.
   *
   * @return the factory instance
   */
  public static VitruviusChangeFactory getInstance() {
    if ((VitruviusChangeFactory.instance == null)) {
      VitruviusChangeFactory.instance = new VitruviusChangeFactory();
    }
    return VitruviusChangeFactory.instance;
  }

  /**
   * Creates a transactional change containing the given atomic changes.
   *
   * @param <E> the type of Elements referenced by the changes
   * @param changes the atomic changes, in order; must not be {@code null}
   * @return a new transactional change
   */
  public <E> TransactionalChange<E> createTransactionalChange(
          final Iterable<? extends EChange<E>> changes) {
    return new TransactionalChangeImpl<>(changes);
  }

  /**
   * Creates a composite change containing the given changes.
   *
   * @param <E> the type of Elements referenced by the changes
   * @param innerChanges the changes to contain, in order
   * @return a new composite change
   */
  public <E> CompositeContainerChange<E> createCompositeChange(
          final Iterable<? extends VitruviusChange<E>> innerChanges) {
    return new CompositeContainerChangeImpl<>(Lists.newArrayList(innerChanges));
  }
}
