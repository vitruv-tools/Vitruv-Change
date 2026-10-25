package tools.vitruv.change.testutils.printing;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.BiFunction;
import org.hamcrest.Description;

public final class ModelPrinting {
  private ModelPrinting() {
  }

  private static ModelPrinter printer = new DefaultModelPrinter();

  public static Description appendModelValue(final Description description, final Object object,
      final PrintIdProvider idProvider) {
    final Function<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final BiFunction<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget subTarget, Object theObject) -> {
        return ModelPrinting.printer.printObject(subTarget, idProvider, theObject);
      };
      return it.<Object>printValue(object, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendModelValueList(final Description description, final List<?> objects,
      final PrintMode mode, final PrintIdProvider idProvider) {
    final Function<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final BiFunction<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
        return ModelPrinting.printer.printObject(target, idProvider, element);
      };
      return it.<Object>printList(objects, mode, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendModelValueSet(final Description description, final Set<?> objects,
      final PrintMode mode, final PrintIdProvider idProvider) {
    final Function<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final BiFunction<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
        return ModelPrinting.printer.printObject(target, idProvider, element);
      };
      return it.<Object>printSet(objects, mode, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendShortenedModelValue(final Description description, final Object object,
      final PrintIdProvider idProvider) {
    final Function<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final BiFunction<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget subTarget, Object theObject) -> {
        return ModelPrinting.printer.printObjectShortened(subTarget, idProvider, theObject);
      };
      return it.<Object>printValue(object, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendShortenedModelValueList(final Description description, final List<?> objects,
      final PrintMode mode, final PrintIdProvider idProvider) {
    final Function<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final BiFunction<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
        return ModelPrinting.printer.printObjectShortened(target, idProvider, element);
      };
      return it.<Object>printList(objects, mode, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendShortenedModelValueSet(final Description description, final Set<?> objects,
      final PrintMode mode, final PrintIdProvider idProvider) {
    final Function<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final BiFunction<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
        return ModelPrinting.printer.printObjectShortened(target, idProvider, element);
      };
      return it.<Object>printSet(objects, mode, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendModelValue(final Description description, final Object object) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendModelValue(description, object, _defaultPrintIdProvider);
  }

  public static Description appendModelValueList(final Description description, final List<?> objects,
      final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendModelValueList(description, objects, mode, _defaultPrintIdProvider);
  }

  public static Description appendModelValueSet(final Description description, final Set<?> objects,
      final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendModelValueSet(description, objects, mode, _defaultPrintIdProvider);
  }

  public static Description appendShortenedModelValue(final Description description, final Object object) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendShortenedModelValue(description, object, _defaultPrintIdProvider);
  }

  public static Description appendShortenedModelValueList(final Description description, final List<?> objects,
      final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendShortenedModelValueList(description, objects, mode, _defaultPrintIdProvider);
  }

  public static Description appendShortenedModelValueSet(final Description description, final Set<?> objects,
      final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendShortenedModelValueSet(description, objects, mode, _defaultPrintIdProvider);
  }

  /**
   * Makes model printing use the printer provided by {@code printerProvider}.
   * Replaces the current printer.
   * 
   * @param printerProvider function that will receive the current model printer
   *                        and returns the new printer to use.
   * @return A closeable that, when closed, will revert the printer change.
   */
  public static AutoCloseable use(final Function<ModelPrinter, ModelPrinter> printerProvider) {
    final ModelPrinter oldPrinter = ModelPrinting.printer;
    ModelPrinting.printer = printerProvider.apply(oldPrinter);
    final AutoCloseable _function = () -> {
      ModelPrinting.printer = oldPrinter;
    };
    return _function;
  }

  /**
   * Makes model printing use the provided printers. Installs a printer that will
   * try the provided printers one after
   * the other until a responsible printer is found, and fall back to the
   * currently installed printer none of the
   * provided printers are responsible.
   * 
   * @return A closeable that, when closed, will revert the printer change.
   */
  public static AutoCloseable prepend(final ModelPrinter... printers) {
    final Function<ModelPrinter, ModelPrinter> _function = (ModelPrinter currentPrinter) -> {
      return new CombinedModelPrinter(Arrays.asList(printers), currentPrinter);
    };
    return ModelPrinting.use(_function);
  }

  public static Description appendPrintResult(final Description description,
      final Function<PrintTarget, PrintResult> block) {
    if ((description instanceof Description.NullDescription)) {
      return description;
    }
    final DefaultPrintTarget printTarget = new DefaultPrintTarget();
    ModelPrinting.assertResponsible(block.apply(printTarget));
    description.appendText(printTarget.toString());
    return description;
  }

  private static PrintResult assertResponsible(final PrintResult result) {
    PrintResult _xblockexpression = null;
    {
      boolean _notEquals = (!Objects.equals(result, PrintResult.NOT_RESPONSIBLE));
      Preconditions.checkState(_notEquals,
          "The current printer is not responsible for printing the provided content! Please make sure that you have set up an appropriate printer!");
      _xblockexpression = result;
    }
    return _xblockexpression;
  }

  public static ModelPrinter getPrinter() {
    return ModelPrinting.printer;
  }
}
