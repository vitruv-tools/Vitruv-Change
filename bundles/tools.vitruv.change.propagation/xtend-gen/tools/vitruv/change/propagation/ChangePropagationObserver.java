package tools.vitruv.change.propagation;

import org.eclipse.emf.ecore.EObject;

@SuppressWarnings("all")
public interface ChangePropagationObserver {
  void objectCreated(final EObject createdObject);
}
