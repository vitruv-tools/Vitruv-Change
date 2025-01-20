package tools.vitruv.testutils;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import tools.vitruv.change.propagation.ProjectMarker;

/**
 * Extension managing the test projects for Eclipse tests. Test classes using this extension can have test project
 * folders injected by using the @{@link TestProject} annotation. Test projects will be cleaned if their corresponding
 * test succeed, but retained if their corresponding test failed. To modify this behaviour, see
 * {@link #RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY}.
 */
@SuppressWarnings("all")
public class TestProjectManager implements ParameterResolver, AfterEachCallback {
  private enum RetainMode {
    ALWAYS,

    ON_FAILURE,

    NEVER;
  }

  @FinalFieldsConstructor
  private static class WorkspaceGuard implements ExtensionContext.Store.CloseableResource {
    private final Path workspace;

    @Override
    public void close() throws Throwable {
      Files.walkFileTree(this.workspace, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException error) {
          try {
            FileVisitResult _postVisitDirectory = super.postVisitDirectory(dir, error);
            final Procedure1<FileVisitResult> _function = (FileVisitResult it) -> {
              try {
                try {
                  Files.delete(dir);
                } catch (final Throwable _t) {
                  if (_t instanceof DirectoryNotEmptyException) {
                  } else {
                    throw Exceptions.sneakyThrow(_t);
                  }
                }
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            };
            return ObjectExtensions.<FileVisitResult>operator_doubleArrow(_postVisitDirectory, _function);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
      });
    }

    public WorkspaceGuard(final Path workspace) {
      super();
      this.workspace = workspace;
    }
  }

  @FinalFieldsConstructor
  private static class ProjectGuard implements ExtensionContext.Store.CloseableResource {
    private final Path projectDir;

    private final ExtensionContext context;

    @Override
    public void close() throws Throwable {
      TestProjectManager.RetainMode _retainMode = TestProjectManager.getRetainMode();
      final TestProjectManager.RetainMode retain = _retainMode;
      boolean _matched = false;
      if (Objects.equal(retain, TestProjectManager.RetainMode.NEVER)) {
        _matched=true;
      }
      if (!_matched) {
        if ((Objects.equal(retain, TestProjectManager.RetainMode.ON_FAILURE) && (!(TestProjectManager.getObservedFailure(this.context)).booleanValue()))) {
          _matched=true;
        }
      }
      if (_matched) {
        final Consumer<Path> _function = (Path it) -> {
          try {
            Files.delete(it);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        };
        TestProjectManager.walkIfExists(this.projectDir).sorted(Comparator.<Path>reverseOrder()).forEach(_function);
      }
      if (!_matched) {
      }
    }

    public ProjectGuard(final Path projectDir, final ExtensionContext context) {
      super();
      this.projectDir = projectDir;
      this.context = context;
    }
  }

  /**
   * Set this system property to “{@code always}”, “{@code on_failure}” or “{@code never}” to retain
   * the test projects always, only when the corresponding test failed, or never. The default is “{@code on_failure}”.
   */
  public static final String RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY = "vitruv.retainTestProjects";

  /**
   * Set this system property to overwrite the workspace path
   */
  public static final String WORKSPACE_PATH_SYSTEM_PROPERTY = "vitruv.workspace";

  private static final Logger log = Logger.getLogger(TestProjectManager.class);

  private static final ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(TestProjectManager.class);

  private static final String observedFailure = "observedFailure";

  private static final ExtensionContext.Namespace projectNamespace = ExtensionContext.Namespace.create(TestProjectManager.class, "projects");

  private static final Pattern invalidFileCharacters = Pattern.compile("[/\\\\<>:\"|?* ]");

  private static Path workspaceCache;

  @Override
  public void afterEach(final ExtensionContext context) throws Exception {
    boolean _isPresent = context.getExecutionException().isPresent();
    if (_isPresent) {
      TestProjectManager.setObservedFailure(context, true);
    }
  }

