package tools.vitruv.change.testutils.matchers;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Containable;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import allElementTypes.ValueBased;
import allElementTypes.impl.NonRootImpl;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pcm_mockup.PInterface;
import pcm_mockup.PMethod;
import pcm_mockup.Repository;
import tools.vitruv.change.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.change.testutils.TestLogging;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;
import tools.vitruv.change.testutils.metamodels.PcmMockupCreators;
import tools.vitruv.change.testutils.printing.ModelPrinterChange;
import tools.vitruv.change.testutils.printing.UnsetFeaturesHidingModelPrinter;
import tools.vitruv.change.testutils.printing.UseModelPrinter;

@UseModelPrinter(UnsetFeaturesHidingModelPrinter.class)
@ExtendWith({RegisterMetamodelsInStandalone.class, ModelPrinterChange.class, TestLogging.class})
public class ModelDeepEqualityMatcherTest {
  private static class ValueBasedNonRoot extends NonRootImpl {
    @Override
    public boolean equals(final Object object) {
      boolean xifexpression = false;
      if ((object == this)) {
        xifexpression = true;
      } else {
        boolean xifexpression1 = false;
        if ((object == null)) {
          xifexpression1 = false;
        } else {
          boolean xifexpression2 = false;
          if ((object instanceof ModelDeepEqualityMatcherTest.ValueBasedNonRoot)) {
            xifexpression2 =
                Objects.equals(
                    this.value, ((ModelDeepEqualityMatcherTest.ValueBasedNonRoot) object).value);
          } else {
            xifexpression2 = false;
          }
          xifexpression1 = xifexpression2;
        }
        xifexpression = xifexpression1;
      }
      return xifexpression;
    }

    @Override
    public int hashCode() {
      int xifexpression = (int) 0;
      if ((this.value == null)) {
        xifexpression = 0;
      } else {
        xifexpression = this.value.hashCode();
      }
      return xifexpression;
    }
  }

  private static class PcmInterfaceNameEquality implements EqualityStrategy {
    @Override
    public EqualityStrategy.Result compare(final EObject left, final EObject right) {
      if ((left instanceof PInterface)) {
        if ((right instanceof PInterface)) {
          EqualityStrategy.Result xifexpression = null;
          String name = ((PInterface) left).getName();
          String name1 = ((PInterface) right).getName();
          boolean equals = Objects.equals(name, name1);
          if (equals) {
            xifexpression = EqualityStrategy.Result.EQUAL;
          } else {
            xifexpression = EqualityStrategy.Result.UNEQUAL;
          }
          return xifexpression;
        }
      }
      return EqualityStrategy.Result.UNKNOWN;
    }

