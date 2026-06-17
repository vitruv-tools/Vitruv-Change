package tools.vitruv.change.composite.description.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.CompositeChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.UserInteractionBase;

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
    return this.changes.stream().anyMatch(it -> it.containsConcreteChange());
  }

  @Override
  public Set<URI> getChangedURIs() {
    LinkedHashSet<URI> result = new LinkedHashSet<>();
    for (ContanedChange it : this.changes) {
      result.addAll(it.getChangedURIs());
    }
    return result;
  }

  @Override
  public Set<MetamodelDescriptor> getAffectedEObjectsMetamodelDescriptors() {
    HashSet<MetamodelDescriptor> result = new HashSet<>();
    for (ContanedChange it : this.changes) {
      result.addAll(it.getAffectedEObjectsMetamodelDescriptors());
    }
    return result;
  }

  @Override
  public List<EChange<Element>> getEChanges() {
    List<EChange<Element>> result = new ArrayList<>();
    for (ContanedChange it : this.changes) {
      result.addAll(it.getEChanges());
    }
    return result;
  }

  @Override
  public Set<EObject> getAffectedEObjects() {
    LinkedHashSet<EObject> result = new LinkedHashSet<>();
    for (ContanedChange it : this.changes) {
      result.addAll(it.getAffectedEObjects());
    }
    return result;
  }

  @Override
  public Set<EObject> getAffectedAndReferencedEObjects() {
    LinkedHashSet<EObject> result = new LinkedHashSet<>();
    for (ContanedChange it : this.changes) {
      result.addAll(it.getAffectedAndReferencedEObjects());
    }
    return result;
  }

  @Override
  public Iterable<UserInteractionBase> getUserInteractions() {
    List<UserInteractionBase> result = new ArrayList<>();
    for (ContanedChange it : this.changes) {
      it.getUserInteractions().forEach(result::add);
    }
    return result;
  }

  @Override
  public String toString() {
    if (this.changes.isEmpty()) {
      return this.getClass().getSimpleName() + " (empty)";
    } else {
      StringBuilder _builder = new StringBuilder();
      _builder.append(this.getClass().getSimpleName()).append(": [").append(System.lineSeparator());
      for (ContanedChange change : this.changes) {
        _builder.append("\t").append(change).append(System.lineSeparator());
      }
      _builder.append("]").append(System.lineSeparator());
      return _builder.toString();
    }
  }

  /**
   * Indicates whether some other object is "equal to" this composite change.
   * This means it is a composite change which contains the same changes as this one in no particular order.
   * @param other is the object to compare with.
   * @return true, if the object is a composite change and has the same changes in any order.
   */
  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (other instanceof CompositeChange<?, ?>) {
      List<? extends VitruviusChange<?>> _changes = ((CompositeChange<?, ?>)other).getChanges();
      return Objects.equals(this.changes, _changes);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.changes.hashCode();
  }
}
