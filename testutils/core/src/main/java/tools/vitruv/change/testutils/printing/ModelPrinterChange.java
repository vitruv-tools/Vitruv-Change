package tools.vitruv.change.testutils.printing;

import com.google.common.base.Preconditions;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * JUnit extension that enables {@link UseModelPrinter}.
 */
public class ModelPrinterChange implements BeforeAllCallback, BeforeEachCallback {
  private static final ExtensionContext.Namespace namespace = ExtensionContext.Namespace
      .create(ModelPrinterChange.class);

  @Override
  public void beforeAll(final ExtensionContext context) {
    final Consumer<Class<?>> _function = (Class<?> testClass) -> {
      this.installPrintersFrom(testClass, context);
    };
    context.getTestClass().ifPresent(_function);
  }

  @Override
  public void beforeEach(final ExtensionContext context) {
    final Consumer<Method> _function = (Method testMethod) -> {
      this.installPrintersFrom(testMethod, context);
    };
    context.getTestMethod().ifPresent(_function);
  }

  private void installPrintersFrom(final AnnotatedElement settingsSource, final ExtensionContext context) {
    final Optional<UseModelPrinter> annotation = AnnotationSupport.<UseModelPrinter>findAnnotation(settingsSource,
        UseModelPrinter.class);
    boolean _isPresent = annotation.isPresent();
    boolean _not = (!_isPresent);
    if (_not) {
      return;
    }
    final Function<Class<? extends ModelPrinter>, ModelPrinter> _function = (
        Class<? extends ModelPrinter> printerClass) -> {
      try {
        ModelPrinter _xblockexpression = null;
        {
          final Constructor<?> constructor = Arrays.stream(printerClass.getConstructors())
              .filter(it -> it.getParameterCount() == 0).findFirst().orElse(null);
          Preconditions.checkArgument((constructor != null), printerClass + " has no zero-arg constructor!");
          ModelPrinter _xtrycatchfinallyexpression = null;
          try {
            Object _newInstance = constructor.newInstance();
            _xtrycatchfinallyexpression = ((ModelPrinter) _newInstance);
          } catch (final Throwable _t) {
            if (_t instanceof RuntimeException) {
              final RuntimeException e = (RuntimeException) _t;
              throw new IllegalStateException("Failed to create an instance of " + printerClass + "!", e);
            } else if (_t instanceof Error) {
              throw (Error) _t;
            } else {
              throw new RuntimeException(_t);
            }
          }
          _xblockexpression = _xtrycatchfinallyexpression;
        }
        return _xblockexpression;
      } catch (Throwable _e) {
        if (_e instanceof RuntimeException)
          throw (RuntimeException) _e;
        if (_e instanceof Error)
          throw (Error) _e;
        throw new RuntimeException(_e);
      }
    };
    final List<ModelPrinter> printers = Arrays.stream(annotation.get().value()).map(_function)
        .collect(Collectors.toList());
    final Function<ModelPrinter, ModelPrinter> _function_1 = (ModelPrinter currentPrinter) -> {
      return new CombinedModelPrinter(printers, currentPrinter);
    };
    final AutoCloseable printerChange = ModelPrinting.use(_function_1);
    context.getStore(ModelPrinterChange.namespace).put(ModelPrinter.class,
        ModelPrinterChange.revertAtEndOfScope(printerChange));
  }

  private static AutoCloseable revertAtEndOfScope(final AutoCloseable printerChange) {
    return printerChange;
  }
}
