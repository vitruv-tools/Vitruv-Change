package tools.vitruv.change.testutils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.testutils.printing.TestMessages;

public class TestUserInteraction {
  public static class Response<Description extends Object, Result extends Object> {
    private final Function<Description, Boolean> matcher;

    private final Result response;

    private final boolean repeatedly;

    public Response(final Function<Description, Boolean> matcher, final Result response, final boolean repeatedly) {
      super();
      this.matcher = matcher;
      this.response = response;
      this.repeatedly = repeatedly;
    }
  }

  public static class SimpleResponseBuilder<Description extends Object, Result extends Object> {
    private final TestUserInteraction owner;

    private final List<TestUserInteraction.Response<Description, Result>> targetQueue;

    private final Function<Description, Boolean> condition;

    private boolean repeatedly = false;

    public TestUserInteraction.SimpleResponseBuilder<Description, Result> always() {
      this.repeatedly = true;
      return this;
    }

    /**
     * Responds the interaction with the provided {@code result} if the condition this builder was created for holds.
     */
    public TestUserInteraction respondWith(final Result result) {
      this.targetQueue.add(new TestUserInteraction.Response<>(this.condition, result, this.repeatedly));
      return this.owner;
    }

    public SimpleResponseBuilder(final TestUserInteraction owner, final List<TestUserInteraction.Response<Description, Result>> targetQueue, final Function<Description, Boolean> condition) {
      super();
      this.owner = owner;
      this.targetQueue = targetQueue;
      this.condition = condition;
    }
  }

  public static class MultipleChoiceResponseBuilder {
    private final TestUserInteraction owner;

    private final Function<TestUserInteraction.MultipleChoiceInteractionDescription, Boolean> condition;

    private boolean repeatedly = false;

    public TestUserInteraction.MultipleChoiceResponseBuilder always() {
      this.repeatedly = true;
      return this;
    }

    public TestUserInteraction respondWith(final String result) {
      return this.respondWithChoiceMatching(it -> Objects.equals(it, result));
    }

    public TestUserInteraction respondWithChoiceAt(final int resultIndex) {
      Function<TestUserInteraction.MultipleChoiceInteractionDescription, Integer> fn = it -> resultIndex;
      this.owner.multipleChoices.add(new TestUserInteraction.Response<>(this.condition, fn, this.repeatedly));
      return this.owner;
    }

    public TestUserInteraction respondWithChoiceMatching(final Function<String, Boolean> selector) {
      Function<TestUserInteraction.MultipleChoiceInteractionDescription, Integer> fn = it ->
          TestUserInteraction.MultipleChoiceResponseBuilder.assertedIndexBy(it, selector);
      this.owner.multipleChoices.add(new TestUserInteraction.Response<>(this.condition, fn, this.repeatedly));
      return this.owner;
    }

    private static int assertedIndexBy(final TestUserInteraction.MultipleChoiceInteractionDescription interaction, final Function<String, Boolean> selector) {
      int i = 0;
      for (String choice : interaction.choices) {
        if (selector.apply(choice)) {
          return i;
        }
        i++;
      }
      throw new AssertionError(interaction.getType() + " without an acceptable choice:" + System.lineSeparator() + interaction);
    }

    public MultipleChoiceResponseBuilder(final TestUserInteraction owner, final Function<TestUserInteraction.MultipleChoiceInteractionDescription, Boolean> condition) {
      super();
      this.owner = owner;
      this.condition = condition;
    }
  }

  public static class MultipleChoiceMultipleSelectionResponseBuilder {
    private final TestUserInteraction owner;

    private final Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Boolean> condition;

    private boolean repeatedly = false;

    public TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder always() {
      this.repeatedly = true;
      return this;
    }

    public TestUserInteraction respondWith(final String... results) {
      return this.respondWith(Set.<String>of(results));
    }

    public TestUserInteraction respondWith(final Set<String> results) {
      return this.respondWithChoicesMatching(results::contains);
    }

    public TestUserInteraction respondWithChoicesAt(final int... resultIndeces) {
      Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, int[]> fn = it -> resultIndeces;
      this.owner.multipleChoiceMultipleSelections.add(new TestUserInteraction.Response<>(this.condition, fn, this.repeatedly));
      return this.owner;
    }

