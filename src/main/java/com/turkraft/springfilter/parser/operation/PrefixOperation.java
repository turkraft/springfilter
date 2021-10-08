package com.turkraft.springfilter.parser.operation;

import com.turkraft.springfilter.parser.FilterLexer;

public enum PrefixOperation implements IOperation {

  IS_NULL(FilterLexer.IS_NULL),

  IS_NOT_NULL(FilterLexer.IS_NOT_NULL),

  IS_EMPTY(FilterLexer.IS_EMPTY),

  IS_NOT_EMPTY(FilterLexer.IS_NOT_EMPTY)

  ;

  private int tokenType;

  PrefixOperation(int tokenType) {
    this.tokenType = tokenType;
  }

  public static PrefixOperation from(int tokenType) {
    for (PrefixOperation element : values()) {
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
