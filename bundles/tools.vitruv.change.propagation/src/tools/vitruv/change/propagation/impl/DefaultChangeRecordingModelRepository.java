package tools.vitruv.change.propagation.impl;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.correspondence.CorrespondenceModel;
import tools.vitruv.change.correspondence.InternalCorrespondenceModel;
import tools.vitruv.change.propagation.PersistableChangeRecordingModelRepository;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.getOrCreateResource;
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.loadOrCreateResource;
import static tools.vitruv.change.correspondence.CorrespondenceModelFactory.createCorrespondenceModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * A default implementation of a {@link PersistableChangeRecordingModelRepository}. It manages a {@link ResourceSet} for
 * the models, which are automatically monitored for changes. It loads existing models calling
 * {@link #loadExistingModels()} by loading the correspondence model at the URI specified in the constructor and
 * resolving all models references in the correspondence model. If no such URI is specified, loading will have no
 * effect.
 */
public class DefaultChangeRecordingModelRepository implements PersistableChangeRecordingModelRepository {
	private static final Logger LOGGER = Logger.getLogger(DefaultChangeRecordingModelRepository.class);
	private final ResourceSet modelsResourceSet;
	private final InternalCorrespondenceModel correspondenceModel;
	private final ChangeRecorder changeRecorder;
	private final Path consistencyMetadataFolder;

	private boolean isLoading = false;

	/**
	 * Creates a repository without a defined persistence for the correspondence model and using a temporary folder for
	 * storing consistency metadata.
	 * @throws IOException when no temporary metadata folder can be instantiated
	 */
	public DefaultChangeRecordingModelRepository() throws IOException {
		this(null, Files.createTempDirectory(null));
	}

	/**
	 * Creates a repository with the defined URI for the correspondence model and using the specified path for storing
	 * metadata. The URI must be resolvable when performing load or save operations, or can be {@code null} is no
	 * persistence of the correspondence model shall be managed.
	 * @param correspondencesURI the URI of the correspondence model to use for saving and loading it, must be {@code null}
	 * or valid
	 * @param consistencyMetadataFolder the folder to store consistency metadata in, must not be {@code null}
	 */
	public DefaultChangeRecordingModelRepository(URI correspondencesURI, Path consistencyMetadataFolder) {
		this.consistencyMetadataFolder = consistencyMetadataFolder;
		this.modelsResourceSet = withGlobalFactories(new ResourceSetImpl());
		this.correspondenceModel = createCorrespondenceModel(correspondencesURI);
		this.modelsResourceSet.eAdapters().add(new ResourceRegistrationAdapter((resource) -> {
			if (!isLoading) {
				getCreateOrLoadModel(resource.getURI());
			}
		}));
		this.changeRecorder = new ChangeRecorder(modelsResourceSet);
	}

	@Override
	public void loadExistingModels() {
		isLoading = true;
		correspondenceModel.loadSerializedCorrespondences(modelsResourceSet);
		isLoading = false;
	}

	@Override
	public CorrespondenceModel getCorrespondenceModel() {
		return correspondenceModel.getGenericView();
	}

	@Override
	public URI getMetadataModelURI(String... metadataKey) {
		Path metadataPath = consistencyMetadataFolder.resolve(String.join("_", metadataKey));
		return URI.createFileURI(metadataPath.toString());
	}

	@Override
	public Resource getModelResource(URI uri) {
		return getCreateOrLoadModel(uri);
	}

	private Resource getCreateOrLoadModel(URI modelURI) {
		Resource existingResource = modelsResourceSet.getResource(modelURI, false);
		if (existingResource != null) {
			return existingResource;
		} else {
			return createOrLoadModelResource(modelURI);
		}
	}

	private Resource createOrLoadModelResource(URI modelResourceURI) {
		Resource resource;
		if ((modelResourceURI.isFile() || modelResourceURI.isPlatform())) {
			resource = getOrCreateResource(modelsResourceSet, modelResourceURI);
			// Only monitor modifiable models (file / platform URIs, not pathmap URIs)
			changeRecorder.addToRecording(resource);
		} else {
			resource = loadOrCreateResource(modelsResourceSet, modelResourceURI);
		}
		return resource;
	}

	@Override
	public void persistAsRoot(EObject rootEObject, URI uri) {
		addRoot(getCreateOrLoadModel(uri), (rootEObject));
	}

	private void addRoot(Resource resource, EObject root) {
		resource.getContents().add(root);
		resource.setModified(true);
		LOGGER.debug("Add root to resource: " + resource);
	}

	@Override
	public void saveOrDeleteModels() {
		Set<Resource> resourcesToDelete = new HashSet<>();
		for (Resource modelResource : modelsResourceSet.getResources()) {
			if (modelResource.getContents().isEmpty()) {
				resourcesToDelete.add(modelResource);
			} else {
				save(modelResource);
			}
		}
		resourcesToDelete.stream().forEach((resource) -> delete(resource));
		correspondenceModel.save();
	}

	private void delete(Resource resource) {
		try {
			resource.delete(null);
		} catch (IOException e) {
			LOGGER.error("Deletion of resource " + resource + " did not work.", e);
			throw new IllegalStateException("Could not delete URI " + resource.getURI(), e);
		}
	}

	private void save(Resource resource) {
		if (!resource.isModified()) {
			return;
		}
		LOGGER.debug("Save resource: " + resource);
		try {
			resource.save(null);
			resource.setModified(false);
		} catch (IOException e) {
			LOGGER.error("Model could not be saved: " + resource.getURI(), e);
			throw new IllegalStateException("Could not save URI " + resource.getURI(), e);
		}
	}

	@Override
	public Iterable<TransactionalChange> recordChanges(Runnable changeApplicator) {
		changeRecorder.beginRecording();
		LOGGER.debug("Start recording changes");
		changeApplicator.run();
		LOGGER.debug("End recording changes");
		changeRecorder.endRecording();
		return List.of(changeRecorder.getChange());
	}

	@Override
	public VitruviusChange applyChange(VitruviusChange change) {
		return change.resolveAndApply(modelsResourceSet);
	}

	@Override
	public void close() throws Exception {
		changeRecorder.close();
		modelsResourceSet.getResources().stream().forEach((resource) -> resource.unload());
		modelsResourceSet.getResources().clear();
		correspondenceModel.close();
	}

}
