package tools.vitruv.change.interaction.impl;

import java.util.ArrayList;
import java.util.List;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.PendingUserInteraction;
import tools.vitruv.change.interaction.PendingUserInteractionException;
import tools.vitruv.change.interaction.UserInteractionOptions.InputValidator;
import tools.vitruv.change.interaction.UserInteractionOptions.NotificationType;
import tools.vitruv.change.interaction.UserInteractionOptions.WindowModality;

/**
 * An {@link InteractionResultProvider} that never blocks: every interaction that needs an answer
 * throws a {@link PendingUserInteractionException} describing it.
 *
 * <p>Meant as the fallback behind a {@link PredefinedInteractionResultProviderImpl} holding the
 * answers known so far: known interactions are answered, unknown ones defer the propagation.
 * Notifications need no answer and are therefore ignored instead of deferring.
 */
public class DeferringInteractionResultProvider implements InteractionResultProvider {

  @Override
  public boolean getConfirmationInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String negativeDecisionText,
      String cancelDecisionText) {
    throw pending(PendingUserInteraction.Kind.CONFIRMATION, title, message, null);
  }

  @Override
  public void getNotificationInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      NotificationType notificationType) {
    // Nothing to wait for: a notification has no result.
  }

  @Override
  public String getTextInputInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String cancelDecisionText,
      InputValidator inputValidator) {
    throw pending(PendingUserInteraction.Kind.TEXT_INPUT, title, message, null);
  }

  @Override
  public int getMultipleChoiceSingleSelectionInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String cancelDecisionText,
      Iterable<String> choices) {
    throw pending(PendingUserInteraction.Kind.SINGLE_SELECTION, title, message, choices);
  }

  @Override
  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String cancelDecisionText,
      Iterable<String> choices) {
    throw pending(PendingUserInteraction.Kind.MULTIPLE_SELECTION, title, message, choices);
  }

  private static PendingUserInteractionException pending(
      PendingUserInteraction.Kind kind, String title, String message, Iterable<String> choices) {
    List<String> choiceList = new ArrayList<>();
    if (choices != null) {
      choices.forEach(choiceList::add);
    }
    return new PendingUserInteractionException(
        new PendingUserInteraction(kind, title, message, choiceList));
  }
}
