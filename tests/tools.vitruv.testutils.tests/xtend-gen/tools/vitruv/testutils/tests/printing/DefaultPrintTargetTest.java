package tools.vitruv.testutils.tests.printing;

import java.util.List;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.testutils.printing.DefaultPrintTarget;
import tools.vitruv.testutils.printing.PrintMode;
import tools.vitruv.testutils.printing.PrintResult;
import tools.vitruv.testutils.printing.PrintTarget;

@SuppressWarnings("all")
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
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("preprint");
    };
    MatcherAssert.<PrintResult>assertThat(ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function).print(""), CoreMatchers.<PrintResult>is(PrintResult.PRINTED_NO_OUTPUT));
  }

  @Test
  @DisplayName("indicates if printed text was not empty")
  public void indicatesNotEmptyText() {
    MatcherAssert.<PrintResult>assertThat(new DefaultPrintTarget().print(" "), CoreMatchers.<PrintResult>is(PrintResult.PRINTED));
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("");
    };
    MatcherAssert.<PrintResult>assertThat(ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function).print(" "), CoreMatchers.<PrintResult>is(PrintResult.PRINTED));
  }

  @Test
  @DisplayName("stores printed text")
  public void storesPrintedText() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("test");
    };
    final DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("test"));
  }

  @Test
  @DisplayName("stores multiple lines")
  public void multipleLines() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("test");
      it.newLine();
      it.print("more");
      it.newLine();
      it.print("text");
    };
    final DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("test");
    _builder.newLine();
    _builder.append("more");
    _builder.newLine();
    _builder.append("text");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("allows to start with a new line")
  public void newlineAtStart() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.newLine();
    };
    final DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
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
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
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
    final DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("test");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("more");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("text");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("on");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("different");
    _builder.newLine();
    _builder.append("        ");
    _builder.newLine();
    _builder.append("levels");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder.toString()));
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
    final Function2<PrintTarget, Object, PrintResult> _function = (PrintTarget $0, Object $1) -> {
      return Assertions.<PrintResult>fail("the print function should not be called!");
    };
    final Function2<? super PrintTarget, ? super Object, ? extends PrintResult> shouldNotBeCalled = _function;
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      it.<Object>printIterableElements(CollectionLiterals.<Object>emptyList(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function_1);
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(""));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_2 = (DefaultPrintTarget it) -> {
      it.<Object>printIterable("/{", "}!", CollectionLiterals.<Object>emptyList(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    DefaultPrintTarget _doubleArrow = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_1, _function_2);
    printed = _doubleArrow;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("/{}!"));
    DefaultPrintTarget _defaultPrintTarget_2 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_3 = (DefaultPrintTarget it) -> {
      it.<Object>printList(CollectionLiterals.<Object>emptyList(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    DefaultPrintTarget _doubleArrow_1 = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_2, _function_3);
    printed = _doubleArrow_1;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("[]"));
    DefaultPrintTarget _defaultPrintTarget_3 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_4 = (DefaultPrintTarget it) -> {
      it.<Object>printSet(CollectionLiterals.<Object>emptyList(), PrintMode.SINGLE_LINE_LIST, shouldNotBeCalled);
    };
    DefaultPrintTarget _doubleArrow_2 = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_3, _function_4);
    printed = _doubleArrow_2;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("{}"));
  }

  @Test
  @DisplayName("reports if nothing was printed when printing an empty iterables")
  public void printedNoOutputIfNoOutputForIterable() {
    final Function2<PrintTarget, Object, PrintResult> _function = (PrintTarget $0, Object $1) -> {
      return null;
    };
    MatcherAssert.<PrintResult>assertThat(new DefaultPrintTarget().<Object>printIterableElements(CollectionLiterals.<Object>emptyList(), PrintMode.SINGLE_LINE_LIST, _function), CoreMatchers.<PrintResult>is(PrintResult.PRINTED_NO_OUTPUT));
  }

  @Test
  @DisplayName("prints iterables as a single line")
  public void iterableSingleLine() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_1 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1)), PrintMode.SINGLE_LINE_LIST, _function_1);
    };
    DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("(1)"));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_2 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6)), PrintMode.SINGLE_LINE_LIST, _function_2);
    };
    DefaultPrintTarget _doubleArrow = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_1, _function_1);
    printed = _doubleArrow;
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("(1, 2, 3, 4, 5, 6)"));
  }

  @Test
  @DisplayName("prints iterables on multiple lines")
  public void iterableMultiLine() {
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, String, PrintResult> _function_1 = (PrintTarget $0, String $1) -> {
        return $0.print($1);
      };
      it.<String>printIterable("(", ")", CollectionLiterals.<String>emptyList(), PrintMode.MULTI_LINE_LIST, _function_1);
    };
    DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("()"));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_2 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1)), PrintMode.MULTI_LINE_LIST, _function_2);
    };
    DefaultPrintTarget _doubleArrow = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_1, _function_1);
    printed = _doubleArrow;
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("1");
    _builder.newLine();
    _builder.append(")");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder.toString()));
    DefaultPrintTarget _defaultPrintTarget_2 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_2 = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_3 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6)), PrintMode.MULTI_LINE_LIST, _function_3);
    };
    DefaultPrintTarget _doubleArrow_1 = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_2, _function_2);
    printed = _doubleArrow_1;
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("(");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("1,");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("2,");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("3,");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("4,");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("5,");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("6");
    _builder_1.newLine();
    _builder_1.append(")");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder_1.toString()));
  }

  @Test
  @DisplayName("respects the requested threshold for multiple lines when printing iterables")
  public void iterableMultilineThreshold() {
    final PrintMode printMode = PrintMode.multiLineIfAtLeast(4).withSeparator("; ");
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, String, PrintResult> _function_1 = (PrintTarget $0, String $1) -> {
        return $0.print($1);
      };
      it.<String>printIterable("(", ")", CollectionLiterals.<String>emptyList(), printMode, _function_1);
    };
    DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is("()"));
    DefaultPrintTarget _defaultPrintTarget_1 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_1 = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_2 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1)), printMode, _function_2);
    };
    DefaultPrintTarget _doubleArrow = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_1, _function_1);
    printed = _doubleArrow;
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(1)");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder.toString()));
    DefaultPrintTarget _defaultPrintTarget_2 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_2 = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_3 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)), printMode, _function_3);
    };
    DefaultPrintTarget _doubleArrow_1 = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_2, _function_2);
    printed = _doubleArrow_1;
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("(1; 2; 3)");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder_1.toString()));
    DefaultPrintTarget _defaultPrintTarget_3 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_3 = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_4 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), printMode, _function_4);
    };
    DefaultPrintTarget _doubleArrow_2 = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_3, _function_3);
    printed = _doubleArrow_2;
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("(");
    _builder_2.newLine();
    _builder_2.append("        ");
    _builder_2.append("1; ");
    _builder_2.newLine();
    _builder_2.append("        ");
    _builder_2.append("2; ");
    _builder_2.newLine();
    _builder_2.append("        ");
    _builder_2.append("3; ");
    _builder_2.newLine();
    _builder_2.append("        ");
    _builder_2.append("4");
    _builder_2.newLine();
    _builder_2.append(")");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder_2.toString()));
    DefaultPrintTarget _defaultPrintTarget_4 = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function_4 = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, Integer, PrintResult> _function_5 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6)), printMode, _function_5);
    };
    DefaultPrintTarget _doubleArrow_3 = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget_4, _function_4);
    printed = _doubleArrow_3;
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("(");
    _builder_3.newLine();
    _builder_3.append("        ");
    _builder_3.append("1; ");
    _builder_3.newLine();
    _builder_3.append("        ");
    _builder_3.append("2; ");
    _builder_3.newLine();
    _builder_3.append("        ");
    _builder_3.append("3; ");
    _builder_3.newLine();
    _builder_3.append("        ");
    _builder_3.append("4; ");
    _builder_3.newLine();
    _builder_3.append("        ");
    _builder_3.append("5; ");
    _builder_3.newLine();
    _builder_3.append("        ");
    _builder_3.append("6");
    _builder_3.newLine();
    _builder_3.append(")");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder_3.toString()));
  }

  @Test
  @DisplayName("returns NOT_RESPONSIBLE when printing an iterable the printer is not responsible for")
  public void iterableNotResponsible() {
    final DefaultPrintTarget target = new DefaultPrintTarget();
    final Function2<PrintTarget, Integer, PrintResult> _function = (PrintTarget $0, Integer $1) -> {
      PrintResult _xblockexpression = null;
      {
        InputOutput.<String>print("bad");
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
      final Function2<PrintTarget, Integer, PrintResult> _function_1 = (PrintTarget target, Integer number) -> {
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
    final Function2<PrintTarget, Integer, PrintResult> _function = (PrintTarget subTarget, Integer number) -> {
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
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      it.print("previous");
      final Function2<PrintTarget, Integer, PrintResult> _function_1 = (PrintTarget $0, Integer $1) -> {
        return $0.print(String.valueOf($1));
      };
      it.<Integer>printIterable("(", ")", List.<Integer>of(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)), PrintMode.MULTI_LINE_LIST, _function_1);
    };
    final DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("previous(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("1,");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("2,");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("3");
    _builder.newLine();
    _builder.append(")");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("manages indents correctly when printing nested iterables")
  public void nestedIterables() {
    final PrintMode printMode = PrintMode.multiLineIfAtLeast(2).withSeparator(",");
    DefaultPrintTarget _defaultPrintTarget = new DefaultPrintTarget();
    final Procedure1<DefaultPrintTarget> _function = (DefaultPrintTarget it) -> {
      final Function2<PrintTarget, List<List<Integer>>, PrintResult> _function_1 = (PrintTarget subTarget, List<List<Integer>> subList) -> {
        final Function2<PrintTarget, List<Integer>, PrintResult> _function_2 = (PrintTarget subSubTarget, List<Integer> subSubList) -> {
          final Function2<PrintTarget, Integer, PrintResult> _function_3 = (PrintTarget $0, Integer $1) -> {
            return $0.print(String.valueOf($1));
          };
          return subSubTarget.<Integer>printIterable("{", "}", subSubList, printMode, _function_3);
        };
        return subTarget.<List<Integer>>printIterable("[", "]", subList, printMode, _function_2);
      };
      it.<List<List<Integer>>>printIterable("(", ")", List.<List<List<Integer>>>of(
        List.<List<Integer>>of(List.<Integer>of(Integer.valueOf(1)), List.<Integer>of(Integer.valueOf(2), Integer.valueOf(2), Integer.valueOf(2), Integer.valueOf(2))), 
        CollectionLiterals.<List<Integer>>emptyList(), 
        List.<List<Integer>>of(List.<Integer>of(Integer.valueOf(3))), 
        List.<List<Integer>>of(CollectionLiterals.<Integer>emptyList(), List.<Integer>of(Integer.valueOf(4), Integer.valueOf(4)), List.<Integer>of(Integer.valueOf(5)))), printMode, _function_1);
    };
    final DefaultPrintTarget printed = ObjectExtensions.<DefaultPrintTarget>operator_doubleArrow(_defaultPrintTarget, _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("[");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("{1},");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("{");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("2,");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("2,");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("2,");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("2");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("],");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("[],");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("[{3}],");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("[");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("{},");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("{");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("4,");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("4");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("},");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("{5}");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("]");
    _builder.newLine();
    _builder.append(")");
    MatcherAssert.<String>assertThat(printed.toString(), CoreMatchers.<String>is(_builder.toString()));
  }
}
