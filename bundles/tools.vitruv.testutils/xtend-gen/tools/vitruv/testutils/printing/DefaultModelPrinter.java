package tools.vitruv.testutils.printing;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@FinalFieldsConstructor
@SuppressWarnings("all")
public final class DefaultModelPrinter implements ModelPrinter {
  private final ModelPrinter subPrinter;

  private static final PrintMode ITERABLE_PRINT_MODE = PrintMode.multiLineIfAtLeast(2).withSeparator(",");

  private static final PrintMode FEATURE_PRINT_MODE = PrintMode.multiLineIfAtLeast(2).withSeparator("");

  public DefaultModelPrinter() {
    this.subPrinter = this;
  }

  @Override
  public PrintResult printObject(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    return this.dispatchPrintObject(target, idProvider, object);
  }

  @Override
  public PrintResult printObjectShortened(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    return this.dispatchPrintObjectShortened(target, idProvider, object);
  }

  private PrintResult _dispatchPrintObject(@Extension final PrintTarget target, final PrintIdProvider idProvider, final Resource resource) {
    PrintResult _print = target.print("Resource@");
    final Function2<PrintTarget, URI, PrintResult> _function = (PrintTarget subTarget, URI uri) -> {
      return this.subPrinter.printObject(subTarget, idProvider, uri);
    };
    PrintResult _printValue = target.<URI>printValue(resource.getURI(), _function);
    PrintResult _plus = PrintResultExtension.operator_plus(_print, _printValue);
    final Function2<PrintTarget, EObject, PrintResult> _function_1 = (PrintTarget subTarget, EObject element) -> {
      return this.subPrinter.printObject(subTarget, idProvider, element);
    };
    PrintResult _printList = target.<EObject>printList(resource.getContents(), PrintMode.MULTI_LINE_LIST, _function_1);
    return PrintResultExtension.operator_plus(_plus, _printList);
  }

  private PrintResult _dispatchPrintObject(@Extension final PrintTarget target, final PrintIdProvider idProvider, final EObject object) {
    PrintResult _xblockexpression = null;
    {
      final EClass eClass = object.eClass();
      final EAttribute idAttribute = eClass.getEIDAttribute();
      List<EAttribute> _xifexpression = null;
      if ((idAttribute != null)) {
        _xifexpression = List.<EAttribute>of(idAttribute);
      } else {
        _xifexpression = CollectionLiterals.<EAttribute>emptyList();
      }
      final Function1<EAttribute, Boolean> _function = (EAttribute it) -> {
        return Boolean.valueOf((!Objects.equal(it, idAttribute)));
      };
      Iterable<EAttribute> _filter = IterableExtensions.<EAttribute>filter(eClass.getEAllAttributes(), _function);
      Iterable<EAttribute> _plus = Iterables.<EAttribute>concat(_xifexpression, _filter);
      EList<EReference> _eAllReferences = eClass.getEAllReferences();
      final Iterable<EStructuralFeature> featuresToPrint = Iterables.<EStructuralFeature>concat(_plus, _eAllReferences);
      PrintResult _print = target.print(eClass.getName());
      PrintResult _xifexpression_1 = null;
      if ((idAttribute == null)) {
        PrintResult _print_1 = target.print("#");
        PrintResult _print_2 = target.print(idProvider.getFallbackId(object));
        _xifexpression_1 = PrintResultExtension.operator_plus(_print_1, _print_2);
      } else {
        _xifexpression_1 = PrintResult.PRINTED_NO_OUTPUT;
      }
      PrintResult _plus_1 = PrintResultExtension.operator_plus(_print, _xifexpression_1);
      final Function2<PrintTarget, EStructuralFeature, PrintResult> _function_1 = (PrintTarget subTarget, EStructuralFeature feature) -> {
        return this.subPrinter.printFeature(subTarget, idProvider, object, feature);
      };
      PrintResult _printIterable = target.<EStructuralFeature>printIterable("(", ")", featuresToPrint, DefaultModelPrinter.FEATURE_PRINT_MODE, _function_1);
      _xblockexpression = PrintResultExtension.operator_plus(_plus_1, _printIterable);
    }
    return _xblockexpression;
  }

  private PrintResult _dispatchPrintObject(@Extension final PrintTarget target, final PrintIdProvider idProvider, final URI uri) {
    return target.print(URI.decode(uri.toString()));
  }

  private PrintResult _dispatchPrintObject(@Extension final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    return target.print(object.toString());
  }

