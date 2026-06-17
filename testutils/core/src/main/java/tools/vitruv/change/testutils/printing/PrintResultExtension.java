package tools.vitruv.change.testutils.printing;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;

public final class PrintResultExtension {
  public static PrintResult operator_plus(final PrintResult a, final PrintResult b) {
    PrintResult _xblockexpression = null;
    {
      Preconditions.<PrintResult>checkNotNull(a, "previous result");
      Preconditions.<PrintResult>checkNotNull(b, "latest result");
      PrintResult _switchResult = null;
      if (a != null) {
        switch (a) {
          case PRINTED:
            PrintResult _switchResult_1 = null;
            if (b != null) {
              switch (b) {
                case PRINTED:
                case PRINTED_NO_OUTPUT:
                  _switchResult_1 = PrintResult.PRINTED;
                  break;
                case NOT_RESPONSIBLE:
                  throw new IllegalStateException(
                      "Got " + PrintResult.NOT_RESPONSIBLE + " after " + PrintResult.PRINTED + "!");
                default:
                  break;
              }
            }
            _switchResult = _switchResult_1;
            break;
          case PRINTED_NO_OUTPUT:
            PrintResult _switchResult_2 = null;
            if (b != null) {
              switch (b) {
                case PRINTED_NO_OUTPUT:
                  _switchResult_2 = PrintResult.PRINTED_NO_OUTPUT;
                  break;
                case NOT_RESPONSIBLE:
                  _switchResult_2 = PrintResult.NOT_RESPONSIBLE;
                  break;
                case PRINTED:
                  _switchResult_2 = PrintResult.PRINTED;
                  break;
                default:
                  break;
              }
            }
            _switchResult = _switchResult_2;
            break;
          case NOT_RESPONSIBLE:
            PrintResult _switchResult_3 = null;
            if (b != null) {
              switch (b) {
                case PRINTED:
                  throw new IllegalStateException(
                      "Got " + PrintResult.PRINTED + " after " + PrintResult.NOT_RESPONSIBLE + "!");
                case PRINTED_NO_OUTPUT:
                case NOT_RESPONSIBLE:
                  _switchResult_3 = PrintResult.NOT_RESPONSIBLE;
                  break;
                default:
                  break;
              }
            }
            _switchResult = _switchResult_3;
            break;
          default:
            break;
        }
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
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
