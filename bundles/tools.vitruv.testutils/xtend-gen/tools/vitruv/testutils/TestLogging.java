package tools.vitruv.testutils;

import edu.kit.ipd.sdq.activextendannotations.Lazy;
import java.util.List;
import java.util.function.Consumer;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Initializes console logger for tests. Sets the logger level to {@code WARN} by default. If the VM property
 * {@link VM_ARGUMENT_LOG_LEVEL} is specified, it is used to determine the logger level.
 */
@SuppressWarnings("all")
public class TestLogging implements BeforeAllCallback {
  @Lazy
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
    Logger.getRootLogger().removeAllAppenders();
    Logger _rootLogger = Logger.getRootLogger();
    PatternLayout _patternLayout = new PatternLayout(TestLogging.LOG_PATTERN);
    ConsoleAppender _consoleAppender = new ConsoleAppender(_patternLayout);
    _rootLogger.addAppender(_consoleAppender);
    Logger _rootLogger_1 = Logger.getRootLogger();
    _rootLogger_1.setLevel(Level.ERROR);
    final Consumer<String> _function = (String it) -> {
      Logger _logger = Logger.getLogger(it);
      _logger.setLevel(Level.toLevel(TestLogging.getDesiredLogLevel(), Level.WARN));
    };
    TestLogging.VITRUV_LOG_ROOTS.forEach(_function);
    Logger _logger = Logger.getLogger(TestProjectManager.class);
    _logger.setLevel(Level.INFO);
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
