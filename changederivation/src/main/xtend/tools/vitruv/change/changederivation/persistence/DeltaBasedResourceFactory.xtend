package tools.vitruv.change.changederivation.persistence

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource

/**
 * Factory to provide {@link DeltaBasedResource}s.
 */
class DeltaBasedResourceFactory implements Resource.Factory {
	override Resource createResource(URI uri) {
		return new DeltaBasedResource(uri)
	}
}
