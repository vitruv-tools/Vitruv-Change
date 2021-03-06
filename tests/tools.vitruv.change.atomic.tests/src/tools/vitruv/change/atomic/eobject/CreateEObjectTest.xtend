package tools.vitruv.change.atomic.eobject

import allElementTypes.Root
import tools.vitruv.change.atomic.eobject.CreateEObject

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

/**
 * Test class for the concrete {@link CreateEObject} EChange,
 * which creates a new EObject and puts it in the staging area.
 */
class CreateEObjectTest extends EObjectTest {
	@BeforeEach
	def void beforeTest() {
		prepareStateBefore
	}

	/**
	 * Tests whether resolving the {@link CreateEObjectTest} EChange returns
	 * the same class.
	 */
	@Test
	def void resolveToCorrectType() {
		// Create change
		val unresolvedChange = createUnresolvedChange(createdObject)

		// Resolve		
		val resolvedChange = unresolvedChange.resolveBefore
		unresolvedChange.assertDifferentChangeSameClass(resolvedChange)
	}

	/**
	 * Tests applying a {@link CreateEObject} EChange forward by creating a
	 * new EObject and putting it in the staging area.
	 */
	@Test
	def void applyForwardTest() {
		// Create change and resolve
		val resolvedChange = createUnresolvedChange(createdObject).resolveBefore as CreateEObject<Root>

		// Apply forward
		resolvedChange.assertApplyForward

		// State after
		assertIsStateAfter(createdObject)

		// Now another change would take the object and inserts it in a resource
		prepareStateBefore

		// Create change and resolve 2
		val resolvedChange2 = createUnresolvedChange(createdObject2).resolveBefore as CreateEObject<Root>

		// Apply forward 2
		resolvedChange2.assertApplyForward

		// State after
		assertIsStateAfter(createdObject2)
	}

	/**
	 * Tests applying a {@link CreateEObject} EChange backward 
	 * by removing a newly created object from the staging area.
	 */
	@Test
	def void applyBackwardTest() {
		// Create change, resolve it and apply it forward
		val resolvedChange = createUnresolvedChange(createdObject).resolveBefore as CreateEObject<Root>
		resolvedChange.assertApplyForward

		// Apply backward
		resolvedChange.assertApplyBackward
	}

	/**
	 * Sets the state of the model before the change.
	 */
	def private void prepareStateBefore() {
		assertIsStateBefore
	}

	/**
	 * Model is in state before the change.
	 */
	def private void assertIsStateBefore() {
	}

	/**
	 * Model is in state after the change.
	 */
	def private void assertIsStateAfter(Root object) {
	}

	/**
	 * Creates new unresolved change.
	 */
	def private CreateEObject<Root> createUnresolvedChange(Root newObject) {
		return atomicFactory.createCreateEObjectChange(newObject)
	}

}
