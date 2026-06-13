package tools.vitruv.change.testutils.printing;

public class PrintMode {
  public static class Builder {
    private final int multiLineIfAtLeastItemCount;

    public PrintMode withSeparator(final String separator) {
      return new PrintMode(this.multiLineIfAtLeastItemCount, separator);
    }

    public Builder(final int multiLineIfAtLeastItemCount) {
      super();
      this.multiLineIfAtLeastItemCount = multiLineIfAtLeastItemCount;
    }
  }

  public static final PrintMode SINGLE_LINE_LIST = PrintMode.multiLineIfAtLeast(Integer.MAX_VALUE).withSeparator(", ");

  public static final PrintMode MULTI_LINE_LIST = PrintMode.multiLineIfAtLeast(1).withSeparator(",");

  private final int multiLineIfAtLeastItemCount;

  private final String separator;

  public static PrintMode.Builder multiLineIfAtLeast(final int count) {
    return new PrintMode.Builder(count);
  }

  public PrintMode(final int multiLineIfAtLeastItemCount, final String separator) {
    super();
    this.multiLineIfAtLeastItemCount = multiLineIfAtLeastItemCount;
    this.separator = separator;
  }
  public int getMultiLineIfAtLeastItemCount() {
    return this.multiLineIfAtLeastItemCount;
  }
  public String getSeparator() {
    return this.separator;
  }
}
