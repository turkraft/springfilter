package com.turkraft.springfilter.token;

import com.turkraft.springfilter.token.LiteralMatcher.ILiteral;

public enum Comparator implements IToken, ILiteral {

  EQUAL(":", Object.class), NOT_EQUAL("!", Object.class), LIKE("~",
      String.class), GREATER_THAN_OR_EQUAL(">:", Comparable.class), GREATER_THAN(">",
          Comparable.class), LESS_THAN_OR_EQUAL("<:", Comparable.class), LESS_THAN("<",
              Comparable.class), IN("in", "^in(?=\\s*\\()", Comparable.class), NULL("is null",
                  "^is\\s*null(?=\\s|\\)|$)"), NOT_NULL("is not null",
                      "^is\\s*not\\s*null(?=\\s|\\)|$)"), EMPTY("is empty",
                          "^is\\s*empty(?=\\s|\\)|$)"), NOT_EMPTY("is not empty",
                              "^is\\s*not\\s*empty(?=\\s|\\)|$)");

  private final String literal;
  private final String regex;
  private final Class<?> fieldType;

  Comparator(String literal, String regex, Class<?> fieldType) {
    this.literal = literal;
    this.regex = regex;
    this.fieldType = fieldType;
  }

  Comparator(String literal, Class<?> fieldType) {
    this.literal = literal;
    this.fieldType = fieldType;
    this.regex = null;
  }

  Comparator(String literal, String regex) {
    this.literal = literal;
    this.regex = regex;
    this.fieldType = null;
  }

  Comparator(String literal) {
    this.literal = literal;
    this.regex = null;
    this.fieldType = null;
  }

  @Override
  public String getLiteral() {
    return literal;
  }

  public boolean needsInput() {
    return fieldType != null;
  }

  @Override
  public String getRegex() {
    return regex;
  }

  public Class<?> getFieldType() {
    return fieldType;
  }

}
