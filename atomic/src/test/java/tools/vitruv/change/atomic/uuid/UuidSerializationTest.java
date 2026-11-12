package tools.vitruv.change.atomic.uuid;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tools.vitruv.change.testutils.metamodels.AllElementTypesCreators.aet;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;

@ExtendWith({TestProjectManager.class, RegisterMetamodelsInStandalone.class})
class UuidSerializationTest {
  private ResourceSet storeResourceSet;
  private UuidResolver storeUuidResolver;
  private ResourceSet loadResourceSet;
  private UuidResolver loadUuidResolver;
  private Path testProjectPath;

  private URI getSerializationUri() {
    return URI.createFileURI(testProjectPath.resolve("UUID.uuid").toString());
  }

  @BeforeEach
  void setup(@TestProject Path testProjectPath) {
    this.testProjectPath = testProjectPath;
    this.storeResourceSet = withGlobalFactories(new ResourceSetImpl());
    this.storeUuidResolver = UuidResolver.create(storeResourceSet);
    this.loadResourceSet = withGlobalFactories(new ResourceSetImpl());
    this.loadUuidResolver = UuidResolver.create(loadResourceSet);
  }

  @ParameterizedTest(name = "{0} element(s)")
  @ValueSource(ints = {1, 2, 5, 10, 100})
  @DisplayName("store and load elements")
  void storeAndLoadElements(int count) {
    Map<EObject, Uuid> uuidMapping = new HashMap<>();
    Map<EObject, EObject> loadToStoreElementsMapping = new HashMap<>();
    for (int i = 0; i < count; i++) {
      URI uri = URI.createFileURI(testProjectPath.resolve("root" + i + ".aet").toString());

      Resource storeResource = storeResourceSet.createResource(uri);
      var storeRoot = aet.Root();
      storeResource.getContents().add(storeRoot);
      Uuid uuid = storeUuidResolver.registerEObject(storeRoot);
      uuidMapping.put(storeRoot, uuid);

      Resource loadResource = loadResourceSet.createResource(uri);
      var loadRoot = aet.Root();
      loadResource.getContents().add(loadRoot);
      loadToStoreElementsMapping.put(loadRoot, storeRoot);
    }

    URI serializationUri = getSerializationUri();
    assertDoesNotThrow(() -> storeUuidResolver.storeAtUri(serializationUri));
    assertDoesNotThrow(() -> loadUuidResolver.loadFromUri(serializationUri));

    loadToStoreElementsMapping.forEach(
        (loadElement, storeElement) -> {
          Uuid uuid = uuidMapping.get(storeElement);
          assertEquals(
              storeElement,
              storeUuidResolver.getEObject(uuid),
              "stored element does not match UUID");
          assertEquals(
              uuid, storeUuidResolver.getUuid(storeElement), "stored UUID does not match element");
          assertEquals(
              loadElement, loadUuidResolver.getEObject(uuid), "loaded element does not match UUID");
          assertEquals(
              uuid, loadUuidResolver.getUuid(loadElement), "loaded UUID does not match element");
        });
  }

  @Test
  @DisplayName("store and load elements in lists and in several roots of a resource")
  void storeAndLoadElementsInListsAndSeveralRoots() {
    URI uri = URI.createFileURI(testProjectPath.resolve("roots.aet").toString());
    Resource storeResource = storeResourceSet.createResource(uri);
    Resource loadResource = loadResourceSet.createResource(uri);
    for (int i = 0; i < 3; i++) {
      storeResource.getContents().add(createRootWithNestedChildren(20));
      loadResource.getContents().add(createRootWithNestedChildren(20));
    }
    storeResource.getAllContents().forEachRemaining(storeUuidResolver::registerEObject);

    URI serializationUri = getSerializationUri();
    assertDoesNotThrow(() -> storeUuidResolver.storeAtUri(serializationUri));
    assertDoesNotThrow(() -> loadUuidResolver.loadFromUri(serializationUri));

    var storeElements = storeResource.getAllContents();
    var loadElements = loadResource.getAllContents();
    while (storeElements.hasNext()) {
      Uuid uuid = storeUuidResolver.getUuid(storeElements.next());
      assertEquals(loadElements.next(), loadUuidResolver.getEObject(uuid));
    }
  }

  private Root createRootWithNestedChildren(int childrenCount) {
    var root = aet.Root();
    var nestedRoot = aet.Root();
    for (int i = 0; i < childrenCount; i++) {
      root.getMultiValuedContainmentEReference().add(aet.NonRoot());
      nestedRoot.getMultiValuedContainmentEReference().add(aet.NonRoot());
    }
    root.setRecursiveRoot(nestedRoot);
    return root;
  }