  private static Boolean getObservedFailure(final ExtensionContext context) {
    return context.getStore(TestProjectManager.namespace).<Boolean>getOrDefault(TestProjectManager.observedFailure, Boolean.class, Boolean.valueOf(false));
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
    Path _xblockexpression = null;
    {
      if ((TestProjectManager.workspaceCache != null)) {
        return TestProjectManager.workspaceCache;
      }
      Path _elvis = null;
      String _property = System.getProperty(TestProjectManager.WORKSPACE_PATH_SYSTEM_PROPERTY);
      Path _path = null;
      if (_property!=null) {
        _path=TestProjectManager.toPath(_property);
      }
      if (_path != null) {
        _elvis = _path;
      } else {
        Path _xifexpression = null;
        boolean _isRunning = Platform.isRunning();
        if (_isRunning) {
          _xifexpression = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().toPath();
        } else {
          _xifexpression = TestProjectManager.toPath(System.getProperty("java.io.tmpdir"));
        }
        _elvis = _xifexpression;
      }
      final Path testWorkspace = _elvis;
      final Path targetDir = testWorkspace.resolve("Vitruv");
      try {
        final Consumer<Path> _function = (Path it) -> {
          try {
            Files.delete(it);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        };
        TestProjectManager.walkIfExists(targetDir).sorted(Comparator.<Path>reverseOrder()).forEach(_function);
      } catch (final Throwable _t) {
        if (_t instanceof IOException) {
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      TestProjectManager.workspaceCache = TestProjectManager.createUniqueDirectory(targetDir);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Running in the test workspace at ");
      _builder.append(TestProjectManager.workspaceCache);
      TestProjectManager.log.info(_builder);
      _xblockexpression = TestProjectManager.workspaceCache;
    }
    return _xblockexpression;
  }

  private static Path toPath(final String string) {
    return Path.of(string);
  }

  private Path getWorkspace(final ExtensionContext context) {
    final Function<String, TestProjectManager.WorkspaceGuard> _function = (String it) -> {
      Path _setupWorkspace = this.setupWorkspace();
      return new TestProjectManager.WorkspaceGuard(_setupWorkspace);
    };
    return context.getRoot().getStore(TestProjectManager.namespace).<String, TestProjectManager.WorkspaceGuard>getOrComputeIfAbsent("workspace", _function, TestProjectManager.WorkspaceGuard.class).workspace;
  }

  public Path getProject(final String variant, final ExtensionContext context) {
    Path _xblockexpression = null;
    {
      final Path workspace = this.getWorkspace(context);
      Path _xifexpression = null;
      boolean _equals = Objects.equal(variant, "");
      if (_equals) {
        _xifexpression = this.getProjectRelativeBasePath(context);
      } else {
        Path _relativize = workspace.relativize(this.getProject("", context));
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("[");
        String _removeInvalidCharacters = TestProjectManager.removeInvalidCharacters(variant);
        _builder.append(_removeInvalidCharacters);
        _builder.append("]");
        _xifexpression = _relativize.resolve(_builder.toString());
      }
      final Path projectPath = _xifexpression;
      final Function<Path, TestProjectManager.ProjectGuard> _function = (Path it) -> {
        TestProjectManager.ProjectGuard _xblockexpression_1 = null;
        {
          Path _createUniqueDirectory = TestProjectManager.createUniqueDirectory(workspace.resolve(projectPath));
          final Procedure1<Path> _function_1 = (Path it_1) -> {
            try {
              ProjectMarker.markAsProjectRootFolder(it_1);
            } catch (Throwable _e) {
              throw Exceptions.sneakyThrow(_e);
            }
          };
          final Path projectDir = ObjectExtensions.<Path>operator_doubleArrow(_createUniqueDirectory, _function_1);
          _xblockexpression_1 = new TestProjectManager.ProjectGuard(projectDir, context);
        }
        return _xblockexpression_1;
      };
      _xblockexpression = context.getStore(TestProjectManager.projectNamespace).<Path, TestProjectManager.ProjectGuard>getOrComputeIfAbsent(projectPath, _function, TestProjectManager.ProjectGuard.class).projectDir;
    }
    return _xblockexpression;
  }

  private Path getProjectRelativeBasePath(final ExtensionContext context) {
    final Function1<ExtensionContext, Boolean> _function = (ExtensionContext it) -> {
      ExtensionContext _root = it.getRoot();
      return Boolean.valueOf(Objects.equal(it, _root));
    };
    final Function1<ExtensionContext, String> _function_1 = (ExtensionContext it) -> {
      return TestProjectManager.removeInvalidCharacters(it.getDisplayName());
    };
    final Function2<Path, String, Path> _function_2 = (Path $0, String $1) -> {
      return $0.resolve($1);
    };
    return IterableExtensions.<String, Path>fold(IterableExtensions.<ExtensionContext, String>map(IterableExtensions.<ExtensionContext>reject(ListExtensions.<ExtensionContext>reverseView(TestProjectManager.getParentChain(context)), _function), _function_1), Path.of(""), _function_2);
  }

  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
    Path _xblockexpression = null;
    {
      final TestProject projectAnnotation = Preconditions.<TestProject>checkNotNull(parameterContext.getParameter().<TestProject>getAnnotation(TestProject.class));
      _xblockexpression = this.getProject(projectAnnotation.variant(), extensionContext);
    }
    return _xblockexpression;
  }

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
    TestProject _annotation = parameterContext.getParameter().<TestProject>getAnnotation(TestProject.class);
    boolean _tripleNotEquals = (_annotation != null);
    if (_tripleNotEquals) {
      Class<?> _type = parameterContext.getParameter().getType();
      boolean _notEquals = (!Objects.equal(_type, Path.class));
      if (_notEquals) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("The parameter ");
        String _name = parameterContext.getParameter().getName();
        _builder.append(_name);
        _builder.append(" is annotated with @");
        String _simpleName = TestProject.class.getSimpleName();
        _builder.append(_simpleName);
        _builder.append(", but its type is not ");
        String _simpleName_1 = Path.class.getSimpleName();
        _builder.append(_simpleName_1);
        _builder.append("!");
        throw new ParameterResolutionException(_builder.toString());
      }
      return true;
    }
    return false;
  }

