package tools.vitruv.change.propagation;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import tools.vitruv.change.composite.MetamodelDescriptor;

@SuppressWarnings("all")
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
    final Function1<MetamodelDescriptor, Boolean> _function = (MetamodelDescriptor it) -> {
      return Boolean.valueOf(sourceMetamodelDescriptor.contains(it));
    };
    final Function1<MetamodelDescriptor, List<ChangePropagationSpecification>> _function_1 = (MetamodelDescriptor it) -> {
      return this.sourceMetamodelToPropagationSpecifications.get(it);
    };
    return IterableExtensions.<ChangePropagationSpecification>toList(IterableExtensions.<MetamodelDescriptor, ChangePropagationSpecification>flatMap(IterableExtensions.<MetamodelDescriptor>filter(this.sourceMetamodelToPropagationSpecifications.keySet(), _function), _function_1));
  }

  @Override
  public Iterator<ChangePropagationSpecification> iterator() {
    return IterableExtensions.<ChangePropagationSpecification>toList(Iterables.<ChangePropagationSpecification>concat(this.sourceMetamodelToPropagationSpecifications.values())).iterator();
  }
}
