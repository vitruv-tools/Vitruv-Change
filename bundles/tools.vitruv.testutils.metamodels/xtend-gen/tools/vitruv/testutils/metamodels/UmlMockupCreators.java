package tools.vitruv.testutils.metamodels;

import com.google.common.base.Preconditions;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import tools.vitruv.testutils.activeannotations.ModelCreators;
import uml_mockup.UAttribute;
import uml_mockup.UClass;
import uml_mockup.UInterface;
import uml_mockup.UMethod;
import uml_mockup.UPackage;
import uml_mockup.Uml_mockupFactory;

@ModelCreators(factory = Uml_mockupFactory.class, stripPrefix = "U")
@SuppressWarnings("all")
public final class UmlMockupCreators {
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

  public static final UmlMockupCreators uml = new UmlMockupCreators();

  public UPackage Package() {
    return Uml_mockupFactory.eINSTANCE.createUPackage();
  }

  public UInterface Interface() {
    return Uml_mockupFactory.eINSTANCE.createUInterface();
  }

  public UClass Class() {
    return Uml_mockupFactory.eINSTANCE.createUClass();
  }

  public UMethod Method() {
    return Uml_mockupFactory.eINSTANCE.createUMethod();
  }

  public UAttribute Attribute() {
    return Uml_mockupFactory.eINSTANCE.createUAttribute();
  }

  private static EClassifier _getClassifier(final String classifierName) {
    return Preconditions.checkNotNull(
    	Uml_mockupFactory.eINSTANCE.getEPackage().getEClassifier(classifierName),
    	"There is no classifier called '%s' in '%s'!", classifierName, Uml_mockupFactory.eINSTANCE.getEPackage().getName()
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
    return Uml_mockupFactory.eINSTANCE.create((EClass) requestedClassifier);
  }

  public EObject create(final String className) {
    return _createInstance(className);
  }

  public <M extends EObject> M create(final Class<? extends M> clazz) {
    return clazz.cast(_createInstance(clazz.getSimpleName()));
  }
}
