package tools.vitruv.change.propagation.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
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
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.AnnotationSource;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.impl.PredefinedInteractionResultProviderImpl;
import tools.vitruv.change.propagation.ChangePropagationInvocation;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository;
import tools.vitruv.change.propagation.ChangePropagationStatus;
import tools.vitruv.change.propagation.ModelRepositorySnapshot;
import tools.vitruv.change.propagation.PropagationReport;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * Tests the propagation levels of {@link ChangePropagator} and the state before a change that it
 * passes to change propagation specifications.
 */
@ExtendWith({TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class})
class ChangePropagatorTest {
  private DefaultChangeRecordingModelRepository modelRepository;
  private ResourceSet viewResourceSet;
  private ChangeRecorder viewRecorder;
  private VitruviusChangeResolver<Uuid> viewChangeResolver;
  private URI modelUri;
  private final List<String> invocations = new ArrayList<>();
  private int createdSnapshots;

  @BeforeAll
  static void registerResourceFactory() {
    Resource.Factory.Registry.INSTANCE
        .getExtensionToFactoryMap()
        .putIfAbsent("xmi", new XMIResourceFactoryImpl());
  }

  @BeforeEach
  void setUp(@TestProject Path testProjectPath) {
    modelRepository = new DefaultChangeRecordingModelRepository(null, testProjectPath) {
      @Override
      public ModelRepositorySnapshot createSnapshot() {
        createdSnapshots++;
        return super.createSnapshot();
      }
    };
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

  @Test
  void higherLevelIsPropagatedAfterLowerLevelAndSeesItsChanges() {
    var lowerLevel = new RecordingSpecification("lower", (changes, previousState) -> {
      if (changes.stream().anyMatch(InsertRootEObject.class::isInstance)) {
        repositoryRoot().setSingleValuedEAttribute(42);
      }
    });
    var higherLevel = new RecordingSpecification("higher", (changes, previousState) -> { });
    var propagator = createPropagator(Map.of(lowerLevel, 0, higherLevel, 1));

    propagateInView(propagator, this::createRootInView);

    assertTrue(invocations.indexOf("lower") < invocations.indexOf("higher"));
    assertTrue(higherLevel.receivedChanges.stream()
        .filter(ReplaceSingleValuedEAttribute.class::isInstance)
        .anyMatch(change -> Integer.valueOf(42).equals(
            ((ReplaceSingleValuedEAttribute<?, ?>) change).getNewValue())),
        "the higher level must receive the changes made on the lower level");
  }

  @Test
  void specificationReceivesStateBeforeTheChange() {
    List<Integer> previousValues = new ArrayList<>();
    var specification = new RecordingSpecification("spec", (changes, previousState) -> {
      Root previousRoot =
          (Root) previousState.getRepositoryToSnapshotMap().get(repositoryRoot());
      if (previousRoot != null) {
        previousValues.add(previousRoot.getSingleValuedEAttribute());
      }
    });
    var propagator = createPropagator(Map.of(specification, 0));
    List<Root> viewRoot = new ArrayList<>();
    propagateInView(propagator, () -> viewRoot.add(createRootInView()));

    propagateInView(propagator, () -> viewRoot.get(0).setSingleValuedEAttribute(2));

    assertEquals(List.of(1), previousValues);
    assertEquals(2, repositoryRoot().getSingleValuedEAttribute());
  }

  @Test
  void noSnapshotIsCreatedIfNoSpecificationUsesTheStateBeforeTheChange() {
    var specification = new SingleChangeSpecification(change -> {
      if (change instanceof InsertRootEObject) {
        repositoryRoot().setSingleValuedEAttribute(42);
      }
    });
    var propagator = createPropagator(Map.of(specification, 0));
    List<Root> viewRoot = new ArrayList<>();
    propagateInView(propagator, () -> viewRoot.add(createRootInView()));

    propagateInView(propagator, () -> viewRoot.get(0).setSingleValuedEAttribute(2));

    assertFalse(specification.usesPreviousState());
    assertEquals(0, createdSnapshots);
    assertTrue(specification.receivedChanges.stream()
        .anyMatch(ReplaceSingleValuedEAttribute.class::isInstance),
        "the specification must still receive the changes");
    assertEquals(2, repositoryRoot().getSingleValuedEAttribute());
  }

  @Test
  void snapshotsAreCreatedIfOneSpecificationUsesTheStateBeforeTheChange() {
    List<ModelRepositorySnapshot> receivedStates = new ArrayList<>();
    var usingSpecification = new RecordingSpecification("using",
        (changes, previousState) -> receivedStates.add(previousState));
    var otherSpecification = new SingleChangeSpecification(change -> {});
    var propagator = createPropagator(Map.of(usingSpecification, 0, otherSpecification, 0));

    propagateInView(propagator, this::createRootInView);

    assertTrue(usingSpecification.usesPreviousState());
    assertTrue(createdSnapshots > 0);
    assertFalse(receivedStates.isEmpty());
    assertTrue(receivedStates.stream().allMatch(Objects::nonNull));
  }

  @Test
  void specificationCanDeclareThatItDoesNotUseTheStateBeforeTheChange() {
    List<ModelRepositorySnapshot> receivedStates = new ArrayList<>();
    var specification = new RecordingSpecification("spec",
        (changes, previousState) -> receivedStates.add(previousState)) {
      @Override
      public boolean usesPreviousState() {
        return false;
      }
    };
    var propagator = createPropagator(Map.of(specification, 0));

    propagateInView(propagator, this::createRootInView);

    assertEquals(0, createdSnapshots);
    assertFalse(receivedStates.isEmpty());
    assertTrue(receivedStates.stream().allMatch(Objects::isNull));
  }

  @Test
  void specificationReceivesAnnotationsOfTheChange() {
    var specification = new RecordingSpecification("spec", (changes, previousState) -> { });
    var propagator = createPropagator(Map.of(specification, 0));
    var author = new Author("alice");

    propagateInView(propagator, author, this::createRootInView);

    assertEquals(List.of(Optional.of(author)), specification.receivedAuthors);
  }

  @Test
  void specificationReceivesEmptyAnnotationsForChangeWithoutAnnotations() {
    var specification = new RecordingSpecification("spec", (changes, previousState) -> { });
    var propagator = createPropagator(Map.of(specification, 0));

    propagateInView(propagator, null, this::createRootInView);

    assertEquals(List.of(Optional.empty()), specification.receivedAuthors);
  }

  @Test
  void transitivelyPropagatedChangesKeepTheAnnotations() {
    var specification = new RecordingSpecification("spec", (changes, previousState) -> {
      if (changes.stream().anyMatch(InsertRootEObject.class::isInstance)) {
        repositoryRoot().setSingleValuedEAttribute(42);
      }
    });
    var propagator = createPropagator(Map.of(specification, 0));
    var author = new Author("alice");

    propagateInView(propagator, author, this::createRootInView);

    assertEquals(List.of(Optional.of(author), Optional.of(author)),
        specification.receivedAuthors,
        "the change made by the specification must be propagated with the same annotations");
  }

  @Test
  void higherLevelReceivesTheAnnotations() {
    var lowerLevel = new RecordingSpecification("lower", (changes, previousState) -> { });
    var higherLevel = new RecordingSpecification("higher", (changes, previousState) -> { });
    var propagator = createPropagator(Map.of(lowerLevel, 0, higherLevel, 1));
    var author = new Author("alice");

    propagateInView(propagator, author, this::createRootInView);

    assertEquals(List.of(Optional.of(author)), lowerLevel.receivedAuthors);
    assertEquals(List.of(Optional.of(author)), higherLevel.receivedAuthors);
  }

  @Test
  void transactionalChangesReceiveTheAnnotationsOfTheirCompositeChange() {
    var specification = new RecordingSpecification("spec", (changes, previousState) -> { });
    var propagator = createPropagator(Map.of(specification, 0));
    var author = new Author("alice");
    viewRecorder.beginRecording();
    createRootInView();
    var transactionalChange = viewChangeResolver.assignIds(viewRecorder.endRecording());
    var compositeChange = VitruviusChangeFactory.getInstance()
        .createCompositeChange(List.of(transactionalChange));
    compositeChange.setAnnotation(Author.class, author);

    propagator.propagateChange(compositeChange, List.of());

    assertEquals(List.of(Optional.of(author)), specification.receivedAuthors);
  }

  @Test
  void laterSpecificationOfTheSameLevelIsNotCalledAfterBlocked() {
    var blocked = new StatusSpecification("blocked", ChangePropagationStatus.BLOCKED);
    var later = new StatusSpecification("later", ChangePropagationStatus.SUCCEEDED);
    var propagator = createPropagator(orderedLevels(blocked, 0, later, 0));

    var report = assertDoesNotThrow(
        () -> propagateInViewWithResult(propagator, this::createRootInView));

    assertEquals(List.of("blocked"), invocations);
    assertEquals(ChangePropagationStatus.BLOCKED, report.result().status());
    assertEquals(1, report.result().invocations().size());
    assertSame(blocked, report.result().invocations().get(0).specification());
  }

  @Test
  void laterSpecificationOfTheSameLevelIsNotCalledAfterFailed() {
    var failed = new StatusSpecification("failed", ChangePropagationStatus.FAILED);
    var later = new StatusSpecification("later", ChangePropagationStatus.SUCCEEDED);
    var propagator = createPropagator(orderedLevels(failed, 0, later, 0));

    var report = assertDoesNotThrow(
        () -> propagateInViewWithResult(propagator, this::createRootInView));

    assertEquals(List.of("failed"), invocations);
    assertEquals(ChangePropagationStatus.FAILED, report.result().status());
    assertEquals("failed failed", report.result().cause().orElseThrow().getMessage());
  }

  @Test
  void higherLevelIsNotPropagatedAfterAHaltOnALowerLevel() {
    var blocked = new StatusSpecification("lower", ChangePropagationStatus.BLOCKED);
    var higher = new StatusSpecification("higher", ChangePropagationStatus.SUCCEEDED);
    var propagator = createPropagator(orderedLevels(blocked, 0, higher, 1));

    propagateInViewWithResult(propagator, this::createRootInView);

    assertEquals(List.of("lower"), invocations);
  }

  @Test
  void transitiveChangesAreNotPropagatedAfterAHalt() {
    var succeeding = new StatusSpecification("spec", ChangePropagationStatus.SUCCEEDED,
        this::setRepositoryRootAttributeOnInsert);
    createPropagatorAndPropagate(succeeding);
    assertEquals(List.of("spec", "spec"), invocations,
        "premise: the change made by the specification is propagated transitively");

    invocations.clear();
    var blocked = new StatusSpecification("spec", ChangePropagationStatus.BLOCKED,
        this::setRepositoryRootAttributeOnInsert);
    var report = createPropagatorAndPropagate(blocked);

    assertEquals(List.of("spec"), invocations);
    assertEquals(ChangePropagationStatus.BLOCKED, report.result().status());
  }

  @Test
  void executedChangesStayAppliedAfterAHalt() {
    var blocked = new StatusSpecification("spec", ChangePropagationStatus.BLOCKED,
        this::setRepositoryRootAttributeOnInsert);

    var report = createPropagatorAndPropagate(blocked);

    assertEquals(ChangePropagationStatus.BLOCKED, report.result().status());
    assertEquals(42, repositoryRoot().getSingleValuedEAttribute(),
        "the modification made before the specification was blocked must not be undone");
  }

  @Test
  void exceptionThrownByASpecificationIsRecordedAsFailedInvocation() {
    var boom = new IllegalStateException("boom");
    var throwing = new ThrowingSpecification("throwing", boom);
    var later = new StatusSpecification("later", ChangePropagationStatus.SUCCEEDED);
    var propagator = createPropagator(orderedLevels(throwing, 0, later, 0));

    var report = assertDoesNotThrow(
        () -> propagateInViewWithResult(propagator, this::createRootInView));

    assertEquals(ChangePropagationStatus.FAILED, report.result().status());
    assertSame(boom, report.result().cause().orElseThrow());
    var invocation = report.result().invocations().get(0);
    assertSame(throwing, invocation.specification());
    assertFalse(invocation.receivedChanges().isEmpty());
    assertTrue(invocation.executedChanges().isEmpty());
    assertEquals(List.of("throwing"), invocations);
  }

  @Test
  void propagateChangeRethrowsTheCauseOfAFailedResult() {
    var boom = new IllegalStateException("boom");
    var propagator = createPropagator(Map.of(new ThrowingSpecification("throwing", boom), 0));

    var thrown = assertThrows(IllegalStateException.class,
        () -> propagateInView(propagator, this::createRootInView));

    assertSame(boom, thrown);
  }

  @Test
  void propagateChangeDoesNotThrowForBlockedAndReturnsThePropagatedChanges() {
    var blocked = new StatusSpecification("blocked", ChangePropagationStatus.BLOCKED);
    var propagator = createPropagator(Map.of(blocked, 0));
    var change = recordViewChange(this::createRootInView);

    var propagatedChanges = assertDoesNotThrow(() -> propagator.propagateChange(change, List.of()));

    assertFalse(propagatedChanges.isEmpty());
  }

  @Test
  void reportIsCompleteWithLoggingDisabled() {
    var previousLevel = LogManager.getRootLogger().getLevel();
    Configurator.setRootLevel(Level.OFF);
    try {
      var blocked = new StatusSpecification("spec", ChangePropagationStatus.BLOCKED);

      var report = createPropagatorAndPropagate(blocked);

      assertEquals(ChangePropagationStatus.BLOCKED, report.result().status());
      var invocation = report.result().invocations().get(0);
      assertSame(blocked, invocation.specification());
      assertEquals(1, invocation.executedChanges().size());
      assertFalse(report.propagatedChanges().isEmpty());
    } finally {
      Configurator.setRootLevel(previousLevel);
    }
  }

  @Test
  void succeededRunChecksForCreatedObjectsWithoutResource() {
    var lostObject = new LostObjectSpecification("lost", ChangePropagationStatus.SUCCEEDED);
    var propagator = createPropagator(Map.of(lostObject, 0));

    var thrown = assertThrows(RuntimeException.class,
        () -> propagateInViewWithResult(propagator, this::createRootInView));

    assertTrue(messageChain(thrown).contains("not in any resource"),
        "premise: the check for objects without a resource must reject this state: "
            + messageChain(thrown));
  }

  @Test
  void haltedRunDoesNotCheckForCreatedObjectsWithoutResource() {
    var blocked = new LostObjectSpecification("lost", ChangePropagationStatus.BLOCKED);
    var propagator = createPropagator(Map.of(blocked, 0));

    var report = assertDoesNotThrow(
        () -> propagateInViewWithResult(propagator, this::createRootInView));

    assertEquals(ChangePropagationStatus.BLOCKED, report.result().status());
  }

  @Test
  void reportListsOneInvocationPerSpecificationCallWithItsExecutedChanges() {
    var first = new ExecutingSpecification("first", 2);
    var second = new ExecutingSpecification("second", 1);
    var propagator = createPropagator(orderedLevels(first, 0, second, 0));

    var report = propagateInViewWithResult(propagator, this::createRootInView);

    var reported = report.result().invocations();
    assertEquals(2, reported.size());
    assertSame(first, reported.get(0).specification());
    assertSame(second, reported.get(1).specification());
    assertEquals(ChangePropagationStatus.SUCCEEDED, report.result().status());
    assertEquals(first.receivedChanges, reported.get(0).receivedChanges());
    assertEquals(second.receivedChanges, reported.get(1).receivedChanges());
    assertEquals(first.executedChanges, reported.get(0).executedChanges());
    assertEquals(second.executedChanges, reported.get(1).executedChanges());
    List<EChange<EObject>> allExecuted = new ArrayList<>(first.executedChanges);
    allExecuted.addAll(second.executedChanges);
    assertEquals(allExecuted, report.result().executedChanges(),
        "the executed changes of the run are those of the invocations, in execution order");
  }

  @Test
  void eachExecutedChangeBelongsToExactlyOneInvocation() {
    var first = new ExecutingSpecification("first", 2);
    var second = new ExecutingSpecification("second", 3);
    var propagator = createPropagator(orderedLevels(first, 0, second, 0));

    var report = propagateInViewWithResult(propagator, this::createRootInView);

    assertEquals(5, report.result().executedChanges().size());
    for (EChange<EObject> executed : report.result().executedChanges()) {
      long invocationsWithTheChange = report.result().invocations().stream()
          .filter(invocation -> invocation.executedChanges().stream()
              .anyMatch(change -> change == executed))
          .count();
      assertEquals(1, invocationsWithTheChange);
    }
  }

  @Test
  void invocationsOfLowerLevelsComeBeforeInvocationsOfHigherLevels() {
    var higher = new ExecutingSpecification("higher", 1);
    var lower = new ExecutingSpecification("lower", 1);
    var propagator = createPropagator(orderedLevels(higher, 1, lower, 0));

    var report = propagateInViewWithResult(propagator, this::createRootInView);

    assertEquals(List.of(lower, higher),
        report.result().invocations().stream()
            .map(invocation -> invocation.specification()).toList());
    assertEquals(List.of("lower", "higher"), invocations);
  }

  private record Author(String name) {}

  private Root createRootInView() {
    Root root = AllElementTypesFactory.eINSTANCE.createRoot();
    root.setId("root");
    root.setSingleValuedEAttribute(1);
    viewResourceSet.createResource(modelUri).getContents().add(root);
    return root;
  }

  private Root repositoryRoot() {
    return (Root) modelRepository.getModelResource(modelUri).getContents().get(0);
  }

  private void propagateInView(ChangePropagator propagator, Runnable modification) {
    propagateInView(propagator, null, modification);
  }

  private void propagateInView(ChangePropagator propagator, Author author,
      Runnable modification) {
    viewRecorder.beginRecording();
    modification.run();
    var change = viewChangeResolver.assignIds(viewRecorder.endRecording());
    if (author != null) {
      change.setAnnotation(Author.class, author);
    }
    propagator.propagateChange(change, List.of());
  }

  private void setRepositoryRootAttributeOnInsert(List<EChange<EObject>> changes) {
    if (changes.stream().anyMatch(InsertRootEObject.class::isInstance)) {
      repositoryRoot().setSingleValuedEAttribute(42);
    }
  }

  private PropagationReport createPropagatorAndPropagate(ChangePropagationSpecification spec) {
    return propagateInViewWithResult(createPropagator(Map.of(spec, 0)), this::createRootInView);
  }

  private static Map<ChangePropagationSpecification, Integer> orderedLevels(
      ChangePropagationSpecification first, int firstLevel,
      ChangePropagationSpecification second, int secondLevel) {
    Map<ChangePropagationSpecification, Integer> levels = new LinkedHashMap<>();
    levels.put(first, firstLevel);
    levels.put(second, secondLevel);
    return levels;
  }

  private static String messageChain(Throwable throwable) {
    StringBuilder messages = new StringBuilder();
    for (Throwable current = throwable; current != null; current = current.getCause()) {
      messages.append(current.getMessage()).append(" | ");
    }
    return messages.toString();
  }

  private VitruviusChange<Uuid> recordViewChange(Runnable modification) {
    viewRecorder.beginRecording();
    modification.run();
    return viewChangeResolver.assignIds(viewRecorder.endRecording());
  }

  private PropagationReport propagateInViewWithResult(
      ChangePropagator propagator, Runnable modification) {
    return propagator.propagateChangeWithResult(recordViewChange(modification), List.of());
  }

  private ChangePropagator createPropagator(Map<ChangePropagationSpecification, Integer> levels) {
    return new ChangePropagator(modelRepository,
        new ChangePropagationSpecificationRepository(levels.keySet(), levels),
        UserInteractionFactory.instance.createUserInteractor(
            new PredefinedInteractionResultProviderImpl(null)));
  }

  /** Records its invocations and the changes it receives, then applies the given reaction. */
  private class RecordingSpecification extends AbstractChangePropagationSpecification {
    private final String name;
    private final BiConsumer<List<EChange<EObject>>, ModelRepositorySnapshot> reaction;
    private final List<EChange<EObject>> receivedChanges = new ArrayList<>();
    private final List<Optional<Author>> receivedAuthors = new ArrayList<>();

    RecordingSpecification(String name,
        BiConsumer<List<EChange<EObject>>, ModelRepositorySnapshot> reaction) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.name = name;
      this.reaction = reaction;
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public ChangePropagationInvocation propagateChanges(List<EChange<EObject>> changes,
        AnnotationSource changeAnnotations,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess, ModelRepositorySnapshot previousState) {
      invocations.add(name);
      receivedChanges.addAll(changes);
      receivedAuthors.add(changeAnnotations.getAnnotation(Author.class));
      reaction.accept(changes, previousState);
      return ChangePropagationInvocation.succeeded(this, changes, List.of());
    }

    @Override
    public ChangePropagationInvocation propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      throw new UnsupportedOperationException("changes are propagated as a whole");
    }
  }

