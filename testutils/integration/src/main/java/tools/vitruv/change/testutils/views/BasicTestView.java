package tools.vitruv.change.testutils.views;

import static com.google.common.base.Preconditions.checkArgument;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil.createFileURI;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.loadOrCreateResource;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.move;
import static org.eclipse.emf.common.util.URI.createPlatformResourceURI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.testutils.TestUserInteraction;

/**
 * A minimal test view that gives access to resources, but does not record any changes.
 */
public class BasicTestView implements TestView {
  private final Path persistenceDirectory;
  private final ResourceSet resourceSet;
  @Getter(value = AccessLevel.PUBLIC)
  private final TestUserInteraction userInteraction;
  private final UriMode uriMode;

  /**
   * Creates a test view that will store its persisted resources in the
   * provided {@code persistenceDirectory}, and use the provided {@code uriMode}.
   */
  public BasicTestView(Path persistenceDirectory, UriMode uriMode) {
    this(persistenceDirectory, new TestUserInteraction(), uriMode);
  }

  /**
   * Creates a test view that will store its persisted resources in the provided 
   * {@code persistenceDirectory}, allow to program interactions through the provided 
   * {@code userInteraction}, and use the provided {@code uriMode}.
   */
  public BasicTestView(
      Path persistenceDirectory,
      TestUserInteraction userInteraction,
      UriMode uriMode
  ) {
    this(
        persistenceDirectory,
        withGlobalFactories(new ResourceSetImpl()),
        userInteraction,
        uriMode
    );
  }

  /**
   * Creates a test view that will store its persisted resources in the provided 
   * {@code persistenceDirectory}, access resources through the provided {@code resourceSet},
   * allow to program interactions through the provided {@code userInteraction},
   * and use the provided {@code uriMode}.
   */
  public BasicTestView(
      Path persistenceDirectory,
      ResourceSet resourceSet,
      TestUserInteraction userInteraction,
      UriMode uriMode
  ) {
    this.persistenceDirectory = persistenceDirectory;
    this.resourceSet = resourceSet;
    this.userInteraction = userInteraction;
    this.uriMode = uriMode;
  }

  @Override
  public Resource resourceAt(URI modelUri) {
    return loadOrCreateResource(resourceSet, modelUri);
  }

  @Override
  public <T extends EObject> T from(Class<T> clazz, URI modelUri) {
    Resource resource = resourceSet.getResource(modelUri, true);
    return this.from(clazz, resource);
  }

  @Override
  public <T extends Notifier> T record(T notifier, Consumer<T> consumer) {
    consumer.accept(notifier);
    return notifier;
  }

  @Override
  public <T extends Notifier> List<PropagatedChange> propagate(T notifier, Consumer<T> consumer) {
    Resource resourceToSave = determineResource(notifier);
    consumer.accept(notifier);
    if (resourceToSave != null) {
      saveOrDelete(resourceToSave);
    }
    return Collections.emptyList();
  }

  @Override
  public void moveTo(Resource resource, Path newViewRelativePath) {
    moveSafelyTo(getAbsolutePath(resource.getURI()), 
        persistenceDirectory.resolve(newViewRelativePath));
    resource.setURI(getUri(newViewRelativePath));
  }

  @Override
  public void moveTo(Resource resource, URI newUri) {
    moveSafelyTo(getAbsolutePath(resource.getURI()), getAbsolutePath(newUri));
    resource.setURI(newUri);
  }

  private void moveSafelyTo(Path source, Path target) {
    try {
      createDirectories(target.getParent());
      move(source, target);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void saveOrDelete(Resource resource) {
    try {
      if (resource.getContents().isEmpty()) {
        resource.delete(Collections.emptyMap());
      } else {
        resource.save(Collections.emptyMap());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public URI getUri(Path viewRelativePath) {
    checkArgument(viewRelativePath != null, "The viewRelativePath must not be null!");
    checkArgument(viewRelativePath.iterator().hasNext(), "The viewRelativePath must not be empty!");
    switch (uriMode) {
      case PLATFORM_URIS: 
        // platform URIs must always use '/' 
        // and be relative to the project (fileName) rather than the workspace
        return createPlatformResourceURI(
            persistenceDirectory.getFileName().resolve(viewRelativePath).normalize().toString()
            + "/", 
            true);
      case FILE_URIS:
        return createFileURI(persistenceDirectory.resolve(viewRelativePath).normalize().toFile());
      default:
        return null;
    }
  }

  private Path getAbsolutePath(URI uri) {
    if (uri.isFile()) {
      checkArgument(uri.hasAbsolutePath(), 
          uri + " is a file URI but does not have an absolute path!");
      return Path.of(URI.decode(uri.path()));
    } 
    if (uri.isPlatformResource()) {
      return persistenceDirectory.resolveSibling(uri.toPlatformString(true));
    }
    throw new IllegalArgumentException("This URI is not supported by this view: " + uri);
  }

  private Resource determineResource(Notifier notifier) {
    if (notifier instanceof Resource resource) {
      return resource;
    }
    if (notifier instanceof EObject eObject) {
      return eObject.eResource();
    }
    return null;
  }

  @Override
  public void close() throws Exception {
    resourceSet.getResources().forEach(Resource::unload);
    resourceSet.getResources().clear();
  }
}
