package tools.vitruv.change.correspondence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.change.correspondence.model.CorrespondenceModelFactory;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;
import tools.vitruv.change.correspondence.view.CorrespondenceModelViewFactory;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;

/**
 * Tests when {@link PersistableCorrespondenceModel#save()} writes the correspondence file, and
 * that it removes the correspondences of removed elements. A save that writes creates the file
 * again after the test deleted it.
 */
@ExtendWith({TestProjectManager.class, TestLogging.class, RegisterMetamodelsInStandalone.class})
class CorrespondenceSaveTest {
  private static final AllElementTypesFactory FACTORY = AllElementTypesFactory.eINSTANCE;

  private Path testProjectFolder;
  private Path correspondenceFile;
  private ResourceSet models;
  private Root left;
  private Root right;
  private PersistableCorrespondenceModel correspondenceModel;
  private EditableCorrespondenceModelView<Correspondence> correspondences;

  @BeforeAll
  static void registerResourceFactory() {
    Resource.Factory.Registry.INSTANCE
        .getExtensionToFactoryMap()
        .putIfAbsent("xmi", new XMIResourceFactoryImpl());
  }

  @BeforeEach
  void setUp(@TestProject Path testProjectFolder) {
    this.testProjectFolder = testProjectFolder;
    correspondenceFile = testProjectFolder.resolve("correspondences.correspondence");
    models = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    left = root("left");
    right = root("right");
    models.createResource(modelUri("left.xmi")).getContents().add(left);
    models.createResource(modelUri("right.xmi")).getContents().add(right);
    correspondenceModel = createCorrespondenceModel();
    correspondences =
        CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(correspondenceModel);
  }

  @Test
  void writesOnlyWhenCorrespondencesOrTheUrisOfTheirElementsChange() throws IOException {
    NonRoot first = child(left, "first");
    NonRoot second = child(right, "second");
    correspondences.addCorrespondenceBetween(first, second, "tag");
    assertWrites();
    assertDoesNotWrite();

    correspondences.addCorrespondenceBetween(child(left, "third"), child(right, "fourth"), null);
    assertWrites();
    assertDoesNotWrite();

    first.setId("renamed");
    assertWrites();
    assertDoesNotWrite();

    right.eResource().setURI(modelUri("moved.xmi"));
    assertWrites();
    assertDoesNotWrite();

    correspondences.removeCorrespondencesBetween(first, second, "tag");
    assertWrites();
    assertDoesNotWrite();
  }

  @Test
  void doesNotWrite_whenModelsChangeButTheUrisOfTheElementsDoNot() throws IOException {
    NonRoot first = child(left, "first");
    correspondences.addCorrespondenceBetween(first, child(right, "second"), null);
    assertWrites();

    left.getMultiValuedContainmentEReference().add(0, FACTORY.createNonRoot());
    first.setValue("changed");
    assertDoesNotWrite();
  }

  @Test
  void writesWhenACorrespondenceChanges() throws IOException {
    Correspondence correspondence =
        correspondences.addCorrespondenceBetween(child(left, "a"), child(right, "b"), "tag");
    assertWrites();

    correspondence.setTag("other tag");
    assertWrites();

    correspondence.getRightEObjects().add(child(right, "c"));
    assertWrites();
    assertDoesNotWrite();
  }

  @Test
  void writesWhenCorrespondencesOfRemovedElementsAreRemoved() throws IOException {
    NonRoot removed = child(left, "removed");
    correspondences.addCorrespondenceBetween(removed, child(right, "kept"), null);
    assertWrites();

    left.getMultiValuedContainmentEReference().remove(removed);
    assertWrites();
    assertTrue(correspondences.getCorrespondingEObjects(removed).isEmpty());
    assertDoesNotWrite();
  }

  @Test
  void writesOnEverySave_ifAnElementHasNoId() throws IOException {
    correspondences.addCorrespondenceBetween(child(left, "a"), child(right, null), null);

    assertWrites();
    assertWrites();
  }

  @Test
  void doesNotWriteAgain_forCorrespondencesWithMetamodelElements() throws IOException {
    correspondences.addCorrespondenceBetween(
        AllElementTypesPackage.Literals.ROOT, child(right, "a"), null);

    assertWrites();
    assertDoesNotWrite();
  }

  @Test
  void writesOnEverySave_forMetamodelElementsWithPathFragmentsInTheResourceSet()
      throws IOException {
    EPackage metamodel = EcoreFactory.eINSTANCE.createEPackage();
    metamodel.setName("metamodel");
    EClass metaclass = EcoreFactory.eINSTANCE.createEClass();
    metaclass.setName("Metaclass");
    metamodel.getEClassifiers().add(metaclass);
    models.createResource(modelUri("metamodel.xmi")).getContents().add(metamodel);
    correspondences.addCorrespondenceBetween(metaclass, child(right, "a"), null);

    assertWrites();
    assertWrites();
  }

