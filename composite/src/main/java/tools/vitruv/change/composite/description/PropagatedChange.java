package tools.vitruv.change.composite.description;

import org.eclipse.emf.ecore.EObject;
public class PropagatedChange {
  private final VitruviusChange<EObject> originalChange;

  private final VitruviusChange<EObject> consequentialChanges;

  @Override
  public String toString() {
    return "Original change:" + System.lineSeparator() +
        "\t" + this.originalChange + System.lineSeparator() +
        "Consequential change:" + System.lineSeparator() +
        "\t" + this.consequentialChanges + System.lineSeparator();
  }

  public PropagatedChange(final VitruviusChange<EObject> originalChange, final VitruviusChange<EObject> consequentialChanges) {
    super();
    this.originalChange = originalChange;
    this.consequentialChanges = consequentialChanges;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.originalChange== null) ? 0 : this.originalChange.hashCode());
    return prime * result + ((this.consequentialChanges== null) ? 0 : this.consequentialChanges.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PropagatedChange other = (PropagatedChange) obj;
    if (this.originalChange == null) {
      if (other.originalChange != null)
        return false;
    } else if (!this.originalChange.equals(other.originalChange))
      return false;
    if (this.consequentialChanges == null) {
      if (other.consequentialChanges != null)
        return false;
    } else if (!this.consequentialChanges.equals(other.consequentialChanges))
      return false;
    return true;
  }
  public VitruviusChange<EObject> getOriginalChange() {
    return this.originalChange;
  }
  public VitruviusChange<EObject> getConsequentialChanges() {
    return this.consequentialChanges;
  }
}
