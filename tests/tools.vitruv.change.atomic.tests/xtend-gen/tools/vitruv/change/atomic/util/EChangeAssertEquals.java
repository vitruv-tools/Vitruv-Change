package tools.vitruv.change.atomic.util;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;

/**
 * Helper class to compare different instances of the same change.
 */
@SuppressWarnings("all")
public class EChangeAssertEquals {
  protected static void _assertEquals(final EChange<?> change, final EChange<?> change2) {
    Assertions.assertTrue(false);
  }

  /**
   * Compares two {@link ReplaceSingleValuedEAttribute} EChanges.
   */
  protected static void _assertEquals(final ReplaceSingleValuedEAttribute<?, ?> change, final EChange<?> change2) {
    ReplaceSingleValuedEAttribute replaceChange = EChangeAssertEquals.assertIsInstanceOf(change2, ReplaceSingleValuedEAttribute.class);
    Assertions.assertSame(change.getAffectedElement(), replaceChange.getAffectedElement());
    Assertions.assertSame(change.getAffectedFeature(), replaceChange.getAffectedFeature());
    Assertions.assertEquals(change.getOldValue(), replaceChange.getOldValue());
    Assertions.assertEquals(change.getNewValue(), replaceChange.getNewValue());
  }

  /**
   * Compares two {@link InsertEAttributeValue} EChanges.
   */
  protected static void _assertEquals(final InsertEAttributeValue<?, ?> change, final EChange<?> change2) {
    InsertEAttributeValue insertChange = EChangeAssertEquals.assertIsInstanceOf(change2, InsertEAttributeValue.class);
    Assertions.assertSame(change.getAffectedElement(), insertChange.getAffectedElement());
    Assertions.assertSame(change.getAffectedFeature(), insertChange.getAffectedFeature());
    Assertions.assertEquals(change.getNewValue(), insertChange.getNewValue());
  }

  /**
   * Compares two {@link RemoveEAttributeValue} EChanges.
   */
  protected static void _assertEquals(final RemoveEAttributeValue<?, ?> change, final EChange<?> change2) {
    RemoveEAttributeValue removeChange = EChangeAssertEquals.assertIsInstanceOf(change2, RemoveEAttributeValue.class);
    Assertions.assertSame(change.getAffectedElement(), removeChange.getAffectedElement());
    Assertions.assertSame(change.getAffectedFeature(), removeChange.getAffectedFeature());
    Assertions.assertEquals(change.getOldValue(), removeChange.getOldValue());
  }

  private static <E extends Object, T extends EChange<? extends E>> T assertIsInstanceOf(final EChange<E> change, final Class<T> type) {
    Assertions.assertTrue(type.isInstance(change));
    return type.cast(change);
  }

  public static void assertEquals(final EChange<?> change, final EChange<?> change2) {
    if (change instanceof InsertEAttributeValue) {
      _assertEquals((InsertEAttributeValue<?, ?>)change, change2);
      return;
    } else if (change instanceof RemoveEAttributeValue) {
      _assertEquals((RemoveEAttributeValue<?, ?>)change, change2);
      return;
    } else if (change instanceof ReplaceSingleValuedEAttribute) {
      _assertEquals((ReplaceSingleValuedEAttribute<?, ?>)change, change2);
      return;
    } else if (change != null) {
      _assertEquals(change, change2);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(change, change2).toString());
    }
  }
}
