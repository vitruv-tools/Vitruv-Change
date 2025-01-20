package tools.vitruv.change.composite.attribute;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;

@SuppressWarnings("all")
public class ChangeDescription2RemoveEAttributeValueTest extends ChangeDescription2ChangeTransformationTest {
  /**
   * Remove attribute value from multi-valued EAttribute
   */
  @Test
  public void testRemoveEAttributeValue() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
      _multiValuedEAttribute.add(Integer.valueOf(42));
      EList<Integer> _multiValuedEAttribute_1 = it.getMultiValuedEAttribute();
      _multiValuedEAttribute_1.add(Integer.valueOf(55));
      EList<Integer> _multiValuedEAttribute_2 = it.getMultiValuedEAttribute();
      _multiValuedEAttribute_2.add(Integer.valueOf(66));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedEAttribute().remove(2);
      it.getMultiValuedEAttribute().remove(0);
      it.getMultiValuedEAttribute().remove(0);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertRemoveEAttribute(AtomicEChangeAssertHelper.assertRemoveEAttribute(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(66), 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(42), 0), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(55), 0));
  }

  /**
   * Clear several attributes in a multi-valued EAttribute
   */
  @Test
  public void testClearEAttributeValue() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
      _multiValuedEAttribute.add(Integer.valueOf(42));
      EList<Integer> _multiValuedEAttribute_1 = it.getMultiValuedEAttribute();
      _multiValuedEAttribute_1.add(Integer.valueOf(55));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedEAttribute().clear();
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertRemoveEAttribute(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(55), 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(42), 0));
  }

  /**
   * Remove attribute value from unsettable multi-valued EAttribute (should not be an unset)
   */
  @Test
  public void testRemoveUnsettableEAttributeValue() {
    Assertions.assertFalse(this.getUniquePersistedRoot().eIsSet(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      _multiValuedUnsettableEAttribute.add(Integer.valueOf(42));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    Assertions.assertTrue(this.getUniquePersistedRoot().eIsSet(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedUnsettableEAttribute().remove(0);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    Assertions.assertTrue(this.getUniquePersistedRoot().eIsSet(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(42), 0));
  }

  @Test
  public void testUnsetEAttributeValue() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      _multiValuedUnsettableEAttribute.add(Integer.valueOf(42));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertUnsetFeature(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(42), 0), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
  }

  @Test
  public void testUnsetEAttributeValuesWithSeveralValues() {
    Assertions.assertFalse(this.getUniquePersistedRoot().isSetMultiValuedUnsettableEAttribute());
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      _multiValuedUnsettableEAttribute.add(Integer.valueOf(42));
      EList<Integer> _multiValuedUnsettableEAttribute_1 = it.getMultiValuedUnsettableEAttribute();
      _multiValuedUnsettableEAttribute_1.add(Integer.valueOf(22));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    Assertions.assertTrue(this.getUniquePersistedRoot().isSetMultiValuedUnsettableEAttribute());
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    Assertions.assertFalse(this.getUniquePersistedRoot().isSetMultiValuedUnsettableEAttribute());
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertUnsetFeature(AtomicEChangeAssertHelper.assertRemoveEAttribute(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(22), 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(42), 0), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
  }
}
