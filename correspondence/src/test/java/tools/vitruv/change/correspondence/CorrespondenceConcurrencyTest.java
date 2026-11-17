package tools.vitruv.change.correspondence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.correspondence.model.CorrespondenceChange;
import tools.vitruv.change.correspondence.model.CorrespondenceModelFactory;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;

/**
 * Concurrent change propagations of one virtual model share its correspondence model: adding and
 * removing correspondences of unrelated elements on several threads must not corrupt it, and
 * each thread records and reverts only its own changes.
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

  @Test
  void eachThreadRecordsAndRevertsOnlyItsOwnChanges() throws Exception {
    PersistableCorrespondenceModel model =
        CorrespondenceModelFactory.createPersistableCorrespondenceModel(null);
    Root shared = root("shared");
    Root other = root("other");
    model.addCorrespondenceBetween(
        List.of(shared), List.of(other), "old",
        CorrespondenceFactory.eINSTANCE::createManualCorrespondence);

    model.startRecording();
    Root mine = root("mine");
    model.addCorrespondenceBetween(
        List.of(mine), List.of(shared), "new",
        CorrespondenceFactory.eINSTANCE::createManualCorrespondence);
    model.removeCorrespondencesBetween(
        Correspondence.class, List.of(shared), List.of(other), "old");
    // another thread changes the model meanwhile, without recording
    Root foreign = root("foreign");
    Thread thread = new Thread(() -> model.addCorrespondenceBetween(List.of(foreign),
        List.of(other), "foreign", CorrespondenceFactory.eINSTANCE::createManualCorrespondence));
    thread.start();
    thread.join();

    List<CorrespondenceChange> changes = model.getRecordedChanges();
    assertEquals(List.of(CorrespondenceChange.Kind.ADDED, CorrespondenceChange.Kind.REMOVED),
        changes.stream().map(CorrespondenceChange::kind).toList());

    model.revertRecordedChanges();
    assertEquals(Set.of(List.<EObject>of(other)),
        model.getCorrespondingEObjects(Correspondence.class, List.of(shared), "old"),
        "the removed correspondence is back");
    assertEquals(Set.of(),
        model.getCorrespondingEObjects(Correspondence.class, List.of(mine), "new"),
        "the added correspondence is gone");
    assertEquals(Set.of(List.<EObject>of(other)),
        model.getCorrespondingEObjects(Correspondence.class, List.of(foreign), "foreign"),
        "the other thread's correspondence stays");
    assertEquals(List.of(), model.getRecordedChanges(), "recording starts afresh");
    model.stopRecording();
  }
}
