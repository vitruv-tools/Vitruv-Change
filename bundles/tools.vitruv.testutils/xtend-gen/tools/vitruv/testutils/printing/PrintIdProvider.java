package tools.vitruv.testutils.printing;

@SuppressWarnings("all")
public interface PrintIdProvider {
  String getFallbackId(final Object object);
}
