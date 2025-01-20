package tools.vitruv.change.composite.util;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import tools.vitruv.change.atomic.EChange;

@Utility
@SuppressWarnings("all")
public final class CompoundEChangeAssertHelper {
  public static Iterable<? extends EChange<EObject>> assertCreateAndInsertNonRoot(final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject, final EStructuralFeature affectedFeature, final EObject expectedNewValue, final int expectedIndex, final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return AtomicEChangeAssertHelper.assertInsertEReference(AtomicEChangeAssertHelper.assertCreateEObject(changes, expectedNewValue), affectedEObject, affectedFeature, expectedNewValue, expectedIndex, true, wasUnset);
  }

  public static Iterable<? extends EChange<EObject>> assertCreateAndReplaceNonRoot(final Iterable<? extends EChange<EObject>> changes, final EObject expectedNewValue, final EObject affectedEObject, final EStructuralFeature affectedFeature, final boolean wasUnset) {
    return CompoundEChangeAssertHelper.assertCreateAndReplaceNonRoot(changes, expectedNewValue, affectedEObject, null, affectedFeature, wasUnset);
  }

  public static Iterable<? extends EChange<EObject>> assertCreateAndReplaceNonRoot(final Iterable<? extends EChange<EObject>> changes, final EObject expectedNewValue, final EObject affectedEObject, final EObject expectedOldValue, final EStructuralFeature affectedFeature, final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(AtomicEChangeAssertHelper.assertCreateEObject(changes, expectedNewValue), affectedEObject, affectedFeature, expectedOldValue, expectedNewValue, true, wasUnset, false);
  }

  public static Iterable<? extends EChange<EObject>> assertReplaceAndDeleteNonRoot(final Iterable<? extends EChange<EObject>> changes, final EObject expectedOldValue, final EObject affectedEObject, final EStructuralFeature affectedFeature, final boolean isUnset) {
    return CompoundEChangeAssertHelper.assertDeleteEObjectAndContainedElements(AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, affectedEObject, affectedFeature, expectedOldValue, null, true, false, isUnset), expectedOldValue);
  }

  public static Iterable<? extends EChange<EObject>> assertDeleteEObjectAndContainedElements(final Iterable<? extends EChange<EObject>> changes, final EObject expectedOldValue) {
    final List<EObject> deletedContainedElements = ListExtensions.<EObject>reverse(IteratorExtensions.<EObject>toList(expectedOldValue.eAllContents()));
    int _size = deletedContainedElements.size();
    int _plus = (1 + _size);
    ChangeAssertHelper.assertSizeGreaterEquals(changes, _plus);
    Iterable<? extends EChange<EObject>> filteredChanges = changes;
    for (final EObject deletedContainedElement : deletedContainedElements) {
      filteredChanges = AtomicEChangeAssertHelper.assertDeleteEObject(filteredChanges, deletedContainedElement);
    }
    return AtomicEChangeAssertHelper.assertDeleteEObject(filteredChanges, expectedOldValue);
  }

  public static Iterable<? extends EChange<EObject>> assertCreateAndInsertRootEObject(final Iterable<? extends EChange<EObject>> changes, final EObject expectedNewValue, final String uri, final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return AtomicEChangeAssertHelper.assertInsertRootEObject(AtomicEChangeAssertHelper.assertCreateEObject(changes, expectedNewValue), expectedNewValue, uri, resource);
  }

  public static Iterable<? extends EChange<EObject>> assertRemoveAndDeleteRootEObject(final Iterable<? extends EChange<EObject>> changes, final EObject expectedOldValue, final String uri, final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return CompoundEChangeAssertHelper.assertDeleteEObjectAndContainedElements(AtomicEChangeAssertHelper.assertRemoveRootEObject(changes, expectedOldValue, uri, resource), expectedOldValue);
  }

  private CompoundEChangeAssertHelper() {
    
  }
}
