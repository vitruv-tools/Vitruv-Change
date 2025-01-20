package tools.vitruv.testutils.matchers;

import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.activextendannotations.Utility;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.hamcrest.Matcher;

@Utility
@SuppressWarnings("all")
public final class ModelMatchers {
  public static Matcher<? super Resource> containsModelOf(final Resource expected, final ModelDeepEqualityOption... options) {
    return ModelMatchers.contains(ModelMatchers.checkResourceContent(expected), options);
  }

  private static EObject checkResourceContent(final Resource resource) {
    EObject _xblockexpression = null;
    {
      int _size = resource.getContents().size();
      boolean _greaterThan = (_size > 0);
      URI _uRI = resource.getURI();
      String _plus = ("The resource to compare with must contain a root element, but was empty: " + _uRI);
      Preconditions.checkArgument(_greaterThan, _plus);
      int _size_1 = resource.getContents().size();
      boolean _lessThan = (_size_1 < 2);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("The resource to compare with must contain only one root element, but contained ");
      int _size_2 = resource.getContents().size();
      _builder.append(_size_2);
      _builder.append(": ");
      URI _uRI_1 = resource.getURI();
      _builder.append(_uRI_1);
      Preconditions.checkArgument(_lessThan, _builder);
      _xblockexpression = resource.getContents().get(0);
    }
    return _xblockexpression;
  }

  public static Matcher<? super Resource> contains(final EObject root, final ModelDeepEqualityOption... options) {
    return ModelMatchers.contains(ModelMatchers.<EObject>equalsDeeply(root, options));
  }

  public static Matcher<? super Resource> contains(final Matcher<? super EObject> rootMatcher) {
    return new ResourceContainmentMatcher(rootMatcher);
  }

  /**
   * Checks if for all items from {@paramref searchedItems} a similar one is included in the given list.
   * Formally: assertThat(L1, containsAllOf(L2)) == \forall(x in L2): \exists(y in L1): equals(x,y)
   * @param searchedItems items, of which all should be contained.
   * @param options ...
   */
  public static Matcher<? super Iterable<? extends EObject>> containsAllOf(final Iterable<? extends EObject> searchedItems, final ModelDeepEqualityOption... options) {
    return new EListMultipleContainmentMatcher(searchedItems, true, options);
  }

  /**
   * Checks if for all items from {@paramref searchedItems} no similar one is included in the given list.
   * Formally: assertThat(L1, containsNoneOf(L2)) == \forall(x in L2): \not \exists(y in L1): equals(x,y)
   * @param searchedItems items, of which none should be contained.
   * @param options ...
   */
  public static Matcher<? super Iterable<? extends EObject>> containsNoneOf(final Iterable<? extends EObject> searchedItems, final ModelDeepEqualityOption... options) {
    return new EListMultipleContainmentMatcher(searchedItems, false, options);
  }

  /**
   * Checks if for the item {@paramref searchedItem} a similar one is included in the given list.
   * Formally: assertThat(L1, listContains(e)) == \exists(y in L1): equals(e,y)
   * @param searchedItem item, which should be contained.
   * @param options ...
   */
  public static Matcher<? extends Iterable<? extends EObject>> listContains(final EObject searchedItem, final ModelDeepEqualityOption... options) {
    return new EListSingleContainmentMatcher(searchedItem, true, options);
  }

  public static Matcher<? super URI> isResource() {
    return new ResourceExistingMatcher(true);
  }

  public static Matcher<? super URI> isNoResource() {
    return new ResourceExistingMatcher(false);
  }

  public static Matcher<? super Resource> exists() {
    return new ResourceExistenceMatcher(true);
  }

  public static Matcher<? super Resource> doesNotExist() {
    return new ResourceExistenceMatcher(false);
  }

  public static Matcher<? super EObject> isContainedIn(final Resource resource) {
    return new EObjectResourceMatcher(resource);
  }

  /**
   * A highly configurable matcher for comparing EObjects with detailed difference printing.
   */
  public static <O extends EObject> Matcher<? super O> equalsDeeply(final O object, final ModelDeepEqualityOption... options) {
    return new ModelDeepEqualityMatcher<O>(object, (List<? extends ModelDeepEqualityOption>)Conversions.doWrapArray(options));
  }

  public static EqualityFeatureFilter ignoringFeatures(final EStructuralFeature... features) {
    Set<EStructuralFeature> _of = Set.<EStructuralFeature>of(features);
    return new IgnoreFeatures(_of);
  }

  public static EqualityFeatureFilter ignoringAllFeaturesExcept(final EStructuralFeature... features) {
    Set<EStructuralFeature> _of = Set.<EStructuralFeature>of(features);
    return new IncludeOnlyFeatures(_of);
  }

  public static EqualityFeatureFilter ignoringFeatures(final String... featureNames) {
    Set<String> _of = Set.<String>of(featureNames);
    return new IgnoreNamedFeatures(_of);
  }

  public static EqualityFeatureFilter ignoringAllFeaturesExcept(final String... featureNames) {
    Set<String> _of = Set.<String>of(featureNames);
    return new IgnoreAllExceptNamedFeatures(_of);
  }

  public static EqualityFeatureFilter ignoringFeaturesOfType(final EClassifier... featureTypes) {
    Set<EClassifier> _of = Set.<EClassifier>of(featureTypes);
    return new IgnoreTypedFeatures(_of);
  }

  public static EqualityFeatureFilter ignoringAllExceptFeaturesOfType(final EClassifier... featureTypes) {
    Set<EClassifier> _of = Set.<EClassifier>of(featureTypes);
    return new IgnoreAllExceptTypedFeatures(_of);
  }

  /**
   * Uses {@link Object#equals} to compare instances of the provided {@code eClasses} if they are referenced in
   * <em>non-containment</em> references.
   */
  public static EqualityStrategy usingEqualsForReferencesTo(final EClass... eClasses) {
    Set<EClass> _of = Set.<EClass>of(eClasses);
    return new EqualsBasedEqualityStrategy(_of);
  }

  public static Matcher<? super EObject> whose(final EStructuralFeature feature, final Matcher<?> featureMatcher) {
    return new EObjectFeatureMatcher(feature, featureMatcher);
  }

  public static Matcher<? super Object> isInstanceOf(final EClassifier classifier) {
    return new InstanceOfEClassifierMatcher(classifier);
  }

  public static Matcher<? super Resource> hasNoErrors() {
    return new ResourceHasNoErrorsMatcher();
  }

  private ModelMatchers() {
    
  }
}
