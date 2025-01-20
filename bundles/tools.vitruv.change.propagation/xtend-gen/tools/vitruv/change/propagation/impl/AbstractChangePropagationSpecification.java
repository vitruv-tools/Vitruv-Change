package tools.vitruv.change.propagation.impl;

import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.interaction.UserInteractor;
import tools.vitruv.change.propagation.ChangePropagationObserver;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

@SuppressWarnings("all")
public abstract class AbstractChangePropagationSpecification implements ChangePropagationSpecification {
  private final List<ChangePropagationObserver> propagationObservers;

  private UserInteractor userInteractor;

  private MetamodelDescriptor sourceMetamodelDescriptor;

  private MetamodelDescriptor targetMetamodelDescriptor;

  public AbstractChangePropagationSpecification(final MetamodelDescriptor sourceMetamodelDescriptor, final MetamodelDescriptor targetMetamodelDescriptor) {
    this.sourceMetamodelDescriptor = sourceMetamodelDescriptor;
    this.targetMetamodelDescriptor = targetMetamodelDescriptor;
    this.propagationObservers = CollectionLiterals.<ChangePropagationObserver>newArrayList();
  }

  protected UserInteractor getUserInteractor() {
    return this.userInteractor;
  }

  @Override
  public MetamodelDescriptor getSourceMetamodelDescriptor() {
    return this.sourceMetamodelDescriptor;
  }

  @Override
  public MetamodelDescriptor getTargetMetamodelDescriptor() {
    return this.targetMetamodelDescriptor;
  }

  @Override
  public void setUserInteractor(final UserInteractor userInteractor) {
    this.userInteractor = userInteractor;
  }

  @Override
  public void registerObserver(final ChangePropagationObserver observer) {
    if ((observer != null)) {
      this.propagationObservers.add(observer);
    }
  }

  @Override
  public void deregisterObserver(final ChangePropagationObserver observer) {
    this.propagationObservers.remove(observer);
  }

  @Override
  public void notifyObjectCreated(final EObject createdObject) {
    final Consumer<ChangePropagationObserver> _function = (ChangePropagationObserver it) -> {
      it.objectCreated(createdObject);
    };
    this.propagationObservers.forEach(_function);
  }
}
