package tools.vitruv.change.composite.util;

import com.google.common.collect.Iterables;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;
import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootEChange;

public final class AtomicEChangeAssertHelper {
  public static void assertEObjectExistenceChange(final EObjectExistenceEChange<EObject> change,
      final EObject affectedEObject) {
    ChangeAssertHelper.assertAffectedEObject(change, affectedEObject);
  }

  public static Iterable<? extends EChange<EObject>> assertCreateEObject(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final CreateEObject createObject = ChangeAssertHelper
        .<CreateEObject>assertType(changes.iterator().next(), CreateEObject.class);
    AtomicEChangeAssertHelper.assertEObjectExistenceChange(createObject, affectedEObject);
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertDeleteEObject(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final DeleteEObject deleteObject = ChangeAssertHelper
        .<DeleteEObject>assertType(changes.iterator().next(), DeleteEObject.class);
    AtomicEChangeAssertHelper.assertEObjectExistenceChange(deleteObject, affectedEObject);
    return Iterables.skip(changes, 1);
  }

  private static void assertRootEChange(final RootEChange<EObject> change, final String uri, final Resource resource) {
    ChangeAssertHelper.assertUri(change, uri);
    ChangeAssertHelper.assertResource(change, resource);
  }

  public static Iterable<? extends EChange<EObject>> assertInsertRootEObject(
      final Iterable<? extends EChange<EObject>> changes, final Object expectedNewValue, final String uri,
      final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertRootEObject insertRopot = ChangeAssertHelper.<InsertRootEObject>assertType(
        changes.iterator().next(), InsertRootEObject.class);
    ChangeAssertHelper.assertNewValue(insertRopot, expectedNewValue);
    AtomicEChangeAssertHelper.assertRootEChange(insertRopot, uri, resource);
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveRootEObject(
      final Iterable<? extends EChange<EObject>> changes, final Object expectedOldValue, final String uri,
      final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveRootEObject removeRoot = ChangeAssertHelper.<RemoveRootEObject>assertType(
        changes.iterator().next(), RemoveRootEObject.class);
    ChangeAssertHelper.assertOldValue(removeRoot, expectedOldValue);
    AtomicEChangeAssertHelper.assertRootEChange(removeRoot, uri, resource);
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertReplaceSingleValuedEAttribute(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final Object expectedOldValue, final Object expectedNewValue,
      final boolean wasUnset, final boolean isUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final ReplaceSingleValuedEAttribute removeEAttributeValue = ChangeAssertHelper
        .<ReplaceSingleValuedEAttribute>assertType(changes.iterator().next(),
            ReplaceSingleValuedEAttribute.class);
    ChangeAssertHelper.assertAffectedEObject(removeEAttributeValue, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(removeEAttributeValue, affectedFeature);
    ChangeAssertHelper.assertOldValue(removeEAttributeValue, expectedOldValue);
    ChangeAssertHelper.assertNewValue(removeEAttributeValue, expectedNewValue);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(removeEAttributeValue.isWasUnset()));
    Assertions.assertEquals(Boolean.valueOf(isUnset), Boolean.valueOf(removeEAttributeValue.isIsUnset()));
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertInsertEAttribute(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final Object expectedNewValue, final int expectedIndex,
      final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertEAttributeValue<EObject, ?> insertEAttributValue = ChangeAssertHelper.<InsertEAttributeValue>assertType(
        changes.iterator().next(), InsertEAttributeValue.class);
    ChangeAssertHelper.assertAffectedEObject(insertEAttributValue, insertEAttributValue.getAffectedElement());
    ChangeAssertHelper.assertNewValue(insertEAttributValue, expectedNewValue);
    ChangeAssertHelper.assertIndex(insertEAttributValue, expectedIndex);
    ChangeAssertHelper.assertAffectedEFeature(insertEAttributValue, affectedFeature);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(insertEAttributValue.isWasUnset()));
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveEAttribute(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final Object expectedOldValue, final int expectedOldIndex) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveEAttributeValue removeEAttributeValue = ChangeAssertHelper.<RemoveEAttributeValue>assertType(
        changes.iterator().next(), RemoveEAttributeValue.class);
    ChangeAssertHelper.assertAffectedEObject(removeEAttributeValue, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(removeEAttributeValue, affectedFeature);
    ChangeAssertHelper.assertOldValue(removeEAttributeValue, expectedOldValue);
    ChangeAssertHelper.assertIndex(removeEAttributeValue, expectedOldIndex);
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertReplaceSingleValuedEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final Object expectedOldValue, final Object expectedNewValue,
      final boolean isContainment, final boolean wasUnset, final boolean isUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper.<ReplaceSingleValuedEReference>assertType(
        changes.iterator().next(), ReplaceSingleValuedEReference.class);
    ChangeAssertHelper.<AdditiveEChange<?, ?>, Object>assertOldAndNewValue(replaceChange, expectedOldValue,
        expectedNewValue);
    ChangeAssertHelper.assertAffectedEFeature(replaceChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(replaceChange, affectedEObject);
    ChangeAssertHelper.assertContainment(replaceChange, isContainment);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(replaceChange.isWasUnset()));
    Assertions.assertEquals(Boolean.valueOf(isUnset), Boolean.valueOf(replaceChange.isIsUnset()));
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertSetSingleValuedEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final EObject expectedNewValue, final boolean isContainment,
      final boolean isCreate, final boolean wasUnset) {
    if ((isContainment && isCreate)) {
      return CompoundEChangeAssertHelper.assertCreateAndReplaceNonRoot(changes, expectedNewValue, affectedEObject,
          affectedFeature, wasUnset);
    } else {
      ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
      final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper.<ReplaceSingleValuedEReference>assertType(
          changes.iterator().next(), ReplaceSingleValuedEReference.class);
      AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, affectedEObject, affectedFeature, null,
          expectedNewValue, isContainment, wasUnset, false);
      Assertions.assertFalse(replaceChange.isFromNonDefaultValue());
      Assertions.assertTrue(replaceChange.isToNonDefaultValue());
      return Iterables.skip(changes, 1);
    }
  }

  public static Iterable<? extends EChange<EObject>> assertUnsetSingleValuedEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final EObject expectedOldValue, final boolean isContainment,
      final boolean isDelete, final boolean isUnset) {
    if ((isContainment && isDelete)) {
      return CompoundEChangeAssertHelper.assertReplaceAndDeleteNonRoot(changes, expectedOldValue, affectedEObject,
          affectedFeature, isUnset);
    } else {
      ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
      final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper.<ReplaceSingleValuedEReference>assertType(
          changes.iterator().next(), ReplaceSingleValuedEReference.class);
      AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, affectedEObject, affectedFeature,
          expectedOldValue, null, isContainment, false, isUnset);
      Assertions.assertTrue(replaceChange.isFromNonDefaultValue());
      Assertions.assertFalse(replaceChange.isToNonDefaultValue());
      return Iterables.skip(changes, 1);
    }
  }

  public static Iterable<? extends EChange<EObject>> assertInsertEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final EObject expectedNewValue, final int expectedIndex,
      final boolean isContainment, final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertEReference insertEReference = ChangeAssertHelper.<InsertEReference>assertType(
        changes.iterator().next(), InsertEReference.class);
    ChangeAssertHelper.assertAffectedEObject(insertEReference, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(insertEReference, affectedFeature);
    ChangeAssertHelper.assertNewValue(insertEReference, expectedNewValue);
    ChangeAssertHelper.assertIndex(insertEReference, expectedIndex);
    ChangeAssertHelper.assertContainment(insertEReference, isContainment);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(insertEReference.isWasUnset()));
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature, final EObject expectedOldValue, final int expectedOldIndex,
      final boolean isContainment) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveEReference subtractiveChange = ChangeAssertHelper.<RemoveEReference>assertType(
        changes.iterator().next(), RemoveEReference.class);
    ChangeAssertHelper.assertAffectedEFeature(subtractiveChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(subtractiveChange, affectedEObject);
    ChangeAssertHelper.assertOldValue(subtractiveChange, expectedOldValue);
    ChangeAssertHelper.assertIndex(subtractiveChange, expectedOldIndex);
    ChangeAssertHelper.assertContainment(subtractiveChange, isContainment);
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertUnsetFeature(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final UnsetFeature unsetChange = ChangeAssertHelper
        .<UnsetFeature>assertType(changes.iterator().next(), UnsetFeature.class);
    ChangeAssertHelper.assertAffectedEFeature(unsetChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(unsetChange, affectedEObject);
    return Iterables.skip(changes, 1);
  }

  public static Iterable<? extends EChange<EObject>> assertInsertRoot(
      final Iterable<? extends EChange<EObject>> changes, final EObject rootElement, final boolean isCreate,
      final Resource resource) {
    if (isCreate) {
      return CompoundEChangeAssertHelper.assertCreateAndInsertRootEObject(changes, rootElement,
          resource.getURI().toFileString(), resource);
    } else {
      return AtomicEChangeAssertHelper.assertInsertRootEObject(changes, rootElement, resource.getURI().toFileString(),
          resource);
    }
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveRoot(
      final Iterable<? extends EChange<EObject>> changes, final EObject rootElement, final boolean isDelete,
      final Resource resource, final URI uri) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    if (isDelete) {
      _xifexpression = CompoundEChangeAssertHelper.assertRemoveAndDeleteRootEObject(changes, rootElement,
          uri.toFileString(), resource);
    } else {
      _xifexpression = AtomicEChangeAssertHelper.assertRemoveRootEObject(changes, rootElement, uri.toFileString(),
          resource);
    }
    return _xifexpression;
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveRoot(
      final Iterable<? extends EChange<EObject>> changes, final EObject rootElement, final boolean isDelete,
      final Resource resource) {
    return AtomicEChangeAssertHelper.assertRemoveRoot(changes, rootElement, isDelete, resource, resource.getURI());
  }

  public static void assertEmpty(final Iterable<? extends EChange<?>> changes) {
    Assertions.assertEquals(0, Iterables.size(changes));
  }

  private AtomicEChangeAssertHelper() {

  }
}
