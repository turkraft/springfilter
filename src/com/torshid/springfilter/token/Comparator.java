package com.torshid.springfilter.token;

import com.torshid.compiler.token.Token;
import com.torshid.compiler.token.matcher.LiteralMatcher.ILiteral;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Comparator extends Token {

  private Type type;

  public enum Type implements ILiteral {

    EQUAL("=", true),
    NOT_EQUAL("!", true),
    REG_EXP("~", true),
    GREATER_THAN_EQUAL(">=", true),
    GREATER_THAN(">", true),
    LESS_THAN_EQUAL("<=", true),
    LESS_THAN("<", true),
    NULL(" is null", "^is\s*null", false),
    NOT_NULL(" is not null", "^is\s*not\s*null", false),
    EMPTY(" is empty", "^is\s*empty", false),
    NOT_EMPTY(" is not empty", "^is\s*not\s*empty", false);

    private final String literal;
    private final String regex;
    private final boolean needsInput;

    Type(String literal, String regex, boolean needsInput) {
      this.literal = literal;
      this.regex = regex;
      this.needsInput = needsInput;
    }

    Type(String literal, boolean needsInput) {
      this.literal = literal;
      this.needsInput = needsInput;
      this.regex = null;
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

}
