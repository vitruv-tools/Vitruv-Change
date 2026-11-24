package tools.vitruv.change.propagation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.AnnotationSource;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.ChangePropagationInvocation;
import tools.vitruv.change.propagation.ChangePropagationStatus;
import tools.vitruv.change.propagation.EChangeApplicationController;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * Tests that {@link CompositeChangePropagationSpecification} calls its processors in order,
 * stops at the first part that does not succeed,
 * and combines the invocations of its parts into one invocation.
 */
class CompositeChangePropagationSpecificationTest {
  private static final MetamodelDescriptor SOURCE = MetamodelDescriptor.with("modelsA");
  private static final MetamodelDescriptor TARGET = MetamodelDescriptor.with("modelsB");

  private final List<String> calls = new ArrayList<>();
  private final EChange<EObject> input = change();
  private final CompositeChangePropagationSpecification composite =
      new CompositeChangePropagationSpecification(SOURCE, TARGET);

  @Test
  void allPartsSucceedAndTheExecutedChangesAreConcatenatedInOrder() {
    var preprocessor = part("pre", ChangePropagationStatus.SUCCEEDED);
    var main1 = part("main1", ChangePropagationStatus.SUCCEEDED);
    var main2 = part("main2", ChangePropagationStatus.SUCCEEDED);
    composite.addChangePreprocessor(preprocessor);
    composite.addChangeMainprocessor(main1);
    composite.addChangeMainprocessor(main2);

    var invocation = composite.propagateChange(input, null, null);

    assertSame(composite, invocation.specification());
    assertEquals(List.of(input), invocation.receivedChanges());
    assertEquals(ChangePropagationStatus.SUCCEEDED, invocation.status());
    assertEquals(
        List.of(preprocessor.lastExecuted, main1.lastExecuted, main2.lastExecuted),
        invocation.executedChanges());
    assertEquals(List.of("pre", "main1", "main2"), calls);
  }

  @Test
  void nonSucceedingPartStopsPropagation() {
    var preprocessor = part("pre", ChangePropagationStatus.SUCCEEDED);
    var blocked = part("blocked", ChangePropagationStatus.BLOCKED);
    var later = part("later", ChangePropagationStatus.SUCCEEDED);
    composite.addChangePreprocessor(preprocessor);
    composite.addChangeMainprocessor(blocked);
    composite.addChangeMainprocessor(later);

    var invocation = composite.propagateChange(input, null, null);

    assertEquals(ChangePropagationStatus.BLOCKED, invocation.status());
    assertEquals(List.of(preprocessor.lastExecuted, blocked.lastExecuted),
        invocation.executedChanges());
    assertEquals(List.of("pre", "blocked"), calls);
  }

  @Test
  void compositeWithoutPartsSucceedsWithoutExecutedChanges() {
    var invocation = composite.propagateChange(input, null, null);

    assertEquals(ChangePropagationStatus.SUCCEEDED, invocation.status());
    assertTrue(invocation.executedChanges().isEmpty());
    assertEquals(List.of(input), invocation.receivedChanges());
  }

  @Test
  void propagateChangesCombinesTheInvocationsOfAllChangesInOrder() {
    var preprocessor = part("pre", ChangePropagationStatus.SUCCEEDED);
    var main = part("main", ChangePropagationStatus.SUCCEEDED);
    composite.addChangePreprocessor(preprocessor);
    composite.addChangeMainprocessor(main);
    var changes = List.of(change(), change());

    var invocation = composite.propagateChanges(changes, AnnotationSource.EMPTY, null, null, null);

    assertSame(composite, invocation.specification());
    assertEquals(changes, invocation.receivedChanges());
    assertEquals(ChangePropagationStatus.SUCCEEDED, invocation.status());
    assertEquals(List.of("pre", "main", "pre", "main"), calls);
    assertEquals(4, invocation.executedChanges().size());
    assertEquals(preprocessor.executedByCall.getFirst(), invocation.executedChanges().get(0));
    assertEquals(main.executedByCall.get(1), invocation.executedChanges().get(3));
  }

