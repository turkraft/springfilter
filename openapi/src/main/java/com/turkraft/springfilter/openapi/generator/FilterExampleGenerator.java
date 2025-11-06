package com.turkraft.springfilter.openapi.generator;

import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.EntitySchema;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.FieldCategory;
import com.turkraft.springfilter.openapi.introspection.EntityIntrospector.FieldInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FilterExampleGenerator {

  private final EntityIntrospector entityIntrospector;

  public FilterExampleGenerator(EntityIntrospector entityIntrospector) {
    this.entityIntrospector = entityIntrospector;
  }

  public List<String> generateExamples(Class<?> entityClass) {

    EntitySchema schema = entityIntrospector.introspect(entityClass);

    List<String> examples = new ArrayList<>();

    examples.addAll(generateBasicExamples(schema));
    examples.addAll(generateRelationExamples(schema));
    examples.addAll(generateCollectionExamples(schema));
    examples.addAll(generateComplexExamples(schema));

    return examples.stream().limit(10).collect(Collectors.toList());

  }

  private List<String> generateBasicExamples(EntitySchema schema) {

    List<String> examples = new ArrayList<>();

    for (FieldInfo field : schema.getRootFields()) {
      String example = generateFieldExample(field);
      if (example != null) {
        examples.add(example);
        if (examples.size() >= 3) {
          break;
        }
      }
    }

    return examples;

  }

  @Nullable
  private String generateFieldExample(FieldInfo field) {

    if (field.isRelation() || field.isEmbedded()) {
      return null;
    }

    String fieldPath = field.getPath();

    FieldCategory category = field.getFieldCategory();
    if (category == null) {
      return null;
    }

    switch (category) {
      case PRIMITIVE:
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
          return fieldPath + " : true";
        } else if (isNumericType(field.getType())) {
          return fieldPath + " > 100";
        }
        break;

      case STRING:
        return fieldPath + " ~ '%example%'";

      case DATE:
        return fieldPath + " > '2025-01-01'";

      case ENUM:
        if (field.getEnumValues() != null && !field.getEnumValues().isEmpty()) {
          String firstValue = field.getEnumValues().get(0);
          return fieldPath + " : '" + firstValue + "'";
        }
        break;

      case COLLECTION:
        return "size(" + fieldPath + ") > 0";

      default:
        break;
    }

    return null;

  }

  private List<String> generateRelationExamples(EntitySchema schema) {

    List<String> examples = new ArrayList<>();

    for (FieldInfo field : schema.getRootFields()) {
      if (field.isRelation()) {
        List<FieldInfo> nestedFields = schema.getNestedFields(field.getPath());

        if (!nestedFields.isEmpty()) {
          FieldInfo firstNested = nestedFields.get(0);
          String example = generateFieldExample(firstNested);
          if (example != null) {
            examples.add(example);
            if (examples.size() >= 2) {
              break;
            }
          }
        } else {
          examples.add(field.getPath() + " is not null");
          if (examples.size() >= 2) {
            break;
          }
        }
      }
    }

    return examples;

  }

  private List<String> generateCollectionExamples(EntitySchema schema) {

    List<String> examples = new ArrayList<>();

    for (FieldInfo field : schema.getRootFields()) {
      if (field.isCollection()) {
        examples.add("size(" + field.getPath() + ") > 2");
        examples.add(field.getPath() + " is not empty");
        break;
      }
    }

    return examples;

  }

  private List<String> generateComplexExamples(EntitySchema schema) {

    List<String> examples = new ArrayList<>();

    List<FieldInfo> rootFields = schema.getRootFields();
    if (rootFields.size() >= 2) {
      String example1 = generateFieldExample(rootFields.get(0));
      String example2 = generateFieldExample(rootFields.get(1));

      if (example1 != null && example2 != null) {
        examples.add(example1 + " and " + example2);
        examples.add("(" + example1 + ") or (" + example2 + ")");
      }
    }

    if (!rootFields.isEmpty()) {
      String example = generateFieldExample(rootFields.get(0));
      if (example != null) {
        examples.add("not (" + example + ")");
      }
    }

    return examples;

  }

  private boolean isNumericType(Class<?> type) {
    return type == int.class || type == Integer.class || type == long.class || type == Long.class
        || type == double.class || type == Double.class || type == float.class
        || type == Float.class || type == short.class || type == Short.class;
  }

  public String generateComprehensiveExample(Class<?> entityClass) {
    EntitySchema schema = entityIntrospector.introspect(entityClass);
    List<String> parts = new ArrayList<>();

    for (FieldInfo field : schema.getRootFields()) {
      if (field.getFieldCategory() == FieldCategory.PRIMITIVE && isNumericType(field.getType())) {
        parts.add(field.getPath() + " > 100");
        break;
      }
    }

    for (FieldInfo field : schema.getRootFields()) {
      if (field.getFieldCategory() == FieldCategory.STRING) {
        parts.add(field.getPath() + " ~ '%text%'");
        break;
      }
    }

    for (FieldInfo field : schema.getRootFields()) {
      if (field.isRelation()) {
        parts.add(field.getPath() + " is not null");
        break;
      }
    }

    if (parts.isEmpty()) {
      return generateBasicExamples(schema).stream().findFirst().orElse("id > 0");
    }

    return String.join(" and ", parts);

  }

}
