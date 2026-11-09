package tools.vitruv.change.propagation;

import tools.vitruv.change.atomic.EChange;

/**
 * An interface to control application of {@link EChange}s in the context of
 * one {@link tools.vitruv.change.composite.description.VitruviusChange}.
 *
 * <p>Besides making the undo of {@link EChange}s explicit in case of failed
 * consistency propagation, it also abstracts away applicability checks,
 * as they are required for concurrent execution.
 *
 * @param <Content> content of the {@link EChange}s applied.
 */
public interface EChangeApplicationController<Content> {
  /**
   * Tests if {@code change} can be applied at this time, and makes
   * any preparations to enable its application.
   * For example, if multiple changes are propagated in parallel, {@code prepare}
   * is responsible for acquiring the required locks.
   *
   * @param change {@link EChange}
   * @return boolean
   */
  boolean prepare(EChange<Content> change);

  /**
   * Applies {@code change} during change propagation.
   * {@code apply} may only be called after {@code prepare}, otherwise, an exception
   * may be thrown.
   * {@code apply} also guarantees that {@code change} is applied atomically.
   *
   * @param change {@link EChange}
   * @return true
   *      if {@code change} was applied without any problems, false if it could not be applied
   */
  boolean apply(EChange<Content> change);

  /**
   * Finishes the execution of {@code change}.
   * Any preparations that were done during {@code prepare} are reverted.
   * For example, if a set of locks was acquired in the prepare step, then {@code finish}
   * releases this set.
   *
   * @param change {@link EChange}
   */
  void finish(EChange<Content> change);

  /**
   * Undoes the effects of {@code change}.
   * {@code change} must have previously been applied.
   *
   * @param change {@link EChange}
   */
  void undo(EChange<Content> change);
}
