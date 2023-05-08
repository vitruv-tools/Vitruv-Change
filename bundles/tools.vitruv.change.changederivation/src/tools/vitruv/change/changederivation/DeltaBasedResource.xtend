package tools.vitruv.change.changederivation

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Collections
import java.util.List
import java.util.Map
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.id.IdResolver
import tools.vitruv.change.composite.description.VitruviusChangeFactory
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories

class DeltaBasedResource extends ResourceImpl {
	
	new(URI uri) {
		super(uri)
	}
	
	private static def List<EChange> loadDeltas(URI modelUri) {
        val resourceSet = withGlobalFactories(new ResourceSetImpl());
        val resource = resourceSet.getResource(modelUri, true);
        return resource.getContents().map[it as EChange].toList
	}
	
	override doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		val deltas = loadDeltas(this.URI)
		VitruviusChangeFactory.getInstance().createTransactionalChange(deltas).resolveAndApply(IdResolver.create(this.resourceSet))
	}
	
	override doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		val deltaChanges = new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(this).EChanges;
		val resourceSet = withGlobalFactories(new ResourceSetImpl());
		val resource = resourceSet.createResource(this.URI);
		resource.getContents().addAll(deltaChanges)
		try (val out = outputStream){
			resource.save(out, Collections.EMPTY_MAP)
		}
	}
}
