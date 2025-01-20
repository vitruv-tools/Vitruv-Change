package tools.vitruv.change.interaction.builder.impl;

import tools.vitruv.change.interaction.NotificationUserInteraction;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.interaction.builder.NotificationInteractionBuilder;
import tools.vitruv.change.interaction.types.InteractionFactory;
import tools.vitruv.change.interaction.types.NotificationInteraction;

/**
 * Builder class for {@link NotificationInteraction}s.
 * Creates a dialog with a notification message and an icon depicting the severity.<br>
 * <br>
 * For further info on the rationale behind the ...InteractionBuilder implementation, see the {@link InteractionBuilder} javadoc.
 * @see NotificationInteractionBuilder
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class NotificationInteractionBuilderImpl extends BaseInteractionBuilder<Void, NotificationInteraction, NotificationInteractionBuilder.OptionalSteps> implements NotificationInteractionBuilder, NotificationInteractionBuilder.OptionalSteps {
  public NotificationInteractionBuilderImpl(final InteractionFactory interactionFactory, final Iterable<UserInteractionListener> userInteractionListener) {
    super(interactionFactory, userInteractionListener);
  }

  @Override
  public NotificationInteractionBuilder.OptionalSteps notificationType(final UserInteractionOptions.NotificationType notificationType) {
    if ((notificationType != null)) {
      NotificationInteraction _interactionToBuild = this.getInteractionToBuild();
      _interactionToBuild.setNotificationType(notificationType);
    }
    return this;
  }

  @Override
  public Void startInteraction() {
    final NotificationUserInteraction result = this.getInteractionToBuild().startInteraction();
    this.notifyUserInputReceived(result);
    return null;
  }

  @Override
  public NotificationInteraction createUserInteraction() {
    return this.getInteractionFactory().createNotificationInteraction();
  }

  @Override
  protected NotificationInteractionBuilder.OptionalSteps getSelf() {
    return this;
  }

  @Override
  public NotificationInteractionBuilder.OptionalSteps message(final String message) {
    this.setMessage(message);
    return this;
  }
}
