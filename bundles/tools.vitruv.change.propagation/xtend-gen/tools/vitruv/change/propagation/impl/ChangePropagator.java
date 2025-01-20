package tools.vitruv.change.propagation.impl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
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
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.propagation.ChangePropagationObserver;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecificationProvider;
import tools.vitruv.change.propagation.ChangeRecordingModelRepository;

@SuppressWarnings("all")
public class ChangePropagator {
  @FinalFieldsConstructor
  private static class ChangePropagation implements ChangePropagationObserver, UserInteractionListener {
    @Extension
    private final ChangePropagator outer;

    private final VitruviusChange<EObject> sourceChange;

    private final ChangePropagator.ChangePropagation previous;

    private final Set<Resource> changedResources = new HashSet<Resource>();

    private final List<EObject> createdObjects = new ArrayList<EObject>();

    private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

    private List<PropagatedChange> propagateChanges() {
      final Function1<TransactionalChange<EObject>, List<PropagatedChange>> _function = (TransactionalChange<EObject> it) -> {
        return this.propagateSingleChange(it);
      };
      final List<PropagatedChange> result = IterableUtil.<TransactionalChange<EObject>, PropagatedChange>flatMapFixed(this.outer.getTransactionalChangeSequence(this.sourceChange), _function);
      this.handleObjectsWithoutResource();
      final Consumer<Resource> _function_1 = (Resource it) -> {
        it.setModified(true);
      };
      this.changedResources.forEach(_function_1);
      return result;
    }

