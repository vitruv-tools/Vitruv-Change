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

/**
 * Tests that changing a single-valued attribute is recorded as the expected
 * {@link tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute}
 * change.
 *
 * <p>Covers setting a value, resetting it to the default, and unsetting it, for
 * both ordinary and unsettable attributes. Unsetting an ordinary attribute is
 * expected to be recorded the same way as setting its default value.
 */
public class ChangeDescription2ReplaceSingleValuedEAttributeTest
        extends ChangeDescription2ChangeTransformationTest {
  /**
   * Write value to non-unsettable EAttribute.
   */
  @Test
  void testReplaceSingleValuedEAttributeValueFromDefault() {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(42);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest
                    .assertChangeCount(result, 1),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE,
                    0, 42, false, false));
  }

  /**
   * Write default value to non-unsettable EAttribute.
   */
  @Test
  void testReplaceSingleValuedEAttributeValueWithDefault() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(42);
    };
    _function.accept(uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedEAttribute(0);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest
                    .assertChangeCount(result, 1),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE,
                    42, 0, false, false));
  }

  /**
   * Explicitly unset non-unsettable EAttribute (should be same as writing default value to it).
   */
  @Test
  void testUnsetSingleValuedEAttributeValue() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(42);
    };
    _function.accept(uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest
                    .assertChangeCount(result, 1),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE,
                    42, 0, false, false));
  }

  /**
   * Write value to unsettable EAttribute.
   */
  @Test
  void testReplaceUnsettableSingleValuedEAttributeValueFromDefault() {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(42);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest
                    .assertChangeCount(result, 1),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE,
                    0, 42, false, false));
  }

  /**
   * Write default value to unsettable EAttribute.
   */
  @Test
  void testReplaceUnsettableSingleValuedEAttributeValueWithDefault() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(42);
    };
    _function.accept(uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(0);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest
                    .assertChangeCount(result, 1), this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE,
                    42, 0, false, false));
  }

  /**
   * Unset unsettable EAttribute.
   */
  @Test
  void testUnsetUnsettableSingleValuedEAttributeValue() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(42);
    };
    _function.accept(uniquePersistedRoot);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest
                    .assertChangeCount(result, 1), this.getUniquePersistedRoot(),
                    AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE,
                    42, 0, false, true));
  }
}
