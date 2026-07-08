package tools.vitruv.change.testutils.printing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests for {@link PrintResultExtension}.
 */
public class PrintResultExtensionTest {
    @ParameterizedTest
    @CsvSource({
            "PRINTED, PRINTED, PRINTED",
            "PRINTED, PRINTED_NO_OUTPUT, PRINTED",
            "PRINTED_NO_OUTPUT, PRINTED, PRINTED",
            "PRINTED_NO_OUTPUT, PRINTED_NO_OUTPUT, PRINTED_NO_OUTPUT",
            "PRINTED_NO_OUTPUT, NOT_RESPONSIBLE, NOT_RESPONSIBLE",
            "NOT_RESPONSIBLE, PRINTED_NO_OUTPUT, NOT_RESPONSIBLE",
            "NOT_RESPONSIBLE, NOT_RESPONSIBLE, NOT_RESPONSIBLE"
    })
    void combinesPrintResults(
            final PrintResult previous,
            final PrintResult latest,
            final PrintResult expected) {
                assertEquals(expected, PrintResultExtension.operatorPlus(previous, latest));
            }

    @ParameterizedTest
    @CsvSource({
            "PRINTED, NOT_RESPONSIBLE",
            "NOT_RESPONSIBLE, PRINTED"
    })
    void rejectsInvalidPrintResultTransitions(
            final PrintResult previous,
            final PrintResult latest) {
        assertThrows(
                IllegalStateException.class,
                () -> PrintResultExtension.operatorPlus(previous, latest));
    }
}
