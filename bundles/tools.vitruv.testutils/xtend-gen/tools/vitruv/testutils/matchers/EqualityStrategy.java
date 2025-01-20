package tools.vitruv.testutils.matchers;

import org.eclipse.emf.ecore.EObject;

/**
 * Customizes how {@link ModelDeepEqualityMatcher} compares objects in <em>non-containment-references</em>.
 */
@SuppressWarnings("all")
public interface EqualityStrategy extends ModelDeepEqualityOption {
  enum Result {
    EQUAL,

    UNEQUAL,

    UNKNOWN;
  }

  EqualityStrategy.Result compare(final EObject left, final EObject right);
}
