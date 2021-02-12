package com.springfilter.compiler.compiler.exception;

public class OutOfInputException extends TokenizerException {

  private static final long serialVersionUID = 1L;

  public OutOfInputException(String message) {
    super(message);
  }

}
