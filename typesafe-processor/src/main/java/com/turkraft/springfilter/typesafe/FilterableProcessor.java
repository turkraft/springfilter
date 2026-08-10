package com.turkraft.springfilter.typesafe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.turkraft.springfilter.typesafe.Filterable")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class FilterableProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getElementsAnnotatedWith(Filterable.class)) {
      if (element.getKind() != ElementKind.CLASS) {
        continue;
      }
      TypeElement typeElement = (TypeElement) element;
      try {
        generateFilterClass(typeElement);
      } catch (IOException e) {
        processingEnv.getMessager().printError(
            "Failed to generate filter for " + typeElement.getQualifiedName() + ": " + e.getMessage());
      }
    }
    return false;
  }

  private void generateFilterClass(TypeElement entityElement) throws IOException {
    Filterable annotation = entityElement.getAnnotation(Filterable.class);
    String entityName = entityElement.getSimpleName().toString();
    String packageName = processingEnv.getElementUtils()
        .getPackageOf(entityElement).getQualifiedName().toString();
    String className = annotation.className().isEmpty()
        ? entityName + "Filter"
        : annotation.className();

    List<FieldInfo> fields = extractAllFields(entityElement);

    JavaFileObject file = processingEnv.getFiler()
        .createSourceFile(packageName + "." + className, entityElement);

    try (PrintWriter out = new PrintWriter(file.openWriter())) {
      writeClass(out, packageName, className, fields);
    }
  }

  private List<FieldInfo> extractAllFields(TypeElement typeElement) {
    List<FieldInfo> fields = new ArrayList<>();
    Set<String> seen = new LinkedHashSet<>();
    TypeElement current = typeElement;
    while (current != null && !current.getQualifiedName().toString().equals("java.lang.Object")) {
      for (Element enclosed : current.getEnclosedElements()) {
        if (enclosed.getKind() != ElementKind.FIELD) {
          continue;
        }
        if (enclosed.getModifiers().contains(Modifier.STATIC)) {
          continue;
        }
        VariableElement field = (VariableElement) enclosed;
        String fieldName = field.getSimpleName().toString();
        if (seen.contains(fieldName)) {
          continue;
        }
        if (hasSkipAnnotation(field)) {
          continue;
        }
        if (isConflictingName(fieldName)) {
          processingEnv.getMessager().printWarning(
              "Skipping field '" + fieldName + "' in " + typeElement.getSimpleName()
                  + " — name conflicts with generated method");
          continue;
        }
        seen.add(fieldName);
        TypeMirror fieldType = field.asType();
        FieldKind kind = classifyType(fieldType);
        fields.add(new FieldInfo(fieldName, kind));
      }
      TypeMirror superclass = current.getSuperclass();
      if (superclass.getKind() == TypeKind.DECLARED) {
        current = (TypeElement) processingEnv.getTypeUtils().asElement(superclass);
      } else {
        current = null;
      }
    }
    return fields;
  }

  private static final Set<String> CONFLICTING_NAMES = Set.of(
      "and", "or", "build", "getFilterBuilder",
      "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
      "class", "const", "continue", "default", "do", "double", "else", "enum",
      "extends", "final", "finally", "float", "for", "goto", "if", "implements",
      "import", "instanceof", "int", "interface", "long", "native", "new",
      "package", "private", "protected", "public", "return", "short", "static",
      "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
      "transient", "try", "void", "volatile", "while", "true", "false", "null"
  );

  private static boolean isConflictingName(String fieldName) {
    return CONFLICTING_NAMES.contains(fieldName);
  }

  private boolean hasSkipAnnotation(VariableElement field) {
    for (var ann : field.getAnnotationMirrors()) {
      String name = ann.getAnnotationType().toString();
      if (name.equals("jakarta.persistence.Transient")
          || name.equals("javax.persistence.Transient")
          || name.equals("com.fasterxml.jackson.annotation.JsonIgnore")) {
        return true;
      }
    }
    return false;
  }

  private FieldKind classifyType(TypeMirror type) {
    switch (type.getKind()) {
      case INT: case LONG: case SHORT: case BYTE:
        return FieldKind.INT;
      case DOUBLE: case FLOAT:
        return FieldKind.DOUBLE;
      case BOOLEAN:
        return FieldKind.BOOLEAN;
      default:
        break;
    }
    if (type.getKind() == TypeKind.DECLARED) {
      DeclaredType declaredType = (DeclaredType) type;
      String qualifiedName = declaredType.asElement().toString();
      if (qualifiedName.equals("java.lang.String")) {
        return FieldKind.STRING;
      }
      if (qualifiedName.equals("java.lang.Integer") || qualifiedName.equals("java.lang.Long")
          || qualifiedName.equals("java.lang.Short") || qualifiedName.equals("java.lang.Byte")) {
        return FieldKind.INT;
      }
      if (qualifiedName.equals("java.lang.Double") || qualifiedName.equals("java.lang.Float")) {
        return FieldKind.DOUBLE;
      }
      if (qualifiedName.equals("java.lang.Boolean")) {
        return FieldKind.BOOLEAN;
      }
      if (qualifiedName.startsWith("java.time.") || qualifiedName.equals("java.util.Date")
          || qualifiedName.equals("java.sql.Date")) {
        return FieldKind.DATE;
      }
      if (declaredType.asElement().getKind() == ElementKind.ENUM) {
        return FieldKind.ENUM;
      }
      TypeMirror collectionType = processingEnv.getElementUtils()
          .getTypeElement("java.util.Collection").asType();
      if (processingEnv.getTypeUtils().isAssignable(declaredType, collectionType)) {
        return FieldKind.COLLECTION;
      }
      return FieldKind.ENTITY;
    }
    return FieldKind.ENTITY;
  }

  private void writeClass(PrintWriter out, String packageName, String className,
      List<FieldInfo> fields) {

    out.println("package " + packageName + ";");
    out.println();
    out.println("import com.turkraft.springfilter.builder.FilterBuilder;");
    out.println("import com.turkraft.springfilter.typesafe.builder.*;");
    out.println("import javax.annotation.processing.Generated;");
    out.println();
    out.println("@Generated(\"" + FilterableProcessor.class.getName() + "\")");
    out.println("public class " + className + " extends FilterChain<" + className + "> {");
    out.println();
    out.println("  public static " + className + " where(FilterBuilder fb) {");
    out.println("    return new " + className + "(fb);");
    out.println("  }");
    out.println();
    out.println("  public " + className + "(FilterBuilder fb) {");
    out.println("    super(fb);");
    out.println("  }");
    out.println();

    for (FieldInfo field : fields) {
      writeFieldAccessor(out, field, className);
    }

    out.println("}");
  }

  private void writeFieldAccessor(PrintWriter out, FieldInfo field, String parentClass) {
    String stepClass;
    switch (field.kind) {
      case INT:
        stepClass = "IntFieldStep";
        break;
      case DOUBLE:
        stepClass = "DoubleFieldStep";
        break;
      case BOOLEAN:
        stepClass = "BooleanFieldStep";
        break;
      case STRING:
        stepClass = "StringFieldStep";
        break;
      case DATE:
        stepClass = "DateFieldStep";
        break;
      case ENUM:
        stepClass = "EnumFieldStep";
        break;
      case COLLECTION:
        stepClass = "CollectionFieldStep";
        break;
      default:
        return;
    }
    String returnType = stepClass + "<" + parentClass + ">";
    out.println("  public " + returnType + " " + field.name + "() {");
    out.println("    return new " + stepClass + "<>(this, \"" + field.name + "\");");
    out.println("  }");
    out.println();
  }

  private enum FieldKind {
    INT, DOUBLE, BOOLEAN, STRING, DATE, ENUM, COLLECTION, ENTITY
  }

  private static class FieldInfo {
    final String name;
    final FieldKind kind;

    FieldInfo(String name, FieldKind kind) {
      this.name = name;
      this.kind = kind;
    }
  }

}
