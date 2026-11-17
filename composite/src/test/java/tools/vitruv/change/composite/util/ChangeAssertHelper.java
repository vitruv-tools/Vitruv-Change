package tools.vitruv.change.composite.util;

import com.google.common.collect.Iterables;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.SubtractiveEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange;
import tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange;
import tools.vitruv.change.atomic.root.RootEChange;
import tools.vitruv.change.testutils.matchers.ModelMatchers;

/**
 * Test assertion helpers for checking individual properties of {@link EChange}s.
 *
 * <p>Values that are {@link EObject}s are compared deeply, so a copy of the
 * expected object is accepted as well as the object itself.
 */
public final class ChangeAssertHelper {

  /**
   * Asserts that the given change has the expected old and new value.
   *
   * @param <T> the type of the change
   * @param eChange the change to check; must also be a {@link SubtractiveEChange}
   * @param oldValue the expected old value
   * @param newValue the expected new value
   */
  public static <T extends AdditiveEChange<?, ?>> void assertOldAndNewValue(
      final T eChange, final Object oldValue, final Object newValue) {
    ChangeAssertHelper.assertOldValue(eChange, oldValue);
    ChangeAssertHelper.assertNewValue(eChange, newValue);
  }

  /**
   * Asserts that the given change has the expected old value.
   *
   * <p>{@link EObject}s are compared deeply; other values are compared with
   * {@code equals}.
   *
   * @param eChange the change to check; must be a {@link SubtractiveEChange}
   * @param oldValue the expected old value
   */
  public static void assertOldValue(final EChange<?> eChange, final Object oldValue) {
    if ((oldValue instanceof EObject)) {
      Object oldValueTemp = ((SubtractiveEChange<?, ?>) eChange).getOldValue();
      MatcherAssert
              .<EObject>assertThat("old value must be the same or a copy as the given old value",
          ((EObject) oldValue),
          ModelMatchers.<EObject>equalsDeeply(((EObject) oldValueTemp)));
    } else {
      Assertions.assertEquals(oldValue, ((SubtractiveEChange<?, ?>) eChange).getOldValue(),
          "old value must be the same as the given old value");
    }
  }

  /**
   * Asserts that the given change has the expected new value.
   *
   * <p>{@link EObject}s are compared deeply; other values are compared with
   * {@code equals}. Passes if both values are {@code null}.
   *
   * @param eChange the change to check
   * @param newValue the expected new value
   */
  public static void assertNewValue(final AdditiveEChange<?, ?> eChange, final Object newValue) {
    final Object newValueInChange = eChange.getNewValue();
    boolean condition = ((newValue == null) && (newValueInChange == null));
    if (((newValue instanceof EObject newEObject)
            && (newValueInChange instanceof EObject newEObjectInChange))) {
      MatcherAssert.<EObject>assertThat(
              "new value in change '" + newValueInChange
                      + "' must be the same as the given new value '"
                      + newValue + "!",
              newEObject, ModelMatchers.<EObject>equalsDeeply(newEObjectInChange));
    } else {
      if ((!condition)) {
        Assertions.assertNotNull(newValue);
        Assertions.assertEquals(newValue, newValueInChange,
            "new value in change '" + newValueInChange
                    + "' must be the same as the given new value '"
                    + newValue + "!");
      }
    }
  }

