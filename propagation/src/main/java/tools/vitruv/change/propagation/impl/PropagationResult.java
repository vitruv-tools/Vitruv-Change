package tools.vitruv.change.propagation.impl;

import java.util.List;
import tools.vitruv.change.composite.description.PropagatedChange;

record PropagationResult(TransactionalChangeWithPreviousState change,
                         List<PropagatedChange> propagatedChanges) {
}