  private PrintResult _dispatchPrintObject(@Extension final PrintTarget target, final PrintIdProvider idProvider, final Void void_) {
    return target.print("∅");
  }

  @Override
  public PrintResult printFeature(@Extension final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature) {
    boolean _isDerived = feature.isDerived();
    if (_isDerived) {
      return PrintResult.PRINTED_NO_OUTPUT;
    }
    PrintResult _print = target.print(feature.getName());
    PrintResult _print_1 = target.print("=");
    PrintResult _plus = PrintResultExtension.operator_plus(_print, _print_1);
    PrintResult _xifexpression = null;
    boolean _isMany = feature.isMany();
    if (_isMany) {
      PrintResult _xifexpression_1 = null;
      boolean _isOrdered = feature.isOrdered();
      if (_isOrdered) {
        Object _eGet = object.eGet(feature);
        _xifexpression_1 = this.subPrinter.printFeatureValueList(target, idProvider, object, feature, ((Collection<?>) _eGet));
      } else {
        Object _eGet_1 = object.eGet(feature);
        _xifexpression_1 = this.subPrinter.printFeatureValueSet(target, idProvider, object, feature, ((Collection<?>) _eGet_1));
      }
      _xifexpression = _xifexpression_1;
    } else {
      _xifexpression = this.subPrinter.printFeatureValue(target, idProvider, object, feature, object.eGet(feature));
    }
    return PrintResultExtension.operator_plus(_plus, _xifexpression);
  }

  @Override
  public PrintResult printFeatureValueList(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Collection<?> valueList) {
    final Function2<PrintTarget, Object, PrintResult> _function = (PrintTarget subTarget, Object element) -> {
      return this.subPrinter.printFeatureValue(subTarget, idProvider, object, feature, element);
    };
    return target.<Object>printList(valueList, DefaultModelPrinter.ITERABLE_PRINT_MODE, _function);
  }

  @Override
  public PrintResult printFeatureValueSet(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Collection<?> valueSet) {
    final Function2<PrintTarget, Object, PrintResult> _function = (PrintTarget subTarget, Object element) -> {
      return this.subPrinter.printFeatureValue(subTarget, idProvider, object, feature, element);
    };
    return target.<Object>printSet(valueSet, DefaultModelPrinter.ITERABLE_PRINT_MODE, _function);
  }

  @Override
  public PrintResult printFeatureValue(final PrintTarget target, final PrintIdProvider idProvider, final EObject object, final EStructuralFeature feature, final Object value) {
    PrintResult _switchResult = null;
    boolean _matched = false;
    if (value instanceof EObject) {
      _matched=true;
      PrintResult _switchResult_1 = null;
      boolean _matched_1 = false;
      if (feature instanceof EReference) {
        boolean _isContainment = ((EReference)feature).isContainment();
        if (_isContainment) {
          _matched_1=true;
          _switchResult_1 = this.subPrinter.printObject(target, idProvider, value);
        }
      }
      if (!_matched_1) {
        _switchResult_1 = this.subPrinter.printObjectShortened(target, idProvider, value);
      }
      _switchResult = _switchResult_1;
    }
    if (!_matched) {
      final Function2<PrintTarget, Object, PrintResult> _function = (PrintTarget subTarget, Object theValue) -> {
        return this.subPrinter.printObject(subTarget, idProvider, theValue);
      };
      _switchResult = target.<Object>printValue(value, _function);
    }
    return _switchResult;
  }

  private PrintResult _dispatchPrintObjectShortened(@Extension final PrintTarget target, final PrintIdProvider idProvider, final Resource resource) {
    PrintResult _print = target.print("Resource@");
    final Function2<PrintTarget, URI, PrintResult> _function = (PrintTarget subTarget, URI uri) -> {
      return this.subPrinter.printObjectShortened(subTarget, idProvider, uri);
    };
    PrintResult _printValue = target.<URI>printValue(resource.getURI(), _function);
    PrintResult _plus = PrintResultExtension.operator_plus(_print, _printValue);
    final Function2<PrintTarget, EObject, PrintResult> _function_1 = (PrintTarget subTarget, EObject element) -> {
      return this.subPrinter.printObjectShortened(subTarget, idProvider, element);
    };
    PrintResult _printList = target.<EObject>printList(resource.getContents(), PrintMode.SINGLE_LINE_LIST, _function_1);
    return PrintResultExtension.operator_plus(_plus, _printList);
  }

