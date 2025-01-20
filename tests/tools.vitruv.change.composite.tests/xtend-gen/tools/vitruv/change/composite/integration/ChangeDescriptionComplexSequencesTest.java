package tools.vitruv.change.composite.integration;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.change.composite.util.CompoundEChangeAssertHelper;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@SuppressWarnings("all")
public class ChangeDescriptionComplexSequencesTest extends ChangeDescription2ChangeTransformationTest {
  /**
   * Changes that overwrite each other between two change propagation triggers are not recognized by EMF.
   */
  @Test
  public void testOverwritingSequence() {
    this.getUniquePersistedRoot();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      it.setSingleValuedContainmentEReference(null);
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertSetSingleValuedEReference(AtomicEChangeAssertHelper.assertUnsetSingleValuedEReference(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertSetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 5), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, true, false), nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, false, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, false, false));
  }

  @Test
  public void testInsertTreeInContainment() {
    this.getUniquePersistedRoot();
    final NonRootObjectContainerHelper nonRootObjectsContainer = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<Root> _function = (Root it) -> {
      final Procedure1<NonRootObjectContainerHelper> _function_1 = (NonRootObjectContainerHelper it_1) -> {
        EList<NonRoot> _nonRootObjectsContainment = it_1.getNonRootObjectsContainment();
        _nonRootObjectsContainment.add(nonRoot);
      };
      NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(nonRootObjectsContainer, _function_1);
      it.setNonRootObjectContainerHelper(_doubleArrow);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(CompoundEChangeAssertHelper.assertCreateAndInsertNonRoot(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertSetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 6), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__NON_ROOT_OBJECT_CONTAINER_HELPER, nonRootObjectsContainer, true, true, false), nonRootObjectsContainer, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRootObjectsContainer.getId(), false, false), nonRootObjectsContainer, AllElementTypesPackage.Literals.NON_ROOT_OBJECT_CONTAINER_HELPER__NON_ROOT_OBJECTS_CONTAINMENT, nonRoot, 0, false), nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false));
  }

  @Test
  public void testInsertComplexTreeInContainment() {
    this.getUniquePersistedRoot();
    final Root secondRoot = AllElementTypesCreators.aet.Root();
    final NonRootObjectContainerHelper nonRootObjectsContainer = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<Root> _function = (Root it) -> {
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setSingleValuedNonContainmentEReference(nonRoot);
        final Procedure1<NonRootObjectContainerHelper> _function_2 = (NonRootObjectContainerHelper it_2) -> {
          EList<NonRoot> _nonRootObjectsContainment = it_2.getNonRootObjectsContainment();
          _nonRootObjectsContainment.add(nonRoot);
        };
        NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(nonRootObjectsContainer, _function_2);
        it_1.setNonRootObjectContainerHelper(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(secondRoot, _function_1);
      it.setRecursiveRoot(_doubleArrow);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(CompoundEChangeAssertHelper.assertCreateAndInsertNonRoot(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertSetSingleValuedEReference(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertSetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 10), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__RECURSIVE_ROOT, secondRoot, true, true, false), secondRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, secondRoot.getId(), false, false), secondRoot, AllElementTypesPackage.Literals.ROOT__NON_ROOT_OBJECT_CONTAINER_HELPER, nonRootObjectsContainer, true, true, false), nonRootObjectsContainer, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRootObjectsContainer.getId(), false, false), nonRootObjectsContainer, AllElementTypesPackage.Literals.NON_ROOT_OBJECT_CONTAINER_HELPER__NON_ROOT_OBJECTS_CONTAINMENT, nonRoot, 0, false), nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false), secondRoot, AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE, null, nonRoot, false, false, false));
  }
}
