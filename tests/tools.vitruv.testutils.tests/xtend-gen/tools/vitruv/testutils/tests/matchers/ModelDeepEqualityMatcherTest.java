package tools.vitruv.testutils.tests.matchers;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Containable;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import allElementTypes.ValueBased;
import allElementTypes.impl.NonRootImpl;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
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
import tools.vitruv.testutils.RegisterMetamodelsInStandalone;
import tools.vitruv.testutils.TestLogging;
import tools.vitruv.testutils.matchers.EqualityStrategy;
import tools.vitruv.testutils.matchers.ModelMatchers;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;
import tools.vitruv.testutils.metamodels.PcmMockupCreators;
import tools.vitruv.testutils.printing.ModelPrinterChange;
import tools.vitruv.testutils.printing.UnsetFeaturesHidingModelPrinter;
import tools.vitruv.testutils.printing.UseModelPrinter;

@UseModelPrinter(UnsetFeaturesHidingModelPrinter.class)
@ExtendWith({ RegisterMetamodelsInStandalone.class, ModelPrinterChange.class, TestLogging.class })
@SuppressWarnings("all")
public class ModelDeepEqualityMatcherTest {
  private static class ValueBasedNonRoot extends NonRootImpl {
    @Override
    public boolean equals(final Object object) {
      boolean _xifexpression = false;
      if ((object == this)) {
        _xifexpression = true;
      } else {
        boolean _xifexpression_1 = false;
        if ((object == null)) {
          _xifexpression_1 = false;
        } else {
          boolean _xifexpression_2 = false;
          if ((object instanceof ModelDeepEqualityMatcherTest.ValueBasedNonRoot)) {
            _xifexpression_2 = Objects.equal(this.value, ((ModelDeepEqualityMatcherTest.ValueBasedNonRoot)object).value);
          } else {
            _xifexpression_2 = false;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      return _xifexpression;
    }

    @Override
    public int hashCode() {
      int _xifexpression = (int) 0;
      if ((this.value == null)) {
        _xifexpression = 0;
      } else {
        _xifexpression = this.value.hashCode();
      }
      return _xifexpression;
    }
  }

  private static class PcmInterfaceNameEquality implements EqualityStrategy {
    @Override
    public EqualityStrategy.Result compare(final EObject left, final EObject right) {
      if ((left instanceof PInterface)) {
        if ((right instanceof PInterface)) {
          EqualityStrategy.Result _xifexpression = null;
          String _name = ((PInterface)left).getName();
          String _name_1 = ((PInterface)right).getName();
          boolean _equals = Objects.equal(_name, _name_1);
          if (_equals) {
            _xifexpression = EqualityStrategy.Result.EQUAL;
          } else {
            _xifexpression = EqualityStrategy.Result.UNEQUAL;
          }
          return _xifexpression;
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
    final Executable _function = () -> {
      MatcherAssert.<EObject>assertThat(left, ModelMatchers.<EObject>equalsDeeply(right));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  public static Stream<Arguments> equalObjects() {
    Root _Root = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function = (Root it) -> {
      it.setId("id");
    };
    Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function);
    Root _Root_1 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_1 = (Root it) -> {
      it.setId("id");
    };
    Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_1);
    Root _Root_2 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_2 = (Root it) -> {
      it.setId("root");
      EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
      Iterables.<Integer>addAll(_multiValuedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(42), Integer.valueOf(8))));
    };
    Root _doubleArrow_2 = ObjectExtensions.<Root>operator_doubleArrow(_Root_2, _function_2);
    Root _Root_3 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_3 = (Root it) -> {
      it.setId("root");
      EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
      Iterables.<Integer>addAll(_multiValuedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(42), Integer.valueOf(8))));
    };
    Root _doubleArrow_3 = ObjectExtensions.<Root>operator_doubleArrow(_Root_3, _function_3);
    Root _Root_4 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_4 = (Root it) -> {
      it.setId("root");
      EList<Integer> _multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
      Iterables.<Integer>addAll(_multiValuedUnorderedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(42), Integer.valueOf(8))));
    };
    Root _doubleArrow_4 = ObjectExtensions.<Root>operator_doubleArrow(_Root_4, _function_4);
    Root _Root_5 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_5 = (Root it) -> {
      it.setId("root");
      EList<Integer> _multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
      Iterables.<Integer>addAll(_multiValuedUnorderedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(42), Integer.valueOf(8))));
    };
    Root _doubleArrow_5 = ObjectExtensions.<Root>operator_doubleArrow(_Root_5, _function_5);
    Root _Root_6 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_6 = (Root it) -> {
      it.setId("root");
      EList<Integer> _multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
      Iterables.<Integer>addAll(_multiValuedUnorderedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(42), Integer.valueOf(8))));
    };
    Root _doubleArrow_6 = ObjectExtensions.<Root>operator_doubleArrow(_Root_6, _function_6);
    Root _Root_7 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_7 = (Root it) -> {
      it.setId("root");
      EList<Integer> _multiValuedUnorderedEAttribute = it.getMultiValuedUnorderedEAttribute();
      Iterables.<Integer>addAll(_multiValuedUnorderedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(8), Integer.valueOf(42), Integer.valueOf(2), Integer.valueOf(1))));
    };
    Root _doubleArrow_7 = ObjectExtensions.<Root>operator_doubleArrow(_Root_7, _function_7);
    Root _Root_8 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_8 = (Root it) -> {
      it.setId("root");
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_9 = (NonRoot it_1) -> {
        it_1.setId("sub");
      };
      NonRoot _doubleArrow_8 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_9);
      it.setSingleValuedNonContainmentEReference(_doubleArrow_8);
    };
    Root _doubleArrow_8 = ObjectExtensions.<Root>operator_doubleArrow(_Root_8, _function_8);
    Root _Root_9 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_9 = (Root it) -> {
      it.setId("root");
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_10 = (NonRoot it_1) -> {
        it_1.setId("sub");
      };
      NonRoot _doubleArrow_9 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_10);
      it.setSingleValuedNonContainmentEReference(_doubleArrow_9);
    };
    Root _doubleArrow_9 = ObjectExtensions.<Root>operator_doubleArrow(_Root_9, _function_9);
    Root _Root_10 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_10 = (Root it) -> {
      it.setId("root");
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_11 = (NonRoot it_1) -> {
        it_1.setId("sub");
        it_1.setValue("different");
      };
      NonRoot _doubleArrow_10 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_11);
      it.setSingleValuedNonContainmentEReference(_doubleArrow_10);
    };
    Root _doubleArrow_10 = ObjectExtensions.<Root>operator_doubleArrow(_Root_10, _function_10);
    Root _Root_11 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_11 = (Root it) -> {
      it.setId("root");
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_12 = (NonRoot it_1) -> {
        it_1.setId("sub");
        it_1.setValue("test");
      };
      NonRoot _doubleArrow_11 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_12);
      it.setSingleValuedNonContainmentEReference(_doubleArrow_11);
    };
    Root _doubleArrow_11 = ObjectExtensions.<Root>operator_doubleArrow(_Root_11, _function_11);
    Root _Root_12 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_12 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_13 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_12 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_13);
      NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_14 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_13 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_14);
      NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_15 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_14 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_15);
      Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_12, _doubleArrow_13, _doubleArrow_14)));
    };
    Root _doubleArrow_12 = ObjectExtensions.<Root>operator_doubleArrow(_Root_12, _function_12);
    Root _Root_13 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_13 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_14 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_13 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_14);
      NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_15 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_14 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_15);
      NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_16 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_15 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_16);
      Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_13, _doubleArrow_14, _doubleArrow_15)));
    };
    Root _doubleArrow_13 = ObjectExtensions.<Root>operator_doubleArrow(_Root_13, _function_13);
    Root _Root_14 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_14 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_15 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_14 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_15);
      NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_16 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_15 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_16);
      NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_17 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_16 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_17);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_14, _doubleArrow_15, _doubleArrow_16)));
    };
    Root _doubleArrow_14 = ObjectExtensions.<Root>operator_doubleArrow(_Root_14, _function_14);
    Root _Root_15 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_15 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_16 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_15 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_16);
      NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_17 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_16 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_17);
      NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_18 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_17 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_18);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_15, _doubleArrow_16, _doubleArrow_17)));
    };
    Root _doubleArrow_15 = ObjectExtensions.<Root>operator_doubleArrow(_Root_15, _function_15);
    Root _Root_16 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_16 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_17 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_16 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_17);
      NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_18 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_17 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_18);
      NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_19 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_18 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_19);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_16, _doubleArrow_17, _doubleArrow_18)));
    };
    Root _doubleArrow_16 = ObjectExtensions.<Root>operator_doubleArrow(_Root_16, _function_16);
    Root _Root_17 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_17 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_18 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_17 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_18);
      NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_19 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_18 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_19);
      NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
      final Procedure1<NonRoot> _function_20 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_19 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_20);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_17, _doubleArrow_18, _doubleArrow_19)));
    };
    Root _doubleArrow_17 = ObjectExtensions.<Root>operator_doubleArrow(_Root_17, _function_17);
    Root _Root_18 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_18 = (Root it) -> {
      it.setId("root");
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_19 = (NonRoot it_1) -> {
        it_1.setId("sub");
      };
      NonRoot _doubleArrow_18 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_19);
      it.setSingleValuedNonContainmentEReference(_doubleArrow_18);
    };
    Root _doubleArrow_18 = ObjectExtensions.<Root>operator_doubleArrow(_Root_18, _function_18);
    Root _Root_19 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_19 = (Root it) -> {
      it.setId("root");
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_20 = (NonRoot it_1) -> {
        it_1.setId("sub");
      };
      NonRoot _doubleArrow_19 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_20);
      it.setSingleValuedNonContainmentEReference(_doubleArrow_19);
    };
    Root _doubleArrow_19 = ObjectExtensions.<Root>operator_doubleArrow(_Root_19, _function_19);
    Root _Root_20 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_20 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_21 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_20 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_21);
      NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_22 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_21 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_22);
      NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_23 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_22 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_23);
      Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_20, _doubleArrow_21, _doubleArrow_22)));
    };
    Root _doubleArrow_20 = ObjectExtensions.<Root>operator_doubleArrow(_Root_20, _function_20);
    Root _Root_21 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_21 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_22 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_21 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_22);
      NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_23 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_22 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_23);
      NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_24 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_23 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_24);
      Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_21, _doubleArrow_22, _doubleArrow_23)));
    };
    Root _doubleArrow_21 = ObjectExtensions.<Root>operator_doubleArrow(_Root_21, _function_21);
    Root _Root_22 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_22 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_23 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_22 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_23);
      NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_24 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_23 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_24);
      NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_25 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_24 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_25);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_22, _doubleArrow_23, _doubleArrow_24)));
    };
    Root _doubleArrow_22 = ObjectExtensions.<Root>operator_doubleArrow(_Root_22, _function_22);
    Root _Root_23 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_23 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_24 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_23 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_24);
      NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_25 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_24 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_25);
      NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_26 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_25 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_26);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_23, _doubleArrow_24, _doubleArrow_25)));
    };
    Root _doubleArrow_23 = ObjectExtensions.<Root>operator_doubleArrow(_Root_23, _function_23);
    Root _Root_24 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_24 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_25 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_24 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_25);
      NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_26 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_25 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_26);
      NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_27 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_26 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_27);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_24, _doubleArrow_25, _doubleArrow_26)));
    };
    Root _doubleArrow_24 = ObjectExtensions.<Root>operator_doubleArrow(_Root_24, _function_24);
    Root _Root_25 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_25 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
      NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_26 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_25 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_26);
      NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_27 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_26 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_27);
      NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
      final Procedure1<NonRoot> _function_28 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_27 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_28);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_25, _doubleArrow_26, _doubleArrow_27)));
    };
    Root _doubleArrow_25 = ObjectExtensions.<Root>operator_doubleArrow(_Root_25, _function_25);
    Root _Root_26 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_26 = (Root it) -> {
      it.setId("root");
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_27 = (NonRoot it_1) -> {
        it_1.setId("sub");
      };
      NonRoot _doubleArrow_26 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_27);
      it.setSingleValuedContainmentEReference(_doubleArrow_26);
    };
    Root _doubleArrow_26 = ObjectExtensions.<Root>operator_doubleArrow(_Root_26, _function_26);
    Root _Root_27 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_27 = (Root it) -> {
      it.setId("root");
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_28 = (NonRoot it_1) -> {
        it_1.setId("sub");
      };
      NonRoot _doubleArrow_27 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_28);
      it.setSingleValuedContainmentEReference(_doubleArrow_27);
    };
    Root _doubleArrow_27 = ObjectExtensions.<Root>operator_doubleArrow(_Root_27, _function_27);
    Root _Root_28 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_28 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_29 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_28 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_29);
      NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_30 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_29 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_30);
      NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_31 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_30 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_31);
      Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_28, _doubleArrow_29, _doubleArrow_30)));
    };
    Root _doubleArrow_28 = ObjectExtensions.<Root>operator_doubleArrow(_Root_28, _function_28);
    Root _Root_29 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_29 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_30 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_29 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_30);
      NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_31 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_30 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_31);
      NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_32 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_31 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_32);
      Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_29, _doubleArrow_30, _doubleArrow_31)));
    };
    Root _doubleArrow_29 = ObjectExtensions.<Root>operator_doubleArrow(_Root_29, _function_29);
    Root _Root_30 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_30 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_31 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_30 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_31);
      NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_32 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_31 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_32);
      NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_33 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_32 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_33);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_30, _doubleArrow_31, _doubleArrow_32)));
    };
    Root _doubleArrow_30 = ObjectExtensions.<Root>operator_doubleArrow(_Root_30, _function_30);
    Root _Root_31 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_31 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_32 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_31 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_32);
      NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_33 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_32 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_33);
      NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_34 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_33 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_34);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_31, _doubleArrow_32, _doubleArrow_33)));
    };
    Root _doubleArrow_31 = ObjectExtensions.<Root>operator_doubleArrow(_Root_31, _function_31);
    Root _Root_32 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_32 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_33 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_32 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_33);
      NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_34 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_33 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_34);
      NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_35 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_34 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_35);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_32, _doubleArrow_33, _doubleArrow_34)));
    };
    Root _doubleArrow_32 = ObjectExtensions.<Root>operator_doubleArrow(_Root_32, _function_32);
    Root _Root_33 = AllElementTypesCreators.aet.Root();
    final Procedure1<Root> _function_33 = (Root it) -> {
      it.setId("root");
      EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
      NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_34 = (NonRoot it_1) -> {
        it_1.setId("sub3");
      };
      NonRoot _doubleArrow_33 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_34);
      NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_35 = (NonRoot it_1) -> {
        it_1.setId("sub1");
      };
      NonRoot _doubleArrow_34 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_35);
      NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
      final Procedure1<NonRoot> _function_36 = (NonRoot it_1) -> {
        it_1.setId("sub2");
      };
      NonRoot _doubleArrow_35 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_36);
      Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_33, _doubleArrow_34, _doubleArrow_35)));
    };
    Root _doubleArrow_33 = ObjectExtensions.<Root>operator_doubleArrow(_Root_33, _function_33);
    return Stream.<Arguments>of(
      Arguments.of("simple object", _doubleArrow, _doubleArrow_1), 
      Arguments.of(
        "with ordered multi-valued attribute", _doubleArrow_2, _doubleArrow_3), 
      Arguments.of(
        "with unordered multi-valued attribute (same order)", _doubleArrow_4, _doubleArrow_5), 
      Arguments.of(
        "with unordered multi-valued attribute (different order)", _doubleArrow_6, _doubleArrow_7), 
      Arguments.of(
        "with single-valued reference to the outside", _doubleArrow_8, _doubleArrow_9), 
      Arguments.of(
        "with single-valued reference to the outside (same id but different content)", _doubleArrow_10, _doubleArrow_11), 
      Arguments.of(
        "with ordered multi-valued reference to the outside", _doubleArrow_12, _doubleArrow_13), 
      Arguments.of(
        "with unordered multi-valued reference to the outside (same order)", _doubleArrow_14, _doubleArrow_15), 
      Arguments.of(
        "with unordered multi-valued reference to the outside (different order)", _doubleArrow_16, _doubleArrow_17), 
      Arguments.of(
        "with single-valued reference to the inside", _doubleArrow_18, _doubleArrow_19), 
      Arguments.of(
        "with ordered multi-valued reference to the inside", _doubleArrow_20, _doubleArrow_21), 
      Arguments.of(
        "with unordered multi-valued reference to the inside (same order)", _doubleArrow_22, _doubleArrow_23), 
      Arguments.of(
        "with unordered multi-valued reference to the inside (different order)", _doubleArrow_24, _doubleArrow_25), 
      Arguments.of(
        "with single-valued containment reference", _doubleArrow_26, _doubleArrow_27), 
      Arguments.of(
        "with ordered multi-valued containment reference", _doubleArrow_28, _doubleArrow_29), 
      Arguments.of(
        "with unordered multi-valued containment reference (same order)", _doubleArrow_30, _doubleArrow_31), 
      Arguments.of(
        "with unordered multi-valued containment reference (different order)", _doubleArrow_32, _doubleArrow_33));
  }

  @Test
  @DisplayName("recognizes a changed single-valued attribute")
  public void changedAttribute() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("different");
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("test");
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("recognizes a different order in a multi-valued attribute")
  public void attributeListReorded() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
        Iterables.<Integer>addAll(_multiValuedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(42), Integer.valueOf(8))));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<Integer> _multiValuedEAttribute = it.getMultiValuedEAttribute();
        Iterables.<Integer>addAll(_multiValuedEAttribute, Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(1), Integer.valueOf(8), Integer.valueOf(2), Integer.valueOf(42))));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, 
        ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("different position for: 8"));
  }

  @Test
  @DisplayName("recognizes a changed single-valued reference to the outside")
  public void changedReferenceToOutside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("different");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        it.setSingleValuedNonContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        it.setSingleValuedNonContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: NonRoot(id=\"different\")"));
  }

  @Test
  @DisplayName("recognizes a changed single-valued reference to the inside")
  public void changedReferenceToInside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("different");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_2);
        it.setSingleValuedNonContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_3);
        it.setSingleValuedNonContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: NonRoot(id=\"different\")"));
  }

  @Test
  @DisplayName("recognizes a reordered multi-valued reference to the outside")
  public void reorderedReferenceToOutside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_3);
        NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_4);
        NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("different position for: NonRoot(id=\"sub1\")"));
  }

  @Test
  @DisplayName("recognizes a reordered multi-valued reference to the inside")
  public void reorderedReferenceToInside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_2);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_3);
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_3);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_4);
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("different position for: NonRoot(id=\"sub1\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued unordered reference to the outside")
  public void insertionInUnorderedReferenceToOutside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_3);
        NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued unordered ereference to the inside")
  public void insertionInUnuorderedReferenceToInside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_2);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_3);
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_3);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2)));
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_5);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued unordered reference to the outside")
  public void removalFromUnorderedReferenceToOutside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_3);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_4);
        NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued unordered reference to the inside")
  public void removalFromUnorderedReferenceToInside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_2);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_3);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1)));
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_4);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_3);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_4);
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued reference to the outside")
  public void insertionInReferenceToOutside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_3);
        NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued reference to the inside")
  public void insertionInReferenceToInside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_2);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_3);
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_3);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2)));
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_5);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued reference to the outside")
  public void removalFromReferenceToOutside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_3);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        NonRoot _inDifferentRoot_1 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_1, _function_4);
        NonRoot _inDifferentRoot_2 = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued reference to the inside")
  public void removalFromReferenceToInside() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_2);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_3);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1)));
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_4);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        NonRoot _nonRootInSameRoot = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot, _function_3);
        NonRoot _nonRootInSameRoot_1 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_1, _function_4);
        NonRoot _nonRootInSameRoot_2 = ModelDeepEqualityMatcherTest.nonRootInSameRoot(it);
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_nonRootInSameRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedNonContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes an id-changed single-valued containment reference")
  public void idChangedSingleContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("different");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        it.setSingleValuedContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        it.setSingleValuedContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("unexpected value: NonRoot(id=\"different\")"));
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("missing the value: NonRoot(id=\"sub\")"));
  }

  @Test
  @DisplayName("recognizes a value-changed single-valued containment reference")
  public void valueSingleChangedContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub");
          it_1.setValue("different");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        it.setSingleValuedContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        it.setSingleValuedContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("recognizes a reordered multi-valued containment reference")
  public void reorderedContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_3);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_4);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("different position for: NonRoot(id=\"sub1\")"));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued containment reference")
  public void insertionInContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_3);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued containment reference")
  public void removalFromContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_3);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_4);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a value change in a multi-valued containment reference")
  public void valueChangedInMultiContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
          it_1.setValue("a");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
          it_1.setValue("different");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_3);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
          it_1.setValue("c");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
          it_1.setValue("a");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
          it_1.setValue("b");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_4);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
          it_1.setValue("c");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("recognizes an insertion into a multi-valued undordered containment reference")
  public void insertionInUnorderedContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_3);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("unexpected value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a removal from a multi-valued unordered containment reference")
  public void removalFromUnorderedContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_3);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_4);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("missing the value: NonRoot(id=\"sub2\")"));
  }

  @Test
  @DisplayName("recognizes a value change in a multi-valued containment reference")
  public void valueChangedInUnorderedMultiContainmentReference() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("sub1");
          it_1.setValue("a");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub2");
          it_1.setValue("different");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_3);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub3");
          it_1.setValue("c");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_4);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow, _doubleArrow_1, _doubleArrow_2)));
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("sub1");
          it_1.setValue("a");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_4 = (NonRoot it_1) -> {
          it_1.setId("sub2");
          it_1.setValue("b");
        };
        NonRoot _doubleArrow_2 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_4);
        NonRoot _NonRoot_2 = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_5 = (NonRoot it_1) -> {
          it_1.setId("sub3");
          it_1.setValue("c");
        };
        NonRoot _doubleArrow_3 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_2, _function_5);
        Iterables.<NonRoot>addAll(_multiValuedUnorderedContainmentEReference, Collections.<NonRoot>unmodifiableList(CollectionLiterals.<NonRoot>newArrayList(_doubleArrow_1, _doubleArrow_2, _doubleArrow_3)));
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: \"different\""));
  }

  @Test
  @DisplayName("reports a value change only via the containment reference (single-valued)")
  public void reportsValueChangeOnlyViaSingleContainmentReference() {
    NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("different");
    };
    final NonRoot actualNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function);
    NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function_1 = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("test");
    };
    final NonRoot expectedNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_1);
    final Executable _function_2 = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_3 = (Root it) -> {
        it.setId("root");
        it.setSingleValuedContainmentEReference(actualNonRoot);
        it.setSingleValuedNonContainmentEReference(actualNonRoot);
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        _multiValuedNonContainmentEReference.add(actualNonRoot);
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        _multiValuedUnorderedNonContainmentEReference.add(actualNonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_3);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_4 = (Root it) -> {
        it.setId("root");
        it.setSingleValuedContainmentEReference(expectedNonRoot);
        it.setSingleValuedNonContainmentEReference(expectedNonRoot);
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        _multiValuedNonContainmentEReference.add(expectedNonRoot);
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        _multiValuedUnorderedNonContainmentEReference.add(expectedNonRoot);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_4);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function_2);
    MatcherAssert.<String>assertThat(exception.getMessage(), ModelDeepEqualityMatcherTest.containsExactDifferences(
      ".singleValuedContainmentEReference (NonRoot(id=\"sub\")).value had the wrong value: \"different\""));
  }

  @Test
  @DisplayName("reports a value change only via the containment reference (multi-valued)")
  public void reportsValueChangeOnlyViaMultiContainmentReference() {
    NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("different");
    };
    final NonRoot actualNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function);
    NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function_1 = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("test");
    };
    final NonRoot expectedNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_1);
    final Executable _function_2 = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_3 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        _multiValuedContainmentEReference.add(actualNonRoot);
        it.setSingleValuedNonContainmentEReference(actualNonRoot);
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        _multiValuedNonContainmentEReference.add(actualNonRoot);
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        _multiValuedUnorderedNonContainmentEReference.add(actualNonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_3);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_4 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedContainmentEReference = it.getMultiValuedContainmentEReference();
        _multiValuedContainmentEReference.add(expectedNonRoot);
        it.setSingleValuedNonContainmentEReference(expectedNonRoot);
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        _multiValuedNonContainmentEReference.add(expectedNonRoot);
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        _multiValuedUnorderedNonContainmentEReference.add(expectedNonRoot);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_4);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function_2);
    MatcherAssert.<String>assertThat(exception.getMessage(), ModelDeepEqualityMatcherTest.containsExactDifferences(
      ".multiValuedContainmentEReference[0] (NonRoot(id=\"sub\")).value had the wrong value: \"different\""));
  }

  @Test
  @DisplayName("reports a value change only via the containment reference (multi-valued unordered)")
  public void reportsValueChangeOnlyViaMultiUnorderedContainmentReference() {
    NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("different");
    };
    final NonRoot actualNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function);
    NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function_1 = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("test");
    };
    final NonRoot expectedNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_1);
    final Executable _function_2 = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_3 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        _multiValuedUnorderedContainmentEReference.add(actualNonRoot);
        it.setSingleValuedNonContainmentEReference(actualNonRoot);
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        _multiValuedNonContainmentEReference.add(actualNonRoot);
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        _multiValuedUnorderedNonContainmentEReference.add(actualNonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_3);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_4 = (Root it) -> {
        it.setId("root");
        EList<NonRoot> _multiValuedUnorderedContainmentEReference = it.getMultiValuedUnorderedContainmentEReference();
        _multiValuedUnorderedContainmentEReference.add(expectedNonRoot);
        it.setSingleValuedNonContainmentEReference(expectedNonRoot);
        EList<NonRoot> _multiValuedNonContainmentEReference = it.getMultiValuedNonContainmentEReference();
        _multiValuedNonContainmentEReference.add(expectedNonRoot);
        EList<NonRoot> _multiValuedUnorderedNonContainmentEReference = it.getMultiValuedUnorderedNonContainmentEReference();
        _multiValuedUnorderedNonContainmentEReference.add(expectedNonRoot);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_4);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function_2);
    MatcherAssert.<String>assertThat(exception.getMessage(), ModelDeepEqualityMatcherTest.containsExactDifferences(
      ".multiValuedUnorderedContainmentEReference{0} (NonRoot(id=\"sub\")).value had the wrong value: \"different\""));
  }

  @Test
  @DisplayName("reports a value change via a non-containment reference if the containment reference is ignored")
  public void reportsValueChangeViaNonContainmentIfNecessary() {
    NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("different");
    };
    final NonRoot actualNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function);
    NonRoot _NonRoot_1 = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function_1 = (NonRoot it) -> {
      it.setId("sub");
      it.setValue("test");
    };
    final NonRoot expectedNonRoot = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot_1, _function_1);
    final Executable _function_2 = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_3 = (Root it) -> {
        it.setId("root");
        it.setSingleValuedContainmentEReference(actualNonRoot);
        it.setSingleValuedNonContainmentEReference(actualNonRoot);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_3);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_4 = (Root it) -> {
        it.setId("root");
        it.setSingleValuedContainmentEReference(expectedNonRoot);
        it.setSingleValuedNonContainmentEReference(expectedNonRoot);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_4);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1, ModelMatchers.ignoringFeatures(AllElementTypesPackage.Literals.ROOT__SINGLE_VALUED_CONTAINMENT_EREFERENCE)));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function_2);
    MatcherAssert.<String>assertThat(exception.getMessage(), ModelDeepEqualityMatcherTest.containsExactDifferences(
      ".singleValuedNonContainmentEReference (NonRoot(id=\"sub\")).value had the wrong value: \"different\""));
  }

  @Test
  @DisplayName("allows to change the value comparison to using #equals()")
  public void comparisonUsingEquals() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("different");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        it.setSingleValuedNonContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("test");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        it.setSingleValuedNonContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1, ModelMatchers.usingEqualsForReferencesTo(AllElementTypesPackage.Literals.NON_ROOT)));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("recognizes changed values when changing the value comparison to using #equals()")
  public void comparisonUsingEqualsFails() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("test");
          it_1.setValue("different");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        it.setSingleValuedNonContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(new ModelDeepEqualityMatcherTest.ValueBasedNonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("test");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        it.setSingleValuedNonContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1, ModelMatchers.usingEqualsForReferencesTo(AllElementTypesPackage.Literals.NON_ROOT)));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: NonRoot(id=\"test\")"));
  }

  @Test
  @DisplayName("recognizes changed values when changing the value comparison to using #equals()")
  public void comparisonUsingEqualsFails2() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("test");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_2);
        it.setSingleValuedNonContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("test");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
        it.setSingleValuedNonContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1, ModelMatchers.usingEqualsForReferencesTo(AllElementTypesPackage.Literals.NON_ROOT)));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: NonRoot(id=\"test\")"));
  }

  @Test
  @DisplayName("allows defining custom strategies to compare referenced elements")
  public void customStrategyForComparison() {
    final Executable _function = () -> {
      PInterface _Interface = PcmMockupCreators.pcm.Interface();
      final Procedure1<PInterface> _function_1 = (PInterface it) -> {
        it.setId("testInterface");
        EList<PMethod> _methods = it.getMethods();
        PMethod _Method = PcmMockupCreators.pcm.Method();
        final Procedure1<PMethod> _function_2 = (PMethod it_1) -> {
          it_1.setId("testMethod");
          it_1.setName("test");
          PInterface _inDifferentRepository = ModelDeepEqualityMatcherTest.inDifferentRepository(PcmMockupCreators.pcm.Interface());
          final Procedure1<PInterface> _function_3 = (PInterface it_2) -> {
            it_2.setId("different");
            it_2.setName("referenced");
          };
          PInterface _doubleArrow = ObjectExtensions.<PInterface>operator_doubleArrow(_inDifferentRepository, _function_3);
          it_1.setReturnType(_doubleArrow);
        };
        PMethod _doubleArrow = ObjectExtensions.<PMethod>operator_doubleArrow(_Method, _function_2);
        _methods.add(_doubleArrow);
      };
      PInterface _doubleArrow = ObjectExtensions.<PInterface>operator_doubleArrow(_Interface, _function_1);
      PInterface _Interface_1 = PcmMockupCreators.pcm.Interface();
      final Procedure1<PInterface> _function_2 = (PInterface it) -> {
        it.setId("testInterface");
        EList<PMethod> _methods = it.getMethods();
        PMethod _Method = PcmMockupCreators.pcm.Method();
        final Procedure1<PMethod> _function_3 = (PMethod it_1) -> {
          it_1.setId("testMethod");
          it_1.setName("test");
          PInterface _inDifferentRepository = ModelDeepEqualityMatcherTest.inDifferentRepository(PcmMockupCreators.pcm.Interface());
          final Procedure1<PInterface> _function_4 = (PInterface it_2) -> {
            it_2.setId("referencedInterface");
            it_2.setName("referenced");
          };
          PInterface _doubleArrow_1 = ObjectExtensions.<PInterface>operator_doubleArrow(_inDifferentRepository, _function_4);
          it_1.setReturnType(_doubleArrow_1);
        };
        PMethod _doubleArrow_1 = ObjectExtensions.<PMethod>operator_doubleArrow(_Method, _function_3);
        _methods.add(_doubleArrow_1);
      };
      PInterface _doubleArrow_1 = ObjectExtensions.<PInterface>operator_doubleArrow(_Interface_1, _function_2);
      MatcherAssert.<PInterface>assertThat(_doubleArrow, ModelMatchers.<PInterface>equalsDeeply(_doubleArrow_1, ModelDeepEqualityMatcherTest.comaringPcmInterfacesByName()));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("ignores nested references when changing the value comparison")
  public void ignoresNestedDifferencesWhenUsingComparisonStrategy() {
    final Executable _function = () -> {
      PInterface _Interface = PcmMockupCreators.pcm.Interface();
      final Procedure1<PInterface> _function_1 = (PInterface it) -> {
        it.setId("testInterface");
        EList<PMethod> _methods = it.getMethods();
        PMethod _Method = PcmMockupCreators.pcm.Method();
        final Procedure1<PMethod> _function_2 = (PMethod it_1) -> {
          it_1.setId("testMethod");
          it_1.setName("test");
          PInterface _inDifferentRepository = ModelDeepEqualityMatcherTest.inDifferentRepository(PcmMockupCreators.pcm.Interface());
          final Procedure1<PInterface> _function_3 = (PInterface it_2) -> {
            it_2.setId("referencedInterface");
            it_2.setName("referenced");
            EList<PMethod> _methods_1 = it_2.getMethods();
            PMethod _Method_1 = PcmMockupCreators.pcm.Method();
            final Procedure1<PMethod> _function_4 = (PMethod it_3) -> {
              it_3.setId("different");
              it_3.setName("different");
            };
            PMethod _doubleArrow = ObjectExtensions.<PMethod>operator_doubleArrow(_Method_1, _function_4);
            _methods_1.add(_doubleArrow);
          };
          PInterface _doubleArrow = ObjectExtensions.<PInterface>operator_doubleArrow(_inDifferentRepository, _function_3);
          it_1.setReturnType(_doubleArrow);
        };
        PMethod _doubleArrow = ObjectExtensions.<PMethod>operator_doubleArrow(_Method, _function_2);
        _methods.add(_doubleArrow);
      };
      PInterface _doubleArrow = ObjectExtensions.<PInterface>operator_doubleArrow(_Interface, _function_1);
      PInterface _Interface_1 = PcmMockupCreators.pcm.Interface();
      final Procedure1<PInterface> _function_2 = (PInterface it) -> {
        it.setId("testInterface");
        EList<PMethod> _methods = it.getMethods();
        PMethod _Method = PcmMockupCreators.pcm.Method();
        final Procedure1<PMethod> _function_3 = (PMethod it_1) -> {
          it_1.setId("testMethod");
          it_1.setName("test");
          PInterface _inDifferentRepository = ModelDeepEqualityMatcherTest.inDifferentRepository(PcmMockupCreators.pcm.Interface());
          final Procedure1<PInterface> _function_4 = (PInterface it_2) -> {
            it_2.setId("referencedInterface");
            it_2.setName("referenced");
            EList<PMethod> _methods_1 = it_2.getMethods();
            PMethod _Method_1 = PcmMockupCreators.pcm.Method();
            final Procedure1<PMethod> _function_5 = (PMethod it_3) -> {
              it_3.setId("fooMethod");
              it_3.setName("fooMethod");
            };
            PMethod _doubleArrow_1 = ObjectExtensions.<PMethod>operator_doubleArrow(_Method_1, _function_5);
            _methods_1.add(_doubleArrow_1);
            EList<PMethod> _methods_2 = it_2.getMethods();
            PMethod _Method_2 = PcmMockupCreators.pcm.Method();
            final Procedure1<PMethod> _function_6 = (PMethod it_3) -> {
              it_3.setId("additionalMethod");
              it_3.setName("additionalMethod");
            };
            PMethod _doubleArrow_2 = ObjectExtensions.<PMethod>operator_doubleArrow(_Method_2, _function_6);
            _methods_2.add(_doubleArrow_2);
          };
          PInterface _doubleArrow_1 = ObjectExtensions.<PInterface>operator_doubleArrow(_inDifferentRepository, _function_4);
          it_1.setReturnType(_doubleArrow_1);
        };
        PMethod _doubleArrow_1 = ObjectExtensions.<PMethod>operator_doubleArrow(_Method, _function_3);
        _methods.add(_doubleArrow_1);
      };
      PInterface _doubleArrow_1 = ObjectExtensions.<PInterface>operator_doubleArrow(_Interface_1, _function_2);
      MatcherAssert.<PInterface>assertThat(_doubleArrow, ModelMatchers.<PInterface>equalsDeeply(_doubleArrow_1, ModelDeepEqualityMatcherTest.comaringPcmInterfacesByName()));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("recognizes change values when using a custom value comparison")
  public void customStrategyForComparisonFailure() {
    final Executable _function = () -> {
      PInterface _Interface = PcmMockupCreators.pcm.Interface();
      final Procedure1<PInterface> _function_1 = (PInterface it) -> {
        it.setId("testInterface");
        EList<PMethod> _methods = it.getMethods();
        PMethod _Method = PcmMockupCreators.pcm.Method();
        final Procedure1<PMethod> _function_2 = (PMethod it_1) -> {
          it_1.setId("testMethod");
          it_1.setName("test");
          PInterface _inDifferentRepository = ModelDeepEqualityMatcherTest.inDifferentRepository(PcmMockupCreators.pcm.Interface());
          final Procedure1<PInterface> _function_3 = (PInterface it_2) -> {
            it_2.setId("referencedInterface");
            it_2.setName("different");
          };
          PInterface _doubleArrow = ObjectExtensions.<PInterface>operator_doubleArrow(_inDifferentRepository, _function_3);
          it_1.setReturnType(_doubleArrow);
        };
        PMethod _doubleArrow = ObjectExtensions.<PMethod>operator_doubleArrow(_Method, _function_2);
        _methods.add(_doubleArrow);
      };
      PInterface _doubleArrow = ObjectExtensions.<PInterface>operator_doubleArrow(_Interface, _function_1);
      PInterface _Interface_1 = PcmMockupCreators.pcm.Interface();
      final Procedure1<PInterface> _function_2 = (PInterface it) -> {
        it.setId("testInterface");
        EList<PMethod> _methods = it.getMethods();
        PMethod _Method = PcmMockupCreators.pcm.Method();
        final Procedure1<PMethod> _function_3 = (PMethod it_1) -> {
          it_1.setId("testMethod");
          it_1.setName("test");
          PInterface _inDifferentRepository = ModelDeepEqualityMatcherTest.inDifferentRepository(PcmMockupCreators.pcm.Interface());
          final Procedure1<PInterface> _function_4 = (PInterface it_2) -> {
            it_2.setId("referencedInterface");
            it_2.setName("referenced");
          };
          PInterface _doubleArrow_1 = ObjectExtensions.<PInterface>operator_doubleArrow(_inDifferentRepository, _function_4);
          it_1.setReturnType(_doubleArrow_1);
        };
        PMethod _doubleArrow_1 = ObjectExtensions.<PMethod>operator_doubleArrow(_Method, _function_3);
        _methods.add(_doubleArrow_1);
      };
      PInterface _doubleArrow_1 = ObjectExtensions.<PInterface>operator_doubleArrow(_Interface_1, _function_2);
      MatcherAssert.<PInterface>assertThat(_doubleArrow, ModelMatchers.<PInterface>equalsDeeply(_doubleArrow_1, ModelDeepEqualityMatcherTest.comaringPcmInterfacesByName()));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    MatcherAssert.<String>assertThat(exception.getMessage(), CoreMatchers.containsString("wrong value: PInterface(id=\"referencedInterface\""));
  }

  @Test
  @DisplayName("allows to ignore ID features")
  public void ignoreIdFeature() {
    final Executable _function = () -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it) -> {
        it.setId("differentRoot");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_2 = (NonRoot it_1) -> {
          it_1.setId("differentSub");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_2);
        it.setSingleValuedContainmentEReference(_doubleArrow);
      };
      Root _doubleArrow = ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
      Root _Root_1 = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_2 = (Root it) -> {
        it.setId("root");
        NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
        final Procedure1<NonRoot> _function_3 = (NonRoot it_1) -> {
          it_1.setId("test");
          it_1.setValue("test");
        };
        NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function_3);
        it.setSingleValuedContainmentEReference(_doubleArrow_1);
      };
      Root _doubleArrow_1 = ObjectExtensions.<Root>operator_doubleArrow(_Root_1, _function_2);
      MatcherAssert.<Root>assertThat(_doubleArrow, ModelMatchers.<Root>equalsDeeply(_doubleArrow_1, ModelMatchers.ignoringFeatures(AllElementTypesPackage.Literals.IDENTIFIED__ID)));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("allows to ignore references (missing object)")
  public void ignoreReferenceMissing() {
    final Executable _function = () -> {
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        _children.add(_ValueBased_1);
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_2 = (ValueBased it) -> {
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_2 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_3 = (ValueBased it_1) -> {
          EList<Containable> _referenced = it_1.getReferenced();
          NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Procedure1<NonRoot> _function_4 = (NonRoot it_2) -> {
            it_2.setId("test");
          };
          NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_4);
          _referenced.add(_doubleArrow_1);
        };
        ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_2, _function_3);
        _children.add(_doubleArrow_1);
      };
      ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
      MatcherAssert.<ValueBased>assertThat(_doubleArrow, ModelMatchers.<ValueBased>equalsDeeply(_doubleArrow_1, ModelMatchers.ignoringFeatures(AllElementTypesPackage.Literals.VALUE_BASED__REFERENCED)));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("allows to ignore references (additional object)")
  public void ignoreReferenceAdded() {
    final Executable _function = () -> {
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_2 = (ValueBased it_1) -> {
          EList<Containable> _referenced = it_1.getReferenced();
          NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Procedure1<NonRoot> _function_3 = (NonRoot it_2) -> {
            it_2.setId("test");
          };
          NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
          _referenced.add(_doubleArrow);
        };
        ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
        _children.add(_doubleArrow);
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_2 = (ValueBased it) -> {
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_2 = AllElementTypesCreators.aet.ValueBased();
        _children.add(_ValueBased_2);
      };
      ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
      MatcherAssert.<ValueBased>assertThat(_doubleArrow, ModelMatchers.<ValueBased>equalsDeeply(_doubleArrow_1, ModelMatchers.ignoringFeatures(AllElementTypesPackage.Literals.VALUE_BASED__REFERENCED)));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("allows to ignore references (changed object)")
  public void ignoreReferenceChanged() {
    final Executable _function = () -> {
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_2 = (ValueBased it_1) -> {
          EList<Containable> _referenced = it_1.getReferenced();
          NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Procedure1<NonRoot> _function_3 = (NonRoot it_2) -> {
            it_2.setId("different");
          };
          NonRoot _doubleArrow = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_3);
          _referenced.add(_doubleArrow);
        };
        ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
        _children.add(_doubleArrow);
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_2 = (ValueBased it) -> {
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_2 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_3 = (ValueBased it_1) -> {
          EList<Containable> _referenced = it_1.getReferenced();
          NonRoot _inDifferentRoot = ModelDeepEqualityMatcherTest.inDifferentRoot(AllElementTypesCreators.aet.NonRoot());
          final Procedure1<NonRoot> _function_4 = (NonRoot it_2) -> {
            it_2.setId("test");
          };
          NonRoot _doubleArrow_1 = ObjectExtensions.<NonRoot>operator_doubleArrow(_inDifferentRoot, _function_4);
          _referenced.add(_doubleArrow_1);
        };
        ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_2, _function_3);
        _children.add(_doubleArrow_1);
      };
      ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
      MatcherAssert.<ValueBased>assertThat(_doubleArrow, ModelMatchers.<ValueBased>equalsDeeply(_doubleArrow_1, ModelMatchers.ignoringFeatures(AllElementTypesPackage.Literals.VALUE_BASED__REFERENCED)));
    };
    Assertions.assertDoesNotThrow(_function);
  }

  @Test
  @DisplayName("uses the same fallback ID for matched objects in reporting")
  public void samefallbackIdInReporting() {
    final Executable _function = () -> {
      ValueBased _ValueBased = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_1 = (ValueBased it) -> {
        it.setValue("test");
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_2 = (ValueBased it_1) -> {
          it_1.setValue("different");
        };
        ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
        _children.add(_doubleArrow);
      };
      ValueBased _doubleArrow = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased, _function_1);
      ValueBased _ValueBased_1 = AllElementTypesCreators.aet.ValueBased();
      final Procedure1<ValueBased> _function_2 = (ValueBased it) -> {
        it.setValue("test");
        EList<Containable> _children = it.getChildren();
        ValueBased _ValueBased_2 = AllElementTypesCreators.aet.ValueBased();
        final Procedure1<ValueBased> _function_3 = (ValueBased it_1) -> {
          it_1.setValue("test");
        };
        ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_2, _function_3);
        _children.add(_doubleArrow_1);
      };
      ValueBased _doubleArrow_1 = ObjectExtensions.<ValueBased>operator_doubleArrow(_ValueBased_1, _function_2);
      MatcherAssert.<ValueBased>assertThat(_doubleArrow, ModelMatchers.<ValueBased>equalsDeeply(_doubleArrow_1));
    };
    final AssertionError exception = Assertions.<AssertionError>assertThrows(AssertionError.class, _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("Expected: a ValueBased deeply equal to <ValueBased#1(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("value=\"test\"");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("children=[ValueBased#2(value=\"test\")]");
    _builder.newLine();
    _builder.append(")>");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("but: found the following differences:");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("•  (ValueBased#1).children contained the unexpected value: ValueBased#3(value=\"different\")");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("•  (ValueBased#1).children was missing the value: ValueBased#2(value=\"test\")");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("for object <ValueBased#1(");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("value=\"test\"");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("children=[ValueBased#3(value=\"different\")]");
    _builder.newLine();
    _builder.append(")>");
    MatcherAssert.<String>assertThat(exception.getMessage().replaceAll("\r?\\n", System.lineSeparator()), CoreMatchers.<String>is(_builder.toString()));
  }

  private static NonRoot inDifferentRoot(final NonRoot nonRoot) {
    final Procedure1<NonRoot> _function = (NonRoot it) -> {
      Root _Root = AllElementTypesCreators.aet.Root();
      final Procedure1<Root> _function_1 = (Root it_1) -> {
        it_1.setId("foreignRoot");
        it_1.setSingleValuedContainmentEReference(nonRoot);
      };
      ObjectExtensions.<Root>operator_doubleArrow(_Root, _function_1);
    };
    return ObjectExtensions.<NonRoot>operator_doubleArrow(nonRoot, _function);
  }

  private static PInterface inDifferentRepository(final PInterface content) {
    final Procedure1<PInterface> _function = (PInterface it) -> {
      Repository _Repository = PcmMockupCreators.pcm.Repository();
      final Procedure1<Repository> _function_1 = (Repository it_1) -> {
        it_1.setId("foreignRepository");
        EList<PInterface> _interfaces = it_1.getInterfaces();
        _interfaces.add(content);
      };
      ObjectExtensions.<Repository>operator_doubleArrow(_Repository, _function_1);
    };
    return ObjectExtensions.<PInterface>operator_doubleArrow(content, _function);
  }

  private static NonRoot nonRootInSameRoot(final Root root) {
    NonRoot _NonRoot = AllElementTypesCreators.aet.NonRoot();
    final Procedure1<NonRoot> _function = (NonRoot it) -> {
      EList<NonRoot> _multiValuedUnorderedContainmentEReference = root.getMultiValuedUnorderedContainmentEReference();
      _multiValuedUnorderedContainmentEReference.add(it);
    };
    return ObjectExtensions.<NonRoot>operator_doubleArrow(_NonRoot, _function);
  }

  private static Matcher<String> containsExactDifferences(final String... differences) {
    String _lineSeparator = System.lineSeparator();
    String _plus = ("found the following differences:" + _lineSeparator);
    final Function1<String, CharSequence> _function = (String it) -> {
      return ("        • " + it);
    };
    String _join = IterableExtensions.<String>join(((Iterable<String>)Conversions.doWrapArray(differences)), System.lineSeparator(), _function);
    String _plus_1 = (_plus + _join);
    String _lineSeparator_1 = System.lineSeparator();
    String _plus_2 = (_plus_1 + _lineSeparator_1);
    String _plus_3 = (_plus_2 + 
      "    for object");
    return CoreMatchers.containsString(_plus_3);
  }

  private static ModelDeepEqualityMatcherTest.PcmInterfaceNameEquality comaringPcmInterfacesByName() {
    return new ModelDeepEqualityMatcherTest.PcmInterfaceNameEquality();
  }
}
