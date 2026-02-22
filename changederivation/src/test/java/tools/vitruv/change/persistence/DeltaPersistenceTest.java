package tools.vitruv.change.persistence;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tools.vitruv.change.atomic.AtomicPackage;
import tools.vitruv.change.changederivation.persistence.DeltaBasedResource;
import tools.vitruv.change.changederivation.persistence.DeltaPersistence;

public class DeltaPersistenceTest {
    @BeforeAll
    public static void setUp() {
        ResourceFactoryRegistryImpl.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        EPackage.Registry.INSTANCE.put(AtomicPackage.eNS_URI, AtomicPackage.eINSTANCE);
    }

    @Disabled
    @Test
    public void testStoreAndLoadAtomicChangeMetamodel() throws Exception {
        Path targetPath = Paths.get("target", "atomic.xmi").toAbsolutePath();

        ResourceSet set = new ResourceSetImpl();
        Resource resourceToStore = set.createResource(URI.createFileURI("temp.xmi"));
        resourceToStore.getContents().add(AtomicPackage.eINSTANCE);

        DeltaPersistence.saveResourceAsChanges(resourceToStore, targetPath);

        DeltaBasedResource loadedDeltaResource = new DeltaBasedResource(URI.createFileURI(targetPath.toString()));
        loadedDeltaResource.load(null);
        // assert: content of loadedDeltaResource should be now the same as AtomicPackage.eINSTANCE
    }
}
