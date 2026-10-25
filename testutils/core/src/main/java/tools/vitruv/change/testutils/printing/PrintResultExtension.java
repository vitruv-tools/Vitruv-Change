package tools.vitruv.change.testutils.printing;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;

public final class PrintResultExtension {
  public static PrintResult operator_plus(final PrintResult a, final PrintResult b) {
    Preconditions.<PrintResult>checkNotNull(a, "previous result");
    Preconditions.<PrintResult>checkNotNull(b, "latest result");

    if (((a == PrintResult.PRINTED) && (b == PrintResult.NOT_RESPONSIBLE))
        || ((a == PrintResult.NOT_RESPONSIBLE) && (b == PrintResult.PRINTED))) {
      throw inconsistentResults(a, b);
    }
    if ((a == PrintResult.PRINTED) || (b == PrintResult.PRINTED)) {
      return PrintResult.PRINTED;
    }
    if ((a == PrintResult.NOT_RESPONSIBLE) || (b == PrintResult.NOT_RESPONSIBLE)) {
      return PrintResult.NOT_RESPONSIBLE;
    }
    return PrintResult.PRINTED_NO_OUTPUT;
  }

  private static IllegalStateException inconsistentResults(
      final PrintResult previousResult, final PrintResult latestResult) {
    return new IllegalStateException("Got " + latestResult + " after " + previousResult + "!");
  }

  public static PrintResult combine(final Iterable<? extends PrintResult> results) {
    PrintResult acc = PrintResult.PRINTED_NO_OUTPUT;
    for (PrintResult r : results) {
      acc = PrintResultExtension.operator_plus(acc, r);
    }
    return acc;
  }

  public static PrintResult appendIfPrinted(final PrintResult result, final Supplier<? extends PrintResult> printer) {
    PrintResult _switchResult = null;
    if (result != null) {
      switch (result) {
        case PRINTED:
          PrintResult _apply = printer.get();
          _switchResult = PrintResultExtension.operator_plus(result, _apply);
          break;
        case PRINTED_NO_OUTPUT:
        case NOT_RESPONSIBLE:
          _switchResult = result;
          break;
        default:
          break;
      }
    }
    return _switchResult;
  }

  private PrintResultExtension() {

  }
}
