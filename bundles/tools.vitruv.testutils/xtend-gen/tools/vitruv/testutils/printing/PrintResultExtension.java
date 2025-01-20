package tools.vitruv.testutils.printing;

import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.activextendannotations.Utility;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@Utility
@SuppressWarnings("all")
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
                  StringConcatenation _builder = new StringConcatenation();
                  _builder.append("Got ");
                  _builder.append(PrintResult.NOT_RESPONSIBLE);
                  _builder.append(" after ");
                  _builder.append(PrintResult.PRINTED);
                  _builder.append("!");
                  throw new IllegalStateException(_builder.toString());
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
                  StringConcatenation _builder_1 = new StringConcatenation();
                  _builder_1.append("Got ");
                  _builder_1.append(PrintResult.PRINTED);
                  _builder_1.append(" after ");
                  _builder_1.append(PrintResult.NOT_RESPONSIBLE);
                  _builder_1.append("!");
                  throw new IllegalStateException(_builder_1.toString());
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
    final Function2<PrintResult, PrintResult, PrintResult> _function = (PrintResult $0, PrintResult $1) -> {
      return PrintResultExtension.operator_plus($0, $1);
    };
    return IterableExtensions.fold(results, PrintResult.PRINTED_NO_OUTPUT, _function);
  }

  public static PrintResult appendIfPrinted(final PrintResult result, final Function0<? extends PrintResult> printer) {
    PrintResult _switchResult = null;
    if (result != null) {
      switch (result) {
        case PRINTED:
          PrintResult _apply = printer.apply();
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
