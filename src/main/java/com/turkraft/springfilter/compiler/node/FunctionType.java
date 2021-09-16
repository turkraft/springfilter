package com.turkraft.springfilter.compiler.node;

import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.Expression;
import com.turkraft.springfilter.Pair;
import com.turkraft.springfilter.exception.InvalidQueryException;

public enum FunctionType {

  ABSOLUTE(Number.class),

  AVERAGE(Number.class),

  MIN(Number.class),

  MAX(Number.class),

  SUM(Number.class),

  SIZE(Collection.class),

  LENGTH(String.class),

  TRIM(String.class),

  UPPER(String.class),

  LOWER(String.class),

  CONCAT(String[].class),

  CURRENTTIME,

  CURRENTDATE,

  CURRENTTIMESTAMP;

  private final Class<?>[] argumentTypes;

  FunctionType(Class<?>... argumentTypes) {

    this.argumentTypes = argumentTypes;

    if (argumentTypes.length > 1 && isVariadic()) {
      throw new RuntimeException("Variadic functions can only have one argument type");
    }

  }

  public Class<?>[] getArgumentTypes() {
    return argumentTypes;
  }

  public boolean matches(String name, List<Pair<IExpression, Expression<?>>> expressions) {

    if (!name().equalsIgnoreCase(name)) {
      return false;
    }

    if (argumentTypes.length > expressions.size()) {
      throw new InvalidQueryException(
          "The function " + name + " should have " + argumentTypes.length + " arguments");
    }

    if (isVariadic()) {

      for (int i = 0; i < expressions.size(); i++) {

        if (expressions.get(i).getKey() instanceof Input) {
          if (!(((Input) expressions.get(i).getKey()).getValue()
              .canBe(argumentTypes[0].getComponentType()))) {
            return false;
          }
        } else if (!argumentTypes[0].getComponentType()
            .isAssignableFrom(expressions.get(i).getValue().getJavaType())) {
          return false;
        }

      }

      return true;

    }


    for (int i = 0; i < argumentTypes.length; i++) {

      if (expressions.get(i).getKey() instanceof Input) {
        if (!(((Input) expressions.get(i).getKey()).getValue().canBe(argumentTypes[i]))) {
          return false;
        }
      } else if (!argumentTypes[i].isAssignableFrom(expressions.get(i).getValue().getJavaType())) {
        return false;
      }

    }

    return true;

  }

  public boolean isVariadic() {
    return argumentTypes.length > 0 && argumentTypes[0].isArray();
  }

  public static FunctionType getMatch(
      String name,
      List<Pair<IExpression, Expression<?>>> expressions) {

    for (FunctionType type : values()) {
      if (type.matches(name, expressions)) {
        return type;
      }
    }

    return null;

  }

}
