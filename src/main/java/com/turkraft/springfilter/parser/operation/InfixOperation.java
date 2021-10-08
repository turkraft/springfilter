package com.turkraft.springfilter.parser.operation;

import com.turkraft.springfilter.parser.FilterLexer;

public enum InfixOperation implements IOperation {

  AND(FilterLexer.AND),

  OR(FilterLexer.OR),

  IN(FilterLexer.IN),

  LIKE(FilterLexer.LIKE),

  EQUAL(FilterLexer.EQUAL),

  NOT_EQUAL(FilterLexer.NOT_EQUAL),

  GREATER_THAN(FilterLexer.GREATER_THAN),

  GREATER_THAN_OR_EQUAL(FilterLexer.GREATER_THAN_OR_EQUAL),

  LESS_THAN(FilterLexer.LESS_THAN),

  LESS_THAN_OR_EQUAL(FilterLexer.LESS_THAN_OR_EQUAL),

  ;

  private int tokenType;

  InfixOperation(int tokenType) {
    this.tokenType = tokenType;
  }

  public static InfixOperation from(int tokenType) {
    for (InfixOperation element : values()) {
      if (tokenType == element.getTokenType()) {
        return element;
      }
    }
    return null;
  }

  @Override
  public int getTokenType() {
    return tokenType;
  }

}
