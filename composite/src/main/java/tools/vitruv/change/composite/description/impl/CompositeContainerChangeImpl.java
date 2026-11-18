package tools.vitruv.change.composite.description.impl;

import java.util.List;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.VitruviusChange;

/**
 * Default implementation of {@link CompositeContainerChange}, a composite change
 * that can contain any kind of {@link VitruviusChange}.
 *
 * @param <Element> the type of elements referenced by the contained changes
 */
public class CompositeContainerChangeImpl<Element> extends
        AbstractCompositeChangeImpl<Element, VitruviusChange<Element>> implements
        CompositeContainerChange<Element> {

  /**
   * Creates a composite change containing the given changes.
   *
   * @param changes the changes to contain, in order
   */
  public CompositeContainerChangeImpl(final List<? extends VitruviusChange<Element>> changes) {
    super(changes);
  }

  @Override
  public CompositeContainerChangeImpl<Element> copy() {
    List<VitruviusChange<Element>> mapFixed =
            this.getChanges().stream().map(VitruviusChange::copy).toList();
    CompositeContainerChangeImpl<Element> copy = new CompositeContainerChangeImpl<>(mapFixed);
    copy.annotations.putAll(this.annotations);
    return copy;
  }
}
