package tools.vitruv.change.atomic.hid.impl;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tools.vitruv.change.testutils.metamodels.AllElementTypesCreators.aet;

import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.Root;
import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
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
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdCalculator;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdResolver;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;

@ExtendWith({TestProjectManager.class, RegisterMetamodelsInStandalone.class})
class HierarchicalIdCalculatorTest {
  private ResourceSet resourceSet;
  private Path testProjectPath;

  @BeforeEach
  void setup(@TestProject Path testProjectPath) {
    this.testProjectPath = testProjectPath;
    this.resourceSet = withGlobalFactories(new ResourceSetImpl());
  }

  @Test
  @DisplayName("calculate the index of an element in its containing list")
  void calculateIndexInList() {
    Root root = createRootWithChildren(5);
    Resource resource = createResource("root.aet");
    resource.getContents().add(root);

    NonRoot child = root.getMultiValuedContainmentEReference().get(3);
    assertEquals(
        new HierarchicalId(resource.getURI() + "#/0/@multiValuedContainmentEReference.3"),
        new HierarchicalIdCalculator().getId(child));
  }

  @Test
  @DisplayName("calculate the IDs of nested elements in lists like the ID resolver")
  void sameIdsAsResolverForNestedElements() {
    Root root = createRootWithChildren(5);
    for (int i = 0; i < 5; i++) {
      root.getMultiValuedUnorderedContainmentEReference().add(aet.NonRoot());
    }
    root.setSingleValuedContainmentEReference(aet.NonRoot());
    root.setRecursiveRoot(createRootWithChildren(3));
    NonRootObjectContainerHelper helper = aet.NonRootObjectContainerHelper();
    helper.getNonRootObjectsContainment().add(aet.NonRoot());
    helper.getNonRootObjectsContainment().add(aet.NonRoot());
    root.setNonRootObjectContainerHelper(helper);
    Resource resource = createResource("root.aet");
    resource.getContents().add(root);

    assertSameIdsAsResolver(resource);
  }

  @Test
  @DisplayName("calculate the IDs of elements in several roots of a resource like the ID resolver")
  void sameIdsAsResolverForSeveralRoots() {
    Resource resource = createResource("roots.aet");
    resource.getContents().add(createRootWithChildren(3));
    resource.getContents().add(aet.NonRoot());
    resource.getContents().add(createRootWithChildren(2));

    assertSameIdsAsResolver(resource);
  }

  @Test
  @DisplayName("calculate the IDs of elements whose containers build their own segments")
  void sameIdsAsResolverForOwnSegments() {
    // Ecore elements use the names of their contents in fragment segments, not their indices
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setName("package");
    EClass first = EcoreFactory.eINSTANCE.createEClass();
    first.setName("Same");
    EClass second = EcoreFactory.eINSTANCE.createEClass();
    second.setName("Same");
    ePackage.getEClassifiers().add(first);
    ePackage.getEClassifiers().add(EcoreFactory.eINSTANCE.createEClass());
    ePackage.getEClassifiers().add(second);
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("source");
    annotation.getContents().add(createRootWithChildren(3));
    second.getEAnnotations().add(annotation);
    Resource resource = createResource("package.ecore");
    resource.getContents().add(ePackage);

    assertSameIdsAsResolver(resource);
  }

  @Test
  @DisplayName("reject element without resource")
  void rejectElementWithoutResource() {
    Root root = aet.Root();
    HierarchicalIdCalculator idCalculator = new HierarchicalIdCalculator();
    assertThrows(IllegalArgumentException.class, () -> idCalculator.getId(root));
  }

  private void assertSameIdsAsResolver(Resource resource) {
    HierarchicalIdResolver idResolver = HierarchicalIdResolver.create(resourceSet);
    HierarchicalIdCalculator idCalculator = new HierarchicalIdCalculator();
    for (var iterator = resource.getAllContents(); iterator.hasNext(); ) {
      EObject element = iterator.next();
      assertEquals(idResolver.getAndUpdateId(element), idCalculator.getId(element));
    }
  }

  private Root createRootWithChildren(int childrenCount) {
    Root root = aet.Root();
    for (int i = 0; i < childrenCount; i++) {
      root.getMultiValuedContainmentEReference().add(aet.NonRoot());
    }
    return root;
  }

  private Resource createResource(String fileName) {
    return resourceSet.createResource(
        URI.createFileURI(testProjectPath.resolve(fileName).toString()));
  }
}
