package tools.vitruv.change.composite.description.impl;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.AtomicEChangeHierarchicalIdResolver;
import tools.vitruv.change.atomic.hid.HierarchicalId;

class VitruviusChangeHierarchicalIdResolverTest {

  @Test
  void assignIdsAppliesCompositeChangesBackwardInReverseOrder() {
    AtomicEChangeHierarchicalIdResolver atomicChangeResolver =
        mock(AtomicEChangeHierarchicalIdResolver.class);

    EChange<EObject> firstChange = mock(EChange.class);
    EChange<EObject> secondChange = mock(EChange.class);
    EChange<EObject> thirdChange = mock(EChange.class);
    EChange<EObject> fourthChange = mock(EChange.class);
    EChange<HierarchicalId> firstAssignedChange = mock(EChange.class);
    EChange<HierarchicalId> secondAssignedChange = mock(EChange.class);
    EChange<HierarchicalId> thirdAssignedChange = mock(EChange.class);
    EChange<HierarchicalId> fourthAssignedChange = mock(EChange.class);
    when(atomicChangeResolver.applyForwardAndAssignIds(firstChange))
        .thenReturn(firstAssignedChange);
    when(atomicChangeResolver.applyForwardAndAssignIds(secondChange))
        .thenReturn(secondAssignedChange);
    when(atomicChangeResolver.applyForwardAndAssignIds(thirdChange))
        .thenReturn(thirdAssignedChange);
    when(atomicChangeResolver.applyForwardAndAssignIds(fourthChange))
        .thenReturn(fourthAssignedChange);

    TransactionalChangeImpl<EObject> firstTransaction =
        new TransactionalChangeImpl<>(List.of(firstChange, secondChange));
    TransactionalChangeImpl<EObject> secondTransaction =
        new TransactionalChangeImpl<>(List.of(thirdChange, fourthChange));
    CompositeContainerChangeImpl<EObject> compositeChange =
        new CompositeContainerChangeImpl<>(List.of(firstTransaction, secondTransaction));

    VitruviusChangeHierarchicalIdResolver resolver =
        new VitruviusChangeHierarchicalIdResolver(atomicChangeResolver);
    resolver.assignIds(compositeChange);

    InOrder inOrder = inOrder(atomicChangeResolver);
    inOrder.verify(atomicChangeResolver).applyBackward(fourthChange);
    inOrder.verify(atomicChangeResolver).applyBackward(thirdChange);
    inOrder.verify(atomicChangeResolver).applyBackward(secondChange);
    inOrder.verify(atomicChangeResolver).applyBackward(firstChange);
    inOrder.verify(atomicChangeResolver).applyForwardAndAssignIds(firstChange);
    inOrder.verify(atomicChangeResolver).applyForwardAndAssignIds(secondChange);
    inOrder.verify(atomicChangeResolver).applyForwardAndAssignIds(thirdChange);
    inOrder.verify(atomicChangeResolver).applyForwardAndAssignIds(fourthChange);
    inOrder.verify(atomicChangeResolver).endTransaction();
    inOrder.verifyNoMoreInteractions();
  }
}