  private PrintResult _dispatchPrintObjectShortened(@Extension final PrintTarget target, final PrintIdProvider idProvider, final EObject object) {
    PrintResult _xblockexpression = null;
    {
      final EAttribute idAttribute = object.eClass().getEIDAttribute();
      PrintResult _print = target.print(object.eClass().getName());
      PrintResult _xifexpression = null;
      if ((idAttribute != null)) {
        PrintResult _print_1 = target.print("(");
        PrintResult _printFeature = this.printFeature(target, idProvider, object, idAttribute);
        PrintResult _plus = PrintResultExtension.operator_plus(_print_1, _printFeature);
        PrintResult _print_2 = target.print(")");
        _xifexpression = PrintResultExtension.operator_plus(_plus, _print_2);
      } else {
        PrintResult _print_3 = target.print("#");
        PrintResult _print_4 = target.print(idProvider.getFallbackId(object));
        _xifexpression = PrintResultExtension.operator_plus(_print_3, _print_4);
      }
      _xblockexpression = PrintResultExtension.operator_plus(_print, _xifexpression);
    }
    return _xblockexpression;
  }

  private PrintResult _dispatchPrintObjectShortened(@Extension final PrintTarget target, final PrintIdProvider idProvider, final String string) {
    PrintResult _xifexpression = null;
    int _length = string.length();
    boolean _greaterThan = (_length > 10);
    if (_greaterThan) {
      PrintResult _print = target.print(string.substring(0, 9));
      PrintResult _print_1 = target.print("…");
      _xifexpression = PrintResultExtension.operator_plus(_print, _print_1);
    } else {
      _xifexpression = target.print(string);
    }
    return _xifexpression;
  }

  private PrintResult _dispatchPrintObjectShortened(@Extension final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    PrintResult _switchResult = null;
    boolean _matched = false;
    if (object instanceof Number) {
      _matched=true;
    }
    if (!_matched) {
      if (object instanceof Boolean) {
        _matched=true;
      }
    }
    if (!_matched) {
      if (object instanceof URI) {
        _matched=true;
      }
    }
    if (_matched) {
      _switchResult = target.print(object.toString());
    }
    if (!_matched) {
      PrintResult _print = target.print(object.getClass().getSimpleName());
      PrintResult _print_1 = target.print("#");
      PrintResult _plus = PrintResultExtension.operator_plus(_print, _print_1);
      PrintResult _print_2 = target.print(idProvider.getFallbackId(object));
      _switchResult = PrintResultExtension.operator_plus(_plus, _print_2);
    }
    return _switchResult;
  }

  private PrintResult _dispatchPrintObjectShortened(@Extension final PrintTarget target, final PrintIdProvider idProvider, final Void void_) {
    return target.print("∅");
  }

  @Override
  public ModelPrinter withSubPrinter(final ModelPrinter printer) {
    return new DefaultModelPrinter(printer);
  }

  private PrintResult dispatchPrintObject(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    if (object instanceof EObject) {
      return _dispatchPrintObject(target, idProvider, (EObject)object);
    } else if (object instanceof Resource) {
      return _dispatchPrintObject(target, idProvider, (Resource)object);
    } else if (object == null) {
      return _dispatchPrintObject(target, idProvider, (Void)null);
    } else if (object instanceof URI) {
      return _dispatchPrintObject(target, idProvider, (URI)object);
    } else if (object != null) {
      return _dispatchPrintObject(target, idProvider, object);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(target, idProvider, object).toString());
    }
  }

  private PrintResult dispatchPrintObjectShortened(final PrintTarget target, final PrintIdProvider idProvider, final Object string) {
    if (string instanceof String) {
      return _dispatchPrintObjectShortened(target, idProvider, (String)string);
    } else if (string instanceof EObject) {
      return _dispatchPrintObjectShortened(target, idProvider, (EObject)string);
    } else if (string instanceof Resource) {
      return _dispatchPrintObjectShortened(target, idProvider, (Resource)string);
    } else if (string == null) {
      return _dispatchPrintObjectShortened(target, idProvider, (Void)null);
    } else if (string != null) {
      return _dispatchPrintObjectShortened(target, idProvider, string);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(target, idProvider, string).toString());
    }
  }

  public DefaultModelPrinter(final ModelPrinter subPrinter) {
    super();
    this.subPrinter = subPrinter;
  }
}
