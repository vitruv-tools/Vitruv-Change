package tools.vitruv.change.composite.description.impl

import tools.vitruv.change.composite.description.CompositeContainerChange
import tools.vitruv.change.composite.description.VitruviusChange
import static extension edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil.*
import java.util.List
import org.eclipse.emf.ecore.resource.ResourceSet

class CompositeContainerChangeImpl extends AbstractCompositeChangeImpl<VitruviusChange> implements CompositeContainerChange {
	new(List<? extends VitruviusChange> changes) {
		super(changes)
	}
	
	override CompositeContainerChangeImpl copy() {
		new CompositeContainerChangeImpl(changes.mapFixed [copy()])
	}
	
	override resolveAndApply(ResourceSet resourceSet) {
		new CompositeContainerChangeImpl(changes.mapFixed[resolveAndApply(resourceSet)])
	}
	
	override unresolve() {
		new CompositeContainerChangeImpl(changes.mapFixed[unresolve()])
	}
	
}