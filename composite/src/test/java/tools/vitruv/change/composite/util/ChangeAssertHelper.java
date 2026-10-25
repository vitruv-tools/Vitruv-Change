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

public final class ChangeAssertHelper {
  public static <T extends AdditiveEChange<?, ?>, SubtractiveEChange extends Object> void assertOldAndNewValue(
      final T eChange, final Object oldValue, final Object newValue) {
    ChangeAssertHelper.assertOldValue(eChange, oldValue);
    ChangeAssertHelper.assertNewValue(eChange, newValue);
  }

  public static void assertOldValue(final EChange<?> eChange, final Object oldValue) {
    if ((oldValue instanceof EObject)) {
      Object _oldValue = ((SubtractiveEChange<?, ?>) eChange).getOldValue();
      MatcherAssert.<EObject>assertThat("old value must be the same or a copy as the given old value",
          ((EObject) oldValue),
          ModelMatchers.<EObject>equalsDeeply(((EObject) _oldValue)));
    } else {
      Assertions.assertEquals(oldValue, ((SubtractiveEChange<?, ?>) eChange).getOldValue(),
          "old value must be the same as the given old value");
    }
  }

  public static void assertNewValue(final AdditiveEChange<?, ?> eChange, final Object newValue) {
    final Object newValueInChange = eChange.getNewValue();
    boolean condition = ((newValue == null) && (newValueInChange == null));
    if (((newValue instanceof EObject) && (newValueInChange instanceof EObject))) {
      final EObject newEObject = ((EObject) newValue);
      EObject newEObjectInChange = ((EObject) newValueInChange);
      MatcherAssert.<EObject>assertThat(
          "new value in change '" + newValueInChange + "' must be the same as the given new value '" + newValue + "!",
          newEObject, ModelMatchers.<EObject>equalsDeeply(newEObjectInChange));
    } else {
      if ((!condition)) {
        Assertions.assertNotNull(newValue);
        Assertions.assertEquals(newValue, newValueInChange,
            "new value in change '" + newValueInChange + "' must be the same as the given new value '" + newValue + "!");
      }
    }
  }

  public static void assertAffectedEObject(final EChange<EObject> eChange, final EObject expectedAffectedEObject) {
    boolean _matched = false;
    if (eChange instanceof FeatureEChange) {
      _matched = true;
      MatcherAssert.<EObject>assertThat(
          "The actual affected EObject is a different one than the expected affected EObject or its copy",
          expectedAffectedEObject,
          ModelMatchers.<EObject>equalsDeeply(((FeatureEChange<EObject, ?>) eChange).getAffectedElement()));
    }
    if (!_matched) {
      if (eChange instanceof EObjectExistenceEChange) {
        _matched = true;
        MatcherAssert.<EObject>assertThat(
            "The actual affected EObject is a different one than the expected affected EObject or its copy",
            expectedAffectedEObject,
            ModelMatchers.<EObject>equalsDeeply(((EObjectExistenceEChange<EObject>) eChange).getAffectedElement()));
      }
    }
    if (!_matched) {
      throw new IllegalArgumentException();
    }
  }

  public static void assertAffectedEFeature(final EChange<?> eChange, final EStructuralFeature expectedEFeature) {
    Assertions.assertEquals(expectedEFeature, ((FeatureEChange<?, ?>) eChange).getAffectedFeature(),
        "The actual affected EStructuralFeature is a different one than the expected EStructuralFeature");
  }

  public static void assertContainment(final UpdateReferenceEChange<?> updateEReference, final boolean expectedValue) {
    boolean _isContainment = updateEReference.isContainment();
    Assertions.assertEquals(Boolean.valueOf(expectedValue), Boolean.valueOf(_isContainment),
        "The containment information of the change " + updateEReference + " is wrong");
  }

  public static void assertUri(final RootEChange<?> rootChange, final String expectedValue) {
    String _string = URI.createFileURI(expectedValue).toString();
    String _uri = rootChange.getUri();
    Assertions.assertEquals(_string, _uri,
        "Change " + rootChange + " shall have the uri " + URI.createFileURI(expectedValue));
  }

  public static void assertResource(final RootEChange<?> rootChange, final Resource resource) {
    Resource _resource = rootChange.getResource();
    Assertions.assertEquals(_resource, resource, "Change " + rootChange + " shall have the resource " + resource);
  }

  public static void assertIndex(final UpdateSingleListEntryEChange<?, ?> change, final int expectedIndex) {
    Assertions.assertEquals(expectedIndex, change.getIndex(), "The value is not at the correct index");
  }

  public static <T extends Object> T assertType(final Object original, final Class<T> type) {
    MatcherAssert.<Object>assertThat(original, Is.<Object>is(IsInstanceOf.<Object>instanceOf(type)));
    return ((T) original);
  }

  public static void assertSizeGreaterEquals(final Iterable<?> iterable, final int size) {
    int _size = Iterables.size(iterable);
    boolean _greaterEqualsThan = (_size >= size);
    Assertions.assertTrue(_greaterEqualsThan);
  }

  private ChangeAssertHelper() {

  }
}
