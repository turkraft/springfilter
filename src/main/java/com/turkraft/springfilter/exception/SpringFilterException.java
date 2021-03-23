package com.turkraft.springfilter.exception;

public abstract class SpringFilterException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SpringFilterException(String message, Throwable cause) {
    super(message, cause);
  }

  public SpringFilterException(String message) {
    super(message);
  }

}
