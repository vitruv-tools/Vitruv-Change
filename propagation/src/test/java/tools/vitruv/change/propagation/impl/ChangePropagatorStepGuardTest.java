package tools.vitruv.change.propagation.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.interaction.UserInteractor;
import tools.vitruv.change.propagation.ChangePropagationInvocation;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository;
import tools.vitruv.change.propagation.ChangePropagationStatus;
import tools.vitruv.change.propagation.PropagationAbortedException;
import tools.vitruv.change.propagation.PropagationReport;
import tools.vitruv.change.propagation.PropagationStepGuard;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * Tests the {@link PropagationStepGuard} hook of the {@link ChangePropagator}: call order,
 * reverting an aborted, blocked or failed propagation (including its correspondence changes),
 * resubmitting it with the user interactions of the halted run, committing inside the guard.
 */
@ExtendWith({TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class})
class ChangePropagatorStepGuardTest {
  private DefaultChangeRecordingModelRepository modelRepository;
  private ResourceSet viewResourceSet;
  private ChangeRecorder viewRecorder;
  private VitruviusChangeResolver<Uuid> viewChangeResolver;
  private URI modelUri;
  private Root viewRoot;
  private int confirmations;

  /** The correspondence model the specification of the running step got. */
  private EditableCorrespondenceModelView<Correspondence> currentCorrespondences;

  @BeforeAll
  static void registerResourceFactory() {
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
        .putIfAbsent("xmi", new XMIResourceFactoryImpl());
  }

  @BeforeEach
  void setUp(@TestProject Path testProjectPath) {
    modelRepository = new DefaultChangeRecordingModelRepository(null, testProjectPath);
    viewResourceSet = withGlobalFactories(new ResourceSetImpl());
    viewRecorder = new ChangeRecorder(viewResourceSet);
    viewRecorder.addToRecording(viewResourceSet);
    viewChangeResolver =
        VitruviusChangeResolverFactory.forUuids(UuidResolver.create(viewResourceSet));
    modelUri = URI.createFileURI(testProjectPath.resolve("model/root.xmi").toString());
  }

  @AfterEach
  void tearDown() throws Exception {
    viewRecorder.close();
    modelRepository.close();
  }

  /** What a test specification does with a change it receives. */
  @FunctionalInterface
  private interface Reaction {
    void react(Root root, EChange<EObject> change, UserInteractor userInteractor);
  }

  private static boolean setsSingleValuedAttribute(EChange<EObject> change, Integer value) {
    return change instanceof ReplaceSingleValuedEAttribute<EObject, ?> replace
        && replace.getAffectedFeature()
            == AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE
        && (value == null || value.equals(replace.getNewValue()));
  }

  /** On a change of {@code singleValuedEAttribute} to n, sets the primitive attribute to 10 n. */
  private static final Reaction COPY_TO_PRIMITIVE = (root, change, userInteractor) -> {
    if (setsSingleValuedAttribute(change, null)
        && ((ReplaceSingleValuedEAttribute<EObject, ?>) change).getNewValue()
            instanceof Integer value) {
      root.setSingleValuedPrimitiveTypeEAttribute(10 * value);
    }
  };

