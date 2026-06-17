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
@SuppressWarnings("all")
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
    int _eventType = notification.getEventType();
    boolean _equals = (_eventType == Notification.ADD);
    if (_equals) {
      Object _notifier = notification.getNotifier();
      if ((_notifier instanceof ResourceSet)) {
        Object _newValue = notification.getNewValue();
        if ((_newValue instanceof Resource)) {
          Object _newValue_1 = notification.getNewValue();
          final Resource resource = ((Resource) _newValue_1);
          this.resourceRegistrationFunction.accept(resource);
        }
      }
    }
  }

  @Override
  public void setTarget(final Notifier newTarget) {
  }
}
