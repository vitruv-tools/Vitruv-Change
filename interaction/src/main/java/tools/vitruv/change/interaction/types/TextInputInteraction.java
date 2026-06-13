package tools.vitruv.change.interaction.types;

import tools.vitruv.change.interaction.FreeTextUserInteraction;
import tools.vitruv.change.interaction.InteractionFactory;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.interaction.UserInteractionOptions.InputValidator;

/**
 * An interaction providing a single- or multi-line text input field, optionally restricted by a {@link InputValidator}.
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class TextInputInteraction extends BaseInteraction<FreeTextUserInteraction> {
  private static final String DEFAULT_TITLE = "Input Text";

  private static final String DEFAULT_MESSAGE = "";

  private UserInteractionOptions.InputValidator inputValidator;

  private UserInteractionOptions.InputFieldType inputFieldType;

  public static final UserInteractionOptions.InputValidator NUMBERS_ONLY_INPUT_VALIDATOR = new UserInteractionOptions.InputValidator() {
    @Override
    public String getInvalidInputMessage(final String input) {
      return "Only numbers are allowed as input";
    }

    @Override
    public boolean isInputValid(final String input) {
      return input.matches("[0-9]*");
    }
  };

  public static final UserInteractionOptions.InputValidator ACCEPT_ALL_INPUT_VALIDATOR = new UserInteractionOptions.InputValidator() {
    @Override
    public String getInvalidInputMessage(final String input) {
      return "";
    }

    @Override
    public boolean isInputValid(final String input) {
      return true;
    }
  };

  protected TextInputInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality, final UserInteractionOptions.InputFieldType fieldType, final UserInteractionOptions.InputValidator inputValidator) {
    super(interactionResultProvider, windowModality, TextInputInteraction.DEFAULT_TITLE, TextInputInteraction.DEFAULT_MESSAGE);
    this.inputFieldType = fieldType;
    this.inputValidator = inputValidator;
  }

  protected TextInputInteraction(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality windowModality) {
    this(interactionResultProvider, windowModality, UserInteractionOptions.InputFieldType.SINGLE_LINE, TextInputInteraction.ACCEPT_ALL_INPUT_VALIDATOR);
  }

  public UserInteractionOptions.InputValidator getInputValidator() {
    return this.inputValidator;
  }

  public void setInputValidator(final UserInteractionOptions.InputValidator newInputValidator) {
    this.inputValidator = newInputValidator;
  }

  public UserInteractionOptions.InputFieldType getInputFieldType() {
    return this.inputFieldType;
  }

  public void setInputFieldType(final UserInteractionOptions.InputFieldType newInputFieldType) {
    this.inputFieldType = newInputFieldType;
  }

  @Override
  public FreeTextUserInteraction startInteraction() {
    final String result = this.getInteractionResultProvider().getTextInputInteractionResult(this.getWindowModality(), this.getTitle(), this.getMessage(), 
      this.getPositiveButtonText(), this.getCancelButtonText(), this.inputValidator);
    final FreeTextUserInteraction userInput = InteractionFactory.eINSTANCE.createFreeTextUserInteraction();
    userInput.setMessage(this.getMessage());
    userInput.setText(result);
    return userInput;
  }
}