    @Override
    public void describeTo(final StringBuilder builder) {
      builder.append("compared referenced PCM interfaces using their name");
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("equalObjects")
  @DisplayName("accepts equal objects as equal")
  public void recognizesEquals(final String name, final EObject left, final EObject right) {
    final Executable function =
        () -> {
          MatcherAssert.<EObject>assertThat(left, ModelMatchers.<EObject>equalsDeeply(right));
        };
    Assertions.assertDoesNotThrow(function);
  }

  public static Stream<Arguments> equalObjects() {
    Root root = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function =
        (Root it) -> {
          it.setId("id");
        };
    function.accept(root);
    Root doubleArrow = root;
    Root root1 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function1 =
        (Root it) -> {
          it.setId("id");
        };
    function1.accept(root1);
    Root doubleArrow1 = root1;
    Root root2 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function2 =
        (Root it) -> {
          it.setId("root");
          EList<Integer> multiValuedEAttribute = it.getMultiValuedEAttribute();
          Iterables.<Integer>addAll(
              multiValuedEAttribute,
              Collections.<Integer>unmodifiableList(
                  List.of(
                      Integer.valueOf(1),
                      Integer.valueOf(2),
                      Integer.valueOf(42),
                      Integer.valueOf(8))));
        };
    function2.accept(root2);
    Root doubleArrow2 = root2;
    Root root3 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function3 =
        (Root it) -> {
          it.setId("root");
          EList<Integer> multiValuedEAttribute = it.getMultiValuedEAttribute();
          Iterables.<Integer>addAll(
              multiValuedEAttribute,
              Collections.<Integer>unmodifiableList(
                  List.of(
                      Integer.valueOf(1),
                      Integer.valueOf(2),
                      Integer.valueOf(42),
                      Integer.valueOf(8))));
        };
    function3.accept(root3);
    Root doubleArrow3 = root3;
    Root root4 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function4 =
        (Root it) -> {
          it.setId("root");
          EList<Integer> multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
          Iterables.<Integer>addAll(
              multiValuedUnorderedEAttribute,
              Collections.<Integer>unmodifiableList(
                  List.of(
                      Integer.valueOf(1),
                      Integer.valueOf(2),
                      Integer.valueOf(42),
                      Integer.valueOf(8))));
        };
    function4.accept(root4);
    Root doubleArrow4 = root4;
    Root root5 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function5 =
        (Root it) -> {
          it.setId("root");
          EList<Integer> multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
          Iterables.<Integer>addAll(
              multiValuedUnorderedEAttribute,
              Collections.<Integer>unmodifiableList(
                  List.of(
                      Integer.valueOf(1),
                      Integer.valueOf(2),
                      Integer.valueOf(42),
                      Integer.valueOf(8))));
        };
    function5.accept(root5);
    Root doubleArrow5 = root5;
    Root root6 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function6 =
        (Root it) -> {
          it.setId("root");
          EList<Integer> multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
          Iterables.<Integer>addAll(
              multiValuedUnorderedEAttribute,
              Collections.<Integer>unmodifiableList(
                  List.of(
                      Integer.valueOf(1),
                      Integer.valueOf(2),
                      Integer.valueOf(42),
                      Integer.valueOf(8))));
        };
    function6.accept(root6);
    Root doubleArrow6 = root6;
    Root root7 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function7 =
        (Root it) -> {
          it.setId("root");
          EList<Integer> multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
          Iterables.<Integer>addAll(
              multiValuedUnorderedEAttribute,
              Collections.<Integer>unmodifiableList(
                  List.of(
                      Integer.valueOf(8),
                      Integer.valueOf(42),
                      Integer.valueOf(2),
                      Integer.valueOf(1))));
        };
    function7.accept(root7);
    Root doubleArrow7 = root7;
    Root root8 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function8 =
        (Root it) -> {
          it.setId("root");
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function9 =
              (NonRoot it1) -> {
                it1.setId("sub");
              };
          function9.accept(inDifferentRoot);
          NonRoot doubleArrow8 = inDifferentRoot;
          it.setSingleValuedNonContainmentEReference(doubleArrow8);
        };
    function8.accept(root8);
    Root doubleArrow8 = root8;
    Root root9 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function9 =
        (Root it) -> {
          it.setId("root");
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function10 =
              (NonRoot it1) -> {
                it1.setId("sub");
              };
          function10.accept(inDifferentRoot);
          NonRoot doubleArrow9 = inDifferentRoot;
          it.setSingleValuedNonContainmentEReference(doubleArrow9);
        };
    function9.accept(root9);
    Root doubleArrow9 = root9;
    Root root10 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function10 =
        (Root it) -> {
          it.setId("root");
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function11 =
              (NonRoot it1) -> {
                it1.setId("sub");
                it1.setValue("different");
              };
          function11.accept(inDifferentRoot);
          NonRoot doubleArrow10 = inDifferentRoot;
          it.setSingleValuedNonContainmentEReference(doubleArrow10);
        };
    function10.accept(root10);
    Root doubleArrow10 = root10;
    Root root11 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function11 =
        (Root it) -> {
          it.setId("root");
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function12 =
              (NonRoot it1) -> {
                it1.setId("sub");
                it1.setValue("test");
              };
          function12.accept(inDifferentRoot);
          NonRoot doubleArrow11 = inDifferentRoot;
          it.setSingleValuedNonContainmentEReference(doubleArrow11);
        };
    function11.accept(root11);
    Root doubleArrow11 = root11;
    Root root12 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function12 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedNonContainmentEReference =
              it.getMultiValuedNonContainmentEReference();
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function13 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function13.accept(inDifferentRoot);
          NonRoot doubleArrow12 = inDifferentRoot;
          NonRoot inDifferentRoot1 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function14 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function14.accept(inDifferentRoot1);
          NonRoot doubleArrow13 = inDifferentRoot1;
          NonRoot inDifferentRoot2 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function15 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function15.accept(inDifferentRoot2);
          NonRoot doubleArrow14 = inDifferentRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow12, doubleArrow13, doubleArrow14)));
        };
    function12.accept(root12);
    Root doubleArrow12 = root12;
    Root root13 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function13 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedNonContainmentEReference =
              it.getMultiValuedNonContainmentEReference();
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function14 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function14.accept(inDifferentRoot);
          NonRoot doubleArrow13 = inDifferentRoot;
          NonRoot inDifferentRoot1 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function15 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function15.accept(inDifferentRoot1);
          NonRoot doubleArrow14 = inDifferentRoot1;
          NonRoot inDifferentRoot2 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function16 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function16.accept(inDifferentRoot2);
          NonRoot doubleArrow15 = inDifferentRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow13, doubleArrow14, doubleArrow15)));
        };
    function13.accept(root13);
    Root doubleArrow13 = root13;
    Root root14 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function14 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function15 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function15.accept(inDifferentRoot);
          NonRoot doubleArrow14 = inDifferentRoot;
          NonRoot inDifferentRoot1 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function16 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function16.accept(inDifferentRoot1);
          NonRoot doubleArrow15 = inDifferentRoot1;
          NonRoot inDifferentRoot2 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function17 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function17.accept(inDifferentRoot2);
          NonRoot doubleArrow16 = inDifferentRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow14, doubleArrow15, doubleArrow16)));
        };
    function14.accept(root14);
    Root doubleArrow14 = root14;
    Root root15 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function15 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function16 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function16.accept(inDifferentRoot);
          NonRoot doubleArrow15 = inDifferentRoot;
          NonRoot inDifferentRoot1 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function17 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function17.accept(inDifferentRoot1);
          NonRoot doubleArrow16 = inDifferentRoot1;
          NonRoot inDifferentRoot2 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function18 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function18.accept(inDifferentRoot2);
          NonRoot doubleArrow17 = inDifferentRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow15, doubleArrow16, doubleArrow17)));
        };
    function15.accept(root15);
    Root doubleArrow15 = root15;
    Root root16 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function16 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function17 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function17.accept(inDifferentRoot);
          NonRoot doubleArrow16 = inDifferentRoot;
          NonRoot inDifferentRoot1 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function18 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function18.accept(inDifferentRoot1);
          NonRoot doubleArrow17 = inDifferentRoot1;
          NonRoot inDifferentRoot2 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function19 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function19.accept(inDifferentRoot2);
          NonRoot doubleArrow18 = inDifferentRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow16, doubleArrow17, doubleArrow18)));
        };
    function16.accept(root16);
    Root doubleArrow16 = root16;
    Root root17 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function17 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          NonRoot inDifferentRoot =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function18 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function18.accept(inDifferentRoot);
          NonRoot doubleArrow17 = inDifferentRoot;
          NonRoot inDifferentRoot1 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function19 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function19.accept(inDifferentRoot1);
          NonRoot doubleArrow18 = inDifferentRoot1;
          NonRoot inDifferentRoot2 =
              ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Consumer<NonRoot> function20 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function20.accept(inDifferentRoot2);
          NonRoot doubleArrow19 = inDifferentRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow17, doubleArrow18, doubleArrow19)));
        };
    function17.accept(root17);
    Root doubleArrow17 = root17;
    Root root18 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function18 =
        (Root it) -> {
          it.setId("root");
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function19 =
              (NonRoot it1) -> {
                it1.setId("sub");
              };
          function19.accept(nonRootInSameRoot);
          NonRoot doubleArrow18 = nonRootInSameRoot;
          it.setSingleValuedNonContainmentEReference(doubleArrow18);
        };
    function18.accept(root18);
    Root doubleArrow18 = root18;
    Root root19 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function19 =
        (Root it) -> {
          it.setId("root");
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function20 =
              (NonRoot it1) -> {
                it1.setId("sub");
              };
          function20.accept(nonRootInSameRoot);
          NonRoot doubleArrow19 = nonRootInSameRoot;
          it.setSingleValuedNonContainmentEReference(doubleArrow19);
        };
    function19.accept(root19);
    Root doubleArrow19 = root19;
    Root root20 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function20 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedNonContainmentEReference =
              it.getMultiValuedNonContainmentEReference();
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function21 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function21.accept(nonRootInSameRoot);
          NonRoot doubleArrow20 = nonRootInSameRoot;
          NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function22 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function22.accept(nonRootInSameRoot1);
          NonRoot doubleArrow21 = nonRootInSameRoot1;
          NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function23 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function23.accept(nonRootInSameRoot2);
          NonRoot doubleArrow22 = nonRootInSameRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow20, doubleArrow21, doubleArrow22)));
        };
    function20.accept(root20);
    Root doubleArrow20 = root20;
    Root root21 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function21 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedNonContainmentEReference =
              it.getMultiValuedNonContainmentEReference();
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function22 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function22.accept(nonRootInSameRoot);
          NonRoot doubleArrow21 = nonRootInSameRoot;
          NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function23 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function23.accept(nonRootInSameRoot1);
          NonRoot doubleArrow22 = nonRootInSameRoot1;
          NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function24 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function24.accept(nonRootInSameRoot2);
          NonRoot doubleArrow23 = nonRootInSameRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow21, doubleArrow22, doubleArrow23)));
        };
    function21.accept(root21);
    Root doubleArrow21 = root21;
    Root root22 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function22 =
        (Root it) -> {
          it.setId("root");
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function23 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function23.accept(nonRootInSameRoot);
          NonRoot doubleArrow22 = nonRootInSameRoot;
          NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function24 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function24.accept(nonRootInSameRoot1);
          NonRoot doubleArrow23 = nonRootInSameRoot1;
          NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function25 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function25.accept(nonRootInSameRoot2);
          NonRoot doubleArrow24 = nonRootInSameRoot2;
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow22, doubleArrow23, doubleArrow24)));
        };
    function22.accept(root22);
    Root doubleArrow22 = root22;
    Root root23 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function23 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function24 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function24.accept(nonRootInSameRoot);
          NonRoot doubleArrow23 = nonRootInSameRoot;
          NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function25 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function25.accept(nonRootInSameRoot1);
          NonRoot doubleArrow24 = nonRootInSameRoot1;
          NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function26 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function26.accept(nonRootInSameRoot2);
          NonRoot doubleArrow25 = nonRootInSameRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow23, doubleArrow24, doubleArrow25)));
        };
    function23.accept(root23);
    Root doubleArrow23 = root23;
    Root root24 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function24 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function25 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function25.accept(nonRootInSameRoot);
          NonRoot doubleArrow24 = nonRootInSameRoot;
          NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function26 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function26.accept(nonRootInSameRoot1);
          NonRoot doubleArrow25 = nonRootInSameRoot1;
          NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function27 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function27.accept(nonRootInSameRoot2);
          NonRoot doubleArrow26 = nonRootInSameRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow24, doubleArrow25, doubleArrow26)));
        };
    function24.accept(root24);
    Root doubleArrow24 = root24;
    Root root25 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function25 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
              it.getMultiValuedUnorderedNonContainmentEReference();
          NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function26 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function26.accept(nonRootInSameRoot);
          NonRoot doubleArrow25 = nonRootInSameRoot;
          NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function27 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function27.accept(nonRootInSameRoot1);
          NonRoot doubleArrow26 = nonRootInSameRoot1;
          NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
          final Consumer<NonRoot> function28 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function28.accept(nonRootInSameRoot2);
          NonRoot doubleArrow27 = nonRootInSameRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedNonContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow25, doubleArrow26, doubleArrow27)));
        };
    function25.accept(root25);
    Root doubleArrow25 = root25;
    Root root26 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function26 =
        (Root it) -> {
          it.setId("root");
          NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function27 =
              (NonRoot it1) -> {
                it1.setId("sub");
              };
          function27.accept(NonRoot);
          NonRoot doubleArrow26 = NonRoot;
          it.setSingleValuedContainmentEReference(doubleArrow26);
        };
    function26.accept(root26);
    Root doubleArrow26 = root26;
    Root root27 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function27 =
        (Root it) -> {
          it.setId("root");
          NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function28 =
              (NonRoot it1) -> {
                it1.setId("sub");
              };
          function28.accept(NonRoot);
          NonRoot doubleArrow27 = NonRoot;
          it.setSingleValuedContainmentEReference(doubleArrow27);
        };
    function27.accept(root27);
    Root doubleArrow27 = root27;
    Root root28 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function28 =
        (Root it) -> {
          it.setId("root");
          NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function29 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function29.accept(nonRoot);
          NonRoot doubleArrow28 = nonRoot;
          NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function30 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function30.accept(nonRoot1);
          NonRoot doubleArrow29 = nonRoot1;
          NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function31 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function31.accept(nonRoot2);
          NonRoot doubleArrow30 = nonRoot2;
          EList<NonRoot> multiValuedContainmentEReference =
              it.getMultiValuedContainmentEReference();
          Iterables.<NonRoot>addAll(
              multiValuedContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow28, doubleArrow29, doubleArrow30)));
        };
    function28.accept(root28);
    Root doubleArrow28 = root28;
    Root root29 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function29 =
        (Root it) -> {
          it.setId("root");
          EList<NonRoot> multiValuedContainmentEReference =
              it.getMultiValuedContainmentEReference();
          NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function30 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function30.accept(nonRoot);
          NonRoot doubleArrow29 = nonRoot;
          NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function31 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function31.accept(nonRoot1);
          NonRoot doubleArrow30 = nonRoot1;
          NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function32 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function32.accept(nonRoot2);
          NonRoot doubleArrow31 = nonRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow29, doubleArrow30, doubleArrow31)));
        };
    function29.accept(root29);
    Root doubleArrow29 = root29;
    Root root30 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function30 =
        (Root it) -> {
          it.setId("root");
          EList<allElementTypes.NonRoot> multiValuedUnorderedContainmentEReference =
              it.getMultiValuedUnorderedContainmentEReference();
          NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function31 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function31.accept(nonRoot);
          NonRoot doubleArrow30 = nonRoot;
          NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function32 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function32.accept(nonRoot1);
          NonRoot doubleArrow31 = nonRoot1;
          NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function33 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function33.accept(nonRoot2);
          NonRoot doubleArrow32 = nonRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow30, doubleArrow31, doubleArrow32)));
        };
    function30.accept(root30);
    Root doubleArrow30 = root30;
    Root root31 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function31 =
        (Root it) -> {
          it.setId("root");
          EList<allElementTypes.NonRoot> multiValuedUnorderedContainmentEReference =
              it.getMultiValuedUnorderedContainmentEReference();
          NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function32 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function32.accept(nonRoot);
          NonRoot doubleArrow31 = nonRoot;
          NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function33 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function33.accept(nonRoot1);
          NonRoot doubleArrow32 = nonRoot1;
          NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function34 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function34.accept(nonRoot2);
          NonRoot doubleArrow33 = nonRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow31, doubleArrow32, doubleArrow33)));
        };
    function31.accept(root31);
    Root doubleArrow31 = root31;
    Root root32 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function32 =
        (Root it) -> {
          it.setId("root");
          EList<allElementTypes.NonRoot> multiValuedUnorderedContainmentEReference =
              it.getMultiValuedUnorderedContainmentEReference();
          NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function33 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function33.accept(nonRoot);
          NonRoot doubleArrow32 = nonRoot;
          NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function34 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function34.accept(nonRoot1);
          NonRoot doubleArrow33 = nonRoot1;
          NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function35 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function35.accept(nonRoot2);
          NonRoot doubleArrow34 = nonRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow32, doubleArrow33, doubleArrow34)));
        };
    function32.accept(root32);
    Root doubleArrow32 = root32;
    Root root33 = AllElementTypesCreators.aet.Root();
    final Consumer<Root> function33 =
        (Root it) -> {
          it.setId("root");
          EList<allElementTypes.NonRoot> multiValuedUnorderedContainmentEReference =
              it.getMultiValuedUnorderedContainmentEReference();
          NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function34 =
              (NonRoot it1) -> {
                it1.setId("sub3");
              };
          function34.accept(nonRoot);
          NonRoot doubleArrow33 = nonRoot;
          NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function35 =
              (NonRoot it1) -> {
                it1.setId("sub1");
              };
          function35.accept(nonRoot1);
          NonRoot doubleArrow34 = nonRoot1;
          NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
          final Consumer<NonRoot> function36 =
              (NonRoot it1) -> {
                it1.setId("sub2");
              };
          function36.accept(nonRoot2);
          NonRoot doubleArrow35 = nonRoot2;
          Iterables.<NonRoot>addAll(
              multiValuedUnorderedContainmentEReference,
              Collections.<NonRoot>unmodifiableList(
                  List.of(doubleArrow33, doubleArrow34, doubleArrow35)));
        };
    function33.accept(root33);
    Root doubleArrow33 = root33;
    return Stream.<Arguments>of(
        Arguments.of("simple object", doubleArrow, doubleArrow1),
        Arguments.of("with ordered multi-valued attribute", doubleArrow2, doubleArrow3),
        Arguments.of(
            "with unordered multi-valued attribute (same order)", doubleArrow4, doubleArrow5),
        Arguments.of(
            "with unordered multi-valued attribute (different order)", doubleArrow6, doubleArrow7),
        Arguments.of("with single-valued reference to the outside", doubleArrow8, doubleArrow9),
        Arguments.of(
            "with single-valued reference to the outside (same id but different content)",
            doubleArrow10,
            doubleArrow11),
        Arguments.of(
            "with ordered multi-valued reference to the outside", doubleArrow12, doubleArrow13),
        Arguments.of(
            "with unordered multi-valued reference to the outside (same order)",
            doubleArrow14,
            doubleArrow15),
        Arguments.of(
            "with unordered multi-valued reference to the outside (different order)",
            doubleArrow16,
            doubleArrow17),
        Arguments.of("with single-valued reference to the inside", doubleArrow18, doubleArrow19),
        Arguments.of(
            "with ordered multi-valued reference to the inside", doubleArrow20, doubleArrow21),
        Arguments.of(
            "with unordered multi-valued reference to the inside (same order)",
            doubleArrow22,
            doubleArrow23),
        Arguments.of(
            "with unordered multi-valued reference to the inside (different order)",
            doubleArrow24,
            doubleArrow25),
        Arguments.of("with single-valued containment reference", doubleArrow26, doubleArrow27),
        Arguments.of(
            "with ordered multi-valued containment reference", doubleArrow28, doubleArrow29),
        Arguments.of(
            "with unordered multi-valued containment reference (same order)",
            doubleArrow30,
            doubleArrow31),
        Arguments.of(
            "with unordered multi-valued containment reference (different order)",
            doubleArrow32,
            doubleArrow33));
  }

  @Test
  @DisplayName("recognizes a changed single-valued attribute")
  public void changedAttribute() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("different");
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("test");
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("recognizes a different order in a multi-valued attribute")
  public void attributeListReorded() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<Integer> multiValuedEAttribute = it.getMultiValuedEAttribute();
                Iterables.<Integer>addAll(
                    multiValuedEAttribute,
                    Collections.<Integer>unmodifiableList(
                        List.of(
                            Integer.valueOf(1),
                            Integer.valueOf(2),
                            Integer.valueOf(42),
                            Integer.valueOf(8))));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<Integer> multiValuedEAttribute = it.getMultiValuedEAttribute();
                Iterables.<Integer>addAll(
                    multiValuedEAttribute,
                    Collections.<Integer>unmodifiableList(
                        List.of(
                            Integer.valueOf(1),
                            Integer.valueOf(8),
                            Integer.valueOf(2),
                            Integer.valueOf(42))));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(), CoreMatchers.containsString("different position for: 8"));
  }

  @Test
  @DisplayName("recognizes a changed single-valued reference to the outside")
  public void changedReferenceToOutside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("different");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("wrong value: NonRoot(id=\"different\")"));
  }

  @Test
  @DisplayName("recognizes a changed single-valued reference to the inside")
  public void changedReferenceToInside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("different");
                    };
                function2.accept(nonRootInSameRoot);
                NonRoot doubleArrow = nonRootInSameRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub");
                    };
                function3.accept(nonRootInSameRoot);
                NonRoot doubleArrow1 = nonRootInSameRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("wrong value: NonRoot(id=\"different\")"));
  }

  @Test
  @DisplayName("recognizes a reordered multi-valued reference to the outside")
  public void reorderedReferenceToOutside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(inDifferentRoot1);
                NonRoot doubleArrow1 = inDifferentRoot1;
                NonRoot inDifferentRoot2 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function4.accept(inDifferentRoot2);
                NonRoot doubleArrow2 = inDifferentRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(inDifferentRoot1);
                NonRoot doubleArrow2 = inDifferentRoot1;
                NonRoot inDifferentRoot2 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(inDifferentRoot2);
                NonRoot doubleArrow3 = inDifferentRoot2;
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("different position for: NonRoot(id=\"sub1\")"));
  }

  @Test
  @DisplayName("recognizes a reordered multi-valued reference to the inside")
  public void reorderedReferenceToInside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function2.accept(nonRootInSameRoot);
                NonRoot doubleArrow = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(nonRootInSameRoot1);
                NonRoot doubleArrow1 = nonRootInSameRoot1;
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function4.accept(nonRootInSameRoot2);
                NonRoot doubleArrow2 = nonRootInSameRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRootInSameRoot);
                NonRoot doubleArrow1 = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nonRootInSameRoot1);
                NonRoot doubleArrow2 = nonRootInSameRoot1;
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(nonRootInSameRoot2);
                NonRoot doubleArrow3 = nonRootInSameRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("different position for: NonRoot(id=\"sub1\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued unordered reference to the outside")
  public void insertionInUnorderedReferenceToOutside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function3.accept(inDifferentRoot1);
                NonRoot doubleArrow1 = inDifferentRoot1;
                NonRoot inDifferentRoot2 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(inDifferentRoot2);
                NonRoot doubleArrow2 = inDifferentRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(inDifferentRoot1);
                NonRoot doubleArrow2 = inDifferentRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow1, doubleArrow2)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued unordered ereference to the inside")
  public void insertionInUnuorderedReferenceToInside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRootInSameRoot);
                NonRoot doubleArrow = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function3.accept(nonRootInSameRoot1);
                NonRoot doubleArrow1 = nonRootInSameRoot1;
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRootInSameRoot2);
                NonRoot doubleArrow2 = nonRootInSameRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRootInSameRoot);
                NonRoot doubleArrow1 = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRootInSameRoot1);
                NonRoot doubleArrow2 = nonRootInSameRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow1, doubleArrow2)));
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function5.accept(nonRootInSameRoot2);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued unordered reference to the outside")
  public void removalFromUnorderedReferenceToOutside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(inDifferentRoot1);
                NonRoot doubleArrow1 = inDifferentRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow, doubleArrow1)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(inDifferentRoot1);
                NonRoot doubleArrow2 = inDifferentRoot1;
                NonRoot inDifferentRoot2 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(inDifferentRoot2);
                NonRoot doubleArrow3 = inDifferentRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued unordered reference to the inside")
  public void removalFromUnorderedReferenceToInside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRootInSameRoot);
                NonRoot doubleArrow = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(nonRootInSameRoot1);
                NonRoot doubleArrow1 = nonRootInSameRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow, doubleArrow1)));
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nonRootInSameRoot2);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRootInSameRoot);
                NonRoot doubleArrow1 = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nonRootInSameRoot1);
                NonRoot doubleArrow2 = nonRootInSameRoot1;
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(nonRootInSameRoot2);
                NonRoot doubleArrow3 = nonRootInSameRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued reference to the outside")
  public void insertionInReferenceToOutside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function3.accept(inDifferentRoot1);
                NonRoot doubleArrow1 = inDifferentRoot1;
                NonRoot inDifferentRoot2 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(inDifferentRoot2);
                NonRoot doubleArrow2 = inDifferentRoot2;
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(inDifferentRoot1);
                NonRoot doubleArrow2 = inDifferentRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow1, doubleArrow2)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued reference to the inside")
  public void insertionInReferenceToInside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRootInSameRoot);
                NonRoot doubleArrow = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function3.accept(nonRootInSameRoot1);
                NonRoot doubleArrow1 = nonRootInSameRoot1;
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRootInSameRoot2);
                NonRoot doubleArrow2 = nonRootInSameRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRootInSameRoot);
                NonRoot doubleArrow1 = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRootInSameRoot1);
                NonRoot doubleArrow2 = nonRootInSameRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow1, doubleArrow2)));
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function5.accept(nonRootInSameRoot2);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued reference to the outside")
  public void removalFromReferenceToOutside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(inDifferentRoot1);
                NonRoot doubleArrow1 = inDifferentRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow, doubleArrow1)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                NonRoot inDifferentRoot1 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(inDifferentRoot1);
                NonRoot doubleArrow2 = inDifferentRoot1;
                NonRoot inDifferentRoot2 =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(inDifferentRoot2);
                NonRoot doubleArrow3 = inDifferentRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued reference to the inside")
  public void removalFromReferenceToInside() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRootInSameRoot);
                NonRoot doubleArrow = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(nonRootInSameRoot1);
                NonRoot doubleArrow1 = nonRootInSameRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow, doubleArrow1)));
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nonRootInSameRoot2);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                NonRoot nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRootInSameRoot);
                NonRoot doubleArrow1 = nonRootInSameRoot;
                NonRoot nonRootInSameRoot1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nonRootInSameRoot1);
                NonRoot doubleArrow2 = nonRootInSameRoot1;
                NonRoot nonRootInSameRoot2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(nonRootInSameRoot2);
                NonRoot doubleArrow3 = nonRootInSameRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedNonContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an id-changed single-valued containment reference")
  public void idChangedSingleContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot NonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("different");
                    };
                function2.accept(NonRoot);
                NonRoot doubleArrow = NonRoot;
                it.setSingleValuedContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                it.setSingleValuedContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("unexpected value: NonRoot(id=\"different\")"));
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("missing the value: NonRoot(id=\"sub\")"));
  }

  @Test
  @DisplayName("recognizes a value-changed single-valued containment reference")
  public void valueSingleChangedContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub");
                      it1.setValue("different");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                it.setSingleValuedContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub");
                      it1.setValue("test");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                it.setSingleValuedContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("recognizes a reordered multi-valued containment reference")
  public void reorderedContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(nonRoot1);
                NonRoot doubleArrow1 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function4.accept(nonRoot2);
                NonRoot doubleArrow2 = nonRoot2;
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonroot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonroot);
                NonRoot doubleArrow1 = nonroot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nonRoot1);
                NonRoot doubleArrow2 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(nonRoot2);
                NonRoot doubleArrow3 = nonRoot2;
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("different position for: NonRoot(id=\"sub1\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued containment reference")
  public void insertionInContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function3.accept(nonRoot1);
                NonRoot doubleArrow1 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRoot2);
                NonRoot doubleArrow2 = nonRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRoot1);
                NonRoot doubleArrow2 = nonRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow1, doubleArrow2)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued containment reference")
  public void removalFromContainmentReference() {
    final Executable function =
        () -> {
          Root Root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(nonRoot1);
                NonRoot doubleArrow1 = nonRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow, doubleArrow1)));
              };
          function1.accept(Root);
          Root doubleArrow = Root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nonRoot1);
                NonRoot doubleArrow2 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(nonRoot2);
                NonRoot doubleArrow3 = nonRoot2;
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a value change in a multi-valued containment reference")
  public void valueChangedInMultiContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                      it1.setValue("a");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                      it1.setValue("different");
                    };
                function3.accept(nonRoot1);
                NonRoot doubleArrow1 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                      it1.setValue("c");
                    };
                function4.accept(nonRoot2);
                NonRoot doubleArrow2 = nonRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                      it1.setValue("a");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                      it1.setValue("b");
                    };
                function4.accept(nonRoot1);
                NonRoot doubleArrow2 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                      it1.setValue("c");
                    };
                function5.accept(nonRoot2);
                NonRoot doubleArrow3 = nonRoot2;
                Iterables.<NonRoot>addAll(
                    multiValuedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued undordered containment reference")
  public void insertionInUnorderedContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function3.accept(nonRoot1);
                NonRoot doubleArrow1 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRoot2);
                NonRoot doubleArrow2 = nonRoot2;
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function4.accept(nonRoot1);
                NonRoot doubleArrow2 = nonRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow1, doubleArrow2)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued unordered containment reference")
  public void removalFromUnorderedContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function3.accept(nonRoot1);
                NonRoot doubleArrow1 = nonRoot1;
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(List.of(doubleArrow, doubleArrow1)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                NonRoot nNonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                    };
                function4.accept(nNonRoot1);
                NonRoot doubleArrow2 = nNonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                    };
                function5.accept(nonRoot2);
                NonRoot doubleArrow3 = nonRoot2;
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a value change in a multi-valued containment reference")
  public void valueChangedInUnorderedMultiContainmentReference() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");

                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                      it1.setValue("a");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                      it1.setValue("different");
                    };
                function3.accept(nonRoot1);
                NonRoot doubleArrow1 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                      it1.setValue("c");
                    };
                function4.accept(nonRoot2);
                NonRoot doubleArrow2 = nonRoot2;
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow, doubleArrow1, doubleArrow2)));
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");

                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("sub1");
                      it1.setValue("a");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function4 =
                    (NonRoot it1) -> {
                      it1.setId("sub2");
                      it1.setValue("b");
                    };
                function4.accept(nonRoot1);
                NonRoot doubleArrow2 = nonRoot1;
                NonRoot nonRoot2 = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function5 =
                    (NonRoot it1) -> {
                      it1.setId("sub3");
                      it1.setValue("c");
                    };
                function5.accept(nonRoot2);
                NonRoot doubleArrow3 = nonRoot2;
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                Iterables.<NonRoot>addAll(
                    multiValuedUnorderedContainmentEReference,
                    Collections.<NonRoot>unmodifiableList(
                        List.of(doubleArrow1, doubleArrow2, doubleArrow3)));
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("reports a value change only via the containment reference (single-valued)")
  public void reportsValueChangeOnlyViaSingleContainmentReference() {
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("different");
        };
    function.accept(nonRoot);
    final NonRoot actualNonRoot = nonRoot;
    NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function1 =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("test");
        };
    function1.accept(nonRoot1);
    final NonRoot expectedNonRoot = nonRoot1;
    final Executable function2 =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function3 =
              (Root it) -> {
                it.setId("root");
                it.setSingleValuedContainmentEReference(actualNonRoot);
                it.setSingleValuedNonContainmentEReference(actualNonRoot);
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                multiValuedNonContainmentEReference.add(actualNonRoot);
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                multiValuedUnorderedNonContainmentEReference.add(actualNonRoot);
              };
          function3.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function4 =
              (Root it) -> {
                it.setId("root");
                it.setSingleValuedContainmentEReference(expectedNonRoot);
                it.setSingleValuedNonContainmentEReference(expectedNonRoot);
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                multiValuedNonContainmentEReference.add(expectedNonRoot);
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                multiValuedUnorderedNonContainmentEReference.add(expectedNonRoot);
              };
          function4.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function2);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        ModelDeepEqualityMatcherTest.containsExactDifferences(
            ".singleValuedContainmentEReference (NonRoot(id=\"sub\")).value had the wrong value:"
                + " \"different\""));
  }

  @Test
  @DisplayName("reports a value change only via the containment reference (multi-valued)")
  public void reportsValueChangeOnlyViaMultiContainmentReference() {
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("different");
        };
    function.accept(nonRoot);
    final NonRoot actualNonRoot = nonRoot;
    NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function1 =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("test");
        };
    function1.accept(nonRoot1);
    final NonRoot expectedNonRoot = nonRoot1;
    final Executable function2 =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function3 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                multiValuedContainmentEReference.add(actualNonRoot);
                it.setSingleValuedNonContainmentEReference(actualNonRoot);
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                multiValuedNonContainmentEReference.add(actualNonRoot);
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                multiValuedUnorderedNonContainmentEReference.add(actualNonRoot);
              };
          function3.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function4 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedContainmentEReference =
                    it.getMultiValuedContainmentEReference();
                multiValuedContainmentEReference.add(expectedNonRoot);
                it.setSingleValuedNonContainmentEReference(expectedNonRoot);
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                multiValuedNonContainmentEReference.add(expectedNonRoot);
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                multiValuedUnorderedNonContainmentEReference.add(expectedNonRoot);
              };
          function4.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function2);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        ModelDeepEqualityMatcherTest.containsExactDifferences(
            ".multiValuedContainmentEReference[0] (NonRoot(id=\"sub\")).value had the wrong value:"
                + " \"different\""));
  }

  @Test
  @DisplayName("reports a value change only via the containment reference (multi-valued unordered)")
  public void reportsValueChangeOnlyViaMultiUnorderedContainmentReference() {
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("different");
        };
    function.accept(nonRoot);
    final NonRoot actualNonRoot = nonRoot;
    NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function1 =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("test");
        };
    function1.accept(nonRoot1);
    final NonRoot expectedNonRoot = nonRoot1;
    final Executable function2 =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function3 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                multiValuedUnorderedContainmentEReference.add(actualNonRoot);
                it.setSingleValuedNonContainmentEReference(actualNonRoot);
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                multiValuedNonContainmentEReference.add(actualNonRoot);
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                multiValuedUnorderedNonContainmentEReference.add(actualNonRoot);
              };
          function3.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function4 =
              (Root it) -> {
                it.setId("root");
                EList<NonRoot> multiValuedUnorderedContainmentEReference =
                    it.getMultiValuedUnorderedContainmentEReference();
                multiValuedUnorderedContainmentEReference.add(expectedNonRoot);
                it.setSingleValuedNonContainmentEReference(expectedNonRoot);
                EList<NonRoot> multiValuedNonContainmentEReference =
                    it.getMultiValuedNonContainmentEReference();
                multiValuedNonContainmentEReference.add(expectedNonRoot);
                EList<NonRoot> multiValuedUnorderedNonContainmentEReference =
                    it.getMultiValuedUnorderedNonContainmentEReference();
                multiValuedUnorderedNonContainmentEReference.add(expectedNonRoot);
              };
          function4.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow, ModelMatchers.<Root>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function2);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        ModelDeepEqualityMatcherTest.containsExactDifferences(
            ".multiValuedUnorderedContainmentEReference{0} (NonRoot(id=\"sub\")).value had the"
                + " wrong value: \"different\""));
  }

  @Test
  @DisplayName(
      "reports a value change via a non-containment reference if the containment reference is"
          + " ignored")
  public void reportsValueChangeViaNonContainmentIfNecessary() {
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("different");
        };
    function.accept(nonRoot);
    final NonRoot actualNonRoot = nonRoot;
    NonRoot nonRoot1 = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function1 =
        (NonRoot it) -> {
          it.setId("sub");
          it.setValue("test");
        };
    function1.accept(nonRoot1);
    final NonRoot expectedNonRoot = nonRoot1;
    final Executable function2 =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function3 =
              (Root it) -> {
                it.setId("root");
                it.setSingleValuedContainmentEReference(actualNonRoot);
                it.setSingleValuedNonContainmentEReference(actualNonRoot);
              };
          function3.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function4 =
              (Root it) -> {
                it.setId("root");
                it.setSingleValuedContainmentEReference(expectedNonRoot);
                it.setSingleValuedNonContainmentEReference(expectedNonRoot);
              };
          function4.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow,
              ModelMatchers.<Root>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.ignoringFeatures(
                      AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE)));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function2);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        ModelDeepEqualityMatcherTest.containsExactDifferences(
            ".singleValuedNonContainmentEReference (NonRoot(id=\"sub\")).value had the wrong value:"
                + " \"different\""));
  }

  @Test
  @DisplayName("allows to change the value comparison to using #equals()")
  public void comparisonUsingEquals() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("different");
                      it1.setValue("test");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("test");
                      it1.setValue("test");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow,
              ModelMatchers.<Root>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.usingEqualsForReferencesTo(
                      AllElementTypesPackage.Literals.NON_ROOT)));
        };
    Assertions.assertDoesNotThrow(function);
  }

  @Test
  @DisplayName("recognizes changed values when changing the value comparison to using #equals()")
  public void comparisonUsingEqualsFails() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("test");
                      it1.setValue("different");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("test");
                      it1.setValue("test");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow,
              ModelMatchers.<Root>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.usingEqualsForReferencesTo(
                      AllElementTypesPackage.Literals.NON_ROOT)));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(), CoreMatchers.containsString("wrong value: NonRoot(id=\"test\")"));
  }

  @Test
  @DisplayName("recognizes changed values when changing the value comparison to using #equals()")
  public void comparisonUsingEqualsFails2() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("test");
                      it1.setValue("test");
                    };
                function2.accept(inDifferentRoot);
                NonRoot doubleArrow = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot inDifferentRoot =
                    ModelDeepEqualityMatcherTest.inDifferentRoot(
                        AllElementTypesCreators.aet.NonRoot());
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("test");
                      it1.setValue("test");
                    };
                function3.accept(inDifferentRoot);
                NonRoot doubleArrow1 = inDifferentRoot;
                it.setSingleValuedNonContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow,
              ModelMatchers.<Root>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.usingEqualsForReferencesTo(
                      AllElementTypesPackage.Literals.NON_ROOT)));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(), CoreMatchers.containsString("wrong value: NonRoot(id=\"test\")"));
  }

  @Test
  @DisplayName("allows defining custom strategies to compare referenced elements")
  public void customStrategyForComparison() {
    final Executable function =
        () -> {
          PInterface pcmInterface = PcmMockupCreators.pcm.Interface();
          final Consumer<PInterface> function1 =
              (PInterface it) -> {
                it.setId("testInterface");
                EList<PMethod> methods = it.getMethods();
                PMethod pcmMethod = PcmMockupCreators.pcm.Method();
                final Consumer<PMethod> function2 =
                    (PMethod it1) -> {
                      it1.setId("testMethod");
                      it1.setName("test");
                      PInterface inDifferentRepository =
                          ModelDeepEqualityMatcherTest.inDifferentRepository(
                              PcmMockupCreators.pcm.Interface());
                      final Consumer<PInterface> function3 =
                          (PInterface it2) -> {
                            it2.setId("different");
                            it2.setName("referenced");
                          };
                      function3.accept(inDifferentRepository);
                      PInterface doubleArrow = inDifferentRepository;
                      it1.setReturnType(doubleArrow);
                    };
                function2.accept(pcmMethod);
                PMethod doubleArrow = pcmMethod;
                methods.add(doubleArrow);
              };
          function1.accept(pcmInterface);
          PInterface doubleArrow = pcmInterface;
          PInterface pcmInterface1 = PcmMockupCreators.pcm.Interface();
          final Consumer<PInterface> function2 =
              (PInterface it) -> {
                it.setId("testInterface");
                EList<PMethod> methods = it.getMethods();
                PMethod pcmMethod = PcmMockupCreators.pcm.Method();
                final Consumer<PMethod> function3 =
                    (PMethod it1) -> {
                      it1.setId("testMethod");
                      it1.setName("test");
                      PInterface inDifferentRepository =
                          ModelDeepEqualityMatcherTest.inDifferentRepository(
                              PcmMockupCreators.pcm.Interface());
                      final Consumer<PInterface> function4 =
                          (PInterface it2) -> {
                            it2.setId("referencedInterface");
                            it2.setName("referenced");
                          };
                      function4.accept(inDifferentRepository);
                      PInterface doubleArrow1 = inDifferentRepository;
                      it1.setReturnType(doubleArrow1);
                    };
                function3.accept(pcmMethod);
                PMethod doubleArrow1 = pcmMethod;
                methods.add(doubleArrow1);
              };
          function2.accept(pcmInterface1);
          PInterface doubleArrow1 = pcmInterface1;
          MatcherAssert.<PInterface>assertThat(
              doubleArrow,
              ModelMatchers.<PInterface>equalsDeeply(
                  doubleArrow1, ModelDeepEqualityMatcherTest.comaringPcmInterfacesByName()));
        };
    Assertions.assertDoesNotThrow(function);
  }

  @Test
  @DisplayName("ignores nested references when changing the value comparison")
  public void ignoresNestedDifferencesWhenUsingComparisonStrategy() {
    final Executable function =
        () -> {
          PInterface pcmInterface = PcmMockupCreators.pcm.Interface();
          final Consumer<PInterface> function1 =
              (PInterface it) -> {
                it.setId("testInterface");
                EList<PMethod> methods = it.getMethods();
                PMethod pcmMethod = PcmMockupCreators.pcm.Method();
                final Consumer<PMethod> function2 =
                    (PMethod it1) -> {
                      it1.setId("testMethod");
                      it1.setName("test");
                      PInterface inDifferentRepository =
                          ModelDeepEqualityMatcherTest.inDifferentRepository(
                              PcmMockupCreators.pcm.Interface());
                      final Consumer<PInterface> function3 =
                          (PInterface it2) -> {
                            it2.setId("referencedInterface");
                            it2.setName("referenced");
                            EList<PMethod> methods1 = it2.getMethods();
                            PMethod pcmMethod1 = PcmMockupCreators.pcm.Method();
                            final Consumer<PMethod> function4 =
                                (PMethod it3) -> {
                                  it3.setId("different");
                                  it3.setName("different");
                                };
                            function4.accept(pcmMethod1);
                            PMethod doubleArrow = pcmMethod1;
                            methods1.add(doubleArrow);
                          };
                      function3.accept(inDifferentRepository);
                      PInterface doubleArrow = inDifferentRepository;
                      it1.setReturnType(doubleArrow);
                    };
                function2.accept(pcmMethod);
                PMethod doubleArrow = pcmMethod;
                methods.add(doubleArrow);
              };
          function1.accept(pcmInterface);
          PInterface doubleArrow = pcmInterface;
          PInterface pcmInterface1 = PcmMockupCreators.pcm.Interface();
          final Consumer<PInterface> function2 =
              (PInterface it) -> {
                it.setId("testInterface");
                EList<PMethod> methods = it.getMethods();
                PMethod pcmMethod = PcmMockupCreators.pcm.Method();
                final Consumer<PMethod> function3 =
                    (PMethod it1) -> {
                      it1.setId("testMethod");
                      it1.setName("test");
                      PInterface inDifferentRepository =
                          ModelDeepEqualityMatcherTest.inDifferentRepository(
                              PcmMockupCreators.pcm.Interface());
                      final Consumer<PInterface> function4 =
                          (PInterface it2) -> {
                            it2.setId("referencedInterface");
                            it2.setName("referenced");
                            EList<PMethod> methods1 = it2.getMethods();
                            PMethod pcmMethod1 = PcmMockupCreators.pcm.Method();
                            final Consumer<PMethod> function5 =
                                (PMethod it3) -> {
                                  it3.setId("fooMethod");
                                  it3.setName("fooMethod");
                                };
                            function5.accept(pcmMethod1);
                            PMethod doubleArrow1 = pcmMethod1;
                            methods1.add(doubleArrow1);
                            EList<PMethod> methods2 = it2.getMethods();
                            PMethod pcmMethod2 = PcmMockupCreators.pcm.Method();
                            final Consumer<PMethod> function6 =
                                (PMethod it3) -> {
                                  it3.setId("additionalMethod");
                                  it3.setName("additionalMethod");
                                };
                            function6.accept(pcmMethod2);
                            PMethod doubleArrow2 = pcmMethod2;
                            methods2.add(doubleArrow2);
                          };
                      function4.accept(inDifferentRepository);
                      PInterface doubleArrow1 = inDifferentRepository;
                      it1.setReturnType(doubleArrow1);
                    };
                function3.accept(pcmMethod);
                PMethod doubleArrow1 = pcmMethod;
                methods.add(doubleArrow1);
              };
          function2.accept(pcmInterface1);
          PInterface doubleArrow1 = pcmInterface1;
          MatcherAssert.<PInterface>assertThat(
              doubleArrow,
              ModelMatchers.<PInterface>equalsDeeply(
                  doubleArrow1, ModelDeepEqualityMatcherTest.comaringPcmInterfacesByName()));
        };
    Assertions.assertDoesNotThrow(function);
  }

  @Test
  @DisplayName("recognizes change values when using a custom value comparison")
  public void customStrategyForComparisonFailure() {
    final Executable function =
        () -> {
          PInterface pcmInterface = PcmMockupCreators.pcm.Interface();
          final Consumer<PInterface> function1 =
              (PInterface it) -> {
                it.setId("testInterface");
                EList<PMethod> methods = it.getMethods();
                PMethod pcmMethod = PcmMockupCreators.pcm.Method();
                final Consumer<PMethod> function2 =
                    (PMethod it1) -> {
                      it1.setId("testMethod");
                      it1.setName("test");
                      PInterface inDifferentRepository =
                          ModelDeepEqualityMatcherTest.inDifferentRepository(
                              PcmMockupCreators.pcm.Interface());
                      final Consumer<PInterface> function3 =
                          (PInterface it2) -> {
                            it2.setId("referencedInterface");
                            it2.setName("different");
                          };
                      function3.accept(inDifferentRepository);
                      PInterface doubleArrow = inDifferentRepository;
                      it1.setReturnType(doubleArrow);
                    };
                function2.accept(pcmMethod);
                PMethod doubleArrow = pcmMethod;
                methods.add(doubleArrow);
              };
          function1.accept(pcmInterface);
          PInterface doubleArrow = pcmInterface;
          PInterface pcmInterface1 = PcmMockupCreators.pcm.Interface();
          final Consumer<PInterface> function2 =
              (PInterface it) -> {
                it.setId("testInterface");
                EList<PMethod> methods = it.getMethods();
                PMethod pcmMethod = PcmMockupCreators.pcm.Method();
                final Consumer<PMethod> function3 =
                    (PMethod it1) -> {
                      it1.setId("testMethod");
                      it1.setName("test");
                      PInterface inDifferentRepository =
                          ModelDeepEqualityMatcherTest.inDifferentRepository(
                              PcmMockupCreators.pcm.Interface());
                      final Consumer<PInterface> function4 =
                          (PInterface it2) -> {
                            it2.setId("referencedInterface");
                            it2.setName("referenced");
                          };
                      function4.accept(inDifferentRepository);
                      PInterface doubleArrow1 = inDifferentRepository;
                      it1.setReturnType(doubleArrow1);
                    };
                function3.accept(pcmMethod);
                PMethod doubleArrow1 = pcmMethod;
                methods.add(doubleArrow1);
              };
          function2.accept(pcmInterface1);
          PInterface doubleArrow1 = pcmInterface1;
          MatcherAssert.<PInterface>assertThat(
              doubleArrow,
              ModelMatchers.<PInterface>equalsDeeply(
                  doubleArrow1, ModelDeepEqualityMatcherTest.comaringPcmInterfacesByName()));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    MatcherAssert.<String>assertThat(
        exception.getMessage(),
        CoreMatchers.containsString("wrong value: PInterface(id=\"referencedInterface\""));
  }

  @Test
  @DisplayName("allows to ignore ID features")
  public void ignoreIdFeature() {
    final Executable function =
        () -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it) -> {
                it.setId("differentRoot");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function2 =
                    (NonRoot it1) -> {
                      it1.setId("differentSub");
                      it1.setValue("test");
                    };
                function2.accept(nonRoot);
                NonRoot doubleArrow = nonRoot;
                it.setSingleValuedContainmentEReference(doubleArrow);
              };
          function1.accept(root);
          Root doubleArrow = root;
          Root root1 = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function2 =
              (Root it) -> {
                it.setId("root");
                NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
                final Consumer<NonRoot> function3 =
                    (NonRoot it1) -> {
                      it1.setId("test");
                      it1.setValue("test");
                    };
                function3.accept(nonRoot);
                NonRoot doubleArrow1 = nonRoot;
                it.setSingleValuedContainmentEReference(doubleArrow1);
              };
          function2.accept(root1);
          Root doubleArrow1 = root1;
          MatcherAssert.<Root>assertThat(
              doubleArrow,
              ModelMatchers.<Root>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.ignoringFeatures(AllElementTypesPackage.Literals.IDENTIFIED__ID)));
        };
    Assertions.assertDoesNotThrow(function);
  }

  @Test
  @DisplayName("allows to ignore references (missing object)")
  public void ignoreReferenceMissing() {
    final Executable function =
        () -> {
          ValueBased valueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                EList<Containable> children = it.getChildren();
                ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
                children.add(valueBased1);
              };
          function1.accept(valueBased);
          ValueBased doubleArrow = valueBased;
          ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function2 =
              (ValueBased it) -> {
                EList<Containable> children = it.getChildren();
                ValueBased valueBased2 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function3 =
                    (ValueBased it1) -> {
                      EList<Containable> referenced = it1.getReferenced();
                      NonRoot inDifferentRoot =
                          ModelDeepEqualityMatcherTest.inDifferentRoot(
                              AllElementTypesCreators.aet.NonRoot());
                      final Consumer<NonRoot> function4 =
                          (NonRoot it2) -> {
                            it2.setId("test");
                          };
                      function4.accept(inDifferentRoot);
                      NonRoot doubleArrow1 = inDifferentRoot;
                      referenced.add(doubleArrow1);
                    };
                function3.accept(valueBased2);
                ValueBased doubleArrow1 = valueBased2;
                children.add(doubleArrow1);
              };
          function2.accept(valueBased1);
          ValueBased doubleArrow1 = valueBased1;
          MatcherAssert.<ValueBased>assertThat(
              doubleArrow,
              ModelMatchers.<ValueBased>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.ignoringFeatures(
                      AllElementTypesPackage.Literals.VALUE_BASED__REFERENCED)));
        };
    Assertions.assertDoesNotThrow(function);
  }

  @Test
  @DisplayName("allows to ignore references (additional object)")
  public void ignoreReferenceAdded() {
    final Executable function =
        () -> {
          ValueBased valueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                EList<Containable> children = it.getChildren();
                ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function2 =
                    (ValueBased it1) -> {
                      EList<Containable> referenced = it1.getReferenced();
                      NonRoot inDifferentRoot =
                          ModelDeepEqualityMatcherTest.inDifferentRoot(
                              AllElementTypesCreators.aet.NonRoot());
                      final Consumer<NonRoot> function3 =
                          (NonRoot it2) -> {
                            it2.setId("test");
                          };
                      function3.accept(inDifferentRoot);
                      NonRoot doubleArrow = inDifferentRoot;
                      referenced.add(doubleArrow);
                    };
                function2.accept(valueBased1);
                ValueBased doubleArrow = valueBased1;
                children.add(doubleArrow);
              };
          function1.accept(valueBased);
          ValueBased doubleArrow = valueBased;
          ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function2 =
              (ValueBased it) -> {
                EList<Containable> children = it.getChildren();
                ValueBased valueBased2 = AllElementTypesCreators.aet.ValueBased();
                children.add(valueBased2);
              };
          function2.accept(valueBased1);
          ValueBased doubleArrow1 = valueBased1;
          MatcherAssert.<ValueBased>assertThat(
              doubleArrow,
              ModelMatchers.<ValueBased>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.ignoringFeatures(
                      AllElementTypesPackage.Literals.VALUE_BASED__REFERENCED)));
        };
    Assertions.assertDoesNotThrow(function);
  }

  @Test
  @DisplayName("allows to ignore references (changed object)")
  public void ignoreReferenceChanged() {
    final Executable function =
        () -> {
          ValueBased valueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                EList<Containable> children = it.getChildren();
                ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function2 =
                    (ValueBased it1) -> {
                      EList<Containable> referenced = it1.getReferenced();
                      NonRoot inDifferentRoot =
                          ModelDeepEqualityMatcherTest.inDifferentRoot(
                              AllElementTypesCreators.aet.NonRoot());
                      final Consumer<NonRoot> function3 =
                          (NonRoot it2) -> {
                            it2.setId("different");
                          };
                      function3.accept(inDifferentRoot);
                      NonRoot doubleArrow = inDifferentRoot;
                      referenced.add(doubleArrow);
                    };
                function2.accept(valueBased1);
                ValueBased doubleArrow = valueBased1;
                children.add(doubleArrow);
              };
          function1.accept(valueBased);
          ValueBased doubleArrow = valueBased;
          ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function2 =
              (ValueBased it) -> {
                EList<Containable> children = it.getChildren();
                ValueBased valueBased2 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function3 =
                    (ValueBased it1) -> {
                      EList<Containable> referenced = it1.getReferenced();
                      NonRoot inDifferentRoot =
                          ModelDeepEqualityMatcherTest.inDifferentRoot(
                              AllElementTypesCreators.aet.NonRoot());
                      final Consumer<NonRoot> function4 =
                          (NonRoot it2) -> {
                            it2.setId("test");
                          };
                      function4.accept(inDifferentRoot);
                      NonRoot doubleArrow1 = inDifferentRoot;
                      referenced.add(doubleArrow1);
                    };
                function3.accept(valueBased2);
                ValueBased doubleArrow1 = valueBased2;
                children.add(doubleArrow1);
              };
          function2.accept(valueBased1);
          ValueBased doubleArrow1 = valueBased1;
          MatcherAssert.<ValueBased>assertThat(
              doubleArrow,
              ModelMatchers.<ValueBased>equalsDeeply(
                  doubleArrow1,
                  ModelMatchers.ignoringFeatures(
                      AllElementTypesPackage.Literals.VALUE_BASED__REFERENCED)));
        };
    Assertions.assertDoesNotThrow(function);
  }

  @Test
  @DisplayName("uses the same fallback ID for matched objects in reporting")
  public void samefallbackIdInReporting() {
    final Executable function =
        () -> {
          ValueBased valueBased = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function1 =
              (ValueBased it) -> {
                it.setValue("test");
                EList<Containable> children = it.getChildren();
                ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function2 =
                    (ValueBased it1) -> {
                      it1.setValue("different");
                    };
                function2.accept(valueBased1);
                ValueBased doubleArrow = valueBased1;
                children.add(doubleArrow);
              };
          function1.accept(valueBased);
          ValueBased doubleArrow = valueBased;
          ValueBased valueBased1 = AllElementTypesCreators.aet.ValueBased();
          final Consumer<ValueBased> function2 =
              (ValueBased it) -> {
                it.setValue("test");
                EList<Containable> children = it.getChildren();
                ValueBased valueBased2 = AllElementTypesCreators.aet.ValueBased();
                final Consumer<ValueBased> function3 =
                    (ValueBased it1) -> {
                      it1.setValue("test");
                    };
                function3.accept(valueBased2);
                ValueBased doubleArrow1 = valueBased2;
                children.add(doubleArrow1);
              };
          function2.accept(valueBased1);
          ValueBased doubleArrow1 = valueBased1;
          MatcherAssert.<ValueBased>assertThat(
              doubleArrow, ModelMatchers.<ValueBased>equalsDeeply(doubleArrow1));
        };
    final AssertionError exception =
        Assertions.<AssertionError>assertThrows(AssertionError.class, function);
    String expected =
        System.lineSeparator()
            + "Expected: a ValueBased deeply equal to <ValueBased#1("
            + System.lineSeparator()
            + "        value=\"test\""
            + System.lineSeparator()
            + "        children=[ValueBased#2(value=\"test\")]"
            + System.lineSeparator()
            + ")>"
            + System.lineSeparator()
            + "     but: found the following differences:"
            + System.lineSeparator()
            + "        •  (ValueBased#1).children contained the unexpected value:"
            + " ValueBased#3(value=\"different\")"
            + System.lineSeparator()
            + "        •  (ValueBased#1).children was missing the value:"
            + " ValueBased#2(value=\"test\")"
            + System.lineSeparator()
            + "    for object <ValueBased#1("
            + System.lineSeparator()
            + "        value=\"test\""
            + System.lineSeparator()
            + "        children=[ValueBased#3(value=\"different\")]"
            + System.lineSeparator()
            + ")>";
    MatcherAssert.<String>assertThat(
        exception.getMessage().replaceAll("\r?\\n", System.lineSeparator()),
        CoreMatchers.<String>is(expected));
  }

  private static NonRoot inDifferentRoot(final NonRoot nonRoot) {
    final Consumer<NonRoot> function =
        (NonRoot it) -> {
          Root root = AllElementTypesCreators.aet.Root();
          final Consumer<Root> function1 =
              (Root it1) -> {
                it1.setId("foreignRoot");
                it1.setSingleValuedContainmentEReference(nonRoot);
              };
          function1.accept(root);
        };
    function.accept(nonRoot);
    return nonRoot;
  }

  private static PInterface inDifferentRepository(final PInterface content) {
    final Consumer<PInterface> function =
        (PInterface it) -> {
          Repository repository = PcmMockupCreators.pcm.Repository();
          final Consumer<Repository> function1 =
              (Repository it1) -> {
                it1.setId("foreignRepository");
                EList<PInterface> interfaces = it1.getInterfaces();
                interfaces.add(content);
              };
          function1.accept(repository);
        };
    function.accept(content);
    return content;
  }

  private static NonRoot nonRootInSameRoot(final Root root) {
    NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<NonRoot> function =
        (NonRoot it) -> {
          EList<NonRoot> multiValuedUnorderedContainmentEReference =
              root.getMultiValuedUnorderedContainmentEReference();
          multiValuedUnorderedContainmentEReference.add(it);
        };
    function.accept(nonRoot);
    return nonRoot;
  }

  private static Matcher<String> containsExactDifferences(final String... differences) {
    String lineSeparator = System.lineSeparator();
    String plus = ("found the following differences:" + lineSeparator);
    final Function<String, CharSequence> function =
        (String it) -> {
          return ("        • " + it);
        };
    String join =
        Arrays.stream(differences)
            .map(function)
            .collect(Collectors.joining(System.lineSeparator()));
    String plus1 = (plus + join);
    String lineSeparator1 = System.lineSeparator();
    String plus2 = (plus1 + lineSeparator1);
    String plus3 = (plus2 + "    for object");
    return CoreMatchers.containsString(plus3);
  }

  private static ModelDeepEqualityMatcherTest.PcmInterfaceNameEquality
      comaringPcmInterfacesByName() {
    return new ModelDeepEqualityMatcherTest.PcmInterfaceNameEquality();
  }
}
