package tools.vitruv.change.interaction.builder.impl;

import tools.vitruv.change.interaction.MultipleChoiceSingleSelectionUserInteraction;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.builder.MultipleChoiceSelectionInteractionBuilder;
import tools.vitruv.change.interaction.builder.MultipleChoiceSingleSelectionInteractionBuilder;
import tools.vitruv.change.interaction.types.InteractionFactory;
import tools.vitruv.change.interaction.types.MultipleChoiceSingleSelectionInteraction;

/**
 * Builder class for {@link MultipleChoiceSelectionInteraction}s.
 * Creates a dialog with check boxes or radio buttons to pick multiple or a single entry from a list of choices.<br>
 * <br>
 * For further info on the rationale behind the ...InteractionBuilder implementation, see the {@link InteractionBuilder} javadoc.
 * @see MultipleChoiceSelectionInteractionBuilder
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class MultipleChoiceSingleSelectionInteractionBuilderImpl extends MultipleChoiceSelectionInteractionBuilderBaseImpl<Integer, MultipleChoiceSingleSelectionInteraction> implements MultipleChoiceSingleSelectionInteractionBuilder {
  public MultipleChoiceSingleSelectionInteractionBuilderImpl(final InteractionFactory interactionFactory, final Iterable<UserInteractionListener> userInteractionListener) {
    super(interactionFactory, userInteractionListener);
  }

  @Override
  public MultipleChoiceSingleSelectionInteraction createUserInteraction() {
    return this.getInteractionFactory().createMultipleChoiceSingleSelectionInteraction();
  }

  @Override
  public Integer startInteraction() {
    final MultipleChoiceSingleSelectionUserInteraction result = this.getInteractionToBuild().startInteraction();
    this.notifyUserInputReceived(result);
    return Integer.valueOf(result.getSelectedIndex());
  }

  @Override
  protected MultipleChoiceSelectionInteractionBuilder.OptionalSteps<Integer> getSelf() {
    return this;
  }
}