  /**
   * Receives the changes one by one, like reactions and commonalities, and thus does not use the
   * state before a change.
   */
  private static class SingleChangeSpecification extends AbstractChangePropagationSpecification {
    private final Consumer<EChange<EObject>> reaction;
    private final List<EChange<EObject>> receivedChanges = new ArrayList<>();

    SingleChangeSpecification(Consumer<EChange<EObject>> reaction) {
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
      receivedChanges.add(change);
      reaction.accept(change);
      return ChangePropagationInvocation.succeeded(this, List.of(change), List.of());
    }
  }

  /**
   * Records its call, runs an action, and returns an invocation with the given status that lists
   * the first received change as executed.
   */
  private class StatusSpecification extends AbstractChangePropagationSpecification {
    private final String name;
    private final ChangePropagationStatus status;
    private final Consumer<List<EChange<EObject>>> action;

    StatusSpecification(String name, ChangePropagationStatus status) {
      this(name, status, changes -> { });
    }

    StatusSpecification(String name, ChangePropagationStatus status,
        Consumer<List<EChange<EObject>>> action) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.name = name;
      this.status = status;
      this.action = action;
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public ChangePropagationInvocation propagateChanges(List<EChange<EObject>> changes,
        AnnotationSource changeAnnotations,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess, ModelRepositorySnapshot previousState) {
      invocations.add(name);
      action.accept(changes);
      List<EChange<EObject>> executed = List.of(changes.get(0));
      return switch (status) {
        case SUCCEEDED -> ChangePropagationInvocation.succeeded(this, changes, executed);
        case BLOCKED -> ChangePropagationInvocation.blocked(this, changes, executed);
        case FAILED -> ChangePropagationInvocation.failed(
            this, changes, executed, new IllegalStateException(name + " failed"));
      };
    }

