package tools.vitruv.change.composite.rootobject;

import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Tests the recording of root objects being moved between resources.
 */
public class ChangeDescription2MoveRootTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  void moveRootEObjectBetweenResources() {
    final Root root = AllElementTypesCreators.aet.Root();
    final Resource resource1 = this.resourceAt("resource1");
    final Resource resource2 = this.resourceAt("resource2");
    EList<EObject> contents = resource1.getContents();
    contents.add(root);
    final Consumer<ResourceSet> _function = (ResourceSet it) -> {
      final Consumer<Resource> _function_1 = (Resource it1) -> {
        EList<EObject> contents1 = it1.getContents();
        contents1.add(root);
      };
      _function_1.accept(resource2);
    };
    final List<EChange<EObject>> result
            = this.<ResourceSet>record_(resource1.getResourceSet(), _function);
    final boolean isDelete = false;
    final boolean isCreate = false;
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
                    .assertInsertRoot(AtomicEChangeAssertHelper
                            .assertRemoveRoot(
                                    ChangeDescription2ChangeTransformationTest
                                            .<EObject>assertChangeCount(result, 2),
                                    root, isDelete, resource1), root, isCreate, resource2));
  }

  @Test
  void moveResource() {
    final String originalResourceName = "resource";
    final String changedResourceName = "newLocation";
    final Root root = AllElementTypesCreators.aet.Root();
    Resource resource = this.resourceAt(originalResourceName);
    final Consumer<Resource> _function = (Resource it) -> {
      EList<EObject> contents = it.getContents();
      contents.add(root);
    };
    _function.accept(resource);
    final URI originalUri = resource.getURI();
    final Consumer<Resource> _function_1 = (Resource it) -> {
      it.setURI(this.getUri(changedResourceName + ".xmi"));
    };
    final List<EChange<EObject>> result = this.<Resource>record_(resource, _function_1);
    final boolean isDelete = false;
    final boolean isCreate = false;
    AtomicEChangeAssertHelper.assertEmpty(
            AtomicEChangeAssertHelper.assertInsertRoot(
                    AtomicEChangeAssertHelper.assertRemoveRoot(
                            ChangeDescription2ChangeTransformationTest
                                    .<EObject>assertChangeCount(result, 2),
                            root,
                            isDelete,
                            this.resourceAt(originalResourceName),
                            originalUri),
                    root,
                    isCreate,
                    this.resourceAt(changedResourceName)));
  }
}
