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

public class TransactionalChangeImpl<Element extends Object> implements TransactionalChange<Element> {
  private List<? extends EChange<Element>> eChanges;

  private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

  public TransactionalChangeImpl(final Iterable<? extends EChange<Element>> eChanges) {
    Preconditions.checkNotNull(eChanges, "eChanges");
    List<EChange<Element>> _list = new ArrayList<>();
    eChanges.forEach(_list::add);
    this.eChanges = _list;
  }

  @Override
  public List<EChange<Element>> getEChanges() {
    return Collections.<EChange<Element>>unmodifiableList(this.eChanges);
  }

  @Override
  public boolean containsConcreteChange() {
    boolean _isEmpty = this.eChanges.isEmpty();
    return (!_isEmpty);
  }

  private static URI getChangedURI(final EChange<?> eChange) {
    URI _switchResult = null;
    boolean _matched = false;
    if (eChange instanceof FeatureEChange) {
      _matched=true;
      EObject _affectedElement = ((FeatureEChange<EObject, ?>)eChange).getAffectedElement();
      URI _objectUri = null;
      if (_affectedElement!=null) {
        _objectUri=TransactionalChangeImpl.getObjectUri(_affectedElement);
      }
      _switchResult = _objectUri;
    }
    if (!_matched) {
      if (eChange instanceof EObjectExistenceEChange) {
        _matched=true;
        EObject _affectedElement = ((EObjectExistenceEChange<EObject>)eChange).getAffectedElement();
        URI _objectUri = null;
        if (_affectedElement!=null) {
          _objectUri=TransactionalChangeImpl.getObjectUri(_affectedElement);
        }
        _switchResult = _objectUri;
      }
    }
    if (!_matched) {
      if (eChange instanceof RootEChange) {
        _matched=true;
        _switchResult = URI.createURI(((RootEChange<?>)eChange).getUri());
      }
    }
    return _switchResult;
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
    Preconditions.checkState(!changedPackages.isEmpty(), "Cannot identify the packages of this change:%s%s",
        System.lineSeparator(), this);
    return MetamodelDescriptor.of(changedPackages);
  }