    public TestUserInteraction respondWithChoicesMatching(final Function<String, Boolean> selector) {
      Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, int[]> fn = it ->
          TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder.assertedIndecesBy(it, selector);
      this.owner.multipleChoiceMultipleSelections.add(new TestUserInteraction.Response<>(this.condition, fn, this.repeatedly));
      return this.owner;
    }

    private static int[] assertedIndecesBy(final TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription interaction, final Function<String, Boolean> selector) {
      final ArrayList<Integer> result = new ArrayList<>();
      int i = 0;
      for (String choice : interaction.getChoices()) {
        if (selector.apply(choice)) {
          result.add(i);
        }
        i++;
      }
      return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public MultipleChoiceMultipleSelectionResponseBuilder(final TestUserInteraction owner, final Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Boolean> condition) {
      super();
      this.owner = owner;
      this.condition = condition;
    }
  }

  public static abstract class InteractionDescription {
    private final UserInteractionOptions.WindowModality windowModality;

    private final String title;

    private final String message;

    private final String positiveDecisionText;

    public abstract String getType();

    @Override
    public String toString() {
      return this.title + ": " + this.message;
    }

    public InteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText) {
      super();
      this.windowModality = windowModality;
      this.title = title;
      this.message = message;
      this.positiveDecisionText = positiveDecisionText;
    }

    public UserInteractionOptions.WindowModality getWindowModality() {
      return this.windowModality;
    }

    public String getTitle() {
      return this.title;
    }

    public String getMessage() {
      return this.message;
    }

    public String getPositiveDecisionText() {
      return this.positiveDecisionText;
    }
  }

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

    public String getNegativeDecisionText() {
      return this.negativeDecisionText;
    }

