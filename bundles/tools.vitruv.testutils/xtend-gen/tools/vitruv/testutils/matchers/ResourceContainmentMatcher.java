package tools.vitruv.testutils.matchers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import tools.vitruv.testutils.printing.ModelPrinting;

@FinalFieldsConstructor
@SuppressWarnings("all")
class ResourceContainmentMatcher extends TypeSafeMatcher<Resource> {
  private final Matcher<? super EObject> delegateMatcher;

  private boolean exists;

  @Override
  public void describeMismatchSafely(final Resource item, final Description mismatchDescription) {
    if ((!this.exists)) {
      mismatchDescription.appendText("there is no resource at ").appendValue(item.getURI());
    } else {
      boolean _isEmpty = item.getContents().isEmpty();
      if (_isEmpty) {
        ModelPrinting.appendModelValue(mismatchDescription, item).appendText(" was empty.");
      } else {
        int _size = item.getContents().size();
        boolean _greaterThan = (_size > 1);
        if (_greaterThan) {
          ModelPrinting.appendModelValue(mismatchDescription, item).appendText(" contained ").appendValue(Integer.valueOf(item.getContents().size())).appendText(" instead of just one content element.");
        } else {
          this.delegateMatcher.describeMismatch(item.getContents().get(0), mismatchDescription);
          ModelPrinting.appendModelValue(mismatchDescription.appendText(System.lineSeparator()).appendText("    in the resource at "), item.getURI());
        }
      }
    }
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("a resource containing ").appendDescriptionOf(this.delegateMatcher);
  }

  @Override
  public boolean matchesSafely(final Resource item) {
    this.exists = item.getResourceSet().getURIConverter().exists(item.getURI(), CollectionLiterals.<Object, Object>emptyMap());
    return ((this.exists && (item.getContents().size() == 1)) && this.delegateMatcher.matches(item.getContents().get(0)));
  }

  public ResourceContainmentMatcher(final Matcher<? super EObject> delegateMatcher) {
    super();
    this.delegateMatcher = delegateMatcher;
  }
}
