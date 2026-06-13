package tools.vitruv.change.composite.description;

import java.util.Set;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.interaction.UserInteractionBase;

/**
 * A {@link TransactionalChange} defines one or more {@link VitruviusChange}s, which have to be performed
 * together. They were recorded together and have to propagated to models completely or not at all.
 */
@SuppressWarnings("all")
public interface TransactionalChange<Element extends Object> extends VitruviusChange<Element> {
  /**
   * Returns the unique metamodel descriptor for the metamodels of the elements whose instances
   * have been modified in this change.
   * 
   * @see VitruviusChange#getAffectedEObjectsMetamodelDescriptors
   */
  MetamodelDescriptor getAffectedEObjectsMetamodelDescriptor();

  @Override
  default Set<MetamodelDescriptor> getAffectedEObjectsMetamodelDescriptors() {
    return Set.<MetamodelDescriptor>of(this.getAffectedEObjectsMetamodelDescriptor());
  }

  void setUserInteractions(final Iterable<UserInteractionBase> userInputs);

  @Override
  TransactionalChange<Element> copy();
}
