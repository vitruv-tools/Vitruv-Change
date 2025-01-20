package tools.vitruv.change.atomic.compound;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link CreateAndReplaceNonRoot} EChange,
 * which creates a new non root EObject and replaces a null value
 * in a single valued containment reference.
 */
@SuppressWarnings("all")
public class CreateAndReplaceNonRootTest extends EChangeTest {
  private Root affectedEObject;

  private EReference affectedFeature;

  @BeforeEach
  public void beforeTest() {
    this.affectedEObject = this.getRootObject();
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE;
    this.prepareStateBefore();
  }

  /**
   * Tests whether resolving the {@link CreateAndReplaceNonRoot} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange();
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests the {@link CreateAndReplaceNonRoot} EChange by applying it forward.
   * Creates and inserts a new non root object into a single valued containment
   * reference.
   */
  @Test
  public void applyForwardTest() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange();
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter();
  }

  /**
   * Tests the {@link CreateAndReplaceNonRoot} EChange by applying it backward.
   * Removes and deletes an existing non root object from a single valued containment
   * reference.
   */
  @Test
  public void applyBackwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange());
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Sets state before
   */
  private void prepareStateBefore() {
    this.affectedEObject.eSet(this.affectedFeature, null);
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the change.
   */
  private void assertIsStateBefore() {
    Assertions.assertNull(this.affectedEObject.eGet(this.affectedFeature));
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter() {
    MatcherAssert.<Object>assertThat(this.affectedEObject.eGet(this.affectedFeature), CoreMatchers.<Object>instanceOf(NonRoot.class));
  }

  /**
   * Creates new unresolved change.
   */
  private List<EChange<Uuid>> createUnresolvedChange() {
    return this.unresolve(this.getCompoundFactory().<Root, NonRoot>createCreateAndReplaceNonRootChange(this.affectedEObject, this.affectedFeature, AllElementTypesCreators.aet.NonRoot()));
  }
}
