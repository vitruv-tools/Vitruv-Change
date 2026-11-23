package tools.vitruv.change.composite.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import tools.vitruv.change.atomic.EChange;

/**
 * Test assertion helpers for sequences of atomic {@link EChange}s that together
 * form one logical change, such as creating an element and inserting it, or
 * removing an element and deleting it together with its contents.
 *
 * <p>Like {@link AtomicEChangeAssertHelper}, each method checks the changes at
 * the start of the given sequence and returns the remaining changes, so calls
 * can be chained.
 */
public final class CompoundEChangeAssertHelper {

  /**
   * Asserts that the next changes create a new element and insert it into a
   * multi-valued containment reference.
   *
   * @param changes the changes to check
   * @param affectedEObject the expected container the element is inserted into
   * @param affectedFeature the expected containment reference
   * @param expectedNewValue the expected created and inserted element
   * @param expectedIndex the expected index the element is inserted at
   * @param wasUnset whether the reference is expected to have been unset before the change
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertCreateAndInsertNonRoot(
      final Iterable<? extends EChange<EObject>> changes, final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final EObject expectedNewValue,
      final int expectedIndex,
      final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return AtomicEChangeAssertHelper.assertInsertEReference(
        AtomicEChangeAssertHelper.assertCreateEObject(changes, expectedNewValue),
            affectedEObject,
            affectedFeature,
        expectedNewValue, expectedIndex, true, wasUnset);
  }

  /**
   * Asserts that the next changes create a new element and set it as the value of
   * a previously empty single-valued containment reference.
   *
   * @param changes the changes to check
   * @param expectedNewValue the expected created element
   * @param affectedEObject the expected container
   * @param affectedFeature the expected containment reference
   * @param wasUnset whether the reference is expected to have been unset before the change
   * @return the remaining changes after the checked ones
   * @see #assertCreateAndReplaceNonRoot(Iterable, EObject, EObject, EObject,
   *     EStructuralFeature, boolean)
   */
  public static Iterable<? extends EChange<EObject>> assertCreateAndReplaceNonRoot(
      final Iterable<? extends EChange<EObject>> changes,
      final EObject expectedNewValue,
      final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final boolean wasUnset) {
    return CompoundEChangeAssertHelper
            .assertCreateAndReplaceNonRoot(changes, expectedNewValue,
                    affectedEObject, null, affectedFeature, wasUnset);
  }

