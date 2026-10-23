package tools.vitruv.change.propagation.impl;

import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.propagation.ModelRepositorySnapshot;

record TransactionalChangeWithPreviousState(TransactionalChange<EObject> change,
                                            ModelRepositorySnapshot previousState) {
}