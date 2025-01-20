package tools.vitruv.change.atomic.hid.impl;

import allElementTypes.Identified;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.atomic.hid.internal.HierarchicalIdResolver;
import tools.vitruv.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.testutils.TestProject;
import tools.vitruv.testutils.TestProjectManager;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@ExtendWith({ TestProjectManager.class, RegisterMetamodelsInStandalone.class })
@SuppressWarnings("all")
public class HierarchicalIdResolverTest {
  private ResourceSet resourceSet;

  private HierarchicalIdResolver idResolver;

  private Path testProjectPath;

  @BeforeEach
  public void setup(@TestProject final Path testProjectPath) {
    this.testProjectPath = testProjectPath;
    this.resourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    this.idResolver = HierarchicalIdResolver.create(this.resourceSet);
  }

  @Test
  @DisplayName("resolve element with cache ID")
  public void resolveElementWithCacheId() {
    final Root root = AllElementTypesCreators.aet.Root();
    final HierarchicalId initialRootId = this.idResolver.getAndUpdateId(root);
    Assertions.assertEquals(root, this.idResolver.getEObject(initialRootId));
  }

  @Test
  @DisplayName("resolve element with URI ID")
  public void resolveElementWithUriId() {
    final Root root = AllElementTypesCreators.aet.Root();
    final URI resourceUri = URI.createFileURI(this.testProjectPath.resolve("root.aet").toString());
    Resource _createResource = this.resourceSet.createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final HierarchicalId rootId = this.idResolver.getAndUpdateId(root);
    Assertions.assertEquals(root, this.idResolver.getEObject(rootId));
  }

