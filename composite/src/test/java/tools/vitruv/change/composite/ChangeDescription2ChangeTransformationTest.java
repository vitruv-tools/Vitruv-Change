package tools.vitruv.change.composite;

import allElementTypes.Root;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestProject;
import tools.vitruv.change.testutils.TestProjectManager;
import tools.vitruv.change.testutils.matchers.ModelMatchers;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Base class for tests that check how model modifications are recorded as
 * {@link EChange}s.
 *
 * <p>Subclasses perform a modification through {@link #recordObject} and check
 * the returned changes. Every recorded change is also validated automatically:
 * it is replayed on a copy of the model taken before the modification, and the
 * result must equal the modified model.
 */
@ExtendWith({TestProjectManager.class, RegisterMetamodelsInStandalone.class})
public abstract class ChangeDescription2ChangeTransformationTest {
  private ChangeRecorder changeRecorder;

  private UuidResolver uuidResolver;

  private VitruviusChangeResolver<Uuid> changeResolver;

  private ResourceSet resourceSet;

  private Path tempFolder;

  /** Create a new model and initialize the change monitoring. */
  @BeforeEach
  public void beforeTest(@TestProject final Path tempFolder) {
    this.tempFolder = tempFolder;
    this.resourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    this.uuidResolver = UuidResolver.create(this.resourceSet);
    this.changeRecorder = new ChangeRecorder(this.resourceSet);
    this.changeResolver = VitruviusChangeResolverFactory.forUuids(this.uuidResolver);
    this.startRecording(this.resourceSet);
  }

  /**
   * Closes the change recorder.
   */
  @AfterEach
  public void afterTest() {
    this.changeRecorder.close();
  }

  /**
   * Records the changes made by the given operation and returns them as a list.
   *
   * @param <T> the type of the recorded object
   * @param objectToRecord the object, resource or resource set to record changes on
   * @param operationToRecord the operation that modifies the object
   * @return the recorded atomic changes
   * @see #recordComposite(Notifier, Consumer)
   */
  protected <T extends Notifier> List<EChange<EObject>> recordObject(
          final T objectToRecord,
          final Consumer<T> operationToRecord) {
    final TransactionalChange<EObject> recordedChange =
            this.recordComposite(objectToRecord, operationToRecord);
    return recordedChange.getEChanges();
  }

  /**
   * Records the changes made by the given operation and validates them.
   *
   * <p>Only changes on {@code objectToRecord} are recorded. The recorded change
   * is replayed on a copy of the model taken before the operation, and the
   * result must equal the modified model, otherwise the test fails.
   *
   * @param <T> the type of the recorded object
   * @param objectToRecord the object, resource or resource set to record changes on
   * @param operationToRecord the operation that modifies the object
   * @return the recorded change
   */
  protected <T extends Notifier> TransactionalChange<EObject> recordComposite(
      final T objectToRecord, final Consumer<T> operationToRecord) {
    this.stopRecording(this.resourceSet);
    this.changeResolver.assignIds(this.changeRecorder.getChange());
    final Function<Consumer<VitruviusChange<Uuid>>, TransactionalChange<EObject>> function =
        (Consumer<VitruviusChange<Uuid>> validationCallback) -> {
          TransactionalChange<EObject> xblockexpression = null;
          {
            this.startRecording(objectToRecord);
            operationToRecord.accept(objectToRecord);
            this.stopRecording(objectToRecord);
            final TransactionalChange<EObject> recordedChange = this.changeRecorder.getChange();
            final VitruviusChange<Uuid> unresolvedChange =
                this.changeResolver.assignIds(recordedChange);
            validationCallback.accept(unresolvedChange);
            xblockexpression = recordedChange;
          }
          return xblockexpression;
        };
    final TransactionalChange<EObject> recordedChange =
        this.validateChange(function);
    this.startRecording(this.resourceSet);
    return recordedChange;
  }

  /**
   * Returns the XMI resource with the given name in the temporary folder,
   * loading it if it exists and creating it otherwise.
   *
   * @param name the resource name, without the {@code .xmi} extension
   * @return the resource
   */
  protected Resource resourceAt(final String name) {
    return ResourceSetUtil.loadOrCreateResource(this.resourceSet, this.getUri(name + ".xmi"));
  }

  /**
   * Returns the file URI of the given path inside the temporary folder.
   *
   * @param relativePath the path relative to the temporary folder
   * @return the file URI
   */
  protected URI getUri(final CharSequence relativePath) {
    return URIUtil.createFileURI(this.tempFolder.resolve(relativePath.toString()).toFile());
  }

  /**
   * Returns the root object of the shared test resource, creating it on first use.
   *
   * <p>Every call within the same test returns the same object.
   *
   * @return the root object of the test resource
   */
  protected Root getUniquePersistedRoot() {
    final Resource resource = this.resourceAt("dummy");
    boolean isEmpty = resource.getContents().isEmpty();
    if (isEmpty) {
      final Root root = AllElementTypesCreators.aet.Root();
      EList<EObject> contents = resource.getContents();
      contents.add(root);
      return root;
    } else {
      EObject get = resource.getContents().getFirst();
      return ((Root) get);
    }
  }

  /**
   * Adds the given notifier to the recording and begins recording.
   *
   * @param notifier the object, resource or resource set to record
   * @return the result of {@link ChangeRecorder#beginRecording()}
   * @throws IllegalStateException if the recorder is already recording
   */
  private List<EChange<EObject>> startRecording(final Notifier notifier) {
    Preconditions.checkState(!this.changeRecorder.isRecording());
    this.changeRecorder.addToRecording(notifier);
    return this.changeRecorder.beginRecording();
  }

  private void stopRecording(final Notifier notifier) {
    Preconditions.checkState(this.changeRecorder.isRecording());
    this.changeRecorder.endRecording();
    this.changeRecorder.removeFromRecording(notifier);
  }

  /**
   * Creates a comparison resource set mirroring the resource set before the given operation is
   * executed. The provided operation must be called with the change describing the performed
   * changes in the resource set. Validates that the given change results in the new state by
   * replaying it in the comparison resource set. Returns the object returned by the given
   * operation.
   */
  private <T> T validateChange(
          final Function<Consumer<VitruviusChange<Uuid>>,
                  T> operationToValidate) {
    final ResourceSet comparisonResourceSet =
            ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    final HashMap<Resource, Resource> originalToComparisonResourceMapping =
            ChangeDescription2ChangeTransformationTest.copyTo(
            this.resourceSet, comparisonResourceSet);
    final UuidResolver comparisonUuidResolver = UuidResolver.create(comparisonResourceSet);
    final VitruviusChangeResolver<Uuid> comparisonChangeResolver =
            VitruviusChangeResolverFactory.forUuids(comparisonUuidResolver);
    this.uuidResolver.resolveResources(originalToComparisonResourceMapping, comparisonUuidResolver);
    final Consumer<VitruviusChange<Uuid>> _function = (VitruviusChange<Uuid> unresolvedChange) -> {
      comparisonChangeResolver.resolveAndApply(unresolvedChange);
      ChangeDescription2ChangeTransformationTest.assertContains(
              this.resourceSet, comparisonResourceSet);
      ChangeDescription2ChangeTransformationTest.assertContains(
              comparisonResourceSet, this.resourceSet);
    };
    return operationToValidate.apply(_function);
  }


  private static HashMap<Resource, Resource> copyTo(
      final ResourceSet original, final ResourceSet target) {
    HashMap<Resource, Resource> resourceMapping = new HashMap<Resource, Resource>();
    EList<Resource> resources = original.getResources();
    for (final Resource originalResource : resources) {
      {
        final Resource comparisonResource = target.createResource(originalResource.getURI());
        boolean isEmpty = originalResource.getContents().isEmpty();
        if (!isEmpty) {
          EList<EObject> contents = comparisonResource.getContents();
          Collection<EObject> copyAll = EcoreUtil.copyAll(originalResource.getContents());
          Iterables.addAll(contents, copyAll);
        }
        resourceMapping.put(originalResource, comparisonResource);
      }
    }
    return resourceMapping;
  }

  private static void assertContains(final ResourceSet first, final ResourceSet second) {
    EList<Resource> resources = second.getResources();
    for (final Resource originalResource : resources) {
      {
        final Resource comparisonResource = first.getResource(originalResource.getURI(), false);
        Assertions.assertNotNull(comparisonResource);
        boolean isEmpty = originalResource.getContents().isEmpty();
        if (!isEmpty) {
          MatcherAssert.assertThat(
              ResourceUtil.getFirstRootEObject(comparisonResource),
              ModelMatchers.equalsDeeply(
                  ResourceUtil.getFirstRootEObject(originalResource)));
        } else {
          Assertions.assertTrue(comparisonResource.getContents().isEmpty());
        }
      }
    }
  }

  /**
   * Asserts that the given changes contain exactly the expected number of changes.
   *
   * @param <E> the type of elements referenced by the changes
   * @param changes the changes to count
   * @param expectedCount the expected number of changes
   * @return the same changes, so the call can be chained with further assertions
   */
  public static <E> Iterable<? extends EChange<E>> assertChangeCount(
      final Iterable<? extends EChange<E>> changes, final int expectedCount) {
    int size = Iterables.size(changes);
    Assertions.assertEquals(
        expectedCount,
        size,
        "There were "
                + size
                + " changes, although "
                + expectedCount + " were expected");
    return changes;
  }
}
