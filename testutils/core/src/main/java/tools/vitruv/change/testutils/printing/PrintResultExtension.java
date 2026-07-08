package tools.vitruv.change.testutils.printing;

import com.google.common.base.Preconditions;

public final class PrintResultExtension {
  /**
   * Combines a previous and latest print result.
   *
   * @param previous the previous print result
   * @param latest the latest print result
   * @return the combined print result
   * @throws IllegalStateException if the transition between both results is invalid
   */
  public static PrintResult operatorPlus(final PrintResult previous, final PrintResult latest) {
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
