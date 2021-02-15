package com.turkraft.springfilter.exception;

public class InvalidQueryException extends ParserException {

  private static final long serialVersionUID = 1L;

  public InvalidQueryException(String message) {
    super(message);
  }

  public InvalidQueryException(String message, Throwable cause) {
    super(message, cause);
  }

}