  @Test
  void guardIsCalledAroundApplyAndEachStep() {
    var propagator = createPropagator(new Specification(COPY_TO_PRIMITIVE));
    createRoot(propagator);
    List<String> calls = new ArrayList<>();
    propagator.setStepGuard(recordingGuard(calls));

    propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2));

    // user change, then the specification's change propagated again (transitive)
    assertEquals(List.of("begin", "apply", "step", "step", "end:true"), calls);
  }

  @Test
  void abortedPropagationIsRevertedAndReportedAsBlocked() {
    var propagator = createPropagator(new Specification(COPY_TO_PRIMITIVE));
    createRoot(propagator);
    // abort before the second step: the user change and the first consequential change are
    // applied by then
    var guard = new AbortingGuard(2);
    propagator.setStepGuard(guard);

    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));
    PropagationReport report = propagator.propagateChangeWithResult(change, List.of());

    assertEquals(ChangePropagationStatus.BLOCKED, report.status());
    assertNotNull(report.abort());
    assertEquals(List.of("false:1,10"), guard.stateAtEnd,
        "the propagation is reverted before the guard ends it, and not retried");
    assertEquals(1, repositoryRoot().getSingleValuedEAttribute());
    assertEquals(10, repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
  }

  @Test
  void abortBeforeApplyAppliesNothing() {
    var propagator = createPropagator(new Specification(COPY_TO_PRIMITIVE));
    createRoot(propagator);
    List<String> calls = new ArrayList<>();
    var abort = new PropagationAbortedException("locks not available");
    propagator.setStepGuard(new PropagationStepGuard() {
      @Override
      public void beforeApply(TransactionalChange<Uuid> change) {
        throw abort;
      }

      @Override
      public void beforeStep(TransactionalChange<EObject> change,
          ChangePropagationSpecification specification) {
        calls.add("step");
      }

      @Override
      public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
        calls.add("end:" + committed);
      }
    });

    var thrown = assertThrows(PropagationAbortedException.class,
        () -> propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2)));

    assertSame(abort, thrown);
    assertEquals(List.of("end:false"), calls);
    assertEquals(1, repositoryRoot().getSingleValuedEAttribute());
  }

  @Test
  void abortFromWithinASpecificationBlocksTheInvocation() {
    var propagator = createPropagator(new Specification((root, change, userInteractor) -> {
      if (setsSingleValuedAttribute(change, 2)) {
        root.setSingleValuedPrimitiveTypeEAttribute(7);
        throw new PropagationAbortedException("lock request of the specification aborted");
      }
    }));
    createRoot(propagator);
    var guard = new AbortingGuard(0);
    propagator.setStepGuard(guard);

    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));
    PropagationReport report = propagator.propagateChangeWithResult(change, List.of());

    assertEquals(ChangePropagationStatus.BLOCKED, report.result().status());
    assertEquals(List.of("false:1,0"), guard.stateAtEnd);
  }

  @Test
  void listApiThrowsTheAbortOfAGuardedPropagation() {
    var propagator = createPropagator(new Specification(COPY_TO_PRIMITIVE));
    createRoot(propagator);
    var guard = new AbortingGuard(1);
    propagator.setStepGuard(guard);

    assertThrows(PropagationAbortedException.class,
        () -> propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2)));
    assertEquals(List.of("false:1,10"), guard.stateAtEnd, "the user change is reverted");
  }

  @Test
  void resubmissionWithTheReportedInteractionsDoesNotAskAgain() {
    List<Boolean> answers = new ArrayList<>();
    var propagator = createPropagator(new Specification((root, change, userInteractor) -> {
      if (setsSingleValuedAttribute(change, 2)) {
        answers.add(userInteractor.getConfirmationDialogBuilder().message("copy?")
            .startInteraction());
        root.setSingleValuedPrimitiveTypeEAttribute(7);
      }
    }));
    createRoot(propagator);
    var guard = new AbortingGuard(2);
    propagator.setStepGuard(guard);
    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));

    PropagationReport blocked = propagator.propagateChangeWithResult(change, List.of());
    PropagationReport resubmitted = propagator.propagateChangeWithResult(change, List.of(),
        blocked.userInteractions(), propagatedChanges -> { });

    assertEquals(ChangePropagationStatus.BLOCKED, blocked.status());
    assertEquals(ChangePropagationStatus.SUCCEEDED, resubmitted.status());
    assertEquals(1, confirmations, "the user must be asked only once");
    assertEquals(List.of(true, true), answers, "the resubmission gets the recorded answer");
    assertEquals(7, repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
  }

  @Test
  void failedPropagationIsRevertedUnderAGuard() {
    var propagator = createPropagator(new Specification((root, change, userInteractor) -> {
      if (setsSingleValuedAttribute(change, 2)) {
        root.setSingleValuedPrimitiveTypeEAttribute(7);
        throw new IllegalStateException("broken specification");
      }
    }));
    createRoot(propagator);
    var guard = new AbortingGuard(0);
    propagator.setStepGuard(guard);

    assertThrows(IllegalStateException.class,
        () -> propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2)));
    assertEquals(List.of("false:1,0"), guard.stateAtEnd);
  }

  @Test
  void commitRunsAfterTheLastStepBeforeTheGuardEnds() {
    var propagator = createPropagator(new Specification(COPY_TO_PRIMITIVE));
    createRoot(propagator);
    List<String> calls = new ArrayList<>();
    propagator.setStepGuard(recordingGuard(calls));

    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));
    propagator.propagateChangeWithResult(change, List.of(), List.of(), result -> calls.add(
        "commit:" + repositoryRoot().getSingleValuedPrimitiveTypeEAttribute() + "/"
            + !result.isEmpty()));

    assertEquals(List.of("begin", "apply", "step", "step", "commit:20/true", "end:true"), calls);
  }

  @Test
  void failedCommitIsRevertedAndPassedOn() {
    var propagator = createPropagator(new Specification(COPY_TO_PRIMITIVE));
    createRoot(propagator);
    List<String> calls = new ArrayList<>();
    propagator.setStepGuard(recordingGuard(calls));

    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));
    var failure = assertThrows(IllegalStateException.class,
        () -> propagator.propagateChangeWithResult(change, List.of(), List.of(), result -> {
          throw new IllegalStateException("disk full");
        }));

    assertEquals("disk full", failure.getMessage());
    assertEquals(List.of("begin", "apply", "step", "step", "end:false"), calls);
    assertEquals(1, repositoryRoot().getSingleValuedEAttribute(), "the user change is reverted");
    assertEquals(10, repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
  }

  @Test
  void abortedPropagationRevertsItsCorrespondences() {
    int[] runs = new int[1];
    var propagator = createPropagator(new Specification((root, change, userInteractor) -> {
      if (setsSingleValuedAttribute(change, 2)) {
        currentCorrespondences.addCorrespondenceBetween(root, root, "run" + ++runs[0]);
        // a model change, so that there is a second step (where the first run is aborted)
        root.setSingleValuedPrimitiveTypeEAttribute(7);
      }
    }));
    createRoot(propagator);
    propagator.setStepGuard(new AbortingGuard(2));
    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));

    propagator.propagateChangeWithResult(change, List.of());
    propagator.propagateChangeWithResult(change, List.of());

    assertEquals(2, runs[0]);
    var correspondences = modelRepository.getCorrespondenceModel();
    assertEquals(Set.of(), correspondences.getCorrespondingEObjects(repositoryRoot(), "run1"),
        "the correspondence of the aborted run is reverted");
    assertEquals(Set.of(repositoryRoot()),
        correspondences.getCorrespondingEObjects(repositoryRoot(), "run2"));
  }

  // ------------------------------------------------------------------ guards

  private static PropagationStepGuard recordingGuard(List<String> calls) {
    return new PropagationStepGuard() {
      @Override
      public void beginPropagation(VitruviusChange<Uuid> change) {
        calls.add("begin");
      }

      @Override
      public void beforeApply(TransactionalChange<Uuid> change) {
        calls.add("apply");
      }

      @Override
      public void beforeStep(TransactionalChange<EObject> change,
          ChangePropagationSpecification specification) {
        calls.add("step");
      }

      @Override
      public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
        calls.add("end:" + committed);
      }
    };
  }

  /**
   * Aborts the first propagation before the given step (0: never) and records, at the end of each
   * propagation, whether it committed and the repository root's two attributes.
   */
  private class AbortingGuard implements PropagationStepGuard {
    private final int abortedStep;
    private final List<String> stateAtEnd = new ArrayList<>();
    private int propagations;
    private int step;

    AbortingGuard(int abortedStep) {
      this.abortedStep = abortedStep;
    }

    @Override
    public void beginPropagation(VitruviusChange<Uuid> change) {
      propagations++;
      step = 0;
    }

    @Override
    public void beforeStep(TransactionalChange<EObject> change,
        ChangePropagationSpecification specification) {
      if (propagations == 1 && ++step == abortedStep) {
        throw new PropagationAbortedException("deadlock victim");
      }
    }

    @Override
    public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
      stateAtEnd.add(committed + ":" + repositoryRoot().getSingleValuedEAttribute() + ","
          + repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
    }
  }

  // ------------------------------------------------------------------ setup helpers

  private VitruviusChange<Uuid> recordInView(Runnable modification) {
    viewRecorder.beginRecording();
    modification.run();
    return viewChangeResolver.assignIds(viewRecorder.endRecording());
  }

  private void propagateInView(ChangePropagator propagator, Runnable modification) {
    propagator.propagateChange(recordInView(modification), List.of());
  }

  private void createRoot(ChangePropagator propagator) {
    propagateInView(propagator, () -> {
      viewRoot = AllElementTypesFactory.eINSTANCE.createRoot();
      viewRoot.setId("root");
      viewRoot.setSingleValuedEAttribute(1);
      viewResourceSet.createResource(modelUri).getContents().add(viewRoot);
    });
  }

  private Root repositoryRoot() {
    return (Root) modelRepository.getModelResource(modelUri).getContents().get(0);
  }

  private ChangePropagator createPropagator(ChangePropagationSpecification specification) {
    return new ChangePropagator(modelRepository,
        new ChangePropagationSpecificationRepository(List.of(specification),
            Map.of(specification, 0)),
        UserInteractionFactory.instance.createUserInteractor(new ConfirmingProvider()));
  }

  /** Confirms every question and counts them; other interaction kinds are not expected. */
  private class ConfirmingProvider implements InteractionResultProvider {
    @Override
    public boolean getConfirmationInteractionResult(UserInteractionOptions.WindowModality modality,
        String title, String message, String positive, String negative, String cancel) {
      confirmations++;
      return true;
    }

    @Override
    public void getNotificationInteractionResult(UserInteractionOptions.WindowModality modality,
        String title, String message, String positive,
        UserInteractionOptions.NotificationType type) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getTextInputInteractionResult(UserInteractionOptions.WindowModality modality,
        String title, String message, String positive, String cancel,
        UserInteractionOptions.InputValidator validator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getMultipleChoiceSingleSelectionInteractionResult(
        UserInteractionOptions.WindowModality modality, String title, String message,
        String positive, String cancel, Iterable<String> choices) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
        UserInteractionOptions.WindowModality modality, String title, String message,
        String positive, String cancel, Iterable<String> choices) {
      throw new UnsupportedOperationException();
    }
  }

  /** Receives changes one by one and applies the given reaction to the repository's root. */
  private class Specification extends AbstractChangePropagationSpecification {
    private final Reaction reaction;

    Specification(Reaction reaction) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.reaction = reaction;
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
      if (!modelRepository.getModelResource(modelUri).getContents().isEmpty()) {
        currentCorrespondences = correspondenceModel;
        reaction.react(repositoryRoot(), change, getUserInteractor());
      }
      return ChangePropagationInvocation.succeeded(this, List.of(change), List.of());
    }
  }
}
