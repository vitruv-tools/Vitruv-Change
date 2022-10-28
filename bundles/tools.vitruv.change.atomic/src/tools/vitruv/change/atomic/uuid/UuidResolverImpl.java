package tools.vitruv.change.atomic.uuid;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil.isPathmap;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.getOrCreateResource;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import tools.vitruv.change.atomic.id.IdResolver;

class UuidResolverImpl implements UuidResolver {
	static private final Logger LOGGER = Logger.getLogger(UuidResolverImpl.class);
	private static final String NON_READONLY_PREFIX = "ord_";
	
	private final ResourceSet resourceSet;
	private final BiMap<EObject, String> eObjectToUuid = HashBiMap.create();
	
	public UuidResolverImpl(ResourceSet resourceSet) {
		checkArgument(resourceSet != null, "Resource set may not be null");
		this.resourceSet = resourceSet;
	}

	@Override
	public String getUuid(EObject eObject) throws IllegalStateException {
		String uuid = getUuidOrNull(eObject);
		checkState(uuid != null, "no UUID could be found for EObject: %s", eObject);
		return uuid;
	}

	@Override
	public EObject getEObject(String uuid) throws IllegalStateException {
		EObject eObject = getEObjectOrNull(uuid);
		checkState(eObject != null, "no EObject could be found for UUID: %s", uuid);
		return eObject;
	}

	@Override
	public void registerEObject(String uuid, EObject element) throws IllegalStateException {
		checkState(uuid != null, "uuid must not be null");
		checkState(element != null, "object must not be null");
		checkState(!isReadOnlyEObject(element), "object must not be read-only");
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Adding UUID " + uuid + " for EObject: " + element);
		}
		EObject oldObject = eObjectToUuid.inverse().get(uuid);
		String oldUuid = eObjectToUuid.get(element);
		if (oldObject != null && oldObject != element) {
			eObjectToUuid.remove(oldObject);
		}
		if (oldUuid != null && !oldUuid.equals(uuid)) {
			eObjectToUuid.inverse().remove(oldUuid);
		}
		eObjectToUuid.put(element, uuid);
	}
	
	@Override
	public String generateUuid(EObject eObject) {
		checkState(!eObject.eIsProxy(), "Cannot generate UUID for proxy object %s", eObject);
		return NON_READONLY_PREFIX + EcoreUtil.generateUUID();
	}

	@Override
	public Resource getResource(URI uri) {
		return getOrCreateResource(resourceSet, uri);
	}

	@Override
	public void endTransaction() {
		cleanupRemovedElements();
	}
	
	@Override
	public UuidResolver resolveIn(ResourceSet resourceSet) throws IllegalStateException {
		var resolver = new UuidResolverImpl(resourceSet);
		for (Resource resource : this.resourceSet.getResources()) {
			checkState(resource.getURI() != null, "trying to resolve in resource without URI: %s", resource);
			resolver.getResource(resource.getURI());
		}
		var idUnresolver = IdResolver.create(this.resourceSet);
		var idResolver = IdResolver.create(resourceSet);
		for (var entry : eObjectToUuid.entrySet()) {
			EObject eObject = entry.getKey();
			checkState(eObject.eResource() != null && eObject.eResource().getResourceSet() != null, "trying to unresolve dangling EObject %s", eObject);
			String uuid = entry.getValue();
			String id = idUnresolver.getAndUpdateId(eObject);
			EObject resolvedEObject = idResolver.getEObject(id);
			checkState(resolvedEObject != null, "could not find object corresponding to %s in resource set %s", eObject, resourceSet);
			resolver.registerEObject(uuid, resolvedEObject);
		}
		return resolver;
	}
	
	private String getUuidOrNull(EObject eObject) {
		if (isReadOnlyEObject(eObject)) {
			return getUuidForReadOnlyEObject(eObject);
		}
		String uuid = eObjectToUuid.get(eObject);
		return uuid;
	}
	
	private String getUuidForReadOnlyEObject(EObject eObject) {
		return EcoreUtil.getURI(eObject).toString();
	}
	
	private EObject getEObjectOrNull(String uuid) {
		if (isReadOnlyUuid(uuid)) {
			return getEObjectForReadOnlyUuid(uuid);
		}
		return eObjectToUuid.inverse().get(uuid);
	}
	
	private EObject getEObjectForReadOnlyUuid(String uuid) {
		URI uri = URI.createURI(uuid);
		return uri.hasFragment() ? resourceSet.getEObject(uri, true) : null;
	}
	
	private boolean isReadOnlyEObject(EObject eObject) {
		return eObject.eResource() != null && eObject.eResource().getURI() != null && isReadOnlyUri(eObject.eResource().getURI());
	}
	
	private boolean isReadOnlyUri(URI uri) {
		return isPathmap(uri) || uri.isArchive();
	}
	
	private boolean isReadOnlyUuid(String uuid) {
		return !uuid.startsWith(NON_READONLY_PREFIX);
	}
	
	private void cleanupRemovedElements() {
		var iterator = eObjectToUuid.keySet().iterator();
		while (iterator.hasNext()) {
			EObject object = iterator.next();
			if (object.eResource() == null || object.eResource().getResourceSet() == null) {
				iterator.remove();
			}
		}
	}
}
