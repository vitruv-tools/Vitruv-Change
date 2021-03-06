package tools.vitruv.change.atomic.compound

import allElementTypes.AllElementTypesPackage
import allElementTypes.NonRoot
import allElementTypes.Root
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EReference
import tools.vitruv.change.atomic.feature.reference.ReferenceEChangeTest

import static extension tools.vitruv.change.atomic.util.EChangeAssertHelper.*
import tools.vitruv.change.atomic.EChange
import java.util.List
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.feature.reference.InsertEReference
import tools.vitruv.change.atomic.EChangeTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertSame
import static org.hamcrest.MatcherAssert.assertThat
import static tools.vitruv.testutils.matchers.ModelMatchers.equalsDeeply

/**
 * Test class for the concrete {@link CreateAndInsertNonRoot} EChange,
 * which creates a new non root EObject and inserts it in containment reference.
 */
class CreateAndInsertNonRootTest extends ReferenceEChangeTest {
	var EReference affectedFeature
	var EList<NonRoot> referenceContent

	@BeforeEach
	def void prepareState() {
		affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE
		referenceContent = affectedEObject.eGet(affectedFeature) as EList<NonRoot>
		assertIsStateBefore
	}

	/**
	 * Resolves a {@link CreateAndInsertNonRoot} EChange. The model is in state
	 * before the change, so the new non root element will be created and inserted
	 * into a containment reference.
	 */
	@Test
	def void resolveBeforeTest() {
		// Create change
		val unresolvedChange = createUnresolvedChange(affectedEObject, newValue, 0)
		unresolvedChange.assertIsNotResolved

		// Resolve
		val resolvedChange = unresolvedChange.resolveBefore
		resolvedChange.assertIsResolved(affectedEObject, newValue)

		// Resolving applies all changes and reverts them, so the model should be unaffected.			
		assertIsStateBefore
	}

	/**
	 * Tests whether resolving the {@link CreateAndInsertNonRoot} EChange
	 * returns the same class.
	 */
	@Test
	def void resolveToCorrectType() {
		// Create change
		val unresolvedChange = createUnresolvedChange(affectedEObject, newValue, 0)

		// Resolve		
		val resolvedChange = unresolvedChange.resolveBefore
		unresolvedChange.assertDifferentChangeSameClass(resolvedChange)
	}

	/**
	 * Tests the {@link CreateAndInsertNonRoot} EChange by applying it forward.
	 * Creates and inserts a new non root object into a multi valued 
	 * containment reference.
	 */
	@Test
	def void applyForwardTest() {
		// Create and resolve and apply change
		val resolvedChange = createUnresolvedChange(affectedEObject, newValue, 0).resolveBefore
		resolvedChange.assertApplyForward

		assertEquals(referenceContent.size, 1)
		val createChange = assertType(resolvedChange.get(0), CreateEObject)
		assertTrue(referenceContent.contains(createChange.affectedEObject))

		// Create and resolve and apply change 2	
		val resolvedChange2 = createUnresolvedChange(affectedEObject, newValue2, 1).resolveBefore
		resolvedChange2.assertApplyForward

		// State after
		assertIsStateAfter
	}

	/**
	 * Tests the {@link CreateAndInsertNonRoot} EChange by applying it backward.
	 * A non root object which was added to a containment reference will be removed and
	 * deleted.
	 */
	@Test
	def void applyBackwardTest() {
		// Create and resolve and apply change 1
		val resolvedChange = createUnresolvedChange(affectedEObject, newValue, 0).resolveBefore
		resolvedChange.assertApplyForward

		// Create and resolve change 2
		val resolvedChange2 = createUnresolvedChange(affectedEObject, newValue2, 1).resolveBefore
		resolvedChange2.assertApplyForward

		// State after
		assertIsStateAfter

		// Apply backward 2
		resolvedChange2.assertApplyBackward

		val createChange = assertType(resolvedChange.get(0), CreateEObject)
		val createChange2 = assertType(resolvedChange2.get(0), CreateEObject)
		assertTrue(referenceContent.contains(createChange.affectedEObject))
		assertFalse(referenceContent.contains(createChange2.affectedEObject))
		assertEquals(referenceContent.size, 1)

		// Apply backward 1	
		resolvedChange.assertApplyBackward

		// State after
		assertIsStateBefore
	}

	/**
	 * Model is in state before the changes.
	 */
	def private void assertIsStateBefore() {
		assertEquals(referenceContent.size, 0)
	}

	/**
	 * Model is in state after the changes.
	 */
	def private void assertIsStateAfter() {
		assertEquals(referenceContent.size, 2)
		assertThat(newValue, equalsDeeply(referenceContent.get(0)))
		assertThat(newValue2, equalsDeeply(referenceContent.get(1)))
	}

	/**
	 * Change is not resolved.
	 */
	def protected static void assertIsNotResolved(List<? extends EChange> changes) {
		EChangeTest.assertIsNotResolved(changes)
		assertEquals(2, changes.size)
		val createChange = assertType(changes.get(0), CreateEObject)
		val insertChange = assertType(changes.get(1), InsertEReference)
		assertEquals(insertChange.newValueID, createChange.affectedEObjectID)
	}

	/**
	 * Change is resolved.
	 */
	def private static void assertIsResolved(List<EChange> changes, Root affectedEObject, NonRoot newNonRoot) {
		changes.assertIsResolved
		assertEquals(2, changes.size)
		val createChange = assertType(changes.get(0), CreateEObject)
		val InsertEReference<?,?> insertChange = assertType(changes.get(1), InsertEReference)
		assertThat(insertChange.newValue, equalsDeeply(newNonRoot))
		assertThat(createChange.affectedEObject, equalsDeeply(newNonRoot))
		assertSame(insertChange.affectedEObject, affectedEObject)
	}

	/**
	 * Creates new unresolved change.
	 */
	def private List<EChange> createUnresolvedChange(Root affectedRootObject, NonRoot newNonRoot, int index) {
		return compoundFactory.createCreateAndInsertNonRootChange(affectedRootObject, affectedFeature, newNonRoot,
			index)
	}
}
