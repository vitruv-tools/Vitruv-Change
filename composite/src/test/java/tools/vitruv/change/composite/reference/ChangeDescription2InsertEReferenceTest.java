package tools.vitruv.change.composite.reference;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.NonRoot;
import allElementTypes.Root;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.composite.util.AtomicEChangeAssertHelper;
import tools.vitruv.change.composite.util.CompoundEChangeAssertHelper;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

public class ChangeDescription2InsertEReferenceTest extends ChangeDescription2ChangeTransformationTest {
  @ParameterizedTest
  @MethodSource("provideParametersInsertMultipleAtOnceAtIndex")
  public void testInsertMultipleAtOnceContainment(final int count, final int insertAt) {
    List<NonRoot> preparationElements = IntStream.range(0, 10)
        .mapToObj(i -> AllElementTypesCreators.aet.NonRoot())
        .collect(Collectors.toList());
    this.<Root>record(this.getUniquePersistedRoot(), it ->
        Iterables.addAll(it.getMultiValuedContainmentEReference(), preparationElements));
    List<NonRoot> nonRootElements = IntStream.range(0, count)
        .mapToObj(i -> {
          NonRoot element = AllElementTypesCreators.aet.NonRoot();
          element.setId(String.valueOf(i));
          return element;
        })
        .collect(Collectors.toList());
    Iterable<? extends EChange<EObject>> actualChanges = this.<Root>record(this.getUniquePersistedRoot(), it ->
        it.getMultiValuedContainmentEReference().addAll(insertAt, nonRootElements));
    Assertions.assertEquals(Iterables.size(actualChanges), nonRootElements.size() * 3);
    for (int i = 0; i < nonRootElements.size(); i++) {
      final NonRoot nonRoot = nonRootElements.get(i);
      final int insertAtIndex = i + insertAt;
      actualChanges = AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(
          CompoundEChangeAssertHelper.assertCreateAndInsertNonRoot(actualChanges, this.getUniquePersistedRoot(),
              AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot, insertAtIndex, false),
          nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false);
    }
  }

  @ParameterizedTest
  @MethodSource("provideParametersInsertMultipleAtOnceAtIndex")
  public void testInsertMultipleAtOnceNonContainment(final int count, final int insertAt) {
    List<NonRoot> preparationElements = IntStream.range(0, 10)
        .mapToObj(i -> AllElementTypesCreators.aet.NonRoot())
        .collect(Collectors.toList());
    this.getUniquePersistedRoot().getMultiValuedContainmentEReference().addAll(preparationElements);
    this.<Root>record(this.getUniquePersistedRoot(), it ->
        it.getMultiValuedNonContainmentEReference().addAll(preparationElements));
    List<NonRoot> nonRootElements = IntStream.range(0, count)
        .mapToObj(i -> AllElementTypesCreators.aet.NonRoot())
        .collect(Collectors.toList());
    this.getUniquePersistedRoot().getMultiValuedContainmentEReference().addAll(nonRootElements);
    Iterable<? extends EChange<EObject>> actualChanges = this.<Root>record(this.getUniquePersistedRoot(), it ->
        it.getMultiValuedNonContainmentEReference().addAll(insertAt, nonRootElements));
    Assertions.assertEquals(Iterables.size(actualChanges), nonRootElements.size());
    for (int i = 0; i < nonRootElements.size(); i++) {
      final NonRoot nonRoot = nonRootElements.get(i);
      final int insertAtIndex = i + insertAt;
      actualChanges = AtomicEChangeAssertHelper.assertInsertEReference(actualChanges, this.getUniquePersistedRoot(),
          AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, insertAtIndex,
          false, false);
    }
  }

  public static Stream<Arguments> provideParametersInsertMultipleAtOnceAtIndex() {
    List<Integer> counts = List.of(2, 3, 5, 10);
    List<Integer> insertionIndexes = List.of(0, 5, 10);
    return insertionIndexes.stream()
        .flatMap(index -> counts.stream().map(it -> Arguments.of(it, index)));
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
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertReplaceSingleValuedEAttribute(
        CompoundEChangeAssertHelper.assertCreateAndInsertNonRoot(
            ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 3),
            this.getUniquePersistedRoot(),
            AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_CONTAINMENT_EREFERENCE, nonRoot, expectedIndex, false),
        nonRoot, AllElementTypesPackage.Literals.IDENTIFIED__ID, null, nonRoot.getId(), false, false));
  }

  private void insertAndAssertSingleNonContainment(final int expectedIndex) {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    this.getUniquePersistedRoot().getMultiValuedContainmentEReference().add(expectedIndex, nonRoot);
    final List<EChange<EObject>> result = this.<Root>record(this.getUniquePersistedRoot(), it ->
        it.getMultiValuedNonContainmentEReference().add(expectedIndex, nonRoot));
    AtomicEChangeAssertHelper.assertEmpty(AtomicEChangeAssertHelper.assertInsertEReference(
        ChangeDescription2ChangeTransformationTest.<EObject>assertChangeCount(result, 1),
        this.getUniquePersistedRoot(),
        AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_NON_CONTAINMENT_EREFERENCE, nonRoot, expectedIndex,
        false, false));
  }
}
