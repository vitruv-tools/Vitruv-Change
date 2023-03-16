package tools.vitruv.change.composite.description;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.interaction.UserInteractionBase;

/**
 * Base interface for all kinds of changes in Vitruvius.
 */
public interface VitruviusChange {
	/**
	 * Returns whether the change contains any concrete change or consists only of
	 * composite ones.
	 */
	public boolean containsConcreteChange();

	/**
	 * Returns the {@link EChange}s that describe this change. Requires the change
	 * to be prepared so that the original change information is transformed into
	 * {@link EChange}s.
	 */
	public List<EChange> getEChanges();

	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method and that the
	 * changes use UUIDs. Returns the resolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or is already
	 *                               resolved.
	 */
	public VitruviusChange resolveAndApply(UuidResolver uuidResolver);

	/**
	 * Resolves the change and applies it forward so that the model is in the state
	 * after the change afterwards. It has to be ensured that the model is in a
	 * state the change can be applied to before calling this method and that the
	 * changes use hierarchical IDs. Returns the resolved change.
	 * 
	 * @throws IllegalStateException if the change cannot be resolved or is already
	 *                               resolved.
	 */
	public VitruviusChange resolveAndApply(IdResolver idResolver);

	/**
	 * Returns an unresolved change, such that all its affected and referenced
	 * {@link EObjects} are removed.
	 */
	public VitruviusChange unresolve();

	/**
	 * Returns all {@link EObject}s directly affected by this change. This does not
	 * include referenced elements.
	 */
	public Set<EObject> getAffectedEObjects();

	/**
	 * Returns all {@link EObject}s affected by this change, including both the
	 * elements of which an attribute or reference was changes, as well as the
	 * referenced elements.
	 */
	public Set<EObject> getAffectedAndReferencedEObjects();

	/**
	 * Returns the {@link URI}s of all {@link Resource}s changed by this change,
	 * i.e. the resources containing the changed {@link EObject}s. The returned
	 * {@link Iterable} may be empty if no {@link EObject}s are affected by this
	 * change or if this change was not resolved yet.
	 */
	public Set<URI> getChangedURIs();

	/**
	 * Returns the descriptors for the metamodels of the elements whose instances
	 * have been modified in this change. These elements are the {@link EObject}s
	 * returned by {@link getAffectedEObjects}.
	 */
	public Set<MetamodelDescriptor> getAffectedEObjectsMetamodelDescriptors();

	/**
	 * Returns all user interactions performed during application of this change and
	 * performing consistency preservation.
	 */
	public Iterable<UserInteractionBase> getUserInteractions();

	public VitruviusChange copy();
}