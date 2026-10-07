package tools.vitruv.change.interaction.types;

/**
 * A factory for creating interactions.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public interface InteractionFactory {
  ConfirmationInteraction createConfirmationInteraction();

  NotificationInteraction createNotificationInteraction();

  TextInputInteraction createTextInputInteraction();

  MultipleChoiceSingleSelectionInteraction createMultipleChoiceSingleSelectionInteraction();

  MultipleChoiceMultipleSelectionInteraction createMultipleChoiceMultipleSelectionInteraction();
}
