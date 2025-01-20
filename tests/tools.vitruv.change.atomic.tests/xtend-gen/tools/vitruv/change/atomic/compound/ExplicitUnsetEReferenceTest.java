package tools.vitruv.change.atomic.compound;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Identified;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
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
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.testutils.matchers.ModelMatchers;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the concrete {@link ExplicitUnsetEReference} EChange,
 * which unsets a single or multi valued reference.
 */
@SuppressWarnings("all")
public class ExplicitUnsetEReferenceTest extends EChangeTest {
  private Root affectedEObject;

  private EReference affectedFeature;

  private EList<NonRoot> referenceContent;

  private NonRoot oldValue;

  private NonRoot oldValue2;

  private NonRoot oldValue3;

  @BeforeEach
  public void beforeTest() {
    this.affectedEObject = this.getRootObject();
    this.oldValue = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
    this.oldValue2 = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
    this.oldValue3 = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
  }

  /**
   * Tests whether the {@link ExplicitUnsetEReference} EChange resolves to the correct type.
   */
  @Test
  public void resolveToCorrectType() {
    this.isSingleValuedNonContainmentTest();
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange();
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  @ParameterizedTest
  @MethodSource("testConfigurations")
  public void applyForwardTest(final boolean isContainment, final boolean isSingleValued) {
    if (isSingleValued) {
      if (isContainment) {
        this.isSingleValuedContainmentTest();
      } else {
        this.isSingleValuedNonContainmentTest();
      }
    } else {
      if (isContainment) {
        this.isMultiValuedContainmentTest();
      } else {
        this.isMultiValuedNonContainmentTest();
      }
    }
    this.applyForwardTest();
  }

  @ParameterizedTest
  @MethodSource("testConfigurations")
  public void applyBackwardTest(final boolean isContainment, final boolean isSingleValued) {
    if (isSingleValued) {
      if (isContainment) {
        this.isSingleValuedContainmentTest();
      } else {
        this.isSingleValuedNonContainmentTest();
      }
    } else {
      if (isContainment) {
        this.isMultiValuedContainmentTest();
      } else {
        this.isMultiValuedNonContainmentTest();
      }
    }
    this.applyBackwardTest();
  }

  /**
   * Starts a test with a single valued non containment reference
   * and sets the state before.
   */
  private void isSingleValuedNonContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE;
    this.prepareStateBefore();
  }

