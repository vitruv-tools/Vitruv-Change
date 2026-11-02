package tools.vitruv.change.correspondence.model;

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
