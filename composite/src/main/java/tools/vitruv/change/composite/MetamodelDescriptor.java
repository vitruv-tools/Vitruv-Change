package tools.vitruv.change.composite;

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.EPackage;

/**
 * A descriptor for a metamodel represented by its namespace URIs.
 */
public final class MetamodelDescriptor {
  private final Set<String> nsUris;

  private MetamodelDescriptor(final Set<String> nsUris) {
    final Consumer<String> _function = (String it) -> {
      Preconditions.checkArgument((it != null), 
        "metamodel descriptor to be instantiated for namespace URIs %s must not contain a null URI", nsUris);
    };
    nsUris.forEach(_function);
    HashSet<String> _hashSet = new HashSet<String>(nsUris);
    this.nsUris = _hashSet;
  }

  /**
   * Returns whether the metamodel of the given descriptor is contained in this
   * descriptor's metamodel, i.e., whether its namespace URIs are a subset of the
   * ones of this descriptor.
   * 
   * @param descriptorForPotentiallyContainedMetamodel the descriptor to check for
   * 			containment of its metamodel in this one, must not be <code>null</code>
   * @return whether the metamodel of the given descriptor is contained in this one
   */
  public boolean contains(final MetamodelDescriptor descriptorForPotentiallyContainedMetamodel) {
    boolean _xblockexpression = false;
    {
      Preconditions.checkArgument((descriptorForPotentiallyContainedMetamodel != null), 
        "metamodel descriptor to check for containment in %s must not be null", this);
      _xblockexpression = this.nsUris.containsAll(descriptorForPotentiallyContainedMetamodel.nsUris);
    }
    return _xblockexpression;
  }

  @Override
  public boolean equals(final Object obj) {
    boolean _xblockexpression = false;
    {
      if ((obj instanceof MetamodelDescriptor)) {
        return Objects.equals(this.nsUris, ((MetamodelDescriptor)obj).nsUris);
      }
      _xblockexpression = false;
    }
    return _xblockexpression;
  }

  @Override
  public int hashCode() {
    return this.nsUris.hashCode();
  }

  @Override
  public String toString() {
    return this.nsUris.toString();
  }

  public static MetamodelDescriptor of(final EPackage rootPackage) {
    return MetamodelDescriptor.with(Set.of(rootPackage.getNsURI()));
  }

  public static MetamodelDescriptor of(final Set<EPackage> rootPackages) {
    return MetamodelDescriptor.with(rootPackages.stream().map(EPackage::getNsURI).collect(Collectors.toSet()));
  }

  public static MetamodelDescriptor with(final String nsUri) {
    return MetamodelDescriptor.with(Set.of(nsUri));
  }

  public static MetamodelDescriptor with(final Set<String> nsUris) {
    return new MetamodelDescriptor(nsUris);
  }
  public Set<String> getNsUris() {
    return this.nsUris;
  }
}
