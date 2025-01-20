package tools.vitruv.change.interaction.dialogs;

import com.google.common.base.Objects;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class TextInputDialogWindow extends BaseDialogWindow {
  private Text inputField;

  private ControlDecoration inputDecorator;

  private UserInteractionOptions.InputFieldType inputFieldType;

  private final UserInteractionOptions.InputValidator inputValidator;

  private final String positiveButtonText;

  private final String cancelButtonText;

  private String input = "";

  public TextInputDialogWindow(final Shell parent, final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveButtonText, final String cancelButtonText, final UserInteractionOptions.InputValidator inputValidator) {
    super(parent, windowModality, title, message);
    this.inputValidator = inputValidator;
    this.positiveButtonText = positiveButtonText;
    this.cancelButtonText = cancelButtonText;
  }

  @Override
  public Control createDialogArea(final Composite parent) {
    Control _createDialogArea = super.createDialogArea(parent);
    final Composite composite = ((Composite) _createDialogArea);
    final int margins = 20;
    final int spacing = 20;
    final GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginWidth = margins;
    gridLayout.marginHeight = margins;
    gridLayout.verticalSpacing = spacing;
    composite.setLayout(gridLayout);
    final Label messageLabel = new Label(composite, SWT.WRAP);
    messageLabel.setText(this.message);
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    messageLabel.setLayoutData(gridData);
    int _switchResult = (int) 0;
    final UserInteractionOptions.InputFieldType inputFieldType = this.inputFieldType;
    if (inputFieldType != null) {
      switch (inputFieldType) {
        case SINGLE_LINE:
          _switchResult = (SWT.SINGLE | SWT.CENTER);
          break;
        case MULTI_LINE:
          _switchResult = (SWT.MULTI | SWT.V_SCROLL);
          break;
        default:
          break;
      }
    }
    final int linesProperty = _switchResult;
    Text _text = new Text(composite, (linesProperty | SWT.BORDER));
    this.inputField = _text;
    this.inputField.addVerifyListener(new VerifyListener() {
      @Override
      public void verifyText(final VerifyEvent e) {
        String currentText = ((Text) e.widget).getText();
        String _substring = currentText.substring(0, e.start);
        String _plus = (_substring + e.text);
        String _substring_1 = currentText.substring(e.end);
        String newText = (_plus + _substring_1);
        boolean _isInputValid = TextInputDialogWindow.this.inputValidator.isInputValid(newText);
        boolean _not = (!_isInputValid);
        if (_not) {
          e.doit = false;
          TextInputDialogWindow.this.inputDecorator.setDescriptionText(TextInputDialogWindow.this.inputValidator.getInvalidInputMessage(newText));
          TextInputDialogWindow.this.inputDecorator.show();
        } else {
          TextInputDialogWindow.this.inputDecorator.hide();
        }
      }
    });
    ControlDecoration _controlDecoration = new ControlDecoration(this.inputField, SWT.CENTER);
    this.inputDecorator = _controlDecoration;
    this.inputDecorator.setDescriptionText(this.inputValidator.getInvalidInputMessage(""));
    final Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
    this.inputDecorator.setImage(image);
    this.inputDecorator.hide();
    GridData _gridData = new GridData();
    gridData = _gridData;
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    boolean _equals = Objects.equal(this.inputFieldType, UserInteractionOptions.InputFieldType.MULTI_LINE);
    if (_equals) {
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = SWT.FILL;
      Shell _shell = composite.getShell();
      Point _point = new Point(300, 300);
      _shell.setMinimumSize(_point);
    }
    this.inputField.setLayoutData(gridData);
    return composite;
  }

  @Override
  public void createButtonsForButtonBar(final Composite parent) {
    this.createButton(parent, IDialogConstants.OK_ID, this.positiveButtonText, true);
    this.createButton(parent, IDialogConstants.CANCEL_ID, this.cancelButtonText, true);
  }

  @Override
  public void okPressed() {
    boolean _isInputValid = this.inputValidator.isInputValid(this.inputField.getText());
    boolean _not = (!_isInputValid);
    if (_not) {
      this.inputDecorator.showHoverText(this.inputDecorator.getDescriptionText());
      return;
    }
    this.input = this.inputField.getText();
    this.close();
  }

  public String getInputText() {
    return this.input;
  }
}
