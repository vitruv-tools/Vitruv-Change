package tools.vitruv.change.composite.description;

/**
 * A {@link CompositeContainerChange} defines a container for {@link VitruviusChange}s.
 * It does not have any specific semantics but containing one or more {@link VitruviusChange}s.
 */
@SuppressWarnings("all")
public interface CompositeContainerChange<Element extends Object> extends CompositeChange<Element, VitruviusChange<Element>> {
  @Override
  CompositeContainerChange<Element> copy();
}
