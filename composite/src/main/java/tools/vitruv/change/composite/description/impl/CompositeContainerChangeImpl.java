package tools.vitruv.change.composite.description.impl;

import java.util.List;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.VitruviusChange;

public class CompositeContainerChangeImpl<Element extends Object> extends AbstractCompositeChangeImpl<Element, VitruviusChange<Element>> implements CompositeContainerChange<Element> {
  public CompositeContainerChangeImpl(final List<? extends VitruviusChange<Element>> changes) {
    super(changes);
  }

  @Override
  public CompositeContainerChangeImpl<Element> copy() {
    List<VitruviusChange<Element>> _mapFixed = this.getChanges().stream().map(it -> it.copy()).toList();
    return new CompositeContainerChangeImpl<Element>(_mapFixed);
  }
}
