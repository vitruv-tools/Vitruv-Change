package tools.vitruv.change.changederivation;

import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.loadOrCreateResource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;

public class PathMapAwareDeltaBasedResourceFactory implements Resource.Factory {
	@Override
	public Resource createResource(URI uri) {
		if(uri.toString().contains("pathmap")) {
			ResourceSet stateBasedResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
			Resource stateBasedResource = loadOrCreateResource(stateBasedResourceSet, uri);
			DeltaBasedResource deltaBasedResource = new DeltaBasedResource(uri);
			
			deltaBasedResource.getContents().addAll(EcoreUtil.copyAll(stateBasedResource.getContents()));
			
			deltaBasedResource.setModified(false);
			deltaBasedResource.markAsLoaded();
			return deltaBasedResource;
		}
		return new DeltaBasedResource(uri);
	}
}