  /**
   * Asserts that the next changes create a new element and set it as the value of
   * a single-valued containment reference, replacing the given old value.
   *
   * @param changes the changes to check
   * @param expectedNewValue the expected created element
   * @param affectedEObject the expected container
   * @param expectedOldValue the expected previous value, or {@code null} if there was none
   * @param affectedFeature the expected containment reference
   * @param wasUnset whether the reference is expected to have been unset before the change
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertCreateAndReplaceNonRoot(
      final Iterable<? extends EChange<EObject>> changes,
      final EObject expectedNewValue,
      final EObject affectedEObject,
      final EObject expectedOldValue,
      final EStructuralFeature affectedFeature,
      final boolean wasUnset) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(
        AtomicEChangeAssertHelper.assertCreateEObject(changes, expectedNewValue),
            affectedEObject,
            affectedFeature,
            expectedOldValue, expectedNewValue, true, wasUnset, false);
  }

  /**
   * Asserts that the next changes remove an element from a single-valued
   * containment reference and then delete it together with its contents.
   *
   * @param changes the changes to check
   * @param expectedOldValue the expected removed and deleted element
   * @param affectedEObject the expected container
   * @param affectedFeature the expected containment reference
   * @param isUnset whether the reference is expected to be unset after the change
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertReplaceAndDeleteNonRoot(
      final Iterable<? extends EChange<EObject>> changes,
      final EObject expectedOldValue,
      final EObject affectedEObject,
      final EStructuralFeature affectedFeature,
      final boolean isUnset) {
    return CompoundEChangeAssertHelper
        .assertDeleteEObjectAndContainedElements(
                AtomicEChangeAssertHelper
                        .assertReplaceSingleValuedEReference(
                                changes,
                                affectedEObject,
                                affectedFeature,
                                expectedOldValue,
                                null,
                                true,
                                false,
                                isUnset),
                expectedOldValue);
  }

  /**
   * Asserts that the next changes delete the given element and everything it
   * contains.
   *
   * <p>Contained elements are expected first, innermost and last first: each one
   * is removed from its container and then deleted. The given element itself is
   * deleted last.
   *
   * @param changes the changes to check
   * @param expectedOldValue the expected deleted element
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertDeleteEObjectAndContainedElements(
      final Iterable<? extends EChange<EObject>> changes, final EObject expectedOldValue) {
    final List<EObject> deletedContainedElements
            = Lists.newArrayList(expectedOldValue.eAllContents());
    Collections.reverse(deletedContainedElements);
    int size = deletedContainedElements.size();
    int doubledSizePlusOne = (1 + 2 * size);
    ChangeAssertHelper.assertSizeGreaterEquals(changes, doubledSizePlusOne);
    Iterable<? extends EChange<EObject>> filteredChanges = changes;
    for (final EObject deletedContainedElement : deletedContainedElements) {
      filteredChanges = CompoundEChangeAssertHelper.assertRemoveFromContainer(filteredChanges,
          deletedContainedElement);
      filteredChanges = AtomicEChangeAssertHelper
              .assertDeleteEObject(filteredChanges, deletedContainedElement);
    }
    return AtomicEChangeAssertHelper
            .assertDeleteEObject(filteredChanges, expectedOldValue);
  }

  /**
   * Asserts that the first change removes the given element from its current
   * container.
   *
   * <p>The expected container, reference and index are taken from the element's
   * current position in the model. For a multi-valued reference this expects a
   * remove change; for a single-valued one, a replace change with {@code null}.
   *
   * @param changes the changes to check
   * @param expectedOldValue the expected removed element; must still be contained
   * @return the remaining changes after the first one
   */
  public static Iterable<? extends EChange<EObject>> assertRemoveFromContainer(
      final Iterable<? extends EChange<EObject>> changes, final EObject expectedOldValue) {
    final EObject container = expectedOldValue.eContainer();
    final EReference containmentFeature = expectedOldValue.eContainmentFeature();
    if (containmentFeature.isMany()) {
      final int index = ((List<?>) container.eGet(containmentFeature)).indexOf(expectedOldValue);
      return AtomicEChangeAssertHelper.assertRemoveEReference(changes, container,
          containmentFeature, expectedOldValue, index, true);
    }
    return AtomicEChangeAssertHelper.assertReplaceSingleValuedEReference(changes, container,
        containmentFeature, expectedOldValue, null, true, false, false);
  }

  /**
   * Asserts that the next changes create a new element and insert it as a root
   * object of the given resource.
   *
   * @param changes the changes to check
   * @param expectedNewValue the expected created root object
   * @param uri the expected URI of the resource
   * @param resource the expected resource
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertCreateAndInsertRootEObject(
      final Iterable<? extends EChange<EObject>> changes,
      final EObject expectedNewValue,
      final String uri,
      final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return AtomicEChangeAssertHelper.assertInsertRootEObject(
        AtomicEChangeAssertHelper.assertCreateEObject(changes, expectedNewValue),
            expectedNewValue,
            uri,
            resource);
  }

  /**
   * Asserts that the next changes remove a root object from the given resource and
   * then delete it together with its contents.
   *
   * @param changes the changes to check
   * @param expectedOldValue the expected removed and deleted root object
   * @param uri the expected URI of the resource
   * @param resource the expected resource
   * @return the remaining changes after the checked ones
   */
  public static Iterable<? extends EChange<EObject>> assertRemoveAndDeleteRootEObject(
      final Iterable<? extends EChange<EObject>> changes,
      final EObject expectedOldValue,
      final String uri,
      final Resource resource) {
    ChangeAssertHelper.assertSizeGreaterEquals(changes, 2);
    return CompoundEChangeAssertHelper.assertDeleteEObjectAndContainedElements(
        AtomicEChangeAssertHelper
                .assertRemoveRootEObject(changes, expectedOldValue, uri, resource),
            expectedOldValue);
  }

  private CompoundEChangeAssertHelper() {

  }
}
