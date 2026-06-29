  package tools.vitruv.change.testutils.matchers;

  import static org.hamcrest.MatcherAssert.assertThat;
  import static org.hamcrest.Matchers.any;
  import static org.hamcrest.Matchers.equalTo;
  import static org.hamcrest.Matchers.is;
  import static org.hamcrest.Matchers.not;
  import static org.hamcrest.Matchers.notNullValue;
  import static org.junit.jupiter.api.Assertions.assertThrows;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.contains;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.containsAllOf;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.containsModelOf;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.containsNoneOf;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.doesNotExist;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.equalsDeeply;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.exists;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.hasNoErrors;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.ignoringAllExceptFeaturesOfType;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.ignoringAllFeaturesExcept;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.ignoringFeatures;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.ignoringFeaturesOfType;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.isContainedIn;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.isInstanceOf;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.isNoResource;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.isResource;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.listContains;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.usingEqualsForReferencesTo;
  import static tools.vitruv.change.testutils.matchers.ModelMatchers.whose;

  import java.util.List;
  import org.eclipse.emf.common.util.URI;
  import org.eclipse.emf.ecore.EClass;
  import org.eclipse.emf.ecore.EObject;
  import org.eclipse.emf.ecore.EStructuralFeature;
  import org.eclipse.emf.ecore.EcoreFactory;
  import org.eclipse.emf.ecore.EcorePackage;
  import org.eclipse.emf.ecore.resource.Resource;
  import org.eclipse.emf.ecore.resource.ResourceSet;
  import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
  import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
  import org.hamcrest.Matcher;
  import org.junit.jupiter.api.BeforeEach;
  import org.junit.jupiter.api.DisplayName;
  import org.junit.jupiter.api.Nested;
  import org.junit.jupiter.api.Test;

  /**
   * Unit tests for {@link ModelMatchers}.
   */
  @DisplayName("ModelMatchers")
  class ModelMatchersTest {

    private static final String URI_EXPECTED = "platform:/expected.xmi";
    private static final String URI_ACTUAL = "platform:/actual.xmi";
    private static final String URI_RESOURCE = "platform:/r.xmi";
    private static final String URI_RESOURCE_2 = "platform:/r2.xmi";
    private static final String URI_RESOURCE_1 = "platform:/r1.xmi";
    private static final String URI_EMPTY = "platform:/empty.xmi";
    private static final String URI_MULTI = "platform:/multi.xmi";
    private static final String URI_CLEAN = "platform:/clean.xmi";
    private static final String URI_BROKEN = "platform:/broken.xmi";
    private static final String FEATURE_NAME_VALUE = "Test";
    private static final String FEATURE_NAME = "name";

    private ResourceSet resourceSet;

    @BeforeEach
    void setUp() {
      resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry()
          .getExtensionToFactoryMap()
          .put("xmi", new XMIResourceFactoryImpl());
    }

    // -------------------------------------------------------------------------
    // Helper factories
    // -------------------------------------------------------------------------

    /**
     * Creates a Resource registered in the ResourceSet with a single EClass root element.
     */
    private Resource resourceWithSingleRoot(final String uriStr) {
      Resource resource = resourceSet.createResource(URI.createURI(uriStr));
      EClass root = EcoreFactory.eINSTANCE.createEClass();
      root.setName("Root");
      resource.getContents().add(root);
      return resource;
    }

    /**
     * Creates an empty Resource registered in the ResourceSet (no contents).
     */
    private Resource emptyResource(final String uriStr) {
      return resourceSet.createResource(URI.createURI(uriStr));
    }

    /**
     * Creates a Resource registered in the ResourceSet with two root elements.
     */
    private Resource resourceWithTwoRoots(final String uriStr) {
      Resource resource = resourceSet.createResource(URI.createURI(uriStr));
      resource.getContents().add(EcoreFactory.eINSTANCE.createEClass());
      resource.getContents().add(EcoreFactory.eINSTANCE.createEClass());
      return resource;
    }

    /**
     * Creates a simple {@link Resource.Diagnostic} with the given message.
     */
    private Resource.Diagnostic diagnosticWithMessage(final String message) {
      return new Resource.Diagnostic() {
        @Override
        public String getMessage() {
          return message;
        }

        @Override
        public String getLocation() {
          return "test://location";
        }

        @Override
        public int getLine() {
          return 1;
        }

        @Override
        public int getColumn() {
          return 0;
        }
      };
    }

    // =========================================================================
    // containsModelOf
    // =========================================================================

    @Nested
    @DisplayName("containsModelOf(Resource, options...)")
    class ContainsModelOfTest {

      @Test
      @DisplayName("returns a non-null matcher for a valid single-root resource")
      void returnsNonNullMatcherForValidResource() {
        Resource expected = resourceWithSingleRoot(URI_EXPECTED);
        Matcher<? super Resource> matcher = containsModelOf(expected);
        assertThat(matcher, is(notNullValue()));
      }

      @Test
      @DisplayName("throws IllegalArgumentException when expected resource is empty")
      void throwsWhenExpectedResourceIsEmpty() {
        Resource empty = emptyResource(URI_EMPTY);
        assertThrows(IllegalArgumentException.class, () -> containsModelOf(empty));
      }

      @Test
      @DisplayName("throws IllegalArgumentException when expected resource has more than one root")
      void throwsWhenExpectedResourceHasMultipleRoots() {
        Resource multi = resourceWithTwoRoots(URI_MULTI);
        assertThrows(IllegalArgumentException.class, () -> containsModelOf(multi));
      }

      @Test
      @DisplayName("matcher matches resource with equal root content")
      void matchesResourceWithEqualContent() {
        Resource expected = resourceWithSingleRoot(URI_EXPECTED);
        Resource actual = resourceWithSingleRoot(URI_ACTUAL);
        assertThat(actual, containsModelOf(expected));
      }
    }

    // =========================================================================
    // contains(EObject, options...)
    // =========================================================================

    @Nested
    @DisplayName("contains(EObject, options...)")
    class ContainsEObjectTest {

      @Test
      @DisplayName("returns a non-null matcher")
      void returnsNonNullMatcher() {
        EObject root = EcoreFactory.eINSTANCE.createEClass();
        assertThat(contains(root), is(notNullValue()));
      }

      @Test
      @DisplayName("matcher matches a resource whose single root equals the expected object")
      void matchesResourceWithMatchingRoot() {
        EClass expected = EcoreFactory.eINSTANCE.createEClass();
        expected.setName("MyClass");
        EClass actual = EcoreFactory.eINSTANCE.createEClass();
        actual.setName("MyClass");
        Resource resource = resourceSet.createResource(URI.createURI(URI_RESOURCE));
        resource.getContents().add(actual);
        assertThat(resource, contains(expected));
      }

      @Test
      @DisplayName("matcher does NOT match a resource with a differently-named root")
      void doesNotMatchResourceWithDifferentRoot() {
        EClass expected = EcoreFactory.eINSTANCE.createEClass();
        expected.setName("A");
        EClass differentRoot = EcoreFactory.eINSTANCE.createEClass();
        differentRoot.setName("B");
        Resource resource = resourceSet.createResource(URI.createURI(URI_RESOURCE_2));
        resource.getContents().add(differentRoot);
        assertThat(resource, not(contains(expected)));
      }
    }

    // =========================================================================
    // contains(Matcher<? super EObject>)
    // =========================================================================

    @Nested
    @DisplayName("contains(Matcher)")
    class ContainsMatcherTest {

      @Test
      @DisplayName("returns a non-null ResourceContainmentMatcher")
      void returnsNonNullMatcher() {
        Matcher<? super Resource> matcher = contains(any(EObject.class));
        assertThat(matcher, is(notNullValue()));
      }
    }

    // =========================================================================
    // containsAllOf / containsNoneOf / listContains
    // =========================================================================

    @Nested
    @DisplayName("containsAllOf / containsNoneOf / listContains")
    class CollectionMatchersTest {

      private EClass eClassA;
      private EClass eClassB;

      @BeforeEach
      void buildObjects() {
        eClassA = EcoreFactory.eINSTANCE.createEClass();
        eClassA.setName("A");
        eClassB = EcoreFactory.eINSTANCE.createEClass();
        eClassB.setName("B");
      }

      @Test
      @DisplayName("containsAllOf returns non-null matcher")
      void containsAllOfReturnsNonNull() {
        assertThat(containsAllOf(List.of(eClassA)), is(notNullValue()));
      }

      @Test
      @DisplayName("containsNoneOf returns non-null matcher")
      void containsNoneOfReturnsNonNull() {
        assertThat(containsNoneOf(List.of(eClassA)), is(notNullValue()));
      }

      @Test
      @DisplayName("listContains returns non-null matcher")
      void listContainsReturnsNonNull() {
        assertThat(listContains(eClassA), is(notNullValue()));
      }

      @Test
      @DisplayName("containsAllOf matches when all searched items are present")
      void containsAllOfMatchesWhenAllPresent() {
        EClass copyA = EcoreFactory.eINSTANCE.createEClass();
        copyA.setName("A");
        EClass copyB = EcoreFactory.eINSTANCE.createEClass();
        copyB.setName("B");
        assertThat(List.of(copyA, copyB), containsAllOf(List.of(eClassA, eClassB)));
      }

      @Test
      @DisplayName("containsAllOf does NOT match when a searched item is absent")
      void containsAllOfDoesNotMatchWhenItemAbsent() {
        EClass copyA = EcoreFactory.eINSTANCE.createEClass();
        copyA.setName("A");
        assertThat(List.of(copyA), not(containsAllOf(List.of(eClassA, eClassB))));
      }

      @Test
      @DisplayName("containsNoneOf matches when none of the searched items is present")
      void containsNoneOfMatchesWhenNonePresent() {
        EClass copyC = EcoreFactory.eINSTANCE.createEClass();
        copyC.setName("C");
        assertThat(List.of(copyC), containsNoneOf(List.of(eClassA, eClassB)));
      }

      @Test
      @DisplayName("containsNoneOf does NOT match when a searched item is present")
      void containsNoneOfDoesNotMatchWhenItemPresent() {
        EClass copyA = EcoreFactory.eINSTANCE.createEClass();
        copyA.setName("A");
        assertThat(List.of(copyA), not(containsNoneOf(List.of(eClassA))));
      }

      @Test
      @DisplayName("listContains matches when the item is present in the list")
      void listContainsMatchesWhenPresent() {
        EClass copyA = EcoreFactory.eINSTANCE.createEClass();
        copyA.setName("A");
        assertThat(List.of(copyA, eClassB), listContains(eClassA));
      }

      @Test
      @DisplayName("listContains does NOT match when the item is absent")
      void listContainsDoesNotMatchWhenAbsent() {
        EClass copyB = EcoreFactory.eINSTANCE.createEClass();
        copyB.setName("B");
        assertThat(List.of(copyB), not(listContains(eClassA)));
      }
    }

    // =========================================================================
    // URI / Resource existence matchers
    // =========================================================================

    @Nested
    @DisplayName("isResource / isNoResource / exists / doesNotExist")
    class ExistenceMatchersTest {

      @Test
      @DisplayName("isResource() returns a non-null matcher")
      void isResourceReturnsNonNull() {
        assertThat(isResource(), is(notNullValue()));
      }

      @Test
      @DisplayName("isNoResource() returns a non-null matcher")
      void isNoResourceReturnsNonNull() {
        assertThat(isNoResource(), is(notNullValue()));
      }

      @Test
      @DisplayName("exists() returns a non-null matcher")
      void existsReturnsNonNull() {
        assertThat(exists(), is(notNullValue()));
      }

      @Test
      @DisplayName("doesNotExist() returns a non-null matcher")
      void doesNotExistReturnsNonNull() {
        assertThat(doesNotExist(), is(notNullValue()));
      }
    }

    // =========================================================================
    // isContainedIn
    // =========================================================================

    @Nested
    @DisplayName("isContainedIn(Resource)")
    class IsContainedInTest {

      @Test
      @DisplayName("returns a non-null matcher")
      void returnsNonNull() {
        Resource resource = resourceSet.createResource(URI.createURI(URI_RESOURCE));
        assertThat(isContainedIn(resource), is(notNullValue()));
      }

      @Test
      @DisplayName("matches an EObject that belongs to the given resource")
      void matchesObjectInResource() {
        Resource resource = resourceSet.createResource(URI.createURI(URI_RESOURCE));
        EClass obj = EcoreFactory.eINSTANCE.createEClass();
        resource.getContents().add(obj);
        assertThat(obj, isContainedIn(resource));
      }

      @Test
      @DisplayName("does NOT match an EObject from a different resource")
      void doesNotMatchObjectFromDifferentResource() {
        Resource resource1 = resourceSet.createResource(URI.createURI(URI_RESOURCE_1));
        Resource resource2 = resourceSet.createResource(URI.createURI(URI_RESOURCE_2));
        EClass obj = EcoreFactory.eINSTANCE.createEClass();
        resource1.getContents().add(obj);
        assertThat(obj, not(isContainedIn(resource2)));
      }
    }

    // =========================================================================
    // equalsDeeply
    // =========================================================================

    @Nested
    @DisplayName("equalsDeeply(EObject, options...)")
    class EqualsDeeplyTest {

      @Test
      @DisplayName("returns a non-null matcher")
      void returnsNonNull() {
        EObject obj = EcoreFactory.eINSTANCE.createEClass();
        assertThat(equalsDeeply(obj), is(notNullValue()));
      }

      @Test
      @DisplayName("matches structurally equal EObjects")
      void matchesEqualObjects() {
        EClass a = EcoreFactory.eINSTANCE.createEClass();
        a.setName("Same");
        EClass b = EcoreFactory.eINSTANCE.createEClass();
        b.setName("Same");
        assertThat(b, equalsDeeply(a));
      }

      @Test
      @DisplayName("does NOT match structurally different EObjects")
      void doesNotMatchDifferentObjects() {
        EClass a = EcoreFactory.eINSTANCE.createEClass();
        a.setName("X");
        EClass b = EcoreFactory.eINSTANCE.createEClass();
        b.setName("Y");
        assertThat(b, not(equalsDeeply(a)));
      }
    }

    // =========================================================================
    // Feature filter factory methods
    // =========================================================================

    @Nested
    @DisplayName("Feature filter factories")
    class FeatureFilterFactoriesTest {

      private EStructuralFeature nameFeature;

      @BeforeEach
      void resolveFeature() {
        nameFeature = EcorePackage.Literals.ENAMED_ELEMENT__NAME;
      }

      @Test
      @DisplayName("ignoringFeatures(EStructuralFeature...) returns non-null filter")
      void ignoringFeaturesReturnsNonNull() {
        assertThat(ignoringFeatures(nameFeature), is(notNullValue()));
      }

      @Test
      @DisplayName("ignoringAllFeaturesExcept(EStructuralFeature...) returns non-null filter")
      void ignoringAllFeaturesExceptReturnsNonNull() {
        assertThat(ignoringAllFeaturesExcept(nameFeature), is(notNullValue()));
      }

      @Test
      @DisplayName("ignoringFeatures(String...) returns non-null filter")
      void ignoringFeaturesByNameReturnsNonNull() {
        assertThat(ignoringFeatures(FEATURE_NAME), is(notNullValue()));
      }

      @Test
      @DisplayName("ignoringAllFeaturesExcept(String...) returns non-null filter")
      void ignoringAllFeaturesExceptByNameReturnsNonNull() {
        assertThat(ignoringAllFeaturesExcept(FEATURE_NAME), is(notNullValue()));
      }

      @Test
      @DisplayName("ignoringFeaturesOfType(EClassifier...) returns non-null filter")
      void ignoringFeaturesOfTypeReturnsNonNull() {
        assertThat(ignoringFeaturesOfType(EcorePackage.Literals.ESTRING), is(notNullValue()));
      }

      @Test
      @DisplayName("ignoringAllExceptFeaturesOfType(EClassifier...) returns non-null filter")
      void ignoringAllExceptFeaturesOfTypeReturnsNonNull() {
        assertThat(
            ignoringAllExceptFeaturesOfType(EcorePackage.Literals.ESTRING),
            is(notNullValue()));
      }

      @Test
      @DisplayName("ignoringFeatures option is respected in equalsDeeply comparison")
      void ignoringFeaturesByNameIsRespectedInEqualsDeeply() {
        EClass a = EcoreFactory.eINSTANCE.createEClass();
        a.setName("NameA");
        EClass b = EcoreFactory.eINSTANCE.createEClass();
        b.setName("NameB");
        assertThat(b, not(equalsDeeply(a)));
        assertThat(b, equalsDeeply(a, ignoringFeatures(FEATURE_NAME)));
      }
    }

    // =========================================================================
    // usingEqualsForReferencesTo
    // =========================================================================

    @Nested
    @DisplayName("usingEqualsForReferencesTo(EClass...)")
    class UsingEqualsForReferencesToTest {

      @Test
      @DisplayName("returns a non-null EqualityStrategy")
      void returnsNonNull() {
        EClass eClass = EcorePackage.Literals.ECLASS;
        assertThat(usingEqualsForReferencesTo(eClass), is(notNullValue()));
      }
    }

    // =========================================================================
    // whose
    // =========================================================================

    @Nested
    @DisplayName("whose(EStructuralFeature, Matcher)")
    class WhoseTest {

      @Test
      @DisplayName("returns a non-null matcher")
      void returnsNonNull() {
        Matcher<? super EObject> matcher =
            whose(EcorePackage.Literals.ENAMED_ELEMENT__NAME, equalTo(FEATURE_NAME_VALUE));
        assertThat(matcher, is(notNullValue()));
      }

      @Test
      @DisplayName("matches EObject whose feature value satisfies the inner matcher")
      void matchesObjectWithMatchingFeatureValue() {
        EClass obj = EcoreFactory.eINSTANCE.createEClass();
        obj.setName(FEATURE_NAME_VALUE);
        assertThat(obj,
            whose(EcorePackage.Literals.ENAMED_ELEMENT__NAME, equalTo(FEATURE_NAME_VALUE)));
      }

      @Test
      @DisplayName("does NOT match EObject whose feature value does not satisfy the inner matcher")
      void doesNotMatchObjectWithNonMatchingFeatureValue() {
        EClass obj = EcoreFactory.eINSTANCE.createEClass();
        obj.setName("Other");
        assertThat(obj,
            not(whose(EcorePackage.Literals.ENAMED_ELEMENT__NAME, equalTo(FEATURE_NAME_VALUE))));
      }
    }

    // =========================================================================
    // isInstanceOf
    // =========================================================================

    @Nested
    @DisplayName("isInstanceOf(EClassifier)")
    class IsInstanceOfTest {

      @Test
      @DisplayName("returns a non-null matcher")
      void returnsNonNull() {
        assertThat(isInstanceOf(EcorePackage.Literals.ECLASS), is(notNullValue()));
      }

      @Test
      @DisplayName("matches an EObject that is an instance of the given EClassifier")
      void matchesCorrectInstance() {
        EClass obj = EcoreFactory.eINSTANCE.createEClass();
        assertThat(obj, isInstanceOf(EcorePackage.Literals.ECLASS));
      }

      @Test
      @DisplayName("does NOT match an EObject of a different EClass")
      void doesNotMatchWrongType() {
        EClass obj = EcoreFactory.eINSTANCE.createEClass();
        assertThat(obj, not(isInstanceOf(EcorePackage.Literals.EPACKAGE)));
      }
    }

    // =========================================================================
    // hasNoErrors
    // =========================================================================

    @Nested
    @DisplayName("hasNoErrors()")
    class HasNoErrorsTest {

      @Test
      @DisplayName("returns a non-null matcher")
      void returnsNonNull() {
        assertThat(hasNoErrors(), is(notNullValue()));
      }

      @Test
      @DisplayName("matches a resource that has no diagnostic errors")
      void matchesResourceWithNoErrors() {
        Resource resource = resourceSet.createResource(URI.createURI(URI_CLEAN));
        assertThat(resource, hasNoErrors());
      }

      @Test
      @DisplayName("does NOT match a resource that has diagnostic errors")
      void doesNotMatchResourceWithErrors() {
        Resource resource = resourceSet.createResource(URI.createURI(URI_BROKEN));
        resource.getErrors().add(diagnosticWithMessage("Synthetic error"));
        assertThat(resource, not(hasNoErrors()));
      }
    }
  }