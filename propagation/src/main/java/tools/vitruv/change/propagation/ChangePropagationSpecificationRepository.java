package tools.vitruv.change.propagation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tools.vitruv.change.composite.MetamodelDescriptor;

/**
 * A {@link ChangePropagationSpecificationProvider} backed by a {@link Map} from
 * source metamodel descriptors to {@link ChangePropagationSpecification}s.
 */
public class ChangePropagationSpecificationRepository
    implements ChangePropagationSpecificationProvider {
  private Map<MetamodelDescriptor, List<ChangePropagationSpecification>>
      sourceMetamodelToPropagationSpecifications;

  /**
   * Creates a new {@link ChangePropagationSpecificationRepository} for all specifications.
   *
   * @param specifications - {@link Iterable}
   */
  public ChangePropagationSpecificationRepository(
      final Iterable<ChangePropagationSpecification> specifications) {
    sourceMetamodelToPropagationSpecifications = new HashMap<>();
    specifications.forEach(specification ->
        sourceMetamodelToPropagationSpecifications
        .computeIfAbsent(specification.getSourceMetamodelDescriptor(), 
          desc -> new ArrayList<ChangePropagationSpecification>())
        .add(specification)
    );
  }

  @Override
  public List<ChangePropagationSpecification> getChangePropagationSpecifications(
      MetamodelDescriptor sourceMetamodelDescriptor) {
    return sourceMetamodelToPropagationSpecifications
      .entrySet()
      .stream()
      .filter(entry -> entry.getKey().equals(sourceMetamodelDescriptor))
      .flatMap(entry -> entry.getValue().stream())
      .toList();
  }

  @Override
  public Iterator<ChangePropagationSpecification> iterator() {
    return sourceMetamodelToPropagationSpecifications
      .values()
      .stream()
      .flatMap(Collection::stream)
      .toList()
      .iterator();
  }
}
