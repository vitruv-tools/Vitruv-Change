package tools.vitruv.change.atomic.uuid;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil.isPathmap;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.getOrCreateResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	private static final String SERIALIZATION_SEPARATOR = "|";

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
		checkState(eObjectToUuid.getOrDefault(element, uuid).equals(uuid),
				"element %s is already registered for UUID %s", element, eObjectToUuid.get(element));
		checkState(eObjectToUuid.inverse().getOrDefault(uuid, element).equals(element),
				"UUID %s is already registered for element %s", uuid, eObjectToUuid.inverse().get(uuid));
		if (isReadOnlyEObject(element)) {
			String expectedUuid = getUuidForReadOnlyEObject(element);
			checkState(uuid.equals(expectedUuid), "read-only object %s must be registered for UUID %s but was %s",
					element, expectedUuid, uuid);
			return;
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Adding UUID " + uuid + " for EObject: " + element);
		}
		eObjectToUuid.put(element, uuid);
	}

	@Override
	public String generateUuid(EObject eObject) {
		checkState(!eObject.eIsProxy(), "Cannot generate UUID for proxy object %s", eObject);
		if (isReadOnlyEObject(eObject)) {
			return getUuidForReadOnlyEObject(eObject);
		}
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
	public void resolveResources(Map<Resource, Resource> sourceToTargetResourceMapping,
			UuidResolver targetUuidResolver) {
		checkState(sourceToTargetResourceMapping != null, "source to target resource mapping must not be null");
		checkState(targetUuidResolver != null, "target UUID resolver must not be null");
		if (sourceToTargetResourceMapping.isEmpty()) {
			return;
		}
		sourceToTargetResourceMapping.keySet().forEach(resource -> checkState(resource.getResourceSet() == resourceSet,
				"trying to unresolve resource %s from different resource set", resource));
		ResourceSet targetResourceSet = sourceToTargetResourceMapping.values().iterator().next().getResourceSet();
		sourceToTargetResourceMapping.values()
				.forEach(resource -> checkState(resource.getResourceSet() == targetResourceSet,
						"trying to resolve resource %s from different resource set", resource));
		Map<String, String> uuidToIdMapping = generateUuidToIdMapping(sourceToTargetResourceMapping.keySet());
		applyUuidToIdMapping(uuidToIdMapping, targetUuidResolver, targetResourceSet, sourceToTargetResourceMapping);
	}

	@Override
	public void loadFromUri(URI uri) throws IOException {
		checkState(eObjectToUuid.isEmpty(),
				"trying to load stored UUID resolver configuration but contained already some UUIDs");
		checkState(uri.isFile(), "Loading UUID resolver requires a file uri but was %s", uri);
		File file = new File(uri.toFileString());
		if (!file.exists()) {
			return;
		}
		Map<String, String> uuidToIdMapping = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			while (line != null) {
				String[] components = line.split("\\" + SERIALIZATION_SEPARATOR);
				checkState(components.length == 2, "invalid UUID resolver serialization (line %s) found at %s", line,
						uri);
				uuidToIdMapping.put(components[0], components[1]);
				line = reader.readLine();
			}
		}
		applyUuidToIdMapping(uuidToIdMapping, this, resourceSet, null);
	}

	@Override
	public void storeAtUri(URI uri) throws IOException {
		checkState(uri.isFile(), "Storing UUID resolver requires a file uri but was %s", uri);
		Map<String, String> uuidToIdMapping = generateUuidToIdMapping(null);
		File file = new File(uri.toFileString());
		try (FileWriter writer = new FileWriter(file)) {
			for (var entry : uuidToIdMapping.entrySet()) {
				String uuid = entry.getKey();
				String id = entry.getValue();
				writer.write(uuid + SERIALIZATION_SEPARATOR + id);
				writer.write(System.lineSeparator());
			}
		}
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
		return eObject.eResource() != null && eObject.eResource().getURI() != null
				&& isReadOnlyUri(eObject.eResource().getURI());
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

	/**
	 * Creates a mapping from UUIDs to hierarchical IDs. If a
	 * <code>resourcesFilter</code> is provided, only {@link EObject}s contained in
	 * those resources are considered for creating the mapping. If
	 * <code>resourcesFilter</code> is <code>null</code>, all {@link EObject}s
	 * registered in the resolver are considered.
	 * 
	 * @param resourcesFilter is the filter for the resources to consider, or
	 *                        <code>null<code> if all resources shall be considered.
	 */
	private Map<String, String> generateUuidToIdMapping(Collection<Resource> resourcesFilter) {
		IdResolver idUnresolver = IdResolver.create(resourceSet);
		Map<String, String> uuidToIdMapping = new HashMap<>();
		for (var entry : eObjectToUuid.entrySet()) {
			EObject eObject = entry.getKey();
			checkState(eObject.eResource() != null && eObject.eResource().getResourceSet() != null,
					"trying to unresolve dangling EObject %s", eObject);
			if (resourcesFilter != null && !resourcesFilter.contains(eObject.eResource())) {
				continue;
			}
			String id = idUnresolver.getAndUpdateId(eObject);
			String uuid = entry.getValue();
			uuidToIdMapping.put(uuid, id);
		}
		return uuidToIdMapping;
	}

	/**
	 * Apply the given UUID to hierarchical ID mapping to the given
	 * {@link UuidResolver} by resolving each ID in the given resolver's resource
	 * set and registering the obtained object with the associated UUID. If
	 * <code>sourceToTargetResourceMapping</code> is not <code>null</code>, the
	 * resources of the obtained and own elements corresponding to a UUID must be a
	 * pair in the given mapping.
	 * 
	 * @param uuidToIdMapping               is the UUID to hierarchical ID mapping.
	 * @param targetUuidResolver            is the {@link UuidResolver} to register
	 *                                      the given UUIDs in.
	 * @param targetResourceSet             is the resource set of the given uuid
	 *                                      resolver.
	 * @param sourceToTargetResourceMapping is the mapping from own resources to the
	 *                                      given resolver's resources, or
	 *                                      <code>null</code> if the mapping shall
	 *                                      not be validated.
	 */
	private void applyUuidToIdMapping(Map<String, String> uuidToIdMapping, UuidResolver targetUuidResolver,
			ResourceSet targetResourceSet, Map<Resource, Resource> sourceToTargetResourceMapping)
			throws IllegalStateException {
		var idResolver = IdResolver.create(targetResourceSet);
		for (var entry : uuidToIdMapping.entrySet()) {
			String uuid = entry.getKey();
			String id = entry.getValue();
			EObject targetEObject = idResolver.getEObject(id);
			checkState(targetEObject != null, "could not find object corresponding to %s in resource set %s", uuid,
					resourceSet);
			if (sourceToTargetResourceMapping != null) {
				EObject sourceEObject = eObjectToUuid.inverse().get(uuid);
				checkState(targetEObject.eResource() == sourceToTargetResourceMapping.get(sourceEObject.eResource()),
						"resolved object %s to element %s which is contained in wrong resource", targetEObject,
						sourceEObject);
			}
			targetUuidResolver.registerEObject(uuid, targetEObject);
		}
	}
}
