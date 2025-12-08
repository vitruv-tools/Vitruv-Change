package tools.vitruv.change.changederivation.persistence;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;

/**
 * A utility class, which supports in the persistence of deltas / changes of models.
 */
public final class DeltaPersistence {
    private DeltaPersistence() {}

    /**
     * Saves a model in an EMF {@see Resource} in the form of atomic {@see EChange}s in a given file.
     * When loaded and applied, the changes simulate the creation of the given model
     * and result in the same model state as given in the EMF Resource.
     * 
     * @param resource the EMF Resource with the model to store.
     * @param storage path to the file, in which the changes are stored.
     * @throws IOException if the changes cannot be stored.
     */
    public static void saveResourceAsChanges(Resource resource, Path storage) throws IOException {
        List<EChange<HierarchicalId>> changes = new DefaultStateBasedChangeResolutionStrategy()
            .getChangeSequenceForCreated(resource)
            .getEChanges();
        saveChanges(changes, storage);
    }

    /**
     * Saves a list of {@see EChange}s in a file.
     * 
     * @param changes the list of changes to store.
     * @param deltaFile path to the file, in which the changes are stored.
     * @throws IOException if the changes cannot be stored.
     */
    public static void saveChanges(List<EChange<HierarchicalId>> changes, Path deltaFile) throws IOException {
        ResourceSet resourceSet = withGlobalFactories(new ResourceSetImpl());

        Resource deltaResource = resourceSet.createResource(URI.createFileURI(deltaFile.toAbsolutePath().toString()));
        deltaResource.getContents().addAll(changes);
        
        Map<String, String> outputOptions = new HashMap<>();
		outputOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
        
        deltaResource.save(outputOptions);
    }
}
