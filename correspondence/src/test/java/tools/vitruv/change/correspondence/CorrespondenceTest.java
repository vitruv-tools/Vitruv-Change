package tools.vitruv.change.correspondence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pcm_mockup.PInterface;
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
import uml_mockup.UInterface;
import uml_mockup.UPackage;
import uml_mockup.Uml_mockupFactory;

/**
 * Tests for the {@link PersistableCorrespondenceModel}
 * and {@link EditableCorrespondenceModelView} classes.
 */
@ExtendWith({TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class})
class CorrespondenceTest {
  private static final Logger LOGGER = LogManager.getLogger(CorrespondenceTest.class);

  private static final String CORRESPONDENCE_MODEL_NAME = "correspondence.correspondence";

  private ResourceSet testResourceSet;

  private Path testProjectFolder;

  /**
   * Sets up the test to store data under {@code testProjectFolder}.
   *
   * @param testProjectFolder {@link Path}
   */
  @BeforeEach
  void acquireTestProjectFolder(@TestProject final Path testProjectFolder) {
    this.testProjectFolder = testProjectFolder;
    this.testResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
  }

  private PersistableCorrespondenceModel createCorrespondenceModel(final boolean loadPersistence) {
    final PersistableCorrespondenceModel correspondenceModel =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(
            this.createModelUri(CorrespondenceTest.CORRESPONDENCE_MODEL_NAME));
    Assertions.assertNotNull(correspondenceModel);
    if (loadPersistence) {
      correspondenceModel.loadSerializedCorrespondences(this.testResourceSet);
    }
    return correspondenceModel;
  }

  private EditableCorrespondenceModelView<Correspondence> createCorrespondenceModelAndReturnView() {
    return this.wrapCorrespondenceModelIntoView(this.createCorrespondenceModel(false));
  }

  private EditableCorrespondenceModelView<Correspondence> wrapCorrespondenceModelIntoView(
      final PersistableCorrespondenceModel correspondenceModel) {
    return CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(
        correspondenceModel);
  }

