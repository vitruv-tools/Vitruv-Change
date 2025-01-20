package tools.vitruv.change.interaction.impl;

import java.util.List;
import java.util.function.Consumer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.PredefinedInteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * An interaction result provider using predefined inputs, given using the
 * {@link PredefinedInteractionResultProvider#addUserInteractions(UserInteractionBase...) addUserInteractions(UserInteractionBase...)}
 * method. The fallback result provider specified in the constructor is used, whenever no matching predefined input for a
 * request is found.
 */
@SuppressWarnings("all")
public class PredefinedInteractionResultProviderImpl implements PredefinedInteractionResultProvider {
  private final InteractionResultProvider fallback;

  private final PredefinedInteractionMatcher predefinedInteractionMatcher = new PredefinedInteractionMatcher();

  /**
   * The <code>fallback</code> is used whenever no matching input for a request was predefined.
   * If no fallback provider is specified, retrieving a result throws an exception whenever no
   * matching predefined input is found.
   * 
   * @param fallback - the result provider to use when no matching predefined input is found
   */
  public PredefinedInteractionResultProviderImpl(final InteractionResultProvider fallback) {
    this.fallback = fallback;
  }

  @Override
  public void addUserInteractions(final UserInteractionBase... interactions) {
    final Consumer<UserInteractionBase> _function = (UserInteractionBase it) -> {
      this.predefinedInteractionMatcher.addInteraction(it);
    };
    ((List<UserInteractionBase>)Conversions.doWrapArray(interactions)).forEach(_function);
  }

  @Override
  public boolean getConfirmationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String negativeDecisionText, final String cancelDecisionText) {
    Boolean result = this.predefinedInteractionMatcher.getConfirmationResult(message);
    if (((result == null) && (this.fallback != null))) {
      result = Boolean.valueOf(this.fallback.getConfirmationInteractionResult(windowModality, title, message, positiveDecisionText, negativeDecisionText, cancelDecisionText));
    }
    final Function0<String> _function = () -> {
      String _printInteraction = this.printInteraction(title, message);
      return ("No input given for confirmation:" + _printInteraction);
    };
    return (this.<Boolean>ifNullThrow(result, _function)).booleanValue();
  }

  @Override
  public void getNotificationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType) {
    final Boolean result = this.predefinedInteractionMatcher.getNotificationResult(message);
    if (((result == null) && (this.fallback != null))) {
      this.fallback.getNotificationInteractionResult(windowModality, title, message, positiveDecisionText, notificationType);
    }
  }

  @Override
  public String getTextInputInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final UserInteractionOptions.InputValidator inputValidator) {
    String _xblockexpression = null;
    {
      final String result = this.predefinedInteractionMatcher.getTextInputResult(message);
      if (((result == null) && (this.fallback != null))) {
        this.fallback.getTextInputInteractionResult(windowModality, title, message, positiveDecisionText, cancelDecisionText, inputValidator);
      }
      final Function0<String> _function = () -> {
        String _printInteraction = this.printInteraction(title, message);
        return ("No input given for text input: " + _printInteraction);
      };
      _xblockexpression = this.<String>ifNullThrow(result, _function);
    }
    return _xblockexpression;
  }

  @Override
  public int getMultipleChoiceSingleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    Integer result = this.predefinedInteractionMatcher.getSingleSelectionResult(message, choices);
    if (((result == null) && (this.fallback != null))) {
      result = Integer.valueOf(this.fallback.getMultipleChoiceSingleSelectionInteractionResult(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices));
    }
    final Function0<String> _function = () -> {
      String _printSelection = this.printSelection(title, message, choices);
      return ("No input given for single selection:" + _printSelection);
    };
    return (this.<Integer>ifNullThrow(result, _function)).intValue();
  }

  @Override
  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    Iterable<Integer> _xblockexpression = null;
    {
      Iterable<Integer> result = this.predefinedInteractionMatcher.getMultiSelectionResult(message, choices);
      if (((result == null) && (this.fallback != null))) {
        result = this.fallback.getMultipleChoiceMultipleSelectionInteractionResult(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
      }
      final Function0<String> _function = () -> {
        String _printSelection = this.printSelection(title, message, choices);
        return ("No input given for multiple selection:" + _printSelection);
      };
      _xblockexpression = this.<Iterable<Integer>>ifNullThrow(result, _function);
    }
    return _xblockexpression;
  }

  private String printInteraction(final String title, final String message) {
    String _lineSeparator = System.lineSeparator();
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(title);
    _builder.append(": ");
    _builder.append(message);
    return (_lineSeparator + _builder);
  }

  private String printSelection(final String title, final String message, final Iterable<String> choices) {
    String _printInteraction = this.printInteraction(title, message);
    String _lineSeparator = System.lineSeparator();
    String _plus = (_printInteraction + _lineSeparator);
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _hasElements = false;
      for(final String c : choices) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          String _lineSeparator_1 = System.lineSeparator();
          _builder.appendImmediate(_lineSeparator_1, "");
        }
        _builder.append("  ");
        _builder.append("∙");
        _builder.append(" ");
        _builder.append(c);
      }
    }
    return (_plus + _builder);
  }

  private <T extends Object> T ifNullThrow(final T value, final Function0<? extends String> messageProducer) {
    if ((value == null)) {
      String _apply = messageProducer.apply();
      throw new IllegalStateException(_apply);
    } else {
      return value;
    }
  }
}
