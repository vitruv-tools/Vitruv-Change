package tools.vitruv.change.testutils.printing;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class DefaultPrintTargetTest {
  @Test
  @DisplayName("stores the empty string by default")
  public void emptyByDefault() {
    MatcherAssert.<String>assertThat(new DefaultPrintTarget().toString(), CoreMatchers.<String>is(""));
  }

  @Test
  @DisplayName("indicates if printed text was empty")
  public void indicatesEmptyText() {
    MatcherAssert.<PrintResult>assertThat(new DefaultPrintTarget().print(""), CoreMatchers.<PrintResult>is(PrintResult.PRINTED_NO_OUTPUT));
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("preprint");
    };
    _function.accept(_defaultPrintTarget);
    MatcherAssert.assertThat(_defaultPrintTarget.print(""), CoreMatchers.<PrintResult>is(PrintResult.PRINTED_NO_OUTPUT));
  }

  @Test
  @DisplayName("indicates if printed text was not empty")
  public void indicatesNotEmptyText() {
    MatcherAssert.<PrintResult>assertThat(new DefaultPrintTarget().print(" "), CoreMatchers.<PrintResult>is(PrintResult.PRINTED));
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("");
    };
    _function.accept(_defaultPrintTarget);
    MatcherAssert.assertThat(_defaultPrintTarget.print(" "), CoreMatchers.<PrintResult>is(PrintResult.PRINTED));
  }

  @Test
  @DisplayName("stores printed text")
  public void storesPrintedText() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("test");
    };
    _function.accept(_defaultPrintTarget);
    final DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("test"));
  }

  @Test
  @DisplayName("stores multiple lines")
  public void multipleLines() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("test");
      it.newLine();
      it.print("more");
      it.newLine();
      it.print("text");
    };
    _function.accept(_defaultPrintTarget);
    final DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "test" + System.lineSeparator() + "more" + System.lineSeparator() + "text"));
  }

  @Test
  @DisplayName("allows to start with a new line")
  public void newlineAtStart() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.newLine();
    };
    _function.accept(_defaultPrintTarget);
    final DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(System.lineSeparator()));
  }

  @Test
  @DisplayName("indicates printed text after a new line")
  public void newLinePrinted() {
    MatcherAssert.<PrintResult>assertThat(new DefaultPrintTarget().newLine(), CoreMatchers.<PrintResult>is(PrintResult.PRINTED));
  }

  @Test
  @DisplayName("can increase and decrease the indent")
  public void indent() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("test");
      it.newLineIncreaseIndent();
      it.print("more");
      it.newLineIncreaseIndent();
      it.print("text");
      it.newLineDecreaseIndent();
      it.print("on");
      it.newLineIncreaseIndent();
      it.print("different");
      it.newLineDecreaseIndent();
      it.newLineDecreaseIndent();
      it.print("levels");
    };
    _function.accept(_defaultPrintTarget);
    final DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "test" + System.lineSeparator() +
      "        more" + System.lineSeparator() +
      "                text" + System.lineSeparator() +
      "        on" + System.lineSeparator() +
      "                different" + System.lineSeparator() +
      "        " + System.lineSeparator() +
      "levels"));
  }

  @Test
  @DisplayName("forbids decreasing the indent below 0")
  public void noLessThanZeroIndent() {
    final Executable _function = () -> {
      new DefaultPrintTarget().newLineDecreaseIndent();
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
    final DefaultPrintTarget target = new DefaultPrintTarget();
    target.newLineIncreaseIndent();
    target.newLineIncreaseIndent();
    target.newLineDecreaseIndent();
    target.newLineIncreaseIndent();
    target.newLineDecreaseIndent();
    target.newLineDecreaseIndent();
    final Executable _function_1 = () -> {
      target.newLineDecreaseIndent();
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function_1);
  }

  @Test
  @DisplayName("prints empty iterables")
  public void emptyIterables() {
    final BiFunction<PrintTarget, Object, PrintResult> _function = (PrintTarget $0, Object $1) -> {
      return Assertions.<PrintResult>fail("the print function should not be called!");
    };
    final BiFunction<PrintTarget, Object, PrintResult> shouldNotBeCalled = _function;
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      it.<Object>printIterableElements(List.of(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    _function_1.accept(_defaultPrintTarget);
    DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(""));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_2 = (DefaultPrintTarget it) -> {
      it.<Object>printIterable("/{", "}!", List.of(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    _function_2.accept(_defaultPrintTarget_1);
    DefaultPrintTarget _doubleArrow = _defaultPrintTarget_1;
    printed = _doubleArrow;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("/{}!"));
    DefaultPrintTarget _defaultPrintTarget_2 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_3 = (DefaultPrintTarget it) -> {
      it.<Object>printList(List.of(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    _function_3.accept(_defaultPrintTarget_2);
    DefaultPrintTarget _doubleArrow_1 = _defaultPrintTarget_2;
    printed = _doubleArrow_1;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("[]"));
    DefaultPrintTarget _defaultPrintTarget_3 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_4 = (DefaultPrintTarget it) -> {
      it.<Object>printSet(List.of(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    _function_4.accept(_defaultPrintTarget_3);
    DefaultPrintTarget _doubleArrow_2 = _defaultPrintTarget_3;
    printed = _doubleArrow_2;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("{}"));
  }

  @Test
  @DisplayName("reports if nothing was printed when printing an empty iterables")
  public void printedNoOutputIfNoOutputForIterable() {
    final BiFunction<PrintTarget, Object, PrintResult> _function = (PrintTarget $0, Object $1) -> {
      return null;
    };
    MatcherAssert.<PrintResult>assertThat(new DefaultPrintTarget().<Object>printIterableElements(List.of(), PrintMode.SINGLE_LINE_LIST, _function), CoreMatchers.<PrintResult>is(PrintResult.PRINTED_NO_OUTPUT));
  }

  @Test
  @DisplayName("prints iterables as a single line")
  public void iterableSingleLine() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_1 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1)), PrintMode.SINGLE_LINE_LIST, _function_1);
    };
    _function.accept(_defaultPrintTarget);
    DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("(1)"));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_2 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6)), PrintMode.SINGLE_LINE_LIST, _function_2);
    };
    _function_1.accept(_defaultPrintTarget_1);
    DefaultPrintTarget _doubleArrow = _defaultPrintTarget_1;
    printed = _doubleArrow;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("(1, 2, 3, 4, 5, 6)"));
  }

  @Test
  @DisplayName("prints iterables on multiple lines")
  public void iterableMultiLine() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, String, PrintResult> _function_1 = (PrintTarget $0, String $1) -> {
        return $0.print($1);
      };
      it.<String>printIterable("(", ")", List.of(), PrintMode.MULTI_LINE_LIST, _function_1);
    };
    _function.accept(_defaultPrintTarget);
    DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("()"));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_2 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1)), PrintMode.MULTI_LINE_LIST, _function_2);
    };
    _function_1.accept(_defaultPrintTarget_1);
    DefaultPrintTarget _doubleArrow = _defaultPrintTarget_1;
    printed = _doubleArrow;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "(" + System.lineSeparator() + "        1" + System.lineSeparator() + ")"));
    DefaultPrintTarget _defaultPrintTarget_2 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_2 = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_3 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6)), PrintMode.MULTI_LINE_LIST, _function_3);
    };
    _function_2.accept(_defaultPrintTarget_2);
    DefaultPrintTarget _doubleArrow_1 = _defaultPrintTarget_2;
    printed = _doubleArrow_1;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "(" + System.lineSeparator() +
      "        1," + System.lineSeparator() +
      "        2," + System.lineSeparator() +
      "        3," + System.lineSeparator() +
      "        4," + System.lineSeparator() +
      "        5," + System.lineSeparator() +
      "        6" + System.lineSeparator() +
      ")"));
  }

  @Test
  @DisplayName("respects the requested threshold for multiple lines when printing iterables")
  public void iterableMultilineThreshold() {
    final PrintMode printMode = PrintMode.multiLineIfAtLeast(4).withSeparator("; ");
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, String, PrintResult> _function_1 = (PrintTarget $0, String $1) -> {
        return $0.print($1);
      };
      it.<String>printIterable("(", ")", List.of(), printMode, _function_1);
    };
    _function.accept(_defaultPrintTarget);
    DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("()"));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_2 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1)), printMode, _function_2);
    };
    _function_1.accept(_defaultPrintTarget_1);
    DefaultPrintTarget _doubleArrow = _defaultPrintTarget_1;
    printed = _doubleArrow;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("(1)"));
    DefaultPrintTarget _defaultPrintTarget_2 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_2 = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_3 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)), printMode, _function_3);
    };
    _function_2.accept(_defaultPrintTarget_2);
    DefaultPrintTarget _doubleArrow_1 = _defaultPrintTarget_2;
    printed = _doubleArrow_1;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("(1; 2; 3)"));
    DefaultPrintTarget _defaultPrintTarget_3 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_3 = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_4 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), printMode, _function_4);
    };
    _function_3.accept(_defaultPrintTarget_3);
    DefaultPrintTarget _doubleArrow_2 = _defaultPrintTarget_3;
    printed = _doubleArrow_2;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "(" + System.lineSeparator() +
      "        1; " + System.lineSeparator() +
      "        2; " + System.lineSeparator() +
      "        3; " + System.lineSeparator() +
      "        4" + System.lineSeparator() +
      ")"));
    DefaultPrintTarget _defaultPrintTarget_4 = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function_4 = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_5 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6)), printMode, _function_5);
    };
    _function_4.accept(_defaultPrintTarget_4);
    DefaultPrintTarget _doubleArrow_3 = _defaultPrintTarget_4;
    printed = _doubleArrow_3;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "(" + System.lineSeparator() +
      "        1; " + System.lineSeparator() +
      "        2; " + System.lineSeparator() +
      "        3; " + System.lineSeparator() +
      "        4; " + System.lineSeparator() +
      "        5; " + System.lineSeparator() +
      "        6" + System.lineSeparator() +
      ")"));
  }

  @Test
  @DisplayName("returns NOT_RESPONSIBLE when printing an iterable the printer is not responsible for")
  public void iterableNotResponsible() {
    final DefaultPrintTarget target = new DefaultPrintTarget();
    final BiFunction<PrintTarget, Integer, PrintResult> _function = (PrintTarget $0, Integer $1) -> {
      PrintResult _xblockexpression = null;
      {
        System.out.println("bad");
        _xblockexpression = PrintResult.NOT_RESPONSIBLE;
      }
      return _xblockexpression;
    };
    MatcherAssert.<PrintResult>assertThat(target.<Integer>printIterable("a", "b", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), PrintMode.SINGLE_LINE_LIST, _function),
      CoreMatchers.<PrintResult>is(PrintResult.NOT_RESPONSIBLE));
    MatcherAssert.<String>assertThat(target.toString(), CoreMatchers.<String>is(""));
  }

  @Test
  @DisplayName("throws if getting NOT_RESPONSIBLE after PRINTED when printing an iterable")
  public void iterableNotResponsibleAfterPrinted() {
    final Executable _function = () -> {
      final BiFunction<PrintTarget, Integer, PrintResult> _function_1 = (PrintTarget target, Integer number) -> {
        PrintResult _switchResult = null;
        if (number != null) {
          switch (number) {
            case 1:
              _switchResult = PrintResult.PRINTED;
              break;
            case 2:
              _switchResult = PrintResult.NOT_RESPONSIBLE;
              break;
            default:
              _switchResult = PrintResult.PRINTED;
              break;
          }
        } else {
          _switchResult = PrintResult.PRINTED;
        }
        return _switchResult;
      };
      new DefaultPrintTarget().<Integer>printIterable("a", "b", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), PrintMode.SINGLE_LINE_LIST, _function_1);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  @Test
  @DisplayName("tolerates if getting NOT_RESPONSIBLE after PRINTED_NO_OUTPUT when printing an iterable ")
  public void iterableNotResponsibleAfterPrintedNoOutput() {
    final DefaultPrintTarget target = new DefaultPrintTarget();
    final BiFunction<PrintTarget, Integer, PrintResult> _function = (PrintTarget subTarget, Integer number) -> {
      PrintResult _xblockexpression = null;
      {
        subTarget.print("bad");
        PrintResult _switchResult = null;
        if (number != null) {
          switch (number) {
            case 1:
              _switchResult = PrintResult.PRINTED_NO_OUTPUT;
              break;
            case 2:
              _switchResult = PrintResult.NOT_RESPONSIBLE;
              break;
            default:
              _switchResult = PrintResult.PRINTED;
              break;
          }
        } else {
          _switchResult = PrintResult.PRINTED;
        }
        _xblockexpression = _switchResult;
      }
      return _xblockexpression;
    };
    MatcherAssert.<PrintResult>assertThat(target.<Integer>printIterable("a", "b", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), PrintMode.SINGLE_LINE_LIST, _function), CoreMatchers.<PrintResult>is(PrintResult.NOT_RESPONSIBLE));
    MatcherAssert.<String>assertThat(target.toString(), CoreMatchers.<String>is(""));
  }

  @Test
  @DisplayName("appends printed iterables to previously printed text")
  public void appendIterable() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("previous");
      final BiFunction<PrintTarget, Integer, PrintResult> _function_1 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)), PrintMode.MULTI_LINE_LIST, _function_1);
    };
    _function.accept(_defaultPrintTarget);
    final DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "previous(" + System.lineSeparator() +
      "        1," + System.lineSeparator() +
      "        2," + System.lineSeparator() +
      "        3" + System.lineSeparator() +
      ")"));
  }

  @Test
  @DisplayName("manages indents correctly when printing nested iterables")
  public void nestedIterables() {
    final PrintMode printMode = PrintMode.multiLineIfAtLeast(2).withSeparator(",");
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Consumer<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final BiFunction<PrintTarget, List<List<Integer>>, PrintResult> _function_1 = (PrintTarget subTarget, List<List<Integer>> subList) -> {
        final BiFunction<PrintTarget, List<Integer>, PrintResult> _function_2 = (PrintTarget subSubTarget, List<Integer> subSubList) -> {
          final BiFunction<PrintTarget, Integer, PrintResult> _function_3 = (PrintTarget $0, Integer $1) -> {
            return $0.print(String.valueOf($1));
          };
          return subSubTarget.<Integer>printIterable("{", "}", subSubList, printMode, _function_3);
        };
        return subTarget.<List<Integer>>printIterable("[", "]", subList, printMode, _function_2);
      };
      it.<List<List<Integer>>>printIterable("(", ")", List.<List<List<Integer>>>of(
        List.<List<Integer>>of(List.<Integer>of(Integer.valueOf(1)), List.<Integer>of(Integer.valueOf(2), Integer.valueOf(2), Integer.valueOf(2), Integer.valueOf(2))),
        List.of(),
        List.<List<Integer>>of(List.<Integer>of(Integer.valueOf(3))),
        List.<List<Integer>>of(List.of(), List.<Integer>of(Integer.valueOf(4), Integer.valueOf(4)), List.<Integer>of(Integer.valueOf(5)))), printMode, _function_1);
    };
    _function.accept(_defaultPrintTarget);
    final DefaultPrintTarget printed = _defaultPrintTarget;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(
      "(" + System.lineSeparator() +
      "        [" + System.lineSeparator() +
      "                {1}," + System.lineSeparator() +
      "                {" + System.lineSeparator() +
      "                        2," + System.lineSeparator() +
      "                        2," + System.lineSeparator() +
      "                        2," + System.lineSeparator() +
      "                        2" + System.lineSeparator() +
      "                }" + System.lineSeparator() +
      "        ]," + System.lineSeparator() +
      "        []," + System.lineSeparator() +
      "        [{3}]," + System.lineSeparator() +
      "        [" + System.lineSeparator() +
      "                {}," + System.lineSeparator() +
      "                {" + System.lineSeparator() +
      "                        4," + System.lineSeparator() +
      "                        4" + System.lineSeparator() +
      "                }," + System.lineSeparator() +
      "                {5}" + System.lineSeparator() +
      "        ]" + System.lineSeparator() +
      ")"));
  }
}
