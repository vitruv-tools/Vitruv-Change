package tools.vitruv.testutils.matchers;

import org.eclipse.emf.ecore.EStructuralFeature;

@SuppressWarnings("all")
public interface EqualityFeatureFilter extends ModelDeepEqualityOption {
  boolean includeFeature(final EStructuralFeature feature);
}