  @Test
  void testCorrespondenceAfterModelPersistence() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final EditableCorrespondenceModelView<Correspondence> correspondenceModel =
        this.createCorrespondenceModelAndReturnView();
    correspondenceModel.addCorrespondenceBetween(repo, pkg, null);
    this.saveUMLPackageInNewFile(pkg);
    this.assertRepositoryCorrespondences(correspondenceModel, repo);
  }

  @Test
  void testCorrespondenceAfterMovingRootEObjectBetweenResources() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final EditableCorrespondenceModelView<Correspondence> correspondenceModel =
        this.createCorrespondenceModelAndReturnView();
    correspondenceModel.addCorrespondenceBetween(repo, pkg, null);
    this.moveUMLPackage(pkg);
    this.validateSingleCorrespondence(correspondenceModel, repo, pkg);
    this.assertRepositoryCorrespondences(correspondenceModel, repo);
  }

  @Test
  void testReloadingCorrespondencesFromPersistence() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final PersistableCorrespondenceModel firstCorrespondenceModel =
        this.createCorrespondenceModel(false);
    this.wrapCorrespondenceModelIntoView(firstCorrespondenceModel)
        .addCorrespondenceBetween(repo, pkg, null);
    firstCorrespondenceModel.save();
    final Repository repo2 =
        this.createPcmRepositoryWithInterfaceAndComponent(this.getAlternativePcmInstanceURI());
    final UPackage pkg2 =
        this.createUmlPackageWithInterfaceAndClass(this.getAlternativeUMLInstanceURI());
    final EditableCorrespondenceModelView<Correspondence> secondCorrespondenceModel =
        this.wrapCorrespondenceModelIntoView(this.createCorrespondenceModel(true));
    final Correspondence secondCorrespondence =
        secondCorrespondenceModel.addCorrespondenceBetween(repo2, pkg2, null);
    IterableUtil.<Set<EObject>, EObject>claimOne(
        secondCorrespondenceModel.getCorrespondingEObjects(repo2));
    Set<EObject> allEObjects = new HashSet<>(secondCorrespondence.getLeftEObjects());
    allEObjects.addAll(secondCorrespondence.getRightEObjects());
    assertEquals(Set.<EObject>of(repo2, pkg2), allEObjects);
    this.validateSingleCorrespondence(secondCorrespondenceModel, repo2, pkg2);
  }

  @Test
  void testRemoveCorrespondence() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final PInterface repoInterface =
        IterableUtil.<EList<PInterface>, PInterface>claimOne(repo.getInterfaces());
    final UInterface pkgInterface =
        IterableUtil.<EList<UInterface>, UInterface>claimOne(pkg.getInterfaces());
    final EditableCorrespondenceModelView<Correspondence> correspondenceModel =
        this.createCorrespondenceModelAndReturnView();

    correspondenceModel.addCorrespondenceBetween(repoInterface, pkgInterface, null);
    final Set<EObject> allCorrespondingEObjects =
        correspondenceModel.getAllEObjectsInACorrespondence();
    assertEquals(Set.of(repoInterface, pkgInterface), allCorrespondingEObjects);
    correspondenceModel.removeCorrespondencesBetween(repoInterface, pkgInterface, null);
    assertTrue(correspondenceModel.getAllEObjectsInACorrespondence().isEmpty());

    final Set<EObject> correspForRepoInterface =
        correspondenceModel.getCorrespondingEObjects(repoInterface);
    Assertions.assertTrue(correspForRepoInterface.isEmpty());
    final Set<EObject> correspForPkgInterface =
        correspondenceModel.getCorrespondingEObjects(pkgInterface);
    Assertions.assertTrue(correspForPkgInterface.isEmpty());
  }

  @Test
  void testTaggingCorrespondences() {
    // Correspondence Model:
    // a <-("1")-> b, c
    // b, c <-("2")-> d
    // b <-> d
    // a, c <-> b, d

    EObject a = Pcm_mockupFactory.eINSTANCE.createComponent();
    EObject b = Pcm_mockupFactory.eINSTANCE.createPInterface();
    EObject c = Pcm_mockupFactory.eINSTANCE.createPMethod();
    EObject d = Pcm_mockupFactory.eINSTANCE.createRepository();
    EditableCorrespondenceModelView<Correspondence> correspondenceView =
        this.createCorrespondenceModelAndReturnView();

    // Empty set
    assertFalse(correspondenceView.hasCorrespondences(List.of(a, b, c, d)));
    assertTrue(correspondenceView.getCorrespondingEObjectsWithTag(a).isEmpty());

    // Add first three correspondences
    correspondenceView.addCorrespondenceBetween(a, b, "1");
    correspondenceView.addCorrespondenceBetween(b, d, "2");
    correspondenceView.addCorrespondenceBetween(b, d, null);
    // Query for b
    var correspondencesForB = correspondenceView.getCorrespondingEObjectsWithTag(b);
    assertEquals(correspondencesForB.get("1"), Set.of(a));
    assertEquals(correspondencesForB.get("2"), Set.of(d));
    assertEquals(correspondencesForB.get(null), Set.of(d));

    // Add further correspondences
    correspondenceView.addCorrespondenceBetween(List.of(a, c), List.of(b, d), null);
    correspondenceView.addCorrespondenceBetween(a, c, "1");
    correspondenceView.addCorrespondenceBetween(c, d, "2");
    // Repeat query
    var correspondencesForA = correspondenceView.getCorrespondingEObjectsWithTag(a);
    assertNull(correspondencesForA.get("3"));
    assertNull(correspondencesForA.get(null));
    assertEquals(Set.of(a, c),
        correspondenceView.getCorrespondingEObjectsWithTag(List.of(b, d)).get(null));
    assertEquals(Set.of(b, c), correspondencesForA.get("1"));
    assertNull(correspondencesForA.get("2"));
  }

  private void validateSingleCorrespondence(
      final EditableCorrespondenceModelView<Correspondence> correspondenceModel,
      final Repository repo,
      final UPackage pkg) {
    final EObject correspForRepo =
        IterableUtil.<Set<EObject>, EObject>claimOne(
            correspondenceModel.getCorrespondingEObjects(repo));
    assertEquals(pkg, correspForRepo);
    final EObject correspForPkg =
        IterableUtil.<Set<EObject>, EObject>claimOne(
            correspondenceModel.getCorrespondingEObjects(pkg));
    assertEquals(repo, correspForPkg);
    final List<PInterface> interfaces = repo.getInterfaces();
    assertEquals(1, interfaces.size());
    final PInterface iface = interfaces.get(0);
    Assertions.assertFalse(correspondenceModel.hasCorrespondences(iface));
    final EObject correspondingPkg =
        IterableUtil.<Set<EObject>, EObject>claimOne(
            correspondenceModel.getCorrespondingEObjects(repo));
    assertEquals(pkg, correspondingPkg);
    final EObject correspondingRepo =
        IterableUtil.<Set<EObject>, EObject>claimOne(
            correspondenceModel.getCorrespondingEObjects(pkg));
    assertEquals(repo, correspondingRepo);
  }

  private void assertRepositoryCorrespondences(
      final EditableCorrespondenceModelView<?> correspondenceModel, final Repository repo) {
    final Set<EObject> correspondingObjects = correspondenceModel.getCorrespondingEObjects(repo);
    assertEquals(
        1,
        correspondingObjects.size(),
        "Only one corresponding object is expected for the repository.");
    for (final EObject correspondingObject : correspondingObjects) {
      Assertions.assertNotNull(correspondingObject, "Corresponding object is null");
      final Set<EObject> reverseCorrespondingObjects =
          correspondenceModel.getCorrespondingEObjects(correspondingObject);
      Assertions.assertNotNull(
          IterableUtil.<Set<EObject>, EObject>claimOne(reverseCorrespondingObjects),
          "Reverse corresponding object is null");
      CorrespondenceTest.LOGGER.info(
          "A: {} corresponds to B: {}", reverseCorrespondingObjects, correspondingObject);
    }
  }

  private Repository createPcmRepositoryWithInterfaceAndComponent() {
    return this.createPcmRepositoryWithInterfaceAndComponent(this.getDefaultPcmInstanceURI());
  }

  private Repository createPcmRepositoryWithInterfaceAndComponent(final URI persistenceURI) {
    final Repository repository = Pcm_mockupFactory.eINSTANCE.createRepository();
    repository.getInterfaces().add(Pcm_mockupFactory.eINSTANCE.createPInterface());
    repository.getComponents().add(Pcm_mockupFactory.eINSTANCE.createComponent());
    this.testResourceSet.createResource(persistenceURI).getContents().add(repository);
    return repository;
  }

  private UPackage createUmlPackageWithInterfaceAndClass() {
    return this.createUmlPackageWithInterfaceAndClass(this.getDefaultUMLInstanceURI());
  }

  private UPackage createUmlPackageWithInterfaceAndClass(final URI persistenceURI) {
    final UPackage uPackage = Uml_mockupFactory.eINSTANCE.createUPackage();
    uPackage.getInterfaces().add(Uml_mockupFactory.eINSTANCE.createUInterface());
    uPackage.getClasses().add(Uml_mockupFactory.eINSTANCE.createUClass());
    this.testResourceSet.createResource(persistenceURI).getContents().add(uPackage);
    return uPackage;
  }

  private URI getDefaultPcmInstanceURI() {
    return this.createModelUri("My.pcm_mockup");
  }

  private URI getDefaultUMLInstanceURI() {
    return this.createModelUri("My.uml_mockup");
  }

  private URI getAlternativePcmInstanceURI() {
    return this.createModelUri("NewPCMInstance.pcm_mockup");
  }

  private URI getAlternativeUMLInstanceURI() {
    return this.createModelUri("NewUMLInstance.uml_mockup");
  }

  private URI createModelUri(final String fileName) {
    return URIUtil.createFileURI(
        this.testProjectFolder.resolve("model").resolve(fileName).toFile());
  }

  private void moveUMLPackage(final UPackage umlPackage) {
    umlPackage.eResource().setURI(this.getAlternativeUMLInstanceURI());
  }

  private void saveUMLPackageInNewFile(final UPackage umlPackage) {
    EcoreUtil.delete(umlPackage);
    new ResourceSetImpl()
        .createResource(this.getAlternativeUMLInstanceURI())
        .getContents()
        .add(umlPackage);
  }
}
