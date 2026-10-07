package tools.vitruv.change.testutils.metamodels;

import java.nio.file.Path;

@SuppressWarnings("all")
public final class TestMetamodelsPathFactory {
  public static Path allElementTypes(final String modelName) {
    return Path.of((modelName + ".allElementTypes"));
  }

  public static Path allElementTypes2(final String modelName) {
    return Path.of((modelName + ".allElementTypes2"));
  }

  public static Path pcm_mockup(final String modelName) {
    return Path.of((modelName + ".pcm_mockup"));
  }

  public static Path uml_mockup(final String modelName) {
    return Path.of((modelName + ".uml_mockup"));
  }

  private TestMetamodelsPathFactory() {

  }
}
