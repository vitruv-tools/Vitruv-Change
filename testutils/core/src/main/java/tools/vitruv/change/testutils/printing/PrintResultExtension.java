package tools.vitruv.change.testutils.printing;

import com.google.common.base.Preconditions;
import java.util.function.Supplier;

public final class PrintResultExtension {
  public static PrintResult operator_plus(final PrintResult previous, final PrintResult latest) {
    Preconditions.checkNotNull(previous, "previous result");
    Preconditions.checkNotNull(latest, "latest result");

    if (isInvalidTransition(previous, latest)) {
      throw invalidTransition(previous, latest);
    }

    if (previous == PrintResult.PRINTED || latest == PrintResult.PRINTED) {
      return PrintResult.PRINTED;
    }

    if (previous == PrintResult.PRINTED_NO_OUTPUT
            && latest == PrintResult.PRINTED_NO_OUTPUT) {
      return PrintResult.PRINTED_NO_OUTPUT;
    }

    return PrintResult.NOT_RESPONSIBLE;
  }

  private static boolean isInvalidTransition(
          final PrintResult previous, final PrintResult latest) {
    return previous == PrintResult.PRINTED && latest == PrintResult.NOT_RESPONSIBLE
            || previous == PrintResult.NOT_RESPONSIBLE && latest == PrintResult.PRINTED;
  }

  private static IllegalStateException invalidTransition(
          final PrintResult previous, final PrintResult latest) {
    return new IllegalStateException("Got " + latest + " after " + previous + "!");
  }

  private PrintResultExtension() {

  }
}
