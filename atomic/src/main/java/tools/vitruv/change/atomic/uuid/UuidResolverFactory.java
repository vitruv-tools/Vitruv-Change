package tools.vitruv.change.atomic.uuid;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Factory class to instantiate implementations of a {@link UuidResolver}.
 *
 * @author Maik Sept
 */
public class UuidResolverFactory {
  
  /**
   * Private constructor to prevent instantiation.
   */
  private UuidResolverFactory() {}
  
  /**
   * Creates a new {@link UuidResolver} with the given resource set.
   *
   * @param resourceSet is the resource set the UUID resolver uses.
   * @return a new {@link UuidResolver} instance.
   */
  public static UuidResolver create(ResourceSet resourceSet) {
    return new UuidResolverImpl(resourceSet);
  }
}
