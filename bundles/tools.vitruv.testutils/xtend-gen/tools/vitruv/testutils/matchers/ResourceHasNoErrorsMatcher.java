package tools.vitruv.testutils.matchers;

import java.util.function.Consumer;
import org.eclipse.emf.ecore.resource.Resource;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

@SuppressWarnings("all")
class ResourceHasNoErrorsMatcher extends TypeSafeMatcher<Resource> {
  @Override
  public boolean matchesSafely(final Resource resource) {
    return resource.getErrors().isEmpty();
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("an EMF resource without errors");
  }

  @Override
  public void describeMismatchSafely(final Resource resource, final Description mismatchDescription) {
    mismatchDescription.appendText("found these errors:");
    final Consumer<Resource.Diagnostic> _function = (Resource.Diagnostic it) -> {
      mismatchDescription.appendText(System.lineSeparator()).appendText("      • ").appendText(it.getMessage()).appendText(" (@");
      String _location = it.getLocation();
      boolean _tripleNotEquals = (_location != null);
      if (_tripleNotEquals) {
        mismatchDescription.appendText(it.getLocation()).appendText("#");
      } else {
        mismatchDescription.appendText("line ");
      }
      mismatchDescription.appendText(Integer.valueOf(it.getLine()).toString()).appendText(":").appendText(Integer.valueOf(it.getColumn()).toString()).appendText(")");
    };
    resource.getErrors().forEach(_function);
    mismatchDescription.appendText(System.lineSeparator());
  }
}
