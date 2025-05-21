package tools.vitruv.change.propagation.impl

import java.util.ArrayList
import java.util.HashSet
import java.util.List
import java.util.Set
import org.apache.log4j.Logger
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import tools.vitruv.change.atomic.uuid.Uuid
import tools.vitruv.change.composite.description.CompositeChange
import tools.vitruv.change.composite.description.PropagatedChange
import tools.vitruv.change.composite.description.TransactionalChange
import tools.vitruv.change.composite.description.VitruviusChange
import tools.vitruv.change.composite.description.VitruviusChangeFactory
import tools.vitruv.change.interaction.InternalUserInteractor
import tools.vitruv.change.interaction.UserInteractionBase
import tools.vitruv.change.interaction.UserInteractionFactory
import tools.vitruv.change.interaction.UserInteractionListener
import tools.vitruv.change.propagation.ChangePropagationMode
import tools.vitruv.change.propagation.ChangePropagationObserver
import tools.vitruv.change.propagation.ChangePropagationSpecification
import tools.vitruv.change.propagation.ChangePropagationSpecificationProvider
import tools.vitruv.change.propagation.ChangeRecordingModelRepository

import static com.google.common.base.Preconditions.checkState

import static extension edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil.*

class ChangePropagator {
	static val logger = Logger.getLogger(ChangePropagator)
	val ChangeRecordingModelRepository modelRepository
	val ChangePropagationSpecificationProvider changePropagationProvider
	val InternalUserInteractor userInteractor
	val ChangePropagationMode changePropagationMode

	/**
	 * Creates a change propagator to which changes can be passed, which are
	 * propagated using the given <code>changePropagationProvider</code> and
	 * <code>userInteractor</code>.
	 * Changes are recorded in the given <code>modelRepository</code> and
	 * propagated transitively and cyclic, i.e. with 
	 * {@link ChangePropagationMode#TRANSITIVE_CYCLIC}.
	 */
	new(ChangeRecordingModelRepository modelRepository,
		ChangePropagationSpecificationProvider changePropagationProvider, InternalUserInteractor userInteractor) {
		this(modelRepository, changePropagationProvider, userInteractor, ChangePropagationMode.TRANSITIVE_CYCLIC)
	}

	/**
	 * Creates a change propagator to which changes can be passed, which are
	 * propagated using the given <code>changePropagationProvider</code> and
	 * <code>userInteractor</code>.
	 * Changes are recorded in the given <code>modelRepository</code> and
	 * propagated using the given <code>mode</code>.
	 */
	new(ChangeRecordingModelRepository modelRepository,
		ChangePropagationSpecificationProvider changePropagationProvider, InternalUserInteractor userInteractor,
		ChangePropagationMode mode) {
		this.modelRepository = modelRepository
		this.changePropagationProvider = changePropagationProvider
		this.userInteractor = userInteractor
		this.changePropagationMode = mode
	}

	def List<PropagatedChange> propagateChange(VitruviusChange<Uuid> change) {
		val resolvedChange = modelRepository.applyChange(change)
		resolvedChange.affectedEObjects.map[eResource].filterNull.forEach[modified = true]

		if (logger.isTraceEnabled) {
			logger.trace('''
				Will now propagate this input change:
					«resolvedChange»
			''')
		}
		return new ChangePropagation(this, resolvedChange, null).propagateChanges()
	}

	@FinalFieldsConstructor
	private static class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
		extension val ChangePropagator outer
		val VitruviusChange<EObject> sourceChange
		val ChangePropagation previous
		val Set<Resource> changedResources = new HashSet
		val List<EObject> createdObjects = new ArrayList
		val List<UserInteractionBase> userInteractions = new ArrayList

		def private propagateChanges() {
		    /* First, the whole vitruviuschange is propagated to CPSs that can handle it.
		     * Then, the other CPSs handle the change in their way.
		     (the non-atomic-enabled CPSs are called, too, but perform noops)
		    */
		    val result = propagateNonAtomicChange()
			result += sourceChange.transactionalChangeSequence.flatMapFixed[propagateSingleChange(it)]

			handleObjectsWithoutResource()
			changedResources.forEach[modified = true]
			return result
		}

