package tools.vitruv.change.atomic.root;

import allElementTypes.Root;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.BeforeEach;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Abstract class which is extended by the Root EChange test classes.
 */
@SuppressWarnings("all")
public abstract class RootEChangeTest extends EChangeTest {
  @Accessors(AccessorType.PROTECTED_GETTER)
  private Root newRootObject = null;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private Root newRootObject2 = null;

  /**
   * Creates two new root elements which can be used in the tests.
   */
  @BeforeEach
  public void beforeTest() {
    this.newRootObject = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
    this.newRootObject2 = this.<Root>withUuid(AllElementTypesCreators.aet.Root());
  }

  @Pure
  protected Root getNewRootObject() {
    return this.newRootObject;
  }

  @Pure
  protected Root getNewRootObject2() {
    return this.newRootObject2;
  }
}
