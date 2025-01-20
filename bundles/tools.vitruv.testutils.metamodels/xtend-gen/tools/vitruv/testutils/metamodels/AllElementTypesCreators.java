package tools.vitruv.testutils.metamodels;

import allElementTypes.AllElementTypesFactory;
import allElementTypes.Containable;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.Root;
import allElementTypes.ValueBased;
import com.google.common.base.Preconditions;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import tools.vitruv.testutils.activeannotations.ModelCreators;

@ModelCreators(factory = AllElementTypesFactory.class)
@SuppressWarnings("all")
public final class AllElementTypesCreators {
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

  public static final AllElementTypesCreators aet = new AllElementTypesCreators();

  private AllElementTypesCreators() {
  }

  public Root Root() {
    return AllElementTypesFactory.eINSTANCE.createRoot();
  }

  public NonRoot NonRoot() {
    return AllElementTypesFactory.eINSTANCE.createNonRoot();
  }

  public NonRootObjectContainerHelper NonRootObjectContainerHelper() {
    return AllElementTypesFactory.eINSTANCE.createNonRootObjectContainerHelper();
  }

  public ValueBased ValueBased() {
    return AllElementTypesFactory.eINSTANCE.createValueBased();
  }

  public Containable Containable() {
    return AllElementTypesFactory.eINSTANCE.createContainable();
  }

  private static EClassifier _getClassifier(final String classifierName) {
    return Preconditions.checkNotNull(
    	AllElementTypesFactory.eINSTANCE.getEPackage().getEClassifier(classifierName),
    	"There is no classifier called '%s' in '%s'!", classifierName, AllElementTypesFactory.eINSTANCE.getEPackage().getName()
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
    return AllElementTypesFactory.eINSTANCE.create((EClass) requestedClassifier);
  }

  public EObject create(final String className) {
    return _createInstance(className);
  }

  public <M extends EObject> M create(final Class<? extends M> clazz) {
    return clazz.cast(_createInstance(clazz.getSimpleName()));
  }
}
