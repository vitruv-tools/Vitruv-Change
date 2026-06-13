package tools.vitruv.change.testutils.views;

import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
  private final TestUserInteraction userInteraction;

  private final UriMode uriMode;

  /**
   * Creates a test view that will store its persisted resources in the
   * provided {@code persistenceDirectory}, and use the provided {@code uriMode}.
   */
  public BasicTestView(final Path persistenceDirectory, final UriMode uriMode) {
    this(persistenceDirectory, new TestUserInteraction(), uriMode);
  }

  /**
   * Creates a test view that will store its persisted resources in the
   * provided {@code persistenceDirectory}, allow to program interactions through the provided {@code userInteraction},
   * and use the provided {@code uriMode}.
   */
  public BasicTestView(final Path persistenceDirectory, final TestUserInteraction userInteraction, final UriMode uriMode) {
    this(persistenceDirectory,
      ResourceSetUtil.withGlobalFactories(new ResourceSetImpl()), userInteraction, uriMode);
  }

  /**
   * Creates a test view that will store its persisted resources in the provided {@code persistenceDirectory}, access
   * resources through the provided {@code resourceSet}, allow to program interactions through the provided
   * {@code userInteraction}, and use the provided {@code uriMode}.
   */
  public BasicTestView(final Path persistenceDirectory, final ResourceSet resourceSet, final TestUserInteraction userInteraction, final UriMode uriMode) {
    this.persistenceDirectory = persistenceDirectory;
    this.resourceSet = resourceSet;
    this.userInteraction = userInteraction;
    this.uriMode = uriMode;
  }

  @Override
  public Resource resourceAt(final URI modelUri) {
    return ResourceSetUtil.loadOrCreateResource(this.resourceSet, modelUri);
  }

  @Override
  public <T extends EObject> T from(final Class<T> clazz, final URI modelUri) {
    final Resource resource = this.resourceSet.getResource(modelUri, true);
    return this.<T>from(clazz, resource);
  }

  @Override
  public <T extends Notifier> T record(final T notifier, final Consumer<T> consumer) {
    consumer.accept(notifier);
    return notifier;
  }

  @Override
  public <T extends Notifier> List<PropagatedChange> propagate(final T notifier, final Consumer<T> consumer) {
    final Resource toSave = this.determineResource(notifier);
    consumer.accept(notifier);
    final Resource toSaveOrDetermined = (toSave != null) ? toSave : this.determineResource(notifier);
    if (toSaveOrDetermined != null) {
      this.saveOrDelete(toSaveOrDetermined);
    }
    return List.of();
  }

  @Override
  public void moveTo(final Resource resource, final Path newViewRelativePath) {
    this.moveSafelyTo(this.getAbsolutePath(resource.getURI()), this.persistenceDirectory.resolve(newViewRelativePath));
    resource.setURI(this.getUri(newViewRelativePath));
  }

  @Override
  public void moveTo(final Resource resource, final URI newUri) {
    this.moveSafelyTo(this.getAbsolutePath(resource.getURI()), this.getAbsolutePath(newUri));
    resource.setURI(newUri);
  }

  private void moveSafelyTo(final Path source, final Path target) {
    try {
      Files.createDirectories(target.getParent());
      Files.move(source, target);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void saveOrDelete(final Resource resource) {
    try {
      if (resource.getContents().isEmpty()) {
        resource.delete(Map.of());
      } else {
        resource.save(Map.of());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public URI getUri(final Path viewRelativePath) {
    Preconditions.checkArgument(viewRelativePath != null, "The viewRelativePath must not be null!");
    Preconditions.checkArgument(viewRelativePath.getNameCount() > 0, "The viewRelativePath must not be empty!");
    switch (this.uriMode) {
      case PLATFORM_URIS:
        return URI.createPlatformResourceURI(
            StreamSupport.stream(this.persistenceDirectory.getFileName().resolve(viewRelativePath).normalize().spliterator(), false)
                .map(Object::toString)
                .collect(Collectors.joining("/")),
            true);
      case FILE_URIS:
        return URIUtil.createFileURI(this.persistenceDirectory.resolve(viewRelativePath).normalize().toFile());
      default:
        throw new IllegalArgumentException("Unsupported URI mode: " + this.uriMode);
    }
  }

  private Path getAbsolutePath(final URI uri) {
    if (uri.isFile()) {
      Preconditions.checkArgument(uri.hasAbsolutePath(), "%s is a file URI but does not have an absolute path!", uri);
      return Path.of(URI.decode(uri.path()));
    } else if (uri.isPlatformResource()) {
      return this.persistenceDirectory.resolveSibling(uri.toPlatformString(true));
    } else {
      throw new IllegalArgumentException("This URI is not supported by this view: " + uri);
    }
  }

  private Resource determineResource(final Notifier notifier) {
    if (notifier instanceof Resource resource) {
      return resource;
    } else if (notifier instanceof EObject eObject) {
      return eObject.eResource();
    }
    return null;
  }

  @Override
  public void close() throws Exception {
    this.resourceSet.getResources().forEach(Resource::unload);
    this.resourceSet.getResources().clear();
  }

  @Override
  public TestUserInteraction getUserInteraction() {
    return this.userInteraction;
  }
}
