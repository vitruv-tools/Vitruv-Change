package tools.vitruv.testutils.printing;

import com.google.common.base.Preconditions;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * JUnit extension that enables {@link UseModelPrinter}.
 */
@SuppressWarnings("all")
public class ModelPrinterChange implements BeforeAllCallback, BeforeEachCallback {
  private static final ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(ModelPrinterChange.class);

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
    final Optional<UseModelPrinter> annotation = AnnotationSupport.<UseModelPrinter>findAnnotation(settingsSource, UseModelPrinter.class);
    boolean _isPresent = annotation.isPresent();
    boolean _not = (!_isPresent);
    if (_not) {
      return;
    }
    final Function1<Class<? extends ModelPrinter>, ModelPrinter> _function = (Class<? extends ModelPrinter> printerClass) -> {
      try {
        ModelPrinter _xblockexpression = null;
        {
          final Function1<Constructor<?>, Boolean> _function_1 = (Constructor<?> it) -> {
            int _parameterCount = it.getParameterCount();
            return Boolean.valueOf((_parameterCount == 0));
          };
          final Constructor<?> constructor = IterableExtensions.<Constructor<?>>findFirst(((Iterable<Constructor<?>>)Conversions.doWrapArray(printerClass.getConstructors())), _function_1);
          StringConcatenation _builder = new StringConcatenation();
          _builder.append(printerClass);
          _builder.append(" has no zero-arg constructor!");
          Preconditions.checkArgument((constructor != null), _builder);
          ModelPrinter _xtrycatchfinallyexpression = null;
          try {
            Object _newInstance = constructor.newInstance();
            _xtrycatchfinallyexpression = ((ModelPrinter) _newInstance);
          } catch (final Throwable _t) {
            if (_t instanceof RuntimeException) {
              final RuntimeException e = (RuntimeException)_t;
              StringConcatenation _builder_1 = new StringConcatenation();
              _builder_1.append("Failed to create an instance of ");
              _builder_1.append(printerClass);
              _builder_1.append("!");
              throw new IllegalStateException(_builder_1.toString(), e);
            } else {
              throw Exceptions.sneakyThrow(_t);
            }
          }
          _xblockexpression = _xtrycatchfinallyexpression;
        }
        return _xblockexpression;
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    final List<ModelPrinter> printers = ListExtensions.<Class<? extends ModelPrinter>, ModelPrinter>map(((List<Class<? extends ModelPrinter>>)Conversions.doWrapArray(annotation.get().value())), _function);
    final Function1<ModelPrinter, ModelPrinter> _function_1 = (ModelPrinter currentPrinter) -> {
      return new CombinedModelPrinter(printers, currentPrinter);
    };
    final AutoCloseable printerChange = ModelPrinting.use(_function_1);
    context.getStore(ModelPrinterChange.namespace).put(ModelPrinter.class, ModelPrinterChange.revertAtEndOfScope(printerChange));
  }

  private static ExtensionContext.Store.CloseableResource revertAtEndOfScope(final AutoCloseable printerChange) {
    final ExtensionContext.Store.CloseableResource _function = () -> {
      printerChange.close();
    };
    return _function;
  }
}
