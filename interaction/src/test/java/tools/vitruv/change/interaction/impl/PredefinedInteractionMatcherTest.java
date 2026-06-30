package tools.vitruv.change.interaction.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.interaction.ConfirmationUserInteraction;
import tools.vitruv.change.interaction.InteractionFactory;

class PredefinedInteractionMatcherTest {
  private static final String MESSAGE = "Proceed?";

  private static ConfirmationUserInteraction confirmation(final String message,
      final boolean confirmed) {
    final ConfirmationUserInteraction interaction =
        InteractionFactory.eINSTANCE.createConfirmationUserInteraction();
    interaction.setMessage(message);
    interaction.setConfirmed(confirmed);
    return interaction;
  }

  @Test
  void getConfirmationResultReturnsConfirmedValueWhenMatchPresent() {
    final PredefinedInteractionMatcher matcher = new PredefinedInteractionMatcher();
    matcher.addInteraction(confirmation(MESSAGE, true));
    assertEquals(Optional.of(Boolean.TRUE), matcher.getConfirmationResult(MESSAGE));
  }

  @Test
  void getConfirmationResultReturnsFalseValueWhenMatchNotConfirmed() {
    final PredefinedInteractionMatcher matcher = new PredefinedInteractionMatcher();
    matcher.addInteraction(confirmation(MESSAGE, false));
    assertEquals(Optional.of(Boolean.FALSE), matcher.getConfirmationResult(MESSAGE));
  }

  @Test
  void getConfirmationResultReturnsEmptyWhenNoMatch() {
    final PredefinedInteractionMatcher matcher = new PredefinedInteractionMatcher();
    assertTrue(matcher.getConfirmationResult(MESSAGE).isEmpty());
  }

  @Test
  void getNotificationResultReturnsPresentWhenMatchPresent() {
    final PredefinedInteractionMatcher matcher = new PredefinedInteractionMatcher();
    matcher.addInteraction(confirmation(MESSAGE, false));
    assertEquals(Optional.of(Boolean.TRUE), matcher.getNotificationResult(MESSAGE));
  }

  @Test
  void getNotificationResultReturnsEmptyWhenNoMatch() {
    final PredefinedInteractionMatcher matcher = new PredefinedInteractionMatcher();
    assertTrue(matcher.getNotificationResult(MESSAGE).isEmpty());
  }

  @Test
  void getConfirmationResultConsumesMatchedInteraction() {
    final PredefinedInteractionMatcher matcher = new PredefinedInteractionMatcher();
    matcher.addInteraction(confirmation(MESSAGE, true));
    assertEquals(Optional.of(Boolean.TRUE), matcher.getConfirmationResult(MESSAGE));
    assertTrue(matcher.getConfirmationResult(MESSAGE).isEmpty());
  }
}
