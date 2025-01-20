package tools.vitruv.change.composite.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.ExclusiveRange;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.change.composite.util.CompoundEChangeAssertHelper;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

@SuppressWarnings("all")
public class ChangeDescription2InsertEReferenceTest extends ChangeDescription2ChangeTransformationTest {
  @ParameterizedTest
  @MethodSource("provideParametersInsertMultipleAtOnceAtIndex")
  public void testInsertMultipleAtOnceContainment(final int count, final int insertAt) {
    final Function1<Integer, NonRoot> _function = (Integer it) -> {
      return AllElementTypesCreators.aet.NonRoot();
    };
    final Iterable<NonRoot> preparationElements = IterableExtensions.<Integer, NonRoot>map(new ExclusiveRange(0, 10, true), _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      Iterables.<NonRoot>addAll(it.getMultiValuedContainmentEReference(), preparationElements);
    };
    this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    final Function1<Integer, NonRoot> _function_2 = (Integer it) -> {
      final NonRoot element = AllElementTypesCreators.aet.NonRoot();
      element.setId(it.toString());
      return element;
    };
    final List<NonRoot> nonRootElements = IterableUtil.<Integer, NonRoot>mapFixed(new ExclusiveRange(0, count, true), _function_2);
    final Consumer<Root> _function_3 = (Root it) -> {
      it.getMultiValuedContainmentEReference().addAll(insertAt, nonRootElements);
    };
    Iterable<? extends EChange<EObject>> actualChanges = this.<Root>record(this.getUniquePersistedRoot(), _function_3);
    final Function1<Pair<Integer, NonRoot>, Pair<NonRoot, Integer>> _function_4 = (Pair<Integer, NonRoot> it) -> {
      NonRoot _value = it.getValue();
      Integer _key = it.getKey();
      int _plus = ((_key).intValue() + insertAt);
      return new Pair<NonRoot, Integer>(_value, Integer.valueOf(_plus));
    };
    final List<Pair<NonRoot, Integer>> expectedInsertions = IterableUtil.<Pair<Integer, NonRoot>, Pair<NonRoot, Integer>>mapFixed(IterableExtensions.<NonRoot>indexed(nonRootElements), _function_4);
    int _size = IterableExtensions.size(actualChanges);
    int _size_1 = expectedInsertions.size();
    int _multiply = (_size_1 * 3);
    Assertions.assertEquals(_size, _multiply);
    for (final Pair<NonRoot, Integer> insertion : expectedInsertions) {
      {
        final NonRoot nonRoot = insertion.getKey();
        final Integer insertAtIndex = insertion.getValue();
        actualChanges = AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(CompoundEChangeAssertHelper.assertCreateAndInsertNonRoot(actualChanges, this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot, (insertAtIndex).intValue(), false), nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false);
      }
    }
  }

