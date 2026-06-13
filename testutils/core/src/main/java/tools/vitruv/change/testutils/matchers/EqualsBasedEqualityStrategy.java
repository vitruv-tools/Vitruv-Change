package tools.vitruv.change.testutils.matchers;

import java.util.Objects;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import java.util.function.Consumer;
import tools.vitruv.change.testutils.printing.TestMessages;

class EqualsBasedEqualityStrategy implements EqualityStrategy {
  private final Set<EClass> targetEClasses;

  @Override
  public EqualityStrategy.Result compare(final EObject left, final EObject right) {
    EqualityStrategy.Result _xifexpression = null;
    if (((!this.isTargetInstance(left)) || (!this.isTargetInstance(right)))) {
      _xifexpression = EqualityStrategy.Result.UNKNOWN;
    } else {
      EqualityStrategy.Result _xifexpression_1 = null;
      boolean _equals = Objects.equals(left, right);
      if (_equals) {
        _xifexpression_1 = EqualityStrategy.Result.EQUAL;
      } else {
        _xifexpression_1 = EqualityStrategy.Result.UNEQUAL;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  public boolean isTargetInstance(final EObject object) {
    return (this.targetEClasses.contains(object.eClass())
        || this.targetEClasses.stream().anyMatch(it -> it.isInstance(object)));
  }

  @Override
  public void describeTo(final StringBuilder builder) {
    final Consumer<EClass> _function = (EClass it) -> {
      builder.append(TestMessages.plural(it.getName()));
    };
    TestMessages.<EClass>joinSemantic(builder.append("used equals() to compare referenced "), this.targetEClasses,
        "and", _function);
  }

  public EqualsBasedEqualityStrategy(final Set<EClass> targetEClasses) {
    super();
    this.targetEClasses = targetEClasses;
  }
}
