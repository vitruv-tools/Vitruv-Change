package tools.vitruv.testutils.printing;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function2;

@SuppressWarnings("all")
public class DefaultPrintTarget implements PrintTarget {
  private ArrayList<StringBuilder> lines = new ArrayList<StringBuilder>();

  private String indent = "";

  @Override
  public PrintResult print(final String text) {
    PrintResult _xblockexpression = null;
    {
      this.checkIsOpen();
      PrintResult _xifexpression = null;
      boolean _isEmpty = text.isEmpty();
      if (_isEmpty) {
        _xifexpression = PrintResult.PRINTED_NO_OUTPUT;
      } else {
        PrintResult _xblockexpression_1 = null;
        {
          boolean _isEmpty_1 = this.lines.isEmpty();
          if (_isEmpty_1) {
            StringBuilder _stringBuilder = new StringBuilder(this.indent);
            this.lines.add(_stringBuilder);
          }
          int _size = this.lines.size();
          int _minus = (_size - 1);
          this.lines.get(_minus).append(text);
          _xblockexpression_1 = PrintResult.PRINTED;
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  private PrintResult print(final DefaultPrintTarget otherTarget) {
    PrintResult _xifexpression = null;
    boolean _isEmpty = otherTarget.lines.isEmpty();
    if (_isEmpty) {
      _xifexpression = PrintResult.PRINTED_NO_OUTPUT;
    } else {
      PrintResult _xblockexpression = null;
      {
        final Iterator<StringBuilder> otherLines = otherTarget.lines.iterator();
        boolean _isEmpty_1 = this.lines.isEmpty();
        boolean _not = (!_isEmpty_1);
        if (_not) {
          int _size = this.lines.size();
          int _minus = (_size - 1);
          this.lines.get(_minus).append(otherLines.next());
        }
        while (otherLines.hasNext()) {
          StringBuilder _insert = otherLines.next().insert(0, this.indent);
          this.lines.add(_insert);
        }
        otherTarget.lines = null;
        _xblockexpression = PrintResult.PRINTED;
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }

  @Override
  public <T extends Object> PrintResult printIterable(final String start, final String end, final Iterable<? extends T> elements, final PrintMode mode, final Function2<? super PrintTarget, ? super T, ? extends PrintResult> elementPrinter) {
    this.checkIsOpen();
    ArrayList<DefaultPrintTarget> _xifexpression = null;
    if ((elements instanceof Collection<?>)) {
      int _size = ((Collection<?>)elements).size();
      _xifexpression = new ArrayList<DefaultPrintTarget>(_size);
    } else {
      _xifexpression = new ArrayList<DefaultPrintTarget>();
    }
    ArrayList<DefaultPrintTarget> preprinted = _xifexpression;
    for (final Iterator<? extends T> elementsIterator = elements.iterator(); elementsIterator.hasNext();) {
      {
        final T element = elementsIterator.next();
        final DefaultPrintTarget subTarget = new DefaultPrintTarget();
        PrintResult _apply = elementPrinter.apply(subTarget, element);
        if (_apply != null) {
          switch (_apply) {
            case NOT_RESPONSIBLE:
              boolean _isEmpty = preprinted.isEmpty();
              if (_isEmpty) {
                return PrintResult.NOT_RESPONSIBLE;
              } else {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("Got NOT_RESPONSIBLE for ‹");
                _builder.append(element);
                _builder.append("› after already having printed something!");
                throw new IllegalStateException(_builder.toString());
              }
            case PRINTED:
              preprinted.add(subTarget);
              break;
            case PRINTED_NO_OUTPUT:
              break;
            default:
              break;
          }
        }
      }
    }
    PrintResult _xifexpression_1 = null;
    boolean _isEmpty = preprinted.isEmpty();
    if (_isEmpty) {
      PrintResult _print = this.print(start);
      PrintResult _print_1 = this.print(end);
      _xifexpression_1 = PrintResultExtension.operator_plus(_print, _print_1);
    } else {
      PrintResult _xifexpression_2 = null;
      int _size_1 = preprinted.size();
      int _multiLineIfAtLeastItemCount = mode.getMultiLineIfAtLeastItemCount();
      boolean _greaterEqualsThan = (_size_1 >= _multiLineIfAtLeastItemCount);
      if (_greaterEqualsThan) {
        _xifexpression_2 = this.<Object>printMultiLine(start, end, mode.getSeparator(), preprinted);
      } else {
        _xifexpression_2 = this.<Object>printSingleLine(start, end, mode.getSeparator(), preprinted);
      }
      _xifexpression_1 = _xifexpression_2;
    }
    return _xifexpression_1;
  }

  private <T extends Object> PrintResult printSingleLine(final String start, final String end, final String separator, final Collection<DefaultPrintTarget> preprinted) {
    PrintResult result = this.print(start);
    for (final Iterator<DefaultPrintTarget> outputs = preprinted.iterator(); outputs.hasNext();) {
      {
        PrintResult _result = result;
        PrintResult _print = this.print(outputs.next());
        result = PrintResultExtension.operator_plus(_result, _print);
        boolean _hasNext = outputs.hasNext();
        if (_hasNext) {
          PrintResult _result_1 = result;
          PrintResult _print_1 = this.print(separator);
          result = PrintResultExtension.operator_plus(_result_1, _print_1);
        }
      }
    }
    PrintResult _print = this.print(end);
    return PrintResultExtension.operator_plus(result, _print);
  }

  private <T extends Object> PrintResult printMultiLine(final String start, final String end, final String separator, final Collection<DefaultPrintTarget> preprinted) {
    PrintResult _print = this.print(start);
    PrintResult _newLineIncreaseIndent = this.newLineIncreaseIndent();
    PrintResult result = PrintResultExtension.operator_plus(_print, _newLineIncreaseIndent);
    for (final Iterator<DefaultPrintTarget> outputs = preprinted.iterator(); outputs.hasNext();) {
      {
        PrintResult _result = result;
        PrintResult _print_1 = this.print(outputs.next());
        result = PrintResultExtension.operator_plus(_result, _print_1);
        boolean _hasNext = outputs.hasNext();
        if (_hasNext) {
          PrintResult _result_1 = result;
          PrintResult _print_2 = this.print(separator);
          PrintResult _newLine = this.newLine();
          PrintResult _plus = PrintResultExtension.operator_plus(_print_2, _newLine);
          result = PrintResultExtension.operator_plus(_result_1, _plus);
        }
      }
    }
    PrintResult _newLineDecreaseIndent = this.newLineDecreaseIndent();
    PrintResult _plus = PrintResultExtension.operator_plus(result, _newLineDecreaseIndent);
    PrintResult _print_1 = this.print(end);
    return PrintResultExtension.operator_plus(_plus, _print_1);
  }

  @Override
  public PrintResult newLine() {
    PrintResult _xblockexpression = null;
    {
      this.checkIsOpen();
      boolean _isEmpty = this.lines.isEmpty();
      if (_isEmpty) {
        StringBuilder _stringBuilder = new StringBuilder(0);
        this.lines.add(_stringBuilder);
      }
      StringBuilder _stringBuilder_1 = new StringBuilder(this.indent);
      this.lines.add(_stringBuilder_1);
      _xblockexpression = PrintResult.PRINTED;
    }
    return _xblockexpression;
  }

  @Override
  public PrintResult newLineIncreaseIndent() {
    PrintResult _xblockexpression = null;
    {
      this.increaseIndent();
      _xblockexpression = this.newLine();
    }
    return _xblockexpression;
  }

  @Override
  public PrintResult newLineDecreaseIndent() {
    PrintResult _xblockexpression = null;
    {
      this.decreaseIndent();
      _xblockexpression = this.newLine();
    }
    return _xblockexpression;
  }

  private String increaseIndent() {
    String _indent = this.indent;
    return this.indent = (_indent + "        ");
  }

  private String decreaseIndent() {
    String _xblockexpression = null;
    {
      int _length = this.indent.length();
      boolean _greaterThan = (_length > 0);
      Preconditions.checkState(_greaterThan, "The indentation is already 0!");
      _xblockexpression = this.indent = this.indent.substring(8);
    }
    return _xblockexpression;
  }

  private void checkIsOpen() {
    if ((this.lines == null)) {
      throw new IllegalStateException(("This printer has already been incorporated into another printer and " + 
        "must not be used anymore!"));
    }
  }

  @Override
  public String toString() {
    this.checkIsOpen();
    String _xifexpression = null;
    boolean _isEmpty = this.lines.isEmpty();
    if (_isEmpty) {
      _xifexpression = "";
    } else {
      String _xifexpression_1 = null;
      int _size = this.lines.size();
      boolean _tripleEquals = (_size == 1);
      if (_tripleEquals) {
        _xifexpression_1 = this.lines.get(0).toString();
      } else {
        String _xblockexpression = null;
        {
          int resultLength = 0;
          for (final StringBuilder line : this.lines) {
            int _resultLength = resultLength;
            int _length = line.length();
            resultLength = (_resultLength + _length);
          }
          int _resultLength_1 = resultLength;
          int _size_1 = this.lines.size();
          int _minus = (_size_1 - 1);
          int _length_1 = System.lineSeparator().length();
          int _multiply = (_minus * _length_1);
          resultLength = (_resultLength_1 + _multiply);
          final StringBuilder result = new StringBuilder(resultLength);
          for (final Iterator<StringBuilder> linesIt = this.lines.iterator(); linesIt.hasNext();) {
            {
              result.append(linesIt.next());
              boolean _hasNext = linesIt.hasNext();
              if (_hasNext) {
                result.append(System.lineSeparator());
              }
            }
          }
          _xblockexpression = result.toString();
        }
        _xifexpression_1 = _xblockexpression;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
}
