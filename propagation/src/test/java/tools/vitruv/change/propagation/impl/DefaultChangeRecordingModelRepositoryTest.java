package tools.vitruv.change.propagation.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DefaultChangeRecordingModelRepositoryTest {

  @TempDir Path folder;

  private DefaultChangeRecordingModelRepository repository;

  @BeforeEach
  void setUp() {
    repository =
        new DefaultChangeRecordingModelRepository(
            URI.createFileURI(folder.resolve("correspondences.correspondence").toString()),
            folder.resolve("metadata"));
  }

  @AfterEach
  void tearDown() throws Exception {
    repository.close();
  }

  @Test
  void recordChanges_canRecordAgain_afterTheApplicatorThrew() {
    RuntimeException failure = new RuntimeException("reaction failed");

    RuntimeException thrown =
        assertThrows(
            RuntimeException.class,
            () ->
                repository.recordChanges(
                    () -> {
                      throw failure;
                    }));

    // The applicator's own exception must surface unchanged ...
    org.junit.jupiter.api.Assertions.assertSame(failure, thrown);
    // ... and must not leave the recorder stuck in "recording".
    assertDoesNotThrow(() -> repository.recordChanges(() -> {}));
  }
}
