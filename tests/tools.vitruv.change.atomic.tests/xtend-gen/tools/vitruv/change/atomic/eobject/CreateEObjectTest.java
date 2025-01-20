package tools.vitruv.change.atomic.eobject;

import allElementTypes.Root;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link CreateEObject} EChange,
 * which creates a new EObject and puts it in the staging area.
 */
@SuppressWarnings("all")
public class CreateEObjectTest extends EObjectTest {
  @BeforeEach
  public void beforeTest() {
    this.prepareStateBefore();
  }

  /**
   * Tests whether resolving the {@link CreateEObjectTest} EChange returns
   * the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests applying a {@link CreateEObject} EChange forward by creating a
   * new EObject and putting it in the staging area.
   */
  @Test
  public void applyForwardTest() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange();
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter();
    this.prepareStateBefore();
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange();
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying a {@link CreateEObject} EChange backward
   * by removing a newly created object from the staging area.
   */
  @Test
  public void applyBackwardTest() {
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange());
    this.applyBackward(resolvedChange);
    this.assertIsStateAfter();
  }

  /**
   * Sets the state of the model before the change.
   */
  private void prepareStateBefore() {
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the change.
   */
  private void assertIsStateBefore() {
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter() {
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange() {
    return this.unresolve(this.getAtomicFactory().<Root>createCreateEObjectChange(AllElementTypesCreators.aet.Root()));
  }
}
