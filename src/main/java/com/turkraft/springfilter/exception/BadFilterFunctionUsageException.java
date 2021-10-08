package com.turkraft.springfilter.exception;

public class BadFilterFunctionUsageException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public BadFilterFunctionUsageException(String message, Throwable cause) {
    super(message, cause);
  }

  public BadFilterFunctionUsageException(String message) {
    super(message);
  }

}
