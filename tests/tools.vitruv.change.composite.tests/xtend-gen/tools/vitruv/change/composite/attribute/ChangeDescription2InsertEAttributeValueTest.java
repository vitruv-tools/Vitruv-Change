package tools.vitruv.change.composite.attribute;

import allElementTypes.AllElementTypesPackage;
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
public class ChangeDescription2InsertEAttributeValueTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void testInsertEAttributeValue() {
    this.testInsertEAttributeValue(0, 42);
  }

  @Test
  public void testMultipleInsertEAttributeValue() {
    this.testInsertEAttributeValue(0, 42);
    this.testInsertEAttributeValue(1, 21);
    this.testInsertEAttributeValue(2, 10);
  }

  @Test
  public void testInsertAtPositionInsertEAttributeValue() {
    this.testInsertEAttributeValue(0, 42);
    this.testInsertEAttributeValue(0, 21, 0);
    this.testInsertEAttributeValue(1, 10, 1);
  }

  @Test
  public void testInsertEAttributeValueSequence() {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
      _multiValuedEAttribute.add(Integer.valueOf(42));
      EList<Integer> _multiValuedEAttribute_1 = it.getMultiValuedEAttribute();
      _multiValuedEAttribute_1.add(Integer.valueOf(55));
      EList<Integer> _multiValuedEAttribute_2 = it.getMultiValuedEAttribute();
      _multiValuedEAttribute_2.add(Integer.valueOf(66));
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEAttribute(AtomicEChangeAssertHelper.assertInsertEAttribute(AtomicEChangeAssertHelper.assertInsertEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(42), 0, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(55), 1, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(66), 2, false));
  }

  @Test
  public void testTreeInsertMultiValuedEAttribute() {
    this.getUniquePersistedRoot();
    final Root innerRoot = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
      _multiValuedEAttribute.add(Integer.valueOf(1));
      EList<Integer> _multiValuedEAttribute_1 = it.getMultiValuedEAttribute();
      _multiValuedEAttribute_1.add(Integer.valueOf(2));
    };
    ObjectExtensions.<Root>operator_doubleArrow(innerRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setRecursiveRoot(innerRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEAttribute(AtomicEChangeAssertHelper.assertInsertEAttribute(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(CompoundEChangeAssertHelper.assertCreateAndReplaceNonRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 5), innerRoot, this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__RECURSIVE_ROOT, false), innerRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, innerRoot.getId(), false, false), innerRoot, AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(1), 0, false), innerRoot, AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(2), 1, false));
  }

  public void testInsertEAttributeValue(final int expectedIndex, final int expectedValue, final int position) {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.getMultiValuedEAttribute().add(position, Integer.valueOf(expectedValue));
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(expectedValue), expectedIndex, false));
  }

  public void testInsertEAttributeValue(final int expectedIndex, final int expectedValue) {
    this.getUniquePersistedRoot();
    final Consumer<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
      _multiValuedEAttribute.add(Integer.valueOf(expectedValue));
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    int _size = this.getUniquePersistedRoot().getMultiValuedEAttribute().size();
    final int index = (_size - 1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(expectedValue), index, false));
  }
}
