package tools.vitruv.testutils;

import edu.kit.ipd.sdq.commons.util.org.eclipse.core.resources.IWorkspaceUtil;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Disables automatic building in an Eclipse workspace during the test, and sets it back
 * to its old value afterwards. Disables automatic building both before all tests and
 * before each test. That means that @BeforeAll
 */
@SuppressWarnings("all")
public class DisableAutoBuild implements BeforeAllCallback, BeforeEachCallback {
  private static class EnableAutoBuildGuard implements ExtensionContext.Store.CloseableResource {
    @Override
    public void close() throws Throwable {
      IWorkspaceUtil.turnOnAutoBuild(ResourcesPlugin.getWorkspace());
    }
  }

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {
    this.disableAutoBuilding(context);
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    this.disableAutoBuilding(context);
  }

  private void disableAutoBuilding(final ExtensionContext context) {
    try {
      boolean _turnOffAutoBuildIfOn = IWorkspaceUtil.turnOffAutoBuildIfOn(ResourcesPlugin.getWorkspace());
      if (_turnOffAutoBuildIfOn) {
        ExtensionContext.Store _store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        String _name = DisableAutoBuild.EnableAutoBuildGuard.class.getName();
        DisableAutoBuild.EnableAutoBuildGuard _enableAutoBuildGuard = new DisableAutoBuild.EnableAutoBuildGuard();
        _store.put(_name, _enableAutoBuildGuard);
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
