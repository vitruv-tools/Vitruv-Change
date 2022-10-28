package tools.vitruv.change.atomic.uuid;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public interface UuidResolver {
	/**
	 * Returns whether the given {@link EObject} has a registered UUID or not.
	 */
	public default boolean hasUuid(EObject eObject) {
		try {
			String uuid = getUuid(eObject);
			return uuid != null;
		}
		catch (IllegalStateException e) {
			return false;
		}
	};
	
	/**
	 * Returns whether an {@link EObject} is registered for the given UUID or not. 
	 */
	public default boolean hasEObject(String uuid) {
		try {
			EObject eObject = getEObject(uuid);
			return eObject != null;
		}
		catch (IllegalStateException e) {
			return false;
		}
	};

	/**
 	 * Returns the UUID for the given {@link EObject}.
 	 * If no UUID is registered for it, an {@link IllegalStateException} is thrown.
 	 */
	public String getUuid(EObject eObject) throws IllegalStateException;

	/**
	 * Returns the {@link EObject} for the given UUID. If more than one object was registered
	 * for the UUID, the last one is returned.
	 * @throws IllegalStateException if no {@link EObject} was registered for the UUID
	 */
	public EObject getEObject(String uuid) throws IllegalStateException;
	
	public String generateUuid(EObject eObject);
	
	/**
 	 * Registers the given {@link EObject} for the given UUID.
 	 * @throws IllegalStateException if there is already a UUID registered for the given {@link EObject}
 	 * or vice versa
 	 */
 	public void registerEObject(String uuid, EObject eObject) throws IllegalStateException;
 	
 	public default String registerEObject(EObject eObject) throws IllegalStateException {
 		String uuid = generateUuid(eObject);
 		registerEObject(uuid, eObject);
 		return uuid;
 	}
	
 	public UuidResolver resolveIn(ResourceSet resourceSet) throws IllegalStateException;
	
	/**
	 * Returns the {@link Resource} for the given {@link URI}. If the resource does not exist yet,
	 * it gets created.
	 */
	public Resource getResource(URI uri);
	
	/**
	 * Ends a transactions such that all {@link EObject}s not being contained in a resource, which is
	 * contained in a resource set, are removed from the ID mapping.
	 */
	public void endTransaction();
	
	public static UuidResolver create(ResourceSet resourceSet) {
		return new UuidResolverImpl(resourceSet);
	}
}
