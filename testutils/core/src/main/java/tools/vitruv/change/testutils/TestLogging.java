package tools.vitruv.change.testutils;

import java.util.List;
import java.util.function.Consumer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Initializes console logger for tests. Sets the logger level to {@code WARN}
 * by default. If the VM property
 * {@link VM_ARGUMENT_LOG_LEVEL} is specified, it is used to determine the
 * logger level.
 */
@SuppressWarnings("all")
public class TestLogging implements BeforeAllCallback {
  private static String _desiredLogLevel;

  /**
   * The default root log level. Defaults to {@link Level#WARN}.
   */
  public static final String VM_ARGUMENT_LOG_LEVEL = "vitruv.logLevel";

  private static final String LOG_PATTERN = "%d{HH:mm:ss.SSS} [%35.35c{1}] %5p: %m%n";

  private static final List<String> VITRUV_LOG_ROOTS = List.<String>of("tools.vitruv", "mir.reactions", "mir.routines");

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {
    TestLogging.configureLog4J();
  }

  private static void configureLog4J() {
    final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
    builder.setStatusLevel(Level.ERROR);
    builder.setConfigurationName("RollingBuilder");
    AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target",
        ConsoleAppender.Target.SYSTEM_OUT);
    appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", TestLogging.LOG_PATTERN));
    builder.add(appenderBuilder);
    final BuiltConfiguration config = builder.build();
    config.initialize();
    final Consumer<String> _function = (String loggerName) -> {
      Logger _logger = LogManager.getLogger(loggerName);
      final org.apache.logging.log4j.core.Logger logger = ((org.apache.logging.log4j.core.Logger) _logger);
      logger.setLevel(Level.toLevel(TestLogging.getDesiredLogLevel(), Level.WARN));
    };
    TestLogging.VITRUV_LOG_ROOTS.forEach(_function);
    Logger _logger = LogManager.getLogger(TestProjectManager.class);
    final org.apache.logging.log4j.core.Logger testLogger = ((org.apache.logging.log4j.core.Logger) _logger);
    testLogger.setLevel(Level.INFO);
  }

  private static boolean _desiredLogLevel_isInitialised = false;

  private static String _desiredLogLevel_initialise() {
    String _elvis = null;
    String _property = System.getProperty(TestLogging.VM_ARGUMENT_LOG_LEVEL);
    if (_property != null) {
      _elvis = _property;
    } else {
      _elvis = "WARN";
    }
    return _elvis;
  }

  public static String getDesiredLogLevel() {
    if (!_desiredLogLevel_isInitialised) {
      try {
        _desiredLogLevel = _desiredLogLevel_initialise();
      } finally {
        _desiredLogLevel_isInitialised = true;
      }
    }
    return _desiredLogLevel;
  }
}
