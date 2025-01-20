package tools.vitruv.change.interaction.types;

import tools.vitruv.change.interaction.ConfirmationUserInteraction;
import tools.vitruv.change.interaction.InteractionFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * An interaction for asking the user to provide a positive or negative answer to some kind of request.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class ConfirmationInteraction extends BaseInteraction<ConfirmationUserInteraction> {
  private static final String DEFAULT_TITLE = "Please Confirm";

  private static final String DEFAULT_MESSAGE = "";

  protected ConfirmationInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality) {
    super(interactionResultProvider, windowModality, ConfirmationInteraction.DEFAULT_TITLE, ConfirmationInteraction.DEFAULT_MESSAGE);
  }

  @Override
  public ConfirmationUserInteraction startInteraction() {
    final boolean result = this.getInteractionResultProvider().getConfirmationInteractionResult(this.getWindowModality(), this.getTitle(), this.getMessage(), 
      this.getPositiveButtonText(), this.getNegativeButtonText(), this.getCancelButtonText());
    ConfirmationUserInteraction userInput = InteractionFactory.eINSTANCE.createConfirmationUserInteraction();
    userInput.setMessage(this.getMessage());
    userInput.setConfirmed(result);
    return userInput;
  }
}
