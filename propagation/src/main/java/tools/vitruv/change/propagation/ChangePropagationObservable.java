package tools.vitruv.change.propagation;

import org.eclipse.emf.ecore.EObject;

/**
 * A {@link ChangePropagationObservable} informs {@link ChangePropagationObserver}s about
 * event that occur during change propagation.
 */
public interface ChangePropagationObservable {
  /**
   * Informs the observers about the creation of <code>createdObject</code>.
   *
   * @param createdObject - {@link EObject}
   */
  void notifyObjectCreated(EObject createdObject);

  /**
   * Registers <code>observer</code>.
   *
   * @param observer - {@link ChangePropagationObserver}
   */
  void registerObserver(final ChangePropagationObserver observer);

  /**
   * Deregisters <code>observer</code>.
   *
   * @param observer - {@link ChangePropagationObserver}
   */
  void deregisterObserver(final ChangePropagationObserver observer);
}
