package tools.vitruv.change.atomic.uuid;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tools.vitruv.change.testutils.metamodels.AllElementTypesCreators.aet;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests that the transactions of a UUID resolver used by several threads are those of the threads:
 * ending the transaction on one thread checks and forgets only the elements that thread handled.
 */
class UuidResolverTransactionConcurrencyTest {
  private ExecutorService otherThread;
  private UuidResolver resolver;
  private Resource resource;

  @BeforeEach
  void setUp() {
    otherThread = Executors.newSingleThreadExecutor();
    var resourceSet = new ResourceSetImpl();
    resource = new ResourceImpl(URI.createFileURI("/vitruv/test/model.allElementTypes"));
    resourceSet.getResources().add(resource);
    resolver = UuidResolver.create(resourceSet);
    resolver.endTransaction();
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    otherThread.shutdownNow();
    otherThread.awaitTermination(10, TimeUnit.SECONDS);
  }

  @Test
  @DisplayName("ending a transaction does not check the elements another thread is changing")
  void endTransactionIgnoresElementsOfOtherThreads() throws Exception {
    Root otherRoot = aet.Root();
    // the other thread removes a registered root and has not unregistered it yet
    onOtherThread(
        () -> {
          resource.getContents().add(otherRoot);
          resolver.registerEObject(otherRoot);
          resource.getContents().remove(otherRoot);
        });

    Root root = aet.Root();
    resource.getContents().add(root);
    resolver.registerEObject(root);
    assertDoesNotThrow(resolver::endTransaction);

    onOtherThread(
        () -> {
          resolver.unregisterEObject(resolver.getUuid(otherRoot), otherRoot);
          resolver.endTransaction();
        });
  }

  @Test
  @DisplayName("ending a transaction does not forget the elements another thread handled")
  void endTransactionKeepsElementsOfOtherThreads() throws Exception {
    Root root = aet.Root();
    resource.getContents().add(root);
    resolver.registerEObject(root);
    resolver.endTransaction();

    NonRoot nonRoot = aet.NonRoot();
    onOtherThread(
        () -> {
          root.getMultiValuedContainmentEReference().add(nonRoot);
          resolver.registerEObject(nonRoot);
        });

    resolver.endTransaction();

    // the other thread removes the element without unregistering it, which its own end of the
    // transaction must detect
    onOtherThread(() -> root.getMultiValuedContainmentEReference().remove(nonRoot));
    var failure =
        assertThrows(ExecutionException.class, () -> onOtherThread(resolver::endTransaction));
    assertThrows(IllegalStateException.class, () -> {
      throw failure.getCause();
    });
  }

  private void onOtherThread(Runnable action) throws Exception {
    otherThread.submit(action).get(10, TimeUnit.SECONDS);
  }
}
