package com.turkraft.springfilter.node;

import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.Expression;
import com.turkraft.springfilter.Pair;
import com.turkraft.springfilter.exception.InvalidQueryException;

public enum FunctionType {

  ABSOLUTE(Number.class), AVERAGE(Number.class), MIN(Number.class), MAX(Number.class), SUM(
      Number.class), SIZE(Collection.class), LENGTH(
          String.class), TRIM(String.class), CURRENTTIME, CURRENTDATE, CURRENTTIMESTAMP;

  private final Class<?>[] argumentTypes;

  FunctionType(Class<?>... argumentTypes) {
    this.argumentTypes = argumentTypes;
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
