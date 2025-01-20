package tools.vitruv.change.composite.description.impl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
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

@SuppressWarnings("all")
public class TransactionalChangeImpl<Element extends Object> implements TransactionalChange<Element> {
  private List<? extends EChange<Element>> eChanges;

  private final List<UserInteractionBase> userInteractions = new ArrayList<UserInteractionBase>();

  public TransactionalChangeImpl(final Iterable<? extends EChange<Element>> eChanges) {
    this.eChanges = IterableExtensions.toList(Preconditions.<Iterable<? extends EChange<Element>>>checkNotNull(eChanges, "eChanges"));
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
    final Function1<EChange<Element>, URI> _function = (EChange<Element> it) -> {
      return TransactionalChangeImpl.getChangedURI(it);
    };
    return IterableExtensions.<URI>toSet(IterableExtensions.<URI>filterNull(ListExtensions.map(this.eChanges, _function)));
  }

  @Override
  public MetamodelDescriptor getAffectedEObjectsMetamodelDescriptor() {
    Set<EObject> _affectedEObjects = this.getAffectedEObjects();
    HashSet<EPackage> _hashSet = new HashSet<EPackage>();
    final Function2<HashSet<EPackage>, EObject, HashSet<EPackage>> _function = (HashSet<EPackage> affectedPackages, EObject changedObject) -> {
      HashSet<EPackage> _xblockexpression = null;
      {
        EPackage currentPackage = changedObject.eClass().getEPackage();
        while ((currentPackage.getESuperPackage() != null)) {
          currentPackage = currentPackage.getESuperPackage();
        }
        if ((currentPackage != null)) {
          affectedPackages.add(currentPackage);
        }
        _xblockexpression = affectedPackages;
      }
      return _xblockexpression;
    };
    final HashSet<EPackage> changedPackages = IterableExtensions.<EObject, HashSet<EPackage>>fold(_affectedEObjects, _hashSet, _function);
    boolean _isEmpty = changedPackages.isEmpty();
    boolean _not = (!_isEmpty);
    Preconditions.checkState(_not, "Cannot identify the packages of this change:%s%s", 
      System.lineSeparator(), this);
    return MetamodelDescriptor.of(changedPackages);
  }

  @Override
  public Set<EObject> getAffectedEObjects() {
    final Function1<EChange<Element>, Set<EObject>> _function = (EChange<Element> it) -> {
      return TransactionalChangeImpl.getAffectedEObjects(it);
    };
    return IterableExtensions.<EObject>toSet(IterableExtensions.flatMap(this.eChanges, _function));
  }

