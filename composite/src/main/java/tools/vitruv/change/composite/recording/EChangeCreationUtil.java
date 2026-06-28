package tools.vitruv.change.composite.recording;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.EObjectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.resource.Resource;
import java.util.function.Function;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.TypeInferringCompoundEChangeFactory;
import tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;

/**
 * A utility class providing extension methods for transforming change
 * descriptions to change models.
 */
final class EChangeCreationUtil {
  public static <A extends EObject> List<AdditiveAttributeEChange<A, Object>>
      createAdditiveEChangeForAttribute(final A affectedEObject, final EAttribute affectedAttribute) {
    List<AdditiveAttributeEChange<A, Object>> xifexpression = null;
    boolean _isMany = affectedAttribute.isMany();
    if (_isMany) {
      final List<?> _featureValues = EObjectUtil.getFeatureValues(affectedEObject, affectedAttribute);
      final List<AdditiveAttributeEChange<A, Object>> _result = new ArrayList<>();
      for (int _i = 0; _i < _featureValues.size(); _i++) {
        _result.add(TypeInferringAtomicEChangeFactory.getInstance()
            .<A, Object>createInsertAttributeChange(affectedEObject, affectedAttribute, _i, _featureValues.get(_i)));
      }
      xifexpression = _result;
    } else {
      final Object oldValue = affectedAttribute.getDefaultValue();
      final Object newValue = EObjectUtil.getFeatureValue(affectedEObject, affectedAttribute);
      ReplaceSingleValuedEAttribute<A, Object> _createReplaceSingleAttributeChange = TypeInferringAtomicEChangeFactory
          .getInstance()
          .<A, Object>createReplaceSingleAttributeChange(affectedEObject, affectedAttribute, oldValue, newValue);
      return List.<AdditiveAttributeEChange<A, Object>>of(_createReplaceSingleAttributeChange);
    }
    return xifexpression;
  }

  @SuppressWarnings("unchecked")
  public static EList<EObject> getReferenceValueList(final EObject eObject,
      final EReference reference) {
    EList<?> _valueList = EObjectUtil.getValueList(eObject, reference);
    return (EList<EObject>) _valueList;
  }

  public static List<EChange<EObject>> createAdditiveEChangeForReferencedObject(
      final EObject referencingEObject, final EReference reference, final Function<EObject, Boolean> isCreate) {
    List<EChange<EObject>> xifexpression = null;
    boolean _isMany = reference.isMany();
    if (_isMany) {
      final List<EChange<EObject>> _result = new ArrayList<>();
      for (EObject referenceValue : EChangeCreationUtil.getReferenceValueList(referencingEObject, reference)) {
        Object _eGet = referencingEObject.eGet(reference);
        _result.addAll(EChangeCreationUtil.createInsertReferenceChange(referencingEObject, reference,
            ((EList<?>) _eGet).indexOf(referenceValue), referenceValue,
            (isCreate.apply(referenceValue)).booleanValue()));
      }
      xifexpression = _result;
    } else {
      List<EChange<EObject>> xblockexpression = null;
      {
        final EObject referenceValue = EChangeCreationUtil.getReferenceValueList(referencingEObject, reference).get(0);
        xblockexpression = EChangeCreationUtil.createReplaceSingleValuedReferenceChange(referencingEObject, reference,
            null, referenceValue, (isCreate.apply(referenceValue)).booleanValue());
      }
      xifexpression = xblockexpression;
    }
    return xifexpression;
  }

  private static boolean isChangeableUnderivedPersistedNotContainingFeature(final EObject eObject,
      final EStructuralFeature feature) {
    return (((feature.isChangeable() && (!feature.isDerived())) && (!feature.isTransient())) &&
        (!Objects.equals(eObject.eContainer(), eObject.eGet(feature))));
  }

  private static boolean valueIsNonDefault(final EObject eObject, final EStructuralFeature feature) {
    final Object value = eObject.eGet(feature);
    boolean _isMany = feature.isMany();
    if (_isMany) {
      final List<?> list = ((List<?>) value);
      return ((list != null) && (!list.isEmpty()));
    } else {
      Object _defaultValue = feature.getDefaultValue();
      return (!Objects.equals(value, _defaultValue));
    }
  }

