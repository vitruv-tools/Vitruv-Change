package tools.vitruv.change.testutils.matchers;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Class to instantiate either a listContains-matcher or a
 * listDoesNotContain-matcher (not yet).
 * 
 * @author Dirk Neumann
 */
public class EListSingleContainmentMatcher extends TypeSafeMatcher<Iterable<? extends EObject>> {
  private EObject itemOfInterest;

  private ModelDeepEqualityOption[] options;

  private boolean included;

  /**
   * Constructor.
   * 
   * @param itemOfInterest item which should (not) be included
   * @param included       Should the item be included (true) or not (false)?
   * @param options        the options for the deep equality check
   */
  public EListSingleContainmentMatcher(final EObject itemOfInterest, final boolean included,
      final ModelDeepEqualityOption[] options) {
    this.itemOfInterest = itemOfInterest;
    this.options = options;
    this.included = included;
  }

  @Override
  protected boolean matchesSafely(final Iterable<? extends EObject> items) {
    boolean _exists = java.util.stream.StreamSupport.stream(items.spliterator(), false)
        .anyMatch(it -> ModelMatchers.<EObject>equalsDeeply(this.itemOfInterest, this.options).matches(it));
    return (_exists == this.included);
  }

  @Override
  public void describeTo(final Description description) {
    String _string = this.itemOfInterest.toString();
    String _plus = ("a list which contains something deeply equal to " + _string);
    description.appendText(_plus);
  }
}
