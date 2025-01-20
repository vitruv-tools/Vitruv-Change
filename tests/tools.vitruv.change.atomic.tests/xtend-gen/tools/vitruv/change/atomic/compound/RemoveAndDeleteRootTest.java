package tools.vitruv.change.atomic.compound;

import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.root.RootEChangeTest;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;

/**
 * Test class for the concrete {@link RemoveAndDeleteRoot} EChange,
 * which removes a root element from a resource and deletes it.
 */
@SuppressWarnings("all")
public class RemoveAndDeleteRootTest extends RootEChangeTest {
  @BeforeEach
  @Override
  public void beforeTest() {
    super.beforeTest();
    this.prepareStateBefore();
  }

  /**
   * Tests whether resolving the {@link RemoveAndDeleteRoot} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(this.getNewRootObject(), 1);
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests the {@link RemoveAndDeleteRoot} EChange by removing
   * and deleting a root object.
   */
  @Test
  public void applyForwardTest() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(this.getNewRootObject(), 1);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.getResourceContent().size(), 2);
    Assertions.assertFalse(this.getResourceContent().contains(this.getNewRootObject()));
    Assertions.assertTrue(this.getResourceContent().contains(this.getNewRootObject2()));
    final List<EChange<Uuid>> unresolvedChange2 = this.createUnresolvedChange(this.getNewRootObject2(), 1);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests the {@link RemoveAndDeleteRoot} EChange by reverting the change.
   * It creates and inserts a root object.
   */
  @Test
  public void applyBackwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewRootObject(), 1));
    final List<EChange<EObject>> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewRootObject2(), 1));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.getResourceContent().size(), 2);
    Assertions.assertTrue(this.getResourceContent().contains(this.getNewRootObject2()));
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Sets the state of the model before the changes.
   */
  private void prepareStateBefore() {
    this.getResourceContent().add(this.getNewRootObject());
    this.getResourceContent().add(this.getNewRootObject2());
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.getResourceContent().size(), 3);
    MatcherAssert.<EObject>assertThat(this.getNewRootObject(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(1)));
    MatcherAssert.<EObject>assertThat(this.getNewRootObject2(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(2)));
  }

  /**
   * Model is in state after the changes.
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.getResourceContent().size(), 1);
  }

  /**
   * Creates new unresolved change.
   */
  private List<EChange<Uuid>> createUnresolvedChange(final Root newObject, final int index) {
    return this.unresolve(this.getCompoundFactory().<Root>createRemoveAndDeleteRootChange(newObject, this.getResource(), index));
  }
}
