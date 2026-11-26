package tools.vitruv.change.composite.description.impl;

import java.util.List;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.VitruviusChange;

/**
 * Default implementation of {@link CompositeContainerChange}, a composite change
 * that can contain any kind of {@link VitruviusChange}.
 *
 * @param <E> the type of elements referenced by the contained changes
 */
public class CompositeContainerChangeImpl<E> extends
        AbstractCompositeChangeImpl<E, VitruviusChange<E>> implements
        CompositeContainerChange<E> {

  /**
   * Creates a composite change containing the given changes.
   *
   * @param changes the changes to contain, in order
   */
  public CompositeContainerChangeImpl(final List<? extends VitruviusChange<E>> changes) {
    super(changes);
  }

  @Override
  public CompositeContainerChangeImpl<E> copy() {
    List<VitruviusChange<E>> mapFixed =
            this.getChanges().stream().map(VitruviusChange::copy).toList();
    CompositeContainerChangeImpl<E> copy = new CompositeContainerChangeImpl<>(mapFixed);
    copy.annotations.putAll(this.annotations);
    return copy;
  }
}
