package tools.vitruv.change.composite.rootobject;

import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@SuppressWarnings("all")
public class ChangeDescription2MoveRootTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void moveRootEObjectBetweenResources() {
    final Root root = AllElementTypesCreators.aet.Root();
    final Resource resource1 = this.resourceAt("resource1");
    final Resource resource2 = this.resourceAt("resource2");
    EList<EObject> _contents = resource1.getContents();
    _contents.add(root);
    final Consumer<ResourceSet> _function = (ResourceSet it) -> {
      final Procedure1<Resource> _function_1 = (Resource it_1) -> {
        EList<EObject> _contents_1 = it_1.getContents();
        _contents_1.add(root);
      };
      ObjectExtensions.<Resource>operator_doubleArrow(resource2, _function_1);
    };
    final List<EChange<EObject>> result = this.<ResourceSet>record(resource1.getResourceSet(), _function);
    final boolean isDelete = false;
    final boolean isCreate = false;
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertRoot(AtomicEChangeAssertHelper.assertRemoveRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), root, isDelete, resource1), root, isCreate, resource2));
  }

  @Test
  public void moveResource() {
    final String originalResourceName = "resource";
    final String changedResourceName = "newLocation";
    final Root root = AllElementTypesCreators.aet.Root();
    Resource _resourceAt = this.resourceAt(originalResourceName);
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_resourceAt, _function);
    final URI originalUri = resource.getURI();
    final Consumer<Resource> _function_1 = (Resource it) -> {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(changedResourceName);
      _builder.append(".xmi");
      it.setURI(this.getUri(_builder));
    };
    final List<EChange<EObject>> result = this.<Resource>record(resource, _function_1);
    final boolean isDelete = false;
    final boolean isCreate = false;
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertRoot(AtomicEChangeAssertHelper.assertRemoveRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), root, isDelete, this.resourceAt(originalResourceName), originalUri), root, isCreate, this.resourceAt(changedResourceName)));
  }
}
