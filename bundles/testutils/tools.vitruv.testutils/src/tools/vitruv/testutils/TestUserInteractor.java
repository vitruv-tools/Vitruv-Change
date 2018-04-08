package tools.vitruv.testutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;

import tools.vitruv.framework.change.interaction.UserInputBase;
import tools.vitruv.framework.userinteraction.ConfirmationDialogBuilder;
import tools.vitruv.framework.userinteraction.DialogBuilder;
import tools.vitruv.framework.userinteraction.InputFieldType;
import tools.vitruv.framework.userinteraction.MultipleChoiceSelectionDialogBuilder;
import tools.vitruv.framework.userinteraction.NotificationDialogBuilder;
import tools.vitruv.framework.userinteraction.NotificationType;
import tools.vitruv.framework.userinteraction.SelectionType;
import tools.vitruv.framework.userinteraction.TextInputDialogBuilder;
import tools.vitruv.framework.userinteraction.UserInteracting;
import tools.vitruv.framework.userinteraction.WindowModality;
import tools.vitruv.framework.userinteraction.impl.TextInputDialog.InputValidator;

/**
 * The {@link TestUserInteractor} can be used in tests to simulate UserInteracting. It has a queue
 * of next selections. If the queue is empty an {@link IllegalStateException} is thrown. It also allows to simulate the
 * thinking time for a user.
 *
 */
public class TestUserInteractor implements UserInteracting, UserInteracting.UserInputListener {
    private static final Logger logger = Logger.getLogger(TestUserInteractor.class);
    private final Queue<URI> uriQueue;
    private final Queue<Boolean> confirmationQueue;
    private final Queue<String> freeTextQueue;
    private final Queue<Integer> singleSelectionQueue;
    private final Queue<Integer[]> multiSelectionQueue;
    private final Random random;
    private final int minWaittime;
    private final int maxWaittime;
    private final int waitTimeRange;

	private Collection<String> messageLog;

    public TestUserInteractor(final int minWaittime, final int maxWaittime) {
        if (minWaittime > maxWaittime) {
            throw new RuntimeException(
                    "Configure min and max waittime properly: Min" + minWaittime + " Max: " + maxWaittime);
        }
        this.minWaittime = minWaittime;
        this.maxWaittime = maxWaittime;
        this.waitTimeRange = maxWaittime - minWaittime;
        uriQueue = new LinkedList<URI>();
        confirmationQueue = new LinkedList<Boolean>();
        freeTextQueue = new LinkedList<String>();
        singleSelectionQueue = new LinkedList<Integer>();
        multiSelectionQueue = new LinkedList<Integer[]>();
        // TODO: messageLog never used?!
        this.random = new Random();
        this.messageLog = new ArrayList<String>();
    }

    public TestUserInteractor() {
        this(-1, -1);
    }
    
    public void addNextConfirmationInputs(final boolean... nextConfirmations) {
    	this.confirmationQueue.clear();
    	for (int i = 0; i < nextConfirmations.length; i++) {
    		this.confirmationQueue.add(nextConfirmations[i]);
    	}
    }
    
    public void addNextTextInputs(final String... nextFreeTexts) {
    	this.freeTextQueue.clear();
    	this.freeTextQueue.addAll(Arrays.asList(nextFreeTexts));
    }
    
    public void addNextSingleSelections(final int... nextSingleSelections) {
    	this.singleSelectionQueue.clear();
    	for (int i = 0; i < nextSingleSelections.length; i++) {
    		this.singleSelectionQueue.add(nextSingleSelections[i]);
    	}
    }
    
    public void addNextMultiSelections(final int[]... nextMultiSelections) {
    	this.multiSelectionQueue.clear();
    	for (int i = 0; i < nextMultiSelections.length; i++) {
    		Integer[] currentMultiSelection = Arrays.stream(nextMultiSelections[i]).boxed().toArray(Integer[]::new);
    		this.multiSelectionQueue.add(currentMultiSelection);
    	}
    }
    
    public void addNextUriSelections(final URI... nextSelections) {
        this.uriQueue.clear();
        this.uriQueue.addAll(Arrays.asList(nextSelections));
    }

    private void simulateUserThinktime() {
        if (-1 < this.maxWaittime) {
            final int currentWaittime = this.random.nextInt(this.waitTimeRange + 1) + this.minWaittime;
            try {
                Thread.sleep(currentWaittime);
            } catch (final InterruptedException e) {
                logger.trace("User think time simulation thread interrupted: " + e, e);
            }
        }
    }

	@Override
	public URI selectURI(String message) {
		if (this.uriQueue.isEmpty()) {
			throw new IllegalStateException("No URI found in " + TestUserInteractor.class.getSimpleName() + " for message " + message);
		}
		
		final URI result = this.uriQueue.poll();
		logger.info(TestUserInteractor.class.getSimpleName() + " selected " + result.toString());
		return result;
	}
	
