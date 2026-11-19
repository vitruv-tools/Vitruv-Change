package tools.vitruv.change.composite;

import com.google.common.base.Preconditions;
import java.util.Collections;
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
    final Consumer<String> _function = (String it) -> Preconditions.checkArgument((it != null),
            "metamodel descriptor to be instantiated for namespace URIs"
              + " %s must not contain a null URI", nsUris);
    nsUris.forEach(_function);
    this.nsUris = new HashSet<String>(nsUris);
  }

  /**
   * Returns whether the metamodel of the given descriptor is contained in this
   * descriptor's metamodel, i.e., whether its namespace URIs are a subset of the
   * ones of this descriptor.
   *
   * @param descriptorForPotentiallyContainedMetamodel the descriptor to check for
   *     containment of its metamodel in this one, must not be <code>null</code>
   * @return whether the metamodel of the given descriptor is contained in this one
   */
  public boolean contains(final MetamodelDescriptor descriptorForPotentiallyContainedMetamodel) {
    boolean xblockexpression = false;

    Preconditions.checkArgument((descriptorForPotentiallyContainedMetamodel != null),
            "metamodel descriptor to check for containment in %s must not be null", this);
    xblockexpression = this.nsUris.containsAll(descriptorForPotentiallyContainedMetamodel.nsUris);

    return xblockexpression;
  }

  /**
   * Returns whether the metamodel of this descriptor overlaps with the metamodel of the
   * given descriptor, i.e., whether they share at least one namespace URI.
   *
   * @param other the descriptor to check for overlap with, must not be <code>null</code>
   * @return whether the metamodel of this descriptor overlaps with the metamodel of the given
   *     descriptor
   */
  public boolean overlaps(final MetamodelDescriptor other) {
    return !Collections.disjoint(this.nsUris, other.nsUris);
  }

  @Override
  public boolean equals(final Object obj) {
    boolean xblockexpression = false;

    if ((obj instanceof MetamodelDescriptor)) {
      return Objects.equals(this.nsUris, ((MetamodelDescriptor) obj).nsUris);
    }

    return xblockexpression;
  }

  @Override
  public int hashCode() {
    return this.nsUris.hashCode();
  }

  @Override
  public String toString() {
    return this.nsUris.toString();
  }

  /**
   * Creates a descriptor for the metamodel with the given root package.
   *
   * @param rootPackage the root package of the metamodel
   * @return a descriptor for the root package's namespace URI
   */
  public static MetamodelDescriptor of(final EPackage rootPackage) {
    return MetamodelDescriptor.with(Set.of(rootPackage.getNsURI()));
  }

  /**
   * Creates a descriptor for the metamodel made up of the given root packages.
   *
   * @param rootPackages the root packages of the metamodel
   * @return a descriptor for the namespace URIs of all given root packages
   */
  public static MetamodelDescriptor of(final Set<EPackage> rootPackages) {
    return MetamodelDescriptor.with(rootPackages.stream()
            .map(EPackage::getNsURI).collect(Collectors.toSet()));
  }

  /**
   * Creates a descriptor for the metamodel with the given namespace URI.
   *
   * @param nsUri the namespace URI of the metamodel
   * @return a descriptor for the given namespace URI
   */
  public static MetamodelDescriptor with(final String nsUri) {
    return MetamodelDescriptor.with(Set.of(nsUri));
  }

  /**
   * Creates a descriptor for the metamodel with the given namespace URIs.
   *
   * @param nsUris the namespace URIs of the metamodel
   * @return a descriptor for the given namespace URIs
   * @throws IllegalArgumentException if the set contains a {@code null} URI
   */
  public static MetamodelDescriptor with(final Set<String> nsUris) {
    return new MetamodelDescriptor(nsUris);
  }

  public Set<String> getNsUris() {
    return this.nsUris;
  }
}