    @Override
    public ChangePropagationInvocation propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      throw new UnsupportedOperationException("changes are propagated as a whole");
    }
  }

  /** Records its call and throws the given exception. */
  private class ThrowingSpecification extends AbstractChangePropagationSpecification {
    private final String name;
    private final RuntimeException exception;

    ThrowingSpecification(String name, RuntimeException exception) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.name = name;
      this.exception = exception;
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public ChangePropagationInvocation propagateChanges(List<EChange<EObject>> changes,
        AnnotationSource changeAnnotations,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess, ModelRepositorySnapshot previousState) {
      invocations.add(name);
      throw exception;
    }

    @Override
    public ChangePropagationInvocation propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      throw new UnsupportedOperationException("changes are propagated as a whole");
    }
  }

  /**
   * Creates an object that is not in any resource, reports it as created, and gives it a
   * correspondence, then returns an invocation with the given status.
   */
  private class LostObjectSpecification extends AbstractChangePropagationSpecification {
    private final String name;
    private final ChangePropagationStatus status;

    LostObjectSpecification(String name, ChangePropagationStatus status) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.name = name;
      this.status = status;
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public ChangePropagationInvocation propagateChanges(List<EChange<EObject>> changes,
        AnnotationSource changeAnnotations,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess, ModelRepositorySnapshot previousState) {
      invocations.add(name);
      Root lostObject = AllElementTypesFactory.eINSTANCE.createRoot();
      notifyObjectCreated(lostObject);
      correspondenceModel.addCorrespondenceBetween(lostObject, repositoryRoot(), "");
      return status == ChangePropagationStatus.BLOCKED
          ? ChangePropagationInvocation.blocked(this, changes, List.of())
          : ChangePropagationInvocation.succeeded(this, changes, List.of());
    }

    @Override
    public ChangePropagationInvocation propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      throw new UnsupportedOperationException("changes are propagated as a whole");
    }
  }

  /**
   * Records its call and the received changes, and returns a succeeded invocation that executed
   * the given number of new changes.
   */
  private class ExecutingSpecification extends AbstractChangePropagationSpecification {
    private final String name;
    private final int executedCount;
    private final List<EChange<EObject>> receivedChanges = new ArrayList<>();
    private final List<EChange<EObject>> executedChanges = new ArrayList<>();

    ExecutingSpecification(String name, int executedCount) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.name = name;
      this.executedCount = executedCount;
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public ChangePropagationInvocation propagateChanges(List<EChange<EObject>> changes,
        AnnotationSource changeAnnotations,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess, ModelRepositorySnapshot previousState) {
      invocations.add(name);
      receivedChanges.addAll(changes);
      List<EChange<EObject>> executed = new ArrayList<>();
      for (int i = 0; i < executedCount; i++) {
        EChange<EObject> change = EobjectFactory.eINSTANCE.createCreateEObject();
        executed.add(change);
      }
      executedChanges.addAll(executed);
      return ChangePropagationInvocation.succeeded(this, changes, executed);
    }

    @Override
    public ChangePropagationInvocation propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      throw new UnsupportedOperationException("changes are propagated as a whole");
    }
  }
}
