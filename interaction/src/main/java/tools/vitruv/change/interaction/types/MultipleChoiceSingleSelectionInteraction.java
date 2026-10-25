package tools.vitruv.change.interaction.types;

import tools.vitruv.change.interaction.InteractionFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.MultipleChoiceSingleSelectionUserInteraction;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * Implementation of an interaction providing a list of choices for the user to select a single one.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class MultipleChoiceSingleSelectionInteraction extends MultipleChoiceSelectionInteraction<MultipleChoiceSingleSelectionUserInteraction> {
  protected MultipleChoiceSingleSelectionInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality) {
    super(interactionResultProvider, windowModality);
  }

  @Override
  public MultipleChoiceSingleSelectionUserInteraction startInteraction() {
    final int result = this.getInteractionResultProvider().getMultipleChoiceSingleSelectionInteractionResult(this.getWindowModality(), this.getTitle(), this.getMessage(), this.getPositiveButtonText(), 
      this.getCancelButtonText(), this.getChoices());
    final MultipleChoiceSingleSelectionUserInteraction userInput = InteractionFactory.eINSTANCE.createMultipleChoiceSingleSelectionUserInteraction();
    userInput.setMessage(this.getMessage());
    userInput.getChoices().addAll(this.getChoices());
    userInput.setSelectedIndex(result);
    return userInput;
  }
}
