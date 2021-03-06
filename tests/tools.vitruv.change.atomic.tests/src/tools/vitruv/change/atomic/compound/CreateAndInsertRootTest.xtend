package tools.vitruv.change.atomic.compound

import allElementTypes.Root
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import tools.vitruv.change.atomic.EChangeTest

import static extension tools.vitruv.change.atomic.util.EChangeAssertHelper.*
import tools.vitruv.change.atomic.EChange
import java.util.List
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.root.InsertRootEObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertEquals
import static tools.vitruv.testutils.metamodels.AllElementTypesCreators.*
import static org.hamcrest.MatcherAssert.assertThat
import static tools.vitruv.testutils.matchers.ModelMatchers.equalsDeeply

/**
 * Test class for the concrete {@link CreateAndInsertRoot} EChange,
 * which creates a new root EObject and inserts it in a resource.
 */
class CreateAndInsertRootTest extends EChangeTest {
	var Root newRootObject
	var Root newRootObject2
	var EList<EObject> resourceContent

	/**
	 * Calls setup of the superclass and creates two new root elements
	 * which can be inserted.
	 */
	@BeforeEach
	def void beforeTest() {
		newRootObject = aet.Root
		newRootObject2 = aet.Root
		resourceContent = resource.contents
		assertIsStateBefore
	}

	/**
	 * Resolves the {@link CreateAndInsertRoot} EChange. The model is in state
	 * before the change is applied, so the staging area and object in progress are empty,
	 * and the root object is not in the resource.
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
	 * Tests whether resolving the {@link CreateAndInsertRoot} EChange
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
	 * Tests applying the {@link CreateAndInsertRoot} EChange forward
	 * by creating and inserting a new root object.
	 */
	@Test
	def void applyForwardTest() {
		// Create and resolve change 1
		val resolvedChange = createUnresolvedChange(newRootObject, 1).resolveBefore

		// Apply 1
		resolvedChange.assertApplyForward

		assertEquals(resourceContent.size, 2)
		val createChange = assertType(resolvedChange.get(0), CreateEObject)
		assertTrue(resourceContent.contains(createChange.affectedEObject))

		// Create and resolve change 2
		val resolvedChange2 = createUnresolvedChange(newRootObject2, 2).resolveBefore

		// Apply 2
		resolvedChange2.assertApplyForward

		// State after
		assertIsStateAfter
	}

	/**
	 * Tests applying the {@link CreateAndInsertRoot} EChange backward 
	 * by reverting the change. It removes and deletes a root object. 
	 */
	@Test
	def void applyBackwardTest() {
		// Create and resolve and apply change 1
		val resolvedChange = createUnresolvedChange(newRootObject, 1).resolveBefore
		resolvedChange.assertApplyForward

		// Create and resolve and apply change 2
		val resolvedChange2 = createUnresolvedChange(newRootObject2, 2).resolveBefore
		resolvedChange2.assertApplyForward

		// State after
		assertIsStateAfter

		// Apply backward 2
		resolvedChange2.assertApplyBackward

		assertEquals(resourceContent.size, 2)
		val createChange = assertType(resolvedChange.get(0), CreateEObject)
		val createChange2 = assertType(resolvedChange2.get(0), CreateEObject)
		assertTrue(resourceContent.contains(createChange.affectedEObject))
		assertFalse(resourceContent.contains(createChange2.affectedEObject))

		// Apply backward 1
		resolvedChange.assertApplyBackward

		// State before
		assertIsStateBefore
	}

	/**
	 * Model is in state before the changes.
	 */
	def private void assertIsStateBefore() {
		assertEquals(resourceContent.size, 1)
	}

	/**
	 * Model is in state after the changes
	 */
	def private void assertIsStateAfter() {
		assertEquals(resourceContent.size, 3)
		assertThat(newRootObject, equalsDeeply(resourceContent.get(1)))
		assertThat(newRootObject2, equalsDeeply(resourceContent.get(2)))
	}

	/**
	 * Change is not resolved.
	 */
	def protected static void assertIsNotResolved(List<? extends EChange> changes) {
		EChangeTest.assertIsNotResolved(changes)
		assertEquals(2, changes.size)
		val createChange = assertType(changes.get(0), CreateEObject)
		val insertChange = assertType(changes.get(1), InsertRootEObject)
		assertEquals(insertChange.newValueID, createChange.affectedEObjectID)
	}

	/**
	 * Change is resolved.
	 */
	def private static void assertIsResolved(List<EChange> changes, Root newRoot) {
		changes.assertIsResolved
		assertEquals(2, changes.size)
		val createChange = assertType(changes.get(0), CreateEObject)
		val insertChange = assertType(changes.get(1), InsertRootEObject)
		assertThat(insertChange.newValue, equalsDeeply(newRoot))
		assertThat(createChange.affectedEObject, equalsDeeply(newRoot))
	}

	/**
	 * Creates new unresolved change.
	 */
	def private List<EChange> createUnresolvedChange(Root newObject, int index) {
		return compoundFactory.createCreateAndInsertRootChange(newObject, resource, index)
	}
}
