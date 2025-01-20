package tools.vitruv.testutils.matchers;

import com.google.common.base.Objects;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import tools.vitruv.testutils.printing.TestMessages;

@FinalFieldsConstructor
@SuppressWarnings("all")
class MultiEqualityStrategy implements EqualityStrategy {
  private final List<? extends EqualityStrategy> strategies;

  @Override
  public EqualityStrategy.Result compare(final EObject left, final EObject right) {
    for (final EqualityStrategy strategy : this.strategies) {
      {
        final EqualityStrategy.Result strategyResult = strategy.compare(left, right);
        boolean _notEquals = (!Objects.equal(strategyResult, EqualityStrategy.Result.UNKNOWN));
        if (_notEquals) {
          return strategyResult;
        }
      }
    }
    return EqualityStrategy.Result.UNKNOWN;
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final Procedure2<StringBuilder, EqualityStrategy> _function = (StringBuilder target, EqualityStrategy it) -> {
      it.describeTo(target);
    };
    TestMessages.<EqualityStrategy>joinSemantic(this.strategies, "and", ";", _function);
  }

  public MultiEqualityStrategy(final List<? extends EqualityStrategy> strategies) {
    super();
    this.strategies = strategies;
  }
}
