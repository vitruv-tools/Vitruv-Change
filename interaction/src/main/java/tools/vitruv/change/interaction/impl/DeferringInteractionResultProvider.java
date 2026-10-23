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
 * Notifications need no answer, so they do not defer; they are recorded instead and can be passed
 * on with {@link #forwardNotificationsTo(InteractionResultProvider)}.
 */
public class DeferringInteractionResultProvider implements InteractionResultProvider {

  /** The arguments of one call of {@link #getNotificationInteractionResult}. */
  private record Notification(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      NotificationType notificationType) {}

  private final List<Notification> notifications = new ArrayList<>();

  @Override
  public boolean getConfirmationInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String negativeDecisionText,
      String cancelDecisionText) {
    throw pending(
        new PendingUserInteraction(
            PendingUserInteraction.Kind.CONFIRMATION,
            title,
            message,
            positiveDecisionText,
            negativeDecisionText,
            cancelDecisionText,
            null));
  }

  @Override
  public void getNotificationInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      NotificationType notificationType) {
    // Nothing to wait for: a notification has no result.
    notifications.add(
        new Notification(windowModality, title, message, positiveDecisionText, notificationType));
  }

  /**
   * Passes the notifications recorded so far on to another provider, in the order they were
   * received.
   *
   * @param target the provider to notify; nothing happens if it is {@code null}
   */
  public void forwardNotificationsTo(InteractionResultProvider target) {
    if (target == null) {
      return;
    }
    for (Notification notification : notifications) {
      target.getNotificationInteractionResult(
          notification.windowModality(),
          notification.title(),
          notification.message(),
          notification.positiveDecisionText(),
          notification.notificationType());
    }
  }

  @Override
  public String getTextInputInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String cancelDecisionText,
      InputValidator inputValidator) {
    throw pending(
        new PendingUserInteraction(
            PendingUserInteraction.Kind.TEXT_INPUT,
            title,
            message,
            positiveDecisionText,
            null,
            cancelDecisionText,
            null));
  }

  @Override
  public int getMultipleChoiceSingleSelectionInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String cancelDecisionText,
      Iterable<String> choices) {
    throw pending(
        new PendingUserInteraction(
            PendingUserInteraction.Kind.SINGLE_SELECTION,
            title,
            message,
            positiveDecisionText,
            null,
            cancelDecisionText,
            toList(choices)));
  }

  @Override
  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
      WindowModality windowModality,
      String title,
      String message,
      String positiveDecisionText,
      String cancelDecisionText,
      Iterable<String> choices) {
    throw pending(
        new PendingUserInteraction(
            PendingUserInteraction.Kind.MULTIPLE_SELECTION,
            title,
            message,
            positiveDecisionText,
            null,
            cancelDecisionText,
            toList(choices)));
  }

  private static PendingUserInteractionException pending(PendingUserInteraction interaction) {
    return new PendingUserInteractionException(interaction);
  }

  private static List<String> toList(Iterable<String> choices) {
    List<String> choiceList = new ArrayList<>();
    if (choices != null) {
      choices.forEach(choiceList::add);
    }
    return choiceList;
  }
}
