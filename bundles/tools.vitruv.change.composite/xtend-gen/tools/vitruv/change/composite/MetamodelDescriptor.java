package tools.vitruv.change.composite;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * A descriptor for a metamodel represented by its namespace URIs.
 */
@SuppressWarnings("all")
public final class MetamodelDescriptor {
  @Accessors(AccessorType.PUBLIC_GETTER)
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
        return Objects.equal(this.nsUris, ((MetamodelDescriptor)obj).nsUris);
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
    String _nsURI = rootPackage.getNsURI();
    return MetamodelDescriptor.with(Collections.<String>unmodifiableSet(CollectionLiterals.<String>newHashSet(_nsURI)));
  }

  public static MetamodelDescriptor of(final Set<EPackage> rootPackages) {
    final Function1<EPackage, String> _function = (EPackage it) -> {
      return it.getNsURI();
    };
    return MetamodelDescriptor.with(IterableExtensions.<String>toSet(IterableExtensions.<EPackage, String>map(rootPackages, _function)));
  }

  public static MetamodelDescriptor with(final String nsUri) {
    return MetamodelDescriptor.with(Collections.<String>unmodifiableSet(CollectionLiterals.<String>newHashSet(nsUri)));
  }

  public static MetamodelDescriptor with(final Set<String> nsUris) {
    return new MetamodelDescriptor(nsUris);
  }

  @Pure
  public Set<String> getNsUris() {
    return this.nsUris;
  }
}
