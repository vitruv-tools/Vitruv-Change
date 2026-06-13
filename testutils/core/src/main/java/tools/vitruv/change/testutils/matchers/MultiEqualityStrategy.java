package tools.vitruv.change.testutils.matchers;

import java.util.List;
import java.util.Objects;
import org.eclipse.emf.ecore.EObject;
import java.util.function.BiConsumer;
import tools.vitruv.change.testutils.printing.TestMessages;
class MultiEqualityStrategy implements EqualityStrategy {
  private final List<? extends EqualityStrategy> strategies;

  @Override
  public EqualityStrategy.Result compare(final EObject left, final EObject right) {
    for (final EqualityStrategy strategy : this.strategies) {
      {
        final EqualityStrategy.Result strategyResult = strategy.compare(left, right);
        boolean _notEquals = (!Objects.equals(strategyResult, EqualityStrategy.Result.UNKNOWN));
        if (_notEquals) {
          return strategyResult;
        }
      }
    }
    return EqualityStrategy.Result.UNKNOWN;
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final BiConsumer<StringBuilder, EqualityStrategy> _function = (StringBuilder target, EqualityStrategy it) -> {
      it.describeTo(target);
    };
    TestMessages.<EqualityStrategy>joinSemantic(this.strategies, "and", ";", _function);
  }

  public MultiEqualityStrategy(final List<? extends EqualityStrategy> strategies) {
    super();
    this.strategies = strategies;
  }
}
