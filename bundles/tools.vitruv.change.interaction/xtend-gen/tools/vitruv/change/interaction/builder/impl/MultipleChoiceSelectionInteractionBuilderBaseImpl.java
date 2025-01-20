package tools.vitruv.change.interaction.builder.impl;

import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.Conversions;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.builder.MultipleChoiceSelectionInteractionBuilder;
import tools.vitruv.change.interaction.types.InteractionFactory;
import tools.vitruv.change.interaction.types.MultipleChoiceSelectionInteraction;

/**
 * Base implementation of the dialog builder for single- and multi-select {@link MultipleChoiceInteraction}s. Implementation
 * of {@link #showDialogAndGetUserInput()} is left up to the two concrete subclasses
 * {@link MultipleChoiceSingleSelectionInteractionBuilderImpl} and {@link MultipleChoiceMultiSelectionInteractionBuilderImpl} so
 * they can specify the return type while inheriting everything else from here.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public abstract class MultipleChoiceSelectionInteractionBuilderBaseImpl<T extends Object, I extends MultipleChoiceSelectionInteraction<?>> extends BaseInteractionBuilder<T, I, MultipleChoiceSelectionInteractionBuilder.OptionalSteps<T>> implements MultipleChoiceSelectionInteractionBuilder<T>, MultipleChoiceSelectionInteractionBuilder.ChoicesStep<T>, MultipleChoiceSelectionInteractionBuilder.OptionalSteps<T> {
  public MultipleChoiceSelectionInteractionBuilderBaseImpl(final InteractionFactory interactionFactory, final Iterable<UserInteractionListener> userInteractionListener) {
    super(interactionFactory, userInteractionListener);
  }

  @Override
  public MultipleChoiceSelectionInteractionBuilder.ChoicesStep<T> message(final String message) {
    this.setMessage(message);
    return this;
  }

  @Override
  public MultipleChoiceSelectionInteractionBuilder.OptionalSteps<T> choices(final Iterable<String> choices) {
    if (((choices == null) || (((Object[])Conversions.unwrapArray(choices, Object.class)).length < 2))) {
      throw new IllegalArgumentException("Provide at least two choices to pick from.");
    }
    final Consumer<String> _function = (String it) -> {
      this.getInteractionToBuild().addChoice(it);
    };
    choices.forEach(_function);
    return this;
  }
}
