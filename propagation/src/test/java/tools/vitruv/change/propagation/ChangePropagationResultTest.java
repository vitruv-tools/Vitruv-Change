package tools.vitruv.change.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
 * Tests {@link ChangePropagationResult}: the derived status, executed changes and cause, the
 * result without invocations, and appending results.
 */
class ChangePropagationResultTest {
  private final ChangePropagationSpecification specification = new StubSpecification();
  private final EChange<EObject> input = change();
  private final EChange<EObject> executed1 = change();
  private final EChange<EObject> executed2 = change();
  private final EChange<EObject> executed3 = change();
  private final IllegalStateException cause = new IllegalStateException("boom");

  @Test
  void unhandledIsSucceededWithoutInvocationsOrExecutedChanges() {
    var result = ChangePropagationResult.UNHANDLED;

    assertTrue(result.invocations().isEmpty());
    assertEquals(ChangePropagationStatus.SUCCEEDED, result.status());
    assertTrue(result.succeeded());
    assertTrue(result.executedChanges().isEmpty());
    assertEquals(Optional.empty(), result.cause());
  }

  @Test
  void statusIsSucceededIfAllInvocationsSucceeded() {
    var result = ChangePropagationResult.of(succeeded(executed1), succeeded(executed2));

    assertEquals(ChangePropagationStatus.SUCCEEDED, result.status());
    assertTrue(result.succeeded());
  }

  @Test
  void statusIsThatOfTheFirstInvocationThatIsNotSucceeded() {
    var blockedFirst = ChangePropagationResult.of(
        succeeded(executed1), blocked(executed2), failed(executed3, cause));
    var failedFirst = ChangePropagationResult.of(
        succeeded(executed1), failed(executed2, cause), blocked(executed3));

    assertEquals(ChangePropagationStatus.BLOCKED, blockedFirst.status());
    assertFalse(blockedFirst.succeeded());
    assertEquals(ChangePropagationStatus.FAILED, failedFirst.status());
    assertFalse(failedFirst.succeeded());
  }

  @Test
  void executedChangesAreTheConcatenationOfTheInvocationsInOrder() {
    var result = ChangePropagationResult.of(
        succeeded(executed1), succeeded(), blocked(executed2, executed3));

    assertEquals(List.of(executed1, executed2, executed3), result.executedChanges());
  }

  @Test
  void causeIsThatOfTheFirstInvocationThatIsNotSucceeded() {
    var failedResult = ChangePropagationResult.of(succeeded(executed1), failed(executed2, cause));
    var blockedFirst = ChangePropagationResult.of(blocked(executed1), failed(executed2, cause));
    var succeededResult = ChangePropagationResult.of(succeeded(executed1));

    assertEquals(Optional.of(cause), failedResult.cause());
    assertEquals(Optional.empty(), blockedFirst.cause());
    assertEquals(Optional.empty(), succeededResult.cause());
  }

  @Test
  void invocationsAreAnUnmodifiableCopy() {
    List<ChangePropagationInvocation> invocations = new ArrayList<>(List.of(succeeded(executed1)));

    var result = new ChangePropagationResult(invocations);
    invocations.add(succeeded(executed2));

    assertEquals(1, result.invocations().size());
    assertThrows(UnsupportedOperationException.class,
        () -> result.invocations().add(succeeded(executed2)));
  }

  @Test
  void invocationsMustNotBeNullOrContainNull() {
    assertThrows(NullPointerException.class, () -> new ChangePropagationResult(null));
    assertThrows(NullPointerException.class,
        () -> new ChangePropagationResult(Arrays.asList((ChangePropagationInvocation) null)));
    assertThrows(NullPointerException.class,
        () -> ChangePropagationResult.of((ChangePropagationInvocation) null));
  }

  @Test
  void appendingToASucceededResultKeepsTheInvocationsInOrder() {
    var first = ChangePropagationResult.of(succeeded(executed1));
    var second = ChangePropagationResult.of(blocked(executed2));

    var appended = first.append(second);

    assertEquals(
        List.of(first.invocations().get(0), second.invocations().get(0)),
        appended.invocations());
    assertEquals(ChangePropagationStatus.BLOCKED, appended.status());
    assertEquals(List.of(executed1, executed2), appended.executedChanges());
  }

  @Test
  void appendingToAResultThatIsNotSucceededReturnsItUnchanged() {
    var blockedResult = ChangePropagationResult.of(blocked(executed1));
    var failedResult = ChangePropagationResult.of(failed(executed1, cause));
    var next = ChangePropagationResult.of(succeeded(executed2));

    assertSame(blockedResult, blockedResult.append(next));
    assertSame(failedResult, failedResult.append(next));
  }

  @Test
  void unhandledIsTheNeutralElementOfAppend() {
    var succeededResult = ChangePropagationResult.of(succeeded(executed1));

    assertSame(succeededResult, ChangePropagationResult.UNHANDLED.append(succeededResult));
    assertSame(succeededResult, succeededResult.append(ChangePropagationResult.UNHANDLED));
    assertSame(ChangePropagationResult.UNHANDLED,
        ChangePropagationResult.UNHANDLED.append(ChangePropagationResult.UNHANDLED));
  }

  @Test
  void appendRejectsNull() {
    assertThrows(NullPointerException.class,
        () -> ChangePropagationResult.UNHANDLED.append(null));
  }

  @SafeVarargs
  private ChangePropagationInvocation succeeded(EChange<EObject>... executed) {
    return ChangePropagationInvocation.succeeded(
        specification, List.of(input), Arrays.asList(executed));
  }

  @SafeVarargs
  private ChangePropagationInvocation blocked(EChange<EObject>... executed) {
    return ChangePropagationInvocation.blocked(
        specification, List.of(input), Arrays.asList(executed));
  }

  private ChangePropagationInvocation failed(EChange<EObject> executed, Throwable cause) {
    return ChangePropagationInvocation.failed(
        specification, List.of(input), List.of(executed), cause);
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
