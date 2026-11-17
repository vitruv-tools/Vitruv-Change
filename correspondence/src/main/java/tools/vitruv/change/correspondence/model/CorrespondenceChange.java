package tools.vitruv.change.correspondence.model;

import tools.vitruv.change.correspondence.Correspondence;

/**
 * A change of a correspondence model, as recorded on the thread that made it (see {@link
 * PersistableCorrespondenceModel#startRecording()}).
 *
 * @param kind whether the correspondence was added or removed
 * @param correspondence the added or removed correspondence
 * @param index its index in the correspondence list: where it was added, or where it was before it
 *     was removed
 */
public record CorrespondenceChange(Kind kind, Correspondence correspondence, int index) {
  /** The kind of a correspondence change. */
  public enum Kind {
    /** The correspondence was added to the model. */
    ADDED,
    /** The correspondence was removed from the model. */
    REMOVED
  }
}
