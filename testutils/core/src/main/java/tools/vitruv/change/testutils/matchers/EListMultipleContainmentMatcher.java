package tools.vitruv.change.testutils.matchers;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Class to instantiate either a containsAllOf-matcher or a
 * containsNoneOf-matcher.
 * 
 * @author Dirk Neumann
 */
class EListMultipleContainmentMatcher extends TypeSafeMatcher<Iterable<? extends EObject>> {
  private Iterable<? extends EObject> itemsOfInterest;

  private Matcher<Object> base;

  private boolean included;

  /**
   * Constructor.
   * 
   * @param itemsOfInterest items which should (not) be included
   * @param included        Should all of them (true) or none of them (false) be
   *                        included?
   * @param options         the options for the deep equality check.
   */
  EListMultipleContainmentMatcher(final Iterable<? extends EObject> itemsOfInterest, final boolean included,
      final ModelDeepEqualityOption[] options) {
    this.itemsOfInterest = itemsOfInterest;
    this.included = included;
    @SuppressWarnings("unchecked")
    Matcher<Object>[] matcherArray = java.util.stream.StreamSupport.stream(itemsOfInterest.spliterator(), false)
        .map(it -> (Matcher<Object>) (Matcher<?>) new EListSingleContainmentMatcher(it, included, options))
        .toArray(Matcher[]::new);
    this.base = CoreMatchers.<Object>allOf(matcherArray);
  }

  @Override
  protected boolean matchesSafely(final Iterable<? extends EObject> items) {
    return this.base.matches(items);
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("a list which contains ");
    if (this.included) {
      description.appendText(String.format("all elements from the following list:%n"));
    } else {
      description.appendText(String.format("none of the elements from the following list:%n"));
    }
    for (final EObject e : this.itemsOfInterest) {
      String _string = e.toString();
      String _plus = (_string + "%n");
      description.appendText(String.format(_plus));
    }
  }
}
