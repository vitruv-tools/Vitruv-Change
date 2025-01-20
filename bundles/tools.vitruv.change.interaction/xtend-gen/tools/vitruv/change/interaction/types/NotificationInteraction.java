package tools.vitruv.change.interaction.types;

import tools.vitruv.change.interaction.InteractionFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.NotificationUserInteraction;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * An interaction to notify the user about something.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class NotificationInteraction extends BaseInteraction<NotificationUserInteraction> {
  private static final String DEFAULT_TITLE = "NOTIFICATION";

  private static final String DEFAULT_MESSAGE = "";

  private UserInteractionOptions.NotificationType notificationType;

  protected NotificationInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality) {
    super(interactionResultProvider, windowModality, NotificationInteraction.DEFAULT_TITLE, NotificationInteraction.DEFAULT_MESSAGE);
    this.notificationType = UserInteractionOptions.NotificationType.INFORMATION;
    this.setPositiveButtonText("Okay");
  }

  public UserInteractionOptions.NotificationType getNotificationType() {
    return this.notificationType;
  }

  public UserInteractionOptions.NotificationType setNotificationType(final UserInteractionOptions.NotificationType type) {
    return this.notificationType = type;
  }

  @Override
  public NotificationUserInteraction startInteraction() {
    this.getInteractionResultProvider().getNotificationInteractionResult(this.getWindowModality(), this.getTitle(), this.getMessage(), this.getPositiveButtonText(), 
      this.notificationType);
    final NotificationUserInteraction userInput = InteractionFactory.eINSTANCE.createNotificationUserInteraction();
    userInput.setMessage(this.getMessage());
    return userInput;
  }
}
