package tools.vitruv.testutils.matchers;

import com.google.common.base.Objects;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tools.vitruv.testutils.printing.TestMessages;

@FinalFieldsConstructor
@SuppressWarnings("all")
class EqualsBasedEqualityStrategy implements EqualityStrategy {
  private final Set<EClass> targetEClasses;

  @Override
  public EqualityStrategy.Result compare(final EObject left, final EObject right) {
    EqualityStrategy.Result _xifexpression = null;
    if (((!this.isTargetInstance(left)) || (!this.isTargetInstance(right)))) {
      _xifexpression = EqualityStrategy.Result.UNKNOWN;
    } else {
      EqualityStrategy.Result _xifexpression_1 = null;
      boolean _equals = Objects.equal(left, right);
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
    return (this.targetEClasses.contains(object.eClass()) || IterableExtensions.<EClass>exists(this.targetEClasses, ((Function1<EClass, Boolean>) (EClass it) -> {
      return Boolean.valueOf(it.isInstance(object));
    })));
  }

  @Override
  public void describeTo(@Extension final StringBuilder builder) {
    final Procedure1<EClass> _function = (EClass it) -> {
      builder.append(TestMessages.plural(it.getName()));
    };
    TestMessages.<EClass>joinSemantic(builder.append("used equals() to compare referenced "), this.targetEClasses, "and", _function);
  }

  public EqualsBasedEqualityStrategy(final Set<EClass> targetEClasses) {
    super();
    this.targetEClasses = targetEClasses;
  }
}
