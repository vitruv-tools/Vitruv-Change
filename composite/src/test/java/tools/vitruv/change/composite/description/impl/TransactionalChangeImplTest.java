package tools.vitruv.change.composite.description.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Unit tests for {@link TransactionalChangeImpl}.
 */
class TransactionalChangeImplTest {

  private static final String URI = "http://test.com/resource";
  private static final String FEATURE_NAME = "testFeature";

  private EObject affectedElement;
  private EObject newValue;
  private EObject oldValue;

  @BeforeEach
  void setUp() {
    affectedElement = mock(EObject.class);
    newValue = mock(EObject.class);
    oldValue = mock(EObject.class);
  }

  // empty

  @Test
  void toStringEmptyChange() {
    TransactionalChangeImpl<EObject> change = new TransactionalChangeImpl<>(List.of());

    assertEquals("TransactionalChangeImpl (empty)", change.toString());
  }

  // root changes

  @Test
  void toStringInsertRootEObject() {
    InsertRootEObject<EObject> eChange = mock(InsertRootEObject.class);
    when(eChange.getNewValue()).thenReturn(newValue);
    when(eChange.getUri()).thenReturn(URI);
    when(eChange.getIndex()).thenReturn(0);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains("insert " + newValue + " at " + URI + " (index 0)"));
  }

  @Test
  void toStringRemoveRootEObject() {
    RemoveRootEObject<EObject> eChange = mock(RemoveRootEObject.class);
    when(eChange.getOldValue()).thenReturn(oldValue);
    when(eChange.getUri()).thenReturn(URI);
    when(eChange.getIndex()).thenReturn(1);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains("remove " + oldValue + " from " + URI + " (index 1)"));
  }

  // EObject existence changes

  @Test
  void toStringCreateEObject() {
    CreateEObject<EObject> eChange = mock(CreateEObject.class);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains("create " + affectedElement));
  }

  @Test
  void toStringDeleteEObject() {
    DeleteEObject<EObject> eChange = mock(DeleteEObject.class);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains("delete " + affectedElement));
  }

  // feature changes

  @Test
  void toStringUnsetFeature() {
    UnsetFeature<EObject, ?> eChange = mock(UnsetFeature.class);
    EStructuralFeature feature = mock(EStructuralFeature.class);
    when(feature.getName()).thenReturn(FEATURE_NAME);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when((EStructuralFeature) eChange.getAffectedFeature()).thenReturn(feature);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains(affectedElement + "." + FEATURE_NAME + " = ∅"));
  }

  @Test
  void toStringReplaceSingleValuedEAttribute() {
    ReplaceSingleValuedEAttribute<EObject, Object> eChange =
        mock(ReplaceSingleValuedEAttribute.class);
    EAttribute feature = mock(EAttribute.class);
    when(feature.getName()).thenReturn(FEATURE_NAME);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getAffectedFeature()).thenReturn(feature);
    when(eChange.getNewValue()).thenReturn(newValue);
    when(eChange.getOldValue()).thenReturn(oldValue);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains(
        affectedElement + "." + FEATURE_NAME + " = " + newValue + " (was " + oldValue + ")"));
  }

  @Test
  void toStringReplaceSingleValuedEReference() {
    ReplaceSingleValuedEReference<EObject> eChange = mock(ReplaceSingleValuedEReference.class);
    EReference feature = mock(EReference.class);
    when(feature.getName()).thenReturn(FEATURE_NAME);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getAffectedFeature()).thenReturn(feature);
    when(eChange.getNewValue()).thenReturn(newValue);
    when(eChange.getOldValue()).thenReturn(oldValue);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains(
        affectedElement + "." + FEATURE_NAME + " = " + newValue + " (was " + oldValue + ")"));
  }

  @Test
  void toStringInsertEAttributeValue() {
    InsertEAttributeValue<EObject, Object> eChange = mock(InsertEAttributeValue.class);
    EAttribute feature = mock(EAttribute.class);
    when(feature.getName()).thenReturn(FEATURE_NAME);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getAffectedFeature()).thenReturn(feature);
    when(eChange.getNewValue()).thenReturn(newValue);
    when(eChange.getIndex()).thenReturn(2);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains(
        affectedElement + "." + FEATURE_NAME + " += " + newValue + " (index 2)"));
  }

  @Test
  void toStringInsertEReference() {
    InsertEReference<EObject> eChange = mock(InsertEReference.class);
    EReference feature = mock(EReference.class);
    when(feature.getName()).thenReturn(FEATURE_NAME);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getAffectedFeature()).thenReturn(feature);
    when(eChange.getNewValue()).thenReturn(newValue);
    when(eChange.getIndex()).thenReturn(3);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains(
        affectedElement + "." + FEATURE_NAME + " += " + newValue + " (index 3)"));
  }

  @Test
  void toStringRemoveEAttributeValue() {
    RemoveEAttributeValue<EObject, Object> eChange = mock(RemoveEAttributeValue.class);
    EAttribute feature = mock(EAttribute.class);
    when(feature.getName()).thenReturn(FEATURE_NAME);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getAffectedFeature()).thenReturn(feature);
    when(eChange.getOldValue()).thenReturn(oldValue);
    when(eChange.getIndex()).thenReturn(4);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains(
        affectedElement + "." + FEATURE_NAME + " -= " + oldValue + " (index 4)"));
  }

  @Test
  void toStringRemoveEReference() {
    RemoveEReference<EObject> eChange = mock(RemoveEReference.class);
    EReference feature = mock(EReference.class);
    when(feature.getName()).thenReturn(FEATURE_NAME);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getAffectedFeature()).thenReturn(feature);
    when(eChange.getOldValue()).thenReturn(oldValue);
    when(eChange.getIndex()).thenReturn(5);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains(
        affectedElement + "." + FEATURE_NAME + " -= " + oldValue + " (index 5)"));
  }

  // multiple changes

  @Test
  void toStringWithMultipleChanges() {
    InsertRootEObject<EObject> insert = mock(InsertRootEObject.class);
    when(insert.getNewValue()).thenReturn(newValue);
    when(insert.getUri()).thenReturn(URI);
    when(insert.getIndex()).thenReturn(0);

    DeleteEObject<EObject> delete = mock(DeleteEObject.class);
    when(delete.getAffectedElement()).thenReturn(affectedElement);

    String result = new TransactionalChangeImpl<>(List.of(insert, delete)).toString();

    assertTrue(result.contains("insert " + newValue + " at " + URI + " (index 0)"));
    assertTrue(result.contains("delete " + affectedElement));
  }

  // unknown change type

  @Test
  void toStringUnknownChangeTypeReturnsNull() {
    EChange<EObject> eChange = mock(EChange.class);

    String result = new TransactionalChangeImpl<>(List.of(eChange)).toString();

    assertTrue(result.contains("null"));
  }

  @Test
  void getAffectedAndReferencedEObjectsForUpdateAttributeEChange() {
    UpdateAttributeEChange<EObject> eChange = mock(UpdateAttributeEChange.class);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertEquals(Set.of(affectedElement), result);
  }

  @Test
  void getAffectedAndReferencedEObjectsForReplaceSingleValuedEReference() {
    ReplaceSingleValuedEReference<EObject> eChange = mock(ReplaceSingleValuedEReference.class);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getOldValue()).thenReturn(oldValue);
    when(eChange.getNewValue()).thenReturn(newValue);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertEquals(Set.of(affectedElement, oldValue, newValue), result);
  }

  @Test
  void getAffectedAndReferencedEObjectsForInsertEReference() {
    InsertEReference<EObject> eChange = mock(InsertEReference.class);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getNewValue()).thenReturn(newValue);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertEquals(Set.of(affectedElement, newValue), result);
  }

  @Test
  void getAffectedAndReferencedEObjectsForRemoveEReference() {
    RemoveEReference<EObject> eChange = mock(RemoveEReference.class);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);
    when(eChange.getOldValue()).thenReturn(oldValue);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertEquals(Set.of(affectedElement, oldValue), result);
  }

  @Test
  void getAffectedAndReferencedEObjectsForEObjectExistenceEChange() {
    EObjectExistenceEChange<EObject> eChange = mock(EObjectExistenceEChange.class);
    when(eChange.getAffectedElement()).thenReturn(affectedElement);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertEquals(Set.of(affectedElement), result);
  }

  @Test
  void getAffectedAndReferencedEObjectsForInsertRootEObject() {
    InsertRootEObject<EObject> eChange = mock(InsertRootEObject.class);
    when(eChange.getNewValue()).thenReturn(newValue);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertEquals(Set.of(newValue), result);
  }

  @Test
  void getAffectedAndReferencedEObjectsForRemoveRootEObject() {
    RemoveRootEObject<EObject> eChange = mock(RemoveRootEObject.class);
    when(eChange.getOldValue()).thenReturn(oldValue);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertEquals(Set.of(oldValue), result);
  }

  @Test
  void getAffectedAndReferencedEObjectsForUnknownChangeTypeReturnsEmptySet() {
    EChange<EObject> eChange = mock(EChange.class);

    Set<EObject> result =
            new TransactionalChangeImpl<>(List.of(eChange)).getAffectedAndReferencedEObjects();

    assertTrue(result.isEmpty());
  }
}