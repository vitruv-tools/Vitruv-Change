package tools.vitruv.change.testutils.matchers;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.hamcrest.Matcher;

public final class ModelMatchers {
  /**
   * Matches resources that contain a model deeply equal to the expected resource.
   */
  public static Matcher<Resource> containsModelOf(final Resource expected,
      final ModelDeepEqualityOption... options) {
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
      String _msg = "The resource to compare with must contain only one root element, but contained "
          + resource.getContents().size() + ": " + resource.getURI();
      Preconditions.checkArgument(_lessThan, _msg);
      _xblockexpression = resource.getContents().get(0);
    }
    return _xblockexpression;
  }

  /**
   * Matches resources that contain a root object deeply equal to {@code root}.
   */
  public static Matcher<Resource> contains(final EObject root,
      final ModelDeepEqualityOption... options) {
    return ModelMatchers.contains(ModelMatchers.<EObject>equalsDeeply(root, options));
  }

  /**
   * Matches resources whose single root object satisfies the given matcher.
   */
  public static Matcher<Resource> contains(final Matcher<? super EObject> rootMatcher) {
    return new ResourceContainmentMatcher(rootMatcher);
  }

  /**
   * Checks if for all items from {@code searchedItems} a similar one is included
   * in the given list.
   * Formally: assertThat(L1, containsAllOf(L2)) == \forall(x in L2): \exists(y in
   * L1): equals(x,y)
   * 
   * @param searchedItems items, of which all should be contained.
   * @param options       ...
   */
  public static <T extends Iterable<? extends EObject>> Matcher<T> containsAllOf(
      final Iterable<? extends EObject> searchedItems, final ModelDeepEqualityOption... options) {
    return ModelMatchers.<T>asIterableMatcher(
        new EListMultipleContainmentMatcher(searchedItems, true, options));
  }

  /**
   * Checks if for all items from {@code searchedItems} no similar one is included
   * in the given list.
   * Formally: assertThat(L1, containsNoneOf(L2)) == \forall(x in L2): \not
   * \exists(y in L1): equals(x,y)
   * 
   * @param searchedItems items, of which none should be contained.
   * @param options       ...
   */
  public static <T extends Iterable<? extends EObject>> Matcher<T> containsNoneOf(
      final Iterable<? extends EObject> searchedItems, final ModelDeepEqualityOption... options) {
    return ModelMatchers.<T>asIterableMatcher(
        new EListMultipleContainmentMatcher(searchedItems, false, options));
  }

  /**
   * Checks if for the item {@code searchedItem} a similar one is included in the
   * given list.
   * Formally: assertThat(L1, listContains(e)) == \exists(y in L1): equals(e,y)
   * 
   * @param searchedItem item, which should be contained.
   * @param options      ...
   */
  public static <T extends Iterable<? extends EObject>> Matcher<T> listContains(
      final EObject searchedItem, final ModelDeepEqualityOption... options) {
    return ModelMatchers.<T>asIterableMatcher(
        new EListSingleContainmentMatcher(searchedItem, true, options));
  }

  private static <T extends Iterable<? extends EObject>> Matcher<T> asIterableMatcher(
      final Matcher<Iterable<? extends EObject>> matcher) {
    @SuppressWarnings("unchecked")
    final Matcher<T> typedMatcher = (Matcher<T>) matcher;
    return typedMatcher;
  }

  /**
   * Matches URIs that point to existing resources.
   */
  public static Matcher<URI> isResource() {
    return new ResourceExistingMatcher(true);
  }

  /**
   * Matches URIs that do not point to existing resources.
   */
  public static Matcher<URI> isNoResource() {
    return new ResourceExistingMatcher(false);
  }

  /**
   * Matches resources whose URI exists.
   */
  public static Matcher<Resource> exists() {
    return new ResourceExistenceMatcher(true);
  }

  /**
   * Matches resources whose URI does not exist.
   */
  public static Matcher<Resource> doesNotExist() {
    return new ResourceExistenceMatcher(false);
  }

  /**
   * Matches objects contained in the given resource.
   */
  public static Matcher<EObject> isContainedIn(final Resource resource) {
    return new EObjectResourceMatcher(resource);
  }

  /**
   * A highly configurable matcher for comparing EObjects with detailed difference
   * printing.
   */
  public static <O extends EObject> Matcher<O> equalsDeeply(final O object,
      final ModelDeepEqualityOption... options) {
    return new ModelDeepEqualityMatcher<O>(object, List.of(options));
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
   * Uses {@link Object#equals} to compare instances of the provided
   * {@code eClasses} if they are referenced in
   * <em>non-containment</em> references.
   */
  public static EqualityStrategy usingEqualsForReferencesTo(final EClass... eClasses) {
    Set<EClass> _of = Set.<EClass>of(eClasses);
    return new EqualsBasedEqualityStrategy(_of);
  }

  /**
   * Matches objects whose feature value satisfies the given matcher.
   */
  public static Matcher<EObject> whose(final EStructuralFeature feature,
      final Matcher<?> featureMatcher) {
    return new EObjectFeatureMatcher(feature, featureMatcher);
  }

  /**
   * Matches values that are instances of the given Ecore classifier.
   */
  public static Matcher<Object> isInstanceOf(final EClassifier classifier) {
    return new InstanceOfEClassifierMatcher(classifier);
  }

  /**
   * Matches resources without diagnostics.
   */
  public static Matcher<Resource> hasNoErrors() {
    return new ResourceHasNoErrorsMatcher();
  }

  private ModelMatchers() {

  }
}
