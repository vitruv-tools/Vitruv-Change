package tools.vitruv.change.testutils.printing;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class CombinedModelPrinter implements ModelPrinter {
  private final List<ModelPrinter> printers;

  public CombinedModelPrinter(final ModelPrinter printer, final ModelPrinter fallback) {
    this(List.<ModelPrinter>of(printer), fallback);
  }

  public CombinedModelPrinter(final List<? extends ModelPrinter> printers, final ModelPrinter fallback) {
    List<ModelPrinter> _of = List.<ModelPrinter>of(fallback);
    final Function<ModelPrinter, List<ModelPrinter>> _function = (ModelPrinter it) -> {
      List<ModelPrinter> _xifexpression = null;
      if ((it instanceof CombinedModelPrinter)) {
        _xifexpression = ((CombinedModelPrinter) it).printers;
      } else {
        _xifexpression = List.<ModelPrinter>of(it);
      }
      return _xifexpression;
    };
    final Function<ModelPrinter, ModelPrinter> _function_1 = (ModelPrinter it) -> {
      return it.withSubPrinter(this);
    };
    this.printers = StreamSupport.stream(Iterables.<ModelPrinter>concat(printers, _of).spliterator(), false)
        .flatMap(it -> _function.apply(it).stream())
        .map(_function_1)
        .collect(Collectors.toList());
  }

  private CombinedModelPrinter(final List<ModelPrinter> printers) {
    this.printers = printers;
  }

  @Override
  public PrintResult printObject(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    final Function<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printObject(target, idProvider, object);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printObjectShortened(final PrintTarget target, final PrintIdProvider idProvider,
      final Object object) {
    final Function<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printObjectShortened(target, idProvider, object);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeature(final PrintTarget target, final PrintIdProvider idProvider, final EObject object,
      final EStructuralFeature feature) {
    final Function<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeature(target, idProvider, object, feature);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeatureValueList(final PrintTarget target, final PrintIdProvider idProvider,
      final EObject object, final EStructuralFeature feature, final Collection<?> valueList) {
    final Function<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeatureValueList(target, idProvider, object, feature, valueList);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeatureValueSet(final PrintTarget target, final PrintIdProvider idProvider,
      final EObject object, final EStructuralFeature feature, final Collection<?> valueSet) {
    final Function<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeatureValueSet(target, idProvider, object, feature, valueSet);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public PrintResult printFeatureValue(final PrintTarget target, final PrintIdProvider idProvider, final EObject object,
      final EStructuralFeature feature, final Object value) {
    final Function<ModelPrinter, PrintResult> _function = (ModelPrinter it) -> {
      return it.printFeatureValue(target, idProvider, object, feature, value);
    };
    return this.useFirstResponsible(_function);
  }

  @Override
  public ModelPrinter withSubPrinter(final ModelPrinter subPrinter) {
    final Function<ModelPrinter, ModelPrinter> _function = (ModelPrinter it) -> {
      return it.withSubPrinter(subPrinter);
    };
    List<ModelPrinter> _mapFixed = this.printers.stream().map(_function).collect(Collectors.toList());
    return new CombinedModelPrinter(_mapFixed);
  }

  private PrintResult useFirstResponsible(final Function<ModelPrinter, PrintResult> action) {
    for (final ModelPrinter printer : this.printers) {
      {
        final PrintResult result = action.apply(printer);
        boolean _notEquals = (!Objects.equals(result, PrintResult.NOT_RESPONSIBLE));
        if (_notEquals) {
          return result;
        }
      }
    }
    throw new IllegalStateException(
        "Could not find a responsible printer! Please make sure that you configure a suitable fallback!");
  }
}
