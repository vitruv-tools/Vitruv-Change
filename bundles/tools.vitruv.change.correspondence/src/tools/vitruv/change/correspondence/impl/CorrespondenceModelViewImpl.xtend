package tools.vitruv.change.correspondence.impl

import java.util.List
import java.util.Set
import org.eclipse.emf.ecore.EObject
import java.util.function.Supplier
import tools.vitruv.change.correspondence.Correspondence
import tools.vitruv.change.correspondence.CorrespondenceModelView
import tools.vitruv.change.correspondence.InternalCorrespondenceModel
import tools.vitruv.change.correspondence.CorrespondenceModelViewFactory
import org.eclipse.xtend.lib.annotations.Accessors

class CorrespondenceModelViewImpl<T extends Correspondence> implements CorrespondenceModelView<T> {
	@Accessors(PROTECTED_GETTER)
	final InternalCorrespondenceModel correspondenceModelDelegate;
	@Accessors(PROTECTED_GETTER)
	final Class<T> correspondenceType;
	final Supplier<T> correspondenceCreator
	
	new(Class<T> correspondenceType, InternalCorrespondenceModel correspondenceModel) {
		this(correspondenceType, correspondenceModel, null)
	}

	new(Class<T> correspondenceType, InternalCorrespondenceModel correspondenceModel,
		Supplier<T> correspondenceCreator) {
		this.correspondenceType = correspondenceType;
		this.correspondenceModelDelegate = correspondenceModel;
		this.correspondenceCreator = correspondenceCreator
	}
	
	override addCorrespondenceBetween(List<EObject> eObjects1, List<EObject> eObjects2, String tag) {
		if (null === this.correspondenceCreator) {
			throw new UnsupportedOperationException("The current view is not able to create new correspondences")
		}
		correspondenceModelDelegate.createAndAddCorrespondence(eObjects1, eObjects2, tag, this.correspondenceCreator)
	}

	override hasCorrespondences(List<EObject> eObjects) {
		var Set<T> correspondences = this.getCorrespondences(eObjects);
		return correspondences !== null && correspondences.size() > 0
	}

	override hasCorrespondences() {
		correspondenceModelDelegate.hasCorrespondences();
	}
	
	override getCorrespondences(List<EObject> eObjects) {
		correspondenceModelDelegate.getCorrespondences(correspondenceType, eObjects, null).toSet();
	}

	override getCorrespondingEObjects(List<EObject> eObjects) {
		correspondenceModelDelegate.getCorrespondingEObjects(correspondenceType, eObjects, null);
	}

	override <C extends EObject> Set<List<C>> getCorrespondingEObjects(List<EObject> eObjects, Class<C> type, String tag) {
		val correspondingObjects = correspondenceModelDelegate.getCorrespondingEObjects(correspondenceType, eObjects, tag);
		correspondingObjects.filter[it.forall[type.isInstance(it)]].map[it.filter(type).toList].toSet;
	}

	override removeCorrespondencesBetween(List<EObject> aEObjects, List<EObject> bEObjects, String tag) {
		correspondenceModelDelegate.removeCorrespondencesBetween(correspondenceType, aEObjects, bEObjects, tag);
	}

	override <V extends CorrespondenceModelView<?>> getView(CorrespondenceModelViewFactory<V> correspondenceModelViewFactory) {
		correspondenceModelDelegate.getView(correspondenceModelViewFactory);
	}
	
	override <V extends CorrespondenceModelView<?>> getEditableView(CorrespondenceModelViewFactory<V> correspondenceModelViewFactory) {
		correspondenceModelDelegate.getEditableView(correspondenceModelViewFactory)
	}
	
	override getGenericView() {
		correspondenceModelDelegate.genericView;
	}
		
}
