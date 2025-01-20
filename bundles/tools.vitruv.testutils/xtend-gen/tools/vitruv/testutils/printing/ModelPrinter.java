package tools.vitruv.testutils.printing;

import java.util.Collection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Strategy determining how to print model objects.
 * <p>
 * Implementation note: Model printers are expected to be immutable. Furthermore, they should delegate to a sub printer
 * whenever they do not feel responsible for printing some value. This allows other printers to modify the behaviour
 * if they wish. Model printers get the sub printer they should use handed in through the method [#withSubPrinter].
 */
@SuppressWarnings("all")
public interface ModelPrinter {
  /**
   * Creates a copy of this printer that delegates to {@code subPrinter} whenever printing a subpart of an object.
   * This allows other printers to change the printing logic for objects referenced by an object that is printed
   * by this printer.
   * <p>
   * Implementation note: Because model printers are expected to be immutable, implementations should either return
   * {@code this} (if they don’t need a sub printer) or create a new instance with the {@code subPrinter}.
   */
  ModelPrinter withSubPrinter(final ModelPrinter subPrinter);

  /**
   * Prints the provided {@code object}.
   * 
   * @throws RuntimeException if {@code object} could not be printed successfully.
   */
  default PrintResult printObject(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    return PrintResult.NOT_RESPONSIBLE;
  }

  /**
   * Prints the provided {@code object} shortened, i.e. in a manner that conveys only an overview of the object.
   */
  default PrintResult printObjectShortened(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    return PrintResult.NOT_RESPONSIBLE;
  }

  /**
   * Print the provided {@code feature} of the provided {@code object}. Includes an identifier for the feature and
   * the feature value in the output.
   */
  default PrintResult printFeature(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature) {
    return PrintResult.NOT_RESPONSIBLE;
  }

  /**
   * Prints the list value of the provided {@code feature}.
   */
  default PrintResult printFeatureValueList(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Collection<?> valueList) {
    return PrintResult.NOT_RESPONSIBLE;
  }

  /**
   * Prints the set value of the provided {@code feature}.
   */
  default PrintResult printFeatureValueSet(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Collection<?> valueSet) {
    return PrintResult.NOT_RESPONSIBLE;
  }

  /**
   * Prints one of the values of the provided {@code feature}.
   */
  default PrintResult printFeatureValue(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Object value) {
    return PrintResult.NOT_RESPONSIBLE;
  }
}
