package tools.vitruv.change.correspondence;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pcm_mockup.Pcm_mockupFactory;
import pcm_mockup.Repository;
import tools.vitruv.change.correspondence.model.CorrespondenceModelFactory;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;
import tools.vitruv.change.correspondence.view.CorrespondenceModelViewFactory;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;
import uml_mockup.UPackage;
import uml_mockup.Uml_mockupFactory;

/** Tests {@link PersistableCorrespondenceModel#copy(Map)}. */
@ExtendWith({TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class})
class CorrespondenceModelCopyTest {
  private static final String TAG = "tag";

  private Repository repository;
  private UPackage umlPackage;

  @BeforeAll
  static void registerResourceFactory() {
    Resource.Factory.Registry.INSTANCE
        .getExtensionToFactoryMap()
        .putIfAbsent("xmi", new XMIResourceFactoryImpl());
  }

  @BeforeEach
  void createModels() {
    repository = Pcm_mockupFactory.eINSTANCE.createRepository();
    umlPackage = Uml_mockupFactory.eINSTANCE.createUPackage();
  }

  @Test
  void copyReplacesMappedObjects() {
    PersistableCorrespondenceModel original =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(null);
    view(original).addCorrespondenceBetween(repository, umlPackage, TAG);
    Repository copiedRepository = EcoreUtil.copy(repository);

    PersistableCorrespondenceModel copy =
        original.copy(new HashMap<>(Map.of(repository, copiedRepository)));

    assertEquals(Set.of(umlPackage), view(copy).getCorrespondingEObjects(copiedRepository, TAG));
    assertEquals(Set.of(copiedRepository), view(copy).getCorrespondingEObjects(umlPackage, TAG));
    assertFalse(view(copy).hasCorrespondences(repository));
    assertEquals(Set.of(TAG), copy.getAllTags());
  }

  @Test
  void copyDoesNotChangeOriginal() {
    PersistableCorrespondenceModel original =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(null);
    view(original).addCorrespondenceBetween(repository, umlPackage, TAG);
    Repository copiedRepository = EcoreUtil.copy(repository);

    PersistableCorrespondenceModel copy =
        original.copy(new HashMap<>(Map.of(repository, copiedRepository)));
    view(copy).removeCorrespondencesBetween(copiedRepository, umlPackage, TAG);

    assertEquals(Set.of(umlPackage), view(original).getCorrespondingEObjects(repository, TAG));
    assertFalse(view(original).hasCorrespondences(copiedRepository));
  }

  @Test
  void copyIsStoredInResourceOfOriginal(@TestProject Path testProjectPath) {
    ResourceSet modelResourceSet = withGlobalFactories(new ResourceSetImpl());
    modelResourceSet.createResource(createFileUri(testProjectPath, "repository.xmi"))
        .getContents().add(repository);
    modelResourceSet.createResource(createFileUri(testProjectPath, "package.xmi"))
        .getContents().add(umlPackage);
    URI correspondencesUri = createFileUri(testProjectPath, "correspondences.xmi");
    PersistableCorrespondenceModel original =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(correspondencesUri);
    view(original).addCorrespondenceBetween(repository, umlPackage, TAG);

    original.copy(new HashMap<>()).save();

    PersistableCorrespondenceModel loaded =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(correspondencesUri);
    loaded.loadSerializedCorrespondences(modelResourceSet);
    assertEquals(Set.of(umlPackage), view(loaded).getCorrespondingEObjects(repository, TAG));
  }

  private static EditableCorrespondenceModelView<Correspondence> view(
      PersistableCorrespondenceModel correspondenceModel) {
    return CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(
        correspondenceModel);
  }

  private static URI createFileUri(Path testProjectPath, String fileName) {
    return URI.createFileURI(testProjectPath.resolve("model").resolve(fileName).toString());
  }
}
