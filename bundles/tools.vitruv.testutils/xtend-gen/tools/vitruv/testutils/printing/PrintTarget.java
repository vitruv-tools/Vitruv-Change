package tools.vitruv.testutils.printing;

import com.google.common.base.Objects;
import org.eclipse.xtext.xbase.lib.Functions.Function2;

@SuppressWarnings("all")
public interface PrintTarget {
  PrintResult print(final String text);

  PrintResult newLine();

  PrintResult newLineIncreaseIndent();

  PrintResult newLineDecreaseIndent();

  <T extends Object> PrintResult printIterable(final String start, final String end, final Iterable<? extends T> elements, final PrintMode mode, final Function2<? super PrintTarget, ? super T, ? extends PrintResult> elementPrinter);

  default <T extends Object> PrintResult printList(final Iterable<? extends T> elements, final PrintMode mode, final Function2<? super PrintTarget, ? super T, ? extends PrintResult> elementPrinter) {
    return this.<T>printIterable("[", "]", elements, mode, elementPrinter);
  }

  default <T extends Object> PrintResult printSet(final Iterable<? extends T> elements, final PrintMode mode, final Function2<? super PrintTarget, ? super T, ? extends PrintResult> elementPrinter) {
    return this.<T>printIterable("{", "}", elements, mode, elementPrinter);
  }

  default <T extends Object> PrintResult printIterableElements(final Iterable<? extends T> elements, final PrintMode mode, final Function2<? super PrintTarget, ? super T, ? extends PrintResult> elementPrinter) {
    return this.<T>printIterable("", "", elements, mode, elementPrinter);
  }

  default <T extends Object> PrintResult printValue(final T value, final Function2<? super PrintTarget, ? super T, ? extends PrintResult> valuePrinter) {
    PrintResult _switchResult = null;
    boolean _matched = false;
    if (Objects.equal(value, null)) {
      _matched=true;
    }
    if (!_matched) {
      if (value instanceof Number) {
        _matched=true;
      }
    }
    if (!_matched) {
      if (value instanceof Boolean) {
        _matched=true;
      }
    }
    if (_matched) {
      _switchResult = valuePrinter.apply(this, value);
    }
    if (!_matched) {
      if (value instanceof String) {
        _matched=true;
        PrintResult _print = this.print("\"");
        PrintResult _apply = valuePrinter.apply(this, value);
        PrintResult _plus = PrintResultExtension.operator_plus(_print, _apply);
        PrintResult _print_1 = this.print("\"");
        _switchResult = PrintResultExtension.operator_plus(_plus, _print_1);
      }
    }
    if (!_matched) {
      PrintResult _print = this.print("<");
      PrintResult _apply = valuePrinter.apply(this, value);
      PrintResult _plus = PrintResultExtension.operator_plus(_print, _apply);
      PrintResult _print_1 = this.print(">");
      _switchResult = PrintResultExtension.operator_plus(_plus, _print_1);
    }
    return _switchResult;
  }
}
