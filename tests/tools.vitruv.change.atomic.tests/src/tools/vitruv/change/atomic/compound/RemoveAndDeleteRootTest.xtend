package tools.vitruv.change.atomic.compound

import allElementTypes.Root
import java.util.List
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.EChangeTest
import tools.vitruv.change.atomic.eobject.DeleteEObject
import tools.vitruv.change.atomic.root.RemoveRootEObject
import tools.vitruv.change.atomic.root.RootEChangeTest

import static org.hamcrest.MatcherAssert.assertThat
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue
import static tools.vitruv.change.atomic.util.EChangeAssertHelper.*
import static tools.vitruv.testutils.matchers.ModelMatchers.equalsDeeply

/**
 * Test class for the concrete {@link RemoveAndDeleteRoot} EChange,
 * which removes a root element from a resource and deletes it.
 */
class RemoveAndDeleteRootTest extends RootEChangeTest {
	@BeforeEach
	override void beforeTest() {
		super.beforeTest()
		prepareStateBefore

	}

	/**
	 * Resolves the {@link RemoveAndDeleteRoot} EChange. The model is in state
	 * before the change is applied, so the staging area is empty and the root 
	 * object is in the resource.
	 */
	@Test
	def void resolveBeforeTest() {
		// Create change
		val unresolvedChange = createUnresolvedChange(newRootObject, 1)
		unresolvedChange.assertIsNotResolved

		// Resolve
		val resolvedChange = unresolvedChange.resolveBefore
		resolvedChange.assertIsResolved(newRootObject)

		// Resolving applies all changes and reverts them, so the model should be unaffected.		
		assertIsStateBefore
	}

	/**
	 * Tests whether resolving the {@link RemoveAndDeleteRoot} EChange
	 * returns the same class.
	 */
	@Test
	def void resolveToCorrectType() {
		// Create change
		val unresolvedChange = createUnresolvedChange(newRootObject, 1)

		// Resolve		
		val resolvedChange = unresolvedChange.resolveBefore
		unresolvedChange.assertDifferentChangeSameClass(resolvedChange)
	}

	/**
	 * Tests the {@link RemoveAndDeleteRoot} EChange by removing
	 * and deleting a root object.
	 */
	@Test
	def void applyForwardTest() {
		// Create and resolve change 1
		val resolvedChange = createUnresolvedChange(newRootObject, 1).resolveBefore

		// Apply 1
		resolvedChange.assertApplyForward

		assertEquals(resourceContent.size, 2)
		assertFalse(resourceContent.contains(newRootObject))
		assertTrue(resourceContent.contains(newRootObject2))

		// Create and resolve change 2
		val resolvedChange2 = createUnresolvedChange(newRootObject2, 1).resolveBefore

		// Apply 1
		resolvedChange2.assertApplyForward

		// State after
		assertIsStateAfter
	}

	/**
	 * Tests the {@link RemoveAndDeleteRoot} EChange by reverting the change.
	 * It creates and inserts a root object.
	 */
	@Test
	def void applyBackwardTest() {
		// Create and resolve and apply change 1
		val resolvedChange = createUnresolvedChange(newRootObject, 1).resolveBefore
		resolvedChange.assertApplyForward

		// Create and resolve and apply change 2
		val resolvedChange2 = createUnresolvedChange(newRootObject2, 1).resolveBefore
		resolvedChange2.assertApplyForward

		// State after
		assertIsStateAfter

		// Apply backward 2
		resolvedChange2.assertApplyBackward

		assertEquals(resourceContent.size, 2)
		assertTrue(resourceContent.contains(newRootObject2))

		// Apply backward 1
		resolvedChange.assertApplyBackward

		// State before
		assertIsStateBefore
	}

	/**
	 * Sets the state of the model before the changes.
	 */
	def private void prepareStateBefore() {
		resourceContent.add(newRootObject)
		resourceContent.add(newRootObject2)
		assertIsStateBefore
	}

	/**
	 * Model is in state before the changes.
	 */
	def private void assertIsStateBefore() {
		assertEquals(resourceContent.size, 3)
		assertThat(newRootObject, equalsDeeply(resourceContent.get(1)))
		assertThat(newRootObject2, equalsDeeply(resourceContent.get(2)))
	}

	/** 
	 * Model is in state after the changes.
	 */
	def private void assertIsStateAfter() {
		assertEquals(resourceContent.size, 1)
	}

	/**
	 * Change is not resolved.
	 */
	def protected static void assertIsNotResolved(List<? extends EChange> changes) {
		EChangeTest.assertIsNotResolved(changes)
		assertEquals(2, changes.size)
		val removeChange = assertType(changes.get(0), RemoveRootEObject)
		val deleteChange = assertType(changes.get(1), DeleteEObject)
		assertEquals(removeChange.oldValueID, deleteChange.affectedEObjectID)
	}

	/**
	 * Change is resolved.
	 */
	def private static void assertIsResolved(List<EChange> changes, Root affectedRootObject) {
		changes.assertIsResolved
		assertEquals(2, changes.size)
		val removeChange = assertType(changes.get(0), RemoveRootEObject)
		val deleteChange = assertType(changes.get(1), DeleteEObject)
		assertThat(removeChange.oldValue, equalsDeeply(affectedRootObject))
		assertThat(deleteChange.affectedEObject, equalsDeeply(affectedRootObject))
	}

	/**
	 * Creates new unresolved change.
	 */
	def private List<EChange> createUnresolvedChange(Root newObject, int index) {
		return compoundFactory.createRemoveAndDeleteRootChange(newObject, resource, index)
	}
}
