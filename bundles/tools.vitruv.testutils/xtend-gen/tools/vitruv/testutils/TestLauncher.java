package tools.vitruv.testutils;

import edu.kit.ipd.sdq.activextendannotations.Lazy;
import org.eclipse.xtext.xbase.lib.Functions.Function1;

@SuppressWarnings("all")
public class TestLauncher {
  public enum Type {
    ECLIPSE,

    SUREFIRE,

    UNKNOWN;
  }

  @Lazy
  private static TestLauncher.Type _current;

  public static TestLauncher.Type currentTestLauncher() {
    return TestLauncher.getCurrent();
  }

  private static boolean _current_isInitialised = false;

  private static TestLauncher.Type _current_initialise() {
    final Function1<Object, TestLauncher.Type> _function = (Object it) -> {
      final String eclipseApplication = System.getProperty("eclipse.application");
      TestLauncher.Type _xifexpression = null;
      if ((eclipseApplication == null)) {
        _xifexpression = TestLauncher.Type.UNKNOWN;
      } else {
        TestLauncher.Type _xifexpression_1 = null;
        boolean _contains = eclipseApplication.contains("org.eclipse.pde.junit");
        if (_contains) {
          _xifexpression_1 = TestLauncher.Type.ECLIPSE;
        } else {
          TestLauncher.Type _xifexpression_2 = null;
          boolean _contains_1 = eclipseApplication.contains("surefire");
          if (_contains_1) {
            _xifexpression_2 = TestLauncher.Type.SUREFIRE;
          } else {
            _xifexpression_2 = TestLauncher.Type.UNKNOWN;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      return _xifexpression;
    };
    TestLauncher.Type _apply = _function.apply(null);
    return _apply;
  }

  public static TestLauncher.Type getCurrent() {
    if (!_current_isInitialised) {
    	try {
    		_current = _current_initialise();
    	} finally {
    		_current_isInitialised = true;
    	}
    }
    return _current;
  }
}