  private static Path createUniqueDirectory(final Path projectPath) {
    try {
      Path uniqueProject = projectPath;
      Files.createDirectories(projectPath.getParent());
      {
        int counter = 2;
        boolean created = false;
        boolean _while = (!created);
        while (_while) {
          {
            try {
              Files.createDirectory(uniqueProject);
              created = true;
            } catch (final Throwable _t) {
              if (_t instanceof FileAlreadyExistsException) {
                StringConcatenation _builder = new StringConcatenation();
                Path _fileName = projectPath.getFileName();
                _builder.append(_fileName);
                _builder.append(" ");
                _builder.append(counter);
                uniqueProject = projectPath.resolveSibling(_builder.toString());
              } else {
                throw Exceptions.sneakyThrow(_t);
              }
            }
            StringConcatenation _builder = new StringConcatenation();
            _builder.append("Failed to create a unique version of ");
            _builder.append(projectPath);
            _builder.append(" with 1000 tries!");
            Preconditions.checkState((counter < 1000), _builder);
          }
          counter++;
          _while = (!created);
        }
      }
      return uniqueProject;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  private static String removeInvalidCharacters(final String fileName) {
    return TestProjectManager.invalidFileCharacters.matcher(fileName).replaceAll("-");
  }

  private static TestProjectManager.RetainMode getRetainMode() {
    String _property = System.getProperty(TestProjectManager.RETAIN_TEST_PROJECTS_SYSTEM_PROPERTY);
    String _lowerCase = null;
    if (_property!=null) {
      _lowerCase=_property.toLowerCase();
    }
    final String propertyValue = _lowerCase;
    if ((propertyValue == null)) {
      return TestProjectManager.RetainMode.ON_FAILURE;
    }
    final Function1<TestProjectManager.RetainMode, Boolean> _function = (TestProjectManager.RetainMode it) -> {
      String _lowerCase_1 = it.name().toLowerCase();
      return Boolean.valueOf(Objects.equal(_lowerCase_1, propertyValue));
    };
    final TestProjectManager.RetainMode mode = IterableExtensions.<TestProjectManager.RetainMode>findFirst(((Iterable<TestProjectManager.RetainMode>)Conversions.doWrapArray(TestProjectManager.RetainMode.values())), _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Unknown test project retain mode \'");
    _builder.append(propertyValue);
    _builder.append("!");
    Preconditions.checkArgument((mode != null), _builder);
    return mode;
  }

  private static Stream<Path> walkIfExists(final Path path) {
    try {
      Stream<Path> _xtrycatchfinallyexpression = null;
      try {
        _xtrycatchfinallyexpression = Files.walk(path);
      } catch (final Throwable _t) {
        if (_t instanceof NoSuchFileException) {
          _xtrycatchfinallyexpression = Stream.<Path>empty();
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      return _xtrycatchfinallyexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
