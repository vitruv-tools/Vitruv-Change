package tools.vitruv.change.atomic.resolve;

import org.eclipse.emf.ecore.*;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootFactory;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static tools.vitruv.change.atomic.message.Error.UNKNOWN_CHANGE_OF_TYPE;

class AtomicEChangeCopierUnitTest {

  @Test
  void constructor_isPrivate() throws Exception {
    Constructor<AtomicEChangeCopier> constructor =
        AtomicEChangeCopier.class.getDeclaredConstructor();
    assertThat(constructor.canAccess(null)).isFalse();

    constructor.setAccessible(true);
    AtomicEChangeCopier instance = constructor.newInstance();
    assertThat(instance).isNotNull();
  }

  @Test
  void testCopy_InsertRootEObject() {
    var original = RootFactory.eINSTANCE.createInsertRootEObject();
    original.setUri("test://insert");
    original.setIndex(42);

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(InsertRootEObject.class);
    var insertCopy = (InsertRootEObject<?>) copy;
    assertThat(insertCopy.getUri()).isEqualTo("test://insert");
    assertThat(insertCopy.getIndex()).isEqualTo(42);
  }

  @Test
  void testCopy_RemoveRootEObject() {
    var original = RootFactory.eINSTANCE.createRemoveRootEObject();
    original.setUri("test://remove");
    original.setIndex(24);

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(RemoveRootEObject.class);
    var removeCopy = (RemoveRootEObject<?>) copy;
    assertThat(removeCopy.getUri()).isEqualTo("test://remove");
    assertThat(removeCopy.getIndex()).isEqualTo(24);
  }

  @Test
  void testCopy_CreateEObject() {
    var original = EobjectFactory.eINSTANCE.createCreateEObject();

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(CreateEObject.class);
  }

  @Test
  void testCopy_DeleteEObject() {
    var original = EobjectFactory.eINSTANCE.createDeleteEObject();

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(DeleteEObject.class);
  }

  @Test
  void testCopy_UnsetFeature() {
    var feature = mock(EStructuralFeature.class);

    var original = FeatureFactory.eINSTANCE.createUnsetFeature();
    original.setAffectedFeature(feature);

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(UnsetFeature.class);
    var unsetCopy = (UnsetFeature<?, ?>) copy;
    assertThat(unsetCopy.getAffectedFeature()).isEqualTo(feature);
  }

  @Test
  void testCopy_InsertEAttributeValue() {
    var feature = mock(EAttribute.class);

    var original =
        TypeInferringAtomicEChangeFactory.getInstance()
            .createInsertAttributeChange(null, feature, 1, "newVal");

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(InsertEAttributeValue.class);
    var insertCopy = (InsertEAttributeValue<?, ?>) copy;
    assertThat(insertCopy.getIndex()).isEqualTo(1);
    assertThat(insertCopy.getNewValue()).isEqualTo("newVal");
  }

  @Test
  void testCopy_ReplaceSingleValuedEAttribute() {
    var feature = mock(EAttribute.class);

    var original =
        TypeInferringAtomicEChangeFactory.getInstance()
            .createReplaceSingleAttributeChange(null, feature, "oldVal", "newVal");
    original.setIsUnset(true);

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(ReplaceSingleValuedEAttribute.class);
    var replaceCopy = (ReplaceSingleValuedFeatureEChange<?, ?, ?>) copy;
    assertThat(replaceCopy.isIsUnset()).isTrue();
  }

  @Test
  void testCopy_RemoveEAttributeValue() {
    var feature = mock(EAttribute.class);

    var original =
        TypeInferringAtomicEChangeFactory.getInstance()
            .createRemoveAttributeChange(null, feature, 2, "oldVal");

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(RemoveEAttributeValue.class);
    var removeCopy = (RemoveEAttributeValue<?, ?>) copy;
    assertThat(removeCopy.getIndex()).isEqualTo(2);
    assertThat(removeCopy.getOldValue()).isEqualTo("oldVal");
  }

  @Test
  void testCopy_InsertEReference() {
    var feature = mock(EReference.class);

    var original =
        TypeInferringAtomicEChangeFactory.getInstance()
            .createInsertReferenceChange(null, feature, null, 3);

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(InsertEReference.class);
    var insertRefCopy = (InsertEReference<?>) copy;
    assertThat(insertRefCopy.getIndex()).isEqualTo(3);
  }

  @Test
  void testCopy_ReplaceSingleValuedEReference() {
    var feature = mock(EReference.class);

    var original =
        TypeInferringAtomicEChangeFactory.getInstance()
            .createReplaceSingleReferenceChange(null, feature, null, null);
    original.setIsUnset(true);

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(ReplaceSingleValuedEReference.class);
    var replaceCopy = (ReplaceSingleValuedFeatureEChange<?, ?, ?>) copy;
    assertThat(replaceCopy.isIsUnset()).isTrue();
  }

  @Test
  void testCopy_RemoveEReference() {
    var feature = mock(EReference.class);

    var original =
        TypeInferringAtomicEChangeFactory.getInstance()
            .createRemoveReferenceChange(null, feature, null, 4);

    var copy = AtomicEChangeCopier.copy(original);

    assertThat(copy).isInstanceOf(RemoveEReference.class);
    var removeRefCopy = (RemoveEReference<?>) copy;
    assertThat(removeRefCopy.getIndex()).isEqualTo(4);
  }

  @Test
  void testCopy_UnknownType_ThrowsException() {
    @SuppressWarnings("unchecked")
    EChange<Object> unknown = mock(EChange.class);

    assertThatThrownBy(() -> AtomicEChangeCopier.copy(unknown))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(String.format(UNKNOWN_CHANGE_OF_TYPE, ""));
  }
}
