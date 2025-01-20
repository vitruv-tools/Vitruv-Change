package tools.vitruv.change.propagation;

import org.eclipse.emf.ecore.EObject;

@SuppressWarnings("all")
public interface ChangePropagationObservable {
  void notifyObjectCreated(final EObject createdObject);

  void registerObserver(final ChangePropagationObserver observer);

  void deregisterObserver(final ChangePropagationObserver observer);
}
