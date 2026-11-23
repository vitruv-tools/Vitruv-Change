package tools.vitruv.change.propagation;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * A change propagation specification is instantiated once per virtual model, and every change
 * propagation registers an observer at it for its duration. Parallel propagations therefore
 * register, deregister and notify observers of one specification at the same time.
 */
class ChangePropagationSpecificationConcurrencyTest {
  private static final int THREADS = 8;
  private static final int ITERATIONS = 20_000;

  private static class NoOpSpecification extends AbstractChangePropagationSpecification {
    NoOpSpecification() {
      super(MetamodelDescriptor.with(Set.of("a")), MetamodelDescriptor.with(Set.of("b")));
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public ChangePropagationInvocation propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      return ChangePropagationInvocation.succeeded(this, List.of(change), List.of());
    }
  }

  private static class NoOpObserver implements ChangePropagationObserver {
    @Override
    public void objectCreated(EObject createdObject) {
      // nothing to do
    }

    @Override
    public void changePropagationStarted(ChangePropagationSpecification specification,
        EChange<EObject> change) {
      // nothing to do
    }

    @Override
    public void changePropagationStopped(ChangePropagationSpecification specification,
        EChange<EObject> change) {
      // nothing to do
    }
  }

  @Test
  void observersCanBeRegisteredAndNotifiedConcurrently() throws Exception {
    NoOpSpecification specification = new NoOpSpecification();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    CyclicBarrier start = new CyclicBarrier(THREADS);
    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < THREADS; t++) {
      Thread thread = new Thread(() -> {
        EObject created = EcoreFactory.eINSTANCE.createEObject();
        try {
          start.await();
          for (int i = 0; i < ITERATIONS; i++) {
            ChangePropagationObserver observer = new NoOpObserver();
            specification.registerObserver(observer);
            specification.notifyObjectCreated(created);
            specification.deregisterObserver(observer);
          }
        } catch (Throwable e) {
          failure.compareAndSet(null, e);
        }
      }, "propagation-" + t);
      threads.add(thread);
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
    assertNull(failure.get(), () -> "concurrent observer handling failed: " + failure.get());
  }
}