		def private List<PropagatedChange> propagateNonAtomicChange() {
			val userInteractorChange = installUserInteractorForChange(sourceChange)
			changePropagationProvider.forEach[registerObserver(this)]
			userInteractor.registerUserInputListener(this)

			val propagationResultChanges = try {
					sourceChange.affectedEObjectsMetamodelDescriptors.flatMap [
					    // we only want ChangePropSpecs that handle non-atomic changes
						changePropagationProvider.getChangePropagationSpecifications(it).filter[it.doesHandleNonAtomicChanges()] => [
							forEach[it.userInteractor = outer.userInteractor]
						]
					].toSet.flatMapFixed [
					    propagateNonAtomicChangeForChangePropagationSpecification(sourceChange, it)
					]
				} finally {
					userInteractor.deregisterUserInputListener(this)
					changePropagationProvider.forEach[deregisterObserver(this)]
					userInteractorChange.close()
				}

			if (logger.isDebugEnabled) {
				logger.debug(
					'''Propagated «FOR p : propagationPath SEPARATOR ' -> '»«p»«ENDFOR» -> {«FOR changeInPropagation : propagationResultChanges SEPARATOR ", "»«
						changeInPropagation.affectedEObjectsMetamodelDescriptors»«ENDFOR»}'''
				)
			}
			if (logger.isTraceEnabled) {
				logger.trace('''
					Result changes:
						«FOR result : propagationResultChanges»
							«result.affectedEObjectsMetamodelDescriptors»: «result»
						«ENDFOR»
				''')
			}

			val resultingChanges = new ArrayList()
			if (!propagationResultChanges.isNullOrEmpty) {
                val propagatedChange = new PropagatedChange(sourceChange,
                    VitruviusChangeFactory.instance.createCompositeChange(propagationResultChanges))
                resultingChanges += propagatedChange
			}

			if (changePropagationMode != ChangePropagationMode.SINGLE_STEP) {
				resultingChanges +=
					propagationResultChanges.filter[it.containsConcreteChange].propagateTransitiveChanges
			}
			return resultingChanges
		}

		def private List<PropagatedChange> propagateSingleChange(TransactionalChange<EObject> change) {
			checkState(!change.affectedEObjects.isNullOrEmpty, "There are no objects affected by this change:%s%s",
				System.lineSeparator, change)

			val userInteractorChange = installUserInteractorForChange(change)
			changePropagationProvider.forEach[registerObserver(this)]
			userInteractor.registerUserInputListener(this)

			val propagationResultChanges = try {
					sourceChange.affectedEObjectsMetamodelDescriptors.flatMap [
						changePropagationProvider.getChangePropagationSpecifications(it) => [
							forEach[it.userInteractor = outer.userInteractor]
						]
					].toSet.flatMapFixed [
						propagateChangeForChangePropagationSpecification(change, it)
					]
				} finally {
					userInteractor.deregisterUserInputListener(this)
					changePropagationProvider.forEach[deregisterObserver(this)]
					userInteractorChange.close()
				}

			if (logger.isDebugEnabled) {
				logger.debug(
					'''Propagated «FOR p : propagationPath SEPARATOR ' -> '»«p»«ENDFOR» -> {«FOR changeInPropagation : propagationResultChanges SEPARATOR ", "»«
						changeInPropagation.affectedEObjectsMetamodelDescriptors»«ENDFOR»}'''
				)
			}
			if (logger.isTraceEnabled) {
				logger.trace('''
					Result changes:
						«FOR result : propagationResultChanges»
							«result.affectedEObjectsMetamodelDescriptors»: «result»
						«ENDFOR»
				''')
			}

			change.userInteractions = userInteractions
			val propagatedChange = new PropagatedChange(change,
				VitruviusChangeFactory.instance.createCompositeChange(propagationResultChanges))
			val resultingChanges = new ArrayList()
			resultingChanges += propagatedChange

			if (changePropagationMode != ChangePropagationMode.SINGLE_STEP) {
				resultingChanges +=
					propagationResultChanges.filter[it.containsConcreteChange].propagateTransitiveChanges
			}
			return resultingChanges
		}

