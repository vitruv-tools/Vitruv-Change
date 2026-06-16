package tools.vitruv.change.correspondence;

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

@ExtendWith({ TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class })
public class CorrespondenceTest {
  private static final Logger LOGGER = LogManager.getLogger(CorrespondenceTest.class);

  private static final String CORRESPONDENCE_MODEL_NAME = "correspondence.correspondence";

  private ResourceSet testResourceSet;

  private Path testProjectFolder;

  @BeforeEach
  public void acquireTestProjectFolder(@TestProject final Path testProjectFolder) {
    this.testProjectFolder = testProjectFolder;
  }

  @BeforeEach
  public void setupResourceSet() {
    this.testResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
  }

  private PersistableCorrespondenceModel createCorrespondenceModel(final boolean loadPersistence) {
    final PersistableCorrespondenceModel correspondenceModel = CorrespondenceModelFactory.createPersistableCorrespondenceModel(
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

  private EditableCorrespondenceModelView<Correspondence> wrapCorrespondenceModelIntoView(final PersistableCorrespondenceModel correspondenceModel) {
    return CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(correspondenceModel);
  }

  @Test
  public void testCorrespondenceAfterModelPersistence() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final EditableCorrespondenceModelView<Correspondence> correspondenceModel = this.createCorrespondenceModelAndReturnView();
    correspondenceModel.addCorrespondenceBetween(repo, pkg, null);
    this.saveUMLPackageInNewFile(pkg);
    this.assertRepositoryCorrespondences(correspondenceModel, repo);
  }

  @Test
  public void testCorrespondenceAfterMovingRootEObjectBetweenResources() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final EditableCorrespondenceModelView<Correspondence> correspondenceModel = this.createCorrespondenceModelAndReturnView();
    correspondenceModel.addCorrespondenceBetween(repo, pkg, null);
    this.moveUMLPackage(pkg);
    this.validateSingleCorrespondence(correspondenceModel, repo, pkg);
    this.assertRepositoryCorrespondences(correspondenceModel, repo);
  }

  @Test
  public void testReloadingCorrespondencesFromPersistence() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final PersistableCorrespondenceModel firstCorrespondenceModel = this.createCorrespondenceModel(false);
    this.wrapCorrespondenceModelIntoView(firstCorrespondenceModel).addCorrespondenceBetween(repo, pkg, null);
    firstCorrespondenceModel.save();
    final Repository repo2 = this.createPcmRepositoryWithInterfaceAndComponent(this.getAlternativePcmInstanceURI());
    final UPackage pkg2 = this.createUmlPackageWithInterfaceAndClass(this.getAlternativeUMLInstanceURI());
    final EditableCorrespondenceModelView<Correspondence> secondCorrespondenceModel = this.wrapCorrespondenceModelIntoView(this.createCorrespondenceModel(true));
    final Correspondence secondCorrespondence = secondCorrespondenceModel.addCorrespondenceBetween(repo2, pkg2, null);
    IterableUtil.<Set<EObject>, EObject>claimOne(secondCorrespondenceModel.getCorrespondingEObjects(repo2));
    Set<EObject> allEObjects = new HashSet<>(secondCorrespondence.getLeftEObjects());
    allEObjects.addAll(secondCorrespondence.getRightEObjects());
    Assertions.assertEquals(Set.<EObject>of(repo2, pkg2), allEObjects);
    this.validateSingleCorrespondence(secondCorrespondenceModel, repo2, pkg2);
  }

  @Test
  public void testRemoveCorrespondence() {
    final Repository repo = this.createPcmRepositoryWithInterfaceAndComponent();
    final UPackage pkg = this.createUmlPackageWithInterfaceAndClass();
    final PInterface repoInterface = IterableUtil.<EList<PInterface>, PInterface>claimOne(repo.getInterfaces());
    final UInterface pkgInterface = IterableUtil.<EList<UInterface>, UInterface>claimOne(pkg.getInterfaces());
    final EditableCorrespondenceModelView<Correspondence> correspondenceModel = this.createCorrespondenceModelAndReturnView();
    correspondenceModel.addCorrespondenceBetween(repoInterface, pkgInterface, null);
    correspondenceModel.removeCorrespondencesBetween(repoInterface, pkgInterface, null);
    final Set<EObject> correspForRepoInterface = correspondenceModel.getCorrespondingEObjects(repoInterface);
    Assertions.assertTrue(correspForRepoInterface.isEmpty());
    final Set<EObject> correspForPkgInterface = correspondenceModel.getCorrespondingEObjects(pkgInterface);
    Assertions.assertTrue(correspForPkgInterface.isEmpty());
  }

  private void validateSingleCorrespondence(final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final Repository repo, final UPackage pkg) {
    final EObject correspForRepo = IterableUtil.<Set<EObject>, EObject>claimOne(correspondenceModel.getCorrespondingEObjects(repo));
    Assertions.assertEquals(pkg, correspForRepo);
    final EObject correspForPkg = IterableUtil.<Set<EObject>, EObject>claimOne(correspondenceModel.getCorrespondingEObjects(pkg));
    Assertions.assertEquals(repo, correspForPkg);
    final List<PInterface> interfaces = repo.getInterfaces();
    Assertions.assertEquals(1, interfaces.size());
    final PInterface iface = interfaces.get(0);
    Assertions.assertFalse(correspondenceModel.hasCorrespondences(iface));
    final EObject correspondingPkg = IterableUtil.<Set<EObject>, EObject>claimOne(correspondenceModel.getCorrespondingEObjects(repo));
    Assertions.assertEquals(pkg, correspondingPkg);
    final EObject correspondingRepo = IterableUtil.<Set<EObject>, EObject>claimOne(correspondenceModel.getCorrespondingEObjects(pkg));
    Assertions.assertEquals(repo, correspondingRepo);
  }

  private void assertRepositoryCorrespondences(final EditableCorrespondenceModelView<?> correspondenceModel, final Repository repo) {
    final Set<EObject> correspondingObjects = correspondenceModel.getCorrespondingEObjects(repo);
    Assertions.assertEquals(1, correspondingObjects.size(), "Only one corresponding object is expected for the repository.");
    for (final EObject correspondingObject : correspondingObjects) {
      Assertions.assertNotNull(correspondingObject, "Corresponding object is null");
      final Set<EObject> reverseCorrespondingObjects = correspondenceModel.getCorrespondingEObjects(correspondingObject);
      Assertions.assertNotNull(IterableUtil.<Set<EObject>, EObject>claimOne(reverseCorrespondingObjects), "Reverse corresponding object is null");
      CorrespondenceTest.LOGGER.info("A: " + reverseCorrespondingObjects + " corresponds to B: " + correspondingObject);
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
    return URIUtil.createFileURI(this.testProjectFolder.resolve("model").resolve(fileName).toFile());
  }

  private void moveUMLPackage(final UPackage umlPackage) {
    umlPackage.eResource().setURI(this.getAlternativeUMLInstanceURI());
  }

  private void saveUMLPackageInNewFile(final UPackage umlPackage) {
    EcoreUtil.delete(umlPackage);
    new ResourceSetImpl().createResource(this.getAlternativeUMLInstanceURI()).getContents().add(umlPackage);
  }
}
