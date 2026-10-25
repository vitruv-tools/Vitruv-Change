package tools.vitruv.change.testutils.matchers;

import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.testutils.printing.TestMessages;

class IncludeOnlyFeatures implements EqualityFeatureFilter {
  private final Set<EStructuralFeature> features;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    return this.features.contains(feature);
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final Consumer<EStructuralFeature> _function = (EStructuralFeature it) -> {
      builder.append(it.getEContainingClass().getName()).append(".").append(it.getName());
    };
    TestMessages.<EStructuralFeature>joinSemantic(
        builder.append("considered only the ").append(TestMessages.plural(this.features, "feature")).append(" "),
        this.features, "and", _function);
  }

  public IncludeOnlyFeatures(final Set<EStructuralFeature> features) {
    super();
    this.features = features;
  }
}
