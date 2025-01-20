package tools.vitruv.change.atomic.command;

import java.util.Collections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.xtext.xbase.lib.Conversions;

/**
 * Command which is used to remove a entry of a EList at a specific index.
 * Only single objects are supported.
 */
@SuppressWarnings("all")
public class RemoveAtCommand extends RemoveCommand {
  /**
   * Index at which the value is removed in the list.
   */
  private int index;

  /**
   * Constructor for a RemoveAtCommand, which removes an entry of an EList feature at a specific index.
   * @param editingDomain The used editing domain.
   * @param owner The owning EObject of the EList.
   * @param feature The feature of the owner which is an EList.
   * @param value The value which will be removed.
   * @param index Index at which the value is removed in the EList.
   */
  public RemoveAtCommand(final EditingDomain editingDomain, final EObject owner, final EStructuralFeature feature, final Object value, final int index) {
    super(editingDomain, owner, feature, Collections.<Object>singleton(value));
    this.index = index;
  }

  /**
   * Constructor for a RemoveATCommand, which removes an entry of a EList at a specific index.
   * @param editingDomain The used editing domain.
   * @param ownerList The edited EList.
   * @param value The value which will be removed.
   * @param index The Index at which the value is removed in the EList.
   */
  public RemoveAtCommand(final EditingDomain editingDomain, final EList<?> ownerList, final Object value, final int index) {
    super(editingDomain, ownerList, Collections.<Object>singleton(value));
    this.index = index;
  }

  /**
   * Returns the index at which the value will be removed.
   * @return The index
   */
  public int getIndex() {
    return this.index;
  }

  @Override
  public void doExecute() {
    this.ownerList.remove(this.index);
  }

  @Override
  public boolean prepare() {
    boolean result = (((super.prepare() && (0 <= this.index)) && (this.index < this.ownerList.size())) && (this.collection.size() == 1));
    if ((!result)) {
      return false;
    }
    return this.ownerList.get(this.index).equals(((Object[])Conversions.unwrapArray(this.collection, Object.class))[0]);
  }

  @Override
  public void doUndo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void doRedo() {
    throw new UnsupportedOperationException();
  }
}
