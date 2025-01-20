package tools.vitruv.testutils.metamodels;

import com.google.common.base.Preconditions;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import pcm_mockup.Component;
import pcm_mockup.PInterface;
import pcm_mockup.PMethod;
import pcm_mockup.Pcm_mockupFactory;
import pcm_mockup.Repository;
import tools.vitruv.testutils.activeannotations.ModelCreators;

@ModelCreators(factory = Pcm_mockupFactory.class, stripPrefix = "P")
@SuppressWarnings("all")
public final class PcmMockupCreators {
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

  public static final PcmMockupCreators pcm = new PcmMockupCreators();

  public Repository Repository() {
    return Pcm_mockupFactory.eINSTANCE.createRepository();
  }

  public PInterface Interface() {
    return Pcm_mockupFactory.eINSTANCE.createPInterface();
  }

  public Component Component() {
    return Pcm_mockupFactory.eINSTANCE.createComponent();
  }

  public PMethod Method() {
    return Pcm_mockupFactory.eINSTANCE.createPMethod();
  }

  private static EClassifier _getClassifier(final String classifierName) {
    return Preconditions.checkNotNull(
    	Pcm_mockupFactory.eINSTANCE.getEPackage().getEClassifier(classifierName),
    	"There is no classifier called '%s' in '%s'!", classifierName, Pcm_mockupFactory.eINSTANCE.getEPackage().getName()
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
    return Pcm_mockupFactory.eINSTANCE.create((EClass) requestedClassifier);
  }

  public EObject create(final String className) {
    return _createInstance(className);
  }

  public <M extends EObject> M create(final Class<? extends M> clazz) {
    return clazz.cast(_createInstance(clazz.getSimpleName()));
  }
}
