package tools.vitruv.change.changederivation

import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl
import org.eclipse.emf.compare.EMFCompare
import org.eclipse.emf.compare.utils.UseIdentifiers

/**
 * This default strategy for diff based state changes uses EMFCompare to resolve a 
 * diff to a sequence of individual changes.
 */
class DefaultStateBasedChangeResolutionStrategy extends AbstractStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
	
	/** The identifier matching behavior used by this strategy */
    public val UseIdentifiers useIdentifiers

    /**
     * Creates a new instance with the default identifier matching behavior 
     * which is match by identifier when available.
     */
    new() {
        this(UseIdentifiers.WHEN_AVAILABLE)
    }

    /**
     * Creates a new instance with the provided identifier matching behavior.
     * @param useIdentifiers The identifier matching behavior to use.
     */
    new(UseIdentifiers useIdentifiers) {
        this.useIdentifiers = useIdentifiers
    }
	
	override protected getEMFCompareInstance() {
		return (EMFCompare.builder => [
            matchEngineFactoryRegistry = MatchEngineFactoryRegistryImpl.createStandaloneInstance => [
                add(new MatchEngineFactoryImpl(useIdentifiers))
            ]
        ]).build
	}
}
