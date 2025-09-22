package tools.vitruv.change.propagation;

import org.eclipse.emf.ecore.EObject;

/**
 * A {@link ChangePropagationObserver} may be informed about events
 * that occur during change propagation.
 */
public interface ChangePropagationObserver {
  /**
   * Informs this observer about the creation of <code>createdObject</code>.
   *
   * @param createdObject - {@link EObject}
   */
  void objectCreated(EObject createdObject);
}
