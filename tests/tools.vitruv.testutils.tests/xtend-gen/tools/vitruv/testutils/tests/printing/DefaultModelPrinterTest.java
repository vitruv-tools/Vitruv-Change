package tools.vitruv.testutils.tests.printing;

import allElementTypes.Containable;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.ValueBased;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;
import tools.vitruv.testutils.printing.DefaultModelPrinter;
import tools.vitruv.testutils.printing.DefaultPrintIdProvider;
import tools.vitruv.testutils.printing.DefaultPrintTarget;
import tools.vitruv.testutils.printing.PrintIdProvider;
import tools.vitruv.testutils.printing.PrintTarget;

@ExtendWith(RegisterMetamodelsInStandalone.class)
@SuppressWarnings("all")
public class DefaultModelPrinterTest {
  @Test
  @DisplayName("prints integer values")
  public void printInteger() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, Integer.valueOf(5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("5"));
  }

  @Test
  @DisplayName("prints integer values (shortened)")
  public void printIntegerShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, Integer.valueOf(5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("5"));
  }

  @Test
  @DisplayName("prints double values")
  public void printDouble() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, Double.valueOf(3.5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("3.5"));
  }

  @Test
  @DisplayName("prints double values (shortened)")
  public void printDoubleShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, Double.valueOf(3.5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("3.5"));
  }

  @Test
  @DisplayName("prints boolean values")
  public void printBoolean() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, Boolean.valueOf(true));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("true"));
  }

  @Test
  @DisplayName("prints boolean values (shortened)")
  public void printBooleanShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, Boolean.valueOf(true));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("true"));
  }

  @Test
  @DisplayName("prints null")
  public void printNull() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, null);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("∅"));
  }

  @Test
  @DisplayName("prints null (shortened)")
  public void printNullShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, null);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("∅"));
  }

  @Test
  @DisplayName("prints string values")
  public void printString() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, "test");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("test"));
    final Procedure2<PrintTarget, PrintIdProvider> _function_1 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, "veryverylongtestthatcouldtbeshortenedwithevenmoreletters");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_1), CoreMatchers.<String>is("veryverylongtestthatcouldtbeshortenedwithevenmoreletters"));
  }

  @Test
  @DisplayName("prints string values (shortened)")
  public void printStringShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "test");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("test"));
    final Procedure2<PrintTarget, PrintIdProvider> _function_1 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylo");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_1), CoreMatchers.<String>is("veryverylo"));
    final Procedure2<PrintTarget, PrintIdProvider> _function_2 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylon");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_2), CoreMatchers.<String>is("veryveryl…"));
    final Procedure2<PrintTarget, PrintIdProvider> _function_3 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylongtestthatcouldtbeshortenedwithevenmoreletters");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_3), CoreMatchers.<String>is("veryveryl…"));
  }

  @Test
  @DisplayName("prints simple objects")
  public void printSimpleObject() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_1 = (NonRoot it) -> {
        it.setId("id");
        it.setValue("test");
      };
      NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_1);
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("NonRoot(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("id=\"id\"");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("value=\"test\"");
    _builder.newLine();
    _builder.append(")");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("prints simple objects shortened by id")
  public void printSimpleObjectShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_1 = (NonRoot it) -> {
        it.setId("id");
        it.setValue("test");
      };
      NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_1);
      _defaultModelPrinter.printObjectShortened($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("NonRoot(id=\"id\")");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("prints value-based objects")
  public void printValueBasedObject() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        it.setValue("test");
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ValueBased#1(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("value=\"test\"");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("children=[]");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("referenced=[]");
    _builder.newLine();
    _builder.append(")");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("prints simple objects shortened by fallback id")
  public void printValuedObjectShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        it.setValue("test");
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      _defaultModelPrinter.printObjectShortened($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ValueBased#1");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("prints resources")
  public void printResource() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      Resource _createResource = new ResourceSetImpl().createResource(URI.createURI("test://foobar"));
      final Procedure1<Resource> _function_1 = (Resource it) -> {
        EList<EObject> _contents = it.getContents();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("first");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        _contents.add(_doubleArrow);
        EList<EObject> _contents_1 = it.getContents();
        ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_3 = (ValueBased it_1) -> {
          it_1.setValue("second");
        };
        ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_3);
        _contents_1.add(_doubleArrow_1);
        EList<EObject> _contents_2 = it.getContents();
        NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
        final Procedure1<NonRootObjectContainerHelper> _function_4 = (NonRootObjectContainerHelper it_1) -> {
          it_1.setId("third");
        };
        NonRootObjectContainerHelper _doubleArrow_2 = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_4);
        _contents_2.add(_doubleArrow_2);
      };
      Resource _doubleArrow = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function_1);
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Resource@<test://foobar>[");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("NonRoot(");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("id=\"first\"");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("value=");
    _builder.append("∅", "                ");
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append("),");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("ValueBased#1(");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("value=\"second\"");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("children=[]");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("referenced=[]");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("),");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("NonRootObjectContainerHelper(");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("id=\"third\"");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("nonRootObjectsContainment=[]");
    _builder.newLine();
    _builder.append("        ");
    _builder.append(")");
    _builder.newLine();
    _builder.append("]");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("prints resources (shortened)")
  public void printResourceShortened() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      Resource _createResource = new ResourceSetImpl().createResource(URI.createURI("test://foobar"));
      final Procedure1<Resource> _function_1 = (Resource it) -> {
        EList<EObject> _contents = it.getContents();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("first");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        _contents.add(_doubleArrow);
        EList<EObject> _contents_1 = it.getContents();
        ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_3 = (ValueBased it_1) -> {
          it_1.setValue("second");
        };
        ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_3);
        _contents_1.add(_doubleArrow_1);
        EList<EObject> _contents_2 = it.getContents();
        NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
        final Procedure1<NonRootObjectContainerHelper> _function_4 = (NonRootObjectContainerHelper it_1) -> {
          it_1.setId("third");
        };
        NonRootObjectContainerHelper _doubleArrow_2 = ObjectExtensions.<NonRootObjectContainerHelper>operator_doubleArrow(_NonRootObjectContainerHelper, _function_4);
        _contents_2.add(_doubleArrow_2);
      };
      Resource _doubleArrow = ObjectExtensions.<Resource>operator_doubleArrow(_createResource, _function_1);
      _defaultModelPrinter.printObjectShortened($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Resource@<test://foobar>[NonRoot(id=\"first\"), ValueBased#1, NonRootObjectContainerHelper(id=\"third\")]");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("prints references by ID")
  public void printReferenceById() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        EList<Containable> _referenced = it.getReferenced();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_2 = (ValueBased it_1) -> {
          it_1.setValue("foo");
        };
        ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
        _referenced.add(_doubleArrow);
        EList<Containable> _referenced_1 = it.getReferenced();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("subId");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        _referenced_1.add(_doubleArrow_1);
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ValueBased#1(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("value=");
    _builder.append("∅", "        ");
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append("children=[]");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("referenced=[");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("ValueBased#2,");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("NonRoot(id=\"subId\")");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("]");
    _builder.newLine();
    _builder.append(")");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  @Test
  @DisplayName("prints nested objects")
  public void printNested() {
    final Procedure2<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        final ValueBased root = it;
        it.setValue("root");
        EList<Containable> _children = it.getChildren();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        _children.add(_doubleArrow);
        EList<Containable> _children_1 = it.getChildren();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_3 = (ValueBased it_1) -> {
          it_1.setValue("sub");
          EList<Containable> _children_2 = it_1.getChildren();
          ValueBased _ValueBased_2 = AllElementTypesCreators.aet.ValueBased();
          final Procedure1<ValueBased> _function_4 = (ValueBased it_2) -> {
            it_2.setValue("subsub 1");
          };
          ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_2, _function_4);
          _children_2.add(_doubleArrow_1);
          EList<Containable> _children_3 = it_1.getChildren();
          ValueBased _ValueBased_3 = AllElementTypesCreators.aet.ValueBased();
          final Procedure1<ValueBased> _function_5 = (ValueBased it_2) -> {
            it_2.setValue("subsub 2");
          };
          ValueBased _doubleArrow_2 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_3, _function_5);
          _children_3.add(_doubleArrow_2);
          EList<Containable> _referenced = it_1.getReferenced();
          Containable _get = root.getChildren().get(0);
          _referenced.add(_get);
        };
        ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_3);
        _children_1.add(_doubleArrow_1);
        EList<Containable> _referenced = it.getReferenced();
        Containable _get = it.getChildren().get(1);
        Containable _get_1 = ((ValueBased) _get).getChildren().get(1);
        _referenced.add(_get_1);
        EList<Containable> _referenced_1 = it.getReferenced();
        Containable _get_2 = it.getChildren().get(0);
        _referenced_1.add(_get_2);
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ValueBased#1(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("value=\"root\"");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("children=[");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("NonRoot(");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("id=\"sub\"");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("value=");
    _builder.append("∅", "                        ");
    _builder.newLineIfNotEmpty();
    _builder.append("                ");
    _builder.append("),");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("ValueBased#2(");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("value=\"sub\"");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("children=[");
    _builder.newLine();
    _builder.append("                                ");
    _builder.append("ValueBased#3(");
    _builder.newLine();
    _builder.append("                                        ");
    _builder.append("value=\"subsub 1\"");
    _builder.newLine();
    _builder.append("                                        ");
    _builder.append("children=[]");
    _builder.newLine();
    _builder.append("                                        ");
    _builder.append("referenced=[]");
    _builder.newLine();
    _builder.append("                                ");
    _builder.append("),");
    _builder.newLine();
    _builder.append("                                ");
    _builder.append("ValueBased#4(");
    _builder.newLine();
    _builder.append("                                        ");
    _builder.append("value=\"subsub 2\"");
    _builder.newLine();
    _builder.append("                                        ");
    _builder.append("children=[]");
    _builder.newLine();
    _builder.append("                                        ");
    _builder.append("referenced=[]");
    _builder.newLine();
    _builder.append("                                ");
    _builder.append(")");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("]");
    _builder.newLine();
    _builder.append("                        ");
    _builder.append("referenced=[NonRoot(id=\"sub\")]");
    _builder.newLine();
    _builder.append("                ");
    _builder.append(")");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("]");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("referenced=[");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("ValueBased#4,");
    _builder.newLine();
    _builder.append("                ");
    _builder.append("NonRoot(id=\"sub\")");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("]");
    _builder.newLine();
    _builder.append(")");
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is(_builder.toString()));
  }

  private static String toString(final Procedure2<? super PrintTarget, ? super PrintIdProvider> printer) {
    final DefaultPrintTarget target = new DefaultPrintTarget();
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    printer.apply(target, _defaultPrintIdProvider);
    return target.toString();
  }
}
