package tools.vitruv.change.changederivation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceCopier;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil;
import java.util.Collection;
import java.util.function.Consumer;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.IMatchEngine.Factory;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.recording.ChangeRecorder;

/**
 * Abstract base class for StateBasedChangeResolutionStrategies that uses EMFCompare
 * to resolve a diff to a sequence of individual changes.
 */
@SuppressWarnings("all")
public abstract class AbstractStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
  private void checkNoProxies(final Resource resource, final String stateNotice) {
    final Iterable<EObject> proxies = ResourceUtil.getReferencedProxies(resource);
    final Function1<EObject, String> _function = (EObject it) -> {
      return it.toString();
    };
    Preconditions.checkArgument(IterableExtensions.isEmpty(proxies), "%s \'%s\' should not contain proxies, but contains the following: %s", stateNotice, 
      resource.getURI(), String.join(", ", IterableExtensions.<EObject, String>map(proxies, _function)));
  }

  @Override
  public VitruviusChange<HierarchicalId> getChangeSequenceBetween(final Resource newState, final Resource oldState) {
    Preconditions.checkArgument(((oldState != null) && (newState != null)), "old state or new state must not be null!");
    this.checkNoProxies(newState, "new state");
    this.checkNoProxies(oldState, "old state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet);
    final Procedure0 _function = () -> {
      URI _uRI = oldState.getURI();
      URI _uRI_1 = newState.getURI();
      boolean _notEquals = (!Objects.equal(_uRI, _uRI_1));
      if (_notEquals) {
        currentStateCopy.setURI(newState.getURI());
      }
      this.compareStatesAndReplayChanges(newState, currentStateCopy);
    };
    return this.<Notifier>record(currentStateCopy, _function);
  }

  @Override
  public VitruviusChange<HierarchicalId> getChangeSequenceForCreated(final Resource newState) {
    Preconditions.checkArgument((newState != null), "new state must not be null!");
    this.checkNoProxies(newState, "new state");
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    final ResourceSet monitoredResourceSet = ResourceSetUtil.withGlobalFactories(_resourceSetImpl);
    final Resource newResource = monitoredResourceSet.createResource(newState.getURI());
    newResource.getContents().clear();
    final Procedure0 _function = () -> {
      EList<EObject> _contents = newResource.getContents();
      Collection<EObject> _copyAll = EcoreUtil.<EObject>copyAll(newState.getContents());
      Iterables.<EObject>addAll(_contents, _copyAll);
    };
    return this.<Notifier>record(newResource, _function);
  }

  @Override
  public VitruviusChange<HierarchicalId> getChangeSequenceForDeleted(final Resource oldState) {
    Preconditions.checkArgument((oldState != null), "old state must not be null!");
    this.checkNoProxies(oldState, "old state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet);
    final Procedure0 _function = () -> {
      currentStateCopy.getContents().clear();
    };
    return this.<Notifier>record(currentStateCopy, _function);
  }

  private <T extends Notifier> VitruviusChange<HierarchicalId> record(final Resource resource, final Procedure0 function) {
    try (final ChangeRecorder changeRecorder = new ChangeRecorder(resource.getResourceSet())) {
      changeRecorder.beginRecording();
      changeRecorder.addToRecording(resource);
      function.apply();
      final TransactionalChange<EObject> recordedChanges = changeRecorder.endRecording();
      final VitruviusChangeResolver<HierarchicalId> changeResolver = VitruviusChangeResolver.forHierarchicalIds(resource.getResourceSet());
      return changeResolver.assignIds(recordedChanges);
    }
  }

  /**
   * Compares states using the EMFCompareInstance provided by {@link #getEMFCompareInstance()} and replays the changes to the current state.
   */
  private void compareStatesAndReplayChanges(final Notifier newState, final Notifier currentState) {
    final DefaultComparisonScope scope = new DefaultComparisonScope(newState, currentState, null);
    final EList<Diff> differences = this.buildEMFCompare().compare(scope).getDifferences();
    final IMerger.Registry mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance();
    final BatchMerger merger = new BatchMerger(mergerRegistry);
    BasicMonitor _basicMonitor = new BasicMonitor();
    merger.copyAllLeftToRight(differences, _basicMonitor);
  }

  private EMFCompare buildEMFCompare() {
    final IMatchEngine.Factory.Registry usedMatchEngineFactoryRegistry = MatchEngineFactoryRegistryImpl.createStandaloneInstance();
    final Consumer<IMatchEngine.Factory> _function = (IMatchEngine.Factory it) -> {
      it.setRanking(20);
      usedMatchEngineFactoryRegistry.add(it);
    };
    this.getMatchEngineFactories().forEach(_function);
    final PostProcessorDescriptorRegistryImpl<String> usedPostProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
    final Consumer<IPostProcessor.Descriptor> _function_1 = (IPostProcessor.Descriptor it) -> {
      usedPostProcessorRegistry.put(it.getInstanceClassName(), it);
    };
    this.getPostProcessors().forEach(_function_1);
    EMFCompare.Builder _builder = EMFCompare.builder();
    final Procedure1<EMFCompare.Builder> _function_2 = (EMFCompare.Builder it) -> {
      it.setMatchEngineFactoryRegistry(usedMatchEngineFactoryRegistry);
      it.setDiffEngine(this.getDiffEngine());
      it.setPostProcessorRegistry(usedPostProcessorRegistry);
    };
    return ObjectExtensions.<EMFCompare.Builder>operator_doubleArrow(_builder, _function_2).build();
  }

  /**
   * {@link Factory}'s (MatchEngine + rank + is MatchEngine suitable for comparison scope) used by EMFCompare
   * The rank will be overwritten by this class. To ensure that it has a greater value than the default implementation.
   */
  protected abstract Collection<IMatchEngine.Factory> getMatchEngineFactories();

  /**
   * {@link IDiffEngine} used by EMFCompare
   */
  protected abstract IDiffEngine getDiffEngine();

  /**
   * {@link IPostProcessor.Descriptor}'s (PostProcessor + Pattern where to be applied) used by EMFCompare
   */
  protected abstract Collection<IPostProcessor.Descriptor> getPostProcessors();
}
