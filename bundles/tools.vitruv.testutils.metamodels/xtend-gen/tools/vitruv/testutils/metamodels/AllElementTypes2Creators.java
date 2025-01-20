package tools.vitruv.testutils.metamodels;

import allElementTypes2.AllElementTypes2Factory;
import allElementTypes2.NonRoot2;
import allElementTypes2.NonRootObjectContainerHelper2;
import allElementTypes2.Root2;
import com.google.common.base.Preconditions;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import tools.vitruv.testutils.activeannotations.ModelCreators;

@ModelCreators(factory = AllElementTypes2Factory.class)
@SuppressWarnings("all")
public final class AllElementTypes2Creators {
  public static class NewEObject implements ArgumentConverter {
    public EObject convert(final Object source, final ParameterContext context) throws ArgumentConversionException {
      try {
      	return _createInstance((String) source);
      } catch (IllegalArgumentException | NullPointerException e) {
      	throw new ArgumentConversionException(e.getMessage(), e);
      }
    }
  }

  public static class Classifier implements ArgumentConverter {
    public EObject convert(final Object source, final ParameterContext context) throws ArgumentConversionException {
      try {
      	return _getClassifier((String) source);
      } catch (NullPointerException e) {
      	throw new ArgumentConversionException(e.getMessage(), e);
      }
    }
  }

  public static final AllElementTypes2Creators aet2 = new AllElementTypes2Creators();

  public Root2 Root2() {
    return AllElementTypes2Factory.eINSTANCE.createRoot2();
  }

  public NonRoot2 NonRoot2() {
    return AllElementTypes2Factory.eINSTANCE.createNonRoot2();
  }

  public NonRootObjectContainerHelper2 NonRootObjectContainerHelper2() {
    return AllElementTypes2Factory.eINSTANCE.createNonRootObjectContainerHelper2();
  }

  private static EClassifier _getClassifier(final String classifierName) {
    return Preconditions.checkNotNull(
    	AllElementTypes2Factory.eINSTANCE.getEPackage().getEClassifier(classifierName),
    	"There is no classifier called '%s' in '%s'!", classifierName, AllElementTypes2Factory.eINSTANCE.getEPackage().getName()
    );
  }

  public EClassifier classifier(final String classifierName) {
    return _getClassifier(classifierName);
  }

  private static EObject _createInstance(final String className) {
    EClassifier requestedClassifier = _getClassifier(className);
    Preconditions.checkArgument(
    	requestedClassifier instanceof EClass,
    	"%s is not an EClass and can thus not be instantiated!", className
    );
    return AllElementTypes2Factory.eINSTANCE.create((EClass) requestedClassifier);
  }

  public EObject create(final String className) {
    return _createInstance(className);
  }

  public <M extends EObject> M create(final Class<? extends M> clazz) {
    return clazz.cast(_createInstance(clazz.getSimpleName()));
  }
}
