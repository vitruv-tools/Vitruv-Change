package tools.vitruv.change.composite.rootobject;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@SuppressWarnings("all")
public class ChangeDescription2InsertRootEObjectTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void insertCreateRootEObjectInResource() {
    final Resource resource = this.resourceAt("insertRoot");
    final Root root = AllElementTypesCreators.aet.Root();
    final Consumer<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    final List<EChange<EObject>> result = this.<Resource>record(resource, _function);
    final boolean isCreate = true;
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertInsertRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), root, isCreate, resource), root, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, root.getId(), false, false));
  }

  @Test
  public void insertCreateTwoRootEObjectsInResource() {
    final Resource resource1 = this.resourceAt("insertRoot1");
    final Resource resource2 = this.resourceAt("insertRoot2");
    final Root root = AllElementTypesCreators.aet.Root();
    final Consumer<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    final List<EChange<EObject>> result1 = this.<Resource>record(resource1, _function);
    final boolean isCreate = true;
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertInsertRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result1, 3), root, isCreate, resource1), root, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, root.getId(), false, false));
    final Root root2 = AllElementTypesCreators.aet.Root();
    final Consumer<Resource> _function_1 = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root2);
    };
    final List<EChange<EObject>> result2 = this.<Resource>record(resource2, _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertInsertRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result2, 3), root2, isCreate, resource2), root2, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, root2.getId(), false, false));
  }
}
