package tools.vitruv.change.propagation.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.CompositeChange;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.PendingUserInteractionException;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.propagation.ChangePropagationObserver;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationProvider;
import tools.vitruv.change.propagation.ChangeRecordingModelRepository;
import tools.vitruv.change.propagation.NonDeferrablePropagationException;

public class ChangePropagator {
  private static class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
    private final ChangePropagator outer;

    private final VitruviusChange<EObject> sourceChange;

    private final ChangePropagator.ChangePropagation previous;

    private final Set<Resource> changedResources = new HashSet<Resource>();

    private final List<EObject> createdObjects = new ArrayList<EObject>();

    private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

    private List<PropagatedChange> propagateChanges() {
      List<PropagatedChange> result = StreamSupport.stream(this.outer.getTransactionalChangeSequence(this.sourceChange).spliterator(), false)
          .flatMap(it -> this.propagateSingleChange(it).stream())
          .collect(Collectors.toList());
      this.handleObjectsWithoutResource();
      this.changedResources.forEach(it -> it.setModified(true));
      return result;
    }

    /** The outcome of propagating one transactional change through all specifications once. */
    private record StepResult(
        PropagatedChange propagatedChange, List<TransactionalChange<EObject>> resultChanges) {}

    private List<PropagatedChange> propagateSingleChange(final TransactionalChange<EObject> change) {
      final StepResult step = this.propagateOneStep(change);
      final ArrayList<PropagatedChange> resultingChanges = new ArrayList<PropagatedChange>();
      resultingChanges.add(step.propagatedChange());
      if (!Objects.equals(this.outer.changePropagationMode, ChangePropagationMode.SINGLE_STEP)) {
        try {
          Iterable<PropagatedChange> transitivelyPropagated =
              this.propagateTransitiveChanges(
                  step.resultChanges().stream()
                      .filter(TransactionalChange::containsConcreteChange)
                      .toList());
          Iterables.<PropagatedChange>addAll(resultingChanges, transitivelyPropagated);
        } catch (RuntimeException e) {
          // Same wrapping as when transitive propagation ran inside propagateOneStep's try block.
          // Only runtime exceptions get here: each nested step already wraps everything it throws.
          throw new RuntimeException(e);
        }
      }
      return resultingChanges;
    }

    /**
     * Propagates a change as a single step for {@link ChangePropagationQueue}: one propagation step
     * followed by the bookkeeping {@link #propagateChanges()} does at the end of a propagation.
     */
    private StepResult propagateQueuedStep(final TransactionalChange<EObject> change) {
      final StepResult step = this.propagateOneStep(change);
      this.handleObjectsWithoutResource();
      this.changedResources.forEach(it -> it.setModified(true));
      return step;
    }

    /**
     * Propagates the given change through every applicable specification once, without propagating
     * the resulting changes further.
     */
    private StepResult propagateOneStep(final TransactionalChange<EObject> change) {
      try {
        Preconditions.checkState(!change.getAffectedEObjects().isEmpty(),
          "There are no objects affected by this change:%s%s", System.lineSeparator(), change);
        final AutoCloseable userInteractorChange = this.installUserInteractorForChange(change);
        this.outer.changePropagationProvider.forEach(it -> it.registerObserver(this));
        this.outer.userInteractor.registerUserInputListener(this);
        List<TransactionalChange<EObject>> _xtrycatchfinallyexpression = null;
        try {
          Set<ChangePropagationSpecification> allSpecs = this.sourceChange.getAffectedEObjectsMetamodelDescriptors().stream()
              .flatMap(it -> {
                List<ChangePropagationSpecification> specs = this.outer.changePropagationProvider.getChangePropagationSpecifications(it);
                specs.forEach(s -> s.setUserInteractor(this.outer.userInteractor));
                return specs.stream();
              })
              .collect(Collectors.toCollection(LinkedHashSet::new));
          _xtrycatchfinallyexpression = this.propagateThroughSpecifications(change, allSpecs);
        } finally {
          this.outer.userInteractor.deregisterUserInputListener(this);
          this.outer.changePropagationProvider.forEach(it -> it.deregisterObserver(this));
          userInteractorChange.close();
        }
        final List<TransactionalChange<EObject>> propagationResultChanges = _xtrycatchfinallyexpression;
        if (ChangePropagator.logger.isDebugEnabled()) {
          String path = String.join(" -> ", this.getPropagationPath());
          String changes = propagationResultChanges.stream()
              .map(c -> String.valueOf(c.getAffectedEObjectsMetamodelDescriptors()))
              .collect(Collectors.joining(", "));
          ChangePropagator.logger.debug("Propagated " + path + " -> {" + changes + "}");
        }
        if (ChangePropagator.logger.isTraceEnabled()) {
          String resultChanges = propagationResultChanges.stream()
              .map(r -> "\t" + r.getAffectedEObjectsMetamodelDescriptors() + ": " + r)
              .collect(Collectors.joining("\n"));
          ChangePropagator.logger.trace("Result changes:\n" + resultChanges);
        }
        change.setUserInteractions(this.userInteractions);
        CompositeContainerChange<EObject> _createCompositeChange = VitruviusChangeFactory.getInstance().<EObject>createCompositeChange(propagationResultChanges);
        final PropagatedChange propagatedChange = new PropagatedChange(change, _createCompositeChange);
        return new StepResult(propagatedChange, propagationResultChanges);
      } catch (Throwable _e) {
        throw new RuntimeException(_e);
      }
    }