		def private propagateTransitiveChanges(Iterable<TransactionalChange<EObject>> transitiveChanges) {
			val nonEmptyChanges = transitiveChanges.filter [
				it.containsConcreteChange
			]
			val nonLeafChanges = if (changePropagationMode == ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES) {
					nonEmptyChanges.filter [
						val targetSpecifications = changePropagationProvider.getChangePropagationSpecifications(
							it.affectedEObjectsMetamodelDescriptor)
						return targetSpecifications.size > 1
					]
				} else {
					nonEmptyChanges
				}
			val nextPropagations = nonLeafChanges.mapFixed [
				new ChangePropagation(outer, it, this)
			]

			return nextPropagations.mapFixed[propagateChanges()].flatten
		}

		def private propagateNonAtomicChangeForChangePropagationSpecification(
			VitruviusChange<EObject> change,
			ChangePropagationSpecification propagationSpecification
		) {
			val transitiveChanges = modelRepository.recordChanges [
			    propagationSpecification.propagateNonAtomicChange(change,  modelRepository.correspondenceModel,
                       modelRepository)
			]

			// Store modification information
			changedResources += transitiveChanges.flatMap[it.affectedEObjects].map[eResource].filterNull

			return transitiveChanges
		}

		def private propagateChangeForChangePropagationSpecification(
			TransactionalChange<EObject> change,
			ChangePropagationSpecification propagationSpecification
		) {
			val transitiveChanges = modelRepository.recordChanges [
				for (eChange : change.EChanges) {
					propagationSpecification.propagateChange(eChange, modelRepository.correspondenceModel,
						modelRepository)
				}
			]

			// Store modification information
			changedResources += transitiveChanges.flatMap[it.affectedEObjects].map[eResource].filterNull

			return transitiveChanges
		}

		def private AutoCloseable installUserInteractorForChange(VitruviusChange<EObject> change) {
			// retrieve user inputs from past changes, construct a UserInteractor which tries to reuse them:
			val pastUserInputsFromChange = change.userInteractions

			if (!pastUserInputsFromChange.nullOrEmpty) {
				userInteractor.replaceUserInteractionResultProvider [ currentProvider |
					UserInteractionFactory.instance.createPredefinedInteractionResultProvider(currentProvider,
						pastUserInputsFromChange)
				]
			} else
				[]
		}

		def private void handleObjectsWithoutResource() {
			// Find created objects without resource
			for (createdObjectWithoutResource : createdObjects.filter[eResource === null]) {
				checkState(!modelRepository.correspondenceModel.hasCorrespondences(createdObjectWithoutResource),
					"The object %s is part of a correspondence to %s but not in any resource",
					createdObjectWithoutResource,
					modelRepository.correspondenceModel.getCorrespondingEObjects(createdObjectWithoutResource))
				logger.warn("Object was created but has no correspondence and is thus lost: " +
					createdObjectWithoutResource)
			}
		}

		override objectCreated(EObject createdObject) {
			createdObjects += createdObject
		}

		override onUserInteractionReceived(UserInteractionBase interaction) {
			userInteractions += interaction
		}

		override toString() '''propagate «FOR p : propagationPath SEPARATOR ' -> '»«p»«ENDFOR»: «sourceChange»'''

		def private Iterable<String> getPropagationPath() {
			if (previous === null)
				List.of("<input change> in " + sourceChange.affectedEObjectsMetamodelDescriptors.toString)
			else
				previous.propagationPath + List.of(sourceChange.affectedEObjectsMetamodelDescriptors.toString)
		}
	}

	def private Iterable<TransactionalChange<EObject>> getTransactionalChangeSequence(VitruviusChange<EObject> change) {
		switch (change) {
			case !change.containsConcreteChange: emptyList()
			TransactionalChange<EObject>: List.of(change)
			CompositeChange<EObject, ?>: change.changes.flatMap[transactionalChangeSequence]
			default: throw new IllegalStateException("Unexpected change type: " + change.class.simpleName)
		}
	}

}
