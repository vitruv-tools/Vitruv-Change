package tools.vitruv.change.composite.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.change.composite.util.CompoundEChangeAssertHelper;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

public class ChangeDescription2ReplaceSingleValuedEReferenceTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void testReplaceSingleValuedEReferenceContainment() {
    this.getUniquePersistedRoot();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(AtomicEChangeAssertHelper.assertSetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, true, false), nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false));
  }

  @Test
  public void testReplaceExistingSingleValuedEReferenceContainment() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final NonRoot replaceNonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedContainmentEReference(replaceNonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(CompoundEChangeAssertHelper.assertDeleteEObjectAndContainedElements(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(CompoundEChangeAssertHelper.assertCreateAndReplaceNonRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 4), replaceNonRoot, this.getUniquePersistedRoot(), nonRoot, AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, false), replaceNonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, replaceNonRoot.getId(), false, false), nonRoot));
  }

  @Test
  public void testReplaceExistingSingleValuedEReferenceContainmentWithDefault() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedContainmentEReference(null);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertUnsetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, true, false));
  }

  @Test
  public void testRemoveContainmentReferenceWithDelete() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      EcoreUtil.delete(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertUnsetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, true, false));
  }

  @Test
  public void testSetSingleValuedEReferenceNonContainment() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedNonContainmentEReference(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertSetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, false, false, false));
  }

  @Test
  public void testReplaceExistingSingleValuedEReferenceNonContainment() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final NonRoot replaceNonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      it.setSingleValuedNonContainmentEReference(nonRoot);
      NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
      final Consumer<NonRootObjectContainerHelper> _function_1 = (NonRootObjectContainerHelper it_1) -> {
        EList<NonRoot> _nonRootObjectsContainment = it_1.getNonRootObjectsContainment();
        _nonRootObjectsContainment.add(replaceNonRoot);
      };
      _function_1.accept(_NonRootObjectContainerHelper);
      it.setNonRootObjectContainerHelper(_NonRootObjectContainerHelper);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedNonContainmentEReference(replaceNonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, replaceNonRoot, false, false, false));
  }

  @Test
  public void testRemoveNonContainmentReferenceWithDelete() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      it.setSingleValuedNonContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      EcoreUtil.delete(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertUnsetSingleValuedEReference(AtomicEChangeAssertHelper.assertUnsetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, false, false, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, true, false));
  }

  @Test
  public void testReplaceExistingSingleValuedEReferenceNonContainmentWithDefault() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      it.setSingleValuedNonContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedNonContainmentEReference(null);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertUnsetSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, false, false, false));
  }

  @Test
  public void testUnsetExistingSingleValuedEReferenceContainment() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(CompoundEChangeAssertHelper.assertReplaceAndDeleteNonRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), nonRoot, this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE, true));
  }

  @Test
  public void testUnsetReplaceExistingSingleValuedEReferenceNonContainment() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
      final Consumer<NonRootObjectContainerHelper> _function_1 = (NonRootObjectContainerHelper it_1) -> {
        EList<NonRoot> _nonRootObjectsContainment = it_1.getNonRootObjectsContainment();
        _nonRootObjectsContainment.add(nonRoot);
      };
      _function_1.accept(_NonRootObjectContainerHelper);
      it.setNonRootObjectContainerHelper(_NonRootObjectContainerHelper);
      it.setSingleValuedUnsettableNonContainmentEReference(nonRoot);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE, nonRoot, null, false, false, true));
  }
}
