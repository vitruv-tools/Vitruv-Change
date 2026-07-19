package tools.vitruv.change.testutils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.io.TempDir;

class TestProjectManagerTest {

  private TestProjectManager manager;
  private ExtensionContext mockContext;
  private ExtensionContext.Store mockStore;
  private Map<Object, Object> storeMap;

  @TempDir
  private Path tempDir;

  @BeforeEach
  void setUp() {
    this.manager = new TestProjectManager();
    this.storeMap = new HashMap<>();

    this.mockStore = mock(ExtensionContext.Store.class);
    lenient().when(this.mockStore.getOrComputeIfAbsent(any(), any(), any()))
        .thenAnswer(invocation -> {
          final Object key = invocation.getArgument(0);
          final java.util.function.Function<Object, Object> func = invocation.getArgument(1);
          return this.storeMap.computeIfAbsent(key, func);
        });
    lenient().doAnswer(invocation -> {
      this.storeMap.put(invocation.getArgument(0), invocation.getArgument(1));
      return null;
    }).when(this.mockStore).put(any(), any());
    lenient().when(this.mockStore.getOrDefault(any(), any(), any()))
        .thenAnswer(invocation -> {
          return this.storeMap.getOrDefault(invocation.getArgument(0), invocation.getArgument(2));
        });

    this.mockContext = mock(ExtensionContext.class);
    lenient().when(this.mockContext.getRoot()).thenReturn(this.mockContext);
    lenient().when(this.mockContext.getStore(any())).thenReturn(this.mockStore);
    lenient().when(this.mockContext.getDisplayName()).thenReturn("TestProjectManagerTest");
    lenient().when(this.mockContext.getParent()).thenReturn(Optional.empty());
    lenient().when(this.mockContext.getExecutionException()).thenReturn(Optional.empty());

    System.setProperty(TestProjectManager.WORKSPACE_PATH_SYSTEM_PROPERTY, this.tempDir.toString());
  }

  @AfterEach
  void tearDown() {
    System.clearProperty(TestProjectManager.WORKSPACE_PATH_SYSTEM_PROPERTY);
    System.clearProperty(TestProjectManager.RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY);
  }

  @SuppressWarnings("unused")
  private void dummyValidMethod(@TestProject final Path path) {
    // Checkstyle empty block protection for parameter resolution testing
  }

  @SuppressWarnings("unused")
  private void dummyInvalidTypeMethod(@TestProject final String notAPath) {
    // Checkstyle empty block protection for parameter resolution testing
  }

  @SuppressWarnings("unused")
  private void dummyUnannotatedMethod(final Path path) {
    // Checkstyle empty block protection for parameter resolution testing
  }

  @Test
  @DisplayName("Should support @TestProject annotation with Path parameter")
  void testSupportsParameterValid() throws Exception {
    final ParameterContext paramContext = this.createParameterContext("dummyValidMethod",
        Path.class);
    assertTrue(this.manager.supportsParameter(paramContext, this.mockContext));
  }

  @Test
  @DisplayName("Should throw exception when @TestProject is used on a non Path parameter")
  void testSupportsParameterInvalidType() throws Exception {
    final ParameterContext paramContext = this.createParameterContext("dummyInvalidTypeMethod",
        String.class);
    assertThrows(ParameterResolutionException.class, () -> {
      this.manager.supportsParameter(paramContext, this.mockContext);
    });
  }

  @Test
  @DisplayName("Should reject parameter without @TestProject annotation")
  void testSupportsParameterNoAnnotation() throws Exception {
    final ParameterContext paramContext = this.createParameterContext("dummyUnannotatedMethod",
        Path.class);
    assertFalse(this.manager.supportsParameter(paramContext, this.mockContext));
  }

  @Test
  @DisplayName("Should resolve parameter and create project directory for default variant")
  void testResolveParameterDefaultVariant() throws Exception {
    final ParameterContext paramContext = this.createParameterContext("dummyValidMethod",
        Path.class);
    final Object resolved = this.manager.resolveParameter(paramContext, this.mockContext);

    assertNotNull(resolved);
    assertTrue(resolved instanceof Path);
    assertTrue(Files.exists((Path) resolved));
  }

