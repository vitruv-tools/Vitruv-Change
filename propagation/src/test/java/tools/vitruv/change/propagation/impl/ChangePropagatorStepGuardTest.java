package tools.vitruv.change.propagation.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository;
import tools.vitruv.change.propagation.PropagationAbortedException;
import tools.vitruv.change.propagation.PropagationStepGuard;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * Tests the {@link PropagationStepGuard} hook of the {@link ChangePropagator}: call order, reverting
 * and retrying an aborted propagation, replaying user interactions.
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

  /** On a change of {@code singleValuedEAttribute} to n, sets the primitive attribute to 10 n. */
  private static final BiConsumer<Root, EChange<EObject>> COPY_TO_PRIMITIVE = (root, change) -> {
    if (change instanceof ReplaceSingleValuedEAttribute<EObject, ?> replace
        && replace.getAffectedFeature()
            == AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE
        && replace.getNewValue() instanceof Integer value) {
      root.setSingleValuedPrimitiveTypeEAttribute(10 * value);
    }
  };

  @Test
  void guardIsCalledAroundApplyAndEachStep() {
    var specification = new Specification(COPY_TO_PRIMITIVE);
    var propagator = createPropagator(specification);
    createRoot(propagator);
    List<String> calls = new ArrayList<>();
    propagator.setStepGuard(new PropagationStepGuard() {
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
          ChangePropagationSpecification spec) {
        calls.add("step");
      }

      @Override
      public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
        calls.add("end:" + committed);
      }
    });

    propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2));

    // user change, then the specification's change propagated again (transitive)
    assertEquals(List.of("begin", "apply", "step", "step", "end:true"), calls);
  }

  @Test
  void abortedPropagationIsRevertedAndRetried() {
    var specification = new Specification(COPY_TO_PRIMITIVE);
    var propagator = createPropagator(specification);
    createRoot(propagator);
    List<String> stateAtEnd = new ArrayList<>();
    propagator.setStepGuard(new PropagationStepGuard() {
      int attempt;
      int step;

      @Override
      public void beginPropagation(VitruviusChange<Uuid> change) {
        attempt++;
        step = 0;
      }

      @Override
      public void beforeStep(TransactionalChange<EObject> change,
          ChangePropagationSpecification spec) {
        // abort the first attempt before its second step: the user change and the first
        // consequential change are applied by then
        if (attempt == 1 && ++step == 2) {
          throw new PropagationAbortedException("deadlock victim");
        }
      }

      @Override
      public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
        stateAtEnd.add(committed + ":" + repositoryRoot().getSingleValuedEAttribute() + ","
            + repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
      }
    });

    propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2));

    assertEquals(List.of("false:1,10", "true:2,20"), stateAtEnd,
        "the aborted attempt must be reverted before it ends, the retry must complete");
    assertEquals(2, repositoryRoot().getSingleValuedEAttribute());
    assertEquals(20, repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
  }

  @Test
  void retryReplaysTheUserInteractionsOfTheAbortedAttempt() {
    List<Boolean> answers = new ArrayList<>();
    var specification = new Specification((root, change) -> {
      if (change instanceof ReplaceSingleValuedEAttribute<EObject, ?> replace
          && replace.getAffectedFeature()
              == AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE
          && Integer.valueOf(2).equals(replace.getNewValue())) {
        answers.add(specificationInteractor.get().getConfirmationDialogBuilder()
            .message("copy?").startInteraction());
        root.setSingleValuedPrimitiveTypeEAttribute(7);
      }
    });
    specificationInteractor.add(specification);
    var propagator = createPropagator(specification);
    createRoot(propagator);
    propagator.setStepGuard(abortFirstAttemptAtStep(2));

    propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2));

    assertEquals(1, confirmations, "the user must be asked only once");
    assertEquals(List.of(true, true), answers, "the retry gets the recorded answer");
    assertEquals(7, repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
  }

  @Test
  void abortIsPassedOnWhenAttemptsAreExhausted() {
    var specification = new Specification(COPY_TO_PRIMITIVE);
    var propagator = createPropagator(specification);
    createRoot(propagator);
    int[] ends = new int[1];
    propagator.setMaximumAttempts(3);
    propagator.setStepGuard(new PropagationStepGuard() {
      @Override
      public void beforeStep(TransactionalChange<EObject> change,
          ChangePropagationSpecification spec) {
        throw new PropagationAbortedException("always");
      }

      @Override
      public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
        ends[0]++;
      }
    });

    assertThrows(PropagationAbortedException.class,
        () -> propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2)));
    assertEquals(3, ends[0]);
    assertEquals(1, repositoryRoot().getSingleValuedEAttribute(), "the user change is reverted");
  }

  @Test
  void failedPropagationEndsTheAttemptWithoutRetry() {
    var specification = new Specification((root, change) -> {
      if (change instanceof ReplaceSingleValuedEAttribute<EObject, ?> replace
          && Integer.valueOf(2).equals(replace.getNewValue())) {
        throw new IllegalStateException("broken specification");
      }
    });
    var propagator = createPropagator(specification);
    createRoot(propagator);
    List<Boolean> ends = new ArrayList<>();
    propagator.setStepGuard(new PropagationStepGuard() {
      @Override
      public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
        ends.add(committed);
      }
    });

    var failure = assertThrows(RuntimeException.class,
        () -> propagateInView(propagator, () -> viewRoot.setSingleValuedEAttribute(2)));
    assertTrue(hasCause(failure, IllegalStateException.class));
    assertEquals(List.of(false), ends);
  }

  @Test
  void commitRunsAfterTheLastStepBeforeTheGuardEnds() {
    var specification = new Specification(COPY_TO_PRIMITIVE);
    var propagator = createPropagator(specification);
    createRoot(propagator);
    List<String> calls = new ArrayList<>();
    propagator.setStepGuard(recordingGuard(calls));

    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));
    propagator.propagateChange(change, List.of(), result -> calls.add(
        "commit:" + repositoryRoot().getSingleValuedPrimitiveTypeEAttribute() + "/"
            + !result.isEmpty()));

    assertEquals(List.of("begin", "step", "step", "commit:20/true", "end:true"), calls);
  }

  @Test
  void failedCommitIsRevertedAndNotRetried() {
    var specification = new Specification(COPY_TO_PRIMITIVE);
    var propagator = createPropagator(specification);
    createRoot(propagator);
    List<String> calls = new ArrayList<>();
    propagator.setStepGuard(recordingGuard(calls));

    var change = recordInView(() -> viewRoot.setSingleValuedEAttribute(2));
    var failure = assertThrows(IllegalStateException.class,
        () -> propagator.propagateChange(change, List.of(), result -> {
          // an abort here must not lead to a retry either
          throw new IllegalStateException("disk full", new PropagationAbortedException("x"));
        }));

    assertEquals("disk full", failure.getMessage());
    assertEquals(List.of("begin", "step", "step", "end:false"), calls);
    assertEquals(1, repositoryRoot().getSingleValuedEAttribute(), "the user change is reverted");
    assertEquals(10, repositoryRoot().getSingleValuedPrimitiveTypeEAttribute());
  }

  private static PropagationStepGuard recordingGuard(List<String> calls) {
    return new PropagationStepGuard() {
      @Override
      public void beginPropagation(VitruviusChange<Uuid> change) {
        calls.add("begin");
      }

      @Override
      public void beforeStep(TransactionalChange<EObject> change,
          ChangePropagationSpecification spec) {
        calls.add("step");
      }

      @Override
      public void endPropagation(VitruviusChange<Uuid> change, boolean committed) {
        calls.add("end:" + committed);
      }
    };
  }

  private VitruviusChange<Uuid> recordInView(Runnable modification) {
    viewRecorder.beginRecording();
    modification.run();
    return viewChangeResolver.assignIds(viewRecorder.endRecording());
  }

  private static boolean hasCause(Throwable throwable, Class<?> type) {
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      if (type.isInstance(t)) {
        return true;
      }
    }
    return false;
  }

  private static PropagationStepGuard abortFirstAttemptAtStep(int abortedStep) {
    return new PropagationStepGuard() {
      int attempt;
      int step;

      @Override
      public void beginPropagation(VitruviusChange<Uuid> change) {
        attempt++;
        step = 0;
      }

      @Override
      public void beforeStep(TransactionalChange<EObject> change,
          ChangePropagationSpecification spec) {
        if (attempt == 1 && ++step == abortedStep) {
          throw new PropagationAbortedException("deadlock victim");
        }
      }
    };
  }

  // ------------------------------------------------------------------ setup helpers

  /** Gives the specification of the interaction test access to its user interactor. */
  private final SpecificationInteractor specificationInteractor = new SpecificationInteractor();

  private static final class SpecificationInteractor {
    private Specification specification;

    void add(Specification specification) {
      this.specification = specification;
    }

    tools.vitruv.change.interaction.UserInteractor get() {
      return specification.interactor();
    }
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

  private void propagateInView(ChangePropagator propagator, Runnable modification) {
    viewRecorder.beginRecording();
    modification.run();
    var change = viewChangeResolver.assignIds(viewRecorder.endRecording());
    propagator.propagateChange(change, List.of());
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
    private final BiConsumer<Root, EChange<EObject>> reaction;

    Specification(BiConsumer<Root, EChange<EObject>> reaction) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.reaction = reaction;
    }

    tools.vitruv.change.interaction.UserInteractor interactor() {
      return getUserInteractor();
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public void propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      if (modelRepository.getModelResource(modelUri).getContents().isEmpty()) {
        return;
      }
      reaction.accept(repositoryRoot(), change);
    }
  }
}
