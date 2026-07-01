package tools.vitruv.change.utils.activeannotations;

import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy.CompilationContext;
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.ResolvedMethod;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;

/**
 * Processes the WithGeneratedRandomIds annotation and generates overrides for all create methods of
 * the extended factory that return an instance of the configured identifierMetaclass. The generated
 * overrides will call the super method and then set a generated UUID as the ID of the created
 * EObject.
 */
public class WithGeneratedIdsProcessor extends AbstractClassProcessor {

  @Override
  public void doTransform(MutableClassDeclaration annotatedClass, TransformationContext context) {
    TypeReference extendedFactory = annotatedClass.getExtendedClass();
    if (!isSupportedFactory(extendedFactory, context)) {
      addInvalidFactoryError(annotatedClass, context);
      return;
    }

    var annotation =
        annotatedClass.findAnnotation(context.findTypeGlobally(WithGeneratedRandomIds.class));
    if (annotation == null) {
      context.addError(
          annotatedClass,
          "Cannot find "
              + WithGeneratedRandomIds.class.getSimpleName()
              + " annotation on "
              + annotatedClass.getSimpleName());
      return;
    }
    TypeReference identifierMetaclass = annotation.getClassValue("identifierMetaclass");

    var createMethods = getMatchingCreateMethods(extendedFactory, identifierMetaclass);

    if (createMethods.isEmpty()) {
      context.addWarning(
          annotatedClass,
          "Could not find a single create method to override. Please check your arguments!");
    }

    for (var createMethod : createMethods) {
      addGeneratedIdOverride(annotatedClass, context, createMethod);
    }
  }

  private static boolean isSupportedFactory(
      TypeReference extendedFactory, TransformationContext context) {
    return extendedFactory != null
        && context.newTypeReference(EFactoryImpl.class).isAssignableFrom(extendedFactory);
  }

  private static void addInvalidFactoryError(
      MutableClassDeclaration annotatedClass, TransformationContext context) {
    context.addError(
        annotatedClass,
        "A class annotated with "
            + WithGeneratedRandomIds.class.getSimpleName()
            + " must extend an extension of "
            + EFactoryImpl.class.getSimpleName()
            + ".");
  }

  private static List<? extends ResolvedMethod> getMatchingCreateMethods(
      TypeReference extendedFactory, TypeReference identifierMetaclass) {
    return StreamSupport.stream(extendedFactory.getDeclaredResolvedMethods().spliterator(), false)
        .filter(method -> isMatchingCreateMethod(method, identifierMetaclass))
        .toList();
  }

  private static boolean isMatchingCreateMethod(
      ResolvedMethod method, TypeReference identifierMetaclass) {
    MethodDeclaration declaration = method.getDeclaration();
    return declaration.getSimpleName().startsWith("create")
        && hasNoParameters(declaration)
        && identifierMetaclass.isAssignableFrom(declaration.getReturnType());
  }

  private static boolean hasNoParameters(MethodDeclaration method) {
    return !method.getParameters().iterator().hasNext();
  }

  private static void addGeneratedIdOverride(
      MutableClassDeclaration annotatedClass,
      TransformationContext context,
      ResolvedMethod createMethod) {
    MutableMethodDeclaration existingMethod =
        getExistingCreateOverride(
            annotatedClass.getDeclaredMethods(), createMethod.getDeclaration().getSimpleName());
    TypeReference resolvedReturn = createMethod.getResolvedReturnType();

    if (existingMethod != null) {
      wrapExistingCreateMethod(annotatedClass, context, existingMethod, resolvedReturn);
      return;
    }
    addSuperCreateOverride(annotatedClass, context, createMethod, resolvedReturn);
  }

  private static MutableMethodDeclaration getExistingCreateOverride(
      Iterable<? extends MutableMethodDeclaration> methods, String methodName) {
    for (var method : methods) {
      if (isExistingCreateOverride(method, methodName)) {
        return method;
      }
    }
    return null;
  }

  private static boolean isExistingCreateOverride(
      MutableMethodDeclaration method, String methodName) {
    return method.getSimpleName().equals(methodName)
        && hasNoParameters(method)
        && !method.isStatic();
  }

  private static void wrapExistingCreateMethod(
      MutableClassDeclaration annotatedClass,
      TransformationContext context,
      MutableMethodDeclaration existingMethod,
      TypeReference resolvedReturn) {
    String privateName = getAvailablePrivateMethodName(annotatedClass, existingMethod);
    TypeReference originalReturnType = existingMethod.getReturnType();
    var originalBody = existingMethod.getBody();
    MutableMethodDeclaration sourceRef = existingMethod;

    annotatedClass.addMethod(
        privateName,
        method -> {
          method.setVisibility(Visibility.PRIVATE);
          method.setReturnType(originalReturnType);
          method.setBody(originalBody);
          context.setPrimarySourceElement(method, sourceRef);
        });

    existingMethod.setBody(ctx -> createGeneratedIdBody(ctx, context, resolvedReturn, privateName));
  }

  private static String getAvailablePrivateMethodName(
      MutableClassDeclaration annotatedClass, MutableMethodDeclaration existingMethod) {
    String newName = existingMethod.getSimpleName();
    do {
      newName = "_" + newName;
    } while (existsWithName(annotatedClass.getDeclaredMethods(), newName));
    return newName;
  }

  private static void addSuperCreateOverride(
      MutableClassDeclaration annotatedClass,
      TransformationContext context,
      ResolvedMethod createMethod,
      TypeReference resolvedReturn) {
    String methodName = createMethod.getDeclaration().getSimpleName();
    String superCall = "super." + methodName;
    annotatedClass.addMethod(
        methodName,
        method -> {
          method.setReturnType(resolvedReturn);
          method.setBody(ctx -> createGeneratedIdBody(ctx, context, resolvedReturn, superCall));
          context.setPrimarySourceElement(method, annotatedClass);
        });
  }

  private static String createGeneratedIdBody(
      CompilationContext ctx,
      TransformationContext context,
      TypeReference resolvedReturn,
      String createCall) {
    String resolvedCode = ctx.toJavaCode(resolvedReturn);
    String ecoreUtil = ctx.toJavaCode(context.newTypeReference(EcoreUtil.class));
    return "final "
        + resolvedCode
        + " created = "
        + createCall
        + "();\n"
        + ecoreUtil
        + ".setID(created, "
        + ecoreUtil
        + ".generateUUID());\n"
        + "return created;";
  }

  private static boolean existsWithName(
      Iterable<? extends MethodDeclaration> methods, String name) {
    for (var m : methods) {
      if (m.getSimpleName().equals(name)) {
        return true;
      }
    }
    return false;
  }
}
