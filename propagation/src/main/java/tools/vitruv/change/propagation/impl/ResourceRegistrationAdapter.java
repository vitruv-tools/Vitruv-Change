package tools.vitruv.change.propagation.impl;

import java.util.function.Consumer;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * An adapter that reacts to additions of resources in a {@link ResourceSet} to call the
 * callback function for that resource given to the constructor.
 */

public class ResourceRegistrationAdapter implements Adapter {
  private final Consumer<Resource> resourceRegistrationFunction;

  public ResourceRegistrationAdapter(final Consumer<Resource> resourceRegistrationFunction) {
    this.resourceRegistrationFunction = resourceRegistrationFunction;
  }

  @Override
  public Notifier getTarget() {
    return null;
  }

  @Override
  public boolean isAdapterForType(final Object type) {
    return false;
  }

  @Override
  public void notifyChanged(final Notification notification) {
    if (notification.getEventType() == Notification.ADD
        && notification.getNotifier() instanceof ResourceSet
        && (notification.getNewValue() instanceof Resource resource)) {
      this.resourceRegistrationFunction.accept(resource);
    }
  }

  @Override
  public void setTarget(final Notifier newTarget) {
    // ResourceRegistrationAdapter has no targets
  }
}
