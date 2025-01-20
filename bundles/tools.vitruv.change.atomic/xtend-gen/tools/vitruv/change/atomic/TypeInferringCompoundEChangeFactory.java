package tools.vitruv.change.atomic;

import java.util.Collections;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

@SuppressWarnings("all")
public class TypeInferringCompoundEChangeFactory {
  protected TypeInferringAtomicEChangeFactory atomicFactory;

  private static TypeInferringCompoundEChangeFactory instance;

  protected TypeInferringCompoundEChangeFactory(final TypeInferringAtomicEChangeFactory atomicFactory) {
    this.atomicFactory = atomicFactory;
  }

  /**
   * Get the singleton instance of the factory.
   * @return The singleton instance.
   */
  public static TypeInferringCompoundEChangeFactory getInstance() {
    if ((TypeInferringCompoundEChangeFactory.instance == null)) {
      TypeInferringAtomicEChangeFactory _instance = TypeInferringAtomicEChangeFactory.getInstance();
      TypeInferringCompoundEChangeFactory _typeInferringCompoundEChangeFactory = new TypeInferringCompoundEChangeFactory(_instance);
      TypeInferringCompoundEChangeFactory.instance = _typeInferringCompoundEChangeFactory;
    }
    return TypeInferringCompoundEChangeFactory.instance;
  }

  /**
   * Creates a new {@link CreateAndInsertRoot} EChange.
   * @param affectedEObject The created and inserted root object by the change.
   * @param resource The resource where the root object will be inserted.
   * @param index The index at which the root object will be inserted into the resource.
   * @return The created change.
   */
  public <T extends EObject> List<EChange<T>> createCreateAndInsertRootChange(final T affectedEObject, final Resource resource, final int index) {
    final CreateEObject<T> createChange = this.atomicFactory.<T>createCreateEObjectChange(affectedEObject);
    final InsertRootEObject<T> insertChange = this.atomicFactory.<T>createInsertRootChange(affectedEObject, resource, index);
    return Collections.<EChange<T>>unmodifiableList(CollectionLiterals.<EChange<T>>newArrayList(createChange, insertChange));
  }

  /**
   * Creates a new {@link CreateAndRemoveDeleteRoot} EChange.
   * @param affectedEObject The removed and deleted root object by the change.
   * @param resource The resource where the root object will be removed from.
   * @param index The index at which the root object will be removed from the resource.
   * @return The created change.
   */
  public <T extends EObject> List<EChange<T>> createRemoveAndDeleteRootChange(final T affectedEObject, final Resource resource, final int index) {
    final DeleteEObject<T> deleteChange = this.atomicFactory.<T>createDeleteEObjectChange(affectedEObject);
    final RemoveRootEObject<T> removeChange = this.atomicFactory.<T>createRemoveRootChange(affectedEObject, resource, index);
    return Collections.<EChange<T>>unmodifiableList(CollectionLiterals.<EChange<T>>newArrayList(removeChange, deleteChange));
  }

  /**
   * Creates a new {@link CreateAndInsertNonRoot} EChange.
   * @param affectedEObject The affected object, in which feature the created non root element will be inserted.
   * @param reference The reference of the affected object, in which the created non root element will be inserted.
   * @param newValue The created and inserted non root element.
   * @param index The index at which the non root element will be inserted into the reference.
   * @return The created change.
   */
  public <A extends EObject, T extends EObject> List<EChange<EObject>> createCreateAndInsertNonRootChange(final A affectedEObject, final EReference reference, final T newValue, final int index) {
    final CreateEObject<EObject> createChange = this.atomicFactory.<EObject>createCreateEObjectChange(newValue);
    final InsertEReference<EObject> insertChange = this.atomicFactory.<EObject>createInsertReferenceChange(affectedEObject, reference, newValue, index);
    return Collections.<EChange<EObject>>unmodifiableList(CollectionLiterals.<EChange<EObject>>newArrayList(createChange, insertChange));
  }

