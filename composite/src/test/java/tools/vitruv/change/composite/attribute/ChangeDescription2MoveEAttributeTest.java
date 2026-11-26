package tools.vitruv.change.composite.attribute;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;

/**
 * Tests the recording of value moves within multi-valued attributes.
 */
public class ChangeDescription2MoveEAttributeTest
        extends ChangeDescription2ChangeTransformationTest {
  @Test
  void moveMultiValuedEAttribute() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<Integer> multiValuedEAttribute = it.getMultiValuedEAttribute();
      multiValuedEAttribute.add(42);
      EList<Integer> multiValuedEAttribute1 = it.getMultiValuedEAttribute();
      multiValuedEAttribute1.add(55);
      EList<Integer> multiValuedEAttribute2 = it.getMultiValuedEAttribute();
      multiValuedEAttribute2.add(66);
    };
    function.accept(uniquePersistedRoot);
    final Consumer<Root> function1 = (Root it) -> {
      it.getMultiValuedEAttribute().move(0, 1);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), function1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertInsertEAttribute(AtomicEChangeAssertHelper
                    .assertRemoveEAttribute(ChangeDescription2ChangeTransformationTest
                            .assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE,
                            55, 1),
                    AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE,
                    55, 0, false));
  }

  @Test
  void moveMultiValuedUnsettableEAttribute() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<Integer> multiValuedUnsettableEAttribute = it.getMultiValuedUnsettableEAttribute();
      multiValuedUnsettableEAttribute.add(42);
      EList<Integer> multiValuedUnsettableEAttribute1 = it.getMultiValuedUnsettableEAttribute();
      multiValuedUnsettableEAttribute1.add(55);
      EList<Integer> multiValuedUnsettableEAttribute2 = it.getMultiValuedUnsettableEAttribute();
      multiValuedUnsettableEAttribute2.add(66);
    };
    function.accept(uniquePersistedRoot);
    final Consumer<Root> function1 = (Root it) -> {
      it.getMultiValuedUnsettableEAttribute().move(0, 1);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), function1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertInsertEAttribute(
                    AtomicEChangeAssertHelper.assertRemoveEAttribute(
                            ChangeDescription2ChangeTransformationTest.assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage.Literals
                                    .ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE,
                            55, 1),
                    AllElementTypesPackage.Literals
                            .ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE,
                    55, 0, false));
  }

  @Test
  void moveMultiValuedUnorderedEAttribute() {
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<Integer> multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
      multiValuedUnorderedEAttribute.add(42);
      EList<Integer> multiValuedUnorderedEAttribute1 = it.getMultiValuedUnorderedEAttribute();
      multiValuedUnorderedEAttribute1.add(55);
      EList<Integer> multiValuedUnorderedEAttribute2 = it.getMultiValuedUnorderedEAttribute();
      multiValuedUnorderedEAttribute2.add(66);
    };
    function.accept(uniquePersistedRoot);
    final Consumer<Root> function1 = (Root it) -> {
      it.getMultiValuedUnorderedEAttribute().move(0, 1);
    };
    final List<EChange<EObject>> result =
            this.recordObject(this.getUniquePersistedRoot(), function1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper
            .assertInsertEAttribute(
                    AtomicEChangeAssertHelper.assertRemoveEAttribute(
                            ChangeDescription2ChangeTransformationTest.assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_EATTRIBUTE,
                            55, 1),
                    AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNORDERED_EATTRIBUTE,
                    55, 0, false));
  }
}
