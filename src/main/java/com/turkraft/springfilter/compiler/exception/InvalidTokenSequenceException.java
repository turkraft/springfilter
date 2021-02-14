package com.turkraft.springfilter.compiler.exception;

import java.util.LinkedList;

import com.turkraft.springfilter.compiler.token.IToken;

public class InvalidTokenSequenceException extends ParserException {

  private static final long serialVersionUID = 1L;

  public InvalidTokenSequenceException(LinkedList<IToken> list) {
    super("Could not manage token sequence " + list);
  }

}
