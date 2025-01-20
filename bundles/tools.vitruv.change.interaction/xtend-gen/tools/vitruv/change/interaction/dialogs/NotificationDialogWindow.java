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
public class NotificationDialogWindow extends BaseDialogWindow {
  private final UserInteractionOptions.NotificationType notificationType;

  private final String positiveButtonText;

  public NotificationDialogWindow(final Shell parent, final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveButtonText, final UserInteractionOptions.NotificationType notificationType) {
    super(parent, windowModality, title, message);
    this.positiveButtonText = positiveButtonText;
    this.notificationType = notificationType;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    Control _createDialogArea = super.createDialogArea(parent);
    Composite composite = ((Composite) _createDialogArea);
    final Display display = parent.getDisplay();
    int _switchResult = (int) 0;
    final UserInteractionOptions.NotificationType notificationType = this.notificationType;
    if (notificationType != null) {
      switch (notificationType) {
        case INFORMATION:
          _switchResult = SWT.ICON_INFORMATION;
          break;
        case WARNING:
          _switchResult = SWT.ICON_WARNING;
          break;
        case ERROR:
          _switchResult = SWT.ICON_ERROR;
          break;
        default:
          break;
      }
    }
    final Image icon = display.getSystemImage(_switchResult);
    final int margins = 20;
    final int spacing = 40;
    final int iconSideLength = 96;
    final GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginWidth = margins;
    gridLayout.marginHeight = margins;
    gridLayout.horizontalSpacing = spacing;
    composite.setLayout(gridLayout);
    final Shell shell = composite.getShell();
    shell.setImage(icon);
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
    this.createButton(parent, IDialogConstants.OK_ID, this.positiveButtonText, true);
  }
}
