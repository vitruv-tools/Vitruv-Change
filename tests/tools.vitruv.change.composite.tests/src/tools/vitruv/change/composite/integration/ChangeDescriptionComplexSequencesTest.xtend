package tools.vitruv.change.composite.integration

import org.eclipse.emf.ecore.util.EcoreUtil
import org.junit.jupiter.api.Test
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest

import static allElementTypes.AllElementTypesPackage.Literals.*
import static tools.vitruv.testutils.metamodels.AllElementTypesCreators.*

import static extension tools.vitruv.change.composite.util.AtomicEChangeAssertHelper.*
import static extension tools.vitruv.change.composite.util.CompoundEChangeAssertHelper.*

class ChangeDescriptionComplexSequencesTest extends ChangeDescription2ChangeTransformationTest {

	/**
	 * Changes that overwrite each other between two change propagation triggers are not recognized by EMF.
	 */
	@Test
	def void testOverwritingSequence() {
		// prepare
		uniquePersistedRoot
		
		// test
		val nonRoot = aet.NonRoot
		val result = uniquePersistedRoot.record [
			val recordNonRoot = EcoreUtil.copy(nonRoot)
			singleValuedContainmentEReference = recordNonRoot
			singleValuedContainmentEReference = null
			singleValuedContainmentEReference = recordNonRoot
		]

		// assert
		result.assertChangeCount(5)
			.assertSetSingleValuedEReference(uniquePersistedRoot, ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, true, false)
			.assertReplaceSingleValuedEAttribute(nonRoot, IDENTIFIED__ID, null, nonRoot.id, false, false)
			.assertUnsetSingleValuedEReference(uniquePersistedRoot, ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, false, false)
			.assertSetSingleValuedEReference(uniquePersistedRoot, ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE, nonRoot, true, false, false)
			.assertEmpty
	}
	
	
	@Test
	def void testInsertTreeInContainment() {
		// prepare
		uniquePersistedRoot
		
		// test
		val nonRootObjectsContainer = aet.NonRootObjectContainerHelper
		val nonRoot = aet.NonRoot
		val result = uniquePersistedRoot.record [
			nonRootObjectContainerHelper = EcoreUtil.copy(nonRootObjectsContainer) => [
				nonRootObjectsContainment += EcoreUtil.copy(nonRoot)
			] 
		]
		
		// replay changes in copied elements
		nonRootObjectsContainer => [
			nonRootObjectsContainment += nonRoot
		]

		// assert
		result.assertChangeCount(6)
			.assertSetSingleValuedEReference(uniquePersistedRoot, ROOT__NON_ROOT_OBJECT_CONTAINER_HELPER, nonRootObjectsContainer, true, true, false)
			.assertReplaceSingleValuedEAttribute(nonRootObjectsContainer, IDENTIFIED__ID, null, nonRootObjectsContainer.id, false, false)
			.assertCreateAndInsertNonRoot(nonRootObjectsContainer, NON_ROOT_OBJECT_CONTAINER_HELPER__NON_ROOT_OBJECTS_CONTAINMENT, nonRoot, 0, false)
			.assertReplaceSingleValuedEAttribute(nonRoot, IDENTIFIED__ID, null, nonRoot.id, false, false)
			.assertEmpty
	}
	
	@Test
	def void testInsertComplexTreeInContainment() {
		// prepare
		uniquePersistedRoot
		
		// test
		val secondRoot = aet.Root
		val nonRootObjectsContainer = aet.NonRootObjectContainerHelper
		val nonRoot = aet.NonRoot
		val result = uniquePersistedRoot.record [
			val recordNonRoot = EcoreUtil.copy(nonRoot)
			recursiveRoot = EcoreUtil.copy(secondRoot) => [
				singleValuedNonContainmentEReference = recordNonRoot
				nonRootObjectContainerHelper = EcoreUtil.copy(nonRootObjectsContainer) => [
					nonRootObjectsContainment += recordNonRoot
				]

			]
		]
		
		// replay changes in copied elements
		secondRoot => [
			singleValuedNonContainmentEReference = nonRoot
			nonRootObjectContainerHelper = nonRootObjectsContainer => [
				nonRootObjectsContainment += nonRoot
			]
		]

		// assert
		result.assertChangeCount(10)
			.assertSetSingleValuedEReference(uniquePersistedRoot, ROOT__RECURSIVE_ROOT, secondRoot, true, true, false)
			.assertReplaceSingleValuedEAttribute(secondRoot, IDENTIFIED__ID, null, secondRoot.id, false, false)
			.assertSetSingleValuedEReference(secondRoot, ROOT__NON_ROOT_OBJECT_CONTAINER_HELPER, nonRootObjectsContainer, true, true, false)
			.assertReplaceSingleValuedEAttribute(nonRootObjectsContainer, IDENTIFIED__ID, null, nonRootObjectsContainer.id, false, false)
			.assertCreateAndInsertNonRoot(nonRootObjectsContainer, NON_ROOT_OBJECT_CONTAINER_HELPER__NON_ROOT_OBJECTS_CONTAINMENT, nonRoot, 0, false)
			.assertReplaceSingleValuedEAttribute(nonRoot, IDENTIFIED__ID, null, nonRoot.id, false, false)
			.assertReplaceSingleValuedEReference(secondRoot, ROOT__SINGLE_VALUED_NON_CONTAINMENT_EREFERENCE, null, nonRoot, false, false, false)
			.assertEmpty
	}

}
