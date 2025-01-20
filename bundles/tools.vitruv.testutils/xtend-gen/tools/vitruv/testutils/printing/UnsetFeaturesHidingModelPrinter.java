package tools.vitruv.testutils.printing;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

@SuppressWarnings("all")
public class UnsetFeaturesHidingModelPrinter implements ModelPrinter {
  @Override
  public PrintResult printFeature(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature) {
    PrintResult _xifexpression = null;
    boolean _eIsSet = object.eIsSet(feature);
    boolean _not = (!_eIsSet);
    if (_not) {
      _xifexpression = PrintResult.PRINTED_NO_OUTPUT;
    } else {
      _xifexpression = PrintResult.NOT_RESPONSIBLE;
    }
    return _xifexpression;
  }

  @Override
  public ModelPrinter withSubPrinter(final ModelPrinter subPrinter) {
    return this;
  }
}
