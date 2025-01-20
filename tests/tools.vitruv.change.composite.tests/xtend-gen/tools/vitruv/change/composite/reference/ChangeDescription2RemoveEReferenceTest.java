package tools.vitruv.change.composite.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.change.composite.util.ChangeAssertHelper;
import tools.vitruv.change.composite.util.CompoundEChangeAssertHelper;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@SuppressWarnings("all")
public class ChangeDescription2RemoveEReferenceTest extends ChangeDescription2ChangeTransformationTest {
  @Test
  public void testRemoveNonContainmentEReferenceFromList() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
      _multiValuedNonContainmentEReference.add(nonRoot);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
      _multiValuedNonContainmentEReference.remove(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, 0, false));
  }

  @Test
  public void testRemoveNonContainmentEReferenceFromListWithExplicitUnset() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      EList<NonRoot> _multiValuedUnsettableNonContainmentEReference = it.getMultiValuedUnsettableNonContainmentEReference();
      _multiValuedUnsettableNonContainmentEReference.add(nonRoot);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertUnsetFeature(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE, nonRoot, 0, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE));
  }

  @Test
  public void testRemoveContainmentEReferenceFromList() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRoot);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.remove(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertDeleteEObject(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 2), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot, 0, true), nonRoot));
  }

  @Test
  public void testRemoveContainmentEReferenceFromListWithExplicitUnset() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
      EList<NonRoot> _multiValuedUnsettableContainmentEReference = it.getMultiValuedUnsettableContainmentEReference();
      _multiValuedUnsettableContainmentEReference.add(nonRoot);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.eUnset(AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertDeleteEObject(AtomicEChangeAssertHelper.assertUnsetFeature(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE, nonRoot, 0, true), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE), nonRoot));
  }

  @Test
  public void testRemoveElementAndReinsertContainedOne() {
    final Root containedRoot = AllElementTypesCreators.aet.Root();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setSingleValuedContainmentEReference(nonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(containedRoot, _function_1);
      it.setRecursiveRoot(_doubleArrow);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setRecursiveRoot(null);
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(CompoundEChangeAssertHelper.assertDeleteEObjectAndContainedElements(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 4), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__RECURSIVE_ROOT, containedRoot, null, true, false, false), containedRoot, AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, null, true, false, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, null, nonRoot, true, false, false), containedRoot));
  }

  @Test
  public void testRemoveElementAndReinsertContainedInContainedOne() {
    final Root containedRoot = AllElementTypesCreators.aet.Root();
    final Root innerContainedRoot = AllElementTypesCreators.aet.Root();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        final Procedure1<Root> _function_2 = (Root it_2) -> {
          it_2.setSingleValuedContainmentEReference(nonRoot);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(innerContainedRoot, _function_2);
        it_1.setRecursiveRoot(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(containedRoot, _function_1);
      it.setRecursiveRoot(_doubleArrow);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.setRecursiveRoot(null);
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(CompoundEChangeAssertHelper.assertDeleteEObjectAndContainedElements(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 5), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__RECURSIVE_ROOT, containedRoot, null, true, false, false), innerContainedRoot, AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, null, true, false, false), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, null, nonRoot, true, false, false), containedRoot));
  }

  @Test
  public void testClearEReferences() {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRoot1);
      EList<NonRoot> _multiValuedContainmentEReference_1 = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference_1.add(nonRoot2);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedContainmentEReference().clear();
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    final Iterable<? extends EChange<EObject>> deleteChanges = ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(AtomicEChangeAssertHelper.assertRemoveEReference(AtomicEChangeAssertHelper.assertRemoveEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 4), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot2, 1, true), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot1, 0, true), 2);
    final Consumer<EChange<EObject>> _function_2 = (EChange<EObject> it) -> {
      ChangeAssertHelper.<DeleteEObject>assertType(it, DeleteEObject.class);
    };
    deleteChanges.forEach(_function_2);
    final Function1<EChange<EObject>, Object> _function_3 = (EChange<EObject> it) -> {
      return ((DeleteEObject<?>) it).getAffectedElement();
    };
    final Iterable<Object> deletedObjects = IterableExtensions.map(deleteChanges, _function_3);
    Assertions.assertTrue(IterableExtensions.contains(deletedObjects, nonRoot1), ("deleted objects are missing " + nonRoot1));
    Assertions.assertTrue(IterableExtensions.contains(deletedObjects, nonRoot2), ("deleted objects are missing " + nonRoot2));
  }
}
