package tools.vitruv.change.atomic;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import java.util.Arrays;
import tools.vitruv.change.atomic.feature.reference.AdditiveReferenceEChange;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Static utility class for the EChange package and subpackages.
 */
@Utility
@SuppressWarnings("all")
public final class EChangeUtil {
  protected static boolean _isContainmentRemoval(final EChange<?> change) {
    return false;
  }

  protected static boolean _isContainmentRemoval(final ReplaceSingleValuedEReference<?> change) {
    return (change.getAffectedFeature().isContainment() && ((change.getOldValue() != null) && (change.getOldValue() != change.getNewValue())));
  }

  protected static boolean _isContainmentRemoval(final RemoveEReference<?> change) {
    return change.getAffectedFeature().isContainment();
  }

  protected static boolean _isContainmentRemoval(final RemoveRootEObject<?> change) {
    return true;
  }

  protected static boolean _isContainmentInsertion(final EChange<?> change) {
    return false;
  }

  protected static boolean _isContainmentInsertion(final AdditiveReferenceEChange<?> change) {
    return (change.getAffectedFeature().isContainment() && (change.getNewValue() != null));
  }

  protected static boolean _isContainmentInsertion(final RemoveRootEObject<?> change) {
    return true;
  }

  protected static boolean _isContainmentInsertion(final InsertRootEObject<?> change) {
    return true;
  }

  public static boolean isContainmentRemoval(final EChange<?> change) {
    if (change instanceof RemoveEReference) {
      return _isContainmentRemoval((RemoveEReference<?>)change);
    } else if (change instanceof ReplaceSingleValuedEReference) {
      return _isContainmentRemoval((ReplaceSingleValuedEReference<?>)change);
    } else if (change instanceof RemoveRootEObject) {
      return _isContainmentRemoval((RemoveRootEObject<?>)change);
    } else if (change != null) {
      return _isContainmentRemoval(change);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(change).toString());
    }
  }

  public static boolean isContainmentInsertion(final EChange<?> change) {
    if (change instanceof AdditiveReferenceEChange) {
      return _isContainmentInsertion((AdditiveReferenceEChange<?>)change);
    } else if (change instanceof InsertRootEObject) {
      return _isContainmentInsertion((InsertRootEObject<?>)change);
    } else if (change instanceof RemoveRootEObject) {
      return _isContainmentInsertion((RemoveRootEObject<?>)change);
    } else if (change != null) {
      return _isContainmentInsertion(change);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(change).toString());
    }
  }

  private EChangeUtil() {
    
  }
}
