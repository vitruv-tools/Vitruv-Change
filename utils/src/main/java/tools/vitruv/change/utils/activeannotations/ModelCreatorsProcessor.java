package tools.vitruv.change.utils.activeannotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableTypeParameterDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend.lib.macro.services.Problem;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import com.google.common.base.Preconditions;

/**
 * Processes the {@link ModelCreators} annotation and generates shortcut methods for the create
 * methods of the provided factory
 */
public class ModelCreatorsProcessor extends AbstractClassProcessor {

  private static final String NEW_EOBJECT_CONVERTER = "NewEObject";
  private static final String CLASSIFIER_CONVERTER = "Classifier";
  private static final String METHOD_NAME_CREATE = "create";

  @Override
  public void doRegisterGlobals(ClassDeclaration annotatedClass, RegisterGlobalsContext context) {
    context.registerClass(annotatedClass.getQualifiedName() + "." + NEW_EOBJECT_CONVERTER);
    context.registerClass(annotatedClass.getQualifiedName() + "." + CLASSIFIER_CONVERTER);
  }

  @Override
  public void doTransform(MutableClassDeclaration annotatedClass, TransformationContext context) {
    checkPreconditions(annotatedClass, context);

    var annotation = annotatedClass.findAnnotation(context.findTypeGlobally(ModelCreators.class));
    if (annotation == null) {
      context.addError(
          annotatedClass,
          "Cannot find ModelCreators annotation on " + annotatedClass.getSimpleName());
    }
    if (context.getProblems(annotatedClass).stream()
        .anyMatch(p -> p.getSeverity() == Problem.Severity.ERROR)) {
      return;
    }

    TypeReference factory = annotation.getClassValue("factory");
    boolean createStatic = annotation.getBooleanValue("staticCreators");
    String stripPrefix = annotation.getStringValue("stripPrefix");
    String prefix = annotation.getStringValue("prefix");
    String[] rawReplacements = annotation.getStringArrayValue("replace");
    Preconditions.checkArgument(
        rawReplacements.length % 2 == 0, "The replace array must have an even number of elements!");
    Map<String, String> replacements = new HashMap<>();
    for (int i = 0; i < rawReplacements.length; i += 2) {
      replacements.put(rawReplacements[i], rawReplacements[i + 1]);
    }

    StreamSupport.stream(factory.getDeclaredResolvedMethods().spliterator(), false)
        .map(rm -> rm.getDeclaration())
        .filter(
            m ->
                    m.getSimpleName().startsWith(METHOD_NAME_CREATE)
                            && !m.getParameters().iterator().hasNext())
        .forEach(
            createMethod -> {
              String baseName =
                  removePrefix(
                          removePrefix(
                                  createMethod.getSimpleName(),
                                  METHOD_NAME_CREATE
                          ),
                          stripPrefix);
              String targetName =
                  prefix + toFirstUpper(replacements.getOrDefault(baseName, baseName));
              TypeReference returnType = createMethod.getReturnType();
              annotatedClass.addMethod(
                  targetName,
                  m -> {
                    m.setStatic(createStatic);
                    m.setReturnType(returnType);
                    m.setBody(
                        ctx ->
                            "return "
                                + ctx.toJavaCode(factory)
                                + ".eINSTANCE."
                                + createMethod.getSimpleName()
                                + "();");
                    context.setPrimarySourceElement(m, annotatedClass);
                  });
            });

    annotatedClass.addMethod(
        "_getClassifier",
        m -> {
          m.setReturnType(context.newTypeReference(EClassifier.class));
          m.setStatic(true);
          m.setVisibility(Visibility.PRIVATE);
          m.addParameter("classifierName", context.newTypeReference(String.class));
          m.setBody(
              ctx -> {
                String preconditions =
                    ctx.toJavaCode(context.newTypeReference(Preconditions.class));
                String factoryCode = ctx.toJavaCode(factory);
                return "return "
                    + preconditions
                    + ".checkNotNull(\n"
                    + "\t"
                    + factoryCode
                    + ".eINSTANCE.getEPackage().getEClassifier(classifierName),\n"
                    + "\t\"There is no classifier called '%s' in '%s'!\", classifierName, "
                    + factoryCode
                    + ".eINSTANCE.getEPackage().getName()\n"
                    + ");";
              });
        });

    annotatedClass.addMethod(
        "classifier",
        m -> {
          m.setReturnType(context.newTypeReference(EClassifier.class));
          m.addParameter("classifierName", context.newTypeReference(String.class));
          m.setBody(ctx -> "return _getClassifier(classifierName);");
        });

    annotatedClass.addMethod(
        "_createInstance",
        m -> {
          m.setReturnType(context.newTypeReference(EObject.class));
          m.setStatic(true);
          m.setVisibility(Visibility.PRIVATE);
          m.addParameter("className", context.newTypeReference(String.class));
          m.setBody(
              ctx -> {
                String eClassifier = ctx.toJavaCode(context.newTypeReference(EClassifier.class));
                String preconditions =
                    ctx.toJavaCode(context.newTypeReference(Preconditions.class));
                String eClass = ctx.toJavaCode(context.newTypeReference(EClass.class));
                String factoryCode = ctx.toJavaCode(factory);
                return eClassifier
                    + " requestedClassifier = _getClassifier(className);\n"
                    + preconditions
                    + ".checkArgument(\n"
                    + "\trequestedClassifier instanceof "
                    + eClass
                    + ",\n"
                    + "\t\"%s is not an EClass and can thus not be instantiated!\", className\n"
                    + ");\n"
                    + "return "
                    + factoryCode
                    + ".eINSTANCE.create(("
                    + eClass
                    + ") requestedClassifier);";
              });
        });

    annotatedClass.addMethod(
            METHOD_NAME_CREATE,
        m -> {
          m.setReturnType(context.newTypeReference(EObject.class));
          m.addParameter("className", context.newTypeReference(String.class));
          m.setBody(ctx -> "return _createInstance(className);");
        });

    annotatedClass.addMethod(
            METHOD_NAME_CREATE,
        m -> {
          MutableTypeParameterDeclaration typeParam =
              m.addTypeParameter("M", context.newTypeReference(EObject.class));
          m.setReturnType(context.newSelfTypeReference(typeParam));
          m.addParameter(
              "clazz",
              context.newTypeReference(
                  Class.class,
                  context.newWildcardTypeReference(context.newSelfTypeReference(typeParam))));
          m.setBody(ctx -> "return clazz.cast(_createInstance(clazz.getSimpleName()));");
        });

    if (!annotatedClass.getDeclaredConstructors().iterator().hasNext()) {
      annotatedClass.addConstructor(c -> c.setVisibility(Visibility.PRIVATE));
    }

    var newEObjectClass =
        context.findClass(annotatedClass.getQualifiedName() + "." + NEW_EOBJECT_CONVERTER);
    newEObjectClass.setImplementedInterfaces(
        List.of(context.newTypeReference(ArgumentConverter.class)));
    newEObjectClass.addMethod(
        "convert",
        m -> {
          m.setReturnType(context.newTypeReference(EObject.class));
          m.addParameter("source", context.newTypeReference(Object.class));
          m.addParameter("parameterContext", context.newTypeReference(ParameterContext.class));
          m.setExceptions(context.newTypeReference(ArgumentConversionException.class));
          m.setBody(
              ctx -> {
                String exType =
                    ctx.toJavaCode(context.newTypeReference(ArgumentConversionException.class));
                return "try {\n"
                    + "\treturn _createInstance((String) source);\n"
                    + "} catch (IllegalArgumentException | NullPointerException e) {\n"
                    + "\tthrow new "
                    + exType
                    + "(e.getMessage(), e);\n"
                    + "}";
              });
        });

    var classifierClass =
        context.findClass(annotatedClass.getQualifiedName() + "." + CLASSIFIER_CONVERTER);
    classifierClass.setImplementedInterfaces(
        List.of(context.newTypeReference(ArgumentConverter.class)));
    classifierClass.addMethod(
        "convert",
        m -> {
          m.setReturnType(context.newTypeReference(EObject.class));
          m.addParameter("source", context.newTypeReference(Object.class));
          m.addParameter("parameterContext", context.newTypeReference(ParameterContext.class));
          m.setExceptions(context.newTypeReference(ArgumentConversionException.class));
          m.setBody(
              ctx -> {
                String exType =
                    ctx.toJavaCode(context.newTypeReference(ArgumentConversionException.class));
                return "try {\n"
                    + "\treturn _getClassifier((String) source);\n"
                    + "} catch (NullPointerException e) {\n"
                    + "\tthrow new "
                    + exType
                    + "(e.getMessage(), e);\n"
                    + "}";
              });
        });
  }

  private void checkPreconditions(ClassDeclaration annotatedClass, TransformationContext context) {
    if (context.newTypeReference(Preconditions.class) == null) {
      context.addError(annotatedClass, "Please add Guava to the classpath!");
    }
    if (context.newTypeReference(ParameterContext.class) == null) {
      context.addError(annotatedClass, "Please add the JUnit Jupiter API to the classpath!");
    }
    if (context.newTypeReference(ArgumentConverter.class) == null) {
      context.addError(annotatedClass, "Please add JUnit 5 Params to the classpath!");
    }
  }

  private static String removePrefix(String target, String prefix) {
    return target.startsWith(prefix) ? target.substring(prefix.length()) : target;
  }

  private static String toFirstUpper(String s) {
    if (s == null || s.isEmpty()) {
      return s;
    }
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }
}
