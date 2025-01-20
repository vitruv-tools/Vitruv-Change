package tools.vitruv.change.composite.util;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
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

@Utility
@SuppressWarnings("all")
public final class AtomicEChangeAssertHelper {
  public static void assertEObjectExistenceChange(final EObjectExistenceEChange<EObject> change, final EObject affectedEObject) {
    ChangeAssertHelper.assertAffectedEObject(change, affectedEObject);
  }

  public static Iterable<? extends EChange<EObject>> assertCreateEObject(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final CreateEObject createObject = ChangeAssertHelper.<CreateEObject>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], CreateEObject.class);
    AtomicEChangeAssertHelper.assertEObjectExistenceChange(createObject, affectedEObject);
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertDeleteEObject(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final DeleteEObject deleteObject = ChangeAssertHelper.<DeleteEObject>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], DeleteEObject.class);
    AtomicEChangeAssertHelper.assertEObjectExistenceChange(deleteObject, affectedEObject);
    return IterableExtensions.tail(changes);
  }

  private static void assertRootEChange(final RootEChange<EObject> change, final String uri, final Resource resource) {
    ChangeAssertHelper.assertUri(change, uri);
    ChangeAssertHelper.assertResource(change, resource);
  }

  public static Iterable<? extends EChange<EObject>> assertInsertRootEObject(final Iterable<? extends EChange<EObject>> changes, final Object expectedNewValue, final String uri, final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertRootEObject insertRopot = ChangeAssertHelper.<InsertRootEObject>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], InsertRootEObject.class);
    ChangeAssertHelper.assertNewValue(insertRopot, expectedNewValue);
    AtomicEChangeAssertHelper.assertRootEChange(insertRopot, uri, resource);
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveRootEObject(final Iterable<? extends EChange<EObject>> changes, final Object expectedOldValue, final String uri, final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveRootEObject removeRoot = ChangeAssertHelper.<RemoveRootEObject>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], RemoveRootEObject.class);
    ChangeAssertHelper.assertOldValue(removeRoot, expectedOldValue);
    AtomicEChangeAssertHelper.assertRootEChange(removeRoot, uri, resource);
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertReplaceSingleValuedEAttribute(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final Object expectedOldValue, final Object expectedNewValue, final boolean wasUnset, final boolean isUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final ReplaceSingleValuedEAttribute removeEAttributeValue = ChangeAssertHelper.<ReplaceSingleValuedEAttribute>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], ReplaceSingleValuedEAttribute.class);
    ChangeAssertHelper.assertAffectedEObject(removeEAttributeValue, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(removeEAttributeValue, affectedFeature);
    ChangeAssertHelper.assertOldValue(removeEAttributeValue, expectedOldValue);
    ChangeAssertHelper.assertNewValue(removeEAttributeValue, expectedNewValue);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(removeEAttributeValue.isWasUnset()));
    Assertions.assertEquals(Boolean.valueOf(isUnset), Boolean.valueOf(removeEAttributeValue.isIsUnset()));
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertInsertEAttribute(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final Object expectedNewValue, final int expectedIndex, final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertEAttributeValue<EObject, ?> insertEAttributValue = ChangeAssertHelper.<InsertEAttributeValue>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], InsertEAttributeValue.class);
    ChangeAssertHelper.assertAffectedEObject(insertEAttributValue, insertEAttributValue.getAffectedElement());
    ChangeAssertHelper.assertNewValue(insertEAttributValue, expectedNewValue);
    ChangeAssertHelper.assertIndex(insertEAttributValue, expectedIndex);
    ChangeAssertHelper.assertAffectedEFeature(insertEAttributValue, affectedFeature);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(insertEAttributValue.isWasUnset()));
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveEAttribute(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final Object expectedOldValue, final int expectedOldIndex) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveEAttributeValue removeEAttributeValue = ChangeAssertHelper.<RemoveEAttributeValue>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], RemoveEAttributeValue.class);
    ChangeAssertHelper.assertAffectedEObject(removeEAttributeValue, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(removeEAttributeValue, affectedFeature);
    ChangeAssertHelper.assertOldValue(removeEAttributeValue, expectedOldValue);
    ChangeAssertHelper.assertIndex(removeEAttributeValue, expectedOldIndex);
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertReplaceSingleValuedEReference(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final Object expectedOldValue, final Object expectedNewValue, final boolean isContainment, final boolean wasUnset, final boolean isUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper.<ReplaceSingleValuedEReference>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], ReplaceSingleValuedEReference.class);
    ChangeAssertHelper.<AdditiveEChange<?, ?>, Object>assertOldAndNewValue(replaceChange, expectedOldValue, expectedNewValue);
    ChangeAssertHelper.assertAffectedEFeature(replaceChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(replaceChange, affectedEObject);
    ChangeAssertHelper.assertContainment(replaceChange, isContainment);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(replaceChange.isWasUnset()));
    Assertions.assertEquals(Boolean.valueOf(isUnset), Boolean.valueOf(replaceChange.isIsUnset()));
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertSetSingleValuedEReference(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final EObject expectedNewValue, final boolean isContainment, final boolean isCreate, final boolean wasUnset) {
    if ((isContainment && isCreate)) {
      return CompoundEChangeAssertHelper.assertCreateAndReplaceNonRoot(changes, expectedNewValue, affectedEObject, affectedFeature, wasUnset);
    } else {
      ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
      final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper.<ReplaceSingleValuedEReference>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], ReplaceSingleValuedEReference.class);
      AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, affectedEObject, affectedFeature, null, expectedNewValue, isContainment, wasUnset, false);
      Assertions.assertFalse(replaceChange.isFromNonDefaultValue());
      Assertions.assertTrue(replaceChange.isToNonDefaultValue());
      return IterableExtensions.tail(changes);
    }
  }

  public static Iterable<? extends EChange<EObject>> assertUnsetSingleValuedEReference(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final EObject expectedOldValue, final boolean isContainment, final boolean isDelete, final boolean isUnset) {
    if ((isContainment && isDelete)) {
      return CompoundEChangeAssertHelper.assertReplaceAndDeleteNonRoot(changes, expectedOldValue, affectedEObject, affectedFeature, isUnset);
    } else {
      ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
      final ReplaceSingleValuedEReference replaceChange = ChangeAssertHelper.<ReplaceSingleValuedEReference>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], ReplaceSingleValuedEReference.class);
      AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, affectedEObject, affectedFeature, expectedOldValue, null, isContainment, false, isUnset);
      Assertions.assertTrue(replaceChange.isFromNonDefaultValue());
      Assertions.assertFalse(replaceChange.isToNonDefaultValue());
      return IterableExtensions.tail(changes);
    }
  }

  public static Iterable<? extends EChange<EObject>> assertInsertEReference(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final EObject expectedNewValue, final int expectedIndex, final boolean isContainment, final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final InsertEReference insertEReference = ChangeAssertHelper.<InsertEReference>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], InsertEReference.class);
    ChangeAssertHelper.assertAffectedEObject(insertEReference, affectedEObject);
    ChangeAssertHelper.assertAffectedEFeature(insertEReference, affectedFeature);
    ChangeAssertHelper.assertNewValue(insertEReference, expectedNewValue);
    ChangeAssertHelper.assertIndex(insertEReference, expectedIndex);
    ChangeAssertHelper.assertContainment(insertEReference, isContainment);
    Assertions.assertEquals(Boolean.valueOf(wasUnset), Boolean.valueOf(insertEReference.isWasUnset()));
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveEReference(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final EObject expectedOldValue, final int expectedOldIndex, final boolean isContainment) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final RemoveEReference subtractiveChange = ChangeAssertHelper.<RemoveEReference>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], RemoveEReference.class);
    ChangeAssertHelper.assertAffectedEFeature(subtractiveChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(subtractiveChange, affectedEObject);
    ChangeAssertHelper.assertOldValue(subtractiveChange, expectedOldValue);
    ChangeAssertHelper.assertIndex(subtractiveChange, expectedOldIndex);
    ChangeAssertHelper.assertContainment(subtractiveChange, isContainment);
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertUnsetFeature(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 1);
    final UnsetFeature unsetChange = ChangeAssertHelper.<UnsetFeature>assertType(((Object[])Conversions.unwrapArray(changes, Object.class))[0], UnsetFeature.class);
    ChangeAssertHelper.assertAffectedEFeature(unsetChange, affectedFeature);
    ChangeAssertHelper.assertAffectedEObject(unsetChange, affectedEObject);
    return IterableExtensions.tail(changes);
  }

  public static Iterable<? extends EChange<EObject>> assertInsertRoot(final Iterable<? extends EChange<EObject>> changes, final EObject rootElement, final boolean isCreate, final Resource resource) {
    if (isCreate) {
      return CompoundEChangeAssertHelper.assertCreateAndInsertRootEObject(changes, rootElement, resource.getURI().toFileString(), resource);
    } else {
      return AtomicEChangeAssertHelper.assertInsertRootEObject(changes, rootElement, resource.getURI().toFileString(), resource);
    }
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveRoot(final Iterable<? extends EChange<EObject>> changes, final EObject rootElement, final boolean isDelete, final Resource resource, final URI uri) {
    Iterable<? extends EChange<EObject>> _xifexpression = null;
    if (isDelete) {
      _xifexpression = CompoundEChangeAssertHelper.assertRemoveAndDeleteRootEObject(changes, rootElement, uri.toFileString(), resource);
    } else {
      _xifexpression = AtomicEChangeAssertHelper.assertRemoveRootEObject(changes, rootElement, uri.toFileString(), resource);
    }
    return _xifexpression;
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveRoot(final Iterable<? extends EChange<EObject>> changes, final EObject rootElement, final boolean isDelete, final Resource resource) {
    return AtomicEChangeAssertHelper.assertRemoveRoot(changes, rootElement, isDelete, resource, resource.getURI());
  }

  public static void assertEmpty(final Iterable<? extends EChange<?>> changes) {
    Assertions.assertEquals(0, IterableExtensions.size(changes));
  }

  private AtomicEChangeAssertHelper() {
    
  }
}
