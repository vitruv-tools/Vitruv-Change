package tools.vitruv.testutils.views;

import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtend.lib.annotations.Delegate;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.propagation.ChangeableModelRepository;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository;
import tools.vitruv.change.propagation.impl.DefaultChangeRecordingModelRepository;
import tools.vitruv.change.propagation.impl.DefaultChangeableModelRepository;
import tools.vitruv.testutils.TestModelRepositoryFactory;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * A test view that will record and publish the changes created in it.
 */
@SuppressWarnings("all")
public class ChangePublishingTestView implements NonTransactionalTestView {
  private final ResourceSet resourceSet;

  private final UuidResolver uuidResolver;

  private final VitruviusChangeResolver<Uuid> changeResolver;

  @Delegate
  private final TestView delegate;

  private final ChangeRecorder changeRecorder;

  private final ChangeableModelRepository modelRepository;

  private final BiConsumer<Resource, UuidResolver> uuidResolution;

  private boolean disposeViewResourcesAfterPropagation = true;

  /**
   * Creates a test view that will store its persisted resources in the
   * provided {@code persistenceDirectory}, allow to program interactions through the provided {@code userInteraction},
   * use the provided {@code uriMode}.
   * 
   * @param persistenceDirectory is the directory to store files at.
   * @param userInteraction the {@link TestUserInteraction} to use for interactions during change propagation.
   * @param uriMode is the URI mode.
   * @param changeableModelRepository is the repository responsible for propagating and storing the models.
   * @param uuidResolution is a consumer that populates the given view's {@link UuidResolver} with the UUIDs of all elements in the given {@link Resource}.
   */
  public ChangePublishingTestView(final Path persistenceDirectory, final TestUserInteraction userInteraction, final UriMode uriMode, final ChangeableModelRepository changeableModelRepository, final BiConsumer<Resource, UuidResolver> uuidResolution) {
    this.resourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    this.uuidResolver = UuidResolver.create(this.resourceSet);
    this.modelRepository = changeableModelRepository;
    BasicTestView _basicTestView = new BasicTestView(persistenceDirectory, this.resourceSet, userInteraction, uriMode);
    this.delegate = _basicTestView;
    ChangeRecorder _changeRecorder = new ChangeRecorder(this.resourceSet);
    this.changeRecorder = _changeRecorder;
    this.changeResolver = VitruviusChangeResolver.forUuids(this.uuidResolver);
    this.uuidResolution = uuidResolution;
    this.changeRecorder.beginRecording();
  }

  /**
   * Creates a test view that will store its persisted resources in the
   * provided {@code persistenceDirectory}, allow to program interactions through the provided {@code userInteraction},
   * use the provided {@code uriMode}.
   * 
   * @param persistenceDirectory is the directory to store files at.
   * @param userInteraction the {@link TestUserInteraction} to use for interactions during change propagation.
   * @param uriMode is the URI mode.
   * @param changeableModelRepository is the repository responsible for propagating and storing the models.
   * @param modelUuidResolver is the {@link UuidResolver} associated with the {@code changeableModelRepository}.
   * @param modelResourceAt is a function that provides the model resource as stored in the
   * 						  {@code changeableModelRepository} for a given URI.
   */
  public ChangePublishingTestView(final Path persistenceDirectory, final TestUserInteraction userInteraction, final UriMode uriMode, final ChangeableModelRepository changeableModelRepository, final UuidResolver modelUuidResolver, final Function<URI, Resource> modelResourceAt) {
    this(persistenceDirectory, userInteraction, uriMode, changeableModelRepository, ((BiConsumer<Resource, UuidResolver>) (Resource viewResource, UuidResolver viewUuidResolver) -> {
      final Resource modelResource = modelResourceAt.apply(viewResource.getURI());
      if ((modelResource != null)) {
        modelUuidResolver.resolveResource(modelResource, viewResource, viewUuidResolver);
      }
    }));
  }

