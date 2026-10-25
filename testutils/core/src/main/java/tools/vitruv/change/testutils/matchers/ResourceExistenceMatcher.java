package tools.vitruv.change.testutils.matchers;

import java.util.Map;
import org.eclipse.emf.ecore.resource.Resource;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import tools.vitruv.change.testutils.printing.ModelPrinting;

class ResourceExistenceMatcher extends TypeSafeMatcher<Resource> {
  private final boolean shouldExist;

  @Override
  public void describeTo(final Description description) {
    String _xifexpression = null;
    if (this.shouldExist) {
      _xifexpression = "";
    } else {
      _xifexpression = "not";
    }
    final String qualifier = _xifexpression;
    description.appendText("the resource " + qualifier + " to exist");
  }

  @Override
  public boolean matchesSafely(final Resource item) {
    boolean _exists = item.getResourceSet().getURIConverter().exists(item.getURI(), Map.of());
    return (_exists == this.shouldExist);
  }

  @Override
  public void describeMismatchSafely(final Resource item, final Description mismatchDescription) {
    String _xifexpression = null;
    if (this.shouldExist) {
      _xifexpression = "no";
    } else {
      _xifexpression = "a";
    }
    final String qualifier = _xifexpression;
    ModelPrinting.appendModelValue(mismatchDescription.appendText("there was " + qualifier + " resource at "),
        item.getURI());
  }

  public ResourceExistenceMatcher(final boolean shouldExist) {
    super();
    this.shouldExist = shouldExist;
  }
}
