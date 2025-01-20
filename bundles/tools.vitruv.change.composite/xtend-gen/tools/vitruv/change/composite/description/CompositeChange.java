package tools.vitruv.change.composite.description;

import java.util.List;
import java.util.Set;
import tools.vitruv.change.composite.MetamodelDescriptor;

@SuppressWarnings("all")
public interface CompositeChange<Element extends Object, ContainedChange extends VitruviusChange<Element>> extends VitruviusChange<Element> {
  /**
   * Returns the metamodel descriptors for the metamodels of the elements whose instances
   * have been modified in this change. It collects the metamodel descriptors for all composed
   * changes (according to {@link #getChanges}).
   * 
   * @return the metamodel descriptors of the composed changes
   */
  @Override
  Set<MetamodelDescriptor> getAffectedEObjectsMetamodelDescriptors();

  /**
   * Returns the changes this one is composed of.
   * 
   * @return the composed changes
   */
  List<ContainedChange> getChanges();

  @Override
  CompositeChange<Element, ContainedChange> copy();
}
