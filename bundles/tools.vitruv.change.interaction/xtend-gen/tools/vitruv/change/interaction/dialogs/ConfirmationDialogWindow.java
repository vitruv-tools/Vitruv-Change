package tools.vitruv.change.interaction.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class ConfirmationDialogWindow extends BaseDialogWindow {
  private boolean confirmed = false;

  private final String positiveButtonText;

  private final String negativeButtonText;

  private final String cancelButtonText;

  public ConfirmationDialogWindow(final Shell parent, final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveButtonText, final String negativeButtonText, final String cancelButtonText) {
    super(parent, windowModality, title, message);
    this.positiveButtonText = positiveButtonText;
    this.negativeButtonText = negativeButtonText;
    this.cancelButtonText = cancelButtonText;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    Control _createDialogArea = super.createDialogArea(parent);
    Composite composite = ((Composite) _createDialogArea);
    final int margins = 20;
    final int spacing = 40;
    final int iconSideLength = 96;
    final Display display = parent.getDisplay();
    final Image icon = display.getSystemImage(SWT.ICON_QUESTION);
    final Shell shell = composite.getShell();
    shell.setImage(icon);
    final GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginWidth = margins;
    gridLayout.marginHeight = margins;
    gridLayout.horizontalSpacing = spacing;
    composite.setLayout(gridLayout);
    final Label iconLabel = new Label(composite, SWT.NONE);
    GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.CENTER;
    gridData.grabExcessVerticalSpace = true;
    iconLabel.setImage(icon);
    Point _point = new Point(iconSideLength, iconSideLength);
    iconLabel.setSize(_point);
    iconLabel.setLayoutData(gridData);
    final Label messageLabel = new Label(composite, SWT.WRAP);
    GridData _gridData = new GridData();
    gridData = _gridData;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = SWT.CENTER;
    messageLabel.setText(this.message);
    messageLabel.setLayoutData(gridData);
    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.createButton(parent, IDialogConstants.YES_ID, this.positiveButtonText, true);
    this.createButton(parent, IDialogConstants.NO_ID, this.negativeButtonText, true);
    this.createButton(parent, IDialogConstants.CANCEL_ID, this.cancelButtonText, true);
  }

  @Override
  protected void buttonPressed(final int buttonId) {
    if ((buttonId == IDialogConstants.YES_ID)) {
      this.confirmed = true;
      this.close();
    } else {
      if ((buttonId == IDialogConstants.NO_ID)) {
        this.confirmed = false;
        this.close();
      } else {
        this.cancelPressed();
      }
    }
  }

  public boolean getConfirmed() {
    return this.confirmed;
  }
}
