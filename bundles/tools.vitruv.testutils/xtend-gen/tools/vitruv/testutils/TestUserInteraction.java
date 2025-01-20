package tools.vitruv.testutils;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.testutils.printing.TestMessages;

@SuppressWarnings("all")
public class TestUserInteraction {
  @FinalFieldsConstructor
  public static class Response<Description extends Object, Result extends Object> {
    private final Function1<? super Description, ? extends Boolean> matcher;

    private final Result response;

    private final boolean repeatedly;

    public Response(final Function1<? super Description, ? extends Boolean> matcher, final Result response, final boolean repeatedly) {
      super();
      this.matcher = matcher;
      this.response = response;
      this.repeatedly = repeatedly;
    }
  }

  @FinalFieldsConstructor
  public static class SimpleResponseBuilder<Description extends Object, Result extends Object> {
    private final TestUserInteraction owner;

    private final List<TestUserInteraction.Response<Description, Result>> targetQueue;

    private final Function1<? super Description, ? extends Boolean> condition;

    private boolean repeatedly = false;

    public TestUserInteraction.SimpleResponseBuilder<Description, Result> always() {
      this.repeatedly = true;
      return this;
    }

    /**
     * Responds the interaction with the provided {@code result} if the condition this builder was created for holds.
     */
    public TestUserInteraction respondWith(final Result result) {
      TestUserInteraction.Response<Description, Result> _response = new TestUserInteraction.Response<Description, Result>(this.condition, result, this.repeatedly);
      this.targetQueue.add(_response);
      return this.owner;
    }

    public SimpleResponseBuilder(final TestUserInteraction owner, final List<TestUserInteraction.Response<Description, Result>> targetQueue, final Function1<? super Description, ? extends Boolean> condition) {
      super();
      this.owner = owner;
      this.targetQueue = targetQueue;
      this.condition = condition;
    }
  }

  @FinalFieldsConstructor
  public static class MultipleChoiceResponseBuilder {
    private final TestUserInteraction owner;

    private final Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Boolean> condition;

    private boolean repeatedly = false;

    public TestUserInteraction.MultipleChoiceResponseBuilder always() {
      this.repeatedly = true;
      return this;
    }

    public TestUserInteraction respondWith(final String result) {
      final Function1<String, Boolean> _function = (String it) -> {
        return Boolean.valueOf(Objects.equal(it, result));
      };
      return this.respondWithChoiceMatching(_function);
    }

    public TestUserInteraction respondWithChoiceAt(final int resultIndex) {
      final Function1<TestUserInteraction.MultipleChoiceInteractionDescription, Integer> _function = (TestUserInteraction.MultipleChoiceInteractionDescription it) -> {
        return Integer.valueOf(resultIndex);
      };
      TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>> _response = new TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>(this.condition, _function, this.repeatedly);
      this.owner.multipleChoices.add(_response);
      return this.owner;
    }

    public TestUserInteraction respondWithChoiceMatching(final Function1<? super String, ? extends Boolean> selector) {
      final Function1<TestUserInteraction.MultipleChoiceInteractionDescription, Integer> _function = (TestUserInteraction.MultipleChoiceInteractionDescription it) -> {
        return Integer.valueOf(TestUserInteraction.MultipleChoiceResponseBuilder.assertedIndexBy(it, selector));
      };
      TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>> _response = new TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>(this.condition, _function, this.repeatedly);
      this.owner.multipleChoices.add(_response);
      return this.owner;
    }

    private static int assertedIndexBy(final TestUserInteraction.MultipleChoiceInteractionDescription interaction, final Function1<? super String, ? extends Boolean> selector) {
      {
        int i = 0;
        final Iterator<String> choiceIt = interaction.choices.iterator();
        boolean _hasNext = choiceIt.hasNext();
        boolean _while = _hasNext;
        while (_while) {
          Boolean _apply = selector.apply(choiceIt.next());
          if ((_apply).booleanValue()) {
            return i;
          }
          int _i = i;
          i = (_i + 1);
          boolean _hasNext_1 = choiceIt.hasNext();
          _while = _hasNext_1;
        }
      }
      StringConcatenation _builder = new StringConcatenation();
      String _type = interaction.getType();
      _builder.append(_type);
      _builder.append(" without an acceptable choice:");
      String _lineSeparator = System.lineSeparator();
      _builder.append(_lineSeparator);
      _builder.append(interaction);
      throw new AssertionError(_builder);
    }

