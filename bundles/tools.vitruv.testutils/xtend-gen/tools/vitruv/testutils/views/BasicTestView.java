package tools.vitruv.testutils.views;

import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * A minimal test view that gives access to resources, but does not record any changes.
 */
@SuppressWarnings("all")
public class BasicTestView implements TestView {
  private final Path persistenceDirectory;

  private final ResourceSet resourceSet;

  @Accessors
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
    Resource _elvis = null;
    if (toSave != null) {
      _elvis = toSave;
    } else {
      Resource _determineResource = this.determineResource(notifier);
      _elvis = _determineResource;
    }
    if (_elvis!=null) {
      this.saveOrDelete(_elvis);
    }
    return CollectionLiterals.<PropagatedChange>emptyList();
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

  private Path moveSafelyTo(final Path source, final Path target) {
    try {
      Path _xblockexpression = null;
      {
        Files.createDirectories(target.getParent());
        _xblockexpression = Files.move(source, target);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  private void saveOrDelete(final Resource resource) {
    try {
      boolean _isEmpty = resource.getContents().isEmpty();
      if (_isEmpty) {
        resource.delete(CollectionLiterals.<Object, Object>emptyMap());
      } else {
        resource.save(CollectionLiterals.<Object, Object>emptyMap());
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  @Override
  public URI getUri(final Path viewRelativePath) {
    Preconditions.checkArgument((viewRelativePath != null), "The viewRelativePath must not be null!");
    boolean _isEmpty = IterableExtensions.isEmpty(viewRelativePath);
    boolean _not = (!_isEmpty);
    Preconditions.checkArgument(_not, "The viewRelativePath must not be empty!");
    URI _switchResult = null;
    final UriMode uriMode = this.uriMode;
    if (uriMode != null) {
      switch (uriMode) {
        case PLATFORM_URIS:
          _switchResult = URI.createPlatformResourceURI(IterableExtensions.join(this.persistenceDirectory.getFileName().resolve(viewRelativePath).normalize(), "/"), true);
          break;
        case FILE_URIS:
          _switchResult = URIUtil.createFileURI(this.persistenceDirectory.resolve(viewRelativePath).normalize().toFile());
          break;
        default:
          break;
      }
    }
    return _switchResult;
  }

  private Path getAbsolutePath(final URI uri) {
    Path _xifexpression = null;
    boolean _isFile = uri.isFile();
    if (_isFile) {
      Path _xblockexpression = null;
      {
        boolean _hasAbsolutePath = uri.hasAbsolutePath();
        StringConcatenation _builder = new StringConcatenation();
        _builder.append(uri);
        _builder.append(" is a file URI but does not have an absolute path!");
        Preconditions.checkArgument(_hasAbsolutePath, _builder);
        _xblockexpression = Path.of(URI.decode(uri.path()));
      }
      _xifexpression = _xblockexpression;
    } else {
      Path _xifexpression_1 = null;
      boolean _isPlatformResource = uri.isPlatformResource();
      if (_isPlatformResource) {
        _xifexpression_1 = this.persistenceDirectory.resolveSibling(uri.toPlatformString(true));
      } else {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("This URI is not supported by this view: ");
        _builder.append(uri);
        throw new IllegalArgumentException(_builder.toString());
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  private Resource determineResource(final Notifier notifier) {
    Resource _switchResult = null;
    boolean _matched = false;
    if (notifier instanceof Resource) {
      _matched=true;
      _switchResult = ((Resource)notifier);
    }
    if (!_matched) {
      if (notifier instanceof EObject) {
        _matched=true;
        _switchResult = ((EObject)notifier).eResource();
      }
    }
    if (!_matched) {
      _switchResult = null;
    }
    return _switchResult;
  }

  @Override
  public void close() throws Exception {
    final Consumer<Resource> _function = (Resource it) -> {
      it.unload();
    };
    this.resourceSet.getResources().forEach(_function);
    this.resourceSet.getResources().clear();
  }

  @Pure
  @Override
  public TestUserInteraction getUserInteraction() {
    return this.userInteraction;
  }
}
