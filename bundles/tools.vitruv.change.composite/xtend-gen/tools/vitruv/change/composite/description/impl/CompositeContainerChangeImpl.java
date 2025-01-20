package tools.vitruv.change.composite.description.impl;

import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.VitruviusChange;

@SuppressWarnings("all")
public class CompositeContainerChangeImpl<Element extends Object> extends AbstractCompositeChangeImpl<Element, VitruviusChange<Element>> implements CompositeContainerChange<Element> {
  public CompositeContainerChangeImpl(final List<? extends VitruviusChange<Element>> changes) {
    super(changes);
  }

  @Override
  public CompositeContainerChangeImpl<Element> copy() {
    final Function1<VitruviusChange<Element>, VitruviusChange<Element>> _function = (VitruviusChange<Element> it) -> {
      return it.copy();
    };
    List<VitruviusChange<Element>> _mapFixed = IterableUtil.<VitruviusChange<Element>, VitruviusChange<Element>>mapFixed(this.getChanges(), _function);
    return new CompositeContainerChangeImpl<Element>(_mapFixed);
  }
}
