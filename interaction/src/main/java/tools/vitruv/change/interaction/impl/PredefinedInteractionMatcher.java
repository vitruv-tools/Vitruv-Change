package tools.vitruv.change.interaction.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import tools.vitruv.change.interaction.ConfirmationUserInteraction;
import tools.vitruv.change.interaction.FreeTextUserInteraction;
import tools.vitruv.change.interaction.MultipleChoiceMultiSelectionUserInteraction;
import tools.vitruv.change.interaction.MultipleChoiceSelectionInteractionBase;
import tools.vitruv.change.interaction.MultipleChoiceSingleSelectionUserInteraction;
import tools.vitruv.change.interaction.UserInteractionBase;

/**
 * A matcher class that allows to get an appropriate {@link UserInteractionBase}
 * of those predefined using
 * {@link PredefinedInteractionMatcher#addInteraction(UserInteractionBase)
 * addInteraction(UserInteractionBase)} for each kind of request.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
public class PredefinedInteractionMatcher {
  private List<UserInteractionBase> userInteractions = new ArrayList<>();

  public PredefinedInteractionMatcher() {
    ArrayList<UserInteractionBase> _arrayList = new ArrayList<UserInteractionBase>();
    this.userInteractions = _arrayList;
  }

  public void addInteraction(final UserInteractionBase interaction) {
    this.userInteractions.add(interaction);
  }

  protected <T extends UserInteractionBase> Iterable<T> getMatchingInput(final String message, final Class<T> type) {
    if ((this.userInteractions == null)) {
      return Collections.<T>emptyList();
    }
    final Function<T, Boolean> _function = (T input) -> {
      return Boolean.valueOf(((!input.isSetMessage()) || input.getMessage().equals(message)));
    };
    final Iterable<T> inputToReuse = Iterables.filter(Iterables.<T>filter(this.userInteractions, type),
        input -> _function.apply(input));
    return inputToReuse;
  }

  protected <T extends MultipleChoiceSelectionInteractionBase> Iterable<T> getMatchingMutltipleChoiceInput(
      final String message, final Iterable<String> choices, final Class<T> type) {
    if ((this.userInteractions == null)) {
      return Collections.<T>emptyList();
    }
    final List<String> choiceList = StreamSupport.stream(choices.spliterator(), false).collect(Collectors.toList());
    final Function<T, Boolean> _function = (T input) -> {
      return Boolean.valueOf(((!input.isSetMessage()) || (input.getMessage().equals(message)
          && input.getChoices().containsAll(choiceList))));
    };
    final Iterable<T> inputToReuse = Iterables.filter(Iterables.<T>filter(this.userInteractions, type),
        input -> _function.apply(input));
    return inputToReuse;
  }

  /**
   * Returns the predefined notification result for the given message, if one is available.
   *
   * @param message the message to find a matching predefined notification for
   * @return present if a predefined interaction matched, empty otherwise
   */
  public Optional<Boolean> getNotificationResult(final String message) {
    final Iterable<ConfirmationUserInteraction> inputToReuseCandidates = this
        .<ConfirmationUserInteraction>getMatchingInput(message, ConfirmationUserInteraction.class);
    boolean _isEmpty = Iterables.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return Optional.empty();
    }
    final ConfirmationUserInteraction inputToReuse = inputToReuseCandidates.iterator().next();
    this.userInteractions.remove(inputToReuse);
    return Optional.of(Boolean.TRUE);
  }

  /**
   * Returns the predefined confirmation result for the given message, if one is available.
   *
   * @param message the message to find a matching predefined confirmation for
   * @return the confirmation result, or empty if no input was predefined
   */
  public Optional<Boolean> getConfirmationResult(final String message) {
    final Iterable<ConfirmationUserInteraction> inputToReuseCandidates = this
        .<ConfirmationUserInteraction>getMatchingInput(message, ConfirmationUserInteraction.class);
    boolean _isEmpty = Iterables.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return Optional.empty();
    }
    final ConfirmationUserInteraction inputToReuse = inputToReuseCandidates.iterator().next();
    this.userInteractions.remove(inputToReuse);
    return Optional.of(Boolean.valueOf(inputToReuse.isConfirmed()));
  }

  public String getTextInputResult(final String message) {
    final Iterable<FreeTextUserInteraction> inputToReuseCandidates = this
        .<FreeTextUserInteraction>getMatchingInput(message, FreeTextUserInteraction.class);
    boolean _isEmpty = Iterables.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final FreeTextUserInteraction inputToReuse = inputToReuseCandidates.iterator().next();
    this.userInteractions.remove(inputToReuse);
    return inputToReuse.getText();
  }

  public Integer getSingleSelectionResult(final String message, final Iterable<String> choices) {
    final Iterable<MultipleChoiceSingleSelectionUserInteraction> inputToReuseCandidates = this
        .<MultipleChoiceSingleSelectionUserInteraction>getMatchingMutltipleChoiceInput(message, choices,
            MultipleChoiceSingleSelectionUserInteraction.class);
    boolean _isEmpty = Iterables.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final MultipleChoiceSingleSelectionUserInteraction inputToReuse = inputToReuseCandidates.iterator().next();
    this.userInteractions.remove(inputToReuse);
    return Integer.valueOf(inputToReuse.getSelectedIndex());
  }

  public Iterable<Integer> getMultiSelectionResult(final String message, final Iterable<String> choices) {
    final Iterable<MultipleChoiceMultiSelectionUserInteraction> inputToReuseCandidates = this
        .<MultipleChoiceMultiSelectionUserInteraction>getMatchingMutltipleChoiceInput(message, choices,
            MultipleChoiceMultiSelectionUserInteraction.class);
    boolean _isEmpty = Iterables.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final MultipleChoiceMultiSelectionUserInteraction inputToReuse = inputToReuseCandidates.iterator().next();
    this.userInteractions.remove(inputToReuse);
    return inputToReuse.getSelectedIndices();
  }
}
