package tools.vitruv.change.atomic.compound;

import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.util.EChangeAssertHelper;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link CreateAndInsertRoot} EChange,
 * which creates a new root EObject and inserts it in a resource.
 */
@SuppressWarnings("all")
public class CreateAndInsertRootTest extends EChangeTest {
  private EList<EObject> resourceContent;

  /**
   * Calls setup of the superclass and creates two new root elements
   * which can be inserted.
   */
  @BeforeEach
  public void beforeTest() {
    this.resourceContent = this.getResource().getContents();
    this.assertIsStateBefore();
  }

  /**
   * Tests whether resolving the {@link CreateAndInsertRoot} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(1);
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests applying the {@link CreateAndInsertRoot} EChange forward
   * by creating and inserting a new root object.
   */
  @Test
  public void applyForwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(1));
    Assertions.assertEquals(this.resourceContent.size(), 2);
    final CreateEObject createChange = EChangeAssertHelper.<CreateEObject>assertType(resolvedChange.get(0), CreateEObject.class);
    Assertions.assertTrue(this.resourceContent.contains(createChange.getAffectedElement()));
    final List<EChange<Uuid>> unresolvedChange2 = this.createUnresolvedChange(2);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying the {@link CreateAndInsertRoot} EChange backward
   * by reverting the change. It removes and deletes a root object.
   */
  @Test
  public void applyBackwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(1));
    final List<EChange<EObject>> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(2));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    Assertions.assertEquals(this.resourceContent.size(), 2);
    final CreateEObject createChange = EChangeAssertHelper.<CreateEObject>assertType(resolvedChange.get(0), CreateEObject.class);
    final CreateEObject createChange2 = EChangeAssertHelper.<CreateEObject>assertType(resolvedChange2.get(0), CreateEObject.class);
    Assertions.assertTrue(this.resourceContent.contains(createChange.getAffectedElement()));
    Assertions.assertFalse(this.resourceContent.contains(createChange2.getAffectedElement()));
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.resourceContent.size(), 1);
  }

  /**
   * Model is in state after the changes
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.resourceContent.size(), 3);
    MatcherAssert.<EObject>assertThat(this.resourceContent.get(1), CoreMatchers.<EObject>instanceOf(Root.class));
    MatcherAssert.<EObject>assertThat(this.resourceContent.get(2), CoreMatchers.<EObject>instanceOf(Root.class));
  }

  /**
   * Creates new unresolved change.
   */
  private List<EChange<Uuid>> createUnresolvedChange(final int index) {
    return this.unresolve(this.getCompoundFactory().<Root>createCreateAndInsertRootChange(AllElementTypesCreators.aet.Root(), this.getResource(), index));
  }
}
