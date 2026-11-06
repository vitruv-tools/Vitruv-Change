package tools.vitruv.change.propagation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import allElementTypes.ValueBased;
import com.google.common.collect.BiMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.change.propagation.impl.DefaultModelRepositorySnapshot.ResourceSetCopy;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;

/**
 * Tests the mapping between the elements of a resource set and their copies that {@link
 * DefaultModelRepositorySnapshot#copyResourceSet(ResourceSet)} creates.
 */
@ExtendWith({TestLogging.class, RegisterMetamodelsInStandalone.class})
class DefaultModelRepositorySnapshotCopyTest {
  private static final AllElementTypesFactory FACTORY = AllElementTypesFactory.eINSTANCE;

  @BeforeAll
  static void registerResourceFactory() {
    Resource.Factory.Registry.INSTANCE
        .getExtensionToFactoryMap()
        .putIfAbsent("xmi", new XMIResourceFactoryImpl());
  }

  @Test
  void mapsEveryElementToItsCopy_inLongListsWithAndWithoutIds() {
    Root root = root("root");
    for (int i = 0; i < 300; i++) {
      root.getMultiValuedContainmentEReference().add(nonRoot(i % 2 == 0 ? "child" + i : null));
    }
    root.setNonRootObjectContainerHelper(FACTORY.createNonRootObjectContainerHelper());
    root.getNonRootObjectContainerHelper().setId(null);
    root.getNonRootObjectContainerHelper().getNonRootObjectsContainment().add(nonRoot(null));
    ResourceSet resourceSet = new ResourceSetImpl();
    resource(resourceSet, "ids.xmi").getContents().add(root);
    Resource withoutIds = resource(resourceSet, "values.xmi");
    withoutIds.getContents().add(valueTree(3, 4));
    withoutIds.getContents().add(valueTree(2, 3));

    ResourceSetCopy copy = DefaultModelRepositorySnapshot.copyResourceSet(resourceSet);

    assertMappedLikeUris(resourceSet, copy);
    assertPairedByStructure(resourceSet, copy);
    assertEquals(count(resourceSet), copy.originalToCopy().size());
  }

  @Test
  void mapsElementsContainedInAnotherResource() {
    ResourceSet resourceSet = new ResourceSetImpl();
    Root root = root("root");
    NonRoot first = nonRoot("first");
    NonRoot second = nonRoot(null);
    root.getMultiValuedContainmentEReference().addAll(List.of(first, second));
    resource(resourceSet, "container.xmi").getContents().add(root);
    resource(resourceSet, "contained.xmi").getContents().add(second);

    ResourceSetCopy copy = DefaultModelRepositorySnapshot.copyResourceSet(resourceSet);

    Root copiedRoot = (Root) copy.originalToCopy().get(root);
    EObject copiedSecond = copy.originalToCopy().get(second);
    assertSame(copiedRoot.getMultiValuedContainmentEReference().get(1), copiedSecond);
    assertEquals(second.eResource().getURI(), copiedSecond.eResource().getURI());
    assertMappedLikeUris(resourceSet, copy);
    assertPairedByStructure(resourceSet, copy);
  }

  @Test
  void pairsElementsOnlyIfTheCopyHasTheSameStructure() {
    Resource original = resource(new ResourceSetImpl(), "original.xmi");
    Root root = root("root");
    root.getMultiValuedContainmentEReference().addAll(List.of(nonRoot("a"), nonRoot("b")));
    original.getContents().add(root);

    Resource sameCopy = resource(new ResourceSetImpl(), "original.xmi");
    sameCopy.getContents().addAll(EcoreUtil.copyAll(original.getContents()));
    Map<EObject, EObject> pairs =
        DefaultModelRepositorySnapshot.pairByStructure(original, sameCopy);
    assertNotNull(pairs);
    assertSame(sameCopy.getContents().get(0), pairs.get(root));

    Resource missingElement = resource(new ResourceSetImpl(), "original.xmi");
    missingElement.getContents().addAll(EcoreUtil.copyAll(original.getContents()));
    ((Root) missingElement.getContents().get(0)).getMultiValuedContainmentEReference().remove(1);
    assertNull(DefaultModelRepositorySnapshot.pairByStructure(original, missingElement));

    Resource extraElement = resource(new ResourceSetImpl(), "original.xmi");
    extraElement.getContents().addAll(EcoreUtil.copyAll(original.getContents()));
    extraElement.getContents().add(root(null));
    assertNull(DefaultModelRepositorySnapshot.pairByStructure(original, extraElement));

    Resource otherFeature = resource(new ResourceSetImpl(), "original.xmi");
    Root otherRoot = root("root");
    otherRoot.getMultiValuedUnsettableContainmentEReference()
        .addAll(List.of(nonRoot("a"), nonRoot("b")));
    otherFeature.getContents().add(otherRoot);
    assertNull(DefaultModelRepositorySnapshot.pairByStructure(original, otherFeature));
  }

  @Test
  void pairsElementsOnlyIfTheyHaveTheSameClass() {
    Resource original = resource(new ResourceSetImpl(), "original.xmi");
    original.getContents().add(root(null));
    Resource otherClass = resource(new ResourceSetImpl(), "original.xmi");
    otherClass.getContents().add(FACTORY.createValueBased());

    assertNull(DefaultModelRepositorySnapshot.pairByStructure(original, otherClass));
  }

