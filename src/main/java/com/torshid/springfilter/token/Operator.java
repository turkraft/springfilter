package com.torshid.springfilter.token;

import com.torshid.compiler.token.Token;
import com.torshid.compiler.token.matcher.LiteralMatcher.ILiteral;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Operator extends Token {

  private Type type;

  public enum Type implements ILiteral {

    AND("and", 1), OR("or", 0), NOT("not");

    private final String literal;

    private final Position position;

    private final int priority;

    Type(String literal) {
      this.literal = literal;
      this.position = Position.PREFIX;
      this.priority = 0;
    }

    Type(String literal, int priority) {
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
      return null;
    }

  }

  public enum Position {

    PREFIX, INFIX

  }

}
