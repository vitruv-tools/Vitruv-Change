package tools.vitruv.change.atomic.feature.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Identified;
import allElementTypes.NonRoot;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;

/**
 * Test class for the concrete {@link InsertEReferenceValue} EChange,
 * which inserts a reference in a multivalued attribute.
 */
@SuppressWarnings("all")
public class InsertEReferenceTest extends ReferenceEChangeTest {
  private EReference affectedFeature;

  private EList<NonRoot> referenceContent;

  /**
   * Tests whether resolving the {@link InsertEReference} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    this.isNonContainmentTest();
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewValue(), 0);
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests applying the {@link InsertEReference} EChange forward by
   * inserting new values in a multivalued reference.
   * The affected feature is a non containment reference, so the
   * new value is already in the resource.
   */
  @Test
  public void applyForwardNonContainmentTest() {
    this.isNonContainmentTest();
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewValue(), 0);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertSame(this.referenceContent.get(0), this.getNewValue());
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange(this.getNewValue2(), 1);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying the {@link InsertEReference} EChange forward by
   * inserting new values from a multivalued reference.
   * The affected feature is a containment reference, so the
   * new value is from the staging area.
   */
  @Test
  public void applyForwardContainmentTest() {
    this.isContainmentTest();
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewValue(), 0);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertSame(this.referenceContent.get(0), this.getNewValue());
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange(this.getNewValue2(), 1);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying two {@link InsertEReference} EChanges backward by
   * removing new added values from a multivalued reference.
   * The affected feature is a non containment reference, so the
   * removed values are already in the resource.
   */
  @Test
  public void applyBackwardNonContainmentTest() {
    this.isNonContainmentTest();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue(), 0));
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue2(), 1));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertSame(this.referenceContent.get(0), this.getNewValue());
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests applying two {@link InsertEReference} EChanges backward by
   * removing new added values from a multivalued reference.
   * The affected feature is a containment reference, so the
   * removed values will be placed in the staging area after removing them.
   */
  @Test
  public void applyBackwardContainmentTest() {
    this.isContainmentTest();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue(), 0));
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue2(), 1));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertSame(this.referenceContent.get(0), this.getNewValue());
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests the {@link InsertEReference} EChange with invalid index.
   */
  @Test
  public void invalidIndexTest() {
    this.isNonContainmentTest();
    final int index = 5;
    Assertions.assertEquals(this.referenceContent.size(), 0);
    final EChange<Uuid> resolvedChange = this.createUnresolvedChange(this.getNewValue(), index);
    final Executable _function = () -> {
      this.applyForwardAndResolve(resolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Tests an affected object which has no such reference.
   */
  @Test
  public void invalidAttributeTest() {
    this.isNonContainmentTest();
    final NonRoot invalidAffectedEObject = this.getNewValue2();
    final InsertEReference<EObject> resolvedChange = this.getAtomicFactory().<EObject>createInsertReferenceChange(invalidAffectedEObject, 
      this.affectedFeature, this.getNewValue(), 0);
    Assertions.assertEquals(invalidAffectedEObject.eClass().getFeatureID(this.affectedFeature), (-1));
    final Executable _function = () -> {
      this.applyBackward(resolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Starts a test with a containment feature and sets state before.
   */
  private void isContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE;
    Object _eGet = this.getAffectedEObject().eGet(this.affectedFeature);
    this.referenceContent = ((EList<NonRoot>) _eGet);
    this.assertIsStateBefore();
  }

  /**
   * Starts a test with a non containment feature and sets state before.
   */
  private void isNonContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE;
    Object _eGet = this.getAffectedEObject().eGet(this.affectedFeature);
    this.referenceContent = ((EList<NonRoot>) _eGet);
    this.prepareResource();
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the change.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.referenceContent.size(), 0);
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.referenceContent.size(), 2);
    MatcherAssert.<NonRoot>assertThat(this.getNewValue(), ModelMatchers.<NonRoot>equalsDeeply(this.referenceContent.get(0)));
    MatcherAssert.<NonRoot>assertThat(this.getNewValue2(), ModelMatchers.<NonRoot>equalsDeeply(this.referenceContent.get(1)));
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange(final NonRoot newValue, final int index) {
    return this.unresolve(this.getAtomicFactory().<Identified>createInsertReferenceChange(this.getAffectedEObject(), this.affectedFeature, newValue, index));
  }
}
