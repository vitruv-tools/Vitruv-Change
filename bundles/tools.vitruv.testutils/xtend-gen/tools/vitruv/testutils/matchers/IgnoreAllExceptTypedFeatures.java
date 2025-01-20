package tools.vitruv.testutils.matchers;

import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tools.vitruv.testutils.printing.TestMessages;

@FinalFieldsConstructor
@SuppressWarnings("all")
class IgnoreAllExceptTypedFeatures implements EqualityFeatureFilter {
  private final Set<EClassifier> featureTypes;

  @Override
  public boolean includeFeature(final EStructuralFeature feature) {
    boolean _xblockexpression = false;
    {
      final EClassifier featureType = feature.getEType();
      boolean _switchResult = false;
      boolean _matched = false;
      if (featureType instanceof EClass) {
        _matched=true;
        _switchResult = (this.featureTypes.contains(featureType) || IterableExtensions.<EClass>exists(((EClass)featureType).getEAllSuperTypes(), ((Function1<EClass, Boolean>) (EClass it) -> {
          return Boolean.valueOf(this.featureTypes.contains(it));
        })));
      }
      if (!_matched) {
        _switchResult = this.featureTypes.contains(featureType);
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }

  @Override
  public void describeTo(@Extension final StringBuilder builder) {
    final Procedure1<EClassifier> _function = (EClassifier it) -> {
      builder.append(it.getName());
    };
    TestMessages.<EClassifier>joinSemantic(builder.append("considered only features of type "), this.featureTypes, "or", _function);
  }

  public IgnoreAllExceptTypedFeatures(final Set<EClassifier> featureTypes) {
    super();
    this.featureTypes = featureTypes;
  }
}
