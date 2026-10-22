package tools.vitruv.change.propagation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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
 * Tests {@link ChangePropagationQueue} with a mocked model repository and the real user interaction
 * machinery.
 */
class ChangePropagationQueueTest {
  private static final String QUESTION = "Delete the corresponding class?";
  private static final String OTHER_QUESTION = "Rename the corresponding class?";
  private static final String NOTICE = "Created the corresponding class.";

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
    EditableCorrespondenceModelView<Correspondence> correspondenceModel =
        mock(EditableCorrespondenceModelView.class);
    when(repository.getCorrespondenceModel()).thenReturn(correspondenceModel);
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
    ChangePropagationQueue queue = queue();

    assertEquals(42, queue.enqueueChange(input(change(models)), 42));

    VitruviusChange<Uuid> duplicate = input(change(models));
    assertThrows(IllegalArgumentException.class, () -> queue.enqueueChange(duplicate, 42));
    QueuedChange queued = queue.getQueuedChange(42).orElseThrow();
    assertEquals(42, queued.rootTaskId());
    assertNull(queued.parentTaskId());
    assertFalse(queued.blocked());
  }

  @Test
  void enqueue_generatesDistinctPositiveTaskIds() {
    ChangePropagationQueue queue = queue();
    Set<Integer> ids = new HashSet<>();

    for (int i = 0; i < 200; i++) {
      ids.add(queue.enqueueChange(input(change(models))));
    }

    assertEquals(200, ids.size());
    assertTrue(ids.stream().allMatch(id -> id > 0));
    assertEquals(200, queue.getQueuedChanges().size());
  }

  // ---- propagation order
  // -------------------------------------------------------------------------

  @Test
  void propagateNext_queuesTransitiveChangesAtTheFront_soOrderMatchesSynchronousPropagation() {
    TransactionalChange<EObject> produced = change(otherModels);
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(produced);
    ChangePropagationQueue queue = queue();
    VitruviusChange<Uuid> first = input(change(models));
    queue.enqueueChange(first, 1);
    queue.enqueueChange(input(change(models)), 2);

    QueuedPropagationStep step1 = queue.propagateNext().orElseThrow();
    assertEquals(
        new QueuedPropagationStep(1, Outcome.PROPAGATED, step1.propagatedChanges()), step1);
    assertEquals(1, step1.propagatedChanges().size());

    // The change produced by task 1 comes before task 2 ...
    QueuedChange child = queue.getQueuedChanges().get(0);
    assertEquals(Integer.valueOf(1), child.parentTaskId());
    assertEquals(1, child.rootTaskId());
    assertTrue(queue.isPending(1));

    QueuedPropagationStep step2 = queue.propagateNext().orElseThrow();
    assertEquals(child.taskId(), step2.taskId());
    assertSame(produced, step2.propagatedChanges().get(0).getOriginalChange());
    assertEquals(1, otherModelsSpec.runs);
    // ... and task 1 is complete once its child has been propagated.
    assertFalse(queue.isPending(1));

    assertEquals(2, queue.propagateNext().orElseThrow().taskId());
    assertTrue(queue.propagateNext().isEmpty());
    verify(repository, times(1)).applyChange(first);
  }

  @Test
  void singleStepMode_doesNotQueueTransitiveChanges() {
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(change(otherModels));
    ChangePropagationQueue queue = queue(ChangePropagationMode.SINGLE_STEP);
    queue.enqueueChange(input(change(models)), 1);

    queue.propagateNext();

    assertFalse(queue.isPending(1));
    assertEquals(0, otherModelsSpec.runs);
  }

  @Test
  void compositeInput_isSplitIntoOneItemPerTransactionalChange_keepingTheTaskIdForTheFirst() {
    TransactionalChange<EObject> part1 = change(models);
    TransactionalChange<EObject> part2 = change(models);
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(compositeInput(part1, part2), 7);

    QueuedPropagationStep first = queue.propagateNext().orElseThrow();

    assertEquals(7, first.taskId());
    assertSame(part1, first.propagatedChanges().get(0).getOriginalChange());
    QueuedChange second = queue.getQueuedChanges().get(0);
    assertEquals(7, second.rootTaskId());
    assertSame(
        part2,
        queue.propagateNext().orElseThrow().propagatedChanges().get(0).getOriginalChange());
    assertFalse(queue.isPending(7));
  }

  @Test
  void compositeInput_whoseFirstPartIsDeferred_hasItsLaterPartsPropagatedFirst() {
    // Documents the current behaviour.
    TransactionalChange<EObject> part1 = change(models);
    TransactionalChange<EObject> part2 = change(models);
    modelsSpec.askFor = part1;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(compositeInput(part1, part2), 7);

    assertEquals(Outcome.DEFERRED, queue.propagateNext().orElseThrow().outcome());
    QueuedPropagationStep second = queue.propagateNext().orElseThrow();
    assertSame(part2, second.propagatedChanges().get(0).getOriginalChange());

    queue.registerUserInteractions(7, confirmation(QUESTION, true));
    QueuedPropagationStep first = queue.propagateNext().orElseThrow();
    assertEquals(7, first.taskId());
    assertSame(part1, first.propagatedChanges().get(0).getOriginalChange());
    assertFalse(queue.isPending(7));
  }

  // ---- user interactions -----------------------------------------------------------------------

  @Test
  void unansweredInteraction_defersTheChange_whileOtherChangesProceed() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagationQueue queue = queue();
    VitruviusChange<Uuid> askingInput = input(askingChange);
    queue.enqueueChange(askingInput, 1);
    queue.enqueueChange(input(change(models)), 2);

    assertEquals(Outcome.DEFERRED, queue.propagateNext().orElseThrow().outcome());

    QueuedChange deferred = queue.getQueuedChange(1).orElseThrow();
    assertEquals(PropagationBlockReason.USER_INTERACTION, deferred.blockReason());
    PendingUserInteraction pending = deferred.pendingInteraction();
    assertEquals(PendingUserInteraction.Kind.CONFIRMATION, pending.kind());
    assertEquals(QUESTION, pending.message());
    // The other change is not held up ...
    QueuedPropagationStep other = queue.propagateNext().orElseThrow();
    assertEquals(
        new QueuedPropagationStep(2, Outcome.PROPAGATED, other.propagatedChanges()), other);
    // ... and the deferred one stays skipped until answered.
    assertEquals(Outcome.SKIPPED_BLOCKED, queue.propagateNext().orElseThrow().outcome());

    queue.registerUserInteractions(1, confirmation(QUESTION, true));

    assertFalse(queue.getQueuedChange(1).orElseThrow().blocked());
    QueuedPropagationStep retried = queue.propagateNext().orElseThrow();
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
    assertFalse(queue.isPending(1));
  }

  @Test
  void answersRegisteredWhileTheChangeRuns_makeItRetryInsteadOfWaiting() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(askingChange), 1);
    // Another thread answers while the change is being propagated, before it asks.
    modelsSpec.beforeAsking = spec -> answerFromAnotherThread(queue, 1, false);

    assertEquals(Outcome.DEFERRED, queue.propagateNext().orElseThrow().outcome());

    assertFalse(queue.getQueuedChange(1).orElseThrow().blocked());
    modelsSpec.beforeAsking = spec -> {};
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(List.of(Boolean.FALSE), modelsSpec.answers);
  }

  @Test
  void interactionAfterModifyingModels_failsInsteadOfRepeatingTheModifications() {
    ScriptedSpecification modifying = new ScriptedSpecification(models, otherModels);
    modifying.onFirstRun = spec -> recordedByCurrentRun.add(change(otherModels));
    TransactionalChange<EObject> askingChange = change(models);
    ScriptedSpecification asking = new ScriptedSpecification(models, otherModels);
    asking.askFor = askingChange;
    ChangePropagationQueue queue =
        new ChangePropagationQueue(
            new ChangePropagator(
                repository,
                new ChangePropagationSpecificationRepository(List.of(modifying, asking)),
                userInteractor));
    queue.enqueueChange(input(askingChange), 1);

    assertThrows(NonDeferrablePropagationException.class, queue::propagateNext);
    assertFalse(queue.isPending(1));
  }

  // ---- notifications ---------------------------------------------------------------------------

  @Test
  void notificationsOfAPropagatedChange_reachThePreviouslyInstalledProvider() {
    modelsSpec.notification = NOTICE;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(change(models)), 1);

    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());

    verify(blockingProvider, times(1))
        .getNotificationInteractionResult(any(), any(), eq(NOTICE), any(), any());
  }

  @Test
  void notificationsOfADeferredChange_areShownOnce_whenTheRetrySucceeds() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    modelsSpec.notification = NOTICE;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(askingChange), 1);

    assertEquals(Outcome.DEFERRED, queue.propagateNext().orElseThrow().outcome());
    // The retry starts over and notifies again, so the deferred attempt shows nothing.
    verify(blockingProvider, never())
        .getNotificationInteractionResult(any(), any(), eq(NOTICE), any(), any());

    queue.registerUserInteractions(1, confirmation(QUESTION, true));
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());

    verify(blockingProvider, times(1))
        .getNotificationInteractionResult(any(), any(), eq(NOTICE), any(), any());
  }

  @Test
  void notifications_doNotUseUpAnAnswerWithoutAMessage() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    modelsSpec.notification = NOTICE;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(askingChange), 1);
    assertEquals(Outcome.DEFERRED, queue.propagateNext().orElseThrow().outcome());

    // Such an answer matches every confirmation, but must be left for the question.
    queue.registerUserInteractions(1, confirmationWithoutMessage(true));

    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(List.of(Boolean.TRUE), modelsSpec.answers);
    verify(blockingProvider, times(1))
        .getNotificationInteractionResult(any(), any(), eq(NOTICE), any(), any());
  }

  @Test
  void notificationsOfAFailingChange_stillReachThePreviouslyInstalledProvider() {
    modelsSpec.notification = NOTICE;
    modelsSpec.onFirstRun =
        spec -> {
          throw new IllegalStateException("reaction failed");
        };
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(change(models)), 1);

    assertThrows(RuntimeException.class, queue::propagateNext);

    verify(blockingProvider, times(1))
        .getNotificationInteractionResult(any(), any(), eq(NOTICE), any(), any());
  }

  // ---- transactions and other blocks
  // --------------------------------------------------------------

  @Test
  void blockingAChange_skipsIt_andTheChangesItProducesLater_untilUnblocked() {
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(change(otherModels));
    ChangePropagationQueue queue = queue();
    VitruviusChange<Uuid> inputChange = input(change(models));
    queue.enqueueChange(inputChange, 1);

    queue.block(1, PropagationBlockReason.TRANSACTION);
    assertEquals(Outcome.SKIPPED_BLOCKED, queue.propagateNext().orElseThrow().outcome());
    verify(repository, never()).applyChange(inputChange);

    queue.unblock(1);
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());

    // The child produced just now is part of the change, so blocking the change blocks it too.
    queue.block(1, PropagationBlockReason.TRANSACTION);
    QueuedChange child = queue.getQueuedChanges().get(0);
    assertEquals(PropagationBlockReason.TRANSACTION, child.blockReason());
    assertEquals(Outcome.SKIPPED_BLOCKED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(0, otherModelsSpec.runs);

    queue.unblock(1);
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(1, otherModelsSpec.runs);
  }

  @Test
  void blockAndAnswer_rejectUnknownTaskIds() {
    ChangePropagationQueue queue = queue();

    assertThrows(
        IllegalArgumentException.class,
        () -> queue.block(5, PropagationBlockReason.TRANSACTION));
    assertThrows(IllegalArgumentException.class, () -> queue.unblock(5));
    assertThrows(IllegalArgumentException.class, () -> queue.registerUserInteractions(5));
  }

  @Test
  void blockAndUnblock_acceptTheItemBeingPropagated() {
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(change(otherModels));
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(change(models)), 1);
    queue.propagateNext();
    int child = queue.getQueuedChanges().get(0).taskId();
    List<QueuedChange> whileRunning = new ArrayList<>();
    otherModelsSpec.onFirstRun =
        spec ->
            inAnotherThread(
                () -> {
                  queue.block(child, PropagationBlockReason.TRANSACTION);
                  whileRunning.add(queue.getQueuedChange(child).orElseThrow());
                  queue.unblock(child);
                  whileRunning.add(queue.getQueuedChange(child).orElseThrow());
                  queue.block(child, PropagationBlockReason.TRANSACTION);
                });

    // A block left on the item has no effect once the item has been propagated.
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(PropagationBlockReason.TRANSACTION, whileRunning.get(0).blockReason());
    assertFalse(whileRunning.get(1).blocked());
    assertFalse(queue.isPending(1));
  }

  @Test
  void itemBlockedWhileRunning_staysBlockedAfterBeingDeferredAndAnswered_untilUnblocked() {
    ChangePropagationQueue queue = queue();
    int child = deferChildBlockedWhileRunning(queue);
    QueuedChange deferred = queue.getQueuedChange(child).orElseThrow();
    assertEquals(PropagationBlockReason.TRANSACTION, deferred.blockReason());
    assertEquals(QUESTION, deferred.pendingInteraction().message());

    queue.registerUserInteractions(1, confirmation(QUESTION, true));

    QueuedChange answered = queue.getQueuedChange(child).orElseThrow();
    assertEquals(PropagationBlockReason.TRANSACTION, answered.blockReason());
    assertNull(answered.pendingInteraction());
    assertEquals(Outcome.SKIPPED_BLOCKED, queue.propagateNext().orElseThrow().outcome());

    queue.unblock(child);
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(List.of(Boolean.TRUE), otherModelsSpec.answers);
    assertFalse(queue.isPending(1));
  }

  @Test
  void itemBlockedWhileRunning_keepsItsQuestion_untilAnAnswerMatchesIt() {
    ChangePropagationQueue queue = queue();
    int child = deferChildBlockedWhileRunning(queue);

    queue.registerUserInteractions(1, confirmation(OTHER_QUESTION, true));

    QueuedChange stillBlocked = queue.getQueuedChange(child).orElseThrow();
    assertEquals(PropagationBlockReason.TRANSACTION, stillBlocked.blockReason());
    assertEquals(QUESTION, stillBlocked.pendingInteraction().message());
    queue.unblock(child);
    QueuedChange unblocked = queue.getQueuedChange(child).orElseThrow();
    assertEquals(PropagationBlockReason.USER_INTERACTION, unblocked.blockReason());
    assertEquals(QUESTION, unblocked.pendingInteraction().message());

    queue.registerUserInteractions(1, confirmationWithoutMessage(true));
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(List.of(Boolean.TRUE), otherModelsSpec.answers);
  }

  @Test
  void deferredItemOfABlockedChange_keepsItsQuestion_untilAnAnswerMatchesIt() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(askingChange), 1);
    assertEquals(Outcome.DEFERRED, queue.propagateNext().orElseThrow().outcome());
    queue.block(1, PropagationBlockReason.TRANSACTION);

    queue.registerUserInteractions(1, confirmation(OTHER_QUESTION, true));
    assertEquals(QUESTION, queue.getQueuedChange(1).orElseThrow().pendingInteraction().message());

    queue.registerUserInteractions(1, confirmation(QUESTION, true));
    assertNull(queue.getQueuedChange(1).orElseThrow().pendingInteraction());
    assertEquals(Outcome.SKIPPED_BLOCKED, queue.propagateNext().orElseThrow().outcome());
    queue.unblock(1);
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
    assertEquals(List.of(Boolean.TRUE), modelsSpec.answers);
  }

  @Test
  void unblockingADeferredItemBeforeItIsAnswered_leavesItWaitingForTheAnswer() {
    ChangePropagationQueue queue = queue();
    int child = deferChildBlockedWhileRunning(queue);

    queue.unblock(child);

    QueuedChange unblocked = queue.getQueuedChange(child).orElseThrow();
    assertEquals(PropagationBlockReason.USER_INTERACTION, unblocked.blockReason());
    assertEquals(QUESTION, unblocked.pendingInteraction().message());
    assertEquals(Outcome.SKIPPED_BLOCKED, queue.propagateNext().orElseThrow().outcome());
    queue.registerUserInteractions(1, confirmation(QUESTION, true));
    assertEquals(Outcome.PROPAGATED, queue.propagateNext().orElseThrow().outcome());
  }

  @Test
  void propagateUntilBlocked_stopsOnceOnlyBlockedChangesRemain() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(askingChange), 1);
    queue.enqueueChange(input(change(models)), 2);
    queue.enqueueChange(input(change(models)), 3);

    List<QueuedPropagationStep> steps = queue.propagateUntilBlocked(List.of());

    assertEquals(
        List.of(Outcome.DEFERRED, Outcome.PROPAGATED, Outcome.PROPAGATED),
        steps.stream().map(QueuedPropagationStep::outcome).toList());
    assertTrue(queue.isPending(1));
    assertFalse(queue.isPending(2));
    assertTrue(queue.propagateUntilBlocked(List.of()).isEmpty());
  }

  @Test
  void propagateUntilBlocked_retriesAChangeThatWasAnsweredWhileItRan() {
    TransactionalChange<EObject> askingChange = change(models);
    modelsSpec.askFor = askingChange;
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(askingChange), 1);
    modelsSpec.beforeAsking =
        spec -> {
          spec.beforeAsking = again -> {};
          answerFromAnotherThread(queue, 1, true);
        };

    List<QueuedPropagationStep> steps = queue.propagateUntilBlocked(List.of());

    assertEquals(
        List.of(Outcome.DEFERRED, Outcome.PROPAGATED),
        steps.stream().map(QueuedPropagationStep::outcome).toList());
    assertEquals(List.of(Boolean.TRUE), modelsSpec.answers);
    assertFalse(queue.isPending(1));
  }

  @Test
  void failingPropagation_removesTheChange_andRethrows() {
    IllegalStateException failure = new IllegalStateException("reaction failed");
    modelsSpec.onFirstRun =
        spec -> {
          throw failure;
        };
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(change(models)), 1);

    RuntimeException thrown = assertThrows(RuntimeException.class, queue::propagateNext);

    assertTrue(causedBy(thrown, failure));
    assertFalse(queue.isPending(1));
  }

  @Test
  void failingItem_isRemovedAlone_whileTheRestOfItsTreeIsStillPropagated() {
    // Documents the current behaviour.
    TransactionalChange<EObject> failing = change(otherModels);
    TransactionalChange<EObject> sibling = change(otherModels);
    modelsSpec.onFirstRun =
        spec -> {
          recordedByCurrentRun.add(failing);
          recordedByCurrentRun.add(sibling);
        };
    otherModelsSpec.onFirstRun =
        spec -> {
          throw new IllegalStateException("reaction failed");
        };
    ChangePropagationQueue queue = queue();
    queue.enqueueChange(input(change(models)), 1);
    queue.propagateNext();

    assertThrows(RuntimeException.class, queue::propagateNext);

    assertTrue(queue.isPending(1));
    assertEquals(1, queue.getQueuedChanges().size());
    QueuedPropagationStep next = queue.propagateNext().orElseThrow();
    assertEquals(Outcome.PROPAGATED, next.outcome());
    assertSame(sibling, next.propagatedChanges().get(0).getOriginalChange());
    assertFalse(queue.isPending(1));
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

  private ChangePropagationQueue queue() {
    return queue(ChangePropagationMode.TRANSITIVE_CYCLIC);
  }

  private ChangePropagationQueue queue(ChangePropagationMode mode) {
    return new ChangePropagationQueue(propagator(mode));
  }

  @SuppressWarnings("unchecked")
  private VitruviusChange<Uuid> input(TransactionalChange<EObject> resolved) {
    VitruviusChange<Uuid> inputChange = mock(VitruviusChange.class);
    when(repository.applyChange(inputChange)).thenReturn(resolved);
    return inputChange;
  }

  /** Creates an input change that the repository resolves to a composite of the given parts. */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  private VitruviusChange<Uuid> compositeInput(TransactionalChange<EObject>... parts) {
    CompositeChange<EObject, VitruviusChange<EObject>> composite = mock(CompositeChange.class);
    when(composite.containsConcreteChange()).thenReturn(true);
    when(composite.getChanges()).thenReturn(List.of(parts));
    VitruviusChange<Uuid> inputChange = mock(VitruviusChange.class);
    when(repository.applyChange(inputChange)).thenReturn((VitruviusChange) composite);
    return inputChange;
  }

  /**
   * Enqueues a change as task 1 and propagates it, then propagates the child it produced, which is
   * blocked for a transaction while it runs and asks the user, so that it is deferred.
   *
   * @return the task id of the child
   */
  private int deferChildBlockedWhileRunning(ChangePropagationQueue queue) {
    TransactionalChange<EObject> askingChange = change(otherModels);
    modelsSpec.onFirstRun = spec -> recordedByCurrentRun.add(askingChange);
    otherModelsSpec.askFor = askingChange;
    queue.enqueueChange(input(change(models)), 1);
    queue.propagateNext();
    int child = queue.getQueuedChanges().get(0).taskId();
    otherModelsSpec.beforeAsking =
        spec -> {
          spec.beforeAsking = again -> {};
          inAnotherThread(() -> queue.block(child, PropagationBlockReason.TRANSACTION));
        };
    assertEquals(Outcome.DEFERRED, queue.propagateNext().orElseThrow().outcome());
    return child;
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

  /** Creates an answer that matches every confirmation, as its message is not set. */
  private static UserInteractionBase confirmationWithoutMessage(boolean confirmed) {
    ConfirmationUserInteraction interaction =
        InteractionFactory.eINSTANCE.createConfirmationUserInteraction();
    interaction.setConfirmed(confirmed);
    return interaction;
  }

  /** Registers an answer from another thread, as a client would while the change runs. */
  private static void answerFromAnotherThread(
      ChangePropagationQueue queue, int taskId, boolean confirmed) {
    inAnotherThread(
        () -> queue.registerUserInteractions(taskId, confirmation(QUESTION, confirmed)));
  }

  /**
   * Runs the action in another thread, as a client would while a change runs, waits for it and
   * rethrows what it threw.
   */
  private static void inAnotherThread(Runnable action) {
    AtomicReference<RuntimeException> failure = new AtomicReference<>();
    Thread client =
        new Thread(
            () -> {
              try {
                action.run();
              } catch (RuntimeException e) {
                failure.set(e);
              }
            });
    client.start();
    try {
      client.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    if (failure.get() != null) {
      throw failure.get();
    }
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

    /** What this specification notifies the user of whenever it runs, before asking. */
    private String notification;

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
      if (notification != null) {
        getUserInteractor().getNotificationDialogBuilder().message(notification).startInteraction();
      }
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
