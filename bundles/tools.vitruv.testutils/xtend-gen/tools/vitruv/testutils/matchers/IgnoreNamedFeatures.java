package tools.vitruv.testutils.matchers;

import java.util.Set;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tools.vitruv.testutils.printing.TestMessages;

@FinalFieldsConstructor
@SuppressWarnings("all")
class IgnoreNamedFeatures implements EqualityFeatureFilter {
  private final Set<String> featureNames;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    boolean _contains = this.featureNames.contains(feature.getName());
    return (!_contains);
  }

  @Override
  public void describeTo(@Extension final StringBuilder builder) {
    final Procedure1<String> _function = (String it) -> {
      builder.append("\'").append(it).append("\'");
    };
    TestMessages.<String>joinSemantic(builder.append("ignored any feature called "), this.featureNames, "or", _function);
  }

  public IgnoreNamedFeatures(final Set<String> featureNames) {
    super();
    this.featureNames = featureNames;
  }
}
