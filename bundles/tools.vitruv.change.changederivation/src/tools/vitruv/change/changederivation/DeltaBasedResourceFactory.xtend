package tools.vitruv.change.changederivation

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource

class DeltaBasedResourceFactory implements Resource.Factory {
	override Resource createResource(URI uri) {
		return new DeltaBasedResource(uri)
	}
}
