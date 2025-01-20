package tools.vitruv.change.atomic.compound;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.uuid.Uuid;

/**
 * Test class for the concrete {@link ExplicitUnsetEAttribute} EChange,
 * which unsets a single or multi valued attribute.
 */
@SuppressWarnings("all")
public class ExplicitUnsetEAttributeTest extends EChangeTest {
  private Root affectedEObject;

  private EAttribute affectedFeature;

  private EList<Integer> attributeContent;

  private static final Integer OLD_VALUE = Integer.valueOf(111);

  private static final Integer OLD_VALUE_2 = Integer.valueOf(222);

  private static final Integer OLD_VALUE_3 = Integer.valueOf(333);

  @BeforeEach
  public void beforeTest() {
    this.affectedEObject = this.getRootObject();
  }

  /**
   * Tests whether resolving the {@link ExplicitUnsetEAttribute} EChange
   * returns the same class.
   */
  @Test
  public void resolveToCorrectType() {
    this.isSingleValuedAttributeTest();
    final List<? extends EChange<Uuid>> unresolvedChange = this.createUnresolvedChange();
    final List<EChange<EObject>> resolvedChange = this.applyForwardAndResolve(unresolvedChange);
    this.helper.assertDifferentChangeSameClass(unresolvedChange, resolvedChange);
  }

  /**
   * Tests a {@link ExplicitUnsetEAttribute} EChange by applying it forward.
   * Unsets a single valued unsettable attribute.
   */
  @Test
  public void applyForwardUnsetSingleValuedAttributeTest() {
    this.isSingleValuedAttributeTest();
    this.applyForwardTest();
  }

  /**
   * Tests a {@link ExplicitUnsetEAttribute} EChange by applying it forward.
   * Unsets a multi valued unsettable attribute.
   */
  @Test
  public void applyForwardUnsetMulitValuedAttributeTest() {
    this.isMultiValuedAttributeTest();
    this.applyForwardTest();
  }

  /**
   * Tests a {@link ExplicitUnsetEAttribute} EChange by applying it backward.
   * Unsets a single valued unsettable attribute.
   */
  @Test
  public void applyBackwardUnsetSingleValuedAttributeTest() {
    this.isSingleValuedAttributeTest();
    this.applyBackwardTest();
  }

  /**
   * Tests a {@link ExplicitUnsetEAttribute} EChange by applying it backward.
   * Unsets a multi valued unsettable attribute.
   */
  @Test
  public void applyBackwardUnsetMulitValuedAttributeTest() {
    this.isMultiValuedAttributeTest();
    this.applyBackwardTest();
  }

  /**
   * Starts a test with a single valued attribute and sets the state before.
   */
  private void isSingleValuedAttributeTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_UNSETTABLE_EATTRIBUTE;
    this.prepareStateBefore();
  }

  /**
   * Starts a test with a multi valued attribute and sets the state before.
   */
  private void isMultiValuedAttributeTest() {
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_UNSETTABLE_EATTRIBUTE;
    Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
    this.attributeContent = ((EList<Integer>) _eGet);
    this.prepareStateBefore();
  }

  /**
   * Sets the state before, depending on single or multi valued attribute test.
   */
  private void prepareStateBefore() {
    boolean _isMany = this.affectedFeature.isMany();
    boolean _not = (!_isMany);
    if (_not) {
      this.affectedEObject.eSet(this.affectedFeature, ExplicitUnsetEAttributeTest.OLD_VALUE);
    } else {
      this.attributeContent.add(ExplicitUnsetEAttributeTest.OLD_VALUE);
      this.attributeContent.add(ExplicitUnsetEAttributeTest.OLD_VALUE_2);
      this.attributeContent.add(ExplicitUnsetEAttributeTest.OLD_VALUE_3);
    }
  }

  /**
   * Model is in state before the single or multi valued unset change.
   */
  private void assertIsStateBefore() {
    Assertions.assertTrue(this.affectedEObject.eIsSet(this.affectedFeature));
    boolean _isMany = this.affectedFeature.isMany();
    boolean _not = (!_isMany);
    if (_not) {
      Assertions.assertEquals(this.affectedEObject.eGet(this.affectedFeature), ExplicitUnsetEAttributeTest.OLD_VALUE);
    } else {
      Assertions.assertEquals(this.attributeContent.get(0), ExplicitUnsetEAttributeTest.OLD_VALUE);
      Assertions.assertEquals(this.attributeContent.get(1), ExplicitUnsetEAttributeTest.OLD_VALUE_2);
      Assertions.assertEquals(this.attributeContent.get(2), ExplicitUnsetEAttributeTest.OLD_VALUE_3);
    }
  }

  /**
   * Model is in state after the single or multi valued unset change.
   */
  private void assertIsStateAfter() {
    Assertions.assertFalse(this.affectedEObject.eIsSet(this.affectedFeature));
  }

  /**
   * Creates new unresolved change.
   */
  private List<? extends EChange<Uuid>> createUnresolvedChange() {
    List<EChange<EObject>> subtractiveChanges = CollectionLiterals.<EChange<EObject>>newArrayList();
    boolean _isMany = this.affectedFeature.isMany();
    boolean _not = (!_isMany);
    if (_not) {
      Object _defaultValue = this.affectedFeature.getDefaultValue();
      subtractiveChanges.add(
        this.getAtomicFactory().<EObject, Integer>createReplaceSingleAttributeChange(this.affectedEObject, this.affectedFeature, ExplicitUnsetEAttributeTest.OLD_VALUE, 
          ((Integer) _defaultValue)));
    } else {
      subtractiveChanges.add(
        this.getAtomicFactory().<EObject, Integer>createRemoveAttributeChange(this.affectedEObject, this.affectedFeature, 2, ExplicitUnsetEAttributeTest.OLD_VALUE_3));
      subtractiveChanges.add(
        this.getAtomicFactory().<EObject, Integer>createRemoveAttributeChange(this.affectedEObject, this.affectedFeature, 1, ExplicitUnsetEAttributeTest.OLD_VALUE_2));
      subtractiveChanges.add(
        this.getAtomicFactory().<EObject, Integer>createRemoveAttributeChange(this.affectedEObject, this.affectedFeature, 0, ExplicitUnsetEAttributeTest.OLD_VALUE));
    }
    UnsetFeature<Root, EAttribute> _createUnsetFeatureChange = this.getAtomicFactory().<Root, EAttribute>createUnsetFeatureChange(this.affectedEObject, this.affectedFeature);
    return this.unresolve(IterableExtensions.<EChange<? extends EObject>>toList(Iterables.<EChange<? extends EObject>>concat(subtractiveChanges, Collections.<UnsetFeature<Root, EAttribute>>unmodifiableList(CollectionLiterals.<UnsetFeature<Root, EAttribute>>newArrayList(_createUnsetFeatureChange)))));
  }

  /**
   * Starts a test with applying the change forward.
   */
  private void applyForwardTest() {
    this.assertIsStateBefore();
    final List<? extends EChange<Uuid>> unresolvedChange = this.createUnresolvedChange();
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
}
