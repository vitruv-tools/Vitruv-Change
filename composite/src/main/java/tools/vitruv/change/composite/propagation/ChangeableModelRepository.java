package tools.vitruv.change.composite.propagation;

import java.util.List;

import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.VitruviusChange;

/**
 * A model repository that can be changed by applying @{link VitruviusChange}s.
 * {@link ChangePropagationListener}s can be registered to 
 * receive notifications about changes to the model repository.
 */
public interface ChangeableModelRepository {
	/**
	 * Propagates the given change to the model repository. This will apply the
	 * given {@link VitruviusChange} to the underlying resources and may execute
	 * further mechanisms, such as preserving the consistency of the underlying
	 * models.
	 * 
	 * @param change the {@link VitruviusChange} to be applied, not
	 *               <code>null</code>
	 */
	List<PropagatedChange> propagateChange(VitruviusChange<Uuid> change);

	void addChangePropagationListener(ChangePropagationListener propagationListener);

	void removeChangePropagationListener(ChangePropagationListener propagationListener);
}
