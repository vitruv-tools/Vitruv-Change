package tools.vitruv.change.correspondence;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

/**
 * A view on a {@link GenericCorrespondenceModel} that is aware of the actual correspondence type to be handled.
 * @author Heiko Klare
 * @param <T> - the type of correspondences to be handled, i.e., to be retrieved, added or removed
 */
public interface CorrespondenceModelView<T extends Correspondence> extends GenericCorrespondenceModel<T> {
	/**
	 * Creates a correspondence of type <T> with the given tag between the given lists of {@link EObject}s.
	 * @param firstEObjects the first list of {@link EObject}s, must not be {@code null} or empty
	 * @param secondEObjects the second list of {@link EObject}s, must not be {@code null} or empty
	 * @param tag the tag to be added to the correspondence, must not be {@code null}
	 * @return the created correspondence
	 */
	public T addCorrespondenceBetween(List<EObject> firstEObjects, List<EObject> secondEObjects, String tag);

	/**
	 * Creates a correspondence of type <T> with the given tag between the given {@link EObject}s.
	 * @param firstEObject the first {@link EObject}, must not be {@code null}
	 * @param secondEObject the second {@link EObject}, must not be {@code null}
	 * @param tag the tag to be added to the correspondence, must not be {@code null}
	 * @return the created correspondence
	 */
	public default T addCorrespondenceBetween(EObject firstEObject, EObject secondEObject, String tag) {
		return addCorrespondenceBetween(List.of(firstEObject), List.of(secondEObject), tag);
	}

	/**
	 * Returns all correspondences for the specified object and an empty set if the object has no correspondences. Should
	 * never return {@link null}.
	 * @param sourceObjects the {@link EObject}s to get the correspondences for, must not be {@code null} or empty
	 * @return all correspondences for the specified object and an empty set if the object has no correspondences.
	 */
	public Set<T> getCorrespondences(List<EObject> sourceObjects);

	/**
	 * Returns all elements corresponding to the given ones.
	 * @param sourceObjects the objects to get the corresponding ones for, must not be {@code null} or empty
	 * @return the elements corresponding to the given ones
	 */
	public Set<List<EObject>> getCorrespondingEObjects(List<EObject> sourceObjects);

	/**
	 * Returns the elements corresponding to the given one.
	 * @param sourceObject the object to get the corresponding ones for, must not be {@code null}
	 * @return the elements corresponding to the given one
	 */
	public default Set<EObject> getCorrespondingEObjects(EObject sourceObject) {
		if (sourceObject == null) {
			return Collections.emptySet();
		}
		return flatten(getCorrespondingEObjects(List.of(sourceObject)));
	}

	/**
	 * Returns the elements of the given type corresponding to the given ones for all correspondences between these elements
	 * containing the given tag.
	 * @param sourceObjects the objects to get the corresponding ones for, must not be {@code null} or empty
	 * @param type the type of the corresponding elements to retrieve, must not be {@code null}
	 * @param tag the tag to filter correspondences for. If the tag is {@code null}, all correspondences will be checked
	 * @return the elements corresponding to the given ones
	 */
	public <C extends EObject> Set<List<C>> getCorrespondingEObjects(List<EObject> sourceObjects, Class<C> type, String tag);

	/**
	 * Returns the elements of the given type corresponding to the given ones for all correspondences between these elements
	 * containing the given tag.
	 * @param sourceObject the object to get the corresponding ones for, must not be {@code null}
	 * @param type the type of the corresponding elements to retrieve, must not be {@code null}
	 * @param tag the tag to filter correspondences for. If the tag is {@code null}, all correspondences will be checked
	 * @return the elements corresponding to the given one
	 */
	public default <C extends EObject> Set<C> getCorrespondingEObjects(EObject sourceObject, Class<C> type, String tag) {
		if (sourceObject == null) {
			return Collections.emptySet();
		}
		return flatten(getCorrespondingEObjects(List.of(sourceObject), type, tag));
	}

	/**
	 * Removes the correspondences between the given lists of {@link EObject}s with the given tag.
	 * @param firstEObjects the first list of {@link EObject}s, must not be {@code null} or empty
	 * @param secondEObjects the second list of {@link EObject}s, must not be {@code null} or empty
	 * @param tag the tag to filter correspondences for or {@code null} if all correspondences shall be removed
	 * @return the removed correspondences
	 */
	public Set<T> removeCorrespondencesBetween(List<EObject> firstEObjects, List<EObject> secondEObjects, String tag);

	/**
	 * Removes the correspondences between the given {@link EObject}s with the given tag.
	 * @param firstEObjects the first {@link EObject}, must not be {@code null}
	 * @param secondEObjects the second {@link EObject}, must not be {@code null}
	 * @param tag the tag to filter correspondences for or {@code null} if all correspondences shall be removed
	 * @return the removed correspondences
	 */
	public default Set<T> removeCorrespondencesBetween(EObject firstEObject, EObject secondEObject, String tag) {
		return removeCorrespondencesBetween(List.of(firstEObject), List.of(secondEObject), tag);
	}
	
	private static <C extends EObject> Set<C> flatten(Set<List<C>> toFlatten) {
		Set<C> correspondingElements = new HashSet<>();
		toFlatten.forEach(correspondingElements::addAll);
		return correspondingElements;
	}

}
