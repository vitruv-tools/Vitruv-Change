package tools.vitruv.change.testutils.matchers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsCollectionContaining;
import tools.vitruv.change.testutils.printing.ModelPrinting;
import tools.vitruv.change.testutils.printing.TestMessages;
class EObjectFeatureMatcher extends TypeSafeMatcher<EObject> {
  private final EStructuralFeature feature;

  private final Matcher<?> featureMatcher;

  @Override
  protected boolean matchesSafely(final EObject item) {
    return (this.feature.getEContainingClass().isInstance(item) && this.featureMatcher.matches(item.eGet(this.feature)));
  }

  @Override
  public void describeTo(final Description description) {
    Description _appendText = description.appendText(TestMessages.a(this.feature.getEContainingClass().getName())).appendText(" whose ").appendText(this.feature.getName());
    String _switchResult = null;
    final Matcher<?> featureMatcher = this.featureMatcher;
    boolean _matched = false;
    if (featureMatcher instanceof EObjectFeatureMatcher) {
      _matched=true;
    }
    if (!_matched) {
      if (featureMatcher instanceof IsCollectionContaining) {
        _matched=true;
      }
    }
    if (_matched) {
      _switchResult = " is ";
    }
    if (!_matched) {
      _switchResult = " ";
    }
    _appendText.appendText(_switchResult).appendDescriptionOf(this.featureMatcher);
  }

  @Override
  public void describeMismatchSafely(final EObject item, final Description mismatchDescription) {
    boolean _isInstance = this.feature.getEContainingClass().isInstance(item);
    boolean _not = (!_isInstance);
    if (_not) {
      mismatchDescription.appendText(" was ").appendText(TestMessages.a(item.eClass().getName()));
    } else {
      this.describeFeatureMismatch(item, mismatchDescription);
    }
    ModelPrinting.appendModelValue(mismatchDescription.appendText(":").appendText(System.lineSeparator()), item);
  }

  private void describeFeatureMismatch(final EObject item, final Description mismatchDescription) {
    mismatchDescription.appendText("->").appendText(this.feature.getName())
        .appendText(this.featureMismatchSuffix());
    this.featureMatcher.describeMismatch(item.eGet(this.feature), mismatchDescription);
    mismatchDescription.appendText(" for");
  }

  private String featureMismatchSuffix() {
    if (this.featureMatcher instanceof IsCollectionContaining) {
      if (this.feature.isOrdered()) {
        return "[*]";
      }
      return "{*}";
    }
    if (this.featureMatcher instanceof EObjectFeatureMatcher) {
      return "";
    }
    return " ";
  }

  public EObjectFeatureMatcher(final EStructuralFeature feature, final Matcher<?> featureMatcher) {
    super();
    this.feature = feature;
    this.featureMatcher = featureMatcher;
  }
}
