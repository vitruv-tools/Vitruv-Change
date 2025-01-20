package tools.vitruv.change.composite.recording;

import com.google.common.base.Objects;
import edu.kit.ipd.sdq.activextendannotations.Utility;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.EObjectUtil;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.TypeInferringCompoundEChangeFactory;
import tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

/**
 * A utility class providing extension methods for transforming change descriptions to change models.
 */
@Utility
@SuppressWarnings("all")
final class EChangeCreationUtil {
  public static <A extends EObject> List<AdditiveAttributeEChange<EObject, ?>> createAdditiveEChangeForAttribute(final A affectedEObject, final EAttribute affectedAttribute) {
    List<AdditiveAttributeEChange<EObject, ?>> _xifexpression = null;
    boolean _isMany = affectedAttribute.isMany();
    if (_isMany) {
      final Function2<Integer, Object, AdditiveAttributeEChange<EObject, ?>> _function = (Integer index, Object value) -> {
        InsertEAttributeValue<A, Object> _createInsertAttributeChange = TypeInferringAtomicEChangeFactory.getInstance().<A, Object>createInsertAttributeChange(affectedEObject, affectedAttribute, (index).intValue(), value);
        return ((AdditiveAttributeEChange<EObject, ?>) _createInsertAttributeChange);
      };
      _xifexpression = IterableUtil.mapFixedIndexed(EObjectUtil.getFeatureValues(affectedEObject, affectedAttribute), _function);
    } else {
      final Object oldValue = affectedAttribute.getDefaultValue();
      final Object newValue = EObjectUtil.getFeatureValue(affectedEObject, affectedAttribute);
      ReplaceSingleValuedEAttribute<A, Object> _createReplaceSingleAttributeChange = TypeInferringAtomicEChangeFactory.getInstance().<A, Object>createReplaceSingleAttributeChange(affectedEObject, affectedAttribute, oldValue, newValue);
      return List.<AdditiveAttributeEChange<EObject, ?>>of(
        ((AdditiveAttributeEChange<EObject, ?>) _createReplaceSingleAttributeChange));
    }
    return _xifexpression;
  }

  public static EList<? extends EObject> getReferenceValueList(final EObject eObject, final EReference reference) {
    EList<?> _valueList = EObjectUtil.getValueList(eObject, reference);
    return ((EList<EObject>) _valueList);
  }

