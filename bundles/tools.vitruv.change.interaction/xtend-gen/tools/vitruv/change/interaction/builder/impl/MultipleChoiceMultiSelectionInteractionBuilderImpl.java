package tools.vitruv.change.interaction.builder.impl;

import java.util.Collection;
import tools.vitruv.change.interaction.MultipleChoiceMultiSelectionUserInteraction;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.builder.MultipleChoiceMultiSelectionInteractionBuilder;
import tools.vitruv.change.interaction.builder.MultipleChoiceSelectionInteractionBuilder;
import tools.vitruv.change.interaction.types.InteractionFactory;
import tools.vitruv.change.interaction.types.MultipleChoiceMultipleSelectionInteraction;

/**
 * Implementation of an interaction builder for multiple choice dialogs that allow multiple items to be selected.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class MultipleChoiceMultiSelectionInteractionBuilderImpl extends MultipleChoiceSelectionInteractionBuilderBaseImpl<Collection<Integer>, MultipleChoiceMultipleSelectionInteraction> implements MultipleChoiceMultiSelectionInteractionBuilder {
  public MultipleChoiceMultiSelectionInteractionBuilderImpl(final InteractionFactory interactionFactory, final Iterable<UserInteractionListener> userInteractionListener) {
    super(interactionFactory, userInteractionListener);
  }

  @Override
  public Collection<Integer> startInteraction() {
    final MultipleChoiceMultiSelectionUserInteraction result = this.getInteractionToBuild().startInteraction();
    this.notifyUserInputReceived(result);
    return result.getSelectedIndices();
  }

  @Override
  public MultipleChoiceMultipleSelectionInteraction createUserInteraction() {
    return this.getInteractionFactory().createMultipleChoiceMultipleSelectionInteraction();
  }

  @Override
  protected MultipleChoiceSelectionInteractionBuilder.OptionalSteps<Collection<Integer>> getSelf() {
    return this;
  }
}
