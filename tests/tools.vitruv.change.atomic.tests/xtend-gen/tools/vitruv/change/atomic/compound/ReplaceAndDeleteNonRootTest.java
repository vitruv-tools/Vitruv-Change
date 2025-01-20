package tools.vitruv.change.atomic.compound;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link ReplaceAndDeleteNonRoot} EChange,
 * which removes a non root EObject from a single valued containment reference
 * and delets it.
 */
@SuppressWarnings("all")
public class ReplaceAndDeleteNonRootTest extends EChangeTest {
  private Root affectedEObject;

  private EReference affectedFeature;

  private NonRoot oldNonRootObject;

  @BeforeEach
  public void beforeTest() {
    this.affectedEObject = this.getRootObject();
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE;
    this.oldNonRootObject = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
    this.prepareStateBefore();
  }

  /**
   * Tests whether resolving the {@link ReplaceAndDeleteNonRoot} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(this.oldNonRootObject);
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests the {@link ReplaceAndDeleteNonRoot} EChange by applying it forward.
   * Removes a non root object from a single valued containment reference and deletes it.
   */
  @Test
  public void applyForwardTest() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(this.oldNonRootObject);
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter();
  }

  /**
   * Tests the {@link ReplaceAndDeleteNonRoot} EChange by applying it backward.
   * Creates and inserts an old non root object into a single valued containment
   * reference.
   */
  @Test
  public void applyBackwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.oldNonRootObject));
    this.prepareStateAfter();
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Sets the state before the change.
   */
  private void prepareStateBefore() {
    this.affectedEObject.eSet(this.affectedFeature, this.oldNonRootObject);
    this.assertIsStateBefore();
  }

  /**
   * Sets the state after the change.
   */
  private void prepareStateAfter() {
    this.affectedEObject.eSet(this.affectedFeature, null);
    this.assertIsStateAfter();
  }

  /**
   * Model is in state before the change.
   */
  private void assertIsStateBefore() {
    Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
    MatcherAssert.<NonRoot>assertThat(this.oldNonRootObject, ModelMatchers.<NonRoot>equalsDeeply(((NonRoot) _eGet)));
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter() {
    Assertions.assertNull(this.affectedEObject.eGet(this.affectedFeature));
  }

  /**
   * Creates new unresolved change.
   */
  private List<EChange<Uuid>> createUnresolvedChange(final NonRoot oldNonRoot) {
    return this.unresolve(this.getCompoundFactory().<Root, NonRoot>createReplaceAndDeleteNonRootChange(this.affectedEObject, this.affectedFeature, oldNonRoot));
  }
}
