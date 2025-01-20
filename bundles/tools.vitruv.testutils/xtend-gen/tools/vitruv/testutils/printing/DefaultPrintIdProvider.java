package tools.vitruv.testutils.printing;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.eclipse.emf.ecore.EObject;

@SuppressWarnings("all")
public class DefaultPrintIdProvider implements PrintIdProvider {
  private final HashMap<Object, String> printed = new HashMap<Object, String>();

  private final HashMap<Object, Integer> classCount = new HashMap<Object, Integer>();

  @Override
  public String getFallbackId(final Object object) {
    final Function<Object, String> _function = (Object theObject) -> {
      String _xblockexpression = null;
      {
        Object _switchResult = null;
        boolean _matched = false;
        if (theObject instanceof EObject) {
          _matched=true;
          _switchResult = ((EObject)theObject).eClass();
        }
        if (!_matched) {
          _switchResult = theObject.getClass();
        }
        final Object classKey = _switchResult;
        final BiFunction<Object, Integer, Integer> _function_1 = (Object key, Integer value) -> {
          int _xifexpression = (int) 0;
          if ((value == null)) {
            _xifexpression = 1;
          } else {
            _xifexpression = ((value).intValue() + 1);
          }
          return Integer.valueOf(_xifexpression);
        };
        _xblockexpression = this.classCount.compute(classKey, _function_1).toString();
      }
      return _xblockexpression;
    };
    return this.printed.computeIfAbsent(object, _function);
  }
}
