package tools.vitruv.testutils.matchers;

import java.util.Set;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tools.vitruv.testutils.printing.TestMessages;

@FinalFieldsConstructor
@SuppressWarnings("all")
class IncludeOnlyFeatures implements EqualityFeatureFilter {
  private final Set<EStructuralFeature> features;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    return this.features.contains(feature);
  }

  @Override
  public void describeTo(@Extension final StringBuilder builder) {
    final Procedure1<EStructuralFeature> _function = (EStructuralFeature it) -> {
      builder.append(it.getEContainingClass().getName()).append(".").append(it.getName());
    };
    TestMessages.<EStructuralFeature>joinSemantic(builder.append("considered only the ").append(TestMessages.plural(this.features, "feature")).append(" "), this.features, "and", _function);
  }

  public IncludeOnlyFeatures(final Set<EStructuralFeature> features) {
    super();
    this.features = features;
  }
}
