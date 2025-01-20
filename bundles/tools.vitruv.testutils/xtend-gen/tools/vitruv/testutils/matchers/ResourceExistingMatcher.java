package tools.vitruv.testutils.matchers;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import tools.vitruv.testutils.printing.ModelPrinting;

@FinalFieldsConstructor
@SuppressWarnings("all")
class ResourceExistingMatcher extends TypeSafeMatcher<URI> {
  private final boolean shouldExist;

  @Override
  public boolean matchesSafely(final URI item) {
    boolean _existsResourceAtUri = URIUtil.existsResourceAtUri(item);
    return (_existsResourceAtUri == this.shouldExist);
  }

  @Override
  public void describeTo(final Description description) {
    String _xifexpression = null;
    if (this.shouldExist) {
      _xifexpression = "an URI pointing to an existing resource";
    } else {
      _xifexpression = "an URI not pointing to any resource";
    }
    description.appendText(_xifexpression);
  }

  @Override
  public void describeMismatchSafely(final URI item, final Description mismatchDescription) {
    String _xifexpression = null;
    if (this.shouldExist) {
      _xifexpression = "no";
    } else {
      _xifexpression = "a";
    }
    final String qualifier = _xifexpression;
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("there was ");
    _builder.append(qualifier);
    _builder.append(" resource at ");
    ModelPrinting.appendModelValue(mismatchDescription.appendText(_builder.toString()), item);
  }

  public ResourceExistingMatcher(final boolean shouldExist) {
    super();
    this.shouldExist = shouldExist;
  }
}