  @Override
  public Set<EObject> getAffectedAndReferencedEObjects() {
    final Function1<EChange<Element>, Set<EObject>> _function = (EChange<Element> it) -> {
      return TransactionalChangeImpl.getAffectedAndReferencedEObjects(it);
    };
    return IterableExtensions.<EObject>toSet(IterableExtensions.flatMap(this.eChanges, _function));
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
    Set<EObject> _switchResult = null;
    boolean _matched = false;
    if (eChange instanceof UpdateAttributeEChange) {
      _matched=true;
      _switchResult = Set.<EObject>of(((UpdateAttributeEChange<EObject>)eChange).getAffectedElement());
    }
    if (!_matched) {
      if (eChange instanceof ReplaceSingleValuedEReference) {
        _matched=true;
        _switchResult = TransactionalChangeImpl.<EObject>setOfNotNull(((ReplaceSingleValuedEReference<EObject>)eChange).getAffectedElement(), ((ReplaceSingleValuedEReference<EObject>)eChange).getOldValue(), ((ReplaceSingleValuedEReference<EObject>)eChange).getNewValue());
      }
    }
    if (!_matched) {
      if (eChange instanceof InsertEReference) {
        _matched=true;
        _switchResult = Set.<EObject>of(((InsertEReference<EObject>)eChange).getAffectedElement(), ((InsertEReference<EObject>)eChange).getNewValue());
      }
    }
    if (!_matched) {
      if (eChange instanceof RemoveEReference) {
        _matched=true;
        _switchResult = Set.<EObject>of(((RemoveEReference<EObject>)eChange).getAffectedElement(), ((RemoveEReference<EObject>)eChange).getOldValue());
      }
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
    final Function1<EChange<Element>, EChange<Element>> _function = (EChange<Element> it) -> {
      return EcoreUtil.<EChange<Element>>copy(it);
    };
    return IterableUtil.mapFixed(this.eChanges, _function);
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
          _xifexpression_2 = Objects.equal(this.eChanges, _eChanges);
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
      _xifexpression = CollectionLiterals.<T>emptySet();
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
    String _xifexpression = null;
    boolean _isEmpty = this.eChanges.isEmpty();
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
        for(final EChange<Element> eChange : this.eChanges) {
          _builder_1.append("\t");
          CharSequence _stringRepresentation = this.getStringRepresentation(eChange);
          _builder_1.append(_stringRepresentation, "\t");
          _builder_1.newLineIfNotEmpty();
        }
      }
      _builder_1.append("]");
      _builder_1.newLine();
      _xifexpression = _builder_1.toString();
    }
    return _xifexpression;
  }

  private CharSequence getStringRepresentation(final EChange<?> change) {
    CharSequence _switchResult = null;
    boolean _matched = false;
    if (change instanceof InsertRootEObject) {
      _matched=true;
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("insert ");
      Object _newValue = ((InsertRootEObject<?>)change).getNewValue();
      _builder.append(_newValue);
      _builder.append(" at ");
      String _uri = ((InsertRootEObject<?>)change).getUri();
      _builder.append(_uri);
      _builder.append(" (index ");
      int _index = ((InsertRootEObject<?>)change).getIndex();
      _builder.append(_index);
      _builder.append(")");
      _switchResult = _builder;
    }
    if (!_matched) {
      if (change instanceof RemoveRootEObject) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("remove ");
        Object _oldValue = ((RemoveRootEObject<?>)change).getOldValue();
        _builder.append(_oldValue);
        _builder.append(" from ");
        String _uri = ((RemoveRootEObject<?>)change).getUri();
        _builder.append(_uri);
        _builder.append(" (index ");
        int _index = ((RemoveRootEObject<?>)change).getIndex();
        _builder.append(_index);
        _builder.append(")");
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof CreateEObject) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("create ");
        Object _affectedElement = ((CreateEObject<?>)change).getAffectedElement();
        _builder.append(_affectedElement);
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof DeleteEObject) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("delete ");
        Object _affectedElement = ((DeleteEObject<?>)change).getAffectedElement();
        _builder.append(_affectedElement);
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof UnsetFeature) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _affectedFeatureString = this.getAffectedFeatureString(((FeatureEChange<?, ?>)change));
        _builder.append(_affectedFeatureString);
        _builder.append(" = ");
        _builder.append("∅");
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof ReplaceSingleValuedEAttribute) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _affectedFeatureString = this.getAffectedFeatureString(((FeatureEChange<?, ?>)change));
        _builder.append(_affectedFeatureString);
        _builder.append(" = ");
        Object _newValue = ((ReplaceSingleValuedEAttribute<?, ?>)change).getNewValue();
        _builder.append(_newValue);
        _builder.append(" (was ");
        Object _oldValue = ((ReplaceSingleValuedEAttribute<?, ?>)change).getOldValue();
        _builder.append(_oldValue);
        _builder.append(")");
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof ReplaceSingleValuedEReference) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _affectedFeatureString = this.getAffectedFeatureString(((FeatureEChange<?, ?>)change));
        _builder.append(_affectedFeatureString);
        _builder.append(" = ");
        Object _newValue = ((ReplaceSingleValuedEReference<?>)change).getNewValue();
        _builder.append(_newValue);
        _builder.append(" (was ");
        Object _oldValue = ((ReplaceSingleValuedEReference<?>)change).getOldValue();
        _builder.append(_oldValue);
        _builder.append(")");
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof InsertEAttributeValue) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _affectedFeatureString = this.getAffectedFeatureString(((FeatureEChange<?, ?>)change));
        _builder.append(_affectedFeatureString);
        _builder.append(" += ");
        Object _newValue = ((InsertEAttributeValue<?, ?>)change).getNewValue();
        _builder.append(_newValue);
        _builder.append(" (index ");
        int _index = ((InsertEAttributeValue<?, ?>)change).getIndex();
        _builder.append(_index);
        _builder.append(")");
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof InsertEReference) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _affectedFeatureString = this.getAffectedFeatureString(((FeatureEChange<?, ?>)change));
        _builder.append(_affectedFeatureString);
        _builder.append(" += ");
        Object _newValue = ((InsertEReference<?>)change).getNewValue();
        _builder.append(_newValue);
        _builder.append(" (index ");
        int _index = ((InsertEReference<?>)change).getIndex();
        _builder.append(_index);
        _builder.append(")");
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof RemoveEAttributeValue) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _affectedFeatureString = this.getAffectedFeatureString(((FeatureEChange<?, ?>)change));
        _builder.append(_affectedFeatureString);
        _builder.append(" -= ");
        Object _oldValue = ((RemoveEAttributeValue<?, ?>)change).getOldValue();
        _builder.append(_oldValue);
        _builder.append(" (index ");
        int _index = ((RemoveEAttributeValue<?, ?>)change).getIndex();
        _builder.append(_index);
        _builder.append(")");
        _switchResult = _builder;
      }
    }
    if (!_matched) {
      if (change instanceof RemoveEReference) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _affectedFeatureString = this.getAffectedFeatureString(((FeatureEChange<?, ?>)change));
        _builder.append(_affectedFeatureString);
        _builder.append(" -= ");
        Object _oldValue = ((RemoveEReference<?>)change).getOldValue();
        _builder.append(_oldValue);
        _builder.append(" (index ");
        int _index = ((RemoveEReference<?>)change).getIndex();
        _builder.append(_index);
        _builder.append(")");
        _switchResult = _builder;
      }
    }
    return _switchResult;
  }

  private CharSequence getAffectedFeatureString(final FeatureEChange<?, ?> change) {
    StringConcatenation _builder = new StringConcatenation();
    Object _affectedElement = change.getAffectedElement();
    _builder.append(_affectedElement);
    _builder.append(".");
    String _name = change.getAffectedFeature().getName();
    _builder.append(_name);
    return _builder;
  }
}
