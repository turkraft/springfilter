package com.turkraft.springfilter.parser.generator.expression;

import java.util.function.BiFunction;
import javax.persistence.criteria.Path;

public class ExpressionGeneratorParameters {

  private ExpressionGeneratorParameters() {}

  public static BiFunction<Path<?>, Object, Boolean> FILTERING_AUTHORIZATION;
  public static boolean ENABLE_ASTERISK_WITH_LIKE_OPERATOR;
  public static boolean CASE_SENSITIVE_LIKE_OPERATOR;

  static {

    FILTERING_AUTHORIZATION = null;
    ENABLE_ASTERISK_WITH_LIKE_OPERATOR = true;
    CASE_SENSITIVE_LIKE_OPERATOR = false;

  }

}
