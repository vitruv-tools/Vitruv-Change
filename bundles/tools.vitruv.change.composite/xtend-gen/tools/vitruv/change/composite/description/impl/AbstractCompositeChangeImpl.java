package tools.vitruv.change.composite.description.impl;

import com.google.common.base.Objects;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.CompositeChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.UserInteractionBase;

@SuppressWarnings("all")
public abstract class AbstractCompositeChangeImpl<Element extends Object, ContanedChange extends VitruviusChange<Element>> implements CompositeChange<Element, ContanedChange> {
  private List<ContanedChange> changes;

  public AbstractCompositeChangeImpl(final List<? extends ContanedChange> changes) {
    this.changes = List.<ContanedChange>copyOf(changes);
  }

  @Override
  public List<ContanedChange> getChanges() {
    return this.changes;
  }

  @Override
  public boolean containsConcreteChange() {
    final Function1<ContanedChange, Boolean> _function = (ContanedChange it) -> {
      return Boolean.valueOf(it.containsConcreteChange());
    };
    return IterableExtensions.<ContanedChange>exists(this.changes, _function);
  }

  @Override
  public Set<URI> getChangedURIs() {
    LinkedHashSet<URI> _linkedHashSet = new LinkedHashSet<URI>();
    final Function1<ContanedChange, Set<URI>> _function = (ContanedChange it) -> {
      return it.getChangedURIs();
    };
    return IterableUtil.<ContanedChange, URI, LinkedHashSet<URI>>flatMapFixedTo(this.changes, _linkedHashSet, _function);
  }

  @Override
  public Set<MetamodelDescriptor> getAffectedEObjectsMetamodelDescriptors() {
    HashSet<MetamodelDescriptor> _hashSet = new HashSet<MetamodelDescriptor>();
    final Function1<ContanedChange, Set<MetamodelDescriptor>> _function = (ContanedChange it) -> {
      return it.getAffectedEObjectsMetamodelDescriptors();
    };
    return IterableUtil.<ContanedChange, MetamodelDescriptor, HashSet<MetamodelDescriptor>>flatMapFixedTo(this.changes, _hashSet, _function);
  }

  @Override
  public List<EChange<Element>> getEChanges() {
    final Function1<ContanedChange, Iterable<? extends EChange<Element>>> _function = (ContanedChange it) -> {
      return it.getEChanges();
    };
    return IterableUtil.<ContanedChange, EChange<Element>>flatMapFixed(this.changes, _function);
  }

  @Override
  public Set<EObject> getAffectedEObjects() {
    LinkedHashSet<EObject> _linkedHashSet = new LinkedHashSet<EObject>();
    final Function1<ContanedChange, Set<EObject>> _function = (ContanedChange it) -> {
      return it.getAffectedEObjects();
    };
    return IterableUtil.<ContanedChange, EObject, LinkedHashSet<EObject>>flatMapFixedTo(this.changes, _linkedHashSet, _function);
  }

  @Override
  public Set<EObject> getAffectedAndReferencedEObjects() {
    LinkedHashSet<EObject> _linkedHashSet = new LinkedHashSet<EObject>();
    final Function1<ContanedChange, Set<EObject>> _function = (ContanedChange it) -> {
      return it.getAffectedAndReferencedEObjects();
    };
    return IterableUtil.<ContanedChange, EObject, LinkedHashSet<EObject>>flatMapFixedTo(this.changes, _linkedHashSet, _function);
  }

  @Override
  public Iterable<UserInteractionBase> getUserInteractions() {
    final Function1<ContanedChange, Iterable<UserInteractionBase>> _function = (ContanedChange it) -> {
      return it.getUserInteractions();
    };
    return IterableExtensions.<ContanedChange, UserInteractionBase>flatMap(this.changes, _function);
  }

  @Override
  public String toString() {
    String _xifexpression = null;
    boolean _isEmpty = this.changes.isEmpty();
    if (_isEmpty) {
      StringConcatenation _builder = new StringConcatenation();
      String _simpleName = this.getClass().getSimpleName();
      _builder.append(_simpleName);
      _builder.append(" (empty)");
      _xifexpression = _builder.toString();
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      String _simpleName_1 = this.getClass().getSimpleName();
      _builder_1.append(_simpleName_1);
      _builder_1.append(": [");
      _builder_1.newLineIfNotEmpty();
      {
        for(final ContanedChange change : this.changes) {
          _builder_1.append("\t");
          _builder_1.append(change, "\t");
          _builder_1.newLineIfNotEmpty();
        }
      }
      _builder_1.append("]");
      _builder_1.newLine();
      _xifexpression = _builder_1.toString();
    }
    return _xifexpression;
  }

  /**
   * Indicates whether some other object is "equal to" this composite change.
   * This means it is a composite change which contains the same changes as this one in no particular order.
   * @param other is the object to compare with.
   * @return true, if the object is a composite change and has the same changes in any order.
   */
  @Override
  public boolean equals(final Object other) {
    boolean _xifexpression = false;
    if ((other == this)) {
      _xifexpression = true;
    } else {
      boolean _xifexpression_1 = false;
      if ((other == null)) {
        _xifexpression_1 = false;
      } else {
        boolean _xifexpression_2 = false;
        if ((other instanceof CompositeChange<?, ?>)) {
          List<? extends VitruviusChange<?>> _changes = ((CompositeChange<?, ?>)other).getChanges();
          _xifexpression_2 = Objects.equal(this.changes, _changes);
        } else {
          _xifexpression_2 = false;
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  @Override
  public int hashCode() {
    return this.changes.hashCode();
  }
}
