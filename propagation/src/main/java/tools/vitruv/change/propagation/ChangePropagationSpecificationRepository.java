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
  private Map<MetamodelDescriptor, List<ChangePropagationSpecification>> sourceMetamodelToPropagationSpecifications;

  public ChangePropagationSpecificationRepository(final Iterable<ChangePropagationSpecification> specifications) {
    HashMap<MetamodelDescriptor, List<ChangePropagationSpecification>> _hashMap = new HashMap<MetamodelDescriptor, List<ChangePropagationSpecification>>();
    this.sourceMetamodelToPropagationSpecifications = _hashMap;
    final Consumer<ChangePropagationSpecification> _function = (ChangePropagationSpecification specification) -> {
      final Function<MetamodelDescriptor, List<ChangePropagationSpecification>> _function_1 = (MetamodelDescriptor it) -> {
        return new ArrayList<ChangePropagationSpecification>();
      };
      this.sourceMetamodelToPropagationSpecifications.computeIfAbsent(specification.getSourceMetamodelDescriptor(), _function_1).add(specification);
    };
    specifications.forEach(_function);
  }

  @Override
  public List<ChangePropagationSpecification> getChangePropagationSpecifications(final MetamodelDescriptor sourceMetamodelDescriptor) {
    return this.sourceMetamodelToPropagationSpecifications.keySet().stream()
        .filter(sourceMetamodelDescriptor::contains)
        .flatMap(it -> this.sourceMetamodelToPropagationSpecifications.get(it).stream())
        .collect(Collectors.toList());
  }

  @Override
  public Iterator<ChangePropagationSpecification> iterator() {
    return Iterables.concat(this.sourceMetamodelToPropagationSpecifications.values()).iterator();
  }
}
