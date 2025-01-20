package tools.vitruv.change.atomic.command.internal;

import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.activextendannotations.Utility;
import java.util.List;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;

/**
 * Utility class for applying an EChange.
 */
@Utility
@SuppressWarnings("all")
public final class ApplyEChangeSwitch {
  /**
   * Applies a given {@link EChange}.
   * @param change					The {@link EChange} which will be applied.
   * @param applyForward				If {@code true} the change will be applied forward,
   * 									otherwise backward.
   * @returns							The change was successfully applied.
   * @throws IllegalStateException	No commands can be generated for the change, or they cannot be executed.
   */
  public static void applyEChange(final EChange<EObject> change, final boolean applyForward) {
    List<Command> _xifexpression = null;
    if (applyForward) {
      _xifexpression = ApplyForwardCommandSwitch.getCommands(change);
    } else {
      _xifexpression = ApplyBackwardCommandSwitch.getCommands(change);
    }
    final List<Command> commands = _xifexpression;
    Preconditions.checkState((commands != null), "no commands could be generated for EChange: %s", change);
    for (final Command c : commands) {
      {
        Preconditions.checkState(c.canExecute(), "cannot execute command generated for EChange: %s", change);
        c.execute();
      }
    }
  }

  private ApplyEChangeSwitch() {
    
  }
}
