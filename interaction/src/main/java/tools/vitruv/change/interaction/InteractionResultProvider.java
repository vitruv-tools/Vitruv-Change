package tools.vitruv.change.interaction;

import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * A provider for interaction results. Implementations can define how the result for a certain kind of input
 * can be generated. This can, for example, be a dialog or a predefined value.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public interface InteractionResultProvider {
  boolean getConfirmationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String negativeDecisionText, final String cancelDecisionText);

  void getNotificationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType);

  String getTextInputInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final UserInteractionOptions.InputValidator inputValidator);

  int getMultipleChoiceSingleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices);

  Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices);
}
