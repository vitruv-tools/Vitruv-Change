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

@SuppressWarnings("all")
public class ChangeDescription2MoveEAttributeTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void moveMultiValuedEAttribute() {
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
      it.getMultiValuedEAttribute().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEAttribute(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(55), 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE, Integer.valueOf(55), 0, false));
  }

  @Test
  public void moveMultiValuedUnsettableEAttribute() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      _multiValuedUnsettableEAttribute.add(Integer.valueOf(42));
      EList<Integer> _multiValuedUnsettableEAttribute_1 = it.getMultiValuedUnsettableEAttribute();
      _multiValuedUnsettableEAttribute_1.add(Integer.valueOf(55));
      EList<Integer> _multiValuedUnsettableEAttribute_2 = it.getMultiValuedUnsettableEAttribute();
      _multiValuedUnsettableEAttribute_2.add(Integer.valueOf(66));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedUnsettableEAttribute().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEAttribute(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(55), 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE, Integer.valueOf(55), 0, false));
  }

  @Test
  public void moveMultiValuedUnorderedEAttribute() {
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<Integer> _multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
      _multiValuedUnorderedEAttribute.add(Integer.valueOf(42));
      EList<Integer> _multiValuedUnorderedEAttribute_1 = it.getMultiValuedUnorderedEAttribute();
      _multiValuedUnorderedEAttribute_1.add(Integer.valueOf(55));
      EList<Integer> _multiValuedUnorderedEAttribute_2 = it.getMultiValuedUnorderedEAttribute();
      _multiValuedUnorderedEAttribute_2.add(Integer.valueOf(66));
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedUnorderedEAttribute().move(0, 1);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEAttribute(AtomicEChangeAssertHelper.assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_EATTRIBUTE, Integer.valueOf(55), 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_EATTRIBUTE, Integer.valueOf(55), 0, false));
  }
}
