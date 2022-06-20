package tools.vitruv.change.atomic

import tools.vitruv.change.atomic.TypeInferringCompoundEChangeFactory
import tools.vitruv.change.atomic.id.IdResolver

package final class TypeInferringUnresolvingCompoundEChangeFactory extends TypeInferringCompoundEChangeFactory {
	
	new(IdResolver idResolver) {
		super(new TypeInferringUnresolvingAtomicEChangeFactory(idResolver));
	}
	
}
