package tools.vitruv.change.atomic.feature.attribute;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link ReplaceSingleValuedEAttribute} EChange,
 * which replaces the value of an attribute with a new one.
 */
@SuppressWarnings("all")
public class ReplaceSingleValuedEAttributeTest extends EChangeTest {
  private Root affectedEObject;

  private EAttribute affectedFeature;

  private String oldValue;

  private String newValue;

  private static final String DEFAULT_ROOT_NAME = "Root Element";

  private static final int DEFAULT_SINGLE_VALUED_EATTRIBUTE_VALUE = 123;

  @BeforeEach
  public void beforeTest() {
    this.affectedEObject = this.getRootObject();
    this.affectedFeature = AllElementTypesPackage.Literals.IDENTIFIED__ID;
    this.oldValue = ReplaceSingleValuedEAttributeTest.DEFAULT_ROOT_NAME;
    this.newValue = "New Root ID";
    this.prepareStateBefore();
  }

  /**
   * Tests whether resolving the {@link ReplaceSingleValuedEAttribute} EChange returns
   * the same class.
   */
  @Test
  public void resolveToCorrectType() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests applying a {@link ReplaceSingleValuedEAttribute} EChange forward
   * by replacing a single value in a root element with a new value.
   */
  @Test
  public void applyForwardTest() {
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange();
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter();
  }

  /**
   * Tests applying a {@link ReplaceSingleValuedEAttribute} EChange backward
   * by replacing a single value in a root element with the old value.
   */
  @Test
  public void applyBackwardTest() {
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange());
    this.prepareStateAfter();
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  /**
   * Tests an affected object which has no such attribute.
   */
  @Test
  public void invalidAttributeTest() {
    final NonRoot affectedNonRootEObject = AllElementTypesCreators.aet.NonRoot();
    this.getResource().getContents().add(affectedNonRootEObject);
    final EAttribute affectedRootFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_EATTRIBUTE;
    final int oldIntValue = ReplaceSingleValuedEAttributeTest.DEFAULT_SINGLE_VALUED_EATTRIBUTE_VALUE;
    final int newIntValue = 500;
    final ReplaceSingleValuedEAttribute<EObject, Integer> resolvedChange = this.getAtomicFactory().<EObject, Integer>createReplaceSingleAttributeChange(affectedNonRootEObject, affectedRootFeature, Integer.valueOf(oldIntValue), Integer.valueOf(newIntValue));
    Assertions.assertEquals(affectedNonRootEObject.eClass().getFeatureID(affectedRootFeature), (-1));
    final Executable _function = () -> {
      this.applyBackward(resolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Tests a {@link ReplaceSingleValuedEAttribue} EChange with the wrong value type.
   */
  @Test
  public void invalidValueTest() {
    final int oldIntValue = ReplaceSingleValuedEAttributeTest.DEFAULT_SINGLE_VALUED_EATTRIBUTE_VALUE;
    final int newIntValue = 500;
    final ReplaceSingleValuedEAttribute<EObject, Integer> resolvedChange = this.getAtomicFactory().<EObject, Integer>createReplaceSingleAttributeChange(this.affectedEObject, 
      this.affectedFeature, Integer.valueOf(oldIntValue), Integer.valueOf(newIntValue));
    Assertions.assertEquals(this.affectedFeature.getEAttributeType().getName(), "EString");
    final Executable _function = () -> {
      this.applyBackward(resolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Set state before the change
   */
  private void prepareStateBefore() {
    this.getRootObject().eSet(this.affectedFeature, this.oldValue);
    this.assertIsStateBefore();
  }

  /**
   * Set state after the change
   */
  private void prepareStateAfter() {
    this.getRootObject().setId(this.newValue);
    this.assertIsStateAfter();
  }

  /**
   * Model is in state before the change.
   */
  private void assertIsStateBefore() {
    Assertions.assertEquals(this.affectedEObject.eGet(this.affectedFeature), this.oldValue);
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter() {
    Assertions.assertEquals(this.affectedEObject.eGet(this.affectedFeature), this.newValue);
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange() {
    return this.unresolve(this.getAtomicFactory().<Root, String>createReplaceSingleAttributeChange(this.affectedEObject, this.affectedFeature, this.oldValue, this.newValue));
  }
}
