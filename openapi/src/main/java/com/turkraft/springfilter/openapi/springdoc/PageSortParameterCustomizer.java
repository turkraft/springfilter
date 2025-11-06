package com.turkraft.springfilter.openapi.springdoc;

import com.turkraft.springfilter.openapi.introspection.EntityIntrospector;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public class PageSortParameterCustomizer extends BaseParameterCustomizer
    implements OperationCustomizer {

  private final EntityIntrospector entityIntrospector;

  public PageSortParameterCustomizer(EntityIntrospector entityIntrospector) {
    this.entityIntrospector = entityIntrospector;
  }

  @Nullable
  @Override
  public Operation customize(@Nullable Operation operation, @Nullable HandlerMethod handlerMethod) {

    if (operation == null || handlerMethod == null) {
      return operation;
    }

    Method method = handlerMethod.getMethod();
    if (method == null) {
      return operation;
    }

    Class<?> entityClass = findEntityClassFromMethod(method);

    try {
      Class<?> fieldsAnnotationClass = Class.forName("com.turkraft.springfilter.boot.Fields");
      Object fieldsAnnotation = method.getAnnotation((Class) fieldsAnnotationClass);

      if (fieldsAnnotation != null) {
        customizeFieldsParameter(operation, fieldsAnnotation, entityClass);
      }
    } catch (ClassNotFoundException ignored) {
    }

    java.lang.reflect.Parameter[] parameters = method.getParameters();
    if (parameters == null) {
      return operation;
    }

    for (java.lang.reflect.Parameter parameter : parameters) {

      try {

        Class<?> sortAnnotation = Class.forName("com.turkraft.springfilter.boot.Sort");
        Class<?> pageAnnotation = Class.forName("com.turkraft.springfilter.boot.Page");

        Object sort = parameter.getAnnotation((Class) sortAnnotation);
        Object page = parameter.getAnnotation((Class) pageAnnotation);

        if (sort != null || isSortType(parameter)) {
          removeAutoDetectedParameter(operation, parameter);
          customizeSortParameter(operation, sort, parameter, entityClass);
        }

        if (page != null || isPageType(parameter)) {
          removeAutoDetectedParameter(operation, parameter);
          customizePageParameter(operation, page, parameter, entityClass);
        }

      } catch (ClassNotFoundException ignored) {
      }

    }

    return operation;

  }

  private boolean isSortType(java.lang.reflect.Parameter parameter) {
    String typeName = parameter
        .getType()
        .getName();
    return typeName.equals("org.springframework.data.domain.Sort");
  }

  private boolean isPageType(java.lang.reflect.Parameter parameter) {
    String typeName = parameter
        .getType()
        .getName();
    return typeName.equals("org.springframework.data.domain.Pageable");
  }


  private void customizeSortParameter(Operation operation, @Nullable Object sortAnnotation,
      java.lang.reflect.Parameter parameter, @Nullable Class<?> entityClass) {

    try {

      String parameterName = "sort";
      boolean required = false;
      String defaultValue = "";
      int maxFields = 10;

      if (sortAnnotation != null) {
        Class<?> sortClass = sortAnnotation.getClass();
        parameterName = (String) sortClass
            .getMethod("parameter")
            .invoke(sortAnnotation);
        required = (Boolean) sortClass
            .getMethod("required")
            .invoke(sortAnnotation);
        defaultValue = (String) sortClass
            .getMethod("defaultValue")
            .invoke(sortAnnotation);
        maxFields = (Integer) sortClass
            .getMethod("maxFields")
            .invoke(sortAnnotation);
      }

      Parameter openApiParameter = findOrCreateParameter(operation, parameterName);
      openApiParameter.setIn("query");
      openApiParameter.setRequired(required);

      String description = "Sort fields (comma-separated). Use `-` prefix for descending order. "
          + "Example: `-createdAt,name` sorts by createdAt descending, then name ascending. "
          + "Max fields: "
          + maxFields;

      openApiParameter.setDescription(description);

      Schema<?> schema = new StringSchema();
      schema.setType("string");

      if (!defaultValue.isEmpty()) {
        schema.setDefault(defaultValue);
      }

      String example = generateSortExample(entityClass);
      schema.setExample(example);

      openApiParameter.setSchema(schema);

      ensureParameterInOperation(operation, openApiParameter);

    } catch (Exception ignored) {
    }

  }

  private String generateSortExample(@Nullable Class<?> entityClass) {

    if (entityClass == null) {
      return "-createdAt,name";
    }

    try {
      EntityIntrospector.EntitySchema schema = entityIntrospector.introspect(entityClass);
      List<String> rootFields = schema
          .getRootFields()
          .stream()
          .filter(f -> !f.isCollection() && !f.isRelation())
          .map(f -> f.getPath())
          .limit(2)
          .collect(Collectors.toList());

      if (rootFields.isEmpty()) {
        return "-createdAt,name";
      }

      if (rootFields.size() == 1) {
        return "-" + rootFields.get(0);
      }

      return "-" + rootFields.get(0) + "," + rootFields.get(1);
    } catch (Exception e) {
      return "-createdAt,name";
    }

  }

  private void customizePageParameter(Operation operation, @Nullable Object pageAnnotation,
      java.lang.reflect.Parameter parameter, @Nullable Class<?> entityClass) {

    try {

      String pageParam = "page";
      String sizeParam = "size";
      String sortParam = "sort";
      int defaultPage = 0;
      int defaultSize = 20;
      int maxSize = 100;
      int sortMaxFields = 10;
      boolean enableSort = true;

      if (pageAnnotation != null) {
        Class<?> pageClass = pageAnnotation.getClass();
        pageParam = (String) pageClass
            .getMethod("pageParameter")
            .invoke(pageAnnotation);
        sizeParam = (String) pageClass
            .getMethod("sizeParameter")
            .invoke(pageAnnotation);
        sortParam = (String) pageClass
            .getMethod("sortParameter")
            .invoke(pageAnnotation);
        defaultPage = (Integer) pageClass
            .getMethod("defaultPage")
            .invoke(pageAnnotation);
        defaultSize = (Integer) pageClass
            .getMethod("defaultSize")
            .invoke(pageAnnotation);
        maxSize = (Integer) pageClass
            .getMethod("maxSize")
            .invoke(pageAnnotation);
        sortMaxFields = (Integer) pageClass
            .getMethod("sortMaxFields")
            .invoke(pageAnnotation);
        enableSort = (Boolean) pageClass
            .getMethod("enableSort")
            .invoke(pageAnnotation);
      }

      Parameter pageParameter = findOrCreateParameter(operation, pageParam);
      pageParameter.setIn("query");
      pageParameter.setRequired(false);
      pageParameter.setDescription("Page number (zero-based)");

      Schema<?> pageSchema = new IntegerSchema();
      pageSchema.setType("integer");
      pageSchema.setDefault(defaultPage);
      pageSchema.setMinimum(java.math.BigDecimal.ZERO);
      pageSchema.setExample(0);

      pageParameter.setSchema(pageSchema);
      ensureParameterInOperation(operation, pageParameter);

      Parameter sizeParameter = findOrCreateParameter(operation, sizeParam);
      sizeParameter.setIn("query");
      sizeParameter.setRequired(false);
      sizeParameter.setDescription("Page size (max: " + maxSize + ")");

      Schema<?> sizeSchema = new IntegerSchema();
      sizeSchema.setType("integer");
      sizeSchema.setDefault(defaultSize);
      sizeSchema.setMinimum(java.math.BigDecimal.ONE);
      sizeSchema.setMaximum(java.math.BigDecimal.valueOf(maxSize));
      sizeSchema.setExample(defaultSize);

      sizeParameter.setSchema(sizeSchema);
      ensureParameterInOperation(operation, sizeParameter);

      if (enableSort) {
        Parameter sortParameter = findOrCreateParameter(operation, sortParam);
        sortParameter.setIn("query");
        sortParameter.setRequired(false);

        String description = "Sort fields (comma-separated). Use `-` prefix for descending order. "
            + "Example: `-createdAt,name` sorts by createdAt descending, then name ascending. "
            + "Max fields: "
            + sortMaxFields;

        sortParameter.setDescription(description);

        Schema<?> sortSchema = new StringSchema();
        sortSchema.setType("string");

        String example = generateSortExample(entityClass);
        sortSchema.setExample(example);

        sortParameter.setSchema(sortSchema);
        ensureParameterInOperation(operation, sortParameter);
      }

    } catch (Exception ignored) {
    }

  }

  private void customizeFieldsParameter(Operation operation, @Nullable Object fieldsAnnotation,
      @Nullable Class<?> entityClass) {

    try {

      String parameterName = "fields";
      boolean required = false;
      String defaultValue = "";

      if (fieldsAnnotation != null) {
        Class<?> fieldsClass = fieldsAnnotation.getClass();
        parameterName = (String) fieldsClass
            .getMethod("parameter")
            .invoke(fieldsAnnotation);
        required = (Boolean) fieldsClass
            .getMethod("required")
            .invoke(fieldsAnnotation);
        defaultValue = (String) fieldsClass
            .getMethod("defaultValue")
            .invoke(fieldsAnnotation);
      }

      Parameter openApiParameter = findOrCreateParameter(operation, parameterName);
      openApiParameter.setIn("query");
      openApiParameter.setRequired(required);

      String description = "Fields to include in response (comma-separated). "
          + "Use `-` or `!` prefix to exclude. Supports wildcards (`*`) and nested paths. "
          + "Examples: `id,name,email` or `*,-password` or `user.*`";

      openApiParameter.setDescription(description);

      Schema<?> schema = new StringSchema();
      schema.setType("string");

      if (!defaultValue.isEmpty()) {
        schema.setDefault(defaultValue);
      }

      String example = generateFieldsExample(entityClass);
      schema.setExample(example);

      openApiParameter.setSchema(schema);

      ensureParameterInOperation(operation, openApiParameter);

    } catch (Exception ignored) {
    }

  }

  private String generateFieldsExample(@Nullable Class<?> entityClass) {

    if (entityClass == null) {
      return "id,name,email";
    }

    try {

      EntityIntrospector.EntitySchema schema = entityIntrospector.introspect(entityClass);
      List<String> allFields = schema
          .getRootFields()
          .stream()
          .filter(f -> !f.isCollection() && !f.isRelation())
          .map(f -> f.getPath())
          .collect(Collectors.toList());

      if (allFields.isEmpty()) {
        return "id,name,email";
      }

      List<String> fieldsForExample = allFields
          .stream()
          .skip(2)
          .limit(3)
          .collect(Collectors.toList());

      if (fieldsForExample.isEmpty()) {
        fieldsForExample = allFields
            .stream()
            .limit(3)
            .collect(Collectors.toList());
      }

      return String.join(",", fieldsForExample);

    } catch (Exception e) {
      return "id,name,email";
    }

  }

}
