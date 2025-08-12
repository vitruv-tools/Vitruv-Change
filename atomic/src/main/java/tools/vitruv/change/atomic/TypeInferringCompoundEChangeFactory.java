package tools.vitruv.change.atomic;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;

public class TypeInferringCompoundEChangeFactory {
	protected TypeInferringAtomicEChangeFactory atomicFactory;
	private static TypeInferringCompoundEChangeFactory instance;

	protected TypeInferringCompoundEChangeFactory(TypeInferringAtomicEChangeFactory atomicFactory) {
		this.atomicFactory = atomicFactory;
	}

	/**
	 * Get the singleton instance of the factory.
	 * @return The singleton instance.
	 */
	public static TypeInferringCompoundEChangeFactory getInstance() {
		if (instance == null) {
			instance = new TypeInferringCompoundEChangeFactory(TypeInferringAtomicEChangeFactory.getInstance());
		}
		return instance;
	}

	/**
	 * Creates a new EChange list that creates and inserts a root object.
	 * @param affectedEObject The created and inserted root object by the change.
	 * @param resource The resource where the root object will be inserted.
	 * @param index The index at which the root object will be inserted into the resource.
	 * @return The created change.
	 */
	public <T extends EObject> List<EChange<T>> createCreateAndInsertRootChange(T affectedEObject, Resource resource,
		int index) {
		final var createChange = atomicFactory.createCreateEObjectChange(affectedEObject);
		final var insertChange = atomicFactory.createInsertRootChange(affectedEObject, resource, index);
		return List.of(createChange, insertChange);
	}

	/**
	 * Creates a new EChange list that removes and delete a root object.
	 * @param affectedEObject The removed and deleted root object by the change.
	 * @param resource The resource where the root object will be removed from.
	 * @param index The index at which the root object will be removed from the resource.
	 * @return The created change.
	 */
	public <T extends EObject> List<EChange<T>> createRemoveAndDeleteRootChange(T affectedEObject, Resource resource,
		int index) {
		final var deleteChange = atomicFactory.createDeleteEObjectChange(affectedEObject);
		final var removeChange = atomicFactory.createRemoveRootChange(affectedEObject, resource, index);
		return List.of(removeChange, deleteChange);
	}

	/**
	 * Creates a new EChange list that creates and inserts a non root element.
	 * @param affectedEObject The affected object, in which feature the created non root element will be inserted.
	 * @param reference The reference of the affected object, in which the created non root element will be inserted.
	 * @param newValue The created and inserted non root element.
	 * @param index The index at which the non root element will be inserted into the reference.
	 * @return The created change.
	 */
	public <A extends EObject, T extends EObject> List<EChange<EObject>> createCreateAndInsertNonRootChange(
		A affectedEObject, EReference reference, T newValue, int index) {
		final CreateEObject<EObject> createChange = atomicFactory.createCreateEObjectChange(newValue);
		final var insertChange = atomicFactory.createInsertReferenceChange(affectedEObject, reference, newValue, index);
		return List.of(createChange, insertChange);
	}

	/**
	 * Creates a new EChange list that removes and deletes a non root element.
	 * @param affectedEObject The affected object, from which feature the non root element will be removed.
	 * @param reference The reference of the affected object, from which the non root element will be removed.
	 * @param oldValue The removed and deleted non root element.
	 * @param index The index at which the non root element will be removed from the reference.
	 * @return The created change.
	 */
	public <A extends EObject, T extends EObject> List<EChange<EObject>> createRemoveAndDeleteNonRootChange(
		A affectedEObject, EReference reference, T oldValue, int index) {
		final DeleteEObject<EObject> deleteChange = atomicFactory.createDeleteEObjectChange(oldValue);
		final var removeChange = atomicFactory.createRemoveReferenceChange(affectedEObject, reference, oldValue, index);
		return List.of(removeChange, deleteChange);
	}

	/**
	 * Creates a new EChange list that creates an EObject and replaces a value with it.
	 * @param affectedEObject The affected object, in which feature null will be replaced by the new value.
	 * @param reference The reference of the affected object, in which null will be replaced by the new value.
	 * @param newValue The new value which replaces null.
	 * @return The created change.
	 */
	public <A extends EObject, T extends EObject> List<EChange<EObject>> createCreateAndReplaceNonRootChange(
		A affectedEObject, EReference reference, T newValue) {
		final CreateEObject<EObject> createChange = atomicFactory.createCreateEObjectChange(newValue);
		final var insertChange = atomicFactory.createReplaceSingleReferenceChange(affectedEObject, reference, null, newValue);
		return List.of(createChange, insertChange);
	}

	/**
	 * Creates a new EChange list that replace a feature value with null and deletes the replaced EObject.
	 * @param affectedEObject The affected object, in which feature the old value will be replaced by null.
	 * @param reference The reference of the affected object, in which the old value will be replaced by null.
	 * @param oldValue The old value which will be replaced by null.
	 * @return The created change.
	 */
	public <A extends EObject, T extends EObject> List<EChange<EObject>> createReplaceAndDeleteNonRootChange(
		A affectedEObject, EReference reference, T oldValue) {
		final var removeChange = atomicFactory.createReplaceSingleReferenceChange(affectedEObject, reference, oldValue, null);
		final DeleteEObject<EObject> deleteChange = atomicFactory.createDeleteEObjectChange(oldValue);
		return List.of(removeChange, deleteChange);
	}

	/**
	 * Creates a new EChange list that replaced a feature value with a newly created EObject and deletes the replaced EObject.
	 * @param affectedEObject The affected object, in which feature the non root element will be replaced.
	 * @param reference The reference of the affected object, in which the non root element will be replaced.
	 * @param oldValue The replaced and deleted non root element.
	 * @param newValue The created and replacing non root element.
	 * @return The created change.
	 */
	public <A extends EObject, T extends EObject> List<EChange<EObject>> createCreateAndReplaceAndDeleteNonRootChange(
		A affectedEObject, EReference reference, T oldValue, T newValue) {
		final DeleteEObject<EObject> deleteChange = atomicFactory.createDeleteEObjectChange(oldValue);
		final CreateEObject<EObject> createChange = atomicFactory.createCreateEObjectChange(newValue);
		final var replaceChange = atomicFactory.createReplaceSingleReferenceChange(affectedEObject, reference, oldValue,
			newValue);
		return List.of(createChange, replaceChange, deleteChange);
	}

}
		