package tools.vitruv.change.testutils.printing;

import allElementTypes.Containable;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.ValueBased;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObject($0, $1, Integer.valueOf(5));
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("5"));
  }

  @Test
  @DisplayName("prints integer values (shortened)")
  public void printIntegerShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObjectShortened($0, $1, Integer.valueOf(5));
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("5"));
  }

  @Test
  @DisplayName("prints double values")
  public void printDouble() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObject($0, $1, Double.valueOf(3.5));
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("3.5"));
  }

  @Test
  @DisplayName("prints double values (shortened)")
  public void printDoubleShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObjectShortened($0, $1, Double.valueOf(3.5));
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("3.5"));
  }

  @Test
  @DisplayName("prints boolean values")
  public void printBoolean() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObject($0, $1, Boolean.valueOf(true));
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("true"));
  }

  @Test
  @DisplayName("prints boolean values (shortened)")
  public void printBooleanShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObjectShortened($0, $1, Boolean.valueOf(true));
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("true"));
  }

  @Test
  @DisplayName("prints null")
  public void printNull() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObject($0, $1, null);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("∅"));
  }

  @Test
  @DisplayName("prints null (shortened)")
  public void printNullShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObjectShortened($0, $1, null);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("∅"));
  }

  @Test
  @DisplayName("prints string values")
  public void printString() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObject($0, $1, "test");
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("test"));
    final BiConsumer<PrintTarget, PrintIdProvider> function1 =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter()
              .printObject($0, $1, "veryverylongtestthatcouldtbeshortenedwithevenmoreletters");
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function1),
        CoreMatchers.<String>is("veryverylongtestthatcouldtbeshortenedwithevenmoreletters"));
  }

  @Test
  @DisplayName("prints string values (shortened)")
  public void printStringShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObjectShortened($0, $1, "test");
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("test"));
    final BiConsumer<PrintTarget, PrintIdProvider> function1 =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylo");
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function1), CoreMatchers.<String>is("veryverylo"));
    final BiConsumer<PrintTarget, PrintIdProvider> function2 =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter().printObjectShortened($0, $1, "veryverylon");
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function2), CoreMatchers.<String>is("veryveryl…"));
    final BiConsumer<PrintTarget, PrintIdProvider> function3 =
        (PrintTarget $0, PrintIdProvider $1) -> {
          new DefaultModelPrinter()
              .printObjectShortened(
                  $0, $1, "veryverylongtestthatcouldtbeshortenedwithevenmoreletters");
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function3), CoreMatchers.<String>is("veryveryl…"));
  }

  @Test
  @DisplayName("prints simple objects")
  public void printSimpleObject() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function1 =
              (NonRoot it) -> {
                it.setId("id");
                it.setValue("test");
              };
          function1.accept(NonRoot);
          NonRoot doubleArrow = NonRoot;
          defaultModelPrinter.printObject($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function),
        CoreMatchers.<String>is("NonRoot(\n        id=\"id\"\n        value=\"test\"\n)"));
  }

  @Test
  @DisplayName("prints simple objects shortened by id")
  public void printSimpleObjectShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function1 =
              (NonRoot it) -> {
                it.setId("id");
                it.setValue("test");
              };
          function1.accept(NonRoot);
          NonRoot doubleArrow = NonRoot;
          defaultModelPrinter.printObjectShortened($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("NonRoot(id=\"id\")"));
  }

  @Test
  @DisplayName("prints value-based objects")
  public void printValueBasedObject() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          ValueBased ValueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                it.setValue("test");
              };
          function1.accept(ValueBased);
          ValueBased doubleArrow = ValueBased;
          defaultModelPrinter.printObject($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function),
        CoreMatchers.<String>is(
            "ValueBased#1(\n"
                + "        value=\"test\"\n"
                + "        children=[]\n"
                + "        referenced=[]\n"
                + ")"));
  }

  @Test
  @DisplayName("prints simple objects shortened by fallback id")
  public void printValuedObjectShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          ValueBased ValueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                it.setValue("test");
              };
          function1.accept(ValueBased);
          ValueBased doubleArrow = ValueBased;
          defaultModelPrinter.printObjectShortened($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function), CoreMatchers.<String>is("ValueBased#1"));
  }

  @Test
  @DisplayName("prints resources")
  public void printResource() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          Resource createResource =
              new ResourceSetImpl().createResource(URI.createURI("test://foobar"));
          final Consumer<Resource> function1 =
              (Resource it) -> {
                EList<EObject> contents = it.getContents();
                NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("first");
                    };
                function2.accept(NonRoot);
                NonRoot doubleArrow = NonRoot;
                contents.add(doubleArrow);
                EList<EObject> contents1 = it.getContents();
                ValueBased ValueBased = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function3 =
                    (ValueBased it1) -> {
                      it1.setValue("second");
                    };
                function3.accept(ValueBased);
                ValueBased doubleArrow1 = ValueBased;
                contents1.add(doubleArrow1);
                EList<EObject> contents2 = it.getContents();
                NonRootObjectContainerHelper NonRootObjectContainerHelper =
                    AllElementTypesCreators.aet.NonRootObjectContainerHelper();
                final Consumer<NonRootObjectContainerHelper> function4 =
                    (NonRootObjectContainerHelper it1) -> {
                      it1.setId("third");
                    };
                function4.accept(NonRootObjectContainerHelper);
                NonRootObjectContainerHelper doubleArrow2 = NonRootObjectContainerHelper;
                contents2.add(doubleArrow2);
              };
          function1.accept(createResource);
          Resource doubleArrow = createResource;
          defaultModelPrinter.printObject($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function),
        CoreMatchers.<String>is(
            "Resource@<test://foobar>[\n"
                + "        NonRoot(\n"
                + "                id=\"first\"\n"
                + "                value=∅\n"
                + "        ),\n"
                + "        ValueBased#1(\n"
                + "                value=\"second\"\n"
                + "                children=[]\n"
                + "                referenced=[]\n"
                + "        ),\n"
                + "        NonRootObjectContainerHelper(\n"
                + "                id=\"third\"\n"
                + "                nonRootObjectsContainment=[]\n"
                + "        )\n"
                + "]"));
  }

  @Test
  @DisplayName("prints resources (shortened)")
  public void printResourceShortened() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          Resource createResource =
              new ResourceSetImpl().createResource(URI.createURI("test://foobar"));
          final Consumer<Resource> function1 =
              (Resource it) -> {
                EList<EObject> contents = it.getContents();
                NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("first");
                    };
                function2.accept(NonRoot);
                NonRoot doubleArrow = NonRoot;
                contents.add(doubleArrow);
                EList<EObject> contents1 = it.getContents();
                ValueBased ValueBased = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function3 =
                    (ValueBased it1) -> {
                      it1.setValue("second");
                    };
                function3.accept(ValueBased);
                ValueBased doubleArrow1 = ValueBased;
                contents1.add(doubleArrow1);
                EList<EObject> contents2 = it.getContents();
                NonRootObjectContainerHelper NonRootObjectContainerHelper =
                    AllElementTypesCreators.aet.NonRootObjectContainerHelper();
                final Consumer<NonRootObjectContainerHelper> function4 =
                    (NonRootObjectContainerHelper it1) -> {
                      it1.setId("third");
                    };
                function4.accept(NonRootObjectContainerHelper);
                NonRootObjectContainerHelper doubleArrow2 = NonRootObjectContainerHelper;
                contents2.add(doubleArrow2);
              };
          function1.accept(createResource);
          Resource doubleArrow = createResource;
          defaultModelPrinter.printObjectShortened($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function),
        CoreMatchers.<String>is(
            "Resource@<test://foobar>[NonRoot(id=\"first\"), ValueBased#1,"
                + " NonRootObjectContainerHelper(id=\"third\")]"));
  }

  @Test
  @DisplayName("prints references by ID")
  public void printReferenceById() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          ValueBased ValueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                EList<Containable> referenced = it.getReferenced();
                ValueBased ValueBased1 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function2 =
                    (ValueBased it1) -> {
                      it1.setValue("foo");
                    };
                function2.accept(ValueBased1);
                ValueBased doubleArrow = ValueBased1;
                referenced.add(doubleArrow);
                EList<Containable> referenced1 = it.getReferenced();
                NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("subId");
                    };
                function3.accept(NonRoot);
                NonRoot doubleArrow1 = NonRoot;
                referenced1.add(doubleArrow1);
              };
          function1.accept(ValueBased);
          ValueBased doubleArrow = ValueBased;
          defaultModelPrinter.printObject($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function),
        CoreMatchers.<String>is(
            "ValueBased#1(\n"
                + "        value=∅\n"
                + "        children=[]\n"
                + "        referenced=[\n"
                + "                ValueBased#2,\n"
                + "                NonRoot(id=\"subId\")\n"
                + "        ]\n"
                + ")"));
  }

  @Test
  @DisplayName("prints nested objects")
  public void printNested() {
    final BiConsumer<PrintTarget, PrintIdProvider> function =
        (PrintTarget $0, PrintIdProvider $1) -> {
          DefaultModelPrinter defaultModelPrinter = new DefaultModelPrinter();
          ValueBased ValueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                final ValueBased root = it;
                it.setValue("root");
                EList<Containable> children = it.getChildren();
                NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub");
                    };
                function2.accept(NonRoot);
                NonRoot doubleArrow = NonRoot;
                children.add(doubleArrow);
                EList<Containable> children1 = it.getChildren();
                ValueBased ValueBased1 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function3 =
                    (ValueBased it1) -> {
                      it1.setValue("sub");
                      EList<Containable> children2 = it1.getChildren();
                      ValueBased ValueBased2 = AllElementTypesCreators.aet.ValueBased();
                      final Consumer<ValueBased> function4 =
                          (ValueBased it2) -> {
                            it2.setValue("subsub 1");
                          };
                      function4.accept(ValueBased2);
                      ValueBased doubleArrow1 = ValueBased2;
                      children2.add(doubleArrow1);
                      EList<Containable> children3 = it1.getChildren();
                      ValueBased ValueBased3 = AllElementTypesCreators.aet.ValueBased();
                      final Consumer<ValueBased> function5 =
                          (ValueBased it2) -> {
                            it2.setValue("subsub 2");
                          };
                      function5.accept(ValueBased3);
                      ValueBased doubleArrow2 = ValueBased3;
                      children3.add(doubleArrow2);
                      EList<Containable> referenced = it1.getReferenced();
                      Containable get = root.getChildren().get(0);
                      referenced.add(get);
                    };
                function3.accept(ValueBased1);
                ValueBased doubleArrow1 = ValueBased1;
                children1.add(doubleArrow1);
                EList<Containable> referenced = it.getReferenced();
                Containable get = it.getChildren().get(1);
                Containable get1 = ((ValueBased) get).getChildren().get(1);
                referenced.add(get1);
                EList<Containable> referenced1 = it.getReferenced();
                Containable get2 = it.getChildren().get(0);
                referenced1.add(get2);
              };
          function1.accept(ValueBased);
          ValueBased doubleArrow = ValueBased;
          defaultModelPrinter.printObject($0, $1, doubleArrow);
        };
    MatcherAssert.<String>assertThat(
        DefaultModelPrinterTest.toString(function),
        CoreMatchers.<String>is(
            "ValueBased#1(\n"
                + "        value=\"root\"\n"
                + "        children=[\n"
                + "                NonRoot(\n"
                + "                        id=\"sub\"\n"
                + "                        value=∅\n"
                + "                ),\n"
                + "                ValueBased#2(\n"
                + "                        value=\"sub\"\n"
                + "                        children=[\n"
                + "                                ValueBased#3(\n"
                + "                                        value=\"subsub 1\"\n"
                + "                                        children=[]\n"
                + "                                        referenced=[]\n"
                + "                                ),\n"
                + "                                ValueBased#4(\n"
                + "                                        value=\"subsub 2\"\n"
                + "                                        children=[]\n"
                + "                                        referenced=[]\n"
                + "                                )\n"
                + "                        ]\n"
                + "                        referenced=[NonRoot(id=\"sub\")]\n"
                + "                )\n"
                + "        ]\n"
                + "        referenced=[\n"
                + "                ValueBased#4,\n"
                + "                NonRoot(id=\"sub\")\n"
                + "        ]\n"
                + ")"));
  }

  private static String toString(final BiConsumer<PrintTarget, ? super PrintIdProvider> printer) {
    final DefaultPrintTarget target = new DefaultPrintTarget();
    DefaultPrintIdProvider defaultPrintIdProvider = new DefaultPrintIdProvider();
    printer.accept(target, defaultPrintIdProvider);
    return target.toString().replace("\r\n", "\n");
  }
}
