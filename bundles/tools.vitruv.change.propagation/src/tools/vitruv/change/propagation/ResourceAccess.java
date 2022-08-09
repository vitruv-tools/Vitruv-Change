package tools.vitruv.change.propagation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Offers possibilities for resource access and persistence during change propagation.
 */
public interface ResourceAccess {
	/**
	 * Provides the resource for storing the specified model in it.
	 * @param uri the model's uri, must not be {@code null}
	 */
	Resource getModelResource(URI uri);

	/**
	 * Persists the given {@code rootObject} at the given {@code uri}.
	 * @param rootObject the object to persist, must not be {@code null}
	 * @param uri the URI to persist at, must not be {@code null}
	 */
	void persistAsRoot(EObject rootObject, URI uri);
}
