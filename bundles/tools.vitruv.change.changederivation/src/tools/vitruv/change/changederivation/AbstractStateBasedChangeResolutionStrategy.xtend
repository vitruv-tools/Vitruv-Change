package tools.vitruv.change.changederivation

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceCopier
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.BasicMonitor
import org.eclipse.emf.compare.EMFCompare
import org.eclipse.emf.compare.merge.BatchMerger
import org.eclipse.emf.compare.merge.IMerger
import org.eclipse.emf.compare.scope.DefaultComparisonScope
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.change.atomic.EChangeIdManager
import tools.vitruv.change.atomic.id.IdResolver
import tools.vitruv.change.composite.description.VitruviusChange
import tools.vitruv.change.composite.recording.ChangeRecorder

import static com.google.common.base.Preconditions.checkArgument
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories

import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil.getReferencedProxies
import static extension tools.vitruv.change.atomic.resolve.EChangeIdResolverAndApplicator.applyBackward
import static extension tools.vitruv.change.atomic.resolve.EChangeIdResolverAndApplicator.applyForward

/**
 * Abstract base class for StateBasedChangeResolutionStrategies
 */
abstract class AbstractStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
    
    private def checkNoProxies(Resource resource, String stateNotice) {
        val proxies = resource.referencedProxies
        checkArgument(proxies.empty, "%s '%s' should not contain proxies, but contains the following: %s", stateNotice,
            resource.URI, String.join(", ", proxies.map[toString]))
    }

    override getChangeSequenceBetween(Resource newState, Resource oldState) {
        checkArgument(oldState !== null && newState !== null, "old state or new state must not be null!")
        newState.checkNoProxies("new state")
        oldState.checkNoProxies("old state")
        val monitoredResourceSet = new ResourceSetImpl()
        val currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet)
        return currentStateCopy.record [
            if (oldState.URI != newState.URI) {
                currentStateCopy.URI = newState.URI
            }
            compareStatesAndReplayChanges(newState, currentStateCopy)
        ]
    }

    override getChangeSequenceForCreated(Resource newState) {
        checkArgument(newState !== null, "new state must not be null!")
        newState.checkNoProxies("new state")
        // It is possible that root elements are automatically generated during resource creation (e.g., Java packages).
        // Thus, we create the resource and then monitor the re-insertion of the elements
        val monitoredResourceSet = withGlobalFactories(new ResourceSetImpl());
        val newResource = monitoredResourceSet.createResource(newState.URI)
        newResource.contents.clear()
        return newResource.record [
            newResource.contents += EcoreUtil.copyAll(newState.contents)
        ]
    }

    override getChangeSequenceForDeleted(Resource oldState) {
        checkArgument(oldState !== null, "old state must not be null!")
        oldState.checkNoProxies("old state")
        // Setup resolver and copy state:
        val monitoredResourceSet = new ResourceSetImpl()
        val currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet)
        return currentStateCopy.record [
            currentStateCopy.contents.clear()
        ]
    }

    private def <T extends Notifier> record(Resource resource, ()=>void function) {
        try (val changeRecorder = new ChangeRecorder(resource.resourceSet)) {
            changeRecorder.beginRecording
            changeRecorder.addToRecording(resource)
            function.apply()
            val recordedChanges = changeRecorder.endRecording
            assignIds(recordedChanges, resource.resourceSet)
            return recordedChanges.unresolve
        }
    }
    
    private def void assignIds(VitruviusChange recordedChange, ResourceSet resourceSet) {
    	val changes = recordedChange.EChanges
    	val idResolver = IdResolver.create(resourceSet)
    	val eChangeIdManager = new EChangeIdManager(idResolver)
    	changes.toList.reverseView.forEach[applyBackward]
		changes.forEach[ change |
			eChangeIdManager.setOrGenerateIds(change)
			change.applyForward(idResolver)
		]
    }
    
    /**
     * Compares states using the EMFCompareInstance provided by {@link #getEMFCompareInstance()} and replays the changes to the current state.
     */
    private def compareStatesAndReplayChanges(Notifier newState, Notifier currentState) {
        val scope = new DefaultComparisonScope(newState, currentState, null)
        val emfCompare = getEMFCompareInstance
        val differences = emfCompare.compare(scope).differences
        // Replay the EMF compare differences
        val mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance()
        val merger = new BatchMerger(mergerRegistry)
        merger.copyAllLeftToRight(differences, new BasicMonitor)
    }
    
    protected abstract def EMFCompare getEMFCompareInstance();
}
