package tools.vitruv.change.propagation.impl

import tools.vitruv.change.interaction.UserInteractor
import tools.vitruv.change.propagation.ChangePropagationSpecification
import tools.vitruv.change.propagation.ChangePropagationObserver
import java.util.List
import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.composite.MetamodelDescriptor

abstract class AbstractChangePropagationSpecification implements ChangePropagationSpecification {
	val List<ChangePropagationObserver> propagationObservers;
	var UserInteractor userInteractor;
	var MetamodelDescriptor sourceMetamodelDescriptor;
	var MetamodelDescriptor targetMetamodelDescriptor;

	new(MetamodelDescriptor sourceMetamodelDescriptor, MetamodelDescriptor targetMetamodelDescriptor) {
		this.sourceMetamodelDescriptor = sourceMetamodelDescriptor
		this.targetMetamodelDescriptor = targetMetamodelDescriptor
		this.propagationObservers = newArrayList();
	}

	protected def UserInteractor getUserInteractor() {
		return userInteractor;
	}

	override getSourceMetamodelDescriptor() {
		return sourceMetamodelDescriptor;
	}

	override getTargetMetamodelDescriptor() {
		return targetMetamodelDescriptor;
	}

	override setUserInteractor(UserInteractor userInteractor) {
		this.userInteractor = userInteractor;
	}

	override registerObserver(ChangePropagationObserver observer) {
		if (observer !== null) {
			this.propagationObservers += observer;
		}
	}

	override deregisterObserver(ChangePropagationObserver observer) {
		this.propagationObservers -= observer;
	}

	override notifyObjectCreated(EObject createdObject) {
		this.propagationObservers.forEach[it.objectCreated(createdObject)];
	}

}
