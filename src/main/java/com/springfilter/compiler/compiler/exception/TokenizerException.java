package com.springfilter.compiler.compiler.exception;

public abstract class TokenizerException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenizerException(String message) {
    super(message);
  }

  public TokenizerException(String message, Throwable cause) {
    super(message, cause);
  }

}
