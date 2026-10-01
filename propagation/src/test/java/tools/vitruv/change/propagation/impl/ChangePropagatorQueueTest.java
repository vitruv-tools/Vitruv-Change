package tools.vitruv.change.propagation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.CompositeChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.ConfirmationUserInteraction;
import tools.vitruv.change.interaction.InteractionFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.PendingUserInteraction;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository;
import tools.vitruv.change.propagation.ChangeRecordingModelRepository;
import tools.vitruv.change.propagation.NonDeferrablePropagationException;
import tools.vitruv.change.propagation.PropagationBlockReason;
import tools.vitruv.change.propagation.QueuedChange;
import tools.vitruv.change.propagation.QueuedPropagationStep;
import tools.vitruv.change.propagation.QueuedPropagationStep.Outcome;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * Tests the change propagation queue of {@link ChangePropagator} with a mocked model repository and
 * the real user interaction machinery.
 */
class ChangePropagatorQueueTest {
  private static final String QUESTION = "Delete the corresponding class?";

  private final MetamodelDescriptor models = MetamodelDescriptor.with("models");
  private final MetamodelDescriptor otherModels = MetamodelDescriptor.with("otherModels");

  /** Changes the specification currently running wants the repository to report as recorded. */
  private final List<TransactionalChange<EObject>> recordedByCurrentRun = new ArrayList<>();

