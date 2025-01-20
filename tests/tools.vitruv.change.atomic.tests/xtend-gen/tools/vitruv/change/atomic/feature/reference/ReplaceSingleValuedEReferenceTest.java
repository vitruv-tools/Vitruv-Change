package tools.vitruv.change.atomic.feature.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Identified;
import allElementTypes.NonRoot;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link ReplaceSingleValuedEReference} EChange,
 * which replaces the value of a reference with a new one.
 */
@SuppressWarnings("all")
public class ReplaceSingleValuedEReferenceTest extends ReferenceEChangeTest {
  private NonRoot oldValue;

  private EReference affectedFeature;

  @BeforeEach
  public void prepareElements() {
    this.oldValue = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
  }

  /**
   * Tests whether resolving the {@link ReplaceSingleValuedEReference} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    this.isNonContainmentTest();
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  @ParameterizedTest
  @MethodSource("testConfigurations")
  public void applyForwardTest(final boolean isContainment, final boolean newValueIsNull, final boolean oldValueIsNull) {
    if (isContainment) {
      this.isContainmentTest();
    } else {
      this.isNonContainmentTest();
    }
    if (newValueIsNull) {
      this.setNewValue(null);
    }
    if (oldValueIsNull) {
      this.oldValue = null;
    }
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange();
    this.assertIsStateBefore(this.getNewValue());
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter(this.oldValue);
  }

  @ParameterizedTest
  @MethodSource("testConfigurations")
  public void applyBackwardTest(final boolean isContainment, final boolean newValueIsNull, final boolean oldValueIsNull) {
    if (isContainment) {
      this.isContainmentTest();
    } else {
      this.isNonContainmentTest();
    }
    if (newValueIsNull) {
      this.setNewValue(null);
    }
    if (oldValueIsNull) {
      this.oldValue = null;
    }
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange());
    this.prepareReference(this.getNewValue());
    this.assertIsStateAfter(null);
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore(null);
  }

  /**
   * Tests applying a {@link ReplaceSingleValuedEReference} EChange backward
   * by replacing a single value containment reference with its old value.
   * The old value was null.
   */
  @Test
  public void replaceSingleValuedEReferenceApplyBackwardOldValueNullTest() {
    this.oldValue = null;
    this.isContainmentTest();
    final EChange<EObject> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange());
    this.prepareReference(this.getNewValue());
    this.assertIsStateAfter(null);
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore(this.getNewValue());
  }

  /**
   * Starts a test with a containment feature and sets state before.
   */
  private void isContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE;
    this.prepareReference(this.oldValue);
  }

  /**
   * Starts a test with a non containment feature and sets state before.
   */
  private void isNonContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE;
    this.prepareReference(this.oldValue);
    this.prepareResource();
  }

  /**
   * Sets the value of the affected feature.
   * @param object The new value of the affected feature.
   */
  private void prepareReference(final EObject object) {
    this.getAffectedEObject().eSet(this.affectedFeature, object);
  }

  /**
   * Prepares all new and old values and stores them in a resource.
   */
  @Override
  protected void prepareResource() {
    super.prepareResource();
    this.getResource().getContents().add(this.oldValue);
  }

  /**
   * Model is in state before the change.
   */
  private void assertIsStateBefore(final NonRoot valueInStagingArea) {
    this.resourceIsStateBefore();
    Object _eGet = this.getAffectedEObject().eGet(this.affectedFeature);
    final EObject currentValue = ((EObject) _eGet);
    if (((this.oldValue != null) || (currentValue != null))) {
      MatcherAssert.<EObject>assertThat(this.oldValue, ModelMatchers.<EObject>equalsDeeply(currentValue));
    }
  }

  /**
   * Resource is in state before the change.
   */
  private void resourceIsStateBefore() {
    boolean _isContainment = this.affectedFeature.isContainment();
    boolean _not = (!_isContainment);
    if (_not) {
      Assertions.assertEquals(this.getResourceContent().size(), 4);
      MatcherAssert.<EObject>assertThat(this.getNewValue(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(1)));
      MatcherAssert.<EObject>assertThat(this.getNewValue2(), ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(2)));
      MatcherAssert.<EObject>assertThat(this.oldValue, ModelMatchers.<EObject>equalsDeeply(this.getResourceContent().get(3)));
    } else {
      Assertions.assertEquals(this.getResourceContent().size(), 1);
    }
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter(final NonRoot valueInStaggingArea) {
    this.resourceIsStateBefore();
    Object _eGet = this.getAffectedEObject().eGet(this.affectedFeature);
    final EObject currentValue = ((EObject) _eGet);
    NonRoot _newValue = this.getNewValue();
    boolean _tripleEquals = (_newValue == null);
    if (_tripleEquals) {
      Assertions.assertNull(currentValue);
    } else {
      MatcherAssert.<EObject>assertThat(this.getNewValue(), ModelMatchers.<EObject>equalsDeeply(currentValue));
    }
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange() {
    return this.unresolve(this.getAtomicFactory().<Identified>createReplaceSingleReferenceChange(this.getAffectedEObject(), this.affectedFeature, this.oldValue, this.getNewValue()));
  }

  public static Stream<Arguments> testConfigurations() {
    return Stream.<Arguments>of(
      Arguments.of(Named.<Boolean>of("non containment", Boolean.valueOf(false)), Named.<Boolean>of("with new", Boolean.valueOf(false)), Named.<Boolean>of("with old", Boolean.valueOf(false))), 
      Arguments.of(Named.<Boolean>of("containment", Boolean.valueOf(true)), Named.<Boolean>of("with new", Boolean.valueOf(false)), Named.<Boolean>of("with old", Boolean.valueOf(false))), 
      Arguments.of(Named.<Boolean>of("containment", Boolean.valueOf(true)), Named.<Boolean>of("with new = null", Boolean.valueOf(true)), Named.<Boolean>of("with old", Boolean.valueOf(false))), 
      Arguments.of(Named.<Boolean>of("containment", Boolean.valueOf(true)), Named.<Boolean>of("with new", Boolean.valueOf(false)), Named.<Boolean>of("with old = null", Boolean.valueOf(false))));
  }
}
