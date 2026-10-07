package tools.vitruv.change.interaction.types;

import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * Implementation of {@link InteractionFactory} that creates all possible types of interactions.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class InteractionFactoryImpl implements InteractionFactory {
  private final InteractionResultProvider interactionResultProvider;

  private final UserInteractionOptions.WindowModality windowModality;

  public InteractionFactoryImpl(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality) {
    this.windowModality = windowModality;
    this.interactionResultProvider = interactionResultProvider;
  }

  @Override
  public ConfirmationInteraction createConfirmationInteraction() {
    return new ConfirmationInteraction(this.interactionResultProvider, this.windowModality);
  }

  @Override
  public NotificationInteraction createNotificationInteraction() {
    return new NotificationInteraction(this.interactionResultProvider, this.windowModality);
  }

  @Override
  public TextInputInteraction createTextInputInteraction() {
    return new TextInputInteraction(this.interactionResultProvider, this.windowModality);
  }

  @Override
  public MultipleChoiceSingleSelectionInteraction createMultipleChoiceSingleSelectionInteraction() {
    return new MultipleChoiceSingleSelectionInteraction(this.interactionResultProvider, this.windowModality);
  }

  @Override
  public MultipleChoiceMultipleSelectionInteraction createMultipleChoiceMultipleSelectionInteraction() {
    return new MultipleChoiceMultipleSelectionInteraction(this.interactionResultProvider, this.windowModality);
  }
}
