package tools.vitruv.change.composite.description.impl;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.composite.description.CompositeContainerChange;
import tools.vitruv.change.composite.description.VitruviusChange;

public class CompositeContainerChangeImpl extends AbstractCompositeChangeImpl<VitruviusChange> implements CompositeContainerChange {
	public CompositeContainerChangeImpl(List<? extends VitruviusChange> changes) {
		super(changes);
	}
	
	@Override
	public CompositeContainerChangeImpl copy() {
		return new CompositeContainerChangeImpl(getChanges().stream().map(VitruviusChange::copy).toList());
	}

	@Override
	public VitruviusChange resolveAndApply(UuidResolver uuidResolver) {
		return new CompositeContainerChangeImpl(getChanges().stream().map(it -> it.resolveAndApply(uuidResolver)).toList());
	}

	@Override
	public VitruviusChange resolveAndApply(IdResolver idResolver) {
		return new CompositeContainerChangeImpl(getChanges().stream().map(it -> it.resolveAndApply(idResolver)).toList());
	}

	@Override
	public VitruviusChange unresolve() {
		return new CompositeContainerChangeImpl(getChanges().stream().map(VitruviusChange::unresolve).toList());
	}
}
