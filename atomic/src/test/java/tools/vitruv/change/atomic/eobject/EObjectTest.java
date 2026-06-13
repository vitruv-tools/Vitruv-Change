package tools.vitruv.change.atomic.eobject;

import allElementTypes.Root;
import org.junit.jupiter.api.BeforeEach;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Abstract class which is used by the EObject EChange test classes.
 */
public abstract class EObjectTest extends EChangeTest {
  private Root createdObject = null;
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
  protected Root getCreatedObject() {
    return this.createdObject;
  }

  protected void setCreatedObject(final Root createdObject) {
    this.createdObject = createdObject;
  }
  protected Root getCreatedObject2() {
    return this.createdObject2;
  }
}
