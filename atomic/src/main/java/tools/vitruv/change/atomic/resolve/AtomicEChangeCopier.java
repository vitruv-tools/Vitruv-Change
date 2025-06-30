package tools.vitruv.change.atomic.resolve;

import org.eclipse.emf.ecore.EStructuralFeature;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootEChange;
import tools.vitruv.change.atomic.root.RootFactory;

/** A copier for {@link EChange}s that copies the change to a new type. */
public class AtomicEChangeCopier {
    
  private AtomicEChangeCopier() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }
  
  /**
   * Copy the given change to a new type.
   *
   * @param <Source> The type of the change to copy.
   * @param <Target> The type of the copied change.
   * @param change The change to copy.
   * @return The copied change.
   */
  public static <Source, Target> EChange<Target> copy(EChange<Source> change) {
    EChange<Target> result = copyOld(change);
    if (change instanceof EObjectExistenceEChange<Source> existenceChange
        && result instanceof EObjectExistenceEChange<Target> existenceTarget) {
      existenceTarget.setIdAttributeValue(existenceChange.getIdAttributeValue());
      existenceTarget.setAffectedEObjectType(existenceChange.getAffectedEObjectType());
    }
    if (change instanceof RootEChange<Source> rootChange
        && result instanceof RootEChange<Target> rootResult) {
      rootResult.setUri(rootChange.getUri());
      rootResult.setIndex(rootChange.getIndex());
    }
    if (change instanceof ReplaceSingleValuedFeatureEChange<Source, ?, ?> featureChange
        && result instanceof ReplaceSingleValuedFeatureEChange<Target, ?, ?> featureResult) {
      featureResult.setIsUnset(featureChange.isIsUnset());
    }
    if (change instanceof AdditiveAttributeEChange<Source, ?> additiveChange
        && result instanceof AdditiveAttributeEChange<Target, ?> additiveResult) {
      additiveResult.setWasUnset(additiveChange.isWasUnset());
    }
    if (change instanceof AdditiveReferenceEChange<Source> additiveChange
        && result instanceof AdditiveReferenceEChange<Target> additiveResult) {
      additiveResult.setWasUnset(additiveChange.isWasUnset());
    }
    return result;
  }

  private static <Source, Target> EChange<Target> copyOld(EChange<Source> change) {
    if (change instanceof InsertRootEObject<Source>) {
      return RootFactory.eINSTANCE.createInsertRootEObject();
    } else if (change instanceof RemoveRootEObject<Source>) {
      return RootFactory.eINSTANCE.createRemoveRootEObject();
    } else if (change instanceof InsertEAttributeValue<Source, ?> c) {
      return getChangeFactory()
          .createInsertAttributeChange(null, c.getAffectedFeature(), c.getIndex(), c.getNewValue());
    } else if (change instanceof ReplaceSingleValuedEAttribute<Source, ?> c) {
      return getChangeFactory()
          .createReplaceSingleAttributeChange(
              null, c.getAffectedFeature(), c.getOldValue(), c.getNewValue());
    } else if (change instanceof RemoveEAttributeValue<Source, ?> c) {
      return getChangeFactory()
          .createRemoveAttributeChange(null, c.getAffectedFeature(), c.getIndex(), c.getOldValue());
    } else if (change instanceof InsertEReference<Source> c) {
      return getChangeFactory()
          .createInsertReferenceChange(null, c.getAffectedFeature(), null, c.getIndex());
    } else if (change instanceof ReplaceSingleValuedEReference<Source> c) {
      return getChangeFactory()
          .createReplaceSingleReferenceChange(null, c.getAffectedFeature(), null, null);
    } else if (change instanceof RemoveEReference<Source> c) {
      return getChangeFactory()
          .createRemoveReferenceChange(null, c.getAffectedFeature(), null, c.getIndex());
    } else if (change instanceof CreateEObject<Source>) {
      return EobjectFactory.eINSTANCE.createCreateEObject();
    } else if (change instanceof DeleteEObject<Source>) {
      return EobjectFactory.eINSTANCE.createDeleteEObject();
    } else if (change instanceof UnsetFeature<Source, ?> c) {
      return copyUnsetFeature(c);
    }
    throw new IllegalStateException(
        "trying to copy unknown change of type " + change.getClass().getSimpleName());
  }

  private static TypeInferringAtomicEChangeFactory getChangeFactory() {
    return TypeInferringAtomicEChangeFactory.getInstance();
  }

  private static <Source, Target, F extends EStructuralFeature>
      UnsetFeature<Target, F> copyUnsetFeature(UnsetFeature<Source, F> change) {
    UnsetFeature<Target, F> result = FeatureFactory.eINSTANCE.createUnsetFeature();
    result.setAffectedFeature(change.getAffectedFeature());
    return result;
  }
}
