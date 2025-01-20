package tools.vitruv.change.composite.recording;

import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.Root;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.testutils.TestProject;
import tools.vitruv.testutils.TestProjectManager;
import tools.vitruv.testutils.matchers.ModelMatchers;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@ExtendWith({ TestProjectManager.class, RegisterMetamodelsInStandalone.class })
@SuppressWarnings("all")
public class ChangeRecorderTest {
  @FinalFieldsConstructor
  private static class EChangeSequenceMatcher extends TypeSafeMatcher<TransactionalChange<EObject>> {
    private final List<Class<? extends EChange>> expectedTypes;

    @Override
    public void describeTo(final Description description) {
      boolean _isEmpty = this.expectedTypes.isEmpty();
      if (_isEmpty) {
        description.appendText("no changes");
      } else {
        final Function1<Class<? extends EChange>, CharSequence> _function = (Class<? extends EChange> it) -> {
          return it.getSimpleName();
        };
        description.appendText("this sequence of EChanges: ").appendText(IterableExtensions.<Class<? extends EChange>>join(this.expectedTypes, "[", ", ", "]", _function));
      }
    }

    @Override
    protected boolean matchesSafely(final TransactionalChange<EObject> item) {
      final Function1<EChange<EObject>, Class<? extends EChange>> _function = (EChange<EObject> it) -> {
        return it.getClass();
      };
      final Iterator<Class<? extends EChange>> actualTypes = ListExtensions.<EChange<EObject>, Class<? extends EChange>>map(item.getEChanges(), _function).iterator();
      for (final Iterator<Class<? extends EChange>> expectedTypesIt = this.expectedTypes.iterator(); expectedTypesIt.hasNext();) {
        if (((!actualTypes.hasNext()) || (!expectedTypesIt.next().isAssignableFrom(actualTypes.next())))) {
          return false;
        }
      }
      boolean _hasNext = actualTypes.hasNext();
      return (!_hasNext);
    }

    public EChangeSequenceMatcher(final List<Class<? extends EChange>> expectedTypes) {
      super();
      this.expectedTypes = expectedTypes;
    }
  }

