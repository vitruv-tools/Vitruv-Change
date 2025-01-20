package tools.vitruv.testutils.matchers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Class to instantiate either a listContains-matcher or a listDoesNotContain-matcher (not yet).
 * @author Dirk Neumann
 */
@SuppressWarnings("all")
public class EListSingleContainmentMatcher extends TypeSafeMatcher<Iterable<? extends EObject>> {
  private EObject itemOfInterest;

  private ModelDeepEqualityOption[] options;

  private boolean included;

  /**
   * Constructor.
   * @param itemOfInterest item which should (not) be included
   * @param included Should the item be included (true) or not (false)?
   * @param options the options for the deep equality check
   */
  public EListSingleContainmentMatcher(final EObject itemOfInterest, final boolean included, final ModelDeepEqualityOption[] options) {
    this.itemOfInterest = itemOfInterest;
    this.options = options;
    this.included = included;
  }

  @Override
  protected boolean matchesSafely(final Iterable<? extends EObject> items) {
    final Function1<EObject, Boolean> _function = (EObject it) -> {
      return Boolean.valueOf(ModelMatchers.<EObject>equalsDeeply(this.itemOfInterest, this.options).matches(it));
    };
    boolean _exists = IterableExtensions.exists(items, _function);
    return (_exists == this.included);
  }

  @Override
  public void describeTo(final Description description) {
    String _string = this.itemOfInterest.toString();
    String _plus = ("a list which contains something deeply equal to " + _string);
    description.appendText(_plus);
  }
}
