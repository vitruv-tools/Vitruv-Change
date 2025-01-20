package tools.vitruv.change.interaction.dialogs;

import com.google.common.base.Objects;
import javax.swing.JOptionPane;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public abstract class BaseDialogWindow extends Dialog {
  protected final String title;

  protected final String message;

  public BaseDialogWindow(final Shell parent, final UserInteractionOptions.WindowModality windowModality, final String title, final String message) {
    super(parent);
    this.title = title;
    this.message = message;
    this.updateWindowModality(windowModality);
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    Control _createDialogArea = super.createDialogArea(parent);
    final Composite composite = ((Composite) _createDialogArea);
    Shell _shell = composite.getShell();
    _shell.setText(this.title);
    Shell _shell_1 = composite.getShell();
    Point _point = new Point(300, 200);
    _shell_1.setMinimumSize(_point);
    return composite;
  }

  @Override
  public boolean isResizable() {
    return true;
  }

  /**
   * Opens the dialog centered on the primary monitor.
   */
  public int show() {
    int _xblockexpression = (int) 0;
    {
      Shell _parentShell = this.getParentShell();
      boolean _tripleNotEquals = (_parentShell != null);
      if (_tripleNotEquals) {
        final Rectangle screenSize = this.getParentShell().getDisplay().getPrimaryMonitor().getBounds();
        this.getParentShell().setLocation(((screenSize.width - this.getParentShell().getBounds().width) / 2), 
          ((screenSize.height - this.getParentShell().getBounds().height) / 2));
      }
      _xblockexpression = this.open();
    }
    return _xblockexpression;
  }

  private void updateWindowModality(final UserInteractionOptions.WindowModality windowModality) {
    boolean _equals = Objects.equal(windowModality, UserInteractionOptions.WindowModality.MODAL);
    if (_equals) {
      this.setShellStyle((this.getShellStyle() | SWT.APPLICATION_MODAL));
      this.setBlockOnOpen(true);
    }
    boolean _equals_1 = Objects.equal(windowModality, UserInteractionOptions.WindowModality.MODELESS);
    if (_equals_1) {
      this.setShellStyle((this.getShellStyle() | (SWT.MODELESS | (~SWT.APPLICATION_MODAL))));
      this.setBlockOnOpen(false);
    }
  }

  @Override
  protected void cancelPressed() {
    JOptionPane.showMessageDialog(null, "Canceling decisions during consistency preservation is currently not supported", "Unsupported", JOptionPane.OK_OPTION);
  }
}