  @ParameterizedTest
  @MethodSource("provideParametersInsertMultipleAtOnceAtIndex")
  public void testInsertMultipleAtOnceNonContainment(final int count, final int insertAt) {
    final Function1<Integer, NonRoot> _function = (Integer it) -> {
      return AllElementTypesCreators.aet.NonRoot();
    };
    final List<NonRoot> preparationElements = IterableUtil.<Integer, NonRoot>mapFixed(new ExclusiveRange(0, 10, true), _function);
    this.getUniquePersistedRoot().getMultiValuedContainmentEReference().addAll(preparationElements);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedNonContainmentEReference().addAll(preparationElements);
    };
    this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    final Function1<Integer, NonRoot> _function_2 = (Integer it) -> {
      return AllElementTypesCreators.aet.NonRoot();
    };
    final List<NonRoot> nonRootElements = IterableUtil.<Integer, NonRoot>mapFixed(new ExclusiveRange(0, count, true), _function_2);
    this.getUniquePersistedRoot().getMultiValuedContainmentEReference().addAll(nonRootElements);
    final Consumer<Root> _function_3 = (Root it) -> {
      it.getMultiValuedNonContainmentEReference().addAll(insertAt, nonRootElements);
    };
    Iterable<? extends EChange<EObject>> actualChanges = this.<Root>record(this.getUniquePersistedRoot(), _function_3);
    final Function1<Pair<Integer, NonRoot>, Pair<NonRoot, Integer>> _function_4 = (Pair<Integer, NonRoot> it) -> {
      NonRoot _value = it.getValue();
      Integer _key = it.getKey();
      int _plus = ((_key).intValue() + insertAt);
      return new Pair<NonRoot, Integer>(_value, Integer.valueOf(_plus));
    };
    final Iterable<Pair<NonRoot, Integer>> expectedInsertions = IterableExtensions.<Pair<Integer, NonRoot>, Pair<NonRoot, Integer>>map(IterableExtensions.<NonRoot>indexed(nonRootElements), _function_4);
    Assertions.assertEquals(IterableExtensions.size(actualChanges), IterableExtensions.size(expectedInsertions));
    for (final Pair<NonRoot, Integer> insertion : expectedInsertions) {
      {
        final NonRoot nonRoot = insertion.getKey();
        final Integer insertAtIndex = insertion.getValue();
        actualChanges = AtomicEChangeAssertHelper.assertInsertEReference(actualChanges, this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, (insertAtIndex).intValue(), false, false);
      }
    }
  }

  public static Stream<Arguments> provideParametersInsertMultipleAtOnceAtIndex() {
    final List<Integer> counts = Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(5), Integer.valueOf(10)));
    final List<Integer> insertionIndexes = Collections.<Integer>unmodifiableList(CollectionLiterals.<Integer>newArrayList(Integer.valueOf(0), Integer.valueOf(5), Integer.valueOf(10)));
    final Function1<Integer, List<Arguments>> _function = (Integer index) -> {
      final Function1<Integer, Arguments> _function_1 = (Integer it) -> {
        return Arguments.of(it, index);
      };
      return ListExtensions.<Integer, Arguments>map(counts, _function_1);
    };
    return StreamSupport.<Arguments>stream(Iterables.<Arguments>concat(ListExtensions.<Integer, List<Arguments>>map(insertionIndexes, _function)).spliterator(), false);
  }

  @Test
  public void testInsertSingleNonContainment() {
    this.insertAndAssertSingleNonContainment(0);
  }

  @Test
  public void testInsertMultipleIterativelyNonContainment() {
    this.insertAndAssertSingleNonContainment(0);
    this.insertAndAssertSingleNonContainment(1);
    this.insertAndAssertSingleNonContainment(2);
    this.insertAndAssertSingleNonContainment(1);
  }

  @Test
  public void testInsertSingleContainment() {
    this.insertAndAssertSingleContainment(0);
  }

  @Test
  public void testInsertMultipleIterativelyContainment() {
    this.insertAndAssertSingleContainment(0);
    this.insertAndAssertSingleContainment(1);
    this.insertAndAssertSingleContainment(2);
    this.insertAndAssertSingleContainment(1);
  }

  private void insertAndAssertSingleContainment(final int expectedIndex) {
    this.getUniquePersistedRoot();
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Consumer<Root> _function = (Root it) -> {
      it.getMultiValuedContainmentEReference().add(expectedIndex, nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(CompoundEChangeAssertHelper.assertCreateAndInsertNonRoot(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot, expectedIndex, false), nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false));
  }

  private void insertAndAssertSingleNonContainment(final int expectedIndex) {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    Root _uniquePersistedRoot = this.getUniquePersistedRoot();
    final Procedure1<Root> _function = (Root it) -> {
      it.getMultiValuedContainmentEReference().add(expectedIndex, nonRoot);
    };
    ObjectExtensions.<Root>operator_doubleArrow(_uniquePersistedRoot, _function);
    final Consumer<Root> _function_1 = (Root it) -> {
      it.getMultiValuedNonContainmentEReference().add(expectedIndex, nonRoot);
    };
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), _function_1);
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1), this.getUniquePersistedRoot(), AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, expectedIndex, false, false));
  }
}
