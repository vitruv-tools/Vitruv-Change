package tools.vitruv.change.composite.description.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;

/** Tests {@link VitruviusChange#getTransactionalChangeSequence()}. */
class TransactionalChangeSequenceTest {
  private final VitruviusChangeFactory factory = VitruviusChangeFactory.getInstance();

  @Test
  void transactionalChangeIsItsOwnSequence() {
    TransactionalChange<EObject> change = createTransactionalChange();

    List<TransactionalChange<EObject>> sequence = toList(change.getTransactionalChangeSequence());

    assertEquals(1, sequence.size());
    assertSame(change, sequence.get(0));
  }

  @Test
  void compositeChangeFlattensNestedChangesInOrder() {
    TransactionalChange<EObject> first = createTransactionalChange();
    TransactionalChange<EObject> second = createTransactionalChange();
    TransactionalChange<EObject> third = createTransactionalChange();
    VitruviusChange<EObject> nested = factory.createCompositeChange(List.of(first, second));
    VitruviusChange<EObject> outer = factory.createCompositeChange(List.of(nested, third));

    List<TransactionalChange<EObject>> sequence = toList(outer.getTransactionalChangeSequence());

    assertEquals(3, sequence.size());
    assertSame(first, sequence.get(0));
    assertSame(second, sequence.get(1));
    assertSame(third, sequence.get(2));
  }

  private TransactionalChange<EObject> createTransactionalChange() {
    EChange<EObject> change = EobjectFactory.eINSTANCE.createCreateEObject();
    return factory.createTransactionalChange(List.of(change));
  }

  private static <T> List<T> toList(Iterable<T> iterable) {
    List<T> result = new ArrayList<>();
    iterable.forEach(result::add);
    return result;
  }
}
