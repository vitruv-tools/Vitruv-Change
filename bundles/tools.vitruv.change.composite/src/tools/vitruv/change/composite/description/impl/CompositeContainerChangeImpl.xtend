package tools.vitruv.change.composite.description.impl

import java.util.List
import tools.vitruv.change.atomic.uuid.UuidResolver
import tools.vitruv.change.composite.description.CompositeContainerChange
import tools.vitruv.change.composite.description.VitruviusChange

import static extension edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil.*
import tools.vitruv.change.atomic.id.IdResolver

class CompositeContainerChangeImpl extends AbstractCompositeChangeImpl<VitruviusChange> implements CompositeContainerChange {
	new(List<? extends VitruviusChange> changes) {
		super(changes)
	}
	
	override CompositeContainerChangeImpl copy() {
		new CompositeContainerChangeImpl(changes.mapFixed [copy()])
	}
	
	override resolveAndApply(UuidResolver uuidResolver) {
		new CompositeContainerChangeImpl(changes.mapFixed[resolveAndApply(uuidResolver)])
	}
	
	override resolveAndApply(IdResolver idResolver) {
		new CompositeContainerChangeImpl(changes.mapFixed[resolveAndApply(idResolver)])
	}
	
	override unresolve() {
		new CompositeContainerChangeImpl(changes.mapFixed[unresolve()])
	}
	
}