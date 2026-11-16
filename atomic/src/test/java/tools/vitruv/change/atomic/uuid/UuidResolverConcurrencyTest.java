package tools.vitruv.change.atomic.uuid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static tools.vitruv.change.testutils.metamodels.AllElementTypesCreators.aet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests that a UUID resolver can be used by several threads on disjoint elements. */
class UuidResolverConcurrencyTest {
  private static final int THREADS = 8;
  private static final int ROUNDS = 200;
  private static final int ELEMENTS = 50;

  @Test
  @DisplayName("register, look up and unregister disjoint elements from several threads")
  void registerAndUnregisterConcurrently() throws InterruptedException {
    UuidResolver resolver = UuidResolver.create(new ResourceSetImpl());
    List<String> failures = new CopyOnWriteArrayList<>();
    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < THREADS; t++) {
      threads.add(
          new Thread(
              () -> {
                try {
                  for (int round = 0; round < ROUNDS; round++) {
                    List<EObject> elements = new ArrayList<>();
                    List<Uuid> uuids = new ArrayList<>();
                    for (int i = 0; i < ELEMENTS; i++) {
                      EObject element = aet.NonRoot();
                      Uuid uuid = resolver.generateUuid(element);
                      resolver.registerEObject(uuid, element);
                      elements.add(element);
                      uuids.add(uuid);
                    }
                    for (int i = 0; i < ELEMENTS; i++) {
                      if (resolver.getEObject(uuids.get(i)) != elements.get(i)
                          || !resolver.getUuid(elements.get(i)).equals(uuids.get(i))) {
                        failures.add("wrong mapping");
                      }
                    }
                    for (int i = 0; i < ELEMENTS; i++) {
                      resolver.unregisterEObject(uuids.get(i), elements.get(i));
                    }
                    for (int i = 0; i < ELEMENTS; i++) {
                      if (resolver.hasUuid(elements.get(i))) {
                        failures.add("element still registered after unregistering");
                      }
                    }
                  }
                } catch (RuntimeException e) {
                  failures.add(e.toString());
                }
              }));
    }
    // a corrupted hash map can make its users loop forever
    threads.forEach(thread -> thread.setDaemon(true));
    threads.forEach(Thread::start);
    long deadline = System.currentTimeMillis() + 60_000;
    for (Thread thread : threads) {
      thread.join(Math.max(1, deadline - System.currentTimeMillis()));
      if (thread.isAlive()) {
        failures.add("thread did not finish within 60 s");
      }
    }

    assertEquals(List.of(), failures.stream().distinct().limit(10).toList());
    assertFalse(resolver.hasEObject(new Uuid("ord_unknown")));
  }
}