  @Test
  void mapsElementsWithTheSameId_toTheirOwnCopies() {
    ResourceSet resourceSet = new ResourceSetImpl();
    Root root = root("root");
    NonRoot first = nonRoot("duplicate");
    NonRoot second = nonRoot("duplicate");
    root.getMultiValuedContainmentEReference().addAll(List.of(first, second));
    resource(resourceSet, "duplicates.xmi").getContents().add(root);

    // the URIs of both elements resolve to the first copy, so only the walk can map them
    ResourceSetCopy copy = DefaultModelRepositorySnapshot.copyResourceSet(resourceSet);

    Root copiedRoot = (Root) copy.originalToCopy().get(root);
    assertSame(copiedRoot.getMultiValuedContainmentEReference().get(0),
        copy.originalToCopy().get(first));
    assertSame(copiedRoot.getMultiValuedContainmentEReference().get(1),
        copy.originalToCopy().get(second));
  }

  @Test
  void mapsToTheFirstCopiedResourceWithTheUri_likeResolvingTheUris() {
    ResourceSet resourceSet = new ResourceSetImpl();
    Resource original = resource(resourceSet, "original.xmi");
    Root root = root("root");
    original.getContents().add(root);
    ResourceSet copiedResourceSet = new ResourceSetImpl();
    Resource firstCopy = resource(copiedResourceSet, "original.xmi");
    firstCopy.getContents().addAll(EcoreUtil.copyAll(original.getContents()));
    Resource secondCopy = resource(copiedResourceSet, "original.xmi");
    secondCopy.getContents().addAll(EcoreUtil.copyAll(original.getContents()));

    BiMap<EObject, EObject> mapping =
        DefaultModelRepositorySnapshot.createOriginalToCopyMapping(resourceSet, copiedResourceSet);

    assertSame(firstCopy.getContents().get(0), mapping.get(root));
  }

  @Test
  void mapsByUri_whenTheCopyOfAResourceHasAnotherStructure() {
    ResourceSet resourceSet = new ResourceSetImpl();
    Resource original = resource(resourceSet, "original.xmi");
    Root root = root("root");
    NonRoot a = nonRoot("a");
    NonRoot b = nonRoot("b");
    root.getMultiValuedContainmentEReference().addAll(List.of(a, b));
    original.getContents().add(root);

    ResourceSet copiedResourceSet = new ResourceSetImpl();
    Resource reordered = resource(copiedResourceSet, "original.xmi");
    reordered.getContents().addAll(EcoreUtil.copyAll(original.getContents()));
    Root copiedRoot = (Root) reordered.getContents().get(0);
    copiedRoot.getMultiValuedContainmentEReference().move(0, 1);

    BiMap<EObject, EObject> mapping =
        DefaultModelRepositorySnapshot.createOriginalToCopyMapping(resourceSet, copiedResourceSet);

    assertSame(copiedRoot, mapping.get(root));
    assertSame(copiedRoot.getMultiValuedContainmentEReference().get(1), mapping.get(a));
    assertSame(copiedRoot.getMultiValuedContainmentEReference().get(0), mapping.get(b));
  }

  /** Checks the mapping against resolving each element's URI in the copied resource set. */
  private static void assertMappedLikeUris(ResourceSet resourceSet, ResourceSetCopy copy) {
    for (Resource resource : resourceSet.getResources()) {
      resource.getAllContents().forEachRemaining(original -> {
        EObject copied = copy.originalToCopy().get(original);
        assertNotNull(copied, () -> "no copy of " + original);
        assertNotSame(original, copied);
        assertSame(copy.resourceSet().getEObject(EcoreUtil.getURI(original), false), copied);
      });
    }
  }

  /** Checks that the copy of each resource is mapped by walking it, not through the URIs. */
  private static void assertPairedByStructure(ResourceSet resourceSet, ResourceSetCopy copy) {
    for (Resource resource : resourceSet.getResources()) {
      Resource copiedResource = copy.resourceSet().getResource(resource.getURI(), false);
      Map<EObject, EObject> pairs =
          DefaultModelRepositorySnapshot.pairByStructure(resource, copiedResource);
      assertNotNull(pairs, () -> "copy of " + resource.getURI() + " not walkable");
      pairs.forEach((original, copied) -> assertSame(copied, copy.originalToCopy().get(original)));
    }
  }

  private static int count(ResourceSet resourceSet) {
    List<EObject> elements = new ArrayList<>();
    for (Resource resource : resourceSet.getResources()) {
      resource.getAllContents().forEachRemaining(elements::add);
    }
    return elements.size();
  }

  private static Resource resource(ResourceSet resourceSet, String name) {
    return resourceSet.createResource(URI.createURI("test:/" + name));
  }

  private static Root root(String id) {
    Root root = FACTORY.createRoot();
    root.setId(id);
    return root;
  }

  private static NonRoot nonRoot(String id) {
    NonRoot nonRoot = FACTORY.createNonRoot();
    nonRoot.setId(id);
    return nonRoot;
  }

  /** A tree of value-based elements, which have no ID. */
  private static ValueBased valueTree(int depth, int width) {
    ValueBased node = FACTORY.createValueBased();
    node.setValue("depth " + depth);
    if (depth > 0) {
      for (int i = 0; i < width; i++) {
        node.getChildren().add(valueTree(depth - 1, width));
      }
    }
    return node;
  }
}
