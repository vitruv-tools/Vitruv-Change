package tools.vitruv.change.interaction.types;

import com.google.common.collect.Iterables;
import tools.vitruv.change.interaction.InteractionFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.MultipleChoiceMultiSelectionUserInteraction;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * Implementation of an interaction providing a list of choices for the user to select multiple ones
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class MultipleChoiceMultipleSelectionInteraction extends MultipleChoiceSelectionInteraction<MultipleChoiceMultiSelectionUserInteraction> {
  protected MultipleChoiceMultipleSelectionInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality) {
    super(interactionResultProvider, windowModality);
  }

  @Override
  public MultipleChoiceMultiSelectionUserInteraction startInteraction() {
    final Iterable<Integer> result = this.getInteractionResultProvider().getMultipleChoiceMultipleSelectionInteractionResult(this.getWindowModality(), this.getTitle(), this.getMessage(), this.getPositiveButtonText(), 
      this.getCancelButtonText(), this.getChoices());
    final MultipleChoiceMultiSelectionUserInteraction userInput = InteractionFactory.eINSTANCE.createMultipleChoiceMultiSelectionUserInteraction();
    userInput.setMessage(this.getMessage());
    userInput.getChoices().addAll(this.getChoices());
    Iterables.<Integer>addAll(userInput.getSelectedIndices(), result);
    return userInput;
  }
}
