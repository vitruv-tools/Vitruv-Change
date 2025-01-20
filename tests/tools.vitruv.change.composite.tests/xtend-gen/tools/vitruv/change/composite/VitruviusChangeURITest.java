package tools.vitruv.change.composite;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@SuppressWarnings("all")
public class VitruviusChangeURITest extends ChangeDescription2ChangeTransformationTest {
  @Test
  @DisplayName("create model elements and ensure their change URIs match resource URI")
  public void createModelElements() {
    final Resource resource = this.resourceAt("test");
    final Root root = AllElementTypesCreators.aet.Root();
    final Consumer<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setId("Root");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_2) -> {
          it_2.setId("NonRoot");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        it_1.setSingleValuedContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(root, _function_1);
      _contents.add(_doubleArrow);
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
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setId("Root");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_2) -> {
          it_2.setId("NonRoot");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        it_1.setSingleValuedContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(root, _function_1);
      _contents.add(_doubleArrow);
    };
    this.<Resource>recordComposite(resource, _function);
    final Consumer<Resource> _function_1 = (Resource it) -> {
      try {
        it.delete(null);
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    final TransactionalChange<EObject> result = this.<Resource>recordComposite(resource, _function_1);
    final Procedure1<EObject> _function_2 = (EObject it) -> {
      Assertions.assertTrue(it.eIsProxy());
    };
    IteratorExtensions.<EObject>forEach(resource.getAllContents(), _function_2);
    final Consumer<URI> _function_3 = (URI it) -> {
      Assertions.assertEquals(it, resource.getURI());
    };
    result.getChangedURIs().forEach(_function_3);
  }
}
