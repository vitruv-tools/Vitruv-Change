package tools.vitruv.change.atomic.feature.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Identified;
import allElementTypes.NonRoot;
import com.google.common.base.Objects;
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
 * Test class for the concrete {@link RemoveEReference} EChange,
 * which removes a reference from a multivalued attribute.
 */
@SuppressWarnings("all")
public class RemoveEReferenceTest extends ReferenceEChangeTest {
  private EReference affectedFeature;

  private EList<NonRoot> referenceContent;

  /**
   * Tests whether resolving the {@link RemoveEReference} EChange
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
   * Tests applying the {@link RemoveEReference} EChange forward by
   * removing inserted values from a multivalued reference.
   * The reference is a non containment reference, so the values has
   * to be a root object in the resource.
   */
  @Test
  public void applyForwardNonContainmentTest() {
    this.isNonContainmentTest();
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewValue(), 0);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertEquals(this.referenceContent.get(0), this.getNewValue2());
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange(this.getNewValue2(), 0);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying the {@link RemoveEReference} EChange forward by
   * removing inserted values from a multivalued reference.
   * The reference is a containment reference, so the values
   * will be in the staging area after removing them.
   */
  @Test
  public void applyForwardContainmentTest() {
    this.isContainmentTest();
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewValue(), 0);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertEquals(this.referenceContent.get(0), this.getNewValue2());
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange(this.getNewValue2(), 0);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying a {@link RemoveEReference} EChange backward. The reference is
   * a non containment reference so the values has to be in the resource.
   */
  @Test
  public void applyBackwardNonContainmentTest() {
    this.isNonContainmentTest();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue(), 0));
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue2(), 0));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertEquals(this.referenceContent.get(0), this.getNewValue2());
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests applying a {@link RemoveEReference} EChange backward. The reference is
   * a containment reference so the values has to be in the staging area
   * before they are reinserted.
   */
  @Test
  public void applyBackwardContainmentTest() {
    this.isContainmentTest();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue(), 0));
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewValue2(), 0));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertEquals(this.referenceContent.get(0), this.getNewValue2());
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests a {@link RemoveEReference} EChange with invalid index.
   */
  @Test
  public void invalidIndexTest() {
    this.isNonContainmentTest();
    int index = 5;
    Assertions.assertEquals(this.referenceContent.size(), 2);
    NonRoot _get = this.referenceContent.get(0);
    NonRoot _newValue = this.getNewValue();
    boolean _equals = Objects.equal(_get, _newValue);
    Assertions.assertTrue(_equals);
    final EChange<Uuid> resolvedChange = this.createUnresolvedChange(this.getNewValue(), index);
    final Executable _function = () -> {
      this.applyForwardAndResolve(resolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Tests a {@link RemoveEReference} EChange with with an affected object which has no
   * such reference.
   */
  @Test
  public void invalidAttributeTest() {
    this.isNonContainmentTest();
    final NonRoot invalidAffectedEObject = this.getNewValue2();
    final RemoveEReference<EObject> resolvedChange = this.getAtomicFactory().<EObject>createRemoveReferenceChange(invalidAffectedEObject, 
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
    this.prepareReference();
    this.assertIsStateBefore();
  }

  /**
   * Starts a test with a non containment feature and sets state before.
   */
  private void isNonContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE;
    Object _eGet = this.getAffectedEObject().eGet(this.affectedFeature);
    this.referenceContent = ((EList<NonRoot>) _eGet);
    this.prepareReference();
    this.prepareResource();
    this.assertIsStateBefore();
  }

  /**
   * Prepares the multivalued reference used in the tests
   * and fills it with the new values.
   */
  private void prepareReference() {
    this.referenceContent.add(this.getNewValue());
    this.referenceContent.add(this.getNewValue2());
  }

  /**
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.referenceContent.size(), 2);
    NonRoot _get = this.referenceContent.get(0);
    MatcherAssert.<EObject>assertThat(this.getNewValue(), ModelMatchers.<EObject>equalsDeeply(((EObject) _get)));
    NonRoot _get_1 = this.referenceContent.get(1);
    MatcherAssert.<EObject>assertThat(this.getNewValue2(), ModelMatchers.<EObject>equalsDeeply(((EObject) _get_1)));
  }

  /**
   * Model is in state after the changes.
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.referenceContent.size(), 0);
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange(final NonRoot oldValue, final int index) {
    return this.unresolve(this.getAtomicFactory().<Identified>createRemoveReferenceChange(this.getAffectedEObject(), this.affectedFeature, oldValue, index));
  }
}
