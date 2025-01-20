package tools.vitruv.change.atomic.feature.reference;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.BeforeEach;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Abstract class which is used by all test classes for references.
 */
@SuppressWarnings("all")
public abstract class ReferenceEChangeTest extends EChangeTest {
  @Accessors(AccessorType.PROTECTED_GETTER)
  private Root affectedEObject;

  @Accessors({ AccessorType.PROTECTED_GETTER, AccessorType.PROTECTED_SETTER })
  private NonRoot newValue;

  @Accessors({ AccessorType.PROTECTED_GETTER, AccessorType.PROTECTED_SETTER })
  private NonRoot newValue2;

  /**
   * Sets the default object and new value for tests.
   */
  @BeforeEach
  public final void beforeTest() {
    this.affectedEObject = this.getRootObject();
    this.newValue = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
    this.newValue2 = this.<NonRoot>withUuid(AllElementTypesCreators.aet.NonRoot());
  }

  /**
   * Prepares the resource and adds the new values.
   */
  protected void prepareResource() {
    this.getResource().getContents().add(this.newValue);
    this.getResource().getContents().add(this.newValue2);
  }

  @Pure
  protected Root getAffectedEObject() {
    return this.affectedEObject;
  }

  @Pure
  protected NonRoot getNewValue() {
    return this.newValue;
  }

  protected void setNewValue(final NonRoot newValue) {
    this.newValue = newValue;
  }

  @Pure
  protected NonRoot getNewValue2() {
    return this.newValue2;
  }

  protected void setNewValue2(final NonRoot newValue2) {
    this.newValue2 = newValue2;
  }
}
