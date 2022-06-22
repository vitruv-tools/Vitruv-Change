package tools.vitruv.change.composite.propagation;

import tools.vitruv.change.composite.description.PropagatedChange;

/**
 * Listener for the change propagation status.
 */
public interface ChangePropagationListener {

	/**
	 * Called before the change propagation is started.
	 */
	void startedChangePropagation();

	/**
	 * Called after the change propagation is finished.
	 * 
	 * @param propagatedChanges the changes that have generated during propagation
	 */
	void finishedChangePropagation(Iterable<PropagatedChange> propagatedChanges);

	/**
	 * Called if the change propagation has been aborted.
	 *
	 * @param cause The cause for the abortion.
	 */
	void abortedChangePropagation(ChangePropagationAbortCause cause);

}
