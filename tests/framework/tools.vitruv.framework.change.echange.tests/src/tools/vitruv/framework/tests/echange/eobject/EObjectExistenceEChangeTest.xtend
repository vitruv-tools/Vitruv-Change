package tools.vitruv.framework.tests.echange.eobject

import allElementTypes.Root
import tools.vitruv.framework.change.echange.eobject.CreateEObject
import tools.vitruv.framework.change.echange.eobject.EObjectExistenceEChange

import static extension tools.vitruv.framework.tests.echange.util.EChangeAssertHelper.*
import static extension tools.vitruv.framework.change.echange.resolve.EChangeResolverAndApplicator.*
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNotSame
import static org.junit.jupiter.api.Assertions.assertThrows

/**
 * Test class for the abstract class {@link EObjectExistenceEChange} EChange,
 * which creates or deletes a new EObject.
 */
class EObjectExistenceEChangeTest extends EObjectTest {
	/**
	 * Test resolves a {@link EObjectExistenceEChangeTest} EChange with 
	 * a new object which was not created yet. So the staging area will be filled.
	 */
	@Test
	def void resovlveBeforeTest() {
		// State before
		assertIsStateBefore

		// Create change
		val unresolvedChange = createUnresolvedChange(createdObject)
		unresolvedChange.assertIsNotResolved(createdObject)
		assertIsStateBefore

		// Resolve
		val resolvedChange = unresolvedChange.resolveBefore(uuidGeneratorAndResolver) as CreateEObject<Root>
		resolvedChange.assertIsResolved(createdObject)
		assertIsStateBefore
	}

	/**
	 * Test resolves a {@link EObjectExistenceEChangeTest} EChange with a
	 * new object which was already created and was put in the staging area.
	 */
	@Test
	def void resolveAfterTest() {
		// State before
		assertIsStateBefore

		// Create change
		val unresolvedChange = createUnresolvedChange(createdObject)
		unresolvedChange.assertIsNotResolved(createdObject)

		// Set state after
		assertIsStateAfter

		// Resolve
		val resolvedChange = unresolvedChange.resolveAfter(uuidGeneratorAndResolver) as CreateEObject<Root>
		resolvedChange.assertIsResolved(createdObject)

		// State after
		assertIsStateAfter
	}

	/**
	 * Test resolves a {@link EObjectExistenceEChangeTest} EChange with a null
	 * affected EObject.
	 */
	@Test
	def void resolveInvalidAffectedEObjectTest() {
		createdObject = null

		// Create change
		// val unresolvedChange = 
		assertThrows(IllegalArgumentException, [createUnresolvedChange(createdObject)])
//		assertFalse(unresolvedChange.isResolved)
//		
//		// Resolve
//		assertNull(unresolvedChange.resolveBefore(uuidGeneratorAndResolver) as CreateEObject<Root>)
//		assertNull(unresolvedChange.resolveAfter(uuidGeneratorAndResolver) as CreateEObject<Root>)		
	}

	/**
	 * Change is not resolved.
	 */
	def private static void assertIsNotResolved(EObjectExistenceEChange<Root> change, Root affectedEObject) {
		assertFalse(change.isResolved)
		assertNotSame(change.affectedEObject, affectedEObject)
	}

	/**
	 * Change is resolved.
	 */
	def private static void assertIsResolved(EObjectExistenceEChange<Root> change, Root affectedEObject) {
		assertTrue(change.isResolved)
		affectedEObject.assertEqualsOrCopy(change.affectedEObject)
	}

	/**
	 * Model is in state before the change.
	 */
	def private void assertIsStateBefore() {
	}

	/**
	 * Model is in state after the change.
	 */
	def private void assertIsStateAfter() {
	}

	/**
	 * Creates new unresolved change.
	 */
	def private EObjectExistenceEChange<Root> createUnresolvedChange(Root createdObject) {
		// The concrete change type CreateEObject will be used for the tests.
		return atomicFactory.createCreateEObjectChange(createdObject)
	}
}
