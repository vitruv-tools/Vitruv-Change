package tools.vitruv.change.composite.description.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootEChange;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.interaction.UserInteractionBase;

/**
 * Default implementation of {@link TransactionalChange}, holding an ordered list
 * of atomic {@link EChange}s together with the user interactions and annotations
 * that belong to them.
 *
 * @param <E> the type of Es referenced by the changes
 */
public class TransactionalChangeImpl<E> implements TransactionalChange<E> {
  private final List<? extends EChange<E>> eChanges;

  private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

  private final Map<Class<?>, Object> annotations = new HashMap<>();

  /**
   * Creates a transactional change containing the given atomic changes.
   *
   * <p>The changes are copied into a new list, so later modifications to the
   * given iterable do not affect this change.
   *
   * @param eChanges the atomic changes, in order; must not be {@code null}
   * @throws NullPointerException if {@code eChanges} is {@code null}
   */
  public TransactionalChangeImpl(final Iterable<? extends EChange<E>> eChanges) {
    Preconditions.checkNotNull(eChanges, "eChanges");
    List<EChange<E>> list = new ArrayList<>();
    eChanges.forEach(list::add);
    this.eChanges = list;
  }

  @Override
  public List<EChange<E>> getEChanges() {
    return Collections.unmodifiableList(this.eChanges);
  }

  @Override
  public boolean containsConcreteChange() {
    boolean isEmpty = this.eChanges.isEmpty();
    return (!isEmpty);
  }

  private static URI getChangedURI(final EChange<?> eChange) {
    URI switchResult = null;
    boolean matched = false;
    if (eChange instanceof FeatureEChange) {
      matched = true;
      EObject affectedE = ((FeatureEChange<EObject, ?>) eChange).getAffectedElement();
      URI objectUri = null;
      if (affectedE != null) {
        objectUri = TransactionalChangeImpl.getObjectUri(affectedE);
      }
      switchResult = objectUri;
    }

    if (!matched && eChange instanceof EObjectExistenceEChange) {
      matched = true;
      EObject affectedE = ((EObjectExistenceEChange<EObject>) eChange).getAffectedElement();
      URI objectUri = null;
      if (affectedE != null) {
        objectUri = TransactionalChangeImpl.getObjectUri(affectedE);
      }
      switchResult = objectUri;
    }

    if (!matched && eChange instanceof RootEChange) {
      switchResult = URI.createURI(((RootEChange<?>) eChange).getUri());
    }

    return switchResult;
  }

