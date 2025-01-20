package tools.vitruv.change.atomic.feature.attribute;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link InsertEAttributeValue} EChange,
 * which inserts a value in a multi valued attribute.
 */
@SuppressWarnings("all")
public class InsertEAttributeValueTest extends InsertRemoveEAttributeTest {
  @BeforeEach
  public void assertStateBefore() {
    this.assertIsStateBefore();
  }

  /**
   * Tests whether resolving the {@link InsertEAttributeValue} EChange returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange((InsertRemoveEAttributeTest.NEW_VALUE).intValue(), 0);
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests applying two {@link InsertEAttributeValue} EChanges forward by
   * inserting new values in a multivalued attribute.
   */
  @Test
  public void applyForwardTest() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange((InsertRemoveEAttributeTest.NEW_VALUE).intValue(), 0);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.getAttributeContent().size(), 1);
    Assertions.assertEquals(this.getAttributeContent().get(0), InsertRemoveEAttributeTest.NEW_VALUE);
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange((InsertRemoveEAttributeTest.NEW_VALUE_2).intValue(), 1);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Applies two {@link InsertEAttributeValue} EChanges backward.
   */
  @Test
  public void applyBackwardTest() {
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange((InsertRemoveEAttributeTest.NEW_VALUE).intValue(), 0));
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange((InsertRemoveEAttributeTest.NEW_VALUE_2).intValue(), 1));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.getAttributeContent().size(), 1);
    Assertions.assertEquals(this.getAttributeContent().get(0), InsertRemoveEAttributeTest.NEW_VALUE);
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests the {@link InsertEAttributeValue} EChange with invalid index.
   */
  @Test
  public void invalidIndexTest() {
    final int index = 5;
    Assertions.assertTrue(this.getAttributeContent().isEmpty());
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange((InsertRemoveEAttributeTest.NEW_VALUE).intValue(), index);
    final Executable _function = () -> {
      this.applyForwardAndResolve(unresolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Tests an {@link InsertEAttributeValue} with an affected object which has no such attribute.
   */
  @Test
  public void invalidAttributeTest() {
    final NonRoot affectedNonRootEObject = AllElementTypesCreators.aet.NonRoot();
    this.getResource().getContents().add(affectedNonRootEObject);
    final InsertEAttributeValue<EObject, Integer> resolvedChange = this.getAtomicFactory().<EObject, Integer>createInsertAttributeChange(affectedNonRootEObject, 
      this.getAffectedFeature(), 0, InsertRemoveEAttributeTest.NEW_VALUE);
    Assertions.assertEquals(affectedNonRootEObject.eClass().getFeatureID(this.getAffectedFeature()), (-1));
    final Executable _function = () -> {
      this.applyBackward(resolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Tests an {@link InsertEAttributeValue} EChange with the wrong value type.
   */
  @Test
  public void invalidValueTest() {
    final String newInvalidValue = "New String Value";
    final InsertEAttributeValue<EObject, String> resolvedChange = this.getAtomicFactory().<EObject, String>createInsertAttributeChange(this.getAffectedEObject(), this.getAffectedFeature(), 0, newInvalidValue);
    Assertions.assertEquals(this.getAffectedFeature().getEAttributeType().getName(), "EIntegerObject");
    final Executable _function = () -> {
      this.applyBackward(resolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.getAttributeContent().size(), 0);
  }

  /**
   * Model is in state after the changes.
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.getAttributeContent().size(), 2);
    Assertions.assertEquals(this.getAttributeContent().get(0), InsertRemoveEAttributeTest.NEW_VALUE);
    Assertions.assertEquals(this.getAttributeContent().get(1), InsertRemoveEAttributeTest.NEW_VALUE_2);
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange(final int newValue, final int index) {
    return this.unresolve(this.getAtomicFactory().<Root, Integer>createInsertAttributeChange(this.getAffectedEObject(), this.getAffectedFeature(), index, Integer.valueOf(newValue)));
  }
}
