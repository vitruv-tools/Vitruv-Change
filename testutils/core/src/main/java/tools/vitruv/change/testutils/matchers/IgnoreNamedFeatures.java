package tools.vitruv.change.testutils.matchers;

import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.testutils.printing.TestMessages;

class IgnoreNamedFeatures implements EqualityFeatureFilter {
  private final Set<String> featureNames;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    boolean _contains = this.featureNames.contains(feature.getName());
    return (!_contains);
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final Consumer<String> _function = (String it) -> {
      builder.append("\'").append(it).append("\'");
    };
    TestMessages.<String>joinSemantic(builder.append("ignored any feature called "), this.featureNames, "or",
        _function);
  }

  public IgnoreNamedFeatures(final Set<String> featureNames) {
    super();
    this.featureNames = featureNames;
  }
}
