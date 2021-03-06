package tools.vitruv.change.interaction.types

import tools.vitruv.change.interaction.types.InteractionFactory
import tools.vitruv.change.interaction.UserInteractionOptions.WindowModality
import tools.vitruv.change.interaction.InteractionResultProvider

/**
 * Implementation of {@link InteractionFactory} that creates all possible types of interactions.
 * 
 * @author Heiko Klare
 */
class InteractionFactoryImpl implements InteractionFactory {
	final InteractionResultProvider interactionResultProvider;
	final WindowModality windowModality;

	new(InteractionResultProvider interactionResultProvider, WindowModality windowModality) {
		this.windowModality = windowModality;
		this.interactionResultProvider = interactionResultProvider;
	}

	override createConfirmationInteraction() {
		return new ConfirmationInteraction(interactionResultProvider, windowModality);
	}

	override createNotificationInteraction() {
		return new NotificationInteraction(interactionResultProvider, windowModality);
	}

	override createTextInputInteraction() {
		return new TextInputInteraction(interactionResultProvider, windowModality);
	}

	override createMultipleChoiceSingleSelectionInteraction() {
		return new MultipleChoiceSingleSelectionInteraction(interactionResultProvider, windowModality);
	}

	override createMultipleChoiceMultipleSelectionInteraction() {
		return new MultipleChoiceMultipleSelectionInteraction(interactionResultProvider, windowModality);
	}
}
