package tools.vitruv.change.utils.activeannotations;

import java.util.stream.StreamSupport;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;

public class WithGeneratedIdsProcessor extends AbstractClassProcessor {

    @Override
    public void doTransform(MutableClassDeclaration annotatedClass, TransformationContext context) {
        TypeReference extendedFactory = annotatedClass.getExtendedClass();
        if (extendedFactory == null
                || !context.newTypeReference(EFactoryImpl.class).isAssignableFrom(extendedFactory)) {
            context.addError(annotatedClass,
                "A class annotated with " + WithGeneratedRandomIds.class.getSimpleName()
                + " must extend an extension of " + EFactoryImpl.class.getSimpleName() + ".");
            return;
        }

        var annotation = annotatedClass.findAnnotation(context.findTypeGlobally(WithGeneratedRandomIds.class));
        if (annotation == null) {
            context.addError(annotatedClass,
                "Cannot find " + WithGeneratedRandomIds.class.getSimpleName()
                + " annotation on " + annotatedClass.getSimpleName());
            return;
        }
        TypeReference identifierMetaclass = annotation.getClassValue("identifierMetaclass");

        var createMethods = StreamSupport.stream(extendedFactory.getDeclaredResolvedMethods().spliterator(), false)
            .filter(rm -> rm.getDeclaration().getSimpleName().startsWith("create")
                && !rm.getDeclaration().getParameters().iterator().hasNext()
                && identifierMetaclass.isAssignableFrom(rm.getDeclaration().getReturnType()))
            .toList();

        if (createMethods.isEmpty()) {
            context.addWarning(annotatedClass,
                "Could not find a single create method to override. Please check your arguments!");
        }

        for (var createMethod : createMethods) {
            MutableMethodDeclaration existingMethod = null;
            for (var m : annotatedClass.getDeclaredMethods()) {
                if (m.getSimpleName().equals(createMethod.getDeclaration().getSimpleName())
                        && !m.getParameters().iterator().hasNext()
                        && !m.isStatic()) {
                    existingMethod = m;
                    break;
                }
            }

            TypeReference resolvedReturn = createMethod.getResolvedReturnType();

            if (existingMethod != null) {
                String newName = existingMethod.getSimpleName();
                do {
                    newName = "_" + newName;
                } while (existsWithName(annotatedClass.getDeclaredMethods(), newName));

                final String privateName = newName;
                final TypeReference originalReturnType = existingMethod.getReturnType();
                final var originalBody = existingMethod.getBody();
                final MutableMethodDeclaration sourceRef = existingMethod;

                annotatedClass.addMethod(privateName, m -> {
                    m.setVisibility(Visibility.PRIVATE);
                    m.setReturnType(originalReturnType);
                    m.setBody(originalBody);
                    context.setPrimarySourceElement(m, sourceRef);
                });

                existingMethod.setBody(ctx -> {
                    String resolvedCode = ctx.toJavaCode(resolvedReturn);
                    String ecoreUtil = ctx.toJavaCode(context.newTypeReference(EcoreUtil.class));
                    return "final " + resolvedCode + " created = " + privateName + "();\n"
                        + ecoreUtil + ".setID(created, " + ecoreUtil + ".generateUUID());\n"
                        + "return created;";
                });
            } else {
                final String superCall = "super." + createMethod.getDeclaration().getSimpleName();
                annotatedClass.addMethod(createMethod.getDeclaration().getSimpleName(), m -> {
                    m.setReturnType(resolvedReturn);
                    m.setBody(ctx -> {
                        String resolvedCode = ctx.toJavaCode(resolvedReturn);
                        String ecoreUtil = ctx.toJavaCode(context.newTypeReference(EcoreUtil.class));
                        return "final " + resolvedCode + " created = " + superCall + "();\n"
                            + ecoreUtil + ".setID(created, " + ecoreUtil + ".generateUUID());\n"
                            + "return created;";
                    });
                    context.setPrimarySourceElement(m, annotatedClass);
                });
            }
        }
    }

    private static boolean existsWithName(Iterable<? extends MethodDeclaration> methods, String name) {
        for (var m : methods) {
            if (m.getSimpleName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
