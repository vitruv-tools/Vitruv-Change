package tools.vitruv.testutils.printing;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class CombinedModelPrinter implements ModelPrinter {
  private final List<ModelPrinter> printers;

  public CombinedModelPrinter(final ModelPrinter printer, final ModelPrinter fallback) {
    this(List.<ModelPrinter>of(printer), fallback);
  }

  public CombinedModelPrinter(final List<? extends ModelPrinter> printers, final ModelPrinter fallback) {
    List<ModelPrinter> _of = List.<ModelPrinter>of(fallback);
    final Function1<ModelPrinter, List<ModelPrinter>> _function = (ModelPrinter it) -> {
      List<ModelPrinter> _xifexpression = null;
      if ((it instanceof CombinedModelPrinter)) {
        _xifexpression = ((CombinedModelPrinter)it).printers;
      } else {
        _xifexpression = List.<ModelPrinter>of(it);
      }
      return _xifexpression;
    };
    final Function1<ModelPrinter, ModelPrinter> _function_1 = (ModelPrinter it) -> {
      return it.withSubPrinter(this);
    };
    this.printers = IterableUtil.<ModelPrinter, ModelPrinter>mapFixed(IterableExtensions.<ModelPrinter, ModelPrinter>flatMap(Iterables.<ModelPrinter>concat(printers, _of), _function), _function_1);
  }

  private CombinedModelPrinter(final List<ModelPrinter> printers) {
    this.printers = printers;
  }

  @Override
  public PrintResult printObject(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    final Function1<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printObject(target, idProvider, object);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printObjectShortened(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    final Function1<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printObjectShortened(target, idProvider, object);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeature(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature) {
    final Function1<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeature(target, idProvider, object, feature);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeatureValueList(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Collection<?> valueList) {
    final Function1<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeatureValueList(target, idProvider, object, feature, valueList);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeatureValueSet(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Collection<?> valueSet) {
    final Function1<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeatureValueSet(target, idProvider, object, feature, valueSet);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeatureValue(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Object value) {
    final Function1<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeatureValue(target, idProvider, object, feature, value);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public ModelPrinter withSubPrinter(final ModelPrinter subPrinter) {
    final Function1<ModelPrinter, ModelPrinter> _function = (ModelPrinter it) -> {
      return it.withSubPrinter(subPrinter);
    };
    List<ModelPrinter> _mapFixed = IterableUtil.<ModelPrinter, ModelPrinter>mapFixed(this.printers, _function);
    return new CombinedModelPrinter(_mapFixed);
  }

  private PrintResult useFirstResponsible(final Function1<? super ModelPrinter, ? extends PrintResult> action) {
    for (final ModelPrinter printer : this.printers) {
      {
        final PrintResult result = action.apply(printer);
        boolean _notEquals = (!Objects.equal(result, PrintResult.NOT_RESPONSIBLE));
        if (_notEquals) {
          return result;
        }
      }
    }
    throw new IllegalStateException(
      "Could not find a responsible printer! Please make sure that you configure a suitable fallback!");
  }
}
