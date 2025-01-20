package tools.vitruv.change.atomic.command.internal;

import allElementTypes.AllElementTypesPackage;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.command.RemoveAtCommand;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for the {@link RemoveAtCommand} which removes
 * elements at a specific index in a EList.
 */
@SuppressWarnings("all")
public class RemoveAtCommandTest extends CommandTest {
  private EditingDomain editingDomain;

  private EObject owner;

  private EStructuralFeature feature;

  private EList<Integer> list;

  @BeforeEach
  public final void beforeTest() {
    this.owner = AllElementTypesCreators.aet.Root();
    this.feature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE;
    this.editingDomain = ChangeCommandUtil.getEditingDomain(this.owner);
    Object _eGet = this.owner.eGet(this.feature);
    this.list = ((EList<Integer>) _eGet);
    for (int i = 0; (i < 10); i++) {
      this.list.add(Integer.valueOf(i));
    }
  }

  /**
   * Tests both creation methods and constructors of the class.
   */
  @Test
  public void createCommandTest() {
    RemoveAtCommand command = new RemoveAtCommand(this.editingDomain, this.list, Integer.valueOf(2), 3);
    RemoveAtCommandTest.assertIsRemoveAtCommand(command, this.list, Integer.valueOf(2), 3);
    RemoveAtCommand command2 = new RemoveAtCommand(this.editingDomain, this.owner, this.feature, Integer.valueOf(4), 8);
    RemoveAtCommandTest.assertIsRemoveAtCommand(command2, this.list, Integer.valueOf(4), 8);
  }

  /**
   * Tests the preparation of the command, which decides
   * whether the command can be executed or not.
   */
  @Test
  public void prepareTest() {
    CommandTest.assertIsPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(0), 0));
    CommandTest.assertIsPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(4), 4));
    CommandTest.assertIsPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(9), 9));
    CommandTest.assertIsNotPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(0), 5));
    CommandTest.assertIsNotPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(3), 6));
    CommandTest.assertIsNotPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(0), (-1)));
    CommandTest.assertIsNotPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(0), 15));
    CommandTest.assertIsNotPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf((-1)), 0));
    CommandTest.assertIsNotPreparable(this.createRemoveAtCommand(this.list, Integer.valueOf(44), 0));
    CommandTest.assertIsNotPreparable(this.createRemoveAtCommand(null, Integer.valueOf(0), 0));
  }

  /**
   * Tests the execution of the command.
   */
  @Test
  public void executeTest() {
    RemoveAtCommandTest.assertRemovedCorrectValueFrom(this.createRemoveAtCommand(this.list, Integer.valueOf(8), 8), this.list);
    RemoveAtCommandTest.assertRemovedCorrectValueFrom(this.createRemoveAtCommand(this.list, Integer.valueOf(0), 0), this.list);
    RemoveAtCommandTest.assertRemovedCorrectValueFrom(this.createRemoveAtCommand(this.list, Integer.valueOf(1), 0), this.list);
    RemoveAtCommandTest.assertRemovedCorrectValueFrom(this.createRemoveAtCommand(this.list, Integer.valueOf(5), 3), this.list);
  }

  /**
   * Creates a new remove at command.
   */
  private RemoveAtCommand createRemoveAtCommand(final EList<?> ownerList, final Object value, final int index) {
    return new RemoveAtCommand(this.editingDomain, ownerList, value, index);
  }

  /**
   * Command is instance of RemoveAtCommand and contains the correct values.
   */
  private static RemoveAtCommand assertIsRemoveAtCommand(final Command command, final EList<?> list, final Object value, final int index) {
    RemoveAtCommand removeAtCommand = CommandTest.<RemoveAtCommand>assertIsInstanceOf(command, RemoveAtCommand.class);
    Assertions.assertSame(removeAtCommand.getOwnerList(), list);
    Assertions.assertTrue(removeAtCommand.getCollection().contains(value));
    Assertions.assertEquals(removeAtCommand.getCollection().size(), 1);
    Assertions.assertEquals(removeAtCommand.getIndex(), index);
    return removeAtCommand;
  }

  /**
   * Executes command and checks if it removed the right value from the list.
   */
  private static void assertRemovedCorrectValueFrom(final RemoveAtCommand command, final EList<?> list) {
    Object value = ((Object[])Conversions.unwrapArray(command.getCollection(), Object.class))[0];
    int size = list.size();
    Assertions.assertEquals(list.get(command.getIndex()), value);
    CommandTest.assertExecuteCommand(command);
    Assertions.assertFalse(list.contains(value));
    Assertions.assertEquals(list.size(), (size - 1));
  }
}
