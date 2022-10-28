package tools.vitruv.testutils.views

import java.nio.file.Path
import java.util.ArrayList
import java.util.LinkedList
import java.util.List
import java.util.function.Consumer
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.xtend.lib.annotations.Delegate
import tools.vitruv.change.composite.description.PropagatedChange
import tools.vitruv.change.composite.description.TransactionalChange
import tools.vitruv.change.composite.description.VitruviusChange
import tools.vitruv.change.composite.recording.ChangeRecorder

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkState

import static extension edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil.flatMapFixed
import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil.withGlobalFactories
import tools.vitruv.change.composite.propagation.ChangeableModelRepository
import tools.vitruv.change.propagation.ChangePropagationSpecification
import tools.vitruv.testutils.TestUserInteraction
import tools.vitruv.change.propagation.ChangePropagationSpecificationRepository
import static tools.vitruv.testutils.TestModelRepositoryFactory.createTestChangeableModelRepository
import tools.vitruv.change.propagation.impl.DefaultChangeableModelRepository
import tools.vitruv.change.atomic.uuid.UuidResolver

/**
 * A test view that will record and publish the changes created in it.
 */
class ChangePublishingTestView implements NonTransactionalTestView {
	val ResourceSet resourceSet
	@Delegate
	val TestView delegate
	val ChangeRecorder changeRecorder
	val List<(VitruviusChange)=>List<PropagatedChange>> changeProcessors = new LinkedList()
	var disposeViewResourcesAfterPropagation = true

	/**
	 * Creates a test view that will store its persisted resources in the
	 * provided {@code persistenceDirectory}, allow to program interactions through the provided {@code userInteraction},
	 * use the provided {@code uriMode}.
	 */
	new(
		Path persistenceDirectory,
		tools.vitruv.testutils.TestUserInteraction userInteraction,
		UriMode uriMode
	) {
		this.resourceSet = new ResourceSetImpl().withGlobalFactories()
		this.delegate = new BasicTestView(persistenceDirectory, resourceSet, userInteraction, uriMode)
		this.changeRecorder = new ChangeRecorder(resourceSet, UuidResolver.create(resourceSet))
		changeRecorder.beginRecording()
	}

	/**
	 * Creates a test view that will store its persisted resources in the
	 * provided {@code persistenceDirectory}, allow to program interactions through the provided {@code userInteraction},
	 * use the provided {@code uriMode} and be connected to the provided {@code virtualModel}.
	 */
	new(
		Path persistenceDirectory,
		tools.vitruv.testutils.TestUserInteraction userInteraction,
		UriMode uriMode,
		ChangeableModelRepository changeableModelRepository
	) {
		this(persistenceDirectory, userInteraction, uriMode)
		registerChangeProcessor [change|changeableModelRepository.propagateChange(change)]
	}

	override close() {
		delegate.close()
		changeRecorder.close()
	}

	override <T extends Notifier> T record(T notifier, Consumer<T> consumer) {
		try {
			startRecordingChanges(notifier)
			return delegate.record(notifier, consumer)
		} finally {
			stopRecordingChanges(notifier)
		}
	}

	override <T extends Notifier> List<PropagatedChange> propagate(T notifier, Consumer<T> consumer) {
		val delegateChanges = delegate.propagate(notifier)[record(consumer)]
		changeRecorder.endRecording()
		val ourChanges = propagateChanges(changeRecorder.change)
		changeRecorder.beginRecording()
		return delegateChanges + ourChanges
	}

	override propagate() {
		changeRecorder.endRecording()
		val recordedChange = changeRecorder.change
		val delegateChanges = recordedChange.changedURIs.map[resourceSet.getResource(it, false)].filterNull.flatMapFixed [ changedResource |
			// Propagating an empty modification for every changed resource gives the delegate a
			// chance to participate in change propagation (e.g. BasicTestView saves or cleans up resources).
			// This is not a meaningful operation at all, but rather a hack to bridge between this
			// non-transactional operation and the transactional delegate.
			delegate.propagate(changedResource)[]
		]
		val ourChanges = propagateChanges(recordedChange)
		changeRecorder.beginRecording()
		return delegateChanges + ourChanges
	}

	def private propagateChanges(TransactionalChange change) {
		val propagationResult = changeProcessors.flatMapFixed[apply(change)]
		if (disposeViewResourcesAfterPropagation) {
			disposeViewResources()
		}
		return propagationResult
	}

	override disposeViewResources() {
		resourceSet.resources.clear()
	}

	/**
	 * Registers the provided {@code processor} to be used when this view publishes changes.
	 */
	def registerChangeProcessor((VitruviusChange)=>List<PropagatedChange> processor) {
		changeProcessors += processor
	}

	override <T extends Notifier> T startRecordingChanges(T notifier) {
		checkState(changeRecorder.recording, "This test view has already been closed!")
		checkArgument(notifier !== null, '''The object to record changes of is null!''')
		changeRecorder.addToRecording(notifier)
		return notifier
	}

	override <T extends Notifier> T stopRecordingChanges(T notifier) {
		checkState(changeRecorder.recording, "This test view has already been closed!")
		checkArgument(notifier !== null, '''The object to stop recording changes of is null!''')
		changeRecorder.removeFromRecording(notifier)
		return notifier
	}

	override setDisposeViewResourcesAfterPropagation(boolean enabled) {
		disposeViewResourcesAfterPropagation = enabled
	}

	def static <T> List<T> operator_plus(List<T> a, List<T> b) {
		return if (a.isEmpty) {
			b
		} else if (b.isEmpty){
			a
		} else {
			val result = new ArrayList(a.size + b.size)
			result.addAll(a)
			result.addAll(b)
			result
		}
	}

	/**
	 * Creates a {@link ChangePublishingTestView} with the given persistence directory
	 * and for the given {@link ChangePropagationSpecification}s. It uses file URIs
	 * (see {@link UriMode}) and instantiates a {@link DefaultChangeableModelRepository}
	 * for a {@link TestUserInteraction}.
	 */
	static def createDefaultChangePublishingTestView(
		Path persistenceDirectory,
		Iterable<ChangePropagationSpecification> changePropagationSpecifications
	) {
		val userInteraction = new TestUserInteraction()
		val changePropagationSpecificationProvider = new ChangePropagationSpecificationRepository(
			changePropagationSpecifications)
		val changeableModelRepository = createTestChangeableModelRepository(changePropagationSpecificationProvider,
			userInteraction)
		return new ChangePublishingTestView(persistenceDirectory, userInteraction, UriMode.FILE_URIS,
			changeableModelRepository)
	}
}
