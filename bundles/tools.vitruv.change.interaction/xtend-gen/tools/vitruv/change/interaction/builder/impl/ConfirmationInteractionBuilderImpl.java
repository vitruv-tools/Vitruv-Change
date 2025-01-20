package tools.vitruv.change.interaction.builder.impl;

import tools.vitruv.change.interaction.ConfirmationUserInteraction;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.builder.ConfirmationInteractionBuilder;
import tools.vitruv.change.interaction.types.ConfirmationInteraction;
import tools.vitruv.change.interaction.types.InteractionFactory;

/**
 * Builder class for {@link ConfirmationInteraction}s.
 * Creates a dialog with a question and buttons to give a positive or negative answer.<br>
 * <br>
 * For further info on the rationale behind the ...InteractionBuilder implementation, see the {@link InteractionBuilder} javadoc.
 * 
 * @see ConfirmationInteractionBuilder
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class ConfirmationInteractionBuilderImpl extends BaseInteractionBuilder<Boolean, ConfirmationInteraction, ConfirmationInteractionBuilder.OptionalSteps> implements ConfirmationInteractionBuilder, ConfirmationInteractionBuilder.OptionalSteps {
  public ConfirmationInteractionBuilderImpl(final InteractionFactory interactionFactory, final Iterable<UserInteractionListener> userInteractionListener) {
    super(interactionFactory, userInteractionListener);
  }

  @Override
  public Boolean startInteraction() {
    final ConfirmationUserInteraction result = this.getInteractionToBuild().startInteraction();
    this.notifyUserInputReceived(result);
    return Boolean.valueOf(result.isConfirmed());
  }

  @Override
  public ConfirmationInteractionBuilder.OptionalSteps message(final String message) {
    this.setMessage(message);
    return this;
  }

  @Override
  public ConfirmationInteraction createUserInteraction() {
    return this.getInteractionFactory().createConfirmationInteraction();
  }

  @Override
  protected ConfirmationInteractionBuilder.OptionalSteps getSelf() {
    return this;
  }
}
