package tools.vitruv.change.interaction;

import java.util.List;

/**
 * A user interaction that a change propagation needs but has no answer for yet.
 *
 * <p>Reactions identify interactions by their message (see {@link
 * PredefinedInteractionResultProvider}), so a client answers this interaction by registering a
 * {@link tools.vitruv.change.interaction.UserInteractionBase} with the same message.
 *
 * @param kind what kind of answer is expected
 * @param title the dialog title, may be {@code null}
 * @param message the dialog message, which identifies the interaction
 * @param choices the options to choose from for selection interactions, empty otherwise
 */
public record PendingUserInteraction(
    Kind kind, String title, String message, List<String> choices) {

  /** The kind of answer an interaction expects. */
  public enum Kind {
    CONFIRMATION,
    TEXT_INPUT,
    SINGLE_SELECTION,
    MULTIPLE_SELECTION
  }

  /** Creates the interaction; {@code choices} is copied. */
  public PendingUserInteraction {
    choices = choices == null ? List.of() : List.copyOf(choices);
  }
}
