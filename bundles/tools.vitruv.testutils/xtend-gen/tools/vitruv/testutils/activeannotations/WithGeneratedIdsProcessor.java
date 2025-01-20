package tools.vitruv.testutils.activeannotations;

import com.google.common.base.Objects;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.ResolvedMethod;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtend2.lib.StringConcatenationClient;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class WithGeneratedIdsProcessor extends AbstractClassProcessor {
  @Override
  public void doTransform(final MutableClassDeclaration annotatedClass, @Extension final TransformationContext context) {
    final TypeReference extendedFactory = annotatedClass.getExtendedClass();
    if (((extendedFactory == null) || (!context.newTypeReference(EFactoryImpl.class).isAssignableFrom(extendedFactory)))) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("\'A class annotated with ");
      String _simpleName = WithGeneratedRandomIds.class.getSimpleName();
      _builder.append(_simpleName);
      _builder.append(" must extend an extension ");
      _builder.append("of");
      String _simpleName_1 = EFactoryImpl.class.getSimpleName();
      _builder.append(_simpleName_1);
      _builder.append(".");
      context.addError(annotatedClass, _builder.toString());
      return;
    }
    final AnnotationReference annotation = annotatedClass.findAnnotation(context.findTypeGlobally(WithGeneratedRandomIds.class));
    if ((annotation == null)) {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("Cannot find ");
      String _simpleName_2 = WithGeneratedRandomIds.class.getSimpleName();
      _builder_1.append(_simpleName_2);
      _builder_1.append(" annotation on ");
      String _simpleName_3 = annotatedClass.getSimpleName();
      _builder_1.append(_simpleName_3);
      context.addError(annotatedClass, _builder_1.toString());
      return;
    }
    final TypeReference identifierMetaclass = annotation.getClassValue("identifierMetaclass");
    final Function1<ResolvedMethod, Boolean> _function = (ResolvedMethod it) -> {
      return Boolean.valueOf(((it.getDeclaration().getSimpleName().startsWith("create") && IterableExtensions.isEmpty(it.getDeclaration().getParameters())) && 
        identifierMetaclass.isAssignableFrom(it.getDeclaration().getReturnType())));
    };
    final Iterable<? extends ResolvedMethod> createMethods = IterableExtensions.filter(extendedFactory.getDeclaredResolvedMethods(), _function);
    boolean _isEmpty = IterableExtensions.isEmpty(createMethods);
    if (_isEmpty) {
      context.addWarning(annotatedClass, "Could not find a single create method to override. Please check your arguments!");
    }
    for (final ResolvedMethod createMethod : createMethods) {
      {
        final Function1<MutableMethodDeclaration, Boolean> _function_1 = (MutableMethodDeclaration it) -> {
          return Boolean.valueOf(((Objects.equal(it.getSimpleName(), createMethod.getDeclaration().getSimpleName()) && IterableExtensions.isEmpty(it.getParameters())) && (!it.isStatic())));
        };
        final MutableMethodDeclaration existingMethod = IterableExtensions.findFirst(annotatedClass.getDeclaredMethods(), _function_1);
        String _xifexpression = null;
        if ((existingMethod != null)) {
          String _xblockexpression = null;
          {
            String newName = existingMethod.getSimpleName();
            do {
              newName = ("_" + newName);
            } while(this.existsWithName(annotatedClass.getDeclaredMethods(), newName));
            final Procedure1<MutableMethodDeclaration> _function_2 = (MutableMethodDeclaration it) -> {
              it.setVisibility(Visibility.PRIVATE);
              it.setReturnType(existingMethod.getReturnType());
              it.setBody(existingMethod.getBody());
              context.setPrimarySourceElement(it, existingMethod);
            };
            annotatedClass.addMethod(newName, _function_2);
            _xblockexpression = newName;
          }
          _xifexpression = _xblockexpression;
        } else {
          String _simpleName_4 = createMethod.getDeclaration().getSimpleName();
          _xifexpression = ("super." + _simpleName_4);
        }
        final String creatorMethod = _xifexpression;
        final TypeReference createdMetaClass = createMethod.getResolvedReturnType();
        StringConcatenationClient _client = new StringConcatenationClient() {
          @Override
          protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
            _builder.append("final ");
            _builder.append(createdMetaClass);
            _builder.append(" created = ");
            _builder.append(creatorMethod);
            _builder.append("();");
            _builder.newLineIfNotEmpty();
            _builder.append(EcoreUtil.class);
            _builder.append(".setID(created, ");
            _builder.append(EcoreUtil.class);
            _builder.append(".generateUUID());");
            _builder.newLineIfNotEmpty();
            _builder.append("return created;");
            _builder.newLine();
          }
        };
        final StringConcatenationClient idSetterBody = _client;
        if ((existingMethod != null)) {
          existingMethod.setBody(idSetterBody);
        } else {
          final Procedure1<MutableMethodDeclaration> _function_2 = (MutableMethodDeclaration it) -> {
            it.setReturnType(createdMetaClass);
            it.setBody(idSetterBody);
            context.setPrimarySourceElement(it, annotatedClass);
          };
          annotatedClass.addMethod(createMethod.getDeclaration().getSimpleName(), _function_2);
        }
      }
    }
  }

  public String removeFromEnd(final String string, final String substring) {
    int _length = string.length();
    int _length_1 = substring.length();
    int _minus = (_length - _length_1);
    final String end = string.substring(_minus, string.length());
    boolean _notEquals = (!Objects.equal(end, substring));
    if (_notEquals) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(string);
      _builder.append(" does not end with ");
      _builder.append(substring);
      _builder.append("!");
      throw new IllegalArgumentException(_builder.toString());
    }
    int _length_2 = string.length();
    int _length_3 = substring.length();
    int _minus_1 = (_length_2 - _length_3);
    return string.substring(0, _minus_1);
  }

  public boolean existsWithName(final Iterable<? extends MethodDeclaration> methods, final String name) {
    final Function1<MethodDeclaration, Boolean> _function = (MethodDeclaration it) -> {
      String _simpleName = it.getSimpleName();
      return Boolean.valueOf(Objects.equal(_simpleName, name));
    };
    return IterableExtensions.exists(methods, _function);
  }
}
