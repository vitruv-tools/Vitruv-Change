package tools.vitruv.change.interaction.builder.impl;

import java.util.function.Function;
import tools.vitruv.change.interaction.FreeTextUserInteraction;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.interaction.UserInteractionOptions.InputValidator;
import tools.vitruv.change.interaction.builder.TextInputInteractionBuilder;
import tools.vitruv.change.interaction.types.InteractionFactory;
import tools.vitruv.change.interaction.types.TextInputInteraction;

/**
 * Builder class for {@link TextInputInteraction}s. Use the add/set... methods to specify details and then call
 * createAndShow() to display and get a reference to the configured dialog.
 * Creates a dialog with a text input field (configurable to accept single or multi-line input). A {@link InputValidator}
 * can also be specified which limits the input to strings conforming to its
 * {@link InputValidator#isInputValid(String) isInputValid} method (the default validator accepts all input).<br>
 * <br>
 * For further info on the rationale behind the ...InteractionBuilder implementation, see the {@link InteractionBuilder} javadoc.
 * @see TextInputInteractionBuilder
 * 
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class TextInputInteractionBuilderImpl extends BaseInteractionBuilder<String, TextInputInteraction, TextInputInteractionBuilder.OptionalSteps> implements TextInputInteractionBuilder, TextInputInteractionBuilder.OptionalSteps {
  public TextInputInteractionBuilderImpl(final InteractionFactory interactionFactory, final Iterable<UserInteractionListener> userInteractionListener) {
    super(interactionFactory, userInteractionListener);
  }

  @Override
  public TextInputInteractionBuilder.OptionalSteps message(final String message) {
    this.setMessage(message);
    return this;
  }

  @Override
  public TextInputInteractionBuilder.OptionalSteps inputValidator(final UserInteractionOptions.InputValidator inputValidator) {
    if ((inputValidator != null)) {
      TextInputInteraction _interactionToBuild = this.getInteractionToBuild();
      _interactionToBuild.setInputValidator(inputValidator);
    }
    return this;
  }

  @Override
  public TextInputInteractionBuilder.OptionalSteps inputValidator(final Function<String, Boolean> validatorFunction, final String invalidInputMessage) {
    if (((validatorFunction != null) && (invalidInputMessage != null))) {
      TextInputInteraction _interactionToBuild = this.getInteractionToBuild();
      _interactionToBuild.setInputValidator(new UserInteractionOptions.InputValidator() {
        @Override
        public String getInvalidInputMessage(final String input) {
          return invalidInputMessage;
        }

        @Override
        public boolean isInputValid(final String input) {
          return (validatorFunction.apply(input)).booleanValue();
        }
      });
    }
    return this;
  }

  @Override
  public TextInputInteractionBuilder.OptionalSteps inputFieldType(final UserInteractionOptions.InputFieldType inputFieldType) {
    if ((inputFieldType != null)) {
      TextInputInteraction _interactionToBuild = this.getInteractionToBuild();
      _interactionToBuild.setInputFieldType(inputFieldType);
    }
    return this;
  }

  @Override
  public String startInteraction() {
    final FreeTextUserInteraction result = this.getInteractionToBuild().startInteraction();
    this.notifyUserInputReceived(result);
    return result.getText();
  }

  @Override
  public TextInputInteraction createUserInteraction() {
    return this.getInteractionFactory().createTextInputInteraction();
  }

  @Override
  protected TextInputInteractionBuilder.OptionalSteps getSelf() {
    return this;
  }
}
