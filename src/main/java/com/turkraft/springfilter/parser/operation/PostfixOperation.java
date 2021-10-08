package com.turkraft.springfilter.parser.operation;

import com.turkraft.springfilter.parser.FilterLexer;

public enum PostfixOperation implements IOperation {

  NOT(FilterLexer.NOT),

  ;

  private int tokenType;

  PostfixOperation(int tokenType) {
    this.tokenType = tokenType;
  }

  public static PostfixOperation from(int tokenType) {
    for (PostfixOperation element : values()) {
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
