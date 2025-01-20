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
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
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
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.testutils.TestProject;
import tools.vitruv.testutils.TestProjectManager;
import tools.vitruv.testutils.matchers.ModelMatchers;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@ExtendWith({ TestProjectManager.class, RegisterMetamodelsInStandalone.class })
@SuppressWarnings("all")
public abstract class ChangeDescription2ChangeTransformationTest {
  private ChangeRecorder changeRecorder;

  private UuidResolver uuidResolver;

  private VitruviusChangeResolver<Uuid> changeResolver;

  private ResourceSet resourceSet;

  private Path tempFolder;

  /**
   * Create a new model and initialize the change monitoring
   */
  @BeforeEach
  public void beforeTest(@TestProject final Path tempFolder) {
    this.tempFolder = tempFolder;
    this.resourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    this.uuidResolver = UuidResolver.create(this.resourceSet);
    ChangeRecorder _changeRecorder = new ChangeRecorder(this.resourceSet);
    this.changeRecorder = _changeRecorder;
    this.changeResolver = VitruviusChangeResolver.forUuids(this.uuidResolver);
    this.startRecording(this.resourceSet);
  }

  @AfterEach
  public void afterTest() {
    this.changeRecorder.close();
  }

  protected <T extends Notifier> List<EChange<EObject>> record(final T objectToRecord, final Consumer<T> operationToRecord) {
    final TransactionalChange<EObject> recordedChange = this.<T>recordComposite(objectToRecord, operationToRecord);
    return recordedChange.getEChanges();
  }

  protected <T extends Notifier> TransactionalChange<EObject> recordComposite(final T objectToRecord, final Consumer<T> operationToRecord) {
    this.stopRecording(this.resourceSet);
    this.changeResolver.assignIds(this.changeRecorder.getChange());
    final Function<Consumer<VitruviusChange<Uuid>>, TransactionalChange<EObject>> _function = (Consumer<VitruviusChange<Uuid>> validationCallback) -> {
      TransactionalChange<EObject> _xblockexpression = null;
      {
        this.startRecording(objectToRecord);
        operationToRecord.accept(objectToRecord);
        this.stopRecording(objectToRecord);
        final TransactionalChange<EObject> recordedChange = this.changeRecorder.getChange();
        final VitruviusChange<Uuid> unresolvedChange = this.changeResolver.assignIds(recordedChange);
        validationCallback.accept(unresolvedChange);
        _xblockexpression = recordedChange;
      }
      return _xblockexpression;
    };
    final TransactionalChange<EObject> recordedChange = this.<TransactionalChange<EObject>>validateChange(_function);
    this.startRecording(this.resourceSet);
    return recordedChange;
  }

