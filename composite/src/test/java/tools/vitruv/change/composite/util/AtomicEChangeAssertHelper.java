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

/**
 * Test assertion helpers for atomic {@link EChange}s.
 *
 * <p>Most methods check that the <em>first</em> change of the given sequence has
 * the expected type and properties, and return the remaining changes. This allows
 * a sequence of recorded changes to be checked step by step by chaining calls.
 */
public final class AtomicEChangeAssertHelper {

  /**
   * Asserts that the given existence change affects the expected object.
   *
   * @param change the create or delete change to check
   * @param affectedEObject the expected affected object
   */
  public static void assertEObjectExistenceChange(final EObjectExistenceEChange<EObject> change,
      final EObject affectedEObject) {
    ChangeAssertHelper.assertAffectedEObject(change, affectedEObject);
  }

  /**
   * Asserts that the first change is a {@link CreateEObject} for the expected object.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected created object
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertCreateEObject(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final CreateEObject createObject = ChangeAssertHelper
        .<CreateEObject>assertType(changes.iterator().next(), CreateEObject.class);
    AtomicEChangeAssertHelper.assertEObjectExistenceChange(createObject, affectedEObject);
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is a {@link DeleteEObject} for the expected object.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected deleted object
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertDeleteEObject(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final DeleteEObject deleteObject = ChangeAssertHelper
        .<DeleteEObject>assertType(changes.iterator().next(), DeleteEObject.class);
    AtomicEChangeAssertHelper.assertEObjectExistenceChange(deleteObject, affectedEObject);
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the given root change refers to the expected URI and resource.
   *
   * @param change the root change to check
   * @param uri the expected URI
   * @param resource the expected resource
   */
  private static void assertRootEChange(final RootEChange<EObject> change,
                                        final String uri,
                                        final Resource resource) {
    ChangeAssertHelper.assertUri(change, uri);
    ChangeAssertHelper.assertResource(change, resource);
  }

