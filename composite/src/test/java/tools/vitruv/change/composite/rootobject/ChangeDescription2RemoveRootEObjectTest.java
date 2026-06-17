package tools.vitruv.change.composite.rootobject;

import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;

@SuppressWarnings("all")
public class ChangeDescription2RemoveRootEObjectTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void removeDeleteRootEObjectInResource() {
    final Root root = this.getUniquePersistedRoot();
    final Resource resource = root.eResource();
    final Consumer<Resource> _function = (Resource it) -> {
      EcoreUtil.delete(root);
    };
    final List<EChange<EObject>> result = this.<Resource>record(resource, _function);
    final boolean isDelete = true;
    AtomicEChangeAssertHelper.assertRemoveRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), root, isDelete, resource);
  }
}
