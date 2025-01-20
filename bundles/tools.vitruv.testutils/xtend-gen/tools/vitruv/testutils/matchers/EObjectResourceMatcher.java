package tools.vitruv.testutils.matchers;

import com.google.common.base.Objects;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import tools.vitruv.testutils.printing.ModelPrinting;

@FinalFieldsConstructor
@SuppressWarnings("all")
class EObjectResourceMatcher extends TypeSafeMatcher<EObject> {
  private final Resource expectedResource;

  @Override
  public boolean matchesSafely(final EObject item) {
    URI _uRI = item.eResource().getURI();
    URI _uRI_1 = this.expectedResource.getURI();
    return Objects.equal(_uRI, _uRI_1);
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("an EObject that is contained in the resource at ").appendValue(this.expectedResource.getURI());
  }

  @Override
  public void describeMismatchSafely(final EObject item, final Description mismatchDescription) {
    ModelPrinting.appendModelValue(mismatchDescription, item);
    final Resource actualResource = item.eResource();
    if ((actualResource == null)) {
      mismatchDescription.appendText(" is not in any resource");
    } else {
      ModelPrinting.appendModelValue(mismatchDescription.appendText(" is in "), actualResource);
    }
  }

  public EObjectResourceMatcher(final Resource expectedResource) {
    super();
    this.expectedResource = expectedResource;
  }
}
