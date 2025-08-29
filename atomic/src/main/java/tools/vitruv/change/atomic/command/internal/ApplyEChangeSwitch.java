package tools.vitruv.change.atomic.command.internal;

import static com.google.common.base.Preconditions.checkState;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;

/**
 * Utility class for applying an EChange.
 */
@Utility
public class ApplyEChangeSwitch {
  /**
   * Forward switch to apply {@link EChange}s.
   */
  private static final ApplyForwardCommandSwitch forwardSwitch = new ApplyForwardCommandSwitch();
  /**
   * Backward switch to apply {@link EChange}s.
   */
  private static final ApplyBackwardCommandSwitch backwardSwitch = new ApplyBackwardCommandSwitch();

  /**
   * Private constructor for utility class.
   */
  private ApplyEChangeSwitch() {}

  /**
   * Applies a given {@link EChange}.
   * Returns if the change was successfully applied.
   *
   * @param change - The {@link EChange} which will be applied.
   * @param applyForward - If {@code true} the change will be applied forward, otherwise backward.
   * @throws IllegalStateException - No commands can be generated for the change, 
   *      or they cannot be executed.
   */
  public static void applyEChange(EChange<EObject> change, boolean applyForward) {
    final var commandSwitch = applyForward ? forwardSwitch : backwardSwitch;
    var commands = commandSwitch.getCommands(change);
    checkState(commands != null, "no commands could be generated for EChange: %s", change);

    for (Command c : commands) {
      checkState(c.canExecute(), "cannot execute command generated for EChange: %s", change);
      c.execute();
    }
  }
}