  public static boolean hasChangeableUnderivedPersistedNotContainingNonDefaultValue(final EObject eObject,
      final EStructuralFeature feature) {
    return (EChangeCreationUtil.isChangeableUnderivedPersistedNotContainingFeature(eObject, feature) &&
        EChangeCreationUtil.valueIsNonDefault(eObject, feature));
  }

  public static boolean isDelete(final EObject newContainer, final Resource newResource) {
    return (((newContainer == null) || (newContainer instanceof ChangeDescription)) && (newResource == null));
  }

  public static List<EChange<EObject>> createInsertReferenceChange(final EObject affectedEObject,
      final EReference affectedReference, final int index, final EObject referenceValue, final boolean forceCreate) {
    final boolean isContainment = affectedReference.isContainment();
    final Resource oldResource = referenceValue.eResource();
    final boolean isCreate = (forceCreate || (isContainment && (oldResource == null)));
    if (isCreate) {
      return TypeInferringCompoundEChangeFactory.getInstance().<EObject, EObject>createCreateAndInsertNonRootChange(
          affectedEObject, affectedReference, referenceValue, index);
    } else {
      return List.<EChange<EObject>>of(
          TypeInferringAtomicEChangeFactory.getInstance().<EObject>createInsertReferenceChange(affectedEObject,
              affectedReference, referenceValue, index));
    }
  }

  public static List<EChange<EObject>> createRemoveReferenceChange(final EObject affectedEObject,
      final EReference affectedReference, final int index, final EObject referenceValue, final EObject newContainer,
      final Resource newResource, final boolean forceDelete) {
    final boolean isContainment = affectedReference.isContainment();
    final boolean isDelete = (forceDelete
        || (isContainment && EChangeCreationUtil.isDelete(newContainer, newResource)));
    List<EChange<EObject>> xifexpression = null;
    if (isDelete) {
      xifexpression = TypeInferringCompoundEChangeFactory.getInstance()
          .<EObject, EObject>createRemoveAndDeleteNonRootChange(affectedEObject, affectedReference, referenceValue,
              index);
    } else {
      xifexpression = List.<EChange<EObject>>of(
          TypeInferringAtomicEChangeFactory.getInstance().<EObject>createRemoveReferenceChange(affectedEObject,
              affectedReference, referenceValue, index));
    }
    return xifexpression;
  }

  public static List<EChange<EObject>> createReplaceSingleValuedReferenceChange(
      final EObject affectedEObject, final EReference affectedReference,
      final EObject oldReferenceValue, final EObject newReferenceValue, final boolean forceCreate) {
    final boolean isContainment = affectedReference.isContainment();
    List<EChange<EObject>> xifexpression = null;
    if ((forceCreate || isContainment)) {
      List<EChange<EObject>> xifexpression1 = null;
      if ((oldReferenceValue == null)) {
        xifexpression1 = TypeInferringCompoundEChangeFactory.getInstance()
            .<EObject, EObject>createCreateAndReplaceNonRootChange(affectedEObject, affectedReference,
                newReferenceValue);
      } else {
        List<EChange<EObject>> xifexpression2 = null;
        if ((newReferenceValue == null)) {
          xifexpression2 = TypeInferringCompoundEChangeFactory.getInstance()
              .<EObject, EObject>createReplaceAndDeleteNonRootChange(affectedEObject, affectedReference,
                  oldReferenceValue);
        } else {
          xifexpression2 = TypeInferringCompoundEChangeFactory.getInstance()
              .<EObject, EObject>createCreateAndReplaceAndDeleteNonRootChange(affectedEObject, affectedReference,
                  oldReferenceValue, newReferenceValue);
        }
        xifexpression1 = xifexpression2;
      }
      xifexpression = xifexpression1;
    } else {
      xifexpression = List.<EChange<EObject>>of(
          TypeInferringAtomicEChangeFactory.getInstance().<EObject>createReplaceSingleReferenceChange(affectedEObject,
              affectedReference, oldReferenceValue, newReferenceValue));
    }
    return xifexpression;
  }

  private EChangeCreationUtil() {

  }
}
