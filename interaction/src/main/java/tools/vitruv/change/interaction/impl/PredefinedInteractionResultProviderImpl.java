package tools.vitruv.change.interaction.impl;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.PredefinedInteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * An interaction result provider using predefined inputs, given using the
 * {@link PredefinedInteractionResultProvider#addUserInteractions(UserInteractionBase...)
 * addUserInteractions(UserInteractionBase...)}
 * method. The fallback result provider specified in the constructor is used,
 * whenever no matching predefined input for a
 * request is found.
 */
public class PredefinedInteractionResultProviderImpl implements PredefinedInteractionResultProvider {
  private final InteractionResultProvider fallback;

  private final PredefinedInteractionMatcher predefinedInteractionMatcher = new PredefinedInteractionMatcher();

  /**
   * The <code>fallback</code> is used whenever no matching input for a request
   * was predefined.
   * If no fallback provider is specified, retrieving a result throws an exception
   * whenever no
   * matching predefined input is found.
   * 
   * @param fallback - the result provider to use when no matching predefined
   *                 input is found
   */
  public PredefinedInteractionResultProviderImpl(final InteractionResultProvider fallback) {
    this.fallback = fallback;
  }

  @Override
  public void addUserInteractions(final UserInteractionBase... interactions) {
    final Consumer<UserInteractionBase> _function = (UserInteractionBase it) -> {
      this.predefinedInteractionMatcher.addInteraction(it);
    };
    Arrays.<UserInteractionBase>asList(interactions).forEach(_function);
  }

  @Override
  public boolean getConfirmationInteractionResult(final UserInteractionOptions.WindowModality windowModality,
      final String title, final String message, final String positiveDecisionText, final String negativeDecisionText,
      final String cancelDecisionText) {
    final Optional<Boolean> predefinedResult =
        this.predefinedInteractionMatcher.getConfirmationResult(message);
    if (predefinedResult.isPresent()) {
      return predefinedResult.get().booleanValue();
    }
    if (this.fallback == null) {
      throw new IllegalStateException(
          "No input given for confirmation:" + this.printInteraction(title, message));
    }
    return this.fallback.getConfirmationInteractionResult(windowModality, title, message,
        positiveDecisionText, negativeDecisionText, cancelDecisionText);
  }

  @Override
  public void getNotificationInteractionResult(final UserInteractionOptions.WindowModality windowModality,
      final String title, final String message, final String positiveDecisionText,
      final UserInteractionOptions.NotificationType notificationType) {
    final boolean noPredefinedResult =
        this.predefinedInteractionMatcher.getNotificationResult(message).isEmpty();
    if ((noPredefinedResult && (this.fallback != null))) {
      this.fallback.getNotificationInteractionResult(windowModality, title, message, positiveDecisionText,
          notificationType);
    }
  }

  @Override
  public String getTextInputInteractionResult(final UserInteractionOptions.WindowModality windowModality,
      final String title, final String message, final String positiveDecisionText, final String cancelDecisionText,
      final UserInteractionOptions.InputValidator inputValidator) {
    String _xblockexpression = null;
    {
      String result = this.predefinedInteractionMatcher.getTextInputResult(message);
      if (((result == null) && (this.fallback != null))) {
        result = this.fallback.getTextInputInteractionResult(windowModality, title, message,
            positiveDecisionText, cancelDecisionText, inputValidator);
      }
      final Supplier<String> _function = () -> {
        String _printInteraction = this.printInteraction(title, message);
        return ("No input given for text input: " + _printInteraction);
      };
      _xblockexpression = this.<String>ifNullThrow(result, _function);
    }
    return _xblockexpression;
  }

  @Override
  public int getMultipleChoiceSingleSelectionInteractionResult(
      final UserInteractionOptions.WindowModality windowModality, final String title, final String message,
      final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    Integer result = this.predefinedInteractionMatcher.getSingleSelectionResult(message, choices);
    if (((result == null) && (this.fallback != null))) {
      result = Integer.valueOf(this.fallback.getMultipleChoiceSingleSelectionInteractionResult(windowModality, title,
          message, positiveDecisionText, cancelDecisionText, choices));
    }
    final Supplier<String> _function = () -> {
      String _printSelection = this.printSelection(title, message, choices);
      return ("No input given for single selection:" + _printSelection);
    };
    return (this.<Integer>ifNullThrow(result, _function)).intValue();
  }

  @Override
  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
      final UserInteractionOptions.WindowModality windowModality, final String title, final String message,
      final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    Iterable<Integer> _xblockexpression = null;
    {
      Iterable<Integer> result = this.predefinedInteractionMatcher.getMultiSelectionResult(message, choices);
      if (((result == null) && (this.fallback != null))) {
        result = this.fallback.getMultipleChoiceMultipleSelectionInteractionResult(windowModality, title, message,
            positiveDecisionText, cancelDecisionText, choices);
      }
      final Supplier<String> _function = () -> {
        String _printSelection = this.printSelection(title, message, choices);
        return ("No input given for multiple selection:" + _printSelection);
      };
      _xblockexpression = this.<Iterable<Integer>>ifNullThrow(result, _function);
    }
    return _xblockexpression;
  }

  private String printInteraction(final String title, final String message) {
    return System.lineSeparator() + title + ": " + message;
  }

  private String printSelection(final String title, final String message, final Iterable<String> choices) {
    StringBuilder builder = new StringBuilder(this.printInteraction(title, message));
    for (final String c : choices) {
      builder.append(System.lineSeparator()).append("  \u2219 ").append(c);
    }
    return builder.toString();
  }

  private <T extends Object> T ifNullThrow(final T value, final Supplier<? extends String> messageProducer) {
    if ((value == null)) {
      throw new IllegalStateException(messageProducer.get());
    } else {
      return value;
    }
  }
}
