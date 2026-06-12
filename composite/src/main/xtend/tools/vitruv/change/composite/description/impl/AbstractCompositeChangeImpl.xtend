package tools.vitruv.change.composite.description.impl

import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.List
import java.util.Map
import java.util.Optional
import java.util.Set
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.composite.MetamodelDescriptor
import tools.vitruv.change.composite.description.CompositeChange
import tools.vitruv.change.composite.description.VitruviusChange

import static extension edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil.*

abstract class AbstractCompositeChangeImpl<Element, ContanedChange extends VitruviusChange<Element>> implements CompositeChange<Element, ContanedChange> {
	List<ContanedChange> changes
	protected val Map<Class<?>, Object> annotations = new HashMap()

	new(List<? extends ContanedChange> changes) {
		this.changes = List.copyOf(changes)
	}

	override List<ContanedChange> getChanges() {
		return this.changes
	}

	override containsConcreteChange() {
		changes.exists [containsConcreteChange]
	}
	
	override getChangedURIs() {
		changes.flatMapFixedTo(new LinkedHashSet) [changedURIs]
	}
	
	override Set<MetamodelDescriptor> getAffectedEObjectsMetamodelDescriptors() {
		changes.flatMapFixedTo(new HashSet) [affectedEObjectsMetamodelDescriptors]
	}

	override List<EChange<Element>> getEChanges() {
		return changes.flatMapFixed [EChanges]
	}
	
	override getAffectedEObjects() {
		changes.flatMapFixedTo(new LinkedHashSet) [affectedEObjects]
	}
	
	override getAffectedAndReferencedEObjects() {
		changes.flatMapFixedTo(new LinkedHashSet) [affectedAndReferencedEObjects]
	}

	override getUserInteractions() {
		return changes.flatMap [userInteractions]
	}

	override <T> setAnnotation(Class<T> type, T value) {
		annotations.put(type, value)
	}

	override <T> getAnnotation(Class<T> type) {
		Optional.ofNullable(type.cast(annotations.get(type)))
	}

	override getAnnotations() {
		Collections.unmodifiableMap(annotations)
	}

	override toString() {
		if (changes.isEmpty) '''«class.simpleName» (empty)'''
		else '''
			«class.simpleName»: [
				«FOR change : changes»
					«change»
				«ENDFOR»
			]
			'''
	}

	/**
	 * Indicates whether some other object is "equal to" this composite change.
	 * This means it is a composite change which contains the same changes as this one in no particular order.
	 * @param other is the object to compare with.
	 * @return true, if the object is a composite change and has the same changes in any order.
	 */
	override equals(Object other) {
		if (other === this) true
		else if (other === null) false
		else if (other instanceof CompositeChange<?, ?>) {
			changes == other.changes
		}
		else false
	}
	
	override hashCode() {
		changes.hashCode()
	}
}
