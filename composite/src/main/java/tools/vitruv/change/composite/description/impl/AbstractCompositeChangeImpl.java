package tools.vitruv.change.composite.description.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.CompositeChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.UserInteractionBase;

/**
 * Base implementation of a {@link CompositeChange} that holds an ordered,
 * immutable list of contained changes.
 *
 * <p>Most queries (changed URIs, affected objects, {@link EChange}s, user
 * interactions, etc.) are answered by combining the results of all contained
 * changes in order. Annotations are stored per composite change and are not
 * passed on to the contained changes.
 *
 * @param <E> the type of elements referenced by the changes
 * @param <C> the type of changes contained in this composite change
 */
public abstract class AbstractCompositeChangeImpl<E,
        C extends VitruviusChange<E>> implements CompositeChange<E,
        C> {
  private final List<C> changes;
  protected final Map<Class<?>, Object> annotations = new HashMap<>();

  /**
   * Creates a composite change containing the given changes.
   *
   * <p>The list is copied, so later modifications to it do not affect this change.
   *
   * @param changes the changes to contain, in order; must not be {@code null}
   *     and must not contain {@code null} elements
   */
  protected AbstractCompositeChangeImpl(final List<? extends C> changes) {
    this.changes = List.copyOf(changes);
  }

  @Override
  public List<C> getChanges() {
    return this.changes;
  }

  @Override
  public boolean containsConcreteChange() {
    return this.changes.stream().anyMatch(VitruviusChange::containsConcreteChange);
  }

  @Override
  public Set<URI> getChangedURIs() {
    LinkedHashSet<URI> result = new LinkedHashSet<>();
    for (C it : this.changes) {
      result.addAll(it.getChangedURIs());
    }
    return result;
  }

  @Override
  public Set<MetamodelDescriptor> getAffectedEObjectsMetamodelDescriptors() {
    HashSet<MetamodelDescriptor> result = new HashSet<>();
    for (C it : this.changes) {
      result.addAll(it.getAffectedEObjectsMetamodelDescriptors());
    }
    return result;
  }

  @Override
  public List<EChange<E>> getEChanges() {
    List<EChange<E>> result = new ArrayList<>();
    for (C it : this.changes) {
      result.addAll(it.getEChanges());
    }
    return result;
  }

  @Override
  public Set<EObject> getAffectedEObjects() {
    LinkedHashSet<EObject> result = new LinkedHashSet<>();
    for (C it : this.changes) {
      result.addAll(it.getAffectedEObjects());
    }
    return result;
  }

  @Override
  public Set<EObject> getAffectedAndReferencedEObjects() {
    LinkedHashSet<EObject> result = new LinkedHashSet<>();
    for (C it : this.changes) {
      result.addAll(it.getAffectedAndReferencedEObjects());
    }
    return result;
  }

  @Override
  public Iterable<UserInteractionBase> getUserInteractions() {
    List<UserInteractionBase> result = new ArrayList<>();
    for (C it : this.changes) {
      it.getUserInteractions().forEach(result::add);
    }
    return result;
  }

  @Override
  public <T> void setAnnotation(final Class<T> type, final T value) {
    this.annotations.put(type, value);
  }

  @Override
  public <T> Optional<T> getAnnotation(final Class<T> type) {
    return Optional.ofNullable(type.cast(this.annotations.get(type)));
  }

  @Override
  public Map<Class<?>, Object> getAnnotations() {
    return Collections.unmodifiableMap(this.annotations);
  }

  @Override
  public Iterable<TransactionalChange<E>> getTransactionalChangeSequence() {
    return changes.stream()
            .flatMap(it ->
                    StreamSupport.stream(it.getTransactionalChangeSequence().spliterator(), false))
            .toList();
  }

  @Override
  public String toString() {
    if (this.changes.isEmpty()) {
      return this.getClass().getSimpleName() + " (empty)";
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getClass().getSimpleName()).append(": [").append(System.lineSeparator());
      for (C change : this.changes) {
        builder.append("\t").append(change).append(System.lineSeparator());
      }
      builder.append("]").append(System.lineSeparator());
      return builder.toString();
    }
  }

  /**
   * Indicates whether some other object is "equal to" this composite change.
   * This is the case if it is a {@link CompositeChange} containing the same
   * changes in the same order.
   *
   * @param other the object to compare with
   * @return {@code true} if the object is a composite change with the same
   *     changes in the same order
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
      List<? extends VitruviusChange<?>> vitruviusChanges =
              ((CompositeChange<?, ?>) other).getChanges();
      return Objects.equals(this.changes, vitruviusChanges);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.changes.hashCode();
  }
}
