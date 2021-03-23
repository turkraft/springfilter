package com.turkraft.springfilter.token;

import com.turkraft.springfilter.token.LiteralMatcher.ILiteral;

public enum Comparator implements IToken, ILiteral {

  LIKE("~", true),

  EQUAL(":", true),

  NOT_EQUAL("!", true),

  GREATER_THAN_OR_EQUAL(">:", true),

  GREATER_THAN(">", true),

  LESS_THAN_OR_EQUAL("<:", true),

  LESS_THAN("<", true),

  IN("in", "^in(?=\\s*\\()", true),

  NULL("is null", "^is\\s*null(?=\\s|\\)|$)", false),

  NOT_NULL("is not null", "^is\\s*not\\s*null(?=\\s|\\)|$)", false),

  EMPTY("is empty", "^is\\s*empty(?=\\s|\\)|$)", false),

  NOT_EMPTY("is not empty", "^is\\s*not\\s*empty(?=\\s|\\)|$)", false);

  private final String literal;
  private final String regex;
  private final boolean needsInput;

  Comparator(String literal, String regex, boolean needsInput) {
    this.literal = literal;
    this.regex = regex;
    this.needsInput = needsInput;
  }

  Comparator(String literal, boolean needsInput) {
    this.literal = literal;
    this.regex = null;
    this.needsInput = needsInput;
  }

  @Override
  public String getLiteral() {
    return literal;
  }

  public boolean needsInput() {
    return needsInput;
  }

  @Override
  public String getRegex() {
    return regex;
  }

}
