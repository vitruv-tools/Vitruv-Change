package tools.vitruv.change.interaction;

/**
 * A {@link InteractionResultProvider} which allows to predefine the performed inputs.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public interface PredefinedInteractionResultProvider extends InteractionResultProvider {
  /**
   * Adds user interaction results to be used whenever an interaction is requested.
   * 
   * @param interactions - the interaction results to use
   */
  void addUserInteractions(final UserInteractionBase... interactions);
}
