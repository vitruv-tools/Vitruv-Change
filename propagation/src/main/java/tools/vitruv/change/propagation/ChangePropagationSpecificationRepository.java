package tools.vitruv.change.propagation;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import tools.vitruv.change.composite.MetamodelDescriptor;

/**
 * Default {@link ChangePropagationSpecificationProvider} that holds a fixed set of
 * change propagation specifications, grouped by their source metamodel.
 *
 * <p>Each specification can be assigned a level. Specifications without an
 * assigned level have level {@code 0}.
 */
public class ChangePropagationSpecificationRepository
        implements ChangePropagationSpecificationProvider {
  private final Map<MetamodelDescriptor,
          List<ChangePropagationSpecification>> sourceMetamodelToPropagationSpecifications;
  private final Map<ChangePropagationSpecification,
          Integer> changePropagationSpecificationToLevel;

  /**
   * Creates a repository with the given specifications, all at level {@code 0}.
   *
   * @param specifications the change propagation specifications to provide
   */
  public ChangePropagationSpecificationRepository(
          final Iterable<ChangePropagationSpecification> specifications) {
    this(specifications, Map.of());
  }

  /**
   * Creates a repository with the given specifications and their levels.
   *
   * @param specifications the change propagation specifications to provide
   * @param changePropagationSpecificationToLevel the level of each specification;
   *     specifications without an entry have level {@code 0}
   */
  public ChangePropagationSpecificationRepository(
          final Iterable<ChangePropagationSpecification> specifications,
          final Map<ChangePropagationSpecification,
                  Integer> changePropagationSpecificationToLevel) {
    this.sourceMetamodelToPropagationSpecifications = new HashMap<>();
    final Consumer<ChangePropagationSpecification> function = specification ->
            this.sourceMetamodelToPropagationSpecifications
                    .computeIfAbsent(specification.getSourceMetamodelDescriptor(),
                            it -> new ArrayList<>())
                    .add(specification);
    specifications.forEach(function);
    this.changePropagationSpecificationToLevel =
            new HashMap<>(changePropagationSpecificationToLevel);
  }

  @Override
  public List<ChangePropagationSpecification> getChangePropagationSpecifications(
          final MetamodelDescriptor sourceMetamodelDescriptor) {
    return this.sourceMetamodelToPropagationSpecifications.keySet().stream()
        .filter(it -> it.overlaps(sourceMetamodelDescriptor))
        .flatMap(it ->
                this.sourceMetamodelToPropagationSpecifications.get(it).stream())
        .toList();
  }

  @Override
  public int getChangePropagationSpecificationLevel(ChangePropagationSpecification specification) {
    return this.changePropagationSpecificationToLevel.getOrDefault(specification, 0);
  }

  @Override
  public int getMaximumPropagationSpecificationLevel() {
    return this.changePropagationSpecificationToLevel
            .values().stream().max(Integer::compareTo).orElse(0);
  }

  @Override
  public Iterator<ChangePropagationSpecification> iterator() {
    return Iterables.concat(this.sourceMetamodelToPropagationSpecifications.values()).iterator();
  }
}
