package tools.vitruv.change.correspondence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.correspondence.model.CorrespondenceModelFactory;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;

/**
 * Concurrent change propagations of one virtual model share its correspondence model: adding,
 * reading and removing correspondences of unrelated elements on several threads must not corrupt
 * it, and each thread must see its own correspondences.
 */
class CorrespondenceConcurrencyTest {
  private static final int THREADS = 8;
  private static final int ROUNDS = 1_000;

  private static Root root(String id) {
    Root root = AllElementTypesFactory.eINSTANCE.createRoot();
    root.setId(id);
    return root;
  }

  private static void expect(boolean condition, String message, Root element,
      List<String> failures) {
    if (!condition) {
      failures.add(message + ": " + element.getId());
    }
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
            // reads of the thread's own correspondences, interleaved with the others' writes
            expect(model.getCorrespondingEObjects(Correspondence.class, List.of(a), "t")
                .equals(Set.of(List.of(b))), "own correspondence not found", a, failures);
            expect(model.hasCorrespondences(List.of(b)), "own correspondence not found", b,
                failures);
            if (i % 2 == 0) {
              model.removeCorrespondencesBetween(Correspondence.class, List.of(a), List.of(b), "t");
              expect(!model.hasCorrespondences(List.of(a)), "removed correspondence still found", a,
                  failures);
            } else {
              kept.add(a);
            }
            if (i % 50 == 0) {
              model.getAllTags(); // now and then a read over all correspondences
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
