package tools.vitruv.change.testutils.printing;

import allElementTypes.Containable;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.ValueBased;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

@ExtendWith(RegisterMetamodelsInStandalone.class)
public class DefaultModelPrinterTest {
  @Test
  @DisplayName("prints integer values")
  public void printInteger() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, Integer.valueOf(5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("5"));
  }

  @Test
  @DisplayName("prints integer values (shortened)")
  public void printIntegerShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, Integer.valueOf(5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("5"));
  }

  @Test
  @DisplayName("prints double values")
  public void printDouble() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, Double.valueOf(3.5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("3.5"));
  }

  @Test
  @DisplayName("prints double values (shortened)")
  public void printDoubleShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, Double.valueOf(3.5));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("3.5"));
  }

  @Test
  @DisplayName("prints boolean values")
  public void printBoolean() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, Boolean.valueOf(true));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("true"));
  }

  @Test
  @DisplayName("prints boolean values (shortened)")
  public void printBooleanShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, Boolean.valueOf(true));
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("true"));
  }

  @Test
  @DisplayName("prints null")
  public void printNull() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, null);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("∅"));
  }

  @Test
  @DisplayName("prints null (shortened)")
  public void printNullShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, null);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("∅"));
  }

  @Test
  @DisplayName("prints string values")
  public void printString() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, "test");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("test"));
    final BiConsumer<PrintTarget, PrintIdProvider> _function_1 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObject($0, $1, "veryverylongtestthatcouldtbeshortenedwithevenmoreletters");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_1), CoreMatchers.<String>is("veryverylongtestthatcouldtbeshortenedwithevenmoreletters"));
  }

  @Test
  @DisplayName("prints string values (shortened)")
  public void printStringShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "test");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("test"));
    final BiConsumer<PrintTarget, PrintIdProvider> _function_1 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylo");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_1), CoreMatchers.<String>is("veryverylo"));
    final BiConsumer<PrintTarget, PrintIdProvider> _function_2 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylon");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_2), CoreMatchers.<String>is("veryveryl…"));
    final BiConsumer<PrintTarget, PrintIdProvider> _function_3 = (PrintTarget $0, PrintIdProvider $1) -> {
      new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylongtestthatcouldtbeshortenedwithevenmoreletters");
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function_3), CoreMatchers.<String>is("veryveryl…"));
  }

  @Test
  @DisplayName("prints simple objects")
  public void printSimpleObject() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Consumer<NonRoot> _function_1 = (NonRoot it) -> {
        it.setId("id");
        it.setValue("test");
      };
      _function_1.accept(_NonRoot);
      NonRoot _doubleArrow = _NonRoot;
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("NonRoot(\n        id=\"id\"\n        value=\"test\"\n)"));
  }

  @Test
  @DisplayName("prints simple objects shortened by id")
  public void printSimpleObjectShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Consumer<NonRoot> _function_1 = (NonRoot it) -> {
        it.setId("id");
        it.setValue("test");
      };
      _function_1.accept(_NonRoot);
      NonRoot _doubleArrow = _NonRoot;
      _defaultModelPrinter.printObjectShortened($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("NonRoot(id=\"id\")"));
  }

  @Test
  @DisplayName("prints value-based objects")
  public void printValueBasedObject() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Consumer<ValueBased> _function_1 = (ValueBased it) -> {
        it.setValue("test");
      };
      _function_1.accept(_ValueBased);
      ValueBased _doubleArrow = _ValueBased;
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("ValueBased#1(\n        value=\"test\"\n        children=[]\n        referenced=[]\n)"));
  }

  @Test
  @DisplayName("prints simple objects shortened by fallback id")
  public void printValuedObjectShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Consumer<ValueBased> _function_1 = (ValueBased it) -> {
        it.setValue("test");
      };
      _function_1.accept(_ValueBased);
      ValueBased _doubleArrow = _ValueBased;
      _defaultModelPrinter.printObjectShortened($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("ValueBased#1"));
  }

  @Test
  @DisplayName("prints resources")
  public void printResource() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      Resource _createResource = new ResourceSetImpl().createResource(URI.createURI("test://foobar"));
      final Consumer<Resource> _function_1 = (Resource it) -> {
        EList<EObject> _contents = it.getContents();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Consumer<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("first");
        };
        _function_2.accept(_NonRoot);
        NonRoot _doubleArrow = _NonRoot;
        _contents.add(_doubleArrow);
        EList<EObject> _contents_1 = it.getContents();
        ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
        final Consumer<ValueBased> _function_3 = (ValueBased it_1) -> {
          it_1.setValue("second");
        };
        _function_3.accept(_ValueBased);
        ValueBased _doubleArrow_1 = _ValueBased;
        _contents_1.add(_doubleArrow_1);
        EList<EObject> _contents_2 = it.getContents();
        NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
        final Consumer<NonRootObjectContainerHelper> _function_4 = (NonRootObjectContainerHelper it_1) -> {
          it_1.setId("third");
        };
        _function_4.accept(_NonRootObjectContainerHelper);
        NonRootObjectContainerHelper _doubleArrow_2 = _NonRootObjectContainerHelper;
        _contents_2.add(_doubleArrow_2);
      };
      _function_1.accept(_createResource);
      Resource _doubleArrow = _createResource;
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("Resource@<test://foobar>[\n        NonRoot(\n                id=\"first\"\n                value=∅\n        ),\n        ValueBased#1(\n                value=\"second\"\n                children=[]\n                referenced=[]\n        ),\n        NonRootObjectContainerHelper(\n                id=\"third\"\n                nonRootObjectsContainment=[]\n        )\n]"));
  }

  @Test
  @DisplayName("prints resources (shortened)")
  public void printResourceShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      Resource _createResource = new ResourceSetImpl().createResource(URI.createURI("test://foobar"));
      final Consumer<Resource> _function_1 = (Resource it) -> {
        EList<EObject> _contents = it.getContents();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Consumer<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("first");
        };
        _function_2.accept(_NonRoot);
        NonRoot _doubleArrow = _NonRoot;
        _contents.add(_doubleArrow);
        EList<EObject> _contents_1 = it.getContents();
        ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
        final Consumer<ValueBased> _function_3 = (ValueBased it_1) -> {
          it_1.setValue("second");
        };
        _function_3.accept(_ValueBased);
        ValueBased _doubleArrow_1 = _ValueBased;
        _contents_1.add(_doubleArrow_1);
        EList<EObject> _contents_2 = it.getContents();
        NonRootObjectContainerHelper _NonRootObjectContainerHelper = AllElementTypesCreators.aet.NonRootObjectContainerHelper();
        final Consumer<NonRootObjectContainerHelper> _function_4 = (NonRootObjectContainerHelper it_1) -> {
          it_1.setId("third");
        };
        _function_4.accept(_NonRootObjectContainerHelper);
        NonRootObjectContainerHelper _doubleArrow_2 = _NonRootObjectContainerHelper;
        _contents_2.add(_doubleArrow_2);
      };
      _function_1.accept(_createResource);
      Resource _doubleArrow = _createResource;
      _defaultModelPrinter.printObjectShortened($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("Resource@<test://foobar>[NonRoot(id=\"first\"), ValueBased#1, NonRootObjectContainerHelper(id=\"third\")]"));
  }

  @Test
  @DisplayName("prints references by ID")
  public void printReferenceById() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Consumer<ValueBased> _function_1 = (ValueBased it) -> {
        EList<Containable> _referenced = it.getReferenced();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        final Consumer<ValueBased> _function_2 = (ValueBased it_1) -> {
          it_1.setValue("foo");
        };
        _function_2.accept(_ValueBased_1);
        ValueBased _doubleArrow = _ValueBased_1;
        _referenced.add(_doubleArrow);
        EList<Containable> _referenced_1 = it.getReferenced();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Consumer<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("subId");
        };
        _function_3.accept(_NonRoot);
        NonRoot _doubleArrow_1 = _NonRoot;
        _referenced_1.add(_doubleArrow_1);
      };
      _function_1.accept(_ValueBased);
      ValueBased _doubleArrow = _ValueBased;
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("ValueBased#1(\n        value=∅\n        children=[]\n        referenced=[\n                ValueBased#2,\n                NonRoot(id=\"subId\")\n        ]\n)"));
  }

  @Test
  @DisplayName("prints nested objects")
  public void printNested() {
    final BiConsumer<PrintTarget, PrintIdProvider> _function = (PrintTarget $0, PrintIdProvider $1) -> {
      DefaultModelPrinter _defaultModelPrinter = new DefaultModelPrinter();
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Consumer<ValueBased> _function_1 = (ValueBased it) -> {
        final ValueBased root = it;
        it.setValue("root");
        EList<Containable> _children = it.getChildren();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Consumer<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub");
        };
        _function_2.accept(_NonRoot);
        NonRoot _doubleArrow = _NonRoot;
        _children.add(_doubleArrow);
        EList<Containable> _children_1 = it.getChildren();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        final Consumer<ValueBased> _function_3 = (ValueBased it_1) -> {
          it_1.setValue("sub");
          EList<Containable> _children_2 = it_1.getChildren();
          ValueBased _ValueBased_2 = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> _function_4 = (ValueBased it_2) -> {
            it_2.setValue("subsub 1");
          };
          _function_4.accept(_ValueBased_2);
          ValueBased _doubleArrow_1 = _ValueBased_2;
          _children_2.add(_doubleArrow_1);
          EList<Containable> _children_3 = it_1.getChildren();
          ValueBased _ValueBased_3 = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> _function_5 = (ValueBased it_2) -> {
            it_2.setValue("subsub 2");
          };
          _function_5.accept(_ValueBased_3);
          ValueBased _doubleArrow_2 = _ValueBased_3;
          _children_3.add(_doubleArrow_2);
          EList<Containable> _referenced = it_1.getReferenced();
          Containable _get = root.getChildren().get(0);
          _referenced.add(_get);
        };
        _function_3.accept(_ValueBased_1);
        ValueBased _doubleArrow_1 = _ValueBased_1;
        _children_1.add(_doubleArrow_1);
        EList<Containable> _referenced = it.getReferenced();
        Containable _get = it.getChildren().get(1);
        Containable _get_1 = ((ValueBased) _get).getChildren().get(1);
        _referenced.add(_get_1);
        EList<Containable> _referenced_1 = it.getReferenced();
        Containable _get_2 = it.getChildren().get(0);
        _referenced_1.add(_get_2);
      };
      _function_1.accept(_ValueBased);
      ValueBased _doubleArrow = _ValueBased;
      _defaultModelPrinter.printObject($0, $1, _doubleArrow);
    };
    MatcherAssert.<String>assertThat(DefaultModelPrinterTest.toString(_function), CoreMatchers.<String>is("ValueBased#1(\n        value=\"root\"\n        children=[\n                NonRoot(\n                        id=\"sub\"\n                        value=∅\n                ),\n                ValueBased#2(\n                        value=\"sub\"\n                        children=[\n                                ValueBased#3(\n                                        value=\"subsub 1\"\n                                        children=[]\n                                        referenced=[]\n                                ),\n                                ValueBased#4(\n                                        value=\"subsub 2\"\n                                        children=[]\n                                        referenced=[]\n                                )\n                        ]\n                        referenced=[NonRoot(id=\"sub\")]\n                )\n        ]\n        referenced=[\n                ValueBased#4,\n                NonRoot(id=\"sub\")\n        ]\n)"));
  }

  private static String toString(final BiConsumer<PrintTarget, ? super PrintIdProvider> printer) {
    final DefaultPrintTarget target = new DefaultPrintTarget();
    DefaultPrintIdProvider _defaultPrintIdProvider = new DefaultPrintIdProvider();
    printer.accept(target, _defaultPrintIdProvider);
    return target.toString().replace("\r\n", "\n");
  }
}
