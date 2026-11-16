package tools.vitruv.change.composite.attribute;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;

/**
 * Tests that removing values from a multi-valued attribute is recorded as the
 * expected {@link tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue}
 * changes.
 *
 * <p>Covers removing single values, clearing the attribute, and removing values
 * from an unsettable attribute, both with and without an explicit unset.
 */
public class ChangeDescription2RemoveEAttributeValueTest
        extends ChangeDescription2ChangeTransformationTest {
  /**
   * Remove attribute value from multi-valued EAttribute.
   */
  @Test
  void testRemoveEAttributeValue() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      EList<Integer> multiValuedEAttribute = it.getMultiValuedEAttribute();
      multiValuedEAttribute.add(42);
      EList<Integer> multiValuedEAttribute1 = it.getMultiValuedEAttribute();
      multiValuedEAttribute1.add(55);
      EList<Integer> multiValuedEAttribute2 = it.getMultiValuedEAttribute();
      multiValuedEAttribute2.add(66);
    };
    _function.accept(uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedEAttribute().remove(2);
      it.getMultiValuedEAttribute().remove(0);
      it.getMultiValuedEAttribute().remove(0);
    };
    final List<EChange<EObject>> result = this.record_(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertRemoveEAttribute(AtomicEChangeAssertHelper
                    .assertRemoveEAttribute(AtomicEChangeAssertHelper
                            .assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest
                                    .assertChangeCount(result, 3),
                                    this.getUniquePersistedRoot(),
                                    AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE,
                                    66, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE,
                            42, 0),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE,
                    55, 0));
  }

  /**
   * Clear several attributes in a multi-valued EAttribute.
   */
  @Test
  void testClearEAttributeValue() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      EList<Integer> multiValuedEAttribute = it.getMultiValuedEAttribute();
      multiValuedEAttribute.add(42);
      EList<Integer> multiValuedEAttribute1 = it.getMultiValuedEAttribute();
      multiValuedEAttribute1.add(55);
    };
    _function.accept(uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedEAttribute().clear();
    };
    final List<EChange<EObject>> result = this.record_(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertRemoveEAttribute(AtomicEChangeAssertHelper
                    .assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest
                            .assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE,
                            55, 1),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE,
                    42, 0));
  }

  /**
   * Remove attribute value from unsettable multi-valued EAttribute (should not be an unset).
   */
  @Test
  void testRemoveUnsettableEAttributeValue() {
    Assertions.assertFalse(this.getUniquePersistedRoot()
            .eIsSet(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      EList<Integer> multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      multiValuedUnsettableEAttribute.add(42);
    };
    _function.accept(uniquePersistedRoot);
    Assertions.assertTrue(this.getUniquePersistedRoot()
            .eIsSet(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedUnsettableEAttribute().removeFirst();
    };
    final List<EChange<EObject>> result = this.record_(this.getUniquePersistedRoot(), _function_1);
    Assertions.assertTrue(this.getUniquePersistedRoot()
            .eIsSet(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest
                    .assertChangeCount(result, 1),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE,
                    42, 0));
  }

  @Test
  void testUnsetEAttributeValue() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      EList<Integer> multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      multiValuedUnsettableEAttribute.add(42);
    };
    _function.accept(uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.record_(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertUnsetFeature(AtomicEChangeAssertHelper
                    .assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest
                            .assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage.Literals
                                    .ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE,
                            42, 0),
                    this.getUniquePersistedRoot(), AllElementTypesPackage
                            .Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
  }

  @Test
  void testUnsetEAttributeValuesWithSeveralValues() {
    Assertions.assertFalse(this.getUniquePersistedRoot().isSetMultiValuedUnsettableEAttribute());
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      EList<Integer> multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      multiValuedUnsettableEAttribute.add(42);
      EList<Integer> multiValuedUnsettableEAttribute1 = it.getMultiValuedUnsettableEAttribute();
      multiValuedUnsettableEAttribute1.add(22);
    };
    _function.accept(uniquePersistedRoot);
    Assertions.assertTrue(this.getUniquePersistedRoot().isSetMultiValuedUnsettableEAttribute());
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.record_(this.getUniquePersistedRoot(), _function_1);
    Assertions.assertFalse(this.getUniquePersistedRoot().isSetMultiValuedUnsettableEAttribute());
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertUnsetFeature(AtomicEChangeAssertHelper
                    .assertRemoveEAttribute(AtomicEChangeAssertHelper
                            .assertRemoveEAttribute(
                                    ChangeDescription2ChangeTransformationTest
                                    .assertChangeCount(result,
                                            3),
                                    this.getUniquePersistedRoot(),
                                    AllElementTypesPackage
                                            .Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE,
                                    22, 1),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage
                                    .Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE,
                            42, 0),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE));
  }
}
