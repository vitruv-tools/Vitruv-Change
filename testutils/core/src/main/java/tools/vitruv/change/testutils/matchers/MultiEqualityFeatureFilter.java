package tools.vitruv.change.testutils.matchers;

import java.util.List;
import org.eclipse.emf.ecore.EStructuralFeature;
import java.util.function.BiConsumer;
import tools.vitruv.change.testutils.printing.TestMessages;

class MultiEqualityFeatureFilter implements EqualityFeatureFilter {
  private final List<EqualityFeatureFilter> filters;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    return this.filters.stream().allMatch(it -> it.includeFeature(feature));
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final BiConsumer<StringBuilder, EqualityFeatureFilter> _function = (StringBuilder target,
        EqualityFeatureFilter it) -> {
      it.describeTo(target);
    };
    TestMessages.<EqualityFeatureFilter>joinSemantic(this.filters, "and", ";", _function);
  }

  public MultiEqualityFeatureFilter(final List<EqualityFeatureFilter> filters) {
    super();
    this.filters = filters;
  }
}
