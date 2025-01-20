package tools.vitruv.change.interaction;

/**
 * Enumerations and interfaces for options that can be made during user interaction.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public final class UserInteractionOptions {
  /**
   * Represents the different ways a window can behave (whether it requires interaction blocking other windows behind it
   * or it can be minimized).
   * 
   * @author Dominik Klooz
   */
  public enum WindowModality {
    MODAL,

    MODELESS;
  }

  /**
   * Enum for the levels of severity for notification messages (specifies the icon used in {@link NotificationInteraction}s).
   * @see NotificationInteraction
   * 
   * @uthor Dominik Klooz
   */
  public enum NotificationType {
    INFORMATION,

    WARNING,

    ERROR;
  }

  /**
   * Enum to specify whether input lines provide single or multi line input.
   * 
   * @author Dominik Klooz
   */
  public enum InputFieldType {
    SINGLE_LINE,

    MULTI_LINE;
  }

  /**
   * Interface for input validators used in {@link TextInputDialog}s. The {@link #isInputValid(String) isInputValid}
   * method is used to accept or deny input changes, the message constructed in
   * {@link #getInvalidInputMessage(String) getInvalidInputMessage} is displayed whenever the user tries to enter
   * illegal characters as determined by {@link #isInputValid(String) isInputValid}.
   */
  public interface InputValidator {
    /**
     * Get a warning message to be displayed when the user tries to add illegal characters (as determined by
     * {@link isInputValid(String)}).
     */
    String getInvalidInputMessage(final String input);

    /**
     * Determines whether or not the current input is to be considered valid. If not, the illegal characters will
     * not be added to the input.
     */
    boolean isInputValid(final String input);
  }

  private UserInteractionOptions() {
  }
}
