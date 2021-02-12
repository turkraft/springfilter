package com.torshid.springfilter.token;

import com.torshid.compiler.token.IToken;
import com.torshid.compiler.token.LiteralMatcher.ILiteral;

public enum Operator implements IToken, ILiteral {

  AND("and", 1), OR("or", 0), NOT("not");

  private final String literal;

  private final Position position;

  private final int priority;

  Operator(String literal) {
    this.literal = literal;
    this.position = Position.PREFIX;
    this.priority = 0;
  }

  Operator(String literal, int priority) {
    this.literal = literal;
    this.priority = priority;
    this.position = Position.INFIX;
  }

  @Override
  public String getLiteral() {
    return literal;
  }

  public Position getPosition() {
    return position;
  }

  public int getPriority() {
    return priority;
  }

  @Override
  public String getRegex() {
    return "^" + literal + "(?=\\s|\\(|$)";
  }

  public enum Position {
    PREFIX, INFIX
  }

}
