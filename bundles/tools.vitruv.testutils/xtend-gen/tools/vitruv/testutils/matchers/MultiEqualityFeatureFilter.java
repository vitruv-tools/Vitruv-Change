package tools.vitruv.testutils.matchers;

import java.util.List;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import tools.vitruv.testutils.printing.TestMessages;

@FinalFieldsConstructor
@SuppressWarnings("all")
class MultiEqualityFeatureFilter implements EqualityFeatureFilter {
  private final List<EqualityFeatureFilter> filters;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    final Function1<EqualityFeatureFilter, Boolean> _function = (EqualityFeatureFilter it) -> {
      boolean _includeFeature = it.includeFeature(feature);
      return Boolean.valueOf((!_includeFeature));
    };
    boolean _exists = IterableExtensions.<EqualityFeatureFilter>exists(this.filters, _function);
    return (!_exists);
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final Procedure2<StringBuilder, EqualityFeatureFilter> _function = (StringBuilder target, EqualityFeatureFilter it) -> {
      it.describeTo(target);
    };
    TestMessages.<EqualityFeatureFilter>joinSemantic(this.filters, "and", ";", _function);
  }

  public MultiEqualityFeatureFilter(final List<EqualityFeatureFilter> filters) {
    super();
    this.filters = filters;
  }
}
