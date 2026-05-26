package tools.vitruv.change.interaction;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A result provider based on CLI in- and output to make the requested input.
 * Add this ResultProvider during VSUM-instanciation to enable resolution via CLI input
 *
 * @author Miriam Boss
 */
public class CliInteractionResultProviderImpl implements InteractionResultProvider {
  private final Console console;

  /**
   * Generates a new Result provider that operates on the CLI.
   */
  public CliInteractionResultProviderImpl() {
    this.console = System.console();
    if ((this.console == null)) {
      System.out.print("Console not available");
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
    System.out.println(notificationType + " " + title + ": " + message);
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
    int i = 0;
    for (String choice : choices) {
      sb.append("(").append(i).append(") ").append(choice).append("\n");
      choices.iterator().next();
      i++;
    }
    sb.append("Please select one option based on the index displayed or type \"")
        .append(cancelDecisionText)
        .append("\" to abort the operation");
    String s = this.console.readLine(sb.toString());
    while (true) {
      if (s.strip().equals(cancelDecisionText)) { // Abort
        return 0;
      }
      try {
        int response = Integer.parseInt(s); // Input given
        if (response >= 0 && response < i) { // Input in the valid range
          return response;
        } else {
          s = this.console.readLine("\"" + response + "\" is not in the valid range, try again");
        }
      } catch (NumberFormatException e) { // Not a number as input
        s = this.console.readLine("\"" + s + "\" is not a valid number, try again");
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
    StringBuilder sb = new StringBuilder(title + ": " + message + "\nChoices:\n");
    int i = 0;
    while (choices.iterator().hasNext()) {
      String choice = choices.toString();
      sb.append("(").append(i).append(") ").append(choice).append("\n");
      choices.iterator().next();
      i++;
    }
    sb.append("Please select options based on the index displayed (comma seperated) or type \"")
        .append(cancelDecisionText)
        .append("\" to abort the operation");
    String s = this.console.readLine(sb.toString()).strip();
    while (true) {
      if (s.strip().equals(cancelDecisionText)) { // Abort
        return Collections.singleton(0);
      }
      String[] fragments = s.strip().split(",");
      ArrayList<Integer> result = new ArrayList<>();
      int correctlyParsedFragments = 0;
      for (String fragment : fragments) {
        try {
          int fragmentInt = Integer.parseInt(fragment);
          if (!(fragmentInt >= 0 && fragmentInt < i)) {
            if (!result.contains(fragmentInt)) {
              result.add(fragmentInt);
            }
            correctlyParsedFragments++;
          } else {
            String rangeError = "\" is not in the valid range, try again\"";
            s = this.console.readLine("\"" + fragmentInt + rangeError);
            break;
          }
        } catch (NumberFormatException e) {
          s = this.console.readLine("\"" + fragment + "\" is not a valid number, try again");
          break;
        }
      }
      if (fragments.length == correctlyParsedFragments) {
        return result;
      }
    }
  }
}