  @Test
  void propagateChangesStopsAtTheFirstChangeThatDoesNotSucceed() {
    composite.addChangeMainprocessor(part("blocked", ChangePropagationStatus.BLOCKED));

    var invocation = composite.propagateChanges(
        List.of(change(), change()), AnnotationSource.EMPTY, null, null,
        null);

    assertEquals(ChangePropagationStatus.BLOCKED, invocation.status());
    assertEquals(List.of("blocked"), calls);
  }

  @Test
  void noExecutedChangeAppearsTwice() {
    composite.addChangePreprocessor(part("pre", ChangePropagationStatus.SUCCEEDED));
    composite.addChangeMainprocessor(part("main1", ChangePropagationStatus.SUCCEEDED));
    composite.addChangeMainprocessor(part("main2", ChangePropagationStatus.SUCCEEDED));

    var invocation = composite.propagateChanges(
        List.of(change(), change()), AnnotationSource.EMPTY, null, null,
        null);

    Set<EChange<EObject>> distinct = new HashSet<>(invocation.executedChanges());
    assertEquals(invocation.executedChanges().size(), distinct.size());
    assertEquals(3 * 2, distinct.size());
  }

  @Test
  void applicationControllerIsForwardedToProcessorsAddedBeforeAndAfter() {
    var before = part("before", ChangePropagationStatus.SUCCEEDED);
    composite.addChangeMainprocessor(before);
    assertNull(before.applicationController);
    var preBefore = part("preBefore", ChangePropagationStatus.SUCCEEDED);
    composite.addChangePreprocessor(preBefore);
    assertNull(preBefore.applicationController);

    EChangeApplicationController<EObject> controller = new DefaultEChangeApplicationController();
    composite.setEChangeApplicationController(controller);
    assertNotNull(before.applicationController);
    assertNotNull(preBefore.applicationController);

    var after = part("after", ChangePropagationStatus.SUCCEEDED);
    composite.addChangeMainprocessor(after);
    var preAfter = part("preAfter", ChangePropagationStatus.SUCCEEDED);
    composite.addChangePreprocessor(preAfter);

    for (var part : List.of(before, preBefore, after, preAfter)) {
      assertSame(controller, part.applicationController, part.name);
    }
  }


  private ScriptedPart part(String name, ChangePropagationStatus status) {
    return new ScriptedPart(name, status);
  }

  private static EChange<EObject> change() {
    return EobjectFactory.eINSTANCE.createCreateEObject();
  }

  /**
   * A part that records its call and returns an invocation with the given status that executed one
   * new change.
   */
  private class ScriptedPart extends AbstractChangePropagationSpecification {
    private final String name;
    private final ChangePropagationStatus status;
    private final List<EChange<EObject>> executedByCall = new ArrayList<>();
    private EChange<EObject> lastExecuted;
    private EChangeApplicationController<EObject> applicationController;

    ScriptedPart(String name, ChangePropagationStatus status) {
      super(SOURCE, TARGET);
      this.name = name;
      this.status = status;
    }

    @Override
    public void setEChangeApplicationController(
        EChangeApplicationController<EObject> applicationController) {
      this.applicationController = applicationController;
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
      calls.add(name);
      lastExecuted = change();
      executedByCall.add(lastExecuted);
      return switch (status) {
        case SUCCEEDED -> ChangePropagationInvocation.succeeded(
            this, List.of(change), List.of(lastExecuted));
        case BLOCKED -> ChangePropagationInvocation.blocked(
            this, List.of(change), List.of(lastExecuted));
        case FAILED -> ChangePropagationInvocation.failed(
            this, List.of(change), List.of(lastExecuted),
            new IllegalStateException(name + " failed"));
      };
    }
  }
}