  @Test
  @DisplayName("Should sanitize invalid characters in variant name and create directory")
  void testGetProjectWithVariantAndInvalidCharacters() {
    final String variantWithInvalidChars = "test/variant:*?\"<>|";
    final Path projectPath = this.manager.getProject(variantWithInvalidChars, this.mockContext);

    assertNotNull(projectPath);
    assertTrue(Files.exists(projectPath));
    assertTrue(projectPath.toString().contains("-"));
    assertFalse(projectPath.toString().contains("*"));
  }

  @Test
  @DisplayName("Should handle directory collision by appending a counter")
  void testCreateUniqueDirectoryCollisionHandling() throws Exception {
    final Method method = TestProjectManager.class
        .getDeclaredMethod("createUniqueDirectory", Path.class);
    method.setAccessible(true);

    final Path targetPath = this.tempDir.resolve("collisionTest");
    final Path firstProject = (Path) method.invoke(null, targetPath);
    final Path secondProject = (Path) method.invoke(null, targetPath);

    assertTrue(Files.exists(firstProject));
    assertTrue(Files.exists(secondProject));
    assertNotEquals(firstProject, secondProject);
    assertTrue(secondProject.getFileName().toString().contains(" 2"));
  }

  @Test
  @DisplayName("Should set observedFailure flag when test execution fails")
  void testAfterEachWithExecutionExceptionSetsObservedFailure() throws Exception {
    when(this.mockContext.getExecutionException())
        .thenReturn(Optional.of(new RuntimeException("Test Failed")));

    this.manager.afterEach(this.mockContext);

    final Boolean observedFailure = (Boolean) this.storeMap.get("observedFailure");
    assertNotNull(observedFailure);
    assertTrue(observedFailure);
  }

  @Test
  @DisplayName("Should delete project directory on close when RetainMode is NEVER")
  void testProjectGuardRetainModeNeverDeletesDirectory() throws Exception {
    System.setProperty(TestProjectManager.RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY, "never");

    final Path projectPath = this.manager.getProject("", this.mockContext);
    assertTrue(Files.exists(projectPath));

    for (final Object value : this.storeMap.values()) {
      if (value instanceof AutoCloseable
          && value.getClass().getSimpleName().contains("ProjectGuard")) {
        ((AutoCloseable) value).close();
      }
    }

    assertFalse(Files.exists(projectPath));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when an invalid RetainMode is set")
  void testInvalidRetainModeThrowsException() {
    System.setProperty(TestProjectManager.RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY,
        "invalid_mode_value");

    this.manager.getProject("", this.mockContext);

    AutoCloseable targetGuard = null;
    for (final Object value : this.storeMap.values()) {
      if (value instanceof AutoCloseable
          && value.getClass().getSimpleName().contains("ProjectGuard")) {
        targetGuard = (AutoCloseable) value;
        break;
      }
    }

    assertNotNull(targetGuard);

    final AutoCloseable finalGuard = targetGuard;

    assertThrows(IllegalArgumentException.class, finalGuard::close);
  }

  @Test
  @DisplayName("Should delete workspace directory when WorkspaceGuard closes")
  void testWorkspaceGuardClosesAndDeletesWorkspace() throws Exception {
    this.manager.getProject("", this.mockContext);

    for (final Object value : this.storeMap.values()) {
      if (value instanceof AutoCloseable
          && value.getClass().getSimpleName().contains("WorkspaceGuard")) {
        ((AutoCloseable) value).close();
      }
    }

    final Path vitruvDir = this.tempDir.resolve("Vitruv");
    assertFalse(Files.exists(vitruvDir));
  }

  private ParameterContext createParameterContext(
      final String methodName, final Class<?> paramType) throws NoSuchMethodException {
    final Method method = this.getClass().getDeclaredMethod(methodName, paramType);
    final Parameter parameter = method.getParameters()[0];

    final ParameterContext paramContext = mock(ParameterContext.class);
    when(paramContext.getParameter()).thenReturn(parameter);
    return paramContext;
  }
}