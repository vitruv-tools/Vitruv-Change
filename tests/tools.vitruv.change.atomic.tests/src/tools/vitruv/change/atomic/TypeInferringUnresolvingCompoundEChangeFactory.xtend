package tools.vitruv.change.atomic

import tools.vitruv.change.atomic.uuid.UuidResolver

package final class TypeInferringUnresolvingCompoundEChangeFactory extends TypeInferringCompoundEChangeFactory {
	
	new(UuidResolver uuidResolver) {
		super(new TypeInferringUnresolvingAtomicEChangeFactory(uuidResolver));
	}
	
}
