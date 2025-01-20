package tools.vitruv.change.correspondence;

import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pcm_mockup.Component;
import pcm_mockup.PInterface;
import pcm_mockup.Pcm_mockupFactory;
import pcm_mockup.Repository;
import tools.vitruv.change.correspondence.model.CorrespondenceModelFactory;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;
import tools.vitruv.change.correspondence.view.CorrespondenceModelViewFactory;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.testutils.TestLogging;
import tools.vitruv.testutils.TestProject;
import tools.vitruv.testutils.TestProjectManager;
import uml_mockup.UClass;
import uml_mockup.UInterface;
import uml_mockup.UPackage;
import uml_mockup.Uml_mockupFactory;

@ExtendWith({ TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class })
@SuppressWarnings("all")
public class CorrespondenceTest {
  private static final Logger LOGGER = Logger.getLogger(CorrespondenceTest.class);

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
    EList<EObject> _leftEObjects = secondCorrespondence.getLeftEObjects();
    EList<EObject> _rightEObjects = secondCorrespondence.getRightEObjects();
    Assertions.assertEquals(Set.<EObject>of(repo2, pkg2), 
      IterableExtensions.<EObject>toSet(Iterables.<EObject>concat(_leftEObjects, _rightEObjects)));
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
    Assertions.assertEquals(correspForRepo, pkg);
    final EObject correspForPkg = IterableUtil.<Set<EObject>, EObject>claimOne(correspondenceModel.getCorrespondingEObjects(pkg));
    Assertions.assertEquals(correspForPkg, repo);
    final List<PInterface> interfaces = repo.getInterfaces();
    Assertions.assertEquals(interfaces.size(), 1);
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
      {
        Assertions.assertNotNull(correspondingObject, "Corresponding object is null");
        final Set<EObject> reverseCorrespondingObjects = correspondenceModel.getCorrespondingEObjects(correspondingObject);
        Assertions.assertNotNull(IterableUtil.<Set<EObject>, EObject>claimOne(reverseCorrespondingObjects), "Reverse corresponding object is null");
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("A: ");
        _builder.append(reverseCorrespondingObjects);
        _builder.append(" corresponds to B: ");
        _builder.append(correspondingObject);
        CorrespondenceTest.LOGGER.info(_builder);
      }
    }
  }

  private Repository createPcmRepositoryWithInterfaceAndComponent() {
    return this.createPcmRepositoryWithInterfaceAndComponent(this.getDefaultPcmInstanceURI());
  }

  private Repository createPcmRepositoryWithInterfaceAndComponent(final URI persistenceURI) {
    Repository _createRepository = Pcm_mockupFactory.eINSTANCE.createRepository();
    final Procedure1<Repository> _function = (Repository it) -> {
      EList<PInterface> _interfaces = it.getInterfaces();
      PInterface _createPInterface = Pcm_mockupFactory.eINSTANCE.createPInterface();
      _interfaces.add(_createPInterface);
      EList<Component> _components = it.getComponents();
      Component _createComponent = Pcm_mockupFactory.eINSTANCE.createComponent();
      _components.add(_createComponent);
      EList<EObject> _contents = this.testResourceSet.createResource(persistenceURI).getContents();
      _contents.add(it);
    };
    return ObjectExtensions.<Repository>operator_doubleArrow(_createRepository, _function);
  }

  private UPackage createUmlPackageWithInterfaceAndClass() {
    return this.createUmlPackageWithInterfaceAndClass(this.getDefaultUMLInstanceURI());
  }

  private UPackage createUmlPackageWithInterfaceAndClass(final URI persistenceURI) {
    UPackage _createUPackage = Uml_mockupFactory.eINSTANCE.createUPackage();
    final Procedure1<UPackage> _function = (UPackage it) -> {
      EList<UInterface> _interfaces = it.getInterfaces();
      UInterface _createUInterface = Uml_mockupFactory.eINSTANCE.createUInterface();
      _interfaces.add(_createUInterface);
      EList<UClass> _classes = it.getClasses();
      UClass _createUClass = Uml_mockupFactory.eINSTANCE.createUClass();
      _classes.add(_createUClass);
      EList<EObject> _contents = this.testResourceSet.createResource(persistenceURI).getContents();
      _contents.add(it);
    };
    return ObjectExtensions.<UPackage>operator_doubleArrow(_createUPackage, _function);
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
    Resource _eResource = umlPackage.eResource();
    _eResource.setURI(this.getAlternativeUMLInstanceURI());
  }

  private void saveUMLPackageInNewFile(final UPackage umlPackage) {
    EcoreUtil.delete(umlPackage);
    EList<EObject> _contents = new ResourceSetImpl().createResource(this.getAlternativeUMLInstanceURI()).getContents();
    _contents.add(umlPackage);
  }
}
