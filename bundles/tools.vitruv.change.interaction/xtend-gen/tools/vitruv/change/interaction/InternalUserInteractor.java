package tools.vitruv.change.interaction;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

/**
 * Internal version of the {@link UserInteractor} interface used to separate methods for internal "bookkeeping" from
 * methods for actual user interaction.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public interface InternalUserInteractor extends UserInteractor {
  /**
   * Registers the given {@link UserInteractionListener} to get notified about interactions.
   */
  void registerUserInputListener(final UserInteractionListener listener);

  /**
   * Removes the given {@link UserInteractionListener} from the list of listeners that get notified about interactions.
   */
  void deregisterUserInputListener(final UserInteractionListener listener);

  /**
   * Replaces the current {@link InteractionResultProvider} with the one provided by the given {@code replacementProvider}.
   * 
   * @param replacementProvider - function that gets the current {@link InteractionResultProvider} and returns the new one.
   * @result an {@link AutoCloseable} that will revert the change when being closed.
   */
  AutoCloseable replaceUserInteractionResultProvider(final Function1<? super InteractionResultProvider, ? extends InteractionResultProvider> replacementProvider);
}
