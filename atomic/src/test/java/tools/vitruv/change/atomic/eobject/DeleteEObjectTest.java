package tools.vitruv.change.atomic.eobject;

import allElementTypes.Root;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;

/**
 * Test class for the concrete {@link DeleteEObject} EChange,
 * which deletes a EObject from the staging area.
 */
@SuppressWarnings("all")
public class DeleteEObjectTest extends EObjectTest {
  @BeforeEach
  public void beforeTest() {
    this.prepareStateBefore(this.getCreatedObject());
  }

  /**
   * Tests whether resolving the {@link DeleteEObjectTest} EChange returns
   * the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getCreatedObject());
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests a {@link DeleteEObject} EChange by deleting a
   * created EObject from the staging area.
   */
  @Test
  public void applyForwardTest() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getCreatedObject());
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter();
    this.prepareStateBefore(this.getCreatedObject2());
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange(this.getCreatedObject2());
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests a {@link DeleteEObject} EChange by reverting it.
   * Adds a deleted object to the staging area again.
   */
  @Test
  public void applyBackwardTest() {
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getCreatedObject()));
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore(this.getCreatedObject());
    this.prepareStateBefore(this.getCreatedObject2());
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getCreatedObject2()));
    this.applyBackward(resolvedChange2);
    this.assertIsStateBefore(this.getCreatedObject2());
  }

  /**
   * Sets the state of the model before a change.
   */
  private void prepareStateBefore(final Root stagingAreaObject) {
    this.assertIsStateBefore(stagingAreaObject);
  }

  /**
   * Model is in state before the change.
   */
  private void assertIsStateBefore(final Root stagingAreaObject) {
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter() {
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange(final Root oldObject) {
    return this.unresolve(this.getAtomicFactory().<Root>createDeleteEObjectChange(oldObject));
  }
}
