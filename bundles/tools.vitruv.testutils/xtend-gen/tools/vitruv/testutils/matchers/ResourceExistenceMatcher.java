package tools.vitruv.testutils.matchers;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import tools.vitruv.testutils.printing.ModelPrinting;

@FinalFieldsConstructor
@SuppressWarnings("all")
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
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("the resource ");
    _builder.append(qualifier);
    _builder.append(" to exist");
    description.appendText(_builder.toString());
  }

  @Override
  public boolean matchesSafely(final Resource item) {
    boolean _exists = item.getResourceSet().getURIConverter().exists(item.getURI(), CollectionLiterals.<Object, Object>emptyMap());
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
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("there was ");
    _builder.append(qualifier);
    _builder.append(" resource at ");
    ModelPrinting.appendModelValue(mismatchDescription.appendText(_builder.toString()), item.getURI());
  }

  public ResourceExistenceMatcher(final boolean shouldExist) {
    super();
    this.shouldExist = shouldExist;
  }
}
