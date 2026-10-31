package tools.vitruv.change.propagation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.nio.file.Path;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.change.propagation.ModelRepositorySnapshot;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;

/** Tests the snapshots created by {@link DefaultChangeRecordingModelRepository#createSnapshot()}. */
@ExtendWith({TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class})
class DefaultModelRepositorySnapshotTest {
  private static final String TAG = "tag";

  private DefaultChangeRecordingModelRepository modelRepository;
  private Path testProjectPath;
  private URI modelUri;
  private Root root;
  private NonRoot nonRoot;

  @BeforeAll
  static void registerResourceFactory() {
    Resource.Factory.Registry.INSTANCE
        .getExtensionToFactoryMap()
        .putIfAbsent("xmi", new XMIResourceFactoryImpl());
  }

  @BeforeEach
  void setUp(@TestProject Path testProjectPath) {
    this.testProjectPath = testProjectPath;
    modelRepository = new DefaultChangeRecordingModelRepository(null, testProjectPath);
    modelUri = createModelUri("root.xmi");

    root = AllElementTypesFactory.eINSTANCE.createRoot();
    root.setId("root");
    root.setSingleValuedEAttribute(1);
    nonRoot = AllElementTypesFactory.eINSTANCE.createNonRoot();
    nonRoot.setId("nonRoot");
    root.setSingleValuedContainmentEReference(nonRoot);
    modelRepository.persistAsRoot(root, modelUri);
    modelRepository.getCorrespondenceModel().addCorrespondenceBetween(root, nonRoot, TAG);
  }

  @AfterEach
  void tearDown() throws Exception {
    modelRepository.close();
  }

  @Test
  void snapshotContainsCopiesOfAllModelElements() {
    try (ModelRepositorySnapshot snapshot = modelRepository.createSnapshot()) {
      Root snapshotRoot = (Root) snapshot.getRepositoryToSnapshotMap().get(root);
      NonRoot snapshotNonRoot = (NonRoot) snapshot.getRepositoryToSnapshotMap().get(nonRoot);

      assertNotSame(root, snapshotRoot);
      assertTrue(EcoreUtil.equals(root, snapshotRoot));
      assertSame(snapshotNonRoot, snapshotRoot.getSingleValuedContainmentEReference());
      assertSame(root, snapshot.getSnapshotToRepositoryMap().get(snapshotRoot));
      assertSame(nonRoot, snapshot.getSnapshotToRepositoryMap().get(snapshotNonRoot));
    }
  }

  @Test
  void snapshotCorrespondencesReferToCopies() {
    try (ModelRepositorySnapshot snapshot = modelRepository.createSnapshot()) {
      Root snapshotRoot = (Root) snapshot.getRepositoryToSnapshotMap().get(root);
      NonRoot snapshotNonRoot = (NonRoot) snapshot.getRepositoryToSnapshotMap().get(nonRoot);

      assertEquals(Set.of(snapshotNonRoot),
          snapshot.getCorrespondenceModel().getCorrespondingEObjects(snapshotRoot, TAG));
      assertFalse(snapshot.getCorrespondenceModel().hasCorrespondences(root));
    }
  }

  @Test
  void snapshotIsNotAffectedByLaterChanges() {
    try (ModelRepositorySnapshot snapshot = modelRepository.createSnapshot()) {
      Root snapshotRoot = (Root) snapshot.getRepositoryToSnapshotMap().get(root);

      root.setSingleValuedEAttribute(2);
      modelRepository.getCorrespondenceModel().removeCorrespondencesBetween(root, nonRoot, TAG);

      assertEquals(1, snapshotRoot.getSingleValuedEAttribute());
      assertTrue(snapshot.getCorrespondenceModel().hasCorrespondences(snapshotRoot));
    }
  }

  @Test
  void modelsPersistedInSnapshotDoNotReachRepository() {
    URI otherModelUri = createModelUri("other.xmi");

    try (ModelRepositorySnapshot snapshot = modelRepository.createSnapshot()) {
      Root otherRoot = AllElementTypesFactory.eINSTANCE.createRoot();
      snapshot.persistAsRoot(otherRoot, otherModelUri);

      assertSame(otherRoot, snapshot.getModelResource(otherModelUri).getContents().get(0));
      assertEquals(2, snapshot.getModelResources().size());
      assertEquals(1, modelRepository.getModelResources().size());
    }
  }

  @Test
  void closingSnapshotUnloadsOnlyItsOwnResources() {
    ModelRepositorySnapshot snapshot = modelRepository.createSnapshot();

    snapshot.close();

    assertTrue(snapshot.getModelResources().isEmpty());
    assertEquals(1, root.getSingleValuedEAttribute());
    assertSame(root, modelRepository.getModelResource(modelUri).getContents().get(0));
  }

  private URI createModelUri(String fileName) {
    return URI.createFileURI(testProjectPath.resolve("model").resolve(fileName).toString());
  }
}