  protected Resource resourceAt(final String name) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(name);
    _builder.append(".xmi");
    return ResourceSetUtil.loadOrCreateResource(this.resourceSet, this.getUri(_builder));
  }

  protected URI getUri(final CharSequence relativePath) {
    return URIUtil.createFileURI(this.tempFolder.resolve(relativePath.toString()).toFile());
  }

  protected Root getUniquePersistedRoot() {
    final Resource resource = this.resourceAt("dummy");
    boolean _isEmpty = resource.getContents().isEmpty();
    if (_isEmpty) {
      final Root root = AllElementTypesCreators.aet.Root();
      EList<EObject> _contents = resource.getContents();
      _contents.add(root);
      return root;
    } else {
      EObject _get = resource.getContents().get(0);
      return ((Root) _get);
    }
  }

  private List<EChange<EObject>> startRecording(final Notifier notifier) {
    List<EChange<EObject>> _xblockexpression = null;
    {
      boolean _isRecording = this.changeRecorder.isRecording();
      boolean _not = (!_isRecording);
      Preconditions.checkState(_not);
      this.changeRecorder.addToRecording(notifier);
      _xblockexpression = this.changeRecorder.beginRecording();
    }
    return _xblockexpression;
  }

  private void stopRecording(final Notifier notifier) {
    Preconditions.checkState(this.changeRecorder.isRecording());
    this.changeRecorder.endRecording();
    this.changeRecorder.removeFromRecording(notifier);
  }

  /**
   * Creates a comparison resource set mirroring the resource set before the given operation is executed.
   * The provided operation must be called with the change describing the performed changes in the resource set.
   * Validates that the given change results in the new state by replaying it in the comparison resource set.
   * Returns the object returned by the given operation.
   */
  private <T extends Object> T validateChange(final Function<Consumer<VitruviusChange<Uuid>>, T> operationToValidate) {
    T _xblockexpression = null;
    {
      final ResourceSet comparisonResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
      final HashMap<Resource, Resource> originalToComparisonResourceMapping = ChangeDescription2ChangeTransformationTest.copyTo(this.resourceSet, comparisonResourceSet);
      final UuidResolver comparisonUuidResolver = UuidResolver.create(comparisonResourceSet);
      final VitruviusChangeResolver<Uuid> comparisonChangeResolver = VitruviusChangeResolver.forUuids(comparisonUuidResolver);
      this.uuidResolver.resolveResources(originalToComparisonResourceMapping, comparisonUuidResolver);
      final Consumer<VitruviusChange<Uuid>> _function = (VitruviusChange<Uuid> unresolvedChange) -> {
        comparisonChangeResolver.resolveAndApply(unresolvedChange);
        ChangeDescription2ChangeTransformationTest.assertContains(this.resourceSet, comparisonResourceSet);
        ChangeDescription2ChangeTransformationTest.assertContains(comparisonResourceSet, this.resourceSet);
      };
      _xblockexpression = operationToValidate.apply(_function);
    }
    return _xblockexpression;
  }

  private static HashMap<Resource, Resource> copyTo(final ResourceSet original, final ResourceSet target) {
    HashMap<Resource, Resource> resourceMapping = new HashMap<Resource, Resource>();
    EList<Resource> _resources = original.getResources();
    for (final Resource originalResource : _resources) {
      {
        final Resource comparisonResource = target.createResource(originalResource.getURI());
        boolean _isEmpty = originalResource.getContents().isEmpty();
        boolean _not = (!_isEmpty);
        if (_not) {
          EList<EObject> _contents = comparisonResource.getContents();
          Collection<EObject> _copyAll = EcoreUtil.<EObject>copyAll(originalResource.getContents());
          Iterables.<EObject>addAll(_contents, _copyAll);
        }
        resourceMapping.put(originalResource, comparisonResource);
      }
    }
    return resourceMapping;
  }

  private static void assertContains(final ResourceSet first, final ResourceSet second) {
    EList<Resource> _resources = second.getResources();
    for (final Resource originalResource : _resources) {
      {
        final Resource comparisonResource = first.getResource(originalResource.getURI(), false);
        Assertions.assertNotNull(comparisonResource);
        boolean _isEmpty = originalResource.getContents().isEmpty();
        boolean _not = (!_isEmpty);
        if (_not) {
          MatcherAssert.<EObject>assertThat(ResourceUtil.getFirstRootEObject(comparisonResource), ModelMatchers.<EObject>equalsDeeply(ResourceUtil.getFirstRootEObject(originalResource)));
        } else {
          Assertions.assertTrue(comparisonResource.getContents().isEmpty());
        }
      }
    }
  }

  public static <E extends Object> Iterable<? extends EChange<E>> assertChangeCount(final Iterable<? extends EChange<E>> changes, final int expectedCount) {
    int _size = IterableExtensions.size(changes);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("There were ");
    int _size_1 = IterableExtensions.size(changes);
    _builder.append(_size_1);
    _builder.append(" changes, although ");
    _builder.append(expectedCount);
    _builder.append(" were expected");
    Assertions.assertEquals(expectedCount, _size, _builder.toString());
    return changes;
  }
}
