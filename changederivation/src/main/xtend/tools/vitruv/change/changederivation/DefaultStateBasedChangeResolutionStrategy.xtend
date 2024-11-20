package tools.vitruv.change.changederivation

import org.eclipse.emf.compare.utils.UseIdentifiers
import java.util.List
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl
import org.eclipse.emf.compare.diff.DiffBuilder
import org.eclipse.emf.compare.diff.DefaultDiffEngine

/**
 * This is a default strategy to resolve a diff as a sequence of individual changes
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
	
	override protected getMatchEngineFactories() {
		return List.of(new MatchEngineFactoryImpl(useIdentifiers))
	}
	
	override protected getDiffEngine() {
		return new DefaultDiffEngine(new DiffBuilder());
	}
	
	override protected getPostProcessors() {
		return List.of()
	}
	
}
