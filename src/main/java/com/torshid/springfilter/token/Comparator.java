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

    EQUAL(":", Object.class),
    NOT_EQUAL("!", Object.class),
    LIKE("~", String.class),
    GREATER_THAN_EQUAL(">:", Comparable.class),
    GREATER_THAN(">", Comparable.class),
    LESS_THAN_EQUAL("<:", Comparable.class),
    LESS_THAN("<", Comparable.class),
    NULL(" is null", "^is\\s*null"),
    NOT_NULL(" is not null", "^is\\s*not\\s*null"),
    EMPTY(" is empty", "^is\\s*empty"),
    NOT_EMPTY(" is not empty", "^is\\s*not\\s*empty");

    private final String literal;
    private final String regex;
    private final Class<?> fieldType;

    Type(String literal, String regex, Class<?> fieldType) {
      this.literal = literal;
      this.regex = regex;
      this.fieldType = fieldType;
    }

    Type(String literal, Class<?> fieldType) {
      this.literal = literal;
      this.fieldType = fieldType;
      this.regex = null;
    }

    Type(String literal, String regex) {
      this.literal = literal;
      this.regex = regex;
      this.fieldType = null;
    }

    Type(String literal) {
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

}
