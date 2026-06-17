package tools.vitruv.change.atomic.root;

import allElementTypes.Root;
import org.junit.jupiter.api.BeforeEach;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Abstract class which is extended by the Root EChange test classes.
 */
public abstract class RootEChangeTest extends EChangeTest {
  private Root newRootObject = null;
  private Root newRootObject2 = null;

  /**
   * Creates two new root elements which can be used in the tests.
   */
  @BeforeEach
  public void beforeTest() {
    this.newRootObject = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
    this.newRootObject2 = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
  }
  protected Root getNewRootObject() {
    return this.newRootObject;
  }
  protected Root getNewRootObject2() {
    return this.newRootObject2;
  }
}
