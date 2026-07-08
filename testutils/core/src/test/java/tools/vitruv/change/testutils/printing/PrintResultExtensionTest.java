package tools.vitruv.change.testutils.printing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PrintResultExtensionTest {
  @ParameterizedTest
  @MethodSource("validResultCombinations")
  void combinesValidResults(
      final PrintResult previousResult,
      final PrintResult latestResult,
      final PrintResult expectedResult) {
    assertEquals(expectedResult, PrintResultExtension.operator_plus(previousResult, latestResult));
  }

  @ParameterizedTest
  @MethodSource("inconsistentResultCombinations")
  void rejectsInconsistentResults(
      final PrintResult previousResult, final PrintResult latestResult) {
    final IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> PrintResultExtension.operator_plus(previousResult, latestResult));

    assertEquals(
        "Got " + latestResult + " after " + previousResult + "!", exception.getMessage());
  }

  private static Stream<Arguments> validResultCombinations() {
    return Stream.of(
        Arguments.of(PrintResult.PRINTED, PrintResult.PRINTED, PrintResult.PRINTED),
        Arguments.of(PrintResult.PRINTED, PrintResult.PRINTED_NO_OUTPUT, PrintResult.PRINTED),
        Arguments.of(PrintResult.PRINTED_NO_OUTPUT, PrintResult.PRINTED, PrintResult.PRINTED),
        Arguments.of(
            PrintResult.PRINTED_NO_OUTPUT,
            PrintResult.PRINTED_NO_OUTPUT,
            PrintResult.PRINTED_NO_OUTPUT),
        Arguments.of(
            PrintResult.PRINTED_NO_OUTPUT,
            PrintResult.NOT_RESPONSIBLE,
            PrintResult.NOT_RESPONSIBLE),
        Arguments.of(
            PrintResult.NOT_RESPONSIBLE,
            PrintResult.PRINTED_NO_OUTPUT,
            PrintResult.NOT_RESPONSIBLE),
        Arguments.of(
            PrintResult.NOT_RESPONSIBLE,
            PrintResult.NOT_RESPONSIBLE,
            PrintResult.NOT_RESPONSIBLE));
  }

  private static Stream<Arguments> inconsistentResultCombinations() {
    return Stream.of(
        Arguments.of(PrintResult.PRINTED, PrintResult.NOT_RESPONSIBLE),
        Arguments.of(PrintResult.NOT_RESPONSIBLE, PrintResult.PRINTED));
  }
}
