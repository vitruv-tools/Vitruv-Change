package tools.vitruv.change.propagation;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import tools.vitruv.change.composite.MetamodelDescriptor;

public class ChangePropagationSpecificationRepository implements ChangePropagationSpecificationProvider {
  private final Map<MetamodelDescriptor, List<ChangePropagationSpecification>> sourceMetamodelToPropagationSpecifications;
  private final Map<ChangePropagationSpecification, Integer> changePropagationSpecificationToLevel;

  public ChangePropagationSpecificationRepository(final Iterable<ChangePropagationSpecification> specifications) {
    this(specifications, Map.of());
  }

  public ChangePropagationSpecificationRepository(final Iterable<ChangePropagationSpecification> specifications, final Map<ChangePropagationSpecification, Integer> changePropagationSpecificationToLevel) {
    HashMap<MetamodelDescriptor, List<ChangePropagationSpecification>> _hashMap = new HashMap<MetamodelDescriptor, List<ChangePropagationSpecification>>();
    this.sourceMetamodelToPropagationSpecifications = _hashMap;
    final Consumer<ChangePropagationSpecification> _function = (ChangePropagationSpecification specification) -> {
      final Function<MetamodelDescriptor, List<ChangePropagationSpecification>> _function_1 = (MetamodelDescriptor it) -> {
        return new ArrayList<ChangePropagationSpecification>();
      };
      this.sourceMetamodelToPropagationSpecifications.computeIfAbsent(specification.getSourceMetamodelDescriptor(), _function_1).add(specification);
    };
    specifications.forEach(_function);
    this.changePropagationSpecificationToLevel = new HashMap<>(changePropagationSpecificationToLevel);
  }

  @Override
  public List<ChangePropagationSpecification> getChangePropagationSpecifications(final MetamodelDescriptor sourceMetamodelDescriptor) {
    return this.sourceMetamodelToPropagationSpecifications.keySet().stream()
        .filter(sourceMetamodelDescriptor::contains)
        .flatMap(it -> this.sourceMetamodelToPropagationSpecifications.get(it).stream())
        .collect(Collectors.toList());
  }

  @Override
  public int getChangePropagationSpecificationLevel(ChangePropagationSpecification specification) {
    return this.changePropagationSpecificationToLevel.getOrDefault(specification, 0);
  }

  @Override
  public Iterator<ChangePropagationSpecification> iterator() {
    return Iterables.concat(this.sourceMetamodelToPropagationSpecifications.values()).iterator();
  }
}
