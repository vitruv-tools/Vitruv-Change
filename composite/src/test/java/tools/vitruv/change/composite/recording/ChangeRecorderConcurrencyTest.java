package tools.vitruv.change.composite.recording;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;

/**
 * Records changes of several threads at once with one {@link ChangeRecorder}, as parallel change
 * propagations in one virtual model would: each thread changes only its own model and must get
 * exactly its own changes.
 */
@ExtendWith(RegisterMetamodelsInStandalone.class)
class ChangeRecorderConcurrencyTest {
  private static final int THREADS = 4;
  private static final int ROUNDS = 200;
  private static final int CHANGES_PER_ROUND = 50;

  @Test
  void eachThreadRecordsOnlyItsOwnChanges() throws Exception {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put("*", new XMIResourceFactoryImpl());
    List<Root> roots = new ArrayList<>();
    for (int t = 0; t < THREADS; t++) {
      Resource resource = resourceSet.createResource(URI.createFileURI("/model" + t + ".xmi"));
      Root root = AllElementTypesFactory.eINSTANCE.createRoot();
      resource.getContents().add(root);
      roots.add(root);
    }
    try (ChangeRecorder recorder = new ChangeRecorder(resourceSet)) {
      recorder.addToRecording(resourceSet);
      AtomicReference<Throwable> failure = new AtomicReference<>();
      CyclicBarrier roundStart = new CyclicBarrier(THREADS);
      List<Thread> threads = new ArrayList<>();
      for (int t = 0; t < THREADS; t++) {
        Root root = roots.get(t);
        Thread thread = new Thread(() -> {
          try {
            for (int round = 0; round < ROUNDS; round++) {
              roundStart.await();
              recorder.beginRecording();
              for (int i = 1; i <= CHANGES_PER_ROUND; i++) {
                root.setSingleValuedEAttribute(round * CHANGES_PER_ROUND + i);
              }
              TransactionalChange<EObject> change = recorder.endRecording();
              assertEquals(CHANGES_PER_ROUND, change.getEChanges().size(),
                  "number of recorded changes");
              for (EChange<EObject> eChange : change.getEChanges()) {
                assertSame(root,
                    ((ReplaceSingleValuedEAttribute<?, ?>) eChange).getAffectedElement(),
                    "recorded a change of another thread");
              }
            }
          } catch (Throwable e) {
            failure.compareAndSet(null, e);
            roundStart.reset();
          }
        }, "recording-" + t);
        threads.add(thread);
        thread.start();
      }
      for (Thread thread : threads) {
        thread.join();
      }
      assertNull(failure.get(), () -> "concurrent recording failed: " + failure.get());
    }
  }
}
