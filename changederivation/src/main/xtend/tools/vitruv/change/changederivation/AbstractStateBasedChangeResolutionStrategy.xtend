package tools.vitruv.change.changederivation

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceCopier
import java.util.Collection
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.BasicMonitor
import org.eclipse.emf.compare.EMFCompare
import org.eclipse.emf.compare.diff.IDiffEngine
import org.eclipse.emf.compare.match.IMatchEngine.Factory
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl
import org.eclipse.emf.compare.merge.BatchMerger
import org.eclipse.emf.compare.merge.IMerger
import org.eclipse.emf.compare.postprocessor.IPostProcessor
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl
import org.eclipse.emf.compare.scope.DefaultComparisonScope
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory
import tools.vitruv.change.composite.recording.ChangeRecorder

import static com.google.common.base.Preconditions.checkArgument
import static edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories

import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil.getReferencedProxies

/**
 * Abstract base class for StateBasedChangeResolutionStrategies that uses EMFCompare 
 * to resolve a diff to a sequence of individual changes.
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
            val changeResolver = VitruviusChangeResolverFactory.forHierarchicalIds(resource.resourceSet)
            return changeResolver.assignIds(recordedChanges)
        }
    }
    
    /**
     * Compares states using the EMFCompareInstance provided by {@link #getEMFCompareInstance()} and replays the changes to the current state.
     */
    private def compareStatesAndReplayChanges(Notifier newState, Notifier currentState) {
        val scope = new DefaultComparisonScope(newState, currentState, null)
        val differences = buildEMFCompare().compare(scope).differences
        // Replay the EMF compare differences
        val mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance()
        val merger = new BatchMerger(mergerRegistry)
        merger.copyAllLeftToRight(differences, new BasicMonitor)
    }
    
    private def buildEMFCompare() {
    	val usedMatchEngineFactoryRegistry = MatchEngineFactoryRegistryImpl.createStandaloneInstance()
    	getMatchEngineFactories.forEach[it | 
    		it.ranking = 20
    		usedMatchEngineFactoryRegistry.add(it)
    	]
    	
    	val usedPostProcessorRegistry = new PostProcessorDescriptorRegistryImpl()
    	getPostProcessors.forEach[it | usedPostProcessorRegistry.put(it.getInstanceClassName, it)]
    	
    	return (EMFCompare.builder => [
            matchEngineFactoryRegistry = usedMatchEngineFactoryRegistry
            diffEngine = getDiffEngine()
            postProcessorRegistry = usedPostProcessorRegistry
        ]).build
    }
    
    /**
     * {@link Factory}'s (MatchEngine + rank + is MatchEngine suitable for comparison scope) used by EMFCompare 
     * The rank will be overwritten by this class. To ensure that it has a greater value than the default implementation.
     */
    abstract protected def Collection<Factory> getMatchEngineFactories();
    
    /**
     * {@link IDiffEngine} used by EMFCompare
     */
    abstract protected def IDiffEngine getDiffEngine();
    
    /**
     * {@link IPostProcessor.Descriptor}'s (PostProcessor + Pattern where to be applied) used by EMFCompare
     */
    abstract protected def Collection<IPostProcessor.Descriptor> getPostProcessors();
}
