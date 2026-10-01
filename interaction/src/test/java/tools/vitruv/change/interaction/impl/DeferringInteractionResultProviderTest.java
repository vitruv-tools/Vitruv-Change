package tools.vitruv.change.interaction.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        List.of(),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getConfirmationInteractionResult(
                    WindowModality.MODAL, "T", "Confirm?", "yes", "no", "cancel")));
    assertPending(
        PendingUserInteraction.Kind.TEXT_INPUT,
        "Name?",
        List.of(),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getTextInputInteractionResult(
                    WindowModality.MODAL, "T", "Name?", "ok", "cancel", null)));
    assertPending(
        PendingUserInteraction.Kind.SINGLE_SELECTION,
        "Pick one",
        List.of("a", "b"),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getMultipleChoiceSingleSelectionInteractionResult(
                    WindowModality.MODAL, "T", "Pick one", "ok", "cancel", pickOne)));
    List<String> pickSome = List.of("x");
    assertPending(
        PendingUserInteraction.Kind.MULTIPLE_SELECTION,
        "Pick some",
        List.of("x"),
        assertThrows(
            PendingUserInteractionException.class,
            () ->
                provider.getMultipleChoiceMultipleSelectionInteractionResult(
                    WindowModality.MODAL, "T", "Pick some", "ok", "cancel", pickSome)));
  }

  @Test
  void notifications_doNotDefer() {
    assertDoesNotThrow(
        () ->
            provider.getNotificationInteractionResult(
                WindowModality.MODAL, "T", "Done", "ok", NotificationType.INFORMATION));
  }

  @Test
  void findIn_locatesThePendingInteractionThroughWrappers() {
    PendingUserInteractionException pending =
        new PendingUserInteractionException(
            new PendingUserInteraction(PendingUserInteraction.Kind.CONFIRMATION, null, "m", null));

    assertSame(
        pending,
        PendingUserInteractionException.findIn(
            new RuntimeException(new IllegalStateException(pending))));
    assertEquals(null, PendingUserInteractionException.findIn(new RuntimeException("other")));
  }

  private static void assertPending(
      PendingUserInteraction.Kind kind,
      String message,
      List<String> choices,
      PendingUserInteractionException exception) {
    PendingUserInteraction interaction = exception.getInteraction();
    assertEquals(kind, interaction.kind());
    assertEquals("T", interaction.title());
    assertEquals(message, interaction.message());
    assertEquals(choices, interaction.choices());
  }
}
