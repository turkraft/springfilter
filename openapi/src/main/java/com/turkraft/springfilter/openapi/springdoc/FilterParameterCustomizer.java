package com.turkraft.springfilter.openapi.springdoc;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.openapi.generator.FilterExampleGenerator;
import com.turkraft.springfilter.openapi.generator.FilterSchemaGenerator;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.lang.reflect.Method;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public class FilterParameterCustomizer implements OperationCustomizer {

  private final FilterSchemaGenerator filterSchemaGenerator;
  private final FilterExampleGenerator filterExampleGenerator;

  public FilterParameterCustomizer(FilterSchemaGenerator filterSchemaGenerator,
      FilterExampleGenerator filterExampleGenerator) {
    this.filterSchemaGenerator = filterSchemaGenerator;
    this.filterExampleGenerator = filterExampleGenerator;
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

    java.lang.reflect.Parameter[] parameters = method.getParameters();
    if (parameters == null) {
      return operation;
    }

    for (java.lang.reflect.Parameter parameter : parameters) {

      Filter filterAnnotation = parameter.getAnnotation(Filter.class);
      boolean isFilterType = isFilterType(parameter);

      if (filterAnnotation != null || isFilterType) {
        Class<?> entityClass = determineEntityClass(filterAnnotation, parameter);
        String parameterName = filterAnnotation != null
            ? filterAnnotation.parameter()
            : "filter";

        removeAutoDetectedParameter(operation, parameter);

        if (entityClass != null && entityClass != Void.class) {
          customizeFilterParameter(operation, parameterName, entityClass);
        } else {
          customizeGenericFilterParameter(operation, parameterName);
        }
      }

    }

    return operation;

  }

  private void removeAutoDetectedParameter(Operation operation, java.lang.reflect.Parameter parameter) {

    if (operation.getParameters() == null) {
      return;
    }

    String paramName = parameter.getName();
    if (paramName == null) {
      return;
    }

    operation
        .getParameters()
        .removeIf(param ->
            paramName.equals(param.getName()) ||
                (param.getName() != null && param
                    .getName()
                    .startsWith("arg"))
        );

  }

  private boolean isFilterType(java.lang.reflect.Parameter parameter) {

    Class<?> type = parameter.getType();
    String typeName = type.getName();

    switch (typeName) {
      case "com.turkraft.springfilter.converter.FilterSpecification" -> {
        return true;
      }
      case "com.turkraft.springfilter.converter.FilterPredicate" -> {
        return true;
      }
      case "com.turkraft.springfilter.parser.node.FilterNode" -> {
        return true;
      }
      case "org.springframework.data.mongodb.core.query.Query" -> {
        return true;
      }
      case "co.elastic.clients.elasticsearch._types.query_dsl.Query" -> {
        return true;
      }
      case "org.bson.Document" -> {
        return true;
      }
    }

    if (type
        .getName()
        .equals("java.util.Optional")) {

      java.lang.reflect.Type genericType = parameter.getParameterizedType();

      if (genericType instanceof java.lang.reflect.ParameterizedType parameterizedType) {
        java.lang.reflect.Type[] typeArgs = parameterizedType.getActualTypeArguments();
        if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
          String innerTypeName = ((Class<?>) typeArgs[0]).getName();
          return innerTypeName.equals("com.turkraft.springfilter.converter.FilterSpecification")
              || innerTypeName.equals("com.turkraft.springfilter.converter.FilterPredicate")
              || innerTypeName.equals("com.turkraft.springfilter.parser.node.FilterNode")
              || innerTypeName.equals("org.springframework.data.mongodb.core.query.Query")
              || innerTypeName.equals("co.elastic.clients.elasticsearch._types.query_dsl.Query")
              || innerTypeName.equals("org.bson.Document");
        }
      }

    }

    return false;

  }

  @Nullable
  private Class<?> determineEntityClass(@Nullable Filter filterAnnotation,
      java.lang.reflect.Parameter parameter) {

    if (filterAnnotation != null && filterAnnotation.entityClass() != Void.class) {
      return filterAnnotation.entityClass();
    }

    java.lang.reflect.Type parameterType = parameter.getParameterizedType();

    if (parameterType instanceof java.lang.reflect.ParameterizedType parameterizedType) {

      java.lang.reflect.Type[] typeArguments = parameterizedType.getActualTypeArguments();

      if (typeArguments.length > 0) {
        if (typeArguments[0] instanceof java.lang.reflect.ParameterizedType innerType) {
          java.lang.reflect.Type[] innerTypeArguments = innerType.getActualTypeArguments();
          if (innerTypeArguments.length > 0 && innerTypeArguments[0] instanceof Class) {
            return (Class<?>) innerTypeArguments[0];
          }
        } else if (typeArguments[0] instanceof Class) {
          return (Class<?>) typeArguments[0];
        }
      }

    }

    return null;

  }

  private void customizeFilterParameter(Operation operation, String parameterName,
      Class<?> entityClass) {

    try {

      Parameter openApiParameter = findOrCreateParameter(operation, parameterName);
      openApiParameter.setIn("query");
      openApiParameter.setRequired(false);

      String description = filterSchemaGenerator.generateSchemaDescription(entityClass);
      openApiParameter.setDescription(description);

      Schema<?> schema = new StringSchema();
      schema.setType("string");

      List<String> examples = filterExampleGenerator.generateExamples(entityClass);
      if (!examples.isEmpty()) {
        schema.setExample(examples.get(0));
      }

      openApiParameter.setSchema(schema);
      ensureParameterInOperation(operation, openApiParameter);

    } catch (Exception e) {
      customizeGenericFilterParameter(operation, parameterName);
    }

  }

  private void customizeGenericFilterParameter(Operation operation, String parameterName) {

    try {

      Parameter openApiParameter = findOrCreateParameter(operation, parameterName);
      openApiParameter.setIn("query");
      openApiParameter.setRequired(false);
      openApiParameter.setDescription("Filter query string");

      Schema<?> schema = new StringSchema();
      schema.setType("string");
      openApiParameter.setSchema(schema);

      ensureParameterInOperation(operation, openApiParameter);

    } catch (Exception ignored) {
    }

  }

  private Parameter findOrCreateParameter(Operation operation, String parameterName) {

    if (operation.getParameters() != null) {
      for (Parameter param : operation.getParameters()) {
        if (param != null && parameterName.equals(param.getName())) {
          return param;
        }
      }
    }

    Parameter parameter = new Parameter();
    parameter.setName(parameterName);
    return parameter;

  }

  private void ensureParameterInOperation(Operation operation, Parameter parameter) {

    if (operation.getParameters() == null) {
      operation.setParameters(new java.util.ArrayList<>());
    }

    String parameterName = parameter.getName();
    if (parameterName == null) {
      return;
    }

    boolean exists = false;
    for (int i = 0; i < operation
        .getParameters()
        .size(); i++) {
      Parameter existingParam = operation
          .getParameters()
          .get(i);
      if (existingParam != null && parameterName.equals(existingParam.getName())) {
        operation
            .getParameters()
            .set(i, parameter);
        exists = true;
        break;
      }
    }

    if (!exists) {
      operation
          .getParameters()
          .add(parameter);
    }

  }

}
