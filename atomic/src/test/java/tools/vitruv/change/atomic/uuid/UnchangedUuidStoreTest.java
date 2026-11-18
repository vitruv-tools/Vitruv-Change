package tools.vitruv.change.atomic.uuid;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tools.vitruv.change.testutils.metamodels.AllElementTypesCreators.aet;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;

/**
 * Tests that storing a {@link UuidResolver} again writes the file only if its content would change.
 *
 * <p>To see whether a store wrote the file, the tests replace its content with other bytes of the
 * same size and restore its modification time. A store that skips writing keeps these bytes.
 */
@ExtendWith({TestProjectManager.class, RegisterMetamodelsInStandalone.class})
class UnchangedUuidStoreTest {
  private Path testProjectPath;
  private ResourceSet resourceSet;
  private UuidResolver uuidResolver;
  /** The registered elements and their UUIDs, to compare with a store from a new resolver. */
  private final Map<EObject, Uuid> registeredElements = new IdentityHashMap<>();

  @BeforeEach
  void setup(@TestProject Path testProjectPath) {
    this.testProjectPath = testProjectPath;
    this.resourceSet = new ResourceSetImpl();
    this.uuidResolver = UuidResolver.create(resourceSet);
  }

  @Test
  @DisplayName("store again without changes")
  void storeAgainWithoutChanges() throws IOException {
    createModel("root", 3);
    store();
    byte[] tampered = tamperKeepingSizeAndModificationTime();

    store();
    assertFileContent(tampered);
  }

  @Test
  @DisplayName("store again after changing attributes")
  void storeAgainAfterChangingAttributes() throws IOException {
    Root root = createModel("root", 3);
    store();
    final byte[] tampered = tamperKeepingSizeAndModificationTime();

    root.getMultiValuedContainmentEReference().get(1).setValue("changed");
    root.setSingleValuedEAttribute(42);
    store();
    assertFileContent(tampered);
  }

  @Test
  @DisplayName("store again after appending an element that is not registered")
  void storeAgainAfterAppendingUnregisteredElement() throws IOException {
    Root root = createModel("root", 3);
    store();
    byte[] tampered = tamperKeepingSizeAndModificationTime();

    root.getMultiValuedContainmentEReference().add(aet.NonRoot());
    store();
    assertFileContent(tampered);
  }

