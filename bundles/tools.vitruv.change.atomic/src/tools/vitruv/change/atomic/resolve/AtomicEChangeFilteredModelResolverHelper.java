package tools.vitruv.change.atomic.resolve;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.function.Function;

import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.change.atomic.EChange;

//TODO nbr Brauch ich diese Klasse überhaupt?
public class AtomicEChangeFilteredModelResolverHelper<Source, Target> extends AtomicEChangeResolverHelper<Source, Target> {

	private AtomicEChangeFilteredModelResolverHelper(Function<Source, Target> elementResolver, Function<Resource, Resource> resourceResolver) {
		super(elementResolver, resourceResolver);
	}
	

	
}


