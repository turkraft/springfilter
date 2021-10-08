package com.turkraft.springfilter.exception;

public class UnknownFilterFunctionException extends SpringFilterException {

  private static final long serialVersionUID = 1L;

  public UnknownFilterFunctionException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnknownFilterFunctionException(String message) {
    super(message);
  }

}
