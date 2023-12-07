package tools.vitruv.change.propagation

import org.eclipse.emf.ecore.EObject

interface ChangePropagationObserver {
	def void objectCreated(EObject createdObject);
}