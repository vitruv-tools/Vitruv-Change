package tools.vitruv.change.composite.integration;

import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import static tools.vitruv.change.composite.util.AtomicEChangeAssertHelper.*;
import static tools.vitruv.change.composite.util.CompoundEChangeAssertHelper.*;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

public class ChangeDescriptionComplexSequencesTest extends ChangeDescription2ChangeTransformationTest {
  /**
   * Changes that overwrite each other between two change propagation triggers are
   * not recognized by EMF.
   */
  @Test
  public void testOverwritingSequence() {
    Root root = getUniquePersistedRoot();
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    List<EChange<EObject>> result = record(root, it -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      it.setSingleValuedContainmentEReference(null);
      it.setSingleValuedContainmentEReference(nonRoot);
    });
    var changes = assertChangeCount(result, 5);
    changes = assertSetSingleValuedEReference(changes, root,
        AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, true, false);
    changes = assertReplaceSingleValuedEAttribute(changes, nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID,
        null, nonRoot.getId(), false, false);
    changes = assertUnsetSingleValuedEReference(changes, root,
        AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, false, false);
    changes = assertSetSingleValuedEReference(changes, root,
        AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, false, false);
    assertEmpty(changes);
  }

  @Test
  public void testInsertTreeInContainment() {
    Root root = getUniquePersistedRoot();
    NonRootObjectContainerHelper nonRootObjectsContainer = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    List<EChange<EObject>> result = record(root, it -> {
      nonRootObjectsContainer.getNonRootObjectsContainment().add(nonRoot);
      it.setNonRootObjectContainerHelper(nonRootObjectsContainer);
    });
    var changes = assertChangeCount(result, 6);
    changes = assertSetSingleValuedEReference(changes, root,
        AllElementTypesPackage.Literals.ROOT__NON_ROOT_OBJECT_CONTAINER_HELPER, nonRootObjectsContainer, true, true,
        false);
    changes = assertReplaceSingleValuedEAttribute(changes, nonRootObjectsContainer,
        AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRootObjectsContainer.getId(), false, false);
    changes = assertCreateAndInsertNonRoot(changes, nonRootObjectsContainer,
        AllElementTypesPackage.Literals.NON_ROOT_OBJECT_CONTAINER_HELPER__NON_ROOT_OBJECTS_CONTAINMENT, nonRoot, 0,
        false);
    changes = assertReplaceSingleValuedEAttribute(changes, nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID,
        null, nonRoot.getId(), false, false);
    assertEmpty(changes);
  }

  @Test
  public void testInsertComplexTreeInContainment() {
    Root root = getUniquePersistedRoot();
    Root secondRoot = AllElementTypesCreators.aet.Root();
    NonRootObjectContainerHelper nonRootObjectsContainer = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    List<EChange<EObject>> result = record(root, it -> {
      secondRoot.setSingleValuedNonContainmentEReference(nonRoot);
      nonRootObjectsContainer.getNonRootObjectsContainment().add(nonRoot);
      secondRoot.setNonRootObjectContainerHelper(nonRootObjectsContainer);
      it.setRecursiveRoot(secondRoot);
    });
    var changes = assertChangeCount(result, 10);
    changes = assertSetSingleValuedEReference(changes, root, AllElementTypesPackage.Literals.ROOT__RECURSIVE_ROOT,
        secondRoot, true, true, false);
    changes = assertReplaceSingleValuedEAttribute(changes, secondRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID,
        null, secondRoot.getId(), false, false);
    changes = assertSetSingleValuedEReference(changes, secondRoot,
        AllElementTypesPackage.Literals.ROOT__NON_ROOT_OBJECT_CONTAINER_HELPER, nonRootObjectsContainer, true, true,
        false);
    changes = assertReplaceSingleValuedEAttribute(changes, nonRootObjectsContainer,
        AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRootObjectsContainer.getId(), false, false);
    changes = assertCreateAndInsertNonRoot(changes, nonRootObjectsContainer,
        AllElementTypesPackage.Literals.NON_ROOT_OBJECT_CONTAINER_HELPER__NON_ROOT_OBJECTS_CONTAINMENT, nonRoot, 0,
        false);
    changes = assertReplaceSingleValuedEAttribute(changes, nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID,
        null, nonRoot.getId(), false, false);
    changes = assertReplaceSingleValuedEReference(changes, secondRoot,
        AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE, null, nonRoot, false, false,
        false);
    assertEmpty(changes);
  }
}
