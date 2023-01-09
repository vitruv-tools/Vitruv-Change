package tools.vitruv.change.composite.util

import edu.kit.ipd.sdq.activextendannotations.Utility
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.resource.Resource
import tools.vitruv.change.atomic.EChange

import static extension tools.vitruv.change.composite.util.AtomicEChangeAssertHelper.*
import static extension tools.vitruv.change.composite.util.ChangeAssertHelper.*

@Utility
class CompoundEChangeAssertHelper {
	def static Iterable<? extends EChange> assertCreateAndInsertNonRoot(
			Iterable<? extends EChange> changes, EObject affectedEObject, EStructuralFeature affectedFeature, EObject expectedNewValue, int expectedIndex, boolean wasUnset) {
		changes.assertSizeGreaterEquals(2)
		return changes.assertCreateEObject(expectedNewValue)
			.assertInsertEReference(affectedEObject, affectedFeature, expectedNewValue,	expectedIndex, true, wasUnset)
	}
	
	def static Iterable<? extends EChange> assertCreateAndReplaceNonRoot(Iterable<? extends EChange> changes, EObject expectedNewValue,
		EObject affectedEObject, EStructuralFeature affectedFeature, boolean wasUnset) {
		assertCreateAndReplaceNonRoot(changes, expectedNewValue, affectedEObject, null, affectedFeature, wasUnset)
	}
	
	def static Iterable<? extends EChange> assertCreateAndReplaceNonRoot(Iterable<? extends EChange> changes, EObject expectedNewValue,
		EObject affectedEObject, EObject expectedOldValue, EStructuralFeature affectedFeature, boolean wasUnset) {
		changes.assertSizeGreaterEquals(2)
		return changes.assertCreateEObject(expectedNewValue)
			.assertReplaceSingleValuedEReference(affectedEObject, affectedFeature, expectedOldValue, expectedNewValue, true, wasUnset, false)
	}
	
	def static Iterable<? extends EChange> assertReplaceAndDeleteNonRoot(Iterable<? extends EChange> changes, EObject expectedOldValue,
		EObject affectedEObject, EStructuralFeature affectedFeature, boolean isUnset) {
		return changes
			.assertReplaceSingleValuedEReference(affectedEObject, affectedFeature, expectedOldValue, null, true, false, isUnset)
			.assertDeleteEObjectAndContainedElements(expectedOldValue)
	}
	
	def static Iterable<? extends EChange> assertDeleteEObjectAndContainedElements(Iterable<? extends EChange> changes, EObject expectedOldValue) {
		val deletedContainedElements = expectedOldValue.eAllContents.toList.reverse // elements are deleted from inner to outer
		changes.assertSizeGreaterEquals(1 + deletedContainedElements.size)
		var filteredChanges = changes
		for (deletedContainedElement : deletedContainedElements) {
			filteredChanges = filteredChanges.assertDeleteEObject(deletedContainedElement)
		}
		return filteredChanges.assertDeleteEObject(expectedOldValue)
	}

	def static Iterable<? extends EChange> assertCreateAndInsertRootEObject(Iterable<? extends EChange> changes, EObject expectedNewValue, String uri, Resource resource) {
		changes.assertSizeGreaterEquals(2)
		return changes.assertCreateEObject(expectedNewValue)
			.assertInsertRootEObject(expectedNewValue, uri, resource)
	}

	def static Iterable<? extends EChange> assertRemoveAndDeleteRootEObject(Iterable<? extends EChange> changes, EObject expectedOldValue, String uri, Resource resource) {
		changes.assertSizeGreaterEquals(2)
		return changes.assertRemoveRootEObject(expectedOldValue, uri, resource)
			.assertDeleteEObjectAndContainedElements(expectedOldValue)
	}
}
		