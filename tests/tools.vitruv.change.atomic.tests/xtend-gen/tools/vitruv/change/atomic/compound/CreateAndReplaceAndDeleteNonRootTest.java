package tools.vitruv.change.atomic.compound;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EObjectTest;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link CreateAndReplaceAndDeleteNonRoot} EChange,
 * which creates a new non root EObject and replaces an existing non root object
 * in a single value containment reference. The existing one will be deleted.
 */
@SuppressWarnings("all")
public class CreateAndReplaceAndDeleteNonRootTest extends EObjectTest {
  private Root affectedEObject;

  private NonRoot oldValue;

  private EReference affectedFeature;

  @BeforeEach
  public void prepareState() {
    this.oldValue = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
    this.affectedEObject = this.getRootObject();
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE;
    this.prepareStateBefore();
  }

  /**
   * Tests applying the {@link CreateAndReplaceAndDeleteNonRoot} EChange forward
   * by creating a new non root and replacing an existing one.
   */
  @Test
  public void applyForwardTest() {
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange();
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying the {@link CreateAndReplaceAndDeleteNonRoot} EChange backward
   * by replacing a single value containment reference with its old value.
   */
  @Test
  public void applyBackwardTest() {
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange());
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  private void prepareStateBefore() {
    this.affectedEObject.eSet(this.affectedFeature, this.oldValue);
    this.assertIsStateBefore();
  }

  /**
   * Model is in state before the changes.
   */
  private void assertIsStateBefore() {
    Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
    MatcherAssert.<NonRoot>assertThat(this.oldValue, ModelMatchers.<NonRoot>equalsDeeply(((NonRoot) _eGet)));
  }

  /**
   * Model is in state after the changes.
   */
  private void assertIsStateAfter() {
    MatcherAssert.<Object>assertThat(this.affectedEObject.eGet(this.affectedFeature), CoreMatchers.<Object>instanceOf(NonRoot.class));
  }

  /**
   * Creates new unresolved change.
   */
  private List<EChange<Uuid>> createUnresolvedChange() {
    return this.unresolve(this.getCompoundFactory().<Root, NonRoot>createCreateAndReplaceAndDeleteNonRootChange(this.affectedEObject, this.affectedFeature, this.oldValue, 
      AllElementTypesCreators.aet.NonRoot()));
  }
}
