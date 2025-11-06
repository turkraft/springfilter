package com.turkraft.springfilter.openapi.springdoc;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

public abstract class BaseParameterCustomizer {

  protected void removeAutoDetectedParameter(Operation operation,
      java.lang.reflect.Parameter parameter) {

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

  protected Parameter findOrCreateParameter(Operation operation, String parameterName) {

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

  protected void ensureParameterInOperation(Operation operation, Parameter parameter) {

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

  @Nullable
  protected Class<?> findEntityClassFromMethod(Method method) {

    if (method == null) {
      return null;
    }

    java.lang.reflect.Parameter[] parameters = method.getParameters();
    if (parameters == null) {
      return null;
    }

    for (java.lang.reflect.Parameter parameter : parameters) {
      Filter filterAnnotation = parameter.getAnnotation(Filter.class);

      if (filterAnnotation != null) {

        Class<?> entityClass = filterAnnotation.entityClass();
        if (entityClass != null && entityClass != Void.class) {
          return entityClass;
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

      }

    }

    return null;

  }

}
