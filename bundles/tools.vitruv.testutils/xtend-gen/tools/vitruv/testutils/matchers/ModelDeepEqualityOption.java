package tools.vitruv.testutils.matchers;

@SuppressWarnings("all")
public interface ModelDeepEqualityOption {
  void describeTo(final StringBuilder builder);

  default String describe() {
    String _xblockexpression = null;
    {
      final StringBuilder target = new StringBuilder();
      this.describeTo(target);
      _xblockexpression = target.toString();
    }
    return _xblockexpression;
  }
}