    /**
     * Propagates the change through the given specifications in order. A pending user interaction
     * after an earlier specification already modified models is reported as non-deferrable.
     */
    private List<TransactionalChange<EObject>> propagateThroughSpecifications(
        final TransactionalChange<EObject> change,
        final Set<ChangePropagationSpecification> specifications) {
      final List<TransactionalChange<EObject>> results = new ArrayList<>();
      for (final ChangePropagationSpecification specification : specifications) {
        try {
          Iterables.addAll(
              results,
              this.propagateChangeForChangePropagationSpecification(change, specification));
        } catch (final RuntimeException e) {
          // Only the changes of the specifications that already finished are known here. If this
          // specification modified models before asking for input, its recording is lost with the
          // exception, so specifications are assumed to ask for user input before modifying models.
          if (PendingUserInteractionException.findIn(e) != null
              && results.stream().anyMatch(TransactionalChange::containsConcreteChange)) {
            throw new NonDeferrablePropagationException(
                "A user interaction is pending, but propagating this change already modified"
                    + " models, so it cannot be deferred and retried without repeating those"
                    + " modifications: "
                    + change,
                e);
          }
          throw e;
        }
      }
      return results;
    }

    private Iterable<PropagatedChange> propagateTransitiveChanges(
        final Iterable<TransactionalChange<EObject>> transitiveChanges) {
      List<TransactionalChange<EObject>> nonLeafChanges =
          this.outer.selectTransitiveChanges(transitiveChanges);
      List<ChangePropagator.ChangePropagation> nextPropagations = nonLeafChanges.stream()
          .map(it -> new ChangePropagator.ChangePropagation(this.outer, it, this))
          .collect(Collectors.toList());
      return Iterables.concat(nextPropagations.stream()
          .map(it -> it.propagateChanges())
          .collect(Collectors.toList()));
    }

    private Iterable<TransactionalChange<EObject>> propagateChangeForChangePropagationSpecification(final TransactionalChange<EObject> change, final ChangePropagationSpecification propagationSpecification) {
      final Runnable _function = () -> {
        for (final EChange<EObject> eChange : change.getEChanges()) {
          propagationSpecification.propagateChange(eChange, change,
              this.outer.modelRepository.getCorrespondenceModel(), this.outer.modelRepository);
        }
      };
      final Iterable<TransactionalChange<EObject>> transitiveChanges = this.outer.modelRepository.recordChanges(_function);
      StreamSupport.stream(transitiveChanges.spliterator(), false)
          .flatMap(it -> it.getAffectedEObjects().stream())
          .map(EObject::eResource)
          .filter(Objects::nonNull)
          .forEach(this.changedResources::add);
      return transitiveChanges;
    }

    private AutoCloseable installUserInteractorForChange(final VitruviusChange<EObject> change) {
      final Iterable<UserInteractionBase> pastUserInputsFromChange = change.getUserInteractions();
      if (pastUserInputsFromChange != null && pastUserInputsFromChange.iterator().hasNext()) {
        return this.outer.userInteractor.replaceUserInteractionResultProvider(
            (InteractionResultProvider currentProvider) -> UserInteractionFactory.instance.createPredefinedInteractionResultProvider(
                currentProvider, Iterables.toArray(pastUserInputsFromChange, UserInteractionBase.class)));
      } else {
        return () -> {};
      }
    }