	public boolean isResourceQueueEmpty() {
		return this.uriQueue.isEmpty();
	}
	
	public Collection<String> getMessageLog(){
		return this.messageLog;
	}

	@Override
	public NotificationDialogBuilder getNotificationDialogBuilder() {
		return new TestNotificationDialogBuilder(this);
	}
	
	public void simulateNotification(String logMessage) {
		logger.info(logMessage);
	}

	@Override
	public ConfirmationDialogBuilder getConfirmationDialogBuilder() {
		return new TestConfirmationDialogBuilder(this);
	}
	
	public boolean simulateConfirmation(String logMessage) {
		logger.info(logMessage);
		this.simulateUserThinktime();
		if (!this.confirmationQueue.isEmpty()) {
            boolean userConfirmed = this.confirmationQueue.poll();
            logger.info(TestUserInteractor.class.getSimpleName() + " selected " + userConfirmed);
            /*ConfirmationUserInput result = InteractionFactory.eINSTANCE.createConfirmationUserInput();
        	result.setConfirmed(userConfirmed);*/
            return userConfirmed;
        } else {
        	throw new IllegalStateException("No user interaction confirmation specified!");
        }       
	}

	@Override
	public TextInputDialogBuilder getTextInputDialogBuilder() {
		return new TestTextInputDialogBuilder(this);
	}
	
	public String simulateTextInput(String logMessage) {
		logger.info(logMessage);
		this.simulateUserThinktime();
		if (!this.freeTextQueue.isEmpty()) {
			String input = freeTextQueue.poll();
			logger.info(TestUserInteractor.class.getSimpleName() + " got input \"" + input + "\"");
			return input;
		} else {
        	throw new IllegalStateException("No user interaction text input specified!");
        }
	}

	@Override
	public MultipleChoiceSelectionDialogBuilder getMultipleChoiceSelectionDialogBuilder() {
		return new TestMultipleChoiceSelectionDialogBuilder(this);
	}
	
	public int simulateSingleSelect(String logMessage, int choicesCount) {
		logger.info(logMessage);
		this.simulateUserThinktime();
		if (!this.singleSelectionQueue.isEmpty()) {
			int selectedIndex = singleSelectionQueue.poll();
			if (selectedIndex >= choicesCount) {
                logger.warn("selectedIndex > choicesCount - could lead to array out of bounds exception later on.");
            }
			logger.info(TestUserInteractor.class.getSimpleName() + " selected index " + selectedIndex);
			return selectedIndex;
		} else {
        	throw new IllegalStateException("No user interaction single select index specified!");
        }
	}
	
	public int[] simulateMultiSelect(String logMessage, int choicesCount) {
		logger.info(logMessage);
		this.simulateUserThinktime();
		if (!this.multiSelectionQueue.isEmpty()) {
			Stream<Integer> choicesStream = Arrays.stream(multiSelectionQueue.poll());
			int[] selectedIndices = choicesStream.mapToInt(Integer::intValue).toArray();
			if (choicesStream.anyMatch(index -> index > choicesCount)) {
				logger.warn("selectedIndices contains index > choicesCount - could lead to array out of bounds exception later on.");
			}
			logger.info(TestUserInteractor.class.getSimpleName() + " selected indices " + Arrays.toString(selectedIndices));
			return selectedIndices;
		} else {
        	throw new IllegalStateException("No user interaction multi select indices specified!");
        }
	}

	@Override
	public void onUserInputReceived(UserInputBase input) {
		// TODO DK: add some sort of bookkeeping here like in UserInteractor?
	}

	@Override
	public Collection<UserInputBase> getUserInputs() {
		// TODO DK: make user inputs accessible?
		return null;
	}
	
	@Override
	public void resetUserInputs() {
		// TODO DK: do something here?
	}
}


abstract class TestBaseDialogBuilder<V, T extends DialogBuilder<V, T>> implements DialogBuilder<V, T> {
	protected Map<String, String> dialogProperties = new HashMap<>();
	protected TestUserInteractor testUserInteractor;
	
	public TestBaseDialogBuilder(TestUserInteractor testUserInteractor) {
		this.testUserInteractor = testUserInteractor;
	}
	
    @Override
    abstract public V showDialogAndGetUserInput(); 
    
    protected void openDialog() { }
    
