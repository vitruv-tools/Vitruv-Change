package tools.vitruv.change.testutils

import edu.kit.ipd.sdq.activextendannotations.Lazy
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.PatternLayout
import org.junit.jupiter.api.^extension.BeforeAllCallback
import org.junit.jupiter.api.^extension.ExtensionContext

import static org.apache.log4j.Level.*
import static org.apache.log4j.Logger.getRootLogger

import static extension org.apache.log4j.Logger.getLogger
import java.util.List

/** 
 * Initializes console logger for tests. Sets the logger level to {@code WARN} by default. If the VM property
 * {@link VM_ARGUMENT_LOG_LEVEL} is specified, it is used to determine the logger level.
 */
class TestLogging implements BeforeAllCallback {
	@Lazy static val String desiredLogLevel = System.getProperty(VM_ARGUMENT_LOG_LEVEL) ?: "WARN"
	/**
	 * The default root log level. Defaults to {@link Level#WARN}. 
	 */
	public static val VM_ARGUMENT_LOG_LEVEL = "vitruv.logLevel"
	static val LOG_PATTERN = "%d{HH:mm:ss.SSS} [%35.35c{1}] %5p: %m%n"
	static val VITRUV_LOG_ROOTS = List.of("tools.vitruv", "mir.reactions", "mir.routines")

	override beforeAll(ExtensionContext context) throws Exception {
		configureLog4J()
	}
	
	def private static configureLog4J() {
		rootLogger.removeAllAppenders()
		rootLogger.addAppender(new ConsoleAppender(new PatternLayout(LOG_PATTERN)))
		rootLogger.level = ERROR
		VITRUV_LOG_ROOTS.forEach [logger.level = toLevel(desiredLogLevel, WARN)]
		TestProjectManager.logger.level = INFO
	}
}