    private void handleObjectsWithoutResource() {
      List<EObject> objectsWithoutResource = this.createdObjects.stream()
          .filter(it -> it.eResource() == null)
          .collect(Collectors.toList());
      for (final EObject createdObjectWithoutResource : objectsWithoutResource) {
        Preconditions.checkState(
            !this.outer.modelRepository.getCorrespondenceModel().hasCorrespondences(createdObjectWithoutResource),
            "The object %s is part of a correspondence to %s but not in any resource", createdObjectWithoutResource,
            this.outer.modelRepository.getCorrespondenceModel().getCorrespondingEObjects(createdObjectWithoutResource));
        ChangePropagator.logger.warn("Object was created but has no correspondence and is thus lost: " + createdObjectWithoutResource);
      }
    }

    @Override
    public void objectCreated(final EObject createdObject) {
      this.createdObjects.add(createdObject);
    }

    @Override
    public void changePropagationStarted(final ChangePropagationSpecification specification, final EChange<EObject> change) {
      return;
    }

    @Override
    public void changePropagationStopped(final ChangePropagationSpecification specification, final EChange<EObject> change) {
      return;
    }

    @Override
    public void onUserInteractionReceived(final UserInteractionBase interaction) {
      this.userInteractions.add(interaction);
    }

    @Override
    public String toString() {
      return "propagate " + String.join(" -> ", this.getPropagationPath()) + ": " + this.sourceChange;
    }

    private Iterable<String> getPropagationPath() {
      if (this.previous == null) {
        return List.of("<input change> in " + this.sourceChange.getAffectedEObjectsMetamodelDescriptors());
      } else {
        return Iterables.concat(this.previous.getPropagationPath(),
            List.of(this.sourceChange.getAffectedEObjectsMetamodelDescriptors().toString()));
      }
    }

