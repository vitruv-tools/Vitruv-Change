package tools.vitruv.change.testutils.views;

import java.util.List;
import org.eclipse.emf.common.notify.Notifier;
import tools.vitruv.change.composite.description.PropagatedChange;

/**
 * An extension of {@link TestView}, which provides non-transactional options for change recording, 
 * i.e., for manually starting and stopping recording changes of elements 
 * and for propagating all changes recorded since the last propagation.
 *
 * @deprecated 
 *     This view is deprecated and exists only to support {@code LegacyVitruvApplicationTest}. 
 *     Do not use this class!
 */
@Deprecated(since = "2022-08-10")
interface NonTransactionalTestView extends TestView {
  /**
   * Starts recording changes for the given {@code notifier} and all its contained elements.
   * Has to be stopped by calling {@link #stopRecordingChanges} with the same argument.
   * Returns the given {@code notifier}.
   * 
   * <p>Use {@link #record} to run recording in a transaction 
   * that automatically stops recording on return. 
   * 
   * <p>Whether changes will effectively be recorded depends on this view. 
   * It is permissible for a view not to record any changes if it deems them irrelevant.
   *
   * @param notifier - any {@link Notifier}
   * @return notifier
   */
  <T extends Notifier> T startRecordingChanges(T notifier);

  /**
   * Stops recording changes for the given {@code notifier} and all its contained elements.
   * When called after {@link #startRecordingChanges}, the same {@code notifier} must be used.
   * Has no effect if recording has not been started before.
   * Returns the given {@code notifier}.
   *
   * <p>Use {@link #record} to run recording in a transaction
   * that automatically stops recording on return. 
   *
   * @param notifier - any {@link Notifier}
   * @return notifier
   */
  <T extends Notifier> T stopRecordingChanges(T notifier);

  /**
   * Propagates all changes recorded since the last call of this method at all objects 
   * for which recording has been started before the changes were performed using 
   * {@link #startRecordingChanges}. 
   * Calling this method does not stop any recording, 
   * so recording for all elements for which it has been started before continues.
   *
   * @return {@link List} of propagated changes
   */
  List<PropagatedChange> propagate();

  /**
   * Clears all loaded resources, such that yet loaded resources become invalid 
   * and have to be reloaded using {@link #resourceAt}.
   */
  void disposeViewResources();

  /**
   * Defines whether the view resources that have previously been loaded 
   * using methods like {@link #resourceAt} shall automatically be disposed up propagating changes,
   * i.e. when calling {@link #propagate}.
   *
   * @param enabled - boolean
   */
  void setDisposeViewResourcesAfterPropagation(boolean enabled);
}