  private final ResourceSet resourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());

  private final UuidResolver uuidResolver = UuidResolver.create(this.resourceSet);

  private final ChangeRecorder changeRecorder = new ChangeRecorder(this.resourceSet);

  private <T extends EObject> T wrapIntoRecordedResource(final T object) {
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    this.changeRecorder.addToRecording(resource);
    final Procedure0 _function = () -> {
      EList<EObject> _contents = resource.getContents();
      _contents.add(object);
    };
    this.record(_function);
    return object;
  }

  private TransactionalChange<EObject> record(final Procedure0 changes) {
    TransactionalChange<EObject> _xblockexpression = null;
    {
      this.changeRecorder.beginRecording();
      changes.apply();
      _xblockexpression = this.changeRecorder.endRecording();
    }
    return _xblockexpression;
  }

  private TransactionalChange<EObject> recordIf(final boolean condition, final Procedure0 changes) {
    TransactionalChange<EObject> _xblockexpression = null;
    {
      if (condition) {
        this.changeRecorder.beginRecording();
      }
      changes.apply();
      TransactionalChange<EObject> _xifexpression = null;
      if (condition) {
        _xifexpression = this.changeRecorder.endRecording();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  @Test
  @DisplayName("does not allow end recording twice")
  public void endRecordingTwice() {
    this.changeRecorder.beginRecording();
    this.changeRecorder.endRecording();
    final Executable _function = () -> {
      this.changeRecorder.endRecording();
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  @Test
  @DisplayName("records direct changes to an object")
  public void recordOnObject() {
    final Root root = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
    this.changeRecorder.addToRecording(root);
    final Procedure0 _function = () -> {
      root.setId("test");
    };
    this.record(_function);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("records direct changes to a resource")
  public void recordOnResource() {
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    this.changeRecorder.addToRecording(resource);
    final Procedure0 _function = () -> {
      EList<EObject> _contents = resource.getContents();
      Root _Root = AllElementTypesCreators.aet.Root();
      _contents.add(_Root);
    };
    this.record(_function);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(CreateEObject.class, InsertRootEObject.class, ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("stops recording changes for an object")
  public void stopRecordingOnObject() {
    final Root root = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final Procedure0 _function = () -> {
      this.changeRecorder.removeFromRecording(root);
      root.setId("test");
    };
    this.record(_function);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @Test
  @DisplayName("stops recording changes for a resource")
  public void stopRecordingOnResource() {
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    this.changeRecorder.addToRecording(resource);
    final Procedure0 _function = () -> {
      this.changeRecorder.removeFromRecording(resource);
      EList<EObject> _contents = resource.getContents();
      Root _Root = AllElementTypesCreators.aet.Root();
      _contents.add(_Root);
    };
    this.record(_function);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @Test
  @DisplayName("records changes to all children of an object")
  public void recordsOnObjectChildren() {
    final NonRoot inner = AllElementTypesCreators.aet.NonRoot();
    Root _wrapIntoRecordedResource = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final Procedure1<Root> _function = (Root it) -> {
      final Procedure0 _function_1 = () -> {
        NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
        final Procedure1<NonRootObjectContainerHelper> _function_2 = (NonRootObjectContainerHelper it_1) -> {
          EList<NonRoot> _nonRootObjectsContainment = it_1.getNonRootObjectsContainment();
          NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
          _nonRootObjectsContainment.add(_NonRoot);
          EList<NonRoot> _nonRootObjectsContainment_1 = it_1.getNonRootObjectsContainment();
          _nonRootObjectsContainment_1.add(inner);
        };
        NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_2);
        it.setNonRootObjectContainerHelper(_doubleArrow);
      };
      this.record(_function_1);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_wrapIntoRecordedResource, _function);
    final Procedure0 _function_1 = () -> {
      inner.setId("test");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("records changes to all children of a resource")
  public void recordsOnResourceChildren() {
    final NonRoot inner = AllElementTypesCreators.aet.NonRoot();
    Resource _createResource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Procedure1<Resource> _function = (Resource it) -> {
      this.changeRecorder.addToRecording(it);
      final Procedure0 _function_1 = () -> {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_2 = (Root it_1) -> {
          NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
          final Procedure1<NonRootObjectContainerHelper> _function_3 = (NonRootObjectContainerHelper it_2) -> {
            EList<NonRoot> _nonRootObjectsContainment = it_2.getNonRootObjectsContainment();
            NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
            _nonRootObjectsContainment.add(_NonRoot);
            EList<NonRoot> _nonRootObjectsContainment_1 = it_2.getNonRootObjectsContainment();
            _nonRootObjectsContainment_1.add(inner);
          };
          NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_3);
          it_1.setNonRootObjectContainerHelper(_doubleArrow);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_2);
        _contents.add(_doubleArrow);
      };
      this.record(_function_1);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final Procedure0 _function_1 = () -> {
      inner.setId("test");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("records changes to all children of a resource set")
  public void recordsOnResourceSetChildren() {
    final NonRoot inner = AllElementTypesCreators.aet.NonRoot();
    this.changeRecorder.addToRecording(this.resourceSet);
    Resource _createResource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Procedure1<Resource> _function = (Resource it) -> {
      final Procedure0 _function_1 = () -> {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_2 = (Root it_1) -> {
          NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
          final Procedure1<NonRootObjectContainerHelper> _function_3 = (NonRootObjectContainerHelper it_2) -> {
            EList<NonRoot> _nonRootObjectsContainment = it_2.getNonRootObjectsContainment();
            NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
            _nonRootObjectsContainment.add(_NonRoot);
            EList<NonRoot> _nonRootObjectsContainment_1 = it_2.getNonRootObjectsContainment();
            _nonRootObjectsContainment_1.add(inner);
          };
          NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_3);
          it_1.setNonRootObjectContainerHelper(_doubleArrow);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_2);
        _contents.add(_doubleArrow);
      };
      this.record(_function_1);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final Procedure0 _function_1 = () -> {
      inner.setId("test");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
    final Procedure0 _function_2 = () -> {
      resource.getContents().clear();
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(RemoveRootEObject.class, DeleteEObject.class, DeleteEObject.class, DeleteEObject.class, DeleteEObject.class));
  }

  @DisplayName("adds an object set as containment to the recording")
  public void recordsOnSetContainment() {
    final Root root = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Procedure0 _function = () -> {
      root.setSingleValuedContainmentEReference(nonRoot);
    };
    this.record(_function);
    final Procedure0 _function_1 = () -> {
      nonRoot.setId("foobar");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @DisplayName("adds an object added as containment to the recording")
  public void recordsOnAddedContainment() {
    final Root root = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Procedure0 _function = () -> {
      EList<NonRoot> _multiValuedContainmentEReference = root.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRoot);
    };
    this.record(_function);
    final Procedure0 _function_1 = () -> {
      nonRoot.setId("foobar");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @DisplayName("adds an object that was resolved from its proxy to the recording")
  public void recordsOnResolvedProxyInContainment(@TestProject final Path testDir) {
    final NonRoot savedNonRoot = AllElementTypesCreators.aet.NonRoot();
    Resource _createResource = this.resourceSet.createResource(URI.createFileURI(testDir.resolve("test.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      try {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_1 = (Root it_1) -> {
          it_1.setSingleValuedContainmentEReference(savedNonRoot);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
        _contents.add(_doubleArrow);
        it.save(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    Root _Root = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_1 = (Root it) -> {
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
        ((InternalEObject) it_1).eSetProxyURI(EcoreUtil.getURI(savedNonRoot));
      };
      NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
      it.setSingleValuedContainmentEReference(_doubleArrow);
    };
    final Root root = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
    Resource _createResource_1 = this.resourceSet.createResource(URI.createURI("test://test2.aet"));
    final Procedure1<Resource> _function_2 = (Resource it) -> {
      this.changeRecorder.addToRecording(it);
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource_1, _function_2);
    final NonRoot nonRoot = root.getSingleValuedContainmentEReference();
    final Procedure0 _function_3 = () -> {
      nonRoot.setId("foobar");
    };
    this.record(_function_3);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @DisplayName("adds multiple objects added as containments to the recording")
  public void recordsOnMultipleAddedContainment() {
    final Root root = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final Procedure0 _function = () -> {
      EList<NonRoot> _multiValuedContainmentEReference = root.getMultiValuedContainmentEReference();
      List<NonRoot> _of = List.<NonRoot>of(nonRoot1, nonRoot2);
      Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, _of);
    };
    this.record(_function);
    final Procedure0 _function_1 = () -> {
      nonRoot1.setId("foobar1");
      nonRoot2.setId("foobar2");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class, ReplaceSingleValuedEAttribute.class));
  }

  @DisplayName("adds an object added as root to the recording")
  public void recordsOnAddedRoot() {
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Root root = AllElementTypesCreators.aet.Root();
    this.changeRecorder.addToRecording(resource);
    final Procedure0 _function = () -> {
      EList<EObject> _contents = resource.getContents();
      _contents.add(root);
    };
    this.record(_function);
    final Procedure0 _function_1 = () -> {
      root.setId("foobar");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @DisplayName("removes a root object while recording")
  public void recordsOnRemovedRoot() {
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Root root = AllElementTypesCreators.aet.Root();
    this.changeRecorder.addToRecording(resource);
    EList<EObject> _contents = resource.getContents();
    _contents.add(root);
    final Procedure0 _function = () -> {
      EcoreUtil.delete(root);
    };
    this.record(_function);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(RemoveRootEObject.class, DeleteEObject.class));
  }

  @DisplayName("adds multiple objects added as roots to the recording")
  public void recordsOnMultipleAddedRoot() {
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Root root1 = AllElementTypesCreators.aet.Root();
    final Root root2 = AllElementTypesCreators.aet.Root();
    this.changeRecorder.addToRecording(resource);
    final Procedure0 _function = () -> {
      EList<EObject> _contents = resource.getContents();
      List<EObject> _of = List.<EObject>of(root1, root2);
      Iterables.<EObject>addAll(_contents, _of);
    };
    this.record(_function);
    final Procedure0 _function_1 = () -> {
      root1.setId("foobar1");
      root2.setId("foobar2");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class, ReplaceSingleValuedEAttribute.class));
  }

  @DisplayName("adds loaded objects to the recording")
  public void recordsOnLoadedObject(@TestProject final Path testProject) {
    final URI resourceUri = URI.createFileURI(testProject.resolve("test.aet").toString());
    Resource _createResource = this.resourceSet.createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      try {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_1 = (Root it_1) -> {
          it_1.setSingleValuedContainmentEReference(AllElementTypesCreators.aet.NonRoot());
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
        _contents.add(_doubleArrow);
        it.save(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    this.resourceSet.getResources().clear();
    final Resource resource = this.resourceSet.createResource(resourceUri);
    this.changeRecorder.addToRecording(resource);
    final Procedure0 _function_1 = () -> {
      try {
        resource.load(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    this.record(_function_1);
    final Procedure0 _function_2 = () -> {
      EObject _get = resource.getContents().get(0);
      NonRoot _singleValuedContainmentEReference = ((Root) _get).getSingleValuedContainmentEReference();
      _singleValuedContainmentEReference.setId("test");
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("adds a resource to the recording")
  @ValueSource(booleans = { false, true })
  public void recordsOnAddedResource(final boolean isRecordingWhileAddingObject) {
    this.changeRecorder.addToRecording(this.resourceSet);
    if (isRecordingWhileAddingObject) {
      this.changeRecorder.beginRecording();
    }
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    if (isRecordingWhileAddingObject) {
      this.changeRecorder.endRecording();
    }
    final Procedure0 _function = () -> {
      EList<EObject> _contents = resource.getContents();
      Root _Root = AllElementTypesCreators.aet.Root();
      _contents.add(_Root);
    };
    this.record(_function);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(CreateEObject.class, InsertRootEObject.class, ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("deletes a resource and records model deletion")
  public void recordsOnDeletedResource(@TestProject final Path testDir) {
    this.changeRecorder.addToRecording(this.resourceSet);
    Resource _createResource = this.resourceSet.createResource(URI.createFileURI(testDir.resolve("test.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      Root _withUuid = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setSingleValuedContainmentEReference(this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot()));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_withUuid, _function_1);
      _contents.add(_doubleArrow);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final Procedure0 _function_1 = () -> {
      try {
        resource.delete(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(RemoveRootEObject.class, DeleteEObject.class, DeleteEObject.class));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("adds multiple added resources to the recording")
  @ValueSource(booleans = { false, true })
  public void recordsOnMultipleAddedResource(final boolean isRecordingWhileAddingObject) {
    final ResourceSet foreignResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    final Resource resource1 = foreignResourceSet.createResource(URI.createURI("test://test1.aet"));
    final Resource resource2 = foreignResourceSet.createResource(URI.createURI("test://test2.aet"));
    this.changeRecorder.addToRecording(this.resourceSet);
    final Procedure0 _function = () -> {
      EList<Resource> _resources = this.resourceSet.getResources();
      List<Resource> _of = List.<Resource>of(resource1, resource2);
      Iterables.<Resource>addAll(_resources, _of);
    };
    this.recordIf(isRecordingWhileAddingObject, _function);
    final Procedure0 _function_1 = () -> {
      EList<EObject> _contents = resource1.getContents();
      Root _Root = AllElementTypesCreators.aet.Root();
      _contents.add(_Root);
      EList<EObject> _contents_1 = resource2.getContents();
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      _contents_1.add(_Root_1);
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(
      CreateEObject.class, 
      InsertRootEObject.class, 
      ReplaceSingleValuedEAttribute.class, 
      CreateEObject.class, 
      InsertRootEObject.class, 
      ReplaceSingleValuedEAttribute.class));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("does not record loading an existing resource in a resource set with demand load")
  @ValueSource(booleans = { false, true })
  public void doesntRecordLoadingExistingResourceOnResourceSetWithDemandLoad(final boolean isRecordingLoadingResource, @TestProject final Path testDir) {
    final URI resourceUri = URI.createFileURI(testDir.resolve("test.aet").toString());
    Resource _createResource = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl()).createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      try {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_1 = (Root it_1) -> {
          NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
          final Procedure1<NonRootObjectContainerHelper> _function_2 = (NonRootObjectContainerHelper it_2) -> {
            EList<NonRoot> _nonRootObjectsContainment = it_2.getNonRootObjectsContainment();
            NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
            _nonRootObjectsContainment.add(_NonRoot);
          };
          NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_2);
          it_1.setNonRootObjectContainerHelper(_doubleArrow);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
        _contents.add(_doubleArrow);
        it.save(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    final Resource originalResource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    this.changeRecorder.addToRecording(this.resourceSet);
    final Procedure0 _function_1 = () -> {
      this.resourceSet.getResource(resourceUri, true);
    };
    this.recordIf(isRecordingLoadingResource, _function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
    final Resource loadedResource = this.resourceSet.getResource(resourceUri, false);
    MatcherAssert.<Resource>assertThat(loadedResource, ModelMatchers.containsModelOf(originalResource));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("does not record loading an existing resource in a resource set with explicit loading")
  @ValueSource(booleans = { false, true })
  public void doesntRecordLoadingExistingResourceOnResourceSetWithExplicitLoading(final boolean isRecordingLoadingResource, @TestProject final Path testDir) {
    final URI resourceUri = URI.createFileURI(testDir.resolve("test.aet").toString());
    Resource _createResource = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl()).createResource(resourceUri);
    final Procedure1<Resource> _function = (Resource it) -> {
      try {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_1 = (Root it_1) -> {
          NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
          final Procedure1<NonRootObjectContainerHelper> _function_2 = (NonRootObjectContainerHelper it_2) -> {
            EList<NonRoot> _nonRootObjectsContainment = it_2.getNonRootObjectsContainment();
            NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
            _nonRootObjectsContainment.add(_NonRoot);
          };
          NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_2);
          it_1.setNonRootObjectContainerHelper(_doubleArrow);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
        _contents.add(_doubleArrow);
        it.save(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    final Resource originalResource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    this.changeRecorder.addToRecording(this.resourceSet);
    final Resource loadedResource = this.resourceSet.createResource(resourceUri);
    final Procedure0 _function_1 = () -> {
      try {
        loadedResource.load(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    this.recordIf(isRecordingLoadingResource, _function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
    MatcherAssert.<Resource>assertThat(loadedResource, ModelMatchers.containsModelOf(originalResource));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("does not record loading a pathmap resource in a resource set")
  @ValueSource(booleans = { false, true })
  public void doesntRecordPathmapResourceOnResourceSet(final boolean isRecordingLoadingResource, @TestProject final Path testDir) {
    Resource _createResource = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl()).createResource(
      URI.createFileURI(testDir.resolve("test.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      try {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_1 = (Root it_1) -> {
          NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
          final Procedure1<NonRootObjectContainerHelper> _function_2 = (NonRootObjectContainerHelper it_2) -> {
            EList<NonRoot> _nonRootObjectsContainment = it_2.getNonRootObjectsContainment();
            NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
            _nonRootObjectsContainment.add(_NonRoot);
          };
          NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_2);
          it_1.setNonRootObjectContainerHelper(_doubleArrow);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
        _contents.add(_doubleArrow);
        it.save(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    final Resource originalResource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final URI pathmapResourcesUri = URI.createURI("pathmap://CHANGE_RECORDER_TEST_MODELS/");
    String _string = testDir.toString();
    String _plus = (_string + "/");
    this.resourceSet.getURIConverter().getURIMap().put(pathmapResourcesUri, URI.createFileURI(_plus));
    final URI pathmapResourceURI = pathmapResourcesUri.appendSegment("test.aet");
    this.changeRecorder.addToRecording(this.resourceSet);
    final Procedure0 _function_1 = () -> {
      this.resourceSet.getResource(pathmapResourceURI, true);
    };
    this.recordIf(isRecordingLoadingResource, _function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
    final Resource pathmapResource = this.resourceSet.getResource(pathmapResourceURI, false);
    MatcherAssert.<URI>assertThat("resource should be loaded via pathmap instead of resolving it", pathmapResource.getURI(), 
      CoreMatchers.<URI>is(pathmapResourceURI));
    MatcherAssert.<Resource>assertThat(pathmapResource, ModelMatchers.containsModelOf(originalResource));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("does not record unloading a resource")
  @ValueSource(booleans = { false, true })
  public void doesntRecordUnloading(final boolean isRecordingUnloadingResource, @TestProject final Path testDir) {
    this.changeRecorder.addToRecording(this.resourceSet);
    Resource _createResource = this.resourceSet.createResource(URI.createFileURI(testDir.resolve("test.aet").toString()));
    final Procedure1<Resource> _function = (Resource it) -> {
      try {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_1 = (Root it_1) -> {
          NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
          final Procedure1<NonRootObjectContainerHelper> _function_2 = (NonRootObjectContainerHelper it_2) -> {
            EList<NonRoot> _nonRootObjectsContainment = it_2.getNonRootObjectsContainment();
            NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
            _nonRootObjectsContainment.add(_NonRoot);
          };
          NonRootObjectContainerHelper _doubleArrow = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_2);
          it_1.setNonRootObjectContainerHelper(_doubleArrow);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
        _contents.add(_doubleArrow);
        it.save(CollectionLiterals.<Object, Object>emptyMap());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final Procedure0 _function_1 = () -> {
      resource.unload();
    };
    this.recordIf(isRecordingUnloadingResource, _function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes an object unset from a containment reference from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterContainmentUnset(final boolean isRecordingWhileRemovingObject) {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _wrapIntoRecordedResource = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final Procedure1<Root> _function = (Root it) -> {
      final Procedure0 _function_1 = () -> {
        it.setSingleValuedContainmentEReference(nonRoot);
      };
      this.record(_function_1);
    };
    final Root root = ObjectExtensions.<Root>operator_doubleArrow(_wrapIntoRecordedResource, _function);
    final Procedure0 _function_1 = () -> {
      root.setSingleValuedContainmentEReference(null);
    };
    this.recordIf(isRecordingWhileRemovingObject, _function_1);
    final Procedure0 _function_2 = () -> {
      nonRoot.setId("foobar");
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes an object removed from a containment reference from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterContainmentRemove(final boolean isRecordingWhileRemovingObject) {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _wrapIntoRecordedResource = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final Procedure1<Root> _function = (Root it) -> {
      final Procedure0 _function_1 = () -> {
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        _multiValuedContainmentEReference.add(nonRoot);
      };
      this.record(_function_1);
    };
    final Root root = ObjectExtensions.<Root>operator_doubleArrow(_wrapIntoRecordedResource, _function);
    final Procedure0 _function_1 = () -> {
      root.getMultiValuedContainmentEReference().clear();
    };
    this.recordIf(isRecordingWhileRemovingObject, _function_1);
    final Procedure0 _function_2 = () -> {
      nonRoot.setId("foobar");
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes multiple objects removed from a containment reference from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterContainmentMultipleRemove(final boolean isRecordingWhileRemovingObject) {
    final NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRoot3 = AllElementTypesCreators.aet.NonRoot();
    Root _wrapIntoRecordedResource = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final Procedure1<Root> _function = (Root it) -> {
      final Procedure0 _function_1 = () -> {
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        List<NonRoot> _of = List.<NonRoot>of(nonRoot1, nonRoot2, nonRoot3);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, _of);
      };
      this.record(_function_1);
    };
    final Root root = ObjectExtensions.<Root>operator_doubleArrow(_wrapIntoRecordedResource, _function);
    final Procedure0 _function_1 = () -> {
      EList<NonRoot> _multiValuedContainmentEReference = root.getMultiValuedContainmentEReference();
      List<NonRoot> _of = List.<NonRoot>of(nonRoot1, nonRoot3);
      Iterables.removeAll(_multiValuedContainmentEReference, _of);
    };
    this.recordIf(isRecordingWhileRemovingObject, _function_1);
    final Procedure0 _function_2 = () -> {
      nonRoot1.setId("foobar1");
      nonRoot2.setId("foobar2");
      nonRoot3.setId("foobar3");
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes a removed resource from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterRemovedResource(final boolean isRecordingWhileRemovingObject) {
    this.changeRecorder.addToRecording(this.resourceSet);
    final Resource resource = this.resourceSet.createResource(URI.createURI("test://test1.aet"));
    final Procedure0 _function = () -> {
      this.resourceSet.getResources().clear();
    };
    this.recordIf(isRecordingWhileRemovingObject, _function);
    final Procedure0 _function_1 = () -> {
      EList<EObject> _contents = resource.getContents();
      Root _Root = AllElementTypesCreators.aet.Root();
      _contents.add(_Root);
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes multiple removed resources from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterMultipleRemovedResource(final boolean isRecordingWhileRemovingObject) {
    final Resource resource1 = this.resourceSet.createResource(URI.createURI("test://test1.aet"));
    final Resource resource2 = this.resourceSet.createResource(URI.createURI("test://test2.aet"));
    final Resource resource3 = this.resourceSet.createResource(URI.createURI("test://test3.aet"));
    this.changeRecorder.addToRecording(this.resourceSet);
    final Procedure0 _function = () -> {
      EList<Resource> _resources = this.resourceSet.getResources();
      List<Resource> _of = List.<Resource>of(resource1, resource3);
      Iterables.removeAll(_resources, _of);
    };
    this.recordIf(isRecordingWhileRemovingObject, _function);
    final Procedure0 _function_1 = () -> {
      EList<EObject> _contents = resource1.getContents();
      Root _Root = AllElementTypesCreators.aet.Root();
      _contents.add(_Root);
      EList<EObject> _contents_1 = resource2.getContents();
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      _contents_1.add(_Root_1);
      EList<EObject> _contents_2 = resource3.getContents();
      Root _Root_2 = AllElementTypesCreators.aet.Root();
      _contents_2.add(_Root_2);
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(CreateEObject.class, InsertRootEObject.class, ReplaceSingleValuedEAttribute.class));
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes unloaded objects from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterUnload(final boolean isRecordingWhileRemovingObject) {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Resource _createResource = this.resourceSet.createResource(URI.createURI("test://test1.aet"));
    final Procedure1<Resource> _function = (Resource it) -> {
      this.changeRecorder.addToRecording(it);
      final Procedure0 _function_1 = () -> {
        EList<EObject> _contents = it.getContents();
        Root _Root = AllElementTypesCreators.aet.Root();
        final Procedure1<Root> _function_2 = (Root it_1) -> {
          it_1.setSingleValuedContainmentEReference(nonRoot);
        };
        Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_2);
        _contents.add(_doubleArrow);
      };
      this.record(_function_1);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final Procedure0 _function_1 = () -> {
      resource.unload();
    };
    this.recordIf(isRecordingWhileRemovingObject, _function_1);
    final Procedure0 _function_2 = () -> {
      nonRoot.setId("test");
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes an object removed as root from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterRemovedRoot(final boolean isRecordingWhileRemovingObject) {
    final Root root = AllElementTypesCreators.aet.Root();
    Resource _createResource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Procedure1<Resource> _function = (Resource it) -> {
      this.changeRecorder.addToRecording(it);
      final Procedure0 _function_1 = () -> {
        EList<EObject> _contents = it.getContents();
        _contents.add(root);
      };
      this.record(_function_1);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final Procedure0 _function_1 = () -> {
      resource.getContents().clear();
    };
    this.recordIf(isRecordingWhileRemovingObject, _function_1);
    final Procedure0 _function_2 = () -> {
      root.setId("foobar");
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
  }

  @ParameterizedTest(name = "while isRecording={0}")
  @DisplayName("removes multiple objects removed as roots from the recording")
  @ValueSource(booleans = { false, true })
  public void removeAfterMultipleRemovedRoot(final boolean isRecordingWhileAddingObject) {
    final Root root1 = AllElementTypesCreators.aet.Root();
    final Root root2 = AllElementTypesCreators.aet.Root();
    final Root root3 = AllElementTypesCreators.aet.Root();
    Resource _createResource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Procedure1<Resource> _function = (Resource it) -> {
      this.changeRecorder.addToRecording(it);
      final Procedure0 _function_1 = () -> {
        EList<EObject> _contents = it.getContents();
        List<EObject> _of = List.<EObject>of(root1, root2, root3);
        Iterables.<EObject>addAll(_contents, _of);
      };
      this.record(_function_1);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    if (isRecordingWhileAddingObject) {
      this.changeRecorder.beginRecording();
    }
    EList<EObject> _contents = resource.getContents();
    List<EObject> _of = List.<EObject>of(root1, root3);
    Iterables.removeAll(_contents, _of);
    if (isRecordingWhileAddingObject) {
      this.changeRecorder.endRecording();
    }
    final Procedure0 _function_1 = () -> {
      root1.setId("foobar1");
      root2.setId("foobar2");
      root3.setId("foobar3");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("does not remove explicitly added child objects")
  public void dontRemoveExplicitlyAddedChild() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _wrapIntoRecordedResource = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final Procedure1<Root> _function = (Root it) -> {
      final Procedure0 _function_1 = () -> {
        it.setSingleValuedContainmentEReference(nonRoot);
      };
      this.record(_function_1);
    };
    final Root root = ObjectExtensions.<Root>operator_doubleArrow(_wrapIntoRecordedResource, _function);
    this.changeRecorder.addToRecording(nonRoot);
    root.setSingleValuedContainmentEReference(null);
    final Procedure0 _function_1 = () -> {
      nonRoot.setId("testid");
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("does not remove explicitly added root objects")
  public void dontRemoveExplicitlyAddedRoot() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _Root = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRoot);
    };
    final Root root = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function);
    Resource _createResource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Procedure1<Resource> _function_1 = (Resource it) -> {
      this.changeRecorder.addToRecording(it);
      final Procedure0 _function_2 = () -> {
        EList<EObject> _contents = it.getContents();
        _contents.add(root);
      };
      this.record(_function_2);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function_1);
    this.changeRecorder.addToRecording(root);
    resource.getContents().clear();
    final Procedure0 _function_2 = () -> {
      root.setId("rootid");
      nonRoot.setId("testid");
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class, ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("does not create deletion for element from containment replacing other element")
  public void doesNotCreateDeletionWhenReplacingExistingElement() {
    final NonRoot nonRootToReplace = AllElementTypesCreators.aet.NonRoot();
    final NonRoot nonRootToMove = AllElementTypesCreators.aet.NonRoot();
    Root _Root = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function = (Root it) -> {
      it.setSingleValuedContainmentEReference(nonRootToReplace);
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      _multiValuedContainmentEReference.add(nonRootToMove);
    };
    final Root root = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function);
    Resource _createResource = this.resourceSet.createResource(URI.createURI("test://test.aet"));
    final Procedure1<Resource> _function_1 = (Resource it) -> {
      this.changeRecorder.addToRecording(it);
      final Procedure0 _function_2 = () -> {
        EList<EObject> _contents = it.getContents();
        _contents.add(root);
      };
      this.record(_function_2);
    };
    final Resource resource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function_1);
    this.changeRecorder.addToRecording(root);
    resource.getContents().clear();
    final Procedure0 _function_2 = () -> {
      root.setSingleValuedContainmentEReference(nonRootToMove);
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(
      RemoveEReference.class, 
      ReplaceSingleValuedEReference.class, 
      DeleteEObject.class));
  }

  @Test
  @DisplayName("resets the recorded changes after ending the recording")
  public void resetsChangesAfterEndRecording() {
    final Root root = this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    final Procedure0 _function = () -> {
      root.setId("test");
    };
    this.record(_function);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(ReplaceSingleValuedEAttribute.class));
    final Procedure0 _function_1 = () -> {
    };
    this.record(_function_1);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasNoChanges());
    final Procedure0 _function_2 = () -> {
      EList<NonRoot> _multiValuedNonContainmentEReference = root.getMultiValuedNonContainmentEReference();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      _multiValuedNonContainmentEReference.add(_NonRoot);
    };
    this.record(_function_2);
    MatcherAssert.<TransactionalChange<EObject>>assertThat(this.changeRecorder.getChange(), ChangeRecorderTest.hasEChanges(CreateEObject.class, InsertEReference.class, ReplaceSingleValuedEAttribute.class));
  }

  @Test
  @DisplayName("refuses to record changes on a different resource set than the one of the ID resolver")
  public void differentResourceSet() {
    final Executable _function = () -> {
      ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
      this.changeRecorder.addToRecording(_resourceSetImpl);
    };
    Assertions.<IllegalArgumentException>assertThrows(IllegalArgumentException.class, _function);
  }

  @Test
  @DisplayName("refuses to record changes on a resource from a different resource set than the one of the ID resolver")
  public void resourceFromDifferentResourceSet() {
    final ResourceSet foreignResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    final Executable _function = () -> {
      this.changeRecorder.addToRecording(foreignResourceSet.createResource(URI.createURI("test://test.aet")));
    };
    Assertions.<IllegalArgumentException>assertThrows(IllegalArgumentException.class, _function);
  }

  @Test
  @DisplayName("refuses to record changes on an object from a different resource set than the one of the ID resolver")
  public void objectFromDifferentResourceSet() {
    final ResourceSet foreignResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    final Root root = AllElementTypesCreators.aet.Root();
    Resource _createResource = foreignResourceSet.createResource(URI.createURI("test://test.aet"));
    final Procedure1<Resource> _function = (Resource it) -> {
      EList<EObject> _contents = it.getContents();
      _contents.add(root);
    };
    final Resource foreignResource = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function);
    final Executable _function_1 = () -> {
      this.changeRecorder.addToRecording(foreignResource);
    };
    Assertions.<IllegalArgumentException>assertThrows(IllegalArgumentException.class, _function_1);
  }

  @Test
  @DisplayName("tolerates a resource without a resource set")
  public void toleratesResourceWithoutResourceSet() {
    final URI uri = URI.createURI("test://test.aet");
    final Resource resource = this.resourceSet.getResourceFactoryRegistry().getFactory(uri).createResource(uri);
    final Executable _function = () -> {
      this.changeRecorder.addToRecording(resource);
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("allows no interactions after being closed")
  public void noInteractionsAfterClose() {
    this.<Root>wrapIntoRecordedResource(AllElementTypesCreators.aet.Root());
    this.changeRecorder.beginRecording();
    this.changeRecorder.close();
    MatcherAssert.<Boolean>assertThat(Boolean.valueOf(this.changeRecorder.isRecording()), CoreMatchers.<Boolean>is(Boolean.valueOf(false)));
    final Executable _function = () -> {
      this.changeRecorder.beginRecording();
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
    final Executable _function_1 = () -> {
      this.changeRecorder.endRecording();
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_1);
    final Executable _function_2 = () -> {
      this.changeRecorder.getChange();
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_2);
    final Executable _function_3 = () -> {
      this.changeRecorder.addToRecording(AllElementTypesCreators.aet.Root());
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_3);
    final Executable _function_4 = () -> {
      this.changeRecorder.removeFromRecording(AllElementTypesCreators.aet.Root());
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_4);
  }

  @Test
  @DisplayName("can be closed twice")
  public void closeTwice() {
    this.changeRecorder.close();
    final Executable _function = () -> {
      this.changeRecorder.close();
    };
    Assertions.assertDoesNotThrow(_function);
  }

  private static ChangeRecorderTest.EChangeSequenceMatcher hasEChanges(final Class<? extends EChange>... expectedTypes) {
    return new ChangeRecorderTest.EChangeSequenceMatcher((List<Class<? extends EChange>>)Conversions.doWrapArray(expectedTypes));
  }

  private static ChangeRecorderTest.EChangeSequenceMatcher hasNoChanges() {
    List<Class<? extends EChange>> _emptyList = CollectionLiterals.<Class<? extends EChange>>emptyList();
    return new ChangeRecorderTest.EChangeSequenceMatcher(_emptyList);
  }

  private <O extends EObject> O withUuid(final O eObject) {
    O _xblockexpression = null;
    {
      this.uuidResolver.registerEObject(eObject);
      _xblockexpression = eObject;
    }
    return _xblockexpression;
  }
}
