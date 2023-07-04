package tools.vitruv.change.atomic.resolve.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.root.RootEChange;

public class AtomicEChangeResolverHelper<Source, Target> {
	private Function<Source, Target> elementResolver;
	private Function<Resource, Resource> resourceResolver;

	private AtomicEChangeResolverHelper(Function<Source, Target> elementResolver, Function<Resource, Resource> resourceResolver) {
		checkArgument(elementResolver != null, "resolver must not be null");
		checkArgument(resourceResolver != null, "resolver must not be null");
		this.elementResolver = elementResolver;
		this.resourceResolver = resourceResolver;
	}

	public static <Source, Target> EChange<Target> resolveChange(EChange<Source> eChange, Function<Source, Target> elementResolver, Function<Resource, Resource> resourceResolver) {
		return new AtomicEChangeResolverHelper<>(elementResolver, resourceResolver).resolve(eChange);
	}

	/**
	 * Dispatch method for resolving the {@link EChange}.
	 * 
	 * @param change The change which should be resolved.
	 */
	private EChange<Target> resolve(EChange<Source> sourceChange) {
		EChange<Target> targetChange = AtomicEChangeCopier.copy(sourceChange);
		if (sourceChange instanceof FeatureEChange<Source, ?> sourceFeatureChange
				&& targetChange instanceof FeatureEChange<Target, ?> targetFeatureChange) {
			resolveFeatureEChange(sourceFeatureChange, targetFeatureChange);
		}

		if (sourceChange instanceof EObjectExistenceEChange<Source> sourceExistenceChange
				&& targetChange instanceof EObjectExistenceEChange<Target> targetExistenceChange) {
			resolve(sourceExistenceChange, targetExistenceChange);
		}

		if (sourceChange instanceof EObjectAddedEChange<Source> sourceAddedChange
				&& targetChange instanceof EObjectAddedEChange<Target> targetAddedChange) {
			resolve(sourceAddedChange, targetAddedChange);
		}

		if (sourceChange instanceof EObjectSubtractedEChange<Source> sourceRemoveChange
				&& targetChange instanceof EObjectSubtractedEChange<Target> targetRemoveChange) {
			resolve(sourceRemoveChange, targetRemoveChange);
		}

		if (sourceChange instanceof RootEChange<Source> sourceRootChange
				&& targetChange instanceof RootEChange<Target> targetRootChange) {
			resolve(sourceRootChange, targetRootChange);
			// TODO: check root value index
		}
		return targetChange;
	}

	/**
	 * Resolves {@link FeatureEChange} attributes {@code affectedElement} and
	 * {@code affectedFeature}.
	 * 
	 * @param change The change which should be resolved.
	 */
	private <F extends EStructuralFeature> void resolveFeatureEChange(FeatureEChange<Source, ?> sourceChange,
			FeatureEChange<Target, ?> targetChange) {
		checkArgument(sourceChange.getAffectedElement() != null, "change %s must have ann affected element",
				sourceChange);
		checkArgument(sourceChange.getAffectedFeature() != null, "change %s must have an affected feature",
				sourceChange);
		targetChange.setAffectedElement(elementResolver.apply(sourceChange.getAffectedElement()));
		checkNotNullAndNotProxy(targetChange.getAffectedElement(), sourceChange, "affected element");
	}

	/**
	 * Method for resolving the {@link EObjectAddedEChange} EChange.
	 * 
	 * @param change The change which should be resolved.
	 */
	private void resolve(EObjectAddedEChange<Source> sourceChange, EObjectAddedEChange<Target> targetChange) {
		if (sourceChange.getNewValue() != null) {
			targetChange.setNewValue(elementResolver.apply(sourceChange.getNewValue()));
			checkNotNullAndNotProxy(targetChange.getNewValue(), sourceChange, "new element");
		}
	}

	/**
	 * Method for resolving the {@link EObjectSubtractedEChange} EChange.
	 * 
	 * @param change The change which should be resolved.
	 */
	private void resolve(EObjectSubtractedEChange<Source> sourceChange, EObjectSubtractedEChange<Target> targetChange) {
		if (sourceChange.getOldValue() != null) {
			targetChange.setOldValue(elementResolver.apply(sourceChange.getOldValue()));
			checkNotNullAndNotProxy(targetChange.getOldValue(), sourceChange, "old element");
		}
	}

	/**
	 * Resolves {@link RootEChange} attribute {@code resource}.
	 * 
	 * @param change The change which should be resolved.
	 */
	private void resolve(RootEChange<Source> sourceChange, RootEChange<Target> targetChange) {
		if (sourceChange.getResource() != null) {
			targetChange.setResource(resourceResolver.apply(sourceChange.getResource()));
		}
	}

	private void resolve(EObjectExistenceEChange<Source> sourceChange, EObjectExistenceEChange<Target> targetChange) {
		// TODO: what happens with deleted elements / newly created elements?
		targetChange.setAffectedElement(elementResolver.apply(sourceChange.getAffectedElement()));
		checkNotNullAndNotProxy(targetChange.getAffectedElement(), sourceChange, "affected element");
	}

	private static <Source, Target> void checkNotNullAndNotProxy(Target object, EChange<Source> change,
			String nameOfElementInChange) {
		checkState(object != null, "%s of change %s was resolved to null", nameOfElementInChange, change);
		if (object instanceof EObject eObject) {
			checkState(!eObject.eIsProxy(), "%s of change %s was resolved to a proxy", nameOfElementInChange, object);
		}
	}
}
