package tools.vitruv.change.correspondence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.correspondence.model.CorrespondenceModelFactory;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;

/**
 * Concurrent change propagations of one virtual model share its correspondence model: adding and
 * removing correspondences of unrelated elements on several threads must not corrupt it.
 */
class CorrespondenceConcurrencyTest {
  private static final int THREADS = 8;
  private static final int ROUNDS = 2_000;

  private static Root root(String id) {
    Root root = AllElementTypesFactory.eINSTANCE.createRoot();
    root.setId(id);
    return root;
  }

  @Test
  void concurrentChangesOfUnrelatedCorrespondences() throws Exception {
    PersistableCorrespondenceModel model =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(null);
    List<String> failures = new CopyOnWriteArrayList<>();
    List<Root> kept = new CopyOnWriteArrayList<>();
    CyclicBarrier start = new CyclicBarrier(THREADS);
    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < THREADS; t++) {
      int thread = t;
      threads.add(new Thread(() -> {
        try {
          start.await();
          for (int i = 0; i < ROUNDS; i++) {
            Root a = root(thread + "a" + i);
            Root b = root(thread + "b" + i);
            model.addCorrespondenceBetween(List.of(a), List.of(b), "t",
                CorrespondenceFactory.eINSTANCE::createManualCorrespondence);
            if (i % 2 == 0) {
              model.removeCorrespondencesBetween(Correspondence.class, List.of(a), List.of(b), "t");
            } else {
              kept.add(a);
            }
          }
        } catch (Throwable e) {
          failures.add(e.toString());
        }
      }));
    }
    threads.forEach(Thread::start);
    for (Thread thread : threads) {
      thread.join();
    }

    assertEquals(List.of(), failures);
    // two elements per kept correspondence
    assertEquals(THREADS * ROUNDS, model.getAllEObjectsInACorrespondence().size());
    for (Root a : kept) {
      assertEquals(1, model.getCorrespondingEObjects(Correspondence.class, List.of(a), "t").size(),
          () -> "lost the correspondence of " + a.getId());
    }
  }
}
