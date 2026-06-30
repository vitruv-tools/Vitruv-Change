package tools.vitruv.change.interaction.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions.InputValidator;
import tools.vitruv.change.interaction.UserInteractionOptions.NotificationType;
import tools.vitruv.change.interaction.UserInteractionOptions.WindowModality;

class PredefinedInteractionResultProviderImplTest {

  private InteractionResultProvider dummyFallback;
  private PredefinedInteractionResultProviderImpl providerWithFallback;
  private PredefinedInteractionResultProviderImpl providerWithoutFallback;

  @BeforeEach
  void setUp() {
    // A simple dummy fallback to test when predefined answers are not found
    dummyFallback = new InteractionResultProvider() {
      @Override
      public boolean getConfirmationInteractionResult(
          WindowModality windowModality, String title, String message,
          String positiveDecisionText, String negativeDecisionText, String cancelDecisionText) {
        return true; // dummy response
      }

      @Override
      public void getNotificationInteractionResult(
          WindowModality windowModality, String title, String message,
          String positiveDecisionText, NotificationType notificationType) {
        // Do nothing
      }

      @Override
      public String getTextInputInteractionResult(
          WindowModality windowModality, String title, String message,
          String positiveDecisionText, String cancelDecisionText, InputValidator inputValidator) {
        return "fallbackText";
      }

      @Override
      public int getMultipleChoiceSingleSelectionInteractionResult(
          WindowModality windowModality, String title, String message,
          String positiveDecisionText, String cancelDecisionText, Iterable<String> choices) {
        return 99; // dummy index
      }

      @Override
      public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
          WindowModality windowModality, String title, String message,
          String positiveDecisionText, String cancelDecisionText,
          Iterable<String> choices) {
          return Arrays.asList(98, 99);
        }
    };

    providerWithFallback = new PredefinedInteractionResultProviderImpl(dummyFallback);
    providerWithoutFallback = new PredefinedInteractionResultProviderImpl(null);
  }

  // --- Tests for getConfirmationInteractionResult ---

  @Test
  void testConfirmationFallbackUsedWhenNoPredefinedResult() {
    boolean result = providerWithFallback.getConfirmationInteractionResult(
        WindowModality.MODAL, "Title", "Message", "Yes", "No", "Cancel");
    assertTrue(result, "Fallback should return true as defined in dummy");
  }

  @Test
  void testConfirmationThrowsExceptionWhenNoFallbackAndNoPredefinedResult() {
    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      providerWithoutFallback.getConfirmationInteractionResult(
          WindowModality.MODAL, "Title", "Message", "Yes", "No", "Cancel");
    });
    assertTrue(exception.getMessage().contains("No input given for confirmation"));
  }

  // --- Tests for getNotificationInteractionResult ---

  @Test
  void testNotificationFallbackUsedWhenNoPredefinedResult() {
    assertDoesNotThrow(() -> {
      providerWithFallback.getNotificationInteractionResult(
          WindowModality.MODAL, "Title", "Message", "OK", null);
    });
  }

  // --- Tests for getTextInputInteractionResult ---

  @Test
  void testTextInputFallbackUsedWhenNoPredefinedResult() {
    String result = providerWithFallback.getTextInputInteractionResult(
        WindowModality.MODAL, "Title", "Message", "OK", "Cancel", null);
    assertEquals("fallbackText", result);
  }

  @Test
  void testTextInputThrowsExceptionWhenNoFallbackAndNoPredefinedResult() {
    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      providerWithoutFallback.getTextInputInteractionResult(
          WindowModality.MODAL, "Title", "Message", "OK", "Cancel", null);
    });
    assertTrue(exception.getMessage().contains("No input given for text input"));
  }

  // --- Tests for getMultipleChoiceSingleSelectionInteractionResult ---

  @Test
  void testSingleSelectionFallbackUsedWhenNoPredefinedResult() {
    List<String> choices = Arrays.asList("A", "B");
    int result = providerWithFallback.getMultipleChoiceSingleSelectionInteractionResult(
        WindowModality.MODAL, "Title", "Message", "OK", "Cancel", choices);
    assertEquals(99, result);
  }

  @Test
  void testSingleSelectionThrowsExceptionWhenNoFallbackAndNoPredefinedResult() {
    List<String> choices = Arrays.asList("A", "B");
    IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
    {
      providerWithoutFallback.getMultipleChoiceSingleSelectionInteractionResult(
          WindowModality.MODAL, "Title", "Message", "OK", "Cancel", choices);
    });
    assertTrue(exception.getMessage().contains("No input given for single selection"));
    assertTrue(exception.getMessage().contains("Message"));
    assertTrue(exception.getMessage().contains("A")); // tests the printSelection method indirectly
  }

  // --- Tests for getMultipleChoiceMultipleSelectionInteractionResult ---

  @Test
  void testMultipleSelectionFallbackUsedWhenNoPredefinedResult() {
    List<String> choices = Arrays.asList("A", "B");
    Iterable<Integer> result = providerWithFallback.
        getMultipleChoiceMultipleSelectionInteractionResult(
            WindowModality.MODAL, "Title", "Message", "OK", "Cancel", choices
        );

    List<Integer> resultList = (List<Integer>) result;
    assertEquals(2, resultList.size());
    assertTrue(resultList.contains(98));
  }

  @Test
  void testMultipleSelectionThrowsExceptionWhenNoFallbackAndNoPredefinedResult() {
    List<String> choices = Arrays.asList("A", "B");
    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      providerWithoutFallback.getMultipleChoiceMultipleSelectionInteractionResult(
          WindowModality.MODAL, "Title", "Message", "OK", "Cancel", choices);
    });
    assertTrue(exception.getMessage().contains("No input given for multiple selection"));
  }
}