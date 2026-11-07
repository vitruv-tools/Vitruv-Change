package tools.vitruv.change.composite.attribute;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;

public class ChangeDescription2ReplaceSingleValuedEAttributeTest extends ChangeDescription2ChangeTransformationTest {
  /**
   * Write value to non-unsettable EAttribute
   */
  @Test
  void testReplaceSingleValuedEAttributeValueFromDefault() {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(42);
    };
    final List<EChange<EObject>> result = this.<Root>record_(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE, 0, 42, false, false));
  }

  /**
   * Write default value to non-unsettable EAttribute
   */
  @Test
  void testReplaceSingleValuedEAttributeValueWithDefault() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(42);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedEAttribute(0);
    };
    final List<EChange<EObject>> result = this.<Root>record_(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE, 42, 0, false, false));
  }

  /**
   * Explicitly unset non-unsettable EAttribute (should be same as writing default value to it)
   */
  @Test
  void testUnsetSingleValuedEAttributeValue() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(42);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.<Root>record_(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE, 42, 0, false, false));
  }

  /**
   * Write value to unsettable EAttribute
   */
  @Test
  void testReplaceUnsettableSingleValuedEAttributeValueFromDefault() {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(42);
    };
    final List<EChange<EObject>> result = this.<Root>record_(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE, 0, 42, false, false));
  }

  /**
   * Write default value to unsettable EAttribute
   */
  @Test
  void testReplaceUnsettableSingleValuedEAttributeValueWithDefault() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(42);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(0);
    };
    final List<EChange<EObject>> result = this.<Root>record_(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE, 42, 0, false, false));
  }

  /**
   * Unset unsettable EAttribute
   */
  @Test
  void testUnsetUnsettableSingleValuedEAttributeValue() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(42);
    };
    _function.accept(_uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.<Root>record_(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE, 42, 0, false, true));
  }
}
