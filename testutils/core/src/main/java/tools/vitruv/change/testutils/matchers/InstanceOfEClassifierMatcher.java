package tools.vitruv.change.testutils.matchers;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import tools.vitruv.change.testutils.printing.ModelPrinting;
import tools.vitruv.change.testutils.printing.TestMessages;
class InstanceOfEClassifierMatcher extends TypeSafeMatcher<Object> {
  private final EClassifier expectedType;

  @Override
  protected boolean matchesSafely(final Object item) {
    return this.expectedType.isInstance(item);
  }

  @Override
  public void describeTo(final Description description) {
    ModelPrinting.appendModelValue(description.appendText("an instance of "), this.expectedType);
  }

  @Override
  public void describeMismatchSafely(final Object item, final Description mismatchDescription) {
    String _switchResult = null;
    boolean _matched = false;
    if (item instanceof EObject) {
      _matched=true;
      _switchResult = ((EObject)item).eClass().getName();
    }
    if (!_matched) {
      _switchResult = item.getClass().getSimpleName();
    }
    ModelPrinting.appendModelValue(mismatchDescription.appendText("is ").appendText(
      TestMessages.a(_switchResult)).appendText(":").appendText(System.lineSeparator()), item);
  }

  public InstanceOfEClassifierMatcher(final EClassifier expectedType) {
    super();
    this.expectedType = expectedType;
  }
}
