package com.turkraft.springfilter.parser.generator.expression;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

public class ExpressionGeneratorParameters {

  protected ExpressionGeneratorParameters() {}

  public static BiFunction<Path<?>, Object, Boolean> FILTERING_AUTHORIZATION;
  public static Function<Expression<?>, Class<?>> JAVA_TYPE_MODIFIER;
  public static boolean ENABLE_ASTERISK_WITH_LIKE_OPERATOR;

  static {

    FILTERING_AUTHORIZATION = null;
    JAVA_TYPE_MODIFIER = null;
    ENABLE_ASTERISK_WITH_LIKE_OPERATOR = true;

  }

}