  /**
   * Asserts that the first change is an {@link InsertRootEObject} with the expected
   * new value, URI and resource.
   *
   * @param changes the changes to check
   * @param expectedNewValue the expected inserted root object
   * @param uri the expected URI of the resource
   * @param resource the expected resource the root is inserted into
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertInsertRootEObject(
      final Iterable<? extends EChange<EObject>> changes,
      final Object expectedNewValue, final String uri, final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertRootEObject insertRoot = ChangeAssertHelper.<InsertRootEObject>assertType(
        changes.iterator().next(), InsertRootEObject.class);
    ChangeAssertHelper.assertNewValue(insertRoot, expectedNewValue);
    AtomicEChangeAssertHelper.assertRootEChange(insertRoot, uri, resource);
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is a {@link RemoveRootEObject} with the expected
   * old value, URI and resource.
   *
   * @param changes the changes to check
   * @param expectedOldValue the expected removed root object
   * @param uri the expected URI of the resource
   * @param resource the expected resource the root is removed from
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertRemoveRootEObject(
      final Iterable<? extends EChange<EObject>> changes,
      final Object expectedOldValue,
      final String uri,
      final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveRootEObject removeRoot = ChangeAssertHelper.assertType(
        changes.iterator().next(), RemoveRootEObject.class);
    ChangeAssertHelper.assertOldValue(removeRoot, expectedOldValue);
    AtomicEChangeAssertHelper.assertRootEChange(removeRoot, uri, resource);
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is a {@link ReplaceSingleValuedEAttribute} with
   * the expected properties.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected affected attribute
   * @param expectedOldValue the expected value before the change
   * @param expectedNewValue the expected value after the change
   * @param wasUnset whether the attribute is expected to have been unset before the change
   * @param isUnset whether the attribute is expected to be unset after the change
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertReplaceSingleValuedEAttribute(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final Object expectedOldValue,
      final Object expectedNewValue,
      final boolean wasUnset,
      final boolean isUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final ReplaceSingleValuedEAttribute removeEAttributeValue = ChangeAssertHelper
        .assertType(changes.iterator().next(),
            ReplaceSingleValuedEAttribute.class);
    ChangeAssertHelper.assertAffectedEObject(removeEAttributeValue, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(removeEAttributeValue, affectedFeature);
    ChangeAssertHelper.assertOldValue(removeEAttributeValue, expectedOldValue);
    ChangeAssertHelper.assertNewValue(removeEAttributeValue, expectedNewValue);
    Assertions.assertEquals(wasUnset, removeEAttributeValue.isWasUnset());
    Assertions.assertEquals(isUnset, removeEAttributeValue.isIsUnset());
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is an {@link InsertEAttributeValue} with the
   * expected properties.
   *
   * @param changes the changes to check
   * @param affectedFeature the expected affected attribute
   * @param expectedNewValue the expected inserted value
   * @param expectedIndex the expected index the value is inserted at
   * @param wasUnset whether the attribute is expected to have been unset before the change
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertInsertEAttribute(
      final Iterable<? extends EChange<EObject>> changes,
      final EStructuralFeature affectedFeature,
      final Object expectedNewValue,
      final int expectedIndex,
      final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertEAttributeValue<EObject, ?> insertEAttributValue = ChangeAssertHelper.assertType(
        changes.iterator().next(), InsertEAttributeValue.class);
    ChangeAssertHelper.assertAffectedEObject(insertEAttributValue,
            insertEAttributValue.getAffectedElement());
    ChangeAssertHelper.assertNewValue(insertEAttributValue, expectedNewValue);
    ChangeAssertHelper.assertIndex(insertEAttributValue, expectedIndex);
    ChangeAssertHelper.assertAffectedEFeature(insertEAttributValue, affectedFeature);
    Assertions.assertEquals(wasUnset, insertEAttributValue.isWasUnset());
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is a {@link RemoveEAttributeValue} with the
   * expected properties.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected affected attribute
   * @param expectedOldValue the expected removed value
   * @param expectedOldIndex the expected index the value is removed from
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertRemoveEAttribute(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final Object expectedOldValue,
      final int expectedOldIndex) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveEAttributeValue removeEAttributeValue = ChangeAssertHelper.assertType(
        changes.iterator().next(), RemoveEAttributeValue.class);
    ChangeAssertHelper.assertAffectedEObject(removeEAttributeValue, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(removeEAttributeValue, affectedFeature);
    ChangeAssertHelper.assertOldValue(removeEAttributeValue, expectedOldValue);
    ChangeAssertHelper.assertIndex(removeEAttributeValue, expectedOldIndex);
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is a {@link ReplaceSingleValuedEReference} with
   * the expected properties.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected affected reference
   * @param expectedOldValue the expected referenced value before the change
   * @param expectedNewValue the expected referenced value after the change
   * @param isContainment whether the reference is expected to be a containment
   * @param wasUnset whether the reference is expected to have been unset before the change
   * @param isUnset whether the reference is expected to be unset after the change
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertReplaceSingleValuedEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final Object expectedOldValue,
      final Object expectedNewValue,
      final boolean isContainment,
      final boolean wasUnset,
      final boolean isUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper
            .assertType(
        changes.iterator().next(), ReplaceSingleValuedEReference.class);
    ChangeAssertHelper.<AdditiveEChange<?, ?>>assertOldAndNewValue(replaceChange, expectedOldValue,
        expectedNewValue);
    ChangeAssertHelper.assertAffectedEFeature(replaceChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(replaceChange, affectedEObject);
    ChangeAssertHelper.assertContainment(replaceChange, isContainment);
    Assertions.assertEquals(wasUnset, replaceChange.isWasUnset());
    Assertions.assertEquals(isUnset, replaceChange.isIsUnset());
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the next changes set a single-valued reference from its default
   * value to the expected new value.
   *
   * <p>If the reference is a containment and the value is newly created, this
   * expects a create change followed by a replace change. Otherwise it expects a
   * single {@link ReplaceSingleValuedEReference}.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected affected reference
   * @param expectedNewValue the expected referenced value after the change
   * @param isContainment whether the reference is expected to be a containment
   * @param isCreate whether the new value is expected to be newly created
   * @param wasUnset whether the reference is expected to have been unset before the change
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertSetSingleValuedEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final EObject expectedNewValue,
      final boolean isContainment,
      final boolean isCreate,
      final boolean wasUnset) {
    if ((isContainment && isCreate)) {
      return CompoundEChangeAssertHelper.assertCreateAndReplaceNonRoot(changes, expectedNewValue,
              affectedEObject, affectedFeature, wasUnset);
    } else {
      ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
      final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper.assertType(
          changes.iterator().next(), ReplaceSingleValuedEReference.class);
      AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, affectedEObject,
              affectedFeature, null, expectedNewValue,
              isContainment, wasUnset, false);
      Assertions.assertFalse(replaceChange.isFromNonDefaultValue());
      Assertions.assertTrue(replaceChange.isToNonDefaultValue());
      return Iterables.skip(changes, 1);
    }
  }

  /**
   * Asserts that the next changes reset a single-valued reference from the
   * expected old value to its default value.
   *
   * <p>If the reference is a containment and the old value is deleted, this
   * expects a replace change followed by a delete change. Otherwise it expects a
   * single {@link ReplaceSingleValuedEReference}.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected affected reference
   * @param expectedOldValue the expected referenced value before the change
   * @param isContainment whether the reference is expected to be a containment
   * @param isDelete whether the old value is expected to be deleted
   * @param isUnset whether the reference is expected to be unset after the change
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertUnsetSingleValuedEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final EObject expectedOldValue,
      final boolean isContainment,
      final boolean isDelete,
      final boolean isUnset) {
    if ((isContainment && isDelete)) {
      return CompoundEChangeAssertHelper.assertReplaceAndDeleteNonRoot(changes,
              expectedOldValue, affectedEObject,
          affectedFeature, isUnset);
    } else {
      ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
      final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper
              .assertType(
          changes.iterator().next(), ReplaceSingleValuedEReference.class);
      AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, affectedEObject,
              affectedFeature, expectedOldValue,
              null, isContainment, false, isUnset);
      Assertions.assertTrue(replaceChange.isFromNonDefaultValue());
      Assertions.assertFalse(replaceChange.isToNonDefaultValue());
      return Iterables.skip(changes, 1);
    }
  }

  /**
   * Asserts that the first change is an {@link InsertEReference} with the expected
   * properties.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected affected reference
   * @param expectedNewValue the expected inserted object
   * @param expectedIndex the expected index the object is inserted at
   * @param isContainment whether the reference is expected to be a containment
   * @param wasUnset whether the reference is expected to have been unset before the change
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertInsertEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final EObject expectedNewValue,
      final int expectedIndex,
      final boolean isContainment,
      final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertEReference insertEReference = ChangeAssertHelper
            .assertType(
        changes.iterator().next(), InsertEReference.class);
    ChangeAssertHelper.assertAffectedEObject(insertEReference, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(insertEReference, affectedFeature);
    ChangeAssertHelper.assertNewValue(insertEReference, expectedNewValue);
    ChangeAssertHelper.assertIndex(insertEReference, expectedIndex);
    ChangeAssertHelper.assertContainment(insertEReference, isContainment);
    Assertions.assertEquals(wasUnset, insertEReference.isWasUnset());
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is a {@link RemoveEReference} with the expected
   * properties.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected affected reference
   * @param expectedOldValue the expected removed object
   * @param expectedOldIndex the expected index the object is removed from
   * @param isContainment whether the reference is expected to be a containment
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertRemoveEReference(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final EObject expectedOldValue,
      final int expectedOldIndex,
      final boolean isContainment) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveEReference subtractiveChange = ChangeAssertHelper.assertType(
        changes.iterator().next(), RemoveEReference.class);
    ChangeAssertHelper.assertAffectedEFeature(subtractiveChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(subtractiveChange, affectedEObject);
    ChangeAssertHelper.assertOldValue(subtractiveChange, expectedOldValue);
    ChangeAssertHelper.assertIndex(subtractiveChange, expectedOldIndex);
    ChangeAssertHelper.assertContainment(subtractiveChange, isContainment);
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the first change is an {@link UnsetFeature} for the expected
   * object and feature.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected affected object
   * @param affectedFeature the expected unset feature
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertUnsetFeature(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final UnsetFeature unsetChange = ChangeAssertHelper
        .assertType(changes.iterator().next(), UnsetFeature.class);
    ChangeAssertHelper.assertAffectedEFeature(unsetChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(unsetChange, affectedEObject);
    return Iterables.skip(changes, 1);
  }

  /**
   * Asserts that the next changes insert the given root object into the resource.
   *
   * <p>If {@code isCreate} is {@code true}, this expects a create change followed
   * by an insert-root change. Otherwise it expects a single insert-root change.
   *
   * @param changes the changes to check
   * @param rootElement the expected inserted root object
   * @param isCreate whether the root object is expected to be newly created
   * @param resource the expected resource the root is inserted into
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertInsertRoot(
      final Iterable<? extends EChange<EObject>> changes,

      final EObject rootElement,

      final boolean isCreate,
      final Resource resource) {
    if (isCreate) {
      return CompoundEChangeAssertHelper.assertCreateAndInsertRootEObject(changes, rootElement,
          resource.getURI().toFileString(), resource);
    } else {
      return AtomicEChangeAssertHelper.assertInsertRootEObject(changes, rootElement,
              resource.getURI().toFileString(), resource);
    }
  }

  /**
   * Asserts that the next changes remove the given root object from the resource
   * with the given URI.
   *
   * <p>If {@code isDelete} is {@code true}, this expects a remove-root change
   * followed by a delete change. Otherwise it expects a single remove-root change.
   *
   * @param changes the changes to check
   * @param rootElement the expected removed root object
   * @param isDelete whether the root object is expected to be deleted
   * @param resource the expected resource the root is removed from
   * @param uri the expected URI of the resource
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertRemoveRoot(
      final Iterable<? extends EChange<EObject>> changes,
      final EObject rootElement,
      final boolean isDelete,
      final Resource resource, final URI uri) {
    Iterable<? extends EChange<EObject>> xifexpression;
    if (isDelete) {
      xifexpression = CompoundEChangeAssertHelper.assertRemoveAndDeleteRootEObject(changes,
              rootElement, uri.toFileString(), resource);
    } else {
      xifexpression = AtomicEChangeAssertHelper.assertRemoveRootEObject(changes, rootElement,
              uri.toFileString(), resource);
    }
    return xifexpression;
  }

  /**
   * Asserts that the next changes remove the given root object from the resource,
   * using the resource's own URI.
   *
   * @param changes the changes to check
   * @param rootElement the expected removed root object
   * @param isDelete whether the root object is expected to be deleted
   * @param resource the expected resource the root is removed from
   * @return the remaining changes after the checked ones
   * @see #assertRemoveRoot(Iterable, EObject, boolean, Resource, URI)
   */
  public static Iterable<? extends EChange<EObject>> assertRemoveRoot(
      final Iterable<? extends EChange<EObject>> changes, final EObject rootElement,
      final boolean isDelete,
      final Resource resource) {
    return AtomicEChangeAssertHelper.assertRemoveRoot(changes, rootElement, isDelete,
            resource, resource.getURI());
  }

  /**
   * Asserts that there are no changes left.
   *
   * @param changes the changes to check
   */
  public static void assertEmpty(final Iterable<? extends EChange<?>> changes) {
    Assertions.assertEquals(0, Iterables.size(changes));
  }

  private AtomicEChangeAssertHelper() {

  }
}
