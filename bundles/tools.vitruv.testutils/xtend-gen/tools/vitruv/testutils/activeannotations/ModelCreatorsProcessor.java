package tools.vitruv.testutils.activeannotations;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableConstructorDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableTypeParameterDeclaration;
import org.eclipse.xtend.lib.macro.declaration.ResolvedMethod;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend.lib.macro.services.Problem;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtend2.lib.StringConcatenationClient;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

@SuppressWarnings("all")
public class ModelCreatorsProcessor extends AbstractClassProcessor {
  private static final String NEW_EOBJECT_ARGUMENT_CONVERTER = "NewEObject";

  private static final String CLASSIFIER_ARGUMENT_CONVERTER = "Classifier";

  private TypeReference checkPreconditions(final ClassDeclaration annotatedClass, @Extension final TransformationContext context) {
    TypeReference _xblockexpression = null;
    {
      try {
        Preconditions.<TypeReference>checkNotNull(context.newTypeReference(Preconditions.class));
      } catch (final Throwable _t) {
        if (_t instanceof NullPointerException || _t instanceof ClassNotFoundException || _t instanceof NoClassDefFoundError) {
          context.addError(annotatedClass, "Please add Guava to the classpath!");
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      try {
        Preconditions.<TypeReference>checkNotNull(context.newTypeReference(ParameterContext.class));
      } catch (final Throwable _t) {
        if (_t instanceof NullPointerException || _t instanceof ClassNotFoundException || _t instanceof NoClassDefFoundError) {
          context.addError(annotatedClass, "Please add the JUnit Jupiter API to the classpath!");
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      TypeReference _xtrycatchfinallyexpression = null;
      try {
        _xtrycatchfinallyexpression = Preconditions.<TypeReference>checkNotNull(context.newTypeReference(ArgumentConverter.class));
      } catch (final Throwable _t) {
        if (_t instanceof NullPointerException || _t instanceof ClassNotFoundException || _t instanceof NoClassDefFoundError) {
          context.addError(annotatedClass, "Please add JUnit 5 Params to the classpath!");
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      _xblockexpression = _xtrycatchfinallyexpression;
    }
    return _xblockexpression;
  }

  @Override
  public void doRegisterGlobals(final ClassDeclaration annotatedClass, @Extension final RegisterGlobalsContext context) {
    StringConcatenation _builder = new StringConcatenation();
    String _qualifiedName = annotatedClass.getQualifiedName();
    _builder.append(_qualifiedName);
    _builder.append(".");
    _builder.append(ModelCreatorsProcessor.NEW_EOBJECT_ARGUMENT_CONVERTER);
    context.registerClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    String _qualifiedName_1 = annotatedClass.getQualifiedName();
    _builder_1.append(_qualifiedName_1);
    _builder_1.append(".");
    _builder_1.append(ModelCreatorsProcessor.CLASSIFIER_ARGUMENT_CONVERTER);
    context.registerClass(_builder_1.toString());
  }

  @Override
  public void doTransform(final MutableClassDeclaration annotatedClass, @Extension final TransformationContext context) {
    this.checkPreconditions(annotatedClass, context);
    final AnnotationReference annotation = annotatedClass.findAnnotation(context.findTypeGlobally(ModelCreators.class));
    if ((annotation == null)) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Cannot find ");
      String _simpleName = ModelCreators.class.getSimpleName();
      _builder.append(_simpleName);
      _builder.append(" annotation on ");
      String _simpleName_1 = annotatedClass.getSimpleName();
      _builder.append(_simpleName_1);
      context.addError(annotatedClass, _builder.toString());
    }
    final Function1<Problem, Boolean> _function = (Problem it) -> {
      Problem.Severity _severity = it.getSeverity();
      return Boolean.valueOf(Objects.equal(_severity, Problem.Severity.ERROR));
    };
    boolean _exists = IterableExtensions.exists(context.getProblems(annotatedClass), _function);
    if (_exists) {
      return;
    }
    final TypeReference factory = annotation.getClassValue("factory");
    final boolean createStatic = annotation.getBooleanValue("staticCreators");
    final String stripPrefix = annotation.getStringValue("stripPrefix");
    final String prefix = annotation.getStringValue("prefix");
    final String[] rawReplacements = annotation.getStringArrayValue("replace");
    int _size = ((List<String>)Conversions.doWrapArray(rawReplacements)).size();
    int _modulo = (_size % 2);
    boolean _equals = (_modulo == 0);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("The replace array must have an even number of elements!");
    Preconditions.checkArgument(_equals, _builder_1);
    final HashMap<String, String> replacements = new HashMap<String, String>();
    {
      int i = 0;
      int _size_1 = ((List<String>)Conversions.doWrapArray(rawReplacements)).size();
      boolean _lessThan = (i < _size_1);
      boolean _while = _lessThan;
      while (_while) {
        replacements.put(rawReplacements[i], rawReplacements[(i + 1)]);
        int _i = i;
        i = (_i + 2);
        int _size_2 = ((List<String>)Conversions.doWrapArray(rawReplacements)).size();
        boolean _lessThan_1 = (i < _size_2);
        _while = _lessThan_1;
      }
    }
    final Function1<ResolvedMethod, MethodDeclaration> _function_1 = (ResolvedMethod it) -> {
      return it.getDeclaration();
    };
    final Function1<MethodDeclaration, Boolean> _function_2 = (MethodDeclaration it) -> {
      return Boolean.valueOf((it.getSimpleName().startsWith("create") && IterableExtensions.isEmpty(it.getParameters())));
    };
    final Consumer<MethodDeclaration> _function_3 = (MethodDeclaration createMethod) -> {
      final String baseName = ModelCreatorsProcessor.removePrefix(ModelCreatorsProcessor.removePrefix(createMethod.getSimpleName(), "create"), stripPrefix);
      String _firstUpper = StringExtensions.toFirstUpper(replacements.getOrDefault(baseName, baseName));
      final String targetName = (prefix + _firstUpper);
      final Procedure1<MutableMethodDeclaration> _function_4 = (MutableMethodDeclaration it) -> {
        it.setStatic(createStatic);
        it.setReturnType(createMethod.getReturnType());
        StringConcatenationClient _client = new StringConcatenationClient() {
          @Override
          protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
            _builder.append("return ");
            _builder.append(factory);
            _builder.append(".eINSTANCE.");
            String _simpleName = createMethod.getSimpleName();
            _builder.append(_simpleName);
            _builder.append("();");
          }
        };
        it.setBody(_client);
        context.setPrimarySourceElement(it, annotatedClass);
      };
      annotatedClass.addMethod(targetName, _function_4);
    };
    IterableExtensions.<MethodDeclaration>filter(IterableExtensions.map(factory.getDeclaredResolvedMethods(), _function_1), _function_2).forEach(_function_3);
    final Procedure1<MutableMethodDeclaration> _function_4 = (MutableMethodDeclaration it) -> {
      it.setReturnType(context.newTypeReference(EClassifier.class));
      it.setStatic(true);
      it.setVisibility(Visibility.PRIVATE);
      it.addParameter("classifierName", context.newTypeReference(String.class));
      StringConcatenationClient _client = new StringConcatenationClient() {
        @Override
        protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
          _builder.append("return ");
          _builder.append(Preconditions.class);
          _builder.append(".checkNotNull(");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append(factory, "\t");
          _builder.append(".eINSTANCE.getEPackage().getEClassifier(classifierName),");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("\"There is no classifier called \'%s\' in \'%s\'!\", classifierName, ");
          _builder.append(factory, "\t");
          _builder.append(".eINSTANCE.getEPackage().getName()");
          _builder.newLineIfNotEmpty();
          _builder.append(");");
          _builder.newLine();
        }
      };
      it.setBody(_client);
    };
    annotatedClass.addMethod("_getClassifier", _function_4);
    final Procedure1<MutableMethodDeclaration> _function_5 = (MutableMethodDeclaration it) -> {
      it.setReturnType(context.newTypeReference(EClassifier.class));
      it.addParameter("classifierName", context.newTypeReference(String.class));
      StringConcatenationClient _client = new StringConcatenationClient() {
        @Override
        protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
          _builder.append("return _getClassifier(classifierName);");
        }
      };
      it.setBody(_client);
    };
    annotatedClass.addMethod("classifier", _function_5);
    final Procedure1<MutableMethodDeclaration> _function_6 = (MutableMethodDeclaration it) -> {
      it.setReturnType(context.newTypeReference(EObject.class));
      it.setStatic(true);
      it.setVisibility(Visibility.PRIVATE);
      it.addParameter("className", context.newTypeReference(String.class));
      StringConcatenationClient _client = new StringConcatenationClient() {
        @Override
        protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
          _builder.append(EClassifier.class);
          _builder.append(" requestedClassifier = _getClassifier(className);");
          _builder.newLineIfNotEmpty();
          _builder.append(Preconditions.class);
          _builder.append(".checkArgument(");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("requestedClassifier instanceof ");
          _builder.append(EClass.class, "\t");
          _builder.append(",");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("\"%s is not an EClass and can thus not be instantiated!\", className");
          _builder.newLine();
          _builder.append(");");
          _builder.newLine();
          _builder.append("return ");
          _builder.append(factory);
          _builder.append(".eINSTANCE.create((");
          _builder.append(EClass.class);
          _builder.append(") requestedClassifier);");
          _builder.newLineIfNotEmpty();
        }
      };
      it.setBody(_client);
    };
    annotatedClass.addMethod("_createInstance", _function_6);
    final Procedure1<MutableMethodDeclaration> _function_7 = (MutableMethodDeclaration it) -> {
      it.setReturnType(context.newTypeReference(EObject.class));
      it.addParameter("className", context.newTypeReference(String.class));
      StringConcatenationClient _client = new StringConcatenationClient() {
        @Override
        protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
          _builder.append("return _createInstance(className);");
        }
      };
      it.setBody(_client);
    };
    annotatedClass.addMethod("create", _function_7);
    final Procedure1<MutableMethodDeclaration> _function_8 = (MutableMethodDeclaration it) -> {
      final MutableTypeParameterDeclaration type = it.addTypeParameter("M", context.newTypeReference(EObject.class));
      it.setReturnType(context.newSelfTypeReference(type));
      it.addParameter("clazz", context.newTypeReference(Class.class, context.newWildcardTypeReference(context.newSelfTypeReference(type))));
      StringConcatenationClient _client = new StringConcatenationClient() {
        @Override
        protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
          _builder.append("return clazz.cast(_createInstance(clazz.getSimpleName()));");
        }
      };
      it.setBody(_client);
    };
    annotatedClass.addMethod("create", _function_8);
    boolean _isEmpty = IterableExtensions.isEmpty(annotatedClass.getDeclaredConstructors());
    if (_isEmpty) {
      final Procedure1<MutableConstructorDeclaration> _function_9 = (MutableConstructorDeclaration it) -> {
        it.setVisibility(Visibility.PRIVATE);
      };
      annotatedClass.addConstructor(_function_9);
    }
    StringConcatenation _builder_2 = new StringConcatenation();
    String _qualifiedName = annotatedClass.getQualifiedName();
    _builder_2.append(_qualifiedName);
    _builder_2.append(".");
    _builder_2.append(ModelCreatorsProcessor.NEW_EOBJECT_ARGUMENT_CONVERTER);
    MutableClassDeclaration _findClass = context.findClass(_builder_2.toString());
    final Procedure1<MutableClassDeclaration> _function_10 = (MutableClassDeclaration it) -> {
      TypeReference _newTypeReference = context.newTypeReference(ArgumentConverter.class);
      it.setImplementedInterfaces(Collections.<TypeReference>unmodifiableList(CollectionLiterals.<TypeReference>newArrayList(_newTypeReference)));
      final Procedure1<MutableMethodDeclaration> _function_11 = (MutableMethodDeclaration it_1) -> {
        it_1.setReturnType(context.newTypeReference(EObject.class));
        it_1.addParameter("source", context.newTypeReference(Object.class));
        it_1.addParameter("context", context.newTypeReference(ParameterContext.class));
        TypeReference _newTypeReference_1 = context.newTypeReference(ArgumentConversionException.class);
        it_1.setExceptions(new TypeReference[] { _newTypeReference_1 });
        StringConcatenationClient _client = new StringConcatenationClient() {
          @Override
          protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
            _builder.append("try {");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("return _createInstance((String) source);");
            _builder.newLine();
            _builder.append("} catch (IllegalArgumentException | NullPointerException e) {");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("throw new ");
            _builder.append(ArgumentConversionException.class, "\t");
            _builder.append("(e.getMessage(), e);");
            _builder.newLineIfNotEmpty();
            _builder.append("}");
            _builder.newLine();
          }
        };
        it_1.setBody(_client);
      };
      it.addMethod("convert", _function_11);
    };
    ObjectExtensions.<MutableClassDeclaration>operator_doubleArrow(_findClass, _function_10);
    StringConcatenation _builder_3 = new StringConcatenation();
    String _qualifiedName_1 = annotatedClass.getQualifiedName();
    _builder_3.append(_qualifiedName_1);
    _builder_3.append(".");
    _builder_3.append(ModelCreatorsProcessor.CLASSIFIER_ARGUMENT_CONVERTER);
    MutableClassDeclaration _findClass_1 = context.findClass(_builder_3.toString());
    final Procedure1<MutableClassDeclaration> _function_11 = (MutableClassDeclaration it) -> {
      TypeReference _newTypeReference = context.newTypeReference(ArgumentConverter.class);
      it.setImplementedInterfaces(Collections.<TypeReference>unmodifiableList(CollectionLiterals.<TypeReference>newArrayList(_newTypeReference)));
      final Procedure1<MutableMethodDeclaration> _function_12 = (MutableMethodDeclaration it_1) -> {
        it_1.setReturnType(context.newTypeReference(EObject.class));
        it_1.addParameter("source", context.newTypeReference(Object.class));
        it_1.addParameter("context", context.newTypeReference(ParameterContext.class));
        TypeReference _newTypeReference_1 = context.newTypeReference(ArgumentConversionException.class);
        it_1.setExceptions(new TypeReference[] { _newTypeReference_1 });
        StringConcatenationClient _client = new StringConcatenationClient() {
          @Override
          protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
            _builder.append("try {");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("return _getClassifier((String) source);");
            _builder.newLine();
            _builder.append("} catch (NullPointerException e) {");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("throw new ");
            _builder.append(ArgumentConversionException.class, "\t");
            _builder.append("(e.getMessage(), e);");
            _builder.newLineIfNotEmpty();
            _builder.append("}");
            _builder.newLine();
          }
        };
        it_1.setBody(_client);
      };
      it.addMethod("convert", _function_12);
    };
    ObjectExtensions.<MutableClassDeclaration>operator_doubleArrow(_findClass_1, _function_11);
  }

  private static String removePrefix(final String target, final String prefix) {
    String _xifexpression = null;
    boolean _startsWith = target.startsWith(prefix);
    if (_startsWith) {
      _xifexpression = target.substring(prefix.length());
    } else {
      _xifexpression = target;
    }
    return _xifexpression;
  }
}
