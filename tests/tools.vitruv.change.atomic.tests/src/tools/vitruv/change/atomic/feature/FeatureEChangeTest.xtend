package tools.vitruv.change.atomic.feature

import allElementTypes.AllElementTypesPackage
import allElementTypes.Root
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.feature.FeatureEChange
import tools.vitruv.change.atomic.EChangeTest
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import tools.vitruv.change.atomic.id.IdResolver
import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertSame
import static org.junit.jupiter.api.Assertions.assertNotSame
import static org.junit.jupiter.api.Assertions.assertThrows
import static tools.vitruv.testutils.metamodels.AllElementTypesCreators.*
import static extension tools.vitruv.change.atomic.resolve.EChangeResolverAndApplicator.*

/**
 * Test class for {@link FeatureEChange} which is used by every {@link EChange} which modifies {@link EStructuralFeature}s 
 * (single- or multi-valued attributes or references) of a {@link EObject}.
 */
class FeatureEChangeTest extends EChangeTest {
	var Root affectedEObject
	var EAttribute affectedFeature

	// Second model instance
	var IdResolver idResolver2
	var Resource resource2

	@BeforeEach
	def final void beforeTest() {
		affectedEObject = rootObject
		affectedFeature = AllElementTypesPackage.Literals.IDENTIFIED__ID

		// Load model in second resource
		val resourceSet2 = new ResourceSetImpl().withGlobalFactories
		this.resource2 = resourceSet2.getResource(resource.URI, true)
		this.idResolver2 = IdResolver.create(resourceSet2)
	}

	/**
	 * Tests if a feature change, which affected object and feature references 
	 * to the changed model instance, resolved correctly to the same model instance, 
	 * after unresolving the object. 
	 */
	@Test
	def void resolveBeforeTest() {
		// Create change 		
		val unresolvedChange = createUnresolvedChange()
		unresolvedChange.assertIsNotResolved(affectedEObject, affectedFeature)

		// Resolve
		val resolvedChange = unresolvedChange.resolveBefore as FeatureEChange<Root, EAttribute>
		resolvedChange.assertIsResolved(affectedEObject, affectedFeature)
	}

	/**
	 * Tests a failed resolve.
	 */
	@Test
	def void resolveEFeatureChangeFails() {
		// Change first resource by insert second root element
		affectedEObject = prepareSecondRoot

		// second resource has no such root element
		assertNull(resource2.getEObject(EcoreUtil.getURI(affectedEObject).fragment))

		// Create change 		
		val unresolvedChange = createUnresolvedChange()
		unresolvedChange.assertIsNotResolved(affectedEObject, affectedFeature)

		// Resolve
		assertThrows(IllegalStateException)[unresolvedChange.resolveBefore(idResolver2)]
	}

	/**
	 * Tests whether resolving an already resolved EFeatureChange throws an exception.
	 */
	@Test
	def void resolveResolvedEFeatureChange() {
		// Create change and resolve	
		val resolvedChange = createUnresolvedChange().resolveBefore as FeatureEChange<Root, EAttribute>
		resolvedChange.assertIsResolved(affectedEObject, affectedFeature)

		// Resolve again
		assertThrows(IllegalArgumentException, [resolvedChange.resolveBefore])
	}

	/**
	 * Test whether resolving the EFeatureChange fails by giving a null EObject
	 */
	@Test
	def void resolveEFeatureAffectedObjectNull() {
		affectedEObject = null

		// Create change	
		assertThrows(IllegalArgumentException) [createUnresolvedChange()]
	}

	/**
	 * Tests whether resolving the EFeatureChange fails by giving a null EFeature
	 */
	@Test
	def void resolveEFeatureAffectedFeatureNull() {
		affectedFeature = null

		// Create change	
		val unresolvedChange = createUnresolvedChange()
		unresolvedChange.assertIsNotResolved(affectedEObject, null)

		// Resolve
		assertThrows(IllegalArgumentException) [unresolvedChange.resolveBefore]
	}

	/**
	 * Creates and inserts a new root element in the resource 1.
	 */
	def private Root prepareSecondRoot() {
		val root = aet.Root
		resource.contents.add(root)
		return root
	}

	/**
	 * Change is not resolved.
	 */
	def private static void assertIsNotResolved(FeatureEChange<Root, EAttribute> change, Root affectedEObject,
		EAttribute affectedFeature) {
		assertFalse(change.isResolved)
		assertNotSame(change.affectedEObject, affectedEObject)
		assertSame(change.affectedFeature, affectedFeature)
	}

	/**
	 * Change is resolved.
	 */
	def private static void assertIsResolved(FeatureEChange<Root, EAttribute> change, Root affectedEObject,
		EAttribute affectedFeature) {
		assertTrue(change.isResolved)
		assertSame(change.affectedEObject, affectedEObject)
		assertSame(change.affectedFeature, affectedFeature)
	}

	/**
	 * Creates new unresolved change.
	 */
	def private FeatureEChange<Root, EAttribute> createUnresolvedChange() {
		// The concrete change type ReplaceSingleEAttributeChange will be used for the tests.
		return atomicFactory.createReplaceSingleAttributeChange(affectedEObject, affectedFeature, null, null)
	}
}
