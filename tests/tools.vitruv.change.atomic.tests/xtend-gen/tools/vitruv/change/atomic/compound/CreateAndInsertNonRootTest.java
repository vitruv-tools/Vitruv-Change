package tools.vitruv.change.atomic.compound;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.EObjectTest;
import tools.vitruv.change.atomic.util.EChangeAssertHelper;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link CreateAndInsertNonRoot} EChange,
 * which creates a new non root EObject and inserts it in containment reference.
 */
@SuppressWarnings("all")
public class CreateAndInsertNonRootTest extends EObjectTest {
  private Root affectedEObject;

  private EReference affectedFeature;

  private EList<NonRoot> referenceContent;

  @BeforeEach
  public void prepareState() {
    this.affectedEObject = this.getRootObject();
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE;
    Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
    this.referenceContent = ((EList<NonRoot>) _eGet);
    this.assertIsStateBefore();
  }

  /**
   * Tests whether resolving the {@link CreateAndInsertNonRoot} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange(this.affectedEObject, 0);
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests the {@link CreateAndInsertNonRoot} EChange by applying it forward.
   * Creates and inserts a new non root object into a multi valued
   * containment reference.
   */
  @Test
  public void applyForwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.affectedEObject, 0));
    Assertions.assertEquals(this.referenceContent.size(), 1);
    final CreateEObject createChange = EChangeAssertHelper.<CreateEObject>assertType(resolvedChange.get(0), CreateEObject.class);
    Assertions.assertTrue(this.referenceContent.contains(createChange.getAffectedElement()));
    final List<EChange<Uuid>> unresolvedChange2 = this.createUnresolvedChange(this.affectedEObject, 1);
    this.applyForwardAndResolve(unresolvedChange2);
    this.assertIsStateAfter();
  }

  /**
   * Tests the {@link CreateAndInsertNonRoot} EChange by applying it backward.
   * A non root object which was added to a containment reference will be removed and
   * deleted.
   */
  @Test
  public void applyBackwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange(this.affectedEObject, 0));
    final List<EChange<EObject>> resolvedChange2 = this.applyForwardAndResolve(this.createUnresolvedChange(this.affectedEObject, 1));
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange2);
    final CreateEObject createChange = EChangeAssertHelper.<CreateEObject>assertType(resolvedChange.get(0), CreateEObject.class);
    final CreateEObject createChange2 = EChangeAssertHelper.<CreateEObject>assertType(resolvedChange2.get(0), CreateEObject.class);
    Assertions.assertTrue(this.referenceContent.contains(createChange.getAffectedElement()));
    Assertions.assertFalse(this.referenceContent.contains(createChange2.getAffectedElement()));
    Assertions.assertEquals(this.referenceContent.size(), 1);
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.referenceContent.size(), 0);
  }

  /**
   * Model is in state after the changes.
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.referenceContent.size(), 2);
    MatcherAssert.<NonRoot>assertThat(this.referenceContent.get(0), CoreMatchers.<NonRoot>instanceOf(NonRoot.class));
    MatcherAssert.<NonRoot>assertThat(this.referenceContent.get(1), CoreMatchers.<NonRoot>instanceOf(NonRoot.class));
  }

  /**
   * Creates new unresolved change.
   */
  private List<EChange<Uuid>> createUnresolvedChange(final Root affectedRootObject, final int index) {
    return this.unresolve(this.getCompoundFactory().<Root, NonRoot>createCreateAndInsertNonRootChange(affectedRootObject, this.affectedFeature, AllElementTypesCreators.aet.NonRoot(), index));
  }
}
