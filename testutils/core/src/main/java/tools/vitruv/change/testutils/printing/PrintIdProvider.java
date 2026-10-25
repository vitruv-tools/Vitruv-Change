package tools.vitruv.change.testutils.printing;

@SuppressWarnings("all")
public interface PrintIdProvider {
  String getFallbackId(final Object object);
}
