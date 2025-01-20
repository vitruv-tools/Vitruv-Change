package tools.vitruv.change.interaction.builder.impl;

import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.interaction.builder.InteractionBuilder;
import tools.vitruv.change.interaction.types.BaseInteraction;
import tools.vitruv.change.interaction.types.InteractionFactory;

/**
 * Abstract base class for dialog builder objects. The dialog to be built is created and returned in createAndShow, the
 * other methods are to be used beforehand to specify adjustments to the dialogs contents / behavior. Standard values
 * for properties not specified using the respective methods are set here or in the constructor for subclasses and
 * subclass-specific properties.<br>
 * <br>
 * For further info on the rationale behind the ...DialogBuilder implementation, see the {@link DialogBuilder} javadoc.
 * 
 * @param <V> type parameter for the return type of {@link #getResult() getResult()}, which returns the user input from
 *          the dialog.
 * @param <I> the interaction type to be built by this buidler
 * @param <T> type parameter for the return type of methods declared in {@link DialogBuilder}. In subclasses, this is to
 *          be set to the specific builder's {@code OptionalSteps} interface (the interface that extends
 *          {@link DialogBuilder} and contains methods whose execution is optional when building a dialog).
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@Accessors
@SuppressWarnings("all")
public abstract class BaseInteractionBuilder<V extends Object, I extends BaseInteraction<?>, T extends InteractionBuilder<V, T>> implements InteractionBuilder<V, T> {
  @Accessors(AccessorType.PROTECTED_GETTER)
  private final InteractionFactory interactionFactory;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private I interactionToBuild;

  @Accessors(AccessorType.NONE)
  private Iterable<UserInteractionListener> userInteractionListener;

  public BaseInteractionBuilder(final InteractionFactory interactionFactory, final Iterable<UserInteractionListener> userInteractionListener) {
    this.interactionFactory = interactionFactory;
    this.userInteractionListener = userInteractionListener;
    this.interactionToBuild = this.createUserInteraction();
  }

  public abstract I createUserInteraction();

  /**
   * @inheritDoc
   * Implementations should call {@link #openDialog()} to make sure displaying the dialog is run on the UI thread.
   */
  @Override
  public abstract V startInteraction();

  protected abstract T getSelf();

  public void setMessage(final String message) {
    if ((message == null)) {
      throw new IllegalArgumentException("Message is null!");
    }
    this.interactionToBuild.setMessage(message);
  }

  @Override
  public T title(final String title) {
    if ((title != null)) {
      this.interactionToBuild.setTitle(title);
    }
    return this.getSelf();
  }

  @Override
  public T windowModality(final UserInteractionOptions.WindowModality windowModality) {
    if ((windowModality != null)) {
      this.interactionToBuild.setWindowModality(windowModality);
    }
    return this.getSelf();
  }

  @Override
  public T positiveButtonText(final String text) {
    if ((text != null)) {
      this.interactionToBuild.setPositiveButtonText(text);
    }
    return this.getSelf();
  }

  @Override
  public T negativeButtonText(final String text) {
    if ((text != null)) {
      this.interactionToBuild.setNegativeButtonText(text);
    }
    return this.getSelf();
  }

  @Override
  public T cancelButtonText(final String text) {
    if ((text != null)) {
      this.interactionToBuild.setCancelButtonText(text);
    }
    return this.getSelf();
  }

  public void notifyUserInputReceived(final UserInteractionBase input) {
    for (final UserInteractionListener listener : this.userInteractionListener) {
      listener.onUserInteractionReceived(input);
    }
  }

  @Pure
  protected InteractionFactory getInteractionFactory() {
    return this.interactionFactory;
  }

  @Pure
  protected I getInteractionToBuild() {
    return this.interactionToBuild;
  }
}
