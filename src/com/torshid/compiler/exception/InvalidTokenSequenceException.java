package com.torshid.compiler.exception;

import java.util.LinkedList;

import com.torshid.compiler.token.Token;

public class InvalidTokenSequenceException extends ParserException {

  private static final long serialVersionUID = 1L;

  public InvalidTokenSequenceException(LinkedList<Token> list) {
    super("Could not manage token sequence " + list);
  }

}
