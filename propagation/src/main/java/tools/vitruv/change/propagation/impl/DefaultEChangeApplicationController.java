package tools.vitruv.change.propagation.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.command.internal.ApplyEChangeSwitch;
import tools.vitruv.change.propagation.EChangeApplicationController;

/**
 * Implements {@link EChangeApplicationController} in a single-threaded context.
 *
 * <p>Preparation includes the marking of {@code EChanges} as prepared,
 * finishing the unmarking as prepared.
 */
public class DefaultEChangeApplicationController
    implements EChangeApplicationController<EObject> {
  private static final Logger LOGGER = LogManager.getLogger(DefaultEChangeApplicationController.class);
  public static final String NOT_YET_PREPARED = "%s has not been yet prepared for execution!";

  /**
   * Set of all prepared, executable changes.
   */
  private final Set<EChange<EObject>> preparedChanges = new HashSet<>();

  @Override
  public boolean prepare(EChange<EObject> change) {
    checkArgument(!preparedChanges.contains(change),
        "%s has already been prepared for execution!",
        change);
    preparedChanges.add(change);
    return true;
  }

  @Override
  public boolean apply(EChange<EObject> change) {
    checkArgument(preparedChanges.contains(change),
        NOT_YET_PREPARED,
        change);
    try {
      ApplyEChangeSwitch.applyEChange(change, true);
      return true;
    } catch (Exception e) {
      LOGGER.info("Could not apply the following EChange: {}", change);
      return false;
    }
  }

  @Override
  public void finish(EChange<EObject> change) {
    checkArgument(preparedChanges.contains(change),
        NOT_YET_PREPARED,
        change);
    preparedChanges.remove(change);
  }

  @Override
  public void undo(EChange<EObject> change) {
    checkArgument(preparedChanges.contains(change),
        NOT_YET_PREPARED,
        change);
    try {
      ApplyEChangeSwitch.applyEChange(change, false);
    } catch (Exception e) {
      LOGGER.info("Could not undo the following EChange: {}", change);
    }
  }
}
