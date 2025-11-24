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

public class DeltaPersistance {
    public static void saveResourceAsChanges(Resource resource, Path storage) throws IOException {
        List<EChange<HierarchicalId>> changes = new DefaultStateBasedChangeResolutionStrategy()
            .getChangeSequenceForCreated(resource)
            .getEChanges();
        saveChanges(changes, storage);
    }

    public static void saveChanges(List<EChange<HierarchicalId>> changes, Path deltaFile) throws IOException {
        ResourceSet resourceSet = withGlobalFactories(new ResourceSetImpl());

        Resource deltaResource = resourceSet.createResource(URI.createFileURI(deltaFile.toAbsolutePath().toString()));
        deltaResource.getContents().addAll(changes);
        
        Map<String, String> outputOptions = new HashMap<>();
		outputOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
        
        deltaResource.save(outputOptions);
    }
}
