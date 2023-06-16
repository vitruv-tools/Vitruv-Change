package tools.vitruv.change.changederivation;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import org.eclipse.emf.ecore.resource.ResourceSet;

@Utility
public class DeltaBasedResourceUtil {
	
	private DeltaBasedResourceUtil() {}
	
	public static ResourceSet withDeltaFactory(ResourceSet resourceSet) {
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new PathMapAwareDeltaBasedResourceFactory());
		return resourceSet;
	}
	
}
 