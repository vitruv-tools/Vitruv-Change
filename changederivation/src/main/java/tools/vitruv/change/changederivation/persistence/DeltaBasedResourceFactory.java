package tools.vitruv.change.changederivation.persistence;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Factory to provide {@link DeltaBasedResource}s.
 *
 * @deprecated Saves the current state as a sequence of create changes rather than a history of
 *     changes, and is not used by Vitruvius. Delta-based persistence of a virtual model is provided
 *     by its model persistence strategy in Vitruv ({@code tools.vitruv.framework.vsum.persistence}).
 *     Will be removed in the next major version.
 */
@Deprecated(forRemoval = true)
public class DeltaBasedResourceFactory implements Resource.Factory {

  @Override
  public Resource createResource(URI uri) {
    return new DeltaBasedResource(uri);
  }
}