  @Override
  public Set<EObject> getAffectedEObjects() {
    return this.eChanges.stream()
        .flatMap(it -> TransactionalChangeImpl.getAffectedEObjects(it).stream())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<EObject> getAffectedAndReferencedEObjects() {
    return this.eChanges.stream()
        .flatMap(it -> TransactionalChangeImpl.getAffectedAndReferencedEObjects(it).stream())
        .collect(Collectors.toSet());
  }

  private static Set<EObject> getAffectedEObjects(final EChange<?> eChange) {
    Set<EObject> _switchResult = null;
    boolean _matched = false;
    if (eChange instanceof FeatureEChange) {
      _matched=true;
      _switchResult = Set.<EObject>of(((FeatureEChange<EObject, ?>)eChange).getAffectedElement());
    }
    if (!_matched) {
      if (eChange instanceof EObjectExistenceEChange) {
        _matched=true;
        _switchResult = Set.<EObject>of(((EObjectExistenceEChange<EObject>)eChange).getAffectedElement());
      }
    }
    if (!_matched) {
      if (eChange instanceof InsertRootEObject) {
        _matched=true;
        _switchResult = Set.<EObject>of(((InsertRootEObject<EObject>)eChange).getNewValue());
      }
    }
    if (!_matched) {
      if (eChange instanceof RemoveRootEObject) {
        _matched=true;
        _switchResult = Set.<EObject>of(((RemoveRootEObject<EObject>)eChange).getOldValue());
      }
    }
    return _switchResult;
  }

  private static Set<EObject> getAffectedAndReferencedEObjects(final EChange<?> eChange) {
    if (eChange instanceof UpdateAttributeEChange) {
      return Set.<EObject>of(((UpdateAttributeEChange<EObject>) eChange).getAffectedElement());
    }

    if (eChange instanceof ReplaceSingleValuedEReference) {
      return TransactionalChangeImpl.<EObject>setOfNotNull(
              ((ReplaceSingleValuedEReference<EObject>) eChange).getAffectedElement(),
              ((ReplaceSingleValuedEReference<EObject>) eChange).getOldValue(),
              ((ReplaceSingleValuedEReference<EObject>) eChange).getNewValue()
      );
    }

    if (eChange instanceof InsertEReference) {
      return Set.<EObject>of(
              ((InsertEReference<EObject>) eChange).getAffectedElement(),
              ((InsertEReference<EObject>) eChange).getNewValue()
      );
    }

    if (eChange instanceof RemoveEReference) {
      return Set.<EObject>of(
              ((RemoveEReference<EObject>) eChange).getAffectedElement(),
              ((RemoveEReference<EObject>) eChange).getOldValue()
      );
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
    return Set.of();
  }

  @Override
  public Iterable<UserInteractionBase> getUserInteractions() {
    return this.userInteractions;
  }

  @Override
  public void setUserInteractions(final Iterable<UserInteractionBase> userInteractions) {
    Preconditions.<Iterable<UserInteractionBase>>checkNotNull(userInteractions, "Interactions must not be null");
    this.userInteractions.clear();
    Iterables.<UserInteractionBase>addAll(this.userInteractions, userInteractions);
  }

  protected List<EChange<Element>> getClonedEChanges() {
    return this.eChanges.stream().map(it -> EcoreUtil.<EChange<Element>>copy(it)).toList();
  }

  @Override
  public TransactionalChangeImpl<Element> copy() {
    List<EChange<Element>> _clonedEChanges = this.getClonedEChanges();
    return new TransactionalChangeImpl<Element>(_clonedEChanges);
  }

  @Override
  public boolean equals(final Object obj) {
    boolean _xifexpression = false;
    if ((obj == this)) {
      _xifexpression = true;
    } else {
      boolean _xifexpression_1 = false;
      if ((obj == null)) {
        _xifexpression_1 = false;
      } else {
        boolean _xifexpression_2 = false;
        if ((obj instanceof TransactionalChange)) {
          List _eChanges = ((TransactionalChange)obj).getEChanges();
          _xifexpression_2 = Objects.equals(this.eChanges, _eChanges);
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
    return this.eChanges.hashCode();
  }

  private static URI getObjectUri(final EObject object) {
    URI _xblockexpression = null;
    {
      final Resource objectResource = object.eResource();
      URI _xifexpression = null;
      if ((objectResource != null)) {
        _xifexpression = objectResource.getURI();
      } else {
        URI _xifexpression_1 = null;
        boolean _eIsProxy = object.eIsProxy();
        if (_eIsProxy) {
          URI _xblockexpression_1 = null;
          {
            final URI proxyURI = ((InternalEObject) object).eProxyURI();
            URI _xifexpression_2 = null;
            if (((proxyURI != null) && (proxyURI.segmentCount() > 0))) {
              _xifexpression_2 = proxyURI.trimFragment();
            } else {
              _xifexpression_2 = null;
            }
            _xblockexpression_1 = _xifexpression_2;
          }
          _xifexpression_1 = _xblockexpression_1;
        } else {
          _xifexpression_1 = null;
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  private static <T extends Object> Set<T> setOfNotNull(final T element) {
    Set<T> _xifexpression = null;
    if ((element != null)) {
      _xifexpression = Set.<T>of(element);
    } else {
      _xifexpression = Set.of();
    }
    return _xifexpression;
  }

  private static <T extends Object> Set<T> setOfNotNull(final T element1, final T element2) {
    Set<T> _xifexpression = null;
    if ((element1 == null)) {
      _xifexpression = TransactionalChangeImpl.<T>setOfNotNull(element2);
    } else {
      Set<T> _xifexpression_1 = null;
      if ((element2 == null)) {
        _xifexpression_1 = Set.<T>of(element1);
      } else {
        _xifexpression_1 = Set.<T>of(element1, element2);
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  private static <T extends Object> Set<T> setOfNotNull(final T element1, final T element2, final T element3) {
    Set<T> _xifexpression = null;
    if ((element1 == null)) {
      _xifexpression = TransactionalChangeImpl.<T>setOfNotNull(element2, element3);
    } else {
      Set<T> _xifexpression_1 = null;
      if ((element2 == null)) {
        _xifexpression_1 = TransactionalChangeImpl.<T>setOfNotNull(element1, element3);
      } else {
        Set<T> _xifexpression_2 = null;
        if ((element3 == null)) {
          _xifexpression_2 = Set.<T>of(element1, element2);
        } else {
          _xifexpression_2 = Set.<T>of(element1, element2, element3);
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  @Override
  public String toString() {
    if (this.eChanges.isEmpty()) {
      return this.getClass().getSimpleName() + " (empty)";
    } else {
      StringBuilder _builder = new StringBuilder();
      _builder.append(this.getClass().getSimpleName()).append(": [").append(System.lineSeparator());
      for (EChange<Element> eChange : this.eChanges) {
        _builder.append("\t").append(this.getStringRepresentation(eChange)).append(System.lineSeparator());
      }
      _builder.append("]").append(System.lineSeparator());
      return _builder.toString();
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
