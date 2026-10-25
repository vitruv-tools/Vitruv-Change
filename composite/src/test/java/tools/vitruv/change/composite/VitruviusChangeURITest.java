package tools.vitruv.change.composite;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

public class VitruviusChangeURITest extends ChangeDescription2ChangeTransformationTest {
  @Test
  @DisplayName("create model elements and ensure their change URIs match resource URI")
  public void createModelElements() {
    final Resource resource = this.resourceAt("test");
    final Root root = AllElementTypesCreators.aet.Root();
    final Consumer<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      _NonRoot.setId("NonRoot");
      root.setId("Root");
      root.setSingleValuedContainmentEReference(_NonRoot);
      _contents.add(root);
    };
    final TransactionalChange<EObject> result = this.<Resource>recordComposite(resource, _function);
    final Consumer<URI> _function_1 = (URI it) -> {
      Assertions.assertEquals(it, resource.getURI());
    };
    result.getChangedURIs().forEach(_function_1);
  }

  @Test
  @DisplayName("delete resource and ensure change URIs for proxy elements match resource URI")
  public void deleteModel() {
    final Resource resource = this.resourceAt("test");
    final Root root = AllElementTypesCreators.aet.Root();
    final Consumer<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      _NonRoot.setId("NonRoot");
      root.setId("Root");
      root.setSingleValuedContainmentEReference(_NonRoot);
      _contents.add(root);
    };
    this.<Resource>recordComposite(resource, _function);
    final Consumer<Resource> _function_1 = (Resource it) -> {
      try {
        it.delete(null);
      } catch (Throwable _e) {
        throw new RuntimeException(_e);
      }
    };
    final TransactionalChange<EObject> result = this.<Resource>recordComposite(resource, _function_1);
    final Consumer<EObject> _function_2 = (EObject it) -> {
      Assertions.assertTrue(it.eIsProxy());
    };
    resource.getAllContents().forEachRemaining(_function_2);
    final Consumer<URI> _function_3 = (URI it) -> {
      Assertions.assertEquals(it, resource.getURI());
    };
    result.getChangedURIs().forEach(_function_3);
  }
}
