package tools.vitruv.change.composite.description;

import java.util.List;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.impl.CompositeContainerChangeImpl;
import tools.vitruv.change.composite.description.impl.TransactionalChangeImpl;

@SuppressWarnings("all")
public class VitruviusChangeFactory {
  private static VitruviusChangeFactory instance;

  private VitruviusChangeFactory() {
  }

  public static VitruviusChangeFactory getInstance() {
    if ((VitruviusChangeFactory.instance == null)) {
      VitruviusChangeFactory _vitruviusChangeFactory = new VitruviusChangeFactory();
      VitruviusChangeFactory.instance = _vitruviusChangeFactory;
    }
    return VitruviusChangeFactory.instance;
  }

  public <Element extends Object> TransactionalChange<Element> createTransactionalChange(final Iterable<? extends EChange<Element>> changes) {
    return new TransactionalChangeImpl<Element>(changes);
  }

  public <Element extends Object> CompositeContainerChange<Element> createCompositeChange(final Iterable<? extends VitruviusChange<Element>> innerChanges) {
    List<? extends VitruviusChange<Element>> _list = IterableExtensions.toList(innerChanges);
    return new CompositeContainerChangeImpl<Element>(_list);
  }
}
