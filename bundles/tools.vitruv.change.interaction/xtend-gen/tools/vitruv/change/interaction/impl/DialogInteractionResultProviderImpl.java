package tools.vitruv.change.interaction.impl;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.interaction.dialogs.BaseDialogWindow;
import tools.vitruv.change.interaction.dialogs.ConfirmationDialogWindow;
import tools.vitruv.change.interaction.dialogs.MultipleChoiceSelectionDialogWindow;
import tools.vitruv.change.interaction.dialogs.NotificationDialogWindow;
import tools.vitruv.change.interaction.dialogs.TextInputDialogWindow;

/**
 * A result provider based on dialog windows to make the requested input.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class DialogInteractionResultProviderImpl implements InteractionResultProvider {
  private final Shell parentShell;

  private final Display display;

  public DialogInteractionResultProviderImpl() {
    Display _xifexpression = null;
    boolean _isWorkbenchRunning = PlatformUI.isWorkbenchRunning();
    if (_isWorkbenchRunning) {
      _xifexpression = PlatformUI.getWorkbench().getDisplay();
    } else {
      _xifexpression = PlatformUI.createDisplay();
    }
    this.display = _xifexpression;
    this.parentShell = null;
  }

  private void showDialog(final BaseDialogWindow dialog) {
    this.display.syncExec(new Runnable() {
      @Override
      public void run() {
        dialog.setBlockOnOpen(true);
        dialog.show();
      }
    });
  }

  @Override
  public boolean getConfirmationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String negativeDecisionText, final String cancelDecisionText) {
    final ConfirmationDialogWindow dialog = new ConfirmationDialogWindow(this.parentShell, windowModality, title, message, positiveDecisionText, negativeDecisionText, cancelDecisionText);
    this.showDialog(dialog);
    return dialog.getConfirmed();
  }

  @Override
  public void getNotificationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType) {
    final NotificationDialogWindow dialog = new NotificationDialogWindow(this.parentShell, windowModality, title, message, positiveDecisionText, notificationType);
    this.showDialog(dialog);
  }

  @Override
  public String getTextInputInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final UserInteractionOptions.InputValidator inputValidator) {
    final TextInputDialogWindow dialog = new TextInputDialogWindow(this.parentShell, windowModality, title, message, positiveDecisionText, cancelDecisionText, inputValidator);
    this.showDialog(dialog);
    return dialog.getInputText();
  }

  @Override
  public int getMultipleChoiceSingleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    final MultipleChoiceSelectionDialogWindow dialog = new MultipleChoiceSelectionDialogWindow(this.parentShell, windowModality, title, message, positiveDecisionText, cancelDecisionText, false, choices);
    this.showDialog(dialog);
    return (int) IterableExtensions.<Integer>head(dialog.getSelectedChoices());
  }

  @Override
  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    final MultipleChoiceSelectionDialogWindow dialog = new MultipleChoiceSelectionDialogWindow(this.parentShell, windowModality, title, message, positiveDecisionText, cancelDecisionText, true, choices);
    this.showDialog(dialog);
    return dialog.getSelectedChoices();
  }
}