  /**
   * Starts a test with a single valued containment reference
   * and sets the state before.
   */
  private void isSingleValuedContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE;
    this.prepareStateBefore();
  }

  /**
   * Starts a test with a multi valued non containment reference
   * and sets the state before.
   */
  private void isMultiValuedNonContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_NON_CONTAINMENT_EREFERENCE;
    Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
    this.referenceContent = ((EList<NonRoot>) _eGet);
    this.prepareStateBefore();
  }

  /**
   * Starts a test with a multi valued containment reference
   * and sets the state before.
   */
  private void isMultiValuedContainmentTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_CONTAINMENT_EREFERENCE;
    Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
    this.referenceContent = ((EList<NonRoot>) _eGet);
    this.prepareStateBefore();
  }

  /**
   * Sets the state before the change, depending on single or multi valued
   * reference, or containment / non containment.
   */
  private void prepareStateBefore() {
    boolean _isContainment = this.affectedFeature.isContainment();
    boolean _not = (!_isContainment);
    if (_not) {
      this.prepareResource();
    }
    this.prepareReference();
  }

  /**
   * Prepares the resource and puts every value
   * of the feature in the resource.
   */
  private void prepareResource() {
    this.getResource().getContents().add(this.oldValue);
    this.getResource().getContents().add(this.oldValue2);
    this.getResource().getContents().add(this.oldValue3);
  }

  /**
   * Prepares the reference and puts the
   * affected values into the reference.
   */
  private void prepareReference() {
    boolean _isMany = this.affectedFeature.isMany();
    boolean _not = (!_isMany);
    if (_not) {
      this.affectedEObject.eSet(this.affectedFeature, this.oldValue);
    } else {
      this.referenceContent.add(this.oldValue);
      this.referenceContent.add(this.oldValue2);
      this.referenceContent.add(this.oldValue3);
    }
  }

  /**
   * The model is in state before the change.
   */
  private void assertIsStateBefore() {
    Assertions.assertTrue(this.affectedEObject.eIsSet(this.affectedFeature));
    boolean _isContainment = this.affectedFeature.isContainment();
    boolean _not = (!_isContainment);
    if (_not) {
      this.assertResourceIsStateBefore();
    }
    this.assertReferenceIsStateBefore();
  }

  /**
   * The affected reference is in state before the change.
   */
  private void assertReferenceIsStateBefore() {
    boolean _isContainment = this.affectedFeature.isContainment();
    boolean _not = (!_isContainment);
    if (_not) {
      boolean _isMany = this.affectedFeature.isMany();
      boolean _not_1 = (!_isMany);
      if (_not_1) {
        Assertions.assertEquals(this.oldValue, this.affectedEObject.eGet(this.affectedFeature));
      } else {
        Assertions.assertEquals(this.oldValue, this.referenceContent.get(0));
        Assertions.assertEquals(this.oldValue2, this.referenceContent.get(1));
        Assertions.assertEquals(this.oldValue3, this.referenceContent.get(2));
      }
    } else {
      boolean _isMany_1 = this.affectedFeature.isMany();
      boolean _not_2 = (!_isMany_1);
      if (_not_2) {
        Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
        MatcherAssert.<Identified>assertThat(this.oldValue, ModelMatchers.<Identified>equalsDeeply(((Identified) _eGet)));
      } else {
        MatcherAssert.<NonRoot>assertThat(this.oldValue, ModelMatchers.<NonRoot>equalsDeeply(this.referenceContent.get(0)));
        MatcherAssert.<NonRoot>assertThat(this.oldValue2, ModelMatchers.<NonRoot>equalsDeeply(this.referenceContent.get(1)));
        MatcherAssert.<NonRoot>assertThat(this.oldValue3, ModelMatchers.<NonRoot>equalsDeeply(this.referenceContent.get(2)));
      }
    }
  }

  /**
   * The resource is in state before the change.
   */
  private void assertResourceIsStateBefore() {
    boolean _isContainment = this.affectedFeature.isContainment();
    boolean _not = (!_isContainment);
    if (_not) {
      EObject _get = this.getResource().getContents().get(1);
      MatcherAssert.<Identified>assertThat(this.oldValue, ModelMatchers.<Identified>equalsDeeply(((Identified) _get)));
      EObject _get_1 = this.getResource().getContents().get(2);
      MatcherAssert.<Identified>assertThat(this.oldValue2, ModelMatchers.<Identified>equalsDeeply(((Identified) _get_1)));
      EObject _get_2 = this.getResource().getContents().get(3);
      MatcherAssert.<Identified>assertThat(this.oldValue3, ModelMatchers.<Identified>equalsDeeply(((Identified) _get_2)));
    } else {
      Assertions.assertEquals(this.getResource().getContents().size(), 1);
    }
  }

  /**
   * Model is in state after the change.
   */
  private void assertIsStateAfter() {
    Assertions.assertFalse(this.affectedEObject.eIsSet(this.affectedFeature));
    this.assertResourceIsStateAfter();
  }

  /**
   * Resource is in state after the change.
   */
  private void assertResourceIsStateAfter() {
    this.assertResourceIsStateBefore();
  }

  /**
   * Creates new unresolved change.
   */
  private List<EChange<Uuid>> createUnresolvedChange() {
    List<EChange<? extends EObject>> changes = new ArrayList<EChange<? extends EObject>>();
    boolean _isContainment = this.affectedFeature.isContainment();
    boolean _not = (!_isContainment);
    if (_not) {
      boolean _isMany = this.affectedFeature.isMany();
      boolean _not_1 = (!_isMany);
      if (_not_1) {
        final ReplaceSingleValuedEReference<Identified> change = this.getAtomicFactory().<Identified>createReplaceSingleReferenceChange(this.affectedEObject, this.affectedFeature, 
          this.oldValue, null);
        change.setIsUnset(true);
        changes.add(change);
      } else {
        changes.add(this.getAtomicFactory().<Identified>createRemoveReferenceChange(this.affectedEObject, this.affectedFeature, this.oldValue3, 2));
        changes.add(this.getAtomicFactory().<Identified>createRemoveReferenceChange(this.affectedEObject, this.affectedFeature, this.oldValue2, 1));
        changes.add(this.getAtomicFactory().<Identified>createRemoveReferenceChange(this.affectedEObject, this.affectedFeature, this.oldValue, 0));
        changes.add(this.getAtomicFactory().<Root, EReference>createUnsetFeatureChange(this.affectedEObject, this.affectedFeature));
      }
    } else {
      boolean _isMany_1 = this.affectedFeature.isMany();
      boolean _not_2 = (!_isMany_1);
      if (_not_2) {
        final List<EChange<EObject>> change_1 = this.getCompoundFactory().<Root, NonRoot>createReplaceAndDeleteNonRootChange(this.affectedEObject, this.affectedFeature, 
          this.oldValue);
        EChange<EObject> _get = change_1.get(0);
        ((ReplaceSingleValuedEReference<?>) _get).setIsUnset(true);
        changes.addAll(change_1);
      } else {
        changes.addAll(
          this.getCompoundFactory().<Root, NonRoot>createRemoveAndDeleteNonRootChange(this.affectedEObject, this.affectedFeature, this.oldValue3, 2));
        changes.addAll(
          this.getCompoundFactory().<Root, NonRoot>createRemoveAndDeleteNonRootChange(this.affectedEObject, this.affectedFeature, this.oldValue2, 1));
        changes.addAll(
          this.getCompoundFactory().<Root, NonRoot>createRemoveAndDeleteNonRootChange(this.affectedEObject, this.affectedFeature, this.oldValue, 0));
        changes.add(this.getAtomicFactory().<Root, EReference>createUnsetFeatureChange(this.affectedEObject, this.affectedFeature));
      }
    }
    return this.unresolve(changes);
  }

  /**
   * Starts a test with applying the change forward.
   */
  private void applyForwardTest() {
    this.assertIsStateBefore();
    final List<EChange<Uuid>> unresolvedChange = this.createUnresolvedChange();
    this.applyForwardAndResolve(unresolvedChange);
    this.assertIsStateAfter();
  }

  /**
   * Starts a test with applying the change backward.
   */
  private void applyBackwardTest() {
    this.assertIsStateBefore();
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(this.createUnresolvedChange());
    this.assertIsStateAfter();
    this.applyBackward(resolvedChange);
    this.assertIsStateBefore();
  }

  public static Stream<Arguments> testConfigurations() {
    return Stream.<Arguments>of(
      Arguments.of(Named.<Boolean>of("containment", Boolean.valueOf(true)), Named.<Boolean>of("single-valued", Boolean.valueOf(true))), 
      Arguments.of(Named.<Boolean>of("non-containment", Boolean.valueOf(false)), Named.<Boolean>of("single-valued", Boolean.valueOf(true))), 
      Arguments.of(Named.<Boolean>of("containment", Boolean.valueOf(true)), Named.<Boolean>of("multi-valued", Boolean.valueOf(false))), 
      Arguments.of(Named.<Boolean>of("non-containment", Boolean.valueOf(false)), Named.<Boolean>of("multi-valued", Boolean.valueOf(false))));
  }
}
