package tools.vitruv.change.testutils.matchers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Tests the mismatch descriptions produced by {@link EObjectFeatureMatcher} for the different
 * feature-matcher and feature kinds it distinguishes.
 */
class EObjectFeatureMatcherTest {

  private static Root root(String id) {
    Root root = AllElementTypesCreators.aet.Root();
    root.setId(id);
    return root;
  }

  private static NonRoot nonRoot(String id) {
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    nonRoot.setId(id);
    return nonRoot;
  }

  private static EStructuralFeature rootFeature(String name) {
    return root("f").eClass().getEStructuralFeature(name);
  }

  private static String describeMismatch(Matcher<?> matcher, Object item) {
    StringDescription description = new StringDescription();
    matcher.describeMismatch(item, description);
    return description.toString();
  }

  @Test
  void describesMismatchWhenItemIsNotAnInstanceOfTheFeaturesClass() {
    Matcher<?> matcher =
        ModelMatchers.whose(rootFeature("singleValuedEAttribute"), CoreMatchers.anything());

    String message = describeMismatch(matcher, nonRoot("n"));

    assertTrue(message.contains("was"), () -> "unexpected mismatch text: " + message);
  }

  @Test
  void describesMismatchForASimpleFeatureMatcher() {
    Matcher<?> matcher = ModelMatchers.whose(
        rootFeature("singleValuedEAttribute"), CoreMatchers.equalTo("expected"));

    String message = describeMismatch(matcher, root("r"));

    assertTrue(message.contains("->singleValuedEAttribute"),
        () -> "unexpected mismatch text: " + message);
  }

  @Test
  void describesMismatchForANestedFeatureMatcher() {
    Matcher<?> nested = ModelMatchers.whose(rootFeature("id"), CoreMatchers.equalTo("x"));
    Matcher<?> matcher = ModelMatchers.whose(rootFeature("singleValuedEAttribute"), nested);

    String message = describeMismatch(matcher, root("r"));

    assertTrue(message.contains("->singleValuedEAttribute"),
        () -> "unexpected mismatch text: " + message);
  }

  @Test
  void describesMismatchForAnOrderedCollectionMatcher() {
    Matcher<?> matcher = ModelMatchers.whose(rootFeature("multiValuedEAttribute"),
        new IsCollectionContaining<Integer>(CoreMatchers.equalTo(1)));

    String message = describeMismatch(matcher, root("r"));

    assertTrue(message.contains("[*]"), () -> "unexpected mismatch text: " + message);
  }

  @Test
  void describesMismatchForAnUnorderedCollectionMatcher() {
    Matcher<?> matcher = ModelMatchers.whose(rootFeature("multiValuedUnorderedEAttribute"),
        new IsCollectionContaining<Integer>(CoreMatchers.equalTo(1)));

    String message = describeMismatch(matcher, root("r"));

    assertTrue(message.contains("{*}"), () -> "unexpected mismatch text: " + message);
  }
}
