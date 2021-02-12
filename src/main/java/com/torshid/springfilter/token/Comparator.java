package com.torshid.springfilter.token;

import com.torshid.compiler.token.IToken;
import com.torshid.compiler.token.LiteralMatcher.ILiteral;

public enum Comparator implements IToken, ILiteral {

  EQUAL(":", Object.class),
  NOT_EQUAL("!", Object.class),
  LIKE("~", String.class),
  GREATER_THAN_EQUAL(">:", Comparable.class),
  GREATER_THAN(">", Comparable.class),
  LESS_THAN_EQUAL("<:", Comparable.class),
  LESS_THAN("<", Comparable.class),
  NULL("is null", "^is\\s*null"),
  NOT_NULL("is not null", "^is\\s*not\\s*null"),
  EMPTY("is empty", "^is\\s*empty"),
  NOT_EMPTY("is not empty", "^is\\s*not\\s*empty");

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