  public static List<? extends EChange<EObject>> createAdditiveEChangeForReferencedObject(final EObject referencingEObject, final EReference reference, final Function1<? super EObject, ? extends Boolean> isCreate) {
    List<? extends EChange<EObject>> _xifexpression = null;
    boolean _isMany = reference.isMany();
    if (_isMany) {
      final Function1<EObject, List<? extends EChange<EObject>>> _function = (EObject referenceValue) -> {
        Object _eGet = referencingEObject.eGet(reference);
        return EChangeCreationUtil.createInsertReferenceChange(referencingEObject, reference, 
          ((EList<?>) _eGet).indexOf(referenceValue), referenceValue, 
          (isCreate.apply(referenceValue)).booleanValue());
      };
      _xifexpression = IterableUtil.flatMapFixed(EChangeCreationUtil.getReferenceValueList(referencingEObject, reference), _function);
    } else {
      List<? extends EChange<EObject>> _xblockexpression = null;
      {
        final EObject referenceValue = EChangeCreationUtil.getReferenceValueList(referencingEObject, reference).get(0);
        _xblockexpression = EChangeCreationUtil.createReplaceSingleValuedReferenceChange(referencingEObject, reference, null, referenceValue, (isCreate.apply(referenceValue)).booleanValue());
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }

  private static boolean isChangeableUnderivedPersistedNotContainingFeature(final EObject eObject, final EStructuralFeature feature) {
    return (((feature.isChangeable() && (!feature.isDerived())) && (!feature.isTransient())) && 
      (!Objects.equal(eObject.eContainer(), eObject.eGet(feature))));
  }

  private static boolean valueIsNonDefault(final EObject eObject, final EStructuralFeature feature) {
    final Object value = eObject.eGet(feature);
    boolean _isMany = feature.isMany();
    if (_isMany) {
      final List<?> list = ((List<?>) value);
      return ((list != null) && (!list.isEmpty()));
    } else {
      Object _defaultValue = feature.getDefaultValue();
      return (!Objects.equal(value, _defaultValue));
    }
  }

  public static boolean hasChangeableUnderivedPersistedNotContainingNonDefaultValue(final EObject eObject, final EStructuralFeature feature) {
    return (EChangeCreationUtil.isChangeableUnderivedPersistedNotContainingFeature(eObject, feature) && 
      EChangeCreationUtil.valueIsNonDefault(eObject, feature));
  }

  public static boolean isDelete(final EObject newContainer, final Resource newResource) {
    return (((newContainer == null) || (newContainer instanceof ChangeDescription)) && (newResource == null));
  }

  public static List<? extends EChange<EObject>> createInsertReferenceChange(final EObject affectedEObject, final EReference affectedReference, final int index, final EObject referenceValue, final boolean forceCreate) {
    final boolean isContainment = affectedReference.isContainment();
    final Resource oldResource = referenceValue.eResource();
    final boolean isCreate = (forceCreate || (isContainment && (oldResource == null)));
    if (isCreate) {
      return TypeInferringCompoundEChangeFactory.getInstance().<EObject, EObject>createCreateAndInsertNonRootChange(affectedEObject, affectedReference, referenceValue, index);
    } else {
      return List.<InsertEReference<EObject>>of(
        TypeInferringAtomicEChangeFactory.getInstance().<EObject>createInsertReferenceChange(affectedEObject, affectedReference, referenceValue, index));
    }
  }

  public static List<? extends EChange<EObject>> createRemoveReferenceChange(final EObject affectedEObject, final EReference affectedReference, final int index, final EObject referenceValue, final EObject newContainer, final Resource newResource, final boolean forceDelete) {
    final boolean isContainment = affectedReference.isContainment();
    final boolean isDelete = (forceDelete || (isContainment && EChangeCreationUtil.isDelete(newContainer, newResource)));
    List<? extends EChange<EObject>> _xifexpression = null;
    if (isDelete) {
      _xifexpression = TypeInferringCompoundEChangeFactory.getInstance().<EObject, EObject>createRemoveAndDeleteNonRootChange(affectedEObject, affectedReference, referenceValue, index);
    } else {
      _xifexpression = List.<RemoveEReference<EObject>>of(
        TypeInferringAtomicEChangeFactory.getInstance().<EObject>createRemoveReferenceChange(affectedEObject, affectedReference, referenceValue, index));
    }
    return _xifexpression;
  }

  public static List<? extends EChange<EObject>> createReplaceSingleValuedReferenceChange(final EObject affectedEObject, final EReference affectedReference, final EObject oldReferenceValue, final EObject newReferenceValue, final boolean forceCreate) {
    final boolean isContainment = affectedReference.isContainment();
    List<? extends EChange<EObject>> _xifexpression = null;
    if ((forceCreate || isContainment)) {
      List<EChange<EObject>> _xifexpression_1 = null;
      if ((oldReferenceValue == null)) {
        _xifexpression_1 = TypeInferringCompoundEChangeFactory.getInstance().<EObject, EObject>createCreateAndReplaceNonRootChange(affectedEObject, affectedReference, newReferenceValue);
      } else {
        List<EChange<EObject>> _xifexpression_2 = null;
        if ((newReferenceValue == null)) {
          _xifexpression_2 = TypeInferringCompoundEChangeFactory.getInstance().<EObject, EObject>createReplaceAndDeleteNonRootChange(affectedEObject, affectedReference, oldReferenceValue);
        } else {
          _xifexpression_2 = TypeInferringCompoundEChangeFactory.getInstance().<EObject, EObject>createCreateAndReplaceAndDeleteNonRootChange(affectedEObject, affectedReference, oldReferenceValue, newReferenceValue);
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    } else {
      _xifexpression = List.<ReplaceSingleValuedEReference<EObject>>of(
        TypeInferringAtomicEChangeFactory.getInstance().<EObject>createReplaceSingleReferenceChange(affectedEObject, affectedReference, oldReferenceValue, newReferenceValue));
    }
    return _xifexpression;
  }

  private EChangeCreationUtil() {
    
  }
}