  /**
   * Asserts that the given change affects the expected object, or a deep copy of it.
   *
   * @param eChange the change to check; must be a {@link FeatureEChange} or an
   *     {@link EObjectExistenceEChange}
   * @param expectedAffectedEObject the expected affected object
   * @throws IllegalArgumentException if the change is of neither supported type
   */
  public static void assertAffectedEObject(
          final EChange<EObject> eChange,
          final EObject expectedAffectedEObject) {
    boolean matched = false;
    if (eChange instanceof FeatureEChange) {
      matched = true;
      MatcherAssert.<EObject>assertThat(
          "The actual affected EObject is a different one "
                  + "than the expected affected EObject or its copy",
          expectedAffectedEObject,
          ModelMatchers
                  .<EObject>equalsDeeply(((FeatureEChange<EObject, ?>) eChange)
                          .getAffectedElement()));
    }
    if (!matched && eChange instanceof EObjectExistenceEChange) {
      matched = true;
      MatcherAssert.<EObject>assertThat("The actual affected EObject is a different one "
                      + "than the expected affected EObject or its copy",
              expectedAffectedEObject, ModelMatchers
                      .<EObject>equalsDeeply(((EObjectExistenceEChange<EObject>) eChange)
                              .getAffectedElement()));
    }
    if (!matched) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Asserts that the given change affects the expected feature.
   *
   * @param eChange the change to check; must be a {@link FeatureEChange}
   * @param expectedEFeature the expected affected feature
   */
  public static void assertAffectedEFeature(
          final EChange<?> eChange,
          final EStructuralFeature expectedEFeature) {
    Assertions.assertEquals(expectedEFeature, ((FeatureEChange<?, ?>) eChange).getAffectedFeature(),
        "The actual affected EStructuralFeature is a different one "
                + "than the expected EStructuralFeature");
  }

  /**
   * Asserts whether the reference changed by the given change is a containment.
   *
   * @param updateEReference the reference change to check
   * @param expectedValue {@code true} if the reference is expected to be a containment
   */
  public static void assertContainment(
          final UpdateReferenceEChange<?> updateEReference,
          final boolean expectedValue) {
    boolean isContainment = updateEReference.isContainment();
    Assertions.assertEquals(expectedValue, isContainment,
        "The containment information of the change " + updateEReference + " is wrong");
  }

  /**
   * Asserts that the given root change refers to the expected URI.
   *
   * @param rootChange the root change to check
   * @param expectedValue the expected URI, as a file path
   */
  public static void assertUri(final RootEChange<?> rootChange, final String expectedValue) {
    String string = URI.createFileURI(expectedValue).toString();
    String uri = rootChange.getUri();
    Assertions.assertEquals(string, uri,
        "Change " + rootChange + " shall have the uri " + URI.createFileURI(expectedValue));
  }

  /**
   * Asserts that the given root change refers to the expected resource.
   *
   * @param rootChange the root change to check
   * @param resource the expected resource
   */
  public static void assertResource(final RootEChange<?> rootChange, final Resource resource) {
    Resource resourceTemp = rootChange.getResource();
    Assertions.assertEquals(resourceTemp, resource,
            "Change " + rootChange + " shall have the resource " + resource);
  }

  /**
   * Asserts that the given list change happens at the expected index.
   *
   * @param change the list change to check
   * @param expectedIndex the expected index
   */
  public static void assertIndex(
          final UpdateSingleListEntryEChange<?, ?> change,
          final int expectedIndex) {
    Assertions.assertEquals(expectedIndex, change.getIndex(),
            "The value is not at the correct index");
  }

  /**
   * Asserts that the given object is an instance of the expected type and returns
   * it cast to that type.
   *
   * @param <T> the expected type
   * @param original the object to check
   * @param type the expected type
   * @return the object cast to the expected type
   */
  public static <T> T assertType(final Object original, final Class<T> type) {
    MatcherAssert.<Object>assertThat(original,
            Is.<Object>is(IsInstanceOf.<Object>instanceOf(type)));
    return ((T) original);
  }

  /**
   * Asserts that the given iterable has at least the given number of elements.
   *
   * @param iterable the iterable to check
   * @param size the minimum expected number of elements
   */
  public static void assertSizeGreaterEquals(final Iterable<?> iterable, final int size) {
    int sizeTemp = Iterables.size(iterable);
    boolean greaterEqualsThan = (sizeTemp >= size);
    Assertions.assertTrue(greaterEqualsThan);
  }

  private ChangeAssertHelper() {

  }
}
