package tools.vitruv.change.atomic.root;

import allElementTypes.Root;
import com.google.common.base.Objects;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;

/**
 * Test class for the concrete {@link InsertRootEObject} EChange,
 * which inserts a new root element into a resource.
 */
@SuppressWarnings("all")
public class InsertRootEObjectTest extends RootEChangeTest {
  /**
   * Tests whether resolving the {@link InsertRootEObject} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewRootObject(), 1);
    EChange<EObject> _applyForwardAndResolve = this.applyForwardAndResolve(unresolvedChange);
    final InsertRootEObject<EObject> resolvedChange = ((InsertRootEObject<EObject>) _applyForwardAndResolve);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
    MatcherAssert.<Resource>assertThat(resolvedChange.getResource(), CoreMatchers.<Resource>is(this.getRootObject().eResource()));
    Assertions.assertEquals(resolvedChange.getUri(), this.getRootObject().eResource().getURI().toString());
  }

  /**
   * Tests applying a {@link InsertRootEObject} EChange forward
   * by inserting a new root elements into a resource.
   */
  @Test
  public void applyForwardTest() {
    this.assertIsStateBefore();
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange(this.getNewRootObject(), 1);
    this.applyForwardAndResolve(unresolvedChange);
    Assertions.assertEquals(this.getResourceContent().size(), 2);
    Root _newRootObject = this.getNewRootObject();
    EObject _get = this.getResourceContent().get(1);
    boolean _equals = Objects.equal(_newRootObject, _get);
    Assertions.assertTrue(_equals);
    final EChange<Uuid> unresolvedChange2 = this.createUnresolvedChange(this.getNewRootObject2(), 2);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying two {@link InsertRootEObject} EChanges backward
   * by removing two inserted root objects from a resource.
   */
  @Test
  public void applyBackwardTest() {
    this.assertIsStateBefore();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewRootObject(), 1));
    final EChange<EObject> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.getNewRootObject2(), 2));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.getResourceContent().size(), 2);
    MatcherAssert.<EObject>assertThat(this.getNewRootObject(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(1)));
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests applying the {@link InsertRootEObject} EChange with invalid index.
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
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.getResourceContent().size(), 1);
  }

  /**
   * Model is in state after the changes.
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.getResourceContent().size(), 3);
    MatcherAssert.<EObject>assertThat(this.getNewRootObject(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(1)));
    MatcherAssert.<EObject>assertThat(this.getNewRootObject2(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(2)));
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange(final Root rootObject, final int index) {
    return this.unresolve(this.getAtomicFactory().<Root>createInsertRootChange(rootObject, this.getResource(), index));
  }
}
