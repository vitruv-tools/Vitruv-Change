package tools.vitruv.change.propagation.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
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
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.AnnotationSource;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.impl.PredefinedInteractionResultProviderImpl;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository;
import tools.vitruv.change.propagation.ModelRepositorySnapshot;
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

  @BeforeAll
  static void registerResourceFactory() {
    Resource.Factory.Registry.INSTANCE
        .getExtensionToFactoryMap()
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
      var previousRoot =
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

  /**
   * Tests that change propagation steps can be undone at any time.
   *
   * <p>Changes:
   * <ol>
   *   <li>Setting Root.singleValuedEAttribute triggers an update of Root.id.
   *   This change should succeed.</li>
   *   <li>Creating a NonRoot element triggers an insertion into Root,
   *   which in turn leads to an exception.</li>
   * </ol>
   * After both changes, only the first should be accepted.
   */
  @Test
  void undoFunctionalityWorks() {
    Predicate<EChange<EObject>> changeRootAttributeTrigger = change ->
        change instanceof ReplaceSingleValuedEAttribute<EObject, ?> replaceChange
        && replaceChange.getAffectedFeature().equals(
            AllElementTypesPackage.eINSTANCE.getRoot_SingleValuedEAttribute());
    var specification1 = new RecordingSpecification("setRootIdOnRootAttribute",
        (changes, previousState) -> {
          if (changes.stream().anyMatch(changeRootAttributeTrigger)) {
            repositoryRoot().setId("Foobar");
          }
        });

    Predicate<EChange<EObject>> createNonRootTrigger = change ->
        change instanceof CreateEObject<?> createEObject
            && createEObject.getAffectedEObjectType().equals(
            AllElementTypesPackage.eINSTANCE.getNonRoot());
    var specification2 = new RecordingSpecification("addNonRootToRoot", (changes, previousState) -> {
      Optional<EChange<EObject>> nonRootCreation =
          changes.stream().filter(createNonRootTrigger).findFirst();
      if (nonRootCreation.isPresent()) {
        var nonRoot = (NonRoot)
            ((CreateEObject<EObject>) nonRootCreation.get()).getAffectedElement();
        repositoryRoot().setSingleValuedNonContainmentEReference(nonRoot);
      }
    });

    Predicate<EChange<EObject>> insertNonRootIntoRootTrigger = change ->
        change instanceof ReplaceSingleValuedEReference<?> replaceReference
            && replaceReference.getAffectedFeature().equals(
            AllElementTypesPackage.eINSTANCE.getRoot_SingleValuedNonContainmentEReference()
        );
    var specification3 = new RecordingSpecification("failWhenAddingNonRoot", (changes, previousState) -> {
      if (changes.stream().anyMatch(insertNonRootIntoRootTrigger)) {
        throw new RuntimeException("Fail on Insertion of Root");
      }
    });

    // Setup view
    var propagator = createPropagator(
        Map.of(
            specification1, 0,
            specification2, 0,
            specification3, 0
        ));
    propagateInView(propagator, this::createRootInView);
    // Change 1 triggers specification 1
    var root = (Root) viewResourceSet.getResource(modelUri, true).getContents().get(0);
    propagateInView(propagator, () -> root.setSingleValuedEAttribute(42));
    // Change 1 succeeds
    assertEquals(42, repositoryRoot().getSingleValuedEAttribute());
    assertEquals("Foobar", repositoryRoot().getId());

    // Change 2 triggers an error
    assertThrows(RuntimeException.class, () -> propagateInView(propagator, () -> {
      var nonRoot = AllElementTypesFactory.eINSTANCE.createNonRoot();
      nonRoot.setId("barbaz");
      root.setSingleValuedContainmentEReference(nonRoot);
    }));
    // NonRoot is empty
    assertNull(repositoryRoot().getSingleValuedContainmentEReference());
    assertNull(repositoryRoot().getSingleValuedNonContainmentEReference());
    // Change 1 effects are still there
    assertEquals(42, repositoryRoot().getSingleValuedEAttribute());
    assertEquals("Foobar", repositoryRoot().getId());
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

  private ChangePropagator createPropagator(Map<ChangePropagationSpecification, Integer> levels) {
    return new ChangePropagator(modelRepository,
        new ChangePropagationSpecificationRepository(levels.keySet(), levels),
        UserInteractionFactory.instance.createUserInteractor(
            new PredefinedInteractionResultProviderImpl(null)));
  }

  /** Records its invocations and the changes it receives, then applies the given reaction. */
  private class RecordingSpecification extends AbstractChangePropagationSpecification {
    private final String name;
    /**
     * {@code reaction} contains the underlying logic of the specification.
     */
    private final BiConsumer<List<EChange<EObject>>, ModelRepositorySnapshot> reaction;
    private final List<EChange<EObject>> receivedChanges = new ArrayList<>();
    private final List<Optional<Author>> receivedAuthors = new ArrayList<>();

    /**
     * Creates a new {@link RecordingSpecification}.
     *
     * @param name {@link String}
     * @param reaction {@link BiConsumer}
     *     A function that processes a {@link EChange} list
     *     for a given {@link ModelRepositorySnapshot}.
     */
    RecordingSpecification(String name,
        BiConsumer<List<EChange<EObject>>, ModelRepositorySnapshot> reaction) {
      super(MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI),
          MetamodelDescriptor.with(AllElementTypesPackage.eNS_URI));
      this.name = name;
      this.reaction = reaction;
    }

    /**
     * A {@link RecordingSpecification} "always" handles any change.
     * The actual handler logic must be part of the {@link RecordingSpecification#reaction}.
     *
     * @param change - the atomic change for which to check
     *      whether this specification reacts to it. Must not be <code>null</code>.
     * @param correspondenceModel - the correspondence model to retrieve information
     *      about the target model from. Must not be <code>null</code>.
     * @return true
     */
    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public void propagateChanges(List<EChange<EObject>> changes,
        AnnotationSource changeAnnotations,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess, ModelRepositorySnapshot previousState) {
      invocations.add(name);
      receivedChanges.addAll(changes);
      receivedAuthors.add(changeAnnotations.getAnnotation(Author.class));
      reaction.accept(changes, previousState);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Always throws an {@link UnsupportedOperationException}. Single {@link EChange}s are
     * not supported. Call {@link RecordingSpecification#propagateChanges} instead.
     */
    @Override
    public void propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      throw new UnsupportedOperationException("changes are propagated as a whole");
    }
  }
}
