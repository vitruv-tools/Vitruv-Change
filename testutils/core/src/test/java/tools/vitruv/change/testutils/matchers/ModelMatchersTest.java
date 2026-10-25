package tools.vitruv.change.testutils.matchers;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Tests the factory methods exposed by {@link ModelMatchers}.
 */
class ModelMatchersTest {
  @Test
  void containsModelOfMatchesDeeplyEqualResource(final @TempDir Path testFolder)
      throws IOException {
    final Resource expected = this.resource(testFolder.resolve("expected.aet"));
    expected.getContents().add(root("root"));

    final Resource actual = this.resource(testFolder.resolve("actual.aet"));
    actual.getContents().add(root("root"));
    actual.save(Map.of());

    MatcherAssert.assertThat(actual, ModelMatchers.containsModelOf(expected));
  }

  @Test
  void containsMatchesDeeplyEqualRoot(final @TempDir Path testFolder) throws IOException {
    final Resource resource = this.resource(testFolder.resolve("model.aet"));
    resource.getContents().add(root("root"));
    resource.save(Map.of());

    MatcherAssert.assertThat(resource, ModelMatchers.contains(root("root")));
  }

  @Test
  void containsAcceptsRootMatcher(final @TempDir Path testFolder) throws IOException {
    final Resource resource = this.resource(testFolder.resolve("model.aet"));
    final Root root = root("root");
    resource.getContents().add(root);
    resource.save(Map.of());

    MatcherAssert.assertThat(resource, ModelMatchers.contains(CoreMatchers.sameInstance(root)));
  }

  @Test
  void iterableMatchersAcceptTypedLists() {
    final NonRoot first = nonRoot("first");
    final NonRoot second = nonRoot("second");
    final List<NonRoot> contents = List.of(first);

    MatcherAssert.assertThat(contents, ModelMatchers.containsAllOf(List.of(nonRoot("first"))));
    MatcherAssert.assertThat(contents, ModelMatchers.containsNoneOf(List.of(second)));
    MatcherAssert.assertThat(contents, ModelMatchers.listContains(nonRoot("first")));
  }

  @Test
  void resourceUriMatchersUseExistingFiles(final @TempDir Path testFolder)
      throws IOException {
    final Resource existingResource = this.resource(testFolder.resolve("existing.aet"));
    existingResource.getContents().add(root("root"));
    existingResource.save(Map.of());

    final Resource missingResource = this.resource(testFolder.resolve("missing.aet"));

    MatcherAssert.assertThat(existingResource.getURI(), ModelMatchers.isResource());
    MatcherAssert.assertThat(missingResource.getURI(), ModelMatchers.isNoResource());
    MatcherAssert.assertThat(existingResource, ModelMatchers.exists());
    MatcherAssert.assertThat(missingResource, ModelMatchers.doesNotExist());
  }

  @Test
  void objectMatchersMatchModelProperties(final @TempDir Path testFolder)
      throws IOException {
    final Resource resource = this.resource(testFolder.resolve("model.aet"));
    final Root root = root("root");
    resource.getContents().add(root);
    resource.save(Map.of());

    MatcherAssert.assertThat(root, ModelMatchers.isContainedIn(resource));
    MatcherAssert.assertThat(root, ModelMatchers.<Root>equalsDeeply(root("root")));
    MatcherAssert.assertThat(
        root,
        ModelMatchers.whose(
            AllElementTypesPackage.Literals.IDENTIFIED__ID,
            CoreMatchers.is("root")));
    MatcherAssert.assertThat(
        root,
        ModelMatchers.isInstanceOf(AllElementTypesPackage.Literals.ROOT));
  }

  @Test
  void hasNoErrorsMatchesResourceWithoutDiagnostics(final @TempDir Path testFolder) {
    final Resource resource = this.resource(testFolder.resolve("model.aet"));

    MatcherAssert.assertThat(resource, ModelMatchers.hasNoErrors());
  }

  private Resource resource(final Path path) {
    final ResourceSet resourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    return resourceSet.createResource(URI.createFileURI(path.toString()));
  }

  private static Root root(final String id) {
    final Root root = AllElementTypesCreators.aet.Root();
    root.setId(id);
    return root;
  }

  private static NonRoot nonRoot(final String id) {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    nonRoot.setId(id);
    return nonRoot;
  }
}
