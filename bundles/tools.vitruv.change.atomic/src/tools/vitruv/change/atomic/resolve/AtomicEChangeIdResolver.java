package tools.vitruv.change.atomic.resolve;

import static com.google.common.base.Preconditions.checkState;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.command.ApplyEChangeSwitch;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.reference.SubtractiveReferenceEChange;
import tools.vitruv.change.atomic.feature.reference.UpdateReferenceEChange;
import tools.vitruv.change.atomic.id.HierarchicalId;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.resolve.internal.AtomicEChangeResolverHelper;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Static class for resolving EChanges internally.
 */
public class AtomicEChangeIdResolver {
	private IdResolver idResolver;

	public AtomicEChangeIdResolver(IdResolver idResolver) {
		this.idResolver = idResolver;
	}
	
	public void applyBackward(EChange<EObject> resolvedEChange) {
		ApplyEChangeSwitch.applyEChange(resolvedEChange, false);
	}

	public EChange<EObject> resolveAndApplyForward(EChange<HierarchicalId> unresolvedEChange) {
		EChange<EObject> resolvedChange = resolve(unresolvedEChange);
		applyForward(resolvedChange);
		return resolvedChange;
	}
	
	public EChange<HierarchicalId> applyForwardAndAssignIds(EChange<EObject> resolvedEChange) {
		EChange<HierarchicalId> unresolvedChange = unresolve(resolvedEChange);
		applyForward(resolvedEChange);
		return unresolvedChange;
	}

	private EChange<EObject> resolve(EChange<HierarchicalId> unresolvedChange) {
		return AtomicEChangeResolverHelper.resolveChange(unresolvedChange, 
				id -> {
			if (unresolvedChange instanceof CreateEObject<HierarchicalId> createChange) {
				EObject createdElement = EcoreUtil.create(createChange.getAffectedEObjectType());
				HierarchicalId createdId = idResolver.getAndUpdateId(createdElement);
				checkState(createdId.equals(id), "generated ID %s does not match the original ID %s on element creation", createdId, id);
				return createdElement;
			} else {
				return idResolver.getEObject(id);
			}
		}, resource -> idResolver.getResource(resource.getURI()));
	}

	private EChange<HierarchicalId> unresolve(EChange<EObject> resolvedChange) {
		return AtomicEChangeResolverHelper.resolveChange(resolvedChange, 
				eObject -> idResolver.getAndUpdateId(eObject), 
				resource -> idResolver.getResource(resource.getURI()));
	}
	
	private void applyForward(EChange<EObject> resolvedChange) {
		EObject affectedEObject = getAffectedEObject(resolvedChange);
		HierarchicalId affectedId = idResolver.getAndUpdateId(affectedEObject);
		EObject oldObject = getOldContainedEObject(resolvedChange);
		ApplyEChangeSwitch.applyEChange(resolvedChange, true);
		if (isContainmentChange(resolvedChange) || affectedId != idResolver.getAndUpdateId(affectedEObject)) {
			refreshIds(affectedEObject);
		}
		if (oldObject != null) {
			refreshIds(oldObject);
		}
	}
	
	private static EObject getAffectedEObject(EChange<EObject> eChange) {
		if (eChange instanceof FeatureEChange<EObject, ?> featureChange) {
			return featureChange.getAffectedElement();
		}
		else if (eChange instanceof EObjectExistenceEChange<EObject> existenceChange) {
			return existenceChange.getAffectedElement();
		}
		else if (eChange instanceof InsertRootEObject<EObject> insertChange) {
			return insertChange.getNewValue();
		}
		else if (eChange instanceof RemoveRootEObject<EObject> removeChange) {
			return removeChange.getOldValue();
		}
		throw new IllegalArgumentException("can not identify affected EObject of change %s".formatted(eChange));
	}
	
	private static EObject getOldContainedEObject(EChange<EObject> eChange) {
		if (eChange instanceof SubtractiveReferenceEChange<EObject> subtractiveChange) {
			if (subtractiveChange.isContainment()) {
				return subtractiveChange.getOldValue();
			}
		}
		return null;
	}
	
	private static boolean isContainmentChange(EChange<EObject> eChange) {
		if (eChange instanceof UpdateReferenceEChange<EObject> referenceChange) {
			return referenceChange.isContainment();
		}
		return false;
	}
	
	private void refreshIds(EObject eObject) {
		idResolver.getAndUpdateId(eObject);
		eObject.eContents().forEach(this::refreshIds);
	}
}
	