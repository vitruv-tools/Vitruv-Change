package tools.vitruv.change.correspondence.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import tools.vitruv.change.correspondence.Correspondence;

/**
 * An persistable representation of a {@link CorrespondenceModel}.
 *
 * @author Heiko Klare
 */
public interface PersistableCorrespondenceModel extends CorrespondenceModel {
  /**
   * Loads the correspondence model from its persistence if existing.
   *
   * @param resolveIn the {@link ResourceSet} to resolve the corresponding objects in
   */
  public void loadSerializedCorrespondences(ResourceSet resolveIn);

  /** Saves this correspondence model instance. */
  public void save();

  /**
   * Copies the correspondence model, using the given mapping to replace the objects referenced in
   * the correspondences.
   *
   * @param eObjectMapping the mapping to replace the objects referenced in the correspondences
   * @return a copy of this correspondence model
   */
  public PersistableCorrespondenceModel copy(Map<EObject, EObject> eObjectMapping);

  /**
   * Returns the correspondences of this model in the order in which they are stored. The list is
   * live: adding or removing correspondences in it adds or removes them in this model. This allows
   * persisting the model as a sequence of changes to the list and replaying them.
   *
   * @return the modifiable list of correspondences
   */
  public List<Correspondence> getCorrespondenceList();

  /**
   * Removes the correspondences of elements that are not contained in a resource anymore, as
   * {@link #save()} does before writing.
   */
  public void removeCorrespondencesForRemovedElements();

  /**
   * Writes the correspondences to the given stream in the format of {@link #save()}, with element
   * references relative to the URI of this model's resource. Does not change what {@link #save()}
   * considers written.
   *
   * @param outputStream the stream to write to; it is not closed
   * @throws IOException if writing fails
   * @throws IllegalStateException if this model has no resource URI
   */
  public void writeTo(OutputStream outputStream) throws IOException;
}
