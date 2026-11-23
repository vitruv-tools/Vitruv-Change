package tools.vitruv.change.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * Tests the invariants, the factories and the combining constructor of
 * {@link ChangePropagationInvocation}.
 */
class ChangePropagationInvocationTest {
  private final ChangePropagationSpecification specification = new StubSpecification();
  private final EChange<EObject> received = change();
  private final EChange<EObject> executed1 = change();
  private final EChange<EObject> executed2 = change();
  private final EChange<EObject> executed3 = change();
  private final IllegalStateException cause = new IllegalStateException("boom");

  @Test
  void checkCorrectConstructorStates() {
    var invocation = ChangePropagationInvocation.succeeded(
        specification, List.of(received), List.of(executed1));

    assertSame(specification, invocation.specification());
    assertEquals(List.of(received), invocation.receivedChanges());
    assertEquals(ChangePropagationStatus.SUCCEEDED, invocation.status());
    assertEquals(List.of(executed1), invocation.executedChanges());
    assertNull(invocation.cause());

    var blockedInvocation = ChangePropagationInvocation.blocked(
        specification, List.of(received), List.of(executed1));

    assertEquals(ChangePropagationStatus.BLOCKED, blockedInvocation.status());
    assertEquals(List.of(executed1), blockedInvocation.executedChanges());
    assertNull(blockedInvocation.cause());

    var failedInvocation = ChangePropagationInvocation.failed(
        specification, List.of(received), List.of(executed1), cause);

    assertEquals(ChangePropagationStatus.FAILED, failedInvocation.status());
    assertEquals(List.of(executed1), failedInvocation.executedChanges());
    assertSame(cause, failedInvocation.cause());

    var failedInvocationWithoutCause = ChangePropagationInvocation.failed(
        specification, List.of(received), List.of(), null);

    assertEquals(ChangePropagationStatus.FAILED, failedInvocationWithoutCause.status());
    assertNull(failedInvocationWithoutCause.cause());
  }

  @Test
  void constructorChecksSucceed() {
    for (ChangePropagationStatus status : ChangePropagationStatus.values()) {
      if (status == ChangePropagationStatus.FAILED) {
        continue;
      }
      List<EChange<EObject>> receivedChanges = List.of(received);
      assertThrows(IllegalArgumentException.class,
          () -> new ChangePropagationInvocation(
              specification, receivedChanges, status, List.of(), cause),
          "a cause must be rejected for " + status);
    }

    assertThrows(NullPointerException.class, () -> new ChangePropagationInvocation(
        null, List.of(), ChangePropagationStatus.SUCCEEDED, List.of(), null));
    assertThrows(NullPointerException.class, () -> new ChangePropagationInvocation(
        specification, null, ChangePropagationStatus.SUCCEEDED, List.of(), null));
    assertThrows(NullPointerException.class, () -> new ChangePropagationInvocation(
        specification, List.of(), null, List.of(), null));
    assertThrows(NullPointerException.class, () -> new ChangePropagationInvocation(
        specification, List.of(), ChangePropagationStatus.SUCCEEDED, null, null));

    List<EChange<EObject>> withNull = Arrays.asList(executed1, null);

    assertThrows(NullPointerException.class,
        () -> ChangePropagationInvocation.succeeded(specification, withNull, List.of()));
    assertThrows(NullPointerException.class,
        () -> ChangePropagationInvocation.succeeded(specification, List.of(), withNull));
  }

  @Test
  void listsAreUnmodifiableCopies() {
    List<EChange<EObject>> receivedList = new ArrayList<>(List.of(received));
    List<EChange<EObject>> executedList = new ArrayList<>(List.of(executed1));

    var invocation = ChangePropagationInvocation.succeeded(
        specification, receivedList, executedList);
    receivedList.add(executed2);
    executedList.add(executed2);

    List<EChange<EObject>> receivedChanges = invocation.receivedChanges();
    assertEquals(List.of(received), receivedChanges);
    List<EChange<EObject>> executedChanges = invocation.executedChanges();
    assertEquals(List.of(executed1), executedChanges);
    assertThrows(UnsupportedOperationException.class,
        () -> receivedChanges.add(executed2));
    assertThrows(UnsupportedOperationException.class,
        () -> executedChanges.add(executed2));
  }


  @Test
  void combiningSucceededInvocationsConcatenatesTheExecutedChangesInOrder() {
    var combineAllSucceed = new ChangePropagationInvocation(specification, List.of(received),
        List.of(
        ChangePropagationInvocation.succeeded(
            specification, List.of(received), List.of(executed1)),
        ChangePropagationInvocation.succeeded(
            specification, List.of(received), List.of()),
        ChangePropagationInvocation.succeeded(
            specification, List.of(received), List.of(executed2, executed3))));

    assertEquals(ChangePropagationStatus.SUCCEEDED, combineAllSucceed.status());
    assertEquals(List.of(executed1, executed2, executed3), combineAllSucceed.executedChanges());
    assertNull(combineAllSucceed.cause());

    var combineAndFailOnSecond = new ChangePropagationInvocation(specification,
        List.of(received),
        List.of(
        ChangePropagationInvocation.succeeded(
            specification, List.of(received), List.of(executed1)),
        ChangePropagationInvocation.failed(
            specification, List.of(received), List.of(executed2), cause),
        ChangePropagationInvocation.blocked(
            specification, List.of(received), List.of(executed3))));

    assertEquals(ChangePropagationStatus.FAILED, combineAndFailOnSecond.status());
    assertSame(cause, combineAndFailOnSecond.cause());
    assertEquals(List.of(executed1, executed2, executed3),
        combineAndFailOnSecond.executedChanges());

    var combineFailedToBlocked = new ChangePropagationInvocation(specification, List.of(received),
        List.of(
        ChangePropagationInvocation.blocked(
            specification, List.of(received), List.of(executed1)),
        ChangePropagationInvocation.failed(
            specification, List.of(received), List.of(), cause)));

    assertEquals(ChangePropagationStatus.BLOCKED, combineFailedToBlocked.status());
    assertNull(combineFailedToBlocked.cause());

    // Combination with null arguments fails
    assertThrows(NullPointerException.class,
        () -> new ChangePropagationInvocation(specification, List.of(), null));
    assertThrows(NullPointerException.class,
        () -> new ChangePropagationInvocation(
            specification, List.of(), Collections.singletonList(null)));
  }

  private static EChange<EObject> change() {
    return EobjectFactory.eINSTANCE.createCreateEObject();
  }

  private static class StubSpecification extends AbstractChangePropagationSpecification {
    StubSpecification() {
      super(MetamodelDescriptor.with("modelsA"), MetamodelDescriptor.with("modelsB"));
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public ChangePropagationInvocation propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      return ChangePropagationInvocation.succeeded(this, List.of(change), List.of());
    }
  }
}
