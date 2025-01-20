package tools.vitruv.change.composite.description;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Pure;

@Data
@SuppressWarnings("all")
public class PropagatedChange {
  private final VitruviusChange<EObject> originalChange;

  private final VitruviusChange<EObject> consequentialChanges;

  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Original change:");
    _builder.newLine();
    _builder.append("\t");
    _builder.append(this.originalChange, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("Consequential change:");
    _builder.newLine();
    _builder.append("\t");
    _builder.append(this.consequentialChanges, "\t");
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }

  public PropagatedChange(final VitruviusChange<EObject> originalChange, final VitruviusChange<EObject> consequentialChanges) {
    super();
    this.originalChange = originalChange;
    this.consequentialChanges = consequentialChanges;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.originalChange== null) ? 0 : this.originalChange.hashCode());
    return prime * result + ((this.consequentialChanges== null) ? 0 : this.consequentialChanges.hashCode());
  }

  @Override
  @Pure
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

  @Pure
  public VitruviusChange<EObject> getOriginalChange() {
    return this.originalChange;
  }

  @Pure
  public VitruviusChange<EObject> getConsequentialChanges() {
    return this.consequentialChanges;
  }
}
