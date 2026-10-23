package tools.vitruv.change.interaction;

import java.util.List;

/**
 * A user interaction that a change propagation needs but has no answer for yet.
 *
 * <p>A client answers it by registering a {@link UserInteractionBase} that {@link
 * PredefinedInteractionResultProvider} matches to it: an instance of the type named at its {@link
 * Kind} with the same message, whose choices contain all of {@link #choices()} for selections. An
 * answer without a message matches every interaction of its type. Within one propagation, each
 * answer is used for one interaction only.
 *
 * @param kind what kind of answer is expected
 * @param title the dialog title, may be {@code null}
 * @param message the dialog message, which identifies the interaction
 * @param positiveDecisionText the label of the button that accepts, may be {@code null}
 * @param negativeDecisionText the label of the button that declines a confirmation, {@code null}
 *     for other kinds
 * @param cancelDecisionText the label of the button that cancels, may be {@code null}
 * @param choices the options to choose from for selection interactions, empty otherwise
 */
public record PendingUserInteraction(
    Kind kind,
    String title,
    String message,
    String positiveDecisionText,
    String negativeDecisionText,
    String cancelDecisionText,
    List<String> choices) {

  /** The kind of answer an interaction expects. */
  public enum Kind {
    /** Answered by a {@link ConfirmationUserInteraction}. */
    CONFIRMATION,
    /** Answered by a {@link FreeTextUserInteraction}. */
    TEXT_INPUT,
    /** Answered by a {@link MultipleChoiceSingleSelectionUserInteraction}. */
    SINGLE_SELECTION,
    /** Answered by a {@link MultipleChoiceMultiSelectionUserInteraction}. */
    MULTIPLE_SELECTION
  }

  /** Creates the interaction; {@code choices} is copied. */
  public PendingUserInteraction {
    choices = choices == null ? List.of() : List.copyOf(choices);
  }
}