  /**
   * Creates a new {@link RemoveAndDeleteNonRoot} EChange.
   * @param affectedEObject The affected object, from which feature the non root element will be removed.
   * @param reference The reference of the affected object, from which the non root element will be removed.
   * @param oldValue The removed and deleted non root element.
   * @param index The index at which the non root element will be removed from the reference.
   * @return The created change.
   */
  public <A extends EObject, T extends EObject> List<EChange<EObject>> createRemoveAndDeleteNonRootChange(final A affectedEObject, final EReference reference, final T oldValue, final int index) {
    final DeleteEObject<EObject> deleteChange = this.atomicFactory.<EObject>createDeleteEObjectChange(oldValue);
    final RemoveEReference<EObject> removeChange = this.atomicFactory.<EObject>createRemoveReferenceChange(affectedEObject, reference, oldValue, index);
    return Collections.<EChange<EObject>>unmodifiableList(CollectionLiterals.<EChange<EObject>>newArrayList(removeChange, deleteChange));
  }

  /**
   * Creates a new {@link CreateAndReplaceNonRoot} EChange.
   * @param affectedEObject The affected object, in which feature null will be replaced by the new value.
   * @param reference The reference of the affected object, in which null will be replaced by the new value.
   * @param newValue The new value which replaces null.
   * @return The created change.
   */
  public <A extends EObject, T extends EObject> List<EChange<EObject>> createCreateAndReplaceNonRootChange(final A affectedEObject, final EReference reference, final T newValue) {
    final CreateEObject<EObject> createChange = this.atomicFactory.<EObject>createCreateEObjectChange(newValue);
    final ReplaceSingleValuedEReference<EObject> insertChange = this.atomicFactory.<EObject>createReplaceSingleReferenceChange(affectedEObject, reference, null, newValue);
    return Collections.<EChange<EObject>>unmodifiableList(CollectionLiterals.<EChange<EObject>>newArrayList(createChange, insertChange));
  }

  /**
   * Creates a new {@link ReplaceAndDeleteNonRoot} EChange.
   * @param affectedEObject The affected object, in which feature the old value will be replaced by null.
   * @param reference The reference of the affected object, in which the old value will be replaced by null.
   * @param oldValue The old value which will be replaced by null.
   * @return The created change.
   */
  public <A extends EObject, T extends EObject> List<EChange<EObject>> createReplaceAndDeleteNonRootChange(final A affectedEObject, final EReference reference, final T oldValue) {
    final ReplaceSingleValuedEReference<EObject> removeChange = this.atomicFactory.<EObject>createReplaceSingleReferenceChange(affectedEObject, reference, oldValue, null);
    final DeleteEObject<EObject> deleteChange = this.atomicFactory.<EObject>createDeleteEObjectChange(oldValue);
    return Collections.<EChange<EObject>>unmodifiableList(CollectionLiterals.<EChange<EObject>>newArrayList(removeChange, deleteChange));
  }

  /**
   * Creates a new {@link CreateAndReplaceAndDeleteNonRoot} EChange.
   * @param affectedEObject The affected object, in which feature the non root element will be replaced.
   * @param reference The reference of the affected object, in which the non root element will be replaced.
   * @param oldValue The replaced and deleted non root element.
   * @param newValue The created and replacing non root element.
   * @return The created change.
   */
  public <A extends EObject, T extends EObject> List<EChange<EObject>> createCreateAndReplaceAndDeleteNonRootChange(final A affectedEObject, final EReference reference, final T oldValue, final T newValue) {
    final DeleteEObject<EObject> deleteChange = this.atomicFactory.<EObject>createDeleteEObjectChange(oldValue);
    final CreateEObject<EObject> createChange = this.atomicFactory.<EObject>createCreateEObjectChange(newValue);
    final ReplaceSingleValuedEReference<EObject> replaceChange = this.atomicFactory.<EObject>createReplaceSingleReferenceChange(affectedEObject, reference, oldValue, newValue);
    return Collections.<EChange<EObject>>unmodifiableList(CollectionLiterals.<EChange<EObject>>newArrayList(createChange, replaceChange, deleteChange));
  }
}
