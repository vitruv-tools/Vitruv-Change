package tools.vitruv.testutils.printing;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.Pure;
import org.hamcrest.Description;

@SuppressWarnings("all")
public final class ModelPrinting {
  private ModelPrinting() {
  }

  @Accessors(AccessorType.PUBLIC_GETTER)
  private static ModelPrinter printer = new DefaultModelPrinter();

  public static Description appendModelValue(final Description description, final Object object, final PrintIdProvider idProvider) {
    final Function1<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final Function2<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget subTarget, Object theObject) -> {
        return ModelPrinting.printer.printObject(subTarget, idProvider, theObject);
      };
      return it.<Object>printValue(object, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendModelValueList(final Description description, final List<?> objects, final PrintMode mode, final PrintIdProvider idProvider) {
    final Function1<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final Function2<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
        return ModelPrinting.printer.printObject(target, idProvider, element);
      };
      return it.<Object>printList(objects, mode, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendModelValueSet(final Description description, final Set<?> objects, final PrintMode mode, final PrintIdProvider idProvider) {
    final Function1<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final Function2<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
        return ModelPrinting.printer.printObject(target, idProvider, element);
      };
      return it.<Object>printSet(objects, mode, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendShortenedModelValue(final Description description, final Object object, final PrintIdProvider idProvider) {
    final Function1<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final Function2<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget subTarget, Object theObject) -> {
        return ModelPrinting.printer.printObjectShortened(subTarget, idProvider, theObject);
      };
      return it.<Object>printValue(object, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendShortenedModelValueList(final Description description, final List<?> objects, final PrintMode mode, final PrintIdProvider idProvider) {
    final Function1<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final Function2<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
        return ModelPrinting.printer.printObjectShortened(target, idProvider, element);
      };
      return it.<Object>printList(objects, mode, _function_1);
    };
    return ModelPrinting.appendPrintResult(description, _function);
  }

  public static Description appendShortenedModelValueSet(final Description description, final Set<?> objects, final PrintMode mode, final PrintIdProvider idProvider) {
    final Function1<PrintTarget, PrintResult> _function = (PrintTarget it) -> {
      final Function2<PrintTarget, Object, PrintResult> _function_1 = (PrintTarget target, Object element) -> {
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

  public static Description appendModelValueList(final Description description, final List<?> objects, final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendModelValueList(description, objects, mode, _defaultPrintIdProvider);
  }

  public static Description appendModelValueSet(final Description description, final Set<?> objects, final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendModelValueSet(description, objects, mode, _defaultPrintIdProvider);
  }

  public static Description appendShortenedModelValue(final Description description, final Object object) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendShortenedModelValue(description, object, _defaultPrintIdProvider);
  }

  public static Description appendShortenedModelValueList(final Description description, final List<?> objects, final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendShortenedModelValueList(description, objects, mode, _defaultPrintIdProvider);
  }

  public static Description appendShortenedModelValueSet(final Description description, final Set<?> objects, final PrintMode mode) {
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    return ModelPrinting.appendShortenedModelValueSet(description, objects, mode, _defaultPrintIdProvider);
  }

  /**
   * Makes model printing use the printer provided by {@code printerProvider}. Replaces the current printer.
   * 
   * @param printerProvider function that will receive the current model printer and returns the new printer to use.
   * @return A closeable that, when closed, will revert the printer change.
   */
  public static AutoCloseable use(final Function1<? super ModelPrinter, ? extends ModelPrinter> printerProvider) {
    final ModelPrinter oldPrinter = ModelPrinting.printer;
    ModelPrinting.printer = printerProvider.apply(oldPrinter);
    final AutoCloseable _function = () -> {
      ModelPrinting.printer = oldPrinter;
    };
    return _function;
  }

  /**
   * Makes model printing use the provided printers. Installs a printer that will try the provided printers one after
   * the other until a responsible printer is found, and fall back to the currently installed printer none of the
   * provided printers are responsible.
   * 
   * @return A closeable that, when closed, will revert the printer change.
   */
  public static AutoCloseable prepend(final ModelPrinter... printers) {
    final Function1<ModelPrinter, ModelPrinter> _function = (ModelPrinter currentPrinter) -> {
      return new CombinedModelPrinter((List<? extends ModelPrinter>)Conversions.doWrapArray(printers), currentPrinter);
    };
    return ModelPrinting.use(_function);
  }

  public static Description appendPrintResult(final Description description, final Function1<? super PrintTarget, ? extends PrintResult> block) {
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
      boolean _notEquals = (!Objects.equal(result, PrintResult.NOT_RESPONSIBLE));
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("The current printer is not responsible for printing the provided content! Please make sure that you have set up an appropriate printer!");
      Preconditions.checkState(_notEquals, _builder);
      _xblockexpression = result;
    }
    return _xblockexpression;
  }

  @Pure
  public static ModelPrinter getPrinter() {
    return ModelPrinting.printer;
  }
}
