package tools.vitruv.change.atomic;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Static utility class for the EChange package and subpackages.
 */
@Utility
public class EChangeUtil {
	/**
	 * Private constructor for utility class.
	 */
	private EChangeUtil() {
	}

	public static boolean isContainmentRemoval(final EChange<?> change) {
		if (change instanceof ReplaceSingleValuedEReference<?> replaceReference) {
			return replaceReference.getAffectedFeature().isContainment() &&
				((replaceReference.getOldValue() != null && replaceReference.getOldValue() != replaceReference.getNewValue()));
		}
		if (change instanceof RemoveEReference<?> removeReference) {
			return removeReference.getAffectedFeature().isContainment();
		}
		return change instanceof RemoveRootEObject;
	}

	public static boolean isContainmentInsertion(final EChange<?> change) {
		if (change instanceof AdditiveReferenceEChange<?> additiveReferenceChange) {
			return additiveReferenceChange.getAffectedFeature().isContainment() && additiveReferenceChange.getNewValue() != null;  
		}
		return change instanceof RemoveRootEObject || change instanceof InsertRootEObject;
	}
}