  @Override
  public void close() {
    try {
      this.delegate.close();
      this.changeRecorder.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  @Override
  public <T extends Notifier> T record(final T notifier, final Consumer<T> consumer) {
    try {
      this.<T>startRecordingChanges(notifier);
      return this.delegate.<T>record(notifier, consumer);
    } finally {
      this.<T>stopRecordingChanges(notifier);
    }
  }

  @Override
  public <T extends Notifier> List<PropagatedChange> propagate(final T notifier, final Consumer<T> consumer) {
    final Consumer<T> _function = (T it) -> {
      this.<T>record(it, consumer);
    };
    final List<PropagatedChange> delegateChanges = this.delegate.<T>propagate(notifier, _function);
    this.changeRecorder.endRecording();
    final List<PropagatedChange> ourChanges = this.propagateChanges(this.changeRecorder.getChange());
    this.changeRecorder.beginRecording();
    return ChangePublishingTestView.<PropagatedChange>operator_plus(delegateChanges, ourChanges);
  }

  @Override
  public List<PropagatedChange> propagate() {
    this.changeRecorder.endRecording();
    final TransactionalChange<EObject> recordedChange = this.changeRecorder.getChange();
    final Function1<URI, Resource> _function = (URI it) -> {
      return this.resourceSet.getResource(it, false);
    };
    final Function1<Resource, List<PropagatedChange>> _function_1 = (Resource changedResource) -> {
      final Consumer<Resource> _function_2 = (Resource it) -> {
      };
      return this.delegate.<Resource>propagate(changedResource, _function_2);
    };
    final List<PropagatedChange> delegateChanges = IterableUtil.<Resource, PropagatedChange>flatMapFixed(IterableExtensions.<Resource>filterNull(IterableExtensions.<URI, Resource>map(recordedChange.getChangedURIs(), _function)), _function_1);
    final List<PropagatedChange> ourChanges = this.propagateChanges(recordedChange);
    this.changeRecorder.beginRecording();
    return ChangePublishingTestView.<PropagatedChange>operator_plus(delegateChanges, ourChanges);
  }

  private List<PropagatedChange> propagateChanges(final TransactionalChange<EObject> change) {
    final VitruviusChange<Uuid> unresolvedChange = this.changeResolver.assignIds(change);
    final List<PropagatedChange> propagationResult = this.modelRepository.propagateChange(unresolvedChange);
    if (this.disposeViewResourcesAfterPropagation) {
      this.disposeViewResources();
    }
    return propagationResult;
  }

  @Override
  public Resource resourceAt(final URI modelUri) {
    final Resource resource = this.delegate.resourceAt(modelUri);
    this.uuidResolution.accept(resource, this.uuidResolver);
    return resource;
  }

  @Override
  public Resource resourceAt(final Path viewRelativePath) {
    return this.resourceAt(this.getUri(viewRelativePath));
  }

  @Override
  public <T extends EObject> T from(final Class<T> clazz, final URI modelUri) {
    final Resource resource = this.resourceSet.getResource(modelUri, true);
    this.uuidResolution.accept(resource, this.uuidResolver);
    return this.<T>from(clazz, resource);
  }

  @Override
  public <T extends EObject> T from(final Class<T> clazz, final Path viewRelativePath) {
    return this.<T>from(clazz, this.getUri(viewRelativePath));
  }

  @Override
  public void disposeViewResources() {
    final Consumer<Resource> _function = (Resource resource) -> {
      if (((resource.getURI() == null) || (!URIUtil.isPathmap(resource.getURI())))) {
        final Procedure1<EObject> _function_1 = (EObject it) -> {
          boolean _hasUuid = this.uuidResolver.hasUuid(it);
          if (_hasUuid) {
            this.uuidResolver.unregisterEObject(this.uuidResolver.getUuid(it), it);
          }
        };
        IteratorExtensions.<EObject>forEach(resource.getAllContents(), _function_1);
      }
    };
    this.resourceSet.getResources().forEach(_function);
    this.resourceSet.getResources().clear();
  }

  @Override
  public <T extends Notifier> T startRecordingChanges(final T notifier) {
    Preconditions.checkState(this.changeRecorder.isRecording(), "This test view has already been closed!");
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("The object to record changes of is null!");
    Preconditions.checkArgument((notifier != null), _builder);
    this.changeRecorder.addToRecording(notifier);
    return notifier;
  }

  @Override
  public <T extends Notifier> T stopRecordingChanges(final T notifier) {
    Preconditions.checkState(this.changeRecorder.isRecording(), "This test view has already been closed!");
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("The object to stop recording changes of is null!");
    Preconditions.checkArgument((notifier != null), _builder);
    this.changeRecorder.removeFromRecording(notifier);
    return notifier;
  }

  @Override
  public void setDisposeViewResourcesAfterPropagation(final boolean enabled) {
    this.disposeViewResourcesAfterPropagation = enabled;
  }

  public static <T extends Object> List<T> operator_plus(final List<T> a, final List<T> b) {
    List<T> _xifexpression = null;
    boolean _isEmpty = a.isEmpty();
    if (_isEmpty) {
      _xifexpression = b;
    } else {
      List<T> _xifexpression_1 = null;
      boolean _isEmpty_1 = b.isEmpty();
      if (_isEmpty_1) {
        _xifexpression_1 = a;
      } else {
        ArrayList<T> _xblockexpression = null;
        {
          int _size = a.size();
          int _size_1 = b.size();
          int _plus = (_size + _size_1);
          final ArrayList<T> result = new ArrayList<T>(_plus);
          result.addAll(a);
          result.addAll(b);
          _xblockexpression = result;
        }
        _xifexpression_1 = _xblockexpression;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  /**
   * Creates a {@link ChangePublishingTestView} with the given persistence directory
   * and for the given {@link ChangePropagationSpecification}s. It uses file URIs
   * (see {@link UriMode}) and instantiates a {@link DefaultChangeableModelRepository}
   * for a {@link TestUserInteraction}.
   */
  public static ChangePublishingTestView createDefaultChangePublishingTestView(final Path persistenceDirectory, final Iterable<ChangePropagationSpecification> changePropagationSpecifications) {
    try {
      final TestUserInteraction userInteraction = new TestUserInteraction();
      final ChangePropagationSpecificationRepository changePropagationSpecificationProvider = new ChangePropagationSpecificationRepository(changePropagationSpecifications);
      Path _createTempDirectory = Files.createTempDirectory(null);
      final DefaultChangeRecordingModelRepository modelRepository = new DefaultChangeRecordingModelRepository(null, _createTempDirectory);
      final ChangeableModelRepository changeableModelRepository = TestModelRepositoryFactory.createTestChangeableModelRepository(modelRepository, changePropagationSpecificationProvider, userInteraction);
      UuidResolver _uuidResolver = modelRepository.getUuidResolver();
      final Function<URI, Resource> _function = (URI it) -> {
        return modelRepository.getModelResource(it);
      };
      return new ChangePublishingTestView(persistenceDirectory, userInteraction, UriMode.FILE_URIS, changeableModelRepository, _uuidResolver, _function);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public <T extends EObject> T from(final Class<T> clazz, final Resource resource) {
    return this.delegate.from(clazz, resource);
  }

  public URI getUri(final Path viewRelativePath) {
    return this.delegate.getUri(viewRelativePath);
  }

  public TestUserInteraction getUserInteraction() {
    return this.delegate.getUserInteraction();
  }

  public void moveTo(final Resource resource, final Path newViewRelativePath) {
    this.delegate.moveTo(resource, newViewRelativePath);
  }

  public void moveTo(final Resource resource, final URI newUri) {
    this.delegate.moveTo(resource, newUri);
  }
}
