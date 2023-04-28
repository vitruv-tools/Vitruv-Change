package tools.vitruv.change.propagation.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.propagation.ChangePropagationListener;
import tools.vitruv.change.composite.propagation.ChangeableModelRepository;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.propagation.ChangePropagationSpecificationProvider;
import tools.vitruv.change.propagation.PersistableChangeRecordingModelRepository;

/**
 * A default implementation of a {@link ChangeableModelRepository} that requires a
 * {@link PersistableChangeRecordingModelRepository} and a {@link ChangePropagationSpecificationProvider} to propagate
 * changes. It uses the default {@link ChangePropagator} to propagate changes passed to the
 * {@link #propagateChange(VitruviusChange)} method.
 */
public class DefaultChangeableModelRepository implements ChangeableModelRepository {
	private static final Logger LOGGER = Logger.getLogger(DefaultChangeableModelRepository.class);

	private final Set<ChangePropagationListener> changePropagationListeners = new HashSet<>();
	private final PersistableChangeRecordingModelRepository modelRepository;
	private final ChangePropagator changePropagator;

	public DefaultChangeableModelRepository(PersistableChangeRecordingModelRepository modelRepository,
			ChangePropagationSpecificationProvider changePropagationSpecificationProvider, InternalUserInteractor userInteractor) {
		this.modelRepository = modelRepository;
		this.changePropagator = new ChangePropagator(modelRepository, changePropagationSpecificationProvider, userInteractor);
	}

	@Override
	public List<PropagatedChange> propagateChange(VitruviusChange change) {
		checkArgument(change != null, "change to propagate must not be null");
		checkArgument(change.containsConcreteChange(), "change to propagate must contain concrete changes:%s%s", System.lineSeparator(), change);
		VitruviusChange unresolvedChange = change.unresolve();

		LOGGER.info("Start change propagation");
		notifyChangePropagationStarted(unresolvedChange);
		List<PropagatedChange> resultChanges = changePropagator.propagateChange(unresolvedChange);
		modelRepository.saveOrDeleteModels();
		notifyChangePropagationFinished(unresolvedChange, resultChanges);
		LOGGER.info("Finished change propagation");
		return resultChanges;
	}

	private void notifyChangePropagationStarted(VitruviusChange change) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Started synchronizing change: " + change);
		}
		changePropagationListeners.stream().forEach((listener) -> listener.startedChangePropagation(change));
	}

	private void notifyChangePropagationFinished(VitruviusChange inputChange, Iterable<PropagatedChange> generatedChanges) {
		changePropagationListeners.stream().forEach((listener) -> listener.finishedChangePropagation(generatedChanges));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Finished synchronizing change: " + inputChange);
		}
	}

	@Override
	public void addChangePropagationListener(ChangePropagationListener propagationListener) {
		checkArgument(propagationListener != null, "propagation listener must not be null");
		changePropagationListeners.add(propagationListener);
	}

	@Override
	public void removeChangePropagationListener(ChangePropagationListener propagationListener) {
		checkArgument(propagationListener != null, "propagation listener must not be null");
		changePropagationListeners.remove(propagationListener);
	}

}
