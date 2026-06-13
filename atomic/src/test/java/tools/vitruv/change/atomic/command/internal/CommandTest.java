package tools.vitruv.change.atomic.command.internal;

import org.eclipse.emf.common.command.Command;
import org.junit.jupiter.api.Assertions;

/**
 * Abstract base class of the command tests.
 */
@SuppressWarnings("all")
public abstract class CommandTest {
  /**
   * Command is prepareable (can be executed)
   */
  protected static void assertIsPreparable(final Command command) {
    Assertions.assertTrue(command.canExecute());
  }

  /**
   * Command is not preparable (cannot be executed)
   */
  protected static void assertIsNotPreparable(final Command command) {
    Assertions.assertFalse(command.canExecute());
  }

  /**
   * Checks if a command can be executed and executes it.
   */
  protected static void assertExecuteCommand(final Command command) {
    Assertions.assertTrue(command.canExecute());
    command.execute();
  }

  /**
   * Command is instance of a specific class.
   */
  protected static <T extends Object> T assertIsInstanceOf(final Command command, final Class<T> type) {
    Assertions.assertTrue(type.isInstance(command));
    return type.cast(command);
  }
}
