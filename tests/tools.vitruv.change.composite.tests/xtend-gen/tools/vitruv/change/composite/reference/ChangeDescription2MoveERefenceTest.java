package tools.vitruv.change.composite.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
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
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@SuppressWarnings("all")
public class ChangeDescription2MoveERefenceTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void moveMultiValuedNonContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedContainmentEReference_1 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedContainmentEReference_2 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    Root _uniquePersistedRoot_1 = this.getUniquePersistedRoot();
    final Procedure1<Root> _function_1 = (Root it) -> {
      EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
      _multiValuedNonContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedNonContainmentEReference_1 = it.getMultiValuedNonContainmentEReference();
      _multiValuedNonContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedNonContainmentEReference_2 = it.getMultiValuedNonContainmentEReference();
      _multiValuedNonContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot_1, _function_1);
    final Consumer<Root> _function_2 = (Root it) -> {
      it.getMultiValuedNonContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_2);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot2, 1, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot2, 0, false, false));
  }

  @Test
  public void moveMultiValuedUnsettableNonContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedContainmentEReference_1 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedContainmentEReference_2 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    Root _uniquePersistedRoot_1 = this.getUniquePersistedRoot();
    final Procedure1<Root> _function_1 = (Root it) -> {
      EList<NonRoot> _multiValuedUnsettableNonContainmentEReference = it.getMultiValuedUnsettableNonContainmentEReference();
      _multiValuedUnsettableNonContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedUnsettableNonContainmentEReference_1 = it.getMultiValuedUnsettableNonContainmentEReference();
      _multiValuedUnsettableNonContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedUnsettableNonContainmentEReference_2 = it.getMultiValuedUnsettableNonContainmentEReference();
      _multiValuedUnsettableNonContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot_1, _function_1);
    final Consumer<Root> _function_2 = (Root it) -> {
      it.getMultiValuedUnsettableNonContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_2);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE, nonRoot2, 1, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE, nonRoot2, 0, false, false));
  }

  @Test
  public void moveMultiValuedUnorderedNonContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedContainmentEReference_1 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedContainmentEReference_2 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    Root _uniquePersistedRoot_1 = this.getUniquePersistedRoot();
    final Procedure1<Root> _function_1 = (Root it) -> {
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      _multiValuedUnorderedNonContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference_1 = it.getMultiValuedUnorderedNonContainmentEReference();
      _multiValuedUnorderedNonContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference_2 = it.getMultiValuedUnorderedNonContainmentEReference();
      _multiValuedUnorderedNonContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot_1, _function_1);
    final Consumer<Root> _function_2 = (Root it) -> {
      it.getMultiValuedUnorderedNonContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_2);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_NON_CONTAINMENT_EREFERENCE, nonRoot2, 1, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_NON_CONTAINMENT_EREFERENCE, nonRoot2, 0, false, false));
  }

  @Test
  public void moveMultiValuedContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedContainmentEReference_1 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedContainmentEReference_2 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot2, 1, true), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot2, 0, true, false));
  }

  @Test
  public void moveMultiValuedUnsettableContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<NonRoot> _multiValuedUnsettableContainmentEReference = it.getMultiValuedUnsettableContainmentEReference();
      _multiValuedUnsettableContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedUnsettableContainmentEReference_1 = it.getMultiValuedUnsettableContainmentEReference();
      _multiValuedUnsettableContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedUnsettableContainmentEReference_2 = it.getMultiValuedUnsettableContainmentEReference();
      _multiValuedUnsettableContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedUnsettableContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE, nonRoot2, 1, true), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE, nonRoot2, 0, true, false));
  }

  @Test
  public void moveMultiValuedUnorderedContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
      _multiValuedUnorderedContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedUnorderedContainmentEReference_1 = it.getMultiValuedUnorderedContainmentEReference();
      _multiValuedUnorderedContainmentEReference_1.add(nonRoot2);
      EList<NonRoot> _multiValuedUnorderedContainmentEReference_2 = it.getMultiValuedUnorderedContainmentEReference();
      _multiValuedUnorderedContainmentEReference_2.add(nonRoot3);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedUnorderedContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_CONTAINMENT_EREFERENCE, nonRoot2, 1, true), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_CONTAINMENT_EREFERENCE, nonRoot2, 0, true, false));
  }
}
