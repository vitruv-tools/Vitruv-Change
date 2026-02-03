package tools.vitruv.change.testutils.views;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil.isPathmap;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static tools.vitruv.change.testutils.TestModelRepositoryFactory.createTestChangeableModelRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory;
import tools.vitruv.change.composite.propagation.ChangeableModelRepository;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository;
import tools.vitruv.change.propagation.impl.DefaultChangeRecordingModelRepository;
import tools.vitruv.change.propagation.impl.DefaultChangeableModelRepository;
import tools.vitruv.change.testutils.TestUserInteraction;

/**
 * A test view that will record and publish the changes created in it.
 */
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
   * Creates a test view that will store its persisted resources in the provided
   * {@code persistenceDirectory}, 
   * allow to program interactions through the provided {@code userInteraction},
   * use the provided {@code uriMode}.
   *
   * @param persistenceDirectory is the directory to store files at.
   * @param userInteraction the {@link TestUserInteraction} 
   *     to use for interactions during change propagation.
   * @param uriMode is the URI mode.
   * @param changeableModelRepository is the repository responsible for propagating
   *     and storing the models.
   * @param uuidResolution is a consumer that populates the given view's {@link UuidResolver} 
   *     with the UUIDs of all elements in the given {@link Resource}.
   */
  public ChangePublishingTestView(
      Path persistenceDirectory,
      TestUserInteraction userInteraction,
      UriMode uriMode,
      ChangeableModelRepository changeableModelRepository,
      BiConsumer<Resource, UuidResolver> uuidResolution
  ) {
    this.resourceSet = withGlobalFactories(new ResourceSetImpl());
    this.uuidResolver = UuidResolver.create(resourceSet);
    this.modelRepository = changeableModelRepository;
    this.delegate = new BasicTestView(persistenceDirectory, resourceSet, userInteraction, uriMode);
    this.changeRecorder = new ChangeRecorder(resourceSet);
    this.changeResolver = VitruviusChangeResolverFactory.forUuids(uuidResolver);
    this.uuidResolution = uuidResolution;
    changeRecorder.beginRecording();
  }

  /**
   * Creates a test view that will store its persisted resources in the provided
   * {@code persistenceDirectory}, 
   * allow to program interactions through the provided {@code userInteraction},
   * use the provided {@code uriMode}.
   *
   * @param persistenceDirectory is the directory to store files at.
   * @param userInteraction the {@link TestUserInteraction} 
   *      to use for interactions during change propagation.
   * @param uriMode is the URI mode.
   * @param changeableModelRepository is the repository responsible for propagating
   *      and storing the models.
   * @param modelUuidResolver is the {@link UuidResolver} 
   *      associated with the {@code changeableModelRepository}.
   * @param modelResourceAt is a function that provides the model resource
   *     as stored in the {@code changeableModelRepository} for a given URI.
   */
  public ChangePublishingTestView(
      Path persistenceDirectory,
      TestUserInteraction userInteraction,
      UriMode uriMode,
      ChangeableModelRepository changeableModelRepository,
      UuidResolver modelUuidResolver,
      Function<URI, Resource> modelResourceAt
  ) {
    this(persistenceDirectory, userInteraction, uriMode, changeableModelRepository,
        buildUuidResolution(modelUuidResolver, modelResourceAt));
  }

  private static BiConsumer<Resource, UuidResolver> buildUuidResolution(
      UuidResolver modelUuidResolver, Function<URI, Resource> modelResourceAt) {
    return (Resource viewResource, UuidResolver viewUuidResolver) -> {
      Resource modelResource = modelResourceAt.apply(viewResource.getURI());
      if (modelResource != null) {
        modelUuidResolver.resolveResource(modelResource, viewResource, viewUuidResolver);
      }
    };
  }

  @Override
  public void close() throws Exception {
    delegate.close();
    changeRecorder.close();
  }

  @Override
  public <T extends Notifier> T record(T notifier, Consumer<T> consumer) {
    try {
      startRecordingChanges(notifier);
      return delegate.record(notifier, consumer);
    } finally {
      stopRecordingChanges(notifier);
    }
  }

  @Override
  public <T extends Notifier> List<PropagatedChange> propagate(T notifier, Consumer<T> consumer) {
    Consumer<T> delegateConsumer = (T notifier2) -> this.record(notifier2, consumer);
    var delegateChanges = delegate.propagate(notifier, delegateConsumer);
    changeRecorder.endRecording();
    var ourChanges = propagateChanges(changeRecorder.getChange());
    changeRecorder.beginRecording();
    return combine(delegateChanges, ourChanges);
  }

  @Override
  public List<PropagatedChange> propagate() {
    changeRecorder.endRecording();
    var recordedChange = changeRecorder.getChange();
    List<PropagatedChange> delegateChanges = recordedChange.getChangedURIs()
        .stream()
        .map(uri -> resourceSet.getResource(uri, false))
        .filter(Objects::nonNull)
        .map(changedResource ->
          // Propagating an empty modification for every changed resource gives the delegate a
          // chance to participate in change propagation 
          // (e.g. BasicTestView saves or cleans up resources).
          // This is not a meaningful operation at all, but rather a hack to bridge between this
          // non-transactional operation and the transactional delegate.
          delegate.propagate(changedResource, res -> {})
        )
        .reduce(new ArrayList<>(), this::combine);

    var ourChanges = propagateChanges(recordedChange);
    changeRecorder.beginRecording();
    return combine(delegateChanges, ourChanges);
  }

  private List<PropagatedChange> propagateChanges(TransactionalChange<EObject> change) {
    var unresolvedChange = changeResolver.assignIds(change);
    var propagationResult = modelRepository.propagateChange(unresolvedChange);
    if (disposeViewResourcesAfterPropagation) {
      disposeViewResources();
    }
    return propagationResult;
  }

  @Override
  public Resource resourceAt(URI modelUri) {
    var resource = delegate.resourceAt(modelUri);
    uuidResolution.accept(resource, uuidResolver);
    return resource;
  }

  @Override
  public Resource resourceAt(Path viewRelativePath) {
    return resourceAt(getUri(viewRelativePath));
  }

  @Override
  public <T extends EObject> T from(Class<T> clazz, URI modelUri) {
    var resource = resourceSet.getResource(modelUri, true);
    uuidResolution.accept(resource, uuidResolver);
    return from(clazz, resource);
  }

  @Override
  public <T extends EObject> T from(Class<T> clazz, Path viewRelativePath) {
    return from(clazz, getUri(viewRelativePath));
  }

  @Override
  public void disposeViewResources() {
    resourceSet.getResources().forEach(resource -> {
      if (resource.getURI() == null || !isPathmap(resource.getURI())) {
        resource.getAllContents().forEachRemaining(eObject -> {
          if (uuidResolver.hasUuid(eObject)) {
            uuidResolver.unregisterEObject(uuidResolver.getUuid(eObject), eObject);
          }
        });
      }
    });
    resourceSet.getResources().clear();
  }

  @Override
  public <T extends Notifier> T startRecordingChanges(T notifier) {
    checkState(changeRecorder.isRecording(), "This test view has already been closed!");
    checkArgument(notifier != null, "The object to record changes of is null!");
    changeRecorder.addToRecording(notifier);
    return notifier;
  }

  @Override
  public <T extends Notifier> T stopRecordingChanges(T notifier) {
    checkState(changeRecorder.isRecording(), "This test view has already been closed!");
    checkArgument(notifier != null, "The object to stop recording changes of is null!");
    changeRecorder.removeFromRecording(notifier);
    return notifier;
  }

  @Override
  public void setDisposeViewResourcesAfterPropagation(boolean enabled) {
    disposeViewResourcesAfterPropagation = enabled;
  }

  @Override
  public <T extends EObject> T from(Class<T> clazz, Resource resource) {
    return this.delegate.from(clazz, resource);
  }

  @Override
  public URI getUri(Path path) {
    return this.delegate.getUri(path);
  }

  @Override
  public TestUserInteraction getUserInteraction() {
    return this.delegate.getUserInteraction();
  }

  @Override
  public void moveTo(Resource resource, Path path) {
    this.delegate.moveTo(resource, path);
  }

  @Override
  public void moveTo(Resource resource, URI uri) {
    this.delegate.moveTo(resource, uri);
  }

  private <T> List<T> combine(List<T> firstList, List<T> secondList) {
    var resultList = new ArrayList<>(firstList);
    resultList.addAll(secondList);
    return resultList;
  }

  /**
   * Creates a {@link ChangePublishingTestView} with the given persistence directory
   * and for the given {@link ChangePropagationSpecification}s. It uses file URIs
   * (see {@link UriMode}) and instantiates a {@link DefaultChangeableModelRepository}
   * for a {@link TestUserInteraction}.
   */
  public static ChangePublishingTestView createDefaultChangePublishingTestView(
      Path persistenceDirectory,
      Iterable<ChangePropagationSpecification> changePropagationSpecifications
  ) {
    try {
      var userInteraction = new TestUserInteraction();
      var changePropagationSpecificationProvider = new ChangePropagationSpecificationRepository(
          changePropagationSpecifications);
      var modelRepository = new DefaultChangeRecordingModelRepository(
          null, 
          Files.createTempDirectory(null));
      var changeableModelRepository = createTestChangeableModelRepository(
          modelRepository,
          changePropagationSpecificationProvider,
          userInteraction);
      
      return new ChangePublishingTestView(
          persistenceDirectory,
          userInteraction,
          UriMode.FILE_URIS,
          changeableModelRepository,
          modelRepository.getUuidResolver(),
          modelRepository::getModelResource);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
