package tools.vitruv.change.interaction;

import tools.vitruv.change.interaction.impl.DialogInteractionResultProviderImpl;
import tools.vitruv.change.interaction.impl.PredefinedInteractionResultProviderImpl;
import tools.vitruv.change.interaction.impl.ThinktimeSimulatingInteractionResultProvider;
import tools.vitruv.change.interaction.impl.UserInteractorImpl;

/**
 * Factory for {@link UserInteraction}s and {@link InteractionResultProvider}.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class UserInteractionFactory {
  public static final UserInteractionFactory instance = new UserInteractionFactory();

  private UserInteractionFactory() {
  }

  /**
   * Creates a {@link InternalUserInteractor} with the given {@link InteractionResultProvider}.
   * 
   * @param interactionResultProvider - the provider for results of an interaction
   */
  public InternalUserInteractor createUserInteractor(final InteractionResultProvider interactionResultProvider) {
    return new UserInteractorImpl(interactionResultProvider);
  }

  /**
   * Creates a {@link InternalUserInteractor} with the a result provider based on UI dialogs.
   */
  public InternalUserInteractor createDialogUserInteractor() {
    InteractionResultProvider _createDialogInteractionResultProvider = this.createDialogInteractionResultProvider();
    return new UserInteractorImpl(_createDialogInteractionResultProvider);
  }

  /**
   * Creates a {@link InteralResultProvider} based on UI dialogs.
   */
  public InteractionResultProvider createDialogInteractionResultProvider() {
    return new DialogInteractionResultProviderImpl();
  }

  /**
   * Creates a {@link PredefinedInteractionResultProvider} on which used inputs can be predefined. The given {@link InteractionResultProvider}
   * is used as a fallback if not appropriate result is predefined.
   * 
   * @param fallbackResultProvider - the provider to be used if no input was predefined
   */
  public PredefinedInteractionResultProvider createPredefinedInteractionResultProvider(final InteractionResultProvider fallbackResultProvider) {
    return new PredefinedInteractionResultProviderImpl(fallbackResultProvider);
  }

  /**
   * Creates a {@link PredefinedInteractionResultProvider} on which the given {@link UserInteractionBase}s are used as predefined inputs.
   * The given {@link InteractionResultProvider} is used as a fallback if not appropriate result is predefined.
   * 
   * @param fallbackResultProvider - the provider to be used if no input was predefined
   */
  public PredefinedInteractionResultProvider createPredefinedInteractionResultProvider(final InteractionResultProvider fallbackResultProvider, final UserInteractionBase... predefinedUserInputs) {
    final PredefinedInteractionResultProvider result = this.createPredefinedInteractionResultProvider(fallbackResultProvider);
    result.addUserInteractions(predefinedUserInputs);
    return result;
  }

  /**
   * Creates a {@link InteractionResultProvider} which simulates waiting for the user. This can, for
   * example, be used for performance evaluations. The given {@code delegate} is used to actually provide results.
   * 
   * @param delegate - the provider to be used if no input was predefined
   * @param minWaittime - the minimum time to wait in milliseconds
   * @param maxWaittime - the maximum time to wait in milliseconds
   */
  public InteractionResultProvider createThinktimeSimulatingInteractionResultProvider(final InteractionResultProvider delegate, final int minWaittime, final int maxWaittime) {
    return new ThinktimeSimulatingInteractionResultProvider(delegate, minWaittime, maxWaittime);
  }
}
