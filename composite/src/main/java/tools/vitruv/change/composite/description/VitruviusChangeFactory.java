package tools.vitruv.change.composite.description;

import com.google.common.collect.Lists;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.impl.CompositeContainerChangeImpl;
import tools.vitruv.change.composite.description.impl.TransactionalChangeImpl;

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
    return new CompositeContainerChangeImpl<Element>(Lists.newArrayList(innerChanges));
  }
}