    public ChangePropagation(final ChangePropagator outer, final VitruviusChange<EObject> sourceChange, final ChangePropagator.ChangePropagation previous) {
      super();
      this.outer = outer;
      this.sourceChange = sourceChange;
      this.previous = previous;
    }
  }

  private static final Logger logger = LogManager.getLogger(ChangePropagator.class);

  private final ChangeRecordingModelRepository modelRepository;

  private final ChangePropagationSpecificationProvider changePropagationProvider;

  private final InternalUserInteractor userInteractor;

  private final ChangePropagationMode changePropagationMode;

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated transitively and cyclic, i.e. with
   * {@link ChangePropagationMode#TRANSITIVE_CYCLIC}.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository, final ChangePropagationSpecificationProvider changePropagationProvider, final InternalUserInteractor userInteractor) {
    this(modelRepository, changePropagationProvider, userInteractor, ChangePropagationMode.TRANSITIVE_CYCLIC);
  }

  /**
   * Creates a change propagator to which changes can be passed, which are
   * propagated using the given <code>changePropagationProvider</code> and
   * <code>userInteractor</code>.
   * Changes are recorded in the given <code>modelRepository</code> and
   * propagated using the given <code>mode</code>.
   */
  public ChangePropagator(final ChangeRecordingModelRepository modelRepository, final ChangePropagationSpecificationProvider changePropagationProvider, final InternalUserInteractor userInteractor, final ChangePropagationMode mode) {
    this.modelRepository = modelRepository;
    this.changePropagationProvider = changePropagationProvider;
    this.userInteractor = userInteractor;
    this.changePropagationMode = mode;
  }

  /**
   * Applies, then propagates <code>change</code>
   * through the models in <code>modelRepository</code>.
   *
   * @param change - {@link VitruviusChange}
   * @param observers - {@link Iterable} of {@link ChangePropagationObserver}
   * @return - {@link List} of {@link PropagatedChange}
   */
  public List<PropagatedChange> propagateChange(final VitruviusChange<Uuid> change, final Iterable<ChangePropagationObserver> observers) {
    final VitruviusChange<EObject> resolvedChange = this.applyAndMarkModified(change);
    if (ChangePropagator.logger.isTraceEnabled()) {
      ChangePropagator.logger.trace("Will now propagate this input change:\n\t" + resolvedChange);
    }
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    List<PropagatedChange> result = new ChangePropagator.ChangePropagation(this, resolvedChange, null).propagateChanges();
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    return result;
  }

  /**
   * Selects which of the changes produced by a propagation step are propagated further, according
   * to the {@link ChangePropagationMode}: only changes with concrete changes, and for {@link
   * ChangePropagationMode#TRANSITIVE_EXCEPT_LEAVES} only those that more than one specification
   * handles.
   */
  private List<TransactionalChange<EObject>> selectTransitiveChanges(
      final Iterable<TransactionalChange<EObject>> transitiveChanges) {
    List<TransactionalChange<EObject>> nonEmptyChanges =
        StreamSupport.stream(transitiveChanges.spliterator(), false)
            .filter(TransactionalChange::containsConcreteChange)
            .collect(Collectors.toList());
    if (Objects.equals(
        this.changePropagationMode, ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES)) {
      return nonEmptyChanges.stream()
          .filter(
              it ->
                  this.changePropagationProvider
                          .getChangePropagationSpecifications(
                              it.getAffectedEObjectsMetamodelDescriptor())
                          .size()
                      > 1)
          .collect(Collectors.toList());
    }
    return nonEmptyChanges;
  }

  /**
   * Result of {@link #propagateSingleStep}.
   *
   * @param propagatedChange the propagated change
   * @param followUpChanges the resulting changes to propagate next, according to the {@link
   *     ChangePropagationMode}
   */
  record SingleStepResult(
      PropagatedChange propagatedChange, List<TransactionalChange<EObject>> followUpChanges) {}

  /** Returns the user interactor the specifications ask for user input. */
  InternalUserInteractor getUserInteractor() {
    return this.userInteractor;
  }

  /**
   * Applies <code>change</code> to the models without propagating it.
   *
   * @return the transactional changes to propagate, in order
   */
  List<TransactionalChange<EObject>> applyWithoutPropagating(final VitruviusChange<Uuid> change) {
    final List<TransactionalChange<EObject>> parts = new ArrayList<>();
    this.getTransactionalChangeSequence(this.applyAndMarkModified(change)).forEach(parts::add);
    return parts;
  }

  /**
   * Propagates <code>change</code> through every applicable specification once, without
   * propagating the resulting changes further.
   *
   * @param change - the applied change to propagate
   * @param observers - observers to register at the specifications while propagating
   * @return - the propagated change and the changes to propagate next
   */
  SingleStepResult propagateSingleStep(
      final TransactionalChange<EObject> change,
      final Iterable<ChangePropagationObserver> observers) {
    this.changePropagationProvider.forEach(spec -> observers.forEach(spec::registerObserver));
    try {
      final ChangePropagation.StepResult step =
          new ChangePropagation(this, change, null).propagateQueuedStep(change);
      final List<TransactionalChange<EObject>> followUps =
          Objects.equals(this.changePropagationMode, ChangePropagationMode.SINGLE_STEP)
              ? List.of()
              : this.selectTransitiveChanges(step.resultChanges());
      return new SingleStepResult(step.propagatedChange(), followUps);
    } finally {
      this.changePropagationProvider.forEach(spec -> observers.forEach(spec::deregisterObserver));
    }
  }

  private VitruviusChange<EObject> applyAndMarkModified(final VitruviusChange<Uuid> change) {
    final VitruviusChange<EObject> resolvedChange = this.modelRepository.applyChange(change);
    resolvedChange.getAffectedEObjects().stream()
        .map(EObject::eResource)
        .filter(Objects::nonNull)
        .forEach(it -> it.setModified(true));
    return resolvedChange;
  }

  private Iterable<TransactionalChange<EObject>> getTransactionalChangeSequence(final VitruviusChange<EObject> change) {
    if (!change.containsConcreteChange()) {
      return List.of();
    }
    if (change instanceof TransactionalChange) {
      return List.of((TransactionalChange<EObject>) change);
    }
    if (change instanceof CompositeChange) {
      return ((CompositeChange<EObject, ?>) change).getChanges().stream()
          .flatMap(it -> StreamSupport.stream(this.getTransactionalChangeSequence(it).spliterator(), false))
          .collect(Collectors.toList());
    }
    throw new IllegalStateException("Unexpected change type: " + change.getClass().getSimpleName());
  }
}
