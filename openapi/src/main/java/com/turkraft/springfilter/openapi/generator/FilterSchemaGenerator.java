package com.turkraft.springfilter.openapi.generator;

import com.turkraft.springfilter.openapi.documentation.FunctionDocumentationProvider;
import com.turkraft.springfilter.openapi.documentation.OperatorDocumentationProvider;
import com.turkraft.springfilter.openapi.documentation.PlaceholderDocumentationProvider;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.EntitySchema;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.FieldInfo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FilterSchemaGenerator {

  private final EntityIntrospector entityIntrospector;
  private final OperatorDocumentationProvider operatorDocumentationProvider;
  private final FunctionDocumentationProvider functionDocumentationProvider;
  private final PlaceholderDocumentationProvider placeholderDocumentationProvider;

  public FilterSchemaGenerator(EntityIntrospector entityIntrospector,
      OperatorDocumentationProvider operatorDocumentationProvider,
      FunctionDocumentationProvider functionDocumentationProvider,
      PlaceholderDocumentationProvider placeholderDocumentationProvider) {
    this.entityIntrospector = entityIntrospector;
    this.operatorDocumentationProvider = operatorDocumentationProvider;
    this.functionDocumentationProvider = functionDocumentationProvider;
    this.placeholderDocumentationProvider = placeholderDocumentationProvider;
  }

  public String generateSchemaDescription(Class<?> entityClass) {

    EntitySchema schema = entityIntrospector.introspect(entityClass);
    StringBuilder description = new StringBuilder();

    description.append("**Available Fields**\n\n");

    List<FieldInfo> rootFields = schema.getRootFields();
    for (FieldInfo field : rootFields) {
      appendFieldDocumentation(description, field, schema);
    }

    List<OperatorDocumentationProvider.OperatorDoc> operators =
        operatorDocumentationProvider.getAllOperatorDocs();
    if (!operators.isEmpty()) {
      description.append("\n**Operators**\n\n");
      appendOperatorSummary(description);
    }

    List<FunctionDocumentationProvider.FunctionDoc> functions =
        functionDocumentationProvider.getAllFunctionDocs();
    if (!functions.isEmpty()) {
      description.append("\n**Functions**\n\n");
      appendFunctionSummary(description);
    }

    List<PlaceholderDocumentationProvider.PlaceholderDoc> placeholders =
        placeholderDocumentationProvider.getAllPlaceholderDocs();
    if (!placeholders.isEmpty()) {
      description.append("\n**Placeholders**\n\n");
      appendPlaceholderSummary(description);
    }

    return description.toString();

  }

  private void appendFieldDocumentation(StringBuilder sb, FieldInfo field, EntitySchema schema) {

    sb
        .append("- **`")
        .append(field.getPath())
        .append("`** ");

    sb
        .append("(")
        .append(getFieldTypeDisplay(field))
        .append(")");

    if (field.getEnumValues() != null && !field
        .getEnumValues()
        .isEmpty()) {
      sb.append(" - Values: ");
      sb
          .append("`")
          .append(String.join("`, `", field.getEnumValues()))
          .append("`");
    }

    sb.append("\n");

    if (field.isRelation() || field.isEmbedded()) {
      List<FieldInfo> nestedFields = schema.getNestedFields(field.getPath());
      if (!nestedFields.isEmpty() && nestedFields.size() <= 5) {
        for (FieldInfo nested : nestedFields) {
          sb
              .append("  - `")
              .append(nested.getPath())
              .append("` (")
              .append(getFieldTypeDisplay(nested))
              .append(")\n");
        }
      } else if (nestedFields.size() > 5) {
        sb
            .append("  - Access nested fields using dot notation: `")
            .append(field.getPath())
            .append(".fieldName`\n");
      }
    }

  }

  private String getFieldTypeDisplay(FieldInfo field) {

    StringBuilder type = new StringBuilder();

    if (field.getFieldCategory() == null) {
      type.append(field
          .getType()
          .getSimpleName());
      if (field.isRelation()) {
        type.append(", relation");
      }
      return type.toString();
    }

    switch (field.getFieldCategory()) {
      case PRIMITIVE:
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
          type.append("boolean");
        } else if (isIntegerType(field.getType())) {
          type.append("integer");
        } else if (isFloatingType(field.getType())) {
          type.append("number");
        }
        break;
      case STRING:
        type.append("string");
        break;
      case DATE:
        type.append("date");
        break;
      case ENUM:
        type.append("enum");
        break;
      case COLLECTION:
        type.append("collection");
        if (field.getTargetType() != Object.class) {
          type
              .append(" of ")
              .append(field
                  .getTargetType()
                  .getSimpleName());
        }
        break;
      case MAP:
        type.append("map");
        break;
      case OBJECT:
        type.append("object");
        break;
      default:
        type.append(field
            .getType()
            .getSimpleName());
    }

    if (field.isRelation()) {
      type.append(", relation");
    }

    return type.toString();

  }

  private boolean isIntegerType(Class<?> type) {
    return type == int.class || type == Integer.class || type == long.class || type == Long.class
        || type == short.class || type == Short.class || type == byte.class
        || type == Byte.class;
  }

  private boolean isFloatingType(Class<?> type) {
    return type == float.class || type == Float.class || type == double.class
        || type == Double.class;
  }

  private void appendOperatorSummary(StringBuilder sb) {

    List<OperatorDocumentationProvider.OperatorDoc> operators =
        operatorDocumentationProvider.getAllOperatorDocs();

    if (operators.isEmpty()) {
      return;
    }

    for (OperatorDocumentationProvider.OperatorDoc op : operators) {
      sb
          .append("- `")
          .append(String.join("`, `", op.getTokens()))
          .append("`");

      if (op.getDescription() != null) {
        sb
            .append(" - ")
            .append(op.getDescription());
      }

      if (op.getExample() != null) {
        sb
            .append(" (`")
            .append(op.getExample())
            .append("`)");
      }

      sb.append("\n");
    }

  }

  private void appendFunctionSummary(StringBuilder sb) {

    List<FunctionDocumentationProvider.FunctionDoc> functions =
        functionDocumentationProvider.getAllFunctionDocs();

    if (functions.isEmpty()) {
      return;
    }

    for (FunctionDocumentationProvider.FunctionDoc func : functions) {
      sb
          .append("- `")
          .append(func.getName())
          .append("`");

      if (func.getDescription() != null) {
        sb
            .append(" - ")
            .append(func.getDescription());
      }

      if (func.getExample() != null) {
        sb
            .append(" (`")
            .append(func.getExample())
            .append("`)");
      }

      sb.append("\n");
    }

  }

  private void appendPlaceholderSummary(StringBuilder sb) {

    List<PlaceholderDocumentationProvider.PlaceholderDoc> placeholders =
        placeholderDocumentationProvider.getAllPlaceholderDocs();

    if (placeholders.isEmpty()) {
      return;
    }

    for (PlaceholderDocumentationProvider.PlaceholderDoc placeholder : placeholders) {
      sb
          .append("- `")
          .append(placeholder.getName())
          .append("`");

      if (placeholder.getDescription() != null) {
        sb
            .append(" - ")
            .append(placeholder.getDescription());
      }

      if (placeholder.getExample() != null) {
        sb
            .append(" (`")
            .append(placeholder.getExample())
            .append("`)");
      }

      sb.append("\n");
    }

  }

  public String generateShortDescription(Class<?> entityClass) {
    return "Filter " + entityClass.getSimpleName()
        + " entities using query syntax. Examples: field : value, field > 100, field ~ 'pattern%'";
  }

  public List<String> getAvailableFields(Class<?> entityClass) {
    EntitySchema schema = entityIntrospector.introspect(entityClass);
    List<String> fields = new ArrayList<>();

    for (FieldInfo field : schema
        .getFields()
        .values()) {
      fields.add(field.getPath());
    }

    return fields;
  }

}
