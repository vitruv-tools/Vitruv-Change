package tools.vitruv.change.changederivation.persistence;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Factory to provide {@link DeltaBasedResource}s.
 */
@SuppressWarnings("all")
public class DeltaBasedResourceFactory implements Resource.Factory {
  @Override
  public Resource createResource(final URI uri) {
    return new DeltaBasedResource(uri);
  }
}
