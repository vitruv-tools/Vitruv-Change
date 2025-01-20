package tools.vitruv.testutils;

import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit extension that registers all EMF models that are offered through
 * extension points when running in standalone mode.
 */
@SuppressWarnings("all")
public class RegisterMetamodelsInStandalone implements BeforeAllCallback {
  @Override
  public void beforeAll(final ExtensionContext context) {
    EcorePlugin.ExtensionProcessor.process(null);
  }
}
