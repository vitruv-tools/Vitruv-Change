package tools.vitruv.change.testutils.printing

import tools.vitruv.change.testutils.printing.ModelPrinter
import java.lang.annotation.Retention
import java.lang.annotation.Target
import java.lang.annotation.Inherited
import tools.vitruv.change.testutils.printing.DefaultModelPrinter
import tools.vitruv.change.testutils.printing.PrintResult

/**
 * Annotation that allows changing which {@linkplain ModelPrinter ModelPrinters} will be used by {@link ModelPrinting}.
 * When printing, the specified printers will be tried in order until one does not return 
 * {@link PrintResult#NOT_RESPONSIBLE}. The last printer will always be {@link DefaultModelPrinter}.
 * <p>
 * All referenced printer classes must have a zero-arg constructor. This constructor will be used to instantiate
 * them.
 * <p>
 * This annotation is inherited, setting it on an extending class overrides the settings of the extended class.
 */
@Retention(RUNTIME)
@Target(TYPE, METHOD)
@Inherited
annotation UseModelPrinter {
	val Class<? extends ModelPrinter>[] value
}
