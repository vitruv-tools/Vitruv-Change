package tools.vitruv.change.atomic

import edu.kit.ipd.sdq.activextendannotations.Utility
import tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange
import tools.vitruv.change.atomic.feature.reference.RemoveEReference
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference
import tools.vitruv.change.atomic.root.InsertRootEObject
import tools.vitruv.change.atomic.root.RemoveRootEObject

/**
 * Static utility class for the EChange package and subpackages.
 */
@Utility
class EChangeUtil {
	static def dispatch isContainmentRemoval(EChange<?> change) {
		false
	}

	static def dispatch isContainmentRemoval(ReplaceSingleValuedEReference<?> change) {
		change.affectedFeature.containment &&
			((change.oldValue !== null && change.oldValue !== change.newValue))
	}

	static def dispatch isContainmentRemoval(RemoveEReference<?> change) {
		change.affectedFeature.containment
	}

	static def dispatch isContainmentRemoval(RemoveRootEObject<?> change) {
		true
	}

	static def dispatch isContainmentInsertion(EChange<?> change) {
		false
	}

	static def dispatch isContainmentInsertion(AdditiveReferenceEChange<?> change) {
		change.affectedFeature.containment && change.newValue !== null
	}

	static def dispatch isContainmentInsertion(RemoveRootEObject<?> change) {
		true
	}

	static def dispatch isContainmentInsertion(InsertRootEObject<?> change) {
		true
	}

}
