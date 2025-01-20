package tools.vitruv.change.interaction.types;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.MultipleChoiceSelectionInteractionBase;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * Implementation of an interaction providing a list of choices for the user to select a single or multiple ones.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public abstract class MultipleChoiceSelectionInteraction<I extends MultipleChoiceSelectionInteractionBase> extends BaseInteraction<I> {
  private static final String DEFAULT_TITLE = "Please Select";

  private static final String DEFAULT_MESSAGE = "";

  @Accessors(AccessorType.PROTECTED_GETTER)
  private final List<String> choices;

  protected MultipleChoiceSelectionInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality) {
    super(interactionResultProvider, windowModality, MultipleChoiceSelectionInteraction.DEFAULT_TITLE, MultipleChoiceSelectionInteraction.DEFAULT_MESSAGE);
    this.setPositiveButtonText("Accept");
    ArrayList<String> _arrayList = new ArrayList<String>();
    this.choices = _arrayList;
  }

  public void addChoice(final String choice) {
    this.choices.add(choice);
  }

  @Pure
  protected List<String> getChoices() {
    return this.choices;
  }
}
