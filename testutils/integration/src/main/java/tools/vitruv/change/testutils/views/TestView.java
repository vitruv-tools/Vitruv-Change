package tools.vitruv.change.testutils.views;

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.testutils.TestUserInteraction;

/**
 * A Vitruv view for testing purposes.
 */
interface TestView extends AutoCloseable {
  /**
   * Loads the model resource for the provided {@code viewRelativePath},
   * casts its root element to the provided {@code clazz} and returns the casted object.
   *
   * @param viewRelativePath A project-relative path to a model.
   */
  default <T extends EObject> T from(Class<T> clazz, Path viewRelativePath) {
    return from(clazz, getUri(viewRelativePath));
  }

  /**
   * Casts the root element of the provided {@code resource} to the provided {@code clazz} 
   * and returns the casted object.
   */
  default <T extends EObject> T from(Class<T> clazz, Resource resource) {
    checkState(!resource.getContents().isEmpty(), 
        "The resource at " + resource.getURI() + " is empty!");
    return clazz.cast(resource.getContents().get(0));
  }

  /**
   * Loads the model resource for the provided {@code modelUri},
   * casts its root element to the provided {@code clazz} and returns the casted object.
   *
   * @param modelUri the {@link URI} of the model to load.
   */
  <T extends EObject> T from(Class<T> clazz, URI modelUri);

  /**
   * Gets the resource at the provided {@code viewRelativePath}. If the resource
   * does not exist yet, it will be created virtually, without being persisted.
   *
   * @param viewRelativePath a path to a model, relative to this view.
   */
  default Resource resourceAt(Path viewRelativePath) {
    return resourceAt(getUri(viewRelativePath));
  }

  /**
   * Gets the resource at the provided {@code modelUri}.
   * If the resource does not exist yet, it will be created virtually,
   * without being persisted.
   *
   * @param modelUri the {@link URI} of the model to load.
   */
  Resource resourceAt(URI modelUri);


  /**
   * Gets an EMF URI that can be used to load the resource that is persisted at the provided
   * {@code viewRelativePath}.
   */
  URI getUri(Path viewRelativePath);

  /**
   * Moves the provided {@code resource} to its {@code newViewRelativePath} 
   * by both updating its URI and moving its serialization on the file system (if it exists).
   */
  void moveTo(Resource resource, Path newViewRelativePath);

  /**
   * Moves the provided {@code resource} to its {@code newUri}
   * by both updating its URI and moving its serialization on the file system (if it exists).
   */
  void moveTo(Resource resource, URI newUri);

  /**
   * Starts recording changes for the provided {@code notifier}, 
   * executes the provided  {@code consumer} on the {@code notifier}
   * and stops recording changes for the {@code notifier} afterwards.
   *
   * <p>Whether changes will effectively be recorded depends on this view.
   * It is permissible for a view not to record any changes if it deems them irrelevant.
   */
  <T extends Notifier> T record(T notifier, Consumer<T> consumer);

  /**
   * {@linkplain #record Records} the changes to {@code notifier} created by the provided 
   * {@code consumer}, saves the modified resource and propagates all recorded changes 
   * (including changes that have been recorded before calling this method).
   * Where the changes are propagated to depends on this view.
   * 
   * <p>Whether changes will effectively be recorded depends on this view. 
   * It is permissible for a view not to record any changes if it deems them irrelevant.
   * In this case, the returned list will be empty.
   *
   * @return the changes resulting from propagating the recorded changes.
   */
  <T extends Notifier> List<PropagatedChange> propagate(T notifier, Consumer<T> consumer);

  /**
   * Returns the test user interaction for this view.
   *
   * @return the user interaction that can be used to program user interactions for this view.
   */
  TestUserInteraction getUserInteraction();
}
