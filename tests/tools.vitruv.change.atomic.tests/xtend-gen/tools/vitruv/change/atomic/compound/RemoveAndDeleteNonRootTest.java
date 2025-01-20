package tools.vitruv.change.atomic.compound;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.reference.ReferenceEChangeTest;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;

/**
 * Test class for the concrete {@link RemoveAndDeleteNonRoot} EChange,
 * which removes a non root element reference from a containment reference
 * list and deletes it.
 */
@SuppressWarnings("all")
public class RemoveAndDeleteNonRootTest extends ReferenceEChangeTest {
  private EReference affectedFeature;

  private EList<NonRoot> referenceContent;

  @BeforeEach
  public void prepareState() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE;
    Object _eGet = this.getAffectedEObject().eGet(this.affectedFeature);
    this.referenceContent = ((EList<NonRoot>) _eGet);
    this.prepareStateBefore();
  }

  /**
   * Tests whether resolving the {@link RemoveAndDeleteNonRoot} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(this.getAffectedEObject(), this.getNewValue(), 0);
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests the {@link RemoveAndDeleteNonRoot} EChange by applying it forward.
   * Removes and deletes a non root element from a containment reference.
   */
  @Test
  public void applyForwardTest() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(this.getAffectedEObject(), this.getNewValue(), 0);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertFalse(this.referenceContent.contains(this.getNewValue()));
    Assertions.assertTrue(this.referenceContent.contains(this.getNewValue2()));
    final List<EChange<Uuid>> unresolvedChange2 = this.createUnresolvedChange(this.getAffectedEObject(), this.getNewValue2(), 0);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests the {@link RemoveAndDeleteNonRoot} EChange by applying it backward.
   * Creates and reinserts the removed object.
   */
  @Test
  public void applyBackwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getAffectedEObject(), this.getNewValue(), 0));
    final List<EChange<EObject>> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getAffectedEObject(), this.getNewValue2(), 0));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.referenceContent.size(), 1);
    Assertions.assertTrue(this.referenceContent.contains(this.getNewValue2()));
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Sets the state of the model before the changes.
   */
  private void prepareStateBefore() {
    this.referenceContent.add(this.getNewValue());
    this.referenceContent.add(this.getNewValue2());
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.referenceContent.size(), 2);
    MatcherAssert.<NonRoot>assertThat(this.getNewValue(), ModelMatchers.<NonRoot>equalsDeeply(this.referenceContent.get(0)));
    MatcherAssert.<NonRoot>assertThat(this.getNewValue2(), ModelMatchers.<NonRoot>equalsDeeply(this.referenceContent.get(1)));
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
  private List<EChange<Uuid>> createUnresolvedChange(final Root affectedRootObject, final NonRoot newNonRoot, final int index) {
    return this.unresolve(this.getCompoundFactory().<Root, NonRoot>createRemoveAndDeleteNonRootChange(affectedRootObject, this.affectedFeature, newNonRoot, index));
  }
}
