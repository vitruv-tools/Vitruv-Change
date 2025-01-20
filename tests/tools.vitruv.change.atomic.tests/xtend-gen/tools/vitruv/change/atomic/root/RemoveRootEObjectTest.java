package tools.vitruv.change.atomic.root;

import allElementTypes.Root;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;

/**
 * Test class for the concrete {@link RemoveRootEObject} EChange,
 * which removes a root element from a resource.
 */
@SuppressWarnings("all")
public class RemoveRootEObjectTest extends RootEChangeTest {
  /**
   * Inserts new root objects into the resource
   * to remove them in the tests.
   */
  @BeforeEach
  public void prepareState() {
    this.prepareStateBefore();
  }

  /**
   * Tests whether resolving the {@link RemoveRootEObject} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewRootObject(), 1);
    EChange<EObject> _applyForwardAndResolve = this.applyForwardAndResolve(unresolvedChange);
    final RemoveRootEObject<EObject> resolvedChange = ((RemoveRootEObject<EObject>) _applyForwardAndResolve);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
    MatcherAssert.<Resource>assertThat(resolvedChange.getResource(), CoreMatchers.<Resource>is(this.getRootObject().eResource()));
    Assertions.assertEquals(resolvedChange.getUri(), this.getRootObject().eResource().getURI().toString());
  }

  /**
   * Tests applying the {@link RemoveRootEObject} EChange
   * by removing two root elements from a resource.
   */
  @Test
  public void applyForwardTest() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewRootObject(), 1);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.getResourceContent().size(), 2);
    MatcherAssert.<EObject>assertThat(this.getNewRootObject2(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(1)));
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange(this.getNewRootObject2(), 1);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying two {@link RemoveRootEObject} EChanges
   * by inserts two removed root objects in a resource.
   */
  @Test
  public void applyBackwardTest() {
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewRootObject(), 1));
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewRootObject2(), 1));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.getResourceContent().size(), 2);
    Assertions.assertTrue(this.getResourceContent().contains(this.getNewRootObject2()));
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests a {@link RemoveRootEObject} EChange with invalid index.
   */
  @Test
  public void invalidIndexTest() {
    final int index = 5;
    int _size = this.getResourceContent().size();
    boolean _lessThan = (_size < index);
    Assertions.assertTrue(_lessThan);
    final EChange<Uuid> invalidChange = this.createUnresolvedChange(this.getNewRootObject(), index);
    final Executable _function = () -> {
      this.applyForwardAndResolve(invalidChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Sets the state of the model before the changes.
   */
  private void prepareStateBefore() {
    this.getResourceContent().add(1, this.getNewRootObject());
    this.getResourceContent().add(2, this.getNewRootObject2());
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
  private EChange<Uuid> createUnresolvedChange(final Root rootObject, final int index) {
    return this.unresolve(this.getAtomicFactory().<Root>createRemoveRootChange(rootObject, this.getResource(), index));
  }
}