    public MultipleChoiceResponseBuilder(final TestUserInteraction owner, final Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Boolean> condition) {
      super();
      this.owner = owner;
      this.condition = condition;
    }
  }

  @FinalFieldsConstructor
  public static class MultipleChoiceMultipleSelectionResponseBuilder {
    private final TestUserInteraction owner;

    private final Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends Boolean> condition;

    private boolean repeatedly = false;

    public TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder always() {
      this.repeatedly = true;
      return this;
    }

    public TestUserInteraction respondWith(final String... results) {
      return this.respondWith(Set.<String>of(results));
    }

    public TestUserInteraction respondWith(final Set<String> results) {
      final Function1<String, Boolean> _function = (String it) -> {
        return Boolean.valueOf(results.contains(it));
      };
      return this.respondWithChoicesMatching(_function);
    }

    public TestUserInteraction respondWithChoicesAt(final int... resultIndeces) {
      final Function1<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, int[]> _function = (TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription it) -> {
        return resultIndeces;
      };
      TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>> _response = new TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>(this.condition, _function, this.repeatedly);
      this.owner.multipleChoiceMultipleSelections.add(_response);
      return this.owner;
    }

    public TestUserInteraction respondWithChoicesMatching(final Function1<? super String, ? extends Boolean> selector) {
      final Function1<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, int[]> _function = (TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription it) -> {
        return TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder.assertedIndecesBy(it, selector);
      };
      TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>> _response = new TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>(this.condition, _function, this.repeatedly);
      this.owner.multipleChoiceMultipleSelections.add(_response);
      return this.owner;
    }

    private static int[] assertedIndecesBy(final TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription interaction, final Function1<? super String, ? extends Boolean> selector) {
      final ArrayList<Integer> result = new ArrayList<Integer>();
      {
        int i = 0;
        final Iterator<String> choiceIt = interaction.getChoices().iterator();
        boolean _hasNext = choiceIt.hasNext();
        boolean _while = _hasNext;
        while (_while) {
          Boolean _apply = selector.apply(choiceIt.next());
          if ((_apply).booleanValue()) {
            result.add(Integer.valueOf(i));
          }
          int _i = i;
          i = (_i + 1);
          boolean _hasNext_1 = choiceIt.hasNext();
          _while = _hasNext_1;
        }
      }
      return ((int[])Conversions.unwrapArray(result, int.class));
    }

    public MultipleChoiceMultipleSelectionResponseBuilder(final TestUserInteraction owner, final Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends Boolean> condition) {
      super();
      this.owner = owner;
      this.condition = condition;
    }
  }

  @FinalFieldsConstructor
  @Accessors
  public static abstract class InteractionDescription {
    private final UserInteractionOptions.WindowModality windowModality;

    private final String title;

    private final String message;

    private final String positiveDecisionText;

    public abstract String getType();

    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(this.title);
      _builder.append(": ");
      _builder.append(this.message);
      return _builder.toString();
    }

