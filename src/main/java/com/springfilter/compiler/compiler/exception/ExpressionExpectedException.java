package com.springfilter.compiler.compiler.exception;

public class ExpressionExpectedException extends ParserException {

  private static final long serialVersionUID = 1L;

  public ExpressionExpectedException(String message) {
    super(message);
  }

  public ExpressionExpectedException(String message, Throwable cause) {
    super(message, cause);
  }

}
