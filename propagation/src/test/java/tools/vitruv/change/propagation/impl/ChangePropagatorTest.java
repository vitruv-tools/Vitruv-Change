package tools.vitruv.change.propagation.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.MetamodelDescriptor;
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
    viewRecorder.beginRecording();
    modification.run();
    propagator.propagateChange(
        viewChangeResolver.assignIds(viewRecorder.endRecording()), List.of());
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
    public void propagateChanges(List<EChange<EObject>> changes,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess, ModelRepositorySnapshot previousState) {
      invocations.add(name);
      receivedChanges.addAll(changes);
      reaction.accept(changes, previousState);
    }

    @Override
    public void propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      throw new UnsupportedOperationException("changes are propagated as a whole");
    }
  }
}
