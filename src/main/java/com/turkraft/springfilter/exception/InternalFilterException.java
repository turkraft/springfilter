package com.turkraft.springfilter.exception;

public class InternalFilterException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public InternalFilterException(String message, Throwable cause) {
    super(message, cause);
  }

  public InternalFilterException(String message) {
    super(message);
  }

}
