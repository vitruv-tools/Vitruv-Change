package tools.vitruv.change.interaction;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A result provider based on CLI in- and output to make the requested input.
 * Add this ResultProvider during VSUM-instanciation to enable resolution via CLI input
 *
 * @author Miriam Boss
 */
public class CliInteractionResultProviderImpl implements InteractionResultProvider {
  private final Console console;
  private static final Logger log = LogManager.getLogger(CliInteractionResultProviderImpl.class);
  private static final int LOWER_BOUND = 0;
  private static final int ERROR_CODE = Integer.MIN_VALUE;

  /**
   * Generates a new Result provider that operates on the CLI.
   */
  public CliInteractionResultProviderImpl() {
    this.console = System.console();
    if ((this.console == null)) {
      log.error("Console not available");
    }
  }

  @Override
  public boolean getConfirmationInteractionResult(
      final UserInteractionOptions.WindowModality windowModality,
      final String title,
      final String message,
      final String positiveDecisionText,
      final String negativeDecisionText,
      final String cancelDecisionText) {
    String s = this.console.readLine(title + ": " + message + " [y/n]?");
    s = s.toLowerCase().strip();
    return (s.equals("yes") || s.equals("y"));
  }

  @Override
  public void getNotificationInteractionResult(
      final UserInteractionOptions.WindowModality windowModality,
      final String title,
      final String message,
      final String positiveDecisionText,
      final UserInteractionOptions.NotificationType notificationType) {
    this.console.printf(notificationType + " " + title + ": " + message);
  }

  @Override
  public String getTextInputInteractionResult(
      final UserInteractionOptions.WindowModality windowModality,
      final String title,
      final String message,
      final String positiveDecisionText,
      final String cancelDecisionText,
      final UserInteractionOptions.InputValidator inputValidator) {
    String s = this.console.readLine(title + ": " + message);
    while ((!inputValidator.isInputValid(s)) && (!s.strip().equals(cancelDecisionText))) {
      s = this.console.readLine("\""
          + inputValidator.getInvalidInputMessage(s)
          + ", please try again or type \"" + cancelDecisionText
          + "\" to abort the input");
    }
    return s;
  }

  @Override
  public int getMultipleChoiceSingleSelectionInteractionResult(
      final UserInteractionOptions.WindowModality windowModality,
      final String title,
      final String message,
      final String positiveDecisionText,
      final String cancelDecisionText,
      final Iterable<String> choices) {
    StringBuilder sb = new StringBuilder(title + ": " + message + "\nChoices:\n");
    int choiceUpperBound = LOWER_BOUND;
    for (String choice : choices) {
      sb.append("(").append(choiceUpperBound).append(") ").append(choice).append("\n");
      choices.iterator().next();
      choiceUpperBound++;
    }
    sb.append("Please select one option based on the index displayed or type \"")
        .append(cancelDecisionText)
        .append("\" to abort the operation");
    String s = this.console.readLine(sb.toString());
    while (true) {
      if (s.strip().equals(cancelDecisionText)) { // Abort
        return 0;
      }
      int response = parseIntOrReturnErrorCode(s);

      if (response != ERROR_CODE && isInValidRange(response, choiceUpperBound)) {
        return response;
      } else {
        s = printErrorAndWaitForInputMC(s, response);
      }
    }
  }

  @Override
  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
      final UserInteractionOptions.WindowModality windowModality,
      final String title,
      final String message,
      final String positiveDecisionText,
      final String cancelDecisionText,
      final Iterable<String> choices) {

    // Print choices
    StringBuilder sb = new StringBuilder(title + ": " + message + "\nChoices:\n");
    int numberOfChoices = LOWER_BOUND;
    while (choices.iterator().hasNext()) {
      String choice = choices.toString();
      sb.append("(").append(numberOfChoices).append(") ").append(choice).append("\n");
      choices.iterator().next();
      numberOfChoices++;
    }
    sb.append("Please select options based on the index displayed (comma seperated) or type \"")
        .append(cancelDecisionText)
        .append("\" to abort the operation");


    String s = this.console.readLine(sb.toString()).strip();
    while (true) { // Read Input until valid or abort
      if (s.strip().equals(cancelDecisionText)) { // Abort
        return Collections.singleton(0);
      }

      // if no abort, start parsing fragments
      String[] fragments = s.strip().split(",");
      ArrayList<Integer> result = new ArrayList<>();
      int correctlyParsedFragments = 0;
      for (String fragment : fragments) {
        int fragmentInt = parseIntOrReturnErrorCode(fragment);
        if (fragmentInt == ERROR_CODE || !isInValidRange(fragmentInt, numberOfChoices)) {
          // Error state
          s = printErrorAndWaitForInputMC(fragment, fragmentInt);
          break;
        }
        correctlyParsedFragments++;
        if (!result.contains(fragmentInt)) {
          result.add(fragmentInt);
        }
      }
      if (fragments.length == correctlyParsedFragments) {
        return result; // only return iff all fragments are valid inputs
      }
    }
  }

  private int parseIntOrReturnErrorCode(String numberString) {
    try {
      return Integer.parseInt(numberString);
    } catch (NumberFormatException e) {
      return ERROR_CODE;
    }
  }

  private boolean isInValidRange(int input, int upperRangeLimit) {
    return (input >= LOWER_BOUND && input < upperRangeLimit);
  }

  private String printErrorAndWaitForInputMC(String originalInput, int parseOutput) {
    String errorMessage = "\"" + originalInput;
    if (parseOutput == ERROR_CODE) {
      errorMessage += "\" is not a valid number, try again";
    } else { // we assume that if the parse was not the problem, the range was
      errorMessage += "\" is not in the valid range, try again";
    }
    return this.console.readLine(errorMessage);
  }

}
