package tools.vitruv.change.atomic.resolve

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.feature.reference.InsertEReference
import tools.vitruv.change.atomic.feature.reference.RemoveEReference
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference
import tools.vitruv.change.atomic.root.InsertRootEObject
import tools.vitruv.change.atomic.root.RemoveRootEObject
import tools.vitruv.change.atomic.root.RootEChange
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.eobject.DeleteEObject
import tools.vitruv.change.atomic.eobject.CreateEObject
import edu.kit.ipd.sdq.activextendannotations.Utility

/**
 * Utility class to unresolve a given EChange.
 */
@Utility
package class EChangeUnresolver {
	/**
	 * Unresolves the attributes of the {@link RootEChange} class.
	 * @param The RootEChange.
	 */
	def static unresolveRootEChange(RootEChange change) {
		change.resource = null
	}
	
	/**
	 * Unresolves the attributes of the {@link FeatureEChange} class.
	 * @param The FeatureEChange.
	 */
	def static <A extends EObject, F extends EStructuralFeature> void unresolveFeatureEChange(FeatureEChange<A,F> change) {
		change.affectedEObject = null
	}
	
	/** 
	 * Unresolves the attributes of the {@link EObjectAddedEChange} class.
	 * @param The EObjectAddedEChange.
	 */
	def static <T extends EObject> void unresolveEObjectAddedEChange(EObjectAddedEChange<T> change) {
		change.newValue = null
	}	

	/** 
	 * Unresolves the attributes of the {@link EObjectExistenceEChange} class.
	 * @param The EObjectExistenceEChange.
	 */	
	def static <T extends EObject> void unresolveEObjectExistenceEChange(EObjectExistenceEChange<T> change) {
		change.affectedEObject = null
	}
	
	/** 
	 * Unresolves the attributes of the {@link EObjectSubtractedEChange} class.
	 * @param The EObjectSubtractedEChange.
	 */	
	def static <T extends EObject> void unresolveEObjectSubtractedEChange(EObjectSubtractedEChange<T> change) {
		change.oldValue = null
	}

	def dispatch static void unresolve(EChange change) {
		// Do nothing
	}

	/**
	 * Dispatch method for {@link InsertRootEObject} to unresolve it.
	 * @param change The InsertRootEObject.
	 */	
	def dispatch static void unresolve(InsertRootEObject<EObject> change) {
		change.unresolveRootEChange
		change.newValue = null
	}	
	/**
	 * Dispatch method for {@link RemoveRootEObject} to unresolve it.
	 * @param change The RemoveRootEObject.
	 */
	def dispatch static void unresolve(RemoveRootEObject<EObject> change) {
		change.unresolveRootEChange
		change.oldValue = null
	}
	/**
	 * Dispatch method for {@link CreateEObject} to unresolve it.
	 * @param change The CreateEObject change.
	 */	
	def dispatch static void unresolve(CreateEObject<EObject> change) {
		change.unresolveEObjectExistenceEChange
	}
	/**
	 * Dispatch method for {@link DeleteEObject} to unresolve it.
	 * @param change The DeleteEObject change.
	 */	
	def dispatch static void unresolve(DeleteEObject<EObject> change) {
		change.unresolveEObjectExistenceEChange
	}
	
	/**
	 * Dispatch method for {@link ReplaceSingleValuedEReference} to unresolve it.
	 * @param change The ReplaceSingleValuedEReference.
	 */	
	def dispatch static void unresolve(ReplaceSingleValuedEReference<EObject, EObject> change) {
		change.unresolveEObjectAddedEChange
		change.unresolveEObjectSubtractedEChange
		change.unresolveFeatureEChange
	}
	
	/**
	 * Dispatch method for {@link InsertEReference} to unresolve it.
	 * @param change The InsertEReference.
	 */	
	def dispatch static void unresolve(InsertEReference<EObject, EObject> change) {
		change.unresolveEObjectAddedEChange
		change.unresolveFeatureEChange		
	}
	
	/**
	 * Dispatch method for {@link RemoveEReference} to unresolve it.
	 * @param change The RemoveEReference.
	 */	
	def dispatch static void unresolve(RemoveEReference<EObject, EObject> change) {
		change.unresolveEObjectSubtractedEChange
		change.unresolveFeatureEChange		
	}
	
	/**
	 * Dispatch method for {@link FeatureEChange} to unresolve it.
	 * @param change The FeatureEChange.
	 */
	def dispatch static void unresolve(FeatureEChange<EObject, EStructuralFeature> change) {
		change.unresolveFeatureEChange
	}
	
}