  @Test
  void writtenFileRefersToTheCurrentElements_afterTheirUrisChanged() throws IOException {
    NonRoot first = child(left, "first");
    NonRoot second = child(right, "second");
    correspondences.addCorrespondenceBetween(first, second, null);
    correspondenceModel.save();
    first.setId("renamed");
    right.eResource().setURI(modelUri("moved.xmi"));
    correspondenceModel.save();
    left.eResource().save(null);
    right.eResource().save(null);

    ResourceSet reloadedModels = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    reloadedModels.getResource(modelUri("left.xmi"), true);
    reloadedModels.getResource(modelUri("moved.xmi"), true);
    PersistableCorrespondenceModel reloaded = createCorrespondenceModel();
    reloaded.loadSerializedCorrespondences(reloadedModels);

    EObject reloadedFirst =
        reloadedModels.getEObject(modelUri("left.xmi").appendFragment("renamed"), false);
    EObject reloadedSecond =
        reloadedModels.getEObject(modelUri("moved.xmi").appendFragment("second"), false);
    assertEquals(
        Set.of(reloadedSecond),
        CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(reloaded)
            .getCorrespondingEObjects(reloadedFirst));
  }

  @Test
  void removesCorrespondencesOfRemovedElementsOneByOne_andKeepsTheOthersInOrder() {
    assertRemovesStaleCorrespondences(100, index -> index % 2 == 0, false);
  }

  @Test
  void removesManyCorrespondencesOfRemovedElementsByRebuilding_andKeepsTheOthersInOrder() {
    // removing the first half of 70,000 one by one would move the others 35,000 times each, more
    // than rebuilding the list costs
    assertRemovesStaleCorrespondences(70_000, index -> index < 35_000, true);
  }

  private void assertRemovesStaleCorrespondences(
      int count, IntPredicate isStale, boolean rebuilds) {
    Path file = testProjectFolder.resolve("many" + count + ".correspondence");
    PersistableCorrespondenceModel model =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(
            URI.createFileURI(file.toString()));
    EditableCorrespondenceModelView<Correspondence> view =
        CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(model);
    Root container = root(null);
    models.createResource(modelUri("many" + count + ".xmi")).getContents().add(container);
    List<NonRoot> lefts = new ArrayList<>();
    List<NonRoot> rights = new ArrayList<>();
    Correspondence first = null;
    for (int i = 0; i < count; i++) {
      lefts.add(child(container, "l" + i));
      rights.add(child(container, "r" + i));
      Correspondence correspondence =
          view.addCorrespondenceBetween(lefts.get(i), rights.get(i), null);
      first = first == null ? correspondence : first;
    }
    List<Integer> bulkRemovals = new ArrayList<>();
    first.eContainer().eAdapters().add(new AdapterImpl() {
      @Override
      public void notifyChanged(Notification notification) {
        if (notification.getEventType() == Notification.REMOVE_MANY) {
          bulkRemovals.add(notification.getPosition());
        }
      }
    });

    for (int i = 0; i < count; i++) {
      if (isStale.test(i)) {
        container.getMultiValuedContainmentEReference().remove(lefts.get(i));
      }
    }
    model.save();

    assertEquals(rebuilds, !bulkRemovals.isEmpty(), "rebuilt the list");
    List<String> expectedLefts = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Set<EObject> corresponding = view.getCorrespondingEObjects(rights.get(i));
      assertEquals(isStale.test(i) ? Set.of() : Set.of(lefts.get(i)), corresponding, "index " + i);
      if (!isStale.test(i)) {
        expectedLefts.add("l" + i);
      }
    }
    assertEquals(expectedLefts, writtenLeftIds(file));
  }

  /** The IDs of the left objects of the correspondences in the file, in their order there. */
  private static List<String> writtenLeftIds(Path file) {
    Correspondences written = (Correspondences) ResourceSetUtil
        .withGlobalFactories(new ResourceSetImpl())
        .getResource(URI.createFileURI(file.toString()), true)
        .getContents()
        .get(0);
    List<String> ids = new ArrayList<>();
    for (Correspondence correspondence : written.getCorrespondences()) {
      EObject proxy = ((InternalEList<EObject>) correspondence.getLeftEObjects()).basicGet(0);
      ids.add(((InternalEObject) proxy).eProxyURI().fragment());
    }
    return ids;
  }

  private void assertWrites() throws IOException {
    Files.deleteIfExists(correspondenceFile);
    correspondenceModel.save();
    assertTrue(Files.exists(correspondenceFile), "save did not write the file");
  }

  private void assertDoesNotWrite() throws IOException {
    Files.deleteIfExists(correspondenceFile);
    correspondenceModel.save();
    assertFalse(Files.exists(correspondenceFile), "save wrote the file");
  }

  private PersistableCorrespondenceModel createCorrespondenceModel() {
    return CorrespondenceModelFactory.createPersistableCorrespondenceModel(
        URI.createFileURI(correspondenceFile.toString()));
  }

  private URI modelUri(String name) {
    return URI.createFileURI(testProjectFolder.resolve(name).toString());
  }

  private static Root root(String id) {
    Root root = FACTORY.createRoot();
    root.setId(id);
    return root;
  }

  private static NonRoot child(Root root, String id) {
    NonRoot child = FACTORY.createNonRoot();
    child.setId(id);
    root.getMultiValuedContainmentEReference().add(child);
    return child;
  }
}