  @Test
  @DisplayName("change ID after adding element to resource")
  public void idChangesWhenAddingElementToResource() {
    final Root root = AllElementTypesCreators.aet.Root();
    final HierarchicalId initialRootId = this.idResolver.getAndUpdateId(root);
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final HierarchicalId initialNonRootId = this.idResolver.getAndUpdateId(nonRoot);
    final URI resourceUri = URI.createFileURI(this.testProjectPath.resolve("root.aet").toString());
    Resource _createResource = this.resourceSet.createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setSingleValuedContainmentEReference(nonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(root, _function_1);
      _contents.add(_doubleArrow);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final HierarchicalId insertedRootId = this.idResolver.getAndUpdateId(root);
    final HierarchicalId insertedNonRootId = this.idResolver.getAndUpdateId(root);
    Assertions.assertNotEquals(initialRootId, insertedRootId);
    Assertions.assertNotEquals(initialNonRootId, insertedNonRootId);
    Assertions.assertEquals(root, this.idResolver.getEObject(insertedRootId));
    Assertions.assertEquals(root, this.idResolver.getEObject(insertedNonRootId));
  }

  @Test
  @DisplayName("removed old ID after element change")
  public void changeRemovesOldId() {
    final Root root = AllElementTypesCreators.aet.Root();
    final HierarchicalId initialRootId = this.idResolver.getAndUpdateId(root);
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final HierarchicalId initialNonRootId = this.idResolver.getAndUpdateId(nonRoot);
    final URI resourceUri = URI.createFileURI(this.testProjectPath.resolve("root.aet").toString());
    Resource _createResource = this.resourceSet.createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setSingleValuedContainmentEReference(nonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(root, _function_1);
      _contents.add(_doubleArrow);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    this.idResolver.getAndUpdateId(root);
    this.idResolver.getAndUpdateId(nonRoot);
    final Executable _function_1 = () -> {
      this.idResolver.getEObject(initialRootId);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_1);
    final Executable _function_2 = () -> {
      this.idResolver.getEObject(initialNonRootId);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_2);
  }

  @Test
  @DisplayName("resolve element with old ID after change")
  public void resolveElementWithOldIdAfterChange() {
    final Root root = AllElementTypesCreators.aet.Root();
    final HierarchicalId initialRootId = this.idResolver.getAndUpdateId(root);
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final HierarchicalId initialNonRootId = this.idResolver.getAndUpdateId(nonRoot);
    final URI resourceUri = URI.createFileURI(this.testProjectPath.resolve("root.aet").toString());
    Resource _createResource = this.resourceSet.createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setSingleValuedContainmentEReference(nonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(root, _function_1);
      _contents.add(_doubleArrow);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    Assertions.assertEquals(root, this.idResolver.getEObject(initialRootId));
    Assertions.assertEquals(nonRoot, this.idResolver.getEObject(initialNonRootId));
  }

  @Test
  @DisplayName("update ID for unchanged object")
  public void updateIdForUnchangedObject() {
    final Root root = AllElementTypesCreators.aet.Root();
    final HierarchicalId initialRootId = this.idResolver.getAndUpdateId(root);
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final HierarchicalId initialNonRootId = this.idResolver.getAndUpdateId(nonRoot);
    final URI resourceUri = URI.createFileURI(this.testProjectPath.resolve("root.aet").toString());
    Resource _createResource = this.resourceSet.createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setSingleValuedContainmentEReference(nonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(root, _function_1);
      _contents.add(_doubleArrow);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    Assertions.assertEquals(root, this.idResolver.getEObject(initialRootId));
    Assertions.assertEquals(nonRoot, this.idResolver.getEObject(initialNonRootId));
  }

  @Test
  @DisplayName("resolve ID in other resource set")
  public void resolveInOtherResourceSet() {
    final Root root = AllElementTypesCreators.aet.Root();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final URI resourceUri = URI.createFileURI(this.testProjectPath.resolve("root.aet").toString());
    Resource _createResource = this.resourceSet.createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      try {
        EList<EObject> _contents = it.getContents();
        final Procedure1<Root> _function_1 = (Root it_1) -> {
          it_1.setSingleValuedContainmentEReference(nonRoot);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(root, _function_1);
        _contents.add(_doubleArrow);
        it.save(null);
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final HierarchicalId generatedRootId = this.idResolver.getAndUpdateId(root);
    final HierarchicalId generatedNonRootId = this.idResolver.getAndUpdateId(nonRoot);
    final ResourceSet childResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    final HierarchicalIdResolver childidResolver = HierarchicalIdResolver.create(childResourceSet);
    childResourceSet.getResource(resourceUri, true);
    final EObject childResolverRoot = this.resourceSet.getEObject(EcoreUtil.getURI(root), true);
    final EObject childResolverNonRoot = this.resourceSet.getEObject(EcoreUtil.getURI(nonRoot), true);
    Assertions.assertEquals(generatedRootId, childidResolver.getAndUpdateId(childResolverRoot));
    Assertions.assertEquals(generatedNonRootId, childidResolver.getAndUpdateId(childResolverNonRoot));
    Assertions.assertEquals(childidResolver.getEObject(generatedRootId), 
      this.idResolver.getEObject(generatedRootId));
    Assertions.assertEquals(childidResolver.getEObject(generatedNonRootId), 
      this.idResolver.getEObject(generatedNonRootId));
  }

  @Test
  @DisplayName("update ID without changing object")
  public void calculateIdMultipleTimesWithoutChangingObject() {
    final Root object = AllElementTypesCreators.aet.Root();
    final HierarchicalId id = this.idResolver.getAndUpdateId(object);
    Assertions.assertEquals(id, this.idResolver.getAndUpdateId(object));
  }

  @Test
  @DisplayName("resolve ID in child resolver for contents of multiple root elements stored in multiple resources")
  public void childResolverElementsContainedInMultipleRootElementsAndResources() {
    try {
      final Root firstRoot = AllElementTypesCreators.aet.Root();
      final Root secondRoot = AllElementTypesCreators.aet.Root();
      final NonRoot firstNonRoot = AllElementTypesCreators.aet.NonRoot();
      final NonRoot secondNonRoot = AllElementTypesCreators.aet.NonRoot();
      final Root containedRoot = AllElementTypesCreators.aet.Root();
      final NonRoot deeperContainedElement = AllElementTypesCreators.aet.NonRoot();
      final List<? extends Identified> elements = Collections.<Identified>unmodifiableList(CollectionLiterals.<Identified>newArrayList(firstRoot, secondRoot, firstNonRoot, secondNonRoot, containedRoot, deeperContainedElement));
      Resource _createResource = this.resourceSet.createResource(
        URI.createFileURI(this.testProjectPath.resolve("containedRoot.aet").toString()));
      final Procedure1<Resource> _function = (Resource it) -> {
        EList<EObject> _contents = it.getContents();
        _contents.add(containedRoot);
      };
      final Resource containedRootResource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
      Resource _createResource_1 = this.resourceSet.createResource(URI.createFileURI(this.testProjectPath.resolve("root.aet").toString()));
      final Procedure1<Resource> _function_1 = (Resource it) -> {
        try {
          EList<EObject> _contents = it.getContents();
          final Procedure1<Root> _function_2 = (Root it_1) -> {
            it_1.setSingleValuedContainmentEReference(firstNonRoot);
          };
          Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(firstRoot, _function_2);
          _contents.add(_doubleArrow);
          EList<EObject> _contents_1 = it.getContents();
          final Procedure1<Root> _function_3 = (Root it_1) -> {
            it_1.setSingleValuedContainmentEReference(secondNonRoot);
            final Procedure1<Root> _function_4 = (Root it_2) -> {
              it_2.setSingleValuedContainmentEReference(deeperContainedElement);
            };
            Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(containedRoot, _function_4);
            it_1.setRecursiveRoot(_doubleArrow_1);
          };
          Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(secondRoot, _function_3);
          _contents_1.add(_doubleArrow_1);
          it.save(null);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      };
      ObjectExtensions.<Resource>operator_doubleArrow(_createResource_1, _function_1);
      containedRootResource.save(null);
      final Consumer<Identified> _function_2 = (Identified it) -> {
        this.idResolver.getAndUpdateId(it);
      };
      elements.forEach(_function_2);
      final ResourceSet additionalResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
      final HierarchicalIdResolver additionalidResolver = HierarchicalIdResolver.create(additionalResourceSet);
      final Consumer<Identified> _function_3 = (Identified it) -> {
        final HierarchicalId elementId = this.idResolver.getAndUpdateId(it);
        Assertions.assertEquals(elementId, additionalidResolver.getAndUpdateId(additionalResourceSet.getEObject(EcoreUtil.getURI(it), true)));
        Assertions.assertNotEquals(this.idResolver.getEObject(elementId), additionalidResolver.getEObject(elementId));
        Assertions.assertTrue(EcoreUtil.equals(this.idResolver.getEObject(elementId), additionalidResolver.getEObject(elementId)));
      };
      elements.forEach(_function_3);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  @Test
  @DisplayName("generate ID and resolve it after element deletion")
  public void elementDeletionDoesNotRemoveId() {
    final Root root = AllElementTypesCreators.aet.Root();
    Resource _createResource = this.resourceSet.createResource(URI.createFileURI(this.testProjectPath.resolve("root.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final HierarchicalId id = this.idResolver.getAndUpdateId(root);
    EcoreUtil.delete(root);
    Assertions.assertEquals(root, this.idResolver.getEObject(id));
  }

  @Test
  @DisplayName("resolve ID for element moved to different container")
  public void elementMovementUpdatesId() {
    final Root root = AllElementTypesCreators.aet.Root();
    Resource _createResource = this.resourceSet.createResource(URI.createFileURI(this.testProjectPath.resolve("root.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final HierarchicalId rootId = this.idResolver.getAndUpdateId(root);
    Resource _createResource_1 = this.resourceSet.createResource(URI.createFileURI(this.testProjectPath.resolve("root2.aet").toString()));
    final Procedure1<Resource> _function_1 = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource_1, _function_1);
    Assertions.assertEquals(root, this.idResolver.getEObject(rootId));
    Assertions.assertNotEquals(rootId, this.idResolver.getAndUpdateId(root));
  }

  @Test
  @DisplayName("resolve ID during element movement to different container")
  public void elementMovementWithResolutionInTransientStateKeepsId() {
    final Root root = AllElementTypesCreators.aet.Root();
    Resource _createResource = this.resourceSet.createResource(URI.createFileURI(this.testProjectPath.resolve("root.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final HierarchicalId initialId = this.idResolver.getAndUpdateId(root);
    resource.getContents().clear();
    Assertions.assertEquals(root, this.idResolver.getEObject(initialId));
    final HierarchicalId cacheId = this.idResolver.getAndUpdateId(root);
    final Executable _function_1 = () -> {
      this.idResolver.getEObject(initialId);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_1);
    Assertions.assertNotEquals(initialId, cacheId);
    Resource _createResource_1 = this.resourceSet.createResource(URI.createFileURI(this.testProjectPath.resolve("root2.aet").toString()));
    final Procedure1<Resource> _function_2 = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource_1, _function_2);
    Assertions.assertEquals(root, this.idResolver.getEObject(cacheId));
    Assertions.assertNotEquals(cacheId, this.idResolver.getAndUpdateId(root));
    Assertions.assertNotEquals(initialId, this.idResolver.getAndUpdateId(root));
  }

  @Test
  @DisplayName("cleanup resolver when saving after element removal")
  public void cleanupAfterElementRemovalRemovesId() {
    final Root root = AllElementTypesCreators.aet.Root();
    final HierarchicalId id = this.idResolver.getAndUpdateId(root);
    this.idResolver.endTransaction();
    final Executable _function = () -> {
      this.idResolver.getEObject(id);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  @Test
  @DisplayName("cleanup resolver when saving resource after element removal")
  public void cleanupAfterElementRemovalRemovesIdWithResource() {
    final Root root = AllElementTypesCreators.aet.Root();
    Resource _createResource = this.resourceSet.createResource(URI.createFileURI(this.testProjectPath.resolve("root.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final HierarchicalId rootId = this.idResolver.getAndUpdateId(root);
    this.idResolver.endTransaction();
    Assertions.assertEquals(rootId, this.idResolver.getAndUpdateId(root));
    resource.getContents().clear();
    Assertions.assertEquals(root, this.idResolver.getEObject(rootId));
    Assertions.assertNotEquals(rootId, this.idResolver.getAndUpdateId(root));
    this.idResolver.endTransaction();
    final Executable _function_1 = () -> {
      this.idResolver.getEObject(rootId);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_1);
  }
}
