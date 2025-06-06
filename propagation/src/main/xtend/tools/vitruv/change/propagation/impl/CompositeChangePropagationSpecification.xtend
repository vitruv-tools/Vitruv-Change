package tools.vitruv.change.propagation.impl

import java.util.List
import java.util.ArrayList
import tools.vitruv.change.interaction.UserInteractor
import tools.vitruv.change.propagation.ChangePropagationSpecification
import org.apache.logging.log4j.LogManager
import tools.vitruv.change.propagation.ChangePropagationObserver
import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.utils.ResourceAccess
import org.eclipse.xtend.lib.annotations.Accessors
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.composite.MetamodelDescriptor
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView
import tools.vitruv.change.correspondence.Correspondence

class CompositeChangePropagationSpecification extends AbstractChangePropagationSpecification implements ChangePropagationObserver {
	static val logger = LogManager.getLogger(CompositeChangePropagationSpecification);

	@Accessors(PROTECTED_GETTER)
	val List<ChangePropagationSpecification> changePreprocessors;
	@Accessors(PROTECTED_GETTER)
	val List<ChangePropagationSpecification> changeMainprocessors;

	new(MetamodelDescriptor sourceMetamodel, MetamodelDescriptor targetMetamodel) {
		super(sourceMetamodel, targetMetamodel);
		changePreprocessors = new ArrayList<ChangePropagationSpecification>();
		changeMainprocessors = new ArrayList<ChangePropagationSpecification>();
	}

	/** 
	 * Adds the specified change processor as a preprocessor, which is executed before the mainprocessors.
	 * The preprocessors are executed in the order in which they are added.
	 */
	protected def addChangePreprocessor(ChangePropagationSpecification changePropagationSpecifcation) {
		assertMetamodelsCompatible(changePropagationSpecifcation);
		changePreprocessors += changePropagationSpecifcation;
		changePropagationSpecifcation.userInteractor = userInteractor;
		changePropagationSpecifcation.registerObserver(this);
	}

	/** 
	 * Adds the specified change processor as a main processor, which is executed after the preprocessors.
	 * The main processors are executed in the order in which they are added.
	 */
	protected def addChangeMainprocessor(ChangePropagationSpecification changePropagationSpecifcation) {
		assertMetamodelsCompatible(changePropagationSpecifcation);
		changeMainprocessors += changePropagationSpecifcation;
		changePropagationSpecifcation.userInteractor = userInteractor;
		changePropagationSpecifcation.registerObserver(this);
	}

	private def void assertMetamodelsCompatible(ChangePropagationSpecification potentialChangeProcessor) {
		if (!this.sourceMetamodelDescriptor.equals(potentialChangeProcessor.sourceMetamodelDescriptor) ||
			!this.targetMetamodelDescriptor.equals(potentialChangeProcessor.targetMetamodelDescriptor)) {
			throw new IllegalArgumentException("ChangeProcessor metamodels are not compatible");
		}
	}

	override propagateChange(EChange<EObject> change, EditableCorrespondenceModelView<Correspondence> correspondenceModel, ResourceAccess resourceAccess) {
		propagateChangeViaPreprocessors(change, correspondenceModel, resourceAccess);
		propagateChangeViaMainprocessors(change, correspondenceModel, resourceAccess);
	}

	protected def propagateChangeViaPreprocessors(EChange<EObject> change, EditableCorrespondenceModelView<Correspondence> correspondenceModel,
		ResourceAccess resourceAccess) {
		for (changeProcessor : changePreprocessors) {
			logger.trace('''Calling change preprocessor «changeProcessor» for change event «change»''');
			changeProcessor.propagateChange(change, correspondenceModel, resourceAccess);
		}
	}

	protected def propagateChangeViaMainprocessors(EChange<EObject> change, EditableCorrespondenceModelView<Correspondence> correspondenceModel,
		ResourceAccess resourceAccess) {
		for (changeProcessor : changeMainprocessors) {
			logger.trace('''Calling change mainprocessor «changeProcessor» for change event «change»''');
			changeProcessor.propagateChange(change, correspondenceModel, resourceAccess);
		}
	}

	override doesHandleChange(EChange<EObject> change, EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		for (changeProcessor : allProcessors) {
			if (changeProcessor.doesHandleChange(change, correspondenceModel)) {
				return true;
			}
		}
		return false;
	}

	override setUserInteractor(UserInteractor userInteractor) {
		super.setUserInteractor(userInteractor)
		for (changeProcessor : allProcessors) {
			changeProcessor.setUserInteractor(userInteractor);
		}
	}

	private def getAllProcessors() {
		val processors = new ArrayList<ChangePropagationSpecification>();
		// processor arrays can be null when calling setUserInteractor from the super constructor
		if(changePreprocessors !== null) processors += changePreprocessors;
		if(changeMainprocessors !== null) processors += changeMainprocessors;
		return processors;
	}

	override objectCreated(EObject createdObject) {
		notifyObjectCreated(createdObject)
	}

}