    public String getCancelDecisionText() {
      return this.cancelDecisionText;
    }
  }

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

    public String getCancelDecisionText() {
      return this.cancelDecisionText;
    }

    public UserInteractionOptions.InputValidator getInputValidator() {
      return this.inputValidator;
    }
  }

  public static class NotificationInteractionDescription extends TestUserInteraction.InteractionDescription {
    private final UserInteractionOptions.NotificationType notificationType;

    @Override
    public String toString() {
      return this.notificationType + " " + super.toString();
    }

    @Override
    public String getType() {
      return "notification interaction";
    }

    public NotificationInteractionDescription(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType) {
      super(windowModality, title, message, positiveDecisionText);
      this.notificationType = notificationType;
    }

    public UserInteractionOptions.NotificationType getNotificationType() {
      return this.notificationType;
    }
  }

  public static class MultipleChoiceInteractionDescription extends TestUserInteraction.InteractionDescription {
    private final String cancelDecisionText;

    private final Iterable<String> choices;

    @Override
    public String toString() {
      return super.toString() + StreamSupport.stream(this.choices.spliterator(), false)
          .map(it -> System.lineSeparator() + "  • " + it)
          .collect(Collectors.joining());
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

    public String getCancelDecisionText() {
      return this.cancelDecisionText;
    }

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

  public static class ResultProvider implements InteractionResultProvider {
    private final TestUserInteraction source;

    @Override
    public boolean getConfirmationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String negativeDecisionText, final String cancelDecisionText) {
      return (boolean) TestUserInteraction.ResultProvider.<TestUserInteraction.ConfirmationInteractionDescription, Boolean>requireMatchingInteraction(
          this.source.confirmations,
          new TestUserInteraction.ConfirmationInteractionDescription(windowModality, title, message, positiveDecisionText, negativeDecisionText, cancelDecisionText));
    }

    @Override
    public void getNotificationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType) {
      TestUserInteraction.ResultProvider.<TestUserInteraction.NotificationInteractionDescription, Void>requireMatchingInteraction(
          this.source.notificationReactions,
          new TestUserInteraction.NotificationInteractionDescription(windowModality, title, message, positiveDecisionText, notificationType));
    }

    @Override
    public String getTextInputInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final UserInteractionOptions.InputValidator inputValidator) {
      return TestUserInteraction.ResultProvider.<TestUserInteraction.TextInputInteractionDescription, String>requireMatchingInteraction(
          this.source.textInputs,
          new TestUserInteraction.TextInputInteractionDescription(windowModality, title, message, positiveDecisionText, cancelDecisionText, inputValidator));
    }

    @Override
    public int getMultipleChoiceSingleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
      final TestUserInteraction.MultipleChoiceInteractionDescription interaction = new TestUserInteraction.MultipleChoiceInteractionDescription(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
      final Function<TestUserInteraction.MultipleChoiceInteractionDescription, Integer> resultProvider = TestUserInteraction.ResultProvider.<TestUserInteraction.MultipleChoiceInteractionDescription, Function<TestUserInteraction.MultipleChoiceInteractionDescription, Integer>>requireMatchingInteraction(this.source.multipleChoices, interaction);
      return resultProvider.apply(interaction);
    }

    @Override
    public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
      final TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription interaction = new TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
      final Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, int[]> resultProvider = TestUserInteraction.ResultProvider.<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, int[]>>requireMatchingInteraction(this.source.multipleChoiceMultipleSelections, interaction);
      return Arrays.stream(resultProvider.apply(interaction)).boxed().collect(Collectors.toList());
    }

    private static <Description extends TestUserInteraction.InteractionDescription, Result extends Object> Result requireMatchingInteraction(final Iterable<TestUserInteraction.Response<Description, Result>> options, final Description interaction) {
      for (final Iterator<TestUserInteraction.Response<Description, Result>> optionsIt = options.iterator(); optionsIt.hasNext();) {
        final TestUserInteraction.Response<Description, Result> option = optionsIt.next();
        if (option.matcher.apply(interaction)) {
          if (!option.repeatedly) {
            optionsIt.remove();
          }
          return option.response;
        }
      }
      throw new AssertionError("An unexpected " + interaction.getType() + " occurred:" + System.lineSeparator() + interaction);
    }

    public ResultProvider(final TestUserInteraction source) {
      super();
      this.source = source;
    }
  }

  private final List<TestUserInteraction.Response<TestUserInteraction.ConfirmationInteractionDescription, Boolean>> confirmations = new LinkedList<>();

  private final List<TestUserInteraction.Response<TestUserInteraction.NotificationInteractionDescription, Void>> notificationReactions = new LinkedList<>();

  private final List<TestUserInteraction.Response<TestUserInteraction.TextInputInteractionDescription, String>> textInputs = new LinkedList<>();

  private final List<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceInteractionDescription, Function<TestUserInteraction.MultipleChoiceInteractionDescription, Integer>>> multipleChoices = new LinkedList<>();

  private final List<TestUserInteraction.Response<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, int[]>>> multipleChoiceMultipleSelections = new LinkedList<>();

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
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.ConfirmationInteractionDescription, Boolean> onConfirmation(final Function<TestUserInteraction.ConfirmationInteractionDescription, Boolean> condition) {
    return new TestUserInteraction.SimpleResponseBuilder<>(this, this.confirmations, condition);
  }

  /**
   * Configures the response to the next confirmation interaction.
   */
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.ConfirmationInteractionDescription, Boolean> onNextConfirmation() {
    return this.onConfirmation(it -> true);
  }

  /**
   * Acknowledges once (i.e. does not raise an error for) a notification passing the provided {@code check}.
   */
  public TestUserInteraction acknowledgeNotification(final Function<TestUserInteraction.NotificationInteractionDescription, Boolean> check) {
    this.notificationReactions.add(new TestUserInteraction.Response<>(check, null, false));
    return this;
  }

  /**
   * Configures a response to a text input interaction. The next text input interaction that matches the
   * provided {@code condition} will be responded to with the result that is configured on the returned builder.
   * Once the result has been provided, it will not be used again. If the {@code condition} matches an interaction
   * that already has a programmed response, the response programmed on the returned builder will take precedence.
   */
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.TextInputInteractionDescription, String> onTextInput(final Function<TestUserInteraction.TextInputInteractionDescription, Boolean> condition) {
    return new TestUserInteraction.SimpleResponseBuilder<>(this, this.textInputs, condition);
  }

  /**
   * Configures the response to the next text input interaction.
   */
  public TestUserInteraction.SimpleResponseBuilder<TestUserInteraction.TextInputInteractionDescription, String> onNextTextInput() {
    return this.onTextInput(it -> true);
  }

  /**
   * Configures a response to a multiple choice interaction. The next multiple choice interaction that matches the
   * provided {@code condition} will be responded to with the result that is configured on the returned builder.
   * Once the result has been provided, it will not be used again. If the {@code condition} matches an interaction
   * that already has a programmed response, the response programmed on the returned builder will take precedence.
   */
  public TestUserInteraction.MultipleChoiceResponseBuilder onMultipleChoiceSingleSelection(final Function<TestUserInteraction.MultipleChoiceInteractionDescription, Boolean> condition) {
    return new TestUserInteraction.MultipleChoiceResponseBuilder(this, condition);
  }

  /**
   * Configures the response to the next multiple choice interaction.
   */
  public TestUserInteraction.MultipleChoiceResponseBuilder onNextMultipleChoiceSingleSelection() {
    return this.onMultipleChoiceSingleSelection(it -> true);
  }

  /**
   * Configures a response to a multiple choice interaction with multiple possible selections. The next such
   * interaction that matches the provided {@code condition} will be responded to with the result that is configured
   * on the returned builder.
   * Once the result has been provided, it will not be used again. If the {@code condition} matches an interaction
   * that already has a programmed response, the response programmed on the returned builder will take precedence.
   */
  public TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder onMultipleChoiceMultiSelection(final Function<TestUserInteraction.MultipleChoiceMultipleSelectionInteractionDescription, Boolean> condition) {
    return new TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder(this, condition);
  }

  /**
   * Configures the response to the next multiple choice interaction with multiple possible selections.
   */
  public TestUserInteraction.MultipleChoiceMultipleSelectionResponseBuilder onNextMultipleChoiceMultiSelection() {
    return this.onMultipleChoiceMultiSelection(it -> true);
  }

  /**
   * Validates that all interactions, to which responses have been defined, have occurred. Otherwise,
   * an {@link AssertionError} is thrown. Responses that have been defined for all occurrences of the
   * same interaction by calling an {@code always} method of the builder will not be considered.
   */
  public void assertAllInteractionsOccurred() {
    List<?> nonRepeatedConfirmations = this.confirmations.stream().filter(it -> !it.repeatedly).collect(Collectors.toList());
    List<?> nonRepeatedNotifications = this.notificationReactions.stream().filter(it -> !it.repeatedly).collect(Collectors.toList());
    List<?> nonRepeatedTextInputs = this.textInputs.stream().filter(it -> !it.repeatedly).collect(Collectors.toList());
    List<?> nonRepeatedMultipleChoices = this.multipleChoices.stream().filter(it -> !it.repeatedly).collect(Collectors.toList());
    List<?> nonRepeatedMultipleChoiceMultipleSelections = this.multipleChoiceMultipleSelections.stream().filter(it -> !it.repeatedly).collect(Collectors.toList());
    final int interactionsLeft = nonRepeatedConfirmations.size() + nonRepeatedNotifications.size()
        + nonRepeatedTextInputs.size() + nonRepeatedMultipleChoices.size()
        + nonRepeatedMultipleChoiceMultipleSelections.size();
    if (interactionsLeft > 0) {
      final StringBuilder resultMessage = new StringBuilder();
      List<Map.Entry<String, Collection<?>>> entries = List.of(
          new AbstractMap.SimpleEntry<>("confirmation", (Collection<?>) nonRepeatedConfirmations),
          new AbstractMap.SimpleEntry<>("notification", (Collection<?>) nonRepeatedNotifications),
          new AbstractMap.SimpleEntry<>("text input", (Collection<?>) nonRepeatedTextInputs),
          new AbstractMap.SimpleEntry<>("single selection", (Collection<?>) nonRepeatedMultipleChoices),
          new AbstractMap.SimpleEntry<>("multi selection", (Collection<?>) nonRepeatedMultipleChoiceMultipleSelections)
      );
      List<Map.Entry<String, Collection<?>>> nonEmpty = entries.stream()
          .filter(it -> !it.getValue().isEmpty())
          .collect(Collectors.toList());
      Consumer<Map.Entry<String, Collection<?>>> appender = it ->
          resultMessage.append(it.getValue().size()).append(" ").append(TestMessages.plural(it.getValue(), it.getKey()));
      TestMessages.<Map.Entry<String, Collection<?>>>joinSemantic(resultMessage, nonEmpty, "and", appender)
          .append(" ")
          .append(interactionsLeft == 1 ? "was" : "were")
          .append(" expected but did not occur!");
      throw new AssertionError(resultMessage.toString());
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
    if (!list.isEmpty()) {
      return result.append(list.size()).append(" ").append(TestMessages.plural(list, type));
    }
    return null;
  }
}