    private List<PropagatedChange> propagateSingleChange(final TransactionalChange<EObject> change) {
      try {
        boolean _isNullOrEmpty = IterableExtensions.isNullOrEmpty(change.getAffectedEObjects());
        boolean _not = (!_isNullOrEmpty);
        Preconditions.checkState(_not, "There are no objects affected by this change:%s%s", 
          System.lineSeparator(), change);
        final AutoCloseable userInteractorChange = this.installUserInteractorForChange(change);
        final Consumer<ChangePropagationSpecification> _function = (ChangePropagationSpecification it) -> {
          it.registerObserver(this);
        };
        this.outer.changePropagationProvider.forEach(_function);
        this.outer.userInteractor.registerUserInputListener(this);
        List<TransactionalChange<EObject>> _xtrycatchfinallyexpression = null;
        try {
          final Function1<MetamodelDescriptor, List<ChangePropagationSpecification>> _function_1 = (MetamodelDescriptor it) -> {
            List<ChangePropagationSpecification> _changePropagationSpecifications = this.outer.changePropagationProvider.getChangePropagationSpecifications(it);
            final Procedure1<List<ChangePropagationSpecification>> _function_2 = (List<ChangePropagationSpecification> it_1) -> {
              final Consumer<ChangePropagationSpecification> _function_3 = (ChangePropagationSpecification it_2) -> {
                it_2.setUserInteractor(this.outer.userInteractor);
              };
              it_1.forEach(_function_3);
            };
            return ObjectExtensions.<List<ChangePropagationSpecification>>operator_doubleArrow(_changePropagationSpecifications, _function_2);
          };
          final Function1<ChangePropagationSpecification, Iterable<TransactionalChange<EObject>>> _function_2 = (ChangePropagationSpecification it) -> {
            return this.propagateChangeForChangePropagationSpecification(change, it);
          };
          _xtrycatchfinallyexpression = IterableUtil.<ChangePropagationSpecification, TransactionalChange<EObject>>flatMapFixed(IterableExtensions.<ChangePropagationSpecification>toSet(IterableExtensions.<MetamodelDescriptor, ChangePropagationSpecification>flatMap(this.sourceChange.getAffectedEObjectsMetamodelDescriptors(), _function_1)), _function_2);
        } finally {
          this.outer.userInteractor.deregisterUserInputListener(this);
          final Consumer<ChangePropagationSpecification> _function_3 = (ChangePropagationSpecification it) -> {
            it.deregisterObserver(this);
          };
          this.outer.changePropagationProvider.forEach(_function_3);
          userInteractorChange.close();
        }
        final List<TransactionalChange<EObject>> propagationResultChanges = _xtrycatchfinallyexpression;
        boolean _isDebugEnabled = ChangePropagator.logger.isDebugEnabled();
        if (_isDebugEnabled) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Propagated ");
          {
            Iterable<String> _propagationPath = this.getPropagationPath();
            boolean _hasElements = false;
            for(final String p : _propagationPath) {
              if (!_hasElements) {
                _hasElements = true;
              } else {
                _builder.appendImmediate(" -> ", "");
              }
              _builder.append(p);
            }
          }
          _builder.append(" -> {");
          {
            boolean _hasElements_1 = false;
            for(final TransactionalChange<EObject> changeInPropagation : propagationResultChanges) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
              } else {
                _builder.appendImmediate(", ", "");
              }
              Set<MetamodelDescriptor> _affectedEObjectsMetamodelDescriptors = changeInPropagation.getAffectedEObjectsMetamodelDescriptors();
              _builder.append(_affectedEObjectsMetamodelDescriptors);
            }
          }
          _builder.append("}");
          ChangePropagator.logger.debug(_builder);
        }
        boolean _isTraceEnabled = ChangePropagator.logger.isTraceEnabled();
        if (_isTraceEnabled) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("Result changes:");
          _builder_1.newLine();
          {
            for(final TransactionalChange<EObject> result : propagationResultChanges) {
              _builder_1.append("\t");
              Set<MetamodelDescriptor> _affectedEObjectsMetamodelDescriptors_1 = result.getAffectedEObjectsMetamodelDescriptors();
              _builder_1.append(_affectedEObjectsMetamodelDescriptors_1, "\t");
              _builder_1.append(": ");
              _builder_1.append(result, "\t");
              _builder_1.newLineIfNotEmpty();
            }
          }
          ChangePropagator.logger.trace(_builder_1);
        }
        change.setUserInteractions(this.userInteractions);
        CompositeContainerChange<EObject> _createCompositeChange = VitruviusChangeFactory.getInstance().<EObject>createCompositeChange(propagationResultChanges);
        final PropagatedChange propagatedChange = new PropagatedChange(change, _createCompositeChange);
        final ArrayList<PropagatedChange> resultingChanges = new ArrayList<PropagatedChange>();
        resultingChanges.add(propagatedChange);
        boolean _notEquals = (!Objects.equal(this.outer.changePropagationMode, ChangePropagationMode.SINGLE_STEP));
        if (_notEquals) {
          final Function1<TransactionalChange<EObject>, Boolean> _function_1 = (TransactionalChange<EObject> it) -> {
            return Boolean.valueOf(it.containsConcreteChange());
          };
          Iterable<PropagatedChange> _propagateTransitiveChanges = this.propagateTransitiveChanges(IterableExtensions.<TransactionalChange<EObject>>filter(propagationResultChanges, _function_1));
          Iterables.<PropagatedChange>addAll(resultingChanges, _propagateTransitiveChanges);
        }
        return resultingChanges;
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    }

    private Iterable<PropagatedChange> propagateTransitiveChanges(final Iterable<TransactionalChange<EObject>> transitiveChanges) {
      final Function1<TransactionalChange<EObject>, Boolean> _function = (TransactionalChange<EObject> it) -> {
        return Boolean.valueOf(it.containsConcreteChange());
      };
      final Iterable<TransactionalChange<EObject>> nonEmptyChanges = IterableExtensions.<TransactionalChange<EObject>>filter(transitiveChanges, _function);
      Iterable<TransactionalChange<EObject>> _xifexpression = null;
      boolean _equals = Objects.equal(this.outer.changePropagationMode, ChangePropagationMode.TRANSITIVE_EXCEPT_LEAVES);
      if (_equals) {
        final Function1<TransactionalChange<EObject>, Boolean> _function_1 = (TransactionalChange<EObject> it) -> {
          final List<ChangePropagationSpecification> targetSpecifications = this.outer.changePropagationProvider.getChangePropagationSpecifications(
            it.getAffectedEObjectsMetamodelDescriptor());
          int _size = targetSpecifications.size();
          return Boolean.valueOf((_size > 1));
        };
        _xifexpression = IterableExtensions.<TransactionalChange<EObject>>filter(nonEmptyChanges, _function_1);
      } else {
        _xifexpression = nonEmptyChanges;
      }
      final Iterable<TransactionalChange<EObject>> nonLeafChanges = _xifexpression;
      final Function1<TransactionalChange<EObject>, ChangePropagator.ChangePropagation> _function_2 = (TransactionalChange<EObject> it) -> {
        return new ChangePropagator.ChangePropagation(this.outer, it, this);
      };
      final List<ChangePropagator.ChangePropagation> nextPropagations = IterableUtil.<TransactionalChange<EObject>, ChangePropagator.ChangePropagation>mapFixed(nonLeafChanges, _function_2);
      final Function1<ChangePropagator.ChangePropagation, List<PropagatedChange>> _function_3 = (ChangePropagator.ChangePropagation it) -> {
        return it.propagateChanges();
      };
      return Iterables.<PropagatedChange>concat(IterableUtil.<ChangePropagator.ChangePropagation, List<PropagatedChange>>mapFixed(nextPropagations, _function_3));
    }

    private Iterable<TransactionalChange<EObject>> propagateChangeForChangePropagationSpecification(final TransactionalChange<EObject> change, final ChangePropagationSpecification propagationSpecification) {
      final Runnable _function = () -> {
        List<EChange<EObject>> _eChanges = change.getEChanges();
        for (final EChange<EObject> eChange : _eChanges) {
          propagationSpecification.propagateChange(eChange, this.outer.modelRepository.getCorrespondenceModel(), 
            this.outer.modelRepository);
        }
      };
      final Iterable<TransactionalChange<EObject>> transitiveChanges = this.outer.modelRepository.recordChanges(_function);
      final Function1<TransactionalChange<EObject>, Set<EObject>> _function_1 = (TransactionalChange<EObject> it) -> {
        return it.getAffectedEObjects();
      };
      final Function1<EObject, Resource> _function_2 = (EObject it) -> {
        return it.eResource();
      };
      Iterable<Resource> _filterNull = IterableExtensions.<Resource>filterNull(IterableExtensions.<EObject, Resource>map(IterableExtensions.<TransactionalChange<EObject>, EObject>flatMap(transitiveChanges, _function_1), _function_2));
      Iterables.<Resource>addAll(this.changedResources, _filterNull);
      return transitiveChanges;
    }

    private AutoCloseable installUserInteractorForChange(final VitruviusChange<EObject> change) {
      AutoCloseable _xblockexpression = null;
      {
        final Iterable<UserInteractionBase> pastUserInputsFromChange = change.getUserInteractions();
        AutoCloseable _xifexpression = null;
        boolean _isNullOrEmpty = IterableExtensions.isNullOrEmpty(pastUserInputsFromChange);
        boolean _not = (!_isNullOrEmpty);
        if (_not) {
          final Function1<InteractionResultProvider, InteractionResultProvider> _function = (InteractionResultProvider currentProvider) -> {
            return UserInteractionFactory.instance.createPredefinedInteractionResultProvider(currentProvider, ((UserInteractionBase[])Conversions.unwrapArray(pastUserInputsFromChange, UserInteractionBase.class)));
          };
          _xifexpression = this.outer.userInteractor.replaceUserInteractionResultProvider(_function);
        } else {
          final AutoCloseable _function_1 = () -> {
          };
          _xifexpression = _function_1;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }

    private void handleObjectsWithoutResource() {
      final Function1<EObject, Boolean> _function = (EObject it) -> {
        Resource _eResource = it.eResource();
        return Boolean.valueOf((_eResource == null));
      };
      Iterable<EObject> _filter = IterableExtensions.<EObject>filter(this.createdObjects, _function);
      for (final EObject createdObjectWithoutResource : _filter) {
        {
          boolean _hasCorrespondences = this.outer.modelRepository.getCorrespondenceModel().hasCorrespondences(createdObjectWithoutResource);
          boolean _not = (!_hasCorrespondences);
          Preconditions.checkState(_not, 
            "The object %s is part of a correspondence to %s but not in any resource", createdObjectWithoutResource, 
            this.outer.modelRepository.getCorrespondenceModel().getCorrespondingEObjects(createdObjectWithoutResource));
          ChangePropagator.logger.warn(("Object was created but has no correspondence and is thus lost: " + createdObjectWithoutResource));
        }
      }
    }

    @Override
    public void objectCreated(final EObject createdObject) {
      this.createdObjects.add(createdObject);
    }

    @Override
    public void onUserInteractionReceived(final UserInteractionBase interaction) {
      this.userInteractions.add(interaction);
    }

    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("propagate ");
      {
        Iterable<String> _propagationPath = this.getPropagationPath();
        boolean _hasElements = false;
        for(final String p : _propagationPath) {
          if (!_hasElements) {
            _hasElements = true;
          } else {
            _builder.appendImmediate(" -> ", "");
          }
          _builder.append(p);
        }
      }
      _builder.append(": ");
      _builder.append(this.sourceChange);
      return _builder.toString();
    }

    private Iterable<String> getPropagationPath() {
      Iterable<String> _xifexpression = null;
      if ((this.previous == null)) {
        String _string = this.sourceChange.getAffectedEObjectsMetamodelDescriptors().toString();
        String _plus = ("<input change> in " + _string);
        _xifexpression = List.<String>of(_plus);
      } else {
        Iterable<String> _propagationPath = this.previous.getPropagationPath();
        List<String> _of = List.<String>of(this.sourceChange.getAffectedEObjectsMetamodelDescriptors().toString());
        _xifexpression = Iterables.<String>concat(_propagationPath, _of);
      }
      return _xifexpression;
    }

    public ChangePropagation(final ChangePropagator outer, final VitruviusChange<EObject> sourceChange, final ChangePropagator.ChangePropagation previous) {
      super();
      this.outer = outer;
      this.sourceChange = sourceChange;
      this.previous = previous;
    }
  }

  private static final Logger logger = Logger.getLogger(ChangePropagator.class);

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

  public List<PropagatedChange> propagateChange(final VitruviusChange<Uuid> change) {
    final VitruviusChange<EObject> resolvedChange = this.modelRepository.applyChange(change);
    final Function1<EObject, Resource> _function = (EObject it) -> {
      return it.eResource();
    };
    final Consumer<Resource> _function_1 = (Resource it) -> {
      it.setModified(true);
    };
    IterableExtensions.<Resource>filterNull(IterableExtensions.<EObject, Resource>map(resolvedChange.getAffectedEObjects(), _function)).forEach(_function_1);
    boolean _isTraceEnabled = ChangePropagator.logger.isTraceEnabled();
    if (_isTraceEnabled) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Will now propagate this input change:");
      _builder.newLine();
      _builder.append("\t");
      _builder.append(resolvedChange, "\t");
      _builder.newLineIfNotEmpty();
      ChangePropagator.logger.trace(_builder);
    }
    return new ChangePropagator.ChangePropagation(this, resolvedChange, null).propagateChanges();
  }

  private Iterable<TransactionalChange<EObject>> getTransactionalChangeSequence(final VitruviusChange<EObject> change) {
    Iterable<TransactionalChange<EObject>> _switchResult = null;
    boolean _matched = false;
    boolean _containsConcreteChange = change.containsConcreteChange();
    boolean _not = (!_containsConcreteChange);
    if (_not) {
      _matched=true;
      _switchResult = CollectionLiterals.<TransactionalChange<EObject>>emptyList();
    }
    if (!_matched) {
      if (change instanceof TransactionalChange) {
        _matched=true;
        _switchResult = List.<TransactionalChange<EObject>>of(((TransactionalChange<EObject>)change));
      }
    }
    if (!_matched) {
      if (change instanceof CompositeChange) {
        _matched=true;
        final Function1<VitruviusChange<EObject>, Iterable<TransactionalChange<EObject>>> _function = (VitruviusChange<EObject> it) -> {
          return this.getTransactionalChangeSequence(it);
        };
        _switchResult = IterableExtensions.flatMap(((CompositeChange<EObject, ?>)change).getChanges(), _function);
      }
    }
    if (!_matched) {
      String _simpleName = change.getClass().getSimpleName();
      String _plus = ("Unexpected change type: " + _simpleName);
      throw new IllegalStateException(_plus);
    }
    return _switchResult;
  }
}
