package tools.vitruv.change.testutils.matchers;

import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import java.util.function.Consumer;
import tools.vitruv.change.testutils.printing.TestMessages;

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
        _matched = true;
        _switchResult = (this.featureTypes.contains(featureType) ||
            ((EClass) featureType).getEAllSuperTypes().stream().anyMatch(this.featureTypes::contains));
      }
      if (!_matched) {
        _switchResult = this.featureTypes.contains(featureType);
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final Consumer<EClassifier> _function = (EClassifier it) -> {
      builder.append(it.getName());
    };
    TestMessages.<EClassifier>joinSemantic(builder.append("considered only features of type "), this.featureTypes, "or",
        _function);
  }

  public IgnoreAllExceptTypedFeatures(final Set<EClassifier> featureTypes) {
    super();
    this.featureTypes = featureTypes;
  }
}