  @Test
  @DisplayName("store again after inserting an element that is not registered before others")
  void storeAgainAfterInsertingUnregisteredElement() throws IOException {
    Root root = createModel("root", 3);
    store();
    tamperKeepingSizeAndModificationTime();

    root.getMultiValuedContainmentEReference().add(0, aet.NonRoot());
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after registering and unregistering elements")
  void storeAgainAfterRegisteringAndUnregistering() throws IOException {
    Root root = createModel("root", 3);
    store();
    tamperKeepingSizeAndModificationTime();

    NonRoot child = aet.NonRoot();
    root.getMultiValuedContainmentEReference().add(child);
    register(child);
    storeAndCheck();

    tamperKeepingSizeAndModificationTime();
    unregister(child);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after moving an element to the same reference of another container")
  void storeAgainAfterMovingToSameReferenceOfOtherContainer() throws IOException {
    Root first = createModel("root", 1);
    Root second = createRegisteredRoot(1);
    first.eResource().getContents().add(second);
    NonRoot child = aet.NonRoot();
    first.setSingleValuedContainmentEReference(child);
    register(child);
    store();
    tamperKeepingSizeAndModificationTime();

    second.setSingleValuedContainmentEReference(child);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after a contained element became a root of another resource")
  void storeAgainAfterContainedElementBecameRoot() throws IOException {
    Root root = createModel("root", 1);
    final Root other = createModel("other", 1);
    NonRoot child = aet.NonRoot();
    root.setSingleValuedContainmentEReference(child);
    register(child);
    store();
    tamperKeepingSizeAndModificationTime();

    // the element stays contained in the root
    other.eResource().getContents().add(child);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after moving an element to another reference of its container")
  void storeAgainAfterMovingToOtherReferenceOfContainer() throws IOException {
    Root root = createModel("root", 1);
    NonRoot child = aet.NonRoot();
    root.setSingleValuedContainmentEReference(child);
    register(child);
    store();
    tamperKeepingSizeAndModificationTime();

    root.setSingleValuedUnsettableContainmentEReference(child);
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after inserting a root that is not registered before others")
  void storeAgainAfterInsertingUnregisteredRoot() throws IOException {
    Root root = createModel("root", 3);
    store();
    tamperKeepingSizeAndModificationTime();

    root.eResource().getContents().add(0, aet.Root());
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after an element in a list became a root of its own resource")
  void storeAgainAfterListElementBecameRoot() throws IOException {
    Root root = createModel("root", 3);
    store();
    tamperKeepingSizeAndModificationTime();

    // the element stays contained in the root
    root.eResource().getContents().add(root.getMultiValuedContainmentEReference().get(1));
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after the file was changed by something else")
  void storeAgainAfterFileWasChanged() throws IOException {
    createModel("root", 3);
    store();

    tamperKeepingSizeAndModificationTime();
    Files.setLastModifiedTime(serializationPath(), FileTime.fromMillis(0));
    storeAndCheck();

    FileTime modificationTime = Files.getLastModifiedTime(serializationPath());
    Files.writeString(serializationPath(), "x", StandardOpenOption.APPEND);
    Files.setLastModifiedTime(serializationPath(), modificationTime);
    storeAndCheck();

    Files.delete(serializationPath());
    storeAndCheck();
  }

  @Test
  @DisplayName("store at another URI whose file has the size and time of the stored one")
  void storeAtOtherUriWithSameSizeAndModificationTime() throws IOException {
    createModel("root", 3);
    store();
    Path otherFile = testProjectPath.resolve("other.uuid");
    byte[] otherContent = new byte[(int) Files.size(serializationPath())];
    Arrays.fill(otherContent, (byte) 'x');
    Files.write(otherFile, otherContent);
    Files.setLastModifiedTime(otherFile, Files.getLastModifiedTime(serializationPath()));

    uuidResolver.storeAtUri(URI.createFileURI(otherFile.toString()));
    assertEquals(fullyStoredLines(), lines(otherFile));
  }

  @Test
  @DisplayName("store alternately at two URIs")
  void storeAlternatelyAtTwoUris() throws IOException {
    Root root = createModel("root", 3);
    store();
    URI otherUri = URI.createFileURI(testProjectPath.resolve("other.uuid").toString());
    uuidResolver.storeAtUri(otherUri);

    root.getMultiValuedContainmentEReference().move(2, 0);
    store();
    assertEquals(fullyStoredLines(), lines(serializationPath()));
    uuidResolver.storeAtUri(otherUri);
    assertEquals(fullyStoredLines(), lines(Path.of(otherUri.toFileString())));
  }

  @Test
  @DisplayName("failed store keeps the file and the next store writes it")
  void failedStoreKeepsFile() throws IOException {
    Root root = createModel("root", 3);
    store();
    byte[] stored = Files.readAllBytes(serializationPath());

    NonRoot removed = root.getMultiValuedContainmentEReference().remove(0);
    assertThrows(IllegalStateException.class, this::store);
    assertFileContent(stored);

    root.getMultiValuedContainmentEReference().add(0, removed);
    tamperKeepingSizeAndModificationTime();
    storeAndCheck();
  }

  @Test
  @DisplayName("store again after renaming the reference that contains elements")
  void storeAgainAfterRenamingContainingReference() throws IOException {
    DynamicModel model = createDynamicModel();
    store();
    tamperKeepingSizeAndModificationTime();

    model.items().setName("entries");
    storeAndCheck();
    assertFalse(Files.readString(serializationPath()).contains("@items."));
  }

  @Test
  @DisplayName("store again after adding a key to the reference that contains elements")
  void storeAgainAfterAddingKeyToContainingReference() throws IOException {
    DynamicModel model = createDynamicModel();
    store();
    tamperKeepingSizeAndModificationTime();

    model.items().getEKeys().add(model.name());
    storeAndCheck();
    assertFalse(Files.readString(serializationPath()).contains("@items."));
  }

  @Test
  @DisplayName("store again after removing a reference that contained elements from its class")
  void storeAgainAfterRemovingContainingReference() throws IOException {
    DynamicModel model = createDynamicModel();
    store();
    tamperKeepingSizeAndModificationTime();

    model.otherItemObjects().addAll(new ArrayList<>(model.itemObjects()));
    model.items().getEContainingClass().getEStructuralFeatures().remove(model.items());
    storeAndCheck();
    assertFalse(Files.readString(serializationPath()).contains("@items."));
  }

  /**
   * Makes random changes to models and stores after each, and checks that the file then contains
   * what a new resolver with the same elements would store. The seeds are fixed, so the test is
   * deterministic.
   */
  @ParameterizedTest(name = "seed {0}")
  @ValueSource(longs = {1, 2, 3, 4, 5})
  @DisplayName("store after random changes")
  void storeAfterRandomChanges(long seed) throws IOException {
    Random random = new Random(seed);
    List<Resource> resources = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      resources.add(createModel("model" + i, 4).eResource());
    }
    store();
    for (int step = 0; step < 300; step++) {
      changeRandomly(random, resources);
      store();
      assertEquals(fullyStoredLines(), lines(serializationPath()), "after step " + step);
    }
  }

  private void changeRandomly(Random random, List<Resource> resources) {
    Resource resource = resources.get(random.nextInt(resources.size()));
    List<Root> roots = roots(resource);
    if (roots.isEmpty()) {
      resource.getContents().add(createRegisteredRoot(random.nextInt(3)));
      return;
    }
    Root root = roots.get(random.nextInt(roots.size()));
    EList<NonRoot> children = root.getMultiValuedContainmentEReference();
    switch (random.nextInt(13)) {
      case 0 -> root.setSingleValuedEAttribute(random.nextInt());
      case 1 -> {
        NonRoot child = aet.NonRoot();
        children.add(random.nextInt(children.size() + 1), child);
        if (random.nextBoolean()) {
          register(child);
        }
      }
      case 2 -> {
        if (!children.isEmpty()) {
          NonRoot child = children.get(random.nextInt(children.size()));
          unregisterAll(child);
          EcoreUtil.remove(child);
        }
      }
      case 3 -> {
        if (children.size() > 1) {
          children.move(random.nextInt(children.size()), random.nextInt(children.size()));
        }
      }
      case 4 -> {
        if (!children.isEmpty()) {
          Root otherRoot = randomRoot(random, resources);
          if (otherRoot != null && otherRoot != root) {
            EList<NonRoot> otherChildren = otherRoot.getMultiValuedContainmentEReference();
            NonRoot child = children.get(random.nextInt(children.size()));
            otherChildren.add(random.nextInt(otherChildren.size() + 1), child);
          }
        }
      }
      case 5 -> resource.getContents()
          .add(random.nextInt(resource.getContents().size() + 1),
              createRegisteredRoot(random.nextInt(3)));
      case 6 -> {
        unregisterAll(root);
        resource.getContents().remove(root);
      }
      case 7 -> resource.setURI(URI.createFileURI(
          testProjectPath.resolve("model" + random.nextInt(6) + ".aet").toString()));
      case 8 -> {
        // makes an element contained in the root also a root of a resource, or not anymore
        List<NonRoot> candidates = new ArrayList<>(children);
        if (root.getSingleValuedContainmentEReference() != null) {
          candidates.add(root.getSingleValuedContainmentEReference());
        }
        if (!candidates.isEmpty()) {
          NonRoot child = candidates.get(random.nextInt(candidates.size()));
          Resource directResource = ((InternalEObject) child).eDirectResource();
          if (directResource == null) {
            resources.get(random.nextInt(resources.size())).getContents().add(child);
          } else {
            directResource.getContents().remove(child);
          }
        }
      }
      case 9 -> {
        NonRoot old = root.getSingleValuedContainmentEReference();
        if (old != null) {
          unregisterAll(old);
        }
        NonRoot child = random.nextBoolean() ? aet.NonRoot() : null;
        root.setSingleValuedContainmentEReference(child);
        if (child != null) {
          register(child);
        }
      }
      case 10 -> {
        Root old = root.getRecursiveRoot();
        if (old != null) {
          unregisterAll(old);
        }
        root.setRecursiveRoot(random.nextBoolean() ? createRegisteredRoot(2) : null);
      }
      case 11 -> {
        NonRoot child = root.getSingleValuedContainmentEReference();
        Root otherRoot = randomRoot(random, resources);
        if (child != null && otherRoot != null && otherRoot != root) {
          NonRoot replaced = otherRoot.getSingleValuedContainmentEReference();
          if (replaced != null) {
            unregisterAll(replaced);
          }
          otherRoot.setSingleValuedContainmentEReference(child);
        }
      }
      default -> {
        // no change at all in this step
      }
    }
  }

  private Root randomRoot(Random random, List<Resource> resources) {
    List<Root> roots = roots(resources.get(random.nextInt(resources.size())));
    return roots.isEmpty() ? null : roots.get(random.nextInt(roots.size()));
  }

  private static List<Root> roots(Resource resource) {
    return resource.getContents().stream()
        .filter(Root.class::isInstance)
        .map(Root.class::cast)
        .toList();
  }

  /** Creates a resource with a root that contains the given number of children, all registered. */
  private Root createModel(String name, int childrenCount) {
    Resource resource =
        new ResourceImpl(URI.createFileURI(testProjectPath.resolve(name + ".aet").toString()));
    resourceSet.getResources().add(resource);
    Root root = createRegisteredRoot(childrenCount);
    resource.getContents().add(root);
    return root;
  }

  private Root createRegisteredRoot(int childrenCount) {
    Root root = aet.Root();
    for (int i = 0; i < childrenCount; i++) {
      root.getMultiValuedContainmentEReference().add(aet.NonRoot());
    }
    register(root);
    root.eAllContents().forEachRemaining(this::register);
    return root;
  }

  /**
   * A model with a metamodel that is created at runtime.
   *
   * @param items the reference that contains the items
   * @param name the name attribute of the items
   * @param itemObjects the items in the reference {@code items} of the first container
   * @param otherItemObjects the items in another reference of the second container, initially
   *     empty
   */
  private record DynamicModel(
      EReference items,
      EAttribute name,
      List<EObject> itemObjects,
      List<EObject> otherItemObjects) {}

  /** Creates a model whose metamodel is created at runtime, so the test can change it. */
  private DynamicModel createDynamicModel() {
    EcoreFactory factory = EcoreFactory.eINSTANCE;
    EClass item = factory.createEClass();
    item.setName("Item");
    EAttribute name = factory.createEAttribute();
    name.setName("name");
    name.setEType(EcorePackage.Literals.ESTRING);
    item.getEStructuralFeatures().add(name);
    EClass container = factory.createEClass();
    container.setName("Container");
    EReference items = factory.createEReference();
    items.setName("items");
    items.setEType(item);
    items.setContainment(true);
    items.setUpperBound(EReference.UNBOUNDED_MULTIPLICITY);
    EReference otherItems = EcoreUtil.copy(items);
    otherItems.setName("otherItems");
    // the last feature, so that removing it keeps the IDs of the other features
    container.getEStructuralFeatures().addAll(List.of(otherItems, items));
    EPackage ePackage = factory.createEPackage();
    ePackage.setName("dynamic");
    ePackage.setNsURI("http://vitruv.tools/test/dynamic");
    EClass model = factory.createEClass();
    model.setName("Model");
    EReference containers = EcoreUtil.copy(items);
    containers.setName("containers");
    containers.setEType(container);
    model.getEStructuralFeatures().add(containers);
    ePackage.getEClassifiers().addAll(List.of(item, container, model));

    // two containers below the root, so that their segments are calculated
    EObject root = EcoreUtil.create(model);
    EObject first = EcoreUtil.create(container);
    EObject second = EcoreUtil.create(container);
    @SuppressWarnings("unchecked")
    List<EObject> containerObjects = (List<EObject>) root.eGet(containers);
    containerObjects.addAll(List.of(first, second));
    @SuppressWarnings("unchecked")
    List<EObject> itemObjects = (List<EObject>) first.eGet(items);
    for (int i = 0; i < 3; i++) {
      EObject itemObject = EcoreUtil.create(item);
      itemObject.eSet(name, "item" + i);
      itemObjects.add(itemObject);
    }
    Resource resource =
        new ResourceImpl(URI.createFileURI(testProjectPath.resolve("dynamic.xmi").toString()));
    resourceSet.getResources().add(resource);
    resource.getContents().add(root);
    register(root);
    root.eAllContents().forEachRemaining(this::register);
    @SuppressWarnings("unchecked")
    List<EObject> otherItemObjects = (List<EObject>) second.eGet(otherItems);
    return new DynamicModel(items, name, itemObjects, otherItemObjects);
  }

  private void register(EObject element) {
    registeredElements.put(element, uuidResolver.registerEObject(element));
  }

  private void unregister(EObject element) {
    uuidResolver.unregisterEObject(registeredElements.remove(element), element);
  }

  private void unregisterAll(EObject element) {
    if (registeredElements.containsKey(element)) {
      unregister(element);
    }
    element.eAllContents().forEachRemaining(content -> {
      if (registeredElements.containsKey(content)) {
        unregister(content);
      }
    });
  }

  private Path serializationPath() {
    return testProjectPath.resolve("UUID.uuid");
  }

  private void store() throws IOException {
    uuidResolver.storeAtUri(URI.createFileURI(serializationPath().toString()));
  }

  private void storeAndCheck() throws IOException {
    store();
    assertEquals(fullyStoredLines(), lines(serializationPath()));
  }

  /** Returns the lines that a new resolver with the same registered elements stores. */
  private Set<String> fullyStoredLines() throws IOException {
    UuidResolver newResolver = UuidResolver.create(resourceSet);
    registeredElements.forEach((element, uuid) -> newResolver.registerEObject(uuid, element));
    Path file = testProjectPath.resolve("expected.uuid");
    newResolver.storeAtUri(URI.createFileURI(file.toString()));
    return lines(file);
  }

  private static Set<String> lines(Path file) throws IOException {
    return new HashSet<>(Files.readAllLines(file));
  }

  /**
   * Replaces the content of the stored file with other bytes of the same size and restores its
   * modification time, so that the file looks as it did after storing.
   */
  private byte[] tamperKeepingSizeAndModificationTime() throws IOException {
    FileTime modificationTime = Files.getLastModifiedTime(serializationPath());
    byte[] tampered = new byte[(int) Files.size(serializationPath())];
    Arrays.fill(tampered, (byte) 'x');
    Files.write(serializationPath(), tampered);
    Files.setLastModifiedTime(serializationPath(), modificationTime);
    return tampered;
  }

  private void assertFileContent(byte[] expected) throws IOException {
    assertArrayEquals(expected, Files.readAllBytes(serializationPath()));
  }
}
