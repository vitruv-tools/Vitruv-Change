package tools.vitruv.change.atomic.util;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;

/**
 * Utility class for frequently used assert methods in the tests.
 */
@SuppressWarnings("all")
public class EChangeAssertHelper {
  /**
   * Tests whether a unresolved change and a resolved change are the same class.
   */
  public void assertDifferentChangeSameClass(final EChange<Uuid> unresolvedChange, final EChange<? extends EObject> resolvedChange) {
    Assertions.assertNotSame(unresolvedChange, resolvedChange);
    Assertions.assertEquals(unresolvedChange.getClass(), resolvedChange.getClass());
  }

  /**
   * Tests whether a unresolved changes and a resolved changes are the same classes.
   */
  public void assertDifferentChangeSameClass(final List<? extends EChange<Uuid>> unresolvedChange, final List<? extends EChange<? extends EObject>> resolvedChange) {
    Assertions.assertEquals(unresolvedChange.size(), resolvedChange.size());
    for (int i = 0; (i < unresolvedChange.size()); i++) {
      this.assertDifferentChangeSameClass(unresolvedChange.get(i), resolvedChange.get(i));
    }
  }

  public static <T extends Object> T assertType(final Object original, final Class<T> type) {
    MatcherAssert.<Object>assertThat(original, Is.<Object>is(IsInstanceOf.<Object>instanceOf(type)));
    return ((T) original);
  }
}