  private ChangeRecordingModelRepository repository;
  private InteractionResultProvider blockingProvider;
  private InternalUserInteractor userInteractor;
  private ScriptedSpecification modelsSpec;
  private ScriptedSpecification otherModelsSpec;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    repository = mock(ChangeRecordingModelRepository.class);
    when(repository.getCorrespondenceModel())
        .thenReturn(mock(EditableCorrespondenceModelView.class));
    when(repository.recordChanges(any()))
        .thenAnswer(
            invocation -> {
              recordedByCurrentRun.clear();
              ((Runnable) invocation.getArgument(0)).run();
              return List.copyOf(recordedByCurrentRun);
            });
    // The provider a VSUM would normally use; the queue must never wait on it.
    blockingProvider = mock(InteractionResultProvider.class);
    userInteractor = UserInteractionFactory.instance.createUserInteractor(blockingProvider);
    modelsSpec = new ScriptedSpecification(models, otherModels);
    otherModelsSpec = new ScriptedSpecification(otherModels, models);
  }

  // ---- enqueueing ------------------------------------------------------------------------------

  @Test
  void enqueue_usesTheGivenTaskId_andRejectsIdsInUse() {
    ChangePropagator propagator = propagator();

    assertEquals(42, propagator.enqueueChange(input(change(models)), 42));

    assertThrows(
        IllegalArgumentException.class, () -> propagator.enqueueChange(input(change(models)), 42));
    QueuedChange queued = propagator.getQueuedChange(42).orElseThrow();
    assertEquals(42, queued.rootTaskId());
    assertNull(queued.parentTaskId());
    assertFalse(queued.blocked());
  }

  @Test
  void enqueue_generatesDistinctPositiveTaskIds() {
    ChangePropagator propagator = propagator();
    Set<Integer> ids = new HashSet<>();

    for (int i = 0; i < 200; i++) {
      ids.add(propagator.enqueueChange(input(change(models))));
    }

    assertEquals(200, ids.size());
    assertTrue(ids.stream().allMatch(id -> id > 0));
    assertEquals(200, propagator.getQueuedChanges().size());
  }

  // ---- propagation order
  // -------------------------------------------------------------------------

  @Test
  void propagateNext_queuesTransitiveChangesAtTheFront_soOrderMatchesSynchronousPropagation() {
    TransactionalChange<EObject> produced = change(otherModels);
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(produced);
    ChangePropagator propagator = propagator();
    VitruviusChange<Uuid> first = input(change(models));
    propagator.enqueueChange(first, 1);
    propagator.enqueueChange(input(change(models)), 2);

    QueuedPropagationStep step1 = propagator.propagateNext().orElseThrow();
    assertEquals(
        new QueuedPropagationStep(1, Outcome.PROPAGATED, step1.propagatedChanges()), step1);
    assertEquals(1, step1.propagatedChanges().size());

    // The change produced by task 1 comes before task 2 ...
    QueuedChange child = propagator.getQueuedChanges().get(0);
    assertEquals(Integer.valueOf(1), child.parentTaskId());
    assertEquals(1, child.rootTaskId());
    assertTrue(propagator.isPending(1));

    QueuedPropagationStep step2 = propagator.propagateNext().orElseThrow();
    assertEquals(child.taskId(), step2.taskId());
    assertSame(produced, step2.propagatedChanges().get(0).getOriginalChange());
    assertEquals(1, otherModelsSpec.runs);
    // ... and task 1 is complete once its child has been propagated.
    assertFalse(propagator.isPending(1));

    assertEquals(2, propagator.propagateNext().orElseThrow().taskId());
    assertTrue(propagator.propagateNext().isEmpty());
    verify(repository, times(1)).applyChange(first);
  }

  @Test
  void singleStepMode_doesNotQueueTransitiveChanges() {
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(change(otherModels));
    ChangePropagator propagator = propagator(ChangePropagationMode.SINGLE_STEP);
    propagator.enqueueChange(input(change(models)), 1);

    propagator.propagateNext();

    assertFalse(propagator.isPending(1));
    assertEquals(0, otherModelsSpec.runs);
  }

  @Test
  @SuppressWarnings("unchecked")
  void compositeInput_isSplitIntoOneItemPerTransactionalChange_keepingTheTaskIdForTheFirst() {
    TransactionalChange<EObject> part1 = change(models);
    TransactionalChange<EObject> part2 = change(models);
    CompositeChange<EObject, VitruviusChange<EObject>> composite = mock(CompositeChange.class);
    when(composite.containsConcreteChange()).thenReturn(true);
    when(composite.getChanges()).thenReturn(List.of(part1, part2));
    VitruviusChange<Uuid> inputChange = mock(VitruviusChange.class);
    when(repository.applyChange(inputChange)).thenReturn((VitruviusChange) composite);
    ChangePropagator propagator = propagator();
    propagator.enqueueChange(inputChange, 7);

    QueuedPropagationStep first = propagator.propagateNext().orElseThrow();

    assertEquals(7, first.taskId());
    assertSame(part1, first.propagatedChanges().get(0).getOriginalChange());
    QueuedChange second = propagator.getQueuedChanges().get(0);
    assertEquals(7, second.rootTaskId());
    assertSame(
        part2,
        propagator.propagateNext().orElseThrow().propagatedChanges().get(0).getOriginalChange());
    assertFalse(propagator.isPending(7));
  }

  // ---- user interactions -----------------------------------------------------------------------

  @Test
  void unansweredInteraction_defersTheChange_whileOtherChangesProceed() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagator propagator = propagator();
    VitruviusChange<Uuid> askingInput = input(askingChange);
    propagator.enqueueChange(askingInput, 1);
    propagator.enqueueChange(input(change(models)), 2);

    assertEquals(Outcome.DEFERRED, propagator.propagateNext().orElseThrow().outcome());

    QueuedChange deferred = propagator.getQueuedChange(1).orElseThrow();
    assertEquals(PropagationBlockReason.USER_INTERACTION, deferred.blockReason());
    PendingUserInteraction pending = deferred.pendingInteraction();
    assertEquals(PendingUserInteraction.Kind.CONFIRMATION, pending.kind());
    assertEquals(QUESTION, pending.message());
    // The other change is not held up ...
    QueuedPropagationStep other = propagator.propagateNext().orElseThrow();
    assertEquals(
        new QueuedPropagationStep(2, Outcome.PROPAGATED, other.propagatedChanges()), other);
    // ... and the deferred one stays skipped until answered.
    assertEquals(Outcome.SKIPPED_BLOCKED, propagator.propagateNext().orElseThrow().outcome());

    propagator.registerUserInteractions(1, confirmation(QUESTION, true));

    assertFalse(propagator.getQueuedChange(1).orElseThrow().blocked());
    QueuedPropagationStep retried = propagator.propagateNext().orElseThrow();
    assertEquals(Outcome.PROPAGATED, retried.outcome());
    assertEquals(List.of(Boolean.TRUE), modelsSpec.answers);
    // The input change was applied once, not again on the retry ...
    verify(repository, times(1)).applyChange(askingInput);
    // ... the answer is recorded on the propagated change like any user input ...
    verify(askingChange)
        .setUserInteractions(
            argThat(
                interactions ->
                    StreamSupport.stream(interactions.spliterator(), false).count() == 1));
    // ... and the VSUM's own provider was never asked.
    verify(blockingProvider, never())
        .getConfirmationInteractionResult(any(), any(), any(), any(), any(), any());
    assertFalse(propagator.isPending(1));
  }

  @Test
  void answersRegisteredWhileTheChangeRuns_makeItRetryInsteadOfWaiting() throws Exception {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagator propagator = propagator();
    propagator.enqueueChange(input(askingChange), 1);
    // Another thread answers while the change is being propagated, before it asks.
    modelsSpec.beforeAsking =
        spec -> {
          Thread answering =
              new Thread(
                  () -> propagator.registerUserInteractions(1, confirmation(QUESTION, false)));
          answering.start();
          try {
            answering.join();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        };

    assertEquals(Outcome.DEFERRED, propagator.propagateNext().orElseThrow().outcome());

    assertFalse(propagator.getQueuedChange(1).orElseThrow().blocked());
    modelsSpec.beforeAsking = spec -> {};
    assertEquals(Outcome.PROPAGATED, propagator.propagateNext().orElseThrow().outcome());
    assertEquals(List.of(Boolean.FALSE), modelsSpec.answers);
  }

  @Test
  void interactionAfterModifyingModels_failsInsteadOfRepeatingTheModifications() {
    ScriptedSpecification modifying = new ScriptedSpecification(models, otherModels);
    modifying.onFirstRun = spec -> recordedByCurrentRun.add(change(otherModels));
    TransactionalChange<EObject> askingChange = change(models);
    ScriptedSpecification asking = new ScriptedSpecification(models, otherModels);
    asking.askFor = askingChange;
    ChangePropagator propagator =
        new ChangePropagator(
            repository,
            new ChangePropagationSpecificationRepository(List.of(modifying, asking)),
            userInteractor);
    propagator.enqueueChange(input(askingChange), 1);

    assertThrows(NonDeferrablePropagationException.class, propagator::propagateNext);
    assertFalse(propagator.isPending(1));
  }

  // ---- transactions and other blocks
  // --------------------------------------------------------------

  @Test
  void blockingAChange_skipsIt_andTheChangesItProducesLater_untilUnblocked() {
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(change(otherModels));
    ChangePropagator propagator = propagator();
    VitruviusChange<Uuid> inputChange = input(change(models));
    propagator.enqueueChange(inputChange, 1);

    propagator.block(1, PropagationBlockReason.TRANSACTION);
    assertEquals(Outcome.SKIPPED_BLOCKED, propagator.propagateNext().orElseThrow().outcome());
    verify(repository, never()).applyChange(inputChange);

    propagator.unblock(1);
    assertEquals(Outcome.PROPAGATED, propagator.propagateNext().orElseThrow().outcome());

    // The child produced just now is part of the change, so blocking the change blocks it too.
    propagator.block(1, PropagationBlockReason.TRANSACTION);
    QueuedChange child = propagator.getQueuedChanges().get(0);
    assertEquals(PropagationBlockReason.TRANSACTION, child.blockReason());
    assertEquals(Outcome.SKIPPED_BLOCKED, propagator.propagateNext().orElseThrow().outcome());
    assertEquals(0, otherModelsSpec.runs);

    propagator.unblock(1);
    assertEquals(Outcome.PROPAGATED, propagator.propagateNext().orElseThrow().outcome());
    assertEquals(1, otherModelsSpec.runs);
  }

  @Test
  void blockAndAnswer_rejectUnknownTaskIds() {
    ChangePropagator propagator = propagator();

    assertThrows(
        IllegalArgumentException.class,
        () -> propagator.block(5, PropagationBlockReason.TRANSACTION));
    assertThrows(IllegalArgumentException.class, () -> propagator.unblock(5));
    assertThrows(IllegalArgumentException.class, () -> propagator.registerUserInteractions(5));
  }

  @Test
  void propagateUntilBlocked_stopsOnceOnlyBlockedChangesRemain() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagator propagator = propagator();
    propagator.enqueueChange(input(askingChange), 1);
    propagator.enqueueChange(input(change(models)), 2);
    propagator.enqueueChange(input(change(models)), 3);

    List<QueuedPropagationStep> steps = propagator.propagateUntilBlocked(List.of());

    assertEquals(
        List.of(Outcome.DEFERRED, Outcome.PROPAGATED, Outcome.PROPAGATED, Outcome.SKIPPED_BLOCKED),
        steps.stream().map(QueuedPropagationStep::outcome).toList());
    assertTrue(propagator.isPending(1));
    assertFalse(propagator.isPending(2));
  }

  @Test
  void failingPropagation_removesTheChange_andRethrows() {
    IllegalStateException failure = new IllegalStateException("reaction failed");
    modelsSpec.onFirstRun =
        spec -> {
          throw failure;
        };
    ChangePropagator propagator = propagator();
    propagator.enqueueChange(input(change(models)), 1);

    RuntimeException thrown = assertThrows(RuntimeException.class, propagator::propagateNext);

    assertTrue(causedBy(thrown, failure));
    assertFalse(propagator.isPending(1));
  }

  @Test
  void synchronousPropagation_stillAsksTheProviderDirectly() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    when(blockingProvider.getConfirmationInteractionResult(
            any(), any(), any(), any(), any(), any()))
        .thenReturn(true);
    ChangePropagator propagator = propagator();

    propagator.propagateChange(input(askingChange), List.of());

    assertEquals(List.of(Boolean.TRUE), modelsSpec.answers);
  }

  // ---- fixtures
  // ----------------------------------------------------------------------------------

  private ChangePropagator propagator() {
    return propagator(ChangePropagationMode.TRANSITIVE_CYCLIC);
  }

  private ChangePropagator propagator(ChangePropagationMode mode) {
    return new ChangePropagator(
        repository,
        new ChangePropagationSpecificationRepository(List.of(modelsSpec, otherModelsSpec)),
        userInteractor,
        mode);
  }

  @SuppressWarnings("unchecked")
  private VitruviusChange<Uuid> input(TransactionalChange<EObject> resolved) {
    VitruviusChange<Uuid> inputChange = mock(VitruviusChange.class);
    when(repository.applyChange(inputChange)).thenReturn(resolved);
    return inputChange;
  }

  @SuppressWarnings("unchecked")
  private TransactionalChange<EObject> change(MetamodelDescriptor descriptor) {
    TransactionalChange<EObject> change = mock(TransactionalChange.class);
    when(change.getAffectedEObjects()).thenReturn(Set.of(mock(EObject.class)));
    when(change.getAffectedEObjectsMetamodelDescriptors()).thenReturn(Set.of(descriptor));
    when(change.getAffectedEObjectsMetamodelDescriptor()).thenReturn(descriptor);
    when(change.getEChanges()).thenReturn(List.of(mock(EChange.class)));
    when(change.containsConcreteChange()).thenReturn(true);
    when(change.getUserInteractions()).thenReturn(List.of());
    return change;
  }

  private static UserInteractionBase confirmation(String message, boolean confirmed) {
    ConfirmationUserInteraction interaction =
        InteractionFactory.eINSTANCE.createConfirmationUserInteraction();
    interaction.setMessage(message);
    interaction.setConfirmed(confirmed);
    return interaction;
  }

  private static boolean causedBy(Throwable throwable, Throwable expected) {
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      if (t == expected) {
        return true;
      }
    }
    return false;
  }

  /** A specification whose behaviour each test scripts. */
  private static final class ScriptedSpecification extends AbstractChangePropagationSpecification {
    private int runs;
    private Consumer<ScriptedSpecification> onFirstRun = spec -> {};
    private Consumer<ScriptedSpecification> beforeAsking = spec -> {};

    /** The change for which this specification asks the user before doing anything. */
    private TransactionalChange<EObject> askFor;

    private final List<Boolean> answers = new ArrayList<>();

    ScriptedSpecification(MetamodelDescriptor source, MetamodelDescriptor target) {
      super(source, target);
    }

    @Override
    public boolean doesHandleChange(
        EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public void propagateChange(
        EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      if (askFor != null && askFor.getEChanges().contains(change)) {
        beforeAsking.accept(this);
        answers.add(
            getUserInteractor()
                .getConfirmationDialogBuilder()
                .message(QUESTION)
                .startInteraction());
      }
      if (runs++ == 0) {
        onFirstRun.accept(this);
      }
    }
  }
}
