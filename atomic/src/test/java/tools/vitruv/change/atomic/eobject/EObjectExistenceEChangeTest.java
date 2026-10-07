package tools.vitruv.change.atomic.eobject;

import allElementTypes.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;

/**
 * Test class for the abstract class {@link EObjectExistenceEChange} EChange,
 * which creates or deletes a new EObject.
 */
@SuppressWarnings("all")
public class EObjectExistenceEChangeTest extends EObjectTest {
  /**
   * Test resolves a {@link EObjectExistenceEChangeTest} EChange with a null
   * affected EObject.
   */
  @Test
  public void resolveInvalidAffectedEObjectTest() {
    this.setCreatedObject(null);
    final Executable _function = () -> {
      this.createUnresolvedChange(this.getCreatedObject());
    };
    Assertions.<IllegalArgumentException>assertThrows(IllegalArgumentException.class, _function);
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange(final Root createdObject) {
    return this.unresolve(this.getAtomicFactory().<Root>createCreateEObjectChange(createdObject));
  }
}