  @Test
  @DisplayName("store again after changing attributes")
  void storeAgainAfterChangingAttributes() throws IOException {
    Root root = createModel("root.aet", 5);
    storeAndCheck();

    root.getMultiValuedContainmentEReference().get(2).setValue("changed");
    root.setSingleValuedEAttribute(42);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after inserting and removing elements at the front of a list")
  void storeAgainAfterInsertingAndRemovingAtFront() throws IOException {
    Root root = createModel("root.aet", 5);
    storeAndCheck();

    NonRoot inserted = aet.NonRoot();
    root.getMultiValuedContainmentEReference().add(0, inserted);
    storeUuidResolver.registerEObject(inserted);
    storeAndCheck();

    NonRoot removed = root.getMultiValuedContainmentEReference().get(0);
    storeUuidResolver.unregisterEObject(storeUuidResolver.getUuid(removed), removed);
    root.getMultiValuedContainmentEReference().remove(0);
    removed = root.getMultiValuedContainmentEReference().get(0);
    storeUuidResolver.unregisterEObject(storeUuidResolver.getUuid(removed), removed);
    root.getMultiValuedContainmentEReference().remove(0);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after moving an element within a list")
  void storeAgainAfterMovingWithinList() throws IOException {
    Root root = createModel("root.aet", 5);
    storeAndCheck();

    root.getMultiValuedContainmentEReference().move(4, 0);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after moving an element to another resource")
  void storeAgainAfterMovingToOtherResource() throws IOException {
    Root first = createModel("first.aet", 5);
    Root second = createModel("second.aet", 5);
    storeAndCheck();

    second.getMultiValuedContainmentEReference().add(first.getMultiValuedContainmentEReference()
        .get(1));
    storeAndCheck();

    second.eResource().getContents().add(first.getMultiValuedContainmentEReference().get(1));
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after adding and removing roots")
  void storeAgainAfterAddingAndRemovingRoots() throws IOException {
    Root first = createModel("root.aet", 3);
    storeAndCheck();

    Root second = createRootWithNestedChildren(3);
    first.eResource().getContents().add(0, second);
    second.eAllContents().forEachRemaining(storeUuidResolver::registerEObject);
    storeUuidResolver.registerEObject(second);
    storeAndCheck();

    first.eAllContents().forEachRemaining(element ->
        storeUuidResolver.unregisterEObject(storeUuidResolver.getUuid(element), element));
    storeUuidResolver.unregisterEObject(storeUuidResolver.getUuid(first), first);
    first.eResource().getContents().remove(first);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after changing the URI of a resource")
  void storeAgainAfterChangingResourceUri() throws IOException {
    Root root = createModel("root.aet", 3);
    storeAndCheck();

    root.eResource().setURI(URI.createFileURI(testProjectPath.resolve("moved.aet").toString()));
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after renaming elements whose IDs contain their names")
  void storeAgainAfterRenamingElementsIdentifiedByName() throws IOException {
    final Resource resource = storeResourceSet.createResource(
        URI.createFileURI(testProjectPath.resolve("package.ecore").toString()));
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setName("package");
    ePackage.setNsURI("http://vitruv.tools/test/package");
    for (String name : new String[] {"A", "B", "C"}) {
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName(name);
      EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
      attribute.setName("attribute");
      eClass.getEStructuralFeatures().add(attribute);
      ePackage.getEClassifiers().add(eClass);
    }
    resource.getContents().add(ePackage);
    storeUuidResolver.registerEObject(ePackage);
    ePackage.eAllContents().forEachRemaining(storeUuidResolver::registerEObject);
    storeAndCheck();

    ePackage.getEClassifiers().get(0).setName("D");
    storeAndCheck();

    // a second element with the same name gets an index in its ID
    ePackage.getEClassifiers().get(2).setName("B");
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after unregistering an element that stays in its resource")
  void storeAgainAfterUnregisteringElementThatStays() throws IOException {
    Root root = createModel("root.aet", 3);
    storeAndCheck();

    NonRoot child = root.getMultiValuedContainmentEReference().get(1);
    storeUuidResolver.unregisterEObject(storeUuidResolver.getUuid(child), child);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after adding an element that was registered before")
  void storeAgainAfterAddingElementRegisteredBefore() throws IOException {
    Root root = createModel("root.aet", 3);
    storeAndCheck();

    NonRoot child = aet.NonRoot();
    storeUuidResolver.registerEObject(child);
    root.getMultiValuedContainmentEReference().add(1, child);
    storeAndCheck();
  }

  @Test
  @DisplayName("store at another URI")
  void storeAtOtherUri() throws IOException {
    createModel("root.aet", 3);
    storeAndCheck();

    URI otherUri = URI.createFileURI(testProjectPath.resolve("other.uuid").toString());
    storeUuidResolver.storeAtUri(otherUri);
    assertStoredUuidsLoad(otherUri);
  }

  @Test
  @DisplayName("store again after the stored file was deleted")
  void storeAgainAfterFileWasDeleted() throws IOException {
    createModel("root.aet", 3);
    storeAndCheck();

    Files.delete(testProjectPath.resolve("UUID.uuid"));
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after removing a registered element without unregistering it")
  void storeAgainAfterRemovingRegisteredElement() throws IOException {
    Root root = createModel("root.aet", 3);
    storeAndCheck();

    root.getMultiValuedContainmentEReference().remove(1);
    assertThrows(
        IllegalStateException.class, () -> storeUuidResolver.storeAtUri(getSerializationUri()));
  }

  @Test
  @DisplayName("store again after removing a resource with registered elements")
  void storeAgainAfterRemovingResource() throws IOException {
    Root root = createModel("root.aet", 3);
    storeAndCheck();

    storeResourceSet.getResources().remove(root.eResource());
    assertThrows(
        IllegalStateException.class, () -> storeUuidResolver.storeAtUri(getSerializationUri()));
  }

  @Test
  @DisplayName("store again after unloading a resource with registered elements")
  void storeAgainAfterUnloadingResource() throws IOException {
    Root root = createModel("root.aet", 3);
    storeAndCheck();

    root.eResource().unload();
    assertThrows(
        IllegalStateException.class, () -> storeUuidResolver.storeAtUri(getSerializationUri()));
  }

  /** Creates a resource with a root that contains the given number of children, all registered. */
  private Root createModel(String fileName, int childrenCount) {
    Resource resource = storeResourceSet.createResource(
        URI.createFileURI(testProjectPath.resolve(fileName).toString()));
    Root root = aet.Root();
    for (int i = 0; i < childrenCount; i++) {
      root.getMultiValuedContainmentEReference().add(aet.NonRoot());
    }
    resource.getContents().add(root);
    resource.getAllContents().forEachRemaining(storeUuidResolver::registerEObject);
    return root;
  }

  private void storeAndCheck() throws IOException {
    for (Resource resource : storeResourceSet.getResources()) {
      resource.save(null);
    }
    storeUuidResolver.storeAtUri(getSerializationUri());
    assertStoredUuidsLoad(getSerializationUri());
  }

  /**
   * Loads the saved resources and the stored UUIDs into a new resource set and checks that each
   * loaded element has the UUID of the element it was saved from.
   */
  private void assertStoredUuidsLoad(URI serializationUri) throws IOException {
    ResourceSet resourceSet = withGlobalFactories(new ResourceSetImpl());
    for (Resource resource : storeResourceSet.getResources()) {
      resourceSet.getResource(resource.getURI(), true);
    }
    UuidResolver uuidResolver = UuidResolver.create(resourceSet);
    uuidResolver.loadFromUri(serializationUri);
    for (Resource resource : storeResourceSet.getResources()) {
      var elements = resource.getAllContents();
      var loadedElements = resourceSet.getResource(resource.getURI(), false).getAllContents();
      while (elements.hasNext()) {
        EObject element = elements.next();
        EObject loadedElement = loadedElements.next();
        assertEquals(
            storeUuidResolver.hasUuid(element) ? storeUuidResolver.getUuid(element) : null,
            uuidResolver.hasUuid(loadedElement) ? uuidResolver.getUuid(loadedElement) : null,
            "wrong UUID for " + loadedElement);
      }
      assertFalse(loadedElements.hasNext());
    }
  }

  @Test
  @DisplayName("store dangling element")
  void storeDanglingElement() {
    var danglingElement = aet.Root();
    storeUuidResolver.registerEObject(danglingElement);
    assertThrows(
        IllegalStateException.class, () -> storeUuidResolver.storeAtUri(getSerializationUri()));
  }

  @Test
  @DisplayName("load with non-empty resolver")
  void loadWithNonEmptyResolver() {
    URI uri = URI.createFileURI(testProjectPath.resolve("root.aet").toString());
    Resource storeResource = storeResourceSet.createResource(uri);
    var storeRoot = aet.Root();
    storeResource.getContents().add(storeRoot);
    storeUuidResolver.registerEObject(storeRoot);
    URI serializationUri = getSerializationUri();
    assertDoesNotThrow(() -> storeUuidResolver.storeAtUri(serializationUri));

    loadUuidResolver.registerEObject(aet.Root());
    assertThrows(IllegalStateException.class, () -> loadUuidResolver.loadFromUri(serializationUri));
  }
}
