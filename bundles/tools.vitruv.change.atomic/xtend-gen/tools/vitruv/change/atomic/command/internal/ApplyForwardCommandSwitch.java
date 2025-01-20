package tools.vitruv.change.atomic.command.internal;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.command.RemoveAtCommand;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Switch to create commands for all EChange classes.
 * The commands applies the EChanges forward.
 */
@Utility
@SuppressWarnings("all")
final class ApplyForwardCommandSwitch {
  private static final Logger logger = Logger.getLogger(ApplyForwardCommandSwitch.class);

  static List<Command> _getCommands(final EChange<EObject> change) {
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList());
  }

  /**
   * Dispatch method to create commands to apply a {@link UnsetFeature} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final UnsetFeature<EObject, ?> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getAffectedElement());
    EObject _affectedElement = change.getAffectedElement();
    EStructuralFeature _affectedFeature = change.getAffectedFeature();
    SetCommand _setCommand = new SetCommand(editingDomain, _affectedElement, _affectedFeature, SetCommand.UNSET_VALUE);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_setCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link InsertEAttributeValue} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final InsertEAttributeValue<EObject, ?> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getAffectedElement());
    EObject _affectedElement = change.getAffectedElement();
    EAttribute _affectedFeature = change.getAffectedFeature();
    Object _newValue = change.getNewValue();
    int _index = change.getIndex();
    AddCommand _addCommand = new AddCommand(editingDomain, _affectedElement, _affectedFeature, _newValue, _index);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_addCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link RemoveEAttributeValue} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final RemoveEAttributeValue<EObject, ?> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getAffectedElement());
    EObject _affectedElement = change.getAffectedElement();
    EAttribute _affectedFeature = change.getAffectedFeature();
    Object _oldValue = change.getOldValue();
    int _index = change.getIndex();
    RemoveAtCommand _removeAtCommand = new RemoveAtCommand(editingDomain, _affectedElement, _affectedFeature, _oldValue, _index);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_removeAtCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link ReplaceSingleValuedEAttribute} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final ReplaceSingleValuedEAttribute<EObject, ?> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getAffectedElement());
    EObject _affectedElement = change.getAffectedElement();
    EAttribute _affectedFeature = change.getAffectedFeature();
    Object _xifexpression = null;
    boolean _isIsUnset = change.isIsUnset();
    if (_isIsUnset) {
      _xifexpression = SetCommand.UNSET_VALUE;
    } else {
      _xifexpression = change.getNewValue();
    }
    SetCommand _setCommand = new SetCommand(editingDomain, _affectedElement, _affectedFeature, _xifexpression);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_setCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link InsertEReference} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final InsertEReference<EObject> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getAffectedElement());
    boolean _alreadyContainsObject = ChangeCommandUtil.alreadyContainsObject(change.getAffectedElement(), change.getAffectedFeature(), change.getNewValue());
    if (_alreadyContainsObject) {
      EReference _eOpposite = change.getAffectedFeature().getEOpposite();
      boolean _tripleEquals = (_eOpposite == null);
      if (_tripleEquals) {
        EObject _newValue = change.getNewValue();
        String _plus = ("Tried to add value " + _newValue);
        String _plus_1 = (_plus + ", but although there is no opposite feature it was already contained in ");
        EObject _affectedElement = change.getAffectedElement();
        String _plus_2 = (_plus_1 + _affectedElement);
        ApplyForwardCommandSwitch.logger.warn(_plus_2);
      }
      return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList());
    }
    EObject _affectedElement_1 = change.getAffectedElement();
    EReference _affectedFeature = change.getAffectedFeature();
    EObject _newValue_1 = change.getNewValue();
    int _index = change.getIndex();
    AddCommand _addCommand = new AddCommand(editingDomain, _affectedElement_1, _affectedFeature, _newValue_1, _index);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_addCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link RemoveEReference} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final RemoveEReference<EObject> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getAffectedElement());
    boolean _alreadyContainsObject = ChangeCommandUtil.alreadyContainsObject(change.getAffectedElement(), change.getAffectedFeature(), change.getOldValue());
    boolean _not = (!_alreadyContainsObject);
    if (_not) {
      EReference _eOpposite = change.getAffectedFeature().getEOpposite();
      boolean _tripleEquals = (_eOpposite == null);
      if (_tripleEquals) {
        EObject _oldValue = change.getOldValue();
        String _plus = ("Tried to remove value " + _oldValue);
        String _plus_1 = (_plus + ", but although there is no opposite feature it was already contained in ");
        EObject _affectedElement = change.getAffectedElement();
        String _plus_2 = (_plus_1 + _affectedElement);
        ApplyForwardCommandSwitch.logger.warn(_plus_2);
      }
      return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList());
    }
    EObject _affectedElement_1 = change.getAffectedElement();
    EReference _affectedFeature = change.getAffectedFeature();
    EObject _oldValue_1 = change.getOldValue();
    int _index = change.getIndex();
    RemoveAtCommand _removeAtCommand = new RemoveAtCommand(editingDomain, _affectedElement_1, _affectedFeature, _oldValue_1, _index);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_removeAtCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link ReplaceSingleValuedEReference} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final ReplaceSingleValuedEReference<EObject> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getAffectedElement());
    boolean _alreadyContainsObject = ChangeCommandUtil.alreadyContainsObject(change.getAffectedElement(), change.getAffectedFeature(), change.getNewValue());
    if (_alreadyContainsObject) {
      EReference _eOpposite = change.getAffectedFeature().getEOpposite();
      boolean _tripleEquals = (_eOpposite == null);
      if (_tripleEquals) {
        EObject _newValue = change.getNewValue();
        String _plus = ("Tried to add value " + _newValue);
        String _plus_1 = (_plus + ", but although there is no opposite feature it was already contained in ");
        EObject _affectedElement = change.getAffectedElement();
        String _plus_2 = (_plus_1 + _affectedElement);
        ApplyForwardCommandSwitch.logger.warn(_plus_2);
      }
      return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList());
    }
    EObject _affectedElement_1 = change.getAffectedElement();
    EReference _affectedFeature = change.getAffectedFeature();
    Object _xifexpression = null;
    boolean _isIsUnset = change.isIsUnset();
    if (_isIsUnset) {
      _xifexpression = SetCommand.UNSET_VALUE;
    } else {
      _xifexpression = change.getNewValue();
    }
    SetCommand _setCommand = new SetCommand(editingDomain, _affectedElement_1, _affectedFeature, _xifexpression);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_setCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link InsertRootEObject} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final InsertRootEObject<EObject> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getNewValue());
    EList<EObject> _contents = change.getResource().getContents();
    EObject _newValue = change.getNewValue();
    int _index = change.getIndex();
    AddCommand _addCommand = new AddCommand(editingDomain, _contents, _newValue, _index);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_addCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link RemoveRootEObject} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final RemoveRootEObject<EObject> change) {
    final EditingDomain editingDomain = ChangeCommandUtil.getEditingDomain(change.getOldValue());
    EList<EObject> _contents = change.getResource().getContents();
    EObject _oldValue = change.getOldValue();
    int _index = change.getIndex();
    RemoveAtCommand _removeAtCommand = new RemoveAtCommand(editingDomain, _contents, _oldValue, _index);
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList(_removeAtCommand));
  }

  /**
   * Dispatch method to create commands to apply a {@link CreateEObject} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final CreateEObject<EObject> change) {
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList());
  }

  /**
   * Dispatch method to create commands to apply a {@link DeleteEObject} change forward.
   * @param object The change which commands should be created.
   */
  static List<Command> _getCommands(final DeleteEObject<EObject> change) {
    return Collections.<Command>unmodifiableList(CollectionLiterals.<Command>newArrayList());
  }

  static List<Command> getCommands(final EChange<EObject> change) {
    if (change instanceof InsertEAttributeValue) {
      return _getCommands((InsertEAttributeValue<EObject, ?>)change);
    } else if (change instanceof RemoveEAttributeValue) {
      return _getCommands((RemoveEAttributeValue<EObject, ?>)change);
    } else if (change instanceof InsertEReference) {
      return _getCommands((InsertEReference<EObject>)change);
    } else if (change instanceof RemoveEReference) {
      return _getCommands((RemoveEReference<EObject>)change);
    } else if (change instanceof ReplaceSingleValuedEAttribute) {
      return _getCommands((ReplaceSingleValuedEAttribute<EObject, ?>)change);
    } else if (change instanceof ReplaceSingleValuedEReference) {
      return _getCommands((ReplaceSingleValuedEReference<EObject>)change);
    } else if (change instanceof InsertRootEObject) {
      return _getCommands((InsertRootEObject<EObject>)change);
    } else if (change instanceof RemoveRootEObject) {
      return _getCommands((RemoveRootEObject<EObject>)change);
    } else if (change instanceof CreateEObject) {
      return _getCommands((CreateEObject<EObject>)change);
    } else if (change instanceof DeleteEObject) {
      return _getCommands((DeleteEObject<EObject>)change);
    } else if (change instanceof UnsetFeature) {
      return _getCommands((UnsetFeature<EObject, ?>)change);
    } else if (change != null) {
      return _getCommands(change);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(change).toString());
    }
  }

  private ApplyForwardCommandSwitch() {
    
  }
}
