package tools.vitruv.change.atomic.resolve.internal;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

public class AtomicEChangeCopier {
	public static <Source, Target> EChange<Target> copy(EChange<Source> change) {
		if (change instanceof InsertRootEObject<Source> c) {
			InsertRootEObject<Target> result = getChangeFactory().createInsertRootChange(null, c.getResource(), c.getIndex());
			if (c.getUri() != null) {
				result.setUri(c.getUri());
			}
			return result;
		}
		else if (change instanceof RemoveRootEObject<Source> c) {
			RemoveRootEObject<Target> result = getChangeFactory().createRemoveRootChange(null, c.getResource(), null, c.getIndex());
			if (c.getUri() != null) {
				result.setUri(c.getUri());
			}
			return result;
		}
		
		else if (change instanceof InsertEAttributeValue<Source, ?> c) {
			return getChangeFactory().createInsertAttributeChange(null, c.getAffectedFeature(), c.getIndex(), c.getNewValue());
		}
		else if (change instanceof ReplaceSingleValuedEAttribute<Source, ?> c) {
			return getChangeFactory().createReplaceSingleAttributeChange(null, c.getAffectedFeature(), c.getOldValue(), c.getNewValue());
		}
		else if (change instanceof RemoveEAttributeValue<Source, ?> c) {
			return getChangeFactory().createRemoveAttributeChange(null, c.getAffectedFeature(), c.getIndex(), c.getOldValue());
		}
		
		else if (change instanceof InsertEReference<Source> c) {
			return getChangeFactory().createInsertReferenceChange(null, c.getAffectedFeature(), null, c.getIndex());
		}
		else if (change instanceof ReplaceSingleValuedEReference<Source> c) {
			return getChangeFactory().createReplaceSingleReferenceChange(null, c.getAffectedFeature(), null, null);
		}
		else if (change instanceof RemoveEReference<Source> c) {
			return getChangeFactory().createRemoveReferenceChange(null, c.getAffectedFeature(), null, c.getIndex());
		}
		
		else if (change instanceof CreateEObject<Source> c) {
			CreateEObject<Target> result = EobjectFactory.eINSTANCE.createCreateEObject();
			result.setAffectedEObjectType(c.getAffectedEObjectType());
			result.setIdAttributeValue(c.getIdAttributeValue());
			return result;
		}
		else if (change instanceof DeleteEObject<Source> c) {
			DeleteEObject<Target> result = EobjectFactory.eINSTANCE.createDeleteEObject();
			result.setAffectedEObjectType(c.getAffectedEObjectType());
			result.setIdAttributeValue(c.getIdAttributeValue());
			return result;
		}
		
		else if (change instanceof UnsetFeature<Source, ?> c) {
			return copyUnsetFeature(c);
		}
		throw new IllegalStateException("trying to copy unknown change of type " + change.getClass().getSimpleName());
	}
	
	private static TypeInferringAtomicEChangeFactory getChangeFactory() {
		return TypeInferringAtomicEChangeFactory.getInstance();
	}
	
	private static <Source, Target, F extends EStructuralFeature> UnsetFeature<Target, F> copyUnsetFeature(UnsetFeature<Source, F> change) {
		UnsetFeature<Target, F> result = FeatureFactory.eINSTANCE.createUnsetFeature();
		result.setAffectedFeature(change.getAffectedFeature());
		return result;
	}
}