    public InteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText) {
      super();
      this.windowModality = windowModality;
      this.title = title;
      this.message = message;
      this.positiveDecisionText = positiveDecisionText;
    }

    @Pure
    public UserInteractionOptions.WindowModality getWindowModality() {
      return this.windowModality;
    }

    @Pure
    public String getTitle() {
      return this.title;
    }

    @Pure
    public String getMessage() {
      return this.message;
    }

    @Pure
    public String getPositiveDecisionText() {
      return this.positiveDecisionText;
    }
  }

  @FinalFieldsConstructor
  @Accessors
  public static class ConfirmationInteractionDescription extends TestUserInteraction.InteractionDescription {
    private final String negativeDecisionText;

    private final String cancelDecisionText;

    @Override
    public String getType() {
      return "confirmation interaction";
    }

    public ConfirmationInteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String negativeDecisionText, final String cancelDecisionText) {
      super(windowModality, title, message, positiveDecisionText);
      this.negativeDecisionText = negativeDecisionText;
      this.cancelDecisionText = cancelDecisionText;
    }

    @Pure
    public String getNegativeDecisionText() {
      return this.negativeDecisionText;
    }

    @Pure
    public String getCancelDecisionText() {
      return this.cancelDecisionText;
    }
  }

  @FinalFieldsConstructor
  @Accessors
  public static class TextInputInteractionDescription extends TestUserInteraction.InteractionDescription {
    private final String cancelDecisionText;

    private final UserInteractionOptions.InputValidator inputValidator;

    @Override
    public String getType() {
      return "text input interaction";
    }

    public TextInputInteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final UserInteractionOptions.InputValidator inputValidator) {
      super(windowModality, title, message, positiveDecisionText);
      this.cancelDecisionText = cancelDecisionText;
      this.inputValidator = inputValidator;
    }

    @Pure
    public String getCancelDecisionText() {
      return this.cancelDecisionText;
    }

    @Pure
    public UserInteractionOptions.InputValidator getInputValidator() {
      return this.inputValidator;
    }
  }

  @FinalFieldsConstructor
  @Accessors
  public static class NotificationInteractionDescription extends TestUserInteraction.InteractionDescription {
    private final UserInteractionOptions.NotificationType notificationType;

    @Override
    public String toString() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(this.notificationType);
      _builder.append(" ");
      String _string = super.toString();
      _builder.append(_string);
      return _builder.toString();
    }

    @Override
    public String getType() {
      return "notification interaction";
    }

    public NotificationInteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType) {
      super(windowModality, title, message, positiveDecisionText);
      this.notificationType = notificationType;
    }

    @Pure
    public UserInteractionOptions.NotificationType getNotificationType() {
      return this.notificationType;
    }
  }

  @FinalFieldsConstructor
  @Accessors
  public static class MultipleChoiceInteractionDescription extends TestUserInteraction.InteractionDescription {
    private final String cancelDecisionText;

    private final Iterable<String> choices;

    @Override
    public String toString() {
      String _string = super.toString();
      final Function1<String, CharSequence> _function = (String it) -> {
        StringConcatenation _builder = new StringConcatenation();
        String _lineSeparator = System.lineSeparator();
        _builder.append(_lineSeparator);
        _builder.append("  • ");
        _builder.append(it);
        return _builder.toString();
      };
      String _join = IterableExtensions.<String>join(this.choices, "", _function);
      return (_string + _join);
    }

    @Override
    public String getType() {
      return "multiple choice interaction";
    }

    public MultipleChoiceInteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
      super(windowModality, title, message, positiveDecisionText);
      this.cancelDecisionText = cancelDecisionText;
      this.choices = choices;
    }

    @Pure
    public String getCancelDecisionText() {
      return this.cancelDecisionText;
    }

    @Pure
    public Iterable<String> getChoices() {
      return this.choices;
    }
  }

  public static class MultipleChoiceMultipleSelectionInteractionDescription extends TestUserInteraction.MultipleChoiceInteractionDescription {
    public MultipleChoiceMultipleSelectionInteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
      super(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
    }

    @Override
    public String getType() {
      return "multiple choice interaction with multiple selections";
    }
  }

  @FinalFieldsConstructor
  public static class ResultProvider implements InteractionResultProvider {
    private final TestUserInteraction source;

    @Override
    public boolean getConfirmationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String negativeDecisionText, final String cancelDecisionText) {
      TestUserInteraction.ConfirmationInteractionDescription _confirmationInteractionDescription = new TestUserInteraction.ConfirmationInteractionDescription(windowModality, title, message, positiveDecisionText, negativeDecisionText, cancelDecisionText);
      return (boolean) TestUserInteraction.ResultProvider.<TestUserInteraction.ConfirmationInteractionDescription, Boolean>requireMatchingInteraction(this.source.confirmations, _confirmationInteractionDescription);
    }

    @Override
    public void getNotificationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType) {
      TestUserInteraction.NotificationInteractionDescription _notificationInteractionDescription = new TestUserInteraction.NotificationInteractionDescription(windowModality, title, message, positiveDecisionText, notificationType);
      TestUserInteraction.ResultProvider.<TestUserInteraction.NotificationInteractionDescription, Void>requireMatchingInteraction(this.source.notificationReactions, _notificationInteractionDescription);
    }

    @Override
    public String getTextInputInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final UserInteractionOptions.InputValidator inputValidator) {
      TestUserInteraction.TextInputInteractionDescription _textInputInteractionDescription = new TestUserInteraction.TextInputInteractionDescription(windowModality, title, message, positiveDecisionText, cancelDecisionText, inputValidator);
      return TestUserInteraction.ResultProvider.<TestUserInteraction.TextInputInteractionDescription, String>requireMatchingInteraction(this.source.textInputs, _textInputInteractionDescription);
    }

    @Override
    public int getMultipleChoiceSingleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
      final TestUserInteraction.MultipleChoiceInteractionDescription interaction = new TestUserInteraction.MultipleChoiceInteractionDescription(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
      final Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer> resultProvider = TestUserInteraction.ResultProvider.<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>requireMatchingInteraction(this.source.multipleChoices, interaction);
      return (resultProvider.apply(interaction)).intValue();
    }

    @Override
    public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
      final TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription interaction = new TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
      final Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]> resultProvider = TestUserInteraction.ResultProvider.<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>requireMatchingInteraction(this.source.multipleChoiceMultipleSelections, interaction);
      return (Iterable<Integer>)Conversions.doWrapArray(resultProvider.apply(interaction));
    }

    private static <Description extends TestUserInteraction.InteractionDescription, Result extends Object> Result requireMatchingInteraction(final Iterable<TestUserInteraction.Response<Description, Result>> options, final Description interaction) {
      for (final Iterator<TestUserInteraction.Response<Description, Result>> optionsIt = options.iterator(); optionsIt.hasNext();) {
        {
          final TestUserInteraction.Response<Description, Result> option = optionsIt.next();
          Boolean _apply = option.matcher.apply(interaction);
          if ((_apply).booleanValue()) {
            if ((!option.repeatedly)) {
              optionsIt.remove();
            }
            return option.response;
          }
        }
      }
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("An unexpected ");
      String _type = interaction.getType();
      _builder.append(_type);
      _builder.append(" occurred:");
      String _lineSeparator = System.lineSeparator();
      _builder.append(_lineSeparator);
      _builder.append(interaction);
      throw new AssertionError(_builder);
    }

    public ResultProvider(final TestUserInteraction source) {
      super();
      this.source = source;
    }
  }

  private final List<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>> confirmations = new LinkedList<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>>();

  private final List<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>> notificationReactions = new LinkedList<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>>();

  private final List<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>> textInputs = new LinkedList<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>>();

  private final List<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>> multipleChoices = new LinkedList<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>>();

  private final List<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>> multipleChoiceMultipleSelections = new LinkedList<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>>();

  public void addNextConfirmationInput(final boolean nextConfirmation) {
    this.onNextConfirmation().respondWith(Boolean.valueOf(nextConfirmation));
  }

  public void addNextTextInput(final String nextInput) {
    this.onNextTextInput().respondWith(nextInput);
  }

  public void addNextSingleSelection(final int nextSelection) {
    this.onNextMultipleChoiceSingleSelection().respondWithChoiceAt(nextSelection);
  }

  public void addNextMultiSelection(final int[] nextSelection) {
    this.onNextMultipleChoiceMultiSelection().respondWithChoicesAt(nextSelection);
  }

  /**
   * Configure a response to a confirmation interaction. The next confirmation that matches the
   * provided {@code condition} will be responded to with the result that is configured on the returned builder.
   * Once the result has been provided, it will not be used again. If the {@code condition} matches an interaction
   * that already has a programmed response, the response programmed on the returned builder will take precedence.
   */
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.ConfirmationInteractionDescription, Boolean> onConfirmation(final Function1<? super TestUserInteraction.ConfirmationInteractionDescription, ? extends Boolean> condition) {
    return new TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.ConfirmationInteractionDescription, Boolean>(this, this.confirmations, condition);
  }

  /**
   * Configures the response to the next confirmation interaction.
   */
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.ConfirmationInteractionDescription, Boolean> onNextConfirmation() {
    final Function1<TestUserInteraction.ConfirmationInteractionDescription, Boolean> _function = (TestUserInteraction.ConfirmationInteractionDescription it) -> {
      return Boolean.valueOf(true);
    };
    return this.onConfirmation(_function);
  }

  /**
   * Acknowledges once (i.e. does not raise an error for) a notification passing the provided {@code check}.
   */
  public TestUserInteraction acknowledgeNotification(final Function1<? super TestUserInteraction.NotificationInteractionDescription, ? extends Boolean> check) {
    TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void> _response = new TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>(check, null, false);
    this.notificationReactions.add(_response);
    return this;
  }

  /**
   * Configures a response to a text input interaction. The next text input interaction that matches the
   * provided {@code condition} will be responded to with the result that is configured on the returned builder.
   * Once the result has been provided, it will not be used again. If the {@code condition} matches an interaction
   * that already has a programmed response, the response programmed on the returned builder will take precedence.
   */
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.TextInputInteractionDescription, String> onTextInput(final Function1<? super TestUserInteraction.TextInputInteractionDescription, ? extends Boolean> condition) {
    return new TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.TextInputInteractionDescription, String>(this, this.textInputs, condition);
  }

  /**
   * Configures the response to the next text input interaction.
   */
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.TextInputInteractionDescription, String> onNextTextInput() {
    final Function1<TestUserInteraction.TextInputInteractionDescription, Boolean> _function = (TestUserInteraction.TextInputInteractionDescription it) -> {
      return Boolean.valueOf(true);
    };
    return this.onTextInput(_function);
  }

  /**
   * Configures a response to a multiple choice interaction. The next multiple choice interaction that matches the
   * provided {@code condition} will be responded to with the result that is configured on the returned builder.
   * Once the result has been provided, it will not be used again. If the {@code condition} matches an interaction
   * that already has a programmed response, the response programmed on the returned builder will take precedence.
   */
  public TestUserInteraction.MultipleChoiceResponseBuilder onMultipleChoiceSingleSelection(final Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Boolean> condition) {
    return new TestUserInteraction.MultipleChoiceResponseBuilder(this, condition);
  }

  /**
   * Configures the response to the next multiple choice interaction.
   */
  public TestUserInteraction.MultipleChoiceResponseBuilder onNextMultipleChoiceSingleSelection() {
    final Function1<TestUserInteraction.MultipleChoiceInteractionDescription, Boolean> _function = (TestUserInteraction.MultipleChoiceInteractionDescription it) -> {
      return Boolean.valueOf(true);
    };
    return this.onMultipleChoiceSingleSelection(_function);
  }

  /**
   * Configures a response to a multiple choice interaction with multiple possible selections. The next such
   * interaction that matches the provided {@code condition} will be responded to with the result that is configured
   * on the returned builder.
   * Once the result has been provided, it will not be used again. If the {@code condition} matches an interaction
   * that already has a programmed response, the response programmed on the returned builder will take precedence.
   */
  public TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder onMultipleChoiceMultiSelection(final Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends Boolean> condition) {
    return new TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder(this, condition);
  }

  /**
   * Configures the response to the next multiple choice interaction with multiple possible selections.
   */
  public TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder onNextMultipleChoiceMultiSelection() {
    final Function1<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Boolean> _function = (TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription it) -> {
      return Boolean.valueOf(true);
    };
    return this.onMultipleChoiceMultiSelection(_function);
  }

  /**
   * Validates that all interactions, to which responses have been defined, have occurred. Otherwise,
   * an {@link AssertionError} is thrown. Responses that have been defined for all occurrences of the
   * same interaction by calling an {@code always} method of the builder will not be considered.
   */
  public void assertAllInteractionsOccurred() {
    final Function1<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>, Boolean> _function = (TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean> it) -> {
      return Boolean.valueOf((!it.repeatedly));
    };
    final Iterable<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>> nonRepeatedConfirmations = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>>filter(this.confirmations, _function);
    final Function1<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>, Boolean> _function_1 = (TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void> it) -> {
      return Boolean.valueOf((!it.repeatedly));
    };
    final Iterable<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>> nonRepeatedNotifications = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>>filter(this.notificationReactions, _function_1);
    final Function1<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>, Boolean> _function_2 = (TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String> it) -> {
      return Boolean.valueOf((!it.repeatedly));
    };
    final Iterable<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>> nonRepeatedTextInputs = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>>filter(this.textInputs, _function_2);
    final Function1<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>, Boolean> _function_3 = (TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>> it) -> {
      return Boolean.valueOf((!it.repeatedly));
    };
    final Iterable<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>> nonRepeatedMultipleChoices = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>>filter(this.multipleChoices, _function_3);
    final Function1<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>, Boolean> _function_4 = (TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>> it) -> {
      return Boolean.valueOf((!it.repeatedly));
    };
    final Iterable<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>> nonRepeatedMultipleChoiceMultipleSelections = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>>filter(this.multipleChoiceMultipleSelections, _function_4);
    int _size = IterableExtensions.size(nonRepeatedConfirmations);
    int _size_1 = IterableExtensions.size(nonRepeatedNotifications);
    int _plus = (_size + _size_1);
    int _size_2 = IterableExtensions.size(nonRepeatedTextInputs);
    int _plus_1 = (_plus + _size_2);
    int _size_3 = IterableExtensions.size(nonRepeatedMultipleChoices);
    int _plus_2 = (_plus_1 + _size_3);
    int _size_4 = IterableExtensions.size(nonRepeatedMultipleChoiceMultipleSelections);
    final int interactionsLeft = (_plus_2 + _size_4);
    if ((interactionsLeft > 0)) {
      final StringBuilder resultMessage = new StringBuilder();
      List<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>> _list = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>>toList(nonRepeatedConfirmations);
      Pair<String, Collection<?>> _mappedTo = Pair.<String, Collection<?>>of("confirmation", _list);
      List<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>> _list_1 = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>>toList(nonRepeatedNotifications);
      Pair<String, Collection<?>> _mappedTo_1 = Pair.<String, Collection<?>>of("notification", _list_1);
      List<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>> _list_2 = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>>toList(nonRepeatedTextInputs);
      Pair<String, Collection<?>> _mappedTo_2 = Pair.<String, Collection<?>>of("text input", _list_2);
      List<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>> _list_3 = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceInteractionDescription, ? extends Integer>>>toList(nonRepeatedMultipleChoices);
      Pair<String, Collection<?>> _mappedTo_3 = Pair.<String, Collection<?>>of("single selection", _list_3);
      List<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>> _list_4 = IterableExtensions.<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function1<? super TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, ? extends int[]>>>toList(nonRepeatedMultipleChoiceMultipleSelections);
      Pair<String, Collection<?>> _mappedTo_4 = Pair.<String, Collection<?>>of("multi selection", _list_4);
      final Function1<Pair<String, Collection<?>>, Boolean> _function_5 = (Pair<String, Collection<?>> it) -> {
        boolean _isEmpty = it.getValue().isEmpty();
        return Boolean.valueOf((!_isEmpty));
      };
      final Procedure1<Pair<String, Collection<?>>> _function_6 = (Pair<String, Collection<?>> it) -> {
        resultMessage.append(it.getValue().size()).append(" ").append(TestMessages.plural(it.getValue(), it.getKey()));
      };
      StringBuilder _append = TestMessages.<Pair<String, Collection<?>>>joinSemantic(resultMessage, IterableExtensions.<Pair<String, Collection<?>>>toList(IterableExtensions.<Pair<String, Collection<?>>>filter(List.<Pair<String, Collection<?>>>of(_mappedTo, _mappedTo_1, _mappedTo_2, _mappedTo_3, _mappedTo_4), _function_5)), "and", _function_6).append(" ");
      String _xifexpression = null;
      if ((interactionsLeft == 1)) {
        _xifexpression = "was";
      } else {
        _xifexpression = "were";
      }
      _append.append(_xifexpression).append(" expected but did not occur!");
      String _string = resultMessage.toString();
      throw new AssertionError(_string);
    }
  }

  /**
   * Removes all responses that have been programmed.
   */
  public void clearResponses() {
    this.confirmations.clear();
    this.notificationReactions.clear();
    this.textInputs.clear();
    this.multipleChoices.clear();
    this.multipleChoiceMultipleSelections.clear();
  }

  public StringBuilder listSize(final StringBuilder result, final Collection<?> list, final String type) {
    StringBuilder _xifexpression = null;
    boolean _isEmpty = list.isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      _xifexpression = result.append(list.size()).append(" ").append(TestMessages.plural(list, type));
    } else {
      _xifexpression = null;
    }
    return _xifexpression;
  }
}
