package tools.vitruv.change.interaction.dialogs;

import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * @author Dominik Klooz
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class MultipleChoiceSelectionDialogWindow extends BaseDialogWindow {
  private final boolean multiple;

  private final Iterable<String> choices;

  private List<Button> choiceButtons;

  private final String positiveButtonText;

  private final String cancelButtonText;

  private Iterable<Integer> selectedChoices;

  public MultipleChoiceSelectionDialogWindow(final Shell parent, final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveButtonText, final String cancelButtonText, final boolean multiple, final Iterable<String> choices) {
    super(parent, windowModality, title, message);
    this.multiple = multiple;
    this.choices = choices;
    this.positiveButtonText = positiveButtonText;
    this.cancelButtonText = cancelButtonText;
    ArrayList<Integer> _arrayList = new ArrayList<Integer>();
    this.selectedChoices = _arrayList;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    Control _createDialogArea = super.createDialogArea(parent);
    Composite composite = ((Composite) _createDialogArea);
    final int margins = 20;
    final int spacing = 40;
    final GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginWidth = margins;
    gridLayout.marginHeight = margins;
    gridLayout.horizontalSpacing = spacing;
    composite.setLayout(gridLayout);
    final Label messageLabel = new Label(composite, SWT.WRAP);
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = SWT.CENTER;
    messageLabel.setText(this.message);
    messageLabel.setLayoutData(gridData);
    int _xifexpression = (int) 0;
    if (this.multiple) {
      _xifexpression = SWT.CHECK;
    } else {
      _xifexpression = SWT.RADIO;
    }
    final int buttonType = _xifexpression;
    final Group group1 = new Group(composite, SWT.SHADOW_IN);
    RowLayout choicesLayout = new RowLayout(SWT.VERTICAL);
    choicesLayout.justify = true;
    choicesLayout.fill = true;
    choicesLayout.wrap = true;
    group1.setLayout(choicesLayout);
    ArrayList<Button> _arrayList = new ArrayList<Button>();
    this.choiceButtons = _arrayList;
    final Consumer<String> _function = (String choice) -> {
      final Button button = new Button(group1, buttonType);
      button.setText(choice);
      this.choiceButtons.add(button);
    };
    this.choices.forEach(_function);
    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.createButton(parent, IDialogConstants.OK_ID, this.positiveButtonText, true);
    this.createButton(parent, IDialogConstants.CANCEL_ID, this.cancelButtonText, true);
  }

  @Override
  public void okPressed() {
    final Function1<Pair<Integer, Button>, Boolean> _function = (Pair<Integer, Button> pair) -> {
      return Boolean.valueOf(pair.getValue().getSelection());
    };
    final Function1<Pair<Integer, Button>, Integer> _function_1 = (Pair<Integer, Button> pair) -> {
      return pair.getKey();
    };
    this.selectedChoices = IterableUtil.<Pair<Integer, Button>, Integer>mapFixed(IterableExtensions.<Pair<Integer, Button>>filter(IterableExtensions.<Button>indexed(this.choiceButtons), _function), _function_1);
    this.close();
  }

  @Override
  public void cancelPressed() {
    this.close();
  }

  public Iterable<Integer> getSelectedChoices() {
    return this.selectedChoices;
  }
}
