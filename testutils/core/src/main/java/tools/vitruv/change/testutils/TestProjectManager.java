package tools.vitruv.change.testutils;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import tools.vitruv.change.utils.ProjectMarker;

/**
 * Extension managing the test projects for Eclipse tests. Test classes using
 * this extension can have test project
 * folders injected by using the @{@link TestProject} annotation. Test projects
 * will be cleaned if their corresponding
 * test succeed, but retained if their corresponding test failed. To modify this
 * behaviour, see
 * {@link #RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY}.
 */
public class TestProjectManager implements ParameterResolver, AfterEachCallback {
  private enum RetainMode {
    ALWAYS,

    ON_FAILURE,

    NEVER;
  }

  private static class WorkspaceGuard implements AutoCloseable {
    private final Path workspace;

    @Override
    public void close() throws IOException {
      Files.walkFileTree(this.workspace, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException error)
            throws IOException {
          FileVisitResult result = super.postVisitDirectory(dir, error);
          try {
            Files.delete(dir);
          } catch (DirectoryNotEmptyException retained) {
            // Keep directories that still contain retained test artifacts.
          }
          return result;
        }
      });
    }

    public WorkspaceGuard(final Path workspace) {
      super();
      this.workspace = workspace;
    }
  }

  private static class ProjectGuard implements AutoCloseable {
    private final Path projectDir;

    private final ExtensionContext context;

    @Override
    public void close() {
      TestProjectManager.RetainMode retain = TestProjectManager.getRetainMode();
      boolean shouldDelete = Objects.equals(retain, TestProjectManager.RetainMode.NEVER)
          || (Objects.equals(retain, TestProjectManager.RetainMode.ON_FAILURE)
              && !TestProjectManager.getObservedFailure(this.context).booleanValue());
      if (shouldDelete) {
        TestProjectManager.deleteRecursively(this.projectDir);
      }
    }

    public ProjectGuard(final Path projectDir, final ExtensionContext context) {
      super();
      this.projectDir = projectDir;
      this.context = context;
    }
  }

  /**
   * Set this system property to “{@code always}”, “{@code on_failure}” or
   * “{@code never}” to retain
   * the test projects always, only when the corresponding test failed, or never.
   * The default is “{@code on_failure}”.
   */
  public static final String RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY = "vitruv.retainTestProjects";

  /**
   * Set this system property to overwrite the workspace path
   */
  public static final String WORKSPACE_PATH_SYSTEM_PROPERTY = "vitruv.workspace";

  private static final Logger log = LogManager.getLogger(TestProjectManager.class);

  private static final ExtensionContext.Namespace namespace = ExtensionContext.Namespace
      .create(TestProjectManager.class);

  private static final String observedFailure = "observedFailure";

  private static final ExtensionContext.Namespace projectNamespace = ExtensionContext.Namespace
      .create(TestProjectManager.class, "projects");

  private static final Pattern invalidFileCharacters = Pattern.compile("[/\\\\<>:\"|?*\u0000]");

  private static Path workspaceCache;

  @Override
  public void afterEach(final ExtensionContext context) throws Exception {
    boolean _isPresent = context.getExecutionException().isPresent();
    if (_isPresent) {
      TestProjectManager.setObservedFailure(context, true);
    }
  }

  private static Boolean getObservedFailure(final ExtensionContext context) {
    return context.getStore(TestProjectManager.namespace).<Boolean>getOrDefault(TestProjectManager.observedFailure,
        Boolean.class, Boolean.valueOf(false));
  }

  private static void setObservedFailure(final ExtensionContext context, final boolean value) {
    final Consumer<ExtensionContext> _function = (ExtensionContext it) -> {
      it.getStore(TestProjectManager.namespace).put(TestProjectManager.observedFailure, Boolean.valueOf(true));
    };
    TestProjectManager.getParentChain(context).forEach(_function);
  }

  private static ArrayList<ExtensionContext> getParentChain(final ExtensionContext context) {
    ArrayList<ExtensionContext> result = new ArrayList<ExtensionContext>();
    for (ExtensionContext current = context; (current != null); current = current.getParent().orElse(null)) {
      result.add(current);
    }
    return result;
  }

  private Path setupWorkspace() {
    if (TestProjectManager.workspaceCache != null) {
      return TestProjectManager.workspaceCache;
    }
    final Path targetDir = resolveWorkspaceRoot().resolve("Vitruv");
    TestProjectManager.deleteRecursively(targetDir);
    TestProjectManager.workspaceCache = TestProjectManager.createUniqueDirectory(targetDir);
    TestProjectManager.log.info(
        "Running in the test workspace at " + TestProjectManager.workspaceCache);
    return TestProjectManager.workspaceCache;
  }

  private static Path resolveWorkspaceRoot() {
    String configuredPath = System.getProperty(TestProjectManager.WORKSPACE_PATH_SYSTEM_PROPERTY);
    if (configuredPath != null) {
      return TestProjectManager.toPath(configuredPath);
    }
    if (Platform.isRunning()) {
      return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().toPath();
    }
    return TestProjectManager.toPath(System.getProperty("java.io.tmpdir"));
  }

  /** Deletes the file tree rooted at {@code path} bottom-up, doing nothing if it is absent. */
  private static void deleteRecursively(final Path path) {
    TestProjectManager.walkIfExists(path).sorted(Comparator.<Path>reverseOrder())
        .forEach(TestProjectManager::delete);
  }

  private static void delete(final Path path) {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Path toPath(final String string) {
    return Path.of(string);
  }

  @SuppressWarnings("deprecation")
  private Path getWorkspace(final ExtensionContext context) {
    final Function<String, TestProjectManager.WorkspaceGuard> _function = (String it) -> {
      Path _setupWorkspace = this.setupWorkspace();
      return new TestProjectManager.WorkspaceGuard(_setupWorkspace);
    };
    ExtensionContext.Store store = context.getRoot().getStore(TestProjectManager.namespace);
    TestProjectManager.WorkspaceGuard guard = (TestProjectManager.WorkspaceGuard) store.getOrComputeIfAbsent(
        "workspace", _function, TestProjectManager.WorkspaceGuard.class);
    return guard.workspace;
  }

  @SuppressWarnings("deprecation")
  public Path getProject(final String variant, final ExtensionContext context) {
    Path _xblockexpression = null;
    {
      final Path workspace = this.getWorkspace(context);
      Path _xifexpression = null;
      boolean _equals = Objects.equals(variant, "");
      if (_equals) {
        _xifexpression = this.getProjectRelativeBasePath(context);
      } else {
        Path _relativize = workspace.relativize(this.getProject("", context));
        _xifexpression = _relativize.resolve("[" + TestProjectManager.removeInvalidCharacters(variant) + "]");
      }
      final Path projectPath = _xifexpression;
      final Function<Path, TestProjectManager.ProjectGuard> _function = (Path it) -> {
        TestProjectManager.ProjectGuard _xblockexpression_1 = null;
        {
          Path _createUniqueDirectory = TestProjectManager.createUniqueDirectory(workspace.resolve(projectPath));
          final Consumer<Path> _function_1 = (Path it_1) -> {
            try {
              ProjectMarker.markAsProjectRootFolder(it_1);
            } catch (Throwable _e) {
              if (_e instanceof RuntimeException)
                throw (RuntimeException) _e;
              if (_e instanceof Error)
                throw (Error) _e;
              throw new RuntimeException(_e);
            }
          };
          _function_1.accept(_createUniqueDirectory);
          final Path projectDir = _createUniqueDirectory;
          _xblockexpression_1 = new TestProjectManager.ProjectGuard(projectDir, context);
        }
        return _xblockexpression_1;
      };
      _xblockexpression = ((TestProjectManager.ProjectGuard) context
          .getStore(TestProjectManager.projectNamespace).getOrComputeIfAbsent(
              projectPath, _function, TestProjectManager.ProjectGuard.class)).projectDir;
    }
    return _xblockexpression;
  }

  private Path getProjectRelativeBasePath(final ExtensionContext context) {
    final Function<ExtensionContext, Boolean> _function = (ExtensionContext it) -> {
      ExtensionContext _root = it.getRoot();
      return Boolean.valueOf(Objects.equals(it, _root));
    };
    final Function<ExtensionContext, String> _function_1 = (ExtensionContext it) -> {
      return TestProjectManager.removeInvalidCharacters(it.getDisplayName());
    };
    final BiFunction<Path, String, Path> _function_2 = (Path $0, String $1) -> {
      return $0.resolve($1);
    };
    java.util.List<ExtensionContext> chain = TestProjectManager.getParentChain(context);
    java.util.Collections.reverse(chain);
    Path result = Path.of("");
    for (ExtensionContext it : chain) {
      if (!_function.apply(it)) {
        result = _function_2.apply(result, _function_1.apply(it));
      }
    }
    return result;
  }

  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
    Path _xblockexpression = null;
    {
      final TestProject projectAnnotation = Preconditions
          .<TestProject>checkNotNull(parameterContext.getParameter().<TestProject>getAnnotation(TestProject.class));
      _xblockexpression = this.getProject(projectAnnotation.variant(), extensionContext);
    }
    return _xblockexpression;
  }

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    TestProject _annotation = parameterContext.getParameter().<TestProject>getAnnotation(TestProject.class);
    boolean _tripleNotEquals = (_annotation != null);
    if (_tripleNotEquals) {
      Class<?> _type = parameterContext.getParameter().getType();
      boolean _notEquals = (!Objects.equals(_type, Path.class));
      if (_notEquals) {
        throw new ParameterResolutionException("The parameter " + parameterContext.getParameter().getName()
            + " is annotated with @" + TestProject.class.getSimpleName()
            + ", but its type is not " + Path.class.getSimpleName() + "!");
      }
      return true;
    }
    return false;
  }

  private static Path createUniqueDirectory(final Path projectPath) {
    try {
      Files.createDirectories(projectPath.getParent());
      Path uniqueProject = projectPath;
      int counter = 2;

      while (true) {
        try {
          Files.createDirectory(uniqueProject);
          return uniqueProject;
        } catch (final FileAlreadyExistsException ignored) {
          uniqueProject = projectPath
              .resolveSibling(projectPath.getFileName() + " " + counter);
          Preconditions.checkState(counter < 1000,
              "Failed to create a unique version of " + projectPath + " with 1000 tries!");
          counter++;
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String removeInvalidCharacters(final String fileName) {
    return TestProjectManager.invalidFileCharacters.matcher(fileName).replaceAll("-");
  }

  private static TestProjectManager.RetainMode getRetainMode() {
    String _property = System.getProperty(TestProjectManager.RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY);
    String _lowerCase = null;
    if (_property != null) {
      _lowerCase = _property.toLowerCase();
    }
    final String propertyValue = _lowerCase;
    if ((propertyValue == null)) {
      return TestProjectManager.RetainMode.ON_FAILURE;
    }
    final TestProjectManager.RetainMode mode = java.util.Arrays.stream(TestProjectManager.RetainMode.values())
        .filter(it -> Objects.equals(it.name().toLowerCase(), propertyValue)).findFirst().orElse(null);
    Preconditions.checkArgument((mode != null), "Unknown test project retain mode '" + propertyValue + "!");
    return mode;
  }

  private static Stream<Path> walkIfExists(final Path path) {
    try {
      return Files.walk(path);
    } catch (NoSuchFileException _t) {
      return Stream.<Path>empty();
    } catch (IOException _e) {
      throw new RuntimeException(_e);
    }
  }
}
