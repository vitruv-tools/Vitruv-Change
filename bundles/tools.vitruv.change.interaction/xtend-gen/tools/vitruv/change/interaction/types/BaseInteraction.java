package tools.vitruv.change.interaction.types;

import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * Base class for interactions defining methods common to all types of interaction like texts for common elements and code to
 * handle changes to the window modality.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public abstract class BaseInteraction<T extends UserInteractionBase> {
  @Accessors(AccessorType.PROTECTED_GETTER)
  private final InteractionResultProvider interactionResultProvider;

  private String title;

  private String message;

  private String positiveButtonText = "Yes";

  private String negativeButtonText = "No";

  private String cancelButtonText = "Cancel";

  private UserInteractionOptions.WindowModality windowModality;

  protected BaseInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality, final String title, final String message) {
    this.interactionResultProvider = interactionResultProvider;
    this.title = title;
    this.message = message;
    this.windowModality = windowModality;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public String getPositiveButtonText() {
    return this.positiveButtonText;
  }

  public String setPositiveButtonText(final String positiveButtonText) {
    return this.positiveButtonText = positiveButtonText;
  }

  public String getNegativeButtonText() {
    return this.negativeButtonText;
  }

  public String setNegativeButtonText(final String negativeButtonText) {
    return this.negativeButtonText = negativeButtonText;
  }

  public String getCancelButtonText() {
    return this.cancelButtonText;
  }

  public String setCancelButtonText(final String cancelButtonText) {
    return this.cancelButtonText = cancelButtonText;
  }

  public UserInteractionOptions.WindowModality getWindowModality() {
    return this.windowModality;
  }

  public void setWindowModality(final UserInteractionOptions.WindowModality windowModality) {
    this.windowModality = windowModality;
  }

  public abstract T startInteraction();

  @Pure
  protected InteractionResultProvider getInteractionResultProvider() {
    return this.interactionResultProvider;
  }
}
