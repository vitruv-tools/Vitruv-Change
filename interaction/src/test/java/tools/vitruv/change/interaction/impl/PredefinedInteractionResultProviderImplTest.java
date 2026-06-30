package tools.vitruv.change.interaction.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tools.vitruv.change.interaction.ConfirmationUserInteraction;
import tools.vitruv.change.interaction.InteractionFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions.InputValidator;
import tools.vitruv.change.interaction.UserInteractionOptions.NotificationType;
import tools.vitruv.change.interaction.UserInteractionOptions.WindowModality;

class PredefinedInteractionResultProviderImplTest {
  private static final String MESSAGE = "Proceed?";
  private static final String TITLE = "Confirm";

  private static ConfirmationUserInteraction confirmation(final String message,
      final boolean confirmed) {
    final ConfirmationUserInteraction interaction =
        InteractionFactory.eINSTANCE.createConfirmationUserInteraction();
    interaction.setMessage(message);
    interaction.setConfirmed(confirmed);
    return interaction;
  }

  @Test
  void confirmationReturnsPredefinedResultWithoutConsultingFallback() {
    final RecordingResultProvider fallback = new RecordingResultProvider();
    final PredefinedInteractionResultProviderImpl provider =
        new PredefinedInteractionResultProviderImpl(fallback);
    provider.addUserInteractions(confirmation(MESSAGE, true));

    final boolean result = provider.getConfirmationInteractionResult(
        WindowModality.MODAL, TITLE, MESSAGE, "Yes", "No", "Cancel");

    assertTrue(result, "Predefined confirmation result should be returned");
    assertFalse(fallback.confirmationRequested, "Fallback must not be used when a match exists");
  }

  @Test
  void confirmationDelegatesToFallbackWhenNoPredefinedResult() {
    final RecordingResultProvider fallback = new RecordingResultProvider();
    fallback.confirmationResult = true;
    final PredefinedInteractionResultProviderImpl provider =
        new PredefinedInteractionResultProviderImpl(fallback);

    final boolean result = provider.getConfirmationInteractionResult(
        WindowModality.MODAL, TITLE, MESSAGE, "Yes", "No", "Cancel");

    assertTrue(result, "Fallback confirmation result should be returned");
    assertTrue(fallback.confirmationRequested, "Fallback should be consulted when no match exists");
  }

  @Test
  void confirmationThrowsWhenNoPredefinedResultAndNoFallback() {
    final PredefinedInteractionResultProviderImpl provider =
        new PredefinedInteractionResultProviderImpl(null);

    assertThrows(
        IllegalStateException.class,
        () -> provider.getConfirmationInteractionResult(
            WindowModality.MODAL, TITLE, MESSAGE, "Yes", "No", "Cancel"),
        "Missing input without a fallback should raise an exception");
  }

  @Test
  void notificationDelegatesToFallbackWhenNoPredefinedResult() {
    final RecordingResultProvider fallback = new RecordingResultProvider();
    final PredefinedInteractionResultProviderImpl provider =
        new PredefinedInteractionResultProviderImpl(fallback);

    provider.getNotificationInteractionResult(
        WindowModality.MODAL, TITLE, MESSAGE, "Ok", NotificationType.INFORMATION);

    assertTrue(fallback.notificationRequested, "Fallback should be notified when no match exists");
  }

  @Test
  void notificationDoesNotDelegateWhenPredefinedResultPresent() {
    final RecordingResultProvider fallback = new RecordingResultProvider();
    final PredefinedInteractionResultProviderImpl provider =
        new PredefinedInteractionResultProviderImpl(fallback);
    provider.addUserInteractions(confirmation(MESSAGE, false));

    provider.getNotificationInteractionResult(
        WindowModality.MODAL, TITLE, MESSAGE, "Ok", NotificationType.INFORMATION);

    assertFalse(fallback.notificationRequested, "Fallback must not be used when a match exists");
  }

  @Test
  void notificationDoesNotThrowWhenNoPredefinedResultAndNoFallback() {
    final PredefinedInteractionResultProviderImpl provider =
        new PredefinedInteractionResultProviderImpl(null);

    // Without a fallback and without a match, the notification is simply ignored.
    assertDoesNotThrow(
        () -> provider.getNotificationInteractionResult(
            WindowModality.MODAL, TITLE, MESSAGE, "Ok", NotificationType.INFORMATION),
        "Notification without a match or fallback should be silently ignored");
  }

  /** Minimal {@link InteractionResultProvider} stub recording whether each method was invoked. */
  private static final class RecordingResultProvider implements InteractionResultProvider {
    private boolean confirmationRequested;
    private boolean notificationRequested;
    private boolean confirmationResult;

    @Override
    public boolean getConfirmationInteractionResult(final WindowModality windowModality,
        final String title, final String message, final String positiveDecisionText,
        final String negativeDecisionText, final String cancelDecisionText) {
      this.confirmationRequested = true;
      return this.confirmationResult;
    }

    @Override
    public void getNotificationInteractionResult(final WindowModality windowModality,
        final String title, final String message, final String positiveDecisionText,
        final NotificationType notificationType) {
      this.notificationRequested = true;
    }

    @Override
    public String getTextInputInteractionResult(final WindowModality windowModality,
        final String title, final String message, final String positiveDecisionText,
        final String cancelDecisionText, final InputValidator inputValidator) {
      return "";
    }

    @Override
    public int getMultipleChoiceSingleSelectionInteractionResult(
        final WindowModality windowModality, final String title, final String message,
        final String positiveDecisionText, final String cancelDecisionText,
        final Iterable<String> choices) {
      return 0;
    }

    @Override
    public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
        final WindowModality windowModality, final String title, final String message,
        final String positiveDecisionText, final String cancelDecisionText,
        final Iterable<String> choices) {
      return java.util.List.of();
    }
  }
}
