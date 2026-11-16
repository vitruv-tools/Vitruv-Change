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
public class EChangeAssertEquals {

  /**
   * Compares two {@link ReplaceSingleValuedEAttribute} EChanges.
   */
  protected static void assertEqualsChange(
          final ReplaceSingleValuedEAttribute<?, ?> change, final EChange<?> change2) {
    var replaceChange = EChangeAssertEquals
            .assertIsInstanceOf(change2, ReplaceSingleValuedEAttribute.class);
    Assertions.assertSame(change.getAffectedElement(), replaceChange.getAffectedElement());
    Assertions.assertSame(change.getAffectedFeature(), replaceChange.getAffectedFeature());
    Assertions.assertEquals(change.getOldValue(), replaceChange.getOldValue());
    Assertions.assertEquals(change.getNewValue(), replaceChange.getNewValue());
  }

  /**
   * Compares two {@link InsertEAttributeValue} EChanges.
   */
  protected static void assertEqualsChange(
          final InsertEAttributeValue<?, ?> change, final EChange<?> change2) {
    InsertEAttributeValue insertChange = EChangeAssertEquals
            .assertIsInstanceOf(change2, InsertEAttributeValue.class);
    Assertions.assertSame(change.getAffectedElement(), insertChange.getAffectedElement());
    Assertions.assertSame(change.getAffectedFeature(), insertChange.getAffectedFeature());
    Assertions.assertEquals(change.getNewValue(), insertChange.getNewValue());
  }

  /**
   * Compares two {@link RemoveEAttributeValue} EChanges.
   */
  protected static void assertEqualsChange(
          final RemoveEAttributeValue<?, ?> change, final EChange<?> change2) {
    var removeChange = EChangeAssertEquals
            .assertIsInstanceOf(change2, RemoveEAttributeValue.class);
    Assertions.assertSame(change.getAffectedElement(), removeChange.getAffectedElement());
    Assertions.assertSame(change.getAffectedFeature(), removeChange.getAffectedFeature());
    Assertions.assertEquals(change.getOldValue(), removeChange.getOldValue());
  }

  private static <E, T extends EChange<? extends E>> T assertIsInstanceOf(
          final EChange<E> change, final Class<T> type) {
    Assertions.assertTrue(type.isInstance(change));
    return type.cast(change);
  }

  /**
   * Fallback for unsupported {@link EChange} types; always fails.
   */
  protected static void assertEquals() {
    Assertions.fail();
  }

  /**
   * Asserts that two {@link EChange}s are equal.
   *
   * <p>Delegates to the matching comparison method based on the type of
   * {@code change}. Supported types are {@link InsertEAttributeValue},
   * {@link RemoveEAttributeValue} and {@link ReplaceSingleValuedEAttribute};
   * any other type fails the assertion.
   *
   * @param change the expected change
   * @param change2 the actual change to compare against
   * @throws IllegalArgumentException if {@code change} is {@code null}
   */
  public static void assertEquals(final EChange<?> change, final EChange<?> change2) {
    if (change instanceof InsertEAttributeValue) {
      assertEqualsChange((InsertEAttributeValue<?, ?>) change, change2);
    } else if (change instanceof RemoveEAttributeValue) {
      assertEqualsChange((RemoveEAttributeValue<?, ?>) change, change2);
    } else if (change instanceof ReplaceSingleValuedEAttribute) {
      assertEqualsChange((ReplaceSingleValuedEAttribute<?, ?>) change, change2);
    } else if (change != null) {
      assertEquals();
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: "
              + Arrays.<Object>asList(change, change2).toString());
    }
  }
}
