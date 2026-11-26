package tools.vitruv.change.composite.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Tests the recording of element moves within multi-valued references.
 */
public class ChangeDescription2MoveERefenceTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  void moveMultiValuedNonContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<NonRoot> multiValuedContainmentEReference
              = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedContainmentEReference1
              = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedContainmentEReference2
              = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference2.add(nonRoot3);
    };
    function.accept(uniquePersistedRoot);
    Root uniquePersistedRoot1 = this.getUniquePersistedRoot();
    final Consumer<Root> function1 = (Root it) -> {
      EList<NonRoot> multiValuedNonContainmentEReference
              = it.getMultiValuedNonContainmentEReference();
      multiValuedNonContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedNonContainmentEReference1
              = it.getMultiValuedNonContainmentEReference();
      multiValuedNonContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedNonContainmentEReference2
              = it.getMultiValuedNonContainmentEReference();
      multiValuedNonContainmentEReference2.add(nonRoot3);
    };
    function1.accept(uniquePersistedRoot1);
    final Consumer<Root> function2 = (Root it) -> {
      it.getMultiValuedNonContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result
            = this.<Root>recordObject(this.getUniquePersistedRoot(), function2);
    AtomicEChangeAssertHelper.assertEmpty(
            AtomicEChangeAssertHelper.assertInsertEReference(
                    AtomicEChangeAssertHelper.assertRemoveEReference(
                            ChangeDescription2ChangeTransformationTest
                                    .<EObject>assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(), AllElementTypesPackage
                                    .Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE,
                            nonRoot2, 1, false),
                    this.getUniquePersistedRoot(), AllElementTypesPackage
                            .Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE,
                    nonRoot2, 0, false, false));
  }

  @Test
  void moveMultiValuedUnsettableNonContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<NonRoot> multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedContainmentEReference1 = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedContainmentEReference2 = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference2.add(nonRoot3);
    };
    function.accept(uniquePersistedRoot);
    Root uniquePersistedRoot1 = this.getUniquePersistedRoot();
    final Consumer<Root> function1 = (Root it) -> {
      EList<NonRoot> multiValuedUnsettableNonContainmentEReference
              = it.getMultiValuedUnsettableNonContainmentEReference();
      multiValuedUnsettableNonContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedUnsettableNonContainmentEReference1
              = it.getMultiValuedUnsettableNonContainmentEReference();
      multiValuedUnsettableNonContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedUnsettableNonContainmentEReference2
              = it.getMultiValuedUnsettableNonContainmentEReference();
      multiValuedUnsettableNonContainmentEReference2.add(nonRoot3);
    };
    function1.accept(uniquePersistedRoot1);
    final Consumer<Root> function2 = (Root it) -> {
      it.getMultiValuedUnsettableNonContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result
            = this.<Root>recordObject(this.getUniquePersistedRoot(), function2);
    AtomicEChangeAssertHelper.assertEmpty(
            AtomicEChangeAssertHelper.assertInsertEReference(
                    AtomicEChangeAssertHelper.assertRemoveEReference(
                            ChangeDescription2ChangeTransformationTest
                                    .<EObject>assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage
                                    .Literals
                                    .ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE,
                            nonRoot2, 1, false),
                    this.getUniquePersistedRoot(), AllElementTypesPackage
                            .Literals
                            .ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE,
                    nonRoot2, 0, false, false));
  }

  @Test
  void moveMultiValuedUnorderedNonContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<NonRoot> multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedContainmentEReference1 = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedContainmentEReference2 = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference2.add(nonRoot3);
    };
    function.accept(uniquePersistedRoot);
    Root uniquePersistedRoot1 = this.getUniquePersistedRoot();
    final Consumer<Root> function1 = (Root it) -> {
      EList<NonRoot> multiValuedUnorderedNonContainmentEReference
              = it.getMultiValuedUnorderedNonContainmentEReference();
      multiValuedUnorderedNonContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedUnorderedNonContainmentEReference1
              = it.getMultiValuedUnorderedNonContainmentEReference();
      multiValuedUnorderedNonContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedUnorderedNonContainmentEReference2
              = it.getMultiValuedUnorderedNonContainmentEReference();
      multiValuedUnorderedNonContainmentEReference2.add(nonRoot3);
    };
    function1.accept(uniquePersistedRoot1);
    final Consumer<Root> function2 = (Root it) -> {
      it.getMultiValuedUnorderedNonContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result
            = this.<Root>recordObject(this.getUniquePersistedRoot(), function2);
    AtomicEChangeAssertHelper.assertEmpty(
            AtomicEChangeAssertHelper.assertInsertEReference(
                    AtomicEChangeAssertHelper.assertRemoveEReference(
                            ChangeDescription2ChangeTransformationTest
                                    .<EObject>assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage
                                    .Literals
                                    .ROOT__MULTI_VALUED_UNORDERED_NON_CONTAINMENT_EREFERENCE,
                            nonRoot2, 1, false),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage
                            .Literals
                            .ROOT__MULTI_VALUED_UNORDERED_NON_CONTAINMENT_EREFERENCE,
                    nonRoot2, 0, false, false));
  }

  @Test
  void moveMultiValuedContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<NonRoot> multiValuedContainmentEReference
              = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedContainmentEReference1
              = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedContainmentEReference2
              = it.getMultiValuedContainmentEReference();
      multiValuedContainmentEReference2.add(nonRoot3);
    };
    function.accept(uniquePersistedRoot);
    final Consumer<Root> function1 = (Root it) -> {
      it.getMultiValuedContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result
            = this.<Root>recordObject(this.getUniquePersistedRoot(), function1);
    AtomicEChangeAssertHelper.assertEmpty(
            AtomicEChangeAssertHelper.assertInsertEReference(
                    AtomicEChangeAssertHelper.assertRemoveEReference(
                            ChangeDescription2ChangeTransformationTest
                                    .<EObject>assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage
                                    .Literals
                                    .ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE,
                            nonRoot2, 1, true),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage
                            .Literals
                            .ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE,
                    nonRoot2, 0, true, false));
  }

  @Test
  void moveMultiValuedUnsettableContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<NonRoot> multiValuedUnsettableContainmentEReference
              = it.getMultiValuedUnsettableContainmentEReference();
      multiValuedUnsettableContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedUnsettableContainmentEReference1
              = it.getMultiValuedUnsettableContainmentEReference();
      multiValuedUnsettableContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedUnsettableContainmentEReference2
              = it.getMultiValuedUnsettableContainmentEReference();
      multiValuedUnsettableContainmentEReference2.add(nonRoot3);
    };
    function.accept(uniquePersistedRoot);
    final Consumer<Root> function1 = (Root it) -> {
      it.getMultiValuedUnsettableContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result
            = this.<Root>recordObject(this.getUniquePersistedRoot(), function1);
    AtomicEChangeAssertHelper.assertEmpty(
            AtomicEChangeAssertHelper.assertInsertEReference(
                    AtomicEChangeAssertHelper.assertRemoveEReference(
                            ChangeDescription2ChangeTransformationTest
                                    .<EObject>assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage
                                    .Literals
                                    .ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE,
                            nonRoot2, 1, true),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage
                            .Literals
                            .ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE,
                    nonRoot2, 0, true, false));
  }

  @Test
  void moveMultiValuedUnorderedContainmentEReference() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root uniquePersistedRoot = this.getUniquePersistedRoot();
    final Consumer<Root> function = (Root it) -> {
      EList<NonRoot> multiValuedUnorderedContainmentEReference
              = it.getMultiValuedUnorderedContainmentEReference();
      multiValuedUnorderedContainmentEReference.add(nonRoot1);
      EList<NonRoot> multiValuedUnorderedContainmentEReference1
              = it.getMultiValuedUnorderedContainmentEReference();
      multiValuedUnorderedContainmentEReference1.add(nonRoot2);
      EList<NonRoot> multiValuedUnorderedContainmentEReference2
              = it.getMultiValuedUnorderedContainmentEReference();
      multiValuedUnorderedContainmentEReference2.add(nonRoot3);
    };
    function.accept(uniquePersistedRoot);
    final Consumer<Root> function1 = (Root it) -> {
      it.getMultiValuedUnorderedContainmentEReference().move(0, 1);
    };
    final List<EChange<EObject>> result
            = this.<Root>recordObject(this.getUniquePersistedRoot(), function1);
    AtomicEChangeAssertHelper.assertEmpty(
            AtomicEChangeAssertHelper.assertInsertEReference(
                    AtomicEChangeAssertHelper.assertRemoveEReference(
                            ChangeDescription2ChangeTransformationTest
                                    .<EObject>assertChangeCount(result, 2),
                            this.getUniquePersistedRoot(),
                            AllElementTypesPackage
                                    .Literals
                                    .ROOT__MULTI_VALUED_UNORDERED_CONTAINMENT_EREFERENCE,
                            nonRoot2, 1, true),
                    this.getUniquePersistedRoot(),
                    AllElementTypesPackage
                            .Literals
                            .ROOT__MULTI_VALUED_UNORDERED_CONTAINMENT_EREFERENCE,
                    nonRoot2, 0, true, false));
  }
}
