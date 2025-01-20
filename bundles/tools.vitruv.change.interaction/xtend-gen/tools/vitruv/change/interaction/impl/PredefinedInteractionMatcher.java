package tools.vitruv.change.interaction.impl;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import tools.vitruv.change.interaction.ConfirmationUserInteraction;
import tools.vitruv.change.interaction.FreeTextUserInteraction;
import tools.vitruv.change.interaction.MultipleChoiceMultiSelectionUserInteraction;
import tools.vitruv.change.interaction.MultipleChoiceSelectionInteractionBase;
import tools.vitruv.change.interaction.MultipleChoiceSingleSelectionUserInteraction;
import tools.vitruv.change.interaction.UserInteractionBase;

/**
 * A matcher class that allows to get an appropriate {@link UserInteractionBase} of those predefined using
 * {@link PredefinedInteractionMatcher#addInteraction(UserInteractionBase) addInteraction(UserInteractionBase)} for each kind of request.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class PredefinedInteractionMatcher {
  private List<UserInteractionBase> userInteractions = CollectionLiterals.<UserInteractionBase>newArrayList();

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
    final Function1<T, Boolean> _function = (T input) -> {
      return Boolean.valueOf(((!input.isSetMessage()) || input.getMessage().equals(message)));
    };
    final Iterable<T> inputToReuse = IterableExtensions.<T>filter(Iterables.<T>filter(this.userInteractions, type), _function);
    return inputToReuse;
  }

  protected <T extends MultipleChoiceSelectionInteractionBase> Iterable<T> getMatchingMutltipleChoiceInput(final String message, final Iterable<String> choices, final Class<T> type) {
    if ((this.userInteractions == null)) {
      return Collections.<T>emptyList();
    }
    final Function1<T, Boolean> _function = (T input) -> {
      return Boolean.valueOf(((!input.isSetMessage()) || (input.getMessage().equals(message) && input.getChoices().containsAll(IterableExtensions.<String>toList(choices)))));
    };
    final Iterable<T> inputToReuse = IterableExtensions.<T>filter(Iterables.<T>filter(this.userInteractions, type), _function);
    return inputToReuse;
  }

  public Boolean getNotificationResult(final String message) {
    final Iterable<ConfirmationUserInteraction> inputToReuseCandidates = this.<ConfirmationUserInteraction>getMatchingInput(message, ConfirmationUserInteraction.class);
    boolean _isEmpty = IterableExtensions.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final ConfirmationUserInteraction inputToReuse = IterableExtensions.<ConfirmationUserInteraction>head(inputToReuseCandidates);
    this.userInteractions.remove(inputToReuse);
    return Boolean.valueOf(true);
  }

  public Boolean getConfirmationResult(final String message) {
    final Iterable<ConfirmationUserInteraction> inputToReuseCandidates = this.<ConfirmationUserInteraction>getMatchingInput(message, ConfirmationUserInteraction.class);
    boolean _isEmpty = IterableExtensions.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final ConfirmationUserInteraction inputToReuse = IterableExtensions.<ConfirmationUserInteraction>head(inputToReuseCandidates);
    this.userInteractions.remove(inputToReuse);
    return Boolean.valueOf(inputToReuse.isConfirmed());
  }

  public String getTextInputResult(final String message) {
    final Iterable<FreeTextUserInteraction> inputToReuseCandidates = this.<FreeTextUserInteraction>getMatchingInput(message, FreeTextUserInteraction.class);
    boolean _isEmpty = IterableExtensions.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final FreeTextUserInteraction inputToReuse = IterableExtensions.<FreeTextUserInteraction>head(inputToReuseCandidates);
    this.userInteractions.remove(inputToReuse);
    return inputToReuse.getText();
  }

  public Integer getSingleSelectionResult(final String message, final Iterable<String> choices) {
    final Iterable<MultipleChoiceSingleSelectionUserInteraction> inputToReuseCandidates = this.<MultipleChoiceSingleSelectionUserInteraction>getMatchingMutltipleChoiceInput(message, choices, 
      MultipleChoiceSingleSelectionUserInteraction.class);
    boolean _isEmpty = IterableExtensions.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final MultipleChoiceSingleSelectionUserInteraction inputToReuse = IterableExtensions.<MultipleChoiceSingleSelectionUserInteraction>head(inputToReuseCandidates);
    this.userInteractions.remove(inputToReuse);
    return Integer.valueOf(inputToReuse.getSelectedIndex());
  }

  public Iterable<Integer> getMultiSelectionResult(final String message, final Iterable<String> choices) {
    final Iterable<MultipleChoiceMultiSelectionUserInteraction> inputToReuseCandidates = this.<MultipleChoiceMultiSelectionUserInteraction>getMatchingMutltipleChoiceInput(message, choices, 
      MultipleChoiceMultiSelectionUserInteraction.class);
    boolean _isEmpty = IterableExtensions.isEmpty(inputToReuseCandidates);
    if (_isEmpty) {
      return null;
    }
    final MultipleChoiceMultiSelectionUserInteraction inputToReuse = IterableExtensions.<MultipleChoiceMultiSelectionUserInteraction>head(inputToReuseCandidates);
    this.userInteractions.remove(inputToReuse);
    return inputToReuse.getSelectedIndices();
  }
}
