package tools.vitruv.change.testutils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Exercises the full workspace and project lifecycle of {@link TestProjectManager} through the
 * JUnit extension, so that workspace setup, project creation and cleanup are covered end-to-end.
 */
@ExtendWith(TestProjectManager.class)
class TestProjectLifecycleTest {

  @Test
  void injectsAnExistingDefaultProjectDirectory(@TestProject Path project) {
    assertNotNull(project);
    assertTrue(Files.isDirectory(project), "The injected test project directory should exist");
  }

  @Test
  void injectsAnExistingNamedVariantProjectDirectory(
      @TestProject(variant = "variantOne") Path project) {
    assertNotNull(project);
    assertTrue(Files.isDirectory(project), "The injected variant project directory should exist");
  }
}
