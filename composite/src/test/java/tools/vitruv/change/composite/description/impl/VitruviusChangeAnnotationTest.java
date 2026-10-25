package tools.vitruv.change.composite.description.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.AtomicEChangeHierarchicalIdResolver;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.composite.description.AnnotationSource;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;

/** Tests for annotations on {@link VitruviusChange} implementations. */
class VitruviusChangeAnnotationTest {
  private record Author(String name) {}

  private static final Author AUTHOR = new Author("alice");

  @Test
  void transactionalChangeStoresAnnotations() {
    TransactionalChangeImpl<EObject> change = new TransactionalChangeImpl<>(List.of());
    assertTrue(change.getAnnotation(Author.class).isEmpty());

    change.setAnnotation(Author.class, AUTHOR);

    assertEquals(Optional.of(AUTHOR), change.getAnnotation(Author.class));
    assertEquals(Map.of(Author.class, AUTHOR), change.getAnnotations());
    assertThrows(UnsupportedOperationException.class, () -> change.getAnnotations().clear());
  }

  @Test
  void transactionalChangeCopyKeepsAnnotations() {
    TransactionalChangeImpl<EObject> change = new TransactionalChangeImpl<>(List.of());
    change.setAnnotation(Author.class, AUTHOR);

    assertEquals(Optional.of(AUTHOR), change.copy().getAnnotation(Author.class));
  }

  @Test
  void compositeChangeStoresAndCopiesAnnotations() {
    CompositeContainerChangeImpl<EObject> change = new CompositeContainerChangeImpl<>(
        List.of(new TransactionalChangeImpl<>(List.of())));
    assertTrue(change.getAnnotation(Author.class).isEmpty());

    change.setAnnotation(Author.class, AUTHOR);

    assertEquals(Map.of(Author.class, AUTHOR), change.getAnnotations());
    assertEquals(Optional.of(AUTHOR), change.copy().getAnnotation(Author.class));
  }

  @Test
  void resolverKeepsAnnotations() {
    AtomicEChangeHierarchicalIdResolver atomicChangeResolver =
        mock(AtomicEChangeHierarchicalIdResolver.class);
    EChange<EObject> eChange = mock(EChange.class);
    EChange<HierarchicalId> assignedEChange = mock(EChange.class);
    when(atomicChangeResolver.applyForwardAndAssignIds(eChange)).thenReturn(assignedEChange);

    TransactionalChangeImpl<EObject> transaction = new TransactionalChangeImpl<>(List.of(eChange));
    transaction.setAnnotation(Author.class, AUTHOR);
    CompositeContainerChangeImpl<EObject> composite =
        new CompositeContainerChangeImpl<>(List.of(transaction));
    composite.setAnnotation(String.class, "composite");

    VitruviusChange<HierarchicalId> result =
        new VitruviusChangeHierarchicalIdResolver(atomicChangeResolver).assignIds(composite);

    assertEquals(Optional.of("composite"), result.getAnnotation(String.class));
    VitruviusChange<HierarchicalId> resolvedTransaction =
        ((CompositeContainerChange<HierarchicalId>) result).getChanges().get(0);
    assertTrue(resolvedTransaction instanceof TransactionalChange);
    assertEquals(Optional.of(AUTHOR), resolvedTransaction.getAnnotation(Author.class));
  }

  @Test
  void emptyAnnotationSourceHasNoAnnotations() {
    assertTrue(AnnotationSource.EMPTY.getAnnotation(Author.class).isEmpty());
  }
}
