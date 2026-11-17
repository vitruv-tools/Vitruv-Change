package tools.vitruv.change.correspondence.model;

import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * An persistable representation of a {@link CorrespondenceModel}.
 *
 * @author Heiko Klare
 */
public interface PersistableCorrespondenceModel extends CorrespondenceModel {
  /**
   * Starts recording the correspondence changes made on the calling thread, for example during
   * one attempt of a change propagation. A recording already running on this thread is discarded.
   * Changes of other threads are not recorded.
   */
  void startRecording();

  /**
   * Returns the correspondence changes recorded on the calling thread since {@link
   * #startRecording()}, in the order they were made.
   *
   * @return the recorded changes, empty if this thread does not record
   */
  List<CorrespondenceChange> getRecordedChanges();

  /**
   * Undoes the correspondence changes recorded on the calling thread, the last one first, and
   * starts recording afresh. Does nothing if this thread does not record.
   */
  void revertRecordedChanges();

  /** Stops recording the correspondence changes of the calling thread. */
  void stopRecording();

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
}
