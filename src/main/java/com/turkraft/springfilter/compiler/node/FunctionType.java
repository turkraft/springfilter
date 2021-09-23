package com.turkraft.springfilter.compiler.node;

import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.Expression;
import com.turkraft.springfilter.Pair;
import com.turkraft.springfilter.exception.InvalidQueryException;

public enum FunctionType {

  ABSOLUTE(false, Number.class),

  AVERAGE(false, Number.class),

  MIN(false, Number.class),

  MAX(false, Number.class),

  SUM(false, Number.class),

  SIZE(false, Collection.class),

  LENGTH(false, String.class),

  TRIM(false, String.class),

  UPPER(false, String.class),

  LOWER(false, String.class),

  CONCAT(false, String[].class),

  CURRENTTIME(false),

  CURRENTDATE(false),

  CURRENTTIMESTAMP(false),

  ALL(true),

  ANY(true),

  SOME(true),

  EXISTS(true);

  private final boolean customizedBehavior;

  private final Class<?>[] argumentTypes;

  FunctionType(boolean customizedBehavior, Class<?>... argumentTypes) {

    this.customizedBehavior = customizedBehavior;

    this.argumentTypes = argumentTypes;

    if (argumentTypes.length > 1 && isVariadic()) {
      throw new RuntimeException("Variadic functions can only have one argument type");
    }

  }

  public boolean hasCustomizedBehavior() {
    return customizedBehavior;
  }

  public Class<?>[] getArgumentTypes() {
    return argumentTypes;
  }

  public boolean matches(String name, List<Pair<IExpression, Expression<?>>> expressions) {

    if (!name().equalsIgnoreCase(name)) {
      return false;
    }

    if (customizedBehavior) {
      return true;
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

  public static FunctionType from(String name) {
    for (FunctionType type : values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }
    return null;
  }

}
