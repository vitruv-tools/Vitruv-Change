package tools.vitruv.change.atomic.eobject;

import allElementTypes.Root;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.BeforeEach;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Abstract class which is used by the EObject EChange test classes.
 */
@SuppressWarnings("all")
public abstract class EObjectTest extends EChangeTest {
  @Accessors({ AccessorType.PROTECTED_GETTER, AccessorType.PROTECTED_SETTER })
  private Root createdObject = null;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private Root createdObject2 = null;

  /**
   * Calls setup of the superclass and creates two new root elements
   * which can be created or deleted in the tests.
   */
  @BeforeEach
  public final void initializeRoots() {
    this.createdObject = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
    this.createdObject2 = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
  }

  @Pure
  protected Root getCreatedObject() {
    return this.createdObject;
  }

  protected void setCreatedObject(final Root createdObject) {
    this.createdObject = createdObject;
  }

  @Pure
  protected Root getCreatedObject2() {
    return this.createdObject2;
  }
}
