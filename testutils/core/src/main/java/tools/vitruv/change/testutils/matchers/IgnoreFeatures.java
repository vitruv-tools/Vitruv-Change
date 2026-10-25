package tools.vitruv.change.testutils.matchers;

import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.testutils.printing.TestMessages;

class IgnoreFeatures implements EqualityFeatureFilter {
  private final Set<EStructuralFeature> features;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    boolean _contains = this.features.contains(feature);
    return (!_contains);
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final Consumer<EStructuralFeature> _function = (EStructuralFeature it) -> {
      builder.append(it.getEContainingClass().getName()).append(".").append(it.getName());
    };
    TestMessages.<EStructuralFeature>joinSemantic(
        builder.append("ignored the ").append(TestMessages.plural(this.features, "feature")).append(" "), this.features,
        "and", _function);
  }

  public IgnoreFeatures(final Set<EStructuralFeature> features) {
    super();
    this.features = features;
  }
}
