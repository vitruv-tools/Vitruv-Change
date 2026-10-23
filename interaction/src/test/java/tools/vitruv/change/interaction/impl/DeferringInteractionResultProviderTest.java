package tools.vitruv.change.interaction.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.interaction.PendingUserInteraction;
import tools.vitruv.change.interaction.PendingUserInteractionException;
import tools.vitruv.change.interaction.UserInteractionOptions.NotificationType;
import tools.vitruv.change.interaction.UserInteractionOptions.WindowModality;

class DeferringInteractionResultProviderTest {

  private final DeferringInteractionResultProvider provider =
      new DeferringInteractionResultProvider();

  @Test
  void interactionsThatNeedAnAnswer_throwWithTheirDetails() {
    List<String> pickOne = List.of("a", "b");
    assertPending(
        PendingUserInteraction.Kind.CONFIRMATION,
        "Confirm?",
        "no",
        List.of(),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getConfirmationInteractionResult(
                    WindowModality.MODAL, "T", "Confirm?", "yes", "no", "cancel")));
    assertPending(
        PendingUserInteraction.Kind.TEXT_INPUT,
        "Name?",
        null,
        List.of(),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getTextInputInteractionResult(
                    WindowModality.MODAL, "T", "Name?", "yes", "cancel", null)));
    assertPending(
        PendingUserInteraction.Kind.SINGLE_SELECTION,
        "Pick one",
        null,
        List.of("a", "b"),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getMultipleChoiceSingleSelectionInteractionResult(
                    WindowModality.MODAL, "T", "Pick one", "yes", "cancel", pickOne)));
    List<String> pickSome = List.of("x");
    assertPending(
        PendingUserInteraction.Kind.MULTIPLE_SELECTION,
        "Pick some",
        null,
        List.of("x"),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getMultipleChoiceMultipleSelectionInteractionResult(
                    WindowModality.MODAL, "T", "Pick some", "yes", "cancel", pickSome)));
  }

  @Test
  void notifications_doNotDefer() {
    assertDoesNotThrow(
        () ->
            provider.getNotificationInteractionResult(
                WindowModality.MODAL, "T", "Done", "ok", NotificationType.INFORMATION));
  }

  @Test
  void notifications_areRecordedAndForwardedInOrder() {
    provider.getNotificationInteractionResult(
        WindowModality.MODAL, "T", "First", "ok", NotificationType.WARNING);
    provider.getNotificationInteractionResult(
        WindowModality.MODELESS, "U", "Second", "fine", NotificationType.INFORMATION);
    List<String> received = new ArrayList<>();
    // Any provider will do as the target; this one writes down what it is notified of.
    DeferringInteractionResultProvider target =
        new DeferringInteractionResultProvider() {
          @Override
          public void getNotificationInteractionResult(
              WindowModality windowModality,
              String title,
              String message,
              String positiveDecisionText,
              NotificationType notificationType) {
            received.add(
                String.join(
                    " ",
                    windowModality.name(),
                    title,
                    message,
                    positiveDecisionText,
                    notificationType.name()));
          }
        };

    provider.forwardNotificationsTo(target);

    assertEquals(
        List.of("MODAL T First ok WARNING", "MODELESS U Second fine INFORMATION"), received);
  }

  @Test
  void forwardingNotifications_toNoTarget_doesNothing() {
    provider.getNotificationInteractionResult(
        WindowModality.MODAL, "T", "Done", "ok", NotificationType.INFORMATION);

    assertDoesNotThrow(() -> provider.forwardNotificationsTo(null));
  }

  @Test
  void findIn_locatesThePendingInteractionThroughWrappers() {
    PendingUserInteractionException pending =
        new PendingUserInteractionException(
            new PendingUserInteraction(
                PendingUserInteraction.Kind.CONFIRMATION, null, "m", null, null, null, null));

    assertSame(
        pending,
        PendingUserInteractionException.findIn(
            new RuntimeException(new IllegalStateException(pending))));
    assertNull(PendingUserInteractionException.findIn(new RuntimeException("other")));
  }

  private static void assertPending(
      PendingUserInteraction.Kind kind,
      String message,
      String negativeDecisionText,
      List<String> choices,
      PendingUserInteractionException exception) {
    PendingUserInteraction interaction = exception.getInteraction();
    assertEquals(kind, interaction.kind());
    assertEquals("T", interaction.title());
    assertEquals(message, interaction.message());
    assertEquals("yes", interaction.positiveDecisionText());
    assertEquals(negativeDecisionText, interaction.negativeDecisionText());
    assertEquals("cancel", interaction.cancelDecisionText());
    assertEquals(choices, interaction.choices());
  }
}
