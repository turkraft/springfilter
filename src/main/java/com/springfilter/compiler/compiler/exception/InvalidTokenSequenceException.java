package com.springfilter.compiler.compiler.exception;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.token.IToken;

public class InvalidTokenSequenceException extends ParserException {

  private static final long serialVersionUID = 1L;

  public InvalidTokenSequenceException(LinkedList<IToken> list) {
    super("Could not manage token sequence " + list);
  }

}
