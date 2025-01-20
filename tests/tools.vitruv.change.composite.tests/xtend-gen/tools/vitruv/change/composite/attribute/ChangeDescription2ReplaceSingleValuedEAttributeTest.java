package tools.vitruv.change.composite.attribute;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;

@SuppressWarnings("all")
public class ChangeDescription2ReplaceSingleValuedEAttributeTest extends ChangeDescription2ChangeTransformationTest {
  /**
   * Write value to non-unsettable EAttribute
   */
  @Test
  public void testReplaceSingleValuedEAttributeValueFromDefault() {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(Integer.valueOf(42));
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE, Integer.valueOf(0), Integer.valueOf(42), false, false));
  }

  /**
   * Write default value to non-unsettable EAttribute
   */
  @Test
  public void testReplaceSingleValuedEAttributeValueWithDefault() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(Integer.valueOf(42));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedEAttribute(Integer.valueOf(0));
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE, Integer.valueOf(42), Integer.valueOf(0), false, false));
  }

  /**
   * Explicitly unset non-unsettable EAttribute (should be same as writing default value to it)
   */
  @Test
  public void testUnsetSingleValuedEAttributeValue() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedEAttribute(Integer.valueOf(42));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE, Integer.valueOf(42), Integer.valueOf(0), false, false));
  }

  /**
   * Write value to unsettable EAttribute
   */
  @Test
  public void testReplaceUnsettableSingleValuedEAttributeValueFromDefault() {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(Integer.valueOf(42));
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(0), Integer.valueOf(42), false, false));
  }

  /**
   * Write default value to unsettable EAttribute
   */
  @Test
  public void testReplaceUnsettableSingleValuedEAttributeValueWithDefault() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(Integer.valueOf(42));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(Integer.valueOf(0));
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(42), Integer.valueOf(0), false, false));
  }

  /**
   * Unset unsettable EAttribute
   */
  @Test
  public void testUnsetUnsettableSingleValuedEAttributeValue() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedUnsettableEAttribute(Integer.valueOf(42));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(42), Integer.valueOf(0), false, true));
  }
}