    @SuppressWarnings("unchecked")
	@Override
    public T title(String title) {
    	dialogProperties.put("title", title);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T windowModality(WindowModality windowModality) {
    	dialogProperties.put("window modality", windowModality.name());
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T positiveButtonText(String text) {
    	dialogProperties.put("positive button text", text);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T negativeButtonText(String text) {
    	dialogProperties.put("negative button text", text);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T cancelButtonText(String text) {
    	dialogProperties.put("cancel button text", text);
        return (T) this;
    }
}


class TestNotificationDialogBuilder extends TestBaseDialogBuilder<Void, NotificationDialogBuilder.OptionalSteps> implements NotificationDialogBuilder,
		NotificationDialogBuilder.OptionalSteps {
	
	public TestNotificationDialogBuilder(TestUserInteractor testUserInteractor) {
		super(testUserInteractor);
	}
    
    @Override
    public NotificationDialogBuilder.OptionalSteps notificationType(NotificationType notificationType) {
    	dialogProperties.put("notification type", notificationType.name());
        return this;
    }

    @Override
    public Void showDialogAndGetUserInput() {
    	String dialogSummary = dialogProperties.entrySet().stream().map(Object::toString).reduce("",
    			((a, b) -> a + ", " + b));
    	testUserInteractor.simulateNotification("Showing NotificationDialog: " + dialogSummary);
        return null; // notifications don't have any form of user input
    }
    
    @Override
    public NotificationDialogBuilder.OptionalSteps message(String message) {
    	dialogProperties.put("message", message);
        return this;
    }
}


class TestConfirmationDialogBuilder extends TestBaseDialogBuilder<Boolean, ConfirmationDialogBuilder.OptionalSteps>
		implements ConfirmationDialogBuilder, ConfirmationDialogBuilder.OptionalSteps {
	
	public TestConfirmationDialogBuilder(TestUserInteractor testUserInteractor) {
		super(testUserInteractor);
	}
	
	@Override
	public Boolean showDialogAndGetUserInput() {
		String dialogSummary = dialogProperties.entrySet().stream().map(Object::toString).reduce("", ((a, b) -> a + ", " + b));
		return testUserInteractor.simulateConfirmation("Showing ConfirmationDialog: " + dialogSummary);
	}
	
	@Override
	public ConfirmationDialogBuilder.OptionalSteps message(String message) {
		dialogProperties.put("message", message);
		return this;
	}
}


class TestTextInputDialogBuilder extends TestBaseDialogBuilder<String, TextInputDialogBuilder.OptionalSteps>
		implements TextInputDialogBuilder, TextInputDialogBuilder.OptionalSteps {
	
	public TestTextInputDialogBuilder(TestUserInteractor testUserInteractor) {
		super(testUserInteractor);
	}
	
	@Override
	public String showDialogAndGetUserInput() {
		String dialogSummary = dialogProperties.entrySet().stream().map(Object::toString).reduce("", ((a, b) -> a + ", " + b));
		return testUserInteractor.simulateTextInput("Showing TextInputDialog: " + dialogSummary);
	}
	
	@Override
	public OptionalSteps message(String message) {
		dialogProperties.put("message", message);
		return this;
	}

	@Override
	public OptionalSteps inputValidator(InputValidator inputValidator) {
		dialogProperties.put("inputValidator", inputValidator.getInvalidInputMessage("[input]"));
		return this;
	}

	@Override
	public OptionalSteps inputValidator(Function<String, Boolean> validatorFunction, String invalidInputMessage) {
		dialogProperties.put("inputValidator", invalidInputMessage);
		return this;
	}

	@Override
	public OptionalSteps inputFieldType(InputFieldType inputFieldType) {
		dialogProperties.put("inputFieldType", inputFieldType.name());
		return this;
	}
}


class TestMultipleChoiceSelectionDialogBuilder extends TestBaseDialogBuilder<Collection<Integer>,
		MultipleChoiceSelectionDialogBuilder.OptionalSteps> implements MultipleChoiceSelectionDialogBuilder,
		MultipleChoiceSelectionDialogBuilder.ChoicesStep, MultipleChoiceSelectionDialogBuilder.OptionalSteps {
	private boolean isSingleSelect = true;
	private String[] choices;
	
	public TestMultipleChoiceSelectionDialogBuilder(TestUserInteractor testUserInteractor) {
		super(testUserInteractor);
	}
	
	@Override
	public Collection<Integer> showDialogAndGetUserInput() {
		String dialogSummary = dialogProperties.entrySet().stream().map(Object::toString).reduce("", ((a, b) -> a + ", " + b));
		Collection<Integer> selection = new ArrayList<>();
		if (isSingleSelect) {
			selection.add(testUserInteractor.simulateSingleSelect(
					"Showing MultipleChoiceSelectionDialog (single selection mode): " + dialogSummary, choices.length));
		} else {
			Arrays.stream(testUserInteractor.simulateMultiSelect(
					"Showing MultipleChoiceSelectionDialog (multi selection mode): " + dialogSummary, choices.length))
					.forEach(e -> selection.add(e));
		}
		return selection;
	}
	
	@Override
	public ChoicesStep message(String message) {
		dialogProperties.put("message", message);
		return this;
	}
	
	@Override
	public OptionalSteps choices(String[] choices) {
		this.choices = choices;
		dialogProperties.put("choices", choices.toString());
		return this;
	}
	
	@Override
	public OptionalSteps selectionType(SelectionType selectionType) {
		isSingleSelect = (selectionType == SelectionType.SINGLE_SELECT);
		dialogProperties.put("selectionType", selectionType.name());
		return this;
	}
}
