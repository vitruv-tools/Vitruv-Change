package tools.vitruv.change.atomic.command.internal

import edu.kit.ipd.sdq.activextendannotations.Utility
import org.eclipse.emf.common.command.Command
import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.atomic.EChange

import static com.google.common.base.Preconditions.checkState

/**
 * Utility class for applying an EChange.
 */
@Utility
class ApplyEChangeSwitch {
	/**
	 * Applies a given {@link EChange}.
	 * Returns if the change was successfully applied.
	 * @param change					The {@link EChange} which will be applied.
	 * @param applyForward				If {@code true} the change will be applied forward,
	 * 									otherwise backward.
	 * @throws IllegalStateException	No commands can be generated for the change, or they cannot be executed.
	 */
	def static void applyEChange(EChange<EObject> change, boolean applyForward) {
		val commands = if (applyForward) {
				ApplyForwardCommandSwitch.getCommands(change)
			} else {
				ApplyBackwardCommandSwitch.getCommands(change)
			}

		checkState(commands !== null, "no commands could be generated for EChange: %s", change)

		for (Command c : commands) {
			checkState(c.canExecute, "cannot execute command generated for EChange: %s", change)
			c.execute()
		}
	}

}