  @Override
  public Set<URI> getChangedURIs() {
    return this.eChanges.stream()
        .map(TransactionalChangeImpl::getChangedURI)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Override
  public MetamodelDescriptor getAffectedEObjectsMetamodelDescriptor() {
    HashSet<EPackage> changedPackages = new HashSet<>();
    for (EObject changedObject : this.getAffectedEObjects()) {
      EPackage currentPackage = changedObject.eClass().getEPackage();
      while ((currentPackage.getESuperPackage() != null)) {
        currentPackage = currentPackage.getESuperPackage();
      }
      changedPackages.add(currentPackage);
    }
    Preconditions.checkState(!changedPackages.isEmpty(),
            "Cannot identify the packages of this change:%s%s",
        System.lineSeparator(), this);
    return MetamodelDescriptor.of(changedPackages);
  }

  @Override
  public Set<EObject> getAffectedEObjects() {
    return this.eChanges.stream()
            .flatMap(it -> TransactionalChangeImpl
                    .getAffectedElementObjects(it).stream())
            .collect(Collectors.toSet());
  }

  private static Set<EObject> getAffectedElementObjects(final EChange<?> eChange) {
    Set<EObject> switchResult = null;
    boolean matched = false;
    if (eChange instanceof FeatureEChange) {
      matched = true;
      switchResult = Set.of(((FeatureEChange<EObject, ?>) eChange).getAffectedElement());
    }
    if (!matched && eChange instanceof EObjectExistenceEChange) {
      matched = true;
      switchResult = Set.of(((EObjectExistenceEChange<EObject>) eChange).getAffectedElement());
    }

    if (!matched && eChange instanceof InsertRootEObject) {
      matched = true;
      switchResult = Set.of(((InsertRootEObject<EObject>) eChange).getNewValue());
    }

    if (!matched && eChange instanceof RemoveRootEObject) {
      switchResult = Set.of(((RemoveRootEObject<EObject>) eChange).getOldValue());
    }
    return switchResult;
  }

  @Override
  public Set<EObject> getAffectedAndReferencedEObjects() {
    return this.eChanges.stream()
            .flatMap(it -> TransactionalChangeImpl
                    .getAffectedAndReferencedEObjects(it).stream())
            .collect(Collectors.toSet());
  }

  private static Set<EObject> getAffectedAndReferencedEObjects(final EChange<?> eChange) {
    if (eChange instanceof UpdateAttributeEChange) {
      return Set.of(((UpdateAttributeEChange<EObject>) eChange).getAffectedElement());
    }

    if (eChange instanceof ReplaceSingleValuedEReference) {
      return TransactionalChangeImpl.setOfNotNull(
              ((ReplaceSingleValuedEReference<EObject>) eChange).getAffectedElement(),
              ((ReplaceSingleValuedEReference<EObject>) eChange).getOldValue(),
              ((ReplaceSingleValuedEReference<EObject>) eChange).getNewValue()
      );
    }

    if (eChange instanceof InsertEReference) {
      return Set.of(
              ((InsertEReference<EObject>) eChange).getAffectedElement(),
              ((InsertEReference<EObject>) eChange).getNewValue()
      );
    }

    if (eChange instanceof RemoveEReference) {
      return Set.of(
              ((RemoveEReference<EObject>) eChange).getAffectedElement(),
              ((RemoveEReference<EObject>) eChange).getOldValue()
      );
    }

    if (eChange instanceof EObjectExistenceEChange) {
      return Set.of(((EObjectExistenceEChange<EObject>) eChange).getAffectedElement());
    }

    if (eChange instanceof InsertRootEObject) {
      return Set.of(((InsertRootEObject<EObject>) eChange).getNewValue());
    }

    if (eChange instanceof RemoveRootEObject) {
      return Set.of(((RemoveRootEObject<EObject>) eChange).getOldValue());
    }
    return Set.of();
  }

  @Override
  public Iterable<UserInteractionBase> getUserInteractions() {
    return this.userInteractions;
  }

  @Override
  public void setUserInteractions(final Iterable<UserInteractionBase> userInteractions) {
    Preconditions.<Iterable<UserInteractionBase>>checkNotNull(userInteractions,
            "Interactions must not be null");
    this.userInteractions.clear();
    Iterables.<UserInteractionBase>addAll(this.userInteractions, userInteractions);
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

  protected List<EChange<E>> getClonedEChanges() {
    return this.eChanges.stream().map(EcoreUtil::<EChange<E>>copy).toList();
  }

  @Override
  public TransactionalChangeImpl<E> copy() {
    List<EChange<E>> clonedEChanges = this.getClonedEChanges();
    TransactionalChangeImpl<E> copy = new TransactionalChangeImpl<E>(clonedEChanges);
    copy.annotations.putAll(this.annotations);
    return copy;
  }

  @Override
  public Iterable<TransactionalChange<E>> getTransactionalChangeSequence() {
    return List.of(this);
  }

  @Override
  public boolean equals(final Object obj) {

    if ((obj == this)) {
      return true;
    }

    if (obj instanceof TransactionalChange transactionalChange) {
      return Objects.equals(this.eChanges, transactionalChange.getEChanges());
    }
    return false;

  }

  @Override
  public int hashCode() {
    return this.eChanges.hashCode();
  }

  private static URI getObjectUri(final EObject object) {

    final Resource resource = object.eResource();

    if ((resource != null)) {
      return resource.getURI();
    }

    if (object.eIsProxy()) {
      URI proxyURI = ((InternalEObject) object).eProxyURI();
      if (proxyURI != null && proxyURI.segmentCount() > 0) {
        return proxyURI.trimFragment();
      }
    }

    return null;
  }

  private static <T> Set<T> setOfNotNull(final T element) {
    if ((element != null)) {
      return Set.of(element);
    }
    return Set.of();
  }

  private static <T> Set<T> setOfNotNull(final T element1, final T element2) {
    if ((element1 == null)) {
      return TransactionalChangeImpl.setOfNotNull(element2);
    }
    if ((element2 == null)) {
      return Set.of(element1);
    }
    return Set.of(element1, element2);
  }

  private static <T> Set<T> setOfNotNull(final T element1, final T element2, final T element3) {
    if ((element1 == null)) {
      return TransactionalChangeImpl.setOfNotNull(element2, element3);
    }
    if ((element2 == null)) {
      return TransactionalChangeImpl.setOfNotNull(element1, element3);
    }
    if ((element3 == null)) {
      return Set.of(element1, element2);
    }
    return Set.of(element1, element2, element3);
  }

  @Override
  public String toString() {
    if (this.eChanges.isEmpty()) {
      return this.getClass().getSimpleName() + " (empty)";
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getClass().getSimpleName())
              .append(": [").append(System.lineSeparator());
      for (EChange<E> eChange : this.eChanges) {
        builder.append("\t").append(this.getStringRepresentation(eChange))
                .append(System.lineSeparator());
      }
      builder.append("]").append(System.lineSeparator());
      return builder.toString();
    }
  }

  private static final String INDEX_PREFIX = " (index ";
  private static final String WAS_PREFIX = " (was ";

  private CharSequence getStringRepresentation(final EChange<?> change) {
    if (change instanceof InsertRootEObject) {
      return getInsertRootEObjectString((InsertRootEObject<?>) change);
    }
    if (change instanceof RemoveRootEObject) {
      return getRemoveRootEObjectString((RemoveRootEObject<?>) change);
    }
    if (change instanceof CreateEObject) {
      return "create " + ((CreateEObject<?>) change).getAffectedElement();
    }
    if (change instanceof DeleteEObject) {
      return "delete " + ((DeleteEObject<?>) change).getAffectedElement();
    }
    if (change instanceof UnsetFeature) {
      return getAffectedFeatureString((FeatureEChange<?, ?>) change) + " = ∅";
    }
    if (change instanceof ReplaceSingleValuedEAttribute) {
      return getReplaceSingleValuedEAttributeString((ReplaceSingleValuedEAttribute<?, ?>) change);
    }
    if (change instanceof ReplaceSingleValuedEReference) {
      return getReplaceSingleValuedEReferenceString((ReplaceSingleValuedEReference<?>) change);
    }
    if (change instanceof InsertEAttributeValue) {
      return getInsertEAttributeValueString((InsertEAttributeValue<?, ?>) change);
    }
    if (change instanceof InsertEReference) {
      return getInsertEReferenceString((InsertEReference<?>) change);
    }
    if (change instanceof RemoveEAttributeValue) {
      return getRemoveEAttributeValueString((RemoveEAttributeValue<?, ?>) change);
    }
    if (change instanceof RemoveEReference) {
      return getRemoveEReferenceString((RemoveEReference<?>) change);
    }
    return null;
  }

  private CharSequence getInsertRootEObjectString(final InsertRootEObject<?> change) {
    return "insert " + change.getNewValue()
            + " at " + change.getUri()
            + INDEX_PREFIX  + change.getIndex() + ")";
  }

  private CharSequence getRemoveRootEObjectString(final RemoveRootEObject<?> change) {
    return "remove " + change.getOldValue()
            + " from " + change.getUri()
            + INDEX_PREFIX  + change.getIndex() + ")";
  }

  private CharSequence getReplaceSingleValuedEAttributeString(
          final ReplaceSingleValuedEAttribute<?, ?> change) {
    return getAffectedFeatureString(change)
            + " = " + change.getNewValue()
            + WAS_PREFIX + change.getOldValue() + ")";
  }

  private CharSequence getReplaceSingleValuedEReferenceString(
          final ReplaceSingleValuedEReference<?> change) {
    return getAffectedFeatureString(change)
            + " = " + change.getNewValue()
            + WAS_PREFIX + change.getOldValue() + ")";
  }

  private CharSequence getInsertEAttributeValueString(final InsertEAttributeValue<?, ?> change) {
    return getAffectedFeatureString(change)
            + " += " + change.getNewValue()
            + INDEX_PREFIX  + change.getIndex() + ")";
  }

  private CharSequence getInsertEReferenceString(final InsertEReference<?> change) {
    return getAffectedFeatureString(change)
            + " += " + change.getNewValue()
            + INDEX_PREFIX  + change.getIndex() + ")";
  }

  private CharSequence getRemoveEAttributeValueString(final RemoveEAttributeValue<?, ?> change) {
    return getAffectedFeatureString(change)
            + " -= " + change.getOldValue()
            + INDEX_PREFIX  + change.getIndex() + ")";
  }

  private CharSequence getRemoveEReferenceString(final RemoveEReference<?> change) {
    return getAffectedFeatureString(change)
            + " -= " + change.getOldValue()
            + INDEX_PREFIX  + change.getIndex() + ")";
  }

  private CharSequence getAffectedFeatureString(final FeatureEChange<?, ?> change) {
    return change.getAffectedElement() + "." + change.getAffectedFeature().getName();
  }
}
