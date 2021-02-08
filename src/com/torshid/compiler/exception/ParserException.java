package com.torshid.compiler.exception;

public abstract class ParserException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ParserException(String message) {
    super(message);
  }

  public ParserException(String message, Throwable cause) {
    super(message, cause);
  }

}
