package tools.vitruv.change.testutils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the refactored private helpers of {@link TestProjectManager}, exercised through
 * reflection since they are not part of the public API.
 */
class TestProjectManagerTest {

  private static Path createUniqueDirectory(Path projectPath) throws Exception {
    Method method = TestProjectManager.class.getDeclaredMethod("createUniqueDirectory", Path.class);
    method.setAccessible(true);
    return (Path) method.invoke(null, projectPath);
  }

  private static void deleteRecursively(Path path) throws Exception {
    Method method = TestProjectManager.class.getDeclaredMethod("deleteRecursively", Path.class);
    method.setAccessible(true);
    method.invoke(null, path);
  }

  @SuppressWarnings("unchecked")
  private static Stream<Path> walkIfExists(Path path) throws Exception {
    Method method = TestProjectManager.class.getDeclaredMethod("walkIfExists", Path.class);
    method.setAccessible(true);
    return (Stream<Path>) method.invoke(null, path);
  }

  @Test
  void createUniqueDirectoryCreatesTheRequestedDirectory(@TempDir Path tempDir) throws Exception {
    Path requested = tempDir.resolve("project");

    Path created = createUniqueDirectory(requested);

    assertEquals(requested, created);
    assertTrue(Files.isDirectory(created));
  }

  @Test
  void createUniqueDirectoryAppendsCounterWhenNameIsTaken(@TempDir Path tempDir) throws Exception {
    Path requested = tempDir.resolve("project");

    Path first = createUniqueDirectory(requested);
    Path second = createUniqueDirectory(requested);
    Path third = createUniqueDirectory(requested);

    assertEquals(requested, first);
    assertEquals(tempDir.resolve("project 2"), second);
    assertEquals(tempDir.resolve("project 3"), third);
    assertTrue(Files.isDirectory(second));
    assertTrue(Files.isDirectory(third));
  }

  @Test
  void deleteRecursivelyRemovesAWholeTree(@TempDir Path tempDir) throws Exception {
    Path root = tempDir.resolve("tree");
    Path nested = root.resolve("sub");
    Files.createDirectories(nested);
    Files.createFile(nested.resolve("file.txt"));

    deleteRecursively(root);

    assertFalse(Files.exists(root));
  }

  @Test
  void deleteRecursivelyIgnoresAMissingPath(@TempDir Path tempDir) {
    Path missing = tempDir.resolve("does-not-exist");
    assertDoesNotThrow(() -> deleteRecursively(missing),
        "Deleting a non-existent path should be a no-op");
  }

  @Test
  void walkIfExistsReturnsEmptyForMissingPath(@TempDir Path tempDir) throws Exception {
    try (Stream<Path> walked = walkIfExists(tempDir.resolve("missing"))) {
      assertEquals(0, walked.count());
    }
  }
}
