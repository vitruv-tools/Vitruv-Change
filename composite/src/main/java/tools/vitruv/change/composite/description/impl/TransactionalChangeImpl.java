package tools.vitruv.change.composite.description.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
 * Implementation of {@link TransactionalChange}.
 *
 * @param <E> the type of the elements affected by the change
 */
public class TransactionalChangeImpl<E extends Object>
    implements TransactionalChange<E> {
  private static final String INDEX_LABEL = " (index ";

  private List<? extends EChange<E>> eChanges;

  private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

  /**
   * Constructs a new {@link TransactionalChangeImpl} with the given changes.
   *
   * @param eChanges the changes to include in this transactional change
   */
  public TransactionalChangeImpl(final Iterable<? extends EChange<E>> eChanges) {
    Preconditions.checkNotNull(eChanges, "eChanges");
    List<EChange<E>> list = new ArrayList<>();
    eChanges.forEach(list::add);
    this.eChanges = list;
  }

  @Override
  public List<EChange<E>> getEChanges() {
    return Collections.<EChange<E>>unmodifiableList(this.eChanges);
  }

  @Override
  public boolean containsConcreteChange() {
    return !this.eChanges.isEmpty();
  }

  private static URI getChangedURI(final EChange<?> eChange) {
    if (eChange instanceof FeatureEChange) {
      EObject affectedElement = ((FeatureEChange<EObject, ?>) eChange).getAffectedElement();
      if (affectedElement != null) {
        return TransactionalChangeImpl.getObjectUri(affectedElement);
      }
      return null;
    }
    if (eChange instanceof EObjectExistenceEChange) {
      EObject affectedElement =
          ((EObjectExistenceEChange<EObject>) eChange).getAffectedElement();
      if (affectedElement != null) {
        return TransactionalChangeImpl.getObjectUri(affectedElement);
      }
      return null;
    }
    if (eChange instanceof RootEChange) {
      return URI.createURI(((RootEChange<?>) eChange).getUri());
    }
    return null;
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
      if ((currentPackage != null)) {
        changedPackages.add(currentPackage);
      }
    }
    Preconditions.checkState(!changedPackages.isEmpty(),
        "Cannot identify the packages of this change:%s%s",
        System.lineSeparator(), this);
    return MetamodelDescriptor.of(changedPackages);
  }

  @Override
  public Set<EObject> getAffectedEObjects() {
    return this.eChanges.stream()
        .flatMap(it -> TransactionalChangeImpl.getAffectedEObjects(it).stream())
        .collect(Collectors.toSet());
  }

  private static Set<EObject> getAffectedEObjects(final EChange<?> eChange) {
    if (eChange instanceof FeatureEChange) {
      return Set.<EObject>of(((FeatureEChange<EObject, ?>) eChange).getAffectedElement());
    }
    if (eChange instanceof EObjectExistenceEChange) {
      return Set.<EObject>of(
          ((EObjectExistenceEChange<EObject>) eChange).getAffectedElement());
    }
    if (eChange instanceof InsertRootEObject) {
      return Set.<EObject>of(((InsertRootEObject<EObject>) eChange).getNewValue());
    }
    if (eChange instanceof RemoveRootEObject) {
      return Set.<EObject>of(((RemoveRootEObject<EObject>) eChange).getOldValue());
    }
    return Collections.emptySet();
  }

  @Override
  public Set<EObject> getAffectedAndReferencedEObjects() {
    return this.eChanges.stream()
        .flatMap(it -> TransactionalChangeImpl.getAffectedAndReferencedEObjects(it).stream())
        .collect(Collectors.toSet());
  }

  private static Set<EObject> getAffectedAndReferencedEObjects(final EChange<?> eChange) {
    if (eChange instanceof UpdateAttributeEChange) {
      return Set.<EObject>of(((UpdateAttributeEChange<EObject>) eChange).getAffectedElement());
    }
    if (eChange instanceof ReplaceSingleValuedEReference) {
      ReplaceSingleValuedEReference<EObject> change =
          (ReplaceSingleValuedEReference<EObject>) eChange;
      return TransactionalChangeImpl.<EObject>setOfNotNull(change.getAffectedElement(),
          change.getOldValue(), change.getNewValue());
    }
    if (eChange instanceof InsertEReference) {
      InsertEReference<EObject> change = (InsertEReference<EObject>) eChange;
      return Set.<EObject>of(change.getAffectedElement(), change.getNewValue());
    }
    if (eChange instanceof RemoveEReference) {
      RemoveEReference<EObject> change = (RemoveEReference<EObject>) eChange;
      return Set.<EObject>of(change.getAffectedElement(), change.getOldValue());
    }
    if (eChange instanceof EObjectExistenceEChange) {
      return Set.<EObject>of(((EObjectExistenceEChange<EObject>) eChange).getAffectedElement());
    }
    if (eChange instanceof InsertRootEObject) {
      return Set.<EObject>of(((InsertRootEObject<EObject>) eChange).getNewValue());
    }
    if (eChange instanceof RemoveRootEObject) {
      return Set.<EObject>of(((RemoveRootEObject<EObject>) eChange).getOldValue());
    }
    return Collections.emptySet();
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

  /**
   * Creates deep copies of the {@link EChange}s contained in this change.
   *
   * @return a list with a copy of each contained change
   */
  protected List<EChange<E>> getClonedEChanges() {
    @SuppressWarnings("unchecked")
    List<EChange<E>> result = (List<EChange<E>>) (List<?>) this.eChanges.stream()
        .map(EcoreUtil::copy).toList();
    return result;
  }

  @Override
  public TransactionalChangeImpl<E> copy() {
    List<EChange<E>> clonedEChanges = this.getClonedEChanges();
    return new TransactionalChangeImpl<E>(clonedEChanges);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TransactionalChange)) {
      return false;
    }
    List<?> objEChanges = ((TransactionalChange<?>) obj).getEChanges();
    return Objects.equals(this.eChanges, objEChanges);
  }

  @Override
  public int hashCode() {
    return this.eChanges.hashCode();
  }

  private static URI getObjectUri(final EObject object) {
    final Resource objectResource = object.eResource();
    if (objectResource != null) {
      return objectResource.getURI();
    }
    if (object.eIsProxy()) {
      final URI proxyURI = ((InternalEObject) object).eProxyURI();
      if (proxyURI != null && proxyURI.segmentCount() > 0) {
        return proxyURI.trimFragment();
      }
    }
    return null;
  }

  private static <T extends Object> Set<T> setOfNotNull(final T element) {
    if (element != null) {
      return Set.<T>of(element);
    }
    return Set.of();
  }

  private static <T extends Object> Set<T> setOfNotNull(final T element1, final T element2) {
    if (element1 == null) {
      return TransactionalChangeImpl.<T>setOfNotNull(element2);
    }
    if (element2 == null) {
      return Set.<T>of(element1);
    }
    return Set.<T>of(element1, element2);
  }

  private static <T extends Object> Set<T> setOfNotNull(final T element1, final T element2,
      final T element3) {
    if (element1 == null) {
      return TransactionalChangeImpl.<T>setOfNotNull(element2, element3);
    }
    if (element2 == null) {
      return TransactionalChangeImpl.<T>setOfNotNull(element1, element3);
    }
    if (element3 == null) {
      return Set.<T>of(element1, element2);
    }
    return Set.<T>of(element1, element2, element3);
  }

  @Override
  public String toString() {
    if (this.eChanges.isEmpty()) {
      return this.getClass().getSimpleName() + " (empty)";
    }
    StringBuilder builder = new StringBuilder();
    builder.append(this.getClass().getSimpleName()).append(": [").append(System.lineSeparator());
    for (EChange<E> eChange : this.eChanges) {
      builder.append("\t").append(this.getStringRepresentation(eChange))
          .append(System.lineSeparator());
    }
    builder.append("]").append(System.lineSeparator());
    return builder.toString();
  }

  private CharSequence getStringRepresentation(final EChange<?> change) {
    if (change instanceof InsertRootEObject) {
      return "insert " + ((InsertRootEObject<?>) change).getNewValue() + " at "
          + ((InsertRootEObject<?>) change).getUri() + INDEX_LABEL
          + ((InsertRootEObject<?>) change).getIndex() + ")";
    }
    if (change instanceof RemoveRootEObject) {
      return "remove " + ((RemoveRootEObject<?>) change).getOldValue() + " from "
          + ((RemoveRootEObject<?>) change).getUri() + INDEX_LABEL
          + ((RemoveRootEObject<?>) change).getIndex() + ")";
    }
    if (change instanceof CreateEObject) {
      return "create " + ((CreateEObject<?>) change).getAffectedElement();
    }
    if (change instanceof DeleteEObject) {
      return "delete " + ((DeleteEObject<?>) change).getAffectedElement();
    }
    if (change instanceof UnsetFeature) {
      return this.getAffectedFeatureString(((FeatureEChange<?, ?>) change)) + " = ∅";
    }
    if (change instanceof ReplaceSingleValuedEAttribute) {
      return this.getAffectedFeatureString(((FeatureEChange<?, ?>) change)) + " = "
          + ((ReplaceSingleValuedEAttribute<?, ?>) change).getNewValue() + " (was "
          + ((ReplaceSingleValuedEAttribute<?, ?>) change).getOldValue() + ")";
    }
    if (change instanceof ReplaceSingleValuedEReference) {
      return this.getAffectedFeatureString(((FeatureEChange<?, ?>) change)) + " = "
          + ((ReplaceSingleValuedEReference<?>) change).getNewValue() + " (was "
          + ((ReplaceSingleValuedEReference<?>) change).getOldValue() + ")";
    }
    if (change instanceof InsertEAttributeValue) {
      return this.getAffectedFeatureString(((FeatureEChange<?, ?>) change)) + " += "
          + ((InsertEAttributeValue<?, ?>) change).getNewValue() + INDEX_LABEL
          + ((InsertEAttributeValue<?, ?>) change).getIndex() + ")";
    }
    if (change instanceof InsertEReference) {
      return this.getAffectedFeatureString(((FeatureEChange<?, ?>) change)) + " += "
          + ((InsertEReference<?>) change).getNewValue() + INDEX_LABEL
          + ((InsertEReference<?>) change).getIndex() + ")";
    }
    if (change instanceof RemoveEAttributeValue) {
      return this.getAffectedFeatureString(((FeatureEChange<?, ?>) change)) + " -= "
          + ((RemoveEAttributeValue<?, ?>) change).getOldValue() + INDEX_LABEL
          + ((RemoveEAttributeValue<?, ?>) change).getIndex() + ")";
    }
    if (change instanceof RemoveEReference) {
      return this.getAffectedFeatureString(((FeatureEChange<?, ?>) change)) + " -= "
          + ((RemoveEReference<?>) change).getOldValue() + INDEX_LABEL
          + ((RemoveEReference<?>) change).getIndex() + ")";
    }
    return null;
  }

  private CharSequence getAffectedFeatureString(final FeatureEChange<?, ?> change) {
    return change.getAffectedElement() + "." + change.getAffectedFeature().getName();
  }
}